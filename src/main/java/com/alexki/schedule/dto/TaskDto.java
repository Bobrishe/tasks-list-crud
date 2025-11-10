package com.alexki.schedule.dto;

import com.alexki.schedule.entities.TaskPriority;
import com.alexki.schedule.entities.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String description,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status
) {
}
