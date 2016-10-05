package Analysis;

import Graph.Algorithms.Export.Formatters.SIFFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ExportToTXT {

    private final static String OUTPUT = "metamath-theorems-only";

    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/".concat(OUTPUT));

        /*
         * Export to txt
         */
        GraphToTxt graphToTxt = new GraphToTxt(graphDb);
        graphToTxt
                .export(OUTPUT.concat(".sif"), new SIFFormatter());
    }

}
