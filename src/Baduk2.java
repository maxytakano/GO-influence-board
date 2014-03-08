import javax.swing.*;
import java.awt.*;

public class Baduk2 extends JApplet{
    private Controller controller;	// !! not sure how to avoid this warning
	
    public void init() {
        setName("Go UI");
        setSize(875, 875);
        //setResizable(false);
        //setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.orange);
     
        Board board = new Board();					// Model
        Screen screen = new Screen(board);			// View
        
        controller = new Controller(board, screen);	// Controller
                 
        screen.setFocusable(true);
        add(screen);
        
        setVisible(true);
    }
    
}