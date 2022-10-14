package castis.exception.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/exception")
public class ExceptionController {


    /**
     * @method 설명 : 401 Error 반환용 컨트롤러
     */
    @RequestMapping(value = {"/entrypoint"})
    public ResponseEntity<String> entrypointException() {
        throw new AuthenticationEntryPointException();
    }

    /**
     * @method 설명 : 403 Error 반환용 컨트롤러
     */
    @RequestMapping(value = {"/accessdenied"})
    public ResponseEntity<String> accessdeniedException() {
        throw new ForbiddenException("Access Deniend");
    }
}
