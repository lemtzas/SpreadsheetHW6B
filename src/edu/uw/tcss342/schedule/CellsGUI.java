package edu.uw.tcss342.schedule;

import javax.swing.JTextField;


//This is assuming we're going off his CellToken Class 
//that he gives in the description

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
	private CellToken cell_token;
	
	/**
	 * CellsGui Constructor with cell token
	 * @param cell_token Given Cell token.
	 */
	public CellsGUI(final CellToken the_cell_token)
	{
		cell_token = the_cell_token;
	}
	
	/**
	 * Default Constructor
	 */
	public CellsGUI()
	{
		cell_token = new CellToken(0, 0);
	}
	
	/**
	 * Cell token setter.
	 * @param the_cell_token
	 */
	public void setToken(final CellToken the_cell_token)
	{
		cell_token = the_cell_token;
	}

	/**
	 * Cell token getter.
	 * @return Cell token associated with this CellsGUI.
	 */
	public CellToken getToken()
	{
		return cell_token;
	}
}
