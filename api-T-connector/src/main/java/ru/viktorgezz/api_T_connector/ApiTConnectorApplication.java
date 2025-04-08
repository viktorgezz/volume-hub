package ru.viktorgezz.api_T_connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ApiTConnectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTConnectorApplication.class, args);
	}
}
