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
         * Main method to run a new instance of the Spreadsheet
         * 
         * @param the_args : command line.
         */
        public static void main(final String... the_args) {
                final SpreadsheetFrame gui = new SpreadsheetFrame(new SpreadSheet( ));
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
         *         	Method for creating the menu for the spreadsheet.
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
        	final StringBuffer stringbuff = new StringBuffer();
        	final StringBuffer stringbuff2 = new StringBuffer();
        	final JMenu help = new JMenu("Help");
        	help.setMnemonic('H');
        
        	stringbuff.append("Awesome Development Crew: \n");
        	stringbuff.append("David - Jacob - Alex \n");
        	stringbuff.append("Spreadsheet Project v1.0");
        	
        	stringbuff2.append("-	To create a new Spreadsheet, simply go to “File”  -> “New”. This will create a blank spreadsheet that has no saved data in it. \n \n");
        	stringbuff2.append("-	This Spreadsheet does not support the user adding or deleting columns/rows. The table size for this Spreadsheet is fixed. It does however support resizing (zooming). \n \n");
        	stringbuff2.append("-	Changing a cells formula  can be done by double clicking on the cell you wish to change. After double clicking, the cells current formula should appear (nothing will \n" +
        			"				come up if there is no formula) and you can change it however you like. To commit the formula, you must press Enter. Upon pressing enter, the Spreadsheet will update \n " +
        			"				the cells value, along with any other cell that it depends on it. \n \n");
        	stringbuff2.append("-	When saving/loading a spreadsheet, you must save/load  with the “.jad” extension. It will not save/load any other extension type.");
        	
        	final Action aboutAction = new AbstractAction("About")
        	{
        		public void actionPerformed(final ActionEvent event)
        		{
        		JOptionPane.showMessageDialog(null, stringbuff, "About Spreadsheet", 1);
        		}
        	};
        	
        	JMenuItem about = new JMenuItem(aboutAction);
        	about.setMnemonic('A');
        	
        	final Action instructionsAction = new AbstractAction("Instructions")
        	{
        		public void actionPerformed(final ActionEvent event)
        		{
        			JOptionPane.showMessageDialog(null, stringbuff2, "Users Guide", 1);
        		}
        	};
        	
        	JMenuItem instructions = new JMenuItem(instructionsAction);
        	instructions.setMnemonic('I');
        	help.add(instructionsAction);
        	help.add(aboutAction);

                
            return help;
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
        
        /**
         * Method for setting up the components needed for Spreadsheet GUI.
         */
        public void setUp() {
                add(panel);
                setJMenuBar(Bar());
                pack();
                setVisible(true);
        }
}