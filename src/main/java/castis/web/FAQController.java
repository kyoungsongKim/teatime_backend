package castis.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Handles requests for the application home page.
 */
@Controller
public class FAQController {

	@Autowired
	ApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(FAQController.class);

	String calendarTimeFormat = "yyyy-MM-dd";
	DateFormat format = new SimpleDateFormat(calendarTimeFormat);

	@RequestMapping(value = "faqmain", method = RequestMethod.GET)
	public String getPointHistory(Model model) {
		logger.info("call FAQ Main");
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = user.getUsername(); // get logged in username
		model.addAttribute("username", name);
		return "faq_main";
	}

}
