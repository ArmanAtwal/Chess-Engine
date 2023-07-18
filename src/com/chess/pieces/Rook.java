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
 * In this class, we are simply designing the rook piece, and its legal movements
 * based on a variety fo factors such as piece position, alliance, etc. (validity as well).
 */
public class Rook extends Piece {
    private final static int[] MOVE_COORDINATES_VECTOR = {-8, -1, 1, 8};
    // These are all the possible amount of spaces on a board,
    // that a rook could move, in a perfect world

    /**
     * This constructor to establish the super class for a convenience constructor. We assume it's the first move
     * for convenience
     * @param piecePosition, this is the piece's spot on the chess board
     * @param pieceColor, this is the team the piece is on
     */
    public Rook(final int piecePosition, final Color pieceColor) {
        super(PieceType.ROOK, piecePosition, pieceColor, true);
    }

    /**
     * This constructor is not as convenient, as we add am extra parameter, so we can specify when it isn't the first
     * move
     */
    public Rook(final int piecePosition, final Color pieceColor, final Boolean isFirstMove) {
        super(PieceType.ROOK, piecePosition, pieceColor, isFirstMove);

    }

    /**
     * The goal of this method is for us to apply all possible moves of a rook to a rook at a current position,
     * see if these moves are valid, if so what is our tile destination, and then from there are we able to claim that
     * spot, based on a variety of factors, such as occupancy, alliance, and exception occurrence.
     *
     * @param board is the current state of the board, according to the board class, allowing us to determine the moves.
     * @return return ImmutableList.copyOf(legalActions); this is a set of all possible moves
     */
    @Override
    public Collection<Action> numLegalMovesPerPiece(final Board board) {
        final List<Action> legalActions = new ArrayList<>();

        for (final int currentMoveOffset : MOVE_COORDINATES_VECTOR) {

            int moveDestinationCoordinate = this.piecePosition;

            /**
             * So, we want to check first if our current position is valid (because we are in a loop where we slide
             * until we can't anymore, So after every iteration we have to make sure) and we have not
             * slid off the edge of the board yet! If we haven't, we can then continue into our next stage,
             * and find the next position on the board.
             */
            while (BoardUtils.isValidTile(moveDestinationCoordinate)) {
                // Here, after seeing if the currentPosition is fine, we will check if an exception is matched,
                // based on our firstColumnExceptions and eighthColumnExceptions method. If so, we stop moving/sliding
                if (firstColumnExceptions(moveDestinationCoordinate, currentMoveOffset) ||
                        eighthColumnExceptions(moveDestinationCoordinate, currentMoveOffset)) {
                    break;
                }
                moveDestinationCoordinate += currentMoveOffset;

                /**
                 * Here, we must check once again to see whether the new position on the board is valid!
                 * If so, we will get the information about the tile we want to move to, and then based on occupancy,
                 * and possibly alliance (if occupied) we either add an ImportantMove or AttackMove to our list.
                 */
                if (BoardUtils.isValidTile(moveDestinationCoordinate)) {
                    final ChessTile moveDestinationTile = board.getTile(moveDestinationCoordinate);
                    // With a valid coordinate, we see the tile we move to is the one on our board, at that new coordinate.

                    // If tile isn't occupied, add legal move, else, we look at piece and the color to determine action
                    if (!moveDestinationTile.isTileOccupied()) {
                        legalActions.add(new ImportantMove(board, this, moveDestinationCoordinate));
                    } else {
                        final Piece pieceAtLocation = moveDestinationTile.getPiece();
                        final Color pieceColor = pieceAtLocation.getPieceColor();

                        // If knight piece color differs from one at destination, enemy piece, so it's a legal move
                        if (this.pieceColor != pieceColor) {
                            legalActions.add(new ImportantAttackMove(board, this, moveDestinationCoordinate, pieceAtLocation));
                        }
                        break; // Break here because we've attacked, and don't want to continue sliding
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
     * @return new Rook(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
     * This returns a new object amongst the piece subclasses, and it passes in the new position along with
     * the color
     */
    @Override
    public Rook movePiece(Action action) {
        return new Rook(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
    }


    /**
     * When this piece needs to be printed as part of our board, we will use the enumerator, and specify rook, so it
     * prints "R"
     * @return PieceType.ROOK.toString();
     */
    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }

    /**
     * So, this method will give us exceptions in our first column, that break the rules of chess piece moves
     *
     * @param currentPosition is the pieces current board placement
     * @param futureOffset    is the pieces future board placement
     * @return return BoardUtils.FIRST_COLUMN[currentPosition] && ((futureOffset == -1));
     * What this basically means is we have a boolean array as part of BoardUtils. Inside this boolean array,
     * everything in the first column is true, else is false. If our current position happens to fall inside
     * the first column, and our offset is an exception, of -1 then we have reached a futureOffset we can't jump to.
     */
    private static boolean firstColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((futureOffset == -1));
    }

    /**
     * So, this method will give us exceptions in our eighth column, that break the rules of chess piece moves
     *
     * @param currentPosition is the pieces current board placement
     * @param futureOffset    is the pieces future board placement
     * @return return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((futureOffset == 1));
     * What this basically means is we have a boolean array as part of BoardUtils. Inside this boolean array,
     * everything in the eighth column is true, else is false. If our current position happens to fall inside
     * the eighth column, and our offset is an exception, of 1 then we have reached a futureOffset we can't jump to.
     */
    private static boolean eighthColumnExceptions(final int currentPosition, final int futureOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((futureOffset == 1));
    }
}
