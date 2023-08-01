package com.project.cms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Users {
    @Id
    String login;
    String password;
    String username;
    String description;
    boolean rewarded;
    int rewards;
    boolean admin;
    boolean banned;

    public Users(String login, String password) {
        this.login = login;
        this.password = password;
        username = "Unnamed";
        description = "No description yet.";
        rewarded = false;
        rewards = 0;
        admin = false;
        banned = false;
    }

}
