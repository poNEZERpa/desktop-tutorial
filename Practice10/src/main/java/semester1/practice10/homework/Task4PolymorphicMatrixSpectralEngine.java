package semester1.practice10.homework;

interface InvertibleOperator {
    ImmutableMatrix2D applyInverse(ImmutableMatrix2D transformed);
}

interface OrthogonalOperator extends InvertibleOperator {
    default boolean verifyNormInvariance(ImmutableMatrix2D before, ImmutableMatrix2D after) {
        return Math.abs(before.getFrobeniusNorm() - after.getFrobeniusNorm()) < 1e-6;
    }
}

abstract class AbstractMatrixOperator {
    private final String operatorName;
    AbstractMatrixOperator(String name) { operatorName = name; }
    public abstract boolean canApply(ImmutableMatrix2D m);
    protected abstract ImmutableMatrix2D transform(ImmutableMatrix2D m);

    public final ImmutableMatrix2D execute(ImmutableMatrix2D m) {
        if (m == null || !canApply(m)) throw new IllegalArgumentException("Непридатна матриця для " + operatorName);
        ImmutableMatrix2D result = transform(m);
        if (this instanceof OrthogonalOperator ort && !ort.verifyNormInvariance(m, result))
            throw new ArithmeticException("Норма змінилася");
        return result;
    }

    public String toString() { return operatorName; }
}

class NonZeroScaleOperator extends AbstractMatrixOperator implements InvertibleOperator {
    private final double k;
    NonZeroScaleOperator(double k) {
        super("Масштабування ×" + k);
        if (k == 0 || !Double.isFinite(k)) throw new IllegalArgumentException("Некоректний множник");
        this.k = k;
    }

    public boolean canApply(ImmutableMatrix2D m) { return m != null; }
    protected ImmutableMatrix2D transform(ImmutableMatrix2D m) { return scaled(m, k); }
    public ImmutableMatrix2D applyInverse(ImmutableMatrix2D m) { return scaled(m, 1 / k); }

    private ImmutableMatrix2D scaled(ImmutableMatrix2D m, double factor) {
        double[][] result = m.getDataCopy();
        for (double[] row : result) for (int j = 0; j < row.length; j++) row[j] *= factor;
        return new ImmutableMatrix2D(result);
    }
}

class TransposeOperator extends AbstractMatrixOperator implements OrthogonalOperator {
    TransposeOperator() { super("Транспонування"); }
    public boolean canApply(ImmutableMatrix2D m) { return m != null; }

    protected ImmutableMatrix2D transform(ImmutableMatrix2D m) {
        double[][] result = new double[m.cols()][m.rows()];
        for (int i = 0; i < m.rows(); i++) for (int j = 0; j < m.cols(); j++) result[j][i] = m.get(i,j);
        return new ImmutableMatrix2D(result);
    }
    public ImmutableMatrix2D applyInverse(ImmutableMatrix2D m) { return execute(m); }
}

class GivensRotationOperator extends AbstractMatrixOperator implements OrthogonalOperator {
    private final double angle;
    GivensRotationOperator(double degrees) {
        super("Поворот на " + degrees + "°");
        angle = Math.toRadians(degrees);
    }
    public boolean canApply(ImmutableMatrix2D m) { return m != null && m.rows() >= 2; }
    protected ImmutableMatrix2D transform(ImmutableMatrix2D m) { return rotated(m, angle); }
    public ImmutableMatrix2D applyInverse(ImmutableMatrix2D m) { return rotated(m, -angle); }

    private ImmutableMatrix2D rotated(ImmutableMatrix2D m, double radians) {
        if (!canApply(m)) throw new IllegalArgumentException("Для повороту потрібно два рядки");
        double[][] result = m.getDataCopy();
        double c = Math.cos(radians), s = Math.sin(radians);
        for (int j = 0; j < m.cols(); j++) {
            result[0][j] = c * m.get(0,j) - s * m.get(1,j);
            result[1][j] = s * m.get(0,j) + c * m.get(1,j);
        }
        return new ImmutableMatrix2D(result);
    }
}

class UpperTriangularProjection extends AbstractMatrixOperator {
    UpperTriangularProjection() { super("Верхньотрикутна проєкція"); }
    public boolean canApply(ImmutableMatrix2D m) { return m != null; }
    protected ImmutableMatrix2D transform(ImmutableMatrix2D m) {
        double[][] result = m.getDataCopy();
        for (int i = 1; i < m.rows(); i++) java.util.Arrays.fill(result[i], 0, Math.min(i, m.cols()), 0);
        return new ImmutableMatrix2D(result);
    }
}

public class Task4PolymorphicMatrixSpectralEngine {
    public static void main(String[] args) {
        ImmutableMatrix2D start = new ImmutableMatrix2D(new double[][]{{3, 4}, {0, 5}});
        ImmutableMatrix2D matrix = start;
        System.out.printf("Початкова матриця %s, норма %.4f, слід %.2f, det %.2f%n",
                start, start.getFrobeniusNorm(), start.getTrace(), start.getDeterminant());
        AbstractMatrixOperator[] pipeline = {new NonZeroScaleOperator(2), new TransposeOperator(), new GivensRotationOperator(60)};
        for (AbstractMatrixOperator op : pipeline) {
            matrix = op.execute(matrix);
            System.out.printf("%s: %s, норма %.4f, det %.2f%n", op, matrix, matrix.getFrobeniusNorm(), matrix.getDeterminant());
        }
        System.out.println("Верхньотрикутна проєкція: " + new UpperTriangularProjection().execute(matrix));
        for (int i = pipeline.length - 1; i >= 0; i--) {
            if (pipeline[i] instanceof InvertibleOperator inv) matrix = inv.applyInverse(matrix);
        }
        double error = start.maxAbsDifference(matrix);
        System.out.printf("Відновлена матриця %s, максимальна похибка %.2e%n", matrix, error);
        if (!(error < 1e-14)) throw new ArithmeticException("Перевищено допустиму похибку");
    }
}
