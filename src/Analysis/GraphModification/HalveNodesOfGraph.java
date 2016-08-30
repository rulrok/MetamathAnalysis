package Analysis.GraphModification;

import Graph.Algorithms.HalveNodes;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class HalveNodesOfGraph {

    public static void main(String[] args) {
        GraphDatabaseService graphCopy = GraphFactory.copyGraph("db/metamath", "db/metamath_halved");

        HalveNodes halveNodes = new HalveNodes(graphCopy);

        halveNodes.execute();
    }

}
