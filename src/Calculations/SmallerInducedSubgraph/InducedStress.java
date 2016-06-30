package Calculations.SmallerInducedSubgraph;

import Graph.Algorithms.SmallerInducedSubgraph;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class InducedStress {

    public static void main(String[] args) {

        //Prepare databases
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/stress_minimum_induced_graph", true);

        //Prepare algorithm object
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

        //Execute it
        msgi.execute(
                "peano1",
                "dvhlvec",
                "dvhlveclem",
                "txmetcnp",
                "txmetcn",
                "pwuninel",
                "tfr1a",
                "frfnom",
                "ltxrlt",
                "1nn"
        );
    }
}
