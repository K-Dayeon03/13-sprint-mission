package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

//User, Channel, Message 자동 직렬화 가능
public abstract class Entity implements Serializable {
    //직렬화 및 역직렬화를 수행할 때 이 클래스의 버전을 의미(명시 권장)
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Long createdAt; //객체의 생성 시간
    private Long updatedAt; //객체의 수정 시간
    // 파라미터 없는 생성자 하나만
    public Entity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    protected void makeUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }
}
