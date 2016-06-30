package Calculations.SmallerInducedSubgraph;

import Graph.Algorithms.SmallerInducedSubgraph;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class InducedBetweennessAndStress {

    public static void main(String[] args) {

        //Prepare databases
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/betweenness_and_stress_minimum_induced_graph", true);

        //Prepare algorithm object
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

        //Execute it
        msgi.execute(
                //Stress
                "peano1",
                "dvhlvec",
                "dvhlveclem",
                "txmetcnp",
                "txmetcn",
                "pwuninel",
                "tfr1a",
                "frfnom",
                "ltxrlt",
                "1nn",
                //Betweenness
                "fmpt",
                "dvhlvec",
                "dvhlveclem",
                "frfnom",
                "pwuninel",
                "occllem",
                "ltxrlt",
                "occl",
                "1nn",
                "fvex"
        );
    }
}
