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

    public Neo4jGraph(String databasePath) {
        this(new File(databasePath));
    }

    public Neo4jGraph(File databaseFolder) {
        graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(databaseFolder)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        registerShutdownHook(graphDb);
    }

    @Override
    public Node addProperty(String nodeName, String key, String value) {
        println("'Added' a new property called '" + key + "' with value '" + value + "' to the node '" + nodeName + "'.");
        return new FakeNode();
    }

    @Override
    public Node addLabel(String nodeName, String labelName) {
        println("'Added' a new label called '" + labelName + "' to the node '" + labelName + "'.");
        return new FakeNode();
    }

    @Override
    public Node addNode(String nodeName) {
        println("'Added' new node called '" + nodeName + "'.");

        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.createNode();
            node.setProperty("name", nodeName);

            tx.success();

            return node;
        }

    }

    @Override
    public Node addNode(String nodeName, String labelName) {
        println("'Added' new node called '" + nodeName + "' with the label '" + labelName + "'.");

        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.createNode(Label.valueOf(labelName.toUpperCase()));
            node.setProperty("name", nodeName);

            tx.success();

            return node;
        }
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest) {
        println("'Added' new relationship (" + nodeNameSrc + ")-->(" + nodeNameDest + ").");
        return new FakeRelationship();
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest, String labelName) {
        RelTypes label = RelTypes.valueOf(labelName);
        println("'Added' new relationship (" + nodeNameSrc + ")-[" + label + "]->(" + nodeNameDest + ").");
        return new FakeRelationship();
    }

    @Override
    public Relationship createRelationship(Node nodeNameSrc, Node nodeNameDest, String labelName, Map<String, String> properties) {
        RelTypes label = RelTypes.valueOf(labelName);
        println("'Added' new relationship (" + nodeNameSrc + ")-[" + label + "]->(" + nodeNameDest + ") with the following properties:");
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            println("\t" + key + ": " + value);

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
        throw new Error("Method not supported");
    }

    @Override
    public void CommitTransaction() {
        throw new Error("Method not supported");
    }

    private void println(String line) {
        //System.out.println(line);
    }
}
