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
        final double[] indegree_array = inner.stream().mapToDouble(i -> i).toArray();

        DescriptiveStatistics innerDS = new DescriptiveStatistics(indegree_array);
        final double[] outdegree_array = outer.stream().mapToDouble(i -> i).toArray();
        DescriptiveStatistics outerDS = new DescriptiveStatistics(outdegree_array);
        final double[] degree_array = all.stream().mapToDouble(i -> i).toArray();
        DescriptiveStatistics allDS = new DescriptiveStatistics(degree_array);

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

        try (FileWriter fw = new FileWriter("node_degrees.csv")) {

            String ls = System.lineSeparator();

            fw.write("degree;indegree;outdegree" + ls);

            for (int i = 0; i < degree_array.length; i++) {

                final String string = String.format("%d;%d;%d", (int) degree_array[i], (int) indegree_array[i], (int) outdegree_array[i]);

                fw.write(string);
                fw.write(ls);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExportDegrees.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
