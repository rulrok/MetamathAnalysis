/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import java.util.Map;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public interface IGraph {

    public void StartTransaction();
    
    public void CommitTransaction();
//    ========
    public Node addProperty(Node node, String key, String value);

    public Node addLabel(Node node, Label label);

//    ========    
    public Node addNode(Node node);

    public Node addNode(Node node, Label label);

//    ========
    public Relationship createRelationship(Node a, Node b);

    public Relationship createRelationship(Node a, Node b, Map<String, String> properties);
}
