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
import java.awt.geom.*;
import java.io.*;

class Element {
    protected double cx;	// center coordinates
    protected double cy;
	protected double area;
	protected boolean uninit;

	protected Point2D data[];

    public Element() {
		reset();
    }
	public Element(Element e) {
		System.out.println("Element");//Daniel Removes
		cx = e.cx;
		cy = e.cy;
		uninit = e.uninit;
		area = e.area;
		data = new Point2D[4];
		for(int j=0; j<4; j++) {
			data[j] = new Point2D();
			data[j].copy(e.data[j]);
		}
	}
    public Element(double _x1, double _y1, double _x2, double _y2) {
		reset();
		uninit = false;
		double tmpx = 0.5*(_x2 - _x1);
		double tmpy = 0.5*(_y2 - _y1);
		data[0].set(_x1, _y1);
		data[1].set(_x2, _y2);
		data[2].set(_x1 + tmpx - tmpy, _y1 + tmpy + tmpx);
		data[3].set(_x1 + tmpx + tmpy, _y1 + tmpy - tmpx);
		calcCenter();
    }
	public Element copy() {
		return new Element(this);
	}
    public void set(double _x, double _y) {
		double tmpx = 0.5*(_x - data[0].getX());
		double tmpy = 0.5*(_y - data[0].getY());
		data[1].set(_x, _y);
		data[2].set(data[0].getX() + tmpx - tmpy, data[0].getY() + tmpy + tmpx);
		data[3].set(data[0].getX() + tmpx + tmpy, data[0].getY() + tmpy - tmpx);
		calcCenter();
    }
	public void setCenter(double _x, double _y) {
		double tx = _x - cx;
		double ty = _y - cy;
		move(tx, ty);
	}
	public int getIndex(Point2D ptmp) {
		for(int i=0; i<4; i++)
			if(data[i].isEqual(ptmp))
				return i;
		return -1;
	}
    public boolean empty() {
        return uninit;
    }
    public double sqrDist(double x, double y) {
		return (cx-x)*(cx-x)+(cy-y)*(cy-y);
	}
	public double dist(double x, double y) {
		return Math.sqrt(sqrDist(x, y));
	}
    protected void reset() {
		cx = Double.NEGATIVE_INFINITY;
        cy = Double.NEGATIVE_INFINITY;
		uninit = true;
		area = -1;
		data = new Point2D [4];
		for(int j=0; j<4; j++)
			data[j] = new Point2D();
    }

	/***
      Calculate area of an ellipse
	***/
	public void calcArea() {
		area = 3.1415926*data[0].dist(cx, cy)*data[2].dist(cx, cy);
	}
	protected void calcCenter() {
		cx = 0;
		cy = 0;
		for(int i=0; i<4; i++) {
			cx += data[i].getX();
			cy += data[i].getY();
		}
		cx = cx / 4;
		cy = cy / 4;
	}
    public void move(double tx, double ty) {
		cx += tx;
		cy += ty;
		for(int i=0; i<4; i++)
			data[i].set(data[i].getX() + tx, data[i].getY() + ty);
    }
	public boolean read(DataInputStream in) {
		uninit = false;
		try {
			cx = in.readDouble();
			cy = in.readDouble();
			data = new Point2D[4];
			for(int i=0; i<4; i++)
				data[i] = new Point2D(in.readDouble(), in.readDouble());
        } catch(IOException e) {
            return false;
        }
		return true;
	}
	
	public boolean write(DataOutputStream out) {
		try {
			out.writeDouble(cx);
			out.writeDouble(cy);
			for(int i=0; i<4; i++) {
				out.writeDouble(data[i].getX());
				out.writeDouble(data[i].getY());
			}
        } catch(IOException e) {
            return false;
        }
		return true;
	}
	public Point2D getNearestPoint(double x, double y) {
		double mindist = 100000000;
		Point2D p = new Point2D(x, y);
		Point2D closest = new Point2D();
		
		for(int i=0; i<4; i++) {
			double tmpdist = p.sqrDist(data[i]);
			if(tmpdist < mindist) {
				mindist = tmpdist;
				closest.copy(data[i]);
			}
		}
		return closest;
	}
	public void adjustControl(int index, Point2D newpoint) {
		if(index < 0 || index >= 4)
			return;

		int i0 = 0, i1 = 1, i2 = 2, i3 = 3;

		switch(index) {
			case 0: i0 = 0; i1 = 1; i2 = 2; i3 = 3; break;
			case 1: i0 = 1; i1 = 0; i2 = 3; i3 = 2; break;
			case 2: i0 = 2; i1 = 3; i2 = 0; i3 = 1; break;
			case 3: i0 = 3; i1 = 2; i2 = 1; i3 = 0; break;
		}

		if(data[i0].sqrDist(data[i1])*4 < data[i2].sqrDist(data[i3]) && data[i0].sqrDist(data[i1]) > 0.01) {
			// short side is adjusted and it is below a critical threshold. To prevent instable 
			// adjustments the new control can only shift along the existing short axis of the ellips
			L2D ltmp = new L2D(data[i0], data[i1]);
			ltmp.project_on_line(newpoint);
		}

		data[i0].copy(newpoint);
		double tmpx = cx - data[i0].getX();
		double tmpy = cy - data[i0].getY();
		data[i1].set(cx + tmpx, cy + tmpy);
		double dist = data[i3].dist(data[i2]);
		double factor = (0.5*dist) / Math.sqrt(tmpx*tmpx + tmpy*tmpy);
		
		data[i2].set(cx - factor*tmpy, cy + factor*tmpx);
		data[i3].set(cx + factor*tmpy, cy - factor*tmpx);

		calcCenter();
	}
	public double getArea() {
		calcArea();
		return area;
	}
	public void draw(Graphics2D g, double factor, double xOffset, double yOffset, int i, boolean oAO)
	{
		AffineTransform curAT = g.getTransform();
		AffineTransform newAT = (AffineTransform) curAT.clone();
		double v0 = data[1].getX() - data[0].getX();
		double v1 = data[1].getY() - data[0].getY();
		double angle = 0;
		if(v0 > -0.01 && v0 < 0.01) {
			if(v0 > 0)
				angle = Math.PI/2;
			else
				angle = -Math.PI/2;
		}
		else
			angle = Math.atan(v1 / v0);

		newAT.rotate(angle, cx*factor+xOffset, cy*factor+yOffset);
		g.setTransform(newAT);

		double radius1 = 0.5*data[0].dist(data[1]);
		double radius2 = 0.5*data[2].dist(data[3]);
		Color cur = g.getColor();
		g.setColor(new Color(I3SMPrefs.getEllipseRed(), I3SMPrefs.getEllipseGreen(), I3SMPrefs.getEllipseBlue()));
		g.draw(new Ellipse2D.Double((cx-radius1)*factor+xOffset, (cy-radius2)*factor+yOffset, 
                                     2*radius1*factor, 2*radius2*factor));
		g.setColor(cur);
		g.setTransform(curAT);

		drawGeneral(g, factor, xOffset, yOffset, i, oAO);
	 }
	 protected void drawGeneral(Graphics2D g, double factor, double xOffset, double yOffset, int i, boolean oAO)
	 {
		 Color cur = g.getColor();
		 g.setColor(new Color(I3SMPrefs.getControlRed(), I3SMPrefs.getControlGreen(), I3SMPrefs.getControlBlue()));
		
		 for(int j=0; j<4; j++)
			 g.fillOval((int)(data[j].getX()*factor+xOffset-2), (int)(data[j].getY()*factor+yOffset-2), 5, 5);
		
		 g.setColor(new Color(I3SMPrefs.getCenterRed(), I3SMPrefs.getCenterGreen(), I3SMPrefs.getCenterBlue()));
		 g.fillOval((int)(cx*factor+xOffset-2), (int)(cy*factor+yOffset-2), 5, 5);

		 if(oAO) {
			 g.setColor(new Color((float)1,(float)1,(float)0.0));
			 calcArea();
			 if(area > -1) {
				 Integer itmp = new Integer((int) area);
				 Integer icnt = new Integer(i);
				 g.drawString(icnt.toString() + ": " + itmp.toString(), (float) (cx*factor+xOffset+10), (float) (cy*factor+yOffset));
			 }
		 }
		 g.setColor(cur);
	 }

	// seek new location in the image, used when pasting a new object
	public void newLocation(double w, double h, double zoom) {
		double step = 50.0 / zoom;
		if(cx > step && cy + step > h) {
			move(-step, 0);
			return;
		}
		if(cx < step && cy > step) {
			move(0, -step);
			return;
		}
		if(cx + step > w && cy + step < h) {
			move(0, step);
			return;
		}
		move(step, 0);
	}
}

