
public class Influence {
    // Influence Members
    private int theX = 0, theY = 0;
    private int moveA[] = new int[400];
    private int moveB[] = new int[400];
    
    /*public void resetBoardInfluence() {
    	for (int y = 0; y < t.length; y++) {
            for (int x = 0; x < t[0].length; x++) {
                t[x][y].resetInfluence();
            }
        }
    }*/
	
    /*
     * !! TODO: Move influence calculations into a new class called Influence
     * Assigns influence to the board for the newly places stone
     */
	/*public void calculateInfluence() {
        for (int i = 0; i < moveA.length; i++){
            theX = moveA[i];
            theY = moveB[i];
            if (t[theX][theY].getStatus() == WHITE) {
                t[theX][theY].whiteInfluenceGiver();
            } else if (t[theX][theY].getStatus() == BLACK) {
                t[theX][theY].blackInfluenceGiver();
            }
        }
    }*/
	
	/*
     * Undoes the last move setting influence and turn counter back one turn.
     * !!TODO: Connect to board model when implemented and allow multiple undo's 
     */
	/*private void undo() {
        tiles[lastX][lastY].isCaptured();

            for (int y = 0; y < tiles.length; y++) {
                for (int x = 0; x < tiles[0].length; x++) {
                    tiles[x][y].resetInfluence();
                }
            }
            
        turn--;    
        calculateInfluence();
        repaint();

    }*/
	
	
	/*
     * Assigns black influence values to all tiles
     */
    /*public void blackInfluenceGiver() {
        nLen = yID - 1;
        eLen = 19 - xID;
        sLen = 19 - yID;
        wLen = xID - 1;

        // North
        for (int y = 1; y < nLen + 1; y++) {
            Screen.t[xID][yID - y].influence = Screen.t[xID][yID - y].influence + ((100 / nLen) * ((nLen + 1) - y));
        }
        
        // East
        for (int x = 1; x < eLen + 1; x++) {
            Screen.t[xID + x][yID].influence = Screen.t[xID + x][yID].influence + ((100 / eLen) * ((eLen + 1) - x));
        }
        
        // South
        for (int y = 1; y < sLen + 1; y++) {
            Screen.t[xID][yID + y].influence = Screen.t[xID][yID + y].influence + ((100 / sLen) * ((sLen + 1) - y));
        }
        
        // West
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
      

    }*/

    /*
     * Assigns white influence values to all tiles
     */
    /*public void whiteInfluenceGiver() {
        nLen = yID - 1;
        eLen = 19 - xID;
        sLen = 19 - yID;
        wLen = xID - 1;

        // North
        for (int y = 1; y < nLen + 1; y++) {
            Screen.t[xID][yID - y].influence = Screen.t[xID][yID - y].influence - ((100 / nLen) * ((nLen + 1) - y));
        }
        // East
        for (int x = 1; x < eLen + 1; x++) {
            Screen.t[xID + x][yID].influence = Screen.t[xID + x][yID].influence - ((100 / eLen) * ((eLen + 1) - x));
        }
        // South
        for (int y = 1; y < sLen + 1; y++) {
            Screen.t[xID][yID + y].influence = Screen.t[xID][yID + y].influence - ((100 / sLen) * ((sLen + 1) - y));
        }
        // West
        for (int x = 1; x < wLen + 1; x++) {
            Screen.t[xID - x][yID].influence = Screen.t[xID - x][yID].influence - ((100 / wLen) * ((wLen + 1) - x));
        }

        ///////////////////////////////Diagonals///////////////////////////////////////

        // NorthEast
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
        
    }*/
	
}
