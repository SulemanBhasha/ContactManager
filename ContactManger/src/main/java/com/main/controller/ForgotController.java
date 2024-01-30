package com.main.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.main.doa.UserRespository;
import com.main.entities.User;
import com.main.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRespository respository;
	@Autowired
	private EmailService emailService;
	Random random = new Random(1000);
//	email id form id handler
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forget_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session)
	{
		
		System.out.println(email +"hello");
//		generating ne wrandon numebr by using random class
		int otp = random.nextInt(9999);
		System.out.println(otp+"-----------------------------------------0tp");
		
		String subject="OTP FROM manoj dumma";
		String message="Your OTP is-->>"+otp;
		String to=email;
		
		 User user = this.respository.getUserByUsername(email);
		 if(user!=null )
		 {
		Boolean flag = this.emailService.sendEmail(email, subject, message);
		
			session.setAttribute("myotp", otp);
		session.setAttribute("email", email);
			return "verify_otp";
		
		 }
		else
		{
			session.setAttribute("message", "entered wrong email id");
			
			return "forget_email_form";

		}
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session)
	{
		int myotp= (int) session.getAttribute("myotp");
		String mail = (String) session.getAttribute("email");
		if(myotp==otp)
		{
			
			 User user = this.respository.getUserByUsername(mail);
			if(user==null)
			{
//				send error mesage
				return "signup";
			}
			else
			{
				System.out.println("you have entered kdwchdwcdbwhcdcdcdecdw");
				return "password_change_form";
				
			}
			
			
		}
		else
		{
			session.setAttribute("message", "you have entered wrong otp");
			return "forget_email_form";
		}
		
	}
	
	
	
	@PostMapping("/newpassword")
	public String changepassword( @RequestParam("password") String Password,HttpSession session )
	{
		String  email =(String) session.getAttribute("email");
		User user = this.respository.getUserByUsername(email);
	user.setPasword(this.bCryptPasswordEncoder.encode(Password));
	this.respository.save(user);

		
		return "login";
	}
	

}
