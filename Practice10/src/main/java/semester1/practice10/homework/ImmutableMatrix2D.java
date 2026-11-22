package semester1.practice10.homework;

import java.util.Arrays;

public final class ImmutableMatrix2D {
    private final double[][] data;
    private final int rows, cols;

    public ImmutableMatrix2D(double[][] source) {
        if (source == null || source.length == 0 || source[0] == null || source[0].length == 0)
            throw new IllegalArgumentException("Порожня матриця");
        rows = source.length;
        cols = source[0].length;
        data = new double[rows][];
        for (int i = 0; i < rows; i++) {
            if (source[i] == null || source[i].length != cols) throw new IllegalArgumentException("Різна довжина рядків");
            data[i] = source[i].clone();
        }
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
    public double get(int r, int c) { return data[r][c]; }
    public double[][] getDataCopy() {
        return Arrays.stream(data).map(double[]::clone).toArray(double[][]::new);
    }

    public double getTrace() {
        if (rows != cols) throw new IllegalStateException("Слід визначено для квадратної матриці");
        double trace = 0;
        for (int i = 0; i < rows; i++) trace += data[i][i];
        return trace;
    }

    public double getFrobeniusNorm() {
        double norm = 0;
        for (double[] row : data) for (double value : row) norm = Math.hypot(norm, value);
        return norm;
    }

    public double getDeterminant() {
        if (rows == 2 && cols == 2) return get(0,0) * get(1,1) - get(0,1) * get(1,0);
        if (rows != 3 || cols != 3) throw new IllegalStateException("Потрібна матриця 2×2 або 3×3");
        double det = 0;
        for (int j = 0; j < 3; j++) {
            int a = (j + 1) % 3, b = (j + 2) % 3;
            det += get(0,j) * (get(1,a) * get(2,b) - get(1,b) * get(2,a));
        }
        return det;
    }

    public double maxAbsDifference(ImmutableMatrix2D other) {
        if (rows != other.rows || cols != other.cols) throw new IllegalArgumentException("Різні розміри");
        double diff = 0;
        for (int i = 0; i < rows; i++) for (int j = 0; j < cols; j++)
            diff = Math.max(diff, Math.abs(get(i,j) - other.get(i,j)));
        return diff;
    }

    public String toString() { return Arrays.deepToString(data); }
}
