package Graph.Algorithms.Export.Formatters;

import Graph.Algorithms.Export.UniqueSequenceGenerator;
import Graph.Algorithms.GraphToHIPRtxt;
import Graph.Label;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 *
 * @author Reuel
 */
public class HiprFormatter implements IGraphFormatter {

    UniqueSequenceGenerator usg;

    private final String SourceNodeName;
    private final String SinkNodeName;

    private boolean superSinkSourceCustomWeight;

    public HiprFormatter(String sourceNodeName, String sinkNodeName) {
        SourceNodeName = sourceNodeName;
        SinkNodeName = sinkNodeName;

        usg = new UniqueSequenceGenerator();
    }

    /**
     * The arcs coming out of a SuperSource will have the weight set as the
     * number of the outter degree of the nodes that they each reach. The same
     * occurs for the SuperSink, but the weight of the arcs are being defined
     * based on the inner degree of the nodes that reach the SuperSink.
     *
     * @return
     */
    public HiprFormatter withCustomWeightForSuperSinkAndSource() {
        superSinkSourceCustomWeight = true;

        return this;
    }

    @Override
    public CharSequence format(GraphDatabaseService graph, List<Node> nodesToPrint, List<Relationship> relsToPrint) {
        StringBuilder output = new StringBuilder();

        try (final Transaction tx = graph.beginTx()) {

            //Headers
            output.append("p max ").append(nodesToPrint.size()).append(' ').append(relsToPrint.size()).append(System.lineSeparator());
            output.append("n ").append("<s_id>").append(" s").append(System.lineSeparator());
            output.append("n ").append("<t_id>").append(" t").append(System.lineSeparator());

            //Arcs
            int weight;
            for (Relationship rel : relsToPrint) {

                Node startNode = rel.getStartNode();
                Node endNode = rel.getEndNode();

                long startNodeId = usg.uniqueSequencialId(startNode.getId());
                long endNodeId = usg.uniqueSequencialId(endNode.getId());

                Object startNodeName = startNode.getProperty("name");
                Object endNodeName = endNode.getProperty("name");

                weight = 1;
                if (superSinkSourceCustomWeight) {
                    if (startNodeName.equals("S")) {
                        //SuperSource
                        weight = endNode.getDegree(Direction.OUTGOING);
                    }

                    if (endNodeName.equals("T")) {
                        //SuperSink
                        weight = startNode.getDegree(Direction.INCOMING);
                    }
                }

                output.append("a ").append(startNodeId).append(' ').append(endNodeId).append(" ").append(weight)
                        //The below line is an extra only to make it easier to read the output
                        .append(" ( ").append(startNodeName).append(" -> ").append(endNodeName).append(" )")
                        .append(System.lineSeparator());
            }

            Node S = graph.findNode(Label.AXIOM, "name", SourceNodeName);
            Node T = graph.findNode(Label.THEOREM, "name", SinkNodeName);
            long S_id = usg.uniqueSequencialId(S.getId());
            long T_id = usg.uniqueSequencialId(T.getId());

            int s_index = output.indexOf("<s_id>");
            output.replace(s_index, s_index + Long.toString((S_id)).length() + 1, Long.toString(S_id));

            int t_index = output.indexOf("<t_id>");
            output.replace(t_index, t_index + Long.toString((T_id)).length() + 1, Long.toString(T_id));

            tx.failure();
        }
        return output;
    }

}
