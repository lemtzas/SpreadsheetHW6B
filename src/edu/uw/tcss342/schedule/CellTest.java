package edu.uw.tcss342.schedule;

import org.junit.*;

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
        assertEquals("Origin failure",(new Cell.CellToken("A1")).toString(),"A1 (1,1)");
        assertEquals("Origin failure",(new Cell.CellToken("A4")).toString(),"A4 (1,4)");
        assertEquals("Origin failure",(new Cell.CellToken("A67")).toString(),"A67 (1,67)");
        assertEquals("Origin failure",(new Cell.CellToken("A-67")).toString(),"A-67 (1,-67)");
        assertEquals("Origin failure",(new Cell.CellToken("C5")).toString(),"C5 (3,5)");
        assertEquals("Origin failure",(new Cell.CellToken("F5")).toString(),"F5 (6,5)");
        assertEquals("Origin failure",(new Cell.CellToken("Z5")).toString(),"Z5 (26,5)");
        assertEquals("Origin failure",(new Cell.CellToken("AA5")).toString(),"AA5 (27,5)");
        assertEquals("Origin failure",(new Cell.CellToken("ZZ5")).toString(),"ZZ5 (702,5)");
    }
}


