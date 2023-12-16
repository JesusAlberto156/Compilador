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

CaracterNoValido = [$¬@%?¡;¿"|"]
GuionBajo = [_]
Simbolo = [ .,=()<>#{}+-:&_]
Simbolo2 = [,=()<>#{}+-:&_]
Punto = [.]
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
Error1 = {Comilla}({Letra}|{Digito}|{CaracterNoValido})({Letra}|{Digito}|{Simbolo}|{CaracterNoValido})*{Comilla}
Error2 = {Comilla}{Comilla}|{Comilla}{EspacioEnBlanco}{EspacioEnBlanco}*{Comilla}
Error3 = {Comilla}({Comilla})*
Error4 = {GuionBajo}({GuionBajo})*
Error6 = \.({Punto})*({Digito})({Digito})*
Error7 = \.({Punto})*({Digito})({Digito}|{CaracterNoValido}|{Simbolo2}|{Letra})*
Error8 = \.({Punto})*({Digito})({Digito}|{Punto})*
Error9 = \.({Punto})*({Digito})({Digito}|{CaracterNoValido}|{Simbolo2}|{Letra}|{Punto})*
Error10 = {Digito}+\.({Punto})*({Digito})({Digito}|{Punto})*
Error11 = {Digito}+\.({Punto})*({Digito})({Digito}|{CaracterNoValido}|{Simbolo2}|{Letra}|{Punto})*
Error12 = {Digito}+\.
Error5 = {Digito}({Digito}|{CaracterNoValido}|{Simbolo2}|{Letra})*\.({Digito})({Digito}|{CaracterNoValido}|{Simbolo2}|{Letra})*
Error13 = {Digito}({Digito}|{CaracterNoValido}|{Simbolo2}|{Letra})*

%%

/* Comentarios o espacios en blanco */
{Comentario}|{EspacioEnBlanco} { /*Ignorar*/ }

{NumeroEntero} { return token(yytext(), "NUMERO_ENTERO", yyline, yycolumn); }
{NumeroReal} { return token(yytext(), "NUMERO_REAL", yyline, yycolumn); }

/* Puertos */
port_A0 { return token(yytext(), "PUERTO_1", yyline, yycolumn); }
port_A1 { return token(yytext(), "PUERTO_2", yyline, yycolumn); }
port_A2 { return token(yytext(), "PUERTO_3", yyline, yycolumn); }
port_A3 { return token(yytext(), "PUERTO_4", yyline, yycolumn); }
port_A4 { return token(yytext(), "PUERTO_5", yyline, yycolumn); }
port_A5 { return token(yytext(), "PUERTO_6", yyline, yycolumn); }
port_B0 { return token(yytext(), "PUERTO_7", yyline, yycolumn); }
port_B1 { return token(yytext(), "PUERTO_8", yyline, yycolumn); }
port_B2 { return token(yytext(), "PUERTO_9", yyline, yycolumn); }
port_B3 { return token(yytext(), "PUERTO_10", yyline, yycolumn); }
port_B4 { return token(yytext(), "PUERTO_11", yyline, yycolumn); }
port_B5 { return token(yytext(), "PUERTO_12", yyline, yycolumn); }
port_B6 { return token(yytext(), "PUERTO_13", yyline, yycolumn); }
port_B7 { return token(yytext(), "PUERTO_14", yyline, yycolumn); }
port_C2 { return token(yytext(), "PUERTO_15", yyline, yycolumn); }
port_C3 { return token(yytext(), "PUERTO_16", yyline, yycolumn); }
port_C4 { return token(yytext(), "PUERTO_17", yyline, yycolumn); }
port_C5 { return token(yytext(), "PUERTO_18", yyline, yycolumn); }
port_C6 { return token(yytext(), "PUERTO_19", yyline, yycolumn); }
port_C7 { return token(yytext(), "PUERTO_20", yyline, yycolumn); }
port_D0 { return token(yytext(), "PUERTO_21", yyline, yycolumn); }
port_D1 { return token(yytext(), "PUERTO_22", yyline, yycolumn); }
port_D2 { return token(yytext(), "PUERTO_23", yyline, yycolumn); }
port_D3 { return token(yytext(), "PUERTO_24", yyline, yycolumn); }
port_D4 { return token(yytext(), "PUERTO_25", yyline, yycolumn); }
port_D5 { return token(yytext(), "PUERTO_26", yyline, yycolumn); }
port_D6 { return token(yytext(), "PUERTO_27", yyline, yycolumn); }
port_D7 { return token(yytext(), "PUERTO_28", yyline, yycolumn); }

/* Tipos de puertos */
proximity { return token(yytext(), "TIPO_PUERTO_1", yyline, yycolumn); }
temperature { return token(yytext(), "TIPO_PUERTO_2", yyline, yycolumn); }
LED { return token(yytext(), "TIPO_PUERTO_3", yyline, yycolumn); }
LCD { return token(yytext(), "TIPO_PUERTO_4", yyline, yycolumn); }
button { return token(yytext(), "TIPO_PUERTO_5", yyline, yycolumn); }
motor { return token(yytext(), "TIPO_PUERTO_6", yyline, yycolumn); }

/* Tipos de dato*/
int     { return token(yytext(), "TIPO_DATO_1", yyline, yycolumn); }
string  { return token(yytext(), "TIPO_DATO_2", yyline, yycolumn); }
decimal { return token(yytext(), "TIPO_DATO_3", yyline, yycolumn); }
boolean { return token(yytext(), "TIPO_DATO_4", yyline, yycolumn); }

/*Declaración de variables o constante */
var { return token(yytext(), "DECLARACION_1", yyline, yycolumn);}
const { return token(yytext(), "DECLARACION_2", yyline, yycolumn);}

/*Operadores logicos */
and { return token(yytext(), "OPERADOR_LOGICO_1", yyline, yycolumn);}
or { return token(yytext(), "OPERADOR_LOGICO_2", yyline, yycolumn); }
not { return token(yytext(), "OPERADOR_LOGICO_3", yyline, yycolumn); }

/*Palabras Reservadas*/
ligther { return token(yytext(), "PALABRA_RESERVADA_1", yyline, yycolumn); }
delay { return token(yytext(), "PALABRA_RESERVADA_2", yyline, yycolumn); }
operation { return token(yytext(), "PALABRA_RESERVADA_3", yyline, yycolumn); }
call { return token(yytext(), "PALABRA_RESERVADA_4", yyline, yycolumn); }
return { return token(yytext(), "PALABRA_RESERVADA_5", yyline, yycolumn); }
print { return token(yytext(), "PALABRA_RESERVADA_6", yyline, yycolumn); }
console { return token(yytext(), "PALABRA_RESERVADA_7", yyline, yycolumn); }
turn_off { return token(yytext(), "PALABRA_RESERVADA_8", yyline, yycolumn); }

/*Palabras para el valor condicional*/
true { return token(yytext(), "CONDICIONAL_1", yyline, yycolumn); }
false { return token(yytext(), "CONDICIONAL_2", yyline, yycolumn); }

/*Funciones Motor*/
move { return token(yytext(), "MOTOR_1", yyline, yycolumn); }
restart { return token(yytext(), "MOTOR_2", yyline, yycolumn); }
start { return token(yytext(), "MOTOR_3", yyline, yycolumn); }

/*Metodos sensores */
distance { return token(yytext(), "METODO_SENSOR_1", yyline, yycolumn); }
state { return token(yytext(), "METODO_SENSOR_2", yyline, yycolumn); }
time { return token(yytext(), "METODO_SENSOR_3", yyline, yycolumn); }
degree { return token(yytext(), "METODO_SENSOR_4", yyline, yycolumn); }

/*Estructuras de control*/
function { return token(yytext(), "ESTRUCTURA_DE_CONTROL_1", yyline, yycolumn); }
begin { return token(yytext(), "ESTRUCTURA_DE_CONTROL_2", yyline, yycolumn); }
loop { return token(yytext(), "ESTRUCTURA_DE_CONTROL_3", yyline, yycolumn); }
if { return token(yytext(), "ESTRUCTURA_DE_CONTROL_4", yyline, yycolumn); }
else { return token(yytext(), "ESTRUCTURA_DE_CONTROL_5", yyline, yycolumn); }

{Identificador} { return token(yytext(), "IDENTIFICADOR", yyline, yycolumn);} 
{IdentificadorCadena} { return token(yytext(), "IDENTIFICADOR_CADENA", yyline, yycolumn); }

/* Fin de linea */
"!" { return token(yytext(), "FIN_DE_LINEA", yyline, yycolumn); }

/* Operadores aritméticos */
"+" { return token(yytext(), "OPERADOR_ARITMETICO_1", yyline, yycolumn); }
"-" { return token(yytext(), "OPERADOR_ARITMETICO_2", yyline, yycolumn); }
"*" { return token(yytext(), "OPERADOR_ARITMETICO_3", yyline, yycolumn); }
"/" { return token(yytext(), "OPERADOR_ARITMETICO_4", yyline, yycolumn); }

/* Operadores relacionales */
">"  { return token(yytext(), "OPERADOR_RELACIONAL_1", yyline, yycolumn); }
"<"  { return token(yytext(), "OPERADOR_RELACIONAL_2", yyline, yycolumn); }
">=" { return token(yytext(), "OPERADOR_RELACIONAL_3", yyline, yycolumn); }
"<=" { return token(yytext(), "OPERADOR_RELACIONAL_4", yyline, yycolumn); }
"==" { return token(yytext(), "OPERADOR_RELACIONAL_5", yyline, yycolumn); }

/* Signos de agrupación */
"{" { return token(yytext(), "SIGNO_AGRUPACION_1", yyline, yycolumn); }
"}" { return token(yytext(), "SIGNO_AGRUPACION_2", yyline, yycolumn); }
"(" { return token(yytext(), "SIGNO_AGRUPACION_3", yyline, yycolumn); }
")" { return token(yytext(), "SIGNO_AGRUPACION_4", yyline, yycolumn); }

/* Operadores de asignación */
"="  { return token(yytext(), "OPERADOR_ASIGNACION", yyline, yycolumn); }

/* Signos de puntuación */
"," { return token(yytext(), "SIGNO_PUNTUACION_1", yyline, yycolumn); }
{Punto} { return token(yytext(), "SIGNO_PUNTUACION_2", yyline, yycolumn); }

/* Errores */

{Error0} { return token(yytext(), "Error_léxico_0", yyline, yycolumn); }
{Error1} { return token(yytext(), "Error_léxico_1", yyline, yycolumn); }
{Error2} { return token(yytext(), "Error_léxico_2", yyline, yycolumn); }
{Error3} { return token(yytext(), "Error_léxico_3", yyline, yycolumn); }
{Error4} { return token(yytext(), "Error_léxico_4", yyline, yycolumn); }
{Error6} { return token(yytext(), "Error_léxico_6", yyline, yycolumn); }
{Error7} { return token(yytext(), "Error_léxico_7", yyline, yycolumn); }
{Error8} { return token(yytext(), "Error_léxico_8", yyline, yycolumn); }
{Error9} { return token(yytext(), "Error_léxico_9", yyline, yycolumn); }
{Error10} { return token(yytext(), "Error_léxico_10", yyline, yycolumn); }
{Error11} { return token(yytext(), "Error_léxico_11", yyline, yycolumn); }
{Error12} { return token(yytext(), "Error_léxico_12", yyline, yycolumn); }
{Error5} { return token(yytext(), "Error_léxico_5", yyline, yycolumn); }
{Error13} { return token(yytext(), "Error_léxico_13", yyline, yycolumn); }

. { return token(yytext(), "Error_léxico_X", yyline, yycolumn); }