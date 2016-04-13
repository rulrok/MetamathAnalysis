
import Graph.Algorithms.ManualDFS;
import Graph.Algorithms.Contracts.StrongConnectedComponents;
import Graph.Label;
import Graph.RelTypes;
import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/**
 *
 * @author Reuel
 */
public class Main {

    public static void main(String[] args) {
        File dbPath = new File("db/metamath");
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(dbPath)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        registerShutdownHook(graphDb);

        try (Transaction tx = graphDb.beginTx()) {

            /* make a new vertex x with edges x->v for all v */
            Node helperNode = graphDb.createNode();
            ResourceIterator<Node> axiomNodes = graphDb.findNodes(Label.AXIOM);
            for (; axiomNodes.hasNext();) {
                Node axiom = axiomNodes.next();
                helperNode.createRelationshipTo(axiom, RelTypes.SUPPORTS);
            }

            StrongConnectedComponents scc = new ManualDFS(graphDb, helperNode);
            scc.execute();

            tx.failure();
        }

        graphDb.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
