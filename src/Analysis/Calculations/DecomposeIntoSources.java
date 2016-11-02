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
public class DecomposeIntoSources {

    public static void main(String[] args) {

        /*
         * Get the graph
         */
        GraphDatabaseService graphDb = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        /*
         * Decompose the graph into sources
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> sourceLayers = decomposition.decomposeIntoSources();

        PlotDataSet dataset;
        dataset = new PlotDataSet("Decomposição em sources");

        double[] x = new double[sourceLayers.size()];
        double[] y = new double[sourceLayers.size()];
        try (Transaction tx = graphDb.beginTx(); PrintWriter writer = new PrintWriter("decomposicao_sources_nomes.txt")) {

            for (int i = 0; i < sourceLayers.size(); i++) {
                List<Node> actualLayer = sourceLayers.get(i);
                x[i] = i;
                y[i] = actualLayer.size();

                writer.append("Componente\t")
                        .append(Integer.toString(i))
                        .append("\tTamanho\t")
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
            Logger.getLogger(DecomposeIntoSources.class.getName()).log(Level.SEVERE, null, ex);
        }

        dataset.addData("Sources", x, y);

        Gnuplot plot = new Gnuplot(dataset);
        plot.setFilename("decomposicao_sources.png")
                .setxLabel("Component k")
                .setyLabel("Component size")
                .plot();

        System.out.print("Total number of source components: ");
        System.out.println(sourceLayers.size());
    }

}
