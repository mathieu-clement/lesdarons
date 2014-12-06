package algogen;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TrainBallFeedForwardNeuralNetworkMain {
    public static void main(String[] args) {
        // NB: For now with one neuron inside the hidden layer
        // NB: Except for data set 4, there is one output

        // Parameters you can change
        int nbIterMax = 20;
        int goalValueMax = 1;
        int goalValueMin = 0;
        double goalValueThreshold = (goalValueMax - goalValueMin) / 2.;
        double eta = 0.02; // learning rate
        double goalSuccessRatio = 0.998;
        String WORKING_DIR = "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\NN_Clement";

        // Program
        BallDataFile ballDataFiles[] = new BallDataFile[2];

        try {
            // ZERO data (or ONE data)
            ballDataFiles[0] = BallDataFile.openForReading(
                    args.length >= 1 ? args[0] :
                            "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\docs_riedi\\" +
                                    "neural_networks\\" +
                                    "ballZEROdata.txt");
            // ONE data (or ZERO data)
            ballDataFiles[1] = BallDataFile.openForReading(
                    args.length >= 2 ? args[1] :
                            WORKING_DIR + "ballONEdata.txt");
            // balltestdata.txt
            ballDataFiles[2] = BallDataFile.openForReading(
                    args.length >= 3 ? args[2] :
                            WORKING_DIR + "balltestdata.txt");


            double[][][] lines = new double[2][][];
            lines[0] = ballDataFiles[0].toArray();
            lines[1] = ballDataFiles[1].toArray();
            int nbInputs = lines[0][0].length;

            int totalPredictions = lines[0].length + lines[1].length;

            Neuron outputNeurons[] = new Neuron[1];
            Neuron hiddenNeurons[] = new Neuron[nbInputs];
            for (int i = 0; i < hiddenNeurons.length; i++) {
                hiddenNeurons[i] = new Neuron(nbInputs);
            }
            for (int i = 0; i < outputNeurons.length; i++) {
                outputNeurons[i] = new Neuron(hiddenNeurons.length);
            }

            double hiddenNeuronsOutputs[] = new double[hiddenNeurons.length+1];
            double hiddenNeuronsErrors[] = new double[hiddenNeurons.length+1];
            double outputNeuronsOutputs[] = new double[outputNeurons.length+1];
            double outputNeuronsErrors[] = new double[outputNeurons.length+1];

            double successRatio = 0;
            int nbIter = 0;

            while (successRatio < goalSuccessRatio && nbIter++ < nbIterMax) {
                int nbGoodPredictions = 0;

                // TODO Goal values might be different for each neuron
                for (int goalValue = 0; goalValue < lines.length; goalValue++) {
                    for (double[] pattern : lines[goalValue]) {
                        double inputs[] = AlgoGenUtils.addColumn1(pattern);

                        // -------------------------------------
                        // Forward-Propagation of input
                        // -------------------------------------

                        // yh
                        for (int h = 0; h < hiddenNeurons.length; h++) {
                            hiddenNeuronsOutputs[h] = hiddenNeurons[h].calculateOutput(inputs);
                        }

                        // zs
                        for (int s = 0; s < outputNeurons.length; s++) {
                            outputNeuronsOutputs[s] = outputNeurons[s].calculateOutput(hiddenNeuronsOutputs);
                        }

                        // -------------------------------------
                        // Back-Propagation of error
                        // -------------------------------------

                        // es
                        for (int s = 0; s < outputNeurons.length; s++) {
                            outputNeuronsErrors[s] = goalValue - outputNeuronsOutputs[s];
                        }

                        // dh
                        for (int h = 0; h < hiddenNeurons.length; h++) {
                            double sum = 0.0;
                            for (int s = 0; s < outputNeurons.length; s++) {
                                double zs = outputNeuronsOutputs[s];
                                sum += outputNeuronsErrors[s] * (zs * (1 - zs)) *
                                        outputNeurons[s].getWeightsNoModifyPlease()[h];
                            }
                            hiddenNeuronsErrors[h] = sum;
                        }

                        // -------------------------------------
                        // Update all weights
                        // -------------------------------------

                        // y0 = 1
                        double hiddenNeuronsOutputsWithY0[] = AlgoGenUtils.addColumn1(hiddenNeuronsOutputs);

                        // x0 = 1 already done (inputs is [1 x1 x2 x3...])

                        // wjn
                        for (int n = 0; n < outputNeurons.length; n++) {
                            for (int j = 0; j < inputs.length; j++) {
                                double oldWeight = outputNeurons[n].getWeightsNoModifyPlease()[j];
                                double newWeight = oldWeight;
                                double zn = hiddenNeuronsOutputs[n];
                                newWeight += eta * outputNeuronsErrors[n] * (zn * (1 - zn)) * hiddenNeuronsOutputs[j];
                                outputNeurons[n].setWeight(j, newWeight);
                            }
                        }

                        // vim
                        for (int m = 0; m < outputNeurons.length; m++) {
                            for (int i = 0; i < inputs.length; i++) {
                                double oldWeight = hiddenNeurons[m].getWeightsNoModifyPlease()[i];
                                double newWeight = oldWeight;
                                double zn = hiddenNeuronsOutputsWithY0[m];
                                newWeight += eta * hiddenNeuronsErrors[m] * (zn * (1 - zn)) * inputs[i];
                                hiddenNeurons[m].setWeight(i, newWeight);
                            }
                        }

                        // TODO Change for multiple output neurons

                        double actualOutput = outputNeurons[0].calculateOutput(inputs);

                        if (goalValue == 0) {
                            if (actualOutput < 0.5) {
                                nbGoodPredictions++;
                                successRatio = (double) nbGoodPredictions / totalPredictions;
                            }
                        } else if (goalValue == 1) {
                            if (actualOutput > 0.5 && actualOutput <= 1.0) {
                                nbGoodPredictions++;
                                successRatio = (double) nbGoodPredictions / totalPredictions;
                            }
                        }
                    }
                    System.out.printf("%5d %.5f%n", nbIter, successRatio);
                }
            }

            // Print weights of network
            System.out.println("Hidden layer:");
            printLayerWeights(hiddenNeurons);

            System.out.println("Output layer:");
            printLayerWeights(outputNeurons);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ballDataFiles[0] != null)
                try {
                    ballDataFiles[0].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (ballDataFiles[1] != null)
                try {
                    ballDataFiles[1].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private static void printLayerWeights(Neuron[] neurons) {
        for (int i = 0; i < neurons.length; i++) {
            Neuron hiddenNeuron = neurons[i];
            System.out.printf("[%02d] ", i);
            for (double w : hiddenNeuron.getWeightsNoModifyPlease()) {
                System.out.printf("%.8f ", w);
            }
            System.out.println();
        }
    }
}
