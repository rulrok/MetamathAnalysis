package Graph.Algorithms;

import Graph.GraphFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class BipartGraph {

    private final GraphDatabaseService graph;

    public BipartGraph(GraphDatabaseService graph) {
        this.graph = graph;

    }

    public GraphDatabaseService execute(String outputGraphPath) {
        GraphDatabaseService bipartedGraph = GraphFactory.makeGraph(outputGraphPath, true);

        try (Transaction tx = graph.beginTx()) {
            try (Transaction tx2 = bipartedGraph.beginTx()) {

                //Create indexes
                IndexManager index = bipartedGraph.index();
                index.forNodes("exact-case-insensitive", MapUtil.stringMap("type", "exact", "to_lower_case", "true"));

                //Create the nodes
                GlobalGraphOperations.at(graph).getAllNodes().forEach((Node n) -> {

                    //Get label
                    Label label = n.getLabels().iterator().next();

                    //Get name
                    String nodeName = n.getProperty("name").toString();

                    //Create nodes
                    Node i1 = bipartedGraph.createNode(label);
                    Node i2 = bipartedGraph.createNode(label);

                    i1.setProperty("name", nodeName);
                    i2.setProperty("name", nodeName.concat("'"));
                });

                //Create the relationships
                GlobalGraphOperations.at(graph).getAllNodes().forEach((Node n) -> {

                    //Get label
                    Label label = n.getLabels().iterator().next();

                    //Get name
                    String nodeName = n.getProperty("name").toString();

                    //Find first node copy
                    Node i1 = bipartedGraph.findNode(label, "name", nodeName);

                    System.out.println("Precessando relacionamentos para nó " + nodeName + " ...");
                    //Create the appropriated relationships
                    n.getRelationships(Direction.OUTGOING).forEach((Relationship r) -> {
                        //Get original relationship and create on the new graph
                        Node endNode = r.getEndNode();
                        Label endNodeLabel = endNode.getLabels().iterator().next();
                        String endNodeName = endNode.getProperty("name").toString();

                        Node i2 = bipartedGraph.findNode(endNodeLabel, "name", endNodeName.concat("'"));

                        i1.createRelationshipTo(i2, r.getType());
                    });
                });

                tx2.success();
            }

            tx.failure();
        }

        return bipartedGraph;
    }
}
