/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.player.ai;

import com.chess.board.Action;
import com.chess.board.Board;
import com.chess.player.ActionTransition;

/**
 * This class is for our AI, where it takes in a given board and depth, and then it uses an algorithm to calculate
 * the best move to be made! This algorithm is minimax and although very complex, to simplify, it uses a binary
 * tree in which our levels alternate. Every odd level is the minimum and every even level is the maximum. Minimax is
 * used commonly to choose the best decision, resulting the smallest amount of loss (mini) and the most benefit (max).
 * On even levels, when choosing a child node, we want to choose the largest child node. On odd levels, when choosing a
 * child node, we want to choose the smallest child node! That's essentially how the algorithm works!
 */
public class MiniMax implements MoveStrat {

    private final BoardEvaluator boardEvaluator; // Evaluates the board!
    private final int searchDepth; // Depth of our movess

    /**
     * Basic constructor to initialize fields!
     */
    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    /**
     *
     */
    @Override
    public Action execute(Board board) {
        final long startTime = System.currentTimeMillis(); // Time at start
        Action bestAction = null; // What is the best move for our AI to make?
        int highestSeenValue = Integer.MIN_VALUE; // This is for the sake of initialization
        int lowestSeenValue = Integer.MAX_VALUE; // This is for the sake of initialization
        int currentValue; // Current Value we examine

        System.out.println(board.currentPlayer() + " Thinking with depth = " + this.searchDepth);

        int numMoves = board.currentPlayer().getLegalActions().size(); // Number of moves for player
        for(final Action action : board.currentPlayer().getLegalActions()) { // Iterate through the players entire possible moves
            final ActionTransition actionTransition = board.currentPlayer().makeAction(action); // Make the current action we're looking at
            if(actionTransition.getMoveStatus().isDone()) { // If move is successfully made
                // So, if the current player who was making the moves is white, then we want to minimize.
                // To make sense of this, if the number is larger, white is winning, and the number smaller,
                // black is winning (during evaluation by AI)! If white had made a move, we view from the opposing perspective,
                // as they want to minimize! But, if white didn't make a move, that means black did, so our opponent is white
                // and wants to maximize! Both scenarios ensure the player can try to win, based on alliance!
                currentValue = board.currentPlayer().getColor().isWhite() ? min(actionTransition.getBoard(), this.searchDepth -1)
                        : max(actionTransition.getBoard(), this.searchDepth - 1);
                if(board.currentPlayer().getColor().isWhite() && currentValue >= highestSeenValue) {
                    // So, if player is white, and current val is greater than highestSeenVal, from the moves
                    highestSeenValue = currentValue;
                    // Then we assign the highest seen value with the current value! This way, our player knows the
                    // maximizing move they can make.
                    bestAction = action; // Update player's best action based on color
                }
                else if(board.currentPlayer().getColor().isBlack() && currentValue <= lowestSeenValue) {
                    // So, if player is black, and current val is less than lowestSeenVal, from the moves
                    lowestSeenValue = currentValue;
                    // Then we assign the lowest seen value with the current value! This way, our player knows the
                    // minimizing move they can make.
                    bestAction = action; // Update player's best action based on color
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime; // How long it takes the AI
        return bestAction;
    }

    /**
     * BOTH OF THE METHODS WORK CO-RECURSIVELY SO OUR AI CAN RUN THROUGH MANY POSSIBLE GAME MOVE COMBINATIONS, UNTIL
     * THE BASE CASE STOPS IT FROM DOING SO (BECAUSE THERE WOULD BE TOO MANY) AND THEN FROM THERE THE AI CAN CHOOSE THE BEST
     * MOVE TO EXECUTE!
     */
    // Returns the lowest node value for the level we are on! Crucial to minimax algorithm
    public int min(final Board board, final int depth) {
        if(depth == 0 || isEndGameScenario(board)) { // If we are on the next iteration/recursive call of the algorithm or game is done
            return this.boardEvaluator.evaluate(board, depth); // Evaluate the board
        }

        int lowestSeenValue = Integer.MAX_VALUE; // We simply make our lowest value equal to the highest for the sake of initialization
        for(final Action action : board.currentPlayer().getLegalActions()) { // Get all possible moves the player can make
            final ActionTransition actionTransition = board.currentPlayer().makeAction(action); // make each move by iterating through them all
            if(actionTransition.getMoveStatus().isDone()) { // Once move is done
                final int currentValue = max(actionTransition.getBoard(), depth - 1);
                // We want to score the move! By scoring our AI can judge every move
                if(currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue; // Update lowest value
                }
            }
        }
        return lowestSeenValue;
        // Return the lowest value of all the legal moves! This helps construct parent nodes for the minimax algorithm
        // Only on odd levels
    }

    /**
     * Lets us know if the current player is in a game ending situation!
     */
    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() ||
                board.currentPlayer().isInStaleMate();

    }

    // Returns the highest node value for the level we are on! Crucial to minimax algorithm
    public int max(final Board board, final int depth) {
        if(depth == 0 || isEndGameScenario(board)) { // If we are on the next iteration/recursive call of the algorithm or game is done
            return this.boardEvaluator.evaluate(board, depth); // Evaluate the board
        }

        int highestSeenValue = Integer.MIN_VALUE; // We simply make our highest value equal to the lowest for the sake of initialization
        for(final Action action : board.currentPlayer().getLegalActions()) { // Get all possible moves the player can make
            final ActionTransition actionTransition = board.currentPlayer().makeAction(action); // make each move by iterating through them all
            if(actionTransition.getMoveStatus().isDone()) { // Once move is done
                final int currentValue = min(actionTransition.getBoard(), depth - 1);
                // We want to score the move! By scoring our AI can judge every move
                if(currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue; // Update highest value
                }
            }
        }
        return highestSeenValue;
        // Return the highest value of all the legal moves! This helps construct parent nodes for the minimax algorithm
        // Only on even levels
    }

    /**
     * This lets us know what AI we're using!
     */
    @Override
    public String toString() {
        return "MiniMax";
    }
}
