package Graph.Algorithms;

import Graph.Algorithms.Contracts.StrongConnectedComponents;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Reuel
 */
public abstract class AbstractStrongConnectedComponentsAlgorithm implements StrongConnectedComponents {

    protected GraphDatabaseService graph;
    protected Node initialNode;
    protected RelationshipType[] relationshipTypes;

    public AbstractStrongConnectedComponentsAlgorithm(GraphDatabaseService graph, Node initialNode) {
        this.graph = graph;
        this.initialNode = initialNode;

        configure();
    }

    public AbstractStrongConnectedComponentsAlgorithm(GraphDatabaseService graph, Node initialNode, RelationshipType... relationshipTypes) {
        this.graph = graph;
        this.initialNode = initialNode;
        this.relationshipTypes = relationshipTypes;

        configure();
    }

    public abstract void configure();
}
