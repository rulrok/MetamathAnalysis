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
            lexer = new Lexer(new StringReader("opnrebl $p |- ( A e. ( topGen ` ran (,) ) <-> ( A C_ RR /\\ A. x e. A E. y\n" +
"    e. RR+ ( ( x - y ) (,) ( x + y ) ) C_ A ) ) $=\n" +
"      cA cioo crn ctg cfv wcel cA cr wss vx cv vy cv cabs cmin ccom cr cr cxp\n" +
"      cres cbl cfv co cA wss vy crp wrex vx cA wral wa cA cr wss vx cv vy cv\n" +
"      cmin co vx cv vy cv caddc co cioo co cA wss vy crp wrex vx cA wral wa\n" +
"      cabs cmin ccom cr cr cxp cres cr cxmt cfv wcel cA cioo crn ctg cfv wcel\n" +
"      cA cr wss vx cv vy cv cabs cmin ccom cr cr cxp cres cbl cfv co cA wss vy\n" +
"      crp wrex vx cA wral wa wb cabs cmin ccom cr cr cxp cres cabs cmin ccom cr\n" +
"      cr cxp cres eqid rexmet vx vy cA cabs cmin ccom cr cr cxp cres cioo crn\n" +
"      ctg cfv cr cabs cmin ccom cr cr cxp cres cabs cmin ccom cr cr cxp cres\n" +
"      cmopn cfv cabs cmin ccom cr cr cxp cres eqid cabs cmin ccom cr cr cxp\n" +
"      cres cmopn cfv eqid tgioo elmopn2 ax-mp cA cr wss vx cv vy cv cabs cmin\n" +
"      ccom cr cr cxp cres cbl cfv co cA wss vy crp wrex vx cA wral vx cv vy cv\n" +
"      cmin co vx cv vy cv caddc co cioo co cA wss vy crp wrex vx cA wral cA cr\n" +
"      wss vx cv vy cv cabs cmin ccom cr cr cxp cres cbl cfv co cA wss vy crp\n" +
"      wrex vx cv vy cv cmin co vx cv vy cv caddc co cioo co cA wss vy crp wrex\n" +
"      vx cA cA cr wss vx cv cA wcel wa vx cv cr wcel vx cv vy cv cabs cmin ccom\n" +
"      cr cr cxp cres cbl cfv co cA wss vy crp wrex vx cv vy cv cmin co vx cv vy\n" +
"      cv caddc co cioo co cA wss vy crp wrex wb cA cr vx cv ssel2 vx cv cr wcel\n" +
"      vx cv vy cv cabs cmin ccom cr cr cxp cres cbl cfv co cA wss vx cv vy cv\n" +
"      cmin co vx cv vy cv caddc co cioo co cA wss vy crp vx cv cr wcel vy cv\n" +
"      crp wcel wa vx cv vy cv cabs cmin ccom cr cr cxp cres cbl cfv co vx cv vy\n" +
"      cv cmin co vx cv vy cv caddc co cioo co cA vy cv crp wcel vx cv cr wcel\n" +
"      vy cv cr wcel vx cv vy cv cabs cmin ccom cr cr cxp cres cbl cfv co vx cv\n" +
"      vy cv cmin co vx cv vy cv caddc co cioo co wceq vy cv rpre vx cv vy cv\n" +
"      cabs cmin ccom cr cr cxp cres cabs cmin ccom cr cr cxp cres eqid bl2ioo\n" +
"      sylan2 sseq1d rexbidva syl ralbidva pm5.32i bitri $."));

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
