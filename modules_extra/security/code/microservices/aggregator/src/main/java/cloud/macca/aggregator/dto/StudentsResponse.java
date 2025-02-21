package cloud.macca.aggregator.dto;

import cloud.macca.aggregator.model.Student;

public class StudentsResponse {
    public final Student[] result;
    public final boolean success;
    public StudentsResponse(
            Student[] result
    ){
        this.result = result;
        this.success = true;
    }
}
