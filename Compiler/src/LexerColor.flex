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

CaracterNoValido = [$¬@%&?¡¿"|""/""*"]
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
Error1 = {Punto}({Punto})*
Error2 = {Comilla}({Comilla})*
Error3 = {GuionBajo}({GuionBajo})

%%

/* Comentarios o espacios en blanco */
{Comentario} { return textColor(yychar, yylength(), new Color(146, 146, 146)); }
{EspacioEnBlanco} { /*Ignorar*/ }

/* Pálabras Reservadas - Puertos */

{NumeroEntero} { return textColor(yychar, yylength(), new Color(146, 146, 146)); }

{NumeroReal} { return textColor(yychar, yylength(), new Color(146, 146, 146)); }

port_1|
port_2|
port_3|
port_4|
port_5|
port_6|
port_7|
port_8|
port_9  { return textColor(yychar, yylength(), new Color(186, 143, 86)); }

/* Tipos de puertos */
proximity |
temperature |
LED |
LED_RGB |
LCD |
speaker |
joystick |
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

/*Funciones Motor*/
move { return textColor(yychar, yylength(), new Color(106, 210, 161)); }
stop { return textColor(yychar, yylength(), new Color(106, 210, 161)); }
start { return textColor(yychar, yylength(), new Color(106, 210, 161)); }

/*Metodos sensores */
distance |
color |
state |
position |
degree { return textColor(yychar, yylength(), new Color(210, 106, 193)); }


begin { return textColor(yychar, yylength(), new Color(165, 187, 232)); }
loop { return textColor(yychar, yylength(), new Color(165, 187, 232)); }

/*Estructuras de control/repetitivas*/
if { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
else if { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
switch { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
do { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
while { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
repeat { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
break { return textColor(yychar, yylength(), new Color(75, 109, 177)); }
case { return textColor(yychar, yylength(), new Color(75, 109, 177)); }


{Identificador} { return textColor(yychar, yylength(), new Color(231, 119, 46)); } 
{IdentificadorVariable} { return textColor(yychar, yylength(), new Color(87, 35, 100)); }
{IdentificadorCadena} { return textColor(yychar, yylength(), new Color(87, 35, 100)); }

"!" { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

/* Operadores aritméticos */
"+" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"-" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"*" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"/" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"^" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }
"%" { return textColor(yychar, yylength(), new Color(178, 127, 232)); }

/* Operadores de agrupación */
"(" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
")" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
"{" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }
"}" { return textColor(yychar, yylength(), new Color(20, 39, 111)); }

/* Operadores de asignación */
"="  { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

/* Signos de puntuación */
"," { return textColor(yychar, yylength(), new Color(249, 194, 60)); }

{Error0} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }
{Error1} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }
{Error2} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }
{Error3} { return textColor(yychar, yylength(), new Color(183, 36, 57)); }

. { return textColor(yychar, yylength(), new Color(183, 36, 57)); }