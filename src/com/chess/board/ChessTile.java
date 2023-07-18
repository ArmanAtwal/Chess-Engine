/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.board;
/**
 * Above we establish a logic package, in which we will store the class that represents our board
 */

import com.chess.pieces.Piece;
/**
 * Here, we must import the Piece class, because it's in its own logic package, and we need
 * to use it.
 */

import com.google.common.collect.ImmutableMap;
/**
 * Here we import the ImmutableMap class from the guava library, allowing
 * us to have our immutable map
 */

import java.util.HashMap;
import java.util.Map;

/**
 * This abstract class is a basic beginner class that establishes our necessary methods,
 * like determining occupancy, and then determining piece if occupancy exists on a board. We have two
 * subclasses as there are two outcomes. A piece will or will not exist
 */
public abstract class ChessTile {

    /** This concept is immutability. We want this value to be
     constant for all objects, and thus we make it protected so only classes in the same package
     can access, and final, so it can't be changed.
     **/
    protected final int tileSpot;

    /**
     * Here we have a container or data structure known as a map, and in this map we are storing
     * objects of the NoPiece class, of integer type, into a map named EMPTY_TILES, using the
     * possibleEmptyTile() method. We do this, so it's easy to access all possible empty positions on
     * a chess board.
     */
    private static final Map<Integer, NoPiece> FREE_SPOTS_CACHE = possibleEmptyTile();

    /**
     * In this method, we assign all 64 values on a chess board (from 0-63), as objects,
     * to their respective, unique, positions in the Map, allowing for simple access.
     * @return ImmutableMap.copyOf(noPieceMap); Although initially confusing, if we are to return
     * just the hash map, it isn't immutable, as we can clear it. Thus, we return it using the
     * ImmutableMap class from the guava library, which we needed to download and then import
     * the class. (as that's the only way for us to have an immutable map).
     * We simply make a copy stored in the class.
     */
    private static Map<Integer, NoPiece> possibleEmptyTile() {

        final Map<Integer, NoPiece> noPieceMap = new HashMap<>();

        for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
            noPieceMap.put(i, new NoPiece(i));
        }
        return ImmutableMap.copyOf(noPieceMap);
    }

    /**
     * This method ensures that only existing tiles from 0-63 can be created, and no more than that. If our piece
     * is not null, then we create an object for the ExistingPiece class, else, it's empty, and we use our spot as a key
     * in our map to grab the object/value for the spot that's empty
     * @param tileSpot spot on board
     * @param piece our piece
     * @return either a new object that indicates piece and spot, or an object that's just a spot (from our map)
     */
    public static ChessTile createTile(final int tileSpot, final Piece piece) {
        return piece != null ? new ExistingPiece(tileSpot, piece) : FREE_SPOTS_CACHE.get(tileSpot);
    }
    private ChessTile(final int tileSpot) {
        this.tileSpot = tileSpot;
    } // Private to prevent the infinite construction of tiles, and we use the createTile method to limit it to 64

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    /**
     * Returns tile coordinate
     * @return this.tileSpot;
     */
    public int getTileCoordinate() {
        return this.tileSpot;
    }

    /**
     * This class represents the existence of No Piece, and extends our abstract class.
     * Thus, we can define our constructor and methods accordingly!
     */
    public static final class NoPiece extends ChessTile {
        private NoPiece(final int spot) { // Limited number of empty tiles
            super(spot);
        }

        /**
         * When printing an empty tile, we return a "-"
         * @return "-";
         */
        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isTileOccupied(){
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    /**
     * This class represents the existence of a Piece, and extends our abstract class.
     * Thus, we can define our constructor and methods accordingly!
     */
    public static class ExistingPiece extends ChessTile{
        private final Piece tilePiece; // Same concept; immutability
        private ExistingPiece(int spot, final Piece tilePiece) { // Limited number of existing tiles
            super(spot);
            this.tilePiece = tilePiece;
        }

        /**
         * All this method does is simply print tiles with black pieces on our board differently from those with white
         * pieces. We pass in a ChessTile object, and then see if the tile is occupied. If so, what we need to do is check
         * the piece color. If it's black, we print the chess tile in lower case, else we don't (indicating it is white).
         * @return getPiece().getPieceColor().isBlack() ? getPiece().toString().toLowerCase() :
         * getPiece().toString();
         */
        @Override
        public String toString() {
            return getPiece().getPieceColor().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }

        @Override
        public boolean isTileOccupied(){
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.tilePiece;
        }
    }
}
