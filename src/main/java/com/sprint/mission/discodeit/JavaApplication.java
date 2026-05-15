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


public class JavaApplication {
    public static void main(String[] args) {
        // 의존성을 위해 서비스를 초기화
        MessageService messageService = new JCFMessageService();
        ChannelService channelService = new JCFChannelService(messageService);
        UserService userService = new JCFUserService(channelService, messageService);

        System.out.println("====== 유저 등록  ======");
        User user1 = userService.create("이경민", "abc1234","naver.com");
        User user2 = userService.create("강다연", "qwer1234","naver.com");
        User user3 = userService.create("장준서", "aaa1234","naver.com");
        User user4 = userService.create("이예은", "bbb1234","naver.com");
        User user5 = userService.create("함지원", "bbb1234","naver.com");

        userService.findAll().forEach(u -> System.out.println(u.toString()));


        // 다건 인원 수 조회
        System.out.println("====== 전체 인원 수 조회 ======");
        List<User> all = userService.findAll();
        System.out.println("전체 조회: " + all.size() + "명");

        //수정
        System.out.println("====== 유저 수정 ======");

        //user가 있는지 없는지 먼저 확인하기
        boolean updated = userService.update(
                user2.getId(),
                "qwer1234",
                "클래스매니저",
                "newpass123",
                "daum@gmil.com"
        );
        System.out.println(updated ? "업데이트 성공: " + userService.findById(user2.getId()).getUsername() : "업데이트 실패");
        userService.findAll().forEach(u -> System.out.println(u.toString()));

        System.out.println("====== 삭제 후 연관 데이터 확인 ======");
        userService.delete(user3.getId());
        System.out.println("남은 유저: "+" "+userService.findAll().size() + "명");
        userService.findAll().forEach(u -> System.out.println(u.toString()));

        System.out.println("====== 채널 생성 ======");

        Channel channel1 = channelService.create(ChannelType.PUBLIC, "행정-공지","코드잇 스프린트 행정 관련 공지", user1.getId() );
        Channel channel2 = channelService.create(ChannelType.PUBLIC, "학습-공지","스프린터를 위한 학습 공지 사항", user2.getId());
        Channel channel3 = channelService.create(ChannelType.PUBLIC, "일반-공지","팀원과 멘토 및 수업 커리큘럼 안내", user3.getId() );
        Channel channel4 = channelService.create(ChannelType.PRIVATE, "위워크-안내","위워크 장소 및 이용 안내", user4.getId());
        Channel channel5 = channelService.create(ChannelType.PRIVATE, "커리어-지원-안내","취업을 위한 커리어 공지", user5.getId() );
        // 공개 채널인지 분기 처리
        // 일일히 다 지정이 아니라, 순회하면서 분기처리하고 싶어서 forEach문 작성
        List<Channel> channels = channelService.findAll();
        channels.forEach(c -> {
            if (c.getType() == ChannelType.PUBLIC) {
                System.out.println(c.getName() + "\n" + "누구나 입장 가능한 채널입니다.");
            } else {
                System.out.println(c.getName() + "\n" + "초대된 사람만 입장 가능한 채널입니다.");
            }

        });

        //채널 단건 조회
        Channel found1 = channelService.findById(channel5.getId());
        System.out.println("채널 한 건 생성 조회: "+found1.getName());

        //전체 조회
        List<Channel> channelAll = channelService.findAll();
        System.out.println(" 전체 조회: " + channelAll.size() + "개");
        channelAll.forEach(c -> System.out.println(" - " + c.getName()+" :" + c.getDescription()));



        System.out.println("====== 메세지 등록 ======");
        Message message1 = messageService.create("함지원 스프린트 팀원 및 스터디 팀장", channel5.getId(), user2.getId());
        Message message2 = messageService.create("이예은 스프린트 팀원", channel2.getId(), user2.getId());
        Message message3 = messageService.create("장준서 스프린트 팀원", channel5.getId(), user2.getId());
        Message message4 = messageService.create("강다연 스프린트 팀장", channel2.getId(), user2.getId());
        Message message5 = messageService.create("이경민 주강사 수업 안내", channel2.getId(), user2.getId());
        Message message6 = messageService.create("클래스_매니저 공지", channel2.getId(), user2.getId());

        List<Message> messages = messageService.findByAll();
        messages.forEach(m -> System.out.println(m.getContent()));

        System.out.println("====== 채널 검색 후 메세지 연관성 확인 ======");
        //채널 조회를 먼저해야한다.
        Channel found2 = channelService.findById(channel5.getId());
        System.out.println("채널명: "+found2.getName());

        User channelOwner = userService.findById(user1.getId());
        System.out.println("채널 방장: "+ channelOwner.getUsername());

        //해당 채널의 메세지를 조회
        List<Message> channelMessages = messageService.findByChannelId(channel5.getId());
        System.out.println("메세지 수: "+ channelMessages.size()+"개");


        System.out.println("====== 메시지 수정 전======");
        channelMessages.forEach(m -> System.out.println(m.getContent()));


        System.out.println("====== 메시지 수정 후======");
        boolean messageModify = messageService.update(message1.getId(),"수정된 메세지 테스트입니다.");
        if(messageModify){
            System.out.println("메세지 수정 성공했습니다!");
        }else{
            System.out.println("존재하지 않는 메세지입니다.");
        }
        System.out.println(messageService.findById(message1.getId()).getContent());

        System.out.println("====== 채널 수정 ======");

        boolean channelModify = channelService.update(channel2.getId(), ChannelType.PUBLIC, "새로운 채널명 변경", "채널명 변경테스트입니다." );
        if(channelModify){
            System.out.println("채널 수정 성공했습니다!");
        }else{
            System.out.println("존재하지 않는 채널입니다.");
        }

        System.out.println(channelService.findById(channel2.getId()).getName());


        System.out.println(messageService.findById(message1.getId()).getContent());
        channelService.delete(channel5.getId());
        System.out.println("채널 삭제 후 수: "+ channelService.findAll().size());

        messageService.delete(message2.getId());
        List<Message> afterDeleteMessages = messageService.findByChannelId(channel5.getId());
        System.out.println("메세지 수: " + afterDeleteMessages.size() + "개");
        afterDeleteMessages.forEach(m -> System.out.println(m.toString()));



    }
}