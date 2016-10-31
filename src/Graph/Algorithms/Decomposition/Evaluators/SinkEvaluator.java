package Graph.Algorithms.Decomposition.Evaluators;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 *
 * @author Reuel
 */
public class SinkEvaluator implements Evaluator {

    @Override
    public Evaluation evaluate(Path path) {
        Node lastNode = path.endNode();
        if (lastNode.getDegree(Direction.OUTGOING) == 0) {
            //We reached a final node
            if (lastNode.getDegree(Direction.INCOMING) > 0) {
                //The node has someone linking to it
                return Evaluation.INCLUDE_AND_CONTINUE;
            }
            //The node is an isolate node
            return Evaluation.EXCLUDE_AND_CONTINUE;
        }
        //The node has outgoing edges
        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
