package Graph.Algorithms.Export.Formatters;

import Graph.Algorithms.Export.UniqueSequenceGenerator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class Simple implements IGraphFormatter {

    private final UniqueSequenceGenerator idGenerator;

    public Simple() {
        this.idGenerator = new UniqueSequenceGenerator();
    }

    @Override
    public CharSequence format(GraphDatabaseService graph, List<Node> nodesToPrint, List<Relationship> relsToPrint) {
        StringBuilder output = new StringBuilder();
        Map<Long, Set<Long>> orderedRelsToPrint = new HashMap<>();
        try (final Transaction tx = graph.beginTx()) {
            relsToPrint.stream().forEach((Relationship r) -> {
                Node startNode = r.getStartNode();
                Node endNode = r.getEndNode();

                long k = idGenerator.uniqueSequencialId(startNode.getId());
                long v = idGenerator.uniqueSequencialId(endNode.getId());

                if (!orderedRelsToPrint.containsKey(k)) {
                    orderedRelsToPrint.put(k, new LinkedHashSet<>());
                }

                orderedRelsToPrint.get(k).add(v);
            });

            output.append(nodesToPrint.size()).append(System.lineSeparator());
            orderedRelsToPrint.forEach((Long s, Set<Long> nodes) -> {
                nodes.forEach((Long e) -> {
                    output.append(s).append("\t").append(e).append(System.lineSeparator());
                });
            });
            tx.failure();
        }

        return output;
    }

}
