package Analysis.FlowAnalysis;

import Graph.Algorithms.Export.EdgeWeigher.IEdgeWeigher;
import Graph.Algorithms.Export.EdgeWeigher.UnitaryWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.GraphNodeRemover;
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
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;

/**
 *
 * @author Reuel
 */
public class MaxFlowBipartedAxiomTheorems {

    private static final String OUTPUT_NAME = "biparted-graph-super-axiom-theorem-nomeredith";

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Copying original graph...");
        GraphDatabaseService graph = GraphFactory.copyGraph("db/biparted-graph-axiom-theorem-nomeredith", "db/".concat(OUTPUT_NAME));

        System.out.println("Removing isolated nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

        System.out.println("Creating super sink and source...");
        SuperSinkSuperSource ssss = new SuperSinkSuperSource(graph);
        ssss
                .removeDefaultSourceEvaluator()
                .addCustomSourceEvaluator((Path e) -> {
                    Node endNode = e.endNode();
                    String nodeName = endNode.getProperty("name").toString();

                    if (!nodeName.endsWith("'") && !nodeName.equals("S")) {
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }

                    return Evaluation.EXCLUDE_AND_CONTINUE;
                })
                .removeDefaultSinkEvaluator()
                .addCustomSinkEvaluator(e -> {
                    Node endNode = e.endNode();
                    String nodeName = endNode.getProperty("name").toString();

                    if (nodeName.endsWith("'")) {
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }

                    return Evaluation.EXCLUDE_AND_CONTINUE;
                })
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        System.out.println("Exporting to TXT...");

        String graphOutput = OUTPUT_NAME.concat(".txt");
        String graphFlowOutput = OUTPUT_NAME.concat("_maxflow.txt");
        String graphFlowSidesOutput = OUTPUT_NAME.concat("_sides.txt");

        final IEdgeWeigher edgeWeigher = new UnitaryWeigher();
        HiprFormatter hiprFormatter = new HiprFormatter("S", "T", edgeWeigher);
        hiprFormatter.setSuperSourceLabel(Label.UNKNOWN).setSuperSinkLabel(Label.UNKNOWN);

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
        ParseHIPRFlowOutput hipr_results_parsed = new ParseHIPRFlowOutput(new File(graphFlowOutput));
        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));

    }
}
