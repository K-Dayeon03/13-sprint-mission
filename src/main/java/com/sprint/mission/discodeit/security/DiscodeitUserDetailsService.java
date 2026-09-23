package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    /*
    * Spring Security가 로그인할 때 자동으로 호출하는 메서드
    *
    * 예를 들어 사용자가 Username = 다연으로 로그인하면,
    * Spring Security가 loadUserByUsername("다연")을 호출한다.
    * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1. 우리 DB에서 username으로 사용자를 찾습니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다."+username));
        //2. UserReponse의 online값을 채우기 위해 UserStatus도 조회
        //유저 상태
        //없을 수도 있으니 null허용으로 처리
        UserStatus userStatus = userStatusRepository.findByUser_Id(user.getId())
                .orElse(null);
        //3. 응답 DTO로 바꾼다.
        UserResponse userResponse = userMapper.toDto(user, userStatus);
        //4. Spring Security가 이해하는 UserDetails 구현체로 감싸서 반환
        return new DiscodeitUserDetails(userResponse, user.getPassword());
    }
}
