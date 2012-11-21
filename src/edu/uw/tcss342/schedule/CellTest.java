package edu.uw.tcss342.schedule;

import org.junit.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: mojito
 * Date: 11/19/12
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class CellTest {
    @Before
    public void init() {

    }

    @Test
    public void testTokenCell() {
//        assertEquals("Origin failure",(new Cell.CellToken("A1")).toString(),"A1 (1,1)");
//        assertEquals("Origin failure",(new Cell.CellToken("A4")).toString(),"A4 (1,4)");
//        assertEquals("Origin failure",(new Cell.CellToken("A67")).toString(),"A67 (1,67)");
//        assertEquals("Origin failure",(new Cell.CellToken("A-67")).toString(),"A-67 (1,-67)");
//        assertEquals("Origin failure",(new Cell.CellToken("C5")).toString(),"C5 (3,5)");
//        assertEquals("Origin failure",(new Cell.CellToken("F5")).toString(),"F5 (6,5)");
//        assertEquals("Origin failure",(new Cell.CellToken("Z5")).toString(),"Z5 (26,5)");
//        assertEquals("Origin failure",(new Cell.CellToken("AA5")).toString(),"AA5 (27,5)");
//        assertEquals("Origin failure",(new Cell.CellToken("ZZ5")).toString(),"ZZ5 (702,5)");
    }

    @Test
    public void parseTest() {
        new Cell("A1","1");
        new Cell("A1","15");
        new Cell("A1","A3");
        new Cell("A1","B7");
        new Cell("A1","6+2");
        new Cell("A1","6+");
        new Cell("A1","A1+2");
        new Cell("A1","A1^2^3");
        new Cell("A1","1+2*3");
        new Cell("A1","3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3");
    }

    @Test
    public void evaluateTest() {
        Map<String,Double> deps = new HashMap<String,Double>();
        assertEquals("Evaluate: Single Digit Integer"                           ,(Double)1.0,   (Double)new Cell("A1","1").evaluate(deps));
        assertEquals("Evaluate: Double Digit Integer"                           ,(Double)15.0,  (Double)new Cell("A1","15").evaluate(deps));
        deps.put("A3",3.0);
        assertEquals("Evaluate: Simple Cell ID"                                 ,(Double)3.0,   (Double)new Cell("A1","A3").evaluate(deps));
        deps.put("ZZTOP1337",3.0);
        assertEquals("Evaluate: Complex Cell ID"                                ,(Double)3.0,   (Double)new Cell("A1","ZZTOP1337").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Single Digits, No References, +",(Double)8.0,   (Double)new Cell("A1","6+2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Single Digits, No References, -",(Double)4.0,   (Double)new Cell("A1","6-2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Single Digits, No References, *",(Double)12.0,  (Double)new Cell("A1","6*2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Single Digits, No References, /",(Double)3.0,   (Double)new Cell("A1","6/2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Single Digits, No References, ^",(Double)36.0,  (Double)new Cell("A1","6^2").evaluate(deps));

        assertEquals("Evaluate: Simple Formula, Double Digits, No References, +",(Double)18.0,  (Double)new Cell("A1","16+2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, No References, -",(Double)14.0,  (Double)new Cell("A1","16-2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, No References, *",(Double)32.0,  (Double)new Cell("A1","16*2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, No References, /",(Double)8.0,   (Double)new Cell("A1","16/2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, No References, ^",(Double)256.0, (Double)new Cell("A1","16^2").evaluate(deps));
        new Cell("A1","6+2");
        new Cell("A1","6+");
        new Cell("A1","A1+2");
        new Cell("A1","A1^2^3");
        new Cell("A1","1+2*3");
        new Cell("A1","3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3");
    }
}


