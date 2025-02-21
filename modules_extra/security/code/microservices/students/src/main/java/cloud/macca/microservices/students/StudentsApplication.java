package cloud.macca.microservices.students;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class StudentsApplication {

	@Value("${grades.uri}")
	private String gradesEndpoint;

	public static void main(String[] args) {
		SpringApplication.run(StudentsApplication.class, args);
	}

}
