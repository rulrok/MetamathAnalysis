package Graph.Algorithms.Export.EdgeWeigher;

import org.neo4j.graphdb.Relationship;

/**
 * The most simple edge weigher possible. All edges have an unitary weight
 * regardless of their kind.
 *
 * @author Reuel
 */
public class UnitaryWeigher implements IEdgeWeigher {

    @Override
    public int weigh(Relationship rel) {
        return 1;
    }

}
