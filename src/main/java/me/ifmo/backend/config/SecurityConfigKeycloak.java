package me.ifmo.backend.config;

import me.ifmo.backend.security.keycloak.KeycloakJwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!keycloak")
@EnableMethodSecurity
public class SecurityConfigKeycloak {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            KeycloakJwtAuthConverter keycloakJwtAuthConverter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/courses/**").hasAuthority("COURSE_READ")

                        .requestMatchers(HttpMethod.POST, "/enrollments").hasAuthority("ENROLLMENT_CREATE")
                        .requestMatchers(HttpMethod.POST, "/enrollments/admin").hasAuthority("ENROLLMENT_CREATE_ALL")
                        .requestMatchers(HttpMethod.GET, "/enrollments/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/payments/enrollment/**").hasAuthority("PAYMENT_CREATE")
                        .requestMatchers(HttpMethod.GET, "/payments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/payments/webhook").hasAuthority("PAYMENT_CALLBACK_HANDLE")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthConverter))
                );

        return http.build();
    }
}