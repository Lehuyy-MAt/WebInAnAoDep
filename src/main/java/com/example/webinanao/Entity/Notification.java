package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @Column(name = "Title")
    private String title;

    @Column(name = "Message")
    private String message;

    @Column(name = "Type")
    private String type;

    @Column(name = "Link")
    private String link;

    @Column(name = "IsRead")
    private Boolean isRead;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}