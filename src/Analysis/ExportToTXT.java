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

    private final static String OUTPUT = "metamath-axioms-theorems-nouserbox-nojunk";

    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        /*
         * Export to txt
         */
        GraphToTxt graphToTxt = new GraphToTxt(graphDb);
        graphToTxt
                .export(OUTPUT.concat(".sif"), new SIFFormatter());
    }

}
