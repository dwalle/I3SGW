import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class MonitorProgress extends JPanel {
	JFrame frame = null;
	JProgressBar progressBar;

    public MonitorProgress() {
    	System.out.println("MonitorProgress");//Daniel Remove

    	
        setLayout(null);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
		progressBar.setBounds(10, 10, 200, 25);
        add(progressBar);
		createAndShowGUI();
	}
	public void exit() {
		frame.dispose();
	}
	public void setValue(int val) {
		progressBar.setValue(val);
	}

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("I3S: Search progress");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //Create and set up the content pane.
        frame.setContentPane(this);
        frame.setLocation(300, 300);
        frame.setSize(220, 70);
		frame.setResizable(false);
        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());

        //ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        //frame.setIconImage(imageIcon.getImage());
        frame.setVisible(true);        
    }
/*
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Warning: could not load system look-and-feel.");
			JOptionPane.showMessageDialog(null, "Warning: could not load system look-and-feel.\n");
        }
		MonitorProgress pbd = new MonitorProgress();
		Thread t = new Thread((Runnable) pbd);
		t.start();
    }
*/
}
