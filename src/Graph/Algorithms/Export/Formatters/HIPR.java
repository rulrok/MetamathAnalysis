package Graph.Algorithms.Export.Formatters;

import Graph.Algorithms.Export.UniqueSequenceGenerator;
import Graph.Label;
import java.util.Set;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 *
 * @author Reuel
 */
public class HIPR implements IGraphFormatter {

    UniqueSequenceGenerator usg;

    public HIPR() {
        usg = new UniqueSequenceGenerator();
    }

    @Override
    public CharSequence format(GraphDatabaseService graph, Set<Node> nodesToPrint, Set<Relationship> relsToPrint) {
        StringBuilder output = new StringBuilder();

        try (final Transaction tx = graph.beginTx()) {

            Node S = graph.findNode(Label.AXIOM, "name", "S");
            Node T = graph.findNode(Label.THEOREM, "name", "T");

            //Headers
            output.append("p max ").append(nodesToPrint.size()).append(' ').append(relsToPrint.size()).append(System.lineSeparator());
            output.append("n ").append(usg.uniqueSequencialId(S.getId())).append(" s").append(System.lineSeparator());
            output.append("n ").append(usg.uniqueSequencialId(T.getId())).append(" t").append(System.lineSeparator());

            //Arcs
            relsToPrint.forEach(rel -> {
                Node startNode = rel.getStartNode();
                Node endNode = rel.getEndNode();

                long startNodeId = usg.uniqueSequencialId(startNode.getId());
                long endNodeId = usg.uniqueSequencialId(endNode.getId());

                Object startNodeName = startNode.getProperty("name");
                Object endNodeName = endNode.getProperty("name");

                output.append("a ").append(startNodeId).append(' ').append(endNodeId).append(" 1 ")
                        //The below line is an extra only to make it easier to interpret the output
                        .append("( ").append(startNodeName).append(" -> ").append(endNodeName).append(" )")
                        .append(System.lineSeparator());
            });
            tx.failure();
        }
        return output;
    }

}
