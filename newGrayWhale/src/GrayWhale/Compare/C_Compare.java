package GrayWhale.Compare;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.event.ListSelectionEvent;

//include fingerprint.hpp

public class C_Compare {

	// vars
	C_FingerPrint sexDefs = new C_FingerPrint();//useless with info, used to access the sex defs values
	C_Affine aff = new C_Affine();//is useless with data, is used to access the affine functions
	ArrayList<String> allFiles = new ArrayList<String>();//fills with all fo the files found within the directory
	
	ArrayList<String> fgpFiles = new ArrayList<String>();//Daniel creation, filled with all the fgp files found in the directory
	public int num_fgpFiles;
	
	
	C_FingerPrint[] fpa;//fpa[] is filled with all of the known fgp files data found in the directory
	int cnt;//cnt is the number of fgpFiles that we have found in the directory
	int fcnt, mcnt, ucnt;//female, male, and unknown genders found from the fgp files
	int realcnt;//is equal to the length of the fpa array, which is the number of fgp files found within the directory
	boolean fpaEmpty = true;//boolean to detect if the fpa array is emtpy or not, is used to close() the object

	public final static int MALE = 0;//just int defs for the genders
	public final static int FEMALE = 1;
	public final static int UNKNOWN = 2;

	// h
	public C_Compare() {
	}

	static void equals(C_Compare c) {
	}

	// Never Called
	int getCnt() {
		return cnt;
	}

	// Never Called
	int getRealCnt() {
		return realcnt;
	}

	// Never Called
	void reset() {
		// fingerPrint.resetScore()
	}

	// *used by: comparedll.initCompare()
	boolean init() {
		// Compare.clsoe()
		// Compare.collectFingerPrints()
		// Compare.alaysePath()
		// FingerPrint.setSex(), .setView()
		// FingerPrint.read()

		StringBuffer mess = null;
		boolean ret = true;

		if (!fpaEmpty)
			close();

		if (collectFingerprints(allFiles, mess) == false) {

			System.out.println("The string mess will show the errors: " + mess);
			return false;
		}
		
		fpa = new C_FingerPrint[fgpFiles.size() + 20];

		for (int i = 0; i < fpa.length; i++) {
			fpa[i] = new C_FingerPrint();
		}

		realcnt = fpa.length;
		fpaEmpty = false;// fpa is not empty now
		cnt = 0;

		for (int i = 0; i < fgpFiles.size(); i++) {
			C_MutableVariables mv = new C_MutableVariables();
			analysePath(fgpFiles.get(i), mv);
			fpa[cnt].setSex(mv.sex);
			fpa[cnt].setView(mv.view);

			switch (mv.sex) {
			case 1:
				fcnt++;
				break;
			case 0:
				mcnt++;
				break;
			default:
				ucnt++;
				break;
			}

			
			boolean result = fpa[cnt].read(fgpFiles.get(i), mess);
			cnt++;
			
			if (result == false) {
				System.out.println(mess);
				ret = false;
			}

		}
		
		for(int i=0;i<fgpFiles.size();i++)
			System.out.println(i+ " : "+fgpFiles.get(i));
		
		
		num_fgpFiles=fgpFiles.size();
		
		System.out.println(num_fgpFiles);
		return ret;
	}

	// *used by: compareDll.initcompare .closecompare
	// Compare.init()
	// COMPLETE
	boolean close() {

		if (!fpaEmpty) {// if there is anything in this array, clear it
			for (int i = 0; i < fpa.length; i++)
				fpa[i] = null;// similar to delete[] fpa. frees memory by
								// allowing the Jvm to use garbage collection on
								// the null elements
		}
		cnt = 0;
		realcnt = 0;

		return true;
	}

	// Never Called
	boolean update(String name) {
		if(name.length()== 0)
		{
			System.out.println( "Parameter error in Compare.update\n");
			return false;
		}

		if(cnt >= realcnt)
			return init();

		allFiles.add(name);
		StringBuffer mess = new StringBuffer();
		if(fpa[cnt++].read(name, mess) == false)
		{
			System.out.println(mess);
			return false;
		}

		return true;
	}

	// *used by: compareDll.scoreShark()
	boolean find(C_FingerPrint u, C_FingerPrint[] best, int bestnr,
			char[] view, int dbpart) {
		// FingerPrint(FingerPrint)
		// FingerPrint.addReference()
		// FingerPrint.FingerPrint()
		// Pair pairs[1000];
		// fingerPrint.getSex(), .setScore(), .setPairCnt(), .getView()
		// Affine.calcAffine()
		// Point2d.getx(), .getY()
		// Fingerprint.addReference(), doAffine(), .distance(...), .setScore()
		// Compare.calcAllPotentialPairs()
		// Compare.exhaustiveSearch()
		// fingerPrint.getScore()

		C_FingerPrint unknown = new C_FingerPrint(u);
		unknown.addReference();

		C_FingerPrint[] results = new C_FingerPrint[cnt];

		
		for(int i=0;i<results.length;i++){
			results[i]=new C_FingerPrint();
		}

		if (System.getenv("I3SM_DATA").isEmpty()) {
			System.out.println("The I3SM_DATA was not found");
			return false;
		}
		String topdir = System.getenv("I3SM_DATA");// get the value of this
													// environment variable

		// Daniel need to add the progress.txt file!!

		int last = 0;

		int progress = 0;
		int dbcnt;
		switch (dbpart) {
		case FEMALE:
			dbcnt = ucnt + fcnt;
			break;
		case MALE:
			dbcnt = ucnt + mcnt;
			break;
		case UNKNOWN:
			dbcnt = cnt;
			break;
		}
		for (int i = 0; i < cnt; i++) {

			C_Pair[] pairs = new C_Pair[1000];

			for (int k = 0; k < pairs.length; k++){
				// DANIEL can this init stuff be moved outside of the bigger for
				// loop
				pairs[k] = new C_Pair();
			}

			// int paircnt=0; has turned into mv....right below this
			C_MutableVariables mv = new C_MutableVariables();
			mv.paircnt = 0;
			
			results[i] = fpa[i];// Daniel, results should equal the address of
								// fpa

			if ((dbpart == MALE && fpa[i].getSex() == FEMALE)
					|| (dbpart == FEMALE && fpa[i].getSex() == MALE)) {
				System.out.println("DBSETHIGH "+fpa[i].getFileName());
				fpa[i].setScore(1000000.0);
				fpa[i].setPairCnt(-1);
				continue;
			}

			// daniel progress.txt stuff goes here

			if (view.length != 0 && view.equals("all views")
					&& view.equals(fpa[i].getView())) {
				fpa[i].setScore(1000000.0);
				fpa[i].setPairCnt(-1);
				continue;
			}

			double[] matrix = new double[6];

			C_FingerPrint tmp = new C_FingerPrint(fpa[i]);
			tmp.read(fpa[i].getFileName(), null);
			tmp.addReference();//add ref vars to element array
			aff.calcAffine(tmp.ref1.getX(), tmp.ref1.getY(),
					tmp.ref2.getX(), tmp.ref2.getY(), tmp.ref3.getX(),
					tmp.ref3.getY(), unknown.ref1.getX(),
					unknown.ref1.getY(), unknown.ref2.getX(),
					unknown.ref2.getY(), unknown.ref3.getX(),
					unknown.ref3.getY(), matrix);


			
			tmp.doAffine(matrix);

			//unknown is the main image. tmp is every other fgp image being compared to it.
			tmp.distance(unknown, pairs, mv, -3);

			
			if (mv.paircnt <= 0) {
				tmp.setScore(1000.);
				calcAllPotentialPairs(tmp, unknown, pairs, mv);
			}	
			
			System.out.println("call exhaust from find: "+tmp.getFileName());
			exhaustiveSearch(tmp, unknown, pairs, mv);
			System.out.println("tmp. paircnt: "+tmp.getPairCnt());
			fpa[i].setScore(tmp.getScore());
			fpa[i].setPairCnt(tmp.getPairCnt());
			
		}
		
		Arrays.sort(results);
		
		
		
		             //bestnr=50 (the amount of TOP 50 or TOP 100 finds)
		for(int i=0;i<bestnr;i++){	
			//System.out.println("results["+i+"].File: "+results[i].getFileName()+ " scoree: "+results[i].getScore());
			if(i <cnt && results[i].getScore()<1000000.0){
				best[i]=results[i];
				//best[i].setScore(4.27);
			}
			else{
				best[i]= null;
			}
			
		}
		
		for(int i=0;i<results.length;i++){
			results[i]=null;//delete
		}
		
		//close the file that was supposed to be open. the i3sprogress.txt
				
		return true;
	}

	// *used by: compareDll.CompareTwo()
	boolean compareTwo(C_FingerPrint u, String fgp, C_Point2D[] orig,
			C_Point2D[] tf, C_Pair[] pairs, C_MutableVariables mv) {

		// FingerPrint
		// Point2D
		// Pair

		// FingerPrint.addReference()
		// FingerPrint.getFileName()
		// Point2D.set()
		// Affine.calcAffine()
		// FingerPrint.getElt()
		// Element.getCx, .getCy
		// Point2D.getX(), .getY()
		// FingerPrint.doAffine()
		// FingerPrint.distance(), .setScore()
		// Comapre.calcAllPotentialPairs()
		// Coampre.exhaustiveSearch()
		// FingerPrint.getCnt()
		
		//parameters
		//u= the unknown image fingerprint
		//fgp=the filename of the known image fgp that we are comparing to
		//(later)orig= the original points of the known image
		//(later)tf= the orig points transformed with affine
		
		if(fgp.isEmpty()||orig.length==0||tf.length==0||pairs.length==0){
			System.out.println("Parameter error in Comapre.comapreTwo");
			return false;
		}
		
		
		C_FingerPrint unknown= new C_FingerPrint(u);
		unknown.addReference();

	
		int index;
		for(index=0;index<cnt;index++)
			if(fpa[index].getFileName().equals(fgp))
				break;
		
		if(index==cnt){
			System.out.println("FingerPrint not found in Compare.compareTwo");
			return false;
		}
		
		double[] matrix= new double[6];
		
		
		//C_FingerPrint tmp= new C_FingerPrint(fpa[index]);
		C_FingerPrint tmp= new C_FingerPrint();
		tmp.read(fgp, null);
		tmp.addReference();
		System.out.println(tmp.getFileName()+" fgp: "+fgp);
		for(int i=0;i<tmp.getCnt();i++){
			orig[i].set(tmp.getElt(i).getCx(), tmp.getElt(i).getCy());
			System.out.println(i+": "+ (int)tmp.getElt(i).getCx()+" y: "+(int) tmp.getElt(i).getCy());
		}
			
		aff.calcAffine(tmp.ref1.getX(), tmp.ref1.getY(), 
				   tmp.ref2.getX(), tmp.ref2.getY(),
				   tmp.ref3.getX(), tmp.ref3.getY(),
				   unknown.ref1.getX(), unknown.ref1.getY(), 
				   unknown.ref2.getX(), unknown.ref2.getY(), 
				   unknown.ref3.getX(), unknown.ref3.getY(),
				   matrix);
		
		tmp.doAffine(matrix);
		tmp.distance(unknown, pairs, mv, -3);

		

		if(mv.paircnt<=0){
			tmp.setScore(1000.0);
			calcAllPotentialPairs(tmp, unknown, pairs, mv);
		}
		

		System.out.println("Call exhaust. file: "+tmp.getFileName());
		exhaustiveSearch(tmp, unknown, pairs, mv);
		System.out.println("tmp. paircnt: "+tmp.getPairCnt());
		for(int i=0;i<tmp.getCnt();i++){
			tf[i].set(tmp.getElt(i).getCx(), tmp.getElt(i).getCy());
		}
		
		
		for(int i=0;i<tmp.getCnt();i++)
		System.out.println(i+": "+ (int)tmp.getElt(i).getCx()+" y: "+(int) tmp.getElt(i).getCy());
			
		return true;
	}

	// *used by: Comapre.find(), Compare.comapreTwo()
	// used to be int paircnt
	void exhaustiveSearch(C_FingerPrint fp, C_FingerPrint unknown,
			C_Pair[] pairs, C_MutableVariables mv) {
		// FingerPrint
		// Pair
		// Element

		// FingerPrint.getElt(int)
		// Affine.calcAffine()
		// Pair.getM1(), .getM2()
		// Element.matches(), getCx(), getCy()
		// FingerPrint.doAffine(), .Distance()
		// Fingerprint.getScore()

		
		double[] matrix = new double[6];

		C_FingerPrint best = new C_FingerPrint(fp);
		C_Pair[] bestPairs = new C_Pair[100];
		
		for(int i=0;i<bestPairs.length;i++){
			bestPairs[i]=new C_Pair();
		}

		int bestPairCnt = 0;

		for (int j = 0; j < mv.paircnt - 2; j++)
			for (int k = j + 1; k < mv.paircnt - 1; k++)
				for (int l = k + 1; l < mv.paircnt; l++) {
					C_FingerPrint test = new C_FingerPrint(fp);
					
					C_Element from1 = test.getElt(pairs[j].getM1());
					C_Element from2 = test.getElt(pairs[k].getM1());
					C_Element from3 = test.getElt(pairs[l].getM1());
					C_Element to1 = unknown.getElt(pairs[j].getM2());
					C_Element to2 = unknown.getElt(pairs[k].getM2());
					C_Element to3 = unknown.getElt(pairs[l].getM2());

					// skip as many transformations in an early stage as
					// possible
					if (from1.matches(to1) == false
							|| from2.matches(to2) == false
							|| from3.matches(to3) == false)
						continue;

					if (aff.calcAffine(from1.getCx(), from1.getCy(),
							from2.getCx(), from2.getCy(), from3.getCx(),
							from3.getCy(), to1.getCx(), to1.getCy(),
							to2.getCx(), to2.getCy(), to3.getCx(), to3.getCy(),
							matrix) == false)
						continue;
					
					test.doAffine(matrix);
					
					
					C_Pair[] tmppairs= new C_Pair[100];
					for(int i=0; i<tmppairs.length;i++){
						tmppairs[i]= new C_Pair();
					}
					
					C_MutableVariables tmpmv= new C_MutableVariables();
					tmpmv.paircnt=0;
					
					test.distance(unknown, tmppairs, tmpmv, -3);
						
					//System.out.println("testScore: "+test.getScore()+ " bestScore: "+best.getScore() + " mv.p "+mv.paircnt+ " tmp.p "+tmpmv.paircnt);
					if(test.getScore()<best.getScore()){
						System.out.println("Success");
						bestPairs=tmppairs;
						bestPairCnt=tmpmv.paircnt;
						best=test;
					}
				}
		
		fp=best;
		if(bestPairCnt!=0){
			mv.paircnt=bestPairCnt;
			pairs=bestPairs;
		}
		System.out.println("end Exhaust");
	}

	// *used by: Compare.find(), Coampre.compareTwo() //used to be int paircnt
	void calcAllPotentialPairs(C_FingerPrint from, C_FingerPrint to,
			C_Pair[] pairs, C_MutableVariables mv) {
		// Point2D.getSqrDist(Point2D)
		// Element.Matches(Element), .sqrDist(Element)
		// Pair.set()
		
		//from = known
		//to= unknown

		// calc maximum distance between ref points
		double d12 = from.ref1.getSqrDist(from.ref2);
		double d13 = from.ref1.getSqrDist(from.ref3);
		double d23 = from.ref2.getSqrDist(from.ref3);
		double maxSqDist = d12;

		if (d13 > maxSqDist)
			maxSqDist = d13;
		if (d23 > maxSqDist)
			maxSqDist = d23;
		maxSqDist = maxSqDist / 16.0; // maximum distance is only 25% of the
										// maximum distance between ref points
										// because of working with squares
										// divide y 4^2

		mv.paircnt = 0;


		for (int i = 0; i < from.cnt; i++) {
			double close1 = 1000000000.0;
			double close2 = 1000000000.0;
			int index1 = -1;
			int index2 = -1;

			for (int j = 0; j < to.cnt; j++) {
				if (from.elt[i].matches(to.elt[j]) == false)
					continue;
				double d = from.elt[i].sqrDist(to.elt[j]);
				if(d > maxSqDist)
					continue;
				if (d < close1) {
					close2 = close1;
					index2 = index1;
					close1 = d;
					index1 = j;
				} else if (d < close2) {
					close2 = d;
					index2 = j;
				}
			}

			if (index1 > -1) {
				pairs[mv.paircnt].set(i, index1, close1);
				mv.paircnt++;
			}
			if (index2 > -1) {
				pairs[mv.paircnt].set(i, index2, close2);
				mv.paircnt++;
			}

		}

	}// end of calcallPotentilaPairs()

	// cpp
	// *used by compare.init()
	boolean collectFingerprints(ArrayList<String> allfiles, StringBuffer mess) {
		// Compare.descendAll()
		// Comapre.userMessage()

		if (System.getenv("I3SM_DATA").isEmpty()) {
			System.out.println("Could not find the environment variable in CollectFingerPrints()");
			return false;
		} 

			String topdir = System.getenv("I3SM_DATA");// get the value of this
														// environment variable
														// this will equal= "C:\Data"
			
			ArrayList<File> fgps = new ArrayList<File>();
			File topFolder = new File(topdir); // Directory is just a list of files
			
			descend(topFolder, fgps);//daniel, add a noMatchingImageFile check on these fgps found
			
			for(int i=0;i<fgps.size();i++){
				fgpFiles.add(fgps.get(i).toString());
			}
			
			return true;
		
	}

	// *used by: compare.init()
	// path = the file being looked at, sex=0, view=null....
	void analysePath(String path, C_MutableVariables mv) {

		mv.sex = sexDefs.UNKNOWN;
		mv.view = "";


		ArrayList<String> tokens = new ArrayList<String>();// tokens are the
															// seperated pieces
															// of each path. so
															// for
															// C:/DATA/Male/Whale1/Whale1.jpg
															// the tokens would
															// be: DATA, Male,
															// Whale1, Whale1.jpg

		char[] token = new char[50];
		int j = 0;// used for char[] token indexing
		for (int i = 0; i < path.length(); i++) {
			if (j > 49) {// dont want to go bigger than the size of char[]
							// token.
				System.out.println("Error in length of Data Pathname. size too large");
				return;
			}
			if (path.charAt(i) == '\\') {
				tokens.add(String.copyValueOf(token, 0, j));
				j = 0;
			} else {
				token[j] = path.charAt(i);
				j++;
			}
			if(i==path.length()-1){
				tokens.add(String.copyValueOf(token, 0, j));
				j = 0;
			}
		}
		
		
		// the "data" of tokens will contain: ex: "C:, Data, Male, whale1.jpg"

		if (tokens.size() < 3)//Daniel, not sure why this is here.... would it be so bad for a path to be short?
			return;

		String gender = tokens.get(2);
		gender = gender.toLowerCase();
		if (gender.equals("male")) {
			mv.sex = sexDefs.MALE;
			mv.view = "";
		} else if (gender.equals("female")) {
			mv.sex = sexDefs.FEMALE;
			mv.view = "";
		}

	}
	

	public static void descend(File root, ArrayList<File> fgpOnly)
	{
	    if(root == null || fgpOnly == null) return; //just for safety   

	    if(root.isDirectory())
	    {
	        for(File file : root.listFiles())
	            descend(file, fgpOnly);
	    }
	    else if(root.isFile() && root.getName().endsWith(".fgp"))
	    {
	        fgpOnly.add(root);
	    }
	}
	

	// *used by comapre.descendAll()
	boolean noMatchingImageFile(String fgp, ArrayList<String> all) {

		String f = null;
		String ext = null;

		// daniel, I dont understand this below part, so not sure if want to
		// keep
		// if(fgp.length()<5)
		// return true;


		return true;
	}

	// *used by: comapre.collectFingerPrints()
	int userMessage(char[] title, char[] mess) {
		return 1;
	}

}
