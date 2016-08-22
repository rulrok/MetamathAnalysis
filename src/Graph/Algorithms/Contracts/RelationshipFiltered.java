package Graph.Algorithms.Contracts;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Reuel
 */
public interface RelationshipFiltered {

    public LabelFiltered addFilterRelationship(RelationshipType label);

}
