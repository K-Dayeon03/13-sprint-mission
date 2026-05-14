package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends Entity {

    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    public void update(String newUsername, String newPassword, String newEmail) {
        this.username = newUsername;
        this.password = newPassword;
        this.email = newEmail;
        makeUpdate();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username=" + username +
                ", email=" + email +
                ", createdAt=" + getCreatedAt() +
                "}";
    }
}