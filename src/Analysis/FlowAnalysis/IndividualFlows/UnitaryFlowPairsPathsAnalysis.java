package Analysis.FlowAnalysis.IndividualFlows;

import Graph.GraphFactory;
import Graph.RelType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.collection.MapUtil;

/**
 *
 * @author reuel
 */
public class UnitaryFlowPairsPathsAnalysis {

    public static void main(String[] args) throws IOException {
        final java.nio.file.Path inputFilePath = Paths.get("metamath-nouserboxes_halved_individual-flow_axiom-theorem_all_maxflows.txt");

        List<String> allLines = Files.readAllLines(inputFilePath);
//        List<String> allLines = new ArrayList<>();
//        allLines.add("A -> F flow: 1.0");

        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
//        GraphDatabaseService graph = GraphFactory.makeTestGraphF();
        for (String line : allLines) {
            String[] values = line.split(" ");

            double flow = Double.parseDouble(values[values.length - 1]);

            if (flow != 1) {
                continue;
            }

            String originName = values[0];
            String destingName = values[2];

            try (Transaction tx = graph.beginTx()) {

                Node startNode = (Node) graph.execute("MATCH (n {name:{nodeName}}) return n;", MapUtil.map("nodeName", originName.replace("'", ""))).next().get("n");
                Node endNode = (Node) graph.execute("MATCH (n {name: {nodeName} }) return n;", MapUtil.map("nodeName", destingName)).next().get("n");

                Traverser traverse = graph
                        .traversalDescription()
                        .breadthFirst()
                        .uniqueness(Uniqueness.RELATIONSHIP_PATH)
                        .evaluator(Evaluators.returnWhereEndNodeIs(endNode))
//                        .evaluator(Evaluators.includingDepths(1, 10))
                        .relationships(RelType.SUPPORTS, Direction.OUTGOING)
                        .traverse(startNode);

                System.out.println("Paths for " + startNode + " -> " + endNode);

                for (Path p : traverse) {
                    System.out.println(p);
                }

                tx.failure();
            }

        };
    }

}
