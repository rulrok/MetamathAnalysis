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
        GraphDatabaseService graphDb = GraphFactory.makeDefaultMetamathGraph();

        /*
         * Export to txt
         */
        GraphToTxt graphToTxt = new GraphToTxt(graphDb);
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .export("grafo_HIPR_metamath-axiom-theorem.txt", new HiprFormatter("S", "T", new SuperSinkSourceCustomWeigher()));
    }

}
