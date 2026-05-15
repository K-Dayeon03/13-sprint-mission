package com.sprint.mission.discodeit.entity;

import java.util.UUID;
//message
public class Message extends Entity{
    private String content;
    private UUID channelId;
    private UUID authorId;
    public Message(String content, UUID channelId, UUID authorId) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }
    public String getContent() {
        return content;
    }


    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }
    // content 수정 메서드 추가
    public void update(String newContent) {
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
