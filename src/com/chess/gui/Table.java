/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.gui;

import com.chess.board.Action;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.ChessTile;
import com.chess.pieces.Piece;
import com.chess.player.ActionTransition;
import com.chess.player.Player;
import com.chess.player.ai.MiniMax;
import com.chess.player.ai.MoveStrat;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.*;

/**
 * This class will be responsible for creating the gui for our chess table!
 */
public class Table extends Observable {
    private final JFrame gameFrame; // Window on our screen
    private final GameHistoryPanel gameHistoryPanel; // Our game history panel
    private final TakenPiecesPanel takenPiecesPanel; // Our taken pieces panel
    private final BoardPanel boardPanel; // This simply represents our board
    private Board chessBoard; // Our current game board
    private final ActionLog actionLog; // log of moves
    private final GameSetup gameSetup; // Object of the gameSetup class

    private ChessTile source; // Tile starting
    private ChessTile destination; // Tile ending
    private Piece humanMovedPiece; // Piece moved by human
    private BoardDirection boardDirection; // The board's orientation!

    private Action computerMove;

    private boolean highlightLegalMoves;

    private final Color lightTileColor = Color.decode("#FFFACD"); // Light tile color
    private final Color darkTileColor = Color.decode("#593E1A"); // Dark tile color;
    private static final String pieceIconPath = "art/holywarriors/"; // This is the directory location of our piece images

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,  600); // Covers 600 by 600 pixels
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(450,  300); // Covers 450 by 300 pixels
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,  10); // Covers 10 by 10 pixels

    private static final Table INSTANCE = new Table(); // Table object

    /**
     * Basic constructor for our Table gui
     */
    private Table() {
        this.gameFrame = new JFrame("Chess"); // Frame title
        this.gameFrame.setLayout(new BorderLayout()); // Creates the layout for our game frame!
        final JMenuBar tableMenuBar = createMenuBar();
        // Creating a game's menu bar
        // Populates table menu bar with items
        this.gameFrame.setJMenuBar(tableMenuBar); // Updates the game's menu bar
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION); // Frame Size
        this.chessBoard = Board.createStandardBoard(); // Our board will be created with pieces as the standard board (GUI)
        this.gameHistoryPanel = new GameHistoryPanel(); // Initialization of field
        this.takenPiecesPanel = new TakenPiecesPanel(); // Initialization of field
        this.boardPanel = new BoardPanel(); // Initializes our board frame/panel
        this.actionLog = new ActionLog(); // Initializes our action log
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        // This is for the gameSetup dropdown. We pass the gameFrame we're on and we pass true! Meaning we MUST fill this dropdown in
        this.boardDirection = BoardDirection.NORMAL; // When we construct a table, the orientation of the table is normal
        this.highlightLegalMoves = false; // Not highlighted by default
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST); // Adding the taken pieces panel to the west side of the application
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER); // Adds game panel to our game frame and centers it
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST); // Adds history panel to the east side of application
        this.gameFrame.setVisible(true); // We can see the frame
    }

    // Getter method for returning instance of table class
    public static Table get() {
        return INSTANCE;
    }

    /**
     * Establishes the start of a game
     */
    public void show() {
        Table.get().getActionLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getActionLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getActionLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    // Returns an instance of the gameSetup class, or our dropdown tab!
    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    // Returns the chessBoard/gameBoard
    private Board getGameBoard() {
        return this.chessBoard;
    }

    /**
     * This is a helper method which calls another method in order to create our Menu Bar
     */
    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu()); // Creates File tab
        tableMenuBar.add(createPreferencesMenu()); // Creates Preferences tab
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    /**
     * We invoke this method to return a JMenu object which is then added to the tableMenuBar object, of
     * JMenuBar type, successfully creating the menu bar!
     * @return
     */
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File"); // Creates the File part of the Menu

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        // Creates the PGN part of our File dropdown!
        // Opens games from a PGN file, allowing our use to load prior played games by anyone else!
        /**
         * So basically, here, whenever openPGN is used by our user, the below action will occur, hence the action
         * performed method, which is overridden, because it can be redefined as needed depending on the action
         */
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Opening PGN File");
            }
        });
        fileMenu.add(openPGN);
        final JMenuItem exitMenuItem = new JMenuItem("Exit"); // This is simply an exit button!
        /**
         * All this does is when clicked we exit the application and dispose the game frame!
         */
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    /**
     * All this method simply does is flip and redraw our board, based on a new added menu item and dropdown for that
     * item
     */
    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences"); // Create a new tab in our menu
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board"); // Add this to the dropdown under the new tab
        flipBoardMenuItem.addActionListener(new ActionListener() {
            /**
             * Basically, this will flip and redraw our board if someone clicks the dropdown
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighlighterCheckBox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        // We have basically made a checkbox for our legal moves highlighter
        /**
         * In the action listeners, we can click it to change between the boolean values of isSelected()
         */
        legalMoveHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckBox.isSelected();
            }
        });
        preferencesMenu.add(legalMoveHighlighterCheckBox);
        return preferencesMenu;
    }

    /**
     * This is responsible for creating our options drop down menu, allowing the player to toggle between various AI states
     * for each player
     */
    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options"); // Creating the options tab
        final JMenuItem setUpAI = new JMenuItem("Setup Game", KeyEvent.VK_M); // Drop down in the tab
        // Here, if we click the "Setup Game" dropdown, then a certain action takes place in the GUI
        setUpAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setAIUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setUpAI);
        return optionsMenu;
    }

    /**
     * This basically notifies the notifier (observer) that it's time for it to make its move!
     */
    private void setAIUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    /**
     * This is a class that implements observer, so it can notify the AI whenever it needs to make an action!
     * This is a class responsible for monitoring the game and updating our AI!
     */
    private static class TableGameAIWatcher implements Observer {
        @Override
        public void update(final Observable o, final Object arg) {
            // So, if our player is an AI player, and it's our current player, who isn't in checkmate or stalemate
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                // Then we create an AI thread and execute the AI's work
                final AILogic logic = new AILogic();
                    logic.execute();
            }
            // If currentPlayer is in checkmate, we display game over
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                System.out.println("Game over!");
            }
            // If currentPlayer is in stalemate, we display game over
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                System.out.println("Game over!");
            }
        }
    }

    /**
     * Update the current gameBoard with the AI move
     */
    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    /**
     * Updates the moves by the computer with every move the AI makes
     */
    public void updateComputerMove(final Action action) {
        this.computerMove = action;
    }

    /**
     * Returns the log of the game history/moves
     */
    private ActionLog getActionLog() {
        return this.actionLog;
    }

    /**
     * Returns the current game history panel to be fixed
     */
    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    /**
     * Returns the current taken pieces panel to be fixed
     */
    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    /**
     * Returns the current panel for the GUI to be redrawn!
     */
    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    /**
     * Notifies us the update was made by the AI
     */
    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    /**
     * This class is responsible for creating an AI thread as part of our GUI, and then executing the work that AI
     * is supposed to do!
     */
    private static class AILogic extends SwingWorker<Action, String> {
        private AILogic() {

        }

        /**
         * This is responsible for executing the work the AI needs to do while the thread is active!
         */
        @Override
        protected Action doInBackground() throws Exception {
            final MoveStrat minMax = new MiniMax(4); // Our AI algorithm
            final Action bestMove = minMax.execute(Table.get().getGameBoard()); // Get the best move through the AI algorithm
            return bestMove; // Return it
        }

        /**
         * When the thread is finished, meaning we've got the bestMove, we can move on to the true implementation!
         */
        @Override
        public void done() {
            try {
                final Action bestMove = get();
                Table.get().updateComputerMove(bestMove); // Update AI with bestMove
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeAction(bestMove).getBoard());
                // Updates board with action from AI
                Table.get().getActionLog().addAction(bestMove); // Add move into the game log for AO
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().actionLog); // Fix the game panel and update with AI
                Table.get().getTakenPiecesPanel().redo(Table.get().actionLog); // Fix the taken pieces panel and update with AI
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard()); // Draw the updated game board!
                Table.get().moveMadeUpdate(PlayerType.COMPUTER); // The move was made and updated by the computer!
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * So, this enumerator has to deal with board directions, and methods involving board directions! The whole reason
     * is for a person to be able to choose how they would like their board to be oriented! Based on the orientation,
     * traversal can change!
     */
    public enum BoardDirection {
        /**
         * For a normally oriented board, we have an opposite return method (flipped) and a traversal, which just runs
         * across the list of tiles like normal
         */
        NORMAL {
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }

            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }
        },
        /**
         * For a flipped board, the opposite returns normal, and we traverse it by traversing the reverse of the list,
         * so we use the guava google library, and reverse the list for traversal!
         */
        FLIPPED {
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }

            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }
        };


        abstract BoardDirection opposite();
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
    }

    /**
     * Visual components to represent the board are included in here
     */
    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles; // This is a list representing all the pieces on a board

        // This is the constructor for the board
        BoardPanel() {
            super(new GridLayout(8, 8)); // We first make a grid for our panel; 8x8, 64 pieces, tiles
            this.boardTiles = new ArrayList<>(); // Initializing the list
            /**
             * Here, what happens is we are iterating through every tile, for the sake of creating a virtual image/
             * graphic of our board. We will first create a new tilePanel object, which we pass the board we are creating,
             * along with the tile's index into. Then, we add this tile into the array list we made earlier, and
             * then directly after that, the board itself (the one we display)
             */
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION); // Set the dimensions of pixels for our board
            validate();
        }

        /**
         * This simply draws our board through the GUI
         * @param board; this is the board!
         */
        public void drawBoard(final Board board) {
            removeAll(); // clear the board
            for(final TilePanel tile : boardDirection.traverse(boardTiles)) {
                // loop through all tiles, and based on preference, traversal, when redrawing, will be changed to fit requirements
                tile.drawTile(board); // draw the tile on the board. Instead of drawing it here, a method does it
                add(tile); // add it to the visual officially
            }
            validate();
            repaint(); // redraw
        }
    }

    /**
     * This class is going to be responsible for keeping track of all the moves in the game, so we can show them
     * in our GUI.
     */
    public static class ActionLog {
        private final List<Action> actions;

        /**
         * Default constructor that initializes our list, which is a storage of all actions in the game!
         */
        ActionLog() {
            this.actions = new ArrayList<>();
        }

        /**
         * Getter method for the list containing all the moves that have been executed in the game!
         * @return this.actions;
         */
        public List<Action> getActions() {
            return this.actions;
        }

        /**
         * This adds an action to the game log
         * @param action, action to be added
         */
        public void addAction(final Action action) {
            this.actions.add(action);
        }

        /**
         * Returns the size of the game log!
         * @return this.actions.size();
         */
        public int size() {
            return this.actions.size();
        }

        /**
         * Once game log is full, we must clear it!
         */
        public void clear() {
            this.actions.clear();
        }

        /**
         * This method is responsible in removing an action from our game log, however, it uses an index for one
         * of the actions in the game log
         * @param index; action index
         * @return this.actions.remove(index); true if removed else false!
         */
        public Action removeAction(int index) {
            return this.actions.remove(index);
        }

        /**
         * This method is also responsible in removing an action from our game log, however, it uses an action
         * specifically, instead of an index
         * @param action, action to remove!
         * @return this.actions.remove(action); true if removed else false!
         */
        public boolean removeAction(final Action action) {
            return this.actions.remove(action);
        }
    }

    /**
     * Visual components to represent the tile are included in here
     */

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }

    private class TilePanel extends JPanel {
        private final int tileID; // This field represents our tileID

        /**
         * So, what's happening here is whenever we make an object of the tile class, we are passed a tileID, along with the
         * current board panel we're working on. We update the tileID field, to keep track of what tile we are on, set the
         * dimensions of the tile, so it's the same as all others, and then we assign the tile color. However, since the tileID
         * updates, the tile color will be alternating from start to finish, making the tile panels on our board panels have differing images!
         * @param boardPanel, board panel object!
         * @param tileID, tile number
         */
        TilePanel(final BoardPanel boardPanel, final int tileID) {
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();

            /**
             * So, this is responsible for detecting mouse clicks on ANY of our tiles! We will only be working on
             * clicking, because we will click a start tile and a destination tile!
             */
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    // Right mouse click cancels out prior selections
                    if(isRightMouseButton(e)) {
                        source = null;
                        destination = null;
                        humanMovedPiece = null;
                    }
                    // Left mouse is for movement
                    else if(isLeftMouseButton(e)) {
                        if(source == null) { // If no click has been made (first click)
                            source = chessBoard.getTile(tileID);
                            // then the source tile is going to be the tile of the
                            // tile that was clicked in our first click! (using its tileID)
                            humanMovedPiece = source.getPiece(); // human moved piece is on clicked tile
                            if(humanMovedPiece == null) { // If clicked on empty tile, undo the assignment of piece and source!
                                source = null;
                            }
                        }
                        else {
                            // second click
                            destination = chessBoard.getTile(tileID);
                            final Action action = Action.MoveFactory.createAction(chessBoard, source.getTileCoordinate(),
                                    destination.getTileCoordinate());
                            // Based on start and destination we choose an action
                            final ActionTransition transition = chessBoard.currentPlayer().makeAction(action);
                            // So we update our board using this transition object. After having determined the action
                            // we will make it, if legal, and this will update the game board!
                            if(transition.getMoveStatus().isDone()) { // If we are done with the move
                                chessBoard = transition.getBoard(); // We update the chess board for our GUI to print!
                                actionLog.addAction(action);
                            }
                            // clear tiles after move!
                            source = null;
                            destination = null;
                            humanMovedPiece = null;
                        }
                        // All this does is redraw our updated chess board!
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, actionLog);
                                takenPiecesPanel.redo(actionLog);
                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            assignTilePiece(chessBoard);
            validate();
        }

        /**
         * Simply recreates our tile, on a different spot in the board, and draws it there
         * @param board, our board
         */
        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePiece(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        /**
         * What this method is responsible for is creating an image on our tile piece, while we are constructing the board
         * image (GUI)
         * @param board, the board and it's information (non-GUI)
         */
        private void assignTilePiece(final Board board) {
            this.removeAll();
            if(board.getTile(this.tileID).isTileOccupied()) {
                /**
                 * All of our image files are going to be inside a directory! Each file is going to reference a piece,
                 * and the piece is going to be named in accordance to the convention that we are using down below!
                 * The file name will have the pieces' allegiance and first character of the allegiance, attached to the first
                 * character in the piece's name, attached to .gif. For example, white bishop is "WB.gif"
                 */
                try {
                    final BufferedImage image = ImageIO.read(new File(pieceIconPath +
                            board.getTile(this.tileID).getPiece().getPieceColor().toString().substring(0, 1) +
                            board.getTile(this.tileID).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * In this method, if we want to highlight the legal moves, we are going to get the moves for the piece which they selected
         * and then if the destination of a certain action, matched the tileID field, meaning we have found our move, we will
         * highlight the destination!
         */
        private void highlightLegals(final Board board) {
            if(highlightLegalMoves) {
                for(final Action action : pieceLegalMoves(board)) {
                    if(action.getDestinationTile() == this.tileID) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png/")))));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /**
         * So, all this method does is it calculates all the legal moves for a piece! We check to see if the human moved piece
         * isn't null, and if it isn't, we want to get its color, and see if it matches the current players turn. If so, we
         * return a collection of the legal moves for that piece! Else we return an empty collection!
         */
        private Collection<Action> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceColor() == board.currentPlayer().getColor()) {
                return humanMovedPiece.numLegalMovesPerPiece(board);
            }
            return Collections.emptyList();
        }

        /**
         * This method is simply going to be used to assign our tile with a color, based on where it is in the board. If
         * a tile is in an odd row, the even tiles in that odd row are of a light color and the odd tiles are of a dark color.
         * If we are in an even row, the even tiles in that even row are of a dark color, and the odd tiles are of a light color.
         */
        private void assignTileColor() {
            if(BoardUtils.FIRST_ROW[this.tileID] ||
                BoardUtils.THIRD_ROW[this.tileID] ||
                BoardUtils.SEVENTH_ROW[this.tileID] ||
                    BoardUtils.FIFTH_ROW[this.tileID]) {
                setBackground(this.tileID % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SECOND_ROW[this.tileID] ||
                    BoardUtils.FOURTH_ROW[this.tileID] ||
                    BoardUtils.SIXTH_ROW[this.tileID] ||
                    BoardUtils.EIGHTH_ROW[this.tileID]) {
                setBackground((this.tileID % 2 != 0 ? lightTileColor : darkTileColor));
            }
        }

    }


}
