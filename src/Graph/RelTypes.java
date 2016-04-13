/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Reuel
 */
public enum RelTypes implements RelationshipType {

    /*
    Relationship labels
     */
    
    RELIES("Relies"),
    USES("Uses"),
    SUPPORTS("Supports"),
    
    /*
    General unknown label
     */
    UNKNOWN("Unknown");

    private final String name;

    RelTypes(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
