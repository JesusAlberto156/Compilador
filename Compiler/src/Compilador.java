
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
import java.awt.Desktop;
import java.awt.Font;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author yisus
 */
public class Compilador extends javax.swing.JFrame {

    /*Pantalla externas*/
    private Tokens T = new Tokens();
    private Errores Error = new Errores();
    
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
        Functions.setAutocompleterJTextComponent(new String[]
            {"port_1 'TIPO PUERTO' 'IDENTIFICADOR'!","port_3 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_3 'TIPO PUERTO' 'IDENTIFICADOR'!","port_4 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_5 'TIPO PUERTO' 'IDENTIFICADOR'!","port_6 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_7 'TIPO PUERTO' 'IDENTIFICADOR'!","port_8 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_9 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_1 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_3 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_3 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_4 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_5 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_6 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_7 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_8 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_9 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "const int 'IDENTIFICADOR' = 'VALORES'!","const string 'IDENTIFICADOR' = 'VALORES'!",
             "const decimal 'IDENTIFICADOR' = 'VALORES'!","const boolean 'IDENTIFICADOR' = 'VALORES'!",
             "var int 'IDENTIFICADOR' = 'VALORES'!","var string 'IDENTIFICADOR' = 'VALORES'!",
             "var decimal 'IDENTIFICADOR' = 'VALORES'!","var boolean 'IDENTIFICADOR' = 'VALORES'!",
             "const int 'IDENTIFICADOR'!","const string 'IDENTIFICADOR'!","const decimal 'IDENTIFICADOR'!",
             "const boolean 'IDENTIFICADOR'!","var int 'IDENTIFICADOR'!","var string 'IDENTIFICADOR'!",
             "var decimal 'IDENTIFICADOR'!","var boolean 'IDENTIFICADOR'!","&const int 'IDENTIFICADOR' = 'VALORES'!",
             "&const string 'IDENTIFICADOR' = 'VALORES'!","&const decimal 'IDENTIFICADOR' = 'VALORES'!",
             "&const boolean 'IDENTIFICADOR' = 'VALORES'!","&var int 'IDENTIFICADOR' = 'VALORES'!",
             "&var string 'IDENTIFICADOR' = 'VALORES'!","&var decimal 'IDENTIFICADOR' = 'VALORES'!",
             "&var boolean 'IDENTIFICADOR' = 'VALORES'!","&const int 'IDENTIFICADOR'!","&const string 'IDENTIFICADOR'!",
             "&const decimal 'IDENTIFICADOR'!","&const boolean 'IDENTIFICADOR'!","&var int 'IDENTIFICADOR'!",
             "&var string 'IDENTIFICADOR'!","&var decimal 'IDENTIFICADOR'!","&var boolean 'IDENTIFICADOR'!",
             "'IDENTIFICADOR' = 'VALORES'!","&'IDENTIFICADOR' = 'VALORES'!","move('VALORES')!","restart()!","start()!",
             "&move('VALORES')!","&restart()!","&start()!","print(console,'IDENTIFICADOR|VALORES')!",
             "print('IDENTIFICADOR','IDENTIFICADOR|VALORES')!","&print(console,'IDENTIFICADOR|VALORES')!",
             "&print('IDENTIFICADOR','IDENTIFICADOR|VALORES')!","operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' + 'IDENTIFICADOR|VALORES'!",
             "operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' - 'IDENTIFICADOR|VALORES'!","operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' * 'IDENTIFICADOR|VALORES'!",
             "operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' / 'IDENTIFICADOR|VALORES'!","&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' + 'IDENTIFICADOR|VALORES'!",
             "&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' - 'IDENTIFICADOR|VALORES'!","&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' * 'IDENTIFICADOR|VALORES'!",
             "&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' / 'IDENTIFICADOR|VALORES'!",
             "function 'IDENTIFICADOR'(){\n  'METODO_MOTOR'\n  'METODO_IMPRESORA'\n}",
             "function 'TIPO DATO' 'IDENTIFICADOR'('TIPO DATO' 'IDENTIFICADOR'){\n  'OPERACION'\n  return 'IDENTIFICADOR'!\n}",
             "&function 'IDENTIFICADOR'(){\n  'METODO_MOTOR'\n  'METODO_IMPRESORA'\n}",
             "&function 'TIPO DATO' 'IDENTIFICADOR'('TIPO DATO' 'IDENTIFICADOR'){\n  'OPERACION'\n  return 'IDENTIFICADOR'!\n}",
             "call 'IDENTIFICADOR'('VALORES')!","call 'IDENTIFICADOR'()!","&call 'IDENTIFICADOR'('VALORES')!","&call 'IDENTIFICADOR'()!",
             "begin{\n  'LLAMADA_FUNCION'\n  'METODO_MOTOR'\n}","&begin{\n  'LLAMADA_FUNCION'\n  'METODO_MOTOR'\n}",
             "call 'IDENTIFICADOR' = 'IDENTIFICADOR'.'METODO SENSOR'()!","&call 'IDENTIFICADOR' = 'IDENTIFICADOR'.'METODO SENSOR'()!",
             "delay('IDENTIFICADOR|VALORES')!","&delay('IDENTIFICADOR|VALORES')!",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "if('IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}else{\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}else{\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "if('IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}else{\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "&if('IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}else{\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}else{\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "&if('IDENTIFICADOR | VALORES'){\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}else{\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n}",
             "loop{\n  'METODO_SENSOR'\n  'METODO_IMPRESORA'\n  'ASIGNACION'\n  'DECLARACION'\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'IF'\n  'METODO_DELAY'\n}",
             "&loop{\n  'METODO_SENSOR'\n  'METODO_IMPRESORA'\n  'ASIGNACION'\n  'DECLARACION'\n  'METODO_MOTOR'\n  'LLAMADA_FUNCION'\n  'IF'\n  'METODO_DELAY'\n}"}, jtpCode, () -> {
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
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();

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
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAbrir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGuardarC, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCompilar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonsFilePanelLayout.setVerticalGroup(
            buttonsFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsFilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEjecutar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCompilar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAbrir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/MenosZoom.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/MasZoom.png"))); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/MenosZoom.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/MasZoom.png"))); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(buttonsFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton10))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addComponent(buttonsFilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(rootPanel);

        jMenu1.setText("Archivo");

        jMenuItem1.setText("Nuevo");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Abrir");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Guardar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Guardar como");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator1);

        jMenuItem5.setText("Compilar");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("Ejecutar");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ayuda");

        jMenuItem7.setText("Lenguaje");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText("Componentes léxicos");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText("Estructuras");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Documentación");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("Acerca de");
        jMenuBar1.add(jMenu4);

        jMenu5.setText("Funcionamiento");

        jMenu8.setText("Análisis");

        jMenuItem12.setText("Léxico");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem12);

        jMenuItem14.setText("Sintáctico");
        jMenu8.add(jMenuItem14);

        jMenu5.add(jMenu8);

        jMenuBar1.add(jMenu5);

        jMenu7.setText("Código ejemplo");
        jMenuBar1.add(jMenu7);

        jMenu6.setText("Tablas");

        jMenuItem10.setText("Tokens");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem10);

        jMenuItem13.setText("Errores");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem13);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

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

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        directorio.New();
        clearFields();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        if (directorio.Open()) {
            colorAnalysis();
            clearFields();
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (directorio.Save()) {
            clearFields();
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (directorio.SaveAs()) {
            clearFields();
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if (getTitle().contains("*") || getTitle().equals(title)) {
            if (directorio.Save()) {
                compile();
            }
        } else {
            compile();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
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
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        try{
            URL ruta = getClass().getResource("/Ayuda/Lenguaje.pdf");
            String rutaNueva = ruta.getFile();
            File ruta2 = new File(rutaNueva.replaceAll("%20"," "));
            Desktop.getDesktop().open(ruta2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        try{
            URL ruta = getClass().getResource("/Ayuda/Componentes lexicos.pdf");
            String rutaNueva = ruta.getFile();
            File ruta2 = new File(rutaNueva.replaceAll("%20"," "));
            Desktop.getDesktop().open(ruta2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        try{
            URL ruta = getClass().getResource("/Ayuda/Estructuras.pdf");
            String rutaNueva = ruta.getFile();
            File ruta2 = new File(rutaNueva.replaceAll("%20"," "));
            Desktop.getDesktop().open(ruta2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        try{
            URL ruta = getClass().getResource("/Funcionamiento/Analisis lexico.pdf");
            String rutaNueva = ruta.getFile();
            File ruta2 = new File(rutaNueva.replaceAll("%20"," "));
            Desktop.getDesktop().open(ruta2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        T.setVisible(true);
        T.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        Error.setVisible(true);
        Error.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String fontName = jtpCode.getFont().getFontName();
        int fontSize = jtpCode.getFont().getSize() - 2;
        
        Font font = new Font(fontName, 0, fontSize);
        
        jtpCode.setFont(font);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String fontName = jtpCode.getFont().getFontName();
        int fontSize = jtpCode.getFont().getSize() + 2;
        
        Font font = new Font(fontName, 0, fontSize);
        
        jtpCode.setFont(font);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        String fontName = jtaOutputConsole.getFont().getFontName();
        int fontSize = jtaOutputConsole.getFont().getSize() - 2;
        
        Font font = new Font(fontName, 0, fontSize);
        
        jtaOutputConsole.setFont(font);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String fontName = jtaOutputConsole.getFont().getFontName();
        int fontSize = jtaOutputConsole.getFont().getSize() + 2;
        
        Font font = new Font(fontName, 0, fontSize);
        
        jtaOutputConsole.setFont(font);
    }//GEN-LAST:event_jButton10ActionPerformed

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
        gramatica.delete("ERROR_X", 0, "----------> Error_XX:  Error desconocido, verififque como está escrito el código, Linea [#] Columna [%]");
        gramatica.delete("ERROR_0", 0, "----------> Error_00:  El carácter no es válido en el lenguaje, Linea [#] Columna [%]");
        gramatica.delete("ERROR_1", 1, "----------> Error_01:  El carácter comilla no está asociado a una cadena, Linea [#] Columna [%]");
        gramatica.delete("ERROR_2", 2, "----------> Error_02:  El carácter guión bajo no está asociado a un puerto, Linea [#] Columna [%]");
        
        /* Agrupación de valores */
        gramatica.group("VALOR_BOOLEANO", "(VALOR_CONDICIONAL_T|VALOR_CONDICIONAL_F)");
        gramatica.group("VALOR_NUMERO_ENTERO", "(VALOR_NUMERO_ENTERO)"); 
        gramatica.group("VALOR_NUMERO_REAL", "(VALOR_NUMERO_REAL)"); 
        gramatica.group("VALOR_CADENA", "(VALOR_CADENA)");
        
        gramatica.group("VALORES", "(VALOR_BOOLEANO | VALOR_NUMERO_ENTERO | VALOR_NUMERO_REAL | VALOR_CADENA)");
        
        /* Agrupacion de palabras reservadas*/
        gramatica.group("PUERTOS", "(PUERTO_1 | PUERTO_2 | PUERTO_3 | PUERTO_4 | PUERTO_5 | PUERTO_6 | PUERTO_7 | PUERTO_8 | PUERTO_9)");
        gramatica.group("TIPO_PUERTOS", "(TIPO_PUERTO_PROXIMITY | TIPO_PUERTO_TEMPERATURE | TIPO_PUERTO_LED | TIPO_PUERTO_LED_RGB | TIPO_PUERTO_LCD | TIPO_PUERTO_BUTTON | TIPO_PUERTO_MOTOR)");
        gramatica.group("DECLARACIONES", "(DECLARACION_V | DECLARACION_C)");
        gramatica.group("TIPO_DATOS", "(TIPO_DATO_I | TIPO_DATO_S | TIPO_DATO_D | TIPO_DATO_B)");
        gramatica.group("MOTORES", "(MOTOR_M | MOTOR_R | MOTOR_S)");
        gramatica.group("SENSORES", "(METODO_SENSOR_DISTANCE | METODO_SENSOR_TIME | METODO_SENSOR_STATE | METODO_SENSOR_DEGREE)");
        
        /* Declaraciones de puertos */
        gramatica.group("DECLARACIONES_PUERTO", "PUERTOS TIPO_PUERTOS IDENTIFICADOR FIN_DE_LINEA",true);
        
        /*ERRORES DE LA DECLARACION DE PUERTO*/
        gramatica.group("DECLARACIONES_PUERTO_E", " TIPO_PUERTOS IDENTIFICADOR FIN_DE_LINEA",3,
                "----------> Error_03:  Falta la palabra reservada en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS IDENTIFICADOR FIN_DE_LINEA",4,
                "----------> Error_04:  Falta el tipo de puerto en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS TIPO_PUERTOS FIN_DE_LINEA",6,
                "----------> Error_06:  Falta el identificador en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS TIPO_PUERTOS IDENTIFICADOR",9,
                "----------> Error_09:  Falta el ! en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS TIPO_PUERTOS",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS IDENTIFICADOR",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_PUERTO_E", "PUERTOS",11,
                "----------> Error_11:  La palabra reservada no está en una declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_PUERTO_E", "TIPO_PUERTOS",11,
                "----------> Error_11:  La palabra reservada no está en una declaración, Linea [#] Columna [%]");
        /*ERRORES DE LA DECLARACION DE PUERTO*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("DECLARACIONES_PUERTO_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Declaraciones */
        gramatica.group("DECLARACIONES_CON_VALOR", "DECLARACIONES TIPO_DATOS IDENTIFICADOR OP_ASIGNACION VALORES FIN_DE_LINEA",true);
        gramatica.group("DECLARACIONES_SIN_VALOR", "DECLARACIONES TIPO_DATOS IDENTIFICADOR FIN_DE_LINEA",true);
        
        /*ERRORES DE LA DECLARACION CON VALOR*/
        gramatica.group("DECLARACIONES_CON_VALOR_E", "TIPO_DATOS IDENTIFICADOR OP_ASIGNACION VALORES FIN_DE_LINEA",3,
                "----------> Error_03:  Falta la palabra reservada en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR OP_ASIGNACION VALORES FIN_DE_LINEA",5,
                "----------> Error_05:  Falta el tipo de dato en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS OP_ASIGNACION VALORES FIN_DE_LINEA",6,
                "----------> Error_06:  Falta el identificador en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR VALORES FIN_DE_LINEA",7,
                "----------> Error_07:  Falta el = en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR OP_ASIGNACION FIN_DE_LINEA",8,
                "----------> Error_08:  Falta el valor en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR OP_ASIGNACION VALORES",9,
                "----------> Error_09:  Falta el ! en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES OP_ASIGNACION VALORES FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR VALORES FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR OP_ASIGNACION FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR OP_ASIGNACION VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS VALORES FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS OP_ASIGNACION FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS OP_ASIGNACION VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR OP_ASIGNACION",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES VALORES FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES OP_ASIGNACION FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES OP_ASIGNACION VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR OP_ASIGNACION",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS OP_ASIGNACION",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES VALORES",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES OP_ASIGNACION",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES IDENTIFICADOR",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_CON_VALOR_E", "DECLARACIONES TIPO_DATOS",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        /*ERRORES DE LA DECLARACION CON VALOR*/
        /*ERRORES DE LA DECLARACION SIN VALOR*/
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "TIPO_DATOS IDENTIFICADOR FIN_DE_LINEA",3,
                "----------> Error_03:  Falta la palabra reservada en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES IDENTIFICADOR FIN_DE_LINEA",5,
                "----------> Error_05:  Falta el tipo de dato en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES TIPO_DATOS FIN_DE_LINEA",6,
                "----------> Error_06:  Falta el identificador en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES TIPO_DATOS IDENTIFICADOR",9,
                "----------> Error_09:  Falta el ! en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES TIPO_DATOS",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES IDENTIFICADOR",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES FIN_DE_LINEA",10,
                "----------> Error_10:  Faltan dos o más elementos en la declaración, Linea [#] Columna [%]");
        
        gramatica.group("DECLARACIONES_SIN_VALOR_E", "DECLARACIONES",11,
                "----------> Error_11:  La palabra reservada no está en una declaración, Linea [#] Columna [%]");
        /*ERRORES DE LA DECLARACION SIN VALOR*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("DECLARACIONES_SIN_VALOR_E");
        gramatica.delete("DECLARACIONES_CON_VALOR_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Asignaciones de valor a las variables*/
        gramatica.group("ASIGNACIONES", "IDENTIFICADOR OP_ASIGNACION VALORES FIN_DE_LINEA",true);
        
        /* Motores */
        gramatica.group("MOTORES_CON_VALOR", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES R_PARENT FIN_DE_LINEA",true);
        gramatica.group("MOTORES_SIN_VALOR", "MOTORES L_PARENT IDENTIFICADOR R_PARENT FIN_DE_LINEA",true);
        
        /*ERRORES DE LA FUNCIONES DE MOTORES CON VALOR*/
        gramatica.group("MOTORES_CON_VALOR_E", "L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES R_PARENT FIN_DE_LINEA",17,
                "----------> Error_17:  Falta la palabra reservada en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES R_PARENT FIN_DE_LINEA",18,
                "----------> Error_18:  Falta el ( o ) o ambos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES FIN_DE_LINEA",18,
                "----------> Error_18:  Falta el ( o ) o ambos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES FIN_DE_LINEA",18,
                "----------> Error_18:  Falta el ( o ) o ambos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA VALORES R_PARENT FIN_DE_LINEA",19,
                "----------> Error_19:  Falta el identificador en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR VALORES R_PARENT FIN_DE_LINEA",20,
                "----------> Error_20:  Falta la , en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",21,
                "----------> Error_21:  Falta el valor en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES R_PARENT",22,
                "----------> Error_22:  Falta el ! en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA VALORES R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR VALORES R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT VALORES R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA VALORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR VALORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES VALORES R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA VALORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR VALORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT VALORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES VALORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES VALORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR SIGNO_PUNTUACION_COMA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT SIGNO_PUNTUACION_COMA VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES VALORES",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES SIGNO_PUNTUACION_COMA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES IDENTIFICADOR",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_CON_VALOR_E", "MOTORES L_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");        
        /*ERRORES DE LA FUNCIONES DE MOTORES CON VALOR*/
        /*ERRORES DE LA FUNCIONES DE MOTORES SIN VALOR*/
        gramatica.group("MOTORES_SIN_VALOR_E", "L_PARENT IDENTIFICADOR R_PARENT FIN_DE_LINEA",17,
                "----------> Error_17:  Falta la palabra reservada en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES IDENTIFICADOR R_PARENT FIN_DE_LINEA",18,
                "----------> Error_18:  Falta el ( o ) o ambos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR FIN_DE_LINEA",18,
                "----------> Error_18:  Falta el ( o ) o ambos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES IDENTIFICADOR FIN_DE_LINEA",18,
                "----------> Error_18:  Falta el ( o ) o ambos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT R_PARENT FIN_DE_LINEA",19,
                "----------> Error_19:  Falta el identificador en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR R_PARENT",22,
                "----------> Error_22:  Falta el ! en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES R_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES IDENTIFICADOR R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT IDENTIFICADOR",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES FIN_DE_LINEA",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES R_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES L_PARENT",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES IDENTIFICADOR",23,
                "----------> Error_23:  Faltan dos o más elementos en la función del motor, Linea [#] Columna [%]");
        
        gramatica.group("MOTORES_SIN_VALOR_E", "MOTORES",24,
                "----------> Error_24:  La palabra reservada no está en una función del motor, Linea [#] Columna [%]");
        /*ERRORES DE LA FUNCIONES DE MOTORES SIN VALOR*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("MOTORES_SIN_VALOR_E");
        gramatica.delete("MOTORES_CON_VALOR_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Impresora */
        gramatica.group("IMPRESORA_A_CONSOLA", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA");
        gramatica.group("IMPRESORA_A_LCD", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA");
        
        /*ERRORES DEL METODO DE LA IMPRESORA A CONSOLA*/
        gramatica.group("IMPRESORA_A_CONSOLA_E", "L_PARENT METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",25,
                "----------> Error_25:  Falta la palabra reservada en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",26,
                "----------> Error_26:  Falta ( o ) o ambos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",26,
                "----------> Error_26:  Falta ( o ) o ambos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",26,
                "----------> Error_26:  Falta ( o ) o ambos en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",27,
                "----------> Error_27:  Falta la palabra reservada console o el identificador en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",28,
                "----------> Error_28:  Falta la , en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",29,
                "----------> Error_29:  Falta el valor o identificador en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",30,
                "----------> Error_30:  Falta el ! en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA R_PARENT ",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C SIGNO_PUNTUACION_COMA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        
        
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C SIGNO_PUNTUACION_COMA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT METODO_C",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        
        
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P SIGNO_PUNTUACION_COMA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P METODO_C",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_CONSOLA_E", "METODO_P L_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        /*ERRORES DEL METODO DE LA IMPRESORA A CONSOLA*/
        /*ERRORES DEL METODO DE LA IMPRESORA A LCD*/
        gramatica.group("IMPRESORA_A_LCD_E", "L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",25,
                "----------> Error_25:  Falta la palabra reservada en el método de la impresora, Linea [#] Columna [%]");
        
        
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",26,
                "----------> Error_26:  Falta ( o ) o ambos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",26,
                "----------> Error_26:  Falta ( o ) o ambos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",26,
                "----------> Error_26:  Falta ( o ) o ambos en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",27,
                "----------> Error_27:  Falta la palabra reservada console o el identificador en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",28,
                "----------> Error_28:  Falta la , en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",29,
                "----------> Error_29:  Falta el valor o identificador en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",30,
                "----------> Error_30:  Falta el ! en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P (VALORES | IDENTIFICADOR) R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P SIGNO_PUNTUACION_COMA R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT R_PARENT FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR) FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT (VALORES | IDENTIFICADOR) R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT SIGNO_PUNTUACION_COMA (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT IDENTIFICADOR SIGNO_PUNTUACION_COMA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P FIN_DE_LINEA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P R_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P (VALORES | IDENTIFICADOR)",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P SIGNO_PUNTUACION_COMA",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P IDENTIFICADOR",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P L_PARENT",31,
                "----------> Error_31:  Faltan dos o más elementos en el método de la impresora, Linea [#] Columna [%]");
        
        gramatica.group("IMPRESORA_A_LCD_E", "METODO_P",32,
                "----------> Error_32:  La palabra reservada no está en el método de la impresora, Linea [#] Columna [%]");
        /*ERRORES DEL METODO DE LA IMPRESORA A LCD*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("IMPRESORA_A_CONSOLA_E");
        gramatica.delete("IMPRESORA_A_LCD_E");
        /*ELIMINACION DE ERRORES*/
        
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
        gramatica.group("OPERACIONES", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA");
        
        /*ERRORES DE OPERACIONES*/
        gramatica.group("OPERACIONES_E", "IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",33,
                "----------> Error_33:  Falta la palabra reservada en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",34,
                "----------> Error_34:  Falta el identificador en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",35,
                "----------> Error_35:  Falta el = en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",36,
                "----------> Error_36:  Falta el operando 1 en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES) FIN_DE_LINEA",37,
                "----------> Error_37:  Falta el operador aritmético en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS FIN_DE_LINEA",38,
                "----------> Error_38:  Falta el operando 2 en la operación, Linea [#] Columna [%]");  
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",39,
                "----------> Error_39:  Falta el ! en la operación , Linea [#] Columna [%]");
        
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        
        gramatica.group("OPERACIONES_E", "METODO_O OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OPERADORES_ARITMETICOS FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OPERADORES_ARITMETICOS (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES) OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR OP_ASIGNACION",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        
        gramatica.group("OPERACIONES_E", "METODO_O FIN_DE_LINEA",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O (IDENTIFICADOR|VALORES)",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OPERADORES_ARITMETICOS",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O OP_ASIGNACION",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        gramatica.group("OPERACIONES_E", "METODO_O IDENTIFICADOR",40,
                "----------> Error_40:  Faltan dos o más elementos en la operación, Linea [#] Columna [%]");
        
        gramatica.group("OPERACIONES_E", "OPERADORES_ARITMETICOS",41,
                "----------> Error_41:  El operador aritmético no está en una operación, Linea [#] Columna [%]");
        
        gramatica.group("OPERACIONES_E", "METODO_O",42,
                "----------> Error_42:  La palabra reservada no está en la operación, Linea [#] Columna [%]");
        /*ERRORES DE OPERACIONES*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("OPERACIONES_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Estructura Funcion */
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",true);
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",true);
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("OPERACION_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (OPERACIONES | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("MOTOR_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR | OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",110,
                "----------> Error_110:  La función del motor no está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",111,
                "----------> Error_111:  El método impresora no está dentro de un bloque de función, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DECLARACION_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES | DECLARACIONES_CON_VALOR | DECLARACIONES_SIN_VALOR)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",116,
                "----------> Error_116:  La declaración está dentro de un bloque de función o un bloque begin, Linea [#] Columna [%]");
        gramatica.group("DECLARACION_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | DECLARACIONES_CON_VALOR | DECLARACIONES_SIN_VALOR)* R_LLAVE",116,
                "----------> Error_116:  La declaración está dentro de un bloque de función o un bloque begin, Linea [#] Columna [%]");
        gramatica.group("ASIGNACION_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES| ASIGNACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",117,
                "----------> Error_117:  La asignación está dentro de un bloque de función o un bloque begin, Linea [#] Columna [%]");
        gramatica.group("ASIGNACION_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES)* R_LLAVE",117,
                "----------> Error_117:  La asignación está dentro de un bloque de función o un bloque begin, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES | DECLARACIONES_PUERTO)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.delete("OPERACION_ESTRUCTURA_E");
        gramatica.delete("MOTOR_ESTRUCTURA_E");
        gramatica.delete("IMPRESORA_ESTRUCTURA_E");
        gramatica.delete("DECLARACION_ESTRUCTURA_E");
        gramatica.delete("ASIGNACION_ESTRUCTURA_E");
        gramatica.delete("FUNCION_ESTRUCTURA_E");
        gramatica.delete("PUERTO_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        /*ERRORES DE LAS FUNCIONES CON PARAMETROS*/
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",43,
                "----------> Error_43:  Falta la palabra reservada en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",44,
                "----------> Error_44:  Falta el tipo de dato de retorno en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",45,
                "----------> Error_45:  Falta el identificador en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",46,
                "----------> Error_46:  Falta el ( o )  o ambos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",46,
                "----------> Error_46:  Falta el ( o )  o ambos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",46,
                "----------> Error_46:  Falta el ( o )  o ambos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",47,
                "----------> Error_47:  Falta el tipo de dato en el o los parámetros en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",48,
                "----------> Error_48:  Falta el identificador en el o los parámetros en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS | SIGNO_PUNTUACION_COMA IDENTIFICADOR | SIGNO_PUNTUACION_COMA)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",49,
                "----------> Error_49:  Falta el tipo de dato o el identificador o ambos en los parámetros en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS | SIGNO_PUNTUACION_COMA IDENTIFICADOR | SIGNO_PUNTUACION_COMA | SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",49,
                "----------> Error_49:  Falta el tipo de dato o el identificador o ambos en los parámetros en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR | SIGNO_PUNTUACION_COMA TIPO_DATOS | SIGNO_PUNTUACION_COMA | SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",49,
                "----------> Error_49:  Falta el tipo de dato o el identificador o ambos en los parámetros en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT SIGNO_PUNTUACION_COMA "
                + "      (SIGNO_PUNTUACION_COMA | TIPO_DATOS | IDENTIFICADOR | TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",49,
                "----------> Error_49:  Falta el tipo de dato o el identificador o ambos en los parámetros en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR"
                + "      (TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",50,
                "----------> Error_50:  Falta la , en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA"
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",51,
                "----------> Error_51:  Falta la { o } o ambas en al función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",51,
                "----------> Error_51:  Falta la { o } o ambas en al función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",51,
                "----------> Error_51:  Falta la { o } o ambas en al función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",52,
                "----------> Error_52:  Falta la palabra reservada return en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",53,
                "----------> Error_53:  Falta el identificador de retorno en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",54,
                "----------> Error_54:  Falta el ! En la función, Linea [#] Columna [%]");
        /*DOS ELEMENTOS*/
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        /*DOS ELEMENTOS*/
        /*TRES ELEMENTOS*/
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");   
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");   
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");    
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR  R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        /*TRES ELEMENTOS*/
        /*CUATRO ELEMENTOS*/
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION  L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENTm (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)*  IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)*L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADORR_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT(OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR  (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR  (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR  TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]"); 
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)*IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR ( "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      )* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");       
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");      
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]"); 
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* IDENTIFICADOR FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* (OPERACIONES)* METODO_R IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* L_LLAVE (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* FIN_DE_LINEA",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* IDENTIFICADOR",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT (OPERACIONES)* METODO_R",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]"); 
        /*CUATRO ELEMENTOS*/
        /*CINCO ELEMENTOS*
        
        gramatica.group("BLOQUE_FUNCION_CON_PARAMETROS_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA "
                + "      TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        /*CINCO ELEMENTOS*/
        /*SEIS ELEMENTOS*/
        
        /*SEIS ELEMENTOS*/
        /*SIETE ELEMENTOS*/
        
        /*SIETE ELEMENTOS*/
        /*OCHO ELEMENTOS*/
        
        /*OCHO ELEMENTOS*/
        /*NUEVE ELEMENTOS*/
        
        /*NUEVE ELEMENTOS*/
        /*DIEZ ELEMENTOS*/
        
        /*DIEZ ELEMENTOS*/
        /*ONCE ELEMENTOS*/
        
        /*ONCE ELEMENTOS*/
        /*DOCE ELEMENTOS*/
        
        /*DOCE ELEMENTOS*/
        /*TRECE ELEMENTOS*/
        
        /*TRECE ELEMENTOS*/
        /*ERRORES DE LAS FUNCIONES CON PARAMETROS*/
        /*ERRORES DE LAS FUNCIONES SIN PARAMETROS*/
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",43,
                "----------> Error_43:  Falta la palabra reservada en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",45,
                "----------> Error_45:  Falta el identificador en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",46,
                "----------> Error_46:  Falta el ( o )  o ambos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",46,
                "----------> Error_46:  Falta el ( o )  o ambos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",46,
                "----------> Error_46:  Falta el ( o )  o ambos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",51,
                "----------> Error_51:  Falta la { o } o ambas en al función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",51,
                "----------> Error_51:  Falta la { o } o ambas en al función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",51,
                "----------> Error_51:  Falta la { o } o ambas en al función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR L_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION R_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION L_PARENT (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION IDENTIFICADOR (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)*",55,
                "----------> Error_55:  Faltan dos o más elementos en la función, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_FUNCION_SIN_PARAMETROS_E", "FUNCION",56,
                "----------> Error_56:  La palabra reservada no está en la función, Linea [#] Columna [%]");
        /*ERRORES DE LAS FUNCIONES SIN PARAMETROS*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("BLOQUE_FUNCION_CON_PARAMETROS_E");
        gramatica.delete("BLOQUE_FUNCION_SIN_PARAMETROS_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Llamada de funciones */
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR", "METODO_CALL IDENTIFICADOR L_PARENT R_PARENT FIN_DE_LINEA");
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("LLAMADA_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (LLAMADA_FUNCIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",112,
                "----------> Error_112:  La llamada de la función no está dentro del bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (LLAMADA_FUNCIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",112,
                "----------> Error_112:  La llamada de la función no está dentro del bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DELAY_ESTRUCTURA_E", "METODO_DELAY",114,
                "----------> Error_114:  El método delay no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DELAY_ESTRUCTURA_E", "METODO_DELAY",114,
                "----------> Error_114:  El método delay no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.delete("LLAMADA_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        
        /* Metodo de los sensores */
        gramatica.group("METODO_SENSORES", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT FIN_DE_LINEA");
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("SENSORES_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (METODO_SENSORES | OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",113,
                "----------> Error_113:  La función del método del sensor no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("SENSORES_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (METODO_SENSORES | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",113,
                "----------> Error_113:  La función del método del sensor no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.delete("SENSORES_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        
        /*ERRORES DE LAS ASIGNACIONES*/
        gramatica.group("ASIGNACIONES_E", "OP_ASIGNACION VALORES FIN_DE_LINEA",12,
                "----------> Error_12:  Falta el identificador en la asignación, Linea [#] Columna [%]");
        gramatica.group("ASIGNACIONES_E", "IDENTIFICADOR VALORES FIN_DE_LINEA",13,
                "----------> Error_13:  Falta el = en la asignación, Linea [#] Columna [%]");
        gramatica.group("ASIGNACIONES_E", "IDENTIFICADOR OP_ASIGNACION FIN_DE_LINEA",14,
                "----------> Error_14:  Falta el valor en la asignación, Linea [#] Columna [%]");
        gramatica.group("ASIGNACIONES_E", "IDENTIFICADOR OP_ASIGNACION VALORES",15,
                "----------> Error_15:  Falta el ! en la asignación, Linea [#] Columna [%]");
        gramatica.group("ASIGNACIONES_E", "IDENTIFICADOR FIN_DE_LINEA",16,
                "----------> Error_16:  Faltan dos elementos en la asignación, Linea [#] Columna [%]");
        gramatica.group("ASIGNACIONES_E", "IDENTIFICADOR VALORES",16,
                "----------> Error_16:  Faltan dos elementos en la asignación, Linea [#] Columna [%]");
        gramatica.group("ASIGNACIONES_E", "IDENTIFICADOR OP_ASIGNACION",16,
                "----------> Error_16:  Faltan dos elementos en la asignación, Linea [#] Columna [%]");
        /*ERRORES DE LAS ASIGNACIONES*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("ASIGNACIONES_E");
        /*ELIMINACION DE ERRORES*/
         
        /*ERRORES DE LLAMADAS DE FUNCION CON VALOR*/
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",57,
                "----------> Error_57:  Falta la palabra reservada en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",58,
                "----------> Error_58:  Falta el identificador en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES SIGNO_PUNTUACION_COMA (VALORES SIGNO_PUNTUACION)* R_PARENT FIN_DE_LINEA",59,
                "----------> Error_59:  Falta el valor en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",60,
                "----------> Error_60:  Falta el ! en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",61,
                "----------> Error_61:  Falta el ( o ) o ambos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",61,
                "----------> Error_61:  Falta el ( o ) o ambos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",61,
                "----------> Error_61:  Falta el ( o ) o ambos en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES VALORES (VALORES)* R_PARENT FIN_DE_LINEA",62,
                "----------> Error_62:  Falta la , en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT SIGNO_PUNTUACION_COMA ( | VALORES | SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",63,
                "----------> Error_63:  Falta el valor o la , en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES ( | VALORES | SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",63,
                "----------> Error_63:  Falta el valor o la , en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA VALORES)*FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (VALORES)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA)* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT VALORES ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL ()* R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (VALORES)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (VALORES)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA)* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA)* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT VALORES ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR VALORES ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL ()* FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL ()* R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (VALORES)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL (SIGNO_PUNTUACION_COMA)*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL VALORES ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL L_PARENT ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_CON_VALOR_E", "METODO_CALL IDENTIFICADOR ()*",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        /*ERRORES DE LLAMADAS DE FUNCION CON VALOR*/
        /*ERRORES DE LLAMADAS DE FUNCION SIN VALOR*/
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "IDENTIFICADOR L_PARENT R_PARENT FIN_DE_LINEA",57,
                "----------> Error_57:  Falta la palabra reservada en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL L_PARENT R_PARENT FIN_DE_LINEA",58,
                "----------> Error_58:  Falta el identificador en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT R_PARENT",60,
                "----------> Error_60:  Falta el ! en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR R_PARENT FIN_DE_LINEA",61,
                "----------> Error_61:  Falta el ( o ) o ambos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT FIN_DE_LINEA",61,
                "----------> Error_61:  Falta el ( o ) o ambos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR FIN_DE_LINEA",61,
                "----------> Error_61:  Falta el ( o ) o ambos en la llamada de la función, Linea [#] Columna [%]");
        
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL R_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL L_PARENT FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL L_PARENT R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR L_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL FIN_DE_LINEA",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL R_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL L_PARENT",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_FUNCIONES_SIN_VALOR_E", "METODO_CALL IDENTIFICADOR",64,
                "----------> Error_64:  Faltan dos o más elementos en la llamada de la función, Linea [#] Columna [%]");
        /*ERRORES DE LLAMADAS DE FUNCION SIN VALOR*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("LLAMADA_FUNCIONES_SIN_VALOR_E");
        gramatica.delete("LLAMADA_FUNCIONES_CON_VALOR_E");
        /*ELIMINACION DE ERRORES*/
        
        /*ERRORES DE METODO SENSOR*/
        gramatica.group("METODO_SENSORES_E", "IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT FIN_DE_LINEA",68,
                "----------> Error_68:  Falta la palabra reservada en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "METODO_CALL OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT FIN_DE_LINEA",69,
                "----------> Error_69:  Falta el identificador en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT FIN_DE_LINEA",70,
                "----------> Error_70:  Falta el = en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SENSORES L_PARENT R_PARENT FIN_DE_LINEA",71,
                "----------> Error_71:  Falta el . en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO L_PARENT R_PARENT FIN_DE_LINEA",72,
                "----------> Error_72:  Falta la función del sensor en el método del sensor, Linea [#] Columna [%]");
        
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES R_PARENT FIN_DE_LINEA",73,
                "----------> Error_73:  Falta el ( o ) o ambos en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT FIN_DE_LINEA",73,
                "----------> Error_73:  Falta el ( o ) o ambos en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES FIN_DE_LINEA",73,
                "----------> Error_73:  Falta el ( o ) o ambos en el método del sensor, Linea [#] Columna [%]");
        
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT",74,
                "----------> Error_74:  Falta el ! en el método del sensor, Linea [#] Columna [%]");
        /*
        gramatica.group("METODO_SENSORES_E", "METODO_CALL IDENTIFICADOR OP_ASIGNACION IDENTIFICADOR SIGNO_PUNTUACION_PUNTO SENSORES L_PARENT R_PARENT FIN_DE_LINEA",75,
                "----------> Error_75:  Faltan dos o más elementos en el método del sensor, Linea [#] Columna [%]");
        */
        
        gramatica.group("METODO_SENSORES_E", "METODO_CALL",76,
                "----------> Error_76:  La palabra reservada no está en la llamada de la función o en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "SIGNO_PUNTUACION_PUNTO",77,
                "----------> Error_77:  El . no está en el método del sensor, Linea [#] Columna [%]");
        gramatica.group("METODO_SENSORES_E", "SENSORES",78,
                "----------> Error_78:  La función del sensor no está en el método del sensor, Linea [#] Columna [%]");
        /*ERRORES DE METODO SENSOR*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("METODO_SENSORES_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Estructura Begin */
        gramatica.group("BLOQUE_BEGIN", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",true);
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("OPERACION_ESTRUCTURA_E", "BEGIN L_LLAVE (OPERACIONES | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_ESTRUCTURA_E", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",111,
                "----------> Error_111:  El método impresora no está dentro de un bloque de función, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("SENSORES_ESTRUCTURA_E", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | METODO_SENSORES)* R_LLAVE",113,
                "----------> Error_113:  La función del método del sensor no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DECLARACION_ESTRUCTURA_E", "BEGIN L_LLAVE (DECLARACIONES_CON_VALOR | DECLARACIONES_SIN_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",116,
                "----------> Error_116:  La declaración está dentro de un bloque de función o un bloque begin, Linea [#] Columna [%]");
        gramatica.group("ASIGNACION_ESTRUCTURA_E", "BEGIN L_LLAVE (ASIGNACIONES | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",117,
                "----------> Error_117:  La declaración está dentro de un bloque de función o un bloque begin, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "BEGIN L_LLAVE (DECLARACIONES_PUERTO | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.group("FUNCION_ESTRUCTURA_E", "BEGIN L_LLAVE (BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.group("BEGIN_ESTRUCTURA_E", "BEGIN L_LLAVE (BLOQUE_BEGIN | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES | BLOQUE_BEGIN)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.delete("OPERACION_ESTRUCTURA_E");
        gramatica.delete("IMPRESORA_ESTRUCTURA_E");
        gramatica.delete("SENSORES_ESTRUCTURA_E");
        gramatica.delete("DECLARACION_ESTRUCTURA_E");
        gramatica.delete("PUERTO_ESTRUCTURA_E");
        gramatica.delete("ASIGNACION_ESTRUCTURA_E");
        gramatica.delete("FUNCION_ESTRUCTURA_E");
        gramatica.delete("BEGIN_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        
        /* Metodo de delay*/
        gramatica.group("METODO_DELAY", "METODO_D L_PARENT (IDENTIFICADOR | VALORES) R_PARENT FIN_DE_LINEA");
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("DELAY_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (METODO_DELAY |OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",114,
                "----------> Error_114:  El método delay no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DELAY_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (METODO_DELAY | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",114,
                "----------> Error_114:  El método delay no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DELAY_ESTRUCTURA_E", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | METODO_DELAY)* R_LLAVE",114,
                "----------> Error_114:  El método delay no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.delete("DELAY_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        
        /*ERRORES DE METODO DELAY*/
        gramatica.group("METODO_DELAY_E", "L_PARENT (IDENTIFICADOR | VALORES) R_PARENT FIN_DE_LINEA",79,
                "----------> Error_79:  Falta la palabra reservada en el método delay, Linea [#] Columna [%]");
        
        gramatica.group("METODO_DELAY_E", "METODO_D (IDENTIFICADOR | VALORES) R_PARENT FIN_DE_LINEA",80,
                "----------> Error_80:  Falta el ( o ) o ambos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT (IDENTIFICADOR | VALORES) FIN_DE_LINEA",80,
                "----------> Error_80:  Falta el ( o ) o ambos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D (IDENTIFICADOR | VALORES) FIN_DE_LINEA",80,
                "----------> Error_80:  Falta el ( o ) o ambos en el método delay, Linea [#] Columna [%]");
        
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT R_PARENT FIN_DE_LINEA",81,
                "----------> Error_81:  Falta el valor en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT (IDENTIFICADOR | VALORES) R_PARENT",82,
                "----------> Error_82:  Falta el ! en el método delay, Linea [#] Columna [%]");
        
        gramatica.group("METODO_DELAY_E", "METODO_D R_PARENT FIN_DE_LINEA",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D (IDENTIFICADOR | VALORES) R_PARENT",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT FIN_DE_LINEA",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT R_PARENT",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT (IDENTIFICADOR | VALORES)",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D FIN_DE_LINEA",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D R_PARENT",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D (IDENTIFICADOR | VALORES)",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        gramatica.group("METODO_DELAY_E", "METODO_D L_PARENT",83,
                "----------> Error_83:  Faltan dos o más elementos en el método delay, Linea [#] Columna [%]");
        
        gramatica.group("METODO_DELAY_E", "METODO_D",84,
                "----------> Error_84:  La palabra reservada no está en el método delay, Linea [#] Columna [%]");
        /*ERRORES DE METODO DELAY*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("METODO_DELAY_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Estructura else */
        gramatica.group("BLOQUE_ELSE_RELACIONAL", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_ELSE_LOGICO_1", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_ELSE_LOGICO_2", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        
        /* Estructura If */
        gramatica.group("BLOQUE_IF_RELACIONAL", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_IF_LOGICO_1", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        gramatica.group("BLOQUE_IF_LOGICO_2", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE");
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("OPERACION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("OPERACION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("OPERACION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE ELSE L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("OPERACION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("OPERACION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("OPERACION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (OPERCIONES | METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | OPERACIONES)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)* R_LLAVE",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.delete("OPERACION_ESTRUCTURA_E");
        gramatica.delete("IF_ESTRUCTURA_E");
        gramatica.delete("BEGIN_ESTRUCTURA_E");
        gramatica.delete("FUNCION_ESTRUCTURA_E");
        gramatica.delete("PUERTO_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        
        /*ERRORES DEL BLOQUE IF CON ELSE*/
        /*RELACIONAL*/
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",85,"----------> Error_85:  Falta la palabra reservada if en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) L_LLAVE"
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",87,"----------> Error_87:  Falta el valor 1 en la condición del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",88,"----------> Error_88:  Falta el operador en la condición del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",89,"----------> Error_89:  Falta el valor 2 en la condición del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",91,"----------> Error_91:  Falta la palabra reservada else en el bloque else, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        /*RELACIONAL*/
        /*LOGICO DOS VALORES*/
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",85,"----------> Error_85:  Falta la palabra reservada if en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",87,"----------> Error_87:  Falta el valor 1 en la condición del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",89,"----------> Error_89:  Falta el valor 2 en la condición del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",91,"----------> Error_91:  Falta la palabra reservada else en el bloque else, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        /*LOGICO DOS VALORES*/
        /*LOGICO UN VALOR*/
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",85,"----------> Error_85:  Falta la palabra reservada if en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",87,"----------> Error_87:  Falta el valor 1 en la condición del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",91,"----------> Error_91:  Falta la palabra reservada else en el bloque else, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",92,"----------> Error_92:  Falta la { o } o ambas del bloque else, Linea [#] Columna [%]");
        /*LOGICO UN VALOR*/
        /*RELACIONAL*
        gramatica.group("BLOQUE_ELSE_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",93,"----------> Error_93:  Faltan dos o más elementos del bloque if, Linea [#] Columna [%]");
        /*RELACIONAL*/
        /*LOGICO DOS VALORES*
        gramatica.group("BLOQUE_ELSE_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",93,"----------> Error_93:  Faltan dos o más elementos del bloque if, Linea [#] Columna [%]");
        /*LOGICO DOS VALORES*/
        /*LOGICO UN VALOR*
        gramatica.group("BLOQUE_ELSE_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE ELSE L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",93,"----------> Error_93:  Faltan dos o más elementos del bloque if, Linea [#] Columna [%]");
        /*LOGICO UN VALOR*/
        
        /*RELACIONAL*/
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",85,"----------> Error_85:  Falta la palabra reservada if en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",87,"----------> Error_87:  Falta el valor 1 en la condición del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",88,"----------> Error_88:  Falta el operador en la condición del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",89,"----------> Error_89:  Falta el valor 2 en la condición del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        /*RELACIONAL*/
        /*LOGICO DOS VALORES*/
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",85,"----------> Error_85:  Falta la palabra reservada if en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",87,"----------> Error_87:  Falta el valor 1 en la condición del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",89,"----------> Error_89:  Falta el valor 2 en la condición del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        /*LOGICO DOS VALORES*/
        /*LOGICO UN VALOR*/
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",85,"----------> Error_85:  Falta la palabra reservada if en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF (IDENTIFICADOR | VALORES) L_LLAVE"
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",86,"----------> Error_86:  Falta el ( o ) o ambos en el bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF L_PARENT R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",87,"----------> Error_87:  Falta el valor 1 en la condición del bloque if, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " ",90,"----------> Error_90:  Falta la { o } o ambas del bloque if, Linea [#] Columna [%]");
        /*LOGICO UN VALOR*/
        /*RELACIONAL*
        gramatica.group("BLOQUE_IF_RELACIONAL_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",93,"----------> Error_93:  Faltan dos o más elementos del bloque if, Linea [#] Columna [%]");
        /*RELACIONAL*/
        /*LOGICO DOS VALORES*
        gramatica.group("BLOQUE_IF_LOGICO_1_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",93,"----------> Error_93:  Faltan dos o más elementos del bloque if, Linea [#] Columna [%]");
        /*LOGICO DOS VALORES*/
        /*LOGICO UN VALOR*
        gramatica.group("BLOQUE_IF_LOGICO_2_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE "
                + "(METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*"
                + " R_LLAVE",93,"----------> Error_93:  Faltan dos o más elementos del bloque if, Linea [#] Columna [%]");
        /*LOGICO UN VALOR*/
        gramatica.group("BLOQUE_IF_E", "IF",94,
                "----------> Error_94:  La palabra reservada no está en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_E", "ELSE",94,
                "----------> Error_94:  La palabra reservada no está en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_E", "OPERADORES_LOGICOS",95,
                "----------> Error_95:  El operador lógico no está en el bloque if, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_IF_E", "OPERADORES_RELACIONALES",94,
                "----------> Error_96:  El operador relacional no está en el bloque if, Linea [#] Columna [%]");
        /*ERRORES DEL BLOQUE IF CON ELSE*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("BLOQUE_ELSE_RELACIONAL_E");
        gramatica.delete("BLOQUE_ELSE_LOGICO_1_E");
        gramatica.delete("BLOQUE_ELSE_LOGICO_2_E");
        gramatica.delete("BLOQUE_IF_RELACIONAL_E");
        gramatica.delete("BLOQUE_IF_LOGICO_1_E");
        gramatica.delete("BLOQUE_IF_LOGICO_2_E");
        gramatica.delete("BLOQUE_IF_E");
        /*ELIMINACION DE ERRORES*/
        
        /* Estructura Loop */
        gramatica.group("BLOQUE_LOOP", "LOOP L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)* R_LLAVE",true);
        
        /*ERROR DE ESTRUCTURA*/
        gramatica.group("OPERACION_ESTRUCTURA_E", "LOOP L_LLAVE (OPERACIONES |METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)* R_LLAVE",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("OPERACION_ESTRUCTURA_E", "OPERACIONES",109,
                "----------> Error_109:  La operación no está dentro del bloque de función con parámetro, Linea [#] Columna [%]");
        gramatica.group("MOTOR_ESTRUCTURA_E", "(MOTORES_CON_VALOR | MOTORES_SIN_VALOR)",110,
                "----------> Error_110:  La función del motor no está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("IMPRESORA_ESTRUCTURA_E", "(IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD)",111,
                "----------> Error_111:  El método impresora no está dentro de un bloque de función, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LLAMADA_ESTRUCTURA_E", "(LLAMADA_FUNCIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR)",112,
                "----------> Error_112:  La llamada de la función no está dentro del bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("SENSORES_ESTRUCTURA_E", "METODO_SENSORES",113,
                "----------> Error_113:  La función del método del sensor no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("DELAY_ESTRUCTURA_E", "METODO_DELAY",114,
                "----------> Error_114:  El método delay no está dentro de un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("IF_ESTRUCTURA_E", "(BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2)",115,
                "----------> Error_115:  El bloque if no está dentro del bloque loop, Linea [#] Columna [%]");
        gramatica.group("PUERTO_ESTRUCTURA_E", "LOOP L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | DECLARACIONES_PUERTO)* R_LLAVE",118,
                "----------> Error_118:  La declaración del puerto está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("FUNCION_ESTRUCTURA_E", "LOOP L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_FUNCION_SIN_PARAMETROS | BLOQUE_FUNCION_CON_PARAMETROS)* R_LLAVE",119,
                "----------> Error_119:  La función está dentro de un bloque funcion, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("BEGIN_ESTRUCTURA_E", "LOOP L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_BEGIN)* R_LLAVE",120,
                "----------> Error_120:  El bloque begin está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        
        gramatica.group("LOOP_ESTRUCTURA_E", "FUNCION TIPO_DATOS IDENTIFICADOR L_PARENT TIPO_DATOS IDENTIFICADOR (SIGNO_PUNTUACION_COMA TIPO_DATOS IDENTIFICADOR)* R_PARENT L_LLAVE (OPERACIONES | BLOQUE_LOOP)* METODO_R IDENTIFICADOR FIN_DE_LINEA R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "FUNCION IDENTIFICADOR L_PARENT R_PARENT L_LLAVE (MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* (IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE ELSE L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) (OPERADORES_RELACIONALES | OP_ASIGNACION) (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) OPERADORES_LOGICOS (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "IF L_PARENT (IDENTIFICADOR | VALORES) R_PARENT L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "BEGIN L_LLAVE (BLOQUE_LOOP | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
        gramatica.group("LOOP_ESTRUCTURA_E", "LOOP L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2 | BLOQUE_LOOP)* R_LLAVE",121,
                "----------> Error_121:  El bloque loop está dentro de un bloque de función, un bloque begin, un bloque if o un bloque loop, Linea [#] Columna [%]");
 
        gramatica.delete("OPERACION_ESTRUCTURA_E");
        gramatica.delete("MOTOR_ESTRUCTURA_E");
        gramatica.delete("IMPRESORA_ESTRUCTURA_E");
        gramatica.delete("LLAMADA_ESTRUCTURA_E");
        gramatica.delete("SENSORES_ESTRUCTURA_E");
        gramatica.delete("DELAY_ESTRUCTURA_E");
        gramatica.delete("IF_ESTRUCTURA_E");
        gramatica.delete("FUNCION_ESTRUCTURA_E");
        gramatica.delete("BEGIN_ESTRUCTURA_E");
        gramatica.delete("PUERTO_ESTRUCTURA_E");
        gramatica.delete("LOOP_ESTRUCTURA_E");
        /*ERROR DE ESTRUCTURA*/
        
        /*ERRORES DE BLOQUE BEGIN*/
        gramatica.group("BLOQUE_BEGIN_E", "L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",65,
                "----------> Error_65:  Falta la palabra reservada en el bloque begin o en el bloque loop, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_BEGIN_E", "BEGIN (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)* R_LLAVE",66,
                "----------> Error_66:  Falta la { o } o ambas en el bloque begin, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_BEGIN_E", "BEGIN L_LLAVE (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*",66,
                "----------> Error_66:  Falta la { o } o ambas en el bloque begin, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_BEGIN_E", "BEGIN (LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR)*",66,
                "----------> Error_66:  Falta la { o } o ambas en el bloque begin, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_BEGIN_E", "BEGIN",67,
                "----------> Error_67:  La palabra reservada no está en el bloque begin, Linea [#] Columna [%]");
        /*ERRORES DE BLOQUE BEGIN*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("BLOQUE_BEGIN_E");
        /*ELIMINACION DE ERRORES*/
        
        /*ERRORES DE BLOQUE LOOP*/
        gramatica.group("BLOQUE_LOOP_E", "L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)* R_LLAVE",65,
                "----------> Error_65:  Falta la palabra reservada en el bloque begin o en el bloque loop, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_LOOP_E", "LOOP (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)* R_LLAVE",97,
                "----------> Error_97:  Falta la { o } o ambas en el bloque loop, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_LOOP_E", "LOOP L_LLAVE (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)*",97,
                "----------> Error_97:  Falta la { o } o ambas en el bloque loop, Linea [#] Columna [%]");
        gramatica.group("BLOQUE_LOOP_E", "LOOP (METODO_DELAY | METODO_SENSORES | IMPRESORA_A_CONSOLA | IMPRESORA_A_LCD | ASIGNACIONES | DECLARACIONES_SIN_VALOR | DECLARACIONES_CON_VALOR | LLAMADA_FUNCIONES_SIN_VALOR | LLAMADA_FUNCIONES_CON_VALOR | MOTORES_CON_VALOR | MOTORES_SIN_VALOR | BLOQUE_IF_RELACIONAL | BLOQUE_IF_LOGICO_1 | BLOQUE_IF_LOGICO_2 | BLOQUE_ELSE_RELACIONAL | BLOQUE_ELSE_LOGICO_1 | BLOQUE_ELSE_LOGICO_2)*",97,
                "----------> Error_97:  Falta la { o } o ambas en el bloque loop, Linea [#] Columna [%]");
        
        gramatica.group("BLOQUE_LOOP_E", "LOOP",98,
                "----------> Error_98:  La palabra reservada no está en el bloque loop, Linea [#] Columna [%]");
        /*ERRORES DE BLOQUE LOOP*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("BLOQUE_LOOP_E");
        /*ELIMINACION DE ERRORES*/
        
        /*ERRORES DE ESTRUCTURAS*/
        gramatica.group("ESTRUCTURAS_E","IDENTIFICADOR",99,
                "----------> Error_99:  El identificador no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","FIN_DE_LINEA",100,
                "----------> Error_100:  El ! no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","TIPO_DATOS",101,
                "----------> Error_101:  El tipo de dato no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","OP_ASIGNACION",102,
                "----------> Error_102:  El = no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","VALORES",103,
                "----------> Error_103:  El valor no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","L_PARENT",104,
                "----------> Error_104:  El ( no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","R_PARENT",105,
                "----------> Error_105:  El ) no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","SIGNO_PUNTUACION_COMA",106,
                "----------> Error_106:  La , no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","L_LLAVE",107,
                "----------> Error_107:  La { no está en ninguna estructura, Linea [#] Columna [%]");
        gramatica.group("ESTRUCTURAS_E","R_LLAVE",108,
                "----------> Error_108:  La } no está en ninguna estructura, Linea [#] Columna [%]");
        /*ERRORES DE ESTRUCTURAS*/
        /*ELIMINACION DE ERRORES*/
        gramatica.delete("ESTRUCTURAS_E");
        /*ELIMINACION DE ERRORES*/
        
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
            if(token.getLexicalComp().equals("ERROR_X") || token.getLexicalComp().equals("ERROR_0") || token.getLexicalComp().equals("ERROR_1") || token.getLexicalComp().equals("ERROR_2")){
                Object[] data = new Object[]{token.getLexicalComp(), token.getLexeme(), "[" + token.getLine() + ", " + token.getColumn() + "]"};
                Functions.addRowDataInTable(Error.TablaL(), data);
            }else{
                Object[] data = new Object[]{token.getLexicalComp(), token.getLexeme(), "[" + token.getLine() + ", " + token.getColumn() + "]"};
                Functions.addRowDataInTable(T.tokens(), data);
            }
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
        jtaOutputConsole.setText("");
        tokens.clear();
        errors.clear();
        identProd.clear();
        identificadores.clear();
        codeHasBeenCompiled = false;
        Functions.clearDataInTable(T.tokens());
        Functions.clearDataInTable(Error.TablaL());
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
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextArea jtaOutputConsole;
    private javax.swing.JTextPane jtpCode;
    private javax.swing.JPanel rootPanel;
    // End of variables declaration//GEN-END:variables
}
