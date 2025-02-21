package cloud.macca.aggregator.model;

public class Grade {
    public final int id, value, studentId;
    public Grade(
            int id,
            int value,
            int studentId
    ){
        this.id = id;
        this.value = value;
        this.studentId = studentId;
    }
}
