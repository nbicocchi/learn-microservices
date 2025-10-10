package cloud.macca.microservices.grades.service;

import cloud.macca.microservices.grades.dto.Student;
import cloud.macca.microservices.grades.error.StudentNotFoundError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class StudentsService {

    private final RestClient http;

    private String studentsEndpoint;

    @Autowired
    public StudentsService(RestClient.Builder builder, @Value("${students.uri}") String studentsEndpoint){
        DefaultUriBuilderFactory f = new DefaultUriBuilderFactory(studentsEndpoint);
        this.http = builder.uriBuilderFactory(f).build();
    }

    public Student getStudent(int studentId, String bearer) {

        return this.http
                .get()
                .uri("/{id}", studentId)
                .header("Authorization", bearer)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new StudentNotFoundError(studentId);
                })
                .body(Student.class);
    }

}
