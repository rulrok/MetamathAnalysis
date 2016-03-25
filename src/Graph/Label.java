package Graph;

/**
 * That enumeration describes the labels used on the graph nodes.
 *
 * @author Reuel
 */
public enum Label {

    AXIOM("Axiom"),
    CONSTANT("Constant"),
    DEFINITION("Definition"),
    HYPOTHESIS("Hypothesis"),
    SYNTAX_DEFINITION("Syntax definition"),
    THEOREM("Theorem"),
    VARIABLE("Variable"),
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