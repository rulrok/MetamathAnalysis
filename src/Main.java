
import Graph.GraphFactory;
import Graph.Algorithms.DegreeDistribution;
import Graph.Label;
import Graph.RelTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.leores.plot.JGnuplot;
import org.leores.plot.JGnuplot.Plot;
import org.leores.util.data.DataTableSet;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class Main {

    public static void main(String[] args) {
        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/metamath");

        /*
         * Begin graph analisys
         */
        List<Node> axiomNodes;
        Node helperNode;
        try (Transaction tx = graphDb.beginTx()) {

            /* make a new vertex x with edges x->v for all v */
            helperNode = graphDb.createNode();

//            ResourceIterator<Node> allNodes = GlobalGraphOperations.at(graphDb).getAllNodes().iterator();
            ResourceIterator<Node> allAxioms = graphDb.findNodes(Label.AXIOM);
            axiomNodes = new ArrayList<>();
            for (; allAxioms.hasNext();) {
                Node node = allAxioms.next();
                helperNode.createRelationshipTo(node, RelTypes.SUPPORTS);
                axiomNodes.add(node);
            }

            tx.failure();
        }


        /*
         * Calculate the distributions
         */
        calculateDegrees(graphDb);
        
        //Make sure we don't change the graph
        graphDb.shutdown();
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

}
