package edu.uw.tcss342.schedule;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


//import Spreadsheet;

//import CellToken;

/**
 * Panel class for the spreadsheet.
 * 
 * @author Alex Stringham
 * 
 */
@SuppressWarnings("serial")
public class SpreadsheetPanel extends JPanel {
	
	
	
	
	
	
	 /**
     * The default height of a cell.
     */
    public static final int INIT_CELL_HEIGHT = 30;

    /**
     * The default width of a cell.
     */
    public static final int INIT_CELL_WIDTH = 80;

    /**
     * The cell array of this spreadsheet.
     */
    private CellsGUI[][] cellArray;

    /**
     * The cell's height.
     */
    private int cellHeight;

    /**
     * The cell's width.
     */
    private int cellWidth;


    /**
     * The columns in this spreadsheet.
     */
    private int myColumns;

    /**
     * The rows in this spreadsheet.
     */
    private int myRows;

    /**
     * The spreadsheet.
     */
    private Spreadsheet mySpreadsheet;

    /**
     * Construct a spreadsheet board from the input.
     * 
     * @param the_spreadsheet
     *            The spreadsheet.
     */
    public SpreadsheetPanel(final Spreadsheet the_spreadsheet) {
            super(new GridLayout(the_spreadsheet.getNumRows() + 1, the_spreadsheet
                            .getNumColumns() + 1));
            mySpreadsheet = the_spreadsheet;
            
            myRows = the_spreadsheet.getNumRows();
            myColumns = the_spreadsheet.getNumColumns();
            cellHeight = INIT_CELL_HEIGHT;
            cellWidth = INIT_CELL_WIDTH;
            
            setPreferredSize(new Dimension(myColumns * cellWidth, myRows * cellHeight));
            cellArray = new CellsGUI[myRows][myColumns];
            initialize();
    }

    
    /**
     * Initializes the array of cells and places them in the panel.
     */
    private void initialize() {
            for (int i = -1; i < myRows; i++)
                    for (int j = -1; j < myColumns; j++) {
                            CellToken cellToken = new CellToken(i, j);

                            if (i == -1) {
                                    if (j == -1)
                                            add(new JLabel("", null, SwingConstants.CENTER));
                                    else
                                            add(new JLabel(cellToken.columnToString(), null,
                                                            SwingConstants.CENTER));
                            } else if (j == -1)
                                    add(new JLabel(Integer.toString(i), null,
                                                    SwingConstants.CENTER));
                            else {
                                    cellArray[i][j] = new CellsGUI(cellToken);
                                    // my_cell_array[i][j].setText(my_spreadsheet.cellValueToString(my_cell_array[i][j].getCellToken()));
                                    setCellText(cellArray[i][j]);
                                    cellArray[i][j]
                                                    .addMouseListener(new MouseCellListener());
                                    cellArray[i][j].addKeyListener(new KeyCellListener());
                                    cellArray[i][j]
                                                    .addFocusListener(new FocusCellListener());
                                    // my_cell_array[i][j].setPreferredSize(new
                                    // Dimension(DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT));
                                    add(cellArray[i][j]);
                            }
                    }
    }
    
    /**
     * Updates each cells values.
     */
    public void update() {
            for (int i = 0; i < myRows; i++)
                    for (int j = 0; j < myColumns; j++)
                            setCellText(cellArray[i][j]);
    }
    

    /**
     * Resets the entire spreadsheet. Clears any data stored in cells.
     */
    public void reset() {
            mySpreadsheet = new Spreadsheet(myRows, myColumns);
    }
    
    @Override
    public void paintComponent(final Graphics the_graphics) {
            super.paintComponent(the_graphics);
            setPreferredSize(new Dimension(myColumns * cellWidth, myRows
                            * cellHeight));
    }
    
    /**
     * Sets the height of each cell.
     * 
     * @param the_height : height of the cell.
     * 
     */
    public void setCellHeight(final int the_height) {
            cellHeight = the_height;
            setPreferredSize(new Dimension(myColumns * cellWidth, myRows
                            * cellHeight));
    }
    
    /**
     * Sets the width of each cell.
     * 
     * @param the_width : width of the cell.
     * 
     */
    public void setCellWidth(final int the_width) {
            cellWidth = the_width;
            setPreferredSize(new Dimension(myColumns * cellWidth, myRows
                            * cellHeight));
    }

    /**
     * Set the text output of the cell. If the cell's formula is not empty, the
     * cell will show its value. Otherwise, the cell will be blank.
     * 
     * @param the_cell
     */
    private void setCellText(final CellsGUI the_cell) {
            if (mySpreadsheet.cellFormulaToString(the_cell.getToken())
                            .isEmpty())
                    the_cell.setText("");
            else
                    the_cell.setText(mySpreadsheet.cellValueToString(the_cell
                                    .getToken()));
    }

    
    /**
     * Getter for the cells height.
     * 
     * @return The cell's height.
     */
    public int getCellHeight() {
            return cellHeight;
    }

    /**
     * Getter for the cells width.
     * 
     * @return The cell's width.
     */
    public int getCellWidth() {
            return cellWidth;
    }

   

    /**
     * Load a saved spreadsheet.
     * @param canonicalPath
     */
    public void load(String path) throws Exception {
            BufferedReader input = new BufferedReader(new FileReader(path));
            String line = input.readLine();
            String output = new String();
            final String empty = new String(" ");
            StringTokenizer stringToken;
            int lineNumber = 0;
            int tokenNumber = 0;
            
            if (line != null) {
                    reset();
            }
            do {
                    tokenNumber = 0;
                    stringToken = new StringTokenizer(line, ",");
                    while(stringToken.hasMoreTokens()) {
                      output = stringToken.nextToken();
                      if (output.equals(empty)) {
                        tokenNumber++;
                      } else {
                        mySpreadsheet.changeCellFormulaAndRecalculate(
                            cellArray[lineNumber][tokenNumber].getToken(), output);
                        tokenNumber++;
                      }                             
                    }
                    lineNumber++;
            } while ((line = input.readLine()) != null);               
            input.close();
    }
    

    /**
     * Save to a file.
     * @param canonicalPath The path.
     */
    public void save(String path) throws Exception {
            BufferedWriter input = new BufferedWriter(new FileWriter(path));
            String output = new String();
            for(CellsGUI[] cells : cellArray) {
                    for(CellsGUI cell : cells) {
                      if (mySpreadsheet.cellFormulaToString(cell.getToken()).isEmpty()) {
                        output = " ";
                      }
                      else {
                        output = mySpreadsheet.cellFormulaToString(cell.getToken());
                      }
                            input.write(output);
                            input.append(',');
                      
                    }
                    input.newLine();
                    input.flush();
            }
            input.close();
    }
    
    /**
     * Class for the Mouse listener. Allows user to double click to edit
     * the cells formula.
     * 
     * @author Alex Stringham
     * 
     */
    private class MouseCellListener extends MouseAdapter {
       
            public void mouseClicked(MouseEvent the_event) {
                    if (the_event.getClickCount() == 2) {
                            final CellsGUI cellGui = (CellsGUI) the_event.getComponent();
                            cellGui.setText(mySpreadsheet.cellFormulaToString(cellGui
                                            .getToken()));
                            cellGui.setCaretPosition(cellGui.getDocument().getLength());
                    }
            }
    }
	
        /**
         * The Focus listener for cells that shows it's numerical value
         * when the cell loses focus.
         * 
         * @author Alex Stringham
         * 
         */
        private class FocusCellListener extends FocusAdapter {
            
        	
                public void focusLost(FocusEvent the_event) {
                        final CellsGUI cellGui = (CellsGUI) the_event.getComponent();
                        // cellGui.setText(mySpreadsheet.cellValueToString(cellGui.getToken()));
                        setCellText(cellGui);
                }

        }

        /**
         * The Keyboard listener for the cells which requires the user to 
         * press enter to commit a formula/change a formula already there.
         * 
         * @author Alex Stringham
         * 
         */
        private class KeyCellListener extends KeyAdapter {
             
        	
                public void keyPressed(KeyEvent the_event) {
                        if (the_event.getKeyCode() == KeyEvent.VK_ENTER) {
                                final CellsGUI cellGui = (CellsGUI) the_event.getComponent();
                                try {
                                        mySpreadsheet.changeCellFormulaAndRecalculate(cellGui
                                                        .getToken(), cellGui.getText());
                                }
                                /*
                                catch (CycleWarning e) {
                                        JOptionPane.showMessageDialog(null,
                                                        "A cycle has been found. Please re-enter formula",
                                                        "Cycle error", JOptionPane.ERROR_MESSAGE);
                                        mySpreadsheet.revert(cell_gui.getCellToken());
                                } catch (ArrayIndexOutOfBoundsException e) {
                                        JOptionPane
                                                        .showMessageDialog(
                                                                        null,
                                                                        "Cells index is out of bounds. Please enter a valid formula",
                                                                        "Array index out of bounds error.",
                                                                        JOptionPane.ERROR_MESSAGE);
                                        mySpreadsheet.revert(cellGui.getToken());
                                } catch (ParenthesesWarning e) {
                                        JOptionPane.showMessageDialog(null,
                                                        "Parentheses are not paired. \n"
                                                                        + "Please enter a formula with matching parentheses.",
                                                        "Parentheses Error", JOptionPane.ERROR_MESSAGE);
                                        mySpreadsheet.revert(cellGui.getToken());
                                } catch (ArithmeticException e) {
                                        JOptionPane.showMessageDialog(null, e.getMessage(),
                                                        "Arithmetic Error", JOptionPane.ERROR_MESSAGE);
                                        mySpreadsheet.revert(cellGui.getToken());
                                }
                                */
                                update();
                        }
                }
        }
        
        @SuppressWarnings("serial")
        private class  ParenthesesWarning extends Exception
        {
        }
        
        @SuppressWarnings("serial")
        private class CycleWarning extends Exception
        {
        }

      

       
}