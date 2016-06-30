package Graph.Algorithms;

import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class HalveNodes {

    private final GraphDatabaseService graph;

    public HalveNodes(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        GraphDatabaseService graphCopy = GraphFactory.copyGraph("db/metamath", "db/halved_graph");

        HalveNodes halveNodes = new HalveNodes(graphCopy);

        halveNodes.execute();
    }

}
