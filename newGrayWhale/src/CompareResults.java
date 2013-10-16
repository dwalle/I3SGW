/*******************************************************************************
 *   I3SM: Interactive Individual Identification System Manta version          *
 *                                                                             *
 *   Copyright (C)  2004-2008  Jurgen den Hartog & Renate Reijns                *
 *                                                                             *
 *   This program is free software; you can redistribute it and/or modify      *
 *   it under the terms of the GNU General Public License as published by      *
 *   the Free Software Foundation; either version 2 of the License, or         *
 *   (at your option) any later version.                                       *
 *                                                                             *
 *   This program is distributed in the hope that it will be useful,           *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 *   GNU General Public License for more details.                              *
 *                                                                             *
 *   You should have received a copy of the GNU General Public License         *
 *   along with this program; see the file COPYING GPL v2.txt. If not,         *
 *   write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, *
 *   Boston, MA 02111-1307, USA.                                               *
 *                                                                             *
 *******************************************************************************/

/*******************************************************************************
 *   Changes with 1.0:                                                         *
 *   In 1.1 a slightly different approach is chosen. Instead of a standard     *
 *   comparison space each known individual is now mapped onto the space of    *
 *   the unknown individual. As a consequence it was necessary to change the   *
 *   comparison between the two transformed arrays into a comparison between   *
 *   the original spots and a transformed array. JdH, February 2007            *
*******************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;

class CompareResults extends JPanel implements ActionListener {
    static JFrame frame = null;
    static SharkPanel sp = null;

    JButton insertInDB;
    JButton newInDB;
    JButton identifyGrayWhale;

    String forig;
    String ffound;
    
    String u="u";
    String k="k";

    public CompareResults(SharkPanel _sp, String _forig, String _ffound, 
                          Point2D _orig1[], Point2D _orig2[], Point2D _tf2[], int _pairs[], double score) {
    	
    	System.out.println("CompareResults");//Daniel Remove
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sp = _sp;
        forig = _forig;
        ffound = _ffound;

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Images", null, new BothSharks(forig, ffound, _orig1, _orig2, _pairs, score),
                          "Shows the images of both individuals together with the selected spots");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_I);
        
        tabbedPane.addTab("Unknown individual", null, new ShowImagePane(forig, u, _orig1), "Shows the image of the unknown individual");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_U);
        tabbedPane.addTab("Found individual", null, new ShowImagePane(ffound, k, _orig2), "Shows the image of the found individual");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_F);
        tabbedPane.addTab("Spot cloud", null, new SpotCloud(forig, ffound, _orig1, _tf2, _pairs, score),
                          "Shows the selected spots of both individuals in a single diagram");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_S);

        tabbedPane.setPreferredSize(new Dimension(900,700));
        
        add(tabbedPane);
        
        insertInDB = new JButton("Include in database");
        insertInDB.setToolTipText("Accept the found individual as the correct match and add the new individual to the I3S database");
        insertInDB.setMnemonic(KeyEvent.VK_I);
        insertInDB.addActionListener(this);

        newInDB = new JButton("New individual");
        newInDB.setToolTipText("The unknown individual is new and will be added as reference to the I3S database");
        newInDB.setMnemonic(KeyEvent.VK_N);
        newInDB.addActionListener(this);

        identifyGrayWhale = new JButton("Only identification");
        identifyGrayWhale.setToolTipText("The current individual is renamed but not inserted in the database");
        identifyGrayWhale.setMnemonic(KeyEvent.VK_O);
        identifyGrayWhale.addActionListener(this);

		JButton close = new JButton("Close");
        close.setToolTipText("Close this window");
        close.setMnemonic(KeyEvent.VK_C);
        close.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(insertInDB);
        panel.add(Box.createRigidArea(new Dimension(10,1)));
        panel.add(newInDB);
        panel.add(Box.createRigidArea(new Dimension(10,1)));
        panel.add(identifyGrayWhale);
        panel.add(Box.createRigidArea(new Dimension(10,1)));
        panel.add(close);
        
        add(Box.createRigidArea(new Dimension(1,7)));
        add(panel);
        add(Box.createRigidArea(new Dimension(1,7)));

		Runnable doWorkRunnable = new Runnable() {
			public void run() { buildGUI(); }
		};
		SwingUtilities.invokeLater(doWorkRunnable);
    }
	
	void buildGUI() {
        if(frame != null)
            frame.dispose();
        frame = new JFrame("I3S Gray Whale: Compare results");
        
        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { dispose(); } });
        frame.setSize(new Dimension(800, 650));                                            
        frame.setLocation(0, 0);
        frame.setResizable(false);
        frame.setVisible(true);        
	}
    
    SharkPanel getSharkPanel() {
        return sp;
    }

    void dispose() {
        frame.dispose();
        frame = null;
    }
    
    public void actionPerformed(ActionEvent e) {
		if((JButton) e.getSource() == insertInDB)
				new AcceptDialog(this, forig, ffound);
		else if((JButton) e.getSource() == identifyGrayWhale)
				new SharkIdentification(this, forig, ffound);
		else if((JButton) e.getSource() == newInDB)
				new InsertDatabase(sp, forig);
		else
			dispose();
    }
}

