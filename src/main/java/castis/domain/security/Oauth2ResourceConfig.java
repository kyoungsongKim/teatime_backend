package castis.domain.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class Oauth2ResourceConfig extends ResourceServerConfigurerAdapter {

    // API 별 필요한 인증정보 설정
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/devices/**")
                .access("#oauth2.hasAnyScope('read')")
                .antMatchers(HttpMethod.POST, "/api/**").permitAll()
                .anyRequest()
                .authenticated();
    }
}
