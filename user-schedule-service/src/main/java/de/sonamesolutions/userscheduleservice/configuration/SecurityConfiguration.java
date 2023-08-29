package de.sonamesolutions.userscheduleservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private static final String SCOPE_PREFIX = "SCOPE_";

    @Value("${resourceserver.id}")
    private String resourceServerId;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authz -> authz.requestMatchers(HttpMethod.GET, "/user-schedule/schedule/**")
                        .hasAuthority(getAuthority(SecurityScope.SCHEDULE_READ))
                        .requestMatchers(HttpMethod.PUT, "/user-schedule/schedule/**")
                        .hasAuthority(getAuthority(SecurityScope.SCHEDULE_UPDATE))
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }

    private String getAuthority(SecurityScope scope) {
        return SCOPE_PREFIX + resourceServerId + "/" + scope.getId();
    }
}
