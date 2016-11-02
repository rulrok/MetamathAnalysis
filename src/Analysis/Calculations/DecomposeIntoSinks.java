package Analysis.Calculations;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.GraphFactory;
import Plot.Gnuplot;
import Plot.PlotDataSet;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class DecomposeIntoSinks {

    public static void main(String[] args) {

        /*
         * Get the graph
         */
        GraphDatabaseService graphDb = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        /*
         * Decompose the graph into sinks
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> sinkLayers = decomposition.decomposeIntoSinks();

        PlotDataSet dataset;
        dataset = new PlotDataSet("Decomposição em sinks");

        double[] x = new double[sinkLayers.size()];
        double[] y = new double[sinkLayers.size()];

        try (Transaction tx = graphDb.beginTx(); PrintWriter writer = new PrintWriter("decomposicao_sinks_nomes.txt")) {

            for (int i = 0; i < sinkLayers.size(); i++) {
                List<Node> actualLayer = sinkLayers.get(i);
                x[i] = i;
                y[i] = actualLayer.size();

                writer.append("Component #\t")
                        .append(Integer.toString(i))
                        .append("\tSize\t")
                        .append(String.format("%05d", actualLayer.size()))
                        .append(" ; ");

                actualLayer.stream()
                        .map((node) -> graphDb.getNodeById(node.getId())) //It's necessary to refetch the node from the graph
                        .map((node) -> node.getProperty("name").toString())
                        .forEach((String nodeName) -> {
                            writer.append(nodeName).append(' ');
                        });
                writer.println();
            }
            tx.failure();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DecomposeIntoSinks.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataset.addData("Sinks", x, y);

        Gnuplot plot = new Gnuplot(dataset);
        plot.setFilename("decomposicao_sinks.png")
                .setxLabel("Component k")
                .setyLabel("Component size")
                .plot();

        System.out.print("Total number of sink components: ");
        System.out.println(sinkLayers.size());
    }
}
