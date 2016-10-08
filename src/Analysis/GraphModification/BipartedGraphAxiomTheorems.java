package Analysis.GraphModification;

import Graph.Algorithms.BipartGraph;
import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class BipartedGraphAxiomTheorems {

    public static void main(String[] args) {
        System.out.println("Copying original graph...");
        GraphDatabaseService inputGraph = GraphFactory.copyGraph(GraphFactory.NOUSERBOX_METAMATH_DB, "db/metamath-nouserboxes-axiom-theorem");

        System.out.println("Removing all but axiom and theorem nodes...");
        GraphNodeRemover gnr = new GraphNodeRemover(inputGraph);
        gnr
                .addCustomFilter(n -> {
                    return n.getProperty("name").toString().startsWith("dummy");
                })
                .addComponentHeadDFS("ax-meredith")
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .execute();

        System.out.println("Removing isolated nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(inputGraph);
        isolatedNodes.execute();

        System.out.println("Biparting the graph...");
        BipartGraph bg = new BipartGraph(inputGraph);
        GraphDatabaseService graph = bg.execute("db/biparted-graph-axiom-theorem-nomeredith");

    }

}
