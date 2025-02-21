package cloud.macca.microservices.grades.error;

public class StudentNotFoundError extends RuntimeException{
    public StudentNotFoundError(int s){
        super("student " + s + " not found");
    }
}
