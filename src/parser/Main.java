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
            lexer = new Lexer(new StringReader("ovolgelb $p |- ( ( A C_ RR /\\ ( vol* ` A ) e. RR /\\ B e. RR+ ) ->\n" +
"              E. g e. ( ( <_ i^i ( RR X. RR ) ) ^m NN )\n" +
"                ( A C_ U. ran ( (,) o. g ) /\\\n" +
"                sup ( ran S , RR* , < ) <_ ( ( vol* ` A ) + B ) ) ) $=\n" +
"        ( vy vx cr wss wcel cxr clt cle wbr wa cn wi wral syl wceq cfv crp cioo\n" +
"        covol w3a cv ccom crn cuni caddc co csup wn cxp cmap wrex simp2 ltaddrp\n" +
"        simp3 syl2anc wb rpre readdcl ltnle mpbid cabs cmin cseq crab ccnv eqid\n" +
"        c1 ovolval 3ad2ant1 breq2d ssrab2 rexr infmxrgelb sylancr eqeq1 supeq1i\n" +
"        rneqi eqeq2i syl6bbr anbi2d rexbidv ralrab ralcom r19.23v ralbii imbi1i\n" +
"        cin ancom impexp bitri cc0 cpnf cico reex xpex inex2 elmap ovolsf sylbi\n" +
"        wf nnex frn icossxr syl6ss supxrcl breq2 imbi2d ceqsralv syl5bb ralbiia\n" +
"        3bitr3i syl6rbb bitr4d rexanali sylibr xrltnle sylbird syl2anr reximdva\n" +
"        mtbid xrltle anim2d mpd ) AHIZAUDUAZHJZBUBJZUEZAUCDUFZUGUHUIIZYJBUJUKZC\n" +
"        UHZKLULZMNZUMZOZDMHHUNZWLZPUOUKZUPZYOYRYPMNZOZDUUDUPYMYOYSQZDUUDRZUMUUE\n" +
"        YMYPYJMNZUUIYMYJYPLNZUUJUMZYMYKYLUUKYIYKYLUQZYIYKYLUSZYJBURUTYMYKYPHJZU\n" +
"        UKUULVAUUMYMYKBHJZUUOUUMYMYLUUPUUNBVBSYJBVCUTZYJYPVDUTVEYMUUJYPYOFUFZUJ\n" +
"        VFVGUGYNUGZVLVHZUHZKLULZTZOZDUUDUPZFKVIZKLVJULZMNZUUIYMYJUVGYPMYIYKYJUV\n" +
"        GTYLFADUVFUVFVKVMVNVOYMUVHYPGUFZMNZGUVFRZUUIYMUVFKIYPKJZUVHUVKVAUVEFKVP\n" +
"        YMUUOUVLUUQYPVQSZGUVFYPVRVSUVKYOUVIYRTZOZDUUDUPZUVJQZGKRZUUIUVEUVPUVJGF\n" +
"        KUURUVITZUVDUVODUUDUVSUVCUVNYOUVSUVCUVIUVBTUVNUURUVIUVBVTYRUVBUVIKYQUVA\n" +
"        LCUUTEWBWAWCWDWEWFWGUVOUVJQZDUUDRZGKRUVTGKRZDUUDRUVRUUIUVTGDKUUDWHUWAUV\n" +
"        QGKUVOUVJDUUDWIWJUWBUUHDUUDUWBUVNYOUVJQZQZGKRZYNUUDJZUUHUVTUWDGKUVTUVNY\n" +
"        OOZUVJQUWDUVOUWGUVJYOUVNWMWKUVNYOUVJWNWOWJUWFYRKJZUWEUUHVAUWFYQKIUWHUWF\n" +
"        YQWPWQWRUKZKUWFPUWICXEZYQUWIIUWFPUUCYNXEUWJUUCPYNUUBMHHWSWSWTXAXFXBCYNU\n" +
"        USUUSVKEXCXDPUWICXGSWPWQXHXIYQXJSZUWCUUHGYRKUVNUVJYSYOUVIYRYPMXKXLXMSXN\n" +
"        XOXPWOXQXRYEYOYSDUUDXSXTYMUUAUUGDUUDYMUWFOYTUUFYOUWFUWHUVLYTUUFQYMUWKUV\n" +
"        MUWHUVLOYTYRYPLNUUFYRYPYAYRYPYFYBYCYGYDYH $."));

            /* Below there is a simple test for analysing the lexer returned 
               tokens. Just uncomment it out. */
            Symbol next_token;
            while ((next_token = lexer.next_token()).sym != 0) {
                System.out.println(sym.terminalNames[next_token.sym] + " ("+ next_token.value + ")");
                
            }
            exit(0);

            parser p = new parser(lexer);

            System.out.println(p.parse());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
