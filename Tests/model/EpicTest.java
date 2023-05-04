package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.Status.*;

public class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void epicHasNewStatusWhenSubTaskListIsEmpty() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        Assertions.assertEquals(NEW, epic.getStatus(), "Status generates incorrectly");
    }

    @Test
    public void epicHasNewStatusWhenAllSubTasksAreNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask #9", "DS", 1, LocalDateTime.now(), Duration.ofDays(2)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask #10", "DS", 1, LocalDateTime.of(2023, 04, 30, 21, 30), Duration.ofMinutes(35)));
        Assertions.assertEquals(NEW, epic.getStatus(), "Status calculates incorrectly");
    }

    @Test
    public void epicHasDoneStatusWhenAllSubTasksAreDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask #9", "DS", 1, LocalDateTime.now(), Duration.ofDays(2)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask #10", "DS", 1, LocalDateTime.of(2023, 04, 30, 21, 30), Duration.ofMinutes(35)));
        taskManager.updateSubTask(subTask1, DONE);
        taskManager.updateSubTask(subTask2, DONE);
        Assertions.assertEquals(DONE, epic.getStatus(), "Status calculates incorrectly");
    }

    @Test
    public void epicHasInProgressStatusWhenSubTasksAreDoneAndNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask #9", "DS", 1, LocalDateTime.now(), Duration.ofDays(2)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask #10", "DS", 1, LocalDateTime.of(2023, 04, 30, 21, 30), Duration.ofMinutes(35)));
        taskManager.updateSubTask(subTask1, DONE);
        taskManager.updateSubTask(subTask2, NEW);
        Assertions.assertEquals(IN_PROGRESS, epic.getStatus(), "Status calculates incorrectly");
    }

    @Test
    public void epicHasInProgressStatusWhenSubTasksAreInProgress() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask #9", "DS", 1, LocalDateTime.now(), Duration.ofDays(2)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask #10", "DS", 1, LocalDateTime.of(2023, 04, 30, 21, 30), Duration.ofMinutes(35)));
        taskManager.updateSubTask(subTask1, IN_PROGRESS);
        taskManager.updateSubTask(subTask2, IN_PROGRESS);
        Assertions.assertEquals(IN_PROGRESS, epic.getStatus(), "Status calculates incorrectly");
    }
}