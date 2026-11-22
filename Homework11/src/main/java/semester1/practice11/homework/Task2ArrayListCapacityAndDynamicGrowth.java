package semester1.practice11.homework;

class CustomArrayCapacityEmulator<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elementData = new Object[DEFAULT_CAPACITY];
    private int size;
    private int resizeCount;
    private int totalElementsCopied;

    private void ensureCapacityInternal() {
        if (size == elementData.length) {
            int old = elementData.length;
            int capacity = old + (old >> 1); // Додаємо половину попередньої ємності.
            if (capacity <= size) {
                capacity = size + 1;
            }
            Object[] buffer = new Object[capacity];
            System.arraycopy(elementData, 0, buffer, 0, size);
            elementData = buffer;
            totalElementsCopied += size;
            resizeCount++;
        }
    }

    public void add(E element) {
        ensureCapacityInternal();
        elementData[size] = element;
        size++;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index);
        }
        ensureCapacityInternal();
        int moved = size - index;
        System.arraycopy(elementData, index, elementData, index + 1, moved);
        totalElementsCopied += moved;
        elementData[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index);
        }
        return (E) elementData[index];
    }

    public E set(int index, E element) {
        E old = get(index);
        elementData[index] = element;
        return old;
    }

    public E remove(int index) {
        E old = get(index);
        int moved = size - index - 1;
        System.arraycopy(elementData, index + 1, elementData, index, moved);
        totalElementsCopied += moved;
        size--;
        elementData[size] = null;
        return old;
    }

    public void trimToSize() {
        if (size < elementData.length) {
            Object[] buffer = new Object[size];
            System.arraycopy(elementData, 0, buffer, 0, size);
            elementData = buffer;
            totalElementsCopied += size;
        }
    }

    public int size() { return size; }
    public int getCapacity() { return elementData.length; }
    public int getResizeCount() { return resizeCount; }
    public int getTotalElementsCopied() { return totalElementsCopied; }
}

public class Task2ArrayListCapacityAndDynamicGrowth {
    public static void main(String[] args) {
        // var list = new ArrayList<>(); виведе тип ArrayList<Object>.
        var emulator = new CustomArrayCapacityEmulator<String>();
        for (int i = 1; i <= 35; i++) {
            int before = emulator.getCapacity();
            emulator.add("Block-" + i);
            if (before != emulator.getCapacity()) {
                System.out.printf("Елемент %d: ємність %d -> %d%n", i, before, emulator.getCapacity());
            }
        }
        emulator.add(0, "HEADER");
        System.out.println("Видалено: " + emulator.remove(5));
        System.out.println("Перший: " + emulator.get(0) + ", останній: " + emulator.get(emulator.size() - 1));
        System.out.printf("Розмір %d, ємність %d, розширень %d, скопійовано зі зсувами %d%n",
                emulator.size(), emulator.getCapacity(), emulator.getResizeCount(), emulator.getTotalElementsCopied());
        emulator.trimToSize();
        System.out.println("Після стиснення: ємність = " + emulator.getCapacity());
    }
}
