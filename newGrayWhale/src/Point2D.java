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
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the              *
 *   GNU General Public License for more details.                              *
 *                                                                             *
 *   You should have received a copy of the GNU General Public License         *
 *   along with this program; see the file COPYING GPL v2.txt. If not,         *
 *   write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, *
 *   Boston, MA 02111-1307, USA.                                               *
 *                                                                             *
 *******************************************************************************/
 
 class Point2D {
	private double x;
	private double y;
	
	public Point2D() {
		
		x = Double.NEGATIVE_INFINITY;
		y = Double.NEGATIVE_INFINITY;
	}
	public Point2D(double _x, double _y) {

		x = _x;
		y = _y;
	}
	public Point2D(Point2D _p) {

		x = _p.x;
		y = _p.y;
	}
	public void copy(Point2D _p) {
		x = _p.x;
		y = _p.y;
	}
	public boolean isEqual(Point2D _p) {
		if(_p.x == x && _p.y == y)
			return true;
		else
			return false;
	}
	public void reset() {
		x = Double.NEGATIVE_INFINITY;
		y = Double.NEGATIVE_INFINITY;
	}
	public boolean isInitialized() {
		if(x != Double.NEGATIVE_INFINITY && y != Double.NEGATIVE_INFINITY)
			return true;
		return false;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setX(double _x) {
		x = _x;
	}
	public void setY(double _y) {
		y = _y;
	}
	public void set(Point2D _p) {
		x = _p.x;
		y = _p.y;
	}
	public void set(double _x, double _y) {
		x = _x;
		y = _y;
	}
	public double dist(double _x, double _y) {
		return Math.sqrt((x-_x)*(x-_x)+(y-_y)*(y-_y));
	}
	public double dist(Point2D pp) {
		return dist(pp.x, pp.y);
	}
	public double sqrDist(double _x, double _y) {
		return (x-_x)*(x-_x)+(y-_y)*(y-_y);
	}
	public double sqrDist(Point2D pp) {
		return sqrDist(pp.x, pp.y);
	}
}

