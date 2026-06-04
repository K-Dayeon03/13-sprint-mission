package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends Entity {

    private String username;
    private String password; //민감한 정보이므로, 직렬화에서 제외시키기
    private String email;

    public User(String username, String password, String email) {
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
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    public void update(String newUsername, String newPassword, String newEmail) {
        if (newUsername == null && newPassword == null && newEmail == null) {
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