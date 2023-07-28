package com.project.cms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Entity
@Getter
@Setter
public class Posts implements Comparable<Posts> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int post_id;
    String text;
    byte[] image;
    int comments_amount;
    String tags;
    String author_login;
    String author_username;
    String date;
    public Posts(String text, byte[] image, String tags, String author_login, String author_username) {
        this.text = text;
        this.image = image;
        this.tags = tags;
        this.author_login = author_login;
        this.author_username = author_username;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }

    public Posts() {
    }

    public String convertToBase64() {
        return Base64.getEncoder().encodeToString(this.image);
    }
    public boolean hasImage() {
        return this.image.length != 0;
    }

    @Override
    public int compareTo(Posts post) {
        return post.getPost_id() - this.getPost_id();
    }
}
