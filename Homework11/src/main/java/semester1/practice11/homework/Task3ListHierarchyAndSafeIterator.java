package semester1.practice11.homework;

import java.util.*;

public class Task3ListHierarchyAndSafeIterator {
    public static void demonstrateFailFastCrash(List<String> logs) {
        try {
            for (String log : logs) {
                if (log != null && log.contains("CORRUPTED")) {
                    logs.remove(log);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Список змінився поза ітератором: modCount != expectedModCount.");
        }
    }

    public static int purgeLogsByKeywordSafely(List<String> logs, String keyword) {
        Iterator<String> it = logs.iterator();
        int removed = 0;
        while (it.hasNext()) {
            String log = it.next();
            if (log == null || log.isBlank() || log.contains(keyword)) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    public static void enrichCriticalLogsWithListIterator(List<String> logs) {
        ListIterator<String> it = logs.listIterator();
        while (it.hasNext()) {
            String line = it.next();
            line = line.trim();
            it.set(line);
            if (line.startsWith("ERROR:")) {
                it.add("[ALERT-TRIGGERED]");
            }
        }
    }

    public static List<String> collectReverseOrder(List<String> logs) {
        List<String> result = new ArrayList<>();
        ListIterator<String> it = logs.listIterator(logs.size());
        while (it.hasPrevious()) {
            result.add(it.previous());
        }
        return result;
    }

    public static void main(String[] args) {
        List<String> raw = Arrays.asList("INFO: Auth OK", "CORRUPTED_PACKET_01", "  ERROR: DB Timeout  ",
                "DEBUG: Ping", "CORRUPTED_PACKET_02", null, "ERROR: Disk Full", "INFO: Logout");
        System.out.println("Початкові записи: " + raw);
        demonstrateFailFastCrash(new ArrayList<>(raw));
        List<String> arrayLogs = new ArrayList<>(raw);
        List<String> linkedLogs = new LinkedList<>(raw);
        processLogs(arrayLogs, "ArrayList");
        processLogs(linkedLogs, "LinkedList");
        System.out.println("Результати однакові: " + arrayLogs.equals(linkedLogs));
    }

    static void processLogs(List<String> logs, String name) {
        System.out.println(name);
        int removed = purgeLogsByKeywordSafely(logs, "CORRUPTED");
        System.out.println("Видалено: " + removed);
        enrichCriticalLogsWithListIterator(logs);
        System.out.println(logs);
        System.out.println("У зворотному порядку: " + collectReverseOrder(logs));
    }
}