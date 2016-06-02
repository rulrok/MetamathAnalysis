package Calculations;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.GraphFactory;
import Plot.Gnuplot;
import Plot.PlotData;
import Plot.PlotDataSet;
import java.util.List;
import org.leores.plot.JGnuplot.Plot;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public class DecomposeIntoSinks {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeDefaultMetamathGraph();

        /*
         * Decompose the graph into sinks
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> sinks = decomposition.decomposeIntoSinks();

        PlotDataSet dataset;
        dataset = new PlotDataSet("Decomposição em sinks");
        
        double[] x = new double[sinks.size()];
        double[] y = new double[sinks.size()];
        for (int i = 0; i < sinks.size(); i++) {
            List<Node> component = sinks.get(i);
            x[i] = i;
            y[i] = component.size();
        }
        dataset.addData("Sinks", x, y);
        
        Gnuplot plot = new Gnuplot(dataset);
        plot.setFilename("decomposicao_sinks.png")
                .setxLabel("Component k")
                .setyLabel("Component size")
                .plot();
        
        
        System.out.print("Total number of sink components: ");
        System.out.println(sinks.size());
    }
}
