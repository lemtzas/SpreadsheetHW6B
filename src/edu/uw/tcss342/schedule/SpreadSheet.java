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
import java.util.Set;

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
    
    //Maps the Cell's name to the Cell Object
    private Map<String, Cell> cellMap = new HashMap<String, Cell>();
    
    //Maps a Cell's name to the resulting value it evaluates to 
    private Map<String, Double> cellValues = new HashMap<String, Double>();
    
    //The adjacency list used in topological sort
    private HashMap<String, List<String>> adjList;;
    
	private Queue<Cell> cellEvalQueue;

    
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
    	//removes the Cell from the map according to the key (name) of the Cell if the_formula = "0" or "".
    	if (the_formula.equals("0") || the_formula.equals(""))
    	{
    		cellMap.remove(the_name);
    	}
    	
    	//if the Cell isn't in the map yet i.e. the name associated with the Cell hasn't yet been modified
    	//from original state of 0.  if the Cell is already in the map, the Cell is updated by being 
    	//replaced with a new Cell that is associated with the Cell's name and has the updated value.
    	else
    	{
        	Cell tempCell = new Cell(the_name, the_formula);
        	cellMap.put(the_name, tempCell);
    	}
   	
    	buildAdjList();
    	topologicalSort();
    	evaluateCells();
    	updateSpreadSheet();
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
    
    public void reset()
    {
    	activeCells.clear();
    	cellMap.clear();
    	cellValues.clear();
    	adjList.clear();
    	cellEvalQueue.clear();
    	updateSpreadSheet();
    }
    
    /*
     * Builds an adjacency list based on the dependencies associated with each cell and adds them to
     * the Map<String, list<Cell>> adjList.
     */
    private void buildAdjList()
    { 	
    	//First, add the all the keys to the adjList, with null values for the List of adjacent Cell names
    	List<String> cellNames = new ArrayList<String>(cellMap.keySet());
    	adjList = new HashMap<String, List<String>>();
    	for (String s : cellNames)
    	{
    		adjList.put(s, null);
    	}
    	
    	/*Second, iterate through every Cell in the cellMap. For each Cell in the cellSet, take note of the
    	 *Cell's name, then go to every key value in the adjList that matches the dependent Cell's name
    	 *and add the destination Cell's name to the list of dependencies. 
    	 */
    	Collection<Cell> cellSet = cellMap.values();
    	for (Cell c : cellSet)
    	{
    		for (String s : c.dependencies)
    		{
    			adjList.get(s).add(c.id);
    			//calculates the inDegree of the dependent Cell
    			cellMap.get(c.id).inDegree++;
    		}   		
    	}
    }
    
    /*
     * Performs the topological sort on the List<Cell> in the spreadsheet
     * (Taken from Weiss p.559)
     */
    private void topologicalSort()
    {
    	//finds a Cell with inDegree of 0 and assigns it to the startName variable which assigns
    	//the first Cell to be executed (startCell).
    	Collection<Cell> cellIndegreeSet = cellMap.values();
    	String startName = "not found";  //the name of the Cell that has indegree 0 assigned above.
    	for (Cell c : cellIndegreeSet)
    		if (c.inDegree == 0)
    		{
    			startName = c.getID();
    		}
    	Cell startCell = cellMap.get(startName);
    	
    	//if there was no cell with an inDegree of 0, throw this exception.
    	if (startCell == null)
    	{
    		throw new SpreadSheetException("Start Cell not found");
    	}
    	
    	//Add Cells to the Queue in the order they should be calculated.
    	Queue<Cell> cellQueue = new LinkedList<Cell>();
    	cellEvalQueue = new LinkedList<Cell>();
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
    		for ( String adjCell : adjList.get(currentCell.id))
    		{
    			cellMap.get(adjCell).inDegree--;
				//if the current adjacent Cell being evaluated now has inDegree = 0, add it to the 
    			//cellQueue so it can be evaluated next.
    			if (cellMap.get(adjCell).inDegree == 0)  
    			{
	    			cellQueue.add(cellMap.get(adjCell));	
    			}
    		}
    		if (iterations != cellMap.size())
    			throw new SpreadSheetException("Spreadsheet has a cycle");
    	}
    }
    
    /*
     * References the cellMap of existing Cells to be evaluated. Evaluates the Cells in the order they
     * are organized in the cellEvalQueue and stores the resultant Cells in the activeCells List. 
     */
    private void evaluateCells()
    {
    	//Assigns the first Cell's id and value into the cellValues Map.
    	Cell firstCell = cellEvalQueue.remove();
    	cellValues.put(firstCell.id, firstCell.last_value);
    	
    	//Iterates down the cellEvalQueue in order 
    	for (int i = cellEvalQueue.size(); i > 0; i--)
    	{
    		Cell currentCell = cellEvalQueue.remove();
    		currentCell.evaluate(cellValues);
    		cellValues.put(currentCell.id, currentCell.last_value);
    	}
    	activeCells.clear();
    	Collection<Cell> cellSet = cellMap.values();
    	for (Cell c : cellSet)
    	{
    		activeCells.add(c);
    	}
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
 * 		Need to sort out the constructor in Cell
 * 			-why do we need a Point object for the name?  I was WRONG!  It is much simpler to simply
 * 			 refer to it as a String, name
 * 			-my suggestion for a constructor
 * 				 public Cell(final String name, final String formula)
*/
