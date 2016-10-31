package Graph;

/**
 *
 * @author Reuel
 */
public enum RelType implements org.neo4j.graphdb.RelationshipType {

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

    RelType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
