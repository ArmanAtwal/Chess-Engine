/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */


package com.chess.board;

import java.util.*;

/**
 * This is simply a class we created to store any useful methods or members we will most likely repeat across
 * classes, so that way we don't have to reuse code over and over.
 */
public class BoardUtils {
    public static final int NUM_TILES = 64;
    public static final int TILES_PER_ROW = 8;
    public static final int START_TILE_INDEX = 0;

    /**
     * In this method, we create our boolean array for any row, by first starting at the beginning tile of the row,
     * then making that tile true. Finally, next, we increment by one, and continue until we reach the end of the row!
     * The remaining spots have their default values!
     * @param tileID, this is the number tile on our board, or starting spot of the row
     * @return row, which is the finished array!
     */
    private static boolean[] rowMaker(int tileID) {
        final boolean[] row = new boolean[NUM_TILES]; // 64 for number of boards
        do {
            row[tileID] = true;
            tileID++;
        } while(tileID % TILES_PER_ROW != 0);
        return row;
    }

    /**
     * In this method, we create our boolean array for any column, by incrementing by 8 for every spot in
     * a column, to reach the next true spot.
     * @param columnNumber; this is the column number and we start here
     * @return a boolean array with the correct values based on column number
     */
    private static boolean[] columnMaker(int columnNumber) {
        final boolean[] column = new boolean[NUM_TILES]; // 64 for number of boards

        do {
            column[columnNumber] = true;
            columnNumber += TILES_PER_ROW;

        } while(columnNumber < NUM_TILES);
        return column;
    }

    public static final boolean[] FIRST_COLUMN = columnMaker(0);
    // Boolean Array for first column to detect any exceptions
    public static final boolean[] SECOND_COLUMN = columnMaker(1); ;
    // Boolean Array for second column to detect any exceptions
    public static final boolean[] SEVENTH_COLUMN = columnMaker(6); ;
    // Boolean Array for seventh column to detect any exceptions
    public static final boolean[] EIGHTH_COLUMN = columnMaker(7);
    // Boolean Array for eighth column to detect any exceptions

    // Creates an array where first row is all true
    public static final boolean[] FIRST_ROW = rowMaker(0);
    // Creates an array where second row is all true
    public static final boolean[] SECOND_ROW = rowMaker(8);
    // Creates an array where third row is all true
    public static final boolean[] THIRD_ROW = rowMaker(16);
    // Creates an array where fourth row is all true
    public static final boolean[] FOURTH_ROW = rowMaker(24);
    // Creates an array where fifth row is all true
    public static final boolean[] FIFTH_ROW = rowMaker(32);
    // Creates an array where sixth row is all true
    public static final boolean[] SIXTH_ROW = rowMaker(40);
    // Creates an array where seventh row is all true
    public static final boolean[] SEVENTH_ROW = rowMaker(48);
    // Creates an array where eighth row is all true
    public static final boolean[] EIGHTH_ROW = rowMaker(56);
    // Using the method below, we can initialize our array, so it contains the positions that map to a specific chess coordinate
    public static final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    // Using the method below, we can create a map, so it contains the coordinate that a specific position maps too
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();



    private BoardUtils() {
        throw new RuntimeException("Class can't be instantiated"); // This ensures our board util class can't have obj.
    }

    /**
     * Here, what this method does is it simply takes every correct position on a chess board, and uses that as the index
     * for that positions correct coordinate! It then makes this into a map, for which we can pass the position into, as a
     * string, and get the coordinate (used in our getCoordinateAtPosition() method)!
     * @return
     */
    private static Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for(int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    /**
     * In this method, we simply make an immutable list of all the positions on a chess board, with numbers and
     * letters, for the purpose of retrieval in our get position at coordinate method!
     * @return
     */
    private static List<String> initializeAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        ));
    }

    /**
     * This method is used in order for us to determine whether the coordinate, after we apply a move for
     * a knight, is valid or not.
     *
     * @param moveDestinationCoordinate; this is our destination on the board, which we will analyze for validity
     * @return moveDestinationCoordinate >=0 && moveDestinationCoordinate < 64; true if in range, else false
     */
    public static boolean isValidTile(final int moveDestinationCoordinate) {
        return moveDestinationCoordinate >= 0 && moveDestinationCoordinate < NUM_TILES;
    }

    /**
     * Here in this method, we pass in a position, on a map, and we return the chess coordinates. We will use a map to
     * complete this process!
     */
    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    /**
     * This method returns the ALGEBRAIC_NOTATION for a coordinate. A position on a chess board is letters and numbers,
     * and we will have an array which maps coordinates to a position, and returns the positions!
     */
    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }
}
