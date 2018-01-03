package br.com.jorchestra.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableJOrchestra;

@SpringBootApplication
@EnableJOrchestra
public class JorchestraApplication {

	public static void main(String[] args) {
		SpringApplication.run(JorchestraApplication.class, args);
	}
}
