package Analysis.TransitiveReduction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reuel
 */
public class ParseJGraphTOutputToSIF {

    private static final String GRAPH_INPUT = "graph_reduced.txt";
    private static final String GRAPH_OUTPUT = "graph_reduced.sif";

    public static void main(String[] args) {
        File reducedGraph = new File(GRAPH_INPUT);

        Map<Long, String> vertexNames = new HashMap<>(18000);

        try (Scanner scanner = new Scanner(reducedGraph)) {
            boolean parsingVertex = true;

            StringBuilder SIF_output = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    //Comment line
                    if (line.toLowerCase().contains("vertex")) {
                        parsingVertex = true;
                    } else if (line.toLowerCase().contains("edge")) {
                        parsingVertex = false;

                    }
                    continue;
                } else if (parsingVertex) {
                    //vertex line
                    String[] lineValues = line.split(" ");
                    Long id = Long.parseLong(lineValues[0]);
                    String name = lineValues[1];
                    vertexNames.put(id, name);
                } else {
                    //parsing edges
                    String[] lineValues = line.split(" ");
                    Long origin = Long.parseLong(lineValues[0]);
                    Long destin = Long.parseLong(lineValues[2]);

                    String originName = vertexNames.getOrDefault(origin, "error");
                    String destinName = vertexNames.getOrDefault(destin, "error");

                    SIF_output.append(originName).append("\tSUPPORTS\t").append(destinName).append(System.lineSeparator());
                }

            }

            try (FileWriter fw = new FileWriter(GRAPH_OUTPUT)) {
                fw.append(SIF_output);
            } catch (IOException ex) {
                Logger.getLogger(ParseJGraphTOutputToSIF.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseJGraphTOutputToSIF.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
