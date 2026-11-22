package semester1.practice10.homework;

public final class ImmutableReferenceSquare extends AbstractShape2D {
    private final double side;

    public ImmutableReferenceSquare(String name, double x, double y, double side) {
        super(name, x, y);
        this.side = positive(side);
    }

    public double getArea() { return side * side; }
    public double getPerimeter() { return 4 * side; }
}
