package me.ifmo.backend.security.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String CLIENT_ID = "backend-api";
    private static final String ROLE_PREFIX = "ROLE_";

    @NonNull
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(extractRealmRoles(jwt));
        authorities.addAll(extractClientRoles(jwt));
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
                authorities.add(new SimpleGrantedAuthority(
                        ROLE_PREFIX + roleName.trim().toUpperCase()
                ));
            }
        }

        return authorities;
    }

    private Collection<? extends GrantedAuthority> extractClientRoles(Jwt jwt) {
        Object resourceAccessObject = jwt.getClaim("resource_access");
        if (!(resourceAccessObject instanceof Map<?, ?> resourceAccess)) {
            return Collections.emptySet();
        }

        Object clientAccessObject = resourceAccess.get(CLIENT_ID);
        if (!(clientAccessObject instanceof Map<?, ?> clientAccess)) {
            return Collections.emptySet();
        }

        Object rolesObject = clientAccess.get("roles");
        if (!(rolesObject instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Object role : roles) {
            if (role instanceof String roleName && !roleName.isBlank()) {
                authorities.add(new SimpleGrantedAuthority(roleName.trim()));
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