package ru.viktorgezz.definition_of_anomaly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class DefinitionOfAnomalyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefinitionOfAnomalyApplication.class, args);
	}

}
