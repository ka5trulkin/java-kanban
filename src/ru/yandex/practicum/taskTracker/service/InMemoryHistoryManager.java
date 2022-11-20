package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            int idTask = task.getId();
            if (task.equals(history.get(idTask))) {
                history.removeNode(history.getNodeById(idTask));
            }
            history.linkLast(task, task.getId());
        }
    }

    @Override
    public void remove(int id) {
        if (history.getNodeById(id) != null) {
            history.removeNode(history.getNodeById(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}

class CustomLinkedList<T> {
    public Node<T> head;
    public Node<T> tail;
    private int size = 0;
    final private Map<Integer, Node<T>> nodeList = new HashMap<>();

    void linkLast(T data, int id) {
        final Node<T> t = tail;
        final Node<T> newNode = new Node<>(t, data, null);
        nodeList.put(id, newNode);
        tail = newNode;
        if (t == null) {
            head = newNode;
        } else {
            t.setNext(newNode);
        }
        size++;
    }

    void removeNode(Node<T> node) {
        for (Integer id : nodeList.keySet()) {
            if (nodeList.get(id).equals(node)) {
                nodeList.remove(id);
            }
        }
    }

    Node<T> getNodeById(int id) {
        return nodeList.get(id);
    }

    T get(Integer id) {
        return nodeList.get(id).getData();
    }
}