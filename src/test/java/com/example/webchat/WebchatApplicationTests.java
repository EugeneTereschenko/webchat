package com.example.webchat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class WebchatApplicationTests {

	@Test
	void contextLoads() {

		Date date = new Date();
		System.out.println("Current date and time: " + date);
	}

}
