package Graph.Algorithms.Decomposition.Evaluators;

import Graph.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 *
 * @author Reuel
 */
public class LabelEvaluator implements Evaluator {

    private final Label label;

    public LabelEvaluator(Label label) {
        this.label = label;
    }

    @Override
    public Evaluation evaluate(Path path) {
        Node lastNode = path.endNode();
        if (lastNode.hasLabel(label)) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
