package cloud.macca.microservices.grades.repository;

import cloud.macca.microservices.grades.model.Grade;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends CrudRepository<Grade, Integer> {
    Iterable<Grade> findByStudentId(Integer studentId);

    @Modifying
    @Query(
            value = "insert into grades (value, student_id) values (:grade, :student)",
            nativeQuery = true
    )
    @Transactional
    void insertGrade(@Param("grade") int grade, @Param("student") int student);
}
