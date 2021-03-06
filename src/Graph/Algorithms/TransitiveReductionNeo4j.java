package Graph.Algorithms;

import Graph.Algorithms.Export.Formatters.SIFFormatter;
import Graph.Algorithms.Export.GraphToTxt;
import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;
import org.ujmp.core.SparseMatrix;
import org.ujmp.core.SparseMatrix2D;

/**
 * Instead of keaping two sparse matrices, we use only one with an object to
 * represent each entry; This avoids 'out of memory' heap problems.
 *
 * @author reuel
 */
class MatrixNode {

    boolean edgeWeight;
    long relId;

    public MatrixNode(boolean weight, long id) {
        this.edgeWeight = weight;
        this.relId = id;
    }

}

/**
 *
 * @author reuel
 */
public class TransitiveReductionNeo4j {

    private final GraphDatabaseService graph;

    public TransitiveReductionNeo4j(GraphDatabaseService graph) {
        this.graph = graph;
    }

    /**
     * This operation permanently changes the graph!.
     */
    public void execute() {
        try (Transaction tx = graph.beginTx()) {
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            int nodeCount = 0;
            long minId = Integer.MAX_VALUE;
            long maxId = Integer.MIN_VALUE;
            //Discover the highest node id to create a matrix to represent the graph
            for (Node n : allNodes) {
                long id = n.getId();
                if (id < minId) {
                    minId = id;
                }
                if (id > maxId) {
                    maxId = id;
                }
            }
            nodeCount = (int) maxId; //Because the IDs are not sequencial

            //We use a sparse matrix to run the simplest algorithm, since Neo4j
            //won't perform well for this scenario
            SparseMatrix graphMatrix = SparseMatrix2D.Factory.zeros(maxId, maxId);

            //Represent the Neo4j graph into the sparse matrix
            Iterable<Relationship> allRelationships = GlobalGraphOperations.at(graph).getAllRelationships();
            for (Relationship r : allRelationships) {
                long u = r.getStartNode().getId();
                long v = r.getEndNode().getId();

                try {
                    // u --> v
                    graphMatrix.setAsObject(new MatrixNode(true, r.getId()), u, v);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            //Algorithm based on http://stackoverflow.com/a/6702198/2561091
            for (int j = 0; j < nodeCount; ++j) {
                for (int i = 0; i < nodeCount; ++i) {

                    Object ij_entry = graphMatrix.getAsObject(i, j);
                    if (!(ij_entry instanceof MatrixNode)) {
                        //There is not an edge i --> j in the graph
                        continue;
                    }

                    if (((MatrixNode) ij_entry).edgeWeight) {
                        // i --> j exists
                        for (int k = 0; k < nodeCount; ++k) {

                            Object jk_entry = graphMatrix.getAsObject(j, k);
                            if (!(jk_entry instanceof MatrixNode)) {
                                //There is not an edge j --> k in the graph
                                continue;
                            }

                            if (((MatrixNode) jk_entry).edgeWeight) {
                                // j --> k exists
                                ((MatrixNode) jk_entry).edgeWeight = false;

                                graph.getRelationshipById(((MatrixNode) jk_entry).relId).delete();
                                graphMatrix.setAsObject(jk_entry, j, k);
                            } else {
                                throw new RuntimeException("This line should never be reachead");
                            }
                        }
                    } else {
                        throw new RuntimeException("This line should never be reachead");
                    }
                }
            }

            tx.success();
        }

        System.out.println("Finished!");
    }

    public static void main(String[] args) {

        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        TransitiveReductionNeo4j reduction = new TransitiveReductionNeo4j(graph);
        reduction.execute();

        GraphToTxt toTxt = new GraphToTxt(graph);
        toTxt.export("TR_metamath.sif", new SIFFormatter());
    }

}
