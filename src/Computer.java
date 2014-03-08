
public class Computer {
	// AI Members
    private double total = 0;
    private double pastTotal = 10000;
    private int moveX = 0, moveY = 0;
	
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
   
    else*/
}
