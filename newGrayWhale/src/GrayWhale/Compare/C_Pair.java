package GrayWhale.Compare;

public class C_Pair implements Comparable<C_Pair> {
	
	//vars
	
	protected int m1, m2;//m1 is the index of the known image's element array that
						//corresponds to m2, which is the index of the unknown image
						//array of elements
	protected double dist;//dist is the distance between the above two points
	
	//*used by: compare.find()
	public C_Pair(){
		dist= C_Point2D.DBL_INIT;
		m1=-1;
		m2=-1;
	}
	
	//used by: compareDll.compareTwo(), fingerPrint.calcCorrectionFacotr()
	//Compare.exhaustiveSearch()
	public final int getM1(){
		return m1;
	}

	//*used by:CompareDll.compareTwo(), FignerPrint.filterOutDuplicatePairs()
	//FingerPrint.calcCorrectionFacotr()
	//Compare.exhaustiveSearch()
	public final int getM2(){
		return m2;
	}
	
	//*used by:FingerPrint.filterOutDuplicatePairs()
	//: FignerPrint.removePair()
	public final double getDist(){
		return dist;
	}
	
	//*used by:Compare.calcAllPotentialPairs()
	public void set(int _m1, int _m2, double _d){
		m1=_m1;
		m2=_m2;
		dist=_d;
	}

	@Override
	public int compareTo(C_Pair passed) {
		// TODO Auto-generated method stub
		int p1= this.m2;
		int p2=passed.m2;
		if(p1>p2) return -1;
		else if(p1<p2) return 1;
		else return 0;
	}
}
