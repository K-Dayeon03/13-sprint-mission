package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChannelType type;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Transient
    private UUID authorId;

    protected Channel() {
    }

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
    }

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
