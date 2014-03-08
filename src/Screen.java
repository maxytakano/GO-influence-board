import javax.swing.*;
import java.awt.*;

/*
 * Screen class for the board
 * Actual model view controller architecture in the works
 * !!TODO: - Move influence and AI calculations into new classes called influence and AI.
 * 		   - Redo Side board capturing
 * 		   - Implement a new class to check KO rules
 * 		   - Game logic should all be in a new class called Board
 */
public class Screen extends JPanel {
	private static final int BLACK = 1;
	private static final int WHITE = 2;
	private static final int EMPTY = 0;
	
	private Board gameBoard;
	
	private int tileSize;
	private int xPosition, yPosition;
		
	private String s = new String();
     
	/*
	 * Constructs the board with given tileSize and initial x,y of board
	 */
	public Screen(Board board) {
		gameBoard = board;
		tileSize = board.getTileSize();
		xPosition = board.getXPosition();
		yPosition = board.getYPosition();
	}

    public void paintComponent(Graphics g) {
       
        g.setColor(Color.orange);         // Orange background
        g.fillRect(0, 0, 1000, 1000);     // screen dimensions
        
        g.setColor(Color.red);            // Red Grid
        
        // !! Make the grid change for any tile configuration
        // Drawing the red grid
        for (int i = 1; i < 19; i++) {    
            for (int j = 1; j < 19; j++) {
                g.drawRect(50 + (i * 36), 50 + (j * 36), 36, 36);
            }
        }

        // Drawing the star points
        for (int i = 4; i < 20; i = i + 6) {       
            for (int j = 4; j < 20; j = j + 6) {
                g.fillOval(45 + (i * 36), 45 + (j * 36), 10, 10);
            }
        }
        
        // Draws each board tile on the screen
        // !! Is there a way to draw the tiles using a method or something?
        for (int y = 0; y < gameBoard.tiles.length; y++) {
            for (int x = 0; x < gameBoard.tiles[0].length; x++) {
            	if (gameBoard.tiles[x][y].getStatus() == BLACK) {               // Drawing the black piece
                    g.setColor(Color.black);
                    g.fillOval(xPosition + gameBoard.tiles[x][y].getxID() * tileSize,  
                    		yPosition + gameBoard.tiles[x][y].getyID() * tileSize, tileSize, tileSize);
                } else if (gameBoard.tiles[x][y].getStatus() == WHITE) {        // Drawing the white piece
                    g.setColor(Color.white);
                    g.fillOval(xPosition + gameBoard.tiles[x][y].getxID() * tileSize,  
                    		yPosition + gameBoard.tiles[x][y].getyID() * tileSize, tileSize, tileSize);
                    g.setColor(Color.black);        							// Nice looking black outline
                    g.drawOval(xPosition + gameBoard.tiles[x][y].getxID() * tileSize,  
                    		yPosition + gameBoard.tiles[x][y].getyID() * tileSize, tileSize, tileSize);
                } /*else {
                    if (influence == 0) {
                    } else {
                        g.setColor(new Color(125 - (int) influence, 125 - (int) influence, 125 - (int) influence));
                        g.fillRect(x + 2, y + 2, width, height);
                        g.setColor(Color.red);
                        String s = String.valueOf((int)influence);
                        //String s = String.valueOf(xID + " " + yID);
                        g.drawString(s, x + 2, y + 14);
                    }
                }	*/
            }
        }
        
        // drawing the labels
        for (int i = 1; i < 20; i++) {             
            s = String.valueOf(i);
            g.drawString(s, 56, 54 + (i * 36));
            g.drawString(s, 46 + (i * 36), 66);
        }

        g.setColor(Color.black);
        g.drawString("Current Turn: " + Integer.toString(gameBoard.getTurnCounter()), 650, 750);        

    }

	public void addModel(Board board) {
    	gameBoard = board;
    }

}