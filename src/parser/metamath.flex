   
/* --------------------------Usercode Section------------------------ */
   
package parser;
import java_cup.runtime.*;
import parser.sym;
      
%%
   
/* -----------------Options and Declarations Section----------------- */
   

%class Lexer

%line
%column

%cup
   
/*
  Declarations
   
  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.  
*/
%{   
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   

/*
  Macro Declarations
  
  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.  
*/
   

LineTerminator      = \r|\n|\r\n
InputCharacter      = [^\r\n]
WhiteSpace          = {LineTerminator} | [ \t\f]
          
SpecialChar         = \`|\~|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\=|\+|\[|\]|\{|\}|\;|\:|\'|\"|\,|\.|\<|\>|\/|\?|\\|\|
          
Label               = [a-zA-Z0-9_.-]+
 
/*
  A math symbol token may consist of any combination of the 93 printable 
  standard ascii characters other than $ . */
MathSymbol          = [a-zA-Z0-9._-]|\`|\~|\!|\@|\#|\%|\^|\&|\*|\(|\)|\-|\_|\=|\+|\[|\]|\{|\}|\;|\:|\'|\"|\,|\.|\<|\>|\/|\?|\\|\|

%%
/* ------------------------Lexical Rules Section---------------------- */
   
/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */
   
   /* YYINITIAL is the state at which the lexer begins scanning.  So
   these regular expressions will only be matched if the scanner is in
   the start state YYINITIAL. */
   
<YYINITIAL> {
   
    /* Print the token found that was declared in the class sym and then
       return it. */
    "${"         { System.out.print(" ${ "); return symbol(sym.SCOPE_START); }
    "$}"         { System.out.print(" $} "); return symbol(sym.SCOPE_END); }
    "$("         { System.out.print(" $( "); return symbol(sym.COMMENT_START); }
    "$)"         { System.out.print(" $) "); return symbol(sym.COMMENT_END); }
    "$["         { System.out.print(" $[ "); return symbol(sym.INCLUDE_START); }
    "$]"         { System.out.print(" $] "); return symbol(sym.INCLUDE_END); }
    "$c"         { System.out.print(" $c "); return symbol(sym.CONSTANT_STMT); }
    "$v"         { System.out.print(" $v "); return symbol(sym.VARIABLE_STMT); }
    "$d"         { System.out.print(" $d "); return symbol(sym.DISJUNCT_VARIABLE_STMT); }
    "$f"         { System.out.print(" $f "); return symbol(sym.VARIABLE_TYPE_HYPOTHESIS_STMT); }
    "$e"         { System.out.print(" $e "); return symbol(sym.LOGICAL_HYPOTHESIS_STMT); }
    "$a"         { System.out.print(" $a "); return symbol(sym.AXIOMATIC_ASSERTION_STMT); }
    "$p"         { System.out.print(" $p "); return symbol(sym.PROVABLE_ASSERTION_STMT); }
    "$="         { System.out.print(" $= "); return symbol(sym.PROOF_STMT); }
    "$."         { System.out.print(" $. "); return symbol(sym.STMT_END); }
   
    {Label}      { System.out.print(yytext()); return symbol(sym.LABEL, yytext()); }

    {MathSymbol} { System.out.print(yytext()); return symbol(sym.MATH_SYMB, yytext()); }
 
   
    /* Don't do anything if whitespace is found */
    {WhiteSpace}       { /* just skip what was found, do nothing */ }   
}


/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
