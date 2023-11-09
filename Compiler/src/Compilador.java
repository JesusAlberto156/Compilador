
import com.formdev.flatlaf.FlatIntelliJLaf;
import compilerTools.CodeBlock;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import compilerTools.Directory;
import compilerTools.ErrorLSSL;
import compilerTools.Functions;
import compilerTools.Grammar;
import compilerTools.Production;
import compilerTools.TextColor;
import compilerTools.Token;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author yisus
 */
public class Compilador extends javax.swing.JFrame {

    private String title;
    private Directory directorio;
    private ArrayList<Token> tokens;
    private ArrayList<ErrorLSSL> errors;
    private ArrayList<TextColor> textsColor;
    private Timer timerKeyReleased;
    private ArrayList<Production> identProd;
    private HashMap<String, String> identificadores;
    private boolean codeHasBeenCompiled = false;
    /**
     * Creates new form Compilador
     */
    public Compilador() {
        initComponents();
        init();
    }

    private void init() {
        title = "RoboKit Compilador";
        setLocationRelativeTo(null);
        setTitle(title);
        directorio = new Directory(this, jtpCode, title, ".comp");
        addWindowListener(new WindowAdapter() {// Cuando presiona la "X" de la esquina superior derecha
            @Override
            public void windowClosing(WindowEvent e) {
                directorio.Exit();
                System.exit(0);
            }
        });
        Functions.setLineNumberOnJTextComponent(jtpCode);
        timerKeyReleased = new Timer((int) (1000 * 0.3), (ActionEvent e) -> {
            timerKeyReleased.stop();
            // Modificación panel de texto
            int posicion = jtpCode.getCaretPosition();
            jtpCode.setText(jtpCode.getText().replaceAll("[\r]+", ""));
            jtpCode.setCaretPosition(posicion);
            colorAnalysis();
        });
        Functions.insertAsteriskInName(this, jtpCode, () -> {
            timerKeyReleased.restart();
        });
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        textsColor = new ArrayList<>();
        identProd = new ArrayList<>();
        identificadores = new HashMap<>();
        Functions.setAutocompleterJTextComponent(new String[]{}, jtpCode, () -> {
            timerKeyReleased.restart();
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new javax.swing.JPanel();
        buttonsFilePanel = new javax.swing.JPanel();
        btnAbrir = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnGuardarC = new javax.swing.JButton();
        btnCompilar = new javax.swing.JButton();
        btnEjecutar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpCode = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaOutputConsole = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        rootPanel.setBackground(new java.awt.Color(255, 255, 255));

        btnAbrir.setText("Abrir");
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });

        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnGuardarC.setText("Guardar como");
        btnGuardarC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCActionPerformed(evt);
            }
        });

        btnCompilar.setText("Compilar");
        btnCompilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompilarActionPerformed(evt);
            }
        });

        btnEjecutar.setText("Ejecutar");
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsFilePanelLayout = new javax.swing.GroupLayout(buttonsFilePanel);
        buttonsFilePanel.setLayout(buttonsFilePanelLayout);
        buttonsFilePanelLayout.setHorizontalGroup(
            buttonsFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsFilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAbrir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardarC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCompilar)
                .addGap(18, 18, 18)
                .addComponent(btnEjecutar)
                .addContainerGap())
        );
        buttonsFilePanelLayout.setVerticalGroup(
            buttonsFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsFilePanelLayout.createSequentialGroup()
                .addGroup(buttonsFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addComponent(btnAbrir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCompilar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEjecutar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jtpCode.setFont(new java.awt.Font("Consolas", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(jtpCode);

        jtaOutputConsole.setEditable(false);
        jtaOutputConsole.setBackground(new java.awt.Color(255, 255, 255));
        jtaOutputConsole.setColumns(20);
        jtaOutputConsole.setRows(5);
        jScrollPane2.setViewportView(jtaOutputConsole);

        jLabel1.setText("Output");

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(rootPanelLayout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(jLabel1))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonsFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(287, 287, 287))
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonsFilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );

        getContentPane().add(rootPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompilarActionPerformed
        if (getTitle().contains("*") || getTitle().equals(title)) {
            if (directorio.Save()) {
                compile();
            }
        } else {
            compile();
        }
    }//GEN-LAST:event_btnCompilarActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        btnCompilar.doClick();
        if (codeHasBeenCompiled) {
            if (!                                                                                                                                                                                    errors.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se puede ejecutar el código ya que se encontró uno o más errores",
                        "Error en la compilación", JOptionPane.ERROR_MESSAGE);
            } else {
                CodeBlock codeBlock = Functions.splitCodeInCodeBlocks(tokens, "{", "}", ";");
                System.out.println(codeBlock);
                ArrayList<String> blocksOfCode = codeBlock.getBlocksOfCodeInOrderOfExec();
                System.out.println(blocksOfCode);

            }
        }
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnGuardarCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCActionPerformed
        if (directorio.SaveAs()) {
            clearFields();
        }
    }//GEN-LAST:event_btnGuardarCActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (directorio.Save()) {
            clearFields();
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        directorio.New();
        clearFields();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        if (directorio.Open()) {
            colorAnalysis();
            clearFields();
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void compile() {
        clearFields();
        lexicalAnalysis();
        fillTableTokens();
        syntacticAnalysis(identProd);
        semanticAnalysis();
        printConsole();
        codeHasBeenCompiled = true;
    }

    private void lexicalAnalysis() {
        // Extraer tokens
        Lexer lexer;
        try {
            File codigo = new File("code.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = jtpCode.getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
            lexer = new Lexer(entrada);
            while (true) {
                Token token = lexer.yylex();
                if (token == null) {
                    break;
                }
                tokens.add(token);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error al escribir en el archivo... " + ex.getMessage());
        }
    }

    private void syntacticAnalysis(ArrayList<Production> identProd)  {
        Grammar gramatica = new Grammar(tokens, errors);
        
        
        /* Eliminacion de errores */
        gramatica.delete("ERROR_X", 1, "----------> ERROR_X:  Error desconocido, verififque como está escrito el código, Linea [#] Columna [%]");
        gramatica.delete("ERROR_0", 1, "----------> ERROR_0:  El carácter no es válido en el lenguaje, Linea [#] Columna [%]");
        gramatica.delete("ERROR_1", 1, "----------> ERROR_1:  El carácter comilla no está asociado a una cadena, Linea [#] Columna [%]");
        gramatica.delete("ERROR_2", 1, "----------> ERROR_2:  El carácter guion bajo no está asociado a un puerto, Linea [#] Columna [%]");
        
        /* Agrupación de valores */
        gramatica.group("VALOR_BOOLEANO", "(VALOR_CONDICIONAL_|VALOR_CONDICIONAL_F)");
        gramatica.group("VALOR_NUMERO_ENTERO", "(VALOR_NUMERO_ENTERO)"); 
        gramatica.group("VALOR_NUMERO_REAL", "(VALOR_NUMERO_REAL)"); 
        gramatica.group("VALOR_CADENA", "(VALOR_CADENA)");
        
        gramatica.group("VALORES", "(VALOR_BOOLEANO | VALOR_NUMERO_ENTERO | VALOR_NUMERO_REAL | VALOR_CADENA)");
        
        /* Agrupacion de palabras reservadas*/
        gramatica.group("PUERTOS", "(PUERTO_1 | PUERTO_2 | PUERTO_3 | PUERTO_4 | PUERTO_5 | PUERTO_6 | PUERTO_7 | PUERTO_8 | PUERTO_9)");
        gramatica.group("TIPO_PUERTOS", "(TIPO_PUERTO_PROXIMITY | TIPO_PUERTO_TEMPERATURE | TIPO_PUERTO_LED | TIPO_PUERTO_LED_RGB | TIPO_PUERTO_LCD | TIPO_PUERTO_SPEAKER | TIPO_PUERTO_JOYSTICK | TIPO_PUERTO_BUTTON | TIPO_PUERTO_MOTOR)");
        gramatica.group("DECLARACIONES", "(DECLARACION_V | DECLARACION_C)");
        gramatica.group("TIPO_DATOS", "(TIPO_DATO_I | TIPO_DATO_S | TIPO_DATO_D | TIPO_DATO_B)");
        gramatica.group("MOTORES", "(MOTOR_M | MOTOR_R | MOTOR_S)");
        gramatica.group("SENSORES", "(METODO_SENSOR_DISTANCE | METODO_SENSOR_COLOR | METODO_SENSOR_STATE | METODO_SENSOR_POSITION | METODO_SENSOR_DEGREE)");
        
        /* Declaraciones de puertos */
        gramatica.group("DECLARACIONES_PUERTO", "PUERTOS TIPO_PUERTOS IDENTIFICADOR FIN_DE_LINEA",true);
        
        /* Declaraciones */
        gramatica.group("DECLARACIONES_SIN_VALOR", "DECLARACIONES TIPO_DATOS IDENTIFICADOR FIN_DE_LINEA",true);
        gramatica.group("DECLARACIONES_CON_VALOR", "DECLARACIONES TIPO_DATOS IDENTIFICADOR OP_ASIGNACION VALORES FIN_DE_LINEA",true);
                
        /* Asignaciones de valor a las variables*/
        gramatica.group("ASIGNACIONES", "IDENTIFICADOR OP_ASIGNACION VALORES FIN_DE_LINEA",true);
        
        /* Motores */
        gramatica.group("MOTORES_CON_VALOR", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES R_PARENT ",true);
        gramatica.group("MOTORES_SIN_VALOR", "MOTORES L_PARENT IDENTIFICADOR R_PARENT FIN_DE_LINEA",true);
        
        /* Impresora */
        gramatica.group("IMPRESORA_A_CONSOLA", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA");
        gramatica.group("IMPRESORA_A_LCD", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA");
        
        /* Palabras reservadas de las estructas de control */
        gramatica.group("BEGIN","ESTRUCTURA_DE_CONTROL_B");
        gramatica.group("LOOP","ESTRUCTURA_DE_CONTROL_L");
        gramatica.group("FUNCION","ESTRUCTURA_DE_CONTROL_F");
        gramatica.group("IF","ESTRUCTURA_DE_CONTROL_I");
        gramatica.group("ELSE","ESTRUCTURA_DE_CONTROL_E");
        
        /* Operadores */
        gramatica.group("OPERADORES_ARITMETICOS","(OP_ARITMETICO_SUMA | OP_ARITMETICO_RESTA | OP_ARITMETICO_MULTI | OP_ARITMETICO_DIV)");
        gramatica.group("OPERADORES_LOGICOS","(OP_LOGICO_AND | OP_LOGICO_OR | OP_LOGICO_NOT)");
        gramatica.group("OPERADORES_RELACIONALES","(OP_RELACIONAL_MENOR_QUE | OP_RELACIONAL_MAYOR_QUE | OP_RELACIONAL_MAYOR_IGUAL_QUE | OP_RELACIONAL_MENOR_IGUAL_QUE)");
        
        /* Oprecaiones aritmeticas*/
        gramatica.group("OPERACIONES", "IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR OPERADORES_ARITMETICOS IDENTIFICADOR FIN_DE_LINEA");
        
        /* Estructura Funcion */
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",true);
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",true);
        
        /* Llamada de funciones */
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR", "IDENTIFICADOR L_PARENT R_PARENT FIN_DE_LINEA");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR", "IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA");
        
        /* Estructura Begin */
        gramatica.group("BLOQUE_BEGIN", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",true);

        /* Metodo de los sensores */
        gramatica.group("METODO_SENSORES", "IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT FIN_DE_LINEA");
        
        /* Estructura else */
        gramatica.group("BLOQUE_ELSE_RELACIONAL", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_ELSE_LOGICO_1", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_ELSE_LOGICO_2", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        
        /* Estructura If */
        gramatica.group("BLOQUE_IF_RELACIONAL", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_IF_LOGICO_1", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_IF_LOGICO_2", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        
        /* Estructura Loop */
        gramatica.group("BLOQUE_LOOP", "LOOP L_LLAVE (METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)* R_LLAVE",true);
        
        /* Bloque de Begin y Loop*/
        gramatica.group("BLOQUE_GLOBAL_BL", "(BLOQUE_LOOP | BLOQUE_BEGIN)*");
        
        /* Bloque de Funciones */
        gramatica.group("BLOQUE_GLOBAL_F", "(BLOQUE_FUNCION_CON_PARAMETROS | BLOQUE_FUNCION_SIN_PARAMETROS)*"); 
        
        /* Bloque de declaraciones de puerto*/
        gramatica.group("BLOQUE_GLOBAL_DP","(DECLARACIONES_PUERTO)*");
        
        /* Bloque de declaraciones de variables*/
        gramatica.group("BLOQUE_GLOBAL_D","(DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR)*");
        
        /* Bloque de declaraciones de puertos y variables*/
        gramatica.group("BLOQUE_GLOBAL_DP_D","(BLOQUE_GLOBAL_DP | BLOQUE_GLOBAL_D)*");
        
        /* Bloque de delraciones de puerto y variables con las asignaciones*/
        gramatica.group("BLOQUE_GLOBAL_DP_D_A","(BLOQUE_GLOBAL_DP_D | ASIGNACIONES)*");
        
        /* Bloque de Declaraciones de puerto y variables y asignaciones con las funciones */
        gramatica.group("BLOQUE_GLOBAL_DP_D_A_F","(BLOQUE_GLOBAL_F | BLOQUE_GLOBAL_DP_D_A)*"); 
        
        /* Bloque de Begin y Loop con las declaraciones de puerto y variables y asignaciones y funciones */
        gramatica.group("BLOQUE_GLOBAL","(BLOQUE_GLOBAL_DP_D_A_F | BLOQUE_GLOBAL_BL)*"); 
        
        
        gramatica.show();
    }

    private void semanticAnalysis() {
    }

    private void colorAnalysis() {
        /* Limpiar el arreglo de colores */
        textsColor.clear();
        /* Extraer rangos de colores */
        LexerColor lexerColor;
        try {
            File codigo = new File("color.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = jtpCode.getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
            lexerColor = new LexerColor(entrada);
            while (true) {
                TextColor textColor = lexerColor.yylex();
                if (textColor == null) {
                    break;
                }
                textsColor.add(textColor);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error al escribir en el archivo... " + ex.getMessage());
        }
        Functions.colorTextPane(textsColor, jtpCode, new Color(40, 40, 40));
    }

    private void fillTableTokens() {
        tokens.forEach(token -> {
            Object[] data = new Object[]{token.getLexicalComp(), token.getLexeme(), "[" + token.getLine() + ", " + token.getColumn() + "]"};
           // Functions.addRowDataInTable(tblTokens, data);
        });
    }

    private void printConsole() {
        int sizeErrors = errors.size();
        if (sizeErrors > 0) {
            Functions.sortErrorsByLineAndColumn(errors);
            String strErrors = "\n";
            for (ErrorLSSL error : errors) {
                String strError = String.valueOf(error);
                strErrors += strError + "\n";
            }
            jtaOutputConsole.setText("RoboKit compilado ...\n" + strErrors + "\nLa compilación terminó con los siguientes errores...");
        } else {
            jtaOutputConsole.setText("RoboKit compilado ... \n Se compiló con éxito sin ningún tipo de error.");
        }
        jtaOutputConsole.setCaretPosition(0);
    }

    private void clearFields() {
       // Functions.clearDataInTable(tblTokens);
        jtaOutputConsole.setText("");
        tokens.clear();
        errors.clear();
        identProd.clear();
        identificadores.clear();
        codeHasBeenCompiled = false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                System.out.println("LookAndFeel no soportado: " + ex);
            }
            new Compilador().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnCompilar;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnGuardarC;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JPanel buttonsFilePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jtaOutputConsole;
    private javax.swing.JTextPane jtpCode;
    private javax.swing.JPanel rootPanel;
    // End of variables declaration//GEN-END:variables
}
