/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */


package com.chess.player;

import com.chess.Color;
import com.chess.board.Action;
import com.chess.board.Action.KingCastleMove;
import com.chess.board.Action.QueenCastleMove;
import com.chess.board.Board;
import com.chess.board.ChessTile;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The purpose of this class is to map out the actions for the blackPlayer, and what they can do during the game
 * based on the current chess board, along with all legal moves available to any piece. This class will account for the
 * scenarios a blackPlayer may experience, and prevent the player from making an illegal move.
 */
public class BlackPlayer extends Player {

    /**
     * The purpose of this is to initialize our fields! We pass in the white and black legal moves, on our
     * current board (this parameter) and this shows the BlackPlayer what they can currently do (all legal castle
     * moves)
     *
     * @param board,                   this is an object of the board class that portrays the current board
     * @param whiteStandardLegalMoves, this is a collection of the current legal moves for whitePieces based on the board
     * @param blackStandardLegalMoves, this is a collection of the current legal moves for blackPieces based on the board
     */
    public BlackPlayer(final Board board, final Collection<Action> whiteStandardLegalMoves,
                       final Collection<Action> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    /**
     * All this method does is simply return to us the active BlackPieces as a collection! Now we can determine if
     * a King is on the board or not!
     *
     * @return this.board.getBlackPieces();
     */
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    /**
     * All this does is simply return the color for the black pieces!
     *
     * @return Color.BLACK;
     */
    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    /**
     * This method is going to return our opponents player, in this case the white player/pieces on the board, so we can
     * continue the game and make moves!
     *
     * @return
     */
    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    /**
     * So, what's basically happening here is we are constructing a collection of moves that will allow for the
     * construction of a king side castle and a queen side castle! This is for the black players specifically!
     * First, we need to make sure the king hasn't made a move yet, and is not in check. Then, if tile 5, and 6
     * (2 tiles to the right aren't occupied), the rook tile should be 7. To make sure, we need to first make sure the
     * rook tile isn't occupied, and it's also the rooks first move. Then, if any attacks can't occur on the tiles between
     * the rook and king, and the piece is actually a rook, we can add it to our castle moves for the kingCastles move!
     * That was for the king side castle, now we must do the queen side castles, which is the exact same. The only difference
     * is that we use different tileIDs.
     *
     * @param playerLegals;   the legal actions for the player
     * @param opponentLegals; the legal actions for the opponent
     * @return ImmutableList.copyOf(kingCastles); which contains moves to form a king castle!
     */
    @Override
    protected Collection<Action> calculateKingCastles(final Collection<Action> playerLegals, final Collection<Action> opponentLegals) {

        final List<Action> kingCastles = new ArrayList<>();

        // King Side Castle
        if (this.playersKing.isFirstMove() && !isInCheck()) {
            if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
                final ChessTile rookTile = this.board.getTile(7);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingCastleMove(this.board, this.playersKing, 6,
                                (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
            }
            // Queen Side Castle
            if (!this.board.getTile(3).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(1).isTileOccupied()) {

                final ChessTile rookTile = this.board.getTile(0);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove())
                    if (Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new QueenCastleMove(this.board, this.playersKing, 2,
                            (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                }
            }

        }
        return ImmutableList.copyOf(kingCastles);
    }
}

