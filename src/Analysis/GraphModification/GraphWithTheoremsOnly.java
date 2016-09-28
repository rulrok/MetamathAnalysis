package Analysis.GraphModification;

import Graph.Algorithms.GraphNodeRemover;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class GraphWithTheoremsOnly {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.DEFAULT_METAMATH_DB, "db/metamath-theorems-only");

        System.out.println("Removing all nodes but theorems...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr.addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .execute();
    }
}
