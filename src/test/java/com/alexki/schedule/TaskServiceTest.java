package com.alexki.schedule;

import com.alexki.schedule.entities.Schedule;
import com.alexki.schedule.entities.Task;
import com.alexki.schedule.entities.TaskPriority;
import com.alexki.schedule.entities.TaskStatus;
import com.alexki.schedule.repositories.TaskRepository;
import com.alexki.schedule.services.ScheduleService;
import com.alexki.schedule.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ScheduleService scheduleService;

    private Schedule schedule;
    private Task task;

    @BeforeEach
    public void setUp() {
        schedule = new Schedule();
        schedule.setTitle("Test Schedule");
        schedule = scheduleService.createSchedule(schedule);

        task = new Task();
        task.setTitle("Test Task");
    }

    @Test
    public void shouldThrowWhenTaskIdNotNull() {
        task.setId(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.createNewTask(schedule.getId(), task));

    }

    @Test
    public void shouldThrowWhenTitleIsNullOrBlank() {

        String[] invalidTitles = {null, "", "  "};

        for (String title : invalidTitles) {
            task.setTitle(title);
            assertThrows(IllegalArgumentException.class, () ->
                    taskService.createNewTask(schedule.getId(), task));
        }

    }

    @Test
    public void shouldThrowWhenScheduleNotFound() {

        assertThrows(IllegalArgumentException.class, () ->
                taskService.createNewTask(UUID.randomUUID(), task));

    }


    @Test
    public void shouldCreateTask() {

        Task createdTask = taskService.createNewTask(schedule.getId(), task);

        assertNotNull(createdTask.getId());
        assertNotNull(createdTask.getCreated());
        assertEquals("Test Task", createdTask.getTitle());

        Optional<Task> fromDb = taskRepository.findById(createdTask.getId());

        assertTrue(fromDb.isPresent());
    }

    @Test
    public void shouldGetTaskByScheduleIdAndTaskId() {

        Task createdTask = taskService.createNewTask(schedule.getId(), task);

        Task expectedTask = taskService.getTaskByListIdAndId(schedule.getId(), createdTask.getId());

        assertEquals(createdTask.getId(), expectedTask.getId());
        assertEquals(createdTask.getTitle(), expectedTask.getTitle());
    }

    @Test
    public void shouldThrowWhenGetTaskWithInvalidIds() {
        assertThrows(IllegalArgumentException.class, () ->
                taskService.getTaskByListIdAndId(schedule.getId(), UUID.randomUUID()));

        assertThrows(IllegalArgumentException.class, () ->
                taskService.getTaskByListIdAndId(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    public void shouldThrowWhenUpdateTaskWithNullOrWrongId() {

        Task createdTask = taskService.createNewTask(schedule.getId(), task);

        Task updateTask = new Task();
        updateTask.setTitle("Updated Task");

        assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTask(schedule.getId(), createdTask.getId(), updateTask));

        updateTask.setId(createdTask.getId());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTask(schedule.getId(), UUID.randomUUID(), updateTask));

        assertThrows(IllegalArgumentException.class, () ->
                taskService.updateTask(UUID.randomUUID(), createdTask.getId(), updateTask));

    }

    @Test
    public void shouldUpdateTask() {

        Task createdTask = taskService.createNewTask(schedule.getId(), task);

        Task updateTask = new Task();
        updateTask.setId(createdTask.getId());
        updateTask.setTitle("Updated Task");
        updateTask.setDescription("Updated Description");
        updateTask.setPriority(TaskPriority.HIGH);
        updateTask.setStatus(TaskStatus.CLOSED);

        Task updatedTask = taskService.updateTask(schedule.getId(), createdTask.getId(), updateTask);

        assertEquals(createdTask.getId(), updatedTask.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskPriority.HIGH, updatedTask.getPriority());
        assertEquals(TaskStatus.CLOSED, updatedTask.getStatus());
    }

    @Test
    public void shouldDeleteTask() {

        Task createdTask = taskService.createNewTask(schedule.getId(), task);

        taskService.deleteTask(schedule.getId(), createdTask.getId());

        Optional<Task> fromDb = taskRepository.findById(createdTask.getId());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.getTaskByListIdAndId(schedule.getId(), createdTask.getId()));

        assertTrue(fromDb.isEmpty());
    }


}
