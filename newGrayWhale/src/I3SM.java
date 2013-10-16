import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
public class I3SM extends JPanel {
	final static double ZOOM_STEPSIZE = 1.25;
	final static int    MAX_VIEWS = 5;

    static JFrame frame;
    static SharkPanel sp;
    static JFileChooser chooser;

	SharkToolbar toolbar = null;
	String views[] = null;
	int viewCnt = 0;

	public I3SM() {
	
		
    	System.out.println("I3SM_I3SM()");//Daniel Remove
        String topdir = System.getenv("I3SM_DATA");
        if(topdir == null || topdir == "") {
            JOptionPane.showMessageDialog(null, "Variable I3SGW_DATA has not been set. Please see the I3SM manual for instructions.");
            exit();
        }

        
        File f = new File(topdir);
        if(!f.exists() || !f.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Variable I3SGW_DATA does not refer to an existing directory. Please see the I3SM manual for instructions.");
            exit();
        }

        views = new String [MAX_VIEWS];
		ScanDir sd = new ScanDir(views);
		viewCnt = sd.getCnt();

        chooser = new JFileChooser(topdir);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new ImageFilter());
        chooser.setFileView(new ImageFileView());
        chooser.setAccessory(new ImagePreview(chooser));
		
        sp = new SharkPanel(this, System.getenv("I3SM_DATA"));
		toolbar = new SharkToolbar(this);
    }
    
	String[] getViews() {
		return views;
	}
	int getViewCnt() {
		return viewCnt;
	}
    static public String selectImage()
    {
    	System.out.println("SelectImage");//Daniel Remove
        chooser.setSelectedFile(null);

        int retval = chooser.showDialog(frame, null);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File theFile = chooser.getSelectedFile();
            if (theFile != null) {
                return theFile.getPath();
            }
        }
        
        return null;
    }

	public void openImage() { 
		System.out.println("Open Image Option Selceted");//DANIEL REMOVE
        StringBuffer mess = new StringBuffer();
        String filename = selectImage();
        System.out.println("An Image has been Opened");//DANIEL REMOVE
    	System.out.println("__________________________");//Daniel Remove

        if(filename != null) {
            saveResultsPrompt();
            if(sp.updateImage(filename, mess) == false)
                JOptionPane.showMessageDialog(frame, mess);
            else 
                frame.setTitle("I3SGW: Intelligent Individual Identification System (Gray Whale 1.0)  File: " + filename);
        }
    }
    public static void saveResultsPrompt() {
        StringBuffer mess = new StringBuffer();

        if(sp.getFileSaved() == false) {
            switch (JOptionPane.showConfirmDialog(frame, "Do you want to save?", "Saving results?", JOptionPane.YES_NO_OPTION)) {
                case JOptionPane.YES_OPTION:
                    if(sp.writeFingerprint(mess) == false)
                        JOptionPane.showMessageDialog(null, mess);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    return;
                case JOptionPane.NO_OPTION:
                    break;
            }
        }
    }
    public void printFile() {
		sp.printFile();
	}
	public void editCommentField() {
		sp.editCommentField();
	}
    public static void exit() {
        sp.closeOnExit();
        saveResultsPrompt();
		Dimension d = frame.getSize();
		I3SMPrefs.putWinSizeX((int) d.getWidth());
		I3SMPrefs.putWinSizeY((int) d.getHeight());
        System.exit(0);    
    }
	public void setMessage(String m) {
		toolbar.setMessage(m);
	}
	public void setNrSpots(int ns) {
		toolbar.setNrSpots(ns, ns >= SharkPanel.MAX_ELT_CNT);
	}
	public void setExclamation(boolean b) {
		toolbar.setExclamation(b);
	}
	public void setZoomPercentage(double d) {
		toolbar.setZoomPercentage(d);
	}
    public SharkPanel getSharkPanel() {
		return sp;
	}
	public JFrame getFrame() {
		return frame;
	}
    public void buildUI() {
        frame = new JFrame("I3SGW: Interactive Individual Identification System (Gray Whale 1.0)");
        
		//Set up the scroll pane.
        JScrollPane pictureScrollPane = new JScrollPane(sp);
        pictureScrollPane.setPreferredSize(new Dimension(I3SMPrefs.getWinSizeX(), I3SMPrefs.getWinSizeY()));
        pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));

        frame.getContentPane().add(pictureScrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(toolbar.createToolBar(), BorderLayout.PAGE_START);

        SharkMenu sharkmenu = new SharkMenu(this);

        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { exit(); } });
        frame.setJMenuBar(sharkmenu.createMenuBar());
        frame.setSize(new Dimension(I3SMPrefs.getWinSizeX(), I3SMPrefs.getWinSizeY()));

        ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
			splash.createGraphics();
			try {
				Thread.sleep(1000);
			} catch(Exception exc) {}
		}

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Warning: could not load system look-and-feel.");
			JOptionPane.showMessageDialog(null, "Warning: could not load system look-and-feel.\n");
        }

        String topdir = System.getenv("I3SM_DATA");
        if(topdir == null || topdir == "") {
            JOptionPane.showMessageDialog(null, "Variable I3SGW_DATA has not been set. Please see the I3SM manual for instructions.");
            return;
        }
		final I3SM controller = new I3SM();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() { controller.buildUI(); }
		});		
		
    }
}


