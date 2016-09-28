package Analysis.GraphModification;

import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class SuperGraphWithTheoremsOnly {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.DEFAULT_METAMATH_DB, "db/metamath-super-theorems-only");

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

        System.out.println("Removing isolated nodes...");
        RemoveIsolatedNodes rin = new RemoveIsolatedNodes(graph);
        rin.execute();

        System.out.println("Creating super source and sink...");
        SuperSinkSuperSource ssss = new SuperSinkSuperSource(graph);
        ssss.
                setSuperSinkLabel(Label.THEOREM)
                .setSuperSourceLabel(Label.THEOREM)
                .execute();
    }
}
