package com.moseory.jtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class JTalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(JTalkApplication.class, args);
	}

}