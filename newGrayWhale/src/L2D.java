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


class L2D {
    private double lea, leb, lec;
    private double x1, y1, x2, y2;
    private double v[];

	public L2D(Point2D p1, Point2D p2) {
    	System.out.println("L2D1");//Daniel Remove
		x1 = p1.getX();
	    y1 = p1.getY();
	    x2 = p2.getX();
	    y2 = p2.getY();
	
	    lea = y2 - y1;
	    leb = x1 - x2;
	    lec = -(lea * x1 + leb * y1);
	 
		v = new double[2];
	    v[0] = x2 - x1;
	    v[1] = y2 - y1;
	}
	
    public L2D(double _x1, double _y1, double _x2, double _y2) {
    	System.out.println("L2D2");//Daniel Remove

		x1 = _x1;
	    y1 = _y1;
	    x2 = _x2;
	    y2 = _y2;
	
	    lea = y2 - y1;
	    leb = x1 - x2;
	    lec = -(lea * x1 + leb * y1);
	 
		v = new double[2];
	    v[0] = x2 - x1;
	    v[1] = y2 - y1;
	}
	
	public boolean point_on_line_piece(double x, double y) {
		double v1[] = new double[2];
		double v2[] = new double[2];
	 
		v1[0] = x - x1;
		v1[1] = y - y1;
		v2[0] = x - x2;
		v2[1] = y - y2;
	 
		if(v1[0]*v2[0] <= 0.000001 && v1[1]*v2[1] <= 0.000001)
		   return true;         // point between p1 and p2
		else
		   return false;
	}
	
	public boolean intersect(L2D l, Point2D p) {
		/* first check for parallel lines */
		if(l.lea * leb == l.leb * lea)  /* parallel */
			return false;
	   
		double a = l.lea;
		double b = l.leb;
		double c = l.lec;
	   
		double lambda = -1.0 * (c + a * x1 + b * y1) / (a * v[0] + b * v[1]);
		p.setX(lambda * v[0] + x1);
		p.setY(lambda * v[1] + y1);
	
		return true;
	}
	
	public boolean intersect_line_pieces(L2D l, Point2D p) {
		if(!intersect(l, p))
			return false;
	
		if(point_on_line_piece(p.getX(), p.getY()) && l.point_on_line_piece(p.getX(), p.getY()))
			return true;
	
		return false;
	}
	public void project_on_line(Point2D p)
	{
	   L2D l = new L2D(p.getX(), p.getY(), p.getX() + v[1], p.getY() - v[0]);
	   intersect(l, p);
	}
	public double getLength() {
		return Math.sqrt((x1-x2)*(x2-x2) + (y1-y2)*(y1-y2));
	}
	public void set(double _x1, double _y1, double _x2, double _y2) {
		x1 = _x1;
		y1 = _y1;
		x2 = _x2;
		y2 = _y2;
	}
	public void set(Point2D p1, Point2D p2) {
		x1 = p1.getX();
		y1 = p1.getY();
		x2 = p2.getX();
		y2 = p2.getY();
	}
	public double getX1() {
		return x1;
	}
	public double getY1() {
		return y1;
	}
	public double getX2() {
		return x2;
	}
	public double getY2() {
		return y2;
	}
}

