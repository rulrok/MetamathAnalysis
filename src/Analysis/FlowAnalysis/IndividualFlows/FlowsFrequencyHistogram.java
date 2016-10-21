package Analysis.FlowAnalysis.IndividualFlows;

import Utils.HistogramUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Reuel
 */
public class FlowsFrequencyHistogram {

    public static void main(String[] args) throws IOException {
        final Path inputFilePath = Paths.get("metamath-nouserboxes_halved_individual-flow_axiom-theorem_all_maxflows.txt");

        List<String> allLines = Files.readAllLines(inputFilePath);

        Map<Integer, Integer> frequencyHistogram = new TreeMap<>();
        allLines.forEach(line -> {
            String[] split = line.split(" ");
            Double flow = Double.parseDouble(split[split.length - 1]);

            Integer frequencyCount = frequencyHistogram.getOrDefault(flow.intValue(), 0);
            frequencyCount++;
            frequencyHistogram.put(flow.intValue(), frequencyCount);
        });

        HistogramUtils.PrintHistogram(frequencyHistogram);
    }
}
