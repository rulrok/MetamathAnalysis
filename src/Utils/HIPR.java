package Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reuel
 */
public class HIPR {

    public HIPR() {
    }

    public int execute(String inputFilePath, String outputFilePath) {
        try {
            Runtime rt = Runtime.getRuntime();

            String command;

            switch (File.separatorChar) {
                case '\\':
                    //Windows
                    command = String.format("cmd.exe /c type %s | .\\lib\\hipr\\hi_pr.exe > %s", inputFilePath, outputFilePath);
                    break;
                case '/':
                    //Unix
                    command = String.format("cat %s | ./hi_pr.exe > %s", inputFilePath, outputFilePath);
                    break;
                default:
                    command = "exit";
                    break;
            }

            Process pr = rt.exec(command);

            return pr.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(HIPR.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (InterruptedException ex) {
            Logger.getLogger(HIPR.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
}
