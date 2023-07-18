/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess;

import com.chess.board.BoardUtils;
import com.chess.player.BlackPlayer;
import com.chess.player.Player;
import com.chess.player.WhitePlayer;

/**
 * Enumerator describing the two constant instances
 */
public enum Color {
    /**
     * For a piece that is white we return -1, so we move up the board, because we start at the bottom (48-64)
     */
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.FIRST_ROW[position];
        }

        // White color chooses white player
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    /**
     * For a piece that is black, we return positive 1, so we move down the board, because we start at the top (0-16)
     */
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.EIGHTH_ROW[position];
        }

        // Black color chooses black player
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    /**
     * So, the entire point of this method is for us to be able to distinguish the direction our piece needs to move,
     * based on it's color. Black up, white down.
     */
    public abstract int getDirection();

    /**
     * So, the entire point of this method is for us to be able to distinguish the opposite direction our piece needs to move,
     * based on it's color. Black down, white up.
     */
    public abstract int getOppositeDirection();

    /**
     * This returns a boolean that tells us if the piece is white or not
     */
    public abstract boolean isWhite();

    /** This returns a boolean that tells us if the piece is black or not
     */
    public abstract boolean isBlack();

    /**
     * Given a tileID, we want to see if the square is that of which a pawn promotion can occur on!
     * @param position, the tile
     * @return true or false!
     */
    public abstract boolean isPawnPromotionSquare(int position);

    /**
     * The point of this method is for us to form a connection between our color/alliance and the player. The white color
     * chooses the white player and the black color chooses the black player
     * @param whitePlayer;
     * @param blackPlayer;
     * @return Player, either the white or black player
     */
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
