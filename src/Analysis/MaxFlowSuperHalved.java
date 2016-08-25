package Analysis;

import Graph.Algorithms.Export.EdgeWeigher.SuperSinkSourceCustomWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.HalveNodes;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuperHalved {

    public static void main(String[] args) {

        System.out.println("Copying original graph...");
        GraphDatabaseService superGraph = GraphFactory.copyGraph("db/metamath", "db/super_halved_metamath");

        System.out.println("Halving nodes...");
        HalveNodes halveNodes = new HalveNodes(superGraph);
        halveNodes
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();

        System.out.println("Creating super sink and super source...");
        SuperSinkSuperSource sinkSuperSource = new SuperSinkSuperSource(superGraph);
        sinkSuperSource
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();

        System.out.println("Exporting to TXT...");
        String graphTxtOutput = "grafo_HIPR_super_halved-axiom-theorem.txt";
        GraphToTxt graphToTxt = new GraphToTxt(superGraph, graphTxtOutput);
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute(new HiprFormatter("S", "T", new SuperSinkSourceCustomWeigher()));

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(graphTxtOutput, graphTxtOutput.replace(".txt", "_maxflow.txt"));
    }
}
