
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class Main {

    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/metamath");

        //Make sure we don't change the graph
        graphDb.shutdown();
    }
}
