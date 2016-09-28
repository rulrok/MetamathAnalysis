package Graph.Algorithms.Export.EdgeWeigher;

import org.neo4j.graphdb.Relationship;

/**
 * Evaluates an edge and attributes it a weight based on its local properties.
 * It must be used within a transaction.
 *
 * @author Reuel
 */
@FunctionalInterface
public interface IEdgeWeigher {

    double weigh(Relationship rel);
}
