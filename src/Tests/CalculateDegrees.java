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
        JGnuplot jg = new JGnuplot() {
            {
                terminal = "pngcairo enhanced dashed";
                output = "plot2d.png";
                extra = "set xrange[0:500]; set yrange[0:1000];";
            }
        };
        JGnuplot.Plot plot = new JGnuplot.Plot("") {
            {
                xlabel = "Number of Links(k)";
                ylabel = "Number of nodes with k Links";
            }
        };
        double[] innerX = innerDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] innerY = innerDegrees.values().stream().mapToDouble(i -> i).toArray();

        double[] outterX = outterDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] outterY = outterDegrees.values().stream().mapToDouble(i -> i).toArray();

        double[] allX = allDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] allY = allDegrees.values().stream().mapToDouble(i -> i).toArray();

        DataTableSet dts = plot.addNewDataTableSet("Degree distribution");
        dts.addNewDataTable("Inner degrees", innerX, innerY);
        dts.addNewDataTable("Outter degrees", outterX, outterY);
        dts.addNewDataTable("All degrees", allX, allY);

        /*
         * Plot graphics
         */        
        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, "degree.plt");
    }
}
