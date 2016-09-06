package Graph.Algorithms.Export.Formatters;

import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class SIFFormatter implements IGraphFormatter {

    @Override
    public CharSequence format(GraphDatabaseService graph, List<Node> nodesToPrint, List<Relationship> relsToPrint) {

        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        try (Transaction tx = graph.beginTx()) {
            relsToPrint.forEach(rel -> {
                Node startNode = rel.getStartNode();
                Node endNode = rel.getEndNode();
                sb
                        .append(startNode.getProperty("name"))
                        .append("\t")
                        .append(rel.getType().name())
                        .append("\t")
                        .append(endNode.getProperty("name"))
                        .append(lineSeparator);
            });
        }

        return sb;
    }

}
