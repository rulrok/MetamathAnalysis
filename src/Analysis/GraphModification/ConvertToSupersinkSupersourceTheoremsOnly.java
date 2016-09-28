package Analysis.GraphModification;

import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ConvertToSupersinkSupersourceTheoremsOnly {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.DEFAULT_METAMATH_DB, "db/metamath_super_theorems");

        GraphNodeRemover gnr = new GraphNodeRemover(graph);

        gnr
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                //                .addCustomFilter(n -> {
                //                    return n.getProperty("name").toString().startsWith("ax");
                //                })
                .execute();

        SuperSinkSuperSource ssss = new SuperSinkSuperSource(graph);
        ssss
                .setSuperSourceLabel(Label.AXIOM)
                .setSuperSinkLabel(Label.THEOREM)
                .execute();
    }

}
