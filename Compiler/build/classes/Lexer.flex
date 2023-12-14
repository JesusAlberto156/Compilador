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
Error1 = {Comilla}({Comilla})*
Error2 = {GuionBajo}({GuionBajo})*

%%

/* Comentarios o espacios en blanco */
{Comentario}|{EspacioEnBlanco} { /*Ignorar*/ }

{NumeroEntero} { return token(yytext(), "VALOR_NUMERO_ENTERO", yyline, yycolumn); }
{NumeroReal} { return token(yytext(), "VALOR_NUMERO_REAL", yyline, yycolumn); }

/* Puertos */
port_A0 { return token(yytext(), "PUERTO_A0", yyline, yycolumn); }
port_A1 { return token(yytext(), "PUERTO_A1", yyline, yycolumn); }
port_A2 { return token(yytext(), "PUERTO_A2", yyline, yycolumn); }
port_A3 { return token(yytext(), "PUERTO_A3", yyline, yycolumn); }
port_A4 { return token(yytext(), "PUERTO_A4", yyline, yycolumn); }
port_A5 { return token(yytext(), "PUERTO_A5", yyline, yycolumn); }
port_B0 { return token(yytext(), "PUERTO_B0", yyline, yycolumn); }
port_B1 { return token(yytext(), "PUERTO_B1", yyline, yycolumn); }
port_B2 { return token(yytext(), "PUERTO_B2", yyline, yycolumn); }
port_B3 { return token(yytext(), "PUERTO_B3", yyline, yycolumn); }
port_B4 { return token(yytext(), "PUERTO_B4", yyline, yycolumn); }
port_B5 { return token(yytext(), "PUERTO_B5", yyline, yycolumn); }
port_B6 { return token(yytext(), "PUERTO_B6", yyline, yycolumn); }
port_B7 { return token(yytext(), "PUERTO_B7", yyline, yycolumn); }
port_C2 { return token(yytext(), "PUERTO_C2", yyline, yycolumn); }
port_C3 { return token(yytext(), "PUERTO_C3", yyline, yycolumn); }
port_C4 { return token(yytext(), "PUERTO_C4", yyline, yycolumn); }
port_C5 { return token(yytext(), "PUERTO_C5", yyline, yycolumn); }
port_C6 { return token(yytext(), "PUERTO_C6", yyline, yycolumn); }
port_C7 { return token(yytext(), "PUERTO_C7", yyline, yycolumn); }
port_D0 { return token(yytext(), "PUERTO_D0", yyline, yycolumn); }
port_D1 { return token(yytext(), "PUERTO_D1", yyline, yycolumn); }
port_D2 { return token(yytext(), "PUERTO_D2", yyline, yycolumn); }
port_D3 { return token(yytext(), "PUERTO_D3", yyline, yycolumn); }
port_D4 { return token(yytext(), "PUERTO_D4", yyline, yycolumn); }
port_D5 { return token(yytext(), "PUERTO_D5", yyline, yycolumn); }
port_D6 { return token(yytext(), "PUERTO_D6", yyline, yycolumn); }
port_D7 { return token(yytext(), "PUERTO_D7", yyline, yycolumn); }

/* Tipos de puertos */
proximity { return token(yytext(), "TIPO_PUERTO_PROXIMITY", yyline, yycolumn); }
temperature { return token(yytext(), "TIPO_PUERTO_TEMPERATURE", yyline, yycolumn); }
LED { return token(yytext(), "TIPO_PUERTO_LED", yyline, yycolumn); }
LCD { return token(yytext(), "TIPO_PUERTO_LCD", yyline, yycolumn); }
button { return token(yytext(), "TIPO_PUERTO_BUTTON", yyline, yycolumn); }
motor { return token(yytext(), "TIPO_PUERTO_MOTOR", yyline, yycolumn); }

/* Tipos de dato*/
int     { return token(yytext(), "TIPO_DATO_I", yyline, yycolumn); }
string  { return token(yytext(), "TIPO_DATO_S", yyline, yycolumn); }
decimal { return token(yytext(), "TIPO_DATO_D", yyline, yycolumn); }
boolean { return token(yytext(), "TIPO_DATO_B", yyline, yycolumn); }

/*Declaración de variables o constante */
var { return token(yytext(), "DECLARACION_V", yyline, yycolumn);}
const { return token(yytext(), "DECLARACION_C", yyline, yycolumn);}

/*Operadores logicos */
and { return token(yytext(), "OP_LOGICO_AND", yyline, yycolumn);}
or { return token(yytext(), "OP_LOGICO_OR", yyline, yycolumn); }
not { return token(yytext(), "OP_LOGICO_NOT", yyline, yycolumn); }

/*Palabras para metodos*/
return { return token(yytext(), "METODO_R", yyline, yycolumn); }
print { return token(yytext(), "METODO_P", yyline, yycolumn); }
console { return token(yytext(), "METODO_C", yyline, yycolumn); }
delay { return token(yytext(), "METODO_D", yyline, yycolumn); }
operation { return token(yytext(), "METODO_O", yyline, yycolumn); }
call { return token(yytext(), "METODO_CALL", yyline, yycolumn); }
ligther { return token(yytext(), "METODO_L", yyline, yycolumn); }

/*Palabras para el valor condicional*/
true { return token(yytext(), "VALOR_CONDICIONAL_T", yyline, yycolumn); }
false { return token(yytext(), "VALOR_CONDICIONAL_F", yyline, yycolumn); }

/*Funciones Motor*/
move { return token(yytext(), "MOTOR_M", yyline, yycolumn); }
restart { return token(yytext(), "MOTOR_R", yyline, yycolumn); }
start { return token(yytext(), "MOTOR_S", yyline, yycolumn); }

/*Metodos sensores */
distance { return token(yytext(), "METODO_SENSOR_DISTANCE", yyline, yycolumn); }
state { return token(yytext(), "METODO_SENSOR_STATE", yyline, yycolumn); }
time { return token(yytext(), "METODO_SENSOR_TIME", yyline, yycolumn); }
degree { return token(yytext(), "METODO_SENSOR_DEGREE", yyline, yycolumn); }

/*Estructuras de control*/
function { return token(yytext(), "ESTRUCTURA_DE_CONTROL_F", yyline, yycolumn); }
begin { return token(yytext(), "ESTRUCTURA_DE_CONTROL_B", yyline, yycolumn); }
loop { return token(yytext(), "ESTRUCTURA_DE_CONTROL_L", yyline, yycolumn); }
if { return token(yytext(), "ESTRUCTURA_DE_CONTROL_I", yyline, yycolumn); }
else { return token(yytext(), "ESTRUCTURA_DE_CONTROL_E", yyline, yycolumn); }

{Identificador} { return token(yytext(), "IDENTIFICADOR", yyline, yycolumn);} 
{IdentificadorCadena} { return token(yytext(), "VALOR_CADENA", yyline, yycolumn); }

"!" { return token(yytext(), "FIN_DE_LINEA", yyline, yycolumn); }

/* Operadores aritméticos */
"+" { return token(yytext(), "OP_ARITMETICO_SUMA", yyline, yycolumn); }
"-" { return token(yytext(), "OP_ARITMETICO_RESTA", yyline, yycolumn); }
"*" { return token(yytext(), "OP_ARITMETICO_MULTI", yyline, yycolumn); }
"/" { return token(yytext(), "OP_ARITMETICO_DIV", yyline, yycolumn); }

/* Operadores relacionales */
">"  { return token(yytext(), "OP_RELACIONAL_MAYOR_QUE", yyline, yycolumn); }
"<"  { return token(yytext(), "OP_RELACIONAL_MENOR_QUE", yyline, yycolumn); }
">=" { return token(yytext(), "OP_RELACIONAL_MAYOR_IGUAL_QUE", yyline, yycolumn); }
"<=" { return token(yytext(), "OP_RELACIONAL_MENOR_IGUAL_QUE", yyline, yycolumn); }

/* Signos de agrupación */
"{" { return token(yytext(), "L_LLAVE", yyline, yycolumn); }
"}" { return token(yytext(), "R_LLAVE", yyline, yycolumn); }
"(" { return token(yytext(), "L_PARENT", yyline, yycolumn); }
")" { return token(yytext(), "R_PARENT", yyline, yycolumn); }

/* Operadores de asignación */
"="  { return token(yytext(), "OP_ASIGNACION", yyline, yycolumn); }

/* Signos de puntuación */
"," { return token(yytext(), "SIGNO_PUNTUACION_COMA", yyline, yycolumn); }
{Punto} { return token(yytext(), "SIGNO_PUNTUACION_PUNTO", yyline, yycolumn); }

/* Errores */

{Error0} { return token(yytext(), "ERROR_0", yyline, yycolumn); }
{Error1} { return token(yytext(), "ERROR_1", yyline, yycolumn); }
{Error2} { return token(yytext(), "ERROR_2", yyline, yycolumn); }

. { return token(yytext(), "ERROR_X", yyline, yycolumn); }