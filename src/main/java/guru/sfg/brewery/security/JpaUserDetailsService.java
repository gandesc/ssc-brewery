package guru.sfg.brewery.security;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User model = repository.findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("User " + s + " not found"));

        return new org.springframework.security.core.userdetails.User(
                model.getUsername(),
                model.getPassword(),
                model.getEnabled(),
                model.getAccountNonExpired(),
                model.getCredentialsNonExpired(),
                model.getAccountNonLocked(),
                convertToSpringAuthorities(model.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return new HashSet<>();
        } else {
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
    }
}
