package Analysis.FlowAnalysis;

import Utils.HIPR.ParseHIPRFlowOutput;
import Utils.HIPR.ParseHIPRInputfile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author Reuel
 */
public class SuperFlowBipartedGraphAxiomTheorems {

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("biparted-graph-super-axiom-theorem_maxflow.txt");
        ParseHIPRFlowOutput hiprOutput = new ParseHIPRFlowOutput(file);
        hiprOutput.parse();

        File file2 = new File("biparted-graph-super-axiom-theorem.txt");
        ParseHIPRInputfile hiprInput = new ParseHIPRInputfile(file2);
        hiprInput.parse();
        int nodesCount = hiprInput.getNodesCount();
        int S = hiprInput.getS();

        Map<Integer, List<String>> paths = new TreeMap<>();

        for (int firstNode = 0, pathCount = 0; firstNode <= nodesCount; firstNode++) {
            //verify if S has and outgoing edge for the actual node
            if (hiprOutput.getArcFlow(S, firstNode) <= 0) {
                continue;
            }

            pathCount++;

            List<String> actualPath = new ArrayList<>();
            paths.put(pathCount, actualPath);

            //And edge has been found from S to some node
//            System.out.printf("[%d] (S ->", pathCount);
            for (int originalNode = firstNode, copyNode = 0; copyNode <= nodesCount; copyNode++) {

                if (hiprOutput.<Integer>getArcFlow(originalNode, copyNode) > 0) {
                    String originalName = hiprInput.getNodeName(originalNode);
                    String copyName = hiprInput.getNodeName(copyNode);

                    actualPath.add(originalName);

                    if (copyName.equals("T") || !copyName.endsWith("'")) {
                        throw new RuntimeException("Some possible inconsistency has been found!");
                    }

                    String nextOriginalNodeName = copyName.substring(0, copyName.length() - 1);
                    int nextOriginalNode = hiprInput.getNodeId(nextOriginalNodeName);

                    if (nextOriginalNode == -1) {
                        break;
                    }

                    originalNode = nextOriginalNode;
                    copyNode = 0;
                }
            }

        }
        System.out.println("===========================================");

        System.out.println("Greatest path:");
        Optional<Entry<Integer, List<String>>> collect = paths.entrySet().stream()
                .collect(
                        Collectors.maxBy(Comparator.comparingInt((Entry<Integer, List<String>> e) -> {
                            return e.getValue().size();
                        }))
                );
        Entry<Integer, List<String>> get = collect.get();
        System.out.println(get);

        Map<Integer, Integer> histogram = new TreeMap<>();

        paths.entrySet().stream()
                .sorted((Map.Entry<Integer, List<String>> t, Map.Entry<Integer, List<String>> t1) -> Integer.compare(t.getValue().size(), t1.getValue().size()))
                .forEach((Map.Entry<Integer, List<String>> entry) -> {
                    Integer key = entry.getKey();
                    List<String> list = entry.getValue();

                    histogram.putIfAbsent(list.size(), 0);

                    int count = histogram.get(list.size());
                    count++;
                    histogram.put(list.size(), count);

                });

        histogram.entrySet().stream().forEach((entry) -> {
            Integer key = entry.getKey();
            Integer value = entry.getValue();

            String column = "";

            for (int i = 0; i < value; i++) {
                column = column.concat("#");
            }
            System.out.printf("%d : %d %s\n", key, value, column);
        });

        Integer pathsSum = histogram.entrySet()
                .stream()
                .collect(Collectors.summingInt(Entry<Integer, Integer>::getValue));

        System.out.println("Total of paths: " + pathsSum);
        int minus = hiprOutput.getNodesCount() - (int) hiprOutput.getMaxFlow();

        System.out.println("Nodes - Flow: " + minus);

        int compare = Integer.compare(pathsSum, minus);

        if (compare == 0) {
            System.out.println("The paths are consistent");
        } else if (compare < 0) {
            System.out.printf("The paths are inconsistent (%d)\n", compare);
        } else {
            System.out.printf("The paths are inconsistent (%d)\n", compare);
        }
    }
}
