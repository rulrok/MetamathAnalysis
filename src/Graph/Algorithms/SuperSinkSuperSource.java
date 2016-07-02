package Graph.Algorithms;

import Graph.Algorithms.Decomposition.Evaluators.SinkEvaluator;
import Graph.Algorithms.Decomposition.Evaluators.SourceEvaluator;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

/**
 * Adds a super sink (S) and super source (T) to a graph.
 * It changes the graph permanently.
 * 
 * @author Reuel
 */
public class SuperSinkSuperSource {

    private final GraphDatabaseService graph;

    public SuperSinkSuperSource(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public void execute() {
        try (Transaction tx = graph.beginTx()) {

            //Create artificial supersink and supersource nodes
            TraversalDescription sourceTraverser = graph.traversalDescription()
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .evaluator(new SourceEvaluator());

            Node s = graph.createNode(Label.UNKNOWN);
            s.setProperty("name", "S");
            sourceTraverser.traverse(graph.getNodeById(0)).nodes().forEach(source -> {
                s.createRelationshipTo(source, RelTypes.UNKNOWN);
            });

            Node t = graph.createNode(Label.UNKNOWN);
            t.setProperty("name", "T");
            TraversalDescription sinkTraverser = graph.traversalDescription()
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .evaluator(new SinkEvaluator());
            sinkTraverser.traverse(graph.getNodeById(0)).nodes().forEach(sink -> {
                sink.createRelationshipTo(t, RelTypes.UNKNOWN);
            });

            tx.success();
        }
    }
}
