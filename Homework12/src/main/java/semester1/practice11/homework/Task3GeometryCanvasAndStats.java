package semester1.practice11.homework;

import java.util.*;

public class Task3GeometryCanvasAndStats {
    public static void main(String[] args) {
        GeometryCanvas canvas = new GeometryCanvas("Головне полотно");
        canvas.addShape(new Circle("C-1", "Red", 3));
        boolean added = canvas.addShape(new Circle("C-1", "Blue", 5));
        System.out.println("Додано повторний C-1: " + added);
        canvas.addShape(new Rectangle("R-1", "Blue", 4, 5));
        canvas.addShape(new Rectangle("R-2", "Green", 2, 3));
        canvas.addShape(new RightTriangle("T-1", "Red", 3, 4));
        canvas.addShape(new RightTriangle("T-2", "Yellow", 6, 8));
        canvas.printStats();
        System.out.println("Видалено малих фігур: " + canvas.removeSmallShapes(10));
        canvas.printStats();

        HashSet<String> second = new HashSet<>(List.of("Blue", "Yellow", "Purple"));
        HashSet<String> common = canvas.getUniqueColors();
        common.retainAll(second);
        HashSet<String> onlyFirst = canvas.getUniqueColors();
        onlyFirst.removeAll(second);
        HashSet<String> all = canvas.getUniqueColors();
        all.addAll(second);
        System.out.println("Спільні кольори: " + common);
        System.out.println("Тільки на першому полотні: " + onlyFirst);
        System.out.println("Усі кольори: " + all);
    }
}

abstract class Shape {
    private final String id;
    private final String color;

    Shape(String id, String color) {
        this.id = id;
        this.color = color;
    }

    public String getId() { return id; }
    public String getColor() { return color; }
    public abstract double getArea();
    public abstract double getPerimeter();

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Shape)) return false;
        Shape item = (Shape) other;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

class Circle extends Shape {
    private final double radius;

    Circle(String id, String color, double radius) {
        super(id, color);
        if (!(radius > 0)) {
            throw new IllegalArgumentException("Розміри мають бути додатними");
        }
        this.radius = radius;
    }

    public double getArea() {
        return Math.PI * radius * radius;
    }

    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
}

class Rectangle extends Shape {
    private final double width;
    private final double height;

    Rectangle(String id, String color, double width, double height) {
        super(id, color);
        if (!(width > 0) || !(height > 0)) {
            throw new IllegalArgumentException("Розміри мають бути додатними");
        }
        this.width = width;
        this.height = height;
    }

    public double getArea() {
        return width * height;
    }

    public double getPerimeter() {
        return 2 * (width + height);
    }
}

class RightTriangle extends Shape {
    private final double legA;
    private final double legB;

    RightTriangle(String id, String color, double legA, double legB) {
        super(id, color);
        if (!(legA > 0) || !(legB > 0)) {
            throw new IllegalArgumentException("Розміри мають бути додатними");
        }
        this.legA = legA;
        this.legB = legB;
    }

    public double getArea() {
        return legA * legB / 2;
    }

    public double getPerimeter() {
        return legA + legB + Math.hypot(legA, legB);
    }
}

class GeometryCanvas {
    private final String title;
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private final HashSet<String> usedIds = new HashSet<>();

    GeometryCanvas(String title) {
        this.title = title;
    }

    public boolean addShape(Shape shape) {
        if (shape == null || !usedIds.add(shape.getId())) return false;
        shapes.add(shape);
        return true;
    }

    public int removeSmallShapes(double minArea) {
        int removed = 0;
        Iterator<Shape> it = shapes.iterator();
        while (it.hasNext()) {
            Shape shape = it.next();
            if (shape.getArea() < minArea) {
                usedIds.remove(shape.getId());
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    public double getTotalArea() {
        double total = 0;
        for (Shape shape : shapes) total += shape.getArea();
        return total;
    }

    public double getTotalPerimeter() {
        double total = 0;
        for (Shape shape : shapes) total += shape.getPerimeter();
        return total;
    }

    public HashSet<String> getUniqueColors() {
        HashSet<String> colors = new HashSet<>();
        for (Shape shape : shapes) colors.add(shape.getColor());
        return colors;
    }

    public ArrayList<Shape> getShapesCopy() {
        return new ArrayList<>(shapes);
    }

    public void printStats() {
        System.out.printf("%s: %d фігур, площа %.2f, периметр %.2f%n",
                title, shapes.size(), getTotalArea(), getTotalPerimeter());
        System.out.println("Кольори: " + getUniqueColors());
    }
}
