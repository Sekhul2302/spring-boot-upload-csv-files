package com.bezkoder.spring.files.csv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringBootUploadCsvFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootUploadCsvFilesApplication.class, args);
	}

}
