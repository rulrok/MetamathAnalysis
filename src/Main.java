
import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.TarjanSCC;
import Graph.Algorithms.Contracts.StrongConnectedComponents;
import Graph.Algorithms.DecompositionTarget;
import Graph.Algorithms.DegreeDistribution;
import Graph.Algorithms.TraverserGraphDecomposition;
import Graph.Algorithms.GraphToTxt;
import Graph.Algorithms.SimpleGraphDecomposition;
import Graph.Label;
import Graph.Neo4jBatchGraph;
import Graph.RelTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.leores.plot.JGnuplot;
import org.leores.plot.JGnuplot.Plot;
import org.leores.util.data.DataTableSet;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

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

        /*
         * Begin graph analisys
         */
        try (Transaction tx = graphDb.beginTx()) {

            /* make a new vertex x with edges x->v for all v */
            Node helperNode = graphDb.createNode();

//            ResourceIterator<Node> allNodes = GlobalGraphOperations.at(graphDb).getAllNodes().iterator();
            ResourceIterator<Node> allAxioms = graphDb.findNodes(Label.AXIOM);
            List<Node> axiomNodes = new ArrayList<>();
            for (; allAxioms.hasNext();) {
                Node node = allAxioms.next();
                helperNode.createRelationshipTo(node, RelTypes.SUPPORTS);
                axiomNodes.add(node);
            }

            /*
             * Export to txt
             */
            //exportToTxt(graphDb);
            /*
             * Calculate SCC
             */
            //calculateSCC(graphDb, helperNode);

            /*
             * Calculate the distributions
             */
            //calculateDegrees(graphDb);

            /*
             * Decompose the graph into sinks
             */
            List<List<Node>> sinks = decomposeIntoSinks(graphDb, axiomNodes);
            System.out.print("Total number of components: ");
            System.out.println(sinks.size());
            
            sinks.forEach((List<Node> component) -> {
                System.out.print("Component size: ");
                System.out.println(component.size());
            });
            
//            List<Node> initialNode = new LinkedList<>();
//            initialNode.add(helperNode);
//            List<List<Node>> decomposeIntoSources = decomposeIntoSources(graphDb,initialNode);

            //Make sure we don't change the graph
            tx.failure();
        } catch (Exception ex) {
            Logger.getLogger(Neo4jBatchGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

        graphDb.shutdown();
    }

    private static List<List<Node>> decomposeIntoSinks(GraphDatabaseService graphDb, List<Node> initialNodes) throws Exception {

        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> components = decomposition.execute(DecompositionTarget.SINK, initialNodes);
        return components;
    }

    private static List<List<Node>> decomposeIntoSources(GraphDatabaseService graphDb, List<Node> initialNodes) throws Exception {

        GraphDecomposition decomposition = new TraverserGraphDecomposition(graphDb);
        List<List<Node>> components = decomposition.execute(DecompositionTarget.SOURCE, initialNodes);
        return components;
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
                extra = "set xrange[0:500]; set yrange[0:1000];";
            }
        };
        Plot plot = new Plot("") {
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

        jg.execute(plot, jg.plot2d);
        jg.compile(plot, jg.plot2d, "degree.plt");
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
