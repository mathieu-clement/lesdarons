package algogen;

import java.util.Arrays;

// Single neuron
public class Neuron {
    private double weights[];

    public Neuron(int nbInputs) {
        weights = new double[nbInputs + 1];
        /*
        for (int i = 0; i < weights.length; i++) {
            weights[i] =  Math.random();
        }
        */
    }

    // Returns y
    // inputs doesn't have some kind of x0
    public double update(double inputs[], double t, double eta) {
        assert this.weights.length == inputs.length;
        assert inputs[0] == 1;

        double y = AlgoGenUtils.sigmoid(AlgoGenUtils.dotProduct(this.weights, inputs));
        double error = t - y;
        for (int j = 0; j < inputs.length; j++) {
            weights[j] += eta * error * inputs[j];
        }

        return y;
    }

    public double[] getWeights() {
        return weights;
    }

    @Override
    public String toString() {
        return "Neuron{" + Arrays.toString(weights) + '}';
    }
}
