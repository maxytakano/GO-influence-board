import javax.swing.*;
import java.awt.*;

public class Baduk2 extends JFrame{
    
    public Baduk2() {
        setTitle("Go UI");
        setSize(875, 875);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.orange);
        
        Screen screen = new Screen();
        
        screen.addKeyListener(screen);
        screen.setFocusable(true);
        
        add(screen);
        setVisible(true);
        
    }
    
    public static void main(String[] args) {
       Baduk2 frame = new Baduk2();
    }
}