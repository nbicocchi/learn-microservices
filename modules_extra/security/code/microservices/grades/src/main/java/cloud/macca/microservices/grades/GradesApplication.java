package cloud.macca.microservices.grades;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GradesApplication {

	@Value("${students.uri}")
	private String studentsEndpoint;

	public static void main(String[] args) {
		SpringApplication.run(GradesApplication.class, args);
	}

}
