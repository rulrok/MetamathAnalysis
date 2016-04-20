
import Graph.RelTypes;
import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/**
 *
 * @author Reuel
 */
public class CreateTestDatabase {

    public static void main(String[] args) {
        File testPath = new File("db/test");
        GraphDatabaseService graphTest = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(testPath)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        try (Transaction beginTx = graphTest.beginTx()) {
            Node a = graphTest.createNode();
            Node b = graphTest.createNode();
            Node c = graphTest.createNode();
            Node d = graphTest.createNode();
            Node e = graphTest.createNode();
            Node f = graphTest.createNode();
            Node g = graphTest.createNode();
            Node h = graphTest.createNode();
            Node i = graphTest.createNode();
            Node j = graphTest.createNode();

            e.createRelationshipTo(d, RelTypes.SUPPORTS);
            d.createRelationshipTo(b, RelTypes.SUPPORTS);
            b.createRelationshipTo(a, RelTypes.SUPPORTS);
            b.createRelationshipTo(c, RelTypes.SUPPORTS);
            e.createRelationshipTo(g, RelTypes.SUPPORTS);
            g.createRelationshipTo(f, RelTypes.SUPPORTS);
            g.createRelationshipTo(i, RelTypes.SUPPORTS);
            i.createRelationshipTo(h, RelTypes.SUPPORTS);
            i.createRelationshipTo(j, RelTypes.SUPPORTS);

            beginTx.success();
        }
    }
}
