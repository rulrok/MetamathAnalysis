package Analysis.FlowAnalysis;

import Graph.Algorithms.Export.EdgeWeigher.InnerOuterEdgeSplittedGraphWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR.HIPR;
import Utils.HIPR.HIPRAnalyzeFlowSides;
import Utils.HIPR.ParseHIPRInputfile;
import Utils.HIPR.ParseHIPRFlowOutput;
import java.io.File;
import java.io.FileNotFoundException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class CriticalNodesRemovalAnalysis {

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Copying original graph...");
        GraphDatabaseService graph = Graph.GraphFactory.copyGraph(GraphFactory.NOUSERBOX_METAMATH_DB, "db/super_halved_metamath-critical-nodes-removal");

        System.out.println("Creating super sink and super source...");
        SuperSinkSuperSource sinkSuperSource = new SuperSinkSuperSource(graph);
        sinkSuperSource
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        //<editor-fold defaultstate="collapsed" desc="Node names">
        String[] names = new String[]{
            "a1i", "a2i", "a4i", "addass", "addcl", "adddi", "alequcom", "alim", "cnex", "cnre", "dfcleq", "gen2", "hbn1", "id1", "idi", "merlem1", "mp2b", "mpg", "mulass", "mulcl", "mulcom", "readdcl", "remulcl", "tbw-ax2", "wel", "weq", "wsb"

        };
//</editor-fold>

        try (Transaction tx = graph.beginTx()) {

//            System.out.println("Removing axiom nodes...");
//            for (String nodeName : names) {
//                Node axiom = graph.findNode(Label.AXIOM, "name", nodeName);
//                if (axiom == null) {
//                    continue;
//                }
//
//                axiom.getRelationships().forEach(Relationship::delete);
//
//            }
            System.out.println("Removing theorem nodes...");
            for (String nodeName : names) {
                Node theorem = graph.findNode(Label.THEOREM, "name", nodeName);
                if (theorem == null) {
                    continue;
                }

                theorem.getRelationships(Direction.OUTGOING).forEach(Relationship::delete);

            }
            tx.success();
        }

        System.out.println("Exporting graph to TXT...");

        String graphHIPR = "metamath-nouserboxes_super_halved_inner1_outer2-axiom-theorem-critial-nodes-removal.txt";
        String graphFlowOutput = graphHIPR.replace(".txt", "_maxflow.txt");
        String graphFlowSidesOutput = graphHIPR.replace(".txt", "_sides.txt");

        GraphToTxt graphToTxt = new GraphToTxt(graph);
        final HiprFormatter hiprFormatter = new HiprFormatter("S", "T", new InnerOuterEdgeSplittedGraphWeigher(1, 2));
        hiprFormatter.setSuperSinkLabel(Label.UNKNOWN).setSuperSourceLabel(Label.UNKNOWN);
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .addFilterLabel(Label.UNKNOWN)
                .export(graphHIPR, hiprFormatter);

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(graphHIPR, graphFlowOutput);

        System.out.println("Analyzing maxflow sides...");

        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(new File(graphHIPR));
        ParseHIPRFlowOutput hipr_results_parsed = new ParseHIPRFlowOutput(new File(graphFlowOutput));

        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));
    }

}
