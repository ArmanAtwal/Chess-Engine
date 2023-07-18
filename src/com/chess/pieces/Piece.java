/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.pieces;
/**
 * Above we establish a logic package, in which we will store the class that represents our pieces
 */

import com.chess.Color;
/**
 * This contains the enumerator in our package, and determines alliances during the game
 */

import com.chess.board.Action;
import com.chess.board.Board;

import java.util.Collection;

/**
 * This is a basic class describing the pieces on a chess board and their possible actions
 */
public abstract class Piece {
    protected final int piecePosition; // Every piece has a placement at some point, that changes during the game
    protected final Color pieceColor; // Each piece also takes a side, whether black or white
    protected final boolean isFirstMove; // Boolean that lets us know if it's the pieces first move
    protected final PieceType pieceType; // This is the pieceType of our piece, based on the ENUM we have made!
    private final int cachedHashCode; // Our modified hashCode from overriding

    Piece(final PieceType pieceType, final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceColor = pieceColor;
        this. isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    /**
     * Computes the hash code for this piece object.
     * The hash code is calculated by combining the hash codes of the piece's type, color, position,
     * and isFirstMove flag using a common algorithm. The resulting hash code is returned as an integer.
     * @return result;
     */
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceColor.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    /**
     * Here we are overriding the equals method to create a proper test for how to compare pieces to on another
     * and see if they're the same
     */
    @Override
    public boolean equals(final Object other) {
        if(this == other) { // If they refer to the same object, then object equality is true!
            return true;
        }
        if(!(other instanceof Piece)) { // If object isn't instance of piece, return false
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceColor == otherPiece.getPieceColor() && isFirstMove == otherPiece.isFirstMove();
    }

    /**
     * We simply return the field, after computing it from using our helper method
     * @return this.cachedHashCode;
     */
    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    /**
     * So, we need a method to determine the legal moves for a piece, based on its position in the board, and obviously
     * the piece type. Through this method we can return a list or set of moves. We will override this method in every
     * subclass.
     *
     * @param board is the current state of the board, according to the board class, allowing us to determine the moves.
     * @return Collection<Action>, which returns a set of all possible actions
     */
    public abstract Collection<Action> numLegalMovesPerPiece(final Board board);

    /**
     * This method tells us what side a piece is on
     * @ this.pieceColor, simply indicating the side the piece is on!
     */
    public Color getPieceColor() {
        return this.pieceColor;
    }

    /**
     * This method informs us on whether this is a first move for a piece
     * @return this.isFirstMove, which is our boolean field that has been initialized to something
     */
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    /**
     * All this method does is return the piece position
     * @return this.piecePosition; our piece position
     */
    public int getPiecePosition() {
        return this.piecePosition;
    }

    /**
     * All this method does is return our piece type!
     * @return this.pieceType;
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * This method simply returns the pieceType (determining that pieces' strength) from the PieceType enum
     * @return this.pieceType.getPieceValue();
     */
    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    /**
     * This method is going to take in a move, and apply it to an existing piece, and then create a new piece,
     * that is the exact same type of piece, but with a different position!
     * @return Piece
     */
    public abstract Piece movePiece(Action action);

    /**
     * Essentially, we are creating an enumerator, and in this enumerator we assign every piece type with a
     * short string that represents it, simplifying the printing process.
     */
    public enum PieceType {
        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false; // Not a king so false
            }

            @Override
            public boolean isRook() {
                return false;
            }
        }, // Pawn print
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false; // Not a king so false
            }

            @Override
            public boolean isRook() {
                return false;
            }
        }, // Knight print
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false; // Not a king so false
            }

            @Override
            public boolean isRook() {
                return false;
            }
        }, // Bishop print
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false; // Not a king so false
            }

            @Override
            public boolean isRook() {
                return true;
            }
        }, // Rook print
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false; // Not a king so false
            }

            @Override
            public boolean isRook() {
                return false;
            }
        }, // Queen print
        KING("K", 10000) {
            @Override
            public boolean isKing() {
                return true; // Is a king so true!!
            }

            @Override
            public boolean isRook() {
                return false;
            }
        }; // King print

        private String pieceName; // pieceName field representing the piece
        private int pieceValue; // piece strength

        /**
         * Constructor for enumerator, where we make the enum values pieceName a specific value, based on a
         * String that is passed
         * @param pieceName, representation of the piece
         */
        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        /**
         * This is a toString method that returns a String for the piece, therefore representing the piece
         * @return this.pieceName; which is the representation discussed earlier
         */
        @Override
        public String toString() {
            return this.pieceName;
        }

        /**
         * This simply is the pieceValue for a piece (strength), and we return it
         * @return this.pieceValue;
         */
        public int getPieceValue() {
            return this.pieceValue;
        }

        /**
         * All this method does is for every piece type we have in our enum, let us know if it is a King or not!
         * @return boolean; true if king, else false
         */
        public abstract boolean isKing();

        /**
         * All this method does is for every piece type we have in our enum, let us know if it is a Rook or not!
         * @return boolean; true if rook, else false
         */
        public abstract boolean isRook();

    }

}
