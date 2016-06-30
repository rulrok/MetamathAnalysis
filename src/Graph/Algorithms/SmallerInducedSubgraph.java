package Graph.Algorithms;

import Graph.GraphFactory;
import java.util.ArrayList;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class SmallerInducedSubgraph {

    private final GraphDatabaseService graph;
    private final GraphDatabaseService outputGraph;

    public SmallerInducedSubgraph(GraphDatabaseService graph, GraphDatabaseService outputGraph) {
        this.graph = graph;
        this.outputGraph = outputGraph;
    }

    public void execute(String... nodeNames) {
        try (Transaction tx = graph.beginTx()) {
            ArrayList<Node> nodes = new ArrayList<>(nodeNames.length);

            //First, get the node proxies based on the input string names
            for (String nodeName : nodeNames) {
                for (Label label : GlobalGraphOperations.at(graph).getAllLabels()) {
                    Node foundNode = graph.findNode(label, "name", nodeName);
                    if (foundNode != null) {
                        nodes.add(foundNode);
                        break;
                    }
                }
            }

            ArrayList<Node> newNodes = new ArrayList<>(nodes.size());
            //Second, construct the graph based on the nodes and relationships
            try (Transaction txOutputGraph = outputGraph.beginTx()) {

                for (Node node : nodes) {

                    //Verify if the node was already inserted in the new graph
                    Node foundNode;
                    foundNode = newNodes.stream()
                            .filter(n -> n.getProperty("name").equals(node.getProperty("name")))
                            .findFirst()
                            .orElse(null);

                    Node newNode;
                    if (foundNode == null) {
                        newNode = outputGraph.createNode();
                        newNode.setProperty("name", node.getProperty("name"));
                        newNodes.add(newNode);
                    } else {
                        newNode = foundNode;
                    }

                    //Verify the incoming relationships from other nodes
                    for (Relationship inRels : node.getRelationships(Direction.INCOMING)) {
                        //'node' is the end node of the relationship
                        //'startNode'---->'node'
                        Node startNode = inRels.getStartNode();

                        //'startNode' is not one of the desired nodes, move on...
                        if (!nodes.contains(startNode)) {
                            continue;
                        }

                        //Verify if the node was already inserted in the new graph
                        foundNode = newNodes.stream()
                                .filter(n -> n.getProperty("name").equals(startNode.getProperty("name")))
                                .findFirst()
                                .orElse(null);

                        Node newStartNode;
                        if (foundNode == null) {
                            newStartNode = outputGraph.createNode();
                            newStartNode.setProperty("name", startNode.getProperty("name"));
                            newNodes.add(newStartNode);
                        } else {
                            newStartNode = foundNode;
                        }

                        //Verify if the relationship has already been added
                        boolean relExists = false;
                        for (Relationship rel : newStartNode.getRelationships(Direction.OUTGOING)) {
                            if (rel.getEndNode().equals(newNode)) {
                                relExists = true;
                                break;
                            }
                        }

                        if (!relExists) {
                            newStartNode.createRelationshipTo(newNode, inRels.getType());
                        }

                    }

                    for (Relationship outRels : node.getRelationships(Direction.OUTGOING)) {
                        //'node' is the start node of the relationship
                        //'node'---->'endNode'
                        Node endNode = outRels.getEndNode();

                        //'endNode' is not one of the desired nodes, move on...
                        if (!nodes.contains(endNode)) {
                            continue;
                        }

                        //Verify if the node was already inserted in the new graph
                        foundNode = newNodes.stream()
                                .filter(n -> n.getProperty("name").equals(endNode.getProperty("name")))
                                .findFirst()
                                .orElse(null);

                        Node newEndNode;
                        if (foundNode == null) {
                            newEndNode = outputGraph.createNode();
                            newEndNode.setProperty("name", endNode.getProperty("name"));
                            newNodes.add(newEndNode);
                        } else {
                            newEndNode = foundNode;
                        }

                        //Verify if the relationship has already been added
                        boolean relExists = false;
                        for (Relationship rel : newNode.getRelationships(Direction.OUTGOING)) {
                            if (rel.getEndNode().equals(newEndNode)) {
                                relExists = true;
                                break;
                            }
                        }

                        if (!relExists) {
                            newNode.createRelationshipTo(newEndNode, outRels.getType());
                        }
                    }
                }

                txOutputGraph.success(); //Persist the new graph
            }

            tx.failure(); //Make sure we do not modify the original graph
        }
    }

    public static void main(String[] args) {

        //Prepare databases
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/Betweenness", true);

        //Prepare algorithm object
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

        //Execute it
        msgi.execute(
                "fmpt",
                "dvhlvec",
                "dvhlveclem",
                "frfnom",
                "pwuninel",
                "occllem",
                "ltxrlt",
                "occl",
                "1nn",
                "fvex"
        );
    }

}
