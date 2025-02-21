package cloud.macca.microservices.grades.dto.request;

public class AddGradeRequest {
    int grade;

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }
}
