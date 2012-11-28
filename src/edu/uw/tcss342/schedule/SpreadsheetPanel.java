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

import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


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
     * Default number of columns in the spreadsheet.
     */
    public static final int COLUMNS = 24;

    /**
     * Default number of rows in the spreadsheet.
     */
    public static final int ROWS = 20;

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
    private SpreadSheet mySpreadsheet;
    
    /**
     * A cell in the spreadsheet.
     */
    private Cell myCell = null;
    
    private String cellName;
    
    private String cellFormula;

    /**
     * Construct a spreadsheet board from the input.
     * 
     * @param the_spreadsheet
     *            The spreadsheet.
     */
    public SpreadsheetPanel(final SpreadSheet the_spreadsheet) {
            super(new GridLayout(ROWS +1 , COLUMNS + 1));
            mySpreadsheet = the_spreadsheet;
            
            myRows = ROWS;
            myColumns = COLUMNS;
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
                        Cell.CellToken cellToken = new Cell.CellToken(j,i);

                            if (i == -1) {
                                    if (j == -1)
                                            add(new JLabel("", null, SwingConstants.CENTER));
                                    else
                                            add(new JLabel(cellToken.columnString(), null,SwingConstants.CENTER));
                            } else if (j == -1)
                                    add(new JLabel(Integer.toString(i), null,SwingConstants.CENTER));
                            else {
                                    cellArray[i][j] = new CellsGUI(cellToken);
                                   
                                    setCellText(cellArray[i][j]);
                                    cellArray[i][j].addMouseListener(new MouseCellListener());
                                    cellArray[i][j].addKeyListener(new KeyCellListener());
                                    cellArray[i][j].addFocusListener(new FocusCellListener());

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
            
    	mySpreadsheet.reset();
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
    	if(the_cell == null) return;
        if(myCell == null) return;

    	double cellValue = myCell.last_value;
    	String textValue = String.valueOf(cellValue);
    	
    	
            if (myCell.formula == null)
                    the_cell.setText("");
            else
                    the_cell.setText(textValue);
                    		
                    	
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
     * @param path
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
                        mySpreadsheet.updateCell(getName(), output);
                        tokenNumber++;
                      }                             
                    }
                    lineNumber++;
            } while ((line = input.readLine()) != null);               
            input.close();
    }
    

    /**
     * Save to a file.
     * @param path : path to the file.
     */
    public void save(String path) throws Exception {
            BufferedWriter input = new BufferedWriter(new FileWriter(path));
            String output = new String();
            for(CellsGUI[] cells : cellArray) {
                    for(CellsGUI cell : cells) {
                      if (myCell.formula == null)
                      {
                    		  //mySpreadsheet.cellFormulaToString(cell.getToken()).isEmpty()) {
                        output = " ";
                      }
                      else {
                        output = myCell.getFormula(); 
                        		//mySpreadsheet.cellFormulaToString(cell.getToken());
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
                            Cell cell = mySpreadsheet.getCellMap().get(cellGui.getToken().toString());
                            if( cell != null )
                                cellGui.setText(cell.getFormula());
                            else
                                cellGui.setText("");
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
                                //try {
                                       mySpreadsheet.updateCell(cellGui.getToken().toString(), cellGui.getText());
                                        Map<String,Cell> cells = mySpreadsheet.getCellMap();
                                        Cell cell = cells.get(cellGui.getToken().toString());
                                        if( cell != null )
                                                cellGui.setText(Double.toString(cell.lastValue()));
                                        else
                                                cellGui.setText("");
                               // }
                                
                                //TODO: Stub for now.
                              //  catch (EOFException e) {
                                	
                              //  }
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
                                //update();
                        }
                }
        }
}