package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;
@Getter
public class Channel extends Entity{
    private ChannelType type;
    private String name;
    private String description;
    private UUID authorId;
    public Channel(ChannelType type, String name, String description, UUID authorId) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
        this.authorId = authorId;
    }
    public void update(ChannelType newType, String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        this.type = newType;
        makeUpdate();
    }

    public UUID getAuthorId() { return authorId;}

    // Channel.java
    @Override
    public String toString() {
        return "채널{" +
                "타입=" + type +
                ", 이름='" + name + '\'' +
                ", 설명='" + description + '\'' +
                ", 작성자ID=" + authorId +
                ", 생성시간=" + getCreatedAt() +
                ", 수정시간=" + getUpdatedAt() +
                '}';
    }

}
