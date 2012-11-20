package edu.uw.tcss342.schedule;

/**
 * The cell token class for the spreadsheet.
 * 
 * @author Alex Stirngham
 * 
 */
public class CellToken {
        /**
         * Column of a given cell in the spreadsheet.
         */
        private int myColumn;

        /**
         * Row of a given cell in the spreadsheet.
         */
        private int myRow;


        /**
         * Default Constructor.
         */
        public CellToken() {
                myColumn = 0;
                myRow = 0;
        }

        /**
         * Constructor with given inputs..
         */
        public CellToken(final int theRow, int theColumn) {
                myRow = theRow;
                myColumn = theColumn;
        }


        /**
         * Simple getter for the column.
         * @return A given column.
         */
        public int getColumn() {
                return myColumn;
        }

        /**
         * Simple getter for the row.
         * @return A given row.
         */
        public int getRow() {
                return myRow;
        }

        /**
         * Setter for a column.
         */
        public void setColumn(final int theCol) {
                myColumn = theCol;
        }

        /**
         * Setter For a row.
         */
        public void setRow(final int theRow) {
                myRow = theRow;
        }

        public String columnsToString() {
                StringBuffer stringBuff = new StringBuffer();
                int columnDiv = myColumn / 26;
                int columnMod = myColumn % 26;
                char letter = (char) (columnMod + 'A');
                stringBuff.append(letter);
                while (columnDiv > 0) {
                        columnMod = (columnDiv - 1) % 26;
                        columnDiv = columnDiv / 26;
                        letter = (char) (columnMod + 'A');
                        stringBuff.insert(0, letter);
                }
                return stringBuff.toString();
        }
}