/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.pieces;

import com.chess.Color;
import com.chess.board.Action;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.board.Action.*;

/**
 * In this class, we are simply designing the king piece, and its legal movements
 * based on a variety of factors such as piece position, alliance, etc. (validity as well).
 */
public class King extends Piece {

    private final static int[] MOVE_COORDINATES = {-9, -8, -7, 1, 1, 7, 8, 9};
    // These are all the possible amount of spaces on a board,
    // that a king could move, in a perfect world

    /**
     * This constructor to establish the super class for a convenience constructor. We assume it's the first move
     * for convenience
     * @param piecePosition, this is the piece's spot on the chess board
     * @param pieceColor, this is the team the piece is on
     */
    public King(final int piecePosition, final Color pieceColor) {
        super(PieceType.KING, piecePosition, pieceColor, true);
    }

    /**
     * This constructor is not as convenient, as we add am extra parameter, so we can specify when it isn't the first
     * move
     */
    public King(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.KING, piecePosition, pieceColor, isFirstMove);
    }

    /**
     * The goal of this method is for us to apply all possible moves of a king to a king at a current position,
     * see if these moves are valid, if so what is our tile destination, and then from there are we able to claim that
     * spot, based on a variety of factors, such as occupancy, alliance, and exception occurrence.
     *
     * @param board is the current state of the board, according to the board class, allowing us to determine the moves.
     * @return return legalActions; this is a set of all possible moves
     */
    @Override
    public Collection<Action> numLegalMovesPerPiece(Board board) {

        final List<Action> legalActions = new ArrayList<>();

        for(final int currentMoveOffset : MOVE_COORDINATES) {

            final int moveDestinationCoordinate = this.piecePosition + currentMoveOffset;

            if(firstColumnExceptions(this.piecePosition, currentMoveOffset) ||
                    eighthColumnExceptions(this.piecePosition, currentMoveOffset)) {
                continue;
            }
            if(BoardUtils.isValidTile(moveDestinationCoordinate)) {
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
        return ImmutableList.copyOf(legalActions);
    }

    /**
     * So, what this does is this method simply gives us a new piece, for the piece that was moved, and
     * in doing so, it makes the new pieces coordinates different from the old one, so it can sit differently
     * on a new board
     * @param action, action class object to use methods in the move class
     * @return new King(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
     * This returns a new object amongst the piece subclasses, and it passes in the new position along with
     * the color
     */
    @Override
    public King movePiece(Action action) {
        return new King(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
    }

    /**
     * When this piece needs to be printed as part of our board, we will use the enumerator, and specify king, so it
     * prints "K"
     * @return PieceType.KING.toString();
     */
    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    /**
     * So, this method will give us exceptions in our first column, that break the rules of chess piece moves
     * @param currentPosition is the pieces current board placement
     * @param futureOffset is the pieces future board placement
     * return BoardUtils.FIRST_COLUMN[currentPosition] && ((futureOffset == -9) || futureOffset == -1 ||
     *                 futureOffset == 7); What this basically means is we have a boolean
     * array as part of BoardUtils. Inside this boolean array, everything in the first column is true, else is false.
     * If our current position happens to fall inside the first column, and our offset is an exception, of -9, -1, or 7,
     * then we have reached a futureOffset we can't jump to.
     */
    private static boolean firstColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((futureOffset == -9) || futureOffset == -1 ||
                futureOffset == 7);

    }

    /**
     * So, this method will give us exceptions in our eighth column, that break the rules of chess piece moves
     * @param currentPosition is the pieces current board placement
     * @param futureOffset is the pieces future board placement
     *return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((futureOffset == -7) || futureOffset == 1 ||
     *                 futureOffset == 9); What this basically means is we have a boolean
     * array as part of BoardUtils. Inside this boolean array, everything in the eighth column is true, else is false.
     * If our current position happens to fall inside the eighth column, and our offset is an exception, of -7, 1, or 9,
     * then we have reached a futureOffset we can't jump to.
     */
    private static boolean eighthColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((futureOffset == -7) || futureOffset == 1 ||
                futureOffset == 9);
    }
}
