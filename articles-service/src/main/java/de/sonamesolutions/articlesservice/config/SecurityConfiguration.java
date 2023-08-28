package de.sonamesolutions.articlesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers("/articles/**").permitAll()  // Allow anonymous access to /public/** endpoints
                .anyExchange().authenticated()          // Require authentication for all other endpoints
                .and()
                .httpBasic()
                .and()
                .formLogin().disable()                    // Disable form-based login for WebFlux
                .csrf().disable()                         // Disable CSRF protection for WebFlux
                .build();
    }
}
