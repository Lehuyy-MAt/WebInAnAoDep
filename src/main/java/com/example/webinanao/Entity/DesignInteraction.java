package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "design_interactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesignInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "design_id")
    private Design design;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}