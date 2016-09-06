package Analysis;

import Graph.Algorithms.Export.Formatters.*;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ExportToTXT {
    
    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/metamath-theorems-only");

        /*
         * Export to txt
         */
        GraphToTxt graphToTxt = new GraphToTxt(graphDb);
        graphToTxt
                .export("metamath-theorems-only.sif", new SIFFormatter());
    }
    
}
