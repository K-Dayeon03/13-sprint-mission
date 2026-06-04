package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {


	static User setupUser(UserService userService) {
		User user = userService.create("woody", "woody1234", "woody@codeit.com");
		return user;
	}

	static Channel setupChannel(ChannelService channelService, UUID authorId) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.", authorId);
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());
		// 생성된 메시지 내용도 같이 출력해서 확인
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
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService, user.getId());

		// 테스트
		messageCreateTest(messageService, channel, user);
	}



}
