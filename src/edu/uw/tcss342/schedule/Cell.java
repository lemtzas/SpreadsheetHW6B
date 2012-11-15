package edu.uw.tcss342.schedule;

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
    public Cell(String formula) {
        //calculate dependencies
    }

    public Double evaluate(Map<String,Double> dep_values) {
        //return result, given values for dependencies
        return null;
    }

    public Set<String> getDependencies() {
        //return calculated dependencies
        return null;
    }
}
