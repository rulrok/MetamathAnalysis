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
public class GMLFormatter implements IGraphFormatter {

    @Override
    public CharSequence format(GraphDatabaseService graph, List<Node> nodesToPrint, List<Relationship> relsToPrint) {

        StringBuilder sb = new StringBuilder();

        String lineSeparator = System.lineSeparator();

        try (Transaction tx = graph.beginTx()) {
            nodesToPrint.forEach((Node node) -> {
                sb.append("node [")
                        .append(" id ").append(node.getId())
                        .append(" label ").append(node.getProperty("name"))
                        .append(" ]")
                        .append(lineSeparator);
            });

            relsToPrint.forEach((Relationship r) -> {
                sb.append("edge [")
                        .append(" source ").append(r.getStartNode().getId())
                        .append(" target ").append(r.getEndNode().getId())
                        .append(" ]")
                        .append(lineSeparator);
            });
        }

        return sb;
    }

}
