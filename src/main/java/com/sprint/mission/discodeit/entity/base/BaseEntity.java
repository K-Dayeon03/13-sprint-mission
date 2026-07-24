package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
//엔티티를 DB에 적용하기 이전에 콜백을 요청할 수 있는 어노테이션
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    //엔터티 생성 시 특정 필드를 자동으로 데이터베이스에 매핑해주기 위해 사용
    @CreatedDate
    @Column(name = "created_at", nullable = false,updatable = false)
    private Instant createdAt;
}
