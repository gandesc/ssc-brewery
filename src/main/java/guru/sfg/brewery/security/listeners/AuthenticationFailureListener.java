package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthenticationFailureListener {

    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login failure");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();
            builder.username(token.getPrincipal().toString());
            userRepository.findByUsername((String) token.getPrincipal()).ifPresent(builder::user);

            log.debug("Attempted Username: " + token.getPrincipal());

            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                builder.sourceIp(details.getRemoteAddress());

                log.debug("Source IP: " + details.getRemoteAddress());
            }

            LoginFailure failure = loginFailureRepository.save(builder.build());

            log.debug("Persisted login failure with id: " + failure.getId());

            if(failure.getUser() != null) {
                lockUserAccount(failure.getUser());
            }
        }

    }

    private void lockUserAccount(User user) {
        List<LoginFailure> failures = loginFailureRepository.findAllByUserAndCreatedAtIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if(failures.size() > 3) {
            log.debug("Locking User Account...");

            user.setAccountNonLocked(false);

            userRepository.save(user);
        }
    }
}
