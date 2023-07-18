package com.chess.gui;

import com.chess.board.Board;
import com.chess.gui.Table.ActionLog;
import com.chess.board.Action;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;



/**
 * This class will be responsible for creating a panel in our GUI that tracks the game history, meaning all the moves, attacks,
 * etc.
 */
public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    // We create a model from a class that we've made, incorporating JTable behavior, however, it's more complex
    // and allows the better expression of information (not minimalist)
    private final JScrollPane scrollPane; // Allows creation of our scrolling function
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(150, 400); // 100 by 400 for scroll function

    /**
     * Simple constructor to set the properties for the features of our game history panel
     */
    GameHistoryPanel() {
        this.setLayout(new BorderLayout()); // Create layout/area for panel
        this.model = new DataModel(); // Initialization of our model
        final JTable table = new JTable(model); // To track the game history, we are using a table
        table.setRowHeight(15); // Row height/number is 15
        this.scrollPane = new JScrollPane(table);
        // If the number of moves exceeds the window frame (in game history panel), we
        // can scroll through the moves
        scrollPane.setColumnHeaderView(table.getTableHeader()); // Header portion
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION); // Size of scroll pane
        this.add(scrollPane, BorderLayout.CENTER); // Adding it to the display, so it's visible in our GUI
        this.setVisible(true); // Made visible
    }

    /**
     * This is responsible for filling up our data model and resetting when needed!
     * @param board, the game board
     * @param actionLog, the game moves history
     */
    void redo(final Board board, final ActionLog actionLog) {
        int currentRow = 0; // Sets the row to zero
        this.model.clear(); // Clears any previous models
        for(final Action action : actionLog.getActions()) { // Iterate through action log, as it's being updated
            final String actionText = action.toString(); // Change move into string
            if (action.getMovedPiece().getPieceColor().isWhite()) { // If white moved
                this.model.setValueAt(actionText, currentRow, 0); // Set text to current row, column 0
            }
            else if (action.getMovedPiece().getPieceColor().isBlack()) { // If black moved
                this.model.setValueAt(actionText, currentRow, 1); // Set text to current row, column 1
                currentRow++; // move on to next row once previous is filled!
            }
        }

        if(actionLog.getActions().size() > 0) { // There is at least one move
            final Action lastAction = actionLog.getActions().get(actionLog.size() - 1); // Get the last action
            final String actionText = lastAction.toString(); // Change last move to string

            if(lastAction.getMovedPiece().getPieceColor().isWhite()) { // If last move is white piece
                // Modify text to make it clear in history log
                this.model.setValueAt(actionText + calcCheckandCheckMate(board), currentRow, 0);
            }
            else if(lastAction.getMovedPiece().getPieceColor().isBlack()) { // If last move is black piece
                // Modify text to make it clear in history log
                this.model.setValueAt(actionText + calcCheckandCheckMate(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vert = scrollPane.getVerticalScrollBar(); // Making a vertical scroll bar for history
        vert.setValue(vert.getMaximum()); // Continues advancing the scroll bar until the last move
    }

    /**
     * So the purpose of this method is to add an extra character to our history panel, while creating it,
     * to let anyone who is reading it know a player was in check or checkmate at x point!
     * @param board, our game board
     */
    private String calcCheckandCheckMate(Board board) {
        if(board.currentPlayer().isInCheckMate()) {
            return "#"; //PGN convention
        }
        else if(board.currentPlayer().isInCheck()) {
            return "+"; //PGN convention
        }
        return "";
    }

    /**
     * This class is used to design the format for our data model which will be used to contain the information
     * of the game history, and also incorporated into the GUI, so we can see it visually.
     */
    private static class DataModel extends DefaultTableModel {

        private final List<Row> values;
        // This is a list of the many rows we will have in the game history section. One white and black both go,
        // a new row is made! Also, we store the coordinate the piece moves to (for both colors), for that turn!
        private static final String[] names = {"White", "Black"}; // Names for our two headers/sections in the model

        /**
         * Basic constructor to initialize fields
         */
        DataModel() {
            this.values = new ArrayList<>(); // Initializes the row arrayList;
        }

        /**
         * This method is for when a new game occurs, and we need to clear the previous histroy log
         */
        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        /**
         * We have to redefine the methods used by the DefaultTableModel, so we can properly create our own! What happens
         * here is if our list has no values or is null, return 0, else return the size!
         *
         * @return
         */
        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        /**
         * This returns 2 for both colors of a chess piece
         */
        @Override
        public int getColumnCount() {
            return names.length;
        }

        /**
         * Now, this method is going to simply return the value at whatever row, in whatever column
         * @param row             the row whose value is to be queried
         * @param column          the column whose value is to be queried
         */
        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.values.get(row); // We use the row index to determine what row we are on!
            if (column == 0) { // If column is 0
                return currentRow.getWhiteMove(); // We return the whiteMove at that row, because 1st column is whiteMoves
            } else if (column == 1) { // If column is 1
                return currentRow.getBlackMove(); // We return the blackMove at that row, because 2nc column is blackMoves
            } else { // else return null
                return null;
            }
        }

        /**
         * The purpose of this is to set a value at a certain spot, given row and column
         * @param aValue          the new value; this can be null
         * @param row             the row whose value is to be changed
         * @param column          the column whose value is to be changed
         */
        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
            final Row currentRow; // Row object
            if (this.values.size() <= row) { // If we're out of rows
                currentRow = new Row(); // Make a new row
                this.values.add(currentRow); // Add the new row to the list
            }
            else {
                currentRow = this.values.get(row); // else get the row from the list if no new row is needed
            }
            if (column == 0) { // If white piece
                currentRow.setWhiteMove((String)aValue); // Set value at current row; cast as String to match param
                fireTableRowsInserted(row, row); // Adding a new row when column is 0
            }
            else if (column == 1) { // If black piece
                currentRow.setBlackMove((String)aValue); // Set value at current row; cast as String to match param
                fireTableCellUpdated(row, column); // update table
            }
        }

        /**
         * Simply returns the class of the column
         * @param column  the column being queried
         */
        @Override
        public Class<?> getColumnClass(final int column) {
            return Action.class;
        }

        /**
         * This returns the columns name!
         * @param column  the column being queried
         */
        @Override
        public String getColumnName(final int column) {
            return names[column];
        }

        /**
         * This class mimics the behavior of one of our rows in the data model. We have two sections, and each section has
         * the same number of rows, which contains the spot a white piece moves to (section 1) and the spot a black piece moves
         * to (section 2).
         */
        private static class Row {
            private String whiteMove; // This field tells us where the white piece moves to (coordinate)
            private String blackMove; // This field tells us where the black piece moves to (coordinate)

            // Regular Constructor
            Row() {

            }

            /**
             * This is a getter returning the whiteMove (where it ended).
             *
             * @return this.whiteMove;
             */
            public String getWhiteMove() {
                return this.whiteMove;
            }

            /**
             * This is a getter returning the blackMove (where it ended).
             *
             * @return this.blackMove;
             */
            public String getBlackMove() {
                return this.blackMove;
            }

            /**
             * This is a setter, which sets the coordinate for a whiteMove
             *
             * @param move, the coordinate
             */
            public void setWhiteMove(final String move) {
                this.whiteMove = move;
            }

            /**
             * This is a setter, which sets the coordinate for a blackMove
             *
             * @param move, the coordinate
             */
            public void setBlackMove(final String move) {
                this.blackMove = move;
            }
        }
    }
}
