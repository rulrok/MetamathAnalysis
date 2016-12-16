package Analysis.Calculations.SmallerInducedSubgraph;

import Graph.Algorithms.SmallerInducedSubgraph;
import Graph.GraphFactory;
import Graph.Label;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author reuel
 */
public class InducedProof2p2e4 {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/induced_2p2pe4", true);

        List<String> proofElements = new ArrayList<>();

        try (Transaction tx = graph.beginTx()) {
            final String p2e4 = "2p2e4";
            proofElements.add(p2e4);
            
            Node t_2503lem2 = graph.findNode(Label.THEOREM, "name", p2e4);

            t_2503lem2.getRelationships(Direction.INCOMING).forEach(r -> {
                Node proofElement = r.getStartNode();
                String proofElementName = proofElement.getProperty("name").toString();

                proofElements.add(proofElementName);
            });
        }

        //Prepare algorithm object
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

        String[] names = new String[proofElements.size()];
        proofElements.toArray(names);

        //Execute it
        msgi.execute(names);
    }
}
