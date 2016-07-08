package Tests;

import Graph.Algorithms.GraphToTxt;
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
        GraphToTxt graphToTxt = new GraphToTxt(graphDb, "grafo.txt");
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute(RelTypes.SUPPORTS);
    }

}
