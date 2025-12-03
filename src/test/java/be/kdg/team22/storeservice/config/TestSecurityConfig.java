package be.kdg.team22.storeservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(mgmt -> mgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/cart/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }
}