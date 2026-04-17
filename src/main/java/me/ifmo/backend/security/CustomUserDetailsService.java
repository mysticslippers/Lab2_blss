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

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email " + username + " not found"
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

        if (user.getRoles() == null) {
            return authorities;
        }

        for (Role role : user.getRoles()) {
            if (role.getName() != null && !role.getName().isBlank()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }

            if (role.getPrivileges() != null) {
                for (Privilege privilege : role.getPrivileges()) {
                    if (privilege.getName() != null && !privilege.getName().isBlank()) {
                        authorities.add(new SimpleGrantedAuthority(privilege.getName()));
                    }
                }
            }
        }

        return authorities;
    }
}