/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import java.util.Map;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Reuel
 */
public interface IGraph {

    public void StartTransaction();

    public void CommitTransaction();
//    ========

    public Node addProperty(String nodeName, String key, String value);

    public Node addLabel(String nodeName, String labelName);

//    ========    
    public Node addNode(String nodeName);

    public Node addNode(String nodeName, String labelName);

//    ========
    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest);

    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest, String labelName);

    public Relationship createRelationship(String nodeNameSrc, String nodeNameDest, String labelName, Map<String, String> properties);
}
