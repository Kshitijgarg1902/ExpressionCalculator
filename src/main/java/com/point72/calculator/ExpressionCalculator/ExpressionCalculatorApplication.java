package com.point72.calculator.ExpressionCalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ExpressionCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpressionCalculatorApplication.class, args);
	}

}
