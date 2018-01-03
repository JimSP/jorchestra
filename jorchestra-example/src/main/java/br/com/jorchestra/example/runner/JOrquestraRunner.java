package br.com.jorchestra.example.runner;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;

@Component
public class JOrquestraRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrquestraRunner.class);

	@Autowired
	private JOrchestraConfigurationProperties jorchestraConfigurationProperties;

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("m=run, args=" + Arrays.toString(args));
		LOGGER.info("m=run, jorchestraConfigurationProperties=" + jorchestraConfigurationProperties);
	}
}
