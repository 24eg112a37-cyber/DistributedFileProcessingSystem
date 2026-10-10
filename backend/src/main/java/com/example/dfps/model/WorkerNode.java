package com.example.dfps.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="worker_nodes")
public class WorkerNode {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String name;
 @Column(nullable=false) public String status="OFFLINE";
 @Column(name="active_jobs",nullable=false) public int activeJobs=0;
 @Column(name="last_heartbeat") public Instant lastHeartbeat;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 public WorkerNode() {}
}
