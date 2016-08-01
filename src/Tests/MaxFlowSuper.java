package Tests;

import Graph.Algorithms.GraphToHIPRtxt;
import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import Graph.Label;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class MaxFlowSuper {

    public static void main(String[] args) {
        GraphDatabaseService superGraph = GraphFactory.copyGraph("db/metamath", "db/super_metamath-axiom-theorem");

        SuperSinkSuperSource ssss = new SuperSinkSuperSource(superGraph);
        ssss
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute();
        
        GraphToHIPRtxt gtHIPR = new GraphToHIPRtxt(superGraph);
        gtHIPR
                .addFilterLabel(Label.AXIOM)
                .addFilterLabel(Label.THEOREM)
                .execute("grafo_HIPR-axiom-theorem.txt");
    }
}
