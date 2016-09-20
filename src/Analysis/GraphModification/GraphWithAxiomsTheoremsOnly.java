package Analysis.GraphModification;

import Graph.Algorithms.GraphNodeRemover;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class GraphWithAxiomsTheoremsOnly {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.copyGraph("db/metamath", "db/metamath-axioms-theorems-only");

        System.out.println("Removing all nodes but axioms and theorems...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .execute();
    }
}
