package Utils;

import Graph.Algorithms.GraphToTxt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reuel
 */
public class ExportMapToTXT {

    public static boolean export(String fileOutput, Map map) {
        return export(fileOutput, map, new String[]{});
    }

    public static <k, V> boolean export(String fileOutput, Map<k, V> map, String[] headers) {

        try {

            File output = new File(fileOutput.trim() + ".txt");
            output.createNewFile();
            try (PrintWriter printWriter = new PrintWriter(output)) {
                //Construct the header
                if (headers.length > 0) {
                    StringJoiner sj = new StringJoiner("\t");
                    for (String header : headers) {
                        sj.add(header);
                    }
                    printWriter.append(sj.toString());
                    printWriter.append("\r\n");
                }
                
                //Construct the body
                map.keySet().stream().forEach((key) -> {
                    V value = map.get(key);
                    printWriter.printf("%s\t%s\r\n", key, value);
                });

                printWriter.flush();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        } catch (IOException ex) {
            Logger.getLogger(ExportMapToTXT.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
