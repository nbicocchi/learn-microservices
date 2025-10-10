package cloud.macca.microservices.frontend.dto;

public class GetAllStudentsResponse {
    public final Student[] result;
    public final boolean success;
    public GetAllStudentsResponse(
            Student[] students,
            boolean success
    ){
        this.result = students;
        this.success = success;
    }
}
