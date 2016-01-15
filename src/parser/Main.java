/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.StringReader;
import static java.lang.System.exit;
import java_cup.runtime.Symbol;

/**
 *
 * @author reuel
 */
public class Main {

    public static void main(String[] args) {
        try {
            Lexer lexer;
            lexer = new Lexer(new StringReader("  ${\n" +
"    imp.1 $e |- ( ph -> ( ps -> ch ) ) $.\n" +
"    $( Importation inference.  (Contributed by NM, 5-Aug-1993.)  (Proof\n" +
"       shortened by Eric Schmidt, 22-Dec-2006.) $)\n" +
"    imp $p |- ( ( ph /\\ ps ) -> ch ) $=\n" +
"      ( wa wn wi df-an impi sylbi ) ABEABFGFCABHABCDIJ $.\n" +
"      $( [22-Dec-2006] $) $( [5-Aug-1993] $)\n" +
"\n" +
"    $( Importation inference with commuted antecedents.  (Contributed by NM,\n" +
"       25-May-2005.) $)\n" +
"    impcom $p |- ( ( ps /\\ ph ) -> ch ) $=\n" +
"      ( com12 imp ) BACABCDEF $.\n" +
"      $( [25-May-2005] $)\n" +
"  $}"));

            /* Below there is a simple test for analysing the lexer returned 
               tokens. Just uncomment it out. */
//            Symbol next_token;
//            while ((next_token = lexer.next_token()).sym != 0) {
//                System.out.println(sym.terminalNames[next_token.sym] + " ("+ next_token.value + ")");
//                
//            }
//            exit(0);

            parser p = new parser(lexer);

            System.out.println(p.parse());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
