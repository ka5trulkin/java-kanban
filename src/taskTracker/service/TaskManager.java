package taskTracker.service;

import taskTracker.model.Epic;
import taskTracker.model.SubTask;
import taskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public ArrayList<String> getTaskList(ArrayList<Task> object) { // Получение списка всех задач
        ArrayList<String> result = new ArrayList<>();

        for (Task task : object) {
            if (task != null) {
                result.add(task.getTaskName());
            }
        }
        if (result.size() == 0) {
            result.add(printEmptyList());
        }
        return result;
    }

    public HashMap<String, ArrayList<String>> getEpicList(ArrayList<Epic> object) { // Получение списка всех эпиков
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        ArrayList<String> resultValues = new ArrayList<>();

        for (Epic epic : object) {
            for (SubTask subtask : epic.getSubtasks()) {
                resultValues.add(subtask.getTaskName());
            }
            result.put(epic.getEpicName(), resultValues);
            resultValues = new ArrayList<>();
        }
        return result;
    }
    
    public void deleteAllTasks(ArrayList<Task> object) { // Удаление всех задач
        System.out.println("Все задачи удалены");
        object.clear();
    }

    public void deleteAllEpic(ArrayList<Epic> object) { // Удаление всех эпиков
        System.out.println("Все эпики удалены");
        object.clear();
    }

    public Task getTaskById(ArrayList<Task> object, int idTask) { // Получение по идентификатору задачи
        Task result = null;

        for (Task task : object) {
            if (task.getId() == idTask) {
                result = task;
                System.out.println("ID " + idTask + ": " + result.getTaskName());
            }
        }
        return result;
    }

    public Epic getEpicById(ArrayList<Epic> object, int idEpic) { // Получение по идентификатору эпика
        Epic result = null;

        for (Epic epic : object) {
            if (epic.getId() == idEpic){
                result = epic;
                System.out.println("ID " + idEpic + ": " + result.getEpicName());
            }
        }
        return result;
    }

    public SubTask getSubtaskById(ArrayList<Epic> object, int idSubtask) { // Получение по идентификатору подзадачи
        SubTask result = null;

        for (Epic epic : object) {
            for (int index = 0; index < epic.getSubtasks().size(); index++) {
                if (epic.getSubtasks().get(index).getId() == idSubtask) {
                    result = epic.getSubtasks().get(index);
                    System.out.println("ID " + idSubtask + ": " + result.getTaskName());
                }
            }
        }
        return result;
    }

    // Если надо создавать сам объект в методе, то перепишу код
    // Создание задачи. Сам объект должен передаваться в качестве параметра
    public void addTask(ArrayList<Task> object, Task task) {
        if (task == null) {
            System.out.println("Введено пустое значение");
        } else {
            boolean isSameTask = false;

            for (Task value : object) {
                if ((value.getId() == task.getId()) && (value.isDone() == task.isDone())) {
                    isSameTask = true;
                    System.out.println("Такая задача уже открыта");
                }
            }
            if (!(isSameTask)) {
                System.out.println("Задача '" + task.getTaskName() + "' добавлена");
                object.add(task);
            }
        }
    }

    // Создание эпика. Сам объект должен передаваться в качестве параметра
    public void addEpic(ArrayList<Epic> object, Epic epic) {
        if (epic == null) {
            System.out.println("Введено пустое значение");
        } else {
            boolean isSameEpic = false;

            for (Epic value : object) {
                if ((value.getId() == epic.getId()) && (value.isDone() == epic.isDone())) {
                    isSameEpic = true;
                    System.out.println("Такой эпик уже открыт");
                }
            }
            if (!(isSameEpic)) {
                System.out.println("Эпик '" + epic.getEpicName() + "' добавлен");
                object.add(epic);
            }
        }
    }
    // Создание подзадачи. Сам объект должен передаваться в качестве параметра
    public void addSubtaskToEpic(ArrayList<Epic> object, int idEpic, SubTask subTask) {
        if (subTask == null) {
            System.out.println("Введено пустое значение");
        } else {
            boolean isCreate = false;

            for (Epic epic : object) {
                if (epic.getId() == idEpic) {
                    isCreate = true;
                    boolean isSame = false;

                    for (SubTask task : epic.getSubtasks()) {
                        if ((task.getId() == subTask.getId()) && (task.getStatus() == subTask.getStatus())) {
                            isSame = true;
                            break;
                        }
                    } if (!(isSame)) {
                        epic.getSubtasks().add(subTask);
                        epic.setCounterSubtask();
                        isCreate = true;
                        System.out.println("Подзадача '" + subTask.getTaskName() + "' добавлена");
                    } else {
                        System.out.println("Такая подзадача уже открыта");
                    }
                }
            }
            if (!(isCreate)) {
                System.out.println("Эпика с таким ID не существует");
            }
        }
    }

    // Обновление задач. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask (ArrayList<Task> object, int idTask, Task task) {
        if (object.isEmpty()) {
            System.out.println(printEmptyList());
        } else {
            boolean isContains = false;

            for (int index = 0; index < object.size(); index++) {
                if (object.get(index).getId() == idTask) {
                    object.add(index, task);
                    object.remove(index + 1);
                    isContains = true;
                    System.out.println("Обновление прошло успешно");
                }
            }
            if (!(isContains)) { // Для проверки работоспособности
                printTaskNoFound(idTask);
            }
        }
    }
    public void deleteTaskById(ArrayList<Task> object, int idTask) { // Удаление по идентификатору
        if (object.isEmpty()) {
            System.out.println(printEmptyList());
        } else {
            boolean isContains = false;

            for (int index = 0; index < object.size(); index++) {
                if (object.get(index).getId() == idTask) {
                    object.remove(index);
                    isContains = true;
                    System.out.println("Удаление прошло успешно");
                }
            }
            if (!(isContains)) { // Для проверки работоспособности
                printTaskNoFound(idTask);
            }
        }
    }

//    public void setNewSubtask(Epic epic, SubTask subTask) {
//        if (subTask != null) {
//            epic.setSubTask(subTask);
//            System.out.println("Подзадание " + subTask.getTaskName() + " успешно добавлено в Эпик " + subTask.getNameEpic());
//            epic.setCounterSubtask();
//        } else {
//            System.out.println("Подзадание не содержит данных");
//        }
//    }


    private String printEmptyList() {
        return  "Список задач пуст";
    }

    private void printTaskNoFound(int idTask) {
        System.out.println("Задачи по ID: " + idTask + " не найдено");
    }
}
