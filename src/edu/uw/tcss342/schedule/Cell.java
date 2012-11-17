package edu.uw.tcss342.schedule;

import java.awt.*;
import java.util.*;

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
        this.formula = formula;
        dependencies = new HashSet<String>();
        calculateDependencies();
    }

    public Cell(final String id, final String formula) {
        this.id = id;
        this.formula = formula;
        dependencies = new HashSet<String>();
        calculateDependencies();
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


    private void calculateDependencies() {
        dependencies.clear(); //empty result space


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
    public interface Token
}
