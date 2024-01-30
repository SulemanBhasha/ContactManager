package com.main.configue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.main.doa.UserRespository;
import com.main.entities.User;

public class UerDetailServiceImpl  implements UserDetailsService {

	@Autowired
	private UserRespository userRespository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		fetching user by databatse
		User user = userRespository.getUserByUsername(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("Could not found exception");
		}
		CustomUserDetails customUserDetails = new  CustomUserDetails(user);
		
		return customUserDetails;
	}
	

}
