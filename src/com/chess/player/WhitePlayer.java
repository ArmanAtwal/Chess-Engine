/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */


package com.chess.player;

import com.chess.Color;
import com.chess.board.Action;
import com.chess.board.Action.QueenCastleMove;
import com.chess.board.Board;
import com.chess.board.ChessTile;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.board.Action.*;

/**
 * The purpose of this class is to map out the actions for the whitePlayer, and what they can do during the game
 * based on the current chess board, along with all legal moves available to any piece. This class will account for the
 * scenarios a whitePlayer may experience, and prevent the player from making an illegal move.
 */
public class WhitePlayer extends Player {

    /**
     * The purpose of this is to initialize our fields! We pass in the white and black legal moves, on our
     * current board (this parameter) and this shows the WhitePlayer what they can currently do (all legal castle
     * moves)
     * @param board, this is an object of the board class that portrays the current board
     * @param whiteStandardLegalMoves, this is a collection of the current legal moves for whitePieces based on the board
     * @param blackStandardLegalMoves, this is a collection of the current legal moves for blackPieces based on the board
     */
    public WhitePlayer(final Board board, final Collection<Action> whiteStandardLegalMoves,
                       final Collection<Action> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    /**
     * All this method does is simply return to us the active WhitePieces as a collection! Now we can determine if
     * a King is on the board or not!
     * @return this.board.getWhitePieces();
     */
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    /**
     * All this does is simply return the color for the white pieces
     * @return Color.WHITE;
     */
    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    /**
     * This method is going to return our opponents player, in this case the black player/pieces on the board, so we can
     * continue the game and make moves!
     * @return
     */
    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    /**
     * So, what's basically happening here is we are constructing a collection of moves that will allow for the
     * construction of a king side castle and a queen side castle! This is for the white players specifically!
     * First, we need to make sure the king hasn't made a move yet, and is not in check. Then, if tile 61, and 62
     * (2 tiles to the right aren't occupied), the rook tile should be 63. To make sure, we need to first make sure the
     * rook tile isn't occupied, and it's also the rooks first move. Then, if any attacks can't occur on the tiles between
     * the rook and king, and the piece is actually a rook, we can add it to our castle moves for the kingCastles move!
     * That was for the king side castle, now we must do the queen side castles, which is the exact same. The only difference
     * is that we use different tileIDs.
     * @param playerLegals; the legal actions for the player
     * @param opponentLegals; the legal actions for the opponent
     * @return ImmutableList.copyOf(kingCastles); which contains moves to form a king castle!
     */
    @Override
    protected Collection<Action> calculateKingCastles(final Collection<Action> playerLegals, final Collection<Action> opponentLegals) {

        final List<Action> kingCastles = new ArrayList<>();

        // King Side Castle
        if(this.playersKing.isFirstMove() && !isInCheck()) {
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
                final ChessTile rookTile = this.board.getTile(63);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                    Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                    rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingCastleMove(this.board, this.playersKing, 62,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            }
            // Queen Side Castle
            if(!this.board.getTile(59).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(57).isTileOccupied()) {

                final ChessTile rookTile = this.board.getTile(56);

                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if(Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new QueenCastleMove(this.board, this.playersKing, 58,
                                (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                    }
                }

            }

        }
        return ImmutableList.copyOf(kingCastles);
    }
}
