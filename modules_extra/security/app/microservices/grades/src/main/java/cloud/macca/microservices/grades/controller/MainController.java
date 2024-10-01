package cloud.macca.microservices.grades.controller;

import cloud.macca.microservices.grades.dto.Student;
import cloud.macca.microservices.grades.dto.request.AddGradeRequest;
import cloud.macca.microservices.grades.dto.response.SuccessResponse;
import cloud.macca.microservices.grades.model.Grade;
import cloud.macca.microservices.grades.repository.GradeRepository;
import cloud.macca.microservices.grades.service.StudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/")
public class MainController {

    @Autowired
    private GradeRepository grades;

    @Autowired
    private StudentsService studentsService;

    @PostMapping(value = "/{studentId}")
    public SuccessResponse<String> addGradeToStudentId(
            @PathVariable String studentId,
            @RequestBody AddGradeRequest body,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        // we are sure the header exists and is valid, because the jwt checks already passed!
        studentsService.getStudent(Integer.parseInt(studentId), authorizationHeader);
        grades.insertGrade(body.getGrade(), Integer.parseInt(studentId));
        return new SuccessResponse<String>("grade added");
    }

    @GetMapping(value = "/{studentId}")
    public SuccessResponse<Iterable<Grade>> getGradesByStudentId(@PathVariable String studentId){
        return new SuccessResponse<Iterable<Grade>>(grades.findByStudentId(Integer.valueOf(studentId)));
    }

    @GetMapping(value = "/")
    public SuccessResponse<Iterable<Grade>> getAllGrades(){
        return new SuccessResponse<Iterable<Grade>>(grades.findAll());
    }

}
