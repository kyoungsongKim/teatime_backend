package castis.domain.sign;

import castis.domain.security.jwt.AuthProvider;
import castis.domain.sign.dto.AuthenticationDto;
import castis.domain.sign.dto.JoinDto;
import castis.domain.sign.dto.LoginDto;
import castis.domain.sign.dto.TokenDto;
import castis.domain.sign.service.SignService;
import castis.domain.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = { "" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class SignController {

    private final SignService apiSignService;

    private final AuthProvider authProvider;

    @PostMapping(value = { "signup","api/auth/sign-up" })
    public ResponseEntity<AuthenticationDto> appJoin(@RequestBody JoinDto joinDto) throws Exception {
        String pass = joinDto.getPassword();
        boolean isJoinSuccess = apiSignService.regMember(joinDto);

        if (isJoinSuccess) {
            LoginDto loginDto = new LoginDto();
            loginDto.setUserId(joinDto.getId());
            loginDto.setPassword(pass);

            AuthenticationDto authentication = apiSignService.loginMember(loginDto);
            authentication.setApiToken(
                    authProvider.createToken(authentication.getUserId(), authentication.getRealName(), "USER"));

            return ResponseEntity.ok().body(authentication);
        } else {
            AuthenticationDto authentication = new AuthenticationDto();
            return ResponseEntity.ok().body(authentication);
        }
    }

    @PostMapping(value = { "/login", "api/auth/sign-in" })
    public ResponseEntity<AuthenticationDto> appLogin(@Valid @RequestBody LoginDto loginDto) throws Exception {
        AuthenticationDto authentication = apiSignService.loginMember(loginDto);
        boolean isAdmin = apiSignService.checkIsAdmin(authentication.getUserId());
        String accessToken = authProvider.createToken(authentication.getUserId(), authentication.getRealName(),
                isAdmin ? "ADMIN" : "USER");
        authentication.setApiToken(accessToken);
        //for new front-end
        authentication.setAccessToken(accessToken);

        return ResponseEntity.ok().body(authentication);
    }

    @PostMapping(value = { "/verifyToken" })
    public ResponseEntity<CustomUserDetails> verifyToken(@RequestBody TokenDto tokenDto) throws Exception {
        Authentication authentication = authProvider.getAuthentication(tokenDto.getApiToken());
        return ResponseEntity.ok().body((CustomUserDetails) authentication.getPrincipal());
    }

    @GetMapping(value = { "api/auth/me" })
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("api/auth/me");
            // "Bearer " 제거 후 토큰 추출
            String token = authorizationHeader.replace("Bearer ", "").replace("bearer ", "");

            // 토큰 검증
            if (!authProvider.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
            Authentication currentAuth = authProvider.getAuthentication(token);
            if (currentAuth == null || !currentAuth.isAuthenticated()) {
                return ResponseEntity.status(401).body("User is not authenticated");
            }
            return ResponseEntity.ok(currentAuth);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("An error occurred: " + e.getMessage());
        }
    }
}
