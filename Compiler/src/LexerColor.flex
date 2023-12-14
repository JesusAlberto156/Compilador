import compilerTools.TextColor;
import java.awt.Color;

%%
%class LexerColor
%type TextColor
%char
%{
    private TextColor textColor(long start, int size, Color color){
        return new TextColor((int) start, size, color);
    }
%}
/* Variables básicas de comentarios y espacios */
TerminadorDeLinea = \r|\n|\r\n
EntradaDeCaracter = [^\r\n]
EspacioEnBlanco = {TerminadorDeLinea} | [ \t\f]
ComentarioTradicional = "#*" [^*] ~"*/" | "/*" "*"+ "/" 
FinDeLineaComentario = "#" {EntradaDeCaracter}* {TerminadorDeLinea}?

CaracterNoValido = [$¬@%?¡¿"|""/""*"]
GuionBajo = [_]
Simbolo = [ .,=()<>#{}+-;:&]
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
NumeroReal = {Digito}+\.({Digito})*

IdentificadorVariable = {Letra}({Letra}|{Digito})*
IdentificadorCadena = {Comilla}({Letra}|{Digito})({Letra}|{Digito}|{Simbolo})*{Comilla}

/* Errores */

Error0 = {CaracterNoValido}({CaracterNoValido})*
Error1 = {Comilla}({Comilla})*
Error2 = {GuionBajo}({GuionBajo})

%%

/* Comentarios o espacios en blanco */
{Comentario} { return textColor(yychar, yylength(), new Color(146, 146, 146)); }
{EspacioEnBlanco} { /*Ignorar*/ }

/* Pálabras Reservadas - Puertos */

{NumeroEntero} { return textColor(yychar, yylength(), new Color(146, 146, 146)); }

{NumeroReal} { return textColor(yychar, yylength(), new Color(146, 146, 146)); }

port_A0|
port_A1|
port_A2|
port_A3|
port_A4|
port_A5|
port_B0|
port_B1|
port_B2|
port_B3|
port_B4|
port_B5|
port_B6|
port_B7|
port_C2|
port_C3|
port_C4|
port_C5|
port_C6|
port_C7|
port_D0|
port_D1|
port_D2|
port_D3|
port_D4|
port_D5|
port_D6|
port_D7 { return textColor(yychar, yylength(), new Color(186, 143, 86)); }

/* Tipos de puertos */
proximity |
temperature |
LED |
LCD |
button |
motor { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

/* Tipos de dato*/
int|
string|
decimal|
boolean { return textColor(yychar, yylength(), new Color(47, 65, 143)); }

/*Declaración de variables o constante */
var { return textColor(yychar, yylength(), new Color(28, 75, 59)); }
const { return textColor(yychar, yylength(), new Color(28, 75, 59)); }

/*Operadores logicos */
and { return textColor(yychar, yylength(), new Color(146, 192, 115)); }
or { return textColor(yychar, yylength(), new Color(146, 192, 115)); }
not { return textColor(yychar, yylength(), new Color(146, 192, 115)); }

function { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
return { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
print { return textColor(yychar, yylength(), new Color(255, 95, 109)); }
true { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
false { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
console { return textColor(yychar, yylength(), new Color(0, 235, 255)); }
delay { return textColor(yychar, yylength(), new Color(255, 95, 109)); }
operation { return textColor(yychar, yylength(), new Color(255, 95, 109)); }
call { return textColor(yychar, yylength(), new Color(255, 95, 109)); }
ligther { return textColor(yychar, yylength(), new Color(255, 95, 109)); }

/*Funciones Motor*/
move { return textColor(yychar, yylength(), new Color(106, 210, 161)); }
restart { return textColor(yychar, yylength(), new Color(106, 210, 161)); }
start { return textColor(yychar, yylength(), new Color(106, 210, 161)); }

/*Metodos sensores */
distance |
time |
state |
degree { return textColor(yychar, yylength(), new Color(210, 106, 193)); }


begin { return textColor(yychar, yylength(), new Color(165, 187, 232)); }
loop { return textColor(yychar, yylength(), new Color(165, 187, 232)); }

/*Estructuras de control/repetitivas*/
if { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
else { return textColor(yychar, yylength(), new Color(75, 109, 177)); }


{Identificador} { return textColor(yychar, yylength(), new Color(231, 119, 46)); } 
{IdentificadorVariable} { return textColor(yychar, yylength(), new Color(87, 35, 100)); }
{IdentificadorCadena} { return textColor(yychar, yylength(), new Color(87, 35, 100)); }

"!" { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

/* Operadores aritméticos */
"+" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"-" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"*" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"/" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }

/* Operadores de agrupación */
"(" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
")" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
"{" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
"}" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }

/* Operadores de asignación */
"="  { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

/* Signos de puntuación */
"," { return textColor(yychar, yylength(), new Color(249, 194, 60)); }
"." { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

{Error0} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }
{Error1} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }
{Error2} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }

"&" { return textColor(yychar, yylength(), new Color(0, 0, 0)); }

. { return textColor(yychar, yylength(), new Color(183, 36, 57)); }