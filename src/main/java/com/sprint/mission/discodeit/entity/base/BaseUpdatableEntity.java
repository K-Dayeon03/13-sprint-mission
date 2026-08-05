package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
//BaseEntity가 가지고 있는 속성을 가지고 올수있음
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity{

    //엔티티 최종 수정 날짜를 자동으로 데이터베이스에 매핑해주기 위해 사용
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
