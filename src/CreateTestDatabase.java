
import Graph.Algorithms.*;
import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Label;
import Graph.RelTypes;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.*;
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
            Node aux2 = graphTest.createNode(Label.UNKNOWN);
            aux2.setProperty("Name", "AUX2");

            e.createRelationshipTo(d, RelTypes.SUPPORTS);
            d.createRelationshipTo(b, RelTypes.SUPPORTS);
            b.createRelationshipTo(a, RelTypes.SUPPORTS);
            b.createRelationshipTo(c, RelTypes.SUPPORTS);
            e.createRelationshipTo(g, RelTypes.SUPPORTS);
            aux.createRelationshipTo(g, RelTypes.SUPPORTS);
            aux2.createRelationshipTo(d, RelTypes.SUPPORTS);
            g.createRelationshipTo(f, RelTypes.SUPPORTS);
            g.createRelationshipTo(i, RelTypes.SUPPORTS);
            i.createRelationshipTo(h, RelTypes.SUPPORTS);
            i.createRelationshipTo(j, RelTypes.SUPPORTS);

            beginTx.success();

//            TraversalDescription traversalDescription = graphTest
//                    .traversalDescription()
//                    .breadthFirst()
//                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
//                    .evaluator(new SourceEvaluator());
//
//            System.out.println("Normal order:");
//            traversalDescription.traverse(e, aux, aux2).forEach((path) -> {
////                System.out.println(path);
//                System.out.println(path.nodes());
////                System.out.print(path.startNode());
////                System.out.print("->");
////                System.out.println(path.endNode());
//                System.out.println("-----------------");
//            });
//
//            System.out.println("--------------------------");
//
//            System.out.println("\nReverse graph:");
//            traversalDescription.reverse().traverse(j).forEach((path) -> {
//                System.out.println(path.endNode().getProperties("Name"));
//            });
        }

        GraphDecomposition gd = new TraverserGraphDecomposition(graphTest);
        List<Node> sinkInitialNodes = new LinkedList<>();

        try (Transaction tx = graphTest.beginTx()) {
            Node e = graphTest.findNode(Label.UNKNOWN, "Name", "E");
            Node aux = graphTest.findNode(Label.UNKNOWN, "Name", "AUX");

            sinkInitialNodes.add(e);
            sinkInitialNodes.add(aux);
        }

        try (Transaction tx = graphTest.beginTx()) {
            List<List<Node>> sinkComponents = gd.execute(DecompositionTarget.SINK, sinkInitialNodes);

            sinkComponents.stream().forEach((List<Node> nodeList) -> {
                System.out.println("Sink component:");
                nodeList.forEach(node -> {
                    Node realNode = graphTest.getNodeById(node.getId());
                    System.out.println("\t" + realNode.getProperty("Name"));
                });
            });
        } catch (Exception ex) {
            Logger.getLogger(CreateTestDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Node> sourceInitialNodes = new LinkedList<>();

        try (Transaction tx = graphTest.beginTx()) {
            Node e = graphTest.findNode(Label.UNKNOWN, "Name", "E");
            Node aux = graphTest.findNode(Label.UNKNOWN, "Name", "AUX");
            Node aux2 = graphTest.findNode(Label.UNKNOWN, "Name", "AUX2");

            sourceInitialNodes.add(e);
            sourceInitialNodes.add(aux);
            sourceInitialNodes.add(aux2);
        }

        try (Transaction tx = graphTest.beginTx()) {
            List<List<Node>> sourceComponents = gd.execute(DecompositionTarget.SOURCE, sourceInitialNodes);

            sourceComponents.stream().forEach((List<Node> nodeList) -> {
                System.out.println("Source component:");
                nodeList.forEach(node -> {
                    Node realNode = graphTest.getNodeById(node.getId());
                    System.out.println("\t" + realNode.getProperty("Name"));
                });
            });
        } catch (Exception ex) {
            Logger.getLogger(CreateTestDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
