package algogen;

import matlabcontrol.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/*
Needs library MatLab JMI wrapper
https://code.google.com/p/matlabcontrol/
*/

public class TrainBallSingleNeuronMain {
    public static void main(String[] args) {
        int goalValueMax = 1;
        int goalValueMin = 0;
        double goalValueThreshold = (goalValueMax - goalValueMin) / 2.;
        double eta = 0.02; // choose freely
        double goalSuccessRatio = 0.998;

        MatlabProxy matlabProxy = null;

        BallDataFile ballDataFiles[] = new BallDataFile[2];

        try {
            ballDataFiles[0] = BallDataFile.openForReading(
                    args.length >= 1 ? args[0] :
                            "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\docs_riedi\\" +
                                    "neural_networks\\" +
                                    "ballZEROdata.txt");
            ballDataFiles[1] = BallDataFile.openForReading(
                    args.length >= 2 ? args[1] :
                            "C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\docs_riedi\\" +
                                    "neural_networks\\" +
                                    "ballONEdata.txt");

            double[][][] lines = new double[2][][];
            lines[0] = ballDataFiles[0].readAsArray();
            lines[1] = ballDataFiles[1].readAsArray();
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
                    //System.out.printf("%5d %.5f%n", nbIter, successRatio);
                }
            }

            String weightsStr = Arrays.toString(neuron.getWeightsNoModifyPlease());
            System.out.println(weightsStr);

            System.out.println("Starting Matlab...");

            MatlabProxyFactoryOptions.Builder factoryOptionsBuilder = new MatlabProxyFactoryOptions.Builder();
            factoryOptionsBuilder.setUsePreviouslyControlledSession(true);
            factoryOptionsBuilder.setMatlabStartingDirectory(new File("C:\\Users\\mathieu\\Dropbox\\LesDarons\\AlgoGen\\SN_Clement"));
            MatlabProxyFactoryOptions matlabProxyFactoryOptions = factoryOptionsBuilder.build();

            MatlabProxyFactory matlabProxyFactory = new MatlabProxyFactory(matlabProxyFactoryOptions);
            matlabProxy = matlabProxyFactory.getProxy();

            System.out.println("Make sure Matlab is started in the correct directory and press [Enter]");
            System.in.read();

            matlabProxy.eval("SingleNeuronCheck(" + weightsStr + ')');

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MatlabConnectionException e) {
            e.printStackTrace();
        } catch (MatlabInvocationException e) {
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
            if (matlabProxy != null) {
                matlabProxy.disconnect();
            }
        }
    }
}
