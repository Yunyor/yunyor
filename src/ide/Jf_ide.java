/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ide;
import java.io.*;
import java.net.ConnectException;
import java.util.Scanner;
import javax.swing.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import com.itextpdf.text.Paragraph;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.AncestorListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;

import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.Scanner;
import javax.swing.WindowConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;


/**
 *
 * @author Saúl
 */
public class Jf_ide extends javax.swing.JFrame implements ActionListener{
    String filename,fileContents,fileAdress;    
    Clipboard clipboard = getToolkit().getSystemClipboard();
    UndoManager editManager = new UndoManager();
    Editar edit = new Editar(this);
    private Editor2 tpEditor;
    int lineCount;
    
    private int buscaUltPalab (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int buscaPrimPalab (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }
   
    
    public Jf_ide() {
        initComponents();
        NumeroLinea numeroLinea = new NumeroLinea(textArea);
        jScrollPane2.setRowHeaderView(numeroLinea);
        
        JMenuItem openFile = new JMenuItem("Abrir");
        openFile.setActionCommand("Abrir");
        openFile.addActionListener(this);
        openFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/open-file-icon.png")));
        jMenu1.add(openFile);
                
        JMenuItem saveFile = new JMenuItem("Guardar");
        saveFile.setActionCommand("Guardar");
        saveFile.addActionListener(this);
        saveFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/floppy-icon.png")));
        jMenu1.add(saveFile);
        
        JMenuItem saveAsFile = new JMenuItem("Guardar Como");
        saveAsFile.setActionCommand("GuardarComo");
        saveAsFile.addActionListener(this);
        saveAsFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Actions-document-save-as-icon.png")));
        jMenu1.add(saveAsFile);
        
        JMenuItem exit = new JMenuItem("Salir");
        exit.setActionCommand("Salir");
        exit.addActionListener(this);
        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Close-2-icon.png")));
        jMenu1.add(exit);
        
        JMenuItem undoText = new JMenuItem("Deshacer");
        undoText.setActionCommand("Undo");
        undoText.addActionListener(this);
        undoText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Undo-icon.png")));
        jMenu2.add(undoText);
        
        JMenuItem redoText = new JMenuItem("Rehacer");
        redoText.setActionCommand("Redo");
        redoText.addActionListener(this);
        redoText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Redo-icon.png")));
        jMenu2.add(redoText);
        
        JMenuItem Ir = new JMenuItem("Ir a la linea ...");
        Ir.setActionCommand("Ir");
        Ir.addActionListener(this);
        Ir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Actions-go-next-icon.png")));
        jMenu2.add(Ir);
                
        
        
        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
        final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);        
        
        DefaultStyledDocument doc = new DefaultStyledDocument() {     
            public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = buscaUltPalab(text, offset);
                if (before < 0) before = 0;
                int after = buscaPrimPalab(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches("(\\W)*(!DOCTYPE|abbr|address|applet|article|figure|form|fieldset|figcaption|footer|area|aside|a|base|basefont|details|datalist|option|output"
                                + "|big|button|base|blockquote|body|br|b|caption|colgroup|col|center|canvas|command|cite|code|dd|bdo|bdi|rp|rt|div|dir|dl|dt|del|dfn|data|em|embed|font|h1|h2|h3|h4|h5"
                                + "|h6|head|hr|html|header|img|iframe|input|isindex|kbd|i|kbd|link|legend|label|li|map|main|menu|meta|metter|mark|math|ol|option|param|pre|p|samp|script|audio|progress|"
                                + "select|span|small|summary|strike|strong|style|svg|sub|source|sup|section|table|td|time|track|textarea|tbody|thead|tfoot|th|title|tr|tt|ul|u|var|nav|q|ruby|wbr|ins|object|video|"
                                + ")"))
                            setCharacterAttributes(wordL, wordR - wordL, attr, false);
                        else
                            setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                        wordL = wordR;
                    }
                    wordR++;
                }
            }

            public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);
                String text = getText(0, getLength());
                int before = buscaUltPalab(text, offs);
                if (before < 0) before = 0;
                int after = buscaPrimPalab(text, offs);

                if (text.substring(before, after).matches("(\\W)*(!DOCTYPE|abbr|address|applet|article|figure|form|fieldset|figcaption|footer|area|aside|a|base|basefont|details|datalist|option|output"
                                + "|big|button|base|blockquote|body|br|b|caption|colgroup|col|center|canvas|command|cite|code|dd|bdo|bdi|rp|rt|div|dir|dl|dt|del|dfn|data|em|embed|font|h1|h2|h3|h4|h5"
                                + "|h6|head|hr|html|header|img|iframe|input|isindex|kbd|i|kbd|link|legend|label|li|map|main|menu|meta|metter|mark|math|ol|option|param|pre|p|samp|script|audio|progress|"
                                + "select|span|small|summary|strike|strong|style|svg|sub|source|sup|section|table|td|time|track|textarea|tbody|thead|tfoot|th|title|tr|tt|ul|u|var|nav|q|ruby|wbr|ins|object|video|"
                                + ")")) {
                    setCharacterAttributes(before, after - before, attr, false);
                } else {
                    setCharacterAttributes(before, after - before, attrBlack, false);
                }
            }
          
        };
       textArea.setStyledDocument(doc); 
      
       doc.addUndoableEditListener(
            new UndoableEditListener() {
                @Override
                public void undoableEditHappened(UndoableEditEvent e) {
                    editManager.addEdit(e.getEdit());       
                }
        });
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        txtruta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newFile = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        cutText = new javax.swing.JMenuItem();
        copyText = new javax.swing.JMenuItem();
        pasteText = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Magnifying-Glass-icon.png"))); // NOI18N
        searchButton.setText("Buscar Palabra");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(textArea);

        jButton1.setText("Generar Arbol HTML");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Abrir En HTML");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("RUTA PDF");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txtruta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtrutaActionPerformed(evt);
            }
        });

        jLabel1.setText("Ruta para Guardar el PDF");

        jButton4.setText("Generar PDF");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Mario ALEXIS SINAY GAYTAN");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Yunyor Gay");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("jButton7");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jMenu1.setText("Archivo");

        newFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/new-file-icon (12).png"))); // NOI18N
        newFile.setText("Nuevo");
        newFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileActionPerformed(evt);
            }
        });
        jMenu1.add(newFile);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        cutText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/cut-icon.png"))); // NOI18N
        cutText.setText("Cortar");
        cutText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutTextActionPerformed(evt);
            }
        });
        jMenu2.add(cutText);

        copyText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/copy-icon.png"))); // NOI18N
        copyText.setText("Copiar");
        copyText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyTextActionPerformed(evt);
            }
        });
        jMenu2.add(copyText);

        pasteText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ide/Iconos/Paste-icon.png"))); // NOI18N
        pasteText.setText("Pegar");
        pasteText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteTextActionPerformed(evt);
            }
        });
        jMenu2.add(pasteText);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 49, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtruta, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(382, 382, 382)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(37, 37, 37)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchButton)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4)
                            .addComponent(jButton3))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(jButton7)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 924, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pasteTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteTextActionPerformed
        try{
            Transferable pasteText = clipboard.getContents(Jf_ide.this);
            String sel = (String) pasteText.getTransferData(DataFlavor.stringFlavor);
            textArea.replaceSelection(sel);
        }catch(Exception e){
            System.out.println("Esta opcion no funciona correctamente");
        }
    }//GEN-LAST:event_pasteTextActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        serchTextArea(textArea,searchField.getText() );
    }//GEN-LAST:event_searchButtonActionPerformed

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch(command){
            case "Undo": edit.undo(); edit.undo(); break;
            case "Redo": edit.redo(); edit.redo(); break;
            case "GuardarComo": guardarComo(); break;
            case "Guardar": guardar(); break;
            case "Salir": salir(); break;
            case "Abrir": abrirArchivo(); break;
            case "Ir": irA(); break;
        }
        
    }

    private Document convertDocumentToString(String xn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
    class myHighligher extends DefaultHighlighter.DefaultHighlightPainter{

        public myHighligher(Color c) {
            super(c);
        }
    }    
    
    DefaultHighlighter.DefaultHighlightPainter highlighter = new myHighligher(Color.lightGray);
    
    public void removeHighLight(JTextComponent textComp){
        Highlighter removeHighlighter = textComp.getHighlighter();
        Highlighter.Highlight[] remove = removeHighlighter.getHighlights();
        
        for(int i=0; i<remove.length; i++){
            if(remove[i].getPainter() instanceof myHighligher){
                removeHighlighter.removeHighlight(remove[i]);
            }               
        }
    
    }
    
    public void serchTextArea(JTextComponent textcomp, String textString){
        removeHighLight(textcomp);
        try{
            Highlighter hilight = textcomp.getHighlighter();
            javax.swing.text.Document doc = textcomp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos =0;
            
            while((pos = text.toUpperCase().indexOf(textString.toUpperCase(),pos)) >=0 ){
                hilight.addHighlight(pos, pos+textString.length(), highlighter);
                pos += textString.length();
            }
            
        }catch(Exception e){
        
        }
    
    }

    private void newFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileActionPerformed
        textArea.setText("");
        setTitle("Nuevo");
        filename = null;
        fileAdress = null;
    }//GEN-LAST:event_newFileActionPerformed

    private void cutTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutTextActionPerformed
        String cutString = textArea.getSelectedText();
        StringSelection cutSelection = new StringSelection(cutString);
        clipboard.setContents(cutSelection, cutSelection);
        textArea.replaceSelection("");        
    }//GEN-LAST:event_cutTextActionPerformed

    private void copyTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyTextActionPerformed
        String copytext = textArea.getSelectedText();
        StringSelection copySelection = new StringSelection(copytext);
        clipboard.setContents(copySelection, copySelection);
        textArea.replaceSelection(copytext);
    }//GEN-LAST:event_copyTextActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed
    String mandar;
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            // TODO add your handling code here:
         org.jsoup.nodes.Document html = null;
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("exception.log", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
        }
        File input = new File(fileAdress+filename);
        
        
      
        try{
            html = Jsoup.parse(input,"UTF-8");
        }catch(ConnectException e){
            System.out.println("Impossibile stabilire la connesione");
            writer.println(e.toString());
            System.exit(404);
        } catch (IOException ex) {
            Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        html.normalise();
        
        intento frame = new intento(html.title());
                                
        html.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if(!node.nodeName().equals("#text") && 
                   !node.nodeName().equals("#comment")){ 
                   //!node.nodeName().equals("#data")){
                        for(int i = 0;i<depth;i++)
                            System.out.print("-");
                    System.out.println(node.nodeName());
                    System.out.println(node.siblingNodes());
                    
                    frame.add(node.nodeName(),depth);
                    
                }
            }
            @Override
            public void tail(Node node, int depth) { /* Nope */ }
        });       
        
        frame.out();
  
        frame.setSize(400, 320);
        frame.toFront();
        frame.setVisible(true);
        writer.close();
       
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
                if(filename == null){
            guardarComo();
        }
        else{
                    try {
                            
                        String url= fileAdress+filename;
                        ProcessBuilder p= new ProcessBuilder();
                        p.command("cmd.exe","/c",url);
                        p.start();
                    } catch (IOException ex) {
                        Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }
    
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        JFileChooser dlg= new JFileChooser();
        int option = dlg.showSaveDialog(this);
        if(option== JFileChooser.APPROVE_OPTION){
            File f = dlg.getSelectedFile();
            txtruta.setText(f.toString());
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txtrutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtrutaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtrutaActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        String ruta = txtruta.getText();
        String contenido = textArea.getText();
         if(ruta == null){
               JOptionPane.showMessageDialog(null,"Porfavor Genere la ruta para guardar el archivo primero");
        }
        else{
        try{
           
            FileOutputStream archivo = new FileOutputStream(ruta+".pdf");
            com.itextpdf.text.Document doc =  new com.itextpdf.text.Document();
            PdfWriter.getInstance(doc,archivo);
            doc.open();
            doc.add(new Paragraph(contenido));
            doc.close();
            JOptionPane.showMessageDialog(null,"pdf correctamente creado");
        } catch(Exception e){
            System.out.println("error: "+e);
        }
         }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed
    
    public void abrirArchivo(){
        FileDialog filedialog  = new FileDialog(Jf_ide.this, "Abrir", FileDialog.LOAD);
        filedialog.setVisible(true);
        
        if(filedialog.getFile() != null){
            filename = filedialog.getFile();
            fileAdress = filedialog.getDirectory();
            setTitle(filename);
        }
        
        try{
           BufferedReader br = new BufferedReader(new FileReader(fileAdress+filename));
           textArea.setText("");
           String line= null;
           StringBuilder sb = new StringBuilder();
                   
           while((line = br.readLine()) != null){
               sb.append(line + "\n");
               textArea.setText(sb.toString());
           }
           br.close();
           
        }catch(Exception e){
            System.out.println("No se pudo guardar correctamente");
        }
    }
    
    public void guardarComo(){
        FileDialog filedialog = new FileDialog(Jf_ide.this,"Gurdar",FileDialog.SAVE);
        filedialog.setVisible(true);
        
        if(filedialog.getFile() != null){
            filename = filedialog.getFile();
            fileAdress = filedialog.getDirectory();
            setTitle(filename);
        }
        
        try{
            FileWriter fw = new FileWriter(fileAdress + filename);
            fw.write(textArea.getText());
            fw.close();
        }catch(Exception e){
            System.out.println("No se pudo realizar guardar como correctamente");
        }
    }
    
    public void guardar(){
       
        if(filename == null){
            guardarComo();
        }
        else{
            try{
                FileWriter fw = new FileWriter(fileAdress + filename);
                fw.write(textArea.getText());
                setTitle(filename);
                fw.close();
            }catch(Exception e){
                System.out.println("No se pudo guardar correctamente :(");
            }
        }
            org.jsoup.nodes.Document html = null;
        
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("exception.log", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
        }
        File input = new File(fileAdress+filename);
        
        
      
        try{
            html = Jsoup.parse(input,"UTF-8");
        }catch(ConnectException e){
            System.out.println("Impossibile stabilire la connesione");
            writer.println(e.toString());
            System.exit(404);
        } catch (IOException ex) {
            Logger.getLogger(Jf_ide.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        html.normalise();
        
        intento frame = new intento(html.title());
                                
        html.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if(!node.nodeName().equals("#text") && 
                   !node.nodeName().equals("#comment")){ 
                   //!node.nodeName().equals("#data")){
                        for(int i = 0;i<depth;i++)
                            System.out.print("-");
                    System.out.println(node.nodeName());
                    System.out.println(node.siblingNodes());
            
                    frame.add(node.nodeName(),depth);
                    
                }
            }
            @Override
            public void tail(Node node, int depth) { /* Nope */ }
        });       
        
        frame.out();
  
        frame.setSize(400, 320);
        frame.toFront();
        frame.setVisible(true);
        writer.close();
}
        
        
    
    public int getLineCount(){ 
        lineCount=0;
        Scanner sc=new Scanner(textArea.getText());
        while(sc.hasNextLine()){
            String line = sc.nextLine(); 
            lineCount++;
       }
        return lineCount;
    }
     
     public int setcursor(int newlineno) {
        int pos = 0;
        int i = 0;
        String line = "";
        Scanner sc = new Scanner(textArea.getText());
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            i++;
            if (newlineno > i) {
                pos = pos + line.length() + 1;
            }
        }
        return pos;
    }
     
     public void irA(){
          do {
            try {
                String str = (String) JOptionPane.showInputDialog(this, 
                        "Numero de Linea:\t", "Ir a la linea:", 
                        JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (str == null) {
                    break;
                }
                int lineNumber = Integer.parseInt(str);
                lineCount = getLineCount();
                System.out.println(lineCount);
                if (lineNumber > lineCount) {
                    JOptionPane.showMessageDialog(this, 
                            "El rango de la linea no existe", "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                //  for ( int i = 0 ; i < lineCount; i++ ){
                //  if ( i+1 == lineNumber ) {
                //Rectangle rectangle = textPane.modelToView(textPane.getCaretPosition());
                textArea.setCaretPosition(0);
                System.out.println(setcursor(lineNumber));
                textArea.setCaretPosition(setcursor(lineNumber));
                return;
                //  }
                //  }
            } catch (Exception e) {
            }
        } while (true);
     }
    
    public void salir(){
        System.exit(0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])throws IOException  {
   
      
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
            java.util.logging.Logger.getLogger(Jf_ide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Jf_ide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Jf_ide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Jf_ide.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Jf_ide().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem copyText;
    private javax.swing.JMenuItem cutText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem newFile;
    private javax.swing.JMenuItem pasteText;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    public javax.swing.JTextPane textArea;
    private javax.swing.JTextField txtruta;
    // End of variables declaration//GEN-END:variables
}
