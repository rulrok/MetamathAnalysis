package Utils.HIPR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class HIPRAnalyzeFlowSides {

    public static void AnalyzeSides(final GraphDatabaseService graph, ParseHIPRInputfile hiprInput, ParseHIPRFlowOutput hiprOutput, final File outputFile) throws FileNotFoundException {

        /**
         * Parse both files
         */
        hiprInput.parse();
        try {
            hiprOutput.parse();
        } catch (IOException ex) {
            Logger.getLogger(HIPRAnalyzeFlowSides.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        /**
         * Gather nodes on SINK side
         */
        Set<String> nodeIDsOnSinkSide = hiprOutput.getNodesIDsOnSinkSide();
        Set<String> nodeNamesSinkSide = new HashSet<>();
        nodeIDsOnSinkSide.stream().forEach((nodeID) -> {
            nodeNamesSinkSide.add(hiprInput.getNodeName(nodeID));
        });
        /**
         * Gather nodes on SOURCE side
         */
        Set<String> nodeNamesSourceSide = new HashSet<>();
        Set<String> nodeIDsOnSourceSide = hiprInput.getAllNodeIDs();
        nodeIDsOnSourceSide.removeAll(nodeIDsOnSinkSide);
        nodeIDsOnSourceSide.stream().forEach((nodeID) -> {
            nodeNamesSourceSide.add(hiprInput.getNodeName(nodeID));
        });

        /*
         * Create indexes
         */
        Map<Long, String> nodeNames = new HashMap<>();
        Map<String, Long> nodeIds = new HashMap<>();
        Map<Long, List<Long>> graphAdjacencyList = new HashMap<>();

        try (Transaction tx = graph.beginTx()) {
            graph.execute("CREATE INDEX ON :AXIOM(name);");
            graph.execute("CREATE INDEX ON :THEOREM(name);");

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            for (Node n : allNodes) {
                long id = n.getId();
                String name = n.getProperty("name").toString();
                nodeNames.put(id, name);
                nodeIds.put(name, id);
            }

            Iterable<Relationship> allRelationships = GlobalGraphOperations.at(graph).getAllRelationships();
            for (Relationship r : allRelationships) {
                long u = r.getStartNode().getId();
                long v = r.getEndNode().getId();

                List<Long> uConnections = graphAdjacencyList.getOrDefault(u, new ArrayList<>());
                uConnections.add(v);
                graphAdjacencyList.put(u, uConnections);
            }

        }

        /**
         * Output nodes
         */
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            outputFile.createNewFile();
            String lineBreak = System.lineSeparator();

            /**
             * Print nodes of frontier
             */
            fileWriter
                    .append("#####################")
                    .append("# NODES OF FRONTIER ")
                    .append("#####################")
                    .append(lineBreak).append(lineBreak);

            try (Transaction tx = graph.beginTx()) {
                //For each node on the source side
                List<String> frontierNodesNames = new ArrayList<>();
                for (String sourceNodeName : nodeNamesSourceSide) {
                    //Search it in the graph to analyse its neighbours
                    Long u = nodeIds.get(sourceNodeName);

                    //Look up all its outgoing relationships
                    List<Long> uRels = graphAdjacencyList.getOrDefault(u, new ArrayList<>());
                    for (Long v : uRels) {
                        //Node u connects to v ( u -> v )
                        String iName = nodeNames.get(v);

                        if (nodeNamesSinkSide.contains(iName)) {
                            frontierNodesNames.add(sourceNodeName);
                            break;
                        }
                    }
                }

                Collections.sort(frontierNodesNames);
                frontierNodesNames.forEach((String sourceNodeName) -> {
                    try {
                        fileWriter.append(sourceNodeName).append(lineBreak);
                    } catch (IOException ex) {
                        Logger.getLogger(HIPRAnalyzeFlowSides.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }

            /**
             * Print nodes on SOURCE side
             */
            fileWriter
                    .append(lineBreak).append(lineBreak)
                    .append("#####################")
                    .append("# NODES ON SOURCE SIDE ")
                    .append("#####################")
                    .append(lineBreak).append(lineBreak);
            for (String nodeOnSource : nodeNamesSourceSide.stream().sorted().collect(Collectors.toList())) {
                fileWriter.append(nodeOnSource).append(lineBreak);
            }

            /**
             * Print nodes on SINK side
             */
            fileWriter
                    .append(lineBreak).append(lineBreak)
                    .append("#####################")
                    .append("# NODES ON SINK SIDE ")
                    .append("#####################")
                    .append(lineBreak).append(lineBreak);
            for (String nodeOnSink : nodeNamesSinkSide.stream().sorted().collect(Collectors.toList())) {
                fileWriter.append(nodeOnSink).append(lineBreak);
            }
            fileWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(HIPRAnalyzeFlowSides.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
