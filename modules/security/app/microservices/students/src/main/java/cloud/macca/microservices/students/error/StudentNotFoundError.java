package cloud.macca.microservices.students.error;

public class StudentNotFoundError extends RuntimeException{
    public StudentNotFoundError(int s){
        super("student " + s + " not found");
    }
}
