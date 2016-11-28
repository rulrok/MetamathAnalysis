package Analysis.FlowAnalysis.IndividualFlows;

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
 * We assume the results of the MaxFlowIndividualSourcesSinks.java script are
 * organized in one folder for each source analyses.
 */
public class VerifyAllIndividualFlowSides {

    public static void main(String[] args) {
        File output_dir = new File("./output");

        Map<String, Integer> histogram = new HashMap<>(8000);

        for (File d : output_dir.listFiles((File pathname) -> {
            return pathname.isDirectory();
        })) {
            String fullName = d.getName();
            System.out.println(fullName);

            for (File f : d.listFiles((File dir, String name) -> {
                return name.endsWith("_sides.txt");
            })) {
                try {
                    Scanner scanner = new Scanner(f);

                    scanner.nextLine(); //Line ######## NODES OF FRONTIER ########
                    String nextLine = scanner.nextLine(); //Empty line

                    do {
                        nextLine = scanner.nextLine(); //Theorem line or empty line
                        String trimmedName = nextLine.trim();

                        if (trimmedName.startsWith("#")) {
                            break; //Line ##### NODES ON SOURCE SIDE #####
                        } else if (trimmedName.isEmpty()) {
                            continue; //Empty line
                        }

                        Integer count = histogram.getOrDefault(trimmedName, 0);
                        count++;
                        histogram.put(trimmedName, count);
                    } while (true);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(VerifyAllIndividualFlowSides.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        try (FileWriter fw = new FileWriter("metamath-nouserboxes_halved_individual-flow_axiom-theorem_hipr_frontier_count.txt")) {

            histogram.entrySet().stream()
                    .forEach(e -> {
                        try {
                            fw.write(e.getKey() + "\t" + e.getValue());
                            fw.write(System.lineSeparator());
                        } catch (IOException ex) {
                            Logger.getLogger(VerifyAllIndividualFlowSides.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (IOException ex) {
            Logger.getLogger(VerifyAllIndividualFlowSides.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
