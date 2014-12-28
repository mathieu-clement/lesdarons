package algogen;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class BallDataFile extends File {

    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean constructorCalledFromHere = false;

    /**
     * @param pathname do not use
     * @throws IOException do not worry about that
     * @deprecated Do NOT call this constructor.
     * Use {@link #openForReading(String)} or
     * {@link #openForWriting(String)} instead.
     */
    public BallDataFile(String pathname) throws IOException {
        super(pathname);
    }

    public static BallDataFile openForReading(String filename) throws IOException {
        BallDataFile ballDataFile = new BallDataFile(filename);
        ballDataFile.reader = new BufferedReader(new FileReader(filename));
        ballDataFile.constructorCalledFromHere = true;
        return ballDataFile;
    }

    public static BallDataFile openForWriting(String filename) throws IOException {
        BallDataFile ballDataFile = new BallDataFile(filename);
        ballDataFile.writer = new BufferedWriter(new FileWriter(filename));
        ballDataFile.constructorCalledFromHere = true;
        return ballDataFile;
    }

    public void close() throws IOException {
        checkProperConstructorCalled();
        if (reader != null)
            reader.close();
        if (writer != null)
            writer.close();
    }

    private void checkProperConstructorCalled() throws IllegalAccessError {
        if (!constructorCalledFromHere)
            throw new IllegalAccessError("Call BallDataFile.openForReading()" +
                    " or BallDataFile.openForWriting()" +
                    " instead of the constructor you used.");
    }

    public double[][] readAsArray() throws IOException {
        checkProperConstructorCalled();
        ReadArrayPatternCallback callback = new ReadArrayPatternCallback();
        applyOnEachLine(callback);
        return callback.getResults();
    }

    // Returns [inputs, outputs]
    public double[][][] read() throws IOException {
        checkProperConstructorCalled();
        ReadArrayPatternCallback inputsCallback = new ReadArrayPatternCallback();
        ReadArrayPatternCallback outputsCallback = new ReadArrayPatternCallback();

        // Find out number of inputs and outputs
        // Must be the second and third number of the first line of the file
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        tokenizer.nextToken(); // ignore number of samples
        int nbInputs = Integer.parseInt(tokenizer.nextToken());
        int nbOutputs = Integer.parseInt(tokenizer.nextToken());

        applyOnEachLineFann(nbInputs, nbOutputs,
                inputsCallback, outputsCallback);

        return new double[][][]{inputsCallback.getResults(), outputsCallback.getResults()};
    }

    private static class ReadArrayPatternCallback implements PatternCallback {

        private double results[][] = new double[2][];
        private int crtIndex = 0;

        public double[][] getResults() {
            return Arrays.copyOfRange(results, 0, crtIndex);
        }

        @Override
        public void onEachPattern(double[] inputs) {
            if (crtIndex == results.length) {
                results = Arrays.copyOf(results, results.length * 2);
            }
            results[crtIndex++] = inputs;
        }
    }

    // Read all lines, parse them as double and call "callback" on each line
    // Is memory-friendly
    // It supposes it starts on an input line (see FANN or PHP AAN format)
    public void applyOnEachLineFann(int nbInputs, int nbOutputs,
                                    PatternCallback inputsCallback, PatternCallback outputsCallback) throws IOException {
        int lineNumber = 0;
        double[] inputs = new double[nbInputs];
        double[] outputs = new double[nbOutputs];

        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            StringTokenizer tokenizer = new StringTokenizer(line);

            if ((lineNumber & 0x1) == 1) {
                inputs = new double[nbInputs];
                for (int i = 0; i < nbInputs; i++) {
                    inputs[i] = Double.parseDouble(tokenizer.nextToken());
                }
                inputsCallback.onEachPattern(inputs);
            } else {
                outputs = new double[nbOutputs];
                for (int i = 0; i < nbOutputs; i++) {
                    outputs[i] = Double.parseDouble(tokenizer.nextToken());
                }
                outputsCallback.onEachPattern(outputs);
            }
        }
    }

    // Read all lines, parse them as double and call "callback" on each line
    // Is memory-friendly
    public void applyOnEachLine(PatternCallback callback) throws IOException {
        int nbInputs = 0;
        int lineNumber = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            StringTokenizer tokenizer = new StringTokenizer(line);

            double inputs[];

            if (lineNumber != 1) {
                inputs = new double[nbInputs];
                for (int i = 0; i < nbInputs; i++) {
                    inputs[i] = Double.parseDouble(tokenizer.nextToken());
                }
            } else {
                inputs = new double[1];
                while (tokenizer.hasMoreTokens()) {
                    inputs = Arrays.copyOf(inputs, nbInputs + 1);
                    inputs[nbInputs] = Double.parseDouble(tokenizer.nextToken());
                    nbInputs++;
                }
            }
            callback.onEachPattern(inputs);
        }
    }

    public void writeArray(double[][] arr) throws IOException {
        for (double[] line : arr) {
            for (double col : line) {
                writeValue(col);
            }
            writer.newLine();
        }
    }

    public void writeValue(double d) throws IOException {
        writer.write(String.format("%04.9f ", d));
    }

    public void writeNewLine() throws IOException {
        writer.newLine();
    }
}
