package Analysis.FlowAnalysis.HalvedNodes;

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
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuperAllHalvedTheoremOnly {

    private static final String OUTPUT_NAME = "metamath-nouserboxes_super-all_halved-i1-o2_theorem-only";

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Copying original graph...");
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.NOUSERBOX_METAMATH_DB, "db/".concat(OUTPUT_NAME));

        System.out.println("Removing all nodes but theorem ones...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr = gnr
                //DFS from ax-meredith to remove undesired componente
                .addComponentHeadDFS("ax-meredith")
                //Add other labels
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                //Specific nodes
                .addCustomFilter(n -> n.getProperty("name").toString().startsWith("dummy"));
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
                //Define all original copies as connected by the super source
                .removeDefaultSourceEvaluator()
                .addCustomSourceEvaluator((Path path) -> {
                    Node endNode = path.endNode();
                    String endNodeName = endNode.getProperty("name").toString();
                    if (endNodeName.endsWith("'")) {
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    } else {
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    }
                })
                //Define all second copies as connecting to the super sink
                .removeDefaultSinkEvaluator()
                .addCustomSinkEvaluator((Path path) -> {
                    Node endNode = path.endNode();
                    String endNodeName = endNode.getProperty("name").toString();
                    if (endNodeName.endsWith("'")) {
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    } else {
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    }
                })
                .addFilterLabel(Label.THEOREM)
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        System.out.println("Exporting to TXT...");

        String graphOutput = OUTPUT_NAME.concat(".txt");
        String graphFlowOutput = OUTPUT_NAME.concat("_maxflow.txt");
        String graphFlowSidesOutput = OUTPUT_NAME.concat("_sides.txt");

        final InnerOuterEdgeSplittedGraphWeigher edgeWeigher = new InnerOuterEdgeSplittedGraphWeigher(1, 2);
//        edgeWeigher.addSpecificWeigh("merlem1", 0.0);
        HiprFormatter hiprFormatter = new HiprFormatter("S", "T", edgeWeigher);
        hiprFormatter.setSuperSourceLabel(Label.UNKNOWN).setSuperSinkLabel(Label.UNKNOWN);

        GraphToTxt graphToTxt = new GraphToTxt(graph);
        graphToTxt
                .addFilterLabel(Label.THEOREM)
                .addFilterLabel(Label.UNKNOWN)
                .export(graphOutput, hiprFormatter);

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR.execute(graphOutput, graphFlowOutput);

        System.out.println("Analyzing maxflow sides...");

        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(new File(graphOutput));
        ParseHIPRFlowOutput hipr_results_parsed = new ParseHIPRFlowOutput(new File(graphFlowOutput));
        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));
    }
}
