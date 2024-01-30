package com.main;

import org.hibernate.annotations.CompositeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.main.controller.EmailService;

import jakarta.validation.constraints.Email;

@SpringBootApplication
public class ContactMangerApplication {

	
	
	
	public static void main(String[] args) {
		SpringApplication.run(ContactMangerApplication.class, args);
		System.out.println("main method hitt");
		
	
	}
	
}
