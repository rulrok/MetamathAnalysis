package Graph;

/**
 * That enumeration describes the labels used on the graph nodes.
 *
 * @author Reuel
 */
public enum Label {

    AXIOM("Axiom"),
    CONSTANT("Constant"),
    HYPOTHESIS("Hypothesis"),
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