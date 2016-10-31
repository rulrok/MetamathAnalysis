package Analysis.Calculations.Distribution;

import Graph.GraphFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author reuel
 */
public class ExportDegrees {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        List<Integer> all = new ArrayList<>();
        List<Integer> inner = new ArrayList<>();
        List<Integer> outer = new ArrayList<>();

        try (Transaction tx = graph.beginTx();) {
            ResourceIterator<Node> iterator = GlobalGraphOperations.at(graph).getAllNodes().iterator();

            for (; iterator.hasNext();) {
                Node node = iterator.next();
                int inner_degree = node.getDegree(Direction.INCOMING);
                int outer_degree = node.getDegree(Direction.OUTGOING);

                inner.add(inner_degree);
                outer.add(outer_degree);
                all.add(inner_degree + outer_degree);
            }

        }

        try (FileWriter fw = new FileWriter("node_degrees.dat")) {

            String ls = System.lineSeparator();
            fw.write("#all" + ls);
            for (Integer integer : all) {
                fw.write(Integer.toString(integer) + ls);
            }
            fw.write(ls.concat("#inner").concat(ls));
            for (Integer integer : inner) {
                fw.write(Integer.toString(integer) + ls);
            }
            fw.write(ls.concat("#outer").concat(ls));
            for (Integer integer : outer) {
                fw.write(Integer.toString(integer) + ls);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExportDegrees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
