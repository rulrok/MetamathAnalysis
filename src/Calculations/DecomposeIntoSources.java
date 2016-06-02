package Calculations;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.GraphFactory;
import Plot.Gnuplot;
import Plot.PlotDataSet;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public class DecomposeIntoSources {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeDefaultMetamathGraph();

        /*
         * Decompose the graph into sources
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> sources = decomposition.decomposeIntoSources();
        
                PlotDataSet dataset;
        dataset = new PlotDataSet("Decomposição em sources");
        
        double[] x = new double[sources.size()];
        double[] y = new double[sources.size()];
        for (int i = 0; i < sources.size(); i++) {
            List<Node> component = sources.get(i);
            x[i] = i;
            y[i] = component.size();
        }
        dataset.addData("Sources", x, y);
        
        Gnuplot plot = new Gnuplot(dataset);
        plot.setFilename("decomposicao_sources.png")
                .setxLabel("Component k")
                .setyLabel("Component size")
                .plot();
        
        System.out.print("Total number of source components: ");
        System.out.println(sources.size());
    }

}
