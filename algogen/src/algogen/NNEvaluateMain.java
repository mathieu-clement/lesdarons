package algogen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by mathieu on 12/28/14.
 */
public class NNEvaluateMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        /*

        Example of input file (args[0]):
        ATTENTION! Must not begin with, end with or contain empty lines


        Hidden layer:
        [00] 0.11714924 0.44080162 0.51801877 0.59945055
        [01] 0.78014303 0.13812290 0.74577909 0.56882102
        [02] 0.15033741 0.71288036 0.12134489 0.10743346
        [03] 0.74676270 0.46097370 0.46290975 0.07707969
        [04] 0.05941195 0.87003141 0.04401967 0.14868005
        [05] 0.87275978 0.73478481 0.50278010 0.77648177
        [06] 0.94160387 0.21211354 0.97565418 0.70795224
        [07] 0.29428401 0.83715547 0.46772361 0.66648286
        [08] 0.07602871 0.97988144 0.06210380 0.38015186
        Output layer:
        [00] -21.85436793 4.81850471 -31.22062659 67.86423995 0.53680459 0.31491318 0.11241291 0.87732164 0.18417445 0.86998248
         */

        String networkFilename = args[0];
        // args[1] is some test data
        // args[2] is the output file
        int nbInputs = Integer.parseInt(args[3]);
        int nbHiddenNeurons = Integer.parseInt(args[4]);
        int nbOutputNeurons = Integer.parseInt(args[5]);

        int nbWeightsHidden = nbInputs + 1;
        int nbWeightsOutputs = nbHiddenNeurons + 1;

        BufferedReader netReader = new BufferedReader(new FileReader(networkFilename));

        Neuron[] hiddenNeurons = new Neuron[nbHiddenNeurons];
        Neuron[] outputNeurons = new Neuron[nbOutputNeurons];

        int crtHiddenNeuronIdx = 0;
        int crtOutputNeuronIdx = 0;

        String line;
        boolean hiddenLayerMode = false;
        boolean outputLayerMode = false;
        while ((line = netReader.readLine()) != null) {
            if (line.startsWith("Hidden layer:")) {
                hiddenLayerMode = true;
                continue;
            } else if (line.startsWith("Output layer:")) {
                hiddenLayerMode = false;
                outputLayerMode = true;
                continue;
            }

            StringTokenizer tokenizer = new StringTokenizer(line);
            // Ignore neuron number
            tokenizer.nextToken();

            Neuron neuron = null;
            int nbWeights = 0;

            if (hiddenLayerMode) {
                hiddenNeurons[crtHiddenNeuronIdx] = new Neuron(nbInputs);
                neuron = hiddenNeurons[crtHiddenNeuronIdx++];
                nbWeights = nbWeightsHidden;
            } else if (outputLayerMode) {
                outputNeurons[crtOutputNeuronIdx] = new Neuron(nbHiddenNeurons);
                neuron = outputNeurons[crtOutputNeuronIdx++];
                nbWeights = nbWeightsOutputs;
            }

            for (int i = 0; i < nbWeights; i++) {
                try {
                    neuron.setWeight(i, Double.parseDouble(tokenizer.nextToken()));
                } catch (NoSuchElementException nse) {
                    System.err.println("Trying to find a " + i + "th weight on " +
                            (hiddenLayerMode ?
                                    "hidden neuron " + crtHiddenNeuronIdx :
                                    "output neuron " + crtOutputNeuronIdx));
                    throw nse;
                }
            }
        }


        String WORKING_DIR = "/home/mathieu/ownCloud/EIA-FR/cours/3eme/AlgoGen/NN_Clement/";
        BallDataFile ballDataFiles[] = new BallDataFile[3];

        ballDataFiles[0] = null;
        // balltestdata.txt
        ballDataFiles[1] = BallDataFile.openForReading(
                args.length >= 1 ? args[1] :
                        WORKING_DIR + "/balltestdata.txt");
        // balleval.txt
        ballDataFiles[2] = BallDataFile.openForWriting(
                args.length >= 2 ? args[2] :
                WORKING_DIR + "/balleval.txt");


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
    }
}
