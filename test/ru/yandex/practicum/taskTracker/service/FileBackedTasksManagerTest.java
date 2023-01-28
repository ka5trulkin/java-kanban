package ru.yandex.practicum.taskTracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskTracker.model.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    FileBackedTasksManager manager = new FileBackedTasksManager(new File("resource/backup-task-manager.csv"));

    @Test
    void loadFromFile() {
        File testManagerFile = new File("resource/test-backup-task-manager.csv");
        File testHistoryManagerFile = new File("resource/test-history-manager.csv");
        File wrongFile = new File("wrongPath");
        String testHistoryManager;
        String expectedMessage = "Ошибка чтения " + wrongFile;
        FileBackedTasksManager managerTest = FileBackedTasksManager.loadFromFile(testManagerFile);

        try {
            testHistoryManager = Files.readString(testHistoryManagerFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(tasksList, managerTest.getTasks());
        assertEquals(subtasksList, managerTest.getSubtasks());
        epicsList.get(firstTaskInList).setStatus(Status.IN_PROGRESS);
        assertEquals(epicsList, managerTest.getEpics());
        System.out.println(managerTest.historyManager.getHistory().toString());
        assertEquals(testHistoryManager, managerTest.historyManager.getHistory().toString());
        ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                () -> FileBackedTasksManager.loadFromFile(wrongFile)
        );
        assertEquals(expectedMessage, exception.getMessage());
        Managers.getDefault();
    }

    @Test
    void getTasks() {
        super.getTasks(manager);
    }

    @Test
    void getEpics() {
        super.getEpics(manager);
    }

    @Test
    void getSubtasks() {
        super.getSubtasks(manager);
    }

    @Test
    void clearAllTasks() {
        super.clearAllTasks(manager);
    }

    @Test
    void clearAllEpics() {
        super.clearAllEpics(manager);
    }

    @Test
    void clearAllSubtasks() {
        super.clearAllSubtasks(manager);
    }

    @Test
    void getTaskById() {
        super.getTaskById(manager);
    }

    @Test
    void getEpicById() {
        super.getEpicById(manager);
    }

    @Test
    void getSubTaskById() {
        super.getSubTaskById(manager);
    }

    @Test
    void addNewTask() {
        super.addNewTask(manager);
    }

    @Test
    void addNewEpic() {
        super.addNewEpic(manager);
    }

    @Test
    void addNewSubtask() {
        super.addNewSubtask(manager);
    }

    @Test
    void updateTask() {
        super.updateTask(manager);
    }

    @Test
    void updateEpic() {
        super.updateEpic(manager);
    }

    @Test
    void updateSubtask() {
        super.updateSubtask(manager);
    }

    @Test
    void removeTaskById() {
        super.removeTaskById(manager);
    }

    @Test
    void removeEpicById() {
        super.removeEpicById(manager);
    }

    @Test
    void removeSubtaskById() {
        super.removeSubtaskById(manager);
    }
}