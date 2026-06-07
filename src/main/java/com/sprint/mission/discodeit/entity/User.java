package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
public class User extends Entity {

    private String username;
    private transient String password; //직렬화 시 제외 테스트해보기
    private String email;
    private UUID profileImageId;

    public User(String username, String password, String email, UUID profileImageId) {
        super();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImageId = profileImageId;
    }

    public void update(String newUsername, String newPassword, String newEmail, UUID newProfileImageId) {
        // profileImageId도 수정 조건에 포함
        if (newUsername == null && newPassword == null && newEmail == null && newProfileImageId == null) {
            throw new IllegalArgumentException("수정할 내용이 없습니다.");
        }
        if (newUsername != null) {
            if (newUsername.isBlank()) {
                throw new IllegalArgumentException("사용자 이름은 빈 문자열일 수 없습니다.");
            }
            this.username = newUsername;
        }
        if (newPassword != null) {
            if (newPassword.isBlank()) {
                throw new IllegalArgumentException("비밀번호는 빈 문자열일 수 없습니다.");
            }
            this.password = newPassword;
        }
        if (newEmail != null) {
            if (newEmail.isBlank()) {
                throw new IllegalArgumentException("이메일은 빈 문자열일 수 없습니다.");
            }
            this.email = newEmail;
        }
        // null이면 기존값 유지, 값이 있으면 교체
        if (newProfileImageId != null) {
            this.profileImageId = newProfileImageId;
        }
        makeUpdate();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username=" + username +
                ", email=" + email +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                "}";
    }
}