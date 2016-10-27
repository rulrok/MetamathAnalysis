package Graph;

import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.RemoveIsolatedNodes;
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

    public static final String DEFAULT_METAMATH_DB = "db/metamath";

    public static final String NOUSERBOX_METAMATH_DB = "db/metamath-nouserboxes";

    public static final String NOUSERBOX_METAMATH_NOJUNK_DB = "db/metamath-nouserboxes-nojunk";

    public static GraphDatabaseService makeDefaultMetamathGraph() {
        return makeGraph(DEFAULT_METAMATH_DB);
    }

    public static GraphDatabaseService makeNoUserboxesMetamathGraph() {
        return makeGraph(NOUSERBOX_METAMATH_DB);
    }

    public static GraphDatabaseService makeNoUserboxesNoJunkMetamathGraph() {

        GraphDatabaseService graph = copyGraph(NOUSERBOX_METAMATH_DB, NOUSERBOX_METAMATH_NOJUNK_DB);

        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr
                .addComponentHeadDFS("ax-meredith")
                .addCustomFilter(n -> n.getProperty("name").toString().startsWith("dummy"))
                .execute();

        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

        return graph;
    }

    public static GraphDatabaseService makeNoUserboxesNoJunkAxiomTheoremMetamathGraph() {
        GraphDatabaseService graph = makeNoUserboxesNoJunkMetamathGraph();
        GraphNodeRemover.KeepOnlyAxiomsAndTheorems(graph);

        return graph;
    }

    public static GraphDatabaseService makeGraph(String path) {
        return makeGraph(path, false);
    }

    public static GraphDatabaseService makeGraph(String path, boolean deleteExisting) {

        if (deleteExisting) {
            try {
                FileUtils.deleteRecursively(new File(path));
            } catch (IOException ex) {
                Logger.getLogger(GraphFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

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
     * </pre> All edges go downward and use RelType.SUPPORT
     *
     * @return GraphDatabaseService
     */
    public static GraphDatabaseService makeTestGraphJ() {

        GraphDatabaseService graphTest = makeGraph("db/test", true);

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

            e.createRelationshipTo(d, RelType.SUPPORTS);
            d.createRelationshipTo(b, RelType.SUPPORTS);
            b.createRelationshipTo(a, RelType.SUPPORTS);
            b.createRelationshipTo(c, RelType.SUPPORTS);
            e.createRelationshipTo(g, RelType.SUPPORTS);
            g.createRelationshipTo(f, RelType.SUPPORTS);
            g.createRelationshipTo(i, RelType.SUPPORTS);
            i.createRelationshipTo(h, RelType.SUPPORTS);
            i.createRelationshipTo(j, RelType.SUPPORTS);

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

    public static GraphDatabaseService copyGraph(String inputGraphPath, String outputGraphPath) {

        File outputFile = new File(outputGraphPath);
        File inputFile = new File(inputGraphPath);

        try {
            FileUtils.deleteRecursively(outputFile);
            FileUtils.copyRecursively(inputFile, outputFile);
        } catch (IOException ex) {
            Logger.getLogger(GraphFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return makeGraph(outputGraphPath, false);
    }
}
