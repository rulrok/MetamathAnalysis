package Analysis;

import Utils.ParseHIPRInputfile;
import Utils.ParseHIPROutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Reuel
 */
public class Analyze_HIPR_Output {

    public static void main(String[] args) throws FileNotFoundException {
        /**
         * Prepare files
         */
        File HIPR_FILE
                = new File("grafo_HIPR.txt");

        File HIPR_RESULTS
                = new File("grafo_HIPR_results.txt");

        /**
         * Parse both files
         */
        ParseHIPRInputfile hipr_parsed = new ParseHIPRInputfile(HIPR_FILE);
        hipr_parsed.parse();

        ParseHIPROutput hipr_results_parsed = new ParseHIPROutput(HIPR_RESULTS);
        hipr_results_parsed.parse();

        /**
         * Gather nodes on SINK side
         */
        Set<String> nodeIDsOnSinkSide = hipr_results_parsed.getNodesOnSinkSide();
        Set<String> nodeNamesSinkSide = new HashSet<>();

        for (String nodeID : nodeIDsOnSinkSide) {
            nodeNamesSinkSide.add(hipr_parsed.getNodeName(nodeID));
        }

        /**
         * Gather nodes on SOURCE side
         */
        Set<String> nodeNamesSourceSide = new HashSet<>();
        Set<String> nodeIDsOnSourceSide = hipr_parsed.getAllNodeIDs();
        nodeIDsOnSourceSide.removeAll(nodeIDsOnSinkSide);

        for (String nodeID : nodeIDsOnSourceSide) {
            nodeNamesSourceSide.add(hipr_parsed.getNodeName(nodeID));
        }

        /**
         * Output nodes
         */
        final File outputFile = new File("grafo_HIPR_sides.txt");
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            outputFile.createNewFile();
            String lineBreak = System.lineSeparator();

            /**
             * Print nodes on SOURCE side
             */
            fileWriter
                    .append("#####################")
                    .append("# NODES ON SOURCE SIDE:")
                    .append("#####################")
                    .append(lineBreak).append(lineBreak);
            for (String nodeOnSource : nodeNamesSourceSide.stream().sorted().collect(Collectors.toList())) {
                fileWriter.append(nodeOnSource).append(lineBreak);
            }

            /**
             * Print nodes on SINK side
             */
            fileWriter
                    .append("#####################")
                    .append("# NODES ON SINK SIDE:")
                    .append("#####################")
                    .append(lineBreak).append(lineBreak);
            for (String nodeOnSink : nodeNamesSinkSide.stream().sorted().collect(Collectors.toList())) {
                fileWriter.append(nodeOnSink).append(lineBreak);
            }
            fileWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(Analyze_HIPR_Output.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
