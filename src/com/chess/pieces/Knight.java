/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.pieces;

import com.chess.Color;
import com.chess.board.Action;
import com.chess.board.Action.ImportantMove;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.board.Action.*;

/**
 * In this class, we will specifically describe the moves that a night can make
 * along with other possible behaviors while in a game.
 */
public class Knight extends Piece{
    /**
     * If we look at the max moves a night can make on a chess board, or the number of squares it can move,
     * this array represents every single possible displacement (8 moves)!
     */
    private final static int[] MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    /**
     * This constructor to establish the super class for a convenience constructor. We assume it's the first move
     * for convenience
     * @param piecePosition, this is the piece's spot on the chess board
     * @param pieceColor, this is the team the piece is on
     */
    public Knight(final int piecePosition, final Color pieceColor) {
        super(PieceType.KNIGHT, piecePosition, pieceColor, true);
    }

    /**
     * This constructor is not as convenient, as we add am extra parameter, so we can specify when it isn't the first
     * move
     */
    public Knight(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.KNIGHT, piecePosition, pieceColor, isFirstMove);
    }

    /**
     * The goal of this method is for us to apply all possible moves of a knight to a knight at a current position,
     * see if these moves are valid, if so what is our tile destination, and then from there are we able to claim that
     * spot, based on a variety of factors, such as occupancy and alliance.
     *
     * @param board is the current state of the board, according to the board class, allowing us to determine the moves.
     * @return return ImmutableList.copyOf(legalActions); this is a set of all possible moves
     */
    @Override
    public Collection<Action> numLegalMovesPerPiece(final Board board) {
        final List<Action> legalActions = new ArrayList<>(); // List/set that captures our legal moves for the knight

        for(final int currentMoveOffset : MOVE_COORDINATES) {

            int moveDestinationCoordinate = this.piecePosition + currentMoveOffset; // In a perfect case, our final
            // coordinate is position + the current offset/move
            /**
             * So, if the coordinate is valid (0-64) and doesn't meet any of the exceptions in the columns specific to
             * a knight, then we move on to check the tile which exists at this spot, and from their determine whether
             * the action is legal or not, based on occupancy and piece status. HOWEVER, if an exception is met,
             * we simply continue on to the next iteration.
             */
            if(BoardUtils.isValidTile(moveDestinationCoordinate)) {
                if(firstColumnExceptions(this.piecePosition, currentMoveOffset) ||
                        secondColumnExceptions(this.piecePosition, currentMoveOffset) ||
                        seventhColumnExceptions(this.piecePosition, currentMoveOffset) ||
                                eighthColumnExceptions(this.piecePosition, currentMoveOffset)) {
                    continue;
                }
                final ChessTile moveDestinationTile = board.getTile(moveDestinationCoordinate);
                // With a valid coordinate, we see the tile we move to is the one on our board, at that new coordinate.

                // If tile isn't occupied, add legal move, else, we look at piece and the color to determine action
                if(!moveDestinationTile.isTileOccupied()) {
                    legalActions.add(new ImportantMove(board, this, moveDestinationCoordinate));
                }
                else {
                   final Piece pieceAtLocation = moveDestinationTile.getPiece();
                   final Color pieceColor = pieceAtLocation.getPieceColor();

                    // If knight piece color differs from one at destination, enemy piece, so it's a legal move
                   if(this.pieceColor != pieceColor) {
                       legalActions.add(new ImportantAttackMove(board, this, moveDestinationCoordinate, pieceAtLocation));
                   }
                }
            }
        }
        return ImmutableList.copyOf(legalActions); // Return set of all possible moves
    }

    /**
     * So, what this does is this method simply gives us a new piece, for the piece that was moved, and
     * in doing so, it makes the new pieces coordinates different from the old one, so it can sit differently
     * on a new board
     * @param action, action class object to use methods in the move class
     * @return new Knight(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
     * This returns a new object amongst the piece subclasses, and it passes in the new position along with
     * the color
     */
    @Override
    public Knight movePiece(Action action) {
        return new Knight(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
    }

    /**
     * When this piece needs to be printed as part of our board, we will use the enumerator, and specify knight, so it
     * prints "N"
     * @return PieceType.KNIGHT.toString();
     */
    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    /**
     * So, this method will give us exceptions in our first column, that break the rules of chess piece moves
     * @param currentPosition is the pieces current board placement
     * @param futureOffset is the pieces future board placement
     * @return BoardUtils.FIRST_COLUMN[currentPosition] && ((futureOffset == -17) || futureOffset == -10 ||
     *                 futureOffset == 6 || futureOffset == 15); What this basically means is we have a boolean
     * array as part of BoardUtils. Inside this boolean array, everything in the first column is true, else is false.
     * If our current position happens to fall inside the first column, and our offset is an exception, of -17, -10, 6,
     * or 15, then we have reached a futureOffset we can't jump to.
     */
    private static boolean firstColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((futureOffset == -17) || futureOffset == -10 ||
                futureOffset == 6 || futureOffset == 15);

    }

    /**
     * So, this method will give us exceptions in our second column, that break the rules of chess piece moves
     * @param currentPosition is the pieces current board placement
     * @param futureOffset is the pieces future board placement
     * @return  return BoardUtils.SECOND_COLUMN[currentPosition] && ((futureOffset == -10) || futureOffset == 6); What
     * this basically means is we have a boolean
     * array as part of BoardUtils. Inside this boolean array, everything in the second column is true, else is false.
     * If our current position happens to fall inside the second column, and our offset is an exception, of -10 or 6,
     * then we have reached a futureOffset we can't jump to.
     */
    private static boolean secondColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((futureOffset == -10) || futureOffset == 6);

    }

    /**
     * So, this method will give us exceptions in our seventh column, that break the rules of chess piece moves
     * @param currentPosition is the pieces current board placement
     * @param futureOffset is the pieces future board placement
     * @return  return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((futureOffset == -6) || futureOffset == 10); What
     * this basically means is we have a boolean
     * array as part of BoardUtils. Inside this boolean array, everything in the seventh column is true, else is false.
     * If our current position happens to fall inside the seventh column, and our offset is an exception, of -6 or 10,
     * then we have reached a futureOffset we can't jump to.
     */
    private static boolean seventhColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((futureOffset == -6) || futureOffset == 10 ||
                futureOffset == 6 || futureOffset == 15);

    }

    /**
     * So, this method will give us exceptions in our eighth column, that break the rules of chess piece moves
     * @param currentPosition is the pieces current board placement
     * @param futureOffset is the pieces future board placement
     * @returnBoardUtils.EIGHTH_COLUMN[currentPosition] && ((futureOffset == -15) || futureOffset == -6 ||
     *                 futureOffset == 10 || futureOffset == 17); What this basically means is we have a boolean
     * array as part of BoardUtils. Inside this boolean array, everything in the eighth column is true, else is false.
     * If our current position happens to fall inside the eighth column, and our offset is an exception, of -15, -6, 10,
     * or 17, then we have reached a futureOffset we can't jump to.
     */
    private static boolean eighthColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((futureOffset == -15) || futureOffset == -6 ||
                futureOffset == 10 || futureOffset == 17);

    }


}
