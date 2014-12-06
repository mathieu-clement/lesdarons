package algogen;

import matlabcontrol.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TrainBallFeedForwardNeuralNetworkMain {
    public static void main(String[] args) {
        // NB: For now with one neuron inside the hidden layer
        // NB: Except for data set 4, there is one output

        // Parameters you can change
        int nbIterMax = 50000;
        double goalSuccessRatio = 0.995;
        double eta = 0.01; // learning rate
        int nbHiddenNeurons = 9; // >= nbInputs
        int nbOutputNeurons = 1;
        int goalValueMax = 1;
        int goalValueMin = 0;
        double goalValueThreshold = (goalValueMax - goalValueMin) / 2.;
        String WORKING_DIR = "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\NN_Clement";

        // Program
        BallDataFile ballDataFiles[] = new BallDataFile[4];

        MatlabProxy matlabProxy = null;

        try {
            // ZERO data (or ONE data)
            ballDataFiles[0] = BallDataFile.openForReading(
                    args.length >= 1 ? args[0] :
                            WORKING_DIR + "\\ballZEROdata.txt");
            // ONE data (or ZERO data)
            ballDataFiles[1] = BallDataFile.openForReading(
                    args.length >= 2 ? args[1] :
                            WORKING_DIR + "\\ballONEdata.txt");
            // balltestdata.txt
            ballDataFiles[2] = BallDataFile.openForReading(
                    args.length >= 3 ? args[2] :
                            WORKING_DIR + "\\balltestdata.txt");
            // balleval.txt
            ballDataFiles[3] = BallDataFile.openForWriting(
                    args.length >= 4 ? args[3] :
                            WORKING_DIR + "\\balleval.txt");


            double[][][] lines = new double[2][][];
            lines[0] = ballDataFiles[0].readAsArray();
            lines[1] = ballDataFiles[1].readAsArray();
            int nbInputs = lines[0][0].length;

            int totalPredictions = lines[0].length + lines[1].length;

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
                            outputNeuronsOutputs[s] = outputNeurons[s].calculateOutput(AlgoGenUtils.addColumn1(hiddenNeuronsOutputs));
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
                                double zn = outputNeuronsOutputs[n];
                                newWeight += eta * outputNeuronsErrors[n] * (zn * (1 - zn)) * hiddenNeuronsOutputsWithY0[j];
                                outputNeurons[n].setWeight(j, newWeight);
                            }
                        }

                        // vim
                        for (int m = 0; m < outputNeurons.length; m++) {
                            for (int i = 0; i < inputs.length; i++) {
                                double oldWeight = hiddenNeurons[m].getWeightsNoModifyPlease()[i];
                                double newWeight = oldWeight;
                                double ym = hiddenNeuronsOutputsWithY0[m];
                                newWeight += eta * hiddenNeuronsErrors[m] * (ym * (1 - ym)) * inputs[i];
                                hiddenNeurons[m].setWeight(i, newWeight);
                            }
                        }

                        // TODO Change for multiple output neurons

                        double actualOutput = outputNeurons[0].calculateOutput(hiddenNeuronsOutputsWithY0);

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
                    if (nbIter % 1000 == 0)
                        System.out.printf("%5d %.5f%n", nbIter, successRatio);
                }
            }

            // Print weights of network
            System.out.println("Hidden layer:");
            printLayerWeights(hiddenNeurons);

            System.out.println("Output layer:");
            printLayerWeights(outputNeurons);

            /*****************************
             *        EVALUATION         *
             *****************************/
            BallDataFile testDataFile = ballDataFiles[2];

            BallDataFile ballEvalDataFile = ballDataFiles[3];

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
                for (int i = 0; i < outputNeurons.length; i++) {
                    double neuronOutput = outputNeurons[i].calculateOutput(AlgoGenUtils.addColumn1(hiddenLayerOutputs));
                    ballEvalDataFile.writeValue(neuronOutput);
                }

                ballEvalDataFile.writeNewLine();

                if (p % 10000 == 0)
                    System.out.printf("\r%8d / %8d patterns evaluated", p + 1, evaluationPatterns.length);
            }

            System.out.printf("\r%8d / %8d patterns evaluated%n", evaluationPatterns.length, evaluationPatterns.length);
            System.out.println();

            ballEvalDataFile.close();

            // MATLAB

            System.out.println("Starting Matlab...");

            MatlabProxyFactoryOptions.Builder factoryOptionsBuilder = new MatlabProxyFactoryOptions.Builder();
            factoryOptionsBuilder.setUsePreviouslyControlledSession(true);
            factoryOptionsBuilder.setMatlabStartingDirectory(new File(WORKING_DIR));
            MatlabProxyFactoryOptions matlabProxyFactoryOptions = factoryOptionsBuilder.build();

            MatlabProxyFactory matlabProxyFactory = new MatlabProxyFactory(matlabProxyFactoryOptions);
            matlabProxy = matlabProxyFactory.getProxy();

            System.out.println("Make sure Matlab is started in the correct directory and press [Enter]");
            System.in.read();

            matlabProxy.eval("NeuralNetworkCheck");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MatlabInvocationException e) {
            System.err.println("MATLAB error: " + e.getCause());
        } catch (MatlabConnectionException e) {
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
            if (matlabProxy != null) {
                matlabProxy.disconnect();
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
