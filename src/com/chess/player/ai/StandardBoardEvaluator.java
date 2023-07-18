/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.player.ai;

import com.chess.board.Board;
import com.chess.pieces.Piece;
import com.chess.player.Player;

/**
 * This class is responsible for allowing the AI to evaluate the board and determine its next action after that
 */
public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50; // Addition value to score if opposing player is in check!
    private static final int CHECK_MATE_BONUS = 10000; // Addition value to score if opposing player is in checkmate!
    private static final int DEPTH_BONUS = 100; // Scale value to score if opposing player is in checkmate and later in game!
    private static final int CASTLE_BONUS = 60; // Scale value to score if opposing player is castled!

    /**
     * In this method, we calculate the score for the board overall! If the number returned is negative, black has the advantage,
     * and vice versa!
     */
    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
                scorePlayer(board, board.blackPlayer(), depth);
    }

    /**
     * This method returns the scores we've calculated, based on the color of the pieces! NOT THE BOARD
     */
    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth) + castled(player);
    }

    /**
     * If opposing player is castled, how does that affect the score?
     */
    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    /**
     * Is the opposing player in checkmate or not? Based on that, we return a value, impacting the score, and ultimately the
     * evaluation of the board!
     */
    private static int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    /**
     * This is simply a scaling factor. If we find the checkmate later in game, then we want to significantly scale
     * the depth value, so we can greatly impact scores during evaluation!
     */
    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    /**
     * Is the opposing player in check or not? Based on that, we return a value, impacting the score, and ultimately the
     * evaluation of the board!
     */
    private static int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    /**
     * This method simply tells us that in the position the player is in, how many options do they have? How constricted
     * are they? We get a value for this to evaluate the board!
     */
    private static int mobility(Player player) {
        return player.getLegalActions().size();
    }

    /**
     * What this method does, is it assigns every active piece a value, based on the color, and will take the sum
     * for all the pieces left on the board, and return that int value!
     */
    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for(final Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }


}
