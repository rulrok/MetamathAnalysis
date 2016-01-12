/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.InputStreamReader;
import java.io.StringReader;

/**
 *
 * @author reuel
 */
public class Main {

    public static void main(String[] args) {
        try {
            Lexer lexer;
            lexer = new Lexer(new StringReader("$c ( $."));
            
            parser p = new parser(lexer);
            
            System.out.println(p.parse());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
