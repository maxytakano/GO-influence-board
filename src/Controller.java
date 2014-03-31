import java.awt.event.*;

public class Controller extends JiGoApplet implements MouseListener, KeyListener {
	private Goban gameBoard;	
	private Screen gameScreen;
    private Rules rules;

	
	public Controller(Goban goban, Screen screen) {
		gameBoard = goban;		// Get the model to manipulate
		gameScreen = screen;	// Get the view to update 	
		
		gameScreen.addMouseListener(this);	// Controller listening to the view
        gameScreen.addKeyListener(this);	// Controller listening to the view
        
        rules = new Rules(gameBoard);
        //makeMove();
	}
	
	/*
     * Places pieces when the mouse is clicked
     * !!TODO: - Move AI to a new Class to be called from here returning the AI's Move
     * 		   - Create a button that switches game between 2 player and Computer opponent moves
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    	Point point = new Point();
    	
    	//!!TODO pass in screen information here instead of letting goban know about it.
    	if( gameBoard.translateCoord( e.getX(), e.getY(), point ) )
        {
    			
    		if( rules.canPlay( point ) )
    		{
    			gameBoard.placeStone( getStone(), point );
    			rules.update(gameBoard, point);
    		}
        }
    	
    	//Logic.updateBoardForClick(gameBoard, e.getX(), e.getY());
    	gameScreen.repaint();
    	//rules.togglePlayer();
		//rules.update(gameBoard, point);

    }
    
    /*private void makeMove()
    {
      boolean
        moved = false,
        passed = false;
      int attempts = gameBoard.getBoardSize() * gameBoard.getBoardSize();
      Point point = new Point();

      gameBoard.removeAllMarks();

      while( !moved && (--attempts > 0) )
      {
        point.x = (int)(Math.random() * gameBoard.getBoardSize());
        point.y = (int)(Math.random() * gameBoard.getBoardSize());

  			if( moved = rules.canPlay( point ) )
        {
          gameBoard.placeStone( getStone(), point );
          gameBoard.placeMark( new Markup( Markup.TRIANGLE ), point );
        }
      }

      if( !moved )
      {
        rules.togglePlayer();
        System.out.println("random player passes");
      }
    }*/

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
    
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int key = e.getKeyChar();

        if (key == 'd') {
        	//gameBoard.undo();
        }
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
      
    }

    /*
     * Add the model to the controller
     */
	public void addModel(Goban goban) {
		gameBoard = goban;
		
	}
	
	private Stone getStone()
	{
		return rules.isWhiteToPlay() ?
      (Stone)getWhiteStone() : (Stone)getBlackStone();
	}

}
