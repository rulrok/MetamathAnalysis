package parser;

import Graph.GraphFactory;
import Graph.IGraph;
import Graph.Neo4jBatchGraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.io.fs.FileUtils;

/**
 *
 * @author reuel
 */
public class ParseMetamath {

    public static void main(String[] args) {

        try {
            Lexer lexer;

            lexer = new Lexer(new FileReader("set.mm"));

            /* Below there is a simple test for analysing the lexer returned
            tokens. Just uncomment it out. */
//            java_cup.runtime.Symbol next_token;
//            while ((next_token = lexer.next_token()).sym != 0) {
//                System.out.println(sym.terminalNames[next_token.sym] + " (" + next_token.value + ")");
//
//            }
//            System.exit(0);
            File databaseFolder = new File(GraphFactory.DEFAULT_METAMATH_DB);

            //Wipe out the database before parsing the file
            FileUtils.deleteRecursively(databaseFolder);

            IGraph graph = new Neo4jBatchGraph(databaseFolder);

            parser p = new parser(lexer, graph);
            p.parse();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseMetamath.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(ParseMetamath.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (Exception ex) {
            Logger.getLogger(ParseMetamath.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

    }
}
