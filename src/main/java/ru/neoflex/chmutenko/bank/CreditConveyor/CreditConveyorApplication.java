package ru.neoflex.chmutenko.bank.CreditConveyor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
@SpringBootApplication
public class CreditConveyorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditConveyorApplication.class, args);
	}

}
