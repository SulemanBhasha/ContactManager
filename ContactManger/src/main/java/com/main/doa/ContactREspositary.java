package com.main.doa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.main.entities.Contact;
import com.main.entities.User;

public interface ContactREspositary extends JpaRepository<Contact, Integer>{
	
	@Query("from Contact as c where c.user.id=:id ")
	public Page<Contact> findContacsByUser(@Param("id") int id,Pageable pagable);

	
//	search bar
	public List<Contact>  findByNameContainingAndUser(String name,User user); 
}
