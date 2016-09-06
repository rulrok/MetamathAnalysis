package Graph.Algorithms;

import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.Algorithms.Decomposition.Evaluators.LabelEvaluator;
import Graph.Algorithms.Decomposition.Evaluators.SinkEvaluator;
import Graph.Algorithms.Decomposition.Evaluators.SourceEvaluator;
import Graph.Label;
import Graph.RelType;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * Adds a super sink (S) and super source (T) to a graph. It changes the graph
 * permanently.
 *
 * @author Reuel
 */
public class SuperSinkSuperSource implements LabelFiltered {

    private final GraphDatabaseService graph;
    private final List<Label> labelFilters = new ArrayList<>();

    private Label SuperSourceLabel = Label.AXIOM;
    private Label SuperSinkLabel = Label.THEOREM;

    public SuperSinkSuperSource(GraphDatabaseService graph) {
        this.graph = graph;
    }

    @Override
    public SuperSinkSuperSource addFilterLabel(Label label) {
        labelFilters.add(label);
        return this;
    }

    public SuperSinkSuperSource setSuperSourceLabel(Label SuperSourceLabel) {
        this.SuperSourceLabel = SuperSourceLabel;
        return this;
    }

    public SuperSinkSuperSource setSuperSinkLabel(Label SuperSinkLabel) {
        this.SuperSinkLabel = SuperSinkLabel;
        return this;
    }

    public void execute() {
        try (Transaction tx = graph.beginTx()) {

            //Create artificial supersink and supersource nodes
            TraversalDescription sourceTraverser = graph.traversalDescription()
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .evaluator(new SourceEvaluator());

            if (!labelFilters.isEmpty()) {
                Label[] labels = new Label[labelFilters.size()];
                sourceTraverser = sourceTraverser.evaluator(new LabelEvaluator(false, labelFilters.toArray(labels)));
            }

            Node S = graph.createNode(SuperSourceLabel);
            S.setProperty("name", "S");
            sourceTraverser.traverse(GlobalGraphOperations.at(graph).getAllNodes()).nodes().forEach(source -> {
                S.createRelationshipTo(source, RelType.UNKNOWN);
            });

            Node T = graph.createNode(SuperSinkLabel);
            T.setProperty("name", "T");
            TraversalDescription sinkTraverser = graph.traversalDescription()
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .evaluator(new SinkEvaluator());

            if (!labelFilters.isEmpty()) {
                Label[] labels = new Label[labelFilters.size()];
                sinkTraverser = sinkTraverser.evaluator(new LabelEvaluator(false, labelFilters.toArray(labels)));
            }

            sinkTraverser.traverse(GlobalGraphOperations.at(graph).getAllNodes()).nodes().forEach(sink -> {
                sink.createRelationshipTo(T, RelType.UNKNOWN);
            });

            tx.success();
        }
    }
}
