package edu.uw.tcss342.schedule;

import javax.swing.JTextField;

//import CellToken;

/**
 * Text field That holds a a CellToken reference for the spreadsheet.
 * 
 * @author Alex Stringham
 * 
 */
@SuppressWarnings("serial")
public class CellsGUI extends JTextField {
	
	/**
	 * Cell token object.
	 */
	private Cell.CellToken cell_token;
	
	/**
	 * CellsGui Constructor with cell token
	 * @param cell_token Given Cell token.
	 */
	public CellsGUI(final Cell.CellToken the_cell_token)
	{
		cell_token = the_cell_token;
	}
	
	/**
	 * Default Constructor
	 */
	public CellsGUI()
	{
		cell_token = new Cell.CellToken(null);
	}
	
	/**
	 * Cell token setter.
	 * @param the_cell_token
	 */
	public void setToken(final Cell.CellToken the_cell_token)
	{
		cell_token = the_cell_token;
	}

	/**
	 * Cell token getter.
	 * @return Cell token associated with this CellsGUI.
	 */
	public Cell.CellToken getToken()
	{
		return cell_token;
	}
}
