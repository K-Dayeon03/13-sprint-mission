package com.sprint.mission.discodeit.entity;
//회원
public class User extends Entity{
        private String username;  // 유저명
        private String password;  // 비밀번호
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

    public void update(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        makeUpdate();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

