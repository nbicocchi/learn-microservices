package cloud.macca.microservices.frontend.controller;

import cloud.macca.microservices.frontend.dto.Grade;
import cloud.macca.microservices.frontend.dto.Student;
import cloud.macca.microservices.frontend.dto.User;
import cloud.macca.microservices.frontend.service.GradesService;
import cloud.macca.microservices.frontend.service.StudentsService;
import cloud.macca.microservices.frontend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/profile/")
public class ProfileController {

    private RestClient http;
    @Value("${auth.endpoint}")
    private String authEndpoint;

    @Autowired
    private UserService users;

    @Autowired
    private GradesService grades;

    @Autowired
    private StudentsService students;

    public ProfileController(
            RestClient.Builder builder
    ){
        this.http = builder.build();
    }

    @GetMapping(value = "/")
    public String profileHome(
            Model model,
            @CookieValue(value = "access_token", required = false) String accessToken
    ){
        if(accessToken == null){
            return "profile/invalid_token";
        }
        User currentUser = users.getUserInfo(accessToken);
        if(!currentUser.emailVerified){
            return "profile/email_not_verified";
        }
        Student[] allStudents = students.getAllStudents(accessToken);
        Grade[] allGrades = grades.getAllGrades(accessToken);
        HashMap<Integer, ArrayList<Integer>> gradesByStudent = new HashMap<Integer, ArrayList<Integer>>();
        Arrays.stream(allGrades).forEach(grade -> {
            if(gradesByStudent.get(grade.studentId) != null){
                gradesByStudent.get(grade.studentId).add(grade.value);
            }else{
                final ArrayList<Integer> arr = new ArrayList<>();
                arr.add(grade.value);
                gradesByStudent.put(grade.studentId, arr);
            }
        });

        model.addAttribute("user", currentUser);
        model.addAttribute("students", allStudents);
        model.addAttribute("grades", gradesByStudent);
        return "profile/index";
    }
}
