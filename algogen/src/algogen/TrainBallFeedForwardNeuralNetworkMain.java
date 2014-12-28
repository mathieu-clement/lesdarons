package algogen;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TrainBallFeedForwardNeuralNetworkMain {
    public static void main(String[] args) {
        // NB: Except for data set 4, there is one output

        // Parameters you can change
        int nbInputs = 3;
        int nbOutputNeurons = 1;
        int maxEpochs = 300000;
        int epochsBetweenReports = 1000;
        double goalSuccessRatio = 0.996;
        double eta = 0.01; // learning rate
        int nbHiddenNeurons = 9; // >= nbInputs
        int goalValueMax = 1; // is ignored when calculating good predictions ratio
        int goalValueMin = 0; // If you set this to -1 you need to use Sigmoid Symmetric...
        double goalValueThreshold = (goalValueMax - goalValueMin) / 2.;
        String WORKING_DIR = "/home/mathieu/ownCloud/EIA-FR/cours/3eme/AlgoGen/NN_Clement/";

        // Program
        BallDataFile ballDataFiles[] = new BallDataFile[3];

        try {
            // Training data, same format than the one used by popular AAN libraries (PHP, FANN, etc.)
            ballDataFiles[0] = BallDataFile.openForReading(
                    args.length >= 1 ? args[0] :
                            WORKING_DIR + "/fann_training.dat");
            // balltestdata.txt
            ballDataFiles[1] = BallDataFile.openForReading(
                    args.length >= 2 ? args[1] :
                            WORKING_DIR + "/balltestdata.txt");
            // balleval.txt
            ballDataFiles[2] = BallDataFile.openForWriting(
                    args.length >= 3 ? args[2] :
                            WORKING_DIR + "/balleval.txt");

            double[][][] dta = ballDataFiles[0].read();

            double[][] allInputs = dta[0];
            int totalPredictions = allInputs.length * nbOutputNeurons;
            double[][] allOutputs = dta[1];

            Neuron outputNeurons[] = new Neuron[nbOutputNeurons];
            Neuron hiddenNeurons[] = new Neuron[nbHiddenNeurons];
            for (int i = 0; i < hiddenNeurons.length; i++) {
                hiddenNeurons[i] = new Neuron(nbInputs);
            }
            for (int i = 0; i < outputNeurons.length; i++) {
                outputNeurons[i] = new Neuron(hiddenNeurons.length);
            }

            /*****************************
             *         TRAINING          *
             *****************************/

            double hiddenNeuronsOutputs[] = new double[hiddenNeurons.length];
            double hiddenNeuronsErrors[] = new double[hiddenNeurons.length];
            double outputNeuronsOutputs[] = new double[outputNeurons.length];
            double outputNeuronsErrors[] = new double[outputNeurons.length];

            double successRatio = 0;
            int nbIter = 0;

            iter_loop:
            while (nbIter++ < maxEpochs) {

                int nbGoodPredictions = 0;

                for (int ai = 0; ai < allInputs.length; ai++) {
                    double[] pattern = allInputs[ai];

                    double inputsLine[] = AlgoGenUtils.addColumn1(pattern);
                    double[] outputsLine = allOutputs[ai];

                    // -------------------------------------
                    // Forward-Propagation of input
                    // -------------------------------------

                    // yh
                    for (int h = 0; h < hiddenNeurons.length; h++) {
                        hiddenNeuronsOutputs[h] = hiddenNeurons[h].calculateOutput(inputsLine);
                    }

                    // zs
                    for (int s = 0; s < outputNeurons.length; s++) {
                        outputNeuronsOutputs[s] = outputNeurons[s].calculateOutput(AlgoGenUtils.addColumn1(hiddenNeuronsOutputs));
                    }

                    // -------------------------------------
                    // Back-Propagation of error
                    // -------------------------------------

                    // es
                    for (int s = 0; s < outputNeurons.length; s++) {
                        outputNeuronsErrors[s] = outputsLine[s] - outputNeuronsOutputs[s];
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

                    // x0 = 1 already done (inputsLine is [1 x1 x2 x3...])

                    // wjn
                    for (int n = 0; n < outputNeurons.length; n++) {
                        for (int j = 0; j < inputsLine.length; j++) {
                            double oldWeight = outputNeurons[n].getWeightsNoModifyPlease()[j];
                            double newWeight = oldWeight;
                            double zn = outputNeuronsOutputs[n];
                            newWeight += eta * outputNeuronsErrors[n] * (zn * (1 - zn)) * hiddenNeuronsOutputsWithY0[j];
                            outputNeurons[n].setWeight(j, newWeight);
                        }
                    }

                    // vim
                    for (int m = 0; m < outputNeurons.length; m++) {
                        for (int i = 0; i < inputsLine.length; i++) {
                            double oldWeight = hiddenNeurons[m].getWeightsNoModifyPlease()[i];
                            double newWeight = oldWeight;
                            double ym = hiddenNeuronsOutputsWithY0[m];
                            newWeight += eta * hiddenNeuronsErrors[m] * (ym * (1 - ym)) * inputsLine[i];
                            hiddenNeurons[m].setWeight(i, newWeight);
                        }
                    }


                    for (int o = 0; o < outputNeurons.length; o++) {
                        double actualOutput = outputNeurons[o].calculateOutput(hiddenNeuronsOutputsWithY0);
                        double goalValue = outputsLine[o];

                        // TODO Change for outputs other than 0 or 1
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
                    } // output neurons
                } // inputs
                if (nbIter % epochsBetweenReports == 0)
                    System.out.printf("%5d %.5f%n", nbIter, successRatio);
            } // iter

            // Print weights of network
            System.out.println("Hidden layer:");
            printLayerWeights(hiddenNeurons);

            System.out.println("Output layer:");
            printLayerWeights(outputNeurons);

            /*****************************
             *        EVALUATION         *
             *****************************/
            if (!System.getProperty("evaluation_enabled", "true").equals("true")) return;

            BallDataFile testDataFile = ballDataFiles[1];

            BallDataFile ballEvalDataFile = ballDataFiles[2];

            System.out.print("Reading test data file...  ");
            double[][] evaluationPatterns = testDataFile.readAsArray();
            System.out.println("OK");
            assert evaluationPatterns[0].length == nbInputs; // different nb of inputs for training and evaluation

            for (int p = 0; p < evaluationPatterns.length; p++) {
                // x0 = 1
                double inputs[] = AlgoGenUtils.addColumn1(evaluationPatterns[p]);

                // Outputs of first layers

                double[] hiddenLayerOutputs = new double[hiddenNeurons.length];
                for (int i = 0; i < hiddenNeurons.length; i++) {
                    hiddenLayerOutputs[i] = hiddenNeurons[i].calculateOutput(inputs);
                }

                // Feed to output neurons
                for (Neuron outputNeuron : outputNeurons) {
                    double neuronOutput = outputNeuron.calculateOutput(AlgoGenUtils.addColumn1(hiddenLayerOutputs));
                    ballEvalDataFile.writeValue(neuronOutput);
                }

                ballEvalDataFile.writeNewLine();

                if (p % 10000 == 0)
                    System.out.printf("\r%8d / %8d patterns evaluated", p + 1, evaluationPatterns.length);
            }

            System.out.printf("\r%8d / %8d patterns evaluated%n", evaluationPatterns.length, evaluationPatterns.length);
            System.out.println();

            ballEvalDataFile.close();

            // Execute octave
            String cmd = "konsole --workdir " + WORKING_DIR + " -e octave NeuralNetworkCheck_octave.m";
            Process child = Runtime.getRuntime().exec(cmd);
            child.waitFor();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (BallDataFile ballDataFile : ballDataFiles) {
                if (ballDataFile != null)
                    try {
                        ballDataFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
