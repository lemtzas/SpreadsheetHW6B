package edu.uw.tcss342.schedule;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    Set<String> dependencies;

    public Cell(final String formula) {
        this.formula = formula;
        //calculate dependencies
        dependencies = new HashSet<String>();
    }

    public Double evaluate(Map<String,Double> dep_values) {
        //return result, given values for dependencies
        return null;
    }

    /**
     * The cells this cell's value depends on, in set format.
     * @return Set of Dependencies; non-null; possibly empty
     */
    public Set<String> getDependencies() {
        //return calculated dependencies
        return new HashSet<String>();
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
}
