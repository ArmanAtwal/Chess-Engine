/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.board;

import com.chess.board.Board.Builder;
import com.chess.pieces.Pawn;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;

/**
 * So, this class is simply our move or action class, and what it does is allow us to program the moves, as in what they
 * do to our members, and then add them into a set or list for each piece. Logically, we must keep track of
 * board positions, so we know what the board looks like, the moved piece (so we know what it is), and its destination,
 * so we can adjust the board for next turn.
 */
public abstract class Action {
    protected final Board board; // Keep track of board image
    protected final Piece movedPiece; // Keep track of piece that moved
    protected final int destination; // Keep track of destination, so you can see how board changes
    protected final boolean isFirstMove; // True if first move, else false

    public static final Action NULL_MOVE = new NullAction();
    // Our null move field will just be an instance of the null action class, ensuring no action exists

    // Simple generic constructor
    private Action(final Board board, final Piece movedPiece, final int destinationSpot) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destination = destinationSpot;
        this.isFirstMove = movedPiece.isFirstMove(); // Lets us know if for the moved piece this is the first move
    }

    /**
     * This is to refactor our null move class
     */
    private Action(final Board board, final int destination) {
        this.board = board;
        this.destination = destination;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    // simple pattern following
    @Override
    public int hashCode() {
        final int num = 31;
        int result = 1;

        result = num * result + this.destination;
        result = num * result + this.movedPiece.hashCode();
        result = num * result + this.movedPiece.getPiecePosition();
        return result;
    }

    // simple pattern following
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Action)) {
            return false;
        }
        final Action otherAction = (Action) other;
        return  getCurrentCoordinate() == otherAction.getCurrentCoordinate() &&
                getDestinationTile() == otherAction.getDestinationTile() &&
                getMovedPiece().equals(otherAction.getAttackedPiece());
    }

    /**
     * Simply returns our current game board!
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * All this does is simply give us the current coordinate of the piece that is going to be moved
     *
     * @return this.getMovedPiece().getPiecePosition();
     */
    public int getCurrentCoordinate() {
        if(this.getMovedPiece() != null) {
            return this.getMovedPiece().getPiecePosition();
        }
        return -1;
    }

    /**
     * Simply returns our tile destination
     *
     * @return this.destination;
     */
    public int getDestinationTile() {
        return this.destination;
    }

    /**
     * All this does is return our movedPiece
     *
     * @return this.movedPiece;
     */
    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    /**
     * Simple helper method indicating if there is an attack move
     *
     * @return false by default
     */
    public boolean isAttack() {
        return false;
    }

    /**
     * Simple helper method indicating if it's a castling move
     *
     * @return false by default
     */
    public boolean isCastlingMove() {
        return false;
    }

    /**
     * This simply returns the piece that was attacked
     *
     * @return null; by default
     */
    public Piece getAttackedPiece() {
        return null;
    }

    /**
     * How this moves works is it executes a move, and it doesn't exactly modify the pre-existing board,
     * but it returns an entirely new board, where pieces have materialized and dematerialized!
     *
     * @return builder.build(); which builds our new board after a move!
     */
    public Board execute() {
        final Board.Builder builder = new Builder();
        // We use a board builder to materialize a new board to return

        // We're going to traverse through the incoming board's current player's pieces
        // AND for all the pieces that aren't the moved piece, we are just going to leave the piece be
        // on the new piece
       this.board.currentPlayer().getActivePieces().stream().filter(piece ->
                !this.movedPiece.equals(piece)).forEach(builder::setPiece);
        // Here, we do the same thing for the enemies pieces, but the enemy can't make a move
        // during the players turn, so all enemies pieces are materialized on the new board in their
        // same spot
        this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
        // Here, the one piece that moves, we are going to move via the setPiece call, so it's ready on the new board
        builder.setPiece(this.movedPiece.movePiece(this));
        // Here we set the move to the opponent, by getting the current player's opponent color
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());

        // Builds new board
        return builder.build();
    }

    /**
     * This class is to program attacks for the major pieces when they make an attack move
     */
    public static class ImportantAttackMove extends AttackMove {

        /**
         * This is a simple constructor to establish the super class and any fields
         */
        public ImportantAttackMove(final Board board,
                                   final Piece piece,
                                   final int destination,
                                   final Piece pieceAttacked) {
            super(board, piece, destination, pieceAttacked);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof ImportantAttackMove && super.equals(other);
        }

        /**
         * This method returns the pieceType and attack location as a string for our game history log
         */
        @Override
        public String toString() {
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destination);
        }
    }

    /**
     * This class is used to store moves characterized as important, such as just a simple movement. We need the board
     * image, the moving piece, and its destination, so that way we can store this as a legal move, and account for
     * every piece of info needed when playing.
     */
    public static final class ImportantMove extends Action {
        public ImportantMove(final Board board, final Piece movedPiece, final int destinationSpot) {
            super(board, movedPiece, destinationSpot);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof ImportantMove && super.equals(other);
        }

        /**
         * This is the toString method, that whenever we make a normal, non-attacking move, we create a string that
         * is going to be the concatenation of the pieceType and its destination coordinate!
         * @return
         */
        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destination);
        }

    }

    /**
     * This class is used to store moves like attacks. We need the board image, the moving piece, and its destination,
     * so that way we can store this as a legal move, and account for every piece of info needed when playing. We also
     * need the attacked piece so we can store it off to the side (this would be the piece at the current spot).
     */
    public static class AttackMove extends Action {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece movedPiece, final int destinationSpot,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationSpot);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        /**
         * Simply override isAttack and return true, because that is the case here
         *
         * @return true;
         */
        @Override
        public boolean isAttack() {
            return true;
        }

        /**
         * Since this is an attack method, here we're returning the attacked piece.
         *
         * @return this.attackedPiece;
         */
        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    /**
     * This class is going to be used to program basic non-attack pawn movements.
     */
    public static final class PawnMove extends Action {
        public PawnMove(final Board board, final Piece movedPiece, final int destinationSpot) {
            super(board, movedPiece, destinationSpot);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        /**
         * Converts a simple pawn move into String format for our game history to print out
         * @return
         */
        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destination);
        }
    }

    /**
     * This class will program pawn attack movements, hence being an extension of the attack move class
     */
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationSpot,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationSpot, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        /**
         * This method simply uses PGN convention to correctly print out any attacks on a chess piece by a pawn
         * (in our game history)!
         * @return
         */
        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destination);
        }

    }

    /**
     * This class will program the special move a pawn has
     */
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationSpot,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationSpot, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        /**
         * The below piece of code is responsible in programming our EnPassant Attack move!
         * @return builder.build(); which constructs our new board!
         */
        @Override
        public Board execute() {
            final Builder builder = new Builder(); // New builder object
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) { // Look at current player's pieces
                if(!this.movedPiece.equals(piece)) { // If piece isn't moved piece
                    builder.setPiece(piece); // Set on new board
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) { // Look at opponent's current pieces
                if(!piece.equals((this.getAttackedPiece()))) { // If piece isn't the one being attacked, set it
                    builder.setPiece(piece); // Set on new board
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this)); // Move the moved piece and set it
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor()); // Switch turns
            return builder.build(); // Construct new board
        }
    }

    /**
     * This class is responsible for implementing our pawn promotion, when it occurs!
     */
    public static class PawnPromotion extends Action {

        final Action decoratedAction; // The action that results in a pawn promotion
        final Pawn promotoedPawn; // The pawn that is being promoted

        /**
         * In this constructor, we simply pass in an action that causes promotion, however, this action contains the board,
         * moved piece, and destination tile, because we wrap another class when passing in the action!
         * @param decoratedAction, the action causing promotion!
         */
        public PawnPromotion(final Action decoratedAction) {
            super(decoratedAction.getBoard(), decoratedAction.getMovedPiece(), decoratedAction.getDestinationTile());
            this.decoratedAction = decoratedAction;
            this.promotoedPawn = (Pawn) decoratedAction.getMovedPiece();
        }

        /**
         * This is the method we will utilize when we want to execute a pawn promotion!
         */
        @Override
        public Board execute() {

            final Board pawnMoveBoard = this.decoratedAction.execute();
            // So our board after the pawn promotion has to execute the pawn promoting action; thus we store this board!
            final Board.Builder builder = new Builder(); // Board builder object
            for(final Piece piece : pawnMoveBoard.currentPlayer().getActivePieces()) { // Loop through all remaining pieces for player
                if(!this.promotoedPawn.equals(piece)) { // If the piece doesn't equal the promoted one
                    builder.setPiece(piece); // then just set the piece
                }
            }
            for(final Piece piece : pawnMoveBoard.currentPlayer().getOpponent().getActivePieces()) { // Loop through opponent pieces
                builder.setPiece(piece); // Set all pieces
            }
            builder.setPiece(this.promotoedPawn.getPromotionPiece().movePiece(this)); // Promote the pawn into a piece of the player's choice
            // Always a queen for sake of simplicity!
            builder.setMoveMaker(pawnMoveBoard.currentPlayer().getColor()); // Set turn/move back to the current player
            return builder.build(); // build board
        }

        /**
         * Lets us know if the action was an attack!
         */
        @Override
        public boolean isAttack() {
            return this.decoratedAction.isAttack();
        }

        /**
         * If an attack occurs, to ensure in promotion, we must get the attacked piece
         */
        @Override
        public Piece getAttackedPiece() {
            return this.decoratedAction.getAttackedPiece();
        }

        /**
         * Whenever a pawn promotion occurs, this is what will appear in the game move history!
         */
        @Override
        public String toString() {
            return "";
        }

        /**
         * Hashing method
         */
        @Override
        public int hashCode() {
            return decoratedAction.hashCode() + (31 * promotoedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }
    }

    /**
     * This will program the pawn jump, where a pawn can jump 2 squares its first turn, as long as there is no blocking
     */
    public static final class PawnJump extends Action {
        public PawnJump(final Board board, final Piece movedPiece, final int destinationSpot) {
            super(board, movedPiece, destinationSpot);
        }

        /**
         * So, what we're doing in this method is we create a new builder object! Then, we use a loop to iterate through
         * all possible active pieces. If the moved piece doesn't equal the current piece we're on, we skip it and continue
         * iterating. If it does, we make sure the builder creates a new piece for us to put on the new, updated board.
         * Then, we iterate once more but make sure to set all the opponent's pieces on the new board. Finally, we create a pawn
         * object, and since we are in the pawn jump class, the only way we can perform execute is if the pawn jumped! Therefore,
         * we make sure to update the pawn position on a new board, and now that the pawn has performed a jump, it's a possible
         * candidate for enPassant, so we set that too. Finally, we hand over the turn to the other player and return the
         * newly created board.
         *
         * @return builder.build();
         */
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            this.board.currentPlayer().getActivePieces().stream().filter(piece ->
                    !this.movedPiece.equals(piece)).forEach(builder::setPiece);
            this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassant(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(destination);
        }

    }

    /**
     * This is a general abstract class to program a CastleMove in chess
     */
    static abstract class CastleMove extends Action {

        protected final Rook castleRook; // Rook for castling
        protected final int castleRookStart; // Tile ID for castle rook
        protected final int castleRookDestination; // Tile ID for where castle rook is going to be

        public CastleMove(final Board board, final Piece movedPiece, final int destinationSpot,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationSpot);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        /**
         * This method simply returns our rook crucial for castle building
         *
         * @return this.castleRook;
         */
        public Rook getCastleRook() {
            return this.castleRook;
        }

        /**
         * If the move is of castling type, we will return true
         *
         * @return true;
         */
        public boolean isCastlingMove() {
            return true;
        }

        /**
         * So, the purpose of this method is for us to be able to execute castling! First, we get all active pieces
         * for the current player! Then, if the piece we're looking at in our loop doesn't equal the moved piece, and
         * the castleRook doesn't equal the piece (meaning the castle rook had to be the moved piece) everything else is
         * set as is on the new board. Then, what we're going to do is set the other players pieces as is, on the new board.
         * Finally, we're going to move both the king, and the rook for the purpose of castling. Finally, switch the turn.
         * @return builder.build(); which constructs a new board!
         */
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!(this.movedPiece.equals(piece)) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceColor(), false));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getColor());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int num = 31;
            int result = super.hashCode();
            result = num * result + this.castleRook.hashCode();
            result = num * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove)other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    /**
     * This is a castleMove for the king, hence an extension of CastleMove
     */
    public static final class KingCastleMove extends CastleMove {
        public KingCastleMove(final Board board, final Piece movedPiece, final int destinationSpot,
                              final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationSpot, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
                return this == other || other instanceof KingCastleMove && super.equals(other);
        }

        /**
         * This prints the king's side castle
         * @return "0-0", convention in PGN for king's side castle
         */
        @Override
        public String toString() {
            return "0-0";
        }

    }

    /**
     * This is a castleMove for the queen, hence an extension of CastleMove
     */
    public static final class QueenCastleMove extends CastleMove {
        public QueenCastleMove(final Board board, final Piece movedPiece, final int destinationSpot,
                               final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationSpot, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof QueenCastleMove && super.equals(other);
        }

        /**
         * This prints the queen's side castle
         * @return "0-0-0", convention in PGN for queen's side castle
         */
        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    /**
     * This class simply represents the null actions, meaning if no move or action can be made
     * we would use this
     */
    public static final class NullAction extends Action {
        public NullAction() {
            super(null, -1);
        }

        /**
         * This throws a runtime exception because we can't execute a move that doesn't exist or is null. This is
         * not a possible move
         *
         * @return
         */
        @Override
        public Board execute() {
            throw new RuntimeException("Can't execute the null move!");
            // this prevents the execution of a null move!
        }
    }

    /**
     * This class will have many methods for us to be able to move track movements and avoid having to focus on
     * object specific details, hence why it's in a factory
     */
    public static class MoveFactory {
        // Not instantiable
        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        /**
         * SO, given a board, with a from and to coordinate, we want to return the move that is available on the board.
         * How this works is we go through a loop, and inside that loop we use an Action class object to run through all the
         * legal moves. Then, if the current coordinate of the piece in the move and the destination coordinate of the piece in
         * the move equal the parameters, we can return the action. If nothing can be returned, we say it was a null move.
         *
         * @param board,                 this is the game board we are playing on
         * @param currentCoordinate,     the starting coordinate
         * @param destinationCoordinate, the ending coordinate
         * @return action (if found) or NULL_MOVE meaning no action can be taken!
         */
        public static Action createAction(final Board board, final int currentCoordinate,
                                          final int destinationCoordinate) {
            for (final Action action : board.getAllLegalActions()) {
                if (action.getCurrentCoordinate() == currentCoordinate &&
                        action.getDestinationTile() == destinationCoordinate) {
                    return action;
                }
            }
            return NULL_MOVE;
        }
    }
}
