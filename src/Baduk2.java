import javax.swing.*;
import java.awt.*;

public class Baduk2 extends JApplet{
    
    public void init() {
        setName("Go UI");
        setSize(875, 875);
        //setResizable(false);
        //setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.orange);
     
        Screen screen = new Screen();
        
        screen.addKeyListener(screen);
        screen.setFocusable(true);
        
        add(screen);
        setVisible(true);
        
    }
    
}