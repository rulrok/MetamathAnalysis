
import Graph.Algorithms.TarjanSCC;
import Graph.Algorithms.Contracts.StrongConnectedComponents;
import Graph.Algorithms.DegreeDistribution;
import Graph.Algorithms.GabowSCC;
import Graph.Algorithms.GraphToTxt;
import Graph.Algorithms.KosarajuSCC;
import Graph.RelTypes;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.leores.plot.JGnuplot;
import org.leores.plot.JGnuplot.Plot;
import org.leores.util.data.DataTableSet;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class Main {

    public static void main(String[] args) {
        File dbPath = new File("db/metamath");
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(dbPath)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();

        registerShutdownHook(graphDb);

        try (Transaction tx = graphDb.beginTx()) {

            /* make a new vertex x with edges x->v for all v */
            Node helperNode = graphDb.createNode();

            ResourceIterator<Node> allNodes = GlobalGraphOperations.at(graphDb).getAllNodes().iterator();// graphDb.findNodes(Label.AXIOM);
            for (; allNodes.hasNext();) {
                Node node = allNodes.next();
                helperNode.createRelationshipTo(node, RelTypes.SUPPORTS);
            }

            /*
             * Export to txt
             */
            exportToTxt(graphDb);
            /*
             * Calculate SCC
             */
            calculateSCC(graphDb, helperNode);

            /*
             * Calculate the distributions
             */
            calculateDegrees(graphDb);

            //Make sure we don't change the graph
            tx.failure();
        }

        graphDb.shutdown();
    }

    private static void exportToTxt(GraphDatabaseService graphDb) {

        GraphToTxt graphToTxt = new GraphToTxt(graphDb, "grafo.txt");
        graphToTxt.execute(RelTypes.SUPPORTS);
    }

    private static void calculateSCC(GraphDatabaseService graphDb, Node helperNode) {

        StrongConnectedComponents scc = new TarjanSCC(graphDb, helperNode, RelTypes.SUPPORTS);
        List<List<Node>> components = scc.execute();

        components.stream()
                .filter((component) -> (component.size() > 1))
                .forEach((component) -> {
                    System.out.printf("Componente com mais de um elemento encontrado. (Tamanho: %d)\n", component.size());
                });
    }

    private static void calculateDegrees(GraphDatabaseService graphDb) {

        DegreeDistribution distribution = new DegreeDistribution(graphDb);
        distribution.calculate();
        Map<Integer, Integer> innerDegrees = distribution.getInnerDegrees();
        Map<Integer, Integer> outterDegrees = distribution.getOutterDegrees();
        Map<Integer, Integer> allDegrees = distribution.getAllDegrees();

        JGnuplot jg = new JGnuplot() {
            {
                terminal = "pngcairo enhanced dashed";
                output = "plot2d.png";
            }
        };
        Plot plot = new Plot("") {
            {
                xlabel = "x";
                ylabel = "y";
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

        jg.execute(plot, jg.plot2d);
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
