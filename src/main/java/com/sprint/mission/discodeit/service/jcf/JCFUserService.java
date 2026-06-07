//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
//import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
//import com.sprint.mission.discodeit.dto.response.UserResponse;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
////JCF활용
//
////연관 삭제위해 추가
//public class JCFUserService implements UserService {
//    private final UserRepository userRepository;
//    private final ChannelService channelService;
//    private final MessageService messageService;
//
//    public JCFUserService(ChannelService channelService, MessageService messageService, UserRepository userRepository) {
//        this.channelService = channelService;
//        this.messageService = messageService;
//        this.userRepository = userRepository;
//        // data 필드 제거
//    }
//
//    @Override
//    public User create(String username, String password, String email) {
//        if (username == null || username.isBlank() ||
//                password == null || password.isBlank()) {
//            throw new IllegalArgumentException("이름 또는 비밀번호를 작성해주세요.");
//        }
//        User user = new User(username, password, email);
//        return userRepository.save(user); // userRepository로 통일
//    }
//
//    @Override
//    public UserResponse findById(UUID id) {
//        return userRepository.findById(id); // userRepository로 통일
//    }
//
//    @Override
//    public List<UserResponse> findByAll() {
//        return userRepository.findByAll(); // userRepository로 통일
//    }
//
//    @Override
//    public UserResponse update(UUID id, UpdateUserRequest userRequest, CreateBinaryContentRequest profileImageRequest) {
//        User user = userRepository.findById(id);
//        if (user == null) {
//            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
//        }
//        if (!user.getPassword().equals(currentPassword)) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//        user.update(newUsername, newPassword, newEmail);
//        return userRepository.save(user);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        messageService.deleteByAuthorId(id);
//        channelService.deleteByAuthorId(id);
//        userRepository.deleteById(id); // userRepository로 통일
//    }
//}