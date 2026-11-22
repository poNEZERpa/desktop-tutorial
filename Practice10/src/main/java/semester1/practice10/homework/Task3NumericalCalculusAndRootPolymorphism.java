package semester1.practice10.homework;

interface Evaluable1D {
    double EPSILON = 1e-7;
    double evaluate(double x);
}

interface Integrable1D extends Evaluable1D {
    default double integrateSimpson(double a, double b, int n) {
        if (n < 2) n = 100;
        if (n % 2 != 0) n++;
        double h = (b - a) / n;
        double sum = evaluate(a) + evaluate(b);
        for (int i = 1; i < n; i++) {
            sum += (i % 2 == 0 ? 2 : 4) * evaluate(a + i * h);
        }
        return sum * h / 3;
    }
}

interface Differentiable1D extends Evaluable1D {
    double derivative(double x);

    default double numericalDerivative(double x) {
        double h = 1e-5;
        return (evaluate(x + h) - evaluate(x - h)) / (2 * h);
    }
}

interface NewtonRootFindable extends Differentiable1D {
    default double findRootNewton(double x0, int maxIter) {
        double x = x0;
        for (int i = 0; i < maxIter; i++) {
            double value = evaluate(x);
            if (Math.abs(value) <= EPSILON) return x;
            double slope = derivative(x);
            if (Math.abs(slope) < 1e-12) throw new ArithmeticException("Нульова похідна");
            x -= value / slope;
        }
        if (Math.abs(evaluate(x)) <= EPSILON) return x;
        throw new ArithmeticException("Метод Ньютона не збігся");
    }
}

abstract class AbstractMathFunction implements Integrable1D {
    private final String formulaTitle;
    AbstractMathFunction(String title) { formulaTitle = title; }
    public final String getFormulaTitle() { return formulaTitle; }
}

class DampedHarmonyFunction extends AbstractMathFunction implements NewtonRootFindable {
    private final double amplitude, gamma, omega;

    DampedHarmonyFunction(double amplitude, double gamma, double omega) {
        super("D(x) = %.2f exp(-%.2f x) cos(%.2f x)".formatted(amplitude, gamma, omega));
        this.amplitude = amplitude;
        this.gamma = gamma;
        this.omega = omega;
    }

    public double evaluate(double x) {
        return amplitude * Math.exp(-gamma * x) * Math.cos(omega * x);
    }

    public double derivative(double x) {
        return -amplitude * Math.exp(-gamma * x) * (gamma * Math.cos(omega * x) + omega * Math.sin(omega * x));
    }
}

class StepHeavisideFunction extends AbstractMathFunction {
    StepHeavisideFunction() { super("H(x) = 2 при x < 1.5, інакше 5"); }
    public double evaluate(double x) { return x < 1.5 ? 2 : 5; }
}

public class Task3NumericalCalculusAndRootPolymorphism {
    public static void main(String[] args) {
        AbstractMathFunction[] functions = {
                new PolynomialFunction("P(x) = x³ − 2x − 5", -5, -2, 0, 1),
                new DampedHarmonyFunction(4, 0.5, 2), new StepHeavisideFunction()};
        for (AbstractMathFunction fn : functions) {
            System.out.println(fn.getFormulaTitle());
            int n = fn instanceof StepHeavisideFunction ? 1000 : 100;
            System.out.printf("Інтеграл Сімпсона [0; 3], n=%d: %.6f%n", n, fn.integrateSimpson(0, 3, n));
            if (fn instanceof NewtonRootFindable rf) {
                boolean polynomial = fn instanceof PolynomialFunction;
                double x = polynomial ? 2 : 1;
                double root = rf.findRootNewton(polynomial ? 2 : 0.7, 50);
                System.out.printf("Похідна в %.1f: %.6f (чисельно %.6f)%n", x, rf.derivative(x), rf.numericalDerivative(x));
                System.out.printf("Корінь: %.7f, нев’язка: %.2e%n", root, rf.evaluate(root));
            } else {
                System.out.println("У точці розриву похідної немає. Точний інтеграл: 10.5.");
            }
        }
    }
}
