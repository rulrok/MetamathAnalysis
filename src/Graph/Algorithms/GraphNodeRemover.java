package Graph.Algorithms;

import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.Label;
import Graph.RelType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GraphNodeRemover implements LabelFiltered {

    private static final Logger LOG = Logger.getLogger(GraphNodeRemover.class.getName());

    public static void KeepOnlyAxioms(GraphDatabaseService graph) {

        LOG.info("Removing all nodes but Axioms...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.THEOREM)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .execute();
    }

    public static void KeepOnlyAxiomsAndTheorems(GraphDatabaseService graph) {

        LOG.info("Removing all nodes but Axioms and Theorems...");
        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr
                .addFilterLabel(Label.CONSTANT)
                .addFilterLabel(Label.DEFINITION)
                .addFilterLabel(Label.HYPOTHESIS)
                .addFilterLabel(Label.SYNTAX_DEFINITION)
                .addFilterLabel(Label.UNKNOWN)
                .addFilterLabel(Label.VARIABLE)
                .execute();
    }

    List<Label> labelFilters;
    List<Function<Node, Boolean>> customFilters;

    List<String> componentHeadsNames;

    Set<Node> nodesToDelete;

    GraphDatabaseService graph;

    public GraphNodeRemover(GraphDatabaseService graph) {

        this.graph = graph;

        //DFS components to remove
        this.componentHeadsNames = new ArrayList<>();

        //filter lists
        this.labelFilters = new ArrayList<>();
        this.customFilters = new ArrayList<>();

        //found nodes to be deleted at the end
        this.nodesToDelete = new HashSet<>();
    }

    @Override
    public GraphNodeRemover addFilterLabel(Label label) {
        labelFilters.add(label);
        return this;
    }

    public GraphNodeRemover addCustomFilter(Function<Node, Boolean> func) {
        customFilters.add(func);
        return this;
    }

    /**
     * Starting from a node, delete all the tree found in a DFS search using the
     * outgoing relationships including the start node. This is executed before
     * any other removal operation.
     *
     * @param startNodeName
     * @return
     */
    public GraphNodeRemover addComponentHeadDFS(String startNodeName) {
        componentHeadsNames.add(startNodeName);
        return this;
    }

    public void execute() {
        try (Transaction tx = graph.beginTx()) {

            componentHeadsNames.forEach(nodeName -> {
                Node foundNode = graph.findNode(Label.AXIOM, "name", nodeName);

                if (foundNode != null) {
                    graph.traversalDescription()
                            .depthFirst()
                            .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                            .relationships(RelType.SUPPORTS, Direction.OUTGOING)
                            .traverse(foundNode)
                            .nodes()
                            .forEach(n -> {
                                Logger.getGlobal().info(
                                        String.format("\tPreparing to delete the node %s and all its relationships\n", n.getProperty("name").toString())
                                );
                                nodesToDelete.add(n);
                            });
                }
            });

            GlobalGraphOperations.at(graph).getAllNodes().forEach(node -> {
                //Try to match with a label filter
                if (labelFilters.stream().anyMatch(l -> node.hasLabel(l))) {
                    nodesToDelete.add(node);
                } else { //Try to match with a custom filter
                    customFilters.forEach(func -> {
                        if (func.apply(node)) {
                            nodesToDelete.add(node);
                        }
                    });
                }
            });

            //Delete the matched nodes
            nodesToDelete.forEach(nodeToDelete -> {
                nodeToDelete.getRelationships().forEach(Relationship::delete);
                nodeToDelete.delete();
            });

            tx.success();
        }
    }

}
