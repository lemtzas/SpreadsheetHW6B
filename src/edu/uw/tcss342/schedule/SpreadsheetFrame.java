package edu.uw.tcss342.schedule;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

//import SpreadsheetCore;

/**
 * GUI for the Spreadsheet. Frame Class
 * 
 * @author Alex Stringham
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SpreadsheetFrame extends JFrame {
        /**
         * Name of this spreadsheet GUI frame.
         */
        public static final String NAME = "Spreadsheet Program - TCSS 342";

        /**
         * Default number of columns in the spreadsheet.
         */
        public static final int COLUMNS = 12;

        /**
         * Default number of rows in the spreadsheet.
         */
        public static final int ROWS = 10;

        /**
         * Main method to run a new instance of the Spreadsheet
         * 
         * @param the_args : command line.
         */
        public static void main(final String... the_args) {
                final SpreadsheetFrame gui = new SpreadsheetFrame(new SpreadSheet(
                                ROWS, COLUMNS));
                gui.setUp();
        }

        /**
         * The cell table in the spreadsheet GUI.
         */
        private SpreadsheetPanel panel;

        /**
         * Constructor
         * 
         * @param the_spreadsheet
         *            The spreadsheet.
         */
        public SpreadsheetFrame(final SpreadSheet the_spreadsheet) {
                super(NAME);
                panel = new SpreadsheetPanel(the_spreadsheet);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
        }

        
        /**
         * Method for setting up the components needed for Spreadsheet GUI.
         */
        public void setUp() {
                add(panel);
                setJMenuBar(Bar());
                pack();
                setVisible(true);
        }
        
        /**
         * 		Method for creating the menu for the spreadsheet.
         * @return A file menu for the spreadsheet.
         */
        private JMenu menu() { //formerly fileMenu
                final JMenu file = new JMenu("File");
                file.setMnemonic('F');
                
                final JMenuItem resetPanel = new JMenuItem(new AbstractAction("New") {
 
                        public void actionPerformed(final ActionEvent the_event) {
                                panel.reset();
                                panel.update();
                        }
                });
                
                //Menu Action for opening a new spreadsheet
                resetPanel.setMnemonic('N');
                file.add(resetPanel);
                final JMenuItem openAction = new JMenuItem(new AbstractAction("Open") {
                        @Override
                        public void actionPerformed(final ActionEvent the_event) {
                                JFileChooser chooser = new JFileChooser();
                                chooser.addChoosableFileFilter(new SpreadsheetExtension());
                                chooser.setAcceptAllFileFilterUsed(false);
                                int returnVal = chooser.showOpenDialog(SpreadsheetFrame.this);
                                if (returnVal == JFileChooser.APPROVE_OPTION)
                                        try {
                                                panel.load(chooser.getSelectedFile().getCanonicalPath());
                                        } catch (Exception e) {
                                        }
                                panel.update();
                        }
                });
                openAction.setMnemonic('O');
                file.add(openAction);
                
                //Menu Item for saving a spreadsheet
                final JMenuItem saveAction = new JMenuItem(new AbstractAction("Save") {
                        @Override
                        public void actionPerformed(final ActionEvent the_event) {
                                JFileChooser chooser = new JFileChooser();
                                chooser.addChoosableFileFilter(new SpreadsheetExtension());
                                chooser.setAcceptAllFileFilterUsed(false);                           
                                int value = chooser.showSaveDialog(SpreadsheetFrame.this);
                                if (value == JFileChooser.APPROVE_OPTION)
                                        try {
                                          String path = chooser.getSelectedFile().getCanonicalPath();
                                          String fileExtension = path.substring(path.length() - 4);
                                          String jad = ".jad";
                                          if (!fileExtension.equals(jad))
                                          {
                                            path = path.concat(jad);
                                          }
                                          panel.save(path);
                                        } catch (Exception e) { }
                                panel.update();
                        }
                });
                
                saveAction.setMnemonic('S');
                file.add(saveAction);
                file.addSeparator();
                
                //Menu Item for exiting the spreadsheet
                final Action quit = new AbstractAction("Quit") {
                        public void actionPerformed(final ActionEvent the_event) {
                                dispose();
                        }
                };
                
                final JMenuItem exit = new JMenuItem(quit);
                exit.setMnemonic('Q');
                file.add(quit);
                return file;
        }
        

        /**
         * @return The menu bar.
         */
        private JMenuBar Bar() {
                final JMenuBar menuBar = new JMenuBar();
                menuBar.add(menu());
                // menu_bar.add(editMenu());
                menuBar.add(help());
                return menuBar;
        }
        
        /**
         * 
         * @return Help menu.
         */
        private JMenu help() {
                final JMenu help_menu = new JMenu("Help");
           
                //Stub for now, need to write a "User guide" on how our spreadsheet works
                //Along with our names, etc.
                
                return help_menu;
        }




        
        /**
         * Filter to only accept our extension type, .jad
         * @author Alex Stringham
         * 
         */
        public class SpreadsheetExtension extends FileFilter
        {
          /**
           * String to declare our file extension.
           */
          public final static String jad = "jad";
          
          /**
           * Getter for returning the extension of a given file.
           * @param file : the chosen file.
           */  
          public String getExt(File file) {
        	  String fileName = file.getName();
              String extension = null;
             
              int i = fileName.lastIndexOf('.');

              if (i > 0 &&  i < fileName.length() - 1) {
                  extension = fileName.substring(i+1).toLowerCase();
              }
              return extension;
          }

          
          /**
           * Checks to see whether a chosen file is the type that
           * this spreadsheet accepts.
           * 
           * @param file : the chosen file by the user.
           * @return : True is the file extension is .jad, false otherwise.
           */
          public boolean accept(final File file)
          {
            if (file.isDirectory())
            {
              return true;
            }
            
            String fileExtension = getExt(file);
            if (fileExtension != null)
            {
              if (fileExtension.equals(jad))
              {
                return true;
              }
              else
              {
                return false;
              }
            }
            return false;
          }
          
          @Override
          public String getDescription() {
            return "Spreadsheet";
          }
          
          
       
        }
}
