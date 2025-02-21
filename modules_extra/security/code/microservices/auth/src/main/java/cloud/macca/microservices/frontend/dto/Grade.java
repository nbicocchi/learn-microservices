package cloud.macca.microservices.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Grade {
    @JsonProperty
    public int id;
    @JsonProperty("studentId")
    public int studentId;
    @JsonProperty("value")
    public int value;
    public Grade(
            int id,
            int studentId,
            int value
    ){
        this.id = id;
        this.studentId = studentId;
        this.value = value;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setId(int id) {
        this.id = id;
    }
}
