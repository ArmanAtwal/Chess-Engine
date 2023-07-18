/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.player.ai;

import com.chess.board.Action;
import com.chess.board.Board;

/**
 * This is a simple interface we're going to use in our class, MiniMax, which is responsible for helping the AI make the
 * best choice
 */
public interface MoveStrat {
    Action execute(Board board);
}
