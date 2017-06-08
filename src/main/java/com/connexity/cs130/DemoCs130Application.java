package com.connexity.cs130;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoCs130Application {
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(DemoCs130Application.class, args);
	}

	public static void exit() {
		SpringApplication.exit(context, new ExitCodeGenerator[]{});
	}
}
