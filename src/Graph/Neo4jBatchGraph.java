/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

/**
 *
 * @author Reuel
 */
public class Neo4jBatchGraph implements IGraph {

    BatchInserter batchInserter;

    public Neo4jBatchGraph(String databasePath) {
        this(new File(databasePath));
    }

    public Neo4jBatchGraph(File databaseFolder) {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("dbms.pagecache.memory", "512m");
            config.put("dbms.string.block.size", "60");
            config.put("dbms.array.block.size", "300");

            batchInserter = BatchInserters.inserter(databaseFolder, config);

            registerShutdownHook(batchInserter);
        } catch (IOException ex) {
            Logger.getLogger(Neo4jBatchGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        return this.addNode(nodeName, Label.UNKNOWN.toString());

    }

    @Override
    public Node addNode(String nodeName, String labelName) {
        println("'Added' new node called '" + nodeName + "' with the label '" + labelName + "'.");

        Map<String, Object> map = new HashMap<>();

        map.put("name", nodeName);
        long createdNodeId = batchInserter.createNode(map, Label.valueOf(labelName.toUpperCase()));
        
        return new FakeNode();
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

    private static void registerShutdownHook(final BatchInserter inserter) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                inserter.shutdown();
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
