package castis.domain.sign;

import castis.domain.security.jwt.AuthProvider;
import castis.domain.sign.dto.AuthenticationDto;
import castis.domain.sign.dto.JoinDto;
import castis.domain.sign.dto.LoginDto;
import castis.domain.sign.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = {""}, produces = MediaType.APPLICATION_JSON_VALUE)
public class SignController {

    private final SignService apiSignService;

    private final AuthProvider authProvider;

    @PostMapping(value = {"signup"})
    public ResponseEntity<AuthenticationDto> appJoin(@RequestBody JoinDto joinDto) throws Exception {
        String pass = joinDto.getPassword();
        boolean isJoinSuccess = apiSignService.regMember(joinDto);

        if (isJoinSuccess) {
            LoginDto loginDto = new LoginDto();
            loginDto.setUserId(joinDto.getId());
            loginDto.setPassword(pass);

            AuthenticationDto authentication = apiSignService.loginMember(loginDto);
            authentication.setApiToken(authProvider.createToken(authentication.getUserId(), authentication.getEmail(), "USER"));

            return ResponseEntity.ok().body(authentication);
        } else {
            AuthenticationDto authentication = new AuthenticationDto();
            return ResponseEntity.ok().body(authentication);
        }
    }

    @PostMapping(value = {"/login"})
    public ResponseEntity<AuthenticationDto> appLogin(@Valid @RequestBody LoginDto loginDto) throws Exception {
        AuthenticationDto authentication = apiSignService.loginMember(loginDto);
        authentication.setApiToken(authProvider.createToken(authentication.getUserId(), authentication.getEmail(), "USER"));
        return ResponseEntity.ok().body(authentication);
    }
}
