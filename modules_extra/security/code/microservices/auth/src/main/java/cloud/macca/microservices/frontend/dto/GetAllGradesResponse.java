package cloud.macca.microservices.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAllGradesResponse {
    @JsonProperty
    public final Grade[] result;
    public final boolean success;
    public GetAllGradesResponse(
            Grade[] grades,
            boolean success
    ){
        this.result = grades;
        this.success = success;
    }
}
