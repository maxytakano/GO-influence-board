import java.util.ArrayList;

public class Logic {
	//!! Is there a better way than just having these constants in multiple model files? 
	// Should I just create model objects and pass them into their constructors?
	private final static int BLACK = 1;
    private final static int WHITE = 2;
    private final static int EMPTY = 0;
	
	public static void updateBoardForClick(Board gameBoard, int mouseX, int mouseY) {
		System.out.println("hihi");
		for (int y = 0; y < gameBoard.tiles.length; y++) {
            for (int x = 0; x < gameBoard.tiles[0].length; x++) {
                if (gameBoard.tiles[x][y].contains(mouseX, mouseY)) {
                    if (gameBoard.getCurrentTurn() == BLACK) {
                        gameBoard.tiles[x][y].placeBlack();         
                        //moveA[turn] = x;
                        //moveB[turn] = y;
                        //calculateInfluence();
                        //lastX = x;
                        //lastY = y;
                        
                    } else if (gameBoard.getCurrentTurn() == WHITE) {
                    	gameBoard.tiles[x][y].placeWhite();   
                    }
                    gameBoard.incrementTurn();
                    
                }


            }
        }
	}
	
	/*
     *  !!TODO: Needs capture function instead of if statements for each direction
     *  This is where the magic happens... 
     *  Every occupied tile is checked resetting all liberties after each tile check
     */	
	/*public static void captureCheck() {
	        
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
	        
	    }*/

	/*
     *  !!TODO: Change this to boolean should return true or false instead of 0 or 1 
     *  Checks all neighbors of tile looking for an open tile signifying the piece is alive
     *  Status of 0 means its empty 1 otherwise
     */
    /*private static int checkLiberties(Tile z) {
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
    }*/
    
    /*
     * Creates a group of tile objects given the tile and its neighbors, 
     * adds it to the list of groups
     */
    /*private static void groupCreator(Tile tile, Tile tileNeighbor) {
        ArrayList<Tile> group = new ArrayList<Tile>();
        group.add(tile);
        group.add(tileNeighbor);
        
        checkTileNeighbors(tileNeighbor, group, 1000, 1000);
        for(Tile t: group) {
            System.out.println("group: " + t);
        }
        masterList.add(group);
    }*/
    
    /*
     * Looks for neighbors of the same color to add to a tiles group
     */
    /*private static void checkTileNeighbors(Tile tileNeighbor, ArrayList<Tile> group, int lastX, int lastY) {
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
       
    }*/
}

