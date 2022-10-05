package castis.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ApiController {
	
	@Autowired
	ApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@RequestMapping(value = "/api/v1/ping", method = RequestMethod.GET)
	public String apiPing(HttpServletRequest request) {
		logger.info("[API] apiPing");
		return "pong.. make this when needs...Open api";
	}
}
