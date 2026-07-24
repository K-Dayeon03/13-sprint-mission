package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.UUID;
//이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용
//수정 불가능 updatedAt 불필요
@Getter
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {
    @Transient
    private UUID userId;

    @Transient
    private UUID messageId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    protected BinaryContent() {
    }

    public BinaryContent(UUID userId, UUID messageId,
                         String fileName, String contentType, Long size) {

        if (userId != null && messageId != null) {
            throw new IllegalArgumentException("유저 아이디와 메세지 아이디는 동시에 설정될 수 없습니다.");
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일명은 필수입니다.");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("콘텐츠 타입은 필수입니다.");
        }
        if (size == null || size <= 0) {
            throw new IllegalArgumentException("파일 크기는 필수입니다.");
        }

        this.userId = userId;
        this.messageId = messageId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }
}
