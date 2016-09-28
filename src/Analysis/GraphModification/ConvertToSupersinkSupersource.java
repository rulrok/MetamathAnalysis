package Analysis.GraphModification;

import Graph.Algorithms.SuperSinkSuperSource;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ConvertToSupersinkSupersource {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.DEFAULT_METAMATH_DB, "db/metamath_super");

        SuperSinkSuperSource ssss = new SuperSinkSuperSource(graph);
        ssss.execute();
    }

}
