package com.example.dfps.repo;
import com.example.dfps.model.ProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JobRepository extends JpaRepository<ProcessingJob,Long> {
 List<ProcessingJob> findAllByOrderByCreatedAtDesc();
 List<ProcessingJob> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
 List<ProcessingJob> findByStatusOrderByCreatedAtAsc(String status);
}
