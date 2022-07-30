package com.Suresh.Controllers;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.Suresh.Models.Student;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@RestController
public class WelcomeRestController {

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/h")
	@HystrixCommand(fallbackMethod = "getDataFromDB")
	public String getDataFromRedis() {
		System.out.println(" getDataFromRedis method called ");
		if (new Random().nextInt(10) <= 10) {
			throw new RuntimeException(" Redis server is down ..");
		}

		return "data from Redis server ";
	}

	public String getDataFromDB() {
		System.out.println(" getDataFromDB() method called ");
		return "Data is coming from DB ";
	}

	// https://github.com/Netflix/Hystrix/wiki/Configuration#command-properties
	@GetMapping("/h1")
	@HystrixCommand(commandKey = "java", groupKey = "java", fallbackMethod = "fall", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "6"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
			@HystrixProperty(name = "circuitBreaker.enabled", value = "false") })
	public String show() {
		System.out.println(" Inside the show method... ");
		List<Student> list = restTemplate.getForObject("http://localhost:1111/getStudentDetailsForSchool/VailSchool",
				List.class);
		System.out.println(" printing the list " + list);
		return "yeah got it !......";
	}

	public String fall() {
		return " Service is unavailable for Accessing try after sometime ";
	}

}
