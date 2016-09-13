package Graph.Algorithms.Decomposition.Evaluators;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 *
 * @author Reuel
 */
public class IsolatedNodeEvaluator implements Evaluator {

    @Override
    public Evaluation evaluate(Path path) {

        Node lastNode = path.endNode();

        if (lastNode.getDegree() == 0) {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
