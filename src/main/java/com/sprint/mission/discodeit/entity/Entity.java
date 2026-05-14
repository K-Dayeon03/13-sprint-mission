package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Entity {
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
