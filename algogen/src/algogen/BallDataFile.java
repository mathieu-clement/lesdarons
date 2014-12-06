package algogen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class BallDataFile {

    private final BufferedReader reader;

    private BallDataFile(String filename) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filename));
    }

    public static BallDataFile open(String filename) throws FileNotFoundException {
        return new BallDataFile(filename);
    }

    public void close() throws IOException {
        reader.close();
    }

    public double[][] toArray() throws IOException {
        ToArrayPatternCallback callback = new ToArrayPatternCallback();
        applyOnEachLine(callback);
        return callback.getResults();
    }

    private static class ToArrayPatternCallback implements PatternCallback {

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
    public void applyOnEachLine(PatternCallback callback) throws IOException {
        int nbInputs = 0;
        int lineNumber = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            Scanner scanner = new Scanner(line);

            double inputs[];

            if (lineNumber != 1) {
                inputs = new double[nbInputs];
                for (int i = 0; i < nbInputs; i++) {
                    inputs[i] = scanner.nextDouble();
                }
            } else {
                inputs = new double[1];
                while (scanner.hasNextDouble()) {
                    inputs = Arrays.copyOf(inputs, nbInputs + 1);
                    inputs[nbInputs] = scanner.nextDouble();
                    nbInputs++;
                }
            }
            callback.onEachPattern(inputs);
        }
    }
}
