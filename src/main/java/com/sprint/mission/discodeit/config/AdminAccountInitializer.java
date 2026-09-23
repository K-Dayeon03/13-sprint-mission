package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // ADMIN 권한 계정이 이미 있으면 중복 생성하지 않는다.
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        User admin = new User(
                "admin",
                passwordEncoder.encode("admin1234!"),
                "admin@discodeit.com",
                null
        );
        admin.updateRole(UserRole.ADMIN);

        userRepository.save(admin);
        log.info("Admin account initialized. username={}", admin.getUsername());
    }
}
