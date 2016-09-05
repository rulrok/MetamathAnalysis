package Graph.Algorithms;

import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GraphNodeRemover implements LabelFiltered {

    List<Label> labelFilters;
    List<Function<Node, Boolean>> customFilters;

    List<Node> nodesToDelete;

    GraphDatabaseService graph;

    public GraphNodeRemover(GraphDatabaseService graph) {

        this.graph = graph;

        //filter lists
        this.labelFilters = new ArrayList<>();
        this.customFilters = new ArrayList<>();

        //found nodes to be deleted at the end
        this.nodesToDelete = new ArrayList<>();
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

    public void execute() {
        try (Transaction tx = graph.beginTx()) {

            GlobalGraphOperations.at(graph).getAllNodes().forEach(node -> {
                if (labelFilters.stream().anyMatch(l -> node.hasLabel(l))) {
                    nodesToDelete.add(node);
                }

                customFilters.forEach(func -> {
                    if (func.apply(node)) {
                        nodesToDelete.add(node);
                    }
                });
            });

            nodesToDelete.forEach(nodeToDelete -> {
                nodeToDelete.getRelationships().forEach(Relationship::delete);
                nodeToDelete.delete();
            });

            tx.success();
        }
    }

}
