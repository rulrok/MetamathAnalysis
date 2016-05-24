package Graph;

import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Reuel
 */
public class GraphFactory {

    public static GraphDatabaseService makeDefaultMetamathGraph() {
        return makeGraph("db/metamath");
    }

    public static GraphDatabaseService makeGraph(String path) {
        File dbPath = new File(path);
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(dbPath)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
        registerShutdownHook(graphDb);
        return graphDb;
    }

    /**
     * Returns the folowing graph
     * <pre>
     *       E(4)
     *      / \
     *     D(3)G(6)
     *    /   /  \
     *   B(1)F(5) I(8)
     *  / \      / \
     * A(0)C(2) H(7)J(9)
     * </pre>
     * All edges go downward and use RelTypes.SUPPORT
     *
     * @return GraphDatabaseService
     */
    public static GraphDatabaseService makeTestGraphJ() {
        GraphDatabaseService graphTest = makeGraph("db/test");

        try (Transaction tx = graphTest.beginTx()) {
            Node a = graphTest.createNode();
            a.setProperty("Name", "A");
            Node b = graphTest.createNode();
            b.setProperty("Name", "B");
            Node c = graphTest.createNode();
            c.setProperty("Name", "C");
            Node d = graphTest.createNode();
            d.setProperty("Name", "D");
            Node e = graphTest.createNode();
            e.setProperty("Name", "E");
            Node f = graphTest.createNode();
            f.setProperty("Name", "F");
            Node g = graphTest.createNode();
            g.setProperty("Name", "G");
            Node h = graphTest.createNode();
            h.setProperty("Name", "H");
            Node i = graphTest.createNode();
            i.setProperty("Name", "I");
            Node j = graphTest.createNode();
            j.setProperty("Name", "J");

            e.createRelationshipTo(d, RelTypes.SUPPORTS);
            d.createRelationshipTo(b, RelTypes.SUPPORTS);
            b.createRelationshipTo(a, RelTypes.SUPPORTS);
            b.createRelationshipTo(c, RelTypes.SUPPORTS);
            e.createRelationshipTo(g, RelTypes.SUPPORTS);
            g.createRelationshipTo(f, RelTypes.SUPPORTS);
            g.createRelationshipTo(i, RelTypes.SUPPORTS);
            i.createRelationshipTo(h, RelTypes.SUPPORTS);
            i.createRelationshipTo(j, RelTypes.SUPPORTS);

            tx.success();

        }

        return graphTest;
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
