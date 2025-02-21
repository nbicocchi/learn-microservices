package cloud.macca.aggregator.task;

import cloud.macca.aggregator.dto.AuthResponse;
import cloud.macca.aggregator.dto.GradesResponse;
import cloud.macca.aggregator.dto.StudentsResponse;
import cloud.macca.aggregator.error.NotOkException;
import cloud.macca.aggregator.model.Grade;
import cloud.macca.aggregator.model.ReportCard;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class Aggregate extends TimerTask {
    private final OkHttpClient client = new OkHttpClient();
    private String bearerToken = "";

    private Request buildRequest(String url, Map<String, String> headers, FormBody body){
        Request.Builder builder = new Request.Builder().url(url);
        if(headers != null){
            headers.forEach(builder::addHeader);
            System.out.println(headers);
        }

        if(body != null){
            builder.post(body);
        }

        return builder.build();

    }

    private <C> C fetchData(String url, Map<String, String> headers, FormBody body, Class<C> classObj) throws IOException, NotOkException {
        Request req = buildRequest(url, headers, body);
        Response res = client.newCall(req).execute();
        ResponseBody resBody = res.body();
        if(res.code() != 200){
            throw new NotOkException();
        }
        assert(resBody != null);
        Gson gson = new Gson();
        String strBody = resBody.string();
        return gson.fromJson(strBody, classObj);
    }

    private void performAuthentication() throws NotOkException, IOException {
        // short lived credentials for the authenticator!
        String clientSecret = System.getenv("CLIENT_SECRET");
        String clientId = System.getenv("CLIENT_ID");
        String authUri = System.getenv("AUTH_URI");

        FormBody body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "client_credentials")
                .build();
        AuthResponse auth = fetchData(authUri, null, body, AuthResponse.class);
        this.bearerToken = auth.accessToken;
    }

    @Override
    public void run() {
        String gradesUri = System.getenv("GRADES_URI");
        if(gradesUri == null){
            gradesUri = "http://localhost:8081";
        }

        String studentsUri = System.getenv("STUDENTS_URI");
        if(studentsUri == null){
            studentsUri = "http://localhost:8080";
        }

        try{
            StudentsResponse allStudents = this.fetchData(studentsUri, Map.of("Authorization", "Bearer " + this.bearerToken), null, StudentsResponse.class);
            GradesResponse allGrades = this.fetchData(gradesUri, Map.of("Authorization", "Bearer " + this.bearerToken), null, GradesResponse.class);

            final List<ReportCard> cards = Arrays.stream(allStudents.result).map(student -> {
                List<Grade> studentGrades = Arrays.stream(allGrades.result).filter(grade -> grade.studentId == student.id).toList();
                return new ReportCard(student.name + " " + student.surname, studentGrades);
            }).toList();

            cards.forEach(card -> {
                System.out.println(card.toString());
            });

        }catch(MalformedURLException e){
            System.out.println("cannot perform task because the url is malformed");
        }catch(IOException e){
            System.out.println("cannot perform http request: ");
            e.printStackTrace();
        }catch(NotOkException e){
            e.printStackTrace();
            System.out.println("unauthorized!");
            try {
                performAuthentication();
            } catch (Exception ex) {
                System.out.println("couldn't authenticate, is there something wrong with the idp?");
            }
        }
    }
}