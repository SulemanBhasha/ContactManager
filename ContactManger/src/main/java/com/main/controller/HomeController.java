package com.main.controller;


import org.springframework.beans.factory.annotation.Autowired;
import com.main.configue.*;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.main.doa.UserRespository;
import com.main.entities.Contact;
import com.main.entities.User;
import com.main.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.main.doa.UserRespository;

@Controller
public class HomeController {
     
	@Autowired
	private BCryptPasswordEncoder BCryptPasswordEncoder;
	
	@Autowired
	private UserRespository userRespository;
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title", "smart contact manager");
		
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title", "About-smart contact manager");
		
		return "about";
	}
	@RequestMapping("/signup")
	public String signUp(Model model)
	{
		model.addAttribute("title", "Register-smart contact manager");
		model.addAttribute("user", new User());
		
		return "signup";
	}
//	handler for register user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user
			, BindingResult bindingResult,@RequestParam (value = "agreement",defaultValue = "false")
	boolean agreement,Model model,HttpSession session)
	{
		try {

			if(!agreement )
			{
				System.out.println("you have not aggred the terms and condition");
				throw new Exception("you have not aggred the terms and condition");
			}
			if(bindingResult.hasErrors())
			{
				System.out.println("Error"+bindingResult.toString());
			model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPasword(BCryptPasswordEncoder.encode(user.getPasword()));
			
			System.out.println(agreement);
			System.out.println("USER---"+user);
			
			 User save = this.userRespository.save(user);
			 System.out.println(save);
			   model.addAttribute("user", new User() );
				session.setAttribute("message", new Message("Successfully  register!!!", "alert-success"));

			 return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			
			session.setAttribute("message", new Message("something went wrong....", "alert-danger"));
			return "signup";
		}
		 
		
	}
//	handler for custom login
	
	@RequestMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Login page");
		return "login";
	}
	
}
