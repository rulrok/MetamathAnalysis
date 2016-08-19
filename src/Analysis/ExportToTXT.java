package Analysis;

import Graph.Algorithms.Export.EGraphFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ExportToTXT {

    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeDefaultMetamathGraph();

        /*
         * Export to txt
         */
        GraphToTxt graphToTxt = new GraphToTxt(graphDb, "grafo_test.txt");
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute(RelTypes.SUPPORTS, EGraphFormatter.NAMES);
    }

}
