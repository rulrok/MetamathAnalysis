package Analysis;

import Graph.Algorithms.Export.EdgeWeigher.InnerOuterEdgeSplittedGraphWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Label;
import Utils.HIPR;
import Utils.HIPRAnalyzeFlowSides;
import Utils.ParseHIPRInputfile;
import Utils.ParseHIPROutput;
import java.io.File;
import java.io.FileNotFoundException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class CriticalNodesRemovalAnalysis {

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Copying original graph...");
        GraphDatabaseService graph = Graph.GraphFactory.copyGraph("db/super_halved_metamath", "db/super_halved_metamath-nodes-removal");

        //<editor-fold defaultstate="collapsed" desc="Node names">
        String[] names = new String[]{
            "ax-ltl5",
            "ax-7",
            "ax-6",
            "ax-9",
            "ax-8",
            "readdcl",
            "ax-his4",
            "ax-his1",
            "ax-his2",
            "ax-his3",
            "ax-rep",
            "ax-3",
            "ax-2",
            "ax-5",
            "ax-4",
            "ax-1",
            "evevifev",
            "ax-mulf",
            "ax-dc",
            "ax-un",
            "ax-hvcom",
            "axltadd",
            "ax-hvmulid",
            "cnex",
            "ax-gen",
            "ax-rrecex",
            "ax-hilex",
            "mulass",
            "ax-hfvmul",
            "ax-hfi",
            "trtrst",
            "althalne",
            "ax-hvdistr1",
            "ax-hvdistr2",
            "ax-nul",
            "ax-17",
            "zfpair2",
            "ax-15",
            "ax-16",
            "ax-13",
            "ax-ac",
            "ax-14",
            "ax-11",
            "ax-hvmulass",
            "ax-mulrcl",
            "ax-10",
            "ax-mulcom",
            "nxtor",
            "ax-addf",
            "ax10lem24",
            "ax-cnre",
            "ax-9o",
            "omex",
            "ax-mulcl",
            "nxtand",
            "axsup",
            "ax-addcl",
            "ax-hcompl",
            "ax-9v",
            "ax-hv0cl",
            "ax-hvmul0",
            "ax-i2m1",
            "adddi",
            "ax-hvass",
            "axlttri",
            "axreg",
            "axmulgt0",
            "ax-mp",
            "axlttrn",
            "ax-hvaddid",
            "ax-hfvadd",
            "ax-1rid",
            "ax-11o",
            "ax-resscn",
            "ax-groth",
            "ax-10o",
            "bibox",
            "axcc2",
            "ax-ext",
            "ax-5o",
            "ax-addass",
            "ax-6o",
            "ax-meredith",
            "ax-icn",
            "ax-1ne0",
            "zfinf",
            "ax-rnegex",
            "ax-12o",
            "ax-1cn",
            "ax-pow",
            "ax-sep"
        };
//</editor-fold>

        try (Transaction tx = graph.beginTx()) {
            
            System.out.println("Removing axiom nodes...");
            for (String nodeName : names) {
                Node axiom = graph.findNode(Label.AXIOM, "name", nodeName);
                if (axiom == null) {
                    continue;
                }

                axiom.getRelationships().forEach(r -> {
                    r.delete();
                });

                axiom.delete();

            }

//            System.out.println("Removing theorem nodes...");
//            for (String nodeName : names) {
//                Node theorem = graph.findNode(Label.THEOREM, "name", nodeName);
//                if (theorem == null) {
//                    continue;
//                }
//
//                theorem.getRelationships().forEach(r -> {
//                    r.delete();
//                });
//
//                theorem.delete();
//
//            }
            tx.success();
        }

        String graphHIPR = "grafo_HIPR_super_halved_inner1_outer2-axiom-theorem-critial-axioms-removal.txt";
        String graphFlowOutput = graphHIPR.replace(".txt", "_maxflow.txt");
        String graphFlowSidesOutput = graphHIPR.replace(".txt", "_sides.txt");

        System.out.println("Exporting graph to TXT...");
        GraphToTxt graphToTxt = new GraphToTxt(graph);
        graphToTxt
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .export(graphHIPR, new HiprFormatter("S", "T", new InnerOuterEdgeSplittedGraphWeigher(1, 2)));

        System.out.println("Analyzing maxflow with HIPR...");
        HIPR hipr = new HIPR();
        hipr.execute(graphHIPR, graphFlowOutput);

        System.out.println("Analyzing maxflow sides...");

        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(new File(graphHIPR));
        ParseHIPROutput hipr_results_parsed = new ParseHIPROutput(new File(graphFlowOutput));

        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hipr_parsed, hipr_results_parsed, new File(graphFlowSidesOutput));
    }

}
