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
public class AxiomEvaluator implements Evaluator {

    @Override
    public Evaluation evaluate(Path path) {
        Node lastNode = path.endNode();
        if (lastNode.hasLabel(Label.AXIOM)) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
