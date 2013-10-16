/*******************************************************************************
 *   I3SM: Interactive Individual Identification System Manta version          *
 *                                                                             *
 *   Copyright (C) 2004-2008  Jurgen den Hartog & Renate Reijns                *
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AboutI3SM extends JPanel {
    static JFrame frame = null;
    static Image img = null;
    
    public AboutI3SM() {
    	System.out.println("AboutI3SM");//Daniel Remove
    	
        if(frame != null)
            frame.dispose();
        frame = new JFrame("About I3S Gray Whale");
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { frame.dispose(); frame = null; } });
        frame.setSize(new Dimension(800, 600));
        frame.setResizable(false);                                            
        frame.setLocation(200, 200);

        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());      
        frame.setVisible(true);
    }

  	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color bg = new Color(37, 0, 134);
        setBackground(bg);
     	Graphics2D graphics2D = (Graphics2D) g;
       	graphics2D.setColor(new Color(255, 255, 192));

        if(img == null)
        {
           ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scbg800x600.jpg"));
           img = imageIcon.getImage();
        }
               
        g.drawImage(img, 0, 0, 800, 600, bg, this);

        graphics2D.setFont(new Font("Arial", Font.BOLD, 24));
        graphics2D.drawString("Interactive Individual Identification System", 160, 60);
        graphics2D.setFont(new Font("Arial", Font.PLAIN, 16));
        graphics2D.drawString("Gray Whale Version 1.0, March 2013", 295, 90);

		graphics2D.setColor(new Color(20, 20, 192));

        graphics2D.setFont(new Font("Arial", Font.ITALIC, 16));
        graphics2D.drawString("Copyright (C) 2004 - 2013 by Jurgen den Hartog and Renate Reijns", 160, 160);

        graphics2D.setFont(new Font("Arial", Font.PLAIN, 16));
        graphics2D.drawString("Please send bug reports, comments, and questions to i3s@reijns.com", 160, 240);
        graphics2D.drawString("This program is free software and distributed under", 215, 285);
        graphics2D.drawString("the GNU General Public License (v2, 1991)", 238, 305);
        graphics2D.drawString("For details, see the copy of the GPL distributed with the software", 170, 325);
		
        graphics2D.setFont(new Font("Arial", Font.BOLD, 16));
        graphics2D.drawString("I3S Gray Whale was realized with support of the University of Redlands", 160, 380);
		
		ImageIcon aware = new ImageIcon(this.getClass().getResource("images/UoR.jpg"));
        Image aimg = aware.getImage();
        g.drawImage(aimg, 300, 420, 200, 127, bg, this);
    }
}
