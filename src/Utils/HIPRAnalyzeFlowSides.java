package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.MapUtil;

/**
 *
 * @author Reuel
 */
public class HIPRAnalyzeFlowSides {

    public static void AnalyzeSides(final GraphDatabaseService graph, ParseHIPRInputfile hipr_parsed, ParseHIPROutput hipr_results_parsed, final File outputFile) throws FileNotFoundException {

        /**
         * Parse both files
         */
        hipr_parsed.parse();
        hipr_results_parsed.parse();

        /**
         * Gather nodes on SINK side
         */
        Set<String> nodeIDsOnSinkSide = hipr_results_parsed.getNodesOnSinkSide();
        Set<String> nodeNamesSinkSide = new HashSet<>();
        nodeIDsOnSinkSide.stream().forEach((nodeID) -> {
            nodeNamesSinkSide.add(hipr_parsed.getNodeName(nodeID));
        });
        /**
         * Gather nodes on SOURCE side
         */
        Set<String> nodeNamesSourceSide = new HashSet<>();
        Set<String> nodeIDsOnSourceSide = hipr_parsed.getAllNodeIDs();
        nodeIDsOnSourceSide.removeAll(nodeIDsOnSinkSide);
        nodeIDsOnSourceSide.stream().forEach((nodeID) -> {
            nodeNamesSourceSide.add(hipr_parsed.getNodeName(nodeID));
        });
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
                for (String sourceNodeName : nodeNamesSourceSide) {
                    //Search it in the graph to analyse its neighbours
                    Map<String, Object> param = MapUtil.map("nodeName", sourceNodeName);
                    Result result = graph.execute("MATCH (n{name: {nodeName}}) RETURN n", param);
                    //If we find it
                    if (result.hasNext()) {
                        Node foundNode = (Node) result.next().get("n");
                        //Look up all its outgoing relationships
                        Iterable<Relationship> relationships = foundNode.getRelationships(Direction.OUTGOING);
                        for (Relationship r : relationships) {
                            Node neighbour = r.getEndNode();
                            String neighbourName = neighbour.getProperty("name").toString();

                            //If the name is on the sink side, the source node is a frontier one
                            if (nodeNamesSinkSide.contains(neighbourName)) {
                                fileWriter.append(sourceNodeName).append(lineBreak);
                                break;
                            }
                        }
                    }
                }
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
