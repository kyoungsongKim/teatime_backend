package castis.domain.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthProvider {

    @Value("${spring.jwt.secret.at}")
    private static String JWT_SECRET;

    // 토큰 유효시간
    private static final int JWT_EXPIRATION_MS = 604800000;

    // jwt 토큰 생성
    public static String generateToken(Authentication authentication) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject((String) authentication.getPrincipal()) // 사용자
                .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                .setExpiration(expiryDate) // 만료 시간 세팅
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                .compact();
    }

    // Jwt 토큰에서 아이디 추출
    public static String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Jwt 토큰 유효성 검사
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

//    @PostConstruct
//    protected void init() {
//        atSecretKey = Base64.getEncoder().encodeToString(atSecretKey.getBytes());
//    }
//
//    private final UserDetailsService userDetailsService;
//
//    /**
//     * @throws Exception
//     * @method 설명 : jwt 토큰 발급
//     */
//    public String createToken(
//            String userId,
//            String email,
//            String userName) {
//
//        /**
//         * 토큰발급을 위한 데이터는 UserDetails에서 담당
//         * 따라서 UserDetails를 세부 구현한 CustomUserDetails로 회원정보 전달
//         */
//        CustomUserDetails user = new CustomUserDetails(
//                userId,    // id
//                email);        // 이메일
//
//        // 유효기간설정을 위한 Date 객체 선언
//        Date date = new Date();
//
//        final JwtBuilder builder = Jwts.builder()
//                .setHeaderParam("typ", "JWT")
//                .setSubject("accesstoken").setExpiration(new Date(date.getTime() + (1000L * 60 * 60 * 12)))
//                .claim("userPk", userId)
//                .claim("email", email)
//                .claim("roles", user.getAuthorities())
//                .signWith(SignatureAlgorithm.HS256, atSecretKey);
//
//        return builder.compact();
//    }
//
//    // 토큰에서 회원 정보 추출
//    public String getUserPk(String token) {
//        return Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token).getBody().getSubject();
//    }
//
//    /**
//     * @method 설명 : 컨텍스트에 해당 유저에 대한 권한을 전달하고 API 접근 전 접근 권한을 확인하여 접근 허용 또는 거부를 진행한다.
//     */
//    @SuppressWarnings("unchecked")
//    public Authentication getAuthentication(String token) {
//
//        // 토큰 기반으로 유저의 정보 파싱
//        Claims claims = Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token).getBody();
//
//        long userPk = claims.get("userPk", Integer.class);
//        String email = claims.get("email", String.class);
//
//        CustomUserDetails userDetails = new CustomUserDetails(userPk, email);
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }
//
//    /**
//     * @method 설명 : request객체 헤더에 담겨 있는 토큰 가져오기
//     */
//    public String resolveToken(HttpServletRequest request) {
//        return request.getHeader("accesstoken");
//    }
//
//    /**
//     * @method 설명 : 토큰 유효시간 만료여부 검사 실행
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jws<Claims> claims = Jwts.parser().setSigningKey(atSecretKey).parseClaimsJws(token);
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (Exception e) {
//            return false;
//        }
//    }

}
