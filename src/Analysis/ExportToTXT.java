package Analysis;

import Graph.Algorithms.Export.EdgeWeigher.SuperSinkSourceCustomWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ExportToTXT {

    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/super_metamath");

        /*
         * Export to txt
         */
        GraphToTxt graphToTxt = new GraphToTxt(graphDb, "grafo_HIPR_test.txt");
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute(new HiprFormatter("S", "T", new SuperSinkSourceCustomWeigher()));
    }

}
