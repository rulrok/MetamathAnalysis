   
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
    private String string_found = "";

    private StringBuilder string_builder = new StringBuilder();

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

/* In order to avoid REGEX problems, all special characters are escaped */
/* We have only removed the '$' character from here to avoid issues with 
   metamath reserved tokens */
SpecialChars        = \`|\~|\!|\@|\#|\%|\^|\&|\*|\(|\)|\-|\_|\=|\+|\[|\]|\{|\}|\;|\:|\'|\"|\,|\.|\<|\>|\/|\?|\\|\|

PrintableChars      = ([a-zA-Z0-9]|{SpecialChars})
 
/* A label token consists of any combination of letters, digits, 
   and the characters hyphen, underscore, and period */          
Label               = [a-zA-Z0-9\-\_\.]+

/* A math symbol token may consist of any combination of the 93 printable 
   standard ascii characters other than '$'. */
/* See metamath book p. 93 for more details. */
MathSymbol          = {PrintableChars}+

%state STRING, PROOF, COMPACT_PROOF

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
    "$="         { yybegin(PROOF); return symbol(sym.PROOF_STMT); }
    "$."         { return symbol(sym.STMT_END); }

    /* Since labels are a subset of math symbols (in terms of regex), we need
        to change to a state where we will lookahead and see if there are any
        $p, $a, $e, or $f symbol. That will tell us when we have found a LABEL
        or MATH_SYMB */
    {MathSymbol} { string_found = yytext(); yybegin(STRING); }
   
    /* Don't do anything if whitespace is found */
    {WhiteSpace}       { /* just skip what was found, do nothing */ }   
}

<STRING> {

    {WhiteSpace}  { /* just skip what was found, do nothing */ } 

    \$(e|f|a|p)   { yypushback(2); yybegin(YYINITIAL); return symbol(sym.LABEL, string_found); }  

    .             { yypushback(1); yybegin(YYINITIAL); return symbol(sym.MATH_SYMB, string_found); }
}

<PROOF> {

    {WhiteSpace}  { /* just skip what was found, do nothing */ } 

    "("           { return symbol(sym.LPARENT); }

    ")"           { yybegin(COMPACT_PROOF); string_builder.setLength(0); return symbol(sym.RPARENT); }

    {Label}       { return symbol(sym.LABEL,yytext()); }

    "$."          { yybegin(YYINITIAL); return symbol(sym.STMT_END); }

    <COMPACT_PROOF> {
        {WhiteSpace}  { /* just skip what was found, do nothing */ }
        {Label}       { string_builder.append(yytext()); }
        "$."          { yybegin(PROOF); yypushback(2); return symbol(sym.COMPACT_PROOF, string_builder.toString()); }
    }

}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
