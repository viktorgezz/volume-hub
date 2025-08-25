package ru.viktorgezz.definition_of_anomaly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DefinitionOfAnomalyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefinitionOfAnomalyApplication.class, args);
	}

}
