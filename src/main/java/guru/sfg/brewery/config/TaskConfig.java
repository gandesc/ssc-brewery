package guru.sfg.brewery.config;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
@Configuration
public class TaskConfig {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 5000) // in practice, longer
    public void unlockAccounts() {
        List<User> lockedUsers = userRepository
                .findAllByAccountNonLockedAndLastModifiedDateIsBefore(
                        false,
                        Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)) // in practice, longer
                );

        if (lockedUsers.size() > 0) {
            log.debug("Locked Accounts Found, Unlocking");

            lockedUsers.forEach(user -> user.setAccountNonLocked(true));

            userRepository.saveAll(lockedUsers);
        }
    }
}
