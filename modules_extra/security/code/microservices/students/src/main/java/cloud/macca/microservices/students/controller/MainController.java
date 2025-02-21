package cloud.macca.microservices.students.controller;

import cloud.macca.microservices.students.dto.request.AddStudentRequest;
import cloud.macca.microservices.students.dto.response.SuccessResponse;
import cloud.macca.microservices.students.error.StudentNotFoundError;
import cloud.macca.microservices.students.model.Student;
import cloud.macca.microservices.students.repository.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/")
public class MainController {

    @Autowired
    StudentsRepository students;

    @GetMapping(value = "/{studentId}")
    public SuccessResponse<Student> getStudentById(@PathVariable String studentId){
        final int id = Integer.parseInt(studentId);
        Student student = students.findById(id).orElseThrow(() -> new StudentNotFoundError(id));
        return new SuccessResponse<Student>(student);
    }

    @PostMapping(value = "/")
    public SuccessResponse<String> addStudent(@RequestBody AddStudentRequest student){
        students.create(student.getName(), student.getSurname());
        return new SuccessResponse<String>("student added");
    }

    @GetMapping(value = "/")
    public SuccessResponse<Iterable<Student>> getAllStudents(){
        return new SuccessResponse<Iterable<Student>>(students.findAll());
    }

}
