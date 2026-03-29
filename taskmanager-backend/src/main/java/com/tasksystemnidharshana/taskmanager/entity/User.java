package com.tasksystemnidharshana.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name="created_at")
    private LocalDateTime createdAt;
    //saves the time of the user creation
    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }

}
