package com.jojoldu.beginner;

import com.jojoldu.beginner.mail.template.HandlebarsFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@SpringBootApplication
@EnableBatchProcessing
@Configuration
public class BatchApplication {

	public static final String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.yml,"
			+ "/app/config/dev-beginner-group-admin/real-application.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(BatchApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);

	}

	@Bean
	public HandlebarsFactory handlebarsFactory() throws IOException {
		HandlebarsFactory factory = new HandlebarsFactory();
		factory.put("WeeklyLetter");
		return factory;
	}
}
