import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*
 * Screen class for the board
 * Actual model view controller architecture in the works
 * !!TODO: - Move influence and AI calculations into new classes called influence and AI.
 * 		   - Redo Side board capturing
 * 		   - Implement a new class to check KO rules
 * 		   - Game logic should all be in a new class called Board
 */
public class Screen extends JPanel implements MouseListener, KeyListener {
	private final static int BLACK = 1;
    private final static int WHITE = 2;
    private final static int EMPTY = 0;
	
	private String s = new String();
	/*
	 * !!TODO: Make Getters for these instead of leaving them public
	 */
    public static Tile[][] t;
    public static int[][] influence;
    public static int counter = 0;
    
    private static ArrayList<ArrayList<Tile>> masterList = new ArrayList<ArrayList<Tile>>();
    private static int turn = 1;
    
    private static boolean dead = true;
    
    // For undo button
    private int lastX = 0;
    private int lastY = 0;
    
    // Influence Members
    private int theX = 0, theY = 0;
    private int moveA[] = new int[400];
    private int moveB[] = new int[400];
    
    // AI Members
    private double total = 0;
    private double pastTotal = 10000;
    private int moveX = 0, moveY = 0;
    
    
    

    public Screen() {
        createTiles();
        addMouseListener(this);
        //addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
 
        // Background drawing
        g.setColor(Color.orange);         // Orange board
        g.fillRect(0, 0, 1000, 1000);     // screen dimensions
        
        // Draws each board tile on the screen
        for (int y = 0; y < t.length; y++) {
            for (int x = 0; x < t[0].length; x++) {
                t[x][y].draw(g);
            }
        }
        
        // Draws a black grid on the board
        g.setColor(Color.red);          // Red Grid
        
        // drawing the rectangles on the board
        for (int i = 1; i < 19; i++) {    
            for (int j = 1; j < 19; j++) {
                g.drawRect(50 + (i * 36), 50 + (j * 36), 36, 36);
            }
        }

        // drawing the start points
        for (int i = 4; i < 20; i = i + 6) {       
            for (int j = 4; j < 20; j = j + 6) {
                g.fillOval(45 + (i * 36), 45 + (j * 36), 10, 10);
            }
        }
        
        // drawing the labels
        for (int i = 1; i < 20; i++) {             
            s = String.valueOf(i);
            g.drawString(s, 56, 54 + (i * 36));
            g.drawString(s, 46 + (i * 36), 66);
        }

        g.setColor(Color.black);
        g.drawString("Current Turn: " + Integer.toString(turn), 650, 750);        

    }

    /*
     *  Creates Tile objects for the 19x19 Board plus reflectors outside
     */
    public static void createTiles() {
        //t = new Tile[19][19];           // To be Implemented when reflector alternative is found
        t = new Tile[21][21];
        for (int y = 0; y < t.length; y++) {
            for (int x = 0; x < t[0].length; x++) {
                t[x][y] = new Tile(32 + x * 36, 32 + y * 36, 36, 36, x, y);
            }
        }
    }

    /*
     *  Increment turn and begin capture checking
     */
    public static void incrementTurn() {
        captureCheck();
        turn++;
    }

    /*
     *  !!TODO: Needs capture function instead of if statements for each direction
     *  This is where the magic happens... 
     *  Every occupied tile is checked resetting all liberties after each tile check
     */
    public static void captureCheck() {
        
        for (int y = 0; y < t.length - 1; y++) {
            for (int x = 0; x < t[0].length - 1; x++) {
                t[x][y].sideNeighborChange();
            }
        }
        
        // populate the master list with group lists this should be a function call
        for (int y = 0; y < t.length; y++) {
            for (int x = 0; x < t[0].length; x++) {
                if (t[x][y].getStatus() != 0) {
                    
                    // if stone to the right is different
                    if (t[x + 1][y].getStatus() + t[x][y].getStatus() == 3) {
                        t[x][y].decrementLiberties();
                    }
                    // else if stone to the right is the same
                    else if (t[x + 1][y].getStatus() == t[x][y].getStatus()) {
                        
                    	// for each group list in the master list...
                        for(ArrayList<Tile> l : masterList ) {     
                        	// for each tile in each group list...
                            for(Tile z : l) {                            
                                
                                if(t[x][y].getxID() == z.getxID() && t[x][y].getyID() == z.getyID()) {
                                    counter++;
                                }
                                if(t[x + 1][y].getxID() == z.getxID() && t[x + 1][y].getyID() == z.getyID()) {
                                    counter++;
                                }
                                
                            }           
                        }  
                        
                        if(counter < 2) {
                            groupCreator(t[x][y], t[x + 1][y]);
                            counter = 0;
                        }
                        else counter = 0;
                    }
                    
                    // stone to the left
                    if (t[x - 1][y].getStatus() + t[x][y].getStatus() == 3) {
                        t[x][y].decrementLiberties();
                    }
                    else if (t[x - 1][y].getStatus() == t[x][y].getStatus()) {
                        for(ArrayList<Tile> l : masterList ) {           // for each group list in the master list...
                            for(Tile z : l) {                            // for each tile in each group list...
                                
                                if(t[x][y].getxID() == z.getxID() && t[x][y].getyID() == z.getyID()) {
                                    counter++;
                                }
                                if(t[x - 1][y].getxID() == z.getxID() && t[x - 1][y].getyID() == z.getyID()) {
                                    counter++;
                                }
                                
                            }           
                        }  
                        
                        if(counter < 2) {
                            groupCreator(t[x][y], t[x - 1][y]);
                            counter = 0;
                        }
                        else counter = 0;
                    }
                    
                    // stone below
                    if (t[x][y + 1].getStatus() + t[x][y].getStatus() == 3) {
                        t[x][y].decrementLiberties();
                    }
                    else if (t[x][y + 1].getStatus() == t[x][y].getStatus()) {
                        for(ArrayList<Tile> l : masterList ) {           // for each group list in the master list...
                            for(Tile z : l) {                            // for each tile in each group list...
                                
                                if(t[x][y].getxID() == z.getxID() && t[x][y].getyID() == z.getyID()) {
                                    counter++;
                                }
                                if(t[x][y + 1].getxID() == z.getxID() && t[x][y + 1].getyID() == z.getyID()) {
                                    counter++;
                                }
                                
                            }           
                        }  
                        
                        if(counter < 2) {
                            groupCreator(t[x][y], t[x][y + 1]);
                            counter = 0;
                        }
                        else counter = 0;
                    }
                    
                    // stone above
                    if (t[x][y - 1].getStatus() + t[x][y].getStatus() == 3) {
                        t[x][y].decrementLiberties();
                    }
                    else if (t[x][y - 1].getStatus() == t[x][y].getStatus()) {
                        for(ArrayList<Tile> l : masterList ) {           // for each group list in the master list...
                            for(Tile z : l) {                            // for each tile in each group list...
                                
                                if(t[x][y].getxID() == z.getxID() && t[x][y].getyID() == z.getyID()) {
                                    counter++;
                                }
                                if(t[x][y - 1].getxID() == z.getxID() && t[x][y - 1].getyID() == z.getyID()) {
                                    counter++;
                                }
                                
                            }           
                        }  
                        
                        if(counter < 2) {
                            groupCreator(t[x][y], t[x + 1][y - 1]);
                            counter = 0;
                        }
                        else counter = 0;
                    }

                    t[x][y].reset();

                }
            }
        }
        
        // iterate through the master list
        for(ArrayList<Tile> x : masterList ) {           // for each group list in the master list...
            for(Tile z : x) {                            // for each tile in the group list...
                if(checkLiberties(z) == 1) {
                    dead = false;
                }
            }
            // Capture the entire group
            if(dead == true) {
                for(Tile z : x) {
                    z.isCaptured();
                }
            }
            dead = true;
        }
        
        // clean old lists
        masterList.clear();
        
    }
    
    /*
     *  !!TODO: Change this to boolean should return true or false instead of 0 or 1 
     *  Checks all neighbors of tile looking for an open tile signifying the piece is alive
     *  Status of 0 means its empty 1 otherwise
     */
    private static int checkLiberties(Tile z) {
    	// Check right
        if(t[z.getxID() + 1][z.getyID()].getStatus() == EMPTY) {      
            return 1;                                             // Return 1 to indicate a liberty 
        }
         
        // Check left
        if(t[z.getxID() - 1][z.getyID()].getStatus() == EMPTY) {      
            return 1;
        }
        
        // Check below
        if(t[z.getxID()][z.getyID() + 1].getStatus() == EMPTY) {      
            return 1;
        }
        
        // Check above
        if(t[z.getxID()][z.getyID() - 1].getStatus() == EMPTY) {      
            return 1; 
        }
        
        return 0;                                                 // Return 0 if no liberties were found
    }

    /*
     * Creates a group of tile objects given the tile and its neighbors, 
     * adds it to the list of groups
     */
    private static void groupCreator(Tile tile, Tile tileNeighbor) {
        ArrayList<Tile> group = new ArrayList<Tile>();
        group.add(tile);
        group.add(tileNeighbor);
        
        checkTileNeighbors(tileNeighbor, group, 1000, 1000);
        for(Tile t: group) {
            System.out.println("group: " + t);
        }
        masterList.add(group);
    }

    /*
     * Looks for neighbors of the same color to add to a tiles group
     */
    private static void checkTileNeighbors(Tile tileNeighbor, ArrayList<Tile> group, int lastX, int lastY) {
        int previousX, previousY;                   // so that back and forth recursion case doesn't happen
        boolean check = true;
        
        previousX = tileNeighbor.getxID();
        previousY = tileNeighbor.getyID();
        
        // If neighbor and neighbor to the right are the same    
        if(tileNeighbor.getStatus() == t[tileNeighbor.getxID() + 1][tileNeighbor.getyID()].getStatus() )    
        {
            for(Tile z: group) {
                if(z == t[tileNeighbor.getxID() + 1][tileNeighbor.getyID()]) {
                    check = false;
                }
            }
            if (check) {
                group.add(t[tileNeighbor.getxID() + 1][tileNeighbor.getyID()]);
                // Recursive call, re adds tileNeighbor, needs to be optimized later
                checkTileNeighbors(t[tileNeighbor.getxID() + 1][tileNeighbor.getyID()], group, previousX, previousY);    
            }
            check = true;
        } 
        
        if(tileNeighbor.getStatus() == t[tileNeighbor.getxID() - 1][tileNeighbor.getyID()].getStatus() )        
        {
            for(Tile z: group) {
                if(z == t[tileNeighbor.getxID() - 1][tileNeighbor.getyID()]) {
                    check = false;
                }
            }
            if (check) {
                group.add(t[tileNeighbor.getxID() - 1][tileNeighbor.getyID()]);
                checkTileNeighbors(t[tileNeighbor.getxID() - 1][tileNeighbor.getyID()], group, previousX, previousY);   
            }
            check = true;
        } 
        
        if(tileNeighbor.getStatus() == t[tileNeighbor.getxID()][tileNeighbor.getyID() + 1].getStatus() )       
        {
            for(Tile z: group) {
                if(z == t[tileNeighbor.getxID()][tileNeighbor.getyID() + 1]) {
                    check = false;
                }
            }
            if (check) {
                group.add(t[tileNeighbor.getxID()][tileNeighbor.getyID() + 1]);
                checkTileNeighbors(t[tileNeighbor.getxID()][tileNeighbor.getyID() + 1], group, previousX, previousY);    
            }
            check = true;
        } 
        
        if(tileNeighbor.getStatus() == t[tileNeighbor.getxID()][tileNeighbor.getyID() - 1].getStatus() )      
        {
            for(Tile z: group) {
                if(z == t[tileNeighbor.getxID()][tileNeighbor.getyID() - 1]) {
                    check = false;
                }
            }
            if (check) {
                group.add(t[tileNeighbor.getxID()][tileNeighbor.getyID() - 1]);
                checkTileNeighbors(t[tileNeighbor.getxID() + 1][tileNeighbor.getyID()], group, previousX, previousY);    
            }
            check = true;
        } 
       
    }

    /*
     * !! TODO: Move influence calculations into a new class called Influence
     * Assigns influence to the board for the newly places stone
     */
    public void calculateInfluence() {
        for (int i = 0; i < moveA.length; i++){
            theX = moveA[i];
            theY = moveB[i];
            if (t[theX][theY].getStatus() == WHITE) {
                t[theX][theY].whiteInfluenceGiver();
            } else if (t[theX][theY].getStatus() == BLACK) {
                t[theX][theY].blackInfluenceGiver();
            }
        }
    }
    
   
    
    /*
     * Places pieces when the mouse is clicked
     * !!TODO: - Move AI to a new Class to be called from here returning the AI's Move
     * 		   - Create a button that switches game between 2 player and Computer opponent moves
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        for (int y = 0; y < t.length; y++) {
            for (int x = 0; x < t[0].length; x++) {
                t[x][y].resetInfluence();
            }
        }
        /*
         *  PROTOTYPE AI OPTION APPROX RATING: < 30 KYU
         *  - Needs a seperate undo, that doesn't affect the turn counter
         *  - Needs to access Screen data
         */
        /*if(turn % 2 == 0) {
            
            for (int y = 1; y < t.length - 1; y++) {
                for (int x = 1; x < t[0].length - 1; x++) {
                    if(t[x][y].getStatus() != 0) {
                        System.out.println("why");
                        continue;
                    }
                    t[x][y].placeWhite();    
                    moveA[turn] = x;
                    moveB[turn] = y;
                    calculateInfluence();
                    
                    for (int i = 0; i < t.length; i++) {
                        for (int j = 0; j < t[0].length; j++) {
                            t[i][j].reduceInfluence(); 
                        }
                    }
                    
                    lastX = x;
                    lastY = y;
                    
                    for (int i = 0; i < t.length; i++) {
                        for (int j = 0; j < t[0].length; j++) {
                            //System.out.println(t[i][j].getInfluence());
                            total = total + t[i][j].getInfluence(); 
                        }
                    }
                             
                    if (total < pastTotal) {
                        System.out.println(x + " " + y + " " + total + " " + pastTotal);

                        moveX = x;
                        moveY = y;
                        pastTotal = total;
                    }
                    
                    undo();
                    total = 0;
                    
                }
            }
           
            System.out.println(moveX + " " + moveY);
            t[moveX][moveY].placeWhite();
            moveA[turn] = moveX;
            moveB[turn] = moveY;
            calculateInfluence();
            lastX = moveX;
            lastY = moveY;
            pastTotal = 10000;
            
            incrementTurn();
            repaint();
        }
       
        else*/ for (int y = 0; y < t.length; y++) {
            for (int x = 0; x < t[0].length; x++) {
                if (t[x][y].contains(e.getX(), e.getY())) {
                    if ((turn % 2) == 0) {
                    	// need to implement tryPlaceWhite which only enters the method if there is not a piece there
                        t[x][y].placeWhite();         
                        moveA[turn] = x;
                        moveB[turn] = y;
                        calculateInfluence();
                        lastX = x;
                        lastY = y;
                        incrementTurn();
                        repaint();
                    } else if ((turn % 2) == 1) {
                        t[x][y].placeBlack();
                        moveA[turn] = x;
                        moveB[turn] = y;
                        calculateInfluence();
                        lastX = x;
                        lastY = y;
                        incrementTurn();
                        repaint();
                    }

                    
                }


            }
        }
        
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /*
     * Undoes the last move setting influence and turn counter back one turn.
     * !!TODO: Connect to board model when implemented and allow multiple undo's 
     */
    private void undo() {
        t[lastX][lastY].isCaptured();

            for (int y = 0; y < t.length; y++) {
                for (int x = 0; x < t[0].length; x++) {
                    t[x][y].resetInfluence();
                }
            }
            
        turn--;    
        calculateInfluence();
        repaint();

    }

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int key = e.getKeyChar();

        if (key == 'd') {
        	undo();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
      
    }
    
}