package com.example.dfps.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="processing_jobs")
public class ProcessingJob {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="owner_id") public AppUser owner;
 @Column(nullable=false) public String filename;
 @Column(name="stored_path",nullable=false,columnDefinition="text") public String storedPath;
 @Column(nullable=false) public String status="QUEUED";
 @Column(name="result_text",columnDefinition="text") public String resultText;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="worker_id") public WorkerNode worker;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 public ProcessingJob() {}
}
