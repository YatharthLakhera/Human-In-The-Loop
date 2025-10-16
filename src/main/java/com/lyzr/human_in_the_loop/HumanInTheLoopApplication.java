package com.lyzr.human_in_the_loop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class HumanInTheLoopApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanInTheLoopApplication.class, args);
	}
}
