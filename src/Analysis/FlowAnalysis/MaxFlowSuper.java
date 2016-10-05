package Analysis.FlowAnalysis;

import Graph.Algorithms.Export.EdgeWeigher.SuperSinkSourceCustomWeigher;
import Graph.Algorithms.Export.EdgeWeigher.UnitaryWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR.HIPR;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuper {

    public static void main(String[] args) {
        System.out.println("Copying original graph...");
        GraphDatabaseService superGraph = GraphFactory.copyGraph(GraphFactory.DEFAULT_METAMATH_DB, "db/metamath_super-axiom-theorem");

        System.out.println("Creating super sink and super source...");
        SuperSinkSuperSource ssss = new SuperSinkSuperSource(superGraph);
        ssss
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();

        GraphToTxt exportToTXT = new GraphToTxt(superGraph);
        exportToTXT = exportToTXT
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM);
        
        
        System.out.println("Exporting to TXT (1)...");
        String output1 = "grafo_HIPR_super-axiom-theorem.txt";
        exportToTXT
                .export(output1, new HiprFormatter("S", "T", new UnitaryWeigher()));

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(output1, output1.replace(".txt", "_maxflow.txt"));

        System.out.println("Exporting to TXT (2)...");
        String output2 = "grafo_HIPR_super_custom_weights-axiom-theorem.txt";
        exportToTXT
                .export(output2, new HiprFormatter("S", "T", new SuperSinkSourceCustomWeigher()));

        System.out.println("Analyzing maxflow with HIPR...");
        hipr.execute(output2, output2.replace(".txt", "_maxflow.txt"));
    }
}
