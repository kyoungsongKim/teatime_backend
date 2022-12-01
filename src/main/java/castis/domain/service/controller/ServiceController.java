package castis.domain.service.controller;

import castis.domain.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class ServiceController {
    private final ServiceService serviceService;

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public ResponseEntity getServiceList(HttpServletRequest httpServletRequest) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        return new ResponseEntity<>(serviceService.getServiceList(), HttpStatus.OK);
    }

    @RequestMapping(value = "/service/{userId}", method = RequestMethod.GET)
    public ResponseEntity getServiceListByUserId(HttpServletRequest httpServletRequest,
        @PathVariable(value = "userId") String userId) {
        log.info("request, uri[{}]", httpServletRequest.getRequestURI());
        return new ResponseEntity<>(serviceService.getServiceListWithCharge(userId), HttpStatus.OK);
    }
}
