package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//message
@Getter
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {
    @Column(name = "content", columnDefinition = "text")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    @Transient
    private UUID channelId;

    @Transient
    private UUID authorId;

    @Transient
    private List<UUID> attachmentIds = new ArrayList<>();

    protected Message() {
    }

    public Message(String content, UUID channelId, UUID authorId) {
        super();
        //검증
        if(content == null || content.isBlank()){
            throw new InvalidRequestException("메세지 내용은 필수입니다.");
        }
        if(channelId == null){
            throw new InvalidRequestException("채널 ID는 필수입니다.");
        }
        if(authorId == null){
            throw new InvalidRequestException("작성자 ID는 필수입니다.");
        }
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public Message(String content, Channel channel, User author) {
        super();
        if (content == null || content.isBlank()) {
            throw new InvalidRequestException("메세지 내용은 필수입니다.");
        }
        if (channel == null) {
            throw new InvalidRequestException("채널은 필수입니다.");
        }
        if (author == null) {
            throw new InvalidRequestException("작성자는 필수입니다.");
        }
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.channelId = channel.getId();
        this.authorId = author.getId();
    }

    public UUID getChannelId() {
        if (channel != null) {
            return channel.getId();
        }
        return channelId;
    }

    public UUID getAuthorId() {
        if (author != null) {
            return author.getId();
        }
        return authorId;
    }

    public List<UUID> getAttachmentIds() {
        if (attachments != null && !attachments.isEmpty()) {
            return attachments.stream()
                    .map(BinaryContent::getId)
                    .toList();
        }
        return attachmentIds;
    }

    public void addAttachmentId(UUID attachmentId) {
        if (attachmentId == null) {
            throw new InvalidRequestException("첨부파일 ID는 필수입니다.");
        }
        this.attachmentIds.add(attachmentId);
    }

    // content 수정 메서드 추가
    public void update(String newContent) {
        if (newContent == null) {
            throw new InvalidRequestException("수정할 내용이 없습니다.");
        }
        if (newContent.isBlank()) {
            throw new InvalidRequestException("메시지 내용은 빈 문자열일 수 없습니다.");
        }
        this.content = newContent;
    }

    // Message.java
    @Override
    public String toString() {
        return "메시지{" +
                "내용='" + content + '\'' +
                ", 채널ID=" + getChannelId() +
                ", 작성자ID=" + getAuthorId() +
                ", 생성시간=" + getCreatedAt() +
                ", 수정시간=" + getUpdatedAt() +
                '}';
    }
}
