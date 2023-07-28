package com.project.cms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comments implements Comparable<Comments> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int comment_id;
    int post_id;
    String writer_login;
    String writer_username;
    String text;
    String date;

    public Comments(int post_id, String writer_login, String writer_username, String text) {
        this.post_id = post_id;
        this.writer_login = writer_login;
        this.writer_username = writer_username;
        this.text = text;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }

    @Override
    public int compareTo(Comments o) {
        return o.comment_id - this.comment_id;
    }

}
