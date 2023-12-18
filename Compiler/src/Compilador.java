
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.Timer;

/**
 *
 * @author yisus
 */
public class Compilador extends javax.swing.JFrame {

    /*Pantalla externas*/
    private Tokens T = new Tokens();
    private Errores Error = new Errores();
    private Estructuras E = new Estructuras();
    private CodigoIntermedio CI = new CodigoIntermedio();
    private Simbolos S = new Simbolos();
    
    private String title;
    private Directory directorio;
    private ArrayList<Token> tokens;
    private ArrayList<ErrorLSSL> errors;
    private ArrayList<TextColor> textsColor;
    private Timer timerKeyReleased;
    
    private ArrayList<Production> identProdDP;
    private ArrayList<Production> identProdDCV;
    private ArrayList<Production> identProdDSV;
    private ArrayList<Production> identProdA;
    private ArrayList<Production> identProdFMCV;
    private ArrayList<Production> identProdFMSV;
    private ArrayList<Production> identProdMIC;
    private ArrayList<Production> identProdMIL;
    private ArrayList<Production> identProdO;
    private ArrayList<Production> identProdLFCP;
    private ArrayList<Production> identProdLFSP;
    private ArrayList<Production> identProdMS;
    private ArrayList<Production> identProdMD;
    private ArrayList<Production> identProdME;
    private ArrayList<Production> identProdMA;
    private ArrayList<Production> identProdFCP;
    private ArrayList<Production> identProdFSP;
    private ArrayList<Production> identProdB;
    private ArrayList<Production> identProdER;
    private ArrayList<Production> identProdEL1V;
    private ArrayList<Production> identProdEL2V;
    private ArrayList<Production> identProdIR;
    private ArrayList<Production> identProdIL1V;
    private ArrayList<Production> identProdIL2V;
    private ArrayList<Production> identProdL;
    private ArrayList<Production> identProd;
    private ArrayList<Production> identProdOrdenado;
    
    private HashMap<String, String> identificadoresC;
    private HashMap<String, String> identificadoresV;
    private HashMap<String, String> identificadoresT;
    
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
        directorio = new Directory(this, jtpCode, title, ".RBK");
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
        
        identProdDP = new ArrayList<>();
        identProdDCV = new ArrayList<>();
        identProdDSV = new ArrayList<>();
        identProdA = new ArrayList<>();
        identProdFMCV = new ArrayList<>();
        identProdFMSV = new ArrayList<>();
        identProdMIC = new ArrayList<>();
        identProdMIL = new ArrayList<>();
        identProdO = new ArrayList<>();
        identProdLFCP = new ArrayList<>();
        identProdLFSP = new ArrayList<>();
        identProdMS = new ArrayList<>();
        identProdMD = new ArrayList<>();
        identProdME = new ArrayList<>();
        identProdMA = new ArrayList<>();
        identProdFCP = new ArrayList<>();
        identProdFSP = new ArrayList<>();
        identProdB = new ArrayList<>();
        identProdER = new ArrayList<>();
        identProdEL1V = new ArrayList<>();
        identProdEL2V = new ArrayList<>();
        identProdIR = new ArrayList<>();
        identProdIL1V = new ArrayList<>();
        identProdIL2V = new ArrayList<>();
        identProdL = new ArrayList<>();
        identProd = new ArrayList<>();
        identProdOrdenado = new ArrayList<>();
        
        identificadoresC = new HashMap<>();
        identificadoresV = new HashMap<>();
        identificadoresT = new HashMap<>();
        
        Functions.setAutocompleterJTextComponent(new String[]
            {"port_?0 'TIPO PUERTO' 'IDENTIFICADOR'!","port_?1 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_?2 'TIPO PUERTO' 'IDENTIFICADOR'!","port_?3 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_?4 'TIPO PUERTO' 'IDENTIFICADOR'!","port_?5 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "port_?6 'TIPO PUERTO' 'IDENTIFICADOR'!","port_?7 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_?0 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_?1 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_?2 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_?3 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_?4 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_?5 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "&port_?6 'TIPO PUERTO' 'IDENTIFICADOR'!","&port_?7 'TIPO PUERTO' 'IDENTIFICADOR'!",
             "const int 'IDENTIFICADOR' = 'VALORES'!","const string 'IDENTIFICADOR' = 'VALORES'!",
             "const decimal 'IDENTIFICADOR' = 'VALORES'!","const boolean 'IDENTIFICADOR' = 'VALORES'!",
             "var int 'IDENTIFICADOR' = 'VALORES'!","var string 'IDENTIFICADOR' = 'VALORES'!",
             "var decimal 'IDENTIFICADOR' = 'VALORES'!","var boolean 'IDENTIFICADOR' = 'VALORES'!",
             "const int 'IDENTIFICADOR'!","const string 'IDENTIFICADOR'!","const decimal 'IDENTIFICADOR'!","const boolean 'IDENTIFICADOR'!",
             "var int 'IDENTIFICADOR'!","var string 'IDENTIFICADOR'!","var decimal 'IDENTIFICADOR'!","var boolean 'IDENTIFICADOR'!",
             "&const int 'IDENTIFICADOR' = 'VALORES'!","&const string 'IDENTIFICADOR' = 'VALORES'!",
             "&const decimal 'IDENTIFICADOR' = 'VALORES'!","&const boolean 'IDENTIFICADOR' = 'VALORES'!",
             "&var int 'IDENTIFICADOR' = 'VALORES'!","&var string 'IDENTIFICADOR' = 'VALORES'!",
             "&var decimal 'IDENTIFICADOR' = 'VALORES'!","&var boolean 'IDENTIFICADOR' = 'VALORES'!",
             "&const int 'IDENTIFICADOR'!","&const string 'IDENTIFICADOR'!","&const decimal 'IDENTIFICADOR'!","&const boolean 'IDENTIFICADOR'!",
             "&var int 'IDENTIFICADOR'!","&var string 'IDENTIFICADOR'!","&var decimal 'IDENTIFICADOR'!","&var boolean 'IDENTIFICADOR'!",
             "'IDENTIFICADOR' = 'VALORES'!","&'IDENTIFICADOR' = 'VALORES'!",
             "move('IDENTIFICADOR','VALORES','IDENTIFICADOR')!","restart('IDENTIFICADOR','IDENTIFICADOR')!","start('IDENTIFICADOR','IDENTIFICADOR')!",
             "&move('IDENTIFICADOR','VALORES','IDENTIFICADOR')!","&restart('IDENTIFICADOR','IDENTIFICADOR')!","&start('IDENTIFICADOR','IDENTIFICADOR')!",
             "print(console,'IDENTIFICADOR|VALORES')!","print('IDENTIFICADOR','IDENTIFICADOR|VALORES')!",
             "&print(console,'IDENTIFICADOR|VALORES')!","&print('IDENTIFICADOR','IDENTIFICADOR|VALORES')!",
             "operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' + 'IDENTIFICADOR|VALORES'!","operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' - 'IDENTIFICADOR|VALORES'!",
             "operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' * 'IDENTIFICADOR|VALORES'!","operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' / 'IDENTIFICADOR|VALORES'!",
             "&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' + 'IDENTIFICADOR|VALORES'!","&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' - 'IDENTIFICADOR|VALORES'!",
             "&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' * 'IDENTIFICADOR|VALORES'!","&operation 'IDENTIFICADOR' = 'IDENTIFICADOR|VALORES' / 'IDENTIFICADOR|VALORES'!",
             "call 'IDENTIFICADOR'('VALORES')!","call 'IDENTIFICADOR'()!",
             "&call 'IDENTIFICADOR'('VALORES')!","&call 'IDENTIFICADOR'()!",
             "call 'IDENTIFICADOR' = 'IDENTIFICADOR'.'METODO SENSOR'()!",
             "&call 'IDENTIFICADOR' = 'IDENTIFICADOR'.'METODO SENSOR'()!",
             "delay('IDENTIFICADOR|VALORES')!",
             "&delay('IDENTIFICADOR|VALORES')!",
             "ligther('IDENTIFICADOR','IDENTIFICADOR')!",
             "&ligther('IDENTIFICADOR','IDENTIFICADOR')!",
             "turn_off('IDENTIFICADOR','IDENTIFICADOR')!",
             "&turn_off('IDENTIFICADOR','IDENTIFICADOR')!",
             "function 'IDENTIFICADOR'(){\n  'FUNCION_MOTOR'\n  'METODO_IMPRESORA'\n}",
             "function 'TIPO DATO' 'IDENTIFICADOR'('TIPO DATO' 'IDENTIFICADOR'){\n  'OPERACION'\n  return 'IDENTIFICADOR'!\n}",
             "&function 'IDENTIFICADOR'(){\n  'FUNCION_MOTOR'\n  'METODO_IMPRESORA'\n}",
             "&function 'TIPO DATO' 'IDENTIFICADOR'('TIPO DATO' 'IDENTIFICADOR'){\n  'OPERACION'\n  return 'IDENTIFICADOR'!\n}",
             "begin{\n  'LLAMADA_FUNCION'\n  'FUNCION_MOTOR'\n}",
             "&begin{\n  'LLAMADA_FUNCION'\n  'FUNCION_MOTOR'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "if('IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}else{\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}else{\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "if('IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}else{\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&if('IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR RELACIONAL' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}else{\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&if('IDENTIFICADOR | VALORES' 'OPERADOR LOGICO' 'IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}else{\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&if('IDENTIFICADOR | VALORES'){\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}else{\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "loop{\n  'IF'\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}",
             "&loop{\n  'IF'\n  'FUNCION_MOTOR'\n  'LLAMADA_FUNCION'\n  'DECLARACION'\n  'ASIGNACION'\n  'METODO_IMPRESORA'\n  'METODO_SENSOR'\n  'METODO_DELAY'\n  'METODO_ENCENDER'\n  'METODO_APAGAR'\n}"}, jtpCode, () -> {
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
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        btnCodigo1 = new javax.swing.JMenuItem();
        btnCodigo2 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
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
                .addGap(13, 13, 13)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1110, Short.MAX_VALUE)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(buttonsFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton10))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(14, 14, 14))
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addComponent(buttonsFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
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

        jMenuItem11.setText("Tipos de datos");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuBar1.add(jMenu2);

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
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem14);

        jMenuItem16.setText("Semántico");
        jMenu8.add(jMenuItem16);

        jMenu5.add(jMenu8);

        jMenuBar1.add(jMenu5);

        jMenu7.setText("Código ejemplo");

        btnCodigo1.setText("Codigo 1");
        btnCodigo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCodigo1ActionPerformed(evt);
            }
        });
        jMenu7.add(btnCodigo1);

        btnCodigo2.setText("Codigo 2");
        btnCodigo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCodigo2ActionPerformed(evt);
            }
        });
        jMenu7.add(btnCodigo2);

        jMenuBar1.add(jMenu7);

        jMenu6.setText("Tablas");

        jMenuItem10.setText("Tokens");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem10);

        jMenuItem17.setText("Estructuras");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem17);

        jMenuItem21.setText("Simbolos");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem21);

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
                executeCode(blocksOfCode,1);
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
                executeCode(blocksOfCode,1);
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

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        try{
            URL ruta = getClass().getResource("/Funcionamiento/Analisis sintactico.pdf");
            String rutaNueva = ruta.getFile();
            File ruta2 = new File(rutaNueva.replaceAll("%20"," "));
            Desktop.getDesktop().open(ruta2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        try{
            URL ruta = getClass().getResource("/Ayuda/Tipos de datos.pdf");
            String rutaNueva = ruta.getFile();
            File ruta2 = new File(rutaNueva.replaceAll("%20"," "));
            Desktop.getDesktop().open(ruta2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void btnCodigo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCodigo1ActionPerformed
        URL ruta = getClass().getResource("/Codigos/codigo1.comp");
        String rutaNueva = ruta.getFile();
        jtpCode.setText(leerArchivo(rutaNueva.replaceAll("%20"," ")));
        colorAnalysis();
        clearFields();
    }//GEN-LAST:event_btnCodigo1ActionPerformed

    private void btnCodigo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCodigo2ActionPerformed
        URL ruta = getClass().getResource("/Codigos/codigo2.comp");
        String rutaNueva = ruta.getFile();
        jtpCode.setText(leerArchivo(rutaNueva.replaceAll("%20"," ")));
        colorAnalysis();
        clearFields();
    }//GEN-LAST:event_btnCodigo2ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        Error.setVisible(true);
        Error.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        E.setVisible(true);
        E.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        T.setVisible(true);
        T.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        S.setVisible(true);
        S.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void compile() {
        clearFields();
        lexicalAnalysis();
        fillTableTokens();
        syntacticAnalysis();
        semanticAnalysis();
        printConsole();
        codeHasBeenCompiled = true;
    }
    private String leerArchivo(String direccion){
        String texto = "";
        
        try{
            BufferedReader bf = new BufferedReader(new FileReader(direccion));
            String temp = "";
            String bfRead;
            while((bfRead = bf.readLine()) != null){
                temp = temp + bfRead+"\n";
            }
            texto = temp;
        }catch(Exception e){
        
        }
        return texto;
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

    private void syntacticAnalysis()  {
        Grammar gramatica = new Grammar(tokens, errors);
        
        /*Eliminacion de errores léxicos*/
        gramatica.delete("Error_léxico_X", 0, "----------> Error_léxico_X:  Error desconocido, verifique como está escrito el código, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_0", 0, "----------> Error_léxico_0:  El carácter o caracteres no es válido en el lenguaje, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_1", 1, "----------> Error_léxico_1:  Un carácter o caracteres del identificador cadena no pertenece a un símbolo válido, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_2", 2, "----------> Error_léxico_2:  No hay ningún elemento entre ‘ ’, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_3", 3, "----------> Error_léxico_3:  La comilla o comillas no está asociada a un identificador cadena, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_4", 4, "----------> Error_léxico_4:  El carácter o caracteres guion bajo no está asociado a un puerto, o a la palabra reservada turn_off, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_5", 5, "----------> Error_léxico_5:  El número real contiene un carácter o caracteres no validos, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_6", 6, "----------> Error_léxico_6:  El número real inicia con un punto o varios puntos, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_7", 7, "----------> Error_léxico_7:  El número real inicia con un punto o varios puntos y cuenta con un carácter o caracteres no validos, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_8", 8, "----------> Error_léxico_8:  El número real inicia con un punto o varios puntos y tiene más de uno, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_9", 9, "----------> Error_léxico_9:  El número real inicia con un punto o varios puntos, tiene más de uno y cuenta con un carácter o caracteres no validos, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_10", 10, "----------> Error_léxico_10:  El número real tiene más de un punto, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_11", 11, "----------> Error_léxico_11:  El número real tiene más de un punto y cuenta con un carácter o caracteres no validos, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_12", 12, "----------> Error_léxico_12:  El número real no continua después del punto, Linea [#] Columna [%]");
        gramatica.delete("Error_léxico_13", 13, "----------> Error_léxico_13:  El numero entero contiene un carácter o caracteres no validos, Linea [#] Columna [%]");
        /*Eliminacion de errores léxicos*/
        
        /*Agrupación de valores*/
        gramatica.group("VALOR_BOOLEANO", "(CONDICIONAL_1 | CONDICIONAL_2)");
        gramatica.group("VALOR_NUMERO_ENTERO", "(NUMERO_ENTERO)"); 
        gramatica.group("VALOR_NUMERO_REAL", "(NUMERO_REAL)"); 
        gramatica.group("VALOR_CADENA", "(IDENTIFICADOR_CADENA)");
        
        gramatica.group("VALORES", "(VALOR_BOOLEANO | VALOR_NUMERO_ENTERO | VALOR_NUMERO_REAL | VALOR_CADENA)");
        /*Agrupación de valores*/
        
        /*Agrupacion de palabras reservadas*/
        gramatica.group("PUERTO", "(PUERTO_1 | PUERTO_2 | PUERTO_3 | PUERTO_4 | PUERTO_5 | PUERTO_6 | PUERTO_7 | PUERTO_8 | PUERTO_9 | PUERTO_10 | PUERTO_11 | PUERTO_12 | PUERTO_13 | PUERTO_14 | PUERTO_15 | PUERTO_16 | PUERTO_17 | PUERTO_18 | PUERTO_19 | PUERTO_20 | PUERTO_21 | PUERTO_22 | PUERTO_23 | PUERTO_24 | PUERTO_25 | PUERTO_26 | PUERTO_27 | PUERTO_28)");
        gramatica.group("TIPO_PUERTO", "(TIPO_PUERTO_1 | TIPO_PUERTO_2 | TIPO_PUERTO_3 | TIPO_PUERTO_4 | TIPO_PUERTO_5 | TIPO_PUERTO_6)");
        gramatica.group("DECLARACION", "(DECLARACION_1 | DECLARACION_2)");
        gramatica.group("TIPO_DATO", "(TIPO_DATO_1 | TIPO_DATO_2 | TIPO_DATO_3 | TIPO_DATO_4)");
        gramatica.group("MOTOR", "(MOTOR_1 | MOTOR_2 | MOTOR_3)");
        gramatica.group("METODO_SENSOR", "(METODO_SENSOR_1 | METODO_SENSOR_2 | METODO_SENSOR_3 | METODO_SENSOR_4)");
        /*Agrupacion de palabras reservadas*/

        /*Agrupacion de operadores*/
        gramatica.group("OPERADOR_ARITMETICO","(OPERADOR_ARITMETICO_1 | OPERADOR_ARITMETICO_2 | OPERADOR_ARITMETICO_3 | OPERADOR_ARITMETICO_4)");
        gramatica.group("OPERADOR_LOGICO","(OPERADOR_LOGICO_1 | OPERADOR_LOGICO_2 | OPERADOR_LOGICO_3)");
        gramatica.group("OPERADOR_RELACIONAL","(OPERADOR_RELACIONAL_1 | OPERADOR_RELACIONAL_2 | OPERADOR_RELACIONAL_3 | OPERADOR_RELACIONAL_4 | OPERADOR_RELACIONAL_5)");
        /*Agrupacion de operadores*/
        
        /*Cambio de nombres de las estructuras de control*/
        gramatica.group("FUNCION","ESTRUCTURA_DE_CONTROL_1");
        gramatica.group("BEGIN","ESTRUCTURA_DE_CONTROL_2");
        gramatica.group("LOOP","ESTRUCTURA_DE_CONTROL_3");
        gramatica.group("IF","ESTRUCTURA_DE_CONTROL_4");
        gramatica.group("ELSE","ESTRUCTURA_DE_CONTROL_5");
        /*Cambio de nombres de las estructuras de control*/
        
        /*Cambio de nombres de los signos de puntuacion*/
        gramatica.group("COMA","SIGNO_PUNTUACION_1");
        gramatica.group("PUNTO","SIGNO_PUNTUACION_2");
        /*Cambio de nombres de los signos de puntuacion*/
        
        /*Cambio de nombres de las palabras reservadas*/
        gramatica.group("LIGTHER","PALABRA_RESERVADA_1");
        gramatica.group("DELAY","PALABRA_RESERVADA_2");
        gramatica.group("OPERATION","PALABRA_RESERVADA_3");
        gramatica.group("CALL","PALABRA_RESERVADA_4");
        gramatica.group("RETURN","PALABRA_RESERVADA_5");
        gramatica.group("PRINT","PALABRA_RESERVADA_6");
        gramatica.group("CONSOLE","PALABRA_RESERVADA_7");
        gramatica.group("TURN_OFF","PALABRA_RESERVADA_8");
        /*Cambio de nombres de las palabras reservadas*/
        
        /*Produccion Declaracion de puerto*/
        gramatica.group("DECLARACION_PUERTO", "PUERTO TIPO_PUERTO IDENTIFICADOR FIN_DE_LINEA",true,identProdDP);
        gramatica.group("DECLARACION_PUERTO", "DECLARACION_PUERTO",true,identProd);
        /*Produccion Declaracion de puerto */
        
        /*Produccion Declaracion*/
        gramatica.group("DECLARACION_CON_VALOR", "DECLARACION TIPO_DATO IDENTIFICADOR OPERADOR_ASIGNACION VALORES FIN_DE_LINEA",true,identProdDCV);
        gramatica.group("DECLARACION_SIN_VALOR", "DECLARACION TIPO_DATO IDENTIFICADOR FIN_DE_LINEA",true,identProdDSV);
        gramatica.group("DECLARACION_CON_VALOR", "DECLARACION_CON_VALOR",true,identProd);
        gramatica.group("DECLARACION_SIN_VALOR", "DECLARACION_SIN_VALOR",true,identProd);
        /*Produccion Declaracion*/
        
        /*Produccion Asignacion*/
        gramatica.group("ASIGNACION", "IDENTIFICADOR OPERADOR_ASIGNACION VALORES FIN_DE_LINEA",true,identProdA);
        gramatica.group("ASIGNACION", "ASIGNACION",true,identProd);
        /*Produccion Asignacion*/
        
        /*Produccion Funcion de motor*/
        gramatica.group("FUNCION_MOTOR_CON_VALOR", "MOTOR SIGNO_AGRUPACION_3 IDENTIFICADOR COMA VALORES COMA IDENTIFICADOR SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdFMCV);
        gramatica.group("FUNCION_MOTOR_SIN_VALOR", "MOTOR SIGNO_AGRUPACION_3 IDENTIFICADOR COMA IDENTIFICADOR SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdFMSV);
        gramatica.group("FUNCION_MOTOR_CON_VALOR", "FUNCION_MOTOR_CON_VALOR",true,identProd);
        gramatica.group("FUNCION_MOTOR_SIN_VALOR", "FUNCION_MOTOR_SIN_VALOR",true,identProd);
        /*Produccion Funcion de motor*/
        
        /*Produccion Metodo de impresora*/
        gramatica.group("METODO_IMPRESORA_A_CONSOLA", "PRINT SIGNO_AGRUPACION_3 CONSOLE COMA (VALORES | IDENTIFICADOR) SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdMIC);
        gramatica.group("METODO_IMPRESORA_A_LCD", "PRINT SIGNO_AGRUPACION_3 IDENTIFICADOR COMA (VALORES | IDENTIFICADOR) SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdMIL);
        gramatica.group("METODO_IMPRESORA_A_CONSOLA", "METODO_IMPRESORA_A_CONSOLA",true,identProd);
        gramatica.group("METODO_IMPRESORA_A_LCD", "METODO_IMPRESORA_A_LCD",true,identProd);
        /*Produccion Metodo de impresora*/
        
        /*Produccion Operacion*/
        gramatica.group("OPERACION", "OPERATION IDENTIFICADOR OPERADOR_ASIGNACION (VALORES | IDENTIFICADOR) OPERADOR_ARITMETICO (VALORES | IDENTIFICADOR) FIN_DE_LINEA",true,identProdO);
        gramatica.group("OPERACION", "OPERACION",true,identProd);
        /*Produccion Operacion*/
        
        /*Produccion Llamada de funcion*/
        gramatica.group("LLAMADA_FUNCION_CON_VALOR", "CALL IDENTIFICADOR SIGNO_AGRUPACION_3 VALORES (COMA VALORES)* SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdLFCP);
        gramatica.group("LLAMADA_FUNCION_SIN_VALOR", "CALL IDENTIFICADOR SIGNO_AGRUPACION_3 SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdLFSP);
        gramatica.group("LLAMADA_FUNCION_CON_VALOR", "LLAMADA_FUNCION_CON_VALOR",true,identProd);
        gramatica.group("LLAMADA_FUNCION_SIN_VALOR", "LLAMADA_FUNCION_SIN_VALOR",true,identProd);
        /*Produccion Llamada de funcion*/
        
        /*Produccion Metodo de sensor*/
        gramatica.group("METODO_SENSOR", "CALL IDENTIFICADOR OPERADOR_ASIGNACION IDENTIFICADOR PUNTO METODO_SENSOR SIGNO_AGRUPACION_3 SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdMS);
        gramatica.group("METODO_SENSOR", "METODO_SENSOR",true,identProd);
        /*Produccion Metodo de sensor*/
        
        /*Produccion Metodo delay*/
        gramatica.group("METODO_DELAY", "DELAY SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdMD);
        gramatica.group("METODO_DELAY", "METODO_DELAY",true,identProd);
        /*Produccion Metodo delay*/
        
        /*Produccion Metodo encender*/
        gramatica.group("METODO_ENCENDER", "LIGTHER SIGNO_AGRUPACION_3 IDENTIFICADOR COMA IDENTIFICADOR SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdME);
        gramatica.group("METODO_ENCENDER", "METODO_ENCENDER",true,identProd);
        /*Produccion Metodo encender*/
        
        /*Produccion Metodo apagar*/
        gramatica.group("METODO_APAGAR", "TURN_OFF SIGNO_AGRUPACION_3 IDENTIFICADOR COMA IDENTIFICADOR SIGNO_AGRUPACION_4 FIN_DE_LINEA",true,identProdMA);
        gramatica.group("METODO_APAGAR", "METODO_APAGAR",true,identProd);
        /*Produccion Metodo apagar*/
        
        /*Produccion Funcion*/
        gramatica.group("FUNCION_CON_PARAMETROS", "FUNCION TIPO_DATO IDENTIFICADOR "
                + "SIGNO_AGRUPACION_3 TIPO_DATO IDENTIFICADOR (COMA TIPO_DATO IDENTIFICADOR)* SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(OPERACION)* RETURN IDENTIFICADOR FIN_DE_LINEA "
                + "SIGNO_AGRUPACION_2",true,identProdFCP);
        gramatica.group("FUNCION_SIN_PARAMETROS", "FUNCION IDENTIFICADOR "
                + "SIGNO_AGRUPACION_3 SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD)* "
                + "SIGNO_AGRUPACION_2",true,identProdFSP);
        gramatica.group("FUNCION_CON_PARAMETROS", "FUNCION_CON_PARAMETROS",true,identProd);
        gramatica.group("FUNCION_SIN_PARAMETROS", "FUNCION_SIN_PARAMETROS",true,identProd);
        /*Produccion Funcion*/
        
        /*Produccion Begin*/
        gramatica.group("BEGIN", "BEGIN "
                + "SIGNO_AGRUPACION_1 "
                + "(LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR)* "
                + "SIGNO_AGRUPACION_2",true,identProdB);
        gramatica.group("BEGIN", "BEGIN",true,identProd);
        /*Produccion Begin*/
        
        /*Produccion If con else*/
        gramatica.group("ELSE_RELACIONAL", "IF "
                + "SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) OPERADOR_RELACIONAL (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2 "
                + "ELSE "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdER);
        gramatica.group("ELSE_LOGICO_1", "IF "
                + "SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) OPERADOR_LOGICO (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2 "
                + "ELSE "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdEL2V);
        gramatica.group("ELSE_LOGICO_2", "IF "
                + "SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2 "
                + "ELSE "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdEL1V);
        gramatica.group("ELSE_RELACIONAL", "ELSE_RELACIONAL",true,identProd);
        gramatica.group("ELSE_LOGICO_1", "ELSE_LOGICO_1",true,identProd);
        gramatica.group("ELSE_LOGICO_2", "ELSE_LOGICO_2",true,identProd);
        /*Produccion If con else*/
        
        /*Produccion If*/
        gramatica.group("IF_RELACIONAL", "IF "
                + "SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) OPERADOR_RELACIONAL (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdIR);
        gramatica.group("IF_LOGICO_1", "IF "
                + "SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) OPERADOR_LOGICO (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdIL2V);
        gramatica.group("IF_LOGICO_2", "IF "
                + "SIGNO_AGRUPACION_3 (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION_4 "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdIL1V);
        gramatica.group("IF_RELACIONAL", "IF_RELACIONAL",true,identProd);
        gramatica.group("IF_LOGICO_1", "IF_LOGICO_1",true,identProd);
        gramatica.group("IF_LOGICO_2", "IF_LOGICO_2",true,identProd);
        /*Produccion If*/
        
        /*Produccion Loop*/
        gramatica.group("LOOP", "LOOP "
                + "SIGNO_AGRUPACION_1 "
                + "(METODO_SENSOR | METODO_IMPRESORA_A_CONSOLA | METODO_IMPRESORA_A_LCD | ASIGNACION | DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR | FUNCION_MOTOR_CON_VALOR | FUNCION_MOTOR_SIN_VALOR | LLAMADA_FUNCION_SIN_VALOR | LLAMADA_FUNCION_CON_VALOR | IF_RELACIONAL | IF_LOGICO_1 | IF_LOGICO_2 | ELSE_RELACIONAL | ELSE_LOGICO_1 | ELSE_LOGICO_2 | METODO_DELAY | METODO_ENCENDER | METODO_APAGAR)* "
                + "SIGNO_AGRUPACION_2",true,identProdL);
        gramatica.group("LOOP", "LOOP",true,identProd);
        /*Produccion Loop*/
        
        /*Bloque de Begin y Loop*/
        gramatica.group("BLOQUE_GLOBAL_BL", "(LOOP | BEGIN)*");
        /*Bloque de Begin y Loop*/
        
        /*Bloque de Funciones*/
        gramatica.group("BLOQUE_GLOBAL_F", "(FUNCION_CON_PARAMETROS | FUNCION_SIN_PARAMETROS)*"); 
        /*Bloque de Funciones*/
        
        /*Bloque de declaraciones de puerto*/
        gramatica.group("BLOQUE_GLOBAL_DP","(DECLARACION_PUERTO)*");
        /*Bloque de declaraciones de puerto*/
        
        /*Bloque de declaraciones de variables*/
        gramatica.group("BLOQUE_GLOBAL_D","(DECLARACION_SIN_VALOR | DECLARACION_CON_VALOR)*");
        /*Bloque de declaraciones de variables*/
        
        /*Bloque de declaraciones de puertos y variables*/
        gramatica.group("BLOQUE_GLOBAL_DP_D","(BLOQUE_GLOBAL_DP | BLOQUE_GLOBAL_D)*");
        /*Bloque de declaraciones de puertos y variables*/
        
        /*Bloque de declaraciones de puerto y variables con las asignaciones*/
        gramatica.group("BLOQUE_GLOBAL_DP_D_A","(BLOQUE_GLOBAL_DP_D | ASIGNACION)*");
        /*Bloque de declaraciones de puerto y variables con las asignaciones*/
        
        /*Bloque de Declaraciones de puerto y variables y asignaciones con las funciones */
        gramatica.group("BLOQUE_GLOBAL_DP_D_A_F","(BLOQUE_GLOBAL_F | BLOQUE_GLOBAL_DP_D_A)*"); 
        /*Bloque de Declaraciones de puerto y variables y asignaciones con las funciones */
        
        /*Bloque de Begin y Loop con las declaraciones de puerto y variables y asignaciones y funciones */
        gramatica.group("BLOQUE_GLOBAL","(BLOQUE_GLOBAL_DP_D_A_F | BLOQUE_GLOBAL_BL)*"); 
        /*Bloque de Begin y Loop con las declaraciones de puerto y variables y asignaciones y funciones */
        
        gramatica.show();
        
        /*Ordenar Producciones*/   
        for(int i = 0; i < 100000 ;i++){
            for(Production p: identProd){
                if(p.getLine() == i){
                    identProdOrdenado.add(p);
                }
            }
        }
        /*Ordenar Producciones*/
        
        E.estructuras().append("\n");
        for(Production p: identProdOrdenado){
            if(p.lexicalCompRank(0).equals("PUERTO_1") || p.lexicalCompRank(0).equals("PUERTO_2") || p.lexicalCompRank(0).equals("PUERTO_3") || p.lexicalCompRank(0).equals("PUERTO_4") || p.lexicalCompRank(0).equals("PUERTO_5") || p.lexicalCompRank(0).equals("PUERTO_6") || p.lexicalCompRank(0).equals("PUERTO_7") || p.lexicalCompRank(0).equals("PUERTO_8") || p.lexicalCompRank(0).equals("PUERTO_9") || p.lexicalCompRank(0).equals("PUERTO_10") || p.lexicalCompRank(0).equals("PUERTO_11") || p.lexicalCompRank(0).equals("PUERTO_12") || p.lexicalCompRank(0).equals("PUERTO_13") || p.lexicalCompRank(0).equals("PUERTO_14") || p.lexicalCompRank(0).equals("PUERTO_15") || p.lexicalCompRank(0).equals("PUERTO_16") || p.lexicalCompRank(0).equals("PUERTO_17") || p.lexicalCompRank(0).equals("PUERTO_18") || p.lexicalCompRank(0).equals("PUERTO_19") || p.lexicalCompRank(0).equals("PUERTO_20") || p.lexicalCompRank(0).equals("PUERTO_21") || p.lexicalCompRank(0).equals("PUERTO_22") || p.lexicalCompRank(0).equals("PUERTO_23") || p.lexicalCompRank(0).equals("PUERTO_24") || p.lexicalCompRank(0).equals("PUERTO_25") || p.lexicalCompRank(0).equals("PUERTO_26") || p.lexicalCompRank(0).equals("PUERTO_27") || p.lexicalCompRank(0).equals("PUERTO_28")){
                E.estructuras().append("PUERTO TIPO_PUERTO "+p.lexicalCompRank(2)+" "+p.lexicalCompRank(3)+" ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
            }
            if(p.lexicalCompRank(0).equals("DECLARACION_1") || p.lexicalCompRank(0).equals("DECLARACION_2")){
                if(p.lexicalCompRank(3).equals("OPERADOR_ASIGNACION")){
                    E.estructuras().append("DECLARACION TIPO_DATO "+p.lexicalCompRank(2)+" "+p.lexicalCompRank(3)+" VALORES "+p.lexicalCompRank(5)+" ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }else{
                    E.estructuras().append("DECLARACION TIPO_DATO "+p.lexicalCompRank(2)+" "+p.lexicalCompRank(3)+" ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }
            }
            if(p.lexicalCompRank(0).equals("IDENTIFICADOR")){
                E.estructuras().append("IDENTIFICADOR OPERADOR_ASIGNACION VALORES FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
            }
            if(p.lexicalCompRank(0).equals("MOTOR_1") || p.lexicalCompRank(0).equals("MOTOR_2") || p.lexicalCompRank(0).equals("MOTOR_3")){
                if(p.lexicalCompRank(4).equals("IDENTIFICADOR")){
                    E.estructuras().append("MOTOR SIGNO_AGRUPACION IDENTIFICADOR SIGNO_PUNTUACION IDENTIFICADOR SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }else{
                    E.estructuras().append("MOTOR SIGNO_AGRUPACION IDENTIFICADOR SIGNO_PUNTUACION VALORES SIGNO_PUNTUACION IDENTIFICADOR SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" "+p.lexemeRank(7)+" "+p.lexemeRank(8)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }
            }
            if(p.lexicalCompRank(0).equals("PALABRA_RESERVADA_6")){
                if(p.lexicalCompRank(2).equals("IDENTIFICADOR")){
                    E.estructuras().append("PALABRA_RESERVADA SIGNO_AGRUPACION IDENTIFICADOR SIGNO_PUNTUACION (VALORES | IDENTIFICADOR) SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }else{
                    E.estructuras().append("PALABRA_RESERVADA SIGNO_AGRUPACION PALABRA_RESERVADA SIGNO_PUNTUACION (VALORES | IDENTIFICADOR) SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }
            }
            if(p.lexicalCompRank(0).equals("PALABRA_RESERVADA_3")){
                E.estructuras().append("PALABRA_RESERVADA IDENTIFICADOR OPERADOR_ASIGNACION (IDENTIFICADOR | VALORES) OPERADOR_ARITMETICO (IDENTIFICADOR | VALORES) FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
            }
            if(p.lexicalCompRank(0).equals("ESTRUCTURA_DE_CONTROL_1")){
            
            }
            if(p.lexicalCompRank(0).equals("ESTRUCTURA_DE_CONTROL_2")){
            
            }
            if(p.lexicalCompRank(0).equals("PALABRA_RESERVADA_2")){
                E.estructuras().append("PALABRA_RESERVADA SIGNO_AGRUPACION (IDENTIFICADOR | VALORES) SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
            }
            if(p.lexicalCompRank(0).equals("PALABRA_RESERVADA_1")){
                E.estructuras().append("PALABRA_RESERVADA SIGNO_AGRUPACION IDENTIFICADOR SIGNO_PUNTUACION IDENTIFICADOR SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
            }
            if(p.lexicalCompRank(0).equals("PALABRA_RESERVADA_8")){
                E.estructuras().append("PALABRA_RESERVADA SIGNO_AGRUPACION IDENTIFICADOR SIGNO_PUNTUACION IDENTIFICADOR SIGNO_AGRUPACION FIN_DE_LINEA ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
            }
            if(p.lexicalCompRank(0).equals("ESTRUCTURA_DE_CONTROL_4")){
            
            }
            if(p.lexicalCompRank(0).equals("ESTRUCTURA_DE_CONTROL_3")){
            
            }
            if(p.lexicalCompRank(0).equals("PALABRA_RESERVADA_4")){
                if(p.lexicalCompRank(2).equals("OPERADOR_ASIGNACION")){
                    E.estructuras().append("PALABRA_RESERVADA "+p.lexicalCompRank(1)+" "+p.lexicalCompRank(2)+" "+p.lexicalCompRank(3)+" SIGNO_PUNTUACION METODO_SENSOR SIGNO_AGRUPACION SIGNO_AGRUPACION "+p.lexicalCompRank(8)+" ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" "+p.lexemeRank(5)+" "+p.lexemeRank(6)+" "+p.lexemeRank(7)+" "+p.lexemeRank(8)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                }else{
                    if(p.lexicalCompRank(3).equals("SIGNO_AGRUPACION_4")){
                        E.estructuras().append("PALABRA_RESERVADA "+p.lexicalCompRank(1)+" SIGNO_AGRUPACION SIGNO_AGRUPACION "+p.lexicalCompRank(4)+" ----> "+p.lexemeRank(0)+" "+p.lexemeRank(1)+" "+p.lexemeRank(2)+" "+p.lexemeRank(3)+" "+p.lexemeRank(4)+" ----> ["+p.getLine()+","+p.getColumn()+"]\n\n");
                    }else{
                        
                    }
                }
            }
        }
    }

    private void semanticAnalysis() {
        //DECLARACIONES 
        for(Production p : identProdDP){
            if(identificadoresC == null){
                identificadoresC.put(p.lexemeRank(2),"SENSOR");
                identificadoresV.put(p.lexemeRank(2),p.lexemeRank(1));
                S.Simbolos().append(p.lexemeRank(2)+" ----> SENSOR ----> "+p.lexemeRank(1)+"\n");
            }
            if(identificadoresC.containsKey(p.lexemeRank(2))){
                errors.add(new ErrorLSSL(125, "----------> Error_125:  La variable ya ha sido declarada, Linea [#] Columna [%]",p, true));
            }
            identificadoresC.put(p.lexemeRank(2),"SENSOR");
            identificadoresV.put(p.lexemeRank(2),p.lexemeRank(1));
            S.Simbolos().append(p.lexemeRank(2)+" ----> SENSOR ----> "+p.lexemeRank(1)+"\n");
        }
        
        for(Production p : identProdDCV){
            if(identificadoresC == null){
                if(p.lexicalCompRank(1).equals("TIPO_DATO_I")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_ENTERO");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_ENTERO ----> "+p.lexemeRank(-2)+"\n");
                }
                if(p.lexicalCompRank(1).equals("TIPO_DATO_S")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_STRING");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_STRING ----> "+p.lexemeRank(-2)+"\n");
                }
                if(p.lexicalCompRank(1).equals("TIPO_DATO_D")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_DECIMAL");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_DECIMAL ----> "+p.lexemeRank(-2)+"\n");
                }
                if(p.lexicalCompRank(1).equals("TIPO_DATO_B")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_BOOLEANO");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_BOOLEANO ----> "+p.lexemeRank(-2)+"\n");
                }
                
            }
            if(identificadoresC.containsKey(p.lexemeRank(2))){
                errors.add(new ErrorLSSL(125, "----------> Error_125:  La variable ya ha sido declarada, Linea [#] Columna [%]",p, true));
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_I")){
                if(p.lexicalCompRank(-2).equals("VALOR_NUMERO_ENTERO")){
                    if(Integer.parseInt(p.lexemeRank(-2)) <= 65535){
                        identificadoresC.put(p.lexemeRank(2),"TIPO_ENTERO");
                        identificadoresV.put(p.lexemeRank(2),p.lexemeRank(-2));
                        identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                        S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_ENTERO ----> "+p.lexemeRank(-2)+"\n");
                    }else{
                        errors.add(new ErrorLSSL(124, "----------> Error_124:  El valor asignado en la declaración no está en rango, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(123, "----------> Error_123:  El tipo de dato no es el mismo que el valor asignado en la declaración, Linea [#] Columna [%]",p, true));
                }
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_S")){
                if(p.lexicalCompRank(-2).equals("VALOR_CADENA")){
                    if(p.lexemeRank(-2).length() <= 32){
                        identificadoresC.put(p.lexemeRank(2),"TIPO_STRING");
                        identificadoresV.put(p.lexemeRank(2),p.lexemeRank(-2));
                        identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                        S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_STRING ----> "+p.lexemeRank(-2)+"\n");
                    }else{
                        errors.add(new ErrorLSSL(124, "----------> Error_124:  El valor asignado en la declaración no está en rango, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(123, "----------> Error_123:  El tipo de dato no es el mismo que el valor asignado en la declaración, Linea [#] Columna [%]",p, true));
                }
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_D")){
                if(p.lexicalCompRank(-2).equals("VALOR_NUMERO_REAL")){
                    if(Double.parseDouble(p.lexemeRank(-2)) <= Float.MAX_VALUE){
                        identificadoresC.put(p.lexemeRank(2),"TIPO_DECIMAL");
                        identificadoresV.put(p.lexemeRank(2),p.lexemeRank(-2));
                        identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                        S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_DECIMAL ----> "+p.lexemeRank(-2)+"\n");
                    }else{
                        errors.add(new ErrorLSSL(124, "----------> Error_124:  El valor asignado en la declaración no está en rango, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(123, "----------> Error_123:  El tipo de dato no es el mismo que el valor asignado en la declaración, Linea [#] Columna [%]",p, true));
                }
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_B")){
                if(p.lexicalCompRank(-2).equals("VALOR_CONDICIONAL_T") || p.lexicalCompRank(-2).equals("VALOR_CONDICIONAL_F")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_BOOLEANO");
                    identificadoresV.put(p.lexemeRank(2),p.lexemeRank(-2));
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_BOOLEANO ----> "+p.lexemeRank(-2)+"\n");
                }else{
                    errors.add(new ErrorLSSL(123, "----------> Error_123:  El tipo de dato no es el mismo que el valor asignado en la declaración, Linea [#] Columna [%]",p, true));
                }
            }
        }
        
        for(Production p : identProdDSV){
            if(identificadoresC == null){
                if(p.lexicalCompRank(1).equals("TIPO_DATO_I")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_ENTERO");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_ENTERO ----> Todavia no cuenta con valor\n");
                }
                if(p.lexicalCompRank(1).equals("TIPO_DATO_S")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_STRING");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_STRING ----> Todavia no cuenta con valor\n");
                }
                if(p.lexicalCompRank(1).equals("TIPO_DATO_D")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_DECIMAL");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_DECIMAL ----> Todavia no cuenta con valor\n");
                }
                if(p.lexicalCompRank(1).equals("TIPO_DATO_B")){
                    identificadoresC.put(p.lexemeRank(2),"TIPO_BOOLEANO");
                    identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_BOOLEANO ----> Todavia no cuenta con valor\n");
                }
            }
            if(identificadoresC.containsKey(p.lexemeRank(2))){
                errors.add(new ErrorLSSL(125, "----------> Error_125:  La variable ya ha sido declarada, Linea [#] Columna [%]",p, true));
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_I")){
                identificadoresC.put(p.lexemeRank(2),"TIPO_ENTERO");
                identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_ENTERO ----> Todavia no cuenta con valor\n");
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_S")){
                identificadoresC.put(p.lexemeRank(2),"TIPO_STRING");
                identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_STRING ----> Todavia no cuenta con valor\n");
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_D")){
                identificadoresC.put(p.lexemeRank(2),"TIPO_DECIMAL");
                identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_DECIMAL ----> Todavia no cuenta con valor\n");
            }
            if(p.lexicalCompRank(1).equals("TIPO_DATO_B")){
                identificadoresC.put(p.lexemeRank(2),"TIPO_BOOLEANO");
                identificadoresT.put(p.lexemeRank(2),p.lexemeRank(0));
                    S.Simbolos().append(p.lexemeRank(2)+" ----> TIPO_BOOLEANO ----> Todavia no cuenta con valor\n");
            }
        }
        //DECLARACIONES 
        
        //ASIGNACIONES
        for(Production p : identProdA){
            if(identificadoresC.containsKey(p.lexemeRank(0))){
                if(identificadoresT.containsKey(p.lexemeRank(0)) && identificadoresV.containsKey(p.lexemeRank(0))){
                    if(identificadoresT.get(p.lexemeRank(0)).equals("const")){
                        errors.add(new ErrorLSSL(126, "----------> Error_126:  La variable es una constante y no es posible modificar su valor, Linea [#] Columna [%]",p, true));
                    }else{
                        if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_ENTERO")){
                            if(p.lexicalCompRank(-2).equals("VALOR_NUMERO_ENTERO")){
                                identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                                S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_ENTERO ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                            }else{
                                errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                            }
                        }
                        if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_DECIMAL")){
                            if(p.lexicalCompRank(-2).equals("VALOR_NUMERO_REAL")){
                                identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                                S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_DECIMAL ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                            }else{
                                errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                            }
                        }
                        if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_STRING")){
                            if(p.lexicalCompRank(-2).equals("VALOR_CADENA")){
                                identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                                S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_STRING ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                            }else{
                                errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                            }
                        }
                        if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_BOOLEANO")){
                            if(p.lexicalCompRank(-2).equals("VALOR_CONDICIONAL_T") || p.lexicalCompRank(-2).equals("VALOR_CONDICIONAL_F")){
                                identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                                S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_BOOLEANO ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                            }else{
                                errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                            }
                        }
                    }
                }else{
                    if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_ENTERO")){
                        if(p.lexicalCompRank(-2).equals("VALOR_NUMERO_ENTERO")){
                            identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                            S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_ENTERO ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                        }else{
                            errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                        }
                    }
                    if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_DECIMAL")){
                        if(p.lexicalCompRank(-2).equals("VALOR_NUMERO_REAL")){
                            identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                            S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_DECIMAL ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                        }else{
                            errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                        }
                    }
                    if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_STRING")){
                        if(p.lexicalCompRank(-2).equals("VALOR_CADENA")){
                            identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                            S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_STRING ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                        }else{
                            errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                        }
                    }
                    if(identificadoresC.get(p.lexemeRank(0)).equals("TIPO_BOOLEANO")){
                        if(p.lexicalCompRank(-2).equals("VALOR_CONDICIONAL_T") || p.lexicalCompRank(-2).equals("VALOR_CONDICIONAL_F")){
                            identificadoresV.put(p.lexemeRank(0),p.lexemeRank(-2));
                            S.Simbolos().append(p.lexemeRank(0)+" ----> TIPO_BOOLEANO ----> Nuevo Valor - "+p.lexemeRank(-2)+"\n");
                        }else{
                            errors.add(new ErrorLSSL(128, "----------> Error_128:  El tipo de dato del valor no es el mismo que el de la variable a asignar, Linea [#] Columna [%]",p, true));
                        }
                    }
                }
            }else{
                errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
            }
        }
        //ASIGNACIONES
        
        //MOTORES
        for(Production p : identProdFMCV){
            if(identificadoresC.containsKey(p.lexemeRank(2))){
                if(identificadoresV.get(p.lexemeRank(2)).equals("motor")){
                    if(p.lexemeRank(0).equals("move")){
                        if(p.lexicalCompRank(-5).equals("VALOR_NUMERO_ENTERO")){
                        
                        }else{
                            errors.add(new ErrorLSSL(132, "----------> Error_132:  La función del motor move tiene un valor diferente de tipo entero, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(131, "----------> Error_131:  La función del motor start o restart tiene valor, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(129, "----------> Error_129:  La variable no está asociado a un sensor de tipo motor, Linea [#] Columna [%]",p, true));
                }
            }else{
                errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
            }    
        }
        for(Production p : identProdFMSV){
            if(identificadoresC.containsKey(p.lexemeRank(2))){
                if(identificadoresV.get(p.lexemeRank(2)).equals("motor")){
                    if(p.lexemeRank(0).equals("move")){
                        errors.add(new ErrorLSSL(130, "----------> Error_130:  La función del motor move no tiene valor, Linea [#] Columna [%]",p, true));
                    }else{
                        
                    }
                }else{
                    errors.add(new ErrorLSSL(129, "----------> Error_129:  La variable no está asociado a un sensor de tipo motor, Linea [#] Columna [%]",p, true));
                }
            }else{
                errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
            }
        }
        //MOTORES
        
        //IMPRESORA
        for(Production p : identProdMIC){
            if(p.lexicalCompRank(-3).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(-3))){
                    if(identificadoresC.get(p.lexemeRank(-3)).equals("TIPO_STRING")){
                        if(identificadoresV.containsKey(p.lexemeRank(-3))){
                        
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(134, "----------> Error_134:  En el método de la impresora el valor de la variable no es de tipo string, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(-3).equals("VALOR_CADENA")){
                
                }else{
                    errors.add(new ErrorLSSL(133, "----------> Error_133:  En el método impresora el valor no es de tipo string, Linea [#] Columna [%]",p, true));
                }
            }
        }
        for(Production p : identProdMIL){
            if(p.lexicalCompRank(-3).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(-3))){
                    if(identificadoresC.get(p.lexemeRank(-3)).equals("TIPO_STRING")){
                        if(identificadoresV.containsKey(p.lexemeRank(-3))){
                            if(identificadoresC.containsKey(p.lexemeRank(2))){
                                if(identificadoresV.get(p.lexemeRank(2)).equals("LCD")){

                                }else{
                                    errors.add(new ErrorLSSL(135, "----------> Error_135:  En el método de la impresora la variable no está asociada a un sensor de tipo LCD, Linea [#] Columna [%]",p, true));
                                }
                            }else{
                                errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                            }
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                                                    
                    }else{
                        errors.add(new ErrorLSSL(134, "----------> Error_134:  En el método de la impresora el valor de la variable no es de tipo string, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(-3).equals("VALOR_CADENA")){
                    if(identificadoresC.containsKey(p.lexemeRank(2))){
                        if(identificadoresV.get(p.lexemeRank(2)).equals("LCD")){
                        
                        }else{
                            errors.add(new ErrorLSSL(135, "----------> Error_135:  En el método de la impresora la variable no está asociada a un sensor de tipo LCD, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(133, "----------> Error_133:  En el método impresora el valor no es de tipo string, Linea [#] Columna [%]",p, true));
                }
            }
        }
        //IMPRESORA
        
        //SENSORES
        for(Production p : identProdMS){
            if(identificadoresC.containsKey(p.lexemeRank(1))){
                if(identificadoresT.containsKey(p.lexemeRank(1)) && identificadoresV.containsKey(p.lexemeRank(1))){
                    if(identificadoresT.get(p.lexemeRank(1)).equals("const")){
                        errors.add(new ErrorLSSL(126, "----------> Error_126:  La variable es una constante y no es posible modificar su valor, Linea [#] Columna [%]",p, true));
                    }else{
                        if(identificadoresC.get(p.lexemeRank(1)).equals("TIPO_STRING")){
                            if(identificadoresC.containsKey(p.lexemeRank(3))){
                                if(identificadoresC.get(p.lexemeRank(3)).equals("SENSOR")){
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("proximity")){
                                        if(p.lexemeRank(5).equals("distance") || p.lexemeRank(5).equals("time")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("temperature")){
                                        if(p.lexemeRank(5).equals("degree")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("LED")){
                                        if(p.lexemeRank(5).equals("state")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("LED_RGB")){
                                        if(p.lexemeRank(5).equals("state")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("LCD")){
                                        if(p.lexemeRank(5).equals("state")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("button")){
                                        if(p.lexemeRank(5).equals("state")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                    if(identificadoresV.get(p.lexemeRank(3)).equals("motor")){
                                        if(p.lexemeRank(5).equals("state")){
                                        
                                        }else{
                                            errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                        }
                                    }
                                }else{
                                    errors.add(new ErrorLSSL(145, "----------> Error_145:  La variable no está asociada a un tipo de sensor, Linea [#] Columna [%]",p, true));
                                }
                            }else{
                                errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                            }
                        }else{
                            errors.add(new ErrorLSSL(144, "----------> Error_144:  La variable no es tipo cadena, Linea [#] Columna [%]",p, true));
                        }
                    }
                }else{
                    if(identificadoresC.get(p.lexemeRank(1)).equals("TIPO_STRING")){
                        if(identificadoresC.containsKey(p.lexemeRank(3))){
                            if(identificadoresC.get(p.lexemeRank(3)).equals("SENSOR")){
                                if(identificadoresV.get(p.lexemeRank(3)).equals("proximity")){
                                    if(p.lexemeRank(5).equals("distance") || p.lexemeRank(5).equals("time")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                                if(identificadoresV.get(p.lexemeRank(3)).equals("temperature")){
                                    if(p.lexemeRank(5).equals("degree")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                                if(identificadoresV.get(p.lexemeRank(3)).equals("LED")){
                                    if(p.lexemeRank(5).equals("state")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                                if(identificadoresV.get(p.lexemeRank(3)).equals("LED_RGB")){
                                    if(p.lexemeRank(5).equals("state")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                                if(identificadoresV.get(p.lexemeRank(3)).equals("LCD")){
                                    if(p.lexemeRank(5).equals("state")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                                if(identificadoresV.get(p.lexemeRank(3)).equals("button")){
                                    if(p.lexemeRank(5).equals("state")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                                if(identificadoresV.get(p.lexemeRank(3)).equals("motor")){
                                    if(p.lexemeRank(5).equals("state")){

                                    }else{
                                        errors.add(new ErrorLSSL(146, "----------> Error_146:  El método del sensor es indefinido al sensor, Linea [#] Columna [%]",p, true));
                                    }
                                }
                            }else{
                                errors.add(new ErrorLSSL(145, "----------> Error_145:  La variable no está asociada a un tipo de sensor, Linea [#] Columna [%]",p, true));
                            }
                        }else{
                            errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(144, "----------> Error_144:  La variable no es tipo cadena, Linea [#] Columna [%]",p, true));
                    }
                }
            }else{
                errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
            }
        }
        //SENSORES
        
        //DELAY
        for(Production p : identProdMD){
            if(p.lexicalCompRank(2).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(2))){
                    if(identificadoresC.get(p.lexemeRank(2)).equals("TIPO_ENTERO")){
                        if(identificadoresV.containsKey(p.lexemeRank(2))){
                        
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(147, "----------> Error_147:  El valor de la variable no es de tipo de dato entero, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(2).equals("VALOR_NUMERO_ENTERO")){
                    
                }else{
                    errors.add(new ErrorLSSL(148, "----------> Error_148:  El valor no es de tipo de dato entero, Linea [#] Columna [%]",p, true));
                }
            }
        }
        //DELAY
        
        //IF
        for(Production p : identProdIR){
            if(p.lexicalCompRank(2).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(2))){
                    if(identificadoresC.get(p.lexemeRank(2)).equals("TIPO_ENTERO") || identificadoresC.get(p.lexemeRank(2)).equals("TIPO_DECIMAL")){
                        if(identificadoresV.containsKey(p.lexemeRank(2))){
                        
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(151, "----------> Error_151:  El valor de la variable 1 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(2).equals("VALOR_NUMERO_ENTERO") || p.lexicalCompRank(2).equals("VALOR_NUMERO_REAL")){

                }else{
                    errors.add(new ErrorLSSL(149, "----------> Error_149:  El valor 1 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                }
            }
            if(p.lexicalCompRank(4).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(4))){
                    if(identificadoresC.get(p.lexemeRank(4)).equals("TIPO_ENTERO") || identificadoresC.get(p.lexemeRank(4)).equals("TIPO_DECIMAL")){
                        if(identificadoresV.containsKey(p.lexemeRank(4))){
                        
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(152, "----------> Error_152:  El valor de la variable 2 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(4).equals("VALOR_NUMERO_ENTERO") || p.lexicalCompRank(4).equals("VALOR_NUMERO_REAL")){

                }else{
                    errors.add(new ErrorLSSL(149, "----------> Error_149:  El valor 2 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                }
            }
        }
        for(Production p : identProdER){
            if(p.lexicalCompRank(2).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(2))){
                    if(identificadoresC.get(p.lexemeRank(2)).equals("TIPO_ENTERO") || identificadoresC.get(p.lexemeRank(2)).equals("TIPO_DECIMAL")){
                        if(identificadoresV.containsKey(p.lexemeRank(2))){
                        
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(151, "----------> Error_151:  El valor de la variable 1 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(2).equals("VALOR_NUMERO_ENTERO") || p.lexicalCompRank(2).equals("VALOR_NUMERO_REAL")){

                }else{
                    errors.add(new ErrorLSSL(149, "----------> Error_149:  El valor 1 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                }
            }
            if(p.lexicalCompRank(4).equals("IDENTIFICADOR")){
                if(identificadoresC.containsKey(p.lexemeRank(4))){
                    if(identificadoresC.get(p.lexemeRank(4)).equals("TIPO_ENTERO") || identificadoresC.get(p.lexemeRank(4)).equals("TIPO_DECIMAL")){
                        if(identificadoresV.containsKey(p.lexemeRank(4))){
                        
                        }else{
                            errors.add(new ErrorLSSL(161, "----------> Error_161:  La variable no tiene valor asignado, Linea [#] Columna [%]",p, true));
                        }
                    }else{
                        errors.add(new ErrorLSSL(152, "----------> Error_152:  El valor de la variable 2 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                    }
                }else{
                    errors.add(new ErrorLSSL(127, "----------> Error_127:  La variable no ha sido declarada, Linea [#] Columna [%]",p, true));
                }
            }else{
                if(p.lexicalCompRank(4).equals("VALOR_NUMERO_ENTERO") || p.lexicalCompRank(4).equals("VALOR_NUMERO_REAL")){

                }else{
                    errors.add(new ErrorLSSL(149, "----------> Error_149:  El valor 2 de la condición no es de tipo entero o decimal, Linea [#] Columna [%]",p, true));
                }
            }
        }
        
        //IF
        
        System.out.print(identificadoresC);
        System.out.print(identificadoresV);
        System.out.print(identificadoresT);
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
            if(token.getLexicalComp().equals("Error_léxico_X") || token.getLexicalComp().equals("Error_léxico_0") || token.getLexicalComp().equals("Error_léxico_1") || token.getLexicalComp().equals("Error_léxico_2") || token.getLexicalComp().equals("Error_léxico_3") || token.getLexicalComp().equals("Error_léxico_4") || token.getLexicalComp().equals("Error_léxico_5") || token.getLexicalComp().equals("Error_léxico_6") || token.getLexicalComp().equals("Error_léxico_7") || token.getLexicalComp().equals("Error_léxico_8") || token.getLexicalComp().equals("Error_léxico_9") || token.getLexicalComp().equals("Error_léxico_10") || token.getLexicalComp().equals("Error_léxico_11") || token.getLexicalComp().equals("Error_léxico_12") || token.getLexicalComp().equals("Error_léxico_13")){
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
        identProdOrdenado.clear();
        identProdME.clear();
        identProdDP.clear();
        identProdDCV.clear();
        identProdDSV.clear();
        identProdA.clear();
        identProdLFCP.clear();
        identProdLFSP.clear();
        identProdO.clear();
        identProdFMCV.clear();
        identProdFMSV.clear();
        identProdFCP.clear();
        identProdFSP.clear();
        identProdMD.clear();
        identProdMS.clear();
        identProdMIC.clear();
        identProdMIL.clear();
        identProdB.clear();
        identProdL.clear();
        identProdIR.clear();
        identProdIL2V.clear();
        identProdIL1V.clear();
        identProdER.clear();
        identProdEL2V.clear();
        identProdEL1V.clear();
        
        identificadoresC.clear();
        identificadoresV.clear();
        identificadoresT.clear();
        codeHasBeenCompiled = false;
        Functions.clearDataInTable(T.tokens());
        Functions.clearDataInTable(Error.TablaL());
        E.estructuras().setText("");
        CI.cuadruplos().setText("Num --> Operador --> Operando 1 --> Operando 2 --> Resultado\n");
        S.Simbolos().setText("");
    }
    private void executeCode(ArrayList<String> blocksOfCode,int repeats){
        int temporal = 0;
        String metodo = "";
        int contador = 0;
        for(int j = 1;j <= repeats; j++){
            int repeatCode = -1;
            for(int i = 0; i < blocksOfCode.size(); i++){
                String blockOfCode = blocksOfCode.get(i);
                if(repeatCode != -1){
                    int[] posicionMarcador = CodeBlock.getPositionOfBothMarkers(blocksOfCode, blockOfCode);
                    executeCode(new ArrayList<>(blocksOfCode.subList(posicionMarcador[0], posicionMarcador[1])),repeatCode);
                    repeatCode = -1;
                    i = posicionMarcador[1];
                }else{
                    String[] sentences = blockOfCode.split("!");
                    for(String sentence: sentences){
                        sentence = sentence.trim();
                        if(sentence.startsWith("port_")){
                            temporal++;
                            CI.cuadruplos().append(contador+" --> ");
                            contador++;
                        }
                    }
                }
            }
        }
        showMessageDialog(this,"Genera este codigo ensamblador\n\nLIST P=16F887\n" +
"INCLUDE <P16F887.INC>\n" +
"__CONFIG _CONFIG1, _FOSC_INTRC_NOCLKOUT & _WDTE_OFF & _PWRTE_OFF & _LVP_OFF\n" +
"\n" +
"CBLOCK  0x20\n" +
"highfactor,\n" +
"highfactor1    \n" +
"ENDC\n" +
"\n" +
"TMR0_LOAD equ   .164	; Approximate value for 100 us of timing.\n" +
"\n" +
"#define OUTPUT  PORTB,0	; Rename RB0 as OUTPUT\n" +
"#define	OUTPUT1	PORTB,1\n" +
"#define	OUTPUT2	PORTB,2\n" +
" \n" +
"#define INPUT1  PORTA,0	; Rename RA0 as INPUT1\n" +
"#define INPUT2  PORTA,1	; Rename RA1 as INPUT2\n" +
"\n" +
"org	    0x00  \n" +
"goto    Main\n" +
"org	    0x04\n" +
"goto    TIMER0_Interrupt	; Interruption vector.\n" +
"\n" +
"Main\n" +
"    bsf     STATUS,6    ; Seleccionar el banco 3.\n" +
"    bsf     STATUS,5\n" +
"    clrf    ANSEL   ; PORTA y PORTB funcionan digitalmente,\n" +
"    clrf    ANSELH\n" +
"    movlw   0xD8\n" +
"    movwf   OPTION_REG ; El reloj de TIMER0 es igual a Fosc/4, prescaler para WDT.\n" +
"    \n" +
"    ; Configuración para el puerto B\n" +
"    bcf     STATUS,6    ; Seleccionar el banco 1.\n" +
"    movlw   0x61\n" +
"    movwf   OSCCON   ; Oscilador interno predeterminado a 4MHz.\n" +
"    bcf     TRISB,0 ; RB0 funciona como salida.\n" +
"    bcf	    TRISB,1\n" +
"    bcf	    TRISB,2\n" +
"    ; Configuración para el puerto C\n" +
"    bcf     TRISC,2 ; RC2 funciona como salida. para encender el LED\n" +
"\n" +
"    bcf     STATUS,5   ; Seleccionar el banco 0.\n" +
"    clrf    PORTB\n" +
"    clrf    PORTC  ; Asegurar que RC0 esté apagado al inicio.\n" +
"    movlw   TMR0_LOAD\n" +
"    movwf   TMR0    ; TIMER0 se iguala a 164.\n" +
"    movlw   0xA0\n" +
"    movwf   INTCON  ; GIE = 1 e interrupción de desbordamiento de Timer0.\n" +
"Loop\n" +
"    \n" +
"    btfss   INPUT1	;RA0 == 0? Servomotor at 0°.\n" +
"    call    Condition1\n" +
"    btfss   INPUT2	;RA1 == 0? Servomotor at 180°.\n" +
"    call    Condition2\n" +
"    goto    Loop\n" +
"\n" +
"Condition1		;Load 5 to highfactor.\n" +
"    call    Delay_50ms\n" +
"    btfsc   INPUT1\n" +
"    return  \n" +
"Stay1	\n" +
"    btfss   INPUT1\n" +
"    goto    Stay1\n" +
"    bsf     PORTC, 2   ; Enciende el LED\n" +
"    movlw   .11\n" +
"    movwf   highfactor\n" +
"    return\n" +
"\n" +
"Condition2		;Load 22 to highfactor.\n" +
"    call    Delay_50ms\n" +
"    btfsc   INPUT2\n" +
"    return\n" +
"Stay2\n" +
"    btfss   INPUT2\n" +
"    goto    Stay2\n" +
"    bcf     PORTC, 2   ; Apaga el led colocandolo en 0\n" +
"    movlw   .22\n" +
"    movwf   highfactor\n" +
"    return\n" +
"\n" +
"CBLOCK\n" +
"timer0_counter\n" +
"ENDC\n" +
"\n" +
";///////////////////////////////////////////////////////////////////////\n" +
"TIMER0_Interrupt	    \n" +
"    movlw   TMR0_LOAD	    ; Load TIMER0 with the value of TMR0_LOAD\n" +
"    movwf   TMR0	    \n" +
"    decfsz  timer0_counter  ; Decrements by one unit, jump if it's zero\n" +
"    goto    EndInterrupt    ; Go to EndInterrupt\n" +
"    btfsc   OUTPUT	    ; Complements the value of the output.\n" +
"    goto    HighS	    \n" +
"    btfsc   OUTPUT1	    ; Complements the value of the output.\n" +
"    goto    HighS1\n" +
"LowS1			    ; set the output to zero.\n" +
"    bsf	    OUTPUT1\n" +
"    movf    highfactor1,W    ; Time the signal is high.\n" +
"    movwf   timer0_counter\n" +
"    goto    EndInterrupt\n" +
"\n" +
"HighS1	\n" +
"    bcf	    OUTPUT1	    ; Set the output to one.\n" +
"    movf    highfactor1,W    \n" +
"    sublw   .200	    ; Time the signal is low.\n" +
"    movwf   timer0_counter    \n" +
"LowS			    ; set the output to zero.\n" +
"    bsf	    OUTPUT\n" +
"    movf    highfactor,W    ; Time the signal is high.\n" +
"    movwf   timer0_counter\n" +
"    goto    EndInterrupt\n" +
"\n" +
"HighS	\n" +
"    bcf	    OUTPUT	    ; Set the output to one.\n" +
"    movf    highfactor,W    \n" +
"    sublw   .200	    ; Time the signal is low.\n" +
"    movwf   timer0_counter\n" +
"    \n" +
"EndInterrupt\n" +
"    bcf	    INTCON,2	    ; Clear interrupt bit of TIMER0.\n" +
"    retfie\n" +
"    INCLUDE <C:\\Users\\Alex\\Downloads\\DELAYS.inc>\n" +
"END");
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
    private javax.swing.JMenuItem btnCodigo1;
    private javax.swing.JMenuItem btnCodigo2;
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
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem21;
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
