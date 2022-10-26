package taskTracker.service;

import taskTracker.model.Epic;
import taskTracker.model.Status;
import taskTracker.model.SubTask;
import taskTracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    // Получение списка всех задач
    public ArrayList<String> getTaskList(ArrayList<Task> object) {
        ArrayList<String> result = new ArrayList<>();

        for (Task task : object) {
            if (task != null) {
                result.add(task.getTaskName());
            }
        }
        if (result.size() == 0) {
            result.add(printListIsEmpty());
        }
        return result;
    }

    // Получение списка всех эпиков
    public HashMap<String, ArrayList<String>> getEpicList(ArrayList<Epic> object) {
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

    // Удаление всех задач
    public void deleteAllTasks(ArrayList<Task> object) {
        System.out.println("Все задачи удалены");
        object.clear();
    }

    // Удаление всех эпиков
    public void deleteAllEpic(ArrayList<Epic> object) {
        System.out.println("Все эпики удалены");
        object.clear();
    }

    // Получение по идентификатору задачи
    public Task getTaskById(ArrayList<Task> object, int idTask) {
        Task result = null;

        for (Task task : object) {
            if (task.getId() == idTask) {
                result = task;
                System.out.println("ID " + idTask + ": " + result.getTaskName());
                break;
            }
        }
        return result;
    }

    // Получение по идентификатору эпика
    public Epic getEpicById(ArrayList<Epic> object, int idEpic) {
        Epic result = null;

        for (Epic epic : object) {
            if (epic.getId() == idEpic) {
                result = epic;
                System.out.println("ID " + idEpic + ": " + result.getEpicName());
                break;
            }
        }
        return result;
    }

    // Получение по идентификатору подзадачи
    public SubTask getSubtaskById(ArrayList<Epic> object, int idSubtask) {
        SubTask result = null;
        boolean isFond = false;

        for (Epic epic : object) {
            for (int index = 0; index < epic.getSubtasks().size(); index++) {
                if (epic.getSubtasks().get(index).getId() == idSubtask) {
                    result = epic.getSubtasks().get(index);
                    System.out.println("ID " + idSubtask + ": " + result.getTaskName());
                    isFond = true;
                    break;
                }
                if (isFond) {
                    break;
                }
            }
        }
        return result;
    }

    // Создание задачи. Сам объект должен передаваться в качестве параметра
    public void addTask(ArrayList<Task> object, Task task) {
        if (task == null) {
            printInputIsEmpty();
        } else {
            boolean isSameTask = false;

            for (Task value : object) {
                if ((value.getId() == task.getId()) && (value.isDone() == task.isDone())) {
                    isSameTask = true;
                    System.out.println("Такая задача уже открыта");
                    break;
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
            printInputIsEmpty();
        } else {
            boolean isSameEpic = false;

            for (Epic value : object) {
                if ((value.getId() == epic.getId()) && (value.isDone() == epic.isDone())) {
                    isSameEpic = true;
                    System.out.println("Такой эпик уже открыт");
                    break;
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
            printInputIsEmpty();
        } else {
            boolean isExistEpic = false; // Для проверки работоспособности
            boolean isCreateSubtask = false;

            for (Epic epic : object) {
                if (epic.getId() == idEpic) {
                    isExistEpic = true;
                    boolean isSame = false;

                    for (SubTask task : epic.getSubtasks()) {
                        if ((task.getId() == subTask.getId()) && (task.getStatus() == subTask.getStatus())) {
                            isSame = true;
                            break;
                        }
                    }
                    if (!(isSame)) {
                        epic.getSubtasks().add(subTask);
                        epic.setCounterSubtaskUp(true);
                        System.out.println("Подзадача '" + subTask.getTaskName() + "' добавлена");
                        isCreateSubtask = true;
                    } else {
                        System.out.println("Такая подзадача уже открыта");
                    }
                }
                if (isCreateSubtask) {
                    break;
                }
            }
            if (!(isExistEpic)) {
                System.out.println("Эпика с таким ID не существует");
            }
        }
    }

    // Обновление задач. Новая версия объекта с верным идентификатором передаётся в виде параметра
    public void updateTask(ArrayList<Task> object, int idTask, Task task) {
        if (object.isEmpty()) {
            System.out.println(printListIsEmpty());
        } else {
            boolean isContains = false;

            for (int index = 0; index < object.size(); index++) {
                if (object.get(index).getId() == idTask) {
                    object.add(index, task);
                    object.remove(index + 1);
                    isContains = true;
                    printUpdateComplete();
                    break;
                }
            }
            if (!(isContains)) { // Для проверки работоспособности
                printTaskNoFoundById(idTask);
            }
        }
    }

    // Обновление эпика. Новая версия объекта с верным идентификатором передаётся в виде параметра
    public void updateEpic(ArrayList<Epic> object, int idEpic, Epic epic) {
        if (object.isEmpty()) {
            System.out.println(printListIsEmpty());
        } else {
            boolean isContains = false;
            int counter = 0;

            for (int index = 0; index < object.size(); index++) {
                if (object.get(index).getId() == epic.getId()) {
                    object.add(index, epic);
                    object.remove(index + 1);
                    isContains = true;
                    printUpdateComplete();
                    for (SubTask subtask : object.get(index).getSubtasks()) {
                        if (subtask.getStatus() != Status.DONE) {
                            counter++;
                        }
                    }
                    if ((counter == 0) || (object.get(index).getSubtasks().size() == 0)) {
                        object.get(index).setDone();
                    }
                }
            }
            if ((!isContains)) {
                printTaskNoFoundById(idEpic);
            }
        }
    }

    // Удаление задачи по идентификатору
    public void deleteTaskById(ArrayList<Task> object, int idTask) {
        if (object.isEmpty()) {
            System.out.println(printListIsEmpty());
        } else {
            boolean isContains = false;

            for (int index = 0; index < object.size(); index++) {
                if (object.get(index).getId() == idTask) {
                    object.remove(index);
                    isContains = true;
                    System.out.println("Удаление прошло успешно");
                    break;
                }
            }
            if (!(isContains)) { // Для проверки работоспособности
                printTaskNoFoundById(idTask);
            }
        }
    }

    // Удаление эпика по идентификатору
    public void deleteEpicById(ArrayList<Epic> object, int idEpic) {
        if (object.isEmpty()) {
            System.out.println(printListIsEmpty());
        } else {
            boolean isContains = false;

            for (int index = 0; index < object.size(); index++) {
                if (object.get(index).getId() == idEpic) {
                    object.remove(index);
                    isContains = true;
                    System.out.println("Удаление прошло успешно");
                    break;
                }
            }
            if (!(isContains)) { // Для проверки работоспособности
                printTaskNoFoundById(idEpic);
            }
        }
    }

    // Удаление подзадачи по идентификатору
    public void deleteSubtaskById(ArrayList<Epic> object, int idEpic, int idSubtask) {
        if (object.isEmpty()) {
            System.out.println(printListIsEmpty());
        } else {
            boolean isDelete = false;

            for (Epic epic : object) {
                ArrayList<SubTask> getSubtask = epic.getSubtasks();

                if (epic.getId() == idEpic) {
                    for (int index = 0; index < epic.getSubtasks().size(); index++) {
                        if (getSubtask.get(index).getId() == idSubtask) {
                            getSubtask.remove(index);
                            epic.setCounterSubtaskUp(false);
                            isDelete = true;
                            break;
                        }
                    }
                }
                if (isDelete) {
                    break;
                }
            }
        }
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<String> getSubtaskListByEpic(ArrayList<Epic> object, int idEpic) {
        ArrayList<String> result = new ArrayList<>();

        if (object.isEmpty()) {
            System.out.println(printListIsEmpty());
        } else {


            for (Epic epic : object) {
                if (epic.getId() == idEpic) {
                    for (SubTask subtask : epic.getSubtasks()) {
                        if (subtask.getTaskName() != null) {
                            result.add(subtask.getTaskName());
                        }
                    }
                }
            }
        }
        return result;
    }

    private void printUpdateComplete() {
        System.out.println("Обновление прошло успешно");
    }

    private void printInputIsEmpty() {
        System.out.println("Введено пустое значение");
    }

    private String printListIsEmpty() {
        return "Список задач пуст";
    }

    private void printTaskNoFoundById(int idTask) {
        System.out.println("Задачи по ID " + idTask + ": не найдено");
    }
}
