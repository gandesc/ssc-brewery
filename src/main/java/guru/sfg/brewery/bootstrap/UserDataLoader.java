package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {
    private final AuthorityRepository authorityRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String[] args) {
        if (authorityRepository.count() > 0 || userRepository.count() > 0) {
            return;
        }

        loadSecurityData();
    }

    private void loadSecurityData() {
        Authority admin = Authority.builder()
                .role("ADMIN")
                .build();

        authorityRepository.save(admin);

        Authority user = Authority.builder()
                .role("USER")
                .build();

        authorityRepository.save(user);

        Authority customer = Authority.builder()
                .role("CUSTOMER")
                .build();

        authorityRepository.save(customer);

        User adminUser = User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .authority(admin)
                .build();

        userRepository.save(adminUser);

        User simpleUser = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authority(user)
                .build();

        userRepository.save(simpleUser);

        User scottUser = User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .authority(customer)
                .build();

        userRepository.save(scottUser);
    }
}
