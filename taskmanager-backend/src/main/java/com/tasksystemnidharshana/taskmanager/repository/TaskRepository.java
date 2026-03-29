package com.tasksystemnidharshana.taskmanager.repository;

import com.tasksystemnidharshana.taskmanager.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Admin — see all tasks, with optional filters + search
    @Query("SELECT t FROM Task t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:assignedToId IS NULL OR t.assignedTo.id = :assignedToId) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "  OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findByFilters(
            @Param("status") String status,
            @Param("assignedToId") Long assignedToId,
            @Param("priority") String priority,
            @Param("search") String search,
            Pageable pageable
    );

    // Regular user — only see tasks they created or are assigned to
    @Query("SELECT t FROM Task t WHERE " +
            "(t.createdBy.id = :userId OR t.assignedTo.id = :userId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "  OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findMyTasks(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("search") String search,
            Pageable pageable
    );
}