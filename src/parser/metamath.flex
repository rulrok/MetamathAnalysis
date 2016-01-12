   
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
MathSymbol          = ([a-zA-Z0-9._-]|\`|\~|\!|\@|\#|\%|\^|\&|\*|\(|\)|\-|\_|\=|\+|\[|\]|\{|\}|\;|\:|\'|\"|\,|\.|\<|\>|\/|\?|\\|\|)+

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
   
    "${"         { return symbol(sym.SCOPE_START); }
    "$}"         { return symbol(sym.SCOPE_END); }
    "$("         { return symbol(sym.COMMENT_START); }
    "$)"         { return symbol(sym.COMMENT_END); }
    "$["         { return symbol(sym.INCLUDE_START); }
    "$]"         { return symbol(sym.INCLUDE_END); }
    "$c"         { return symbol(sym.CONSTANT_STMT); }
    "$v"         { return symbol(sym.VARIABLE_STMT); }
    "$d"         { return symbol(sym.DISJUNCT_VARIABLE_STMT); }
    "$f"         { return symbol(sym.VARIABLE_TYPE_HYPOTHESIS_STMT); }
    "$e"         { return symbol(sym.LOGICAL_HYPOTHESIS_STMT); }
    "$a"         { return symbol(sym.AXIOMATIC_ASSERTION_STMT); }
    "$p"         { return symbol(sym.PROVABLE_ASSERTION_STMT); }
    "$="         { return symbol(sym.PROOF_STMT); }
    "$."         { return symbol(sym.STMT_END); }
   
    {Label}      { return symbol(sym.LABEL, yytext()); }

    {MathSymbol} { return symbol(sym.MATH_SYMB, yytext()); }
 
   
    /* Don't do anything if whitespace is found */
    {WhiteSpace}       { /* just skip what was found, do nothing */ }   
}


/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
