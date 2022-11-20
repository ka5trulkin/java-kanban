package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == MAX_TASKS_IN_HISTORY) {
                history.removeFirst();
            }
            history.add(task);
        }
    }

    @Override
    public void remove(int id) {

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
    public Map<Integer, Node<T>> nodeList = new HashMap<>();

    void linkLast(T data, int id) {
        final Node<T> t = tail;
        final Node<T> newNode = new Node<>(t, data, null);
        nodeList.put(id, newNode);
        tail = newNode;
        if (t == null) {
            head = newNode;
        } else {
            t.next = newNode;
        }
        size++;
    }

    void removeNode()
}