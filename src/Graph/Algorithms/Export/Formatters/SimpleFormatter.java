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
public class SimpleFormatter implements IGraphFormatter {

    private final UniqueSequenceGenerator idGenerator;
    private boolean withNames;

    public SimpleFormatter() {
        this.idGenerator = new UniqueSequenceGenerator();
    }

    public SimpleFormatter withNames() {
        withNames = true;
        return this;
    }

    public SimpleFormatter withoutNames() {
        withNames = false;
        return this;
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
            if (withNames) {
                orderedRelsToPrint.forEach((Long s, Set<Long> nodes) -> {
                    nodes.forEach((Long e) -> {
                        Node startNode = graph.getNodeById(s);
                        Node endNode = graph.getNodeById(e);
                        output.append(s).append("\t").append(e).append("\t")
                                .append("[ ")
                                .append(startNode.getProperty("name")).append(" -> ").append(endNode.getProperty("name"))
                                .append(" ]")
                                .append(System.lineSeparator());
                    });
                });
            } else {

                orderedRelsToPrint.forEach((Long s, Set<Long> nodes) -> {
                    nodes.forEach((Long e) -> {
                        output.append(s).append("\t").append(e).append(System.lineSeparator());
                    });
                });
            }
            tx.failure();
        }

        return output;
    }

}
