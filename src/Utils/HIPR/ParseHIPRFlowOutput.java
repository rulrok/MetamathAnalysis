package Utils.HIPR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        INITIAL_STATE, FLOW_VALUES, NODES_SINK_SIDE, FINISH_STATE
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
    private double maxFlow;
    private boolean suppressConsoleOutput = false;

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
        return maxFlow;
    }

    public double getArcFlow(int startNode, int endNode) {
        if (maxFlow == 0) {
            //We are sure no edge has flow passing through it.
            return 0;
        }

        return graph.getAsDouble(startNode, endNode);
    }

    public void suppressConsoleOutput() {
        suppressConsoleOutput = true;
    }

    public void parse() throws FileNotFoundException, IOException {

        List<String> readAllLines = Files.readAllLines(file.toPath());

        readAllLines.stream().forEach((nextLine) -> {

            //Don't need to change the state of the parser
            boolean parsedOK = parse_line(nextLine);
            if (!(parsedOK)) {
                if (nextLine.startsWith("c flow values")) {
                    change_actual_state(ParseState.FLOW_VALUES);
                } else if (nextLine.startsWith("c nodes on the sink side")) {
                    change_actual_state(ParseState.NODES_SINK_SIDE);
                }
            }

        });

        change_actual_state(ParseState.FINISH_STATE);
    }

    private void change_actual_state(ParseState newState) {
        if (actualState == ParseState.INITIAL_STATE) {
            //If we are changing from the INITIAL_STATE,
            //execute the needed actions
            post_initial_state_actions();
        }

        actualState = newState;
    }

    /**
     * After knowing the flow value, we can decided to not instanciate a new
     * sparse matrix, since all value will be zero.
     */
    private void post_initial_state_actions() {
        if (maxFlow > 0) {
            graph = SparseMatrix.Factory.zeros(nodesCount, nodesCount);
        }
        nodesIDsSinkSide = new HashSet<>(nodesCount + 1);
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
                return parse_flow_values_line(nextLine);
            case FINISH_STATE:
                throw new RuntimeException("The parser has already finished parsing lines. New lines are not expected.");
            default:
                return false;
        }
    }

    private boolean parse_initial_state_line(String nextLine) {

        /*
         * Initial state matcher
         */
        Pattern pattern = Pattern.compile("^c +(flow values|nodes on the).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nextLine);
        if (matcher.matches()) {
            return false;
        }

        if (!suppressConsoleOutput) {
            System.out.println(nextLine);
        }

        /*
         * Nodes pattern matcher
         */
        Pattern nodesPat = Pattern.compile("c nodes:\\s+?(\\d+)");
        Matcher nodesMatcher = nodesPat.matcher(nextLine);
        if (nodesMatcher.matches()) {
            String nodesStr = nodesMatcher.group(1);
            this.nodesCount = Integer.parseInt(nodesStr);
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
            this.maxFlow = Double.parseDouble(flowStr);
        }

        return true;
    }

    private boolean parse_nodes_sink_side_line(String nextLine) {
        Pattern pattern = Pattern.compile("^c +[0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nextLine);

        if (!matcher.matches()) {
            return false;
        }

        String[] split = nextLine.split(" ");
        nodesIDsSinkSide.add(split[1]);

        return true;
    }

    private static final Pattern FLOW_PATTERN = Pattern.compile("^f +?([0-9]+) +?([0-9]+) +?([0-9]+)$", Pattern.CASE_INSENSITIVE);
    private static final Matcher FLOW_MATCHER = FLOW_PATTERN.matcher("");

    private boolean parse_flow_values_line(String nextLine) {

        if (maxFlow == 0) {
            return false;
        }

        FLOW_MATCHER.reset(nextLine);
        if (!FLOW_MATCHER.matches()) {
            return false;
        }

        int originNode = Integer.parseInt(FLOW_MATCHER.group(1));
        int destinNode = Integer.parseInt(FLOW_MATCHER.group(2));
        int edgeCost = Integer.parseInt(FLOW_MATCHER.group(3));

        if (edgeCost > 0) {
            graph.setAsInt(edgeCost, originNode, destinNode);
        }

        return true;

    }
}
