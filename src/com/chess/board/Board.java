/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.board;

import com.chess.Color;
import com.chess.pieces.*;
import com.chess.player.BlackPlayer;
import com.chess.player.Player;
import com.chess.player.WhitePlayer;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

/**
 * In this class, we are simply describing the board as it changes throughout the game,
 * so we can make the correct moves. We also implement a builder pattern, to do so.
 */
public class Board {

    private final List<ChessTile> gameBoard;
    // This is simply a list which represents our game board

    // We will now keep track of the pieces as well
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    // These are objects of our two subclasses, that belong to the player class, and will allow us to keep track
    // of each player's moves, pieces, etc.
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer; // This is the current player in the game
    private final Pawn enPassantPawn; // Our en passant pawn!


    /**
     * This is our constructor for the board class, and we pass a builder object into it. What we do is we take our
     * list, which is the game board, and equal it to the method that designs the game board, using a builder. Then,
     * we have two collections, one for white pieces, and one for black pieces. In this, we will look at our gameBoard,
     * and for pieces of each color, we will determine which ones still remain.
     * @param builder, this is an object of the builder class
     */
    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Color.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Color.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Action> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Action> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);
        // These two Collections are going to store our standard legal moves for the white pieces and black pieces
        // based on the current pieces available, which is stored in the whitePieces and blackPieces Collections.

        // The purpose of this is to initialize our fields! We pass in the white and black legal moves, on our
        // current board (this parameter) and this shows the white player what they can do and the
        // black player what they can do!
        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMove.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    /**
     * So, what this method does is it will calculate the legal moves for all white or black pieces. How this works is
     * we iterate through the parameter, which is a collection of active pieces, and then we use the legal moves calculator
     * that we made in every piece's class (bishop, rook, king, etc.). This method takes in the current board, observes
     * it, and then returns a possible collection of moves (for that piece). Thus, we will capture this inside a list,
     * using the addAll method. Finally, we return this list, that contains all possible moves, for every piece of a
     * certain color, based on the board's layout.
     * @param pieces is simply a list of all active pieces
     * @return ImmutableList.copyOf(legalMoves); this returns a list of all possible moves for all pieces on the board,
     * based on board layout and piece color.
     */
    private Collection<Action> calculateLegalMoves(final Collection<Piece> pieces) {

        final List<Action> legalMoves = new ArrayList<>();

        for(final Piece piece : pieces) {
            legalMoves.addAll(piece.numLegalMovesPerPiece(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    /**
     * The main purpose of this method is for us to simply see how our board would look when it's printed out, hence
     * why we're overriding toString().
     * @return builder.toString(); this simply returns our board as string
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(); // String builder object
        for(int i = 0; i < BoardUtils.NUM_TILES; i++) { // Iterating through all 64 tiles
            final String tileText = this.gameBoard.get(i).toString();
            // Here, we use the overridden toString methods in our ChessTile class to turn the tile into a string for printing
            builder.append(String.format("%3s", tileText)); // We append the String to SB, and format it properly
            if((i +1) % BoardUtils.TILES_PER_ROW == 0) { // Once we have reached the end of a row we need a new line
                builder.append("\n");
            }
        }
        return builder.toString(); // This returns our board as a string
    }

    /**
     * Getter for our en passant pawn
     */
    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    /**
     * All this method does is return our opponent player, the whitePlayer in this case
     * @return this.whitePlayer;
     */
    public Player whitePlayer() {
        return this.whitePlayer;
    }

    /**
     * All this method does is return our opponent player, the blackPlayer in this case
     * @return this.blackPlayer;
     */
    public Player blackPlayer() {
        return this.blackPlayer;
    }

    /**
     * All this method does is return who has the current turn/who is currently playing
     * @return this.currentPlayer;
     */
    public Player currentPlayer() {
        return this.currentPlayer;
    }

    /**
     * All this method does is return to us all the whitePieces that we were keeping track of on our board!
     * This is useful in our getActivePieces() method in the BlackPlayer class, because then we can observe
     * all the pieces and determine if we have a king left!
     * @return this.blackPieces; These are the blackPieces!
     */
    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    /**
     * All this method does is return to us all the whitePieces that we were keeping track of on our board!
     * This is useful in our getActivePieces() method in the WhitePlayer class, because then we can observe
     * all the pieces and determine if we have a king left!
     * @return this.whitePieces; These are the whitePieces!
     */
    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }


    /**
     * So, in this method, what we do is we pass in the current gameBoard, and a color (either black or white). Then,
     * from there, we make a list called activePieces, which we will initialize. We use a loop to iterate through our
     * tiles in the gameBoard, and then check if they're occupied. If so, we want to get the piece on the tile, and if
     * that piece matches the color whose pieces we are looking for, then we will add it to the activePieces list.
     * Finally, we return an immutable copy of the list.
     * @param gameBoard is our list with the current tiles and their properties
     * @param color is the color of our piece
     * @return ImmutableList.copyOf(activePieces); returns an immutable list of the activePieces, to initialize our fields.
     */
    private static Collection<Piece> calculateActivePieces(final List<ChessTile> gameBoard, final Color color) {
        final List<Piece> activePieces = new ArrayList<>();

        for(final ChessTile tile : gameBoard) {
            if(tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();
                if(piece.getPieceColor() == color) {
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    /**
     * The purpose of the get tile method is for us to be able to return a spot on our board, given a coordinate.
     * Using this, we can determine the action to take for a chess piece, by analyzing what is at that returned spot.
     * @param tileCoordinate; this parameter is a spot on our board, and from this spot we will return an object, which
     * is a tile, and this tile has a lot of useful information.
     * @return gameBoard.get(tileCoordinate); Our gameBoard list is updated by the creation method, so we use the
     * tileCoordinate field to access a tile in that list, and can therefore gather info about it.
     */
    public ChessTile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    /**
     * What happens in this method is that we are going to create our game board. We need to return a list, because
     * we initialize our gameBoard field with this method. We first need to pass an object of the builder class because
     * that will help us create our board. Then, what we do is make an array of tiles, 64 tiles long. We then iterate
     * through every tile, and during the body of the loop, we create tiles for the current tile we are at (these tiles
     * don't exist yet). We must use the ChessTile class' createTile method. Here we pass the current tile we're at,
     * then using the boardConfig map in the Builder class, to get the piece at the tile, successfully making our game
     * board (after completely iterating).
     * @param builder, an object of the builder class
     * @return ImmutableList.copyOf(tiles); which is an immutable list of our current game board.
     */
    private static List<ChessTile> createGameBoard(final Builder builder) {
        final ChessTile tiles[] = new ChessTile[BoardUtils.NUM_TILES];
        for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles[i] = ChessTile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    /**
     * This method is going to be responsible for creating our simple, beginning, game board.
     * @return
     */
    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        // This is an object of the builder class

        /** Black Layout; here, what we will do is call the setPiece method using the builder class. In doing so, we
         * pass in a piece, and the setPiece method will put it as part of our map, representing the boardConfig (using
         * the position of the piece and the piece itself). We create our desired object, while passing it, and we include
         * the color, so we know alliance, along with the position on the board, represented as an int.
         *
         */
        builder.setPiece(new Rook(0, Color.BLACK));
        builder.setPiece(new Knight(1, Color.BLACK));
        builder.setPiece(new Bishop(2, Color.BLACK));
        builder.setPiece(new Queen(3, Color.BLACK));
        builder.setPiece(new King(4, Color.BLACK));
        builder.setPiece(new Bishop(5, Color.BLACK));
        builder.setPiece(new Knight(6, Color.BLACK));
        builder.setPiece(new Rook(7, Color.BLACK));
        builder.setPiece(new Pawn(8, Color.BLACK));
        builder.setPiece(new Pawn(9, Color.BLACK));
        builder.setPiece(new Pawn(10, Color.BLACK));
        builder.setPiece(new Pawn(11, Color.BLACK));
        builder.setPiece(new Pawn(12, Color.BLACK));
        builder.setPiece(new Pawn(13, Color.BLACK));
        builder.setPiece(new Pawn(14, Color.BLACK));
        builder.setPiece(new Pawn(15, Color.BLACK));

        /** White Layout; here, what we will do is call the setPiece method using the builder class. In doing so, we
         * pass in a piece, and the setPiece method will put it as part of our map, representing the boardConfig (using
         * the position of the piece and the piece itself). We create our desired object, while passing it, and we include
         * the color, so we know alliance, along with the position on the board, represented as an int.
         *
         */
        builder.setPiece(new Rook(56, Color.WHITE));
        builder.setPiece(new Knight(57, Color.WHITE));
        builder.setPiece(new Bishop(58, Color.WHITE));
        builder.setPiece(new Queen(59, Color.WHITE));
        builder.setPiece(new King(60, Color.WHITE));
        builder.setPiece(new Bishop(61, Color.WHITE));
        builder.setPiece(new Knight(62, Color.WHITE));
        builder.setPiece(new Rook(63, Color.WHITE));
        builder.setPiece(new Pawn(48, Color.WHITE));
        builder.setPiece(new Pawn(49, Color.WHITE));
        builder.setPiece(new Pawn(50, Color.WHITE));
        builder.setPiece(new Pawn(51, Color.WHITE));
        builder.setPiece(new Pawn(52, Color.WHITE));
        builder.setPiece(new Pawn(53, Color.WHITE));
        builder.setPiece(new Pawn(54, Color.WHITE));
        builder.setPiece(new Pawn(55, Color.WHITE));

        builder.setMoveMaker(Color.WHITE); // White always goes first!

        return builder.build(); // This will construct our board
    }

    /**
     * So, how this method works is we return an iterable of Action type, and we use on the methods from the guava library,
     * where we make not only an unmodifiable literal, but we concat two lists together; one being all legal actions for
     * white player and all legal actions for black player!
     * @return Iterables.unmodifiableIterable(Iterables.concat ( this.whitePlayer.getLegalActions (),
     *                 this.blackPlayer.getLegalActions()));
     */
    public Iterable<Action> getAllLegalActions() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalActions(),
                this.blackPlayer.getLegalActions()));
    }

    /**
     * This class is responsible for helping us build an instance of a board.
     */
    public static class Builder {
        Map<Integer, Piece> boardConfig;
        // This map contains our board configuration. All we do is pass an int for position and then the piece
        Color nextMove;
        // This variable has the color of whose turn it is. If black, it's black's turn etc. It changes as turns switch.
        Pawn enPassantPawn; // The pawn that can be possibly passed up in the en passant move

        /**
         * A simple constructor initializing our fields
         */
        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        /**
         * This method simply takes a piece as a parameter and sets its position on the board. We must put it in our
         * boardConfig map, using the put method. By using getPiecePosition, we know where on the board it lies, and we
         * already know what the piece is (so we are able to insert it into our map properly).
         * @param piece, this the piece on our game board
         * @return this, which is the Builder, so we can construct the board
         */
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        /**
         * We simply pass in the current color, White or Black. Then, our parameter is set to this color, and based
         * on the color in the parameter, we can determine which players turn it is.
         * @param nextMove, represents our color
         * @return this, which is the Builder, so we can construct the board
         */
        public Builder setMoveMaker(final Color nextMove) {
            this.nextMove = nextMove;
            return this;
        }

        /**
         * What this method is responsible for is simple creating or building an instance of the board using a
         * builder, which is why we pass in the "this" parameter during return.
         * @return new Board(this); Here we are returning an object of the board class, and we pass in the "this"
         * parameter (which is our builder) to create our game board.
         */
        public Board build() {
            return new Board(this);
        }

        /**
         * All this does is initialize our pawn if it is in the position to be attacked by a special pawn (en passant move)
         * @param enPassantPawn
         */
        public void setEnPassant(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
