package castis.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class ApiController {

    @RequestMapping(value = "/api/v1/ping", method = RequestMethod.GET)
    public String apiPing(HttpServletRequest request) {
        log.info("[API] apiPing");
        return "pong.. make this when needs...Open api";
    }
}
