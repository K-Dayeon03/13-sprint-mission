package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	static UserResponse setupUser(UserService userService) {
		return userService.create(
				new CreateUserRequest("woody", "woody@codeit.com", "woody1234"),
				null // 프로필 이미지 없음
		);
	}

	static ChannelResponse setupChannel(ChannelService channelService) {
		return channelService.createPublic(
				new CreatePublicChannelRequest("공지", "공지 채널입니다.")
		);
	}

	static void messageCreateTest(MessageService messageService,
	                              ChannelResponse channel, UserResponse author) {
		Message message = messageService.create(
				new CreateMessageRequest("안녕하세요.", channel.id(), author.id(), null)
		);
		System.out.println("메시지 생성: " + message.getId());
		System.out.println("메시지 내용: " + message.getContent());
		System.out.println("채널 ID    : " + message.getChannelId());
		System.out.println("작성자 ID  : " + message.getAuthorId());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// context에서 Bean 조회
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 셋업
		UserResponse user = setupUser(userService);
		ChannelResponse channel = setupChannel(channelService);

		// 테스트
		messageCreateTest(messageService, channel, user);
	}
}