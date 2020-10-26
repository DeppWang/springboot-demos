package wang.depp.session.web.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import wang.depp.session.entity.User;
import wang.depp.session.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController extends BaseController {

	final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String KEY_USER = "__user__";

	@Autowired
	UserService userService;

	@GetMapping("/")
	public ModelAndView index() {
		User user = getKeyUser(KEY_USER);
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
		} catch (RuntimeException e) {
			logger.warn(e.getMessage(), e);
			return new ModelAndView("register.html", Map.of("email", email, "error", "Register failed"));
		}
		return new ModelAndView("redirect:/signin");
	}

	@GetMapping("/signin")
	public ModelAndView signin() {
		User user = getKeyUser(KEY_USER);
		if (user != null) {
			return new ModelAndView("redirect:/profile");
		}
		return new ModelAndView("signin.html");
	}

	@PostMapping("/signin")
	public ModelAndView doSignin(@RequestParam("email") String email, @RequestParam("password") String password) {
		try {
			User user = userService.signin(email, password);
			setAttribute(KEY_USER, user);
		} catch (RuntimeException e) {
			return new ModelAndView("signin.html", Map.of("email", email, "error", "Signin failed"));
		}
		return new ModelAndView("redirect:/profile");
	}

	@GetMapping("/profile")
	public ModelAndView profile() {
		User user = getKeyUser(KEY_USER);
		if (user == null) {
			return new ModelAndView("redirect:/signin");
		}
		return new ModelAndView("profile.html", Map.of("user", user));
	}

	@GetMapping("/signout")
	public String signout() {
		removeAttribute(KEY_USER);
		return "redirect:/signin";
	}


}
