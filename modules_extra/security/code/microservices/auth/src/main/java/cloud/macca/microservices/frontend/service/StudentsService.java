package cloud.macca.microservices.frontend.service;

import cloud.macca.microservices.frontend.dto.GetAllStudentsResponse;
import cloud.macca.microservices.frontend.dto.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class StudentsService {

    @Value("${microservices.students_endpoint}")
    private String studentsEndpoint;
    private final RestClient http;

    public StudentsService(
            RestClient.Builder builder
    ){
        this.http = builder.build();
    }

    public Student[] getAllStudents(String accessToken){
        RestClient.ResponseSpec response = this.http.get()
                .uri(studentsEndpoint + "/")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve();
        GetAllStudentsResponse students = response.body(GetAllStudentsResponse.class);
        return students.result;
    }
}
