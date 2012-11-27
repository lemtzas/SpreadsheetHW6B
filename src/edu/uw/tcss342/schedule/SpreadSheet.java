package edu.uw.tcss342.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	//the List of Cells that are currently active in the spreadsheet.  All Cells not 
	//included in this List have the value 0.
    private List<Cell> activeCells;
    
    //Maps the Cell's name to the Cell Object
    private Map<String, Cell> cellMap = new HashMap<String, Cell>();
    
    //Maps a Cell's name to the resulting value it evaluates to 
    private Map<String, Double> cellValues = new HashMap<String, Double>();
    
    //The adjacency list used in topological sort
    private HashMap<String, List<String>> adjList;
    
    //The Queue that holds the Cells in the order they should be evaluated in.
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
    		try
    		{
            	Cell tempCell = new Cell(the_name, the_formula);
            	cellMap.put(the_name, tempCell);
    		}
    		catch( IllegalStateException e)
    		{
    			throw new IllegalStateException(e); 
    		}
    	}
    	buildAdjList();
		try
		{
	    	topologicalSort();
	    	evaluateCells();
		}
		catch( IllegalStateException e)
		{
			throw new IllegalStateException(e); 
		}
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
    
    /*
     * Returns a Map of all the Cells input into the SpreadSheet with their name as the key.
     * Only used for testing purposes.
     * 
     * @return cellMap The Map that associates a Cell's name with the Cell itself.
     */
    public Map<String, Cell> getCellMap()
    {
    	return cellMap;
    }

    /*
     * Allows the GUI to reset the SpreadSheet to default value of 0 for all Cells.
     */
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
    		List<String> temp = new ArrayList<String>();
    		adjList.put(s, temp);
    	}
    	
    	Collection<String> cellSetNames = adjList.keySet(); 
    	/*Second, iterate through every Cell in the cellMap. For each Cell in the cellSet, take note of the
    	 *Cell's name, then go to every key value in the adjList that matches the dependent Cell's name
    	 *and add the destination Cell's name to the list of dependencies. 
    	 */	
    	Collection<Cell> cellSet = cellMap.values();
		for (Cell c : cellSet)
    	{
			c.inDegree = 0;
    		for (String s : c.dependencies)
    		{    			
				List<String> temp = adjList.get(s);
				if (temp == null)
				{
					temp = new ArrayList<String>();
				}
				temp.add(c.id);
				adjList.put(s, temp);	
    			//calculates the inDegree of the dependent Cell
    			cellMap.get(c.id).inDegree++;
    		}   		
    	}
		
		//
    	for (String s : cellSetNames)
    	{
    		if (cellMap.get(s) == null)
    		{
            	Cell tempCell = new Cell(s, "0");
            	cellMap.put(s, tempCell);
    		}
    	}
    }
    
    /*
     * Performs the topological sort on the List<Cell> in the spreadsheet
     * (Adapted from code printed in  Weiss p.559)
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
    		throw new IllegalStateException("Start Cell not found");
    	}
    	
    	//Add Cells to the Queue in the order they should be calculated.
    	Queue<Cell> cellQueue = new LinkedList<Cell>();
    	cellEvalQueue = new LinkedList<Cell>();
    	Collection<Cell> cellSet = cellMap.values();
    	for (Cell c : cellSet)
    	{
    		if (c.inDegree == 0)
    		{
    			cellQueue.add(c);
    		}
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
    	}
    	//Throws Exception if there is a cycle detected in the SpreadSheet
   		if (iterations != cellMap.size())
   			throw new IllegalStateException("Spreadsheet has a cycle");
    }
    
    /*
     * References the cellMap of existing Cells to be evaluated. Evaluates the Cells in the order they
     * are organized in the cellEvalQueue and stores the resultant Cells in the activeCells List. 
     */
    private void evaluateCells()
    {
    	//Assigns the first Cell's id and value into the cellValues Map.
    	Cell firstCell = cellEvalQueue.remove();
    	firstCell.last_value = firstCell.evaluate(null);
    	cellValues.put(firstCell.id, firstCell.last_value);
    	
    	//Iterates down the cellEvalQueue in order 
    	for (int i = cellEvalQueue.size(); i > 0; i--)
    	{
    		Cell currentCell = cellEvalQueue.remove();
    		currentCell.evaluate(cellValues);
    		cellValues.put(currentCell.id, currentCell.last_value);
    	}
    	activeCells = new ArrayList<Cell>();
    	Collection<Cell> cellSet = cellMap.values();
    	for (Cell c : cellSet)
    	{
    		activeCells.add(c);
    	}
    }
    
    /*
     * Used only for test purposes.  Prints to the console the list of Active Cells after 
     * the GUI has called updateCell on a single Cell.  The SpreadSheet recalculates all 
     * values for all active cells and stores it in the activeCells List for the GUI to retrieve.
     */
    public void printActiveCells()
    {
		System.out.println("Contents of the activeCell List that contains the final Cells and" +
							" their calculated values: ");
    	for (Cell c : activeCells)
    	{
    		System.out.println("     Name: " + c.id + "     Value: " + c.last_value + 
    							"     Formula: " + c.formula);
    	}
    }
    
    /*
     * Used only for test purposes.  Prints to the console a representation of the Cells present
     * in the SpreadSheet by name, value, and formula.
     */
    public void printCellMap()
    {
    	Collection<String> cellSetNames = cellMap.keySet(); 
		System.out.println("Contents of the cellMap which maps a Cell's name to the Cell Object: ");
    	for (String s : cellSetNames)
    	{
    		System.out.println("     Name: " + s + "     Value: " + cellMap.get(s).last_value + 
    							"     Formula: " + cellMap.get(s).formula);
    	}
    }
    
    /*
     * Used only for test purposes.  Prints to the console the values associated with each cell
     * after calculation.
     */
    public void printCellValues()
    {
    	Collection<String> cellSetNames = cellMap.keySet(); 
		System.out.println("Contents of the cellValue which maps a Cell's name to the value it's" +
							" formula evaluates to in the SpreadSheet: ");
    	for (String s : cellSetNames)
    	{
    		System.out.println("     Name: " + s + "     Value: " + cellValues.get(s));
    	}
    }
    
    /*
     * Used only for test purposes.  Prints to the console a representation of the adjacency list
     * which is the list of Cells in the SpreadSheet and the adjacent Cells who's values they are dependent
     * on for purposes of evaluation.  Also prints the inDegree associated with each Cell.
     */
    public void printAdjList()
    {
    	Collection<String> cellSetNames = adjList.keySet(); 
		System.out.println("Contents of the adjList which maps a Cell's name to the list of Cells" +
							" it must be performed before: ");
    	for (String s : cellSetNames)
    	{
    		System.out.println("     Name: " + s + "     inDegree: "  + cellMap.get(s).inDegree +
    							"     Adjacent Cells: " + adjList.get(s));
    	}
    }
    
    /*
     * Used only for test purposes.  Prints to the console the list of Cells (by name) that 
     * are in topological order to be evaluated.
     */
    public void printcellEvalQueue()
    {
    	System.out.print("The Cells in cellEvalQueue: [");
    	for (Cell d : cellEvalQueue)
    	{
        	System.out.print(" " + d.id);
    	}
    	System.out.println(" ]");
    }
}
