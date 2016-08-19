package Graph.Algorithms.Export.Formatters;

import java.util.Set;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Reuel
 */
public interface IGraphFormatter {

    CharSequence format(GraphDatabaseService graph, Set<Node> nodesToPrint, Set<Relationship> relsToPrint);
}
