/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */
package com.chess.player;

/**
 * This enum is responsible for determining the eligibility of our move before transforming the board!
 */
public enum MoveStatus {

    /**
     * If we are "DONE" that mean's a move was a success, so we return true!
     */
    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },

    /**
     * For an illegal move, isDone() returns false, because the move was never done
     */
    ILLEGAL_MOVE {
      @Override
      public boolean isDone() {
          return false;
      }
  },

    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    /**
     * This is a method to tell us if our MoveStatus, is done, meaning we have made the move/action.
     * @return boolean; true if we can move, false otherwise
     */
    public abstract boolean isDone();
}
