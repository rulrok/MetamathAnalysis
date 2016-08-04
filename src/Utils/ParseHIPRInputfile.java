package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Reuel
 */
public class ParseHIPRInputfile {

    private final File file;
    private final Map<String, String> nodes;

    public ParseHIPRInputfile(File file) {
        this.file = file;
        this.nodes = new HashMap<>(5000);
    }

    public void parse() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();

            if (!nextLine.startsWith("a")) {
                continue;
            }

            String[] params = nextLine.split(" ");

            String label = params[0]; //a
            String source = params[1];
            String destin = params[2];
            String weight = params[3];
            //params[4] = parenthesis
            String sourceName = params[5];
            //params[6] = arrow
            String destinName = params[7];

            String oldValueSource = nodes.put(source, sourceName);
            if (oldValueSource != null && !oldValueSource.equals(sourceName)) {
                throw new RuntimeException("Valores inconsistentes");
            }

            String oldValueDestin = nodes.put(destin, destinName);
            if (oldValueDestin != null && !oldValueDestin.equals(destinName)) {
                throw new RuntimeException("Valores inconsistentes");
            }
        }

    }

    public String getNodeName(Integer id) {
        return getNodeName(Integer.toString(id));
    }

    public String getNodeName(String id) {
        return nodes.get(id);
    }

    public static void main(String[] args) throws FileNotFoundException {
        File input = new File("grafo_HIPR-axiom-theorem.txt");
        ParseHIPRInputfile phipr = new ParseHIPRInputfile(input);
        phipr.parse();

        String nodeName = phipr.getNodeName(1);
        System.out.println(nodeName);

    }

}
