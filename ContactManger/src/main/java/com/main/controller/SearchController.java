package com.main.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.main.doa.ContactREspositary;
import com.main.doa.UserRespository;
import com.main.entities.Contact;
import com.main.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRespository respository;
	@Autowired
	private ContactREspositary contactREspositary;
	
	
//	search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> serch( @PathVariable("query") String query , Principal principal)
	{
		
		System.out.println(query);
		
		User user = this.respository.getUserByUsername(principal.getName());
		List<Contact> contact = this.contactREspositary.findByNameContainingAndUser(query, user);
		
		
		return ResponseEntity.ok(contact);
	}
	

	
	
	
	
	
	
}
