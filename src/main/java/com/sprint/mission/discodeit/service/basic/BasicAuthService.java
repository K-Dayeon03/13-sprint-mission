package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public UserResponse login(LoginRequest loginRequest) {
        //username과 password가 일치하는 유저 찾기
        return userRepository.findByAll().stream()
                .filter(u -> u.getUsername().equals(loginRequest.username())
                && u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .map(user->{
                    UserStatus userStatus = userStatusRepository.findById(user.getId());
                    return UserResponse.from(user, userStatus);
                })
                .orElseThrow(() -> new IllegalArgumentException("유저 이름 또는 비밀번호가 일치하지 않습니다."));

    }
}
