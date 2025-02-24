package castis.domain.security.jwt;

import castis.domain.user.CustomUserDetails;
import castis.enums.UserRole;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthProvider {

    private static final String BEARER_TYPE = "bearer ";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String ACCESS_USER_ID = "id";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 12;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.secret.signature}")
    private String signatureKey;

    @PostConstruct
    protected void init() {
        signatureKey = Base64.getEncoder().encodeToString(signatureKey.getBytes());
    }

    /**
     * @throws Exception
     * @method 설명 : jwt 토큰 발급
     */
    public String createToken(
            String id,
            String username,
            String role) {

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        final JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setExpiration(accessTokenExpiresIn)
                .claim(AUTHORITIES_KEY, role)
                .claim(ACCESS_USER_ID, id)
                .signWith(SignatureAlgorithm.HS256, signatureKey);

        return BEARER_TYPE + builder.compact();
    }

    /**
     * @method 설명 : 컨텍스트에 해당 유저에 대한 권한을 전달하고 API 접근 전 접근 권한을 확인하여 접근 허용 또는 거부를 진행한다.
     */
    @SuppressWarnings("unchecked")
    public Authentication getAuthentication(String token) {

        // 토큰 기반으로 유저의 정보 파싱
        Claims claims = Jwts.parser().setSigningKey(signatureKey).parseClaimsJws(token.replace(BEARER_TYPE, ""))
                .getBody();

        String username = claims.getSubject();
        String id = claims.get(ACCESS_USER_ID, String.class);
        String role = claims.get(AUTHORITIES_KEY, String.class);

        CustomUserDetails userDetails = new CustomUserDetails(id, username, role, token);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(signatureKey).parseClaimsJws(token.replace(BEARER_TYPE, ""))
                .getBody();
        String id = claims.get(ACCESS_USER_ID, String.class);
        return id;
    }

    public String getUserIdFromRequest(HttpServletRequest request) {
        String token = resolveToken(request);
        Claims claims = Jwts.parser().setSigningKey(signatureKey).parseClaimsJws(token.replace(BEARER_TYPE, ""))
                .getBody();
        String id = claims.get(ACCESS_USER_ID, String.class);
        return id;
    }

    public boolean isAdmin(HttpServletRequest request) {
        String token = resolveToken(request);
        CustomUserDetails user = (CustomUserDetails) getAuthentication(token).getPrincipal();
        List<String> ADMIN_ROLES = Arrays.asList(
                UserRole.ROLE_ADMIN.getValue(),
                UserRole.ROLE_SUPER_ADMIN.getValue()
        );
        return user.getRoles().stream().anyMatch(ADMIN_ROLES::contains);
    }

    public boolean isOwn(HttpServletRequest request, String userId) {
        String token = resolveToken(request);
        CustomUserDetails user = (CustomUserDetails) getAuthentication(token).getPrincipal();
        return user.getUserId().equals(userId);
    }

    /**
     * @method 설명 : request객체 헤더에 담겨 있는 토큰 가져오기
     */
    public String resolveToken(HttpServletRequest request) {
        if (request.getHeader("Authorization") == null) {
            return null;
        }
        return request.getHeader("Authorization").replace(BEARER_TYPE, "");
    }

    /**
     * @method 설명 : 토큰 유효시간 만료여부 검사 실행
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(signatureKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
