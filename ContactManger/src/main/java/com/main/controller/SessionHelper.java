package com.main.controller;



import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;


@Component
public class SessionHelper {

		public void removeMessage()
		{
			
			try {
				 ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			        HttpSession session = attributes.getRequest().getSession();
			        session.removeAttribute("message");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
