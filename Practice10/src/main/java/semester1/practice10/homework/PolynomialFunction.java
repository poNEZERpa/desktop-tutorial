package semester1.practice10.homework;

import java.util.Arrays;

public final class PolynomialFunction extends AbstractMathFunction implements NewtonRootFindable {
    private final double[] coeffs;

    public PolynomialFunction(String title, double... coefficients) {
        super(title);
        coeffs = Arrays.copyOf(coefficients, coefficients.length);
    }

    public double evaluate(double x) {
        double value = 0;
        for (int i = coeffs.length - 1; i >= 0; i--) value = value * x + coeffs[i];
        return value;
    }

    public double derivative(double x) {
        double value = 0;
        for (int i = coeffs.length - 1; i > 0; i--) value = value * x + i * coeffs[i];
        return value;
    }
}
