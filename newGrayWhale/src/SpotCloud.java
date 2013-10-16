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

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;

class SpotCloud extends JPanel {
    private Point2D s1[] = null;
    private Point2D s2[] = null;
    private int pairs[] = null;
    private String orig = null;
    private String found = null;
    private double score;
    
    public SpotCloud(String forig, String ffound, Point2D _s1[], Point2D _s2[], int _pairs[], double _score) {
    	System.out.println("SpotCloud "+forig+ " ffound "+ffound);//Daniel Remove

        s1 = new Point2D [_s1.length];
        s2 = new Point2D [_s2.length];
        
        for(int i=0; i<_s1.length; i++)
            s1[i] = new Point2D (_s1[i].getX(), _s1[i].getY());
        for(int i=0; i<_s2.length; i++)
            s2[i] = new Point2D (_s2[i].getX(), _s2[i].getY());

        pairs = _pairs;
        score = _score;
        
        
        
        GetImageFile gif = new GetImageFile(forig);
        orig = gif.getImageString();
        gif = new GetImageFile(ffound);
        found = gif.getImageString();
        
        scale();
    }
    
    void scale() {
        double minx = Double.POSITIVE_INFINITY;
        double miny = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY;
        double maxy = Double.NEGATIVE_INFINITY;
        
        for(int i=0; i<s1.length; i++) {
            if(s1[i].isInitialized() == false)
                continue;
            if(s1[i].getX() < minx) minx = s1[i].getX();
            if(s1[i].getY() < miny) miny = s1[i].getY();
            if(s1[i].getX() > maxx) maxx = s1[i].getX();
            if(s1[i].getY() > maxy) maxy = s1[i].getY();
        }
        for(int i=0; i<s2.length; i++) {
            if(s2[i].isInitialized() == false)
                continue;
            if(s2[i].getX() < minx) minx = s2[i].getX();
            if(s2[i].getY() < miny) miny = s2[i].getY();
            if(s2[i].getX() > maxx) maxx = s2[i].getX();
            if(s2[i].getY() > maxy) maxy = s2[i].getY();
        }
        
        double scalex = 700.0 / (maxx - minx);
        double scaley = 500.0 / (maxy - miny);
        double offsetx = 0;
        double offsety = 0;
        
        if(scalex < scaley) {
            offsety = (500.0 - 500.0 * (scalex / scaley)) / 2.0;
            scaley = scalex;
        }
        else {
            offsetx = (700.0 - 700.0 * (scaley / scalex)) / 2.0;
            scalex = scaley;
        }
        
        for(int i=0; i<s1.length; i++) {
            if(s1[i].isInitialized() == false)
                continue;
            s1[i].set((s1[i].getX() - minx) * scalex + offsetx + 50, (s1[i].getY() - miny) * scaley + offsety + 50);
        }
        
        for(int i=0; i<s2.length; i++) {
            if(s2[i].isInitialized() == false)
                continue;
			s2[i].set((s2[i].getX() - minx) * scalex + offsetx + 50, (s2[i].getY() - miny) * scaley + offsety + 50);
        }
    }

  	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.white);
     	Graphics2D graphics2D = (Graphics2D) g;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);

        int nrpairs = 0;
        for(nrpairs = 0; nrpairs < pairs.length && pairs[nrpairs] != -1; nrpairs++);
        
        
        
        graphics2D.setColor(Color.red);
        graphics2D.drawString("Unknown individual: " + orig, 50, 30);
        graphics2D.setColor(Color.blue);
        graphics2D.drawString("Found individual: " + found, 50, 48);
        graphics2D.setColor(Color.black);
        graphics2D.drawString("Nr of spot pairs: " + nrpairs, 50, 66);
        graphics2D.drawString("Score: " + nf.format(score), 50, 84);
        
       	graphics2D.setColor(Color.green);
        for(int i=0 ; i<pairs.length; i++) {
            if(pairs[i] == -1)
                break;
            int index1 = pairs[i] % 1000;
            int index2 = pairs[i] / 1000;
            
            graphics2D.draw(new Line2D.Double(s1[index1].getX(), s1[index1].getY(), s2[index2].getX(), s2[index2].getY()));
        }

		graphics2D.setFont(new Font("Arial", Font.PLAIN, 10));

       	graphics2D.setColor(Color.red);
        for(int i=0 ; i<s1.length; i++)
			if(s1[i].isInitialized()) {
				if(i<3)	// this is a reference point
					graphics2D.draw(new Rectangle2D.Double(s1[i].getX()-2, s1[i].getY()-3, 6, 6));

				graphics2D.fill(new Rectangle2D.Double(s1[i].getX()-1, s1[i].getY()-1, 3, 3));
				Integer ii = new Integer(i);
				//graphics2D.drawString(ii.toString(), (int) s1[i].getX(), (int) s1[i].getY() + 16);//this displays index of point under it in spot cloud
			}

        graphics2D.setColor(Color.blue);
        for(int i=0 ; i<s2.length; i++)
			if(s2[i].isInitialized()) {
				if(i<3)	// this is a reference point
					graphics2D.drawOval((int) s2[i].getX() - 5, (int) s2[i].getY() - 5, 10, 10);

                graphics2D.drawOval((int) s2[i].getX() - 3, (int) s2[i].getY() - 3, 6, 6);
				Integer ii = new Integer(i);
			//graphics2D.drawString(ii.toString(), (int) s2[i].getX(), (int) s2[i].getY() + 16);
			}
    }
}

