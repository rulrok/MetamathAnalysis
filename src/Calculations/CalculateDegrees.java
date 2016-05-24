package Calculations;

import Graph.Algorithms.DegreeDistribution;
import Graph.GraphFactory;
import Plot.Gnuplot;
import Plot.PlotDataSet;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class CalculateDegrees {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeDefaultMetamathGraph();

        /*
         * Calculate the distributions
         */
        DegreeDistribution distribution = new DegreeDistribution(graphDb);
        distribution.calculate();
        Map<Integer, Integer> innerDegrees = distribution.getInnerDegrees();
        Map<Integer, Integer> outterDegrees = distribution.getOutterDegrees();
        Map<Integer, Integer> allDegrees = distribution.getAllDegrees();

        /*
         * Prepare data
         */
        PlotDataSet dataSet = new PlotDataSet("Degree distribution");

        double[] innerX = innerDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] innerY = innerDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Inner degrees", innerX, innerY);

        double[] outterX = outterDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] outterY = outterDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Outter degrees", outterX, outterY);

        double[] allX = allDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] allY = allDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("All degrees", allX, allY);

        String xLabel = "Number of Links(k)";
        String yLabel = "Number of nodes with k Links";
        new Gnuplot(dataSet)
                .setFilename("grafo_degrees.png")
                .setxLabel(xLabel)
                .setyLabel(yLabel)
                .setyRange(0, 1000)
                .setxRange(0, 1000)
//                .setyLogScale()
                .plot();
    }
}
