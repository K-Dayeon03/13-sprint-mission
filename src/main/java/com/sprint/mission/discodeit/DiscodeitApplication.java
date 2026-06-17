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

	static String display(Object value) {
		return value != null ? value.toString() : "없음";
	}

	static void printUser(UserResponse user) {
		System.out.println("유저 ID     : " + user.id());
		System.out.println("유저 이름   : " + user.username());
		System.out.println("이메일      : " + user.email());
		System.out.println("온라인 여부 : " + user.isOnline());
		System.out.println("생성 시간   : " + user.createdAt());
	}

	static void printChannelForUser(ChannelResponse channel, UserResponse user) {
		List<java.util.UUID> participantIds = channel.participantIds() != null
				? channel.participantIds()
				: List.of(user.id());
		printChannel(channel, participantIds);
	}

	static void printChannel(ChannelResponse channel, List<java.util.UUID> participantIds) {
		System.out.println("채널 ID         : " + channel.id());
		System.out.println("채널 타입       : " + channel.type());
		System.out.println("채널 이름       : " + display(channel.name()));
		System.out.println("채널 설명       : " + display(channel.description()));
		System.out.println("최근 메시지 시간: " + display(channel.lastMessageAt()));
		System.out.println("참여자 목록     : " + participantIds);
	}

	static void printMessage(Message message) {
		System.out.println("메시지 ID  : " + message.getId());
		System.out.println("메시지 내용: " + message.getContent());
		System.out.println("채널 ID    : " + message.getChannelId());
		System.out.println("작성자 ID  : " + message.getAuthorId());
		System.out.println("생성 시간  : " + message.getCreatedAt());
	}

	// ======================== 사용자 시나리오 ========================
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

	// ======================== 채널 시나리오 ========================
	static ChannelResponse channelCreatePublicTest(ChannelService channelService, UserResponse user) {
		printDivider("PUBLIC 채널 생성");
		ChannelResponse channel = channelService.createPublic(
				new CreatePublicChannelRequest("공지", "공지 채널입니다.")
		);
		printChannelForUser(channel, user);
		return channel;
	}

	static ChannelResponse channelCreatePrivateTest(ChannelService channelService, UserResponse user) {
		printDivider("PRIVATE 채널 생성");
		ChannelResponse channel = channelService.createPrivate(
				new CreatePrivateChannelRequest(List.of(user.id()))
		);
		printChannelForUser(channel, user);
		return channel;
	}

	static void channelFindTest(ChannelService channelService, UserResponse user, String title) {
		printDivider(title);
		List<ChannelResponse> channels = channelService.findAllByUserId(user.id());
		channels.forEach(c -> {
			printChannelForUser(c, user);
			System.out.println("---");
		});
	}

	static void channelUpdateTest(ChannelService channelService, ChannelResponse channel, UserResponse user) {
		printDivider("채널 수정");
		ChannelResponse updated = channelService.update(
				channel.id(),
				new UpdateChannelRequest("공지(수정됨)", "수정된 설명입니다.")
		);
		printChannelForUser(updated, user);
	}

	static void channelDetailTest(ChannelService channelService, ChannelResponse channel, UserResponse user, String title) {
		printDivider(title);
		ChannelResponse found = channelService.findById(channel.id());
		printChannelForUser(found, user);
	}

	// ======================== 메시지 시나리오 ========================
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

	static void messageDeleteTest(MessageService messageService, ChannelService channelService,
	                              Message message, UserResponse user) {
		printDivider("메시지 삭제");
		messageService.deleteById(message.getId());
		System.out.println("메시지 삭제 완료: " + message.getId());

		printDivider("메시지 삭제 후 채널 상태");
		ChannelResponse channelAfterDelete = channelService.findById(message.getChannelId());
		printChannelForUser(channelAfterDelete, user);
	}

	// ======================== main ========================
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		printDivider("애플리케이션 시작");
		System.out.println("Spring Context에서 Service Bean 조회 완료");

		// 1. 사용자 접속
		UserResponse user = userCreateTest(userService);
		userFindTest(userService, user);
		userUpdateTest(userService, user);

		// 2. 서버 채널 준비
		ChannelResponse publicChannel = channelCreatePublicTest(channelService, user);
		channelCreatePrivateTest(channelService, user);
		channelUpdateTest(channelService, publicChannel, user);

		// 3. 채널 목록을 보고 PUBLIC 채널 입장
		channelFindTest(channelService, user, "현재 유저가 볼 수 있는 채널 목록");
		channelDetailTest(channelService, publicChannel, user, "PUBLIC 채널 입장");

		// 4. 입장한 채널에서 대화
		Message message = messageCreateTest(messageService, publicChannel, user);
		messageFindTest(messageService, publicChannel);
		channelDetailTest(channelService, publicChannel, user, "메시지 작성 후 채널 상태");

		// 5. 메시지 관리
		messageUpdateTest(messageService, message);
		messageFindTest(messageService, publicChannel);
		messageDeleteTest(messageService, channelService, message, user);

		// 6. 정리
		printDivider("유저 삭제");
		userService.deleteById(user.id());
		System.out.println("유저 삭제 완료: " + user.id());
	}
}
