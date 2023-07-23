package com.project.cms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    String role;
    String user_status;

    public Users(String login, String password) {
        this.login = login;
        this.password = password;
        username = "Unnamed";
        description = "No description yet.";
        rewarded = false;
        rewards = 0;
        role = "basic";
        user_status = "normal";
    }

}
