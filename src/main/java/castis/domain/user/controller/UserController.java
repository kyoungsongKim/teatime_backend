package castis.domain.user.controller;

import castis.domain.report.dto.ReportUserRequestDto;
import castis.domain.user.dto.PasswordDto;
import castis.domain.user.dto.UserDto;
import castis.domain.user.dto.UserUpdateDto;
import castis.domain.user.service.UserService;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping(value = "/user/info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changeMyInfo(@RequestBody UserUpdateDto updateDto) throws Exception {
        log.info("request /user/info change user detail : {}", updateDto);
        userService.updateInfo(updateDto);
        return ResponseEntity.ok().body("user detail changed!");
    }

    @RequestMapping(value = "/user/pass", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changePassword(@RequestBody PasswordDto passwordDto) throws Exception {
        userService.updatePassword(passwordDto);
        return ResponseEntity.ok().body("password changed!");
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<UserDto> getUserInfoById(@PathVariable(value = "id") String id) throws Exception {
        UserDto userInfo = userService.getUserDtoById(id);
        if (userInfo != null) {
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }

    @RequestMapping(value = "/user/except", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUserInfoExceptId(@RequestParam(value = "userId") String userId) throws Exception {
        List<UserDto> userDtoList = userService.findAllUserDtoExceptId(userId);
        if (userDtoList != null) {
            return new ResponseEntity<>(userDtoList, HttpStatus.OK);
        } else {
            throw new UserNotFoundException("User List Not Found");
        }
    }

    @RequestMapping(value = "/user/id", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getUserIdList() {
        List<UserDto> userList = userService.getUserDtoList();
        return new ResponseEntity<>(userList.stream().map(UserDto::getId).collect(Collectors.toList()),
                HttpStatus.OK);
    }
}
