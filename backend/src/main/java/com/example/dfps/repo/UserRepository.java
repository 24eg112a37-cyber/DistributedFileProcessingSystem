package com.example.dfps.repo;
import com.example.dfps.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<AppUser,Long> {
 Optional<AppUser> findByEmailIgnoreCase(String email);
}
