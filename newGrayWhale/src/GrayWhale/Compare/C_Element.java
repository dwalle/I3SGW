package GrayWhale.Compare;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

//include point2D.hpp
//include mm_cstring.hpp

public class C_Element {
	// h
	// vars
	// priv
	C_Point2D data[];
	double cx, cy;
	double area;
	double mbr_ratio;
	private final double PI=3.1415926;

	// *used by: FingerPrint.FingerPrint(FingerPrint), .addReference()
	C_Element() {
		cx = 0;
		cy = 0;
		area = 0;
		mbr_ratio = 0;
		data = new C_Point2D[4];
		for (int i = 0; i < data.length; i++) {
			data[i] = new C_Point2D();
		}
	}

	C_Element(C_Element e) {
	}

	void equals(C_Element e) {
	}

	// used by FingerPrint.read()
	public void read(DataInputStream in) {
		try {
			cx = in.readDouble();
			cy = in.readDouble();
			// data = new C_Point2D[4];
			for (int i = 0; i < 4; i++)
				data[i] = new C_Point2D(in.readDouble(), in.readDouble());
		} catch (IOException e) {
			return;
		}

		calcShapeDescriptors();

	}

	// used by FingerPrint.doAffine(...)
	void doAffine(double[] matrix) {

		// Point2d.getX(), getY(), .set(...)
		// Element._doAffine(...), .calcArea()

		if (matrix.length == 0)
			return;

		for (int i = 0; i < 4; i++) {
			double tmpx = data[i].getX();
			double tmpy = data[i].getY();

			// removed _doaffine() due to not being able to use addresses
			double _x = tmpx;
			double _y = tmpy;
			tmpx = matrix[0] * _x + matrix[1] * _y + matrix[2];
			tmpy = matrix[3] * _x + matrix[4] * _y + matrix[5];

			data[i].set(tmpx, tmpy);
		}
		cx = (data[0].getX() + data[1].getX()) / 2.0;
		cy = (data[0].getY() + data[1].getY()) / 2.0;

		// @@@ to do: incorporate direction. direction changes under
		// transformation in contrast to mbr ratio
		calcArea();
	}

	// used by:compare.exhaustiveSearch()
	// Compare.comparetwo()
	double getCx() {
		return cx;
	}

	// used by:Compare.ExhaustiveSearch()
	// Comapre.comapreTwo()
	double getCy() {
		return cy;
	}

	// Never Called
	double getArea() {
		return area;
	}

	// Never Called
	double getMbrRatio() {
		return mbr_ratio;
	}

	// *used by: FignerPrint.addReference()
	void set(double _x, double _y) {
		// point2D.set(dbl, dbl)
		cx = _x;
		cy = _y;
		area = 0;
		mbr_ratio = 1;
		data[0].set(_x, _y);
		data[1].set(_x, _y);
		data[2].set(_x, _y);
		data[3].set(_x, _y);
	}

	// *used by: FingerPrint.FingerPrint(double, double, int)
	void set(double[] _d) {
		// Point2D.set(double, double)

		cx = _d[0];
		cy = _d[1];

		data[0].set(_d[2], _d[3]);
		data[1].set(_d[4], _d[5]);
		data[2].set(_d[6], _d[7]);
		data[3].set(_d[8], _d[9]);
		calcShapeDescriptors();
	}

	// *used by: FingerPrint.Distance()
	// compare.CalcAllPotentialPairs()
	double sqrDist(C_Element p) {
		//p is from the unknown main image.
		return (p.cx - cx) * (p.cx - cx) + (p.cy - cy) * (p.cy - cy);
	}

	// Never Called
	double getDist(C_Element p) {
		return 1.0;
	}

	// *used by:fingerPrint.CalcCorrectionFactor()
	double calcSimilartiyRate(C_Element e) {

		// @@@ to do: direction
	    double rate = 1.0;

		if(area > 0 && e.area > 0) {
			double t = area / e.area;
			if(t < 1.0)
				t = 1.0/t;
			t = 0.5*t + 0.5;
			//fprintf(stderr, "area penalty: %5.2lf     (%5.2lf, %5.2lf)\n", t, area, e.area);
			rate *= t;
		}
	    if(mbr_ratio > 0 && e.mbr_ratio > 0) {
	        double t = mbr_ratio / e.mbr_ratio;
	        if(t < 1.0)
	            t = 1.0/t;
	        t = 0.5*t + 0.5;
	        rate *= t;
			//fprintf(stderr, "ratio penalty: %5.2lf     (%5.2lf, %5.2lf)\n", t, mbr_ratio, e.mbr_ratio);
	    }
		
		return rate;
	}

	// Never Called
	C_Point2D getData(int i) {
		return data[i];
	}

	// *used by: FingerPrint.distance()
	// Compare.calcAllPotentialPairs()
	// Compare.exhaustiveSearch()
	boolean matches(C_Element e) {
		// Element.isSingular(), .withinRatio()
		// areaRatio = 0.5, mbrRatio = 0.55. @@@ to do direction
		//isSingular means that they are a reference point
		
		//e is from the main, unknown image file
		
		//is isgnular checks to see if that element is a reference point
		if (isSingular() && e.isSingular()){
			return true;
		}
		if (isSingular() != e.isSingular()){
			return false;
		}
		
		//this checks if the elemnts are roughly the same shap
		if (withinRatio(area, e.area, 0.5) == false){
			return false;
		}
		//this further examines the shapes
		if (e.mbr_ratio > -1 && mbr_ratio > -1){
			if (withinRatio(mbr_ratio, e.mbr_ratio, 0.55) == false){
				return false;
			}
		}

		return true;
	}

	// priv funcs

	// used by: Element.calcShapedescriptors, Element.doAffine()
	void calcArea() {
		// Elemenet.isSingular()
		// Point2D.getSqrDist(dbl, dbl)

		if (isSingular())//if it is a reference point, then it has no area
			area = 0;
		else//this gets the area of an ellipse! (pi)*a*b (a=radius1, b=radius2)
			area = Math.sqrt(data[0].getSqrDist(cx, cy)
					* data[2].getSqrDist(cx, cy)) * PI;
	}

	// used by Element.set(double), .read()
	void calcShapeDescriptors() {
		// Point2D.getSqrDist(Point 2D)
		// Element._absoluteDiff(dbl, dbl)
		// Element.calcArea()

		// data[] contains the surrounding C_Point2Ds

		double l1 = data[0].getSqrDist(data[1]); // use sqr dist here to spare
													// one sqrt
		double l2 = data[2].getSqrDist(data[3]);

		if (l1 < l2)
			mbr_ratio = l1 / l2;
		else if (_absoluteDiff(l1, l2) < 0.01)
			mbr_ratio = 1;
		else
			mbr_ratio = l2 / l1;
		mbr_ratio = Math.sqrt(mbr_ratio);// now do the sqrt

		calcArea();
	}

	// *used by: Element.calcArea(), .matches()
	boolean isSingular() {
		// Point2D.getX(), .getY()
		//if something isSingular=true. that means it is a reference point. dictated by set(double,double)
		return data[0].getX() == cx && data[0].getY() == cy;
	}

	// other

	// *used by: Element.Matches()
	boolean withinRatio(double x1, double x2, double ratio) {
		if (x1 < x2) {
			if (x1 / x2 >= ratio)
				return true;
		} else {
			if (x2 / x1 >= ratio)
				return true;
		}
		return false;
	}

	// cpp

	// *used by: Element.calcShapeDescriptors()
	double _absoluteDiff(double x1, double x2) {
		if (x1 < x2)
			return x2 - x1;
		return x1 - x2;
	}

}
