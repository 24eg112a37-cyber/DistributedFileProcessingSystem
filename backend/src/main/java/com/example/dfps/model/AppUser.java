package com.example.dfps.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="app_users")
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false, unique=true) public String email;
 @Column(name="password_hash",nullable=false) public String passwordHash;
 @Column(nullable=false) public String role="USER";
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 public AppUser() {}
}
