package com.main.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.main.doa.ContactREspositary;
import com.main.doa.UserRespository;
import com.main.entities.Contact;
import com.main.entities.User;
import com.main.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.websocket.Session;

@Controller
@RequestMapping("/user")
public class UserController {

	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRespository respository;
	@Autowired
	private ContactREspositary contactREspositary;
	private Optional<Contact> findById;


//	methos fror adding comon data
	@ModelAttribute
	public void addcommondata(Model model,Principal principal)
	{
		String name = principal.getName();
		System.out.println("username --"+name);
//		get the data from the database from the user
		User user = respository.getUserByUsername(name);
		System.out.println(user);
		model.addAttribute("user",user);	
	}
	
	
	
	
	
	
	@RequestMapping("/index")
	public  String dashboard(Model model , Principal principal)
	{
		String name = principal.getName();
		System.out.println("username --"+name);
//		get the data from the database from the user
		User user = respository.getUserByUsername(name);
		System.out.println(user);
		model.addAttribute("user",user);
		return "normal/user_dashboard";
	}
//	open add form handler
	@GetMapping("/add-contact")
	public String openaddContactForm( @Valid Model model,Principal principal)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		
		
		return  "normal/add_contact_form";
	}

//	processing to save contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact,
			@RequestParam("image-profile") MultipartFile file,
			Model model,
			HttpSession httpSession,Principal principal)
	{
		try {	
		String name = principal.getName();
		User userByUsername = respository.getUserByUsername(name);
	 	contact.setUser(userByUsername);
		userByUsername.getContacts().add(contact);
//		processing and uploading profile-image
		if(file.isEmpty())
		{
			System.out.println("file is empty");
			contact.setImage("profile.png");
		}
		else {
//			upload the file to folder
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/IMAGE").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+contact.getCid()+file.getOriginalFilename());
			
			long copy = Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING );
			System.out.println("sysout file is uploaded");
		}
		
		
		
		User save = this.respository.save(userByUsername);
		System.out.println(save +"added to databases");
		System.out.println("contact saved ");
		
		System.out.println("data--"+contact);
		 model.addAttribute("contact", new Contact() );
			httpSession.setAttribute("message", new Message("Successfully  register!!!", "alert-success"));
			return "normal/add_contact_form";
		}
		
		catch(Exception e){
			e.printStackTrace();
			model.addAttribute("contact", contact);
			System.out.println("exception accured");
			httpSession.setAttribute("message", new Message("something went wrong....", "alert-danger"));
			return "normal/add_contact_form";

		}
		

		
		
		
		
		
	}
	
	
//	handler for view contacts
//	per page we need 10 contacts current page= 0 
	@GetMapping("/view-contact/{page}")
	public String viewContact( @PathVariable("page") int page, Model model,Principal principal)
	{
		String name = principal.getName();
		User user = this.respository.getUserByUsername(name);
		
		Pageable pagable = PageRequest.of(page, 8) ;
		Page<Contact> contacts = this.contactREspositary.findContacsByUser(user.getId(),pagable);
		
//		Collections.sort(contacts,new Comparator<Contact>() {
//			public int compare(Contact c1 ,Contact c2)
//			{
//				return c1.getName().compareToIgnoreCase(c2.getName());
//			}
//		});
		Model addAttribute = model.addAttribute("contact", contacts);
		model.addAttribute("currentpage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("title","view-contacts");
		return "normal/show_contacts";
	}
	
	
	
	
//	show perticular contact details
	@GetMapping("/{cid}/contact")
	public String  showPerticularContact(@PathVariable("cid") Integer cid,Model model,Principal principal)
	{
		try {
		String name = principal.getName();
		User user = this.respository.getUserByUsername(name);
		System.out.println("cid------------------------------------------------"+cid);
		Optional<Contact> contactOptional = this.contactREspositary.findById(cid);
		Contact contact = contactOptional.get();
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
			
		}
		return "normal/contact_details";
		}
		catch (Exception e) {
			e.printStackTrace();
			return "normal/noContactFound";
		}
		
	}
	
//	delete Contact handler
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid,Principal principal,Model model,HttpSession httpSession)
	{
		try {
			
		
		  Contact contact = this.contactREspositary.findById(cid).get();
		  
		     User user= this.respository.getUserByUsername(principal.getName());
		      boolean remove = user.getContacts().remove(contact);
		      this.respository.save(user);
		     
//		       Checking the id and ADMIN id 
		       System.out.println("deleted");
		       httpSession.setAttribute("message", new Message("Contact deleted sucessfully", "alert-success") );
		      
		}
		catch (Exception e) {
			 e.printStackTrace();
	       }
			
		 return  "redirect:/user/view-contact/0";
		
		
	}
//	 open update contact handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") int cid,Model model)
	{
		
		model.addAttribute("title","upadte page");
		
	 Contact contact = this.contactREspositary.findById(cid).get();
	 
	 model.addAttribute("contact",contact);
		return "normal/updateform";
	}
	
//	updating cantact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("image-profile") MultipartFile file,Model model,
			HttpSession session,Principal principal)
	{
		try {
//			old contact deteils
			Contact oldContact = this.contactREspositary.findById(contact.getCid()).get();
			if(!file.isEmpty())
			{
//				file work 
				File deleteFile = new ClassPathResource("static/IMAGE").getFile();
				File  file1= new File(deleteFile,oldContact.getImage());
				file1.delete();
				contact.setImage(contact.getCid()+file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/IMAGE").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+contact.getCid()+file.getOriginalFilename());
				
				long copy = Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING );
				System.out.println("sysout file is uploaded");
				
			}
			else
			{
				oldContact.setImage(oldContact.getImage());
				contact.setImage("profile.png");
			}
			User user= this.respository.getUserByUsername(principal.getName());
			contact.setUser(user);
			this.contactREspositary.save(contact);
			session.setAttribute("message", new Message("youtr contact is updated...", "alert-success"));
		}
		catch (Exception e) {
			
		}

		return "normal/contact_details";
	}
	
	
//	your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title","user profile");
		
		return "normal/profile";
	}
	//	search engine
	@GetMapping("/search/{cid}")
	public String serachtag(@PathVariable("cid") int cid)
	{
		
		return "redirect:/user/"+cid+"/contact";
		
	}
	
	@GetMapping("/openSetting")
	public String openSetting()
	{
		System.out.println("hello jikise ho sare");
		return "normal/setting";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, 
			@RequestParam("newPassword") String newPassword ,Principal principal,
			HttpSession session) 
	{
		String username = principal.getName();
		User currentUser = this.respository.getUserByUsername(username);
		System.out.println(currentUser.getPasword());
				
		System.out.println(oldPassword);
		System.out.println(newPassword);
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPasword()))
		{
//			change the password
			currentUser.setPasword(this.bCryptPasswordEncoder.encode(newPassword));
			this.respository.save(currentUser);
			session.setAttribute("message1", new Message("Your password id succesfully changed !!", "alert-success"));
		}
		else
		{
			session.setAttribute("message1", new Message("enter old password currectly!!!", "alert-danger"));

			
		}
		return "redirect:/user/openSetting";
	}
	
}
