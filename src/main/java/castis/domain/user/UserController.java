package castis.domain.user;

import castis.domain.report.ReportUserRequestDto;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserDto> getUserInfo(@RequestBody ReportUserRequestDto req) throws Exception {
        UserDto userInfo = userService.getUserDtoById(req.getUserId());
        if (userInfo != null) {
            return ResponseEntity.ok().body(userInfo);
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }
}
