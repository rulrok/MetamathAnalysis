package Analysis.TransitiveReduction;

import Graph.GraphFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.TransitiveReduction;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class JGraphT {

    static void exportToTxT(DirectedGraph<Long, DefaultEdge> g, Map<Long, String> vertexNames, String outputname) {
        try (FileWriter fw = new FileWriter(outputname)) {

            final String lineSeparator = System.lineSeparator();

            Set<Long> vertexSet = g.vertexSet();
            fw.write("#Vertex set" + lineSeparator);
            for (Long l : vertexSet) {
                String name = vertexNames.get(l);
                fw.write(String.format("%d %s%s", l, name, lineSeparator));
            }

            Set<DefaultEdge> edgeSet = g.edgeSet();
            fw.write("#Edges:" + lineSeparator);
            for (DefaultEdge e : edgeSet) {
                Long u = g.getEdgeSource(e);
                Long v = g.getEdgeTarget(e);

                fw.write(String.format("%d -> %d%s", u, v, lineSeparator));
            }

        } catch (IOException ex) {
            Logger.getLogger(JGraphT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        DirectedGraph<Long, DefaultEdge> g = new DirectedAcyclicGraph<>(DefaultEdge.class);

        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        Map<Long, String> vertexNames = new HashMap<>(18000);

        System.out.println("Converting graph...");
        try (Transaction tx = graph.beginTx()) {
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            for (Node n : allNodes) {
                long id = n.getId();
                String name = n.getProperty("name").toString();

                g.addVertex(id);
                vertexNames.put(id, name);
            }

            Iterable<Relationship> allRelationships = GlobalGraphOperations.at(graph).getAllRelationships();

            for (Relationship r : allRelationships) {
                long startId = r.getStartNode().getId();
                long endId = r.getEndNode().getId();

                g.addEdge(startId, endId);
            }
        }

        TransitiveReduction transitiveReduction = TransitiveReduction.INSTANCE;

        exportToTxT(g, vertexNames, "graph_original.txt");

        System.out.println("Going to reduce...");
        transitiveReduction.reduce(g);
        System.out.println("Reduced...");
        
        exportToTxT(g, vertexNames, "graph_reduced.txt");

    }
}
