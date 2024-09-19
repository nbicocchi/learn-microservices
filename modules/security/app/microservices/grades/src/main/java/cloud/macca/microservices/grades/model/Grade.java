package cloud.macca.microservices.grades.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    
    @Column(nullable = false, name = "value")
    private Integer value;

    @Column(nullable = false, name = "student_id")
    private Integer studentId;

    public Integer getId() {
        return id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public Integer getValue() {
        return value;
    }
}
