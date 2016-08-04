package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Reuel
 */
public class ParseHIPROutput {

    private enum ParseState {
        INITIAL_STATE, FLOW_VALUES, NODES_SINK_SIDE
    }

    private final File file;
    private final Set<String> nodesSinkSide;
    private ParseState actualState = ParseState.INITIAL_STATE;

    public ParseHIPROutput(File file) {
        this.file = file;
        this.nodesSinkSide = new HashSet<>(5000);
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
        nodesSinkSide.add(split[1]);

        return true;
    }

    private boolean parse_flow_values(String nextLine) {
        Pattern pattern = Pattern.compile("^f +[0-9]+ +[0-9]+ +[0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nextLine);
        return matcher.matches();
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("grafo_HIPR-axiom-theorem_results.txt");
        ParseHIPROutput phipro = new ParseHIPROutput(file);
        phipro.parse();
    }

}
