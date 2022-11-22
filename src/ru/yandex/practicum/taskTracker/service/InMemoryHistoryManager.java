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
        final Node<T> prevNode = tail;
        final Node<T> newNode = new Node<>(prevNode, data, null);

        nodeMap.put(id, newNode);
        tail = newNode;
        if (prevNode == null) {
            head = newNode;
        } else {
            prevNode.next = newNode;
        }
        size++;
    }

    void removeNode(Node<T> node) {
        if (node == head) {
            if (node.next != null) {
                head = node.next;
                head.prev = null;
                if (head.next == null) {
                    tail = null;
                }
            } else {
                head = null;
                tail = null;
            }
        } else if (node == tail) {
            tail = tail.prev;
            tail.next = null;
            if (head.equals(tail)) {
                head.next = null;
                tail = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
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
        Node<T> node = head;

        while (node != null) {
            result.add(node.data);
            node = node.next;
        }
        return result;
    }

    private static class Node <T> {
        private final T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

}