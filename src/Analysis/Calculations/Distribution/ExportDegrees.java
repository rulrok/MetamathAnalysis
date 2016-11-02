package Analysis.Calculations.Distribution;

import Graph.GraphFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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

        DescriptiveStatistics innerDS = new DescriptiveStatistics(inner.stream().mapToDouble(i -> i).toArray());
        DescriptiveStatistics outerDS = new DescriptiveStatistics(outer.stream().mapToDouble(i -> i).toArray());
        DescriptiveStatistics allDS = new DescriptiveStatistics(all.stream().mapToDouble(i -> i).toArray());

        double all_standardDeviation = allDS.getStandardDeviation();
        double all_mean = allDS.getMean();
        System.out.printf("all   : μ = %f; σ = %f\n", all_mean, all_standardDeviation);
        System.out.println(allDS);

        double inner_standardDeviation = innerDS.getStandardDeviation();
        double inner_mean = innerDS.getMean();
        System.out.printf("inner : μ = %f; σ = %f\n", inner_mean, inner_standardDeviation);
        System.out.println(innerDS);

        double outer_standardDeviation = outerDS.getStandardDeviation();
        double outer_mean = outerDS.getMean();
        System.out.printf("outer : μ = %f; σ = %f\n", outer_mean, outer_standardDeviation);
        System.out.println(outerDS);

        try (FileWriter fw = new FileWriter("node_degrees.dat")) {

            String ls = System.lineSeparator();
            fw.write("#all" + ls);
            for (Integer integer : all) {
                fw.write(Integer.toString(integer) + ls);
            }
            fw.write(ls + ls + "#inner" + ls);
            for (Integer integer : inner) {
                fw.write(Integer.toString(integer) + ls);
            }
            fw.write(ls + ls + "#outer" + ls);
            for (Integer integer : outer) {
                fw.write(Integer.toString(integer) + ls);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExportDegrees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
