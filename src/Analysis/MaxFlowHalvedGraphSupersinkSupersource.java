package Analysis;

import Graph.Algorithms.GraphToHIPRtxt;
import Graph.Algorithms.HalveNodes;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowHalvedGraphSupersinkSupersource {

    public static void main(String[] args) {

        GraphDatabaseService superGraph = GraphFactory.copyGraph("db/metamath", "db/super_halved_metamath");

        HalveNodes halveNodes = new HalveNodes(superGraph);
        halveNodes
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();

        SuperSinkSuperSource sinkSuperSource = new SuperSinkSuperSource(superGraph);
        sinkSuperSource
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();

        GraphToHIPRtxt gtHIPR = new GraphToHIPRtxt(superGraph);
        gtHIPR
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute("grafo_HIPR.txt");
    }
}
