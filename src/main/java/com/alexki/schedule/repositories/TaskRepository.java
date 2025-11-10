package com.alexki.schedule.repositories;

import com.alexki.schedule.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByScheduleId(UUID scheduleId);

    Optional<Task> findByScheduleIdAndId(UUID scheduleId, UUID Id);
}
