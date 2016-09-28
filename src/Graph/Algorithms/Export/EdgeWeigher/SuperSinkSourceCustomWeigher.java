package Graph.Algorithms.Export.EdgeWeigher;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * The arcs coming out of a SuperSource will have the weight set as the number
 * of the outer degree of the nodes that they each reach. The same occurs for
 * the SuperSink, but the weight of the arcs are being defined based on the
 * inner degree of the nodes that reach the SuperSink. For all other edges, it
 * returns 1.
 *
 * @author Reuel
 */
public class SuperSinkSourceCustomWeigher implements IEdgeWeigher {

    @Override
    public double weigh(Relationship rel) {
        Node startNode = rel.getStartNode();
        Node endNode = rel.getEndNode();

        Object startNodeName = startNode.getProperty("name");
        Object endNodeName = endNode.getProperty("name");

        if (startNodeName.equals("S")) {
            //SuperSource
            return endNode.getDegree(Direction.OUTGOING);
        }

        if (endNodeName.equals("T")) {
            //SuperSink
            return startNode.getDegree(Direction.INCOMING);
        }

        return 1;
    }

}
