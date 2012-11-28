package edu.uw.tcss342.schedule;


//import com.sun.javaws.exceptions.InvalidArgumentException;

import sun.plugin.dom.exception.InvalidStateException;

import java.awt.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Cell representation in HW6B Spreadsheet. Immutable.
 *
 * Manages individual Cell data and operations.
 * Client of Core.
 * Calculates cell dependencies and evaluates expressions.
 *
 * @author David Sharer
 */
public class Cell {
    /** Topo Sort workspace variable. **/
    public int inDegree;
    /** The cell's formula. **/
    String formula;
    /** The cell's identifying string. **/
    String id;
    /** The last calculated value for this cell. **/
    double last_value;
    /** The cells that this cell depends on. **/
    Set<String> dependencies;
    /** The post-fix processing queue. For internal use only. **/
    Queue<Token> token_queue;

    /**
     * Create a cell with the given ID and formula. Determines dependencies immediately.
     *
     * @param id The cell's identifying string
     * @param formula The cell's formula. Any errors in this will result in an IllegalStateException.
     */
    public Cell(final String id, final String formula) {
        this.id = id;
        this.last_value = 0;
        this.formula = formula;
        dependencies = new HashSet<String>();
        parse();
    }

    /**
     * Evaluates a cell given a map of dependency cell ids to their values.
     * The value is stored for quick lookup later.
     *
     * @param dep_values Map<ID String, Double Value>.
     * @return the value of this cell.
     */
    public Double evaluate(Map<String,Double> dep_values) {
        //return result, given values for dependencies
        Queue<Token> remaining_postfix = new LinkedList<Token>(token_queue);
        Stack<Double> values = new Stack<Double>();
        while(!remaining_postfix.isEmpty()) {
            Token t = remaining_postfix.poll();
            if(t instanceof LiteralToken) {
                values.push(((LiteralToken) t).value);
            } else if(t instanceof CellToken) {
                values.push(((CellToken) t).value(dep_values));
            } else if(t instanceof OperatorToken) {
                if(t instanceof BinaryOperatorToken) {
                    BinaryOperatorToken bot = (BinaryOperatorToken) t;
                    if(values.empty()) throw new IllegalStateException("Invalid Formula");
                    Double right = values.pop();
                    if(values.empty()) throw new IllegalStateException("Invalid Formula");
                    Double left = values.pop();
                    values.push(bot.operation.run(left,right));
                }
            }                                                                                                           //TODO: Function Tokens
        }
        if(values.empty()) throw new IllegalStateException("Invalid Formula");
        last_value = values.pop();
        return last_value;
    }

    /**
     * @return the last value calculated with evaluate()
     */
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

    /**
     * @return The raw formula stored in this cell.
     */
    public String toString() {
        return getFormula();
    }

    /**
     * @return The raw formula in this cell.
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @return The identifier of this cell.
     */
    public String getID() {
        return id;
    }

    /**
     * Internal function that interprets the formula for this cell.
     * Determines dependencies and prepares a postfix expression for later parsing.
     */
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
        //finish up
        while(!workspace_stack.empty())
            output_queue.add(workspace_stack.pop());
        token_queue = new LinkedList<Token>(output_queue);

        //postfixify
        StringBuilder sb = new StringBuilder();
        while(!output_queue.isEmpty())
            sb.append(" ").append(output_queue.poll().toString());

//        System.out.println(formula + " = " + sb);
    }

    /**
     * Helper function for interpreting formula tokens.
     * @param remainder The index to look for the start of the token at
     * @param output_queue the output queue of tokens
     * @param workspace_stack a workspace stack for operator tokens
     * @return the remaining characters in the formula string
     */
    private String parseToken(String remainder,Queue<Token> output_queue,Stack<Token> workspace_stack) {
        Pattern cell = Pattern.compile("^[A-Z]+[0-9]+");
        Pattern literal = Pattern.compile("^[0-9]+(?:\\.[0-9]+)?");

        Matcher cell_matcher = cell.matcher(remainder);
        Matcher literal_matcher = literal.matcher(remainder);
        String match = "";
        if( cell_matcher.find() ) { //found cell id
            match = remainder.substring(cell_matcher.start(),cell_matcher.end());                                       //substring cell name
            dependencies.add(match);                                                                                    //add dependency
            output_queue.add(new CellToken(match));                                                                     //it acts as a value, so add it to the output queue

        } else if( literal_matcher.find() ) { //found literal
            match = remainder.substring(literal_matcher.start(),literal_matcher.end());                                 //substring the value string
            output_queue.add(new LiteralToken(Double.parseDouble(match)));                                              //add values to output queue



                                                                                                                        //TODO: Function Tokens
                                                                                                                        //TODO: Comma support
        } else if(remainder.startsWith("(")) { //left paren
            match = "(";
            workspace_stack.push(new LeftParenToken());                                                                 //simply push onto the stack

        } else if(remainder.startsWith(")")) { //right paren
            match = ")";
            while(!workspace_stack.empty() && !(workspace_stack.peek() instanceof LeftParenToken))
                output_queue.add(workspace_stack.pop());                                                                //transfer items to output until we find a left paren
            if(workspace_stack.empty()) throw new IllegalStateException("Mismatched Parentheses");                      //TODO: Make custom exception
            workspace_stack.pop();                                                                                      //remove the left paren
            //TODO: Pop function token

        } else {
            //fallback: check for operation
            for(int i = 0; i < operations.length; i++) {
                if(remainder.startsWith(operations[i].token)) { //found operator
                    match = operations[i].token;                                                                        //set match
                    while(!workspace_stack.empty() && (workspace_stack.peek() instanceof BinaryOperatorToken) &&        //while top item on stack is an operator
                            ( operations[i].is_left_associative && operations[i].precedence <= (((BinaryOperatorToken) workspace_stack.peek()).precedence )
                            || operations[i].precedence < (((BinaryOperatorToken) workspace_stack.peek()).precedence) )){ //and token is either left associative and of <= precedence
                                                                                                                        //or is of lower precedence
                        output_queue.add(workspace_stack.pop());                                                        //shift token into output

                    }
                    workspace_stack.push(operations[i]);                                                                //put new operator onto workspace
                    break;                                                                                              //found token, we can leave
                }
            }
        }
        //System.out.println("'" + remainder + "' | Matched: '" + match + "'");
        if(match.length() == 0) throw new IllegalStateException("Formula contains Invalid Token");                      //TODO: Make custom exception
        //determine type of next token and add to appropriate stack
            //literal
            //cell
            //operator (unary,binary,association)
            //function
        return remainder.substring(match.length());
    }






    //token stuff
    /**The operations**/
    BinaryOperatorToken operations[] = new BinaryOperatorToken[]{
            new BinaryOperatorToken(2,true,"+",
                    new BinaryOperatorRunnable() {
                        @Override public double run(double left, double right) {return left + right;}}),
            new BinaryOperatorToken(2,true,"-",
                    new BinaryOperatorRunnable() {
                        @Override public double run(double left, double right) {return left - right;}}),
            new BinaryOperatorToken(3,true,"*",
                    new BinaryOperatorRunnable() {
                        @Override public double run(double left, double right) {return left * right;}}),
            new BinaryOperatorToken(3,true,"/",
                    new BinaryOperatorRunnable() {
                        @Override public double run(double left, double right) {if(right == 0) throw new IllegalStateException("Divid by zero!"); return left / right;}}),
            new BinaryOperatorToken(4,false,"^",
                    new BinaryOperatorRunnable() {
                        @Override public double run(double left, double right) {return Math.pow(left, right);}})
    };

    /** Represents a token. **/
    private static interface Token {}
    /** Represents a left Parentheses. **/
    private static class LeftParenToken implements Token{}
    /** Represents a Literal Token (number). **/
    private static class LiteralToken implements Token {
        public LiteralToken(final double value) {
            this.value = value;
        }
        public String toString() {
            return Double.toString(value);
        }
        public final double value;
    }
    /** Represents an operator token. **/
    private static interface OperatorToken extends Token {}
    /** Function wrapper for operator tokens. **/
    private static abstract class BinaryOperatorRunnable {
        /** Performs the operation for this operator **/
        abstract public double run(double left, double right);
    }

    /** Represents a binary operator token. **/
    private static class BinaryOperatorToken implements OperatorToken {
        /** The precedence of the operator. Higher is more important. **/
        public final int precedence;
        /** The string representing this operator. **/
        public final String token;
        /** The function to perform for this operator. **/
        public final BinaryOperatorRunnable operation;
        /** Is this operator left associative? False means right-associative. **/
        public final boolean is_left_associative;

        /**
         * Constructs a token template with the appropriate values.
         * @param precedence The precedence of the operator. Higher is more important.
         * @param is_left_assocaitive Is this operator left associative? False means right-associative.
         * @param token The string representing this operator.
         * @param operation The function to perform for this operator.
         */
        public BinaryOperatorToken(final int precedence, final boolean is_left_assocaitive,
                                    final String token, BinaryOperatorRunnable operation) {
            this.precedence = precedence;
            this.is_left_associative = is_left_assocaitive;
            this.token = token;
            this.operation = operation;
        }
        public String toString() {
            return token;
        }
    }

    /** Represents a cell. **/
    public static class CellToken implements Token {
        int column;
        int row;
        String identifier;

        /**
         * Creates a cell token based on a given identifier.
         * @param identifier The identifier of the cell
         */
        public CellToken(final String identifier) {
            this.identifier = identifier.toUpperCase();
            String temp_ident_remain = this.identifier.toUpperCase();
            column = 0; //init column
            //calculate column
            while(temp_ident_remain.matches("^[A-Z].*")) { //starts with character
                //gets value of char, 10-35
                int value = Character.getNumericValue(temp_ident_remain.charAt(0)) - 10;

                //add to value
                column *= 26;
                column += value;
                temp_ident_remain = temp_ident_remain.substring(1);
            }
            row = Integer.parseInt(temp_ident_remain); //the match

            //fix up identifier
            //this.identifier = this.columnString() + Integer.toString(row);
        }

        /**
         * Creates a cell based on a column and row.
         * @param col The column of the cell.
         * @param row Tje row of the cell.
         */
        public CellToken(int col, int row) {
            this.row = row;
            this.column = col;
            identifier = this.columnString() + Integer.toString(row);
        }

        /** Gets the value of this cell from a map of cell-value pairs. **/
        public double value(Map<String,Double> values) {
            return values.get(this.identifier);
        }

        /** Outputs the identifier of this cell. **/
        public String toString() {
            return identifier;// + " (" + column + "," + row + ")";
        }

        /** Outputs the identifier and name of this cell in the format [A-Z]+[0-9]+ (<row>,<col>) **/
        public String fullString() {
            return identifier + " (" + column + "," + row + ")";
        }

        /** A helper function that determines the base-26 (A-Z) representation of the column for this cell. **/
        public String columnString() {
            StringBuffer stringBuff = new StringBuffer();
            int digit = this.column % 26;
            int remain = (this.column-digit) / 26;
            char letter = (char) (digit + 'A');
            stringBuff.append(letter);
            while (remain > 0) {
                digit = (remain) % 26;
                remain = (remain-digit) / 26;
                letter = (char) (digit + 'A');
                stringBuff.insert(0, letter);
            }
            return stringBuff.toString();
        }
    }
}
