package ru.yandex.practicum.taskTracker.service;

import ru.yandex.practicum.taskTracker.interfaces.HistoryManager;
import ru.yandex.practicum.taskTracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            int idTask = task.getId();

            remove(idTask);
            history.linkLast(task, task.getId());
        }
    }

    @Override
    public void remove(int id) {
        if (history.getNodeById(id) != null) {
            history.removeNode(history.getNodeById(id));
            history.getNodeMap().remove(id);
        }
    }

    @Override
    public void clearAll(List<Integer> idList) {
        for (Integer id : idList) {
            remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.toArrayList();
    }
}

class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size = 0;
    private final Map<Integer, Node<T>> nodeMap = new HashMap<>();

    void linkLast(T data, int id) {
        final Node<T> t = tail;
        final Node<T> newNode = new Node<>(t, data, null);

        nodeMap.put(id, newNode);
        tail = newNode;
        if (t == null) {
            head = newNode;
        } else {
            t.setNext(newNode);
        }
        size++;
    }

    void removeNode(Node<T> node) {
        if (node.equals(head)) {
            if (node.getNext() != null) {
                head = node.getNext();
                head.setPrev(null);
                if (head.getNext() == null) {
                    tail = null;
                }
            } else {
                head = null;
                tail = null;
            }
        } else if (node.equals(tail)) {
            tail = tail.getPrev();
            tail.setNext(null);
            if (head.equals(tail)) {
                head.setNext(null);
                tail = null;
            }
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        size--;
    }

    public Map<Integer, Node<T>> getNodeMap() {
        return nodeMap;
    }

    Node<T> getNodeById(int id) {
        return nodeMap.get(id);
    }

    boolean contains(Integer id) {
        return nodeMap.containsKey(id);
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public int getSize() {
        return size;
    }

    List<T> toArrayList() {
        List<T> result = new ArrayList<>();
        for (Node<T> node = head; node != null; node = node.getNext()) {
            result.add(node.getData());
        }
        return result;
    }
}