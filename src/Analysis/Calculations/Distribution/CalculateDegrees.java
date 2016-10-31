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
        Map<Integer, Integer> innerDegrees = distribution.getInnerDegrees();
        Map<Integer, Integer> outerDegrees = distribution.getOuterDegrees();
        Map<Integer, Integer> allDegrees = distribution.getAllDegrees();

        /*
         * Prepare data
         */
        System.out.println("Plotting data...");
        PlotDataSet dataSet = new PlotDataSet("Degree distribution");

        double[] innerX = innerDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] innerY = innerDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Inner degrees", innerX, innerY);

        DescriptiveStatistics innerDS = new DescriptiveStatistics(innerY);

        double[] outerX = outerDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] outerY = outerDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Outer degrees", outerX, outerY);

        DescriptiveStatistics outerDS = new DescriptiveStatistics(outerY);

        double[] allX = allDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] allY = allDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("All degrees", allX, allY);

        DescriptiveStatistics allDS = new DescriptiveStatistics(allY);

        double inner_standardDeviation = innerDS.getStandardDeviation();
        double inner_mean = innerDS.getMean();
        System.out.printf("inner : μ = %f; σ = %f\n", inner_mean, inner_standardDeviation);
        System.out.println(innerDS);

        double outer_standardDeviation = outerDS.getStandardDeviation();
        double outer_mean = outerDS.getMean();
        System.out.printf("outer : μ = %f; σ = %f\n", outer_mean, outer_standardDeviation);
        System.out.println(outerDS);

        double all_standardDeviation = allDS.getStandardDeviation();
        double all_mean = allDS.getMean();
        System.out.printf("all   : μ = %f; σ = %f\n", all_mean, all_standardDeviation);
        System.out.println(allDS);

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
