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
	
	private final static int TILE_SIZE = 36;
    private final static int X_POSITION = 32;
    private final static int Y_POSITION = 32;
    private final static int SCREEN_MARGIN = 50;
	
	private Goban gameBoard;
	
	private String s = new String();
     
	/*
	 * Constructs the board with given tileSize and initial x,y of board
	 */
	public Screen(Goban goban) {
		gameBoard = goban;
	}

    public void paintComponent(Graphics g) {
       
        g.setColor(Color.orange);         // Orange background
        g.fillRect(0, 0, 1000, 1000);     // screen dimensions
        
        g.setColor(Color.red);            // Red Grid
        
        // !!TODO Make the grid change for any tile configuration
        // Drawing the red grid
        for (int i = 0; i < gameBoard.getBoardSize() - 1; i++) {    
            for (int j = 0; j < gameBoard.getBoardSize() - 1; j++) {
                g.drawRect(SCREEN_MARGIN + (i * TILE_SIZE), SCREEN_MARGIN + (j * TILE_SIZE), TILE_SIZE, TILE_SIZE);
            }
        }

        // Drawing the star points
        /*for (int i = 4; i < 20; i = i + 6) {       
            for (int j = 4; j < 20; j = j + 6) {
                g.fillOval(45 + (i * TILE_SIZE), 45 + (j * TILE_SIZE), 10, 10);
            }
        }*/
        
        // Draws each board tile on the screen
        // !! Is there a way to draw the tiles using a method or something?
        for (int y = 0; y < gameBoard.getBoardSize(); y++) {
            for (int x = 0; x < gameBoard.getBoardSize(); x++) {
            	if(gameBoard.getStone(x,y) == null) {
            		continue;
            	}
            	else if (gameBoard.getStone(x, y).isWhite() == false) {               // Drawing the black piece
            		g.setColor(Color.black);
                    g.fillOval(X_POSITION + x * TILE_SIZE,  
                    		Y_POSITION + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (gameBoard.getStone(x, y).isWhite() == true) {        // Drawing the white piece
                    g.setColor(Color.white);
                    g.fillOval(X_POSITION + x * TILE_SIZE,  
                    		Y_POSITION + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.black);        							// Nice looking black outline
                    g.drawOval(X_POSITION + x * TILE_SIZE,  
                    		Y_POSITION + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
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
        /*for (int i = 1; i < 20; i++) {             
            s = String.valueOf(i);
            g.drawString(s, 56, 54 + (i * 36));
            g.drawString(s, 46 + (i * 36), 66);
        }*/

        g.setColor(Color.black);
        g.drawString("Current Turn: " + Integer.toString(gameBoard.getTurnCounter()), 650, 750);        

    }

	public void addModel(Goban goban) {
    	gameBoard = goban;
    }

}