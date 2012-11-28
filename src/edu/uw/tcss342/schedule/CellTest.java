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
        assertEquals("Origin failure",(new Cell.CellToken("A1")).fullString(),"A1 (0,1)");
        assertEquals("Origin failure",(new Cell.CellToken("A4")).fullString(),"A4 (0,4)");
        assertEquals("Origin failure",(new Cell.CellToken("A67")).fullString(),"A67 (0,67)");
        assertEquals("Origin failure",(new Cell.CellToken("A-67")).fullString(),"A-67 (0,-67)");
        assertEquals("Origin failure",(new Cell.CellToken("C5")).fullString(),"C5 (2,5)");
        assertEquals("Origin failure",(new Cell.CellToken("F5")).fullString(),"F5 (5,5)");
        assertEquals("Origin failure",(new Cell.CellToken("Z5")).fullString(),"Z5 (25,5)");
        assertEquals("Origin failure",(new Cell.CellToken("AA5")).fullString(),"AA5 (26,5)");
        assertEquals("Origin failure",(new Cell.CellToken("ZZ5")).fullString(),"ZZ5 (701,5)");
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
        deps.put("A3",3.0);
        deps.put("ZZTOP1337",5.0);
        deps.put("ONE1",1.0);
        deps.put("DEUS2",2.0);
        assertEquals("Evaluate: Single Digit Integer"                           ,(Double)1.0,   (Double)new Cell("A1","1").evaluate(deps));
        assertEquals("Evaluate: Double Digit Integer"                           ,(Double)15.0,  (Double)new Cell("A1","15").evaluate(deps));
        assertEquals("Evaluate: Simple Cell ID"                                 ,(Double)3.0,   (Double)new Cell("A1","A3").evaluate(deps));
        assertEquals("Evaluate: Complex Cell ID"                                ,(Double)5.0,   (Double)new Cell("A1","ZZTOP1337").evaluate(deps));
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

        assertEquals("Evaluate: Simple Formula, Double Digits, References, +",  (Double)5.0,  (Double)new Cell("A1","A3+2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, References, -",  (Double)1.0,  (Double)new Cell("A1","A3-2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, References, *",  (Double)6.0,  (Double)new Cell("A1","A3*2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, References, /",  (Double)1.5,   (Double)new Cell("A1","A3/2").evaluate(deps));
        assertEquals("Evaluate: Simple Formula, Double Digits, References, ^",  (Double)9.0, (Double)new Cell("A1","A3^2").evaluate(deps));

        assertEquals("Evaluate: Complex Formula, No References"              ,  (Double)3.0001220703125,  (Double)new Cell("A1","3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3").evaluate(deps));
        assertEquals("Evaluate: Complex Formula, References"                 ,  (Double)3.0001220703125,  (Double)new Cell("A1","A3 + 4 * DEUS2 / ( ONE1 - ZZTOP1337 ) ^ DEUS2 ^ A3").evaluate(deps));
    }



    @Test
    public void idConversionTest() {
        assertEquals("Origin failure1","Y5 (24,5)",(new Cell.CellToken(24,5)).fullString());
        assertEquals("Origin failure1","Z5 (25,5)",(new Cell.CellToken(25,5)).fullString());
        assertEquals("Origin failure1","A0 (0,0)",(new Cell.CellToken(0,0)).fullString());
        assertEquals("Origin failure1","A4 (0,4)",(new Cell.CellToken(0,4)).fullString());
        assertEquals("Origin failure1","A67 (0,67)",(new Cell.CellToken(0,67)).fullString());
        assertEquals("Origin failure1","A-67 (0,-67)",(new Cell.CellToken(0,-67)).fullString());
        assertEquals("Origin failure1","C5 (2,5)",(new Cell.CellToken(2,5)).fullString());
        assertEquals("Origin failure1","F5 (5,5)",(new Cell.CellToken(5,5)).fullString());
        assertEquals("Origin failure1","AA5 (26,5)",(new Cell.CellToken(26,5)).fullString());
        assertEquals("Origin failure1","ZZ5 (701,5)",(new Cell.CellToken(701,5)).fullString());
    }
}


