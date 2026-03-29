package com.tasksystemnidharshana.taskmanager.repository;
import com.tasksystemnidharshana.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//gets user by email and checks if the email exists
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User>findByEmail(String email);
    boolean existsByEmail(String email);
}
