/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */


package com.chess.player.ai;

import com.chess.board.Board;

/**
 * The purpose of this interface is for us to be able to implement it, and have our Ai
 * evaluate the board to make the best choice possible
 */
public interface BoardEvaluator {
    /**
     * More positive the number, white winning and vice versa!
     */
    int evaluate(Board board, int depth);
}
