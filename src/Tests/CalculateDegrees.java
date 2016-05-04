package Tests;

import Graph.Algorithms.DegreeDistribution;
import Graph.GraphFactory;
import java.util.Map;
import org.leores.plot.JGnuplot;
import org.leores.util.data.DataTableSet;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class CalculateDegrees {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/metamath");

        /*
         * Calculate the distributions
         */
        DegreeDistribution distribution = new DegreeDistribution(graphDb);
        distribution.calculate();
        Map<Integer, Integer> innerDegrees = distribution.getInnerDegrees();
        Map<Integer, Integer> outterDegrees = distribution.getOutterDegrees();
        Map<Integer, Integer> allDegrees = distribution.getAllDegrees();

        /*
         * Prepare GNUPlot
         */
        String plot2dpng = "plot2d.png";
        String xLabel = "Number of Links(k)";
        String yLabel = "Number of nodes with k Links";

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

        plot(plot2dpng, xLabel, yLabel, dataSet);
    }

    private static void plot(String plot2dpng, String xLabel, String yLabel, PlotDataSet set) {
        JGnuplot jg = new JGnuplot() {
            {
                terminal = "pngcairo enhanced dashed";
                output = plot2dpng;
                extra = "set xrange[0:500]; set yrange[0:1000];";
            }
        };
        JGnuplot.Plot plot = new JGnuplot.Plot("") {
            {
                xlabel = xLabel;
                ylabel = yLabel;
            }
        };

        DataTableSet dts = plot.addNewDataTableSet(set.getTitle());
        for (PlotData data : set.getValues()) {
            dts.addNewDataTable(data.title, data.xAxis, data.yAxis);
        }

        /*
        * Plot graphics
         */
        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, "degree.plt");
    }
}
