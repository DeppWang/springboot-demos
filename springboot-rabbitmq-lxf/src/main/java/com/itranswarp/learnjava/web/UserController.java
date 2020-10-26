package com.itranswarp.learnjava.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.itranswarp.learnjava.entity.User;
import com.itranswarp.learnjava.messaging.LoginMessage;
import com.itranswarp.learnjava.messaging.RegistrationMessage;
import com.itranswarp.learnjava.service.MessagingService;
import com.itranswarp.learnjava.service.UserService;

@Controller
public class UserController {

	public static final String KEY_USER = "__user__";

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	UserService userService;

	@Autowired
	MessagingService messagingService;

	@GetMapping("/")
	public ModelAndView index(HttpSession session) {
		User user = (User) session.getAttribute(KEY_USER);
		Map<String, Object> model = new HashMap<>();
		if (user != null) {
			model.put("user", model);
		}
		return new ModelAndView("index.html", model);
	}

	@GetMapping("/register")
	public ModelAndView register() {
		return new ModelAndView("register.html");
	}

	@PostMapping("/register")
	public ModelAndView doRegister(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("name") String name) throws Exception {
		try {
			User user = userService.register(email, password, name);
			logger.info("user registered: {}", user.getEmail());
			messagingService.sendRegistrationMessage(RegistrationMessage.of(user.getEmail(), user.getName()));
		} catch (RuntimeException e) {
			logger.warn(e.getMessage(), e);
			return new ModelAndView("register.html", Map.of("email", email, "error", "Register failed"));
		}
		return new ModelAndView("redirect:/signin");
	}

	@GetMapping("/signin")
	public ModelAndView signin(HttpSession session) {
		User user = (User) session.getAttribute(KEY_USER);
		if (user != null) {
			return new ModelAndView("redirect:/profile");
		}
		return new ModelAndView("signin.html");
	}

	@PostMapping("/signin")
	public ModelAndView doSignin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session) {
		try {
			User user = userService.signin(email, password);
			messagingService.sendLoginMessage(LoginMessage.of(user.getEmail(), user.getName(), true));
			session.setAttribute(KEY_USER, user);
		} catch (RuntimeException e) {
			messagingService.sendLoginMessage(LoginMessage.of(email, "(unknown)", false));
			return new ModelAndView("signin.html", Map.of("email", email, "error", "Signin failed"));
		}
		return new ModelAndView("redirect:/profile");
	}

	@GetMapping("/profile")
	public ModelAndView profile(HttpSession session) {
		User user = (User) session.getAttribute(KEY_USER);
		if (user == null) {
			return new ModelAndView("redirect:/signin");
		}
		return new ModelAndView("profile.html", Map.of("user", user));
	}

	@GetMapping("/signout")
	public String signout(HttpSession session) {
		session.removeAttribute(KEY_USER);
		return "redirect:/signin";
	}
}
