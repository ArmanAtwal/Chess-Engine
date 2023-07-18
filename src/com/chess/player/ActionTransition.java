/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */


package com.chess.player;
import com.chess.board.Action;
import com.chess.board.Board;

/**
 * So, this is a very crucial class! What this class represents is that when we make a move, we change the board.
 * We obviously need to update the board and carry over the information to create the next form of the board, so
 * that is exactly what we do in this class.
 */
public class ActionTransition {

    private final Board nextBoard; // This is the next board after we make an action!
    private final Action action; // This is the action we take to reach the next board!
    private final MoveStatus moveStatus;
    // This lets us know if we can actually make the move,
    // or we can't because we may be in checkmate, or it's not a legal move, etc.
    // This is crucial because when using the makeAction method, we will only return the transformation for the next board
    // if the move can be made, of course.

    /**
     * This constructor simply initializes our nextBoard field to the most recent board that was created. Then, we have
     * the action which will be taken to create the newer board and will update this field! Finally, we have the
     * move status, which determines if we can transform the board (based on the action)
     * @param nextBoard; this starts as the current board that was created, then gets updated once we transform based on
     * action, if possible
     * @param action; action to transform the board
     * @param moveStatus; this is if we can perform the action
     */
    public ActionTransition(final Board nextBoard, Action action, MoveStatus moveStatus) {
        this.nextBoard = nextBoard;
        this.action = action;
        this.moveStatus = moveStatus;

    }

    /**
     * This returns our move status, which is in our Move Status enum. When we make an action, this updates the board,
     * and our moveStatus for the board, letting us know if our movve was successful or not!
     * @return this.moveStatus;
     */
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    /**
     * Returns new board!
     * @return this.nextBoard;
     */
    public Board getBoard() {
        return this.nextBoard;
    }
}
