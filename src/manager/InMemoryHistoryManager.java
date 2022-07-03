package manager;

import alltasks.Task;

import java.util.*;

import static java.util.Objects.isNull;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> taskReferences = new HashMap<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        if (taskReferences.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, null, task);
        if (isNull(head)) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        taskReferences.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (Objects.nonNull(current)) {
            tasks.add(current.getReference());
            current = current.getNext();
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        final Node<Task> node = taskReferences.get(id);
        if (node != null) {
            final Node<Task> prev = node.getPrev();
            final Node<Task> next = node.getNext();
            if (prev != null) {
                prev.setNext(next);
            } else {
                head = next;
            }
            if (next != null) {
                next.setPrev(prev);
            } else {
                tail = prev;
            }
            taskReferences.remove(id);
        }
    }

    static class Node<T> {
        private Node<T> prev;
        private Node<T> next;
        private final T reference;

        Node(Node<T> prev, Node<T> next, T reference) {
            this.prev = prev;
            this.next = next;
            this.reference = reference;
        }

        Node<T> getPrev() {
            return prev;
        }

        Node<T> getNext() {
            return next;
        }

        T getReference() {
            return reference;
        }

        void setPrev(final Node<T> newPrev) {
            prev = newPrev;
        }

        void setNext(final Node<T> newNext) {
            next = newNext;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return prev.equals(node.prev) && next.equals(node.next) && reference.equals(node.reference);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prev, next, reference);
        }
    }
}
