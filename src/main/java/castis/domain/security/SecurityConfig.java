package castis.domain.security;

import castis.domain.security.filter.CustomFilter;
import castis.domain.security.jwt.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${useJwt}")
    private static boolean useJwt;

    private final AuthProvider authProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge((long) 3600);
        configuration.setAllowCredentials(false);
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("content-disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Override
    public void configure(WebSecurity web) {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (useJwt) {
            http
                    .httpBasic().disable()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers("/static/css/**").permitAll()          // css
                    .antMatchers("/static/js/**").permitAll()           // js
                    .antMatchers("/static/img/**").permitAll()          // ims
                    .antMatchers("/static/**").permitAll()              // statics
                    .antMatchers("/signup").permitAll()          // 회원가입
                    .antMatchers("/login/**").permitAll()        // 로그인
                    .antMatchers("/verifyToken/**").permitAll()  // 토큰
                    .antMatchers("/exception/**").permitAll()    // 예외처리 포인트
                    .anyRequest().hasRole("USER")                // 이외 나머지는 USER 권한필요
                    .and()
                    .cors()
                    .and()
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedPoint())
                    .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .and()
                    .addFilterBefore(new CustomFilter(authProvider), UsernamePasswordAuthenticationFilter.class);
        } else {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .exceptionHandling().accessDeniedPage("/")
                    .and()
                    .csrf().disable();
        }
    }
}
