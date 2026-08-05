package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("username으로 사용자를 조회한다")
    void findByUsername_success() {
        User user = userRepository.save(new User("woody", "password1", "woody@codeit.com", null));

        Optional<User> result = userRepository.findByUsername("woody");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("username에 맞는 사용자가 없으면 빈 Optional을 반환한다")
    void findByUsername_fail_notFound() {
        Optional<User> result = userRepository.findByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("username 또는 email 중복 여부를 확인한다")
    void existsByUsernameOrEmail_success() {
        userRepository.save(new User("woody", "password1", "woody@codeit.com", null));

        boolean existsByUsername = userRepository.existsByUsernameOrEmail("woody", "other@codeit.com");
        boolean existsByEmail = userRepository.existsByUsernameOrEmail("other", "woody@codeit.com");

        assertThat(existsByUsername).isTrue();
        assertThat(existsByEmail).isTrue();
    }

    @Test
    @DisplayName("페이징과 정렬로 사용자 목록을 조회한다")
    void findAll_pageAndSort() {
        userRepository.save(new User("charlie", "password1", "charlie@codeit.com", null));
        userRepository.save(new User("alice", "password1", "alice@codeit.com", null));
        userRepository.save(new User("bravo", "password1", "bravo@codeit.com", null));

        Page<User> page = userRepository.findAll(PageRequest.of(0, 2, Sort.by("username").ascending()));

        assertThat(page.getContent()).extracting(User::getUsername)
                .containsExactly("alice", "bravo");
        assertThat(page.hasNext()).isTrue();
        assertThat(page.getTotalElements()).isEqualTo(3);
    }
}
