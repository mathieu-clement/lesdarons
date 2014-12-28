package algogen;

public class AlgoGenUtils {
    // y: Activation function
    public static double sigmoid(double a) {
        return 1. / (1. + 1. / Math.exp(a));
    }

    // sigmoid symmetric: useful for values between -1 and +1
    public static double sigmoidSymmetric(double a) {
        return Math.tanh(a);
    }

    // Dot product, weighted sum
    // a = w0 + w1 * x1 + w2 * x2
    // x0 is always 1
    public static double dotProduct(double weights[], // w
                                    double inputs[]   // x
    ) {
        assert inputs[0] == 1.;
        assert weights.length == inputs.length;

        double sum = 0.;
        for (int i = 0; i < weights.length; i++)
            sum += weights[i] * inputs[i];

        return sum;
    }

    // G(w0, w1, ...)
    public static double objectiveFunction(double objectiveResults[], // tp
                                           double sigmoidResults[] // yp
    ) {
        assert objectiveResults.length == sigmoidResults.length;
        double sum = 0.;
        for (int i = 0; i < objectiveResults.length; i++) {
            double diff = objectiveResults[i] - sigmoidResults[i];
            sum += diff * diff;
        }
        sum /= 2.;
        return sum;
    }

    // gj : partial derivative of G
    public static double derivativeOfObjectiveFunction(double t[], // value to reach
                                                       double y[], // activation fct
                                                       double x[]   // inputs
    ) {
        assert t.length == y.length && y.length == x.length;
        assert x[0] == 1;

        double sum = 0.;
        for (int p = 0; p < t.length; p++) {
            sum += (t[p] - y[p]) * y[p] * (1 - y[p]) * x[p];
        }

        return -sum;
    }

    public static double[] addColumn1(double arr[]) {
        double results[] = new double[arr.length + 1];

        results[0] = 1; // initially w0 = 1

        for (int i = 0; i < arr.length; i++) {
            results[i + 1] = arr[i];
        }

        return results;
    }
}
