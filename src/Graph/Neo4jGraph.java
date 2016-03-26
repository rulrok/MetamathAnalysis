/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import java.io.File;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/**
 *
 * @author Reuel
 */
public class Neo4jGraph implements IGraph {

    GraphDatabaseService graphDb;
    Transaction tx;

    public Neo4jGraph(String databasePath) {
        File dbPath = new File(databasePath);

        graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(dbPath)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        registerShutdownHook(graphDb);
    }

    @Override
    public Node addProperty(String nodeName, String key, String value) {
        System.out.println("'Added' a new property called '" + key + "' with value '" + value + "' to the node '" + nodeName + "'.");
        return new FakeNode();
    }

    @Override
    public Node addLabel(String nodeName, String labelName) {
        System.out.println("'Added' a new label called '" + labelName + "' to the node '" + labelName + "'.");
        return new FakeNode();
    }

    @Override
    public Node addNode(String nodeName) {
        System.out.println("'Added' new node called '" + nodeName + "'.");
        return new FakeNode();
    }

    @Override
    public Node addNode(String nodeName, String labelName) {
        System.out.println("'Added' new node called '" + nodeName + "' with the label '" + labelName + "'.");
        return new FakeNode();
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest) {
        System.out.println("'Added' new relationship (" + nodeNameSrc + ")-->(" + nodeNameDest + ").");
        return new FakeRelationship();
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest, String labelName) {
        RelTypes label = RelTypes.valueOf(labelName);
        System.out.println("'Added' new relationship (" + nodeNameSrc + ")-[" + label + "]->(" + nodeNameDest + ").");
        return new FakeRelationship();
    }

    @Override
    public Relationship createRelationship(Node nodeNameSrc, Node nodeNameDest, String labelName, Map<String, String> properties) {
        RelTypes label = RelTypes.valueOf(labelName);
        System.out.println("'Added' new relationship (" + nodeNameSrc + ")-[" + label + "]->(" + nodeNameDest + ") with the following properties:");
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("\t" + key + ": " + value);

        }
        return new FakeRelationship();
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

    @Override
    public void StartTransaction() {
        if (tx == null) {
            tx = graphDb.beginTx();
        }
    }

    @Override
    public void CommitTransaction() {
        if (tx != null) {
            tx.success();
            tx = null;
        }
    }
}
