package algogen;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TrainBallFeedForwardNeuralNetworkMain {
    public static void main(String[] args) {
        // NB: For now with one neuron inside the hidden layer
        // NB: Except for data set 4, there is one output

        int goalValueMax = 1;
        int goalValueMin = 0;
        double goalValueThreshold = (goalValueMax - goalValueMin) / 2.;
        double eta = 0.02; // learning rate
        double goalSuccessRatio = 0.998;

        BallDataFile ballDataFiles[] = new BallDataFile[2];

        try {
            ballDataFiles[0] = BallDataFile.open(
                    args.length >= 1 ? args[0] :
                    "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\docs_riedi\\" +
                            "neural_networks\\" +
                            "ballZEROdata.txt");
            ballDataFiles[1] = BallDataFile.open(
                    args.length >= 2 ? args[1] :
                    "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\docs_riedi\\" +
                            "neural_networks\\" +
                            "ballONEdata.txt");

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

            double outputNeuronInputs[] = new double[hiddenNeurons.length];

            double successRatio = 0;
            int nbIter = 0;

            while (successRatio < goalSuccessRatio && nbIter++ < 1000) {
                int nbGoodPredictions = 0;

                for (int goalValue = 0; goalValue < lines.length; goalValue++) {
                    for (double[] pattern : lines[goalValue]) {
                        pattern = AlgoGenUtils.addColumn1(pattern);

                        double y = hiddenNeurons[0].update(pattern, goalValue, eta);

                        for (int i = 0; i < outputNeuronInputs.length; i++) {
                            outputNeuronInputs[i] = hiddenNeurons[i].update(pattern, goalValue, eta);
                        }

                        double z = outputNeurons[0].update(outputNeuronInputs, goalValue, eta);


                        if (goalValue == 0) {
                            if (y < 0.5) {
                                nbGoodPredictions++;
                                successRatio = (double) nbGoodPredictions / totalPredictions;
                            }
                        } else if (goalValue == 1) {
                            if (y > 0.5 && y <= 1.0) {
                                nbGoodPredictions++;
                                successRatio = (double) nbGoodPredictions / totalPredictions;
                            }
                        }
                    }
                    System.out.printf("%5d %.5f%n", nbIter, successRatio);
                }
            }

            // TODO
            // Print weights of network
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
}
