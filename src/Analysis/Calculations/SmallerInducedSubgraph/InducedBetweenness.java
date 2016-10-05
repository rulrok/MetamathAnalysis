package Analysis.Calculations.SmallerInducedSubgraph;

import Graph.Algorithms.SmallerInducedSubgraph;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class InducedBetweenness {

    public static void main(String[] args) {

        //Prepare databases
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/betweenness_minimum_induced_graph", true);

        //Prepare algorithm object
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

        //Execute it
        msgi.execute(
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
