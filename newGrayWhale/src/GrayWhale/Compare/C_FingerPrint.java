package GrayWhale.Compare;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//include mm_cstring.hpp

public class C_FingerPrint implements Comparable<C_FingerPrint>{

	// friend class Compare

	// h

	// vars

	public final static int MALE = 0;
	public final static int FEMALE = 1;
	public final static int UNKNOWN = 2;

	// priv vars
	public C_Element[] elt;
	public C_Point2D ref1= new C_Point2D(), ref2= new C_Point2D(), ref3= new C_Point2D();
	public int cnt;//cnt is the number of elements
	
	ArrayList<C_Pair> posPairs=new ArrayList<C_Pair>();//daniel, is this arraylist needed?

	private double score;
	private int paircnt;

	private int sex;
	private String view, filename;

	// funcs

	//*used by: compare.find(...)
	C_FingerPrint(C_FingerPrint f) {
		//new Element()
		filename=f.filename;
		cnt=f.cnt;
		score=f.score;
		paircnt=f.paircnt;
		sex=f.sex;
		view=f.view;
		
		elt= new C_Element[cnt];
		
		for(int i=0;i<elt.length;i++){
			elt[i]=new C_Element();
		}
		
		if(f.elt.length>0){
			for(int i=0;i<elt.length;i++){
				elt[i]=f.elt[i];
			}
		}
		ref1=f.ref1;
		ref2=f.ref2;
		ref3=f.ref3;
	}

	//*used by: compareDll.scoreShark() .compareTwo()
	C_FingerPrint(double[] ref, double[] data, int nr) {
		//new Element
		//Element.set(double[])
		
		score=ref1.DBL_INIT;//ref1 is uneeded, just using it for access to DBLINIT
		
		cnt= nr;
		elt= new C_Element[cnt];
		
		ref1.x=ref[0];
		ref1.y=ref[1];
		ref2.x=ref[2];
		ref2.y=ref[3];
		ref3.x=ref[4];
		ref3.y=ref[5];
		for(int i=0; i<cnt; i++){
			elt[i]= new C_Element();
			double[] tmpData=Arrays.copyOfRange(data, i*10, i*10+10);
			elt[i].set(tmpData);//
		}
	}

	//*used by: Coampre.find(...) 
	C_FingerPrint() {
	}

	C_FingerPrint equals(C_FingerPrint f) {
		return f;
	}

	//*used by: comapre.find(...)
	//compare.compareTwo()
	//this funcvtion adds the ref points to the ele array
	void addReference() {
		//new Element
		//Element.set(double, double)
		C_Element[] temp= new C_Element[cnt+3];
		int j=3;
		for( int i=0; i<elt.length;i++){
			temp[j]=new C_Element();
			temp[j]=elt[i];
			j++;
		}
		
		C_Element e1= new C_Element();
		C_Element e2= new C_Element();
		C_Element e3= new C_Element();
		e1.set(ref1.x, ref1.y);
		e2.set(ref2.x,ref2.y);
		e3.set(ref3.x,ref3.y);
		
		
		for( int i=0;i<3;i++)
			temp[i]=new C_Element();
		
		temp[0]=e1;
		temp[1]=e2;
		temp[2]=e3;
		cnt+=3;
		
		for(int i=0;i<elt.length;i++)
			elt[i]=null;
		
		elt=temp;
		
		
	}

	//*used by compare.init()
	public boolean read(String fn, StringBuffer mess) {
		
		DataInputStream in=null;
		try {
			in = new DataInputStream(new FileInputStream(fn));
		} catch (FileNotFoundException e1) {
			System.out.println("File not found: "+fn);
			mess.append("File not found: "+fn);
			e1.printStackTrace();
			return false;
		}
		
		
		try {
			byte id[] = new byte [4];
            in.read(id, 0, 4);
			int version = versionOk(id);

            if(version == -1)
            {
            	System.out.println("Fail READ");
               // mess.append("Unknown type of fingerprint file " + filename);
                in.close();
                return false;
            }
            if(version < 2)
            {
            	System.out.println("Fail READ");
                mess.append("Fingerprint file "  + filename + " is from standard I3S and cannot be used.");
                in.close();
                return false;
            }

            
            
            ref1.setX(in.readDouble());
            ref1.setY(in.readDouble());
            ref2.setX(in.readDouble());
            ref2.setY(in.readDouble());
            ref3.setX(in.readDouble());
            ref3.setY(in.readDouble());

            cnt = in.readInt();
            
            elt = new C_Element[cnt];
            for (int i = 0; i < cnt; i++) {
				elt[i] = new C_Element();
                elt[i].read(in);
			}
            

			//disregard comments for now                 
            
        } catch(IOException e) {
            mess.append("Error while reading from " + filename);
            return false;
        }
		
		filename=fn;

        return true;
    }

	
	//used by FignerPrint.read()
	private int versionOk(byte[] id) {
		 if(id[0] == 'I' && id[1] == 'f' && id[2] == '0' && id[3] == '1')
	            return 0;
	        if(id[0] == 'I' && id[1] == 'f' && id[2] == '2' && id[3] == '0')
	            return 1;
	        if(id[0] == 'I' && id[1] == 'f' && id[2] == '2' && id[3] == '1')
	            return 1;
	        if(id[0] == 'I' && id[1] == 'm' && id[2] == '2' && id[3] == '1')
	            return 2;
	        if(id[0] == 'I' && id[1] == 'm' && id[2] == '2' && id[3] == 'a')	// not a new version, but more than 2.1
	            return 3;
	        return -1;
	}

	//*used by compare.find(...), .exhaustiveSearch()
	//Compare.coampreTwo()							//was int nrpairs
	double distance(C_FingerPrint f, C_Pair[] pairs, C_MutableVariables mv, int affine_corr) {
		//element.matches(Element)
		//element.sqrDist(element)
		//FingerPrint.filterOutDuplciatePairs()
		//fingerPrint.calcCorrectionFactor()
		double totaldist=0;
		paircnt=0;
		
		//the FingerPrint f, is the main fgp image that we are searching for a match with
		//the "this" FingerPrint, is the current fgp we are comparing with out of all the fgps
		//the pairs array is empty, only contains constructor for Pair class
		//the mv contains the overall paircnt variable
		//and still not sure what the aff_corr does...
		
		if(pairs.length==0)
			return -1;
		
		
		mv.paircnt=0;
		
		//System.out.println("FileBeing observed "+filename);
		
		 // process all possible point pairs. this is the most  
	    // time consuming part of I3S. JdH May 13, 2007
		for(int i=0; i<cnt; i++) 
	    {
	    	
	    	//within these for loops we are looking at each element of the known image one by one
	    	//while comparing it one element to EVERY element of the unknown image and finding the J index of the
	    	//closest element with the corresponding I index
	        double mindist = 1000000000;
	        double second  = 1000000000;
	        int minj = -1;

	        for(int j=0; j<f.cnt; j++)      
	        {
				// only compare similar elements
	        	//right here we are comparing each element of the known. one at a time with every other element of the unknown.
	        	if(elt[i].matches(f.elt[j]) == false){
	        		continue;
	        	}
			    
	        	double dist = elt[i].sqrDist(f.elt[j]);

	            if(dist < mindist)
	            {
	                second = mindist;
	                mindist = dist;
	                minj = j;
	            } // else if(dist < second) 
				// second = dist;
	        }

	        
	        //so here it says that the smallest dist has to be 4 times less than the second smallest dist
	        //and that the min dist also has to be less than 5000
	        if(mindist*4 <= second && mindist < 5000) // @@@ change!!! make dependent on distance between ref points
	        {
	            double sqrtd = Math.sqrt(mindist);
	            totaldist += sqrtd;

	            paircnt++;

	            pairs[mv.paircnt].m1 = i;//grabs the index of the known image element (elt[i])
	            pairs[mv.paircnt].m2 = minj;//grabs the index of the CLOSEST element from the unknown image to the index of the known image element (f.elt[i])
	            pairs[mv.paircnt].dist = sqrtd;//grabs the distance between those two points
	            mv.paircnt = mv.paircnt + 1;//increments the number of pairs that these two images have with eachother
	        }
	    }
	    
	    
	    filterOutDuplicatePairs(pairs, mv, totaldist);

	    	    
	    paircnt=mv.paircnt+affine_corr;
	    	  
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    //after comparing all of the elements with each other
	    //calculate the score:
	    
	    if(paircnt<=0){
	    	score=1000000.0;
	    }
	    else{
	    	score= totaldist/ ((paircnt*paircnt));
	    }
	    if(paircnt==1){
	    	score=score+2.0;
	    }
	    else{ 
	    	if(paircnt==2){
	    		score=score+0.5;
	    
	    	}
	    }
	    
	    	    
	    
	    
	    
	    
	    
	    
	    
	    
	    score=score*calcCorrectionFactor(pairs, mv, f);
	    
	    paircnt-=affine_corr;
	    
	    
	    
		return score;
	}

	//*Used by: Compare.Reset()
	void resetscore() {
	}

	//*used by: comapre.find(...)
	//comapre.exhaustiveSearch()
	//Coampre.comapreTwo()
	void doAffine(double[] matrix) {
		//element.doAffine(double[])
		if(elt.length==0||matrix.length==0)
			return;
		
		for(int i=0;i<cnt; i++)
			elt[i].doAffine(matrix);
	}

	//*used by: Compare.exhaustiveSearch()
	//Comapre.compareTwo()
	C_Element getElt(int i) {
		//Element
		if(i<0||i>=cnt){
			C_Element dummy= new C_Element();
			return dummy;
		}
		return elt[i];
	}

	//used by: comapre.compareTwo()
	int getCnt() {
		return cnt;
	}

	//*used by COmapreDll.scoreShark(), Comapre.comapretwo()
	String getFileName() {
		return filename;
	}
	void setfileName(String f){
		filename=f;
	}

	//*used by: compareDll.scoreShark
	//Compare.exhaustiveSearch()
	//Compare.find()
	double getScore() {
		return score;
	}

	
	//*used by: compareDll.scoreShark
	int getPairCnt() {
		return paircnt;
	}
	
	//*used by: compare.find()
	int getSex() {
		return sex;
	}

	//*used by: compare.find()
	String getView() {
		return view;
	}

	//*used by: compare.find(...)
	//Coampre.comapreTwo()
	void setScore(double s) {
		score=s;
	}

	//used by: comapre.find(...)
	void setPairCnt(int p) {
		paircnt=p;
	}

	//*used by:Coampre.init()
	void setSex(int val) {
		sex=val;
	}

	//*used by: Coampre.init()
	void setView(String s) {
		view=s;
	}


	// cpp

	//*used by: FingerPrint.Distance()			//was int paircnt
	void filterOutDuplicatePairs(C_Pair[] pairs, C_MutableVariables mv, double totaldist) {
		//Pair.getM2(), .getDist()
		//FingerPrint.RemovePair()
		
		if(pairs.length==0||mv.paircnt==0||mv.paircnt<=0)
			return;
		//Arrays.sort <-------START HERE, go low to high
		Arrays.sort(pairs);
		
		for(int i=0;i<pairs.length;i++){
			if(pairs[i].getM2()>0){
				posPairs.add(pairs[i]);
			}
		}
				
		for(int i=1;i<mv.paircnt;i++){
			if(pairs[i-1].getM2() != pairs[i].getM2())
				continue;
			if(pairs[i-1].getDist() < pairs[i].getDist())
				removePair(pairs,i,mv,totaldist);
			else
				removePair(pairs,i-1,mv,totaldist);
			i--;//exits by mv.paircnt's value being diminished
		}
			
	}

	
	//*used by:FingerPrint.FilterOutDuplicatePairs()
											//used to be int paircnt
	void removePair(C_Pair[] pairs, int d, C_MutableVariables mv, double totaldist) {
		//Pair.getDist()
		
		totaldist -= pairs[d].getDist();
		for(int i=d;i<mv.paircnt-1;i++)
			pairs[i]=pairs[i+1];
		mv.paircnt=mv.paircnt-1;
	}

	//never Called, daniel this is not needed
	int paircmp(C_Pair p1, C_Pair p2) {
		return 1;
	}

	//*used by:FingerPrint.Distance()
												//used to be int paircnt
	double calcCorrectionFactor(C_Pair[] pairs, C_MutableVariables mv, C_FingerPrint f) {
		//Pair.getM1(), .getM2()
		//Element.calcSimilarityRate()
		
		double factor=1.0;
		
		//C_Element from=new C_Element();
		//C_Element to=new C_Element();
		for(int i=0;i<mv.paircnt;i++){
			C_Element from=elt[pairs[i].getM1()];//grab known image
			C_Element to= f.elt[pairs[i].getM2()];
			factor *= from.calcSimilartiyRate(to);
		}
				
		return Math.pow(factor,0.25);
	}

	@Override
	public int compareTo(C_FingerPrint passed) {
		// TODO Auto-generated method stub
		double p1= this.getScore();
		double p2=passed.getScore();
		if(p1<p2) return -1;
		else if(p1>p2) return 1;
		else return 0;
	}
}
