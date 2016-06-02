package Graph.Algorithms.SCC;

import java.util.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GabowSCC extends AbstractStrongConnectedComponentsAlgorithm {

    public GabowSCC(GraphDatabaseService graph, Node initialNode) {
        super(graph, initialNode);
    }

    public GabowSCC(GraphDatabaseService graph, Node initialNode, RelationshipType... relationshipTypes) {
        super(graph, initialNode, relationshipTypes);
    }

    Stack<Node> S;
    Stack<Node> P;

    Set<Long> assignedNodes;
    Map<Long, Integer> preorderNumber;
    int C;

    @Override
    public void configure() {
        components = new LinkedList<>();
        assignedNodes = new HashSet<>();

        S = new Stack<>();
        P = new Stack<>();
        preorderNumber = new HashMap<>();
        C = 0;
    }

    @Override
    public List<List<Node>> execute() {

        try (Transaction tx = graph.beginTx()) {

            visit(initialNode);

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            allNodes.forEach((node) -> {
                if (!assignedNodes.contains(node.getId())) {
                    visit(node);
                }
            });
            
            tx.failure();
        }
        return components;
    }

    private void visit(Node v) {

        //Set the preorder number of v to C, and increment C.
        preorderNumber.put(v.getId(), C);
        C++;

        //Push v onto S and also onto P.
        P.push(v);
        S.push(v);

        //For each edge from v to a neighboring vertex w:
        Iterable<Relationship> relationships;
        relationships = (relationshipTypes != null) ? v.getRelationships(relationshipTypes) : v.getRelationships();
        for (Relationship v_w : relationships) {
            Node w = v_w.getEndNode();
            //If the preorder number of w has not yet been assignedNodes, recursively search w;
            if (!preorderNumber.containsKey(w.getId())) {
                visit(w);
            }//Otherwise, if w has not yet been assignedNodes to a strongly connected component:
            else if (!assignedNodes.contains(w.getId())) {

                //Repeatedly pop vertices from P 
                //until the top element of P has a preorder number less than or equal to the preorder number of w.
                while (!P.empty() && preorderNumber.get(P.peek().getId()) > preorderNumber.get(w.getId())) {
                    P.pop();
                }
            }
        }
        //If v is the top element of P:
        if (v.getId() == P.peek().getId()) {
            List<Node> component = new LinkedList<>();

            Node s;
            //Pop vertices from S until v has been popped, and assign the popped vertices to a new component.
            do {
                s = S.pop();
                assignedNodes.add(s.getId());
                component.add(s);

            } while (s.getId() != v.getId());
            //Pop v from P.
            P.pop();

            components.add(component);
        }
    }
}
