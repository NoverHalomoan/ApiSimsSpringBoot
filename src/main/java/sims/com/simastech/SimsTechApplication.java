package sims.com.simastech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class SimsTechApplication {

	@Profile({ "dev", "prod" })
	public static void main(String[] args) {
		SpringApplication.run(SimsTechApplication.class, args);
	}

}
