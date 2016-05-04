package Tests;

import Graph.Algorithms.SCC.TarjanSCC;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class StrongConnectedComponents {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/metamath");

        Node helperNode;

        try (Transaction tx = graphDb.beginTx()) {

            /* make a new vertex x with edges x->v for all v */
            helperNode = graphDb.createNode();

            ResourceIterator<Node> allAxioms = graphDb.findNodes(Label.AXIOM);

            for (; allAxioms.hasNext();) {
                Node node = allAxioms.next();
                helperNode.createRelationshipTo(node, RelTypes.SUPPORTS);
            }

            /*
             * Calculate SCC
             */
            Graph.Algorithms.Contracts.StrongConnectedComponents scc = new TarjanSCC(graphDb, helperNode, RelTypes.SUPPORTS);
            List<List<Node>> components = scc.execute();

            /*
             * Print the result
             */            
            System.out.println("Total components found: " + components.size());
            components.stream()
                    .filter((component) -> (component.size() > 1))
                    .forEach((component) -> {
                        System.out.printf("Componente com mais de um elemento encontrado. (Tamanho: %d)\n", component.size());
                    });

            tx.failure();
        }
    }
}
