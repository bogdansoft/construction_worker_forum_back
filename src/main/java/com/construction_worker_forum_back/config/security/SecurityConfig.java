package com.construction_worker_forum_back.config.security;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    UserDetailsService userDetailsService;
    PasswordEncoderConfig passwordEncoderConfig;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(configurer -> configurer
                        .authenticationEntryPoint(restAuthenticationEntryPoint()))
                .csrf().disable()
                .headers(configurer -> configurer
                        .frameOptions().disable())
                .authorizeRequests(configurer -> configurer
                        .mvcMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                        .mvcMatchers("/api/post/**").hasAuthority("ACTIVE")
                        .mvcMatchers("/api/comment/**").hasAuthority("ACTIVE")
                        .mvcMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return ((request, response, authException) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage()));
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> filterRegistrationBean() {
        FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtFilter());
        filterRegistrationBean.setUrlPatterns(Collections.singleton("/api/user/login"));
        return filterRegistrationBean;
    }
}
