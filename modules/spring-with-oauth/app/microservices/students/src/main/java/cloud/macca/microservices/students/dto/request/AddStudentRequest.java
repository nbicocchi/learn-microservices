package cloud.macca.microservices.students.dto.request;

public class AddStudentRequest {
    String name, surname;

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }
}
