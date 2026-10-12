public class DoublyLinkedList {

    private static class Node {
        int value;
        Node prev;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public DoublyLinkedList() {
    }

    public DoublyLinkedList(int... values) {
        for (int v : values) {
            addLast(v);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Node cur = head;
        while (cur != null) {
            Node next = cur.next;
            cur.prev = null;
            cur.next = null;
            cur = next;
        }
        head = null;
        tail = null;
        size = 0;
    }

    public void addLast(int value) {
        Node node = new Node(value);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
        size++;
    }

    public void addFirst(int value) {
        Node node = new Node(value);
        if (head == null) {
            tail = node;
        } else {
            head.prev = node;
            node.next = head;
        }
        head = node;
        size++;
    }

    public int get(int index) {
        return nodeAt(index).value;
    }

    public void set(int index, int value) {
        nodeAt(index).value = value;
    }

    public void insert(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Неверный индекс вставки: " + index + ", размер: " + size);
        }
        if (index == 0) {
            addFirst(value);
            return;
        }
        if (index == size) {
            addLast(value);
            return;
        }
        Node next = nodeAt(index);
        Node prev = next.prev;
        Node node = new Node(value);
        node.prev = prev;
        node.next = next;
        prev.next = node;
        next.prev = node;
        size++;
    }

    public int remove(int index) {
        Node node = nodeAt(index);
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
        size--;
        return node.value;
    }

    public void forEach(ToDo<Integer> action) {
        for (Node cur = head; cur != null; cur = cur.next) {
            action.toDo(cur.value);
        }
    }

    public void forEachReverse(ToDo<Integer> action) {
        for (Node cur = tail; cur != null; cur = cur.prev) {
            action.toDo(cur.value);
        }
    }

    public void sort() {
        head = mergeSort(head);
        Node prev = null;
        for (Node cur = head; cur != null; cur = cur.next) {
            cur.prev = prev;
            prev = cur;
        }
        tail = prev;
    }

    private Node mergeSort(Node first) {
        if (first == null || first.next == null) {
            return first;
        }
        Node middle = middleOf(first);
        Node second = middle.next;
        middle.next = null;
        return merge(mergeSort(first), mergeSort(second));
    }

    private Node middleOf(Node first) {
        Node slow = first;
        Node fast = first.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private Node merge(Node a, Node b) {
        Node dummy = new Node(0);
        Node last = dummy;
        while (a != null && b != null) {
            if (a.value <= b.value) {
                last.next = a;
                a = a.next;
            } else {
                last.next = b;
                b = b.next;
            }
            last = last.next;
        }
        last.next = (a != null) ? a : b;
        return dummy.next;
    }

    public boolean isSorted() {
        for (Node cur = head; cur != null && cur.next != null; cur = cur.next) {
            if (cur.value > cur.next.value) {
                return false;
            }
        }
        return true;
    }

    public int[] toArray() {
        int[] res = new int[size];
        int i = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            res[i++] = cur.value;
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node cur = head; cur != null; cur = cur.next) {
            sb.append(cur.value);
            if (cur.next != null) {
                sb.append(" <-> ");
            }
        }
        return sb.append("]").toString();
    }

    private Node nodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Неверный индекс: " + index + ", размер: " + size);
        }
        Node cur;
        if (index < size / 2) {
            cur = head;
            for (int i = 0; i < index; i++) {
                cur = cur.next;
            }
        } else {
            cur = tail;
            for (int i = size - 1; i > index; i--) {
                cur = cur.prev;
            }
        }
        return cur;
    }
}
