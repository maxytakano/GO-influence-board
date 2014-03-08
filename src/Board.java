import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Board {	
	private final static int BLACK = 1;
    private final static int WHITE = 2;
    private final static int EMPTY = 0;
    
    private final static int TILE_SIZE = 36;
    private final static int X_POSITION = 32;
    private final static int Y_POSITION = 32;
	
	public Tile[][] tiles;
    public static int[][] influence;
    public static int counter = 0;
    
    private static ArrayList<ArrayList<Tile>> masterList = new ArrayList<ArrayList<Tile>>();
    private static int turnCounter = 1;
    private static int currentTurn = BLACK;
    
    private static boolean dead = true;
    
    // For undo button
    private int lastX = 0;
    private int lastY = 0;
    
    public Board() {
    	createTiles();
    }
    
    /*
     *  Creates Tile objects for the 19x19 Board plus reflectors outside
     */
    public void createTiles() {
        //t = new Tile[19][19];           // To be Implemented when reflector alternative is found
        tiles = new Tile[21][21];
        for (int y = 0; y < tiles.length; y++) {			// Initialize tiles in the y direction
            for (int x = 0; x < tiles[0].length; x++) {		// Initialize tiles in the x direction
            	 
            	//tiles[x][y] = new Tile(x,y);
                tiles[x][y] = new Tile(X_POSITION + x * TILE_SIZE, Y_POSITION + y * TILE_SIZE, TILE_SIZE, TILE_SIZE, x, y);
            }
        }
    }
    
    /*
     *  Increment turn counter
     */
	public void incrementTurn() {
        //captureCheck();
        turnCounter++;
        if (currentTurn == BLACK) {
        	currentTurn = WHITE;
        }
        else if (currentTurn == WHITE) {
        	currentTurn = BLACK;
        }
    }

	public void undo() {
		// TODO Auto-generated method stub
		
	}

	public int getTurnCounter() {	
		return turnCounter;
	}
	
	public int getCurrentTurn() {
		return currentTurn;
	}
	
	public int getTileSize() {
		return TILE_SIZE;
	}
	
	public int getXPosition() {
		return X_POSITION;
	}
	
	public int getYPosition() {
		return Y_POSITION;
	}
	
	
}
