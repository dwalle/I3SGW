package GrayWhale.Compare;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class C_compareDll {

	C_Compare cmp;
	C_FingerPrint defs = new C_FingerPrint();

	boolean DllMain() {
		return true;
	}

	// *
	public void initCompare() {
		// Compare.close, init()

		if (cmp != null) {
			cmp.close();
		}
		cmp = new C_Compare();
		cmp.init();

	}

	// *
	public void closeCompare() {
		// Compare.close

		if (cmp != null) {
			cmp.close();
		}

		cmp = null;// delete
	}

	// *
	public String[] scoreWhale(int[] env, int obj, char[] _instr,
			double[] _ref, double[] _data, double[] _scores, int[] _nrpairs) {

		// FingerPrint f(double[], double[], int)
		// Compare.find(FingerPrint, FingerPrint[], int, String, int)
		// FingerPrint.getScore .getPairCnt
		// FingerPritn.getFileName()
		int len = _data.length / 10;// number of eles of the unknown image.
									// '/10' because it doesnt want to onclude
									// the x and ys of them as their own element
		int strlen = _instr.length;
		char[] instr = _instr;// "u" or "f" or "m" +"all views"
		double[] ref = _ref;// x and y coords of the 3 reference points

		double[] data = _data;// center x and y coords of each element plus
								// their 4 surrounding x and y points
		double[] scores = _scores;// all at 0.0
		int[] nrpairs = _nrpairs;
		
		//for(int i=0;i<data.length;i++)
		//	System.out.print(data[i]+ " ,");
		
		C_FingerPrint f = new C_FingerPrint(ref, data, len);
		int nr_results = _scores.length;
		C_FingerPrint[] best = new C_FingerPrint[nr_results];
		for (int i = 0; i < nr_results; i++) {
			best[i] = new C_FingerPrint();
		}

		char[] view = new char[strlen - 1];

		for (int i = 1; i < strlen; i++)
			view[i - 1] = instr[i];

		// view[strlen-1]=0;
		switch (instr[0]) {
		case 'F':
			cmp.find(f, best, nr_results, view, defs.FEMALE);
			break;
		case 'M':
			cmp.find(f, best, nr_results, view, defs.MALE);
			break;
		default:
			cmp.find(f, best, nr_results, view, defs.UNKNOWN);
			break;
		}

		String[] ret = new String[nr_results];
		for (int i = 0; i < nr_results; i++) {

			if (best[i] == null) {
				ret[i] = "";
				scores[i] = 1000000.;
				nrpairs[i] = -1;
			} else {
				scores[i] = best[i].getScore();
				nrpairs[i] = best[i].getPairCnt();
				ret[i] = best[i].getFileName();
			}
		}
		
		for( int i=0; i<best.length;i++){
			if(best[i]==null)
				System.out.println("best["+i+"]=null");
			else
			System.out.println("best["+i+"].File: "+best[i].getFileName()+ " with score: "+best[i].getScore());	
		}

		return ret;
	}

	// *
	public boolean compareTwo(String s, double[] _ref, double[] _data,
			double[] _matchx, double[] _matchy, double[] _origx,
			double[] _origy, int[] _pairs) {

		// FingerPrint f(double[], double[], int)
		// Point2D, Pair
		// Compare.compareTwo()
		// Pair.getM1(), .getM2()
		// Point2D.getX(), .getY()
		System.out.println("comapreTwoTWO");

		int len = _data.length / 10;
		int max = _origx.length;

		double[] ref = _ref;// 3 reference points and their x and y of the
							// unknown image
		double[] data = _data;// the elements and their cx, cy, and surrounding
								// x's y's
		double[] matchx = _matchx;// empty array
		double[] matchy = _matchy;// empty array
		double[] origx = _origx;// empty array
		double[] origy = _origy;// empty array
		int[] pairs = _pairs;// empty array

		String fgp = s;// String s is the filename of the image that we want to
						// see if it matches the unknown image
		
		//for(int i=0;i<data.length;i++)
		//	System.out.print(data[i]+ " ,");

		C_FingerPrint f = new C_FingerPrint(ref, data, len);//unknown image

		for (int i = 0; i < _pairs.length; i++)
			pairs[i] = -1;

		C_Point2D[] origp = new C_Point2D[max];
		C_Point2D[] tfp = new C_Point2D[max];

		for (int i = 0; i < max; i++) {
			origp[i] = new C_Point2D();
			tfp[i] = new C_Point2D();
		}

		C_Pair[] mypairs = new C_Pair[_pairs.length];

		for (int i = 0; i < mypairs.length; i++) {
			mypairs[i] = new C_Pair();
		}
		C_MutableVariables mv = new C_MutableVariables();
		mv.paircnt = 0;

		if (cmp.compareTwo(f, fgp, origp, tfp, mypairs, mv) == false)
			return false;

		for (int i = 0; i < mv.paircnt; i++)
			pairs[i] = mypairs[i].getM1() * 1000 + mypairs[i].getM2();

		for (int i = 0; i < max; i++) {
			origx[i] = origp[i].getX();
			origy[i] = origp[i].getY();
			matchx[i] = tfp[i].getX();
			matchy[i] = tfp[i].getY();
			// if(i<50) fprintf(stderr,
			// "%d  (%5.2lf, %5.2lf)  (%5.2lf, %5.2lf)\n", i, origx[i],
			// origy[i], matchx[i], matchy[i]);
		}

		for (int i = 0; i < origp.length; i++) {
			origp[i] = null;// delete
			tfp[i] = null;// delete
		}

		return true;
	}

}// end class
