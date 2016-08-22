package Graph.Algorithms.Export;

import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.Algorithms.Contracts.RelationshipFiltered;
import Graph.Algorithms.Export.Formatters.IGraphFormatter;
import Graph.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GraphToTxt implements LabelFiltered, RelationshipFiltered {

    private final GraphDatabaseService graph;
    private final String outputFilePath;

    private final List<Label> labelFilters;
    private final List<RelationshipType> relFilters;

    private IGraphFormatter formatter;

    private final List<Node> nodesToPrint;
    private final List<Relationship> relsToPrint;

    public GraphToTxt(GraphDatabaseService graph, String outputFilePath) {
        //Basic stuff
        this.graph = graph;
        this.outputFilePath = outputFilePath;
        //Formatter
        this.formatter = EGraphFormatter.SIMPLE.getFormatter();
        //Filters
        this.labelFilters = new ArrayList<>();
        this.relFilters = new ArrayList<>();
        //Things to print
        this.nodesToPrint = new ArrayList<>();
        this.relsToPrint = new ArrayList<>();
    }

    @Override
    public GraphToTxt addFilterLabel(Label label) {
        labelFilters.add(label);
        return this;
    }

    @Override
    public GraphToTxt addFilterRelationship(RelationshipType label) {
        relFilters.add(label);
        return this;
    }

    
    /**
     * @param relationshipType
     * @param formatter
     * @return 
     * @deprecated Use the 'addFilterRelationship' method instead to filter by relationship.
     */
    public boolean execute(RelationshipType relationshipType, EGraphFormatter formatter) {
        this.addFilterRelationship(relationshipType);
        return this.execute(formatter);
    }

    public boolean execute(EGraphFormatter formatter) {
        this.formatter = formatter.getFormatter();
        File outputGraph = new File(outputFilePath);

        try {
            outputGraph.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Cannot create file");
            return false;
        }

        if (!outputGraph.canWrite()) {
            System.err.println("Cannot write into the file");
            return false;
        }

        if (!processNodesAndRelationships()) {
            return false;
        }

        return writeGraph(outputGraph);
    }

    private boolean writeGraph(File outputGraph) {

        /*
         * Print elements to a file
         */
        CharSequence format = formatter.format(graph, nodesToPrint, relsToPrint);
        try (final PrintWriter printWriter = new PrintWriter(outputGraph)) {
            printWriter.write(format.toString());

            printWriter.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private boolean processNodesAndRelationships() {
        try (final Transaction tx = graph.beginTx()) {
            /*
            * Get nodes
             */
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            for (Node node : allNodes) {

                if (!labelFilters.isEmpty()) {
                    boolean labelFound = false;
                    for (Label l : labelFilters) {
                        labelFound |= node.hasLabel(l);
                    }
                    if (!labelFound) {
                        continue;
                    }
                }

                nodesToPrint.add(node);
            }
            /*
            * Get relationships
             */
            Iterable<Relationship> allRelationships = GlobalGraphOperations.at(graph).getAllRelationships();
            relIterator:
            for (Relationship relationship : allRelationships) {

                //Filter relationships
                relTypeIterator:
                for (RelationshipType r : relFilters) {
                    if (!relationship.isType(r)) {
                        continue relIterator;
                    }
                }

                Node startNode = relationship.getStartNode();
                Node endNode = relationship.getEndNode();

                //Filter nodes
                if (!labelFilters.isEmpty()) {

                    boolean startMatch = false, endMatch = false;
                    for (Label l : labelFilters) {
                        startMatch |= startNode.hasLabel(l);
                        endMatch |= endNode.hasLabel(l);
                    }
                    if (!startMatch || !endMatch) {
                        continue;
                    }
                }

                relsToPrint.add(relationship);

            }
            //Make sure we don't modify the original graph
            tx.failure();
        } catch (Exception ex) {
            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

}
