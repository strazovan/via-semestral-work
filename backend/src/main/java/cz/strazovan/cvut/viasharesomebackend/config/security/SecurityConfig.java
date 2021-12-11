package cz.strazovan.cvut.viasharesomebackend.config.security;

import cz.strazovan.cvut.viasharesomebackend.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .logout()
                .logoutSuccessUrl(this.frontendUrl)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .successHandler(this.loginSuccessHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint());
    }

}