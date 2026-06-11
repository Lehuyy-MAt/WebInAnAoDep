package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DesignInteractions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesignInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "DesignId")
    private Design design;

    @Column(name = "ActionType")
    private String actionType;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}