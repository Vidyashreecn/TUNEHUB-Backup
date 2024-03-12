package com.kodnest.tunehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kodnest.tunehub.entity.Song;
import com.kodnest.tunehub.entity.User;
import com.kodnest.tunehub.service.SongService;
//import org.springframework.web.bind.annotation.RequestParam;
import com.kodnest.tunehub.serviceimpl.UserServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	@Autowired
	UserServiceImpl serviceImpl;
	
	@Autowired
	SongService songService;

	/*@PostMapping("/register")
	public String addUser(@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("gender") String gender, 
			@RequestParam("role") String role,
			@RequestParam("address") String address){
		System.out.println(username+" "+email+" "
			+password+" "+gender+" "+role+" "+address);
		return "Home";
	}
	 */
	@PostMapping("/register")
	public String addUser(@ModelAttribute User user) {

		//email taken from the registration form
		String email = user.getEmail();
		//checking if email as entered in registration form is present in DB or not.
		boolean status=serviceImpl.emailExists(email);

		if(status==false) {
			serviceImpl.addUser(user);
			System.out.println("User added");
		}
		else {
			System.out.println("User already exists");
		}
		return "login";
	}

	/*System.out.println(user.getUsername()+" "+user.getEmail()+" "
	+user.getPassword()+" "+user.getGender()+" "+user.getRole()+" "+user.getAddress());*/
	
	@PostMapping("/validate")
	public String validateUser(@RequestParam("email")String email,
			@RequestParam("password") String password,HttpSession session,Model model) {
		if(serviceImpl.validateUser(email,password)==true) {
			String role=serviceImpl.getRole(email);
			
			session.setAttribute("email", email);
			
			if(role.equals("Admin")) {
				return "adminhome";
			}
			else {
				User user=serviceImpl.getUser(email);
				boolean userstatus = user.isIspremium();
				List<Song> fetchSongList=songService.fetchAllSongs();
				model.addAttribute("songs", fetchSongList);
				
				model.addAttribute("ispremium",userstatus);
				
				return "customerhome";
			}
		}
		else {
			return "login";
		}

	}
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}

}

