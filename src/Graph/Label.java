package Graph;

/**
 * That enumeration describes the labels used on the graph nodes.
 *
 * @author Reuel
 */
public enum Label implements org.neo4j.graphdb.Label {

    /*
    Node labels
     */
    AXIOM("Axiom"),
    CONSTANT("Constant"),
    DEFINITION("Definition"),
    HYPOTHESIS("Hypothesis"),
    SYNTAX_DEFINITION("Syntax_Definition"),
    THEOREM("Theorem"),
    VARIABLE("Variable"),
    /*
    General unknown label
     */
    UNKNOWN("Unknown");

    private final String name;

    Label(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
