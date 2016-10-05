package Analysis.GraphModification;

import Graph.Algorithms.Export.Formatters.SIFFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import java.util.Arrays;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class RemoveSinkSide {

    private final static String OUTPUT = "metamath_source_sink_disconnected";

    public static void main(String[] args) {

        System.out.println("Copying original graph...");
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.DEFAULT_METAMATH_DB, "db/".concat(OUTPUT));

        System.out.println("Removing all but axiom and theorem nodes...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .execute();

        //Remove isolated nodes
        System.out.println("Removing isolated nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

        System.out.println("Creating super source and sink...");
        SuperSinkSuperSource ssss = new SuperSinkSuperSource(graph);
        ssss
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        List<String> frontierNodesNames = Arrays.asList(
                "a1i", "a2i", "a4i", "addass", "addcl", "adddi", "alequcom", "alim", "ax9v", "cnex", "cnre", "dfbi1gb", "dfcleq", "e0_", "gen2", "hbn1", "id1", "idi", "iin1", "iin2", "iin3", "impbox", "impxt", "merlem1", "mp2b", "mpg", "mulass", "mulcl", "mulcom", "re1ax2", "readdcl", "remulcl", "tbw-ax2", "wel", "weq", "wsb"
        );

        int[] iarr = new int[]{0};
        try (Transaction tx = graph.beginTx()) {

            GlobalGraphOperations
                    .at(graph)
                    .getAllNodes()
                    .forEach(node -> {
                        String nodeName = node.getProperty("name").toString();

                        if (frontierNodesNames.contains(nodeName)) {
                            System.out.printf("Node %s found (%d)\n", nodeName, ++iarr[0]);

                            node.getRelationships(Direction.OUTGOING).forEach(Relationship::delete);
                        }
                    });

            tx.success();
        }

        GraphNodeRemover gnr1 = new GraphNodeRemover(graph);
        gnr1.addFilterLabel(Label.UNKNOWN).execute();

        System.out.println("Creating super source and sink...");
        ssss
                .setSuperSourceLabel(Label.UNKNOWN)
                .setSuperSinkLabel(Label.UNKNOWN)
                .execute();

        GraphToTxt graphToTxt = new GraphToTxt(graph);
        graphToTxt
                .export(OUTPUT.concat(".sif"), new SIFFormatter());
    }
}
