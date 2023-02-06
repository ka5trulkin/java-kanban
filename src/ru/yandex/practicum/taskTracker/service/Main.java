package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.model.Epic;
import ru.yandex.practicum.taskTracker.model.Subtask;
import ru.yandex.practicum.taskTracker.model.Task;

import java.time.*;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("новый эпик 1", "описание эпика 1", manager.setId());
        manager.addNewEpic(epic);
        //Данная задача должна быть 4 по счёту
        Subtask subtask1 = new Subtask("новая подзадача 1", "описание подзадачи 1",LocalDateTime.of(2022, 12, 30, 0, 30).plusDays(2), Duration.ofMinutes(15),
                manager.setId(), epic.getId());
        manager.addNewSubtask(subtask1);
        //Данная задача должна быть 1 по счёту
        Subtask subtask2 = new Subtask("новая подзадача 2", "описание подзадачи 2",LocalDateTime.of(2022, 12, 30, 0, 30), Duration.ofMinutes(30),
                manager.setId(), epic.getId());
        manager.addNewSubtask(subtask2);
        //Данная задача должна быть 3 по счёту
        Subtask subtask3 = new Subtask("новая подзадача 3", "описание подзадачи 3", LocalDateTime.of(2022, 12, 30, 0, 30).plusDays(1), Duration.ofMinutes(45),
                manager.setId(), epic.getId());
        manager.addNewSubtask(subtask3);
        //Данная задача должна быть 2 по счёту
        Subtask subtask4 = new Subtask("новая подзадача 4", "описание подзадачи 4",LocalDateTime.of(2022, 12, 30, 0, 30).plusHours(12), Duration.ofMinutes(60),
                manager.setId(), epic.getId());
        manager.addNewSubtask(subtask4);
        //Порядок добавления следующий subtask1 -> subtask2 -> subtask3 -> subtask4
        //Порядок следующий subtask2 -> subtask4 -> subtask3 -> subtask1
        System.out.println(epic.getDuration() + " == " + (subtask1.getDuration().plus(subtask2.getDuration()).plus(subtask3.getDuration()).plus(subtask4.getDuration()) + " " + (epic.getDuration().equals(subtask1.getDuration().plus((subtask2.getDuration().plus(subtask3.getDuration()).plus((subtask4.getDuration()))))))));
        System.out.println(epic.getStartTime() + " == " + subtask2.getStartTime() + " " + epic.getStartTime().equals(subtask2.getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask1.getEndTime() + " " + epic.getEndTime().equals(subtask1.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()));
        //Удаление подзадачи subtask3
        manager.removeSubtaskById(subtask3.getId());
        //Новый порядок subtask2 -> subtask4 -> subtask1
        System.out.println(epic.getDuration() + " == " + (subtask1.getDuration().plus(subtask2.getDuration()).plus(subtask4.getDuration()) + " " + (epic.getDuration().equals((subtask1.getDuration()).plus(subtask2.getDuration()).plus(subtask4.getDuration())))));
        System.out.println(epic.getStartTime() + " == " + subtask2.getStartTime() + " " + epic.getStartTime().equals(subtask2.getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask1.getEndTime() + " " + epic.getEndTime().equals(subtask1.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()));
        //Удаление подзадачи subtask2
        manager.removeSubtaskById(subtask2.getId());
        //Новый порядок subtask4 -> subtask1
        System.out.println(epic.getDuration() + " == " + (subtask1.getDuration().plus(subtask4.getDuration())  + " " + (epic.getDuration().equals(subtask1.getDuration().plus(subtask4.getDuration())))));
        System.out.println(epic.getStartTime() + " == " + subtask4.getStartTime() + " " + epic.getStartTime().equals(subtask4.getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask1.getEndTime() + " " + epic.getEndTime().equals(subtask1.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()));
        //Удаление подзадачи subtask1
        manager.removeSubtaskById(subtask1.getId());
        //Новый порядок subtask4
        System.out.println(epic.getDuration() + " == " + subtask4.getDuration()  + " " + (epic.getDuration().equals(subtask4.getDuration())));
        System.out.println(epic.getStartTime() + " == " + subtask4.getStartTime() + " " + epic.getStartTime().equals(subtask4.getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask4.getEndTime() + " " + epic.getEndTime().equals(subtask4.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(Task::getTaskName).collect(Collectors.toList()));
    }
}
