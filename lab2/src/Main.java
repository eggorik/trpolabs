import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Создание списка ===");
        DoublyLinkedList list = new DoublyLinkedList();
        System.out.println("Пустой список: " + list + ", размер = " + list.size());
        for (int v : new int[]{42, 7, 19, 3, 88, 25, 61, 14}) {
            list.addLast(v);
            System.out.println("addLast(" + v + "): " + list);
        }
        System.out.println("Размер = " + list.size());

        System.out.println("\n=== Получение по логическому номеру ===");
        System.out.println("get(0) = " + list.get(0));
        System.out.println("get(3) = " + list.get(3));
        System.out.println("get(" + (list.size() - 1) + ") = " + list.get(list.size() - 1));

        System.out.println("\n=== Вставка по логическому номеру ===");
        list.insert(0, 100);
        System.out.println("insert(0, 100):  " + list);
        list.insert(4, 200);
        System.out.println("insert(4, 200):  " + list);
        list.insert(list.size(), 300);
        System.out.println("insert(" + (list.size() - 1) + ", 300): " + list);
        System.out.println("Размер = " + list.size());

        System.out.println("\n=== Удаление по логическому номеру ===");
        System.out.println("remove(0) = " + list.remove(0) + "  -> " + list);
        System.out.println("remove(3) = " + list.remove(3) + "  -> " + list);
        System.out.println("remove(" + (list.size() - 1) + ") = " + list.remove(list.size() - 1) + "  -> " + list);
        System.out.println("Размер = " + list.size());

        System.out.println("\n=== Итератор forEach (анонимные классы) ===");
        System.out.print("Прямой обход:   ");
        list.forEach(new ToDo<Integer>() {
            @Override
            public void toDo(Integer v) {
                System.out.print(v + " ");
            }
        });
        System.out.print("\nОбратный обход: ");
        list.forEachReverse(new ToDo<Integer>() {
            @Override
            public void toDo(Integer v) {
                System.out.print(v + " ");
            }
        });
        System.out.println();

        final int[] sum = {0};
        list.forEach(new ToDo<Integer>() {
            @Override
            public void toDo(Integer v) {
                sum[0] += v;
            }
        });
        System.out.println("Сумма элементов: " + sum[0]);

        System.out.print("Чётные элементы: ");
        list.forEach(new ToDo<Integer>() {
            @Override
            public void toDo(Integer v) {
                if (v % 2 == 0) {
                    System.out.print(v + " ");
                }
            }
        });
        System.out.println();

        System.out.println("\n=== Сортировка рекурсивным слиянием ===");
        System.out.println("До сортировки:    " + list);
        list.sort();
        System.out.println("После сортировки: " + list);
        System.out.print("Обратный обход:   ");
        list.forEachReverse(v -> System.out.print(v + " "));
        System.out.println("\nУпорядочен: " + list.isSorted());

        System.out.println("\n=== Сортировка списка из 100000 случайных чисел ===");
        Random rnd = new Random(8);
        DoublyLinkedList big = new DoublyLinkedList();
        for (int i = 0; i < 100_000; i++) {
            big.addLast(rnd.nextInt(2_000_001) - 1_000_000);
        }
        int[] expected = big.toArray();
        Arrays.sort(expected);
        long t0 = System.nanoTime();
        big.sort();
        long t1 = System.nanoTime();
        System.out.printf("Время сортировки: %.1f мс%n", (t1 - t0) / 1e6);
        System.out.println("Упорядочен: " + big.isSorted());
        System.out.println("Совпадает с Arrays.sort: " + Arrays.equals(expected, big.toArray()));

        System.out.println("\n=== Граничные случаи ===");
        DoublyLinkedList one = new DoublyLinkedList(5);
        one.sort();
        System.out.println("Сортировка из 1 элемента: " + one);
        System.out.println("remove(0) = " + one.remove(0) + ", список: " + one + ", пуст: " + one.isEmpty());
        one.insert(0, 9);
        System.out.println("insert(0, 9) в пустой список: " + one);
        DoublyLinkedList dup = new DoublyLinkedList(3, 1, 3, 2, 1, 3);
        dup.sort();
        System.out.println("Сортировка с повторами: " + dup);

        System.out.println("\n=== Ошибки ===");
        try {
            list.get(100);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        try {
            list.insert(-1, 5);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        try {
            new DoublyLinkedList().remove(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        list.clear();
        System.out.println("\nПосле clear(): " + list + ", размер = " + list.size());
    }
}
