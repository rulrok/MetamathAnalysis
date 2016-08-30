package Graph.Algorithms.Export.EdgeWeigher;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * For splitted in half graphs, this weigher allows to return a custom weight
 * when the edge connects two splitted nodes, or another weight otherwise.
 *
 * Example:
 *
 * The edge node --> node2 is considered an outer egde because it connects two
 * different nodes.
 *
 * The edge node2 --> node2' is considered an inner edge because it connects two
 * nodes which are the same, but spliited in half.
 *
 * @author Reuel
 */
public class InnerOuterEdgeSplittedGraph implements IEdgeWeigher {

    private final int innerWeight;
    private final int outerWeight;

    public InnerOuterEdgeSplittedGraph(int innerWeight, int outerWeight) {
        this.innerWeight = innerWeight;
        this.outerWeight = outerWeight;
    }

    @Override
    public int weigh(Relationship rel) {
        Node startNode = rel.getStartNode();
        Node endNode = rel.getEndNode();

        String startName = startNode.getProperty("name").toString();
        String endName = endNode.getProperty("name").toString();

        //If the startName is equal to endName when we append it with an apostrophe,
        //then we have an inner edge.
        boolean isInnerEdge = startName.concat("'").equals(endName);

        if (isInnerEdge) {
            return innerWeight;
        }

        return outerWeight;
    }

}
