package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.example.test.dto.HatApps;

@SpringBootApplication
public class SpringBootRestClientApplication implements CommandLineRunner {
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootRestClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestClientApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HatApps hatApps = restTemplate.getForObject("http://localhost:8080/api/sample", HatApps.class);
		
	}

}
