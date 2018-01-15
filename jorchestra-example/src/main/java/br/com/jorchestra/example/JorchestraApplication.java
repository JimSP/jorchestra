package br.com.jorchestra.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableJOrchestra;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJOrchestra
public class JorchestraApplication {

	public static void main(final String[] args) {
		SpringApplication.run(JorchestraApplication.class, args);
	}
}
