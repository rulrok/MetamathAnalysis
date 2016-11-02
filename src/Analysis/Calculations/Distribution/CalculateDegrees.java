package Analysis.Calculations.Distribution;

import Graph.Algorithms.DegreeDistribution;
import Graph.GraphFactory;
import Plot.Gnuplot;
import Plot.PlotDataSet;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class CalculateDegrees {

    public static void main(String[] args) {

        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        /*
         * Calculate the distributions
         */
        System.out.println("Calculating distribution...");
        DegreeDistribution distribution = new DegreeDistribution(graph);
        distribution.calculate();
        Map<Integer, Integer> innerDegreesHistogram = distribution.getInnerDegrees();
        Map<Integer, Integer> outerDegreesHistogram = distribution.getOuterDegrees();
        Map<Integer, Integer> allDegreesHistogram = distribution.getAllDegrees();

        /*
         * Prepare data
         */
        System.out.println("Plotting data...");
        PlotDataSet dataSet = new PlotDataSet("Degree distribution");

        double[] innerX = innerDegreesHistogram.keySet().stream().mapToDouble(i -> i).toArray();
        double[] innerY = innerDegreesHistogram.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Inner degrees", innerX, innerY);

        double[] outerX = outerDegreesHistogram.keySet().stream().mapToDouble(i -> i).toArray();
        double[] outerY = outerDegreesHistogram.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Outer degrees", outerX, outerY);

        double[] allX = allDegreesHistogram.keySet().stream().mapToDouble(i -> i).toArray();
        double[] allY = allDegreesHistogram.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("All degrees", allX, allY);

        new Gnuplot(dataSet)
                .setFilename("grafo_degrees.png")
                .setxLabel("Number of Links(k)")
                .setyLabel("Number of nodes with k Links")
                //                .setyRange(0, 1000)
                //                .setxRange(0, 1000)
                .setyLogScale()
                .plot();
    }
}
