package algogen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class TrainBallSingleNeuronMain {
    public static void main(String[] args) {
        int goalValueMax = 1;
        int goalValueMin = 0;
        double goalValueThreshold = (goalValueMax - goalValueMin) / 2.;
        double eta = 0.02; // choose freely
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

            Neuron neuron = new Neuron(nbInputs);
            double successRatio = 0;
            int nbIter = 0;

            while (successRatio < goalSuccessRatio && nbIter++ < 1000) {
                int nbGoodPredictions = 0;

                for (int goalValue = 0; goalValue < lines.length; goalValue++) {
                    for (double[] pattern : lines[goalValue]) {
                        pattern = AlgoGenUtils.addColumn1(pattern);
                        double y = neuron.update(pattern, goalValue, eta);
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

            System.out.println(Arrays.toString(neuron.getWeights()));
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
