package Graph.Algorithms.Decomposition.Evaluators;

import Graph.Label;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 *
 * @author Reuel
 */
public class LabelEvaluator implements Evaluator {

    private final List<Label> labels = new ArrayList<>();
    private final boolean exclusive;

    public LabelEvaluator(Label label) {
        labels.add(label);
        exclusive = true;
    }

    public LabelEvaluator(boolean exclusive, Label... labels) {
        this.labels.addAll(Arrays.asList(labels));
        this.exclusive = exclusive;
    }

    @Override
    public Evaluation evaluate(Path path) {
        Node lastNode = path.endNode();
        boolean include;
        
        if (exclusive) {
            include = true;
            for (Label l : labels) {
                include &= lastNode.hasLabel(l);
            }
        } else {
            include = false;
            for (Label l : labels) {
                include |= lastNode.hasLabel(l);
            }
        }

        if (include) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
