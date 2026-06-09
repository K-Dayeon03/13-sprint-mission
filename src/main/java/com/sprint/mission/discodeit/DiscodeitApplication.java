package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	// ======================== 출력 유틸 ========================
	static void printDivider(String title) {
		System.out.println("\n========== " + title + " ==========");
	}

	static void printUser(UserResponse user) {
		System.out.println("유저 ID     : " + user.id());
		System.out.println("유저 이름   : " + user.username());
		System.out.println("이메일      : " + user.email());
		System.out.println("온라인 여부 : " + user.isOnline());
		System.out.println("생성 시간   : " + user.createdAt());
	}

	static void printChannel(ChannelResponse channel) {
		System.out.println("채널 ID         : " + channel.id());
		System.out.println("채널 타입       : " + channel.type());
		System.out.println("채널 이름       : " + channel.name());
		System.out.println("채널 설명       : " + channel.description());
		System.out.println("최근 메시지 시간: " + channel.lastMessageAt());
		System.out.println("참여자 목록     : " + channel.participantIds());
	}

	static void printMessage(Message message) {
		System.out.println("메시지 ID  : " + message.getId());
		System.out.println("메시지 내용: " + message.getContent());
		System.out.println("채널 ID    : " + message.getChannelId());
		System.out.println("작성자 ID  : " + message.getAuthorId());
		System.out.println("생성 시간  : " + message.getCreatedAt());
	}

	// ======================== 유저 테스트 ========================
	static UserResponse userCreateTest(UserService userService) {
		printDivider("유저 생성");
		UserResponse user = userService.create(
				new CreateUserRequest("woody", "woody@codeit.com", "woody1234"),
				null
		);
		printUser(user);
		return user;
	}

	static void userFindTest(UserService userService, UserResponse user) {
		printDivider("유저 단건 조회");
		UserResponse found = userService.findById(user.id());
		printUser(found);

		printDivider("유저 전체 조회");
		List<UserResponse> users = userService.findByAll();
		users.forEach(u -> {
			printUser(u);
			System.out.println("---");
		});
	}

	static void userUpdateTest(UserService userService, UserResponse user) {
		printDivider("유저 수정");
		UserResponse updated = userService.update(
				user.id(),
				new UpdateUserRequest("woodyUpdated", null, null),
				null
		);
		printUser(updated);
	}

	// ======================== 채널 테스트 ========================
	static ChannelResponse channelCreatePublicTest(ChannelService channelService) {
		printDivider("PUBLIC 채널 생성");
		ChannelResponse channel = channelService.createPublic(
				new CreatePublicChannelRequest("공지", "공지 채널입니다.")
		);
		printChannel(channel);
		return channel;
	}

	static ChannelResponse channelCreatePrivateTest(ChannelService channelService, UserResponse user) {
		printDivider("PRIVATE 채널 생성");
		ChannelResponse channel = channelService.createPrivate(
				new CreatePrivateChannelRequest(List.of(user.id()))
		);
		printChannel(channel);
		return channel;
	}

	static void channelFindTest(ChannelService channelService, UserResponse user) {
		printDivider("유저가 볼 수 있는 채널 목록 조회");
		List<ChannelResponse> channels = channelService.findAllByUserId(user.id());
		channels.forEach(c -> {
			printChannel(c);
			System.out.println("---");
		});
	}

	static void channelUpdateTest(ChannelService channelService, ChannelResponse channel) {
		printDivider("채널 수정");
		ChannelResponse updated = channelService.update(
				channel.id(),
				new UpdateChannelRequest("공지(수정됨)", "수정된 설명입니다.")
		);
		printChannel(updated);
	}

	// ======================== 메시지 테스트 ========================
	static Message messageCreateTest(MessageService messageService,
	                                 ChannelResponse channel, UserResponse author) {
		printDivider("메시지 생성");
		Message message = messageService.create(
				new CreateMessageRequest("안녕하세요.", channel.id(), author.id(), null)
		);
		printMessage(message);
		return message;
	}

	static void messageFindTest(MessageService messageService, ChannelResponse channel) {
		printDivider("채널 메시지 목록 조회");
		List<Message> messages = messageService.findAllByChannelId(channel.id());
		messages.forEach(m -> {
			printMessage(m);
			System.out.println("---");
		});
	}

	static void messageUpdateTest(MessageService messageService, Message message) {
		printDivider("메시지 수정");
		Message updated = messageService.update(
				message.getId(),
				new UpdateMessageRequest("수정된 메시지입니다.")
		);
		printMessage(updated);
	}

	static void messageDeleteTest(MessageService messageService, Message message) {
		printDivider("메시지 삭제");
		messageService.deleteById(message.getId());
		System.out.println("메시지 삭제 완료: " + message.getId());
	}

	// ======================== main ========================
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 유저 테스트
		UserResponse user = userCreateTest(userService);
		userFindTest(userService, user);
		userUpdateTest(userService, user);

		// 채널 테스트
		ChannelResponse publicChannel = channelCreatePublicTest(channelService);
		ChannelResponse privateChannel = channelCreatePrivateTest(channelService, user);
		channelFindTest(channelService, user);
		channelUpdateTest(channelService, publicChannel);

		// 메시지 테스트
		Message message = messageCreateTest(messageService, publicChannel, user);
		messageFindTest(messageService, publicChannel);
		messageUpdateTest(messageService, message);
		messageDeleteTest(messageService, message);

		// 유저 삭제 (연관 데이터 같이 삭제 확인)
		printDivider("유저 삭제");
		userService.deleteById(user.id());
		System.out.println("유저 삭제 완료: " + user.id());
	}
}