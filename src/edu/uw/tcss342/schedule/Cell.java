package edu.uw.tcss342.schedule;


import java.awt.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Cell representation in HW6B Spreadsheet. Immutable.
 *
 * Manages individual Cell data and operations.
 * Client of Core.
 * Calculates cell dependencies and evaluates expressions.
 *
 * User: David
 * Date: 11/14/12
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cell {
    /** Topo Sort workspace variable **/
    public int inDegree;
    String formula;
    String id;
    double last_value;
    int column;
    int row;
    Set<String> dependencies;

    public Cell(final Point column_row, final String formula) {
        this.column = column_row.x;
        this.row = column_row.y;
        this.id = "colrow"; //TODO: Interpret this based on columnn_row
        this.formula = formula.trim();
        dependencies = new HashSet<String>();
        parse();
    }

    public Cell(final String id, final String formula) {
        this.id = id;
        this.formula = formula;
        dependencies = new HashSet<String>();
        parse();
    }

    public Double evaluate(Map<String,Double> dep_values) {
        //return result, given values for dependencies
        return null;
    }

    public Double lastValue() {
        return last_value;
    }

    /**
     * The cells this cell's value depends on, in set format.
     * @return Set of Dependencies; non-null; possibly empty
     */
    public Set<String> getDependencies() {
        //return calculated dependencies
        return new HashSet<String>();
    }

    public String toString() {
        return getFormula();
    }

    public String getFormula() {
        return formula;
    }

    public String getID() {
        return id;
    }

    public Point getColumnRow() {
        //TODO: Make this return the correct point
        return new Point();
    }


    private void parse() {
        dependencies.clear(); //empty result space

        Queue<Token> output_queue = new LinkedList<Token>();
        Stack<Token> workspace_stack = new Stack<Token>();
        String remaining = formula; //copy trimmed formula into workspace
        remaining = remaining.replaceAll(" ",""); //kill all whitespace
        int index = 0; //where are we in the process?
        char ch = ' ';
        while(!remaining.isEmpty()) { //while we aren't done (while there are tokens to be read)
            remaining = parseToken(remaining,output_queue,workspace_stack);
        }
    }

    /**
     *
     * @param remainder The index to look for the start of the token at
     * @param output_queue the queue of tokens
     * @return the new index
     */
    private String parseToken(String remainder,Queue<Token> output_queue,Stack<Token> workspace_stack) {
        Pattern cell = Pattern.compile("^[A-Z]+[0-9]+");
        Pattern token;
        Pattern literal = Pattern.compile("^[-]?[0-9]+");
        //determine type of next token and add to appropriate stack
            //literal
            //cell
            //operator (unary,binary,association)
            //function
        return null;
    }




    public static Point convertString(String identifier) {
        return new Point();
    }

    public static String convertPoint(Point p){
        return convertPoint(p.x,p.y);
    }

    public static String convertPoint(int x, int y) {
        //get dependencies
        //make dep graph
        //topo sort
        //evaluate points, pass calculated values
        return "";
    }







    //token stuff
    /** Map<Operator,Operation> **/
    private Map<String,OperatorToken> operators = new HashMap<String,OperatorToken>();


    private static interface Token {}
    private static class LiteralToken implements Token {
        public LiteralToken(final double value) {
            this.value = value;
        }
        double value;
    }
    private static interface OperatorToken extends Token {}
    private static interface BinaryOperatorRunnable extends OperatorToken {
        /** @return True if left associative, false if right associative. */
        boolean leftAssociative();
        /** Performs the operation for this operator **/
        double run(double left, double right);
    }
    private static interface UnaryOperatorRunnable extends OperatorToken {
        /** Performs the operation for this operator **/
        double run(double value);
    }

    public static class CellToken implements Token {
        int column;
        int row;
        String identifier;
        public CellToken(final String identifier) {
            this.identifier = identifier.toUpperCase();
            String temp_ident_remain = this.identifier;
            column = 0; //init column
            //calculate column
            while(temp_ident_remain.matches("^[A-Z].*")) { //starts with character
                //gets value of char, 10-35
                int value = Character.getNumericValue(temp_ident_remain.charAt(0));

                //get value from letter
                value = value - 9;

                //add to value
                column *= 26;
                column += value;
                temp_ident_remain = temp_ident_remain.substring(1);
            }
            row = Integer.parseInt(temp_ident_remain); //the match
        }

        public String toString() {
            return identifier + " (" + column + "," + row + ")";
        }
    }
}
