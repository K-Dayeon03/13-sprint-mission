package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BadRequestException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        // 1. 입력받은 username으로 유저를 리포지토리에서 바로 찾습니다.
        User user = userRepository.findByUsername(loginRequest.username())
                // 2. 유저가 존재한다면, 입력된 password와 일치하는지 필터링합니다.
                .filter(u -> u.getPassword().equals(loginRequest.password()))
                // 3. 유저가 없거나 비밀번호가 틀렸다면 예외를 던집니다.
                .orElseThrow(() -> new BadRequestException("유저 이름 또는 비밀번호가 일치하지 않습니다."));

        // 4. 유저 상태 정보 조회
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 UserStatus입니다."));

        // 5. 로그인 성공 시 마지막 접속 시간 갱신
        userStatus.updateLastActiveAt(Instant.now());
        userStatusRepository.save(userStatus);

        // 6. Response DTO로 변환하여 반환
        return UserResponse.from(user, userStatus);
    }
}
