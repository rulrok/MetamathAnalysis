package Graph;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.io.fs.FileUtils;

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
     * </pre> All edges go downward and use RelTypes.SUPPORT
     *
     * @return GraphDatabaseService
     */
    public static GraphDatabaseService makeTestGraphJ() {
        try {
            FileUtils.deleteRecursively(new File("db/test"));
        } catch (IOException ex) {
            Logger.getLogger(GraphFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        GraphDatabaseService graphTest = makeGraph("db/test");

        try (Transaction tx = graphTest.beginTx()) {
            Node a = graphTest.createNode();
            a.setProperty("name", "A");
            Node b = graphTest.createNode();
            b.setProperty("name", "B");
            Node c = graphTest.createNode();
            c.setProperty("name", "C");
            Node d = graphTest.createNode();
            d.setProperty("name", "D");
            Node e = graphTest.createNode();
            e.setProperty("name", "E");
            Node f = graphTest.createNode();
            f.setProperty("name", "F");
            Node g = graphTest.createNode();
            g.setProperty("name", "G");
            Node h = graphTest.createNode();
            h.setProperty("name", "H");
            Node i = graphTest.createNode();
            i.setProperty("name", "I");
            Node j = graphTest.createNode();
            j.setProperty("name", "J");

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
