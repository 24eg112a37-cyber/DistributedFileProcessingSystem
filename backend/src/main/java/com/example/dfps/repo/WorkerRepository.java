package com.example.dfps.repo;
import com.example.dfps.model.WorkerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface WorkerRepository extends JpaRepository<WorkerNode,Long> {
 Optional<WorkerNode> findByName(String name);
}
