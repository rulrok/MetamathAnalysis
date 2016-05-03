/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.DecompositionTarget;
import Graph.Algorithms.SimpleGraphDecomposition;
import Graph.GraphFactory;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public class DecomposeIntoSources {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeGraph("db/metamath");

        /*
         * Decompose the graph into sources
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> sources = decomposition.execute(DecompositionTarget.SOURCE, null);
        System.out.print("Total number of source components: ");
        System.out.println(sources.size());
    }

}
