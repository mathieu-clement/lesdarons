package algogen;

import java.util.Arrays;

// Single neuron
public class Neuron {
    private double weights[];

    public Neuron(int nbInputs) {
        weights = new double[nbInputs + 1];
        // Optionally assign random weights
        for (int i = 0; i < weights.length; i++) {
            weights[i] =  Math.random();
        }
    }

    // Returns y
    public double update(double inputs[], double t, double eta) {
        double y = calculateOutput(inputs);
        double error = t - y;
        for (int j = 0; j < inputs.length; j++) {
            weights[j] += eta * error * inputs[j];
        }

        return y;
    }

    // Returns y
    public double calculateOutput(double inputs[]) {
        assert this.weights.length == inputs.length;
        assert inputs[0] == 1;

        double y = AlgoGenUtils.sigmoid(AlgoGenUtils.dotProduct(this.weights, inputs));
        return y;
    }

    // Returns a copy
    public double[] getWeightsCopy() {
        return Arrays.copyOf(weights, weights.length);
    }

    // Avoids to create a copy because we trust the user of the class
    public double[] getWeightsNoModifyPlease() {
        return weights;
    }

    public void setWeight(int weightIndex, double value) {
        weights[weightIndex] = value;
    }

    @Override
    public String toString() {
        return "Neuron{" + Arrays.toString(weights) + '}';
    }
}
