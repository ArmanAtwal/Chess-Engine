/** Created by Arman Atwal
 * Inspired from online Tutorials and Videos about AI and software design
 */

package com.chess.gui;

import com.chess.gui.Table.ActionLog;
import com.chess.pieces.Piece;
import com.chess.board.Action;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The point of this class is to design the GUI implementation for a panel which displays the pieces that have been taken
 */
public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel; // We're creating frameworks inside the taken pieces panel. This lies in the north
    private final JPanel southPanel; // We're creating frameworks inside the taken pieces panel. This lies in the south

    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6"); // The panel color
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80); // 40 by 80 pixels
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    // Fancy way of simply creating a border for the area in which our taken pieces will go

    /**
     * This constructor is simply helping us set up the panel in which our taken pieces will lie!
     */
    public TakenPiecesPanel() {
        super(new BorderLayout()); // Creates new JPanel
        setBackground(PANEL_COLOR); // Background of panel is beige
        setBorder(PANEL_BORDER); // Creates a boundary for our panel
        this.northPanel = new JPanel(new GridLayout(8, 2));
        // We create this grid for both north and south to ensure all 16 pieces can be put inside, and assumed taken
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR); // Setting color of north internal panel
        this.southPanel.setBackground(PANEL_COLOR); // Setting color of south internal panel
        this.add(this.northPanel, BorderLayout.NORTH);
        // This ensures the internal JPanel actually becomes a component of our takenPiecesPanel (and is in the north)
        this.add(this.southPanel, BorderLayout.SOUTH);
        // This ensures the internal JPanel actually becomes a component of our takenPiecesPanel (and is in the south)
        setPreferredSize(TAKEN_PIECES_DIMENSION); // This determines the number of pixels our panel will take in the application
    }

    /**
     * So, after a match, we have to redraw the taken pieces panel! This method will be responsible for doing that.
     * @param actionLog, a log of the moves that have been made in the game, and it's constantly updated
     *                   throughout the game!
     */
    public void redo(final ActionLog actionLog) {
        southPanel.removeAll(); // Clear south panel component
        northPanel.removeAll(); // Clear north panel component

        final List<Piece> whiteTakenPieces = new ArrayList<>(); // List for white pieces taken in game
        final List<Piece> blackTakenPieces = new ArrayList<>(); // List for black pieces taken in game

        /**
         * We are going to fo through every action that has been made in the game, and if it is an attack move that was
         * made, then we will determine the piece that was taken from the attack! After doing so, determine the color,
         * and add it to the correct list for taken pieces!
         */
        for(final Action action : actionLog.getActions()) { // Go through all game moves
            if(action.isAttack()) { // If move is attack
                final Piece takenPiece = action.getAttackedPiece(); // Get attacked piece
                if(takenPiece.getPieceColor().isWhite()) { // If white
                    whiteTakenPieces.add(takenPiece); // Add to list for white taken pieces
                }
                else if(takenPiece.getPieceColor().isBlack()) { // If black
                    blackTakenPieces.add(takenPiece); // Add to list for black taken pieces
                }
                else { // Should never reach here, but if it does, exception is thrown!
                    throw new RuntimeException("Shouldn't be here");
                }
            }
        }
        /**
         * Here, within the panel, we are going to sort the taken pieces by how strong they are! Only for visual pleasing.
         * Both for black and white pieces. We use the getPieceValue method in order to determine the pieces' strength!
         */
        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });
        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        /**
         * The last step is for us to draw the taken pieces/make it visible on our GUI!
         */
        for(final Piece takenPiece : whiteTakenPieces) { // Iterate through all white taken pieces
            try {
                // For the taken piece, we want to map to the image in the art directory. This is basically the image naming pattern
                final BufferedImage image = ImageIO.read(new File("art/holywarriors/" + "" +
                        takenPiece.getPieceColor().toString().substring(0, 1) + "" +
                        takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() - 15,
                        icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.southPanel.add(imageLabel); // Creates the image for taken pieces in our panel!
            } catch(final IOException e) { // Catches exception
                e.printStackTrace();
            }
        }
        for(final Piece takenPiece : blackTakenPieces) { // Iterate through all black taken pieces
            try {
                // For the taken piece, we want to map to the image in the art directory. This is basically the image naming pattern
                final BufferedImage image = ImageIO.read(new File("art/holywarriors/" + "" +
                        takenPiece.getPieceColor().toString().substring(0, 1) + "" +
                        takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() - 15,
                        icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.southPanel.add(imageLabel); // Creates the image for taken pieces in our panel!
            } catch(final IOException e) { // Catches exception
                e.printStackTrace();
            }
        }
        validate();
    }
}
