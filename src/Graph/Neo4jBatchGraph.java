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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

/**
 *
 * @author Reuel
 */
public class Neo4jBatchGraph implements IGraph {

    BatchInserter batchInserter;
    Map<String, Long> nodesIds;

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
            nodesIds = new TreeMap<>();

            registerShutdownHook(batchInserter);
        } catch (IOException ex) {
            Logger.getLogger(Neo4jBatchGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Node addProperty(String nodeName, String key, String value) {

        Long foundNode = nodesIds.get(nodeName);

        if (foundNode != null) {
            println("Added a new property called '" + key + "' with value '" + value + "' to the node '" + nodeName + "'.");
            batchInserter.setNodeProperty(foundNode, key, value);
        }
        return new FakeNode();
    }

    @Override
    public Node addLabel(String nodeName, String labelName) {

        Long foundNode = nodesIds.get(nodeName);

        if (foundNode != null) {
            println("Added a new label called '" + labelName + "' to the node '" + labelName + "'.");
            batchInserter.setNodeLabels(foundNode, Label.valueOf(labelName.toUpperCase()));
        }
        return new FakeNode();
    }

    @Override
    public Node addNode(String nodeName) {

        return this.addNode(nodeName, Label.UNKNOWN.toString());

    }

    @Override
    public Node addNode(String nodeName, String labelName) {
        println("Added new node called '" + nodeName + "' with the label '" + labelName + "'.");

        Map<String, Object> map = new HashMap<>();

        map.put("name", nodeName);
        long createdNodeId = batchInserter.createNode(map, Label.valueOf(labelName.toUpperCase()));

        nodesIds.put(nodeName, createdNodeId);

        return new FakeNode();
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest) {
        return this.createRelationship(nodeNameSrc, nodeNameDest, RelTypes.UNKNOWN.toString());
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest, String labelName) {
        return this.createRelationship(nodeNameSrc, nodeNameDest, labelName, null);
    }

    @Override
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest, String labelName, Map<String, String> properties) {
        RelTypes label = RelTypes.valueOf(labelName);
        println("Added new relationship (" + nodeNameSrc + ")-[" + label + "]->(" + nodeNameDest + ") with the following properties:");

        Long srcNodeId = nodesIds.getOrDefault(nodeNameSrc, 2L);  //) 2 happens to be the dummylink $p assertion
        Long destNodeId = nodesIds.getOrDefault(nodeNameDest, 2L);

        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                println("\t" + key + ": " + value);

            }
            Map<String, Object> convertedProperties = new TreeMap<>(properties);

            batchInserter.createRelationship(srcNodeId, destNodeId, label, convertedProperties);
        } else {
            batchInserter.createRelationship(srcNodeId, destNodeId, label, null);
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
