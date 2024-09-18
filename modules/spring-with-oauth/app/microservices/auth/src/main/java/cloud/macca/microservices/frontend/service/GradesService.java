package cloud.macca.microservices.frontend.service;

import cloud.macca.microservices.frontend.dto.GetAllGradesResponse;
import cloud.macca.microservices.frontend.dto.Grade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class GradesService {

    @Value("${microservices.grades_endpoint}")
    private String gradesEndpoint;
    private final RestClient http;

    public GradesService(
            RestClient.Builder builder
    ){
        this.http = builder.build();
    }

    public Grade[] getAllGrades(String accessToken){
        GetAllGradesResponse response = this.http.get()
                .uri(gradesEndpoint + "/")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(GetAllGradesResponse.class);
        return response.result;
    }
}
