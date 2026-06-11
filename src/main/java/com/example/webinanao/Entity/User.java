package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Email", unique = true)
    private String email;

    @Column(name = "PasswordHash")
    private String passwordHash;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Address")
    private String address;

    @Column(name = "AvatarUrl")
    private String avatarUrl;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "RoleId")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Design> designs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<DesignInteraction> interactions = new ArrayList<>();
}