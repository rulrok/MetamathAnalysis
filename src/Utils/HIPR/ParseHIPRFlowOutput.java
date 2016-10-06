package Utils.HIPR;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ujmp.core.SparseMatrix;

/**
 *
 * @author Reuel
 */
public class ParseHIPRFlowOutput {

    /*
     * Parser internal states
     */
    private enum ParseState {
        INITIAL_STATE, FLOW_VALUES, NODES_SINK_SIDE
    }

    /*
     * Parser data structures
     */
    private final File file;
    private Set<String> nodesIDsSinkSide;

    private SparseMatrix graph;

    private ParseState actualState = ParseState.INITIAL_STATE;

    /*
     * File properties
     */
    private int nodesCount;
    private int edgesCount;
    private double flow;

    public ParseHIPRFlowOutput(File file) {
        this.file = file;
    }

    public Set<String> getNodesIDsOnSinkSide() {
        return Collections.unmodifiableSet(nodesIDsSinkSide);
    }

    public boolean isNodeOnSinkSide(String nodeId) {
        return nodesIDsSinkSide.contains(nodeId);
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public int getEdgesCount() {
        return edgesCount;
    }

    public double getMaxFlow() {
        return flow;
    }

    public double getArcFlow(int startNode, int endNode) {

        return graph.getAsDouble(startNode, endNode);
    }

    public void parse() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();

            boolean parsedOK = parse_line(nextLine);

            if (parsedOK) {
                //Don't need to change the state of the parser
                continue;
            }
            if (nextLine.startsWith("c flow values")) {
                actualState = ParseState.FLOW_VALUES;
            } else if (nextLine.startsWith("c nodes on the sink side")) {
                actualState = ParseState.NODES_SINK_SIDE;
            }

        }
    }

    /**
     * Parse a line accordingly to the actual parser internal state. If the
     * found line is valid for the actual state, it is parsed and true is
     * returned. If the found line is not parseable in the actual parser state,
     * return false.
     *
     * @param nextLine
     * @return True if line could be parsed, false otherwise.
     */
    private boolean parse_line(String nextLine) {
        switch (actualState) {
            case INITIAL_STATE:
                return parse_initial_state_line(nextLine);
            case NODES_SINK_SIDE:
                return parse_nodes_sink_side_line(nextLine);
            case FLOW_VALUES:
                return parse_flow_values(nextLine);
            default:
                return false;
        }
    }

    private boolean parse_initial_state_line(String nextLine) {

        System.out.println(nextLine);

        /*
         * Nodes pattern matcher
         */
        Pattern nodesPat = Pattern.compile("c nodes:\\s+?(\\d+)");
        Matcher nodesMatcher = nodesPat.matcher(nextLine);
        if (nodesMatcher.matches()) {
            String nodesStr = nodesMatcher.group(1);
            this.nodesCount = Integer.parseInt(nodesStr);

            graph = SparseMatrix.Factory.zeros(nodesCount, nodesCount);
            nodesIDsSinkSide = new HashSet<>(nodesCount + 1);
        }

        /*
         * Edges pattern matcher
         */
        Pattern edgePat = Pattern.compile("c arcs:\\s+?(\\d+)");
        Matcher edgeMatcher = edgePat.matcher(nextLine);
        if (edgeMatcher.matches()) {
            String edgeStr = edgeMatcher.group(1);
            this.edgesCount = Integer.parseInt(edgeStr);
        }

        /*
         * Flow pattern matcher
         */
        Pattern flowPat = Pattern.compile("c flow:\\s+?(\\d+\\.\\d+)");
        Matcher flowMatcher = flowPat.matcher(nextLine);
        if (flowMatcher.matches()) {
            String flowStr = flowMatcher.group(1);
            this.flow = Double.parseDouble(flowStr);
        }

        /*
         * Initial state matcher
         */
        Pattern pattern = Pattern.compile("^c +(flow values|nodes on the).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nextLine);
        return !matcher.matches();
    }

    private boolean parse_nodes_sink_side_line(String nextLine) {
        Pattern pattern = Pattern.compile("^c +[0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nextLine);
        boolean matches = matcher.matches();
        if (!matches) {
            return false;
        }

        String[] split = nextLine.split(" ");
        nodesIDsSinkSide.add(split[1]);

        return true;
    }

    private boolean parse_flow_values(String nextLine) {
        Pattern flowPat = Pattern.compile("^f\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)", Pattern.CASE_INSENSITIVE);
        Matcher flowMatcher = flowPat.matcher(nextLine);

        if (flowMatcher.matches()) {

            int originNode = Integer.parseInt(flowMatcher.group(1));
            int destinNode = Integer.parseInt(flowMatcher.group(2));
            int edgeCost = Integer.parseInt(flowMatcher.group(3));

            graph.setAsInt(edgeCost, originNode, destinNode);
        }

        return flowMatcher.matches();
    }
}
