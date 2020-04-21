/*
  File Name: article.flex
  JFlex specification for a news article format
*/

%%

%class Lexer
%type Token
%line
%column

%eofval{
  return null;
%eofval};

DIGIT = [0-9]
ALPHANUM = [a-zA-Z0-9]

NEWLINE = \r|\n|\r\n
WHITESPACE = [ \t\f]
LABEL = "$DOC"|"$TITLE"|"$TEXT"
NUMBER = ("-"|"+")?{DIGIT}*"."?{DIGIT}+
WORD = {ALPHANUM}+
APOSTROPHIZED = ({HYPHENATED}|{WORD})'{WORD}('{WORD})*
HYPHENATED = {WORD}"-"{WORD}("-"{WORD})*
PUNCTUATION = .

%%

{WHITESPACE}       { /** Skip Whitespace **/}
{LABEL}            { return new Token(Token.LABEL, yytext(), yyline, yycolumn); }
{NUMBER}           { return new Token(Token.NUMBER, yytext(), yyline, yycolumn); }
{WORD}             { return new Token(Token.WORD, yytext(), yyline, yycolumn); }
{APOSTROPHIZED}    { return new Token(Token.APOSTROPHIZED, yytext(), yyline, yycolumn); }
{HYPHENATED}       { return new Token(Token.HYPHENATED, yytext(), yyline, yycolumn); }
{NEWLINE}          { return new Token(Token.NEWLINE, yytext(), yyline, yycolumn); }
{PUNCTUATION}      { return new Token(Token.PUNCTUATION, yytext(), yyline, yycolumn); }
