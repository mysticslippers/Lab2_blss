package me.ifmo.backend.security.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(extractRealmRoles(jwt));
        authorities.addAll(extractPrivilegeClaims(jwt));

        String principalName = jwt.getClaimAsString("preferred_username");
        if (principalName == null || principalName.isBlank()) {
            principalName = jwt.getSubject();
        }

        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Object realmAccessObject = jwt.getClaim("realm_access");
        if (!(realmAccessObject instanceof Map<?, ?> realmAccess)) {
            return Collections.emptySet();
        }

        Object rolesObject = realmAccess.get("roles");
        if (!(rolesObject instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Object role : roles) {
            if (role instanceof String roleName && !roleName.isBlank()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName.trim().toUpperCase()));
            }
        }

        return authorities;
    }

    private Collection<? extends GrantedAuthority> extractPrivilegeClaims(Jwt jwt) {
        Object privilegesObject = jwt.getClaim("privileges");
        if (!(privilegesObject instanceof Collection<?> privileges)) {
            return Collections.emptySet();
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Object privilege : privileges) {
            if (privilege instanceof String privilegeName && !privilegeName.isBlank()) {
                authorities.add(new SimpleGrantedAuthority(privilegeName.trim()));
            }
        }

        return authorities;
    }
}