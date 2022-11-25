package ru.yandex.practicum.taskTracker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size = 0;
    private final Map<Integer, Node<T>> nodeMap = new HashMap<>();

    private void removeNode(Node<T> node) {
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
            if (head == tail) {
                tail = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size--;
    }

    private Node<T> linkLast(T data) {
        final Node<T> prevNode = tail;
        final Node<T> newNode = new Node<>(prevNode, data, null);

        tail = newNode;
        if (prevNode == null) {
            head = newNode;
        } else {
            prevNode.next = newNode;
        }
        size++;
        return newNode;
    }

    public void add(int id, T data) {
        nodeMap.put(id, linkLast(data));
    }

    public void remove(int id) {
        if (nodeMap.get(id) != null) {
            Node<T> node = nodeMap.get(id);

            removeNode(node);
            nodeMap.remove(id);
        }
    }

    public List<T> toArrayList() {
        List<T> result = new ArrayList<>();
        Node<T> node = head;

        while (node != null) {
            result.add(node.data);
            node = node.next;
        }
        return result;
    }

    private static class Node<T> {
        final T data;
        Node<T> next;
        Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}