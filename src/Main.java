import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author Reuel
 */
public class Main {
 
    public static void main(String[] args) {
        String DB_PATH = "";
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        
        
    }
}
