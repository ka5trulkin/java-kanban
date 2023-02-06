package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.interfaces.TaskManager;
import ru.yandex.practicum.taskTracker.model.Status;
import ru.yandex.practicum.taskTracker.model.Subtask;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    @Test
    void test() {
        System.out.println(manager.getClass());
//        tasksList.forEach(System.out::println);
        tasksList.forEach(manager::addNewTask);
        manager.getTasks().forEach(System.out::println);
    }
//    TaskManagerTest manager = new InMemoryTaskManagerTest();

    //    void changeSubtaskStatus(Subtask subtaskFromManager, Status status) {
//        manager.updateSubtask(
//                new Subtask(
//                        subtaskFromManager.getTaskName(),
//                        subtaskFromManager.getDescription(),
//                        subtaskFromManager.getId(),
//                        subtaskFromManager.getEpicId(),
//                        status
//                )
//        );
//    }
//
//    @Test
//    void checkEpicStatus() {
//        manager.addNewEpic(epic);
//        manager.updateEpic(epic);
//        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
//        assertEquals(Collections.emptyList(), manager.getSubtasks());
//        fillManager(manager);
//        assertEquals(Status.IN_PROGRESS, manager.getEpicById(idEpic).getStatus());
//        for (Subtask subtaskFromManager : manager.getSubtasks()) {
//            changeSubtaskStatus(subtaskFromManager, Status.DONE);
//        }
//        assertEquals(Status.DONE, manager.getEpicById(idEpic).getStatus());
//        changeSubtaskStatus(manager.getSubTaskById(idSubtask), Status.NEW);
//        assertEquals(Status.IN_PROGRESS, manager.getEpicById(idEpic).getStatus());
//        for (Subtask subtaskFromManager : manager.getSubtasks()) {
//            changeSubtaskStatus(subtaskFromManager, Status.IN_PROGRESS);
//        }
//        assertEquals(Status.IN_PROGRESS, manager.getEpicById(idEpic).getStatus());
//        manager.clearAllSubtasks();
//        assertEquals(Status.NEW, manager.getEpicById(idEpic).getStatus());
//    }
//    @Test
//    void checkTasksToCrossByStartTime() {
//        String errorByCrossTask
//                = "Задача 'Задача 1' пересекается с другой задачей по времени начала задачи: 2022-07-28T01:21";
//        String errorByCrossSubtask
//                = "Задача 'Подзадача 1' пересекается с другой задачей по времени начала задачи: 2022-07-28T01:41";
//
//        fillManager(manager);
//        IllegalArgumentException taskException = assertThrows(
//                IllegalArgumentException.class,
//                () -> manager.addNewTask(tasksList.get(firstTaskInList))
//        );
//        assertEquals(errorByCrossTask, taskException.getMessage());
//        Assertions.assertDoesNotThrow(() -> manager.addNewEpic(epicsList.get(firstTaskInList)));
//        IllegalArgumentException subtaskExeption = assertThrows(
//                IllegalArgumentException.class,
//                () -> manager.addNewSubtask(subtasksList.get(firstTaskInList))
//        );
//        assertEquals(errorByCrossSubtask, subtaskExeption.getMessage());
//    }

}