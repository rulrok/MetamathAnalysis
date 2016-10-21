package Analysis.FlowAnalysis.IndividualFlows;

import Graph.GraphFactory;
import Graph.Label;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class MaxFlowIndividualSourcesSinksParseResult {

    public static void main(String[] args) throws IOException {
        /**
         * Input and output files
         */
        final Path inputFilePath = Paths.get("metamath-nouserboxes_halved_individual-flow_axiom-theorem_all_maxflows.txt");
        final Path outputFilePath = Paths.get("metamath-nouserboxes_halved_individual-flow_axiom-theorem_all_maxflows_results.csv");

        /**
         * Read all lines from input files
         */
        List<String> allLines = Files.readAllLines(inputFilePath);
        StringBuilder analysisSB = new StringBuilder(allLines.size() * 80);

        /**
         * Parse each line
         */
        GraphDatabaseService graph = GraphFactory.makeGraph("db/metamath-nouserboxes_halved_individual-flow_axiom-theorem");
        Map<String, Integer> nodesDegreeCache = new HashMap<>();

        Map<String, Double> sourcesFlows = new TreeMap<>();
        int destinBottleNeckCount = 0, originBottleneckCount = 0, elsewhereBottleneckCount = 0;
        try (Transaction tx = graph.beginTx()) {
            for (String line : allLines) {
                String[] values = line.split(" ");
                String origin = values[0];
                String destin = values[2];
                Double maxFlow = Double.parseDouble(values[values.length - 1]);

                if (sourcesFlows.containsKey(origin)) {
                    Double currentFlow = sourcesFlows.get(origin);
                    currentFlow += maxFlow;
                    sourcesFlows.put(origin, currentFlow);
                } else {
                    sourcesFlows.put(origin, maxFlow);
                }

                /**
                 * Interpret line
                 */
                if (maxFlow > 0) {

                    if (!nodesDegreeCache.containsKey(origin)) {
                        Node nodeFound = graph.findNode(Label.AXIOM, "name", origin);
                        if (nodeFound == null) {
                            nodeFound = graph.findNode(Label.THEOREM, "name", origin);
                        }
                        int originOutDegree = nodeFound.getDegree(Direction.OUTGOING);
                        nodesDegreeCache.put(origin, originOutDegree);
                    }
                    Integer originOutDegree = nodesDegreeCache.get(origin);

                    if (!nodesDegreeCache.containsKey(destin)) {
                        Node nodeFound = graph.findNode(Label.THEOREM, "name", destin);
                        if (nodeFound == null) {
                            nodeFound = graph.findNode(Label.AXIOM, "name", destin);
                        }
                        int destinInDegree = nodeFound.getDegree(Direction.INCOMING);
                        nodesDegreeCache.put(destin, destinInDegree);
                    }
                    Integer destingInDegree = nodesDegreeCache.get(destin);

                    String bottleNeck = "elsewhere";
                    if (maxFlow.intValue() == originOutDegree) {
                        bottleNeck = "origin";
                        originBottleneckCount++;
                    } else if (maxFlow.intValue() == destingInDegree) {
                        bottleNeck = "destin";
                        destinBottleNeckCount++;
                    } else {
                        elsewhereBottleneckCount++;
                    }

                    analysisSB
                            .append(origin).append(" ; ")
                            .append(destin).append(" ; ")
                            .append(maxFlow).append(" ; ")
                            .append(originOutDegree).append(" ; ")
                            .append(destingInDegree).append(" ; ")
                            .append(bottleNeck)
                            .append(System.lineSeparator());
                }
            }
            tx.failure();
        }

        /**
         * Write stringbuilder to file
         */
        try (FileWriter fileWriter = new FileWriter(new File(outputFilePath.toUri()))) {
            fileWriter.append("# Bottlenecks" + System.lineSeparator());
            fileWriter.append("Destin ; " + destinBottleNeckCount + System.lineSeparator());
            fileWriter.append("Origin ; " + originBottleneckCount + System.lineSeparator());
            fileWriter.append("Elsewhere ; " + elsewhereBottleneckCount + System.lineSeparator());
            fileWriter.append("# origin ; destin ; flow ; origin_out_degree ; desting_in_degree ; bottleneck" + System.lineSeparator());
            fileWriter.append(analysisSB);
        }

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
