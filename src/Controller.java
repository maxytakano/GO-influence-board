import java.awt.event.*;

public class Controller implements MouseListener, KeyListener {
	private Board gameBoard;	
	private Screen gameScreen;
	
	public Controller(Board board, Screen screen) {
		gameBoard = board;		// Get the model to manipulate
		gameScreen = screen;	// Get the view to update 	
		
		gameScreen.addMouseListener(this);	// Controller listening to the view
        gameScreen.addKeyListener(this);	// Controller listening to the view
	}
	

	/*
     * Places pieces when the mouse is clicked
     * !!TODO: - Move AI to a new Class to be called from here returning the AI's Move
     * 		   - Create a button that switches game between 2 player and Computer opponent moves
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    	System.out.println("hihi");
    	Logic.updateBoardForClick(gameBoard, e.getX(), e.getY());
    	gameScreen.repaint();
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
    
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int key = e.getKeyChar();

        if (key == 'd') {
        	gameBoard.undo();
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
	public void addModel(Board board) {
		gameBoard = board;
		
	}

}
