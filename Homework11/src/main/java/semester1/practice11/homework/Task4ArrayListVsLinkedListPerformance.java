package semester1.practice11.homework;

import java.util.*;

public class Task4ArrayListVsLinkedListPerformance {
    static long measure(List<Integer> numbers, boolean middle) {
        for (int i = 0; i < 20000; i++) {
            numbers.add(i);
        }
        long start = System.nanoTime();
        for (int i = 0; i < 20000; i++) {
            int index = 0;
            if (middle) {
                index = numbers.size() / 2;
            }
            numbers.add(index, i);
        }
        return System.nanoTime() - start;
    }

    static void compare(boolean middle) {
        long arrayTime = measure(new ArrayList<>(), middle);
        long linkedTime = measure(new LinkedList<>(), middle);
        System.out.printf("ArrayList: %.2f мс, LinkedList: %.2f мс%n",
                arrayTime / 1000000.0, linkedTime / 1000000.0);
        if (arrayTime < linkedTime) {
            System.out.println("У цьому запуску швидше: ArrayList");
        } else if (arrayTime == linkedTime) {
            System.out.println("Однаковий час");
        } else {
            System.out.println("У цьому запуску швидше: LinkedList");
        }
    }

    public static void main(String[] args) {
        System.out.println("20 000 вставок на початок");
        compare(false);
        System.out.println("20 000 вставок у середину");
        compare(true);
        List<Integer> numbers = new ArrayList<>(List.of(45, 12, 89, 3, 22, 67));
        System.out.println("До сортування: " + numbers);
        Collections.sort(numbers);
        System.out.println("Після сортування: " + numbers);
        List<Integer> part = numbers.subList(1, 4);
        System.out.println("Підсписок: " + part);
        part.set(0, 999);
        System.out.println("Після зміни через підсписок: " + numbers);
    }
}
