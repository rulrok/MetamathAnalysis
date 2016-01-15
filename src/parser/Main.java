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
            lexer = new Lexer(new StringReader("label-reuel $f wff P $."));

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
