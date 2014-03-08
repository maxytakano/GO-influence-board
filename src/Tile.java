import java.awt.*;


//public class Tile extends Rectangle {
public class Tile extends Rectangle{
	private final static int EMPTY = 0;
	private final static int BLACK = 1;
    private final static int WHITE = 2;
   
    private final static int LEFT = 1;
    private final static int TOP = 2;
    private final static int RIGHT = 3;
    private final static int BOTTOM = 4;
	
    private int status;
    private int xID;
    private int yID;
    private int liberties;
    
    private double influence = 0;
    private double nLen, eLen, sLen, wLen;
    
    private int side = 0;
    
    // Constructing the Tiles starting out as empty
    public Tile(int x, int y, int width, int height, int xid, int yid) {
    	xID = xid;
        yID = yid;
    	setBounds(x, y, width, height); 
   
   		//!! can do better than this
        if(xID == 0) {   // left side
            side = LEFT;
        }
        else if(yID == 0) {  // top side
            side = TOP;
        }
        else if(xID == 20) {  // right side
            side = RIGHT;
        }
        else if(yID == 20) {   // bottom side
            side = BOTTOM;
        }
        
        liberties = 0;                  // The critical liberty count
        status = EMPTY;
    }

    /*public boolean contains(int x, int y) {
        if (!(x >= this.x && x <= this.x + 36)) {
            return false;
        }

        if (!(y >= this.y && y <= this.y + 36)) {
            return false;
        }
        return true;
    }*/


    public void isCaptured() {
        status = EMPTY;
        influence = 0;
        liberties = 0;
    }

    public void placeBlack() {
        if (status != EMPTY) {
        							   // if there is a piece there already do nothing
        } 								
        else {
            status = BLACK;            // and change the tile to a black piece
            liberties = 4;
            influence = 100;
        
        }

    }

    public void placeWhite() {
        if (status != EMPTY) {
            System.out.println("placement failed");
        } else {
            status = WHITE;
            liberties = 4;
            influence = -100;
        }
    }

    public void decrementLiberties() {
        liberties--;
        if (liberties == 0) {
            // System.out.println("lame death");
            isCaptured();
        }
    }

    public void incrementLiberties(int x) {
        liberties = liberties + x;
    }

    public void removePiece() {
        resetInfluence();
        status = 0;
    }

    /*
     * Caps the Influence to max out at 100 or -100.
     */
    private void reduceInfluence() {
        if (influence > 100) {
            influence = 100;
        } else if (influence < -100) {
            influence = -100;
        }
    }
    
    
    
    /*
     *  If pieces are outside of the board, they "reflect" the color of the piece placed there
     *  to create the edge and corner capturing effect
     */
    /*public void sideNeighborChange() {
        // LEFT
        if(side == LEFT) {
            if(Screen.t[xID + 1][yID].getStatus() == WHITE) {
                status = BLACK;
            }
            else if (Screen.t[xID + 1][yID].getStatus() == BLACK) {
                status = WHITE;
            }
        }
        
        // TOP
        if(side == TOP) {
            if(Screen.t[xID][yID + 1].getStatus() == WHITE) {
                status = BLACK;
            }
            else if (Screen.t[xID][yID + 1].getStatus() == BLACK) {
                status = WHITE;
            }
        }
        
        // RIGHT
        if(side == RIGHT) {
            if(Screen.t[xID - 1][yID].getStatus() == WHITE) {
                status = BLACK;
            }
            else if (Screen.t[xID - 1][yID].getStatus() == BLACK) {
                status = WHITE;
            }
        }
        
        // BOTTOM
        if(side == BOTTOM) {
            if(Screen.t[xID][yID - 1].getStatus() == WHITE) {
                status = BLACK;
            }
            else if (Screen.t[xID][yID - 1].getStatus() == BLACK) {
                status = WHITE;
            }
        }
    }*/
    
    public void reset() {
        liberties = 4;
    }
    
    public void resetInfluence() {
        influence = 0;
    }
    
    public double getInfluence() {
        return influence;
    }

    
    public int getxID() {
        return xID;
    }

    public int getyID() {
        return yID;
    }
    
    public int getStatus() {
        return status;
    }
    
    public int getSide() {
        return side;
    }
}
