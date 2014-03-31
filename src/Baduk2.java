import javax.swing.*;

public class Baduk2 extends JApplet{
    private Controller controller;	// !! not sure how to avoid this warning
    private Goban goban;
    private Screen screen;
    
	private WhiteStone myWhiteStone = new WhiteStone();

    public void init() {
        setName("Go UI");
        setSize(875, 875);
        //setResizable(false);
        //setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        
        // Initialize model
        goban = new Goban(19, myWhiteStone);
        
        // Initialize view
        screen = new Screen(goban);			
        
        // Initialize controller
        controller = new Controller(goban, screen);	
        
        
        add(screen);
        setVisible(true);
    }
    
}