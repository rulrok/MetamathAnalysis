package Analysis.FlowAnalysis.IndividualFlows;

import Utils.HistogramUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 *
 * @author Reuel
 */
public class MaxFlowIndividualSourcesSinksParseResult {

    public static void main(String[] args) throws IOException {
        final Path inputFilePath = Paths.get("metamath-nouserboxes_halved_individual-flow_axiom-theorem_all_maxflows.txt");
        List<String> allLines = Files.readAllLines(inputFilePath);

        Map<String, Double> sourcesFlows = new TreeMap<>();
        allLines.forEach((String line) -> {
            String[] values = line.split(" ");
            String origin = values[0];
            Double maxFlow = Double.parseDouble(values[values.length - 1]);

            if (sourcesFlows.containsKey(origin)) {
                Double currentFlow = sourcesFlows.get(origin);
                currentFlow += maxFlow;
                sourcesFlows.put(origin, currentFlow);
            } else {
                sourcesFlows.put(origin, maxFlow);
            }
        });

        System.out.println("======================================");
        System.out.println("== Max flows:");
        System.out.println("======================================");

        sourcesFlows
                .entrySet()
                .stream()
                .sorted((Map.Entry<String, Double> e, Map.Entry<String, Double> e1) -> {
                    return Double.compare(e.getValue(), e1.getValue());
                })
                .forEach(e -> {
                    System.out.println(e.getKey().concat("\t").concat(e.getValue().toString()));
                });

    }

}
