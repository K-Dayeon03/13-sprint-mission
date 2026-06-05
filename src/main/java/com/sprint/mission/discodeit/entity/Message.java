package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.List;
import java.util.UUID;
//message
@Getter
public class Message extends Entity{
    private String content;
    private UUID channelId;
    private UUID authorId;
    List<UUID> attachmentIds;
    public Message(String content, UUID channelId, UUID authorId) {
        super();
        //검증
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("메세지 내용은 필수입니다.");
        }
        if(channelId == null){
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }
        if(authorId == null){
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }
//    public String getContent() {
//        return content;
//    }
//
//
//    public UUID getChannelId() {
//        return channelId;
//    }
//
//    public UUID getAuthorId() {
//        return authorId;
//    }
    // content 수정 메서드 추가
    public void update(String newContent) {
        if (newContent == null) {
            throw new IllegalArgumentException("수정할 내용이 없습니다.");
        }
        if (newContent.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 빈 문자열일 수 없습니다.");
        }
        this.content = newContent;
        makeUpdate();
    }


    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                '}';
    }
}
