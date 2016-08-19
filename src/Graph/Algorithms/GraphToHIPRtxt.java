package Graph.Algorithms;

import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.GraphFactory;
import Graph.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GraphToHIPRtxt implements LabelFiltered {

    private final List<Label> labelFilters = new ArrayList<>();
    private boolean superSinkSourceCustomWeight = false;
    GraphDatabaseService graph;

    public GraphToHIPRtxt(GraphDatabaseService graph) {
        this.graph = graph;
    }

    /**
     * If set to false, ALL arcs will have an unitary weight. Otherwise, the
     * arcs coming out of a SuperSource will have the weight set as the number
     * of the outter degree of the nodes that they each reache. The same occurs
     * for the SuperSink, but the weight of the arcs being defined based on the
     * inner degree of the nodes which reach the SuperSink. It is assumed that
     * the SuperSouce and SuperSink are named 'S' and 'T', respectively.
     *
     * @param value
     * @return
     */
    public GraphToHIPRtxt customWeightForSuperSinkAndSource(boolean value) {
        superSinkSourceCustomWeight = value;

        return this;
    }

    @Override
    public GraphToHIPRtxt addFilterLabel(Label label) {
        labelFilters.add(label);
        return this;
    }

    public boolean execute(String outputFilePath) {

        File outputGraph = new File(outputFilePath);
        try {
            outputGraph.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Cannot create file");
            return false;
        }

        if (!outputGraph.canWrite()) {
            System.err.println("Cannot write into the file");
            return false;
        }

        try (Transaction tx = graph.beginTx()) {

            /*
             * Prepare a stringbuilder to hold the information about the arcs
             */
            StringBuilder arcsInfo = new StringBuilder();

            /*
             * Count the nodes
             */
            int nodesCount = 0;

            /*
             * Count the arcs and prepare the output string
             */
            int arcsCount = 0;

            //The if is outsite for performance reasons
            if (labelFilters.isEmpty()) {
                for (Node n : GlobalGraphOperations.at(graph).getAllNodes()) {
                    nodesCount++;
                }

                int weight = 1;
                for (Relationship r : GlobalGraphOperations.at(graph).getAllRelationships()) {
                    arcsCount++;

                    Node startNode = r.getStartNode();
                    Node endNode = r.getEndNode();

                    //Nodes ids start at 0 at least
                    long startNodeId = startNode.getId();
                    long endNodeId = endNode.getId();

                    Object startNodeName = startNode.getProperty("name");
                    Object endNodeName = endNode.getProperty("name");

                    if (superSinkSourceCustomWeight) {
                        if (startNodeName.equals("S")) {
                            //SuperSource
                            weight = endNode.getDegree(Direction.OUTGOING);
                        }

                        if (endNodeName.equals("T")) {
                            //SuperSink
                            weight = startNode.getDegree(Direction.INCOMING);
                        }
                    }

                    print_line(arcsInfo, startNodeId, endNodeId, startNodeName, endNodeName, weight);
                    weight = 1;
                }
            } else {

                int weight = 1;
                for (Relationship r : GlobalGraphOperations.at(graph).getAllRelationships()) {

                    Node startNode = r.getStartNode();
                    Node endNode = r.getEndNode();

                    //Verify if both the ends are the intended nodes
                    boolean startMatch = false, endMatch = false;
                    for (Label l : labelFilters) {
                        startMatch |= startNode.hasLabel(l);
                        endMatch |= endNode.hasLabel(l);
                    }
                    if (!startMatch || !endMatch) {
                        continue;
                    }

                    //At this point, we are sure that both startNode and endNode
                    //have one of the desired labels
                    arcsCount++;

                    //Nodes ids start at 0 at least
                    long startNodeId = uniqueSequencialId(startNode.getId());
                    long endNodeId = uniqueSequencialId(endNode.getId());

                    Object startNodeName = startNode.getProperty("name");
                    Object endNodeName = endNode.getProperty("name");

                    if (superSinkSourceCustomWeight) {
                        if (startNodeName.equals("S")) {
                            //SuperSource
                            weight = endNode.getDegree(Direction.OUTGOING);
                        }

                        if (endNodeName.equals("T")) {
                            //SuperSink
                            weight = startNode.getDegree(Direction.INCOMING);
                        }
                    }

                    print_line(arcsInfo, startNodeId, endNodeId, startNodeName, endNodeName, weight);
                    weight = 1;
                }

                nodesCount = (int) guid;
            }

            //TODO Remove dependency with the SUperSinkSUperSource algorithm.
            //We must know that S is artificially labeled as Axiom, and T as a THEOREM
            Node S = graph.findNode(Label.AXIOM, "name", "S");
            Node T = graph.findNode(Label.THEOREM, "name", "T");

            try (PrintWriter printWriter = new PrintWriter(outputGraph)) {
                printWriter.printf("p max %d %d\r\n", nodesCount, arcsCount);
                printWriter.printf("n %d s\r\n", uniqueSequencialId(S.getId()));
                printWriter.printf("n %d t\r\n", uniqueSequencialId(T.getId()));

                printWriter.append(arcsInfo);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(GraphToHIPRtxt.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Make sure we don't modify the original graph
            tx.failure();
        }

        return true;
    }

    private void print_line(StringBuilder arcsInfo, long startNodeId, long endNodeId, Object startNodeName, Object endNodeName, int weight) {
        //e.g., a 1 2 5
        //a = means 'arc', source, destination, weight of the arc
        arcsInfo.append("a ").append(startNodeId).append(' ').append(endNodeId).append(" ").append(weight).append(" ")
                //The below line is an extra only to make it easier to interpret the output
                .append("( ").append(startNodeName).append(" -> ").append(endNodeName).append(" )\r\n");
    }

    private long guid = 0;
    private final Map<Long, Long> inputs = new HashMap<>();

    /**
     * For the HIPR library, the index of node numbers needs to be sequencial.
     * This function associates a unique sequencial value for a given
     * non-sequencial number.
     *
     * @param input
     * @return
     */
    private long uniqueSequencialId(long input) {

        if (inputs.containsKey(input)) {
            return inputs.get(input);
        } else {
            inputs.put(input, ++guid);
            return guid;
        }
    }
}
