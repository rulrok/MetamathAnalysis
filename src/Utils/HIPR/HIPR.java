package Utils.HIPR;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jodd.io.StreamGobbler;

/**
 *
 * @author Reuel
 */
public class HIPR {

    public HIPR() {
    }

    public static int execute(String inputFilePath, String outputFilePath) {
        try {
            Runtime rt = Runtime.getRuntime();

            String[] command;

            switch (File.separatorChar) {
                case '\\':
                    //Windows
                    command = new String[]{
                        String.format("cmd.exe /c type %s | .\\lib\\hipr\\hi_pr.exe > %s", inputFilePath, outputFilePath)
                    };
                    break;
                case '/':
                    //Unix
                    command = new String[]{
                        "/bin/sh",
                        "-c",
                        String.format("cat %s | ./lib/hipr/hi_pr > %s", inputFilePath, outputFilePath)
                    };
                    break;
                default:
                    command = new String[]{
                        "exit"
                    };
                    break;
            }
            Process pr = rt.exec(command);

            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(pr.getErrorStream());

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(pr.getInputStream());

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            return pr.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HIPR.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
}
