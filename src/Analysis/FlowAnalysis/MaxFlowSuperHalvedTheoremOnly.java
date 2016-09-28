package Analysis.FlowAnalysis;

import Graph.Algorithms.Export.EdgeWeigher.*;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR;
import Utils.HIPRAnalyzeFlowSides;
import Utils.ParseHIPRInputfile;
import Utils.ParseHIPROutput;
import java.io.File;
import java.io.FileNotFoundException;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuperHalvedTheoremOnly {

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Copying original graph...");
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.NOUSERBOX_METAMATH_DB, "db/metamath-nouserboxes_halved_super-theorem-only");

        System.out.println("Removing all nodes but theorem ones...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr = gnr
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE);
        gnr.execute();

        System.out.println("Creating super sink and super source...");
        SuperSinkSuperSource sinkSuperSource = new SuperSinkSuperSource(graph);
        sinkSuperSource
                .addFilterLabel(Label.THEOREM)
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        System.out.println("Exporting to TXT...");

        String graphOutput = "metamath-nouserboxes_super_halved_inner1_outer2-theorem-only.txt";
        String graphFlowOutput = graphOutput.replace(".txt", "_maxflow.txt");
        String graphFlowSidesOutput = graphOutput.replace(".txt", "_sides.txt");

        HiprFormatter hiprFormatter = new HiprFormatter("S", "T", new InnerOuterEdgeSplittedGraphWeigher(1, 2));
        hiprFormatter = hiprFormatter.setSuperSourceLabel(Label.UNKNOWN).setSuperSinkLabel(Label.UNKNOWN);

        GraphToTxt graphToTxt = new GraphToTxt(graph);
        graphToTxt
                .addFilterLabel(Label.THEOREM)
                .addFilterLabel(Label.UNKNOWN)
                .export(graphOutput, hiprFormatter);

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(graphOutput, graphFlowOutput);

        System.out.println("Analyzing maxflow sides...");

        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(new File(graphOutput));
        ParseHIPROutput hipr_results_parsed = new ParseHIPROutput(new File(graphFlowOutput));
        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));
    }
}
