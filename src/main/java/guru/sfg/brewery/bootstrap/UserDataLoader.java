package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {
    private final AuthorityRepository authorityRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String[] args) {
        if (authorityRepository.count() > 0 || userRepository.count() > 0) {
            return;
        }

        loadSecurityData();
    }

    private void loadSecurityData() {

        //beer auths
        Authority createBeer = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority readBeer = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority updateBeer = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority deleteBeer = authorityRepository.save(Authority.builder().permission("beer.delete").build());

        Role adminRole = Role.builder().name("ADMIN")
                .authorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer))
                .build();

        Role customerRole = Role.builder().name("CUSTOMER")
                .authorities(Set.of(readBeer))
                .build();

        Role userRole = Role.builder().name("USER")
                .authorities(Set.of(readBeer))
                .build();

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        User adminUser = User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build();

        userRepository.save(adminUser);

        User simpleUser = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build();

        userRepository.save(simpleUser);

        User scottUser = User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build();

        userRepository.save(scottUser);
    }
}
