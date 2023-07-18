/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.pieces;

import com.chess.Color;
import com.chess.board.Action;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.board.Action.*;

/**
 * So, in this class, we are programming the complex movements and actions of a pawn
 * during a chess game. A pawn at the start has the option to move one or two spaces,
 * and then after that it can only move one. Moreover, when attacking, it can go
 * diagonally. Additionally, if a pawn reaches the end, it can be promoted to another
 * piece, besides the king. Finally, we have directionality, as the black pieces move up
 * in terms of spots (they start at top of board or 0), and the white pieces move down (opposite).
 * NOTE: There is a special move known as En Passant, pawn captures passing pawn
 */
public class Pawn extends Piece {
    private final static int[] MOVE_COORDINATES = {8, 16, 7, 9};
    // These are all the possible amount of spaces on a board,
    // that a pawn could move, in a perfect world

    /**
     * This constructor to establish the super class for a convenience constructor. We assume it's the first move
     * for convenience
     * @param piecePosition, this is the piece's spot on the chess board
     * @param pieceColor, this is the team the piece is on
     */
    public Pawn(final int piecePosition, final Color pieceColor) {
        super(PieceType.PAWN, piecePosition, pieceColor, true);
    }

    /**
     * This constructor is not as convenient, as we add am extra parameter, so we can specify when it isn't the first
     * move
     */
    public Pawn(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceColor, isFirstMove);
    }

    /**
     * The goal of this method is for us to apply all possible moves of a pawn to a pawn at a current position,
     * see if these moves are valid, if so what is our tile destination, and then from there are we able to claim that
     * spot, based on a variety of factors, such as occupancy, alliance, and exception occurrence.
     *
     * @param board is the current state of the board, according to the board class, allowing us to determine the moves.
     * @return return legalActions; this is a set of all possible moves
     */
    @Override
    public Collection<Action> numLegalMovesPerPiece(final Board board) {

        final List<Action> legalActions = new ArrayList<>();

        for(final int currentMoveOffset : MOVE_COORDINATES) {

            final int moveDestinationCoordinate = this.piecePosition + this.getPieceColor().getDirection() * currentMoveOffset;
            // This allows us to look at the piece color, and based on that, determine if we add a negative value (White)
            // to move up the board, or add a positive value to move down (Black)/

            // If final destination is not valid coordinate, we then just skip and continue to the next possible movement.
            if(!BoardUtils.isValidTile(moveDestinationCoordinate)) {
                continue;
            }

            /**
             * So, if the current offset is 8, and the tile in front of us, where we want to move, is NOT (!) occupied,
             * then we're going to add this as a legal move.
             */
            if (currentMoveOffset == 8 && !board.getTile(moveDestinationCoordinate).isTileOccupied()) {
                if(this.pieceColor.isPawnPromotionSquare(moveDestinationCoordinate)) { // If we reach a promotion square
                    // We will have a pawn promotion move added to the legal actions
                    legalActions.add(new PawnPromotion(new PawnMove(board, this, moveDestinationCoordinate)));
                }
                else {
                    legalActions.add(new PawnMove(board, this, moveDestinationCoordinate));
                }
            }
            /**
             * HOWEVER, if our currentOffset is 16, it's the first move for piece, and this piece is black in the second
             * row (meaning a black pawn that hasn't moved) or white in the seventh row (meaning a black pawn that hasn't
             * moved) there is a possibility to move 2 SPACES (space in front can't be occupied)!
             */
            else if (currentMoveOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceColor().isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceColor().isWhite()))) {

                /** Before We can move two spaces we want to make sure and check that the tile a single space ahead isn't
                 * occupied. Thus, what we do is find the coordinate of that tile by doing half the current offset of 16 (8),
                 * and multiplying by -1 for white or 1 for black (then adding to the current position). From here, if the tile
                 * at that spot isn't occupied, and the tile at the goal spot isn't either, we can move the piece 2 spots.
                 */
                final int behindMoveDestinationCoordinate = this.piecePosition + (this.getPieceColor().getDirection() * 8);
                if(!board.getTile(behindMoveDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(moveDestinationCoordinate).isTileOccupied()) {
                    legalActions.add(new PawnJump(board, this, moveDestinationCoordinate));
                }
            }
            /**
             * If the current offset is 7, and we have a piece in the eighth column and its white, or we have a piece in
             * the first column, and it's black, then we run into an exception. HOWEVER, if the exception doesn't occur,
             * then check if the tile on the board is occupied, where you'd like to move. If so, determine
             * the piece that exists there, and if the piece there is different from the current piece, you perform
             * an ATTACK!
             */
            else if(currentMoveOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[piecePosition] && this.pieceColor.isWhite() ||
                            (BoardUtils.FIRST_COLUMN[piecePosition] && this.pieceColor.isBlack())))) {
                if(board.getTile(moveDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnDestination = board.getTile(moveDestinationCoordinate).getPiece();
                    if (this.pieceColor != pieceOnDestination.getPieceColor()) {
                        if(this.pieceColor.isPawnPromotionSquare(moveDestinationCoordinate)) { // If we reach pawn promotion square
                            // We will have a pawn promotion move added to the legal actions
                            legalActions.add(new PawnPromotion(new PawnAttackMove(board, this, moveDestinationCoordinate, pieceOnDestination)));
                        }
                        else {
                            legalActions.add(new PawnAttackMove(board, this, moveDestinationCoordinate, pieceOnDestination));
                        }
                    }
                }
                else if(board.getEnPassantPawn() != null) { // If an EnPassantPawn exists
                    /**
                     * Basically, if the en passant pawn is to your left or right, meaning if you go the opposite direction
                     * with your piece, you land on the en passant pawn,
                     */
                    if(board.getEnPassantPawn().getPiecePosition() ==
                            (this.piecePosition + (this.pieceColor.getOppositeDirection()))) {
                        // then that is indeed our en passant pawn!
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        // If the colors of the two pieces are different!
                        if(this.pieceColor != pieceOnCandidate.getPieceColor()) {
                            // We perform an en passant attack move, where we attack diagonally and pass the pawn/capture it!
                            legalActions.add(new PawnEnPassantAttackMove(board, this,
                                    moveDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
            /**
             * If the current offset is 9, and we have a piece in the eighth column and its black, or we have a piece in
             * the first column, and it's white, then we run into an exception. HOWEVER, if you're not part of this
             * exception, then check if the tile on the board is occupied, where you'd like to move. If so, determine
             * the piece that exists there, and if the piece there is different from the current piece, you perform
             * an ATTACK!
             */
            else if (currentMoveOffset == 9 &&
                    !((BoardUtils.EIGHTH_COLUMN[piecePosition] && this.pieceColor.isBlack() ||
                            (BoardUtils.FIRST_COLUMN[piecePosition] && this.pieceColor.isWhite()))))  {
                if(board.getTile(moveDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnDestination = board.getTile(moveDestinationCoordinate).getPiece();
                    if (this.pieceColor != pieceOnDestination.getPieceColor()) {
                        if(this.pieceColor.isPawnPromotionSquare(moveDestinationCoordinate)) { // If we reach pawn promotion square
                            // We will have a pawn promotion move added to the legal actions
                            legalActions.add(new PawnPromotion(new PawnAttackMove(board, this, moveDestinationCoordinate, pieceOnDestination)));
                        }
                        else {
                            legalActions.add(new PawnAttackMove(board, this, moveDestinationCoordinate, pieceOnDestination));
                        }
                    }
                }
                else if(board.getEnPassantPawn() != null) { // If an EnPassantPawn exists
                    /**
                     * Basically, if the en passant pawn is to your left or right, meaning if you go the opposite direction
                     * with your piece, you land on the en passant pawn,
                     */
                    if(board.getEnPassantPawn().getPiecePosition() ==
                            (this.piecePosition - (this.pieceColor.getOppositeDirection()))) {
                        // then that is indeed our en passant pawn!
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        // If the colors of the two pieces are different!
                        if(this.pieceColor != pieceOnCandidate.getPieceColor()) {
                            // We perform an en passant attack move, where we attack diagonally and pass the pawn/capture it!
                            legalActions.add(new PawnEnPassantAttackMove(board, this,
                                    moveDestinationCoordinate, pieceOnCandidate));
                        }
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
     * @return new Pawn(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
     * This returns a new object amongst the piece subclasses, and it passes in the new position along with
     * the color
     */
    @Override
    public Pawn movePiece(Action action) {
        return new Pawn(action.getDestinationTile(), action.getMovedPiece().getPieceColor());
    }

    /**
     * When this piece needs to be printed as part of our board, we will use the enumerator, and specify pawn, so it
     * prints "P"
     * @return PieceType.PAWN.toString();
     */
    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    /**
     * So when a pawn is promoted, we change it to another piece, for promotion! ALWAYS a queen, never underpromoting,
     * even though it's possible!
     */
    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.pieceColor, false);
    }
}
