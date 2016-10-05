package Analysis.FlowAnalysis;

import Graph.Algorithms.Export.EdgeWeigher.InnerOuterEdgeSplittedGraphWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.HalveNodes;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR.HIPR;
import Utils.HIPR.HIPRAnalyzeFlowSides;
import Utils.HIPR.ParseHIPRFlowOutput;
import Utils.HIPR.ParseHIPRInputfile;
import java.io.File;
import java.io.FileNotFoundException;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuperHalvedTheoremOnlyStrict {

    private static final String OUTPUT_NAME = "metamath_halved_super-theorem-strict";

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Copying original graph...");
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.NOUSERBOX_METAMATH_DB, "db/".concat(OUTPUT_NAME));

        System.out.println("Removing undesired nodes...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr = gnr
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .addCustomFilter(n -> n.getProperty("name").toString().startsWith("dummy"))
                .addCustomFilter(n -> n.getProperty("name").toString().startsWith("ax"))
                .addCustomFilter(n -> n.getProperty("name").toString().matches("ax-7d|ax-8d|ax-9d1|ax-9d2|ax-10d|ax-11d"));
        gnr.execute();

        System.out.println("Removing isolated remaining nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

        System.out.println("Halving nodes...");
        HalveNodes halveNodes = new HalveNodes(graph);
        halveNodes
                .addFilterLabel(Label.THEOREM)
                .execute();

        System.out.println("Creating super sink and super source...");
        SuperSinkSuperSource sinkSuperSource = new SuperSinkSuperSource(graph);
        sinkSuperSource
                .addFilterLabel(Label.THEOREM)
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        System.out.println("Exporting to TXT...");

        String graphOutput = OUTPUT_NAME.concat(".txt");
        String graphFlowOutput = OUTPUT_NAME.concat("_maxflow.txt");
        String graphFlowSidesOutput = OUTPUT_NAME.concat("_sides.txt");

        GraphToTxt graphToTxt = new GraphToTxt(graph);
        HiprFormatter hiprFormatter = new HiprFormatter("S", "T", new InnerOuterEdgeSplittedGraphWeigher(1, 2));
        hiprFormatter = hiprFormatter.setSuperSourceLabel(Label.UNKNOWN).setSuperSinkLabel(Label.UNKNOWN);

        graphToTxt
                .addFilterLabel(Label.THEOREM)
                .addFilterLabel(Label.UNKNOWN)
                .export(graphOutput, hiprFormatter);

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(graphOutput, graphFlowOutput);

        System.out.println("Analyzing maxflow sides...");

        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(new File(graphOutput));
        ParseHIPRFlowOutput hipr_results_parsed = new ParseHIPRFlowOutput(new File(graphFlowOutput));
        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));
    }
}
