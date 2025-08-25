package com.conal.dishbuilder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;


@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class DishBuilderApplication implements CommandLineRunner {
    private final Environment environment;
    private final ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(DishBuilderApplication.class, args);
        log.info("Dish Builder Application started!!");
    }

    @PostConstruct
    public void init() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            System.out.println(beanName);
        }
        String property = environment.getProperty("spring.flyway.baseline-on-migrate");
        log.error("Property: {}", property);
    }

    @Override
    public void run(String... args) throws Exception {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            System.out.println(beanName);
        }
    }
}
