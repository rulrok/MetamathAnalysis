package Utils.HIPR;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Reuel
 */
public class ParseHIPRInputfile {

    private final File file;
    private final Map<String, String> nodesNames;

    private int S;
    private int T;
    private int nodesCount;
    private int edgesCount;

    public ParseHIPRInputfile(File file) {
        this.file = file;
        this.nodesNames = new HashMap<>(5000);
    }

    public void parse() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();

            if (!nextLine.startsWith("a")) {

                if (nextLine.startsWith("p")) {
                    String[] firstLine = nextLine.split(" ");
                    nodesCount = Integer.parseInt(firstLine[2]);
                    edgesCount = Integer.parseInt(firstLine[3]);
                } else if (nextLine.endsWith("s")) {
                    String[] secondLine = nextLine.split(" ");
                    S = Integer.parseInt(secondLine[1]);
                } else if (nextLine.endsWith("t")) {
                    String[] thirdLine = nextLine.split(" ");
                    T = Integer.parseInt(thirdLine[1]);
                }

                continue;
            }

            String[] params = nextLine.split(" ");

//            String label = params[0]; //a
            String source = params[1]; //source node id
            String destin = params[2]; //dest node id
//            String weight = params[3]; //edge weight
            //params[4] = parenthesis
            String sourceName = params[5]; //extra info: source name
            //params[6] = arrow
            String destinName = params[7]; //extra info: dest name

            String oldValueSource = nodesNames.put(source, sourceName);
            if (oldValueSource != null && !oldValueSource.equals(sourceName)) {
                throw new RuntimeException("Valores inconsistentes");
            }

            String oldValueDestin = nodesNames.put(destin, destinName);
            if (oldValueDestin != null && !oldValueDestin.equals(destinName)) {
                throw new RuntimeException("Valores inconsistentes");
            }
        }

    }

    public int getNodesCount() {
        return nodesCount;
    }

    public int getEdgesCount() {
        return edgesCount;
    }

    public int getS() {
        return S;
    }

    public int getT() {
        return T;
    }

    public Set<String> getAllNodeIDs() {
        return new HashSet<>(nodesNames.keySet());
    }

    public String getNodeName(Integer id) {
        return getNodeName(Integer.toString(id));
    }

    public String getNodeName(String id) {
        return nodesNames.get(id);
    }

    public int getNodeId(String nodeName) {

        for (Entry<String, String> entry : nodesNames.entrySet()) {
            if (entry.getValue().equals(nodeName)) {
                return Integer.parseInt(entry.getKey());
            }
        }

        return -1;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("biparted-graph-super-axiom-theorem.txt");
        ParseHIPRInputfile hIPRInputfile = new ParseHIPRInputfile(file);
        hIPRInputfile.parse();

        System.out.println(hIPRInputfile.getEdgesCount());
        System.out.println(hIPRInputfile.getNodesCount());
        System.out.println(hIPRInputfile.getS());
        System.out.println(hIPRInputfile.getT());

    }
}
