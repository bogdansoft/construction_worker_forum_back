package com.construction_worker_forum_back.config.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("dev")
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class DevSecurityConfig {
    JwtFilter jwtFilter;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .headers(configurer -> configurer
                        .frameOptions().disable())
                .authorizeRequests(configurer -> configurer
                        .mvcMatchers("/api/login").permitAll()
                        .mvcMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .mvcMatchers(HttpMethod.OPTIONS, "/users/summaries").permitAll()
                        .mvcMatchers(HttpMethod.OPTIONS, "/messages/**").permitAll()
                        .mvcMatchers(HttpMethod.POST, "/api/user/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/post/likers/{id}").authenticated() //to be removed
                        .mvcMatchers(HttpMethod.GET, "/api/comment/likers/{id}").authenticated() //to be removed
                        .mvcMatchers(HttpMethod.GET, "/api/post/followers/{id}").authenticated() //to be removed
                        .mvcMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/topic/**").permitAll()
                        .mvcMatchers("/ws").anonymous() //to be removed
                        .mvcMatchers("/ws/**").anonymous() //to be removed
                        .mvcMatchers(HttpMethod.GET, "/messages/**").authenticated() //to be removed
                        .mvcMatchers(HttpMethod.GET, "/users/summaries").authenticated() //to be removed
                        .mvcMatchers("/api/post/**").hasAuthority("ACTIVE") //move it to controller
                        .mvcMatchers("/api/comment/**").hasAuthority("ACTIVE") //move it to controller
                        .mvcMatchers("/api/**").authenticated()
                        .mvcMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().denyAll())
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint()).and()
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return ((request, response, authException) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage()));
    }
}
