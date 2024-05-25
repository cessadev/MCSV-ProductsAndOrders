package com.cessadev.mcsv_products.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .securityMatcher("/**").authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(config -> config.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter())));

        return httpSecurity.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Reemplaza con la URL de tu servidor JWKS
        String jwkSetUri = "http://localhost:8181/realms/microservicios-realm/protocol/openid-connect/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @SuppressWarnings("unchecked")
    static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            if (jwt.getClaims() == null || jwt.getClaims().get("realm_access") == null) {
                return List.of();
            }

            Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");

            return realmAccess.get("roles").stream()
                    .map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

}
