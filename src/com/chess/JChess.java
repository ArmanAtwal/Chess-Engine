/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess;

import com.chess.board.Board;
import com.chess.gui.Table;

import java.io.IOException;

/**
 * This is simply our driver/test class
 */
public class JChess {

    public static void main(String[] args) {

        // We want to check and see if our initial board can be represented as Strings,
        // and then we can view it by printing all these strings we've managed to collect


        Board board = Board.createStandardBoard();

        System.out.println(board);

        Table.get().show();

    }
}
