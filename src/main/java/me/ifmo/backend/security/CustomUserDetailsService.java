package me.ifmo.backend.security;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Privilege;
import me.ifmo.backend.entities.Role;
import me.ifmo.backend.entities.User;
import me.ifmo.backend.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedUsername = normalize(username);

        if (normalizedUsername == null) {
            throw new UsernameNotFoundException("Username must not be blank");
        }

        User user = userRepository.findByEmail(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email " + normalizedUsername + " not found"
                ));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .disabled(Boolean.FALSE.equals(user.getEnabled()))
                .accountLocked(Boolean.FALSE.equals(user.getAccountNonLocked()))
                .credentialsExpired(Boolean.FALSE.equals(user.getCredentialsNonExpired()))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return authorities;
        }

        for (Role role : user.getRoles()) {
            addRoleAuthority(authorities, role);
            addPrivilegeAuthorities(authorities, role);
        }

        return authorities;
    }

    private void addRoleAuthority(Set<GrantedAuthority> authorities, Role role) {
        if (role == null) {
            return;
        }

        String roleName = normalize(role.getName());
        if (roleName == null) {
            return;
        }

        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + roleName));
    }

    private void addPrivilegeAuthorities(Set<GrantedAuthority> authorities, Role role) {
        if (role == null || role.getPrivileges() == null || role.getPrivileges().isEmpty()) {
            return;
        }

        for (Privilege privilege : role.getPrivileges()) {
            if (privilege == null) {
                continue;
            }

            String privilegeName = normalize(privilege.getName());
            if (privilegeName == null) {
                continue;
            }

            authorities.add(new SimpleGrantedAuthority(privilegeName));
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}