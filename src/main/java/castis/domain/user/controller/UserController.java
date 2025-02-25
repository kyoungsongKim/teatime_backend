package castis.domain.user.controller;

import castis.domain.report.dto.ReportUserRequestDto;
import castis.domain.security.jwt.AuthProvider;
import castis.domain.user.CustomUserDetails;
import castis.domain.user.dto.*;
import castis.domain.user.entity.User;
import castis.domain.user.service.UserService;
import castis.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthProvider authProvider;

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

    @RequestMapping(value = "/user/details", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity postUserDetails(@RequestBody UserDetailDto userDetailDto) throws Exception {
        log.info("request /user/detail change user detail : {}", userDetailDto);
        userService.postUserDetails(userDetailDto);
        return ResponseEntity.ok().body("user detail changed!");
    }

    @RequestMapping(value = "/user/details/social", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity postUserDetailsSocial(@RequestBody UserDetailSocialDto userDetailSocialDto) throws Exception {
        log.info("request /user/detail/social change user detail social: {}", userDetailSocialDto);
        userService.postUserDetailsSocial(userDetailSocialDto);
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

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getUserList(HttpServletRequest httpServletRequest) {
        CustomUserDetails userDetails = authProvider.getUserDetails(httpServletRequest);
        if (userDetails.getRoles().contains("ROLE_ADMIN")) {
            User user = userService.getUser(userDetails.getUserId());
            List<UserDto> userList = userService.getUserTeamDtoList(user.getTeamName());
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } else if (userDetails.getRoles().contains("ROLE_SUPER_ADMIN")) {
            List<UserDto> userList = userService.getUserDtoList();
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/admins", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAdminList() {
        List<UserDto> result = userService.getAdminList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 삭제시 teamname을 external로 변경
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteUserById(@PathVariable(value = "userId") String userId) throws Exception {
        User userInfo = userService.getUser(userId);
        if (userInfo != null) {
            userService.updateTeamName(userInfo);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }
}
