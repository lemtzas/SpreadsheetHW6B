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
        assertEquals("Origin failure","A1 (0,1)",(new Cell.CellToken("A1")).fullString());
        assertEquals("Origin failure","A4 (0,4)",(new Cell.CellToken("A4")).fullString());
        assertEquals("Origin failure","A67 (0,67)",(new Cell.CellToken("A67")).fullString());
        assertEquals("Origin failure","A-67 (0,-67)",(new Cell.CellToken("A-67")).fullString());
        assertEquals("Origin failure","C5 (2,5)",(new Cell.CellToken("C5")).fullString());
        assertEquals("Origin failure","F5 (5,5)",(new Cell.CellToken("F5")).fullString());
        assertEquals("Origin failure","Z5 (25,5)",(new Cell.CellToken("Z5")).fullString());
        assertEquals("Origin failure","BA5 (26,5)",(new Cell.CellToken("BA5")).fullString());
        assertEquals("Origin failure","BAZ5 (701,5)",(new Cell.CellToken("BAZ5")).fullString());
        //assertEquals("Origin failure","A0 (0,0)",(new Cell.CellToken("AAAAAAAAAA000000000")).fullString());
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

        assertEquals("Evaluate: Double Formula, No References"               ,  (Double)3.0001220703125,  (Double)new Cell("A1","3.0 + 4.00 * 2.000 / ( 1.0000 - 5.00000 ) ^ 2.000000 ^ 3.0000000").evaluate(deps));
        assertEquals("Evaluate: Double Formula, References"                  ,  (Double)3.0001220703125,  (Double)new Cell("A1","A3 + 4.00 * DEUS2 / ( ONE1 - ZZTOP1337 ) ^ DEUS2 ^ A3").evaluate(deps));

        assertEquals("Evaluate: Double Formula, No References"               ,  (Double)(72.0/96.0),  (Double)new Cell("A1","72.0/96.0").evaluate(deps));
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
        assertEquals("Origin failure1","BA5 (26,5)",(new Cell.CellToken(26,5)).fullString());
        assertEquals("Origin failure1","BAZ5 (701,5)",(new Cell.CellToken(701,5)).fullString());
    }
}


