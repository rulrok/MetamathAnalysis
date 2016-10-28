package Analysis.Calculations.Distribution.Reachability;

import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.RelType;
import Utils.ExportMapToTXT;
import Utils.HistogramUtils;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class ReachabilityFromEverybodyToEveryone {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            Map<String, Integer> calculate = reachabilityFromSource.calculate(allNodes.iterator(), RelType.SUPPORTS);

            final String OUTPUT = "reach_distribution_everyone_to_everybody";

            ExportMapToTXT.export(OUTPUT, calculate, new String[]{"id", "name", "count"});

            Map<Integer, Integer> histogram = HistogramUtils.CreateHistogramFromMapBasedOn(calculate);

            ExportMapToTXT.export(OUTPUT.concat("_histogram"), histogram, new String[]{"reach", "count"}
            );

            tx.failure();
        }

    }

}
