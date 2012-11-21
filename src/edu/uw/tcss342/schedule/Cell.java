package edu.uw.tcss342.schedule;


import com.sun.javaws.exceptions.InvalidArgumentException;

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
    Set<String> dependencies;
    Queue<Token> token_queue;

    public Cell(final String id, final String formula) {
        this.id = id;
        this.formula = formula;
        dependencies = new HashSet<String>();
        parse();
    }

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
                    Double left = values.pop();
                    if(values.empty()) throw new IllegalStateException("Invalid Formula");
                    Double right = values.pop();
                    values.push(bot.operation.run(left,right));
                }
            }
        }
        if(values.empty()) throw new IllegalStateException("Invalid Formula");
        return values.pop();
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
        //finish up
        while(!workspace_stack.empty())
            output_queue.add(workspace_stack.pop());
        token_queue = new LinkedList<Token>(output_queue);

        //postfixify
        StringBuilder sb = new StringBuilder();
        while(!output_queue.isEmpty())
            sb.append(" ").append(output_queue.poll().toString());

        System.out.println(formula + " = " + sb);
    }

    /**
     *
     * @param remainder The index to look for the start of the token at
     * @param output_queue the queue of tokens
     * @return the new index
     */
    private String parseToken(String remainder,Queue<Token> output_queue,Stack<Token> workspace_stack) {
        Pattern cell = Pattern.compile("^[A-Z]+[0-9]+");
        Pattern literal = Pattern.compile("^[0-9]+");

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
                        System.out.print("*");
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
                        @Override public double run(double left, double right) {return left / right;}}),
            new BinaryOperatorToken(4,false,"^",
                    new BinaryOperatorRunnable() {
                        @Override public double run(double left, double right) {return Math.pow(left, right);}})
    };


    private static interface Token {}
    private static class LeftParenToken implements Token{}
    private static class RightParenToken implements Token{}
    private static class LiteralToken implements Token {
        public LiteralToken(final double value) {
            this.value = value;
        }
        public String toString() {
            return Double.toString(value);
        }
        public final double value;
    }
    private static interface OperatorToken extends Token {}
    private static abstract class BinaryOperatorRunnable {
        /** Performs the operation for this operator **/
        abstract public double run(double left, double right);
    }
    private static class BinaryOperatorToken implements OperatorToken {
        public final int precedence;
        public final String token;
        public final BinaryOperatorRunnable operation;
        public final boolean is_left_associative;
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

        public double value(Map<String,Double> values) {
            return values.get(this.identifier);
        }

        public String toString() {
            return identifier;// + " (" + column + "," + row + ")";
        }
    }
}
