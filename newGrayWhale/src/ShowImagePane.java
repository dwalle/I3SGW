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

import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.util.Arrays;



class ShowImagePane extends JPanel {
    private Image img = null;
    private String fim= null;
    private String kOu=null;//known or unknown image
    private int ix=0;
    private int iy=0;
    Point2D[] imgPoints=null;
    
    private int sLen;
    
    public ShowImagePane(String f, String type, Point2D points[]) {
    	System.out.println("ShowImagePane");//Daniel Remove
    	kOu=type;//daniel added
    	
    	imgPoints=new Point2D[points.length];
    	for(int i=0;i<imgPoints.length;i++)
    		imgPoints[i]=new Point2D();
    	copy(points);

    	
        GetImageFile gif = new GetImageFile(f);
        fim = gif.getImageString();
        ImageIcon imageIcon = new ImageIcon(fim);
        img = imageIcon.getImage();
    }
    
    private void copy(Point2D[] points) {
		double x,y;
    	for(int i=0;i<points.length;i++){
    		imgPoints[i]=new Point2D();//this for loop puts all the "passed" points into a new array
    		x=points[i].getX(); 		//that can be altered without changing the original array 
    		y=points[i].getY();
    		imgPoints[i].setX(x);
    		imgPoints[i].setY(y);
    		
    		if (imgPoints[i].getX() > -1000000) 
				sLen++;

    	}
		
	}

	private void paintImage(Graphics2D g) {
        double iw = (double) img.getWidth(this);
        double ih = (double) img.getHeight(this);
        double factor;
		if(iw > ih*1.333)
			factor = 800 / iw;
		else
			factor = 570 / ih;
		
		ix=400 - (int) (0.5*iw*factor);
		iy=285 - (int) (0.5*ih*factor);
          
        g.drawImage(img, ix,iy, (int)(iw*factor), (int)(ih*factor), new Color(37, 0, 134), this);
	}

  	public void paintComponent(Graphics g) {
        super.paintComponent(g);
     	Graphics2D graphics2D = (Graphics2D) g;
     	if(kOu.equals("k"))
     		graphics2D.setColor(Color.blue);
     	else
     		graphics2D.setColor(Color.red);
        
        if(img != null)
            paintImage(graphics2D);
        graphics2D.drawString(fim, 400, 30);
        graphics2D.drawString("Number of Spots: "+sLen, 400, 50);
    
        
        
        if(kOu.equals("k"))
        	graphics2D.setColor(Color.blue);
        else
        	graphics2D.setColor(Color.red);
        
        
        double iw = (double) img.getWidth(this);
        double ih = (double) img.getHeight(this);
        double factor;
        if(iw > ih*1.333)
			factor = 800 / iw;
		else
			factor = 570 / ih;


        for(int i=0 ; i<imgPoints.length; i++) {
			if(i < 3)		// is a reference point
				graphics2D.draw(new Rectangle2D.Double( (imgPoints[i].getX()*factor)+ix , (imgPoints[i].getY()*factor)+iy , 7, 7));
            if(imgPoints[i].isInitialized()){
                graphics2D.fill(new Rectangle2D.Double( (imgPoints[i].getX()*factor)+ix , (imgPoints[i].getY()*factor)+iy , 3, 3));
            }
		}


  	
  	}
  	
 
}

