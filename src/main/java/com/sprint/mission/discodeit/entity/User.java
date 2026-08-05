package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.InvalidRequestException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "password", nullable = false, length = 60)
    private String password;
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    protected User() {

    }

    //orphanRemoval는 부모와의 관계가 끊긴 자식 엔티티를 자동 삭제 옵션
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus userStatus;

    @Transient
    private UUID profileImageId;

    public User(String username, String password, String email, UUID profileImageId) {
        super();
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("사용자 이름은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidRequestException("비밀번호는 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("이메일은 필수입니다.");
        }
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImageId = profileImageId;
    }

    public void update(String newUsername, String newPassword, String newEmail, BinaryContent newProfile) {
        // profileImageId도 수정 조건에 포함
        if (newUsername == null && newPassword == null && newEmail == null && newProfile == null) {
            throw new InvalidRequestException("수정할 내용이 없습니다.");
        }
        if (newUsername != null) {
            if (newUsername.isBlank()) {
                throw new InvalidRequestException("사용자 이름은 빈 문자열일 수 없습니다.");
            }
            this.username = newUsername;
        }
        if (newPassword != null) {
            if (newPassword.isBlank()) {
                throw new InvalidRequestException("비밀번호는 빈 문자열일 수 없습니다.");
            }
            this.password = newPassword;
        }
        if (newEmail != null) {
            if (newEmail.isBlank()) {
                throw new InvalidRequestException("이메일은 빈 문자열일 수 없습니다.");
            }
            this.email = newEmail;
        }
        if (newProfile != null) {
            this.profile = newProfile;
            this.profileImageId = newProfile.getId();
        }

    }

    public void update(String newUsername, String newPassword, String newEmail, UUID newProfileImageId) {
        if (newUsername == null && newPassword == null && newEmail == null && newProfileImageId == null) {
            throw new InvalidRequestException("수정할 내용이 없습니다.");
        }
        update(newUsername, newPassword, newEmail, (BinaryContent) null);
        if (newProfileImageId != null) {
            this.profileImageId = newProfileImageId;
            this.profile = null;
        }
    }

    public UUID getProfileImageId() {
        if (profile != null) {
            return profile.getId();
        }
        return profileImageId;
    }

    // User.java
    @Override
    public String toString() {
        return "유저{" +
                "ID=" + getId() +
                ", 이름='" + username + '\'' +
                ", 이메일='" + email + '\'' +
                ", 프로필이미지ID=" + getProfileImageId() +
                ", 생성시간=" + getCreatedAt() +
                ", 수정시간=" + getUpdatedAt() +
                '}';
    }
}
