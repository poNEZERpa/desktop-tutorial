package semester1.practice10.homework;

interface Scalable2D {
    double MIN_SCALE_FACTOR = 0.1;
    void scale(double factor);
}

interface Rotatable2D {
    void rotate(double deltaDegrees);
    double getRotationAngle();
}

abstract class AbstractShape2D {
    private final String name;
    private final double centerX;
    private final double centerY;

    AbstractShape2D(String name, double x, double y) {
        this.name = name;
        centerX = x;
        centerY = y;
    }

    static double positive(double size) {
        if (!(size > 0) || !Double.isFinite(size)) throw new IllegalArgumentException("Некоректний розмір");
        return size;
    }

    public abstract double getArea();
    public abstract double getPerimeter();

    public final double getIsoperimetricQuotient() {
        return 4 * Math.PI * getArea() / Math.pow(getPerimeter(), 2);
    }

    public String toString() {
        return "%s (%.1f, %.1f)  S=%.2f  P=%.2f  Q=%.4f".formatted(
                name, centerX, centerY, getArea(), getPerimeter(), getIsoperimetricQuotient());
    }
}

class CircleShape extends AbstractShape2D implements Scalable2D {
    private double radius;

    CircleShape(String name, double x, double y, double radius) {
        super(name, x, y);
        this.radius = positive(radius);
    }

    public double getArea() { return Math.PI * radius * radius; }
    public double getPerimeter() { return 2 * Math.PI * radius; }
    public void scale(double factor) { radius *= Math.max(MIN_SCALE_FACTOR, factor); }
}

class RectangleShape extends AbstractShape2D implements Scalable2D, Rotatable2D {
    private double width;
    private double height;
    private double rotationAngle;

    RectangleShape(String name, double x, double y, double width, double height) {
        super(name, x, y);
        this.width = positive(width);
        this.height = positive(height);
    }

    public double getArea() { return width * height; }
    public double getPerimeter() { return 2 * (width + height); }
    public double getRotationAngle() { return rotationAngle; }
    public void rotate(double degrees) { rotationAngle = ((rotationAngle + degrees) % 360 + 360) % 360; }

    public void scale(double factor) {
        width *= Math.max(MIN_SCALE_FACTOR, factor);
        height *= Math.max(MIN_SCALE_FACTOR, factor);
    }
}

public class Task1PolymorphicShapesAndTransforms {
    public static double calculateTotalArea(AbstractShape2D[] shapes) {
        double total = 0;
        for (AbstractShape2D shape : shapes) total += shape.getArea();
        return total;
    }

    public static void transformScene(AbstractShape2D[] shapes, double scaleFactor, double rotateDeg) {
        for (AbstractShape2D shape : shapes) {
            if (shape instanceof Scalable2D s) s.scale(scaleFactor);
            if (shape instanceof Rotatable2D r) r.rotate(rotateDeg);
        }
    }

    private static void printScene(AbstractShape2D[] shapes) {
        for (AbstractShape2D shape : shapes) {
            System.out.print(shape);
            if (shape instanceof Rotatable2D r) System.out.printf("  Кут=%.1f°", r.getRotationAngle());
            System.out.println();
        }
        System.out.printf("Сумарна площа: %.2f%n", calculateTotalArea(shapes));
    }

    public static void main(String[] args) {
        AbstractShape2D[] shapes = {new CircleShape("Коло", 0, 0, 5),
                new RectangleShape("Прямокутник", 2, 3, 4, 6),
                new ImmutableReferenceSquare("Еталонний квадрат", 10, 10, 5)};
        System.out.println("До перетворення");
        printScene(shapes);
        transformScene(shapes, 2, 45);
        System.out.println("Після масштабування ×2 та повороту на 45°");
        printScene(shapes);
    }
}
