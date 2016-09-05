package Tests.Traversals;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

/**
 *
 * @author Reuel
 */
public class PrintTraversal {

    public static void printResults(TraversalDescription traversalDescription, Node... initialNode) {

        System.out.println("Order:");
        Traverser traverse = traversalDescription.traverse(initialNode);

        System.out.println("Nodes: ");
        ResourceIterable<Node> nodes = traverse.nodes();
        for (Node node : nodes) {
            System.out.print(node.getProperty("name") + " | ");
        }

        System.out.println("\nRelationships: ");
        ResourceIterable<Relationship> relationships = traverse.relationships();
        for (Relationship r : relationships) {
            System.out.print(r.getStartNode().getProperty("name"));
            System.out.print("->");
            System.out.print(r.getEndNode().getProperty("name"));
            System.out.println("");
        }

        System.out.println("\nGraph levels: ");
        int actualLevel = 0;
        for (Path p : traverse) {
            Node endNode = p.endNode();

            if (p.length() > actualLevel) {
                actualLevel = p.length();
                System.out.println("");
            }
            System.out.print(endNode.getProperty("name") + " | ");
        }
    }
}
