package Analysis.Calculations.Distribution;

import Graph.Algorithms.DegreeDistribution;
import Graph.Algorithms.GraphNodeRemover;
import Graph.Algorithms.RemoveIsolatedNodes;
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

        GraphDatabaseService graph = GraphFactory.copyGraph(GraphFactory.NOUSERBOX_METAMATH_DB, "db/degree_distribution");

        System.out.println("Removing undesired nodes...");
        GraphNodeRemover.KeepOnlyAxiomsAndTheorems(graph);
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr
                //DFS from ax-meredith to remove undesired componente
                .addComponentHeadDFS("ax-meredith")
                //Specific nodes
                .addCustomFilter(n -> n.getProperty("name").toString().startsWith("dummy"));

        System.out.println("Removing isolated nodes...");
        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

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

        double[] outerX = outerDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] outerY = outerDegrees.values().stream().mapToDouble(i -> i).toArray();
        dataSet.addData("Outer degrees", outerX, outerY);

        double[] allX = allDegrees.keySet().stream().mapToDouble(i -> i).toArray();
        double[] allY = allDegrees.values().stream().mapToDouble(i -> i).toArray();
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
