/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */


package com.chess.player;

import com.chess.Color;
import com.chess.board.Action;
import com.chess.board.Board;
import com.chess.pieces.King;
import com.chess.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is an abstract base class, which serves as a superclass to our whitePlayer class and blackPlayer class.
 * In this class, we will keep track of the board, important pieces, like the king, and all possible legal moves available
 * (in general) to a player.
 */
public abstract class Player {

    protected final Board board; // this is our current game board
    protected final King playersKing; // this is the players most important piece, the king
    protected final Collection<Action> legalActions; // these are all legal plays/moves for a player
    private final boolean isInCheck; // field for if we're in check

    /**
     * Here this is our simple, generic base class constructor. In here, we have the current game board, so we can keep
     * track of the game and update the players as needed. Then we have the legalActions, which are the moves the player
     * can make, and the opponentsActions, which are the moves the opponent can make!
     * @param board,           this is an object of the board class that portrays the current board
     * @param legalActions,    this is a collection of the current legal moves for the player based on the board
     * @param opponentActions, this is a collection of the current legal moves for the opponent based on the board
     */
    Player(final Board board, final Collection<Action> legalActions,
           final Collection<Action> opponentActions) {

        this.board = board;
        this.playersKing = establishKing(); // This just initializes our king to a king, if one exists for the player!
        this.legalActions = ImmutableList.copyOf(Iterables.concat(legalActions, calculateKingCastles(legalActions, opponentActions)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playersKing.getPiecePosition(), opponentActions).isEmpty();
        // What we're really saying here is we will have a method that looks at the position of the playersKing, and the
        // opponents moves. If we have some collection that we return, after looking at the opponents moves, and it's empty,
        // meaning there are no attacks on the king/tile, then isInCheck = false, else true.

    }

    /**
     * This simply returns the players king
     * @return this.playersKing;
     */
    public King getPlayersKing() {
        return this.playersKing;
    }

    /**
     * This returns the legal moves for the player
     * @return this.legalActions;
     */
    public Collection<Action> getLegalActions() {
        return this.legalActions;
    }

    /**
     * So, how this method works is it simply takes in a tiles position (for the player), and an opponent's moves. Next, we
     * create a List of the Action class called attackMoves. From there, we iterate through every action in the opponent moves,
     * and if the position of the piece is the destination of one of these attacks, we add it to the attackMoves list. Finally,
     * return that list. This is good to see if we are in check, because we can give the player's position, and opponents
     * moves, and see if the returned list is empty or not.
     * @param piecePosition, piece's position (for player)
     * @param opponentActions, all of opponent's moves
     * @return ImmutableList.copyOf(attackMoves); an immutable copy of the list with opponents attackMoves
     */
    protected static Collection<Action> calculateAttacksOnTile(int piecePosition, Collection<Action> opponentActions) {
        final List<Action> attackMoves = new ArrayList<>();
        for (final Action action : opponentActions) {
            if (piecePosition == action.getDestinationTile()) {
                attackMoves.add(action);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    /**
     * To calculate whether the king can escape, we're going to go through each of the players legal moves. Then, we
     * are going to have an object of our ActionTransition class. We initialize this transition, by taking the action,
     * and performing it. Then, we are going to take the transition object, and get its status, after the action. If the
     * Move Status, which comes from our enum is "Done" that means we can escape, because the moves was successful, and
     * the board can be changed to save our king, hence we're not in checkmate.
     * @return true if the move is done, meaning it was made, else we return false.
     */
    private boolean canEscape() {
        for(final Action action : this.legalActions) {
            final ActionTransition transition = makeAction(action);
            if(transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    /**
     * All this method does is ensure that the player has a King piece on the board. If they do the game continues,
     * else we know that the player has been defeated and the game is over. We use a for loop, then through the
     * getActivePieces method, implemented by both our subclasses for the two pieces, we can look through every piece!
     * NOTE: The getActivePieces method calls a method in the board class that returns a Collection of current pieces on
     * the board. Then, from there, we check the piece type! All our pieces have a pieceType, and in our piece class, we
     * there's a method that returns it. If it is a king according to isKing(), then we simply return the king, indicating
     * the player is alive. Our enum all have an isKing() method for every pieceType, and if false, then we know it's not a
     * king and vice versa!
     * @return (King) piece; so if our piece is a king, we simply return the King piece!
     */
    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board!");
        // This means the king doesn't exist, so the board is invalid!
    }

    /**
     * All this class is doing is looking at the player, then it takes in an Action class object (a single move) as a parameter,
     * and using this object, it checks if the legalActions Collections contains said move. This is checking for the
     * player not for the opponent because we don't use the opponent's collection!
     * @param action, an object representing a move
     * @return this.legalActions.contains(action);
     */
    public boolean isMoveLegal(final Action action) {
        return this.legalActions.contains(action);
    }

    /**
     * Simply lets us know if we are in check!
     * @return this.isInCheck;
     */
    public boolean isInCheck() {
        return this.isInCheck;
    }

    /**
     * This will return true if we are in check, and we can't escape!
     * @return this.isInCheck && !canEscape();
     */
    public boolean isInCheckMate() {
        return this.isInCheck && !canEscape();
    }

    /**
     * What stalemate is, is when you're not in check, but can't escape as well
     * @return !this.isInCheck && !canEscape();
     */
    public boolean isInStaleMate() {
        return !this.isInCheck && !canEscape();
    }

    public boolean isCastled() {
        return false;
    }

    /**
     *
     * @return
     */
    public ActionTransition makeAction(final Action action) {
        if(!isMoveLegal(action)) {
            return new ActionTransition(this.board, action, MoveStatus.ILLEGAL_MOVE);
            // Since the move is illegal, we keep the same board, so we pass the same board, the action, and the status
        }

        final Board transitionBoard = action.execute();
        // When we make a move that's legal, it will execute the move, and we use this to initialize
        // an object of the board class known as the transition board. This is basically our new board

        /**
         * All this does is it checks for an attack on a tile. Specifically, it looks at a current player's King's position,
         * from the opponents point of view after a move has been made. Then, it will analyze and see if there are any
         * attacks on the king that can be made, and if there are some attacks, we will add it to the collection.
         */
        final Collection<Action> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayersKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalActions());

        // If the kingAttacks collection isn't empty, then we don't want to make a move, because we don't want ro expose the king
        // to check, so we return the same board, but with a new status!
        if(!kingAttacks.isEmpty()) {
            return new ActionTransition(this.board, action, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        // Finally, if there was nothing to worry about, and king won't be exposed to check, then we can make the move,
        // and pass in a new board.
        return new ActionTransition(transitionBoard, action, MoveStatus.DONE);
    }

    // This is simply an abstract method that will return the players active pieces!
    public abstract Collection<Piece> getActivePieces();

    // Simply gives us the pieces color!
    public abstract Color getColor();

    // This simply returns our opponents pieces, so we can analyze and make a move!
    public abstract Player getOpponent();

    // We will use this to see if any castling moves are possible for the King!
    protected abstract Collection<Action> calculateKingCastles(Collection<Action> playerLegals, Collection<Action> opponentLegals);

}
