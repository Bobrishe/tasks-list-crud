package com.alexki.schedule;

import com.alexki.schedule.entities.Schedule;
import com.alexki.schedule.entities.Task;
import com.alexki.schedule.repositories.ScheduleRepository;
import com.alexki.schedule.services.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleService scheduleService;

    private Schedule schedule;

    @BeforeEach
    public void setUp() {
        schedule = new Schedule();
        schedule.setTitle("Test Schedule");
    }

    @Test
    public void shouldCreatedSchedule() {

        Schedule createdSchedule = scheduleService.createSchedule(schedule);

        assertNotNull(createdSchedule.getId());
        assertNotNull(createdSchedule.getCreated());

        assertEquals("Test Schedule", createdSchedule.getTitle());

        Optional<Schedule> scheduleFromDb = scheduleRepository.findById(createdSchedule.getId());
        assertTrue(scheduleFromDb.isPresent());
    }

    @Test
    public void shouldThrowWhenCreateScheduleWithId() {

        schedule.setId(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.createSchedule(schedule));

    }

    @Test
    public void shouldThrowWhenCreateScheduleWithoutTitle() {

        String[] invalidTitles = {null, "", " "};

        for (String invalidTitle : invalidTitles) {
            schedule.setTitle(invalidTitle);

            assertThrows(IllegalArgumentException.class, () ->
                    scheduleService.createSchedule(schedule)
            );
        }
    }

    @Test
    public void shouldThrowWhenGetScheduleWithNullIdOrInvalidId() {
        assertThrows(NullPointerException.class, () -> scheduleService.getSchedule(null));

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.getSchedule(UUID.randomUUID()));
    }

    @Test
    public void shouldGetScheduleById() {
        Schedule createdSchedule = scheduleService.createSchedule(schedule);

        Schedule expectedSchedule = scheduleService.getSchedule(createdSchedule.getId());

        assertEquals(createdSchedule.getId(), expectedSchedule.getId());
        assertEquals(createdSchedule.getTitle(), expectedSchedule.getTitle());
    }

    @Test
    public void shouldGetAllSchedules() {

        List<Schedule> beforeCreatingCount = scheduleService.getSchedules();

        int shouldCreateCount = 3;

        for (int i = 0; i < shouldCreateCount; i++) {
            schedule.setTitle("Schedule " + i);
            scheduleService.createSchedule(schedule);
        }

        List<Schedule> afterCreatingCount = scheduleService.getSchedules();

        assertEquals(shouldCreateCount, afterCreatingCount.size() - beforeCreatingCount.size());

    }

    @Test
    public void shouldThrowWhenGetScheduleTasksWithNullIdOrInvalidId() {
        assertThrows(NullPointerException.class, () ->
                scheduleService.getScheduleTasksById(null));

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.getScheduleTasksById(UUID.randomUUID()));
    }

    @Test
    public void shouldGetScheduleTasksById() {
        Schedule createdSchedule = scheduleService.createSchedule(schedule);

        List<Task> taskList = scheduleService.getScheduleTasksById(createdSchedule.getId());

        assertNotNull(taskList);
    }

    @Test
    public void shouldThrowWhenUpdateScheduleWithNullOrChangedId() {
        assertThrows(NullPointerException.class, () ->
                scheduleService.updateSchedule(null, schedule));

        assertThrows(IllegalArgumentException.class, () -> {
            schedule.setId(UUID.randomUUID());
            scheduleService.updateSchedule(UUID.randomUUID(), schedule);
        });
    }

    @Test
    public void shouldDeleteSchedule() {

        Schedule createdSchedule = scheduleService.createSchedule(schedule);
        scheduleService.deleteSchedule(createdSchedule.getId());

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.getSchedule(createdSchedule.getId()));
    }

}
