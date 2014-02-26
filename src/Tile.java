import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Tile extends Rectangle {

    private int status;
    private int xID;
    private int yID;
    private int liberties;
    private int blackNeighbors, whiteNeighbors;
    final static int BLACK = 1;
    final static int WHITE = 2;
    private Screen board;
    ////////////////////AI
    //!! old
    // private int whiteInfluence = 0;
    // private int blackInfluence = 0;
    //!! old
    private double influence = 0;
    private double nLen, eLen, sLen, wLen;
    private int side = 0;
    private final static int LEFT = 1;
    private final static int TOP = 2;
    private final static int RIGHT = 3;
    private final static int BOTTOM = 4;
    
    

    // Constructing the Tiles starting out as blank
    public Tile(int x, int y, int width, int height, int xid, int yid) {
        setBounds(x, y, width, height); 
   
        xID = xid;
        yID = yid;
        
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
        
        liberties = 0;                  // Critical liberty count
        status = 0;
        blackNeighbors = 0;
        whiteNeighbors = 0;
    }

    public boolean contains(int x, int y) {
        if (!(x >= this.x && x <= this.x + 36)) {
            return false;
        }

        if (!(y >= this.y && y <= this.y + 36)) {
            return false;
        }
        return true;
    }

    public int getStatus() {
        return status;
    }

    public int getxID() {
        return xID;
    }

    public int getyID() {
        return yID;
    }

    public void getNeighbors() {
    }

    public void isCaptured() {
        status = 0;
        influence = 0;
        liberties = 0;
    }

    public void placeBlack() {
        if (status != 0) {
        } // if there is a piece there already do nothing
        else {
            status = BLACK;            // and change the tile to a black piece
            liberties = 4;

            //blackInfluence = 100;
            influence = 100;
            //blackInfluenceGiver();
        }

    }

    public void placeWhite() {
        if (status != 0) {
            System.out.println("placement failed");
        } else {
            status = WHITE;
            liberties = 4;

            //whiteInfluence = 100;
            influence = -100;
            //whiteInfluenceGiver();
        }
    }

    public void draw(Graphics g) {
        if (status == BLACK) {               // Drawing the black piece
            g.setColor(Color.black);
            g.fillOval(x, y, width, height);
        } else if (status == WHITE) {         // Drawing the white piece
            g.setColor(Color.white);
            g.fillOval(x, y, width, height);
            g.setColor(Color.black);        // Nice looking black outline
            g.drawOval(x, y, width, height);
        } else {
            if (influence == 0) {
            } else {
              //  reduceInfluence();
                //System.out.println(125 - influence);
                g.setColor(new Color(125 - (int) influence, 125 - (int) influence, 125 - (int) influence));
                g.fillRect(x + 2, y + 2, width, height);
                g.setColor(Color.red);
                String s = String.valueOf((int)influence);
                //String s = String.valueOf(xID + " " + yID);
                g.drawString(s, x + 2, y + 14);

                //g.setColor(Color.green);
                //g.drawLine( (x + (width / 2) ) , (y) , (x + (width / 2) ) , (y + height ) );
                //g.drawLine( (x) , (y + (height / 2) ) , (x + width ) , (y + (height / 2) ) );


            }

        }
        
        /*if (xID == 19 && yID == 19) {
            //g.setXORMode(Color.magenta);
            g.setColor(Color.black);
            for (int i = 1; i < 19; i++) {    // drawing the rectangles on the board
                for (int j = 1; j < 19; j++) {
                    g.drawRect(50 + (i * 36), 50 + (j * 36), 36, 36);
                }
            }

            for (int i = 4; i < 20; i = i + 6) {       // drawing the start points
                for (int j = 4; j < 20; j = j + 6) {
                    g.fillOval(45 + (i * 36), 45 + (j * 36), 10, 10);
                }
            }
        }*/


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

    public void reset() {
        liberties = 4;
    }

    /*
     * Assigns black influence values to all tiles
     */
    public void blackInfluenceGiver() {
        nLen = yID - 1;
        eLen = 19 - xID;
        sLen = 19 - yID;
        wLen = xID - 1;

        /*
         * North
         */
        for (int y = 1; y < nLen + 1; y++) {
            Screen.t[xID][yID - y].influence = Screen.t[xID][yID - y].influence + ((100 / nLen) * ((nLen + 1) - y));
        }
        /*
         * East
         */
        for (int x = 1; x < eLen + 1; x++) {
            Screen.t[xID + x][yID].influence = Screen.t[xID + x][yID].influence + ((100 / eLen) * ((eLen + 1) - x));
        }
        /*
         * South
         */
        for (int y = 1; y < sLen + 1; y++) {
            Screen.t[xID][yID + y].influence = Screen.t[xID][yID + y].influence + ((100 / sLen) * ((sLen + 1) - y));
        }
        /*
         * West
         */
        for (int x = 1; x < wLen + 1; x++) {
            Screen.t[xID - x][yID].influence = Screen.t[xID - x][yID].influence + ((100 / wLen) * ((wLen + 1) - x));
        }

        ///////////////////////////////Diagonals///////////////////////////////////////

        for (int y = 1; y < nLen + 1; y++) {
            for (int x = 1; x < eLen + 1; x++) {
                Screen.t[xID + x][yID - y].influence = Screen.t[xID + x][yID - y].influence + ((((((100 / nLen) * ((nLen + 1) - y)) / (eLen + 2)) * ((eLen + 2) - x))
                        + ((((100 / eLen) * ((eLen + 1) - x)) / (nLen + 1)) * ((nLen + 1) - y))) / 2);
            }
        }
        //se
        for (int y = 1; y < sLen + 1; y++) {
            for (int x = 1; x < eLen + 1; x++) {
                Screen.t[xID + x][yID + y].influence = Screen.t[xID + x][yID + y].influence + ((((((100 / sLen) * ((sLen + 1) - y)) / (eLen + 2)) * ((eLen + 2) - x))
                        + ((((100 / eLen) * ((eLen + 1) - x)) / (sLen + 1)) * ((sLen + 1) - y))) / 2);
            }
        }
        //sw
        for (int y = 1; y < sLen + 1; y++) {
            for (int x = 1; x < wLen + 1; x++) {
                Screen.t[xID - x][yID + y].influence = Screen.t[xID - x][yID + y].influence + ((((((100 / sLen) * ((sLen + 1) - y)) / (wLen + 2)) * ((wLen + 2) - x))
                        + ((((100 / wLen) * ((wLen + 1) - x)) / (sLen + 1)) * ((sLen + 1) - y))) / 2);
            }
        }

        //nw
        for (int y = 1; y < nLen + 1; y++) {
            for (int x = 1; x < wLen + 1; x++) {
                Screen.t[xID - x][yID - y].influence = Screen.t[xID - x][yID - y].influence + ((((((100 / nLen) * ((nLen + 1) - y)) / (wLen + 2)) * ((wLen + 2) - x))
                        + ((((100 / wLen) * ((wLen + 1) - x)) / (nLen + 1)) * ((nLen + 1) - y))) / 2);
            }
        }
        
        for (int y = 0; y < Screen.t.length; y++) {
            for (int x = 0; x < Screen.t[0].length; x++) {
                Screen.t[x][y].reduceInfluence();
            }
        }
      

    }

    /*
     * Assigns white influence values to all tiles
     */
    public void whiteInfluenceGiver() {
        nLen = yID - 1;
        eLen = 19 - xID;
        sLen = 19 - yID;
        wLen = xID - 1;

        /*
         * North
         */
        for (int y = 1; y < nLen + 1; y++) {
            Screen.t[xID][yID - y].influence = Screen.t[xID][yID - y].influence - ((100 / nLen) * ((nLen + 1) - y));
        }
        /*
         * East
         */
        for (int x = 1; x < eLen + 1; x++) {
            Screen.t[xID + x][yID].influence = Screen.t[xID + x][yID].influence - ((100 / eLen) * ((eLen + 1) - x));
        }
        /*
         * South
         */
        for (int y = 1; y < sLen + 1; y++) {
            Screen.t[xID][yID + y].influence = Screen.t[xID][yID + y].influence - ((100 / sLen) * ((sLen + 1) - y));
        }
        /*
         * West
         */
        for (int x = 1; x < wLen + 1; x++) {
            Screen.t[xID - x][yID].influence = Screen.t[xID - x][yID].influence - ((100 / wLen) * ((wLen + 1) - x));
        }

        ///////////////////////////////Diagonals///////////////////////////////////////

        /*
         * North East
         */
        for (int y = 1; y < nLen + 1; y++) {
            for (int x = 1; x < eLen + 1; x++) {
                Screen.t[xID + x][yID - y].influence = Screen.t[xID + x][yID - y].influence - ((((((100 / nLen) * ((nLen + 1) - y)) / (eLen + 2)) * ((eLen + 2) - x))
                        + ((((100 / eLen) * ((eLen + 1) - x)) / (nLen + 1)) * ((nLen + 1) - y))) / 2);
            }
        }
        //se
        for (int y = 1; y < sLen + 1; y++) {
            for (int x = 1; x < eLen + 1; x++) {
                Screen.t[xID + x][yID + y].influence = Screen.t[xID + x][yID + y].influence - ((((((100 / sLen) * ((sLen + 1) - y)) / (eLen + 2)) * ((eLen + 2) - x))
                        + ((((100 / eLen) * ((eLen + 1) - x)) / (sLen + 1)) * ((sLen + 1) - y))) / 2);
            }
        }
        //sw
        for (int y = 1; y < sLen + 1; y++) {
            for (int x = 1; x < wLen + 1; x++) {
                Screen.t[xID - x][yID + y].influence = Screen.t[xID - x][yID + y].influence - ((((((100 / sLen) * ((sLen + 1) - y)) / (wLen + 2)) * ((wLen + 2) - x))
                        + ((((100 / wLen) * ((wLen + 1) - x)) / (sLen + 1)) * ((sLen + 1) - y))) / 2);
            }
        }

        //nw
        for (int y = 1; y < nLen + 1; y++) {
            for (int x = 1; x < wLen + 1; x++) {
                Screen.t[xID - x][yID - y].influence = Screen.t[xID - x][yID - y].influence - ((((((100 / nLen) * ((nLen + 1) - y)) / (wLen + 2)) * ((wLen + 2) - x))
                        + ((((100 / wLen) * ((wLen + 1) - x)) / (nLen + 1)) * ((nLen + 1) - y))) / 2);
            }
        }

        for (int y = 0; y < Screen.t.length; y++) {
            for (int x = 0; x < Screen.t[0].length; x++) {
                Screen.t[x][y].reduceInfluence();
            }
        }
        
    }

    public double getInfluence() {
        return influence;
    }

    public void resetInfluence() {
        influence = 0;
    }
   
    public void removePiece() {
        resetInfluence();
        status = 0;
    }

    void reduceInfluence() {
        if (influence > 100) {
            influence = 100;
        } else if (influence < -100) {
            influence = -100;
        }
    }
    
    public int getSide() {
        return side;
    }
    
    // side checking
    public void sideNeighborChange() {
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
    }
    
}
