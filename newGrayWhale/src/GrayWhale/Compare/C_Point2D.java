package GrayWhale.Compare;

public class C_Point2D {

	
	public static int DBL_INIT=-1000000000;
	
	//variables
	
	double x,y;
	
	//functions
	
	public C_Point2D(){
		x=DBL_INIT;
		y=DBL_INIT;
	}
	
	public C_Point2D(final C_Point2D p){
		x=p.x;
		y=p.y;
	}
	
	public C_Point2D(double _x, double _y){
		x=_x;
		y=_y;
	}
	
	//inline bool Point2D::operator==(const Point2D& p) const 
	public final boolean CompareP(final C_Point2D p){
		return(x == p.x && y== p.y);
	}
	
	//inline const Point2D& Point2D::operator=(const Point2D& p)
	public final C_Point2D equals(final C_Point2D p){
		x= p.x;
		y=p.y;
		return this;
	}
	
	//*used by: element.set(double), .read()
	//Comapre.compareTwo()
	public void set(double _x, double _y){
		x=_x;
		y=_y;
	}
	
	//Never Called
	public void setX(double _x){
		x=_x;
	}
	
	//Never Called
	public void setY(double _y){
		y = _y;
	}
	
	
	//*used by: compareDll.comapreTwo(), compare.find(...)
	//comapre.compareTwo()
	public final double getX(){
		return x;
	}
	
	//*used by: compareDll.comapreTwo(), compare.find(...)
	//coampre.compareTwo()
	public final double getY(){
		return y;
	}
	
	//Never Called
	public final double getDist(final C_Point2D p){
		return Math.sqrt(getSqrDist(p));
	}
	
	//Never Called
	public final double getdist(final double _x, final double _y){
		return Math.sqrt(getSqrDist(_x, _y));
	}

	
	//*used by: Elment.calcArea() 
	//this function gets the srq distance from the center of the 
	//point2d element, and its corners
	public double getSqrDist(double _x, double _y) {
		return (_x -x)*(_x-x)+(_y-y)*(_y-y);
	}

	
	//*used by: element.calcShapeDescriptors
	//Compare.calcAlllPotentialPairs()
	public double getSqrDist(final C_Point2D p) {
		return (p.x-x)*(p.x-x)+(p.y-y)*(p.y-y);
	}
	
	
	
	
	
	
	
}
