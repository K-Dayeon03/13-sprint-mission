package com.sprint.mission.discodeit;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;


public class JavaApplication {
    public static void main(String[] args) {
        // 의존성을 위해 서비스를 초기화
//        MessageService messageService = new JCFMessageService();
//        ChannelService channelService = new JCFChannelService(messageService);
//        UserService userService = new JCFUserService(channelService, messageService);
        JCFMessageService messageService = new JCFMessageService();
        JCFChannelService channelService = new JCFChannelService(messageService);
        JCFUserService userService = new JCFUserService(channelService, messageService);
        messageService.init(userService, channelService);

        System.out.println("====== 유저 등록  ======");
        User user1 = userService.create("이경민", "abc1234","naver.com");
        User user2 = userService.create("강다연", "qwer1234","naver.com");
        User user3 = userService.create("장준서", "aaa1234","naver.com");
        User user4 = userService.create("이예은", "bbb1234","naver.com");
        User user5 = userService.create("함지원", "bbb1234","naver.com");

        userService.findAll().forEach(u -> System.out.println(u.toString()));


        //  조회(단건, 다건)
        System.out.println("====== 전체 인원 수 조회 ======");
        List<User> all = userService.findAll();
        System.out.println("전체 조회: " + all.size() + "명");
        all.forEach(u -> System.out.println(" - " + u.getUsername()));

        System.out.println("====== 원하는 인원 조회 ======");
        User found1 = userService.findById(user1.getId());
        System.out.println(" 조회: " + found1.getUsername());


        //수정
        System.out.println("====== 유저 수정 전 조회 ======");
        userService.findAll().forEach(u -> System.out.println(u.getUsername()+" - "+u.getEmail()));
        boolean updated = userService.update(
                user2.getId(),
                "qwer1234",
                "클래스매니저",
                "newpass123",
                "daum@gmil.com"
        );
        System.out.println(updated ? "업데이트 성공: " + userService.findById(user2.getId()).getUsername() : "업데이트 실패");
        System.out.println("====== 유저 수정 후 조회 ======");
        userService.findAll().forEach(u -> System.out.println(u.getUsername()+" - "+u.getEmail()));
        //삭제 + 삭제 확인
        //삭제 전 인원 확인 및 이름
        System.out.println("====== 삭제 전 인원 및 이름 조회 ======");
        List<User> delAfter = userService.findAll();
        System.out.println("삭제 전 조회: " + delAfter.size() + "명");
        all.forEach(u -> System.out.println(" - " + u.getUsername()));


        System.out.println("====== 삭제 후 인원 및 이름 조회 ======");
        userService.delete(user3.getId()); //장준서
        System.out.println("남은 유저: "+" "+userService.findAll().size() + "명");
        userService.findAll().forEach(u -> System.out.println(u.getUsername()+" - "+u.getEmail()));

        System.out.println("====== 채널 등록 ======");
        Channel channel1 = channelService.create(ChannelType.PUBLIC, "행정-공지","코드잇 스프린트 행정 관련 공지", user1.getId() );
        Channel channel2 = channelService.create(ChannelType.PUBLIC, "학습-공지","스프린터를 위한 학습 공지 사항", user2.getId());
        Channel channel3 = channelService.create(ChannelType.PUBLIC, "일반-공지","팀원과 멘토 및 수업 커리큘럼 안내", user3.getId() );
        Channel channel4 = channelService.create(ChannelType.PRIVATE, "위워크-안내","위워크 장소 및 이용 안내", user4.getId());
        Channel channel5 = channelService.create(ChannelType.PRIVATE, "커리어-지원-안내","취업을 위한 커리어 공지", user5.getId() );

        List<Channel> channels1 = channelService.findAll();
        channels1.forEach(c -> System.out.println(c.getName()));

        //  조회(단건, 다건)
        System.out.println("====== 전체 채널 수 조회 ======");
        List<Channel> channelAll = channelService.findAll();
        System.out.println("전체 조회: " + channelAll.size() + "개");
        channelAll.forEach(c -> System.out.println(" - " + c.getName()+" :" + c.getDescription()));


        //원하는 채널 조회하된, 채널 방장을 조회하고 싶었다.
        System.out.println("====== 원하는 채널 조회 ======");
        Channel found2 = channelService.findById(channel5.getId());
        User channelOwner = userService.findById((UUID) found2.getAuthorId());

        System.out.println("채널명: " + found2.getName() + " / " + found2.getDescription());
        System.out.println("채널 방장: " + channelOwner.getUsername());

        // 공개 채널인지 분기 처리
        // 일일히 다 지정이 아니라, 순회하면서 분기처리하고 싶어서 forEach문 작성
        System.out.println("====== 채널 공개 여부 조회 ======");
        List<Channel> channels2 = channelService.findAll();
        channels2.forEach(c -> {
            if (c.getType() == ChannelType.PUBLIC) {
                System.out.println("["+c.getName() +"]" + "\n" + "누구나 입장 가능한 채널입니다.");
            } else {
                System.out.println("["+c.getName() +"]" + "\n" + "초대된 사람만 입장 가능한 채널입니다.");
            }

        });

        System.out.println("====== 채널 수정 전 조회 ======");
        List<Channel> channelModifyBefore = channelService.findAll();
        channelModifyBefore.forEach(c -> System.out.println(c.getName()));
        System.out.println("====== 채널 수정 후 조회 ======"); //"학습-공지","스프린터를 위한 학습 공지 사항"
        boolean channelModify = channelService.update(channel2.getId(), ChannelType.PUBLIC, "새로운-채널명-변경", "채널명 변경테스트입니다." );
        if(channelModify){
            System.out.println("[채널 수정 성공했습니다!]");
            List<Channel> channelModifyAfter = channelService.findAll();
            channelModifyAfter.forEach(c -> System.out.println(c.getName()));
        }else{
            System.out.println("[존재하지 않는 채널입니다.]");
        }

        System.out.println("====== 채널 삭제 전 조회 ======");
        List<Channel> channelDelBefore = channelService.findAll();
        System.out.println("개수 조회: " + channelDelBefore.size() + "개");
        channelDelBefore.forEach(c -> System.out.println(c.getName()));

        System.out.println("====== 채널 삭제 후 조회 ======");
        channelService.delete(channel1.getId());//행정-공지
        List<Channel> channelDelAfter = channelService.findAll();
        System.out.println("개수 조회: " + channelDelAfter.size() + "개");
        channelDelAfter.forEach(c -> System.out.println(c.getName()));

        System.out.println("====== 메세지 등록 ======");

        System.out.println("====== 메세지 등록 ======");

        // channel1 삭제됐다→ 검증 확인
        Message message1 = messageService.create("함지원 스프린트 팀원 및 스터디 팀장", channel1.getId(), user1.getId());
        if (message1 == null) {
            System.out.println("존재하지 않는 채널입니다.");
        }

        // user3 삭제됐다 → 검증 확인
        Message message3 = messageService.create("장준서 스프린트 팀원", channel3.getId(), user3.getId());
        if (message3 == null) {
            System.out.println("존재하지 않는 유저입니다.");
        }

        // 정상 등록
        Message message2 = messageService.create("이예은 스프린트 팀원", channel2.getId(), user2.getId());
        Message message4 = messageService.create("강다연 스프린트 팀장", channel4.getId(), user4.getId());
        Message message5 = messageService.create("이경민 주강사 수업 안내", channel5.getId(), user5.getId());
        Message message6 = messageService.create("클래스_매니저 공지", channel5.getId(), user5.getId());

        // 수정 시 null 체크
        System.out.println("====== 메시지 수정 ======");
        if (message1 != null) {
            messageService.update(message1.getId(), "[수정된 메세지]");
        } else {
            System.out.println("메시지가 없어 수정할 수 없습니다.");
        }
        //등록 조회
        messageService.findByAll().forEach(m -> System.out.println(m.toString()));

        //단건/다건 조회
        System.out.println("====== 전체 메세지 수 조회 ======");
        List<Message> messages = messageService.findByAll();
        System.out.println("전체 조회: " + messages.size() + "개");
        messages.forEach(m -> System.out.println(m.getContent()));

        System.out.println("====== 원하는 메세지 조회 ======");
        //메세지 조회
        Message found3 = messageService.findById(message5.getId());//"이경민 주강사 수업 안내"
        System.out.println("메세지 내용: "+found3.getContent());

        System.out.println("====== 원하는 채널명&메세지 연관 ======");
        //해당 채널의 이름과 개수, 메세지를 조회
        Channel channel = channelService.findById(channel5.getId());
        List<Message> channelMessages = messageService.findByChannelId(channel5.getId()); //메세지가 2개 떠야한다.

        System.out.println("채널명: " + channel.getName());
        System.out.println("메시지 수: " + channelMessages.size() + "개");
        channelMessages.forEach(m -> System.out.println(" - " + m.getContent()));


        System.out.println("====== 메시지 수정 전======");
        channelMessages.forEach(m -> System.out.println(m.getContent()));


        System.out.println("====== 메시지 수정 후======");

        if (message1 != null) {
            System.out.println("메세지 수정 성공했습니다!");
            messageService.update(message1.getId(), "[수정된 메세지]");
            messages.forEach(m -> System.out.println(m.getContent()));

        } else {
            System.out.println("메시지가 없어 수정할 수 없습니다.");
        }


        System.out.println("====== 메시지 삭제 전 조회 ======");

        List<Message> messagesDelBefore  = messageService.findByAll();
        System.out.println("개수 조회: " + messagesDelBefore.size() + "개");
        messagesDelBefore.forEach(m -> System.out.println(m.getContent()));

        System.out.println("====== 메세지 삭제 후 조회 ======");
        if (message3 != null) {
            messageService.delete(message3.getId());
        } else {
            System.out.println("삭제할 메시지가 없습니다.");
            messageService.delete(message4.getId());
        }
        System.out.println("====== 메세지 삭제 후 조회 ======");
        List<Message> messagesDelAfter = messageService.findByAll();
        System.out.println("개수 조회: " + messagesDelAfter.size() + "개");
        messagesDelAfter.forEach(m -> System.out.println(m.getContent()));





    }
}