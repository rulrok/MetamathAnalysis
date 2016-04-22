
import Graph.Algorithms.*;
import Graph.*;
import Graph.Label;
import Graph.RelTypes;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.io.fs.FileUtils;

/**
 *
 * @author Reuel
 */
public class CreateTestDatabase {

    public static void main(String[] args) throws IOException {
        File testPath = new File("db/test");
        FileUtils.deleteRecursively(testPath);
        GraphDatabaseService graphTest = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(testPath)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        try (Transaction beginTx = graphTest.beginTx()) {
            Node a = graphTest.createNode();
            a.setProperty("Name", "A");
            Node b = graphTest.createNode();
            b.setProperty("Name", "B");
            Node c = graphTest.createNode();
            c.setProperty("Name", "C");
            Node d = graphTest.createNode();
            d.setProperty("Name", "D");
            Node e = graphTest.createNode(Label.UNKNOWN);
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
            Node aux = graphTest.createNode(Label.UNKNOWN);
            aux.setProperty("Name", "AUX");

            e.createRelationshipTo(d, RelTypes.SUPPORTS);
            d.createRelationshipTo(b, RelTypes.SUPPORTS);
            b.createRelationshipTo(a, RelTypes.SUPPORTS);
            b.createRelationshipTo(c, RelTypes.SUPPORTS);
            e.createRelationshipTo(g, RelTypes.SUPPORTS);
            aux.createRelationshipTo(g, RelTypes.SUPPORTS);
            g.createRelationshipTo(f, RelTypes.SUPPORTS);
            g.createRelationshipTo(i, RelTypes.SUPPORTS);
            i.createRelationshipTo(h, RelTypes.SUPPORTS);
            i.createRelationshipTo(j, RelTypes.SUPPORTS);

            beginTx.success();

//            TraversalDescription traversalDescription = graphTest
//                    .traversalDescription()
//                    .breadthFirst()
//                    .order(BranchOrderingPolicies.POSTORDER_BREADTH_FIRST)
//                    .evaluator(new SinkEvaluator());
//
//            System.out.println("Normal order:");
//            traversalDescription.traverse(e).forEach((path) -> {
//                System.out.println(path.endNode().getProperties("Name"));
//            });
//
//            System.out.println("--------------------------");
//
//            System.out.println("\nReverse graph:");
//            traversalDescription.reverse().traverse(j).forEach((path) -> {
//                System.out.println(path.endNode().getProperties("Name"));
//            });
        }

        GraphDecomposition gd = new GraphDecomposition(graphTest);
        List<Node> sinkInitialNodes = new LinkedList<>();

        try (Transaction tx = graphTest.beginTx()) {
            Node e = graphTest.findNode(Label.UNKNOWN, "Name", "E");
            Node aux = graphTest.findNode(Label.UNKNOWN, "Name", "AUX");

            sinkInitialNodes.add(e);
            sinkInitialNodes.add(aux);
        }
        List<List<Node>> components = gd.execute(DecompositionTarget.SINK, sinkInitialNodes);

        try (Transaction tx = graphTest.beginTx()) {

            components.stream().forEach((List<Node> nodeList) -> {
                System.out.println("Sink component:");
                nodeList.forEach(node -> {
                    Node realNode = graphTest.getNodeById(node.getId());
                    System.out.println("\t" + realNode.getProperty("Name"));
                });
            });
        }
    }
}
