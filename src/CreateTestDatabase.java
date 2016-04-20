
import Graph.RelTypes;
import java.io.File;
import java.io.IOException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
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

            beginTx.success();
        }
    }
}
