package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "OrderHistories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "OrderId", nullable = false)
    private Order order;

    @Column(name = "Status")
    private String status;

    @Column(name = "Note")
    private String note;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}