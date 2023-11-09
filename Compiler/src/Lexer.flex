import compilerTools.Token;

%%
%class Lexer
%type Token
%line
%column
%{
    private Token token(String lexeme, String lexicalComp, int line, int column){
        return new Token(lexeme, lexicalComp, line+1, column+1);
    }
%}
/* Variables básicas de comentarios y espacios */
TerminadorDeLinea = \r|\n|\r\n
EntradaDeCaracter = [^\r\n]
EspacioEnBlanco = {TerminadorDeLinea} | [ \t\f]
ComentarioTradicional = "#*" [^*] ~"*/" | "/*" "*"+ "/" 
FinDeLineaComentario = "#" {EntradaDeCaracter}* {TerminadorDeLinea}?

CaracterNoValido = [$¬@%&?¡;¿"|"]
GuionBajo = [_]
Simbolo = [ .,=()<>#{}+-:&]
Punto = [.]
Coma = [,]
Comilla = [']

/* Comentario */
Comentario = {ComentarioTradicional} | {FinDeLineaComentario} 

/* Identificador */
Letra = [a-zA-Z]
Digito = [0-9]
Identificador = {Letra}({Letra}|{Digito})*

/* Número */
NumeroEntero = {Digito}({Digito})*

/* Número Real */
NumeroReal = {Digito}+\.({Digito})({Digito})*

IdentificadorCadena = {Comilla}({Letra}|{Digito})({Letra}|{Digito}|{Simbolo})*{Comilla}

/* Errores */

Error0 = {CaracterNoValido}({CaracterNoValido})*
Error1 = {Comilla}({Comilla})*
Error2 = {GuionBajo}({GuionBajo})

%%

/* Comentarios o espacios en blanco */
{Comentario}|{EspacioEnBlanco} { /*Ignorar*/ }

{NumeroEntero} { return token(yytext(), "NUMERO", yyline, yycolumn); }
{NumeroReal} { return token(yytext(), "NUMERO_REAL", yyline, yycolumn); }

/* Puertos */
port_1|
port_2|
port_3|
port_4|
port_5|
port_6|
port_7|
port_8|
port_9 { return token(yytext(), "PUERTO", yyline, yycolumn); }

/* Tipos de puertos */
proximity |
temperature |
LED |
LED_RGB |
LCD |
speaker |
joystick |
button |
motor { return token(yytext(), "TIPO_PUERTO", yyline, yycolumn); }

/* Tipos de dato*/
int|
string|
decimal|
boolean { return token(yytext(), "TIPO_DATO", yyline, yycolumn); }

/*Declaración de variables o constante */
var { return token(yytext(), "VARIABLE", yyline, yycolumn);}
const { return token(yytext(), "CONSTANTE", yyline, yycolumn);}

/*Operadores logicos */
and { return token(yytext(), "OP_LOGICO_AND", yyline, yycolumn);}
or { return token(yytext(), "OP_LOGICO_OR", yyline, yycolumn); }
not { return token(yytext(), "OP_LOGICO_NOT", yyline, yycolumn); }

/*Palabras reservadas*/
function { return token(yytext(), "FUNCION_PR", yyline, yycolumn); }
return { return token(yytext(), "RETURN_PR", yyline, yycolumn); }
print { return token(yytext(), "PRINT_PR", yyline, yycolumn); }
true { return token(yytext(), "VERDADERO", yyline, yycolumn); }
false { return token(yytext(), "FALSO", yyline, yycolumn); }
console { return token(yytext(), "CONSOLA", yyline, yycolumn); }

/*Funciones Motor*/
move { return token(yytext(), "MOVE_FM", yyline, yycolumn); }
restart { return token(yytext(), "RESTART_FM", yyline, yycolumn); }
start { return token(yytext(), "START_FM", yyline, yycolumn); }

/*Metodos sensores */
distance |
color |
state |
position |
degree { return token(yytext(), "METODO_SENSOR", yyline, yycolumn); }


begin { return token(yytext(), "BEGIN", yyline, yycolumn); }
loop { return token(yytext(), "LOOP", yyline, yycolumn); }

/*Estructuras de control/repetitivas*/
if { return token(yytext(), "IF_CONDICIONAL", yyline, yycolumn); }
else { return token(yytext(), "ELSE_CONDICIONAL", yyline, yycolumn); }

{Identificador} { return token(yytext(), "IDENTIFICADOR", yyline, yycolumn);} 
{IdentificadorCadena} { return token(yytext(), "CADENA", yyline, yycolumn); }

"!" { return token(yytext(), "END_INSTRUCTION", yyline, yycolumn); }

/* Operadores aritméticos */
"+" { return token(yytext(), "SUMA", yyline, yycolumn); }
"-" { return token(yytext(), "RESTA", yyline, yycolumn); }
"*" { return token(yytext(), "MULTI", yyline, yycolumn); }
"/" { return token(yytext(), "DIV", yyline, yycolumn); }

/* Operadores relacionales */
">"|
"<"|
">="|
"<=" { return token(yytext(), "OP_RELACIONAL", yyline, yycolumn); }

/* Signos de agrupación */
"{" { return token(yytext(), "L_LLAVE", yyline, yycolumn); }
"}" { return token(yytext(), "R_LLAVE", yyline, yycolumn); }
"(" { return token(yytext(), "L_PARENT", yyline, yycolumn); }
")" { return token(yytext(), "R_PARENT", yyline, yycolumn); }

/* Operadores de asignación */
"="  { return token(yytext(), "OP_ASIGNACION", yyline, yycolumn); }

/* Signos de puntuación */
"," { return token(yytext(), "COMA", yyline, yycolumn); }
{Punto} { return token(yytext(), "PUNTO", yyline, yycolumn); }

/* Errores */

{Error0} { return token(yytext(), "ERROR_0", yyline, yycolumn); }
{Error1} { return token(yytext(), "ERROR_1", yyline, yycolumn); }
{Error2} { return token(yytext(), "ERROR_2", yyline, yycolumn); }

. { return token(yytext(), "ERROR_X", yyline, yycolumn); }