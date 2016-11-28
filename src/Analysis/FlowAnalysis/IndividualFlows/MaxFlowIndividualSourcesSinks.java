package Analysis.FlowAnalysis.IndividualFlows;

import Graph.Algorithms.Decomposition.Evaluators.SinkEvaluator;
import Graph.Algorithms.Decomposition.Evaluators.SourceEvaluator;
import Graph.Algorithms.Export.EdgeWeigher.InnerOuterEdgeSplittedGraphWeigher;
import Graph.Algorithms.Export.Formatters.HiprFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.HalveNodes;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.GraphFactory;
import Graph.Label;
import Utils.HIPR.HIPR;
import Utils.HIPR.HIPRAnalyzeFlowSides;
import Utils.HIPR.ParseHIPRFlowOutput;
import Utils.HIPR.ParseHIPRInputfile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class MaxFlowIndividualSourcesSinks {

    private static final String OUTPUT_NAME = "metamath-nouserboxes_halved_individual-flow_axiom-theorem";

    public static void main(String[] args) throws FileNotFoundException {

//        GraphDatabaseService graph = GraphFactory.makeGraph("db/".concat(OUTPUT_NAME));
        System.out.println("Getting graph...");
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        System.out.println("Removing isolated remaining nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

        System.out.println("Halving nodes...");
        HalveNodes halveNodes = new HalveNodes(graph);
        halveNodes
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();

        List<Node> sources = new ArrayList<>();
        List<Node> sinks = new ArrayList<>();

        System.out.print("Finding all sources...");
        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            graph.traversalDescription()
                    .breadthFirst()
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
                    .evaluator(new SourceEvaluator())
                    .traverse(allNodes)
                    .nodes()
                    .forEach(sources::add);
        }
        System.out.printf("\t %d sources found", sources.size());

        System.out.print("Finding all sinks...");
        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            graph.traversalDescription()
                    .breadthFirst()
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
                    .evaluator(new SinkEvaluator())
                    .traverse(allNodes)
                    .nodes()
                    .forEach(sinks::add);
        }
        System.out.printf("\t %d sinks found", sinks.size());

        System.out.print("Exporting to txt...");
        String graphOutput = OUTPUT_NAME.concat(".txt");

        try (Transaction tx = graph.beginTx()) {
            final InnerOuterEdgeSplittedGraphWeigher edgeWeigher = new InnerOuterEdgeSplittedGraphWeigher(1, 2);
            sources.forEach(n -> {
                edgeWeigher.addSpecificWeigh(n.getProperty("name").toString(), 7000.0);
            });
            sinks.forEach(n -> {
                edgeWeigher.addSpecificWeigh(n.getProperty("name").toString(), 7000.0);
            });

            Node arbitratySource = sources.get(0);
            Node arbitrarySink = sinks.get(0);
            String arbitrarySourceName = arbitratySource.getProperty("name").toString();
            String arbitrarySinkName = arbitrarySink.getProperty("name").toString();
            HiprFormatter hiprFormatter = new HiprFormatter(arbitrarySourceName, arbitrarySinkName, edgeWeigher);
            hiprFormatter
                    .setSuperSourceLabel(arbitratySource.getLabels().iterator().next())
                    .setSuperSinkLabel(arbitrarySink.getLabels().iterator().next());

            GraphToTxt graphToTxt = new GraphToTxt(graph);
            graphToTxt
                    .addFilterLabel(Label.AXIOM)
                    .addFilterLabel(Label.THEOREM)
                    .addFilterLabel(Label.UNKNOWN)
                    .export(graphOutput, hiprFormatter);
        }

        File hiprInputFile = new File(graphOutput);
        ParseHIPRInputfile hiprInput = new ParseHIPRInputfile(hiprInputFile);
        hiprInput.parse();

        System.out.println("Calculating all flows from sources to sinks (slow)...");
        String graphFlowOutput = OUTPUT_NAME.concat("_maxflow.txt");
        String allIndividualFlows = OUTPUT_NAME.concat("_all_maxflows.txt");

        try (Transaction tx = graph.beginTx(); FileWriter fw = new FileWriter(allIndividualFlows)) {

            for (Node source : sources) {
                System.out.println("Processing for source: " + source.getProperty("name"));

                String sourceName = source.getProperty("name").toString().concat("'");
                int sourceId = hiprInput.getNodeId(sourceName);
                for (Node sink : sinks) {
                    String sinkName = sink.getProperty("name").toString().replace("'", "");
                    System.out.println("Processing for sink: " + sinkName);
                    int sinkId = hiprInput.getNodeId(sinkName);

                    try (RandomAccessFile file = new RandomAccessFile(hiprInputFile, "rw")) {
                        int length = file.readLine().length();
                        file.seek(length);
                        file.write(System.lineSeparator().getBytes());
                        file.write(String.format("n %06d s" + System.lineSeparator(), sourceId).getBytes());
                        file.write(String.format("n %06d t" + System.lineSeparator(), sinkId).getBytes());
                    } catch (IOException ex) {
                        Logger.getLogger(MaxFlowIndividualSourcesSinks.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    System.out.println("\tExecuting HIPR " + Date.from(Instant.now()));
                    HIPR.execute(graphOutput, graphFlowOutput);

                    ParseHIPRFlowOutput hiprOutput = new ParseHIPRFlowOutput(new File(graphFlowOutput));
                    hiprOutput.suppressConsoleOutput();
                    hiprOutput.parse();

                    if (hiprOutput.hasFlow()) {
                        //output folder: output/<source-name>/<source-name> <sink-name>_sides.txt
                        String sidesOutput = "output" + File.separator + sourceName + File.separator + sourceName + " " + sinkName + "_sides.txt";
                        System.out.println("\tAnalyzing frontier sides for " + sourceName + " & " + sinkName + " " + Date.from(Instant.now()));
                        HIPRAnalyzeFlowSides.AnalyzeSides(graph, hiprInput, hiprOutput, new File(sidesOutput));
                    }

                    fw.write(String.format("%s -> %s flow: %04.2f" + System.lineSeparator(), sourceName, sinkName, hiprOutput.getMaxFlow()));
                    fw.flush();
                }
            }
            tx.failure();
        } catch (IOException ex) {
            Logger.getLogger(MaxFlowIndividualSourcesSinks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
