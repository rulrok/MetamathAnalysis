package Analysis.FlowAnalysis;

import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.EdgeWeigher.*;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.HalveNodes;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR;
import Utils.HIPRAnalyzeFlowSides;
import Utils.ParseHIPRInputfile;
import Utils.ParseHIPROutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuperHalvedAxiomTheorem {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        System.out.println("Copying original graph...");
        GraphDatabaseService superGraph = GraphFactory.copyGraph("db/metamath", "db/metamath_halved_super-axiom-theorem");

        System.out.println("Removing undesired nodes");
        GraphNodeRemover gnr = new GraphNodeRemover(superGraph);
        gnr = gnr
                .addCustomFilter(n -> n.getProperty("name").toString().startsWith("dummy"))
                .addCustomFilter(n -> n.getProperty("name").toString().matches("ax-7d|ax-8d|ax-9d1|ax-9d2|ax-10d|ax-11d"));
        gnr.execute();

        System.out.println("Removing isolated remaining nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(superGraph);
        isolatedNodes.execute();

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

        String graphOutput = "grafo_HIPR_super_halved_inner1_outer2-axiom-theorem.txt";
        String graphFlowOutput = graphOutput.replace(".txt", "_maxflow.txt");
        String graphFlowSidesOutput = graphOutput.replace(".txt", "_sides.txt");

        GraphToTxt graphToTxt = new GraphToTxt(superGraph);
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .export(graphOutput, new HiprFormatter("S", "T", new InnerOuterEdgeSplittedGraphWeigher(1, 2)));

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(graphOutput, graphFlowOutput);

        System.out.println("Analyzing maxflow sides...");

        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(new File(graphOutput));
        ParseHIPROutput hipr_results_parsed = new ParseHIPROutput(new File(graphFlowOutput));
        HIPRAnalyzeFlowSides.AnalyzeSides(superGraph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));
    }
}
