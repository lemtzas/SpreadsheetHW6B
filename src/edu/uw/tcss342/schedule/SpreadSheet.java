package edu.uw.tcss342.schedule;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Queue;

/**
 * HW6B Spreadsheet.
 *
 * Manages overall data and cell updates.
 * Is a client of GUI. Utilizes Cell for cell-level tasks.
 *
 * User: Jacob Hall
 * Date: 11/18/12
 * Time: 9:34
 */
public class SpreadSheet extends Observable{
    private List<Cell> activeCells;
    
    //A vertexMap field that stores maps the cell's name to the Cell Object
    private Map<String, Cell> cellMap = new HashMap<String, Cell>();
    
    private int my_rows;
    
    private int my_columns;
    
    public SpreadSheet(final int the_rows, final int the_columns)
    {
    	my_rows = the_rows;
    	my_columns = the_columns;
    }
    
    public int getNumColumns()
    {
    	return my_columns;
    }
    
    public int getNumRows()
    {
    	return my_rows;
    }
    
    /*
     * Called by the GUI every time a single Cell has been created or altered.  The spreadsheet will
     * add it to the Map<String, Cell> the correlates the Identifier of the cell with it's calculated 
     * value.  The spreadsheet will notify observers of any changes.
     * 
     * Note:  Cells that haven't been modified from original condition in the GUI are assumed to 
     * 		  have the value 0.
     * 
     * 
     * @param the_name The String name i.e. (A0, B1, FF21,...) that represents the cell's row and column 
     * 						location.
     * @param the_formula  The String formula i.e. (B1 + 7, A0*2, 10,...) that represents the cell's 
     * 						formula.  Parsing of values and dependency calculations to be done at the cell
     * 						level.
     */
    public void updateCell(final String the_name, final String the_formula)
    {   	
    	Cell tempCell = cellMap.get(the_name);
    	//if the Cell isn't in the map yet i.e. the name associated with the Cell hasn't yet been modified
    	//from original state of 0.
    	if (tempCell == null)
    	{
    		tempCell = new Cell(the_name, the_formula);
    		cellMap.put(the_name, tempCell);
    	}
    	//if the Cell is already in the map, the Cell is updated by being replaced with a new Cell that is
    	//associated with the Cell's name and has the updated value.
    	else
    	{
    		tempCell = new Cell(the_name, the_formula);
    		cellMap.put(the_name, tempCell);
    	}
    	topologicalSort();
    }
    
    /*
     * Notify's all observers when a change has been made to the SpreadSheet
     */
    public void updateSpreadSheet()
    {
        setChanged();
        notifyObservers();
    }
    
    /*
     * Returns a copy of the List of Cells contained within the SpreadSheet.  To be called once the 
     * SpreadSheet notifies the GUI of any updates.  Makes a shallow copy of the original list of cells
     * 
     * @return returnCells the ArrayList<Cell> of all Cells with their id(name), formula, and value.
     */
    public List<Cell> getActiveCells()
    {
    	List<Cell> returnCells = new ArrayList<Cell>(activeCells);
		return returnCells;	
    }
    
    /*
     * Performs the topological sort on the List<Cell> in the spreadsheet
     * (Taken from Weiss p.559)
     */
    private void topologicalSort()
    {
    	//finds a Cell with inDegree of 0 and assigns it to the startName variable
    	Collection<Cell> cellIndegreeSet = cellMap.values();
    	String startName = "not found";  //the name of the Cell that has indegree 0 assigned above.
    	for (Cell c : cellIndegreeSet)
    		if (c.inDegree == 0)
    		{
    			startName = c.getID();
    		}
    	Cell start = cellMap.get(startName);
    	
    	//if there was no cell with an inDegree of 0, will throw this exception.
    	if (start == null)
    	{
    		throw new NoSuchElementException("Start Cell not found");
    	}
    	
    	//Compute the inDegrees and add to the Queue in the order they should be calculated.
    	clearAll();
    	Queue<Cell> cellQueue = new LinkedList<Cell>();
    	Queue<Cell> cellEvalQueue = new LinkedList<Cell>();
    	Collection<Cell> cellSet = cellMap.values();
    	for (Cell c : cellSet)
    		if (c.inDegree == 0)
    		{
    			cellQueue.add(c);
    		}
    	
    	//Add the Cells to the Queue in the order they are to be executed.
    	int iterations;
    	for (iterations = 0; !cellQueue.isEmpty(); iterations++)
    	{
    		Cell currentCell = cellQueue.remove();
    		cellEvalQueue.add(currentCell);
    		//for each adjacent Cell in the current Cell's list of adjacent Cells, evaluate the inDegree
    		for ( Cell adjCell : currentCell.adjacent)
    		{
    			Cell dest = adjCell; //evaluate each adjacent Cell
    			if (--dest.inDegree == 0)  //if the current adjacent Cell being evaluated now has 
    									   //inDegree = 0, add it to the cellQueue so it can be evaluated
    									   //next
	    			cellQueue.add(dest);
    		}
    		if (iterations != cellMap.size())
    			throw new SpreadSheetException("Spreadsheet has a cycle");
    	}
    	
    	/*TODO  The cellEvalQueue now has all Cells queued up in the order they should be evaluated.
    	 * 		Need to write code to evaluate the Cells and add them to the list of activeCells.
    	 */
    }
    
    private void clearAll()
    {
    	for (Cell c : cellMap.values())
    		c.inDegree = 0;
    }

    

    public String convertPoint(int x, int y) {
        //get dependencies
        //make dep graph
        //topo sort
            //evaluate points, pass calculated values
        return "";
    }
}

/*
 * Used to signal violations of preconditions in the SpreadSheet algorithms
 * 
 * @param name The name of the exception
 */
@SuppressWarnings("serial")
class SpreadSheetException extends RuntimeException
{
	public SpreadSheetException(String name)
	{
		super(name);
	}
}


/*
 * Current Issues:
 * 		Need adjacent field in Cell
 * 			-public List<Cell> adjacent;  //adjacent vertices (Cells)  The list of dependencies.
 * 			-Can this be done?  Can the Cell class have within it a List<Cell>?  If not, need to modify the
 * 				SpreadSheet code.  Establish what the list of adjacent is going to be.  Set<String>?
 * 		Need to sort out the constuctor in Cell
 * 			-why do we need a Point object for the name?  I was WRONG!  It is much simplier to simply
 * 			 refer to it as a String, name
 * 			-my suggestion for a constructor
 * 				 public Cell(final String name, final String formula)
 * 		CellToken Stuff.  Do we need it, who will create it?
 * 			-seperate classes exactly like layed out in the directions?
 * 		Should we add a clone method to Cell? In my getActiveCells() method, I'm returning an ArrayList<Cell>
 * 			that is a copy of the LinkedList<Cell> that is just a container in my SpreadSheet class to 
 * 			hold all the Cells after the calculations have been performed.  I think the text book answer is 
 * 			to clone each Cell individually, add them to the new list and then return that.  That means
 * 			I need to write up a proper clone method in Cell to copy each field individually example here:
 * 			http://stackoverflow.com/questions/7042182/how-to-make-a-deep-copy-of-java-arraylist
 * 			
 * 			I can do this, just need you guy's go-ahead for the work.
 */
