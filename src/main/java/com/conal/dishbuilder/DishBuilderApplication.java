package com.conal.dishbuilder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class DishBuilderApplication {
private final Environment environment;
	public static void main(String[] args) {
		SpringApplication.run(DishBuilderApplication.class, args);
		log.info("Dish Builder Application started!!");
	}

	@PostConstruct
	public void init(){
		String property = environment.getProperty("spring.flyway.baseline-on-migrate");
		log.error("Property: {}", property);
	}

}
