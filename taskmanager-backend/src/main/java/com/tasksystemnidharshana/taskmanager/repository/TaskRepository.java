package com.tasksystemnidharshana.taskmanager.repository;

import com.tasksystemnidharshana.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // admin - all tasks with optional filters and ordered by priority
    @Query("SELECT t FROM Task t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:assignedToId IS NULL OR t.assignedTo.id = :assignedToId) AND " +
            "(:priority IS NULL OR t.priority = :priority) " +
            "ORDER BY CASE t.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END")
    List<Task> findByFilters(
            @Param("status") String status,
            @Param("assignedToId") Long assignedToId,
            @Param("priority") String priority
    );

    // user sees tasks created by them or assigned to them
    @Query("SELECT t FROM Task t WHERE " +
            "(t.createdBy.id = :userId OR t.assignedTo.id = :userId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) " +
            "ORDER BY CASE t.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END")
    List<Task> findMyTasks(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("priority") String priority
    );
}