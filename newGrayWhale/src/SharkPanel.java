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

/*******************************************************************************
 *   Changes since 1.0:                                                         *
 *   In 1.1 a slightly different approach is chosen. Instead of a standard     *
 *   comparison space each known individual is now mapped onto the space of    *
 *   the unknown individual. As a consequence it was necessary to give the c++ *
 *   code information about the reference points and to exchange the original  *
 *   spots of the unknown individual instead of the transformed spots.         *
 *                                                                             *
 *   Changes since 1.1:                                                        *
 *   Considerable changes in the GUI (zoom functionality, toolbar). Relevant   *
 *   controls and information for the user are now displayed in the toolbar.   *
 *   First step made towards multi-threaded searching using the SwingWorker    *
 *   class. This was also required for display of the progress bar.            *
 *                                                                             *
 *   Changes since 2.0                                                         *
 *   Complete redesign of the interface introducing ellipse shaped spot        *
 *   annotation.                                                               *
 *******************************************************************************/

import GrayWhale.Compare.*;
import java.awt.*;
import javax.swing.*;

import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.beans.*;


class SharkPanel extends JPanel implements Printable, PropertyChangeListener {
    final static int MIN_ELT_CNT   = 1;
    final static int MAX_ELT_CNT   = 80;
    final static int NR_OF_MATCHES = 50;
	final static int STEPSIZE      = 2;			// stepsize to move objects around by arrow keys
	final static double WHEEL_ZOOM_STEPSIZE = 1.05;
    
    ShowResults showRes = null;
	CompareResults cr = null;
	SharkPanel sp;	// translation of this for internal class

	private I3SM i3s = null;
    private String i3sDataDir = null;
	private FingerPrint fp = null;
    private Element copiedElt;
	private ActionAdmin aa;
	private MetaDataList mdl;
	
	// display variables
    private int xOffset, yOffset;
    private double factor;
    private double zoom = -1;
	private int origWidth  = -1;
	private int origHeight = -1;

	// interaction variables
    private boolean dragMode = false;
	private boolean objectReplaced = false;
	private boolean refPointEmpty = false;
	private int selectedObj = -1;
	private int selectedControl = -1;

    private String results[] = null;
    
    private boolean fileSaved;
    private boolean eltSelection;
    private Image img;
	
	private StringBuffer control1, control2, control3;
	private MonitorProgress monitor = null;
	
	public C_compareDll cd= new C_compareDll();
	public C_Compare c= new C_Compare();
	
	

    /* external C++ function definitions
    public native String[] scoreShark(char instr[], double ref[], double data[], double scores[], int nrpairs[]);
    public native boolean  compareTwo(String s,
                                      double ref[], double data[], 
                                      double matchx[], double matchy[], double origx[], double origy[],
                                      int pairs[]);
    public native void     initCompare();
    public native void     closeCompare();

    // loading of external C++ dll
    static {
        try {
            //System.loadLibrary("i3sm_compare");
        	System.load("C:/Program Files (x86)/I3SM2.1/i3sm_compare.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Could not load library i3sm_compare.dll.\n");
			JOptionPane.showMessageDialog(null, "Could not load library i3sm_compare.dll.\n");
			System.exit(0);
        }
    }
    */

	// monitors the progress in the corresponding file. Each progress is step is reported via propertyChangeListener
	// using this class is the only way to get a proper sync on the progress bar
    class MonitorWorker extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
			Reader in = null;
	
			String topdir = System.getenv("I3SM_INSTALL");
			if(topdir == null || topdir == "")
				topdir = System.getenv("I3SM_DATA");
	
			try {
				Thread.sleep(250);
				in = new FileReader(topdir + "/i3s_progress.txt");
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Cannot get progress data. Search continues without a progress bar.");
				return null;
			}
	
			int ch = 0;
			int fffff=0;
			while(ch != 65+21) {
				try {
					ch = in.read();
				} catch(Exception e) { ch = -1; };
	
				try {
					if(ch == -1)
						Thread.sleep(150);
						
				} catch(InterruptedException ignore) {}
				if(ch != -1) {
					setProgress((ch-65)*5);
				}
			}
			try {
				in.close();
			} catch(Exception ignore) { 
				JOptionPane.showMessageDialog(null, "Could not close progress file. Please report this bug to i3s@reijns.com.");
			}
	
			try {
				File f = new File(topdir + "/i3s_progress.txt");
				if(f.exists())
					f.delete();
			} catch(Exception ignore) {};
			
			return null;
        }
    }

	// put the actual dll-call in a specific class to allow for threaded-calls
	// needed for distributing the database search over multiple threads (not yet implemented) and
	// for showing a proper progress bar
	public class DllCall extends SwingWorker<Void, Void> {
		MetaDataCompareList mdcl;
		
		double data[];
		double ref[];
		double scores[];
		char instr[];
		int nrpairs[]; 
		int[] x;
		int y;
		String newInstr;

		public DllCall(char  _instr[], MetaDataCompareList _mdcl, double _data[], double _ref[], double _scores[], int _nrp[]) {
			super();
			data = _data;
			ref = _ref;
			scores = _scores;
			nrpairs = _nrp;
			instr = _instr;
			mdcl = _mdcl;
		}

        @Override
        public Void doInBackground() throws IOException {
        	System.out.println("trace--Score Shark Void doInBackGround");//daniel remove
        	results = cd.scoreWhale(x, y, instr, ref, data, scores, nrpairs);
        	System.out.println("\nScore shark done");
			return null;
		}

        @Override
		public void done() {
			try {
				Thread.sleep(250);
			} catch(Exception ignore) {}
			monitor.exit();
			System.out.println("trace-- SharkPanel.DllCall. done()");
			filterResults(results, scores, nrpairs, mdcl);
	        showRes = new ShowResults(sp, results, scores, nrpairs);
		}
	}                
	
    public SharkPanel(I3SM _i3s, String _i3sDataDir) {
    	System.out.println("SharkPanel");//Daniel Remove

		i3s = _i3s;
		sp = this;
        i3sDataDir = _i3sDataDir;
		fp = new FingerPrint(i3s);
        copiedElt = null;

        fileSaved = true;
        eltSelection = false;
        img = null;

		getMetaDataList();

		aa = new ActionAdmin();

        addMouseListener(new MouseClickListener());
        addMouseMotionListener(new MouseMotionListener());		
		addMouseWheelListener(new MyMouseWheelListener());
       System.out.println("trace-- initCompare SharkPanel()");
		cd.initCompare();      // read database with all fingerprints
		
    }
	
	public void getMetaDataList() {
		MetaDataXML metadata = new MetaDataXML(System.getenv("I3SM_DATA") + "/metadata.xml");
		mdl = metadata.getMetaDataList(metadata.getSpeciesIndex());
		setControlStrings(metadata);
	}	
	public void setControlStrings(MetaDataXML md) {
		control1 = new StringBuffer();
		control2 = new StringBuffer();
		control3 = new StringBuffer();
		md.getNamesControlPoints(control1, control2, control3);
		repaint();
	}
	public void filterResults(String[] results, double[] scores, int[] nrpairs, MetaDataCompareList mdcl) {
		for(int i=0; i<results.length && scores[i] < 1000000; i++) {
			FingerPrint fptmp = new FingerPrint(i3s);
			StringBuffer mess = new StringBuffer();

			System.out.println("Filename: Results["+i+"]: "+results[i] + " s: " +scores[i] +" np:" +nrpairs[i]);
			
			if(fptmp.read(results[i], mess) == false) {
				JOptionPane.showMessageDialog(i3s.getFrame(), "Error while filtering results. Please report this bug to i3s@reijns.com");
				return;
			}

			if(fptmp.match(mdcl))
				continue;

			// current result must be removed
			int j = 0;
			for(j=i; j<results.length-1 && scores[j] < 1000000; j++) {
				results[j] = results[j+1];
				scores[j]  = scores[j+1];
				nrpairs[j] = nrpairs[j+1];
			}
			scores[j] = 10000000;
			i--;
		}
	}
	public String[] getViews() {
		return i3s.getViews(); 
	}
    public void closeOnExit() {
        System.out.println("trace-- closeCompare SharkPanel closeOnExit");//daniel remove
    	cd.closeCompare();
    }
    public void updateDatabase() {
        cd.closeCompare();
        cd.initCompare();
        System.out.println("trace-- initCompare and closeCompare SharkPanel updatedatabase()");//daniel Remove
    }
    public void toggleEltSelection() {
		eltSelection = !eltSelection;
    }
	public void copySelected() {
		if(selectedObj == -1 || img == null)
			return;
		copiedElt = fp.get(selectedObj).copy();
	}
	public void pasteSelected() {
		if(copiedElt == null || img == null || fp.getCnt() == MAX_ELT_CNT)
			return;
		aa.addAction(fp);
		copiedElt.newLocation(img.getWidth(this), img.getHeight(this), zoom);
		fp.add(copiedElt.copy());
		repaint();
	}
	public void deleteSelected() {
		if(selectedObj == -1 || img == null)
			return;
		aa.addAction(fp);
		fp.removeElement(selectedObj);
		repaint();
	}
    public void moveSelected(int dx, int dy) {
        if(selectedObj == -1 || img == null)
            return;
		aa.addAction(fp);
        fp.get(selectedObj).move(dx*STEPSIZE, dy*STEPSIZE);
        repaint();
    }    
	public void undoAction() {
		aa.undoAction(fp);
		if(fp.getCnt() <= selectedObj)
			selectedObj = -1;
		selectedControl = -1;
        repaint();
	}
	public void removeAction() {
		aa.removeAction(fp);
		if(fp.getCnt() <= selectedObj)
			selectedObj = -1;
		selectedControl = -1;
        repaint();
	}
	public void redoAction() {
		aa.redoAction(fp);
        repaint();
	}
    public void killResultWindows() {
        if(showRes != null) {
            showRes.dispose();
            showRes = null;
        }
		if(cr != null) {
			cr.dispose();
			cr = null;
		}
    }
    public boolean insertInDatabase(StringBuffer mess) {
        if(!imageOk()) {
            mess.append("Nothing to insert in database. Open an image first.");
            return false;
        }
        if(!fp.refPointsOk()) {
            mess.append("Unsufficient data, point out all three reference points first.");
            return false;
        }
        if(fp.getCnt() < MIN_ELT_CNT) {
            mess.append("Unsufficient data, point out at least " + MIN_ELT_CNT + " spots first.");
            return false;
        }
        if(fp.write(mess) == false)
            return false;
		aa.clearActionBuffer();
        changeMade(false);
        repaint();

        new InsertDatabase(this, fp.getFileName());

        return true;
    }

    public boolean compareWithDatabase(String instr, MetaDataCompareList mdcl, StringBuffer mess) {
        if(!imageOk()) {
            mess.append("No data to compare. Open an image first.");
            return false;
        }
        if(!fp.refPointsOk()) {
            mess.append("Point out the control points first.\nWithout these points a comparison is not possible.");
            return false;
        }

        if(fp.getCnt() < MIN_ELT_CNT) {
			if(MIN_ELT_CNT == 1)
				mess.append("Insufficient input. At least one spot is required.\nNo comparison has been made.");
			else	
				mess.append("Insufficient input. At least " + MIN_ELT_CNT + " spots are required.\nNo comparison has been made.");
            return false;
        }

        double data[] = new double [fp.getCnt()*10];
		double ref[] = new double[6];
		
		int arraySize = 1000;
		if(I3SMPrefs.getTop50())
			arraySize = NR_OF_MATCHES;
				
        double scores[] = new double [arraySize];
        int nrpairs[] = new int [arraySize];

		fp.copyData(data, ref);

        monitor = new MonitorProgress();
		DllCall dc = new DllCall(instr.toCharArray(), mdcl, data, ref, scores, nrpairs);
		dc.execute();

		MonitorWorker task = new MonitorWorker();
		task.addPropertyChangeListener(this);
		task.execute();

        return true;
    }
    /**
     * Invoked when MonitorWorker's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            monitor.setValue(progress);
        } 
    }
    
    public void doVisualComparison(int index, double score) {
        double data[]  = new double [fp.getCnt()*10];
		double ref[] = new double [6];
		fp.copyData(data, ref);
        
        double matchx[] = new double [1000];
        double matchy[] = new double [1000];
        double origx[]  = new double [1000];
        double origy[]  = new double [1000];
        int pairs[]     = new int [1000];

        
        System.out.println("trace--compareTwo doVisualComparison");//daniel remove
        if(cd.compareTwo(results[index], ref, data, matchx, matchy, origx, origy, pairs) == false) {
            JOptionPane.showMessageDialog(i3s.getFrame(), "Error while making comparison. Please report this bug to i3s@reijns.com");
            return;
        }

        Point2D match[] = new Point2D [1000];        
        Point2D orig[] = new Point2D [1000];
		Point2D spots[] = new Point2D [fp.getCnt()+3];
		
		spots[0] = new Point2D(fp.getRef1());
		spots[1] = new Point2D(fp.getRef2());
		spots[2] = new Point2D(fp.getRef3());
		
		for(int i=0; i<fp.getCnt(); i++) {
			spots[i+3] = new Point2D(data[i*10],data[i*10+1]);
		}
        for(int i=0; i<1000; i++) {
            if(origx[i] < -1000000)
                orig[i] = new Point2D();
            else
                orig[i] = new Point2D(origx[i], origy[i]);
            
            if(matchx[i] < -1000000)
                match[i] = new Point2D();
            else
                match[i] = new Point2D(matchx[i], matchy[i]);
        }

        cr = new CompareResults(this, fp.getFileName(), results[index], spots, orig, match, pairs, score);
    }
    
    public void close() {
        img = null;
        selectedObj = -1;
		selectedControl = -1;
		copiedElt = null;

        fp.reset();
		i3s.setNrSpots(0);
        changeMade(false);
        killResultWindows();
		i3s.getFrame().setTitle("I3SGW: Intelligent Individual Identification System (Gray Whale)");
		aa.clearActionBuffer();
		setPreferredSize(new Dimension(1024,756));
		revalidate();
        repaint();
    }
    public void editCommentField() {
        if(!imageOk())
        {
            JOptionPane.showMessageDialog(i3s.getFrame(), "Open an image first.");
            return;
        }
		new MetaDataInput(fp, mdl, this);
	}
    public boolean updateImage(String filename, StringBuffer mess) {
		ImageIcon imageIcon = new ImageIcon(filename);
        
        File ftest = new File(filename);
        if(ftest.exists() == false) {
            mess.append("File " + filename + " does not exist");
            return false;
        }
        String test = filename.toLowerCase();
        if(test.endsWith(".jpg") || test.endsWith(".jpeg") ||test.endsWith(".gif"))
			filename = new String(new StringBuffer(filename.substring(0, filename.lastIndexOf('.'))).append(".fgp"));
        else
        {
            mess.append("Only files with extension '.jpg' or '.gif' are supported");
            return false;
        }
        
        img = imageIcon.getImage();
        fp.reset();
		i3s.setNrSpots(0);
        changeMade(false);
		aa.initActionBuffer();
        
        if(fp.read(filename, mess) == false)
            return false;
		i3s.setNrSpots(fp.getCnt());
		zoom = -1;
        calcDisplayFactor();
        repaint();
        
        return true;
    }
    
    public boolean writeFingerprint(StringBuffer mess) {
		if(fp.write(mess) == false)
			return false;
		aa.clearActionBuffer();

        changeMade(false);
		repaint();
		return true;
	}
	private class MyMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getWheelRotation() < 1)
				setZoom(WHEEL_ZOOM_STEPSIZE);//zoom in
			else
				setZoom(1 / WHEEL_ZOOM_STEPSIZE);//zoom out
		}
	}
    private class MouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged (MouseEvent e) {
			try {
				if(img == null || eltSelection == false)
					return;

				double x = (e.getX()-xOffset) / factor;
				double y = (e.getY()-yOffset) / factor;

				if(e.isControlDown()) {		// object selection
					if(objectReplaced == false) {
						aa.addAction(fp);
						objectReplaced = true;
					}
					if(selectedObj == -1)
						return;
					changeMade(true);
					if(selectedControl == -1)
						fp.get(selectedObj).setCenter(x, y);
					else
						fp.get(selectedObj).adjustControl(selectedControl, new Point2D(x, y));
				} else {	// creating new objects
					if(dragMode) 
						return;

					fp.get(fp.getCnt()-1).set(x, y);
					changeMade(true);
				}
				repaint();
			} catch(Exception exc) {
				JOptionPane.showMessageDialog(null, "Unexpected error. Exiting...");
				i3s.exit();
			}
        }
    }
    private class MouseClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
			if(!imageOk() || e.getButton() != e.BUTTON1)
				return;
			if(eltSelection == false) {
				JOptionPane.showMessageDialog(i3s.getFrame(), "Please activate 'Element selection' first, using the button in the toolbar or press ALT+P.");
				return;
			}

			double x = (e.getX()-xOffset) / factor;
			double y = (e.getY()-yOffset) / factor;

			if(e.isControlDown()) {
				dragMode = true;
				selectNearestClick(x, y);
			}
			else {		// new ellipse
				if(fp.refPointEmpty())
					refPointEmpty = true;
				else
					if(fp.getCnt() == MAX_ELT_CNT) {
						JOptionPane.showMessageDialog(i3s.getFrame(), new StringBuffer("Maximum number of elements is reached."));
						return;
					}
				aa.addAction(fp);
				fp.add(x, y);
				changeMade(true);
				i3s.setNrSpots(fp.getCnt());
			}
			repaint();
        }
        public void mouseReleased(MouseEvent e) {
			dragMode = false;
			objectReplaced = false;
			repaint();
        }
        public void mouseClicked(MouseEvent e) {
            if(!imageOk())
                return;
            if(eltSelection == false) {
                JOptionPane.showMessageDialog(i3s.getFrame(), "Please activate 'Element selection' first, using the button in the toolbar or press ALT+P.");
                return;
            }

            double x = (e.getX()-xOffset) / factor;
            double y = (e.getY()-yOffset) / factor;
            
            if(e.getButton() == e.BUTTON1 && e.isControlDown()) {
				selectNearestClick(x, y);
			}
            if(e.getButton() == e.BUTTON1 && e.isControlDown() == false && refPointEmpty == false) {
				fp.removeElement(fp.getCnt()-1);
				removeAction();
			}
            if(e.getButton() == e.BUTTON3 || e.getButton() == e.BUTTON2) {
				aa.addAction(fp);
				fp.removeNearest(x, y);
				changeMade(true);
				i3s.setNrSpots(fp.getCnt());
			}
			refPointEmpty = false;

            repaint();
        }
    }
    public void selectNearestClick(double x, double y) {
        double mindist = Double.POSITIVE_INFINITY;
		selectedObj = -1;
		selectedControl = -1;
		
        for(int i=0; i<fp.getCnt(); i++)
        {
            double dist = fp.get(i).sqrDist(x, y);
            if(dist < mindist && dist < 200)
            {
                mindist = dist;
                selectedObj = i;
				selectedControl = -1;
            }
			Point2D ptmp = fp.get(i).getNearestPoint(x, y);
			dist = ptmp.sqrDist(x, y);
            if(dist < (mindist+10) && dist < 200)		// +10 om control points lichte voorkeur te geven boven centrale punten
            {
                mindist = dist;
                selectedObj = i;
				selectedControl = fp.get(i).getIndex(ptmp);
            }

        }
    }
    public void removeNearestClick(double x, double y) {
		if(fp.isInitialized() == false)
			return;
		aa.addAction(fp);
		fp.removeNearest(x, y);
		selectedObj = -1;
		selectedControl = -1;
		repaint();
	}

	public void printFile()	{
		if(!imageOk()) {
			JOptionPane.showMessageDialog(i3s.getFrame(), "Open an image first.");
			return;
		}
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(this);
		if(pj.printDialog()) {
			try {
				pj.print();
			} catch(Exception exc) {
				JOptionPane.showMessageDialog(i3s.getFrame(), "An unexpected error occured while printing.");
			}
		}
	}
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0 || !imageOk()) {
            return Printable.NO_SUCH_PAGE;
        }
		
		try {
			Graphics2D g2 = (Graphics2D)g;

			setBackground(new Color(204, 204, 204));

			double scalex = pf.getImageableWidth() / img.getWidth(this);
			double scaley = (pf.getImageableHeight()-72) / img.getHeight(this);
			int offsetX = (int) (img.getWidth(this)*scalex*0.025);
			if(scalex < scaley)
				scaley = scalex;
			else
				scalex = scaley;
			scalex = scalex*0.95;
			scaley = scaley*0.95;

			g2.drawImage(img, (int) pf.getImageableX()+offsetX, (int) pf.getImageableY()+72, 
						 (int) (img.getWidth(this)*scalex), (int) (img.getHeight(this)*scaley), Color.white, this);
			fp.print(g2, scalex, scaley, (int) pf.getImageableX()+offsetX, (int) pf.getImageableY()+72, img.getHeight(this)); 
		} catch(Exception exc) {
			JOptionPane.showMessageDialog(i3s.getFrame(), "An unexpected error occured while printing (" + exc.toString() + ").");
		}

		return Printable.PAGE_EXISTS;
    }
  	public void paintComponent(Graphics g) {
        super.paintComponent(g);
     	Graphics2D g2d = (Graphics2D) g;

        if(!imageOk()) {
			ImageIcon imageBgIcon = new ImageIcon(this.getClass().getResource("images/i3scbg1920x1080.gif"));
			int w = getWidth();
			int h = getHeight();
			g2d.drawImage(imageBgIcon.getImage(), 0, 0, w, h, (1920-w)/2, (1080-h)/2, (1920+w)/2, (1080+h)/2, Color.white, this);
			return;
		}

        g2d.drawImage(img, xOffset, yOffset, (int) (img.getWidth(this)*factor), (int) (img.getHeight(this)*factor), Color.white, this);

       	g2d.setColor(Color.red);
		fp.paint(g2d, factor, xOffset, yOffset, eltSelection, selectedObj, control1.toString(), control2.toString(), control3.toString());
    }
	public void setZoom(double _z) {
		if(!imageOk())
			return;
		adaptZoom(zoom * _z);
		calcDisplayFactor();
	}
	private void adaptZoom(double _z) {
		zoom = _z;
		i3s.setZoomPercentage(zoom);
	}
    private void calcDisplayFactor() {
		if(origWidth == -1) 
			origWidth = getWidth();
		if(origHeight == -1) 
			origHeight = getHeight();
		
        if(!imageOk())
            return;

        double iw = (double) img.getWidth(this);
        double ih = (double) img.getHeight(this);

		if(iw/ih < (double) origWidth / (double) origHeight) {
			factor = origHeight / ih;
			xOffset = (int)(((double) origWidth - iw*factor)/2.0);
			yOffset = 5;
		}
		else {
			factor = origWidth / iw;
			xOffset = 5;
			yOffset = (int)(((double) origHeight - ih*factor)/2.0);
		}

		if(zoom > 0 && zoom < factor / (I3SM.ZOOM_STEPSIZE*I3SM.ZOOM_STEPSIZE))
			adaptZoom(factor / (I3SM.ZOOM_STEPSIZE*I3SM.ZOOM_STEPSIZE));
		if(zoom > 9.99)
			adaptZoom(9.99);
		if(zoom / factor > 1/(WHEEL_ZOOM_STEPSIZE - 0.01) && zoom / factor < (WHEEL_ZOOM_STEPSIZE - 0.01))
			zoom = factor;

		if(zoom > 0) {
			factor = zoom;
			xOffset = (int)(((double) origWidth - iw*factor)/2.0);
			if(xOffset < 0)
				xOffset = 5;
			yOffset = (int)(((double) origHeight - ih*factor)/2.0);
			if(yOffset < 0)
				yOffset = 5;
		}
		else 		// only very first time when zoom is initialized to -1
			adaptZoom(factor);

		setPreferredSize(new Dimension((int)(iw*factor + xOffset + 5), (int)(ih*factor + yOffset + 5)));
		revalidate();
		repaint();
    }
    
	public void changeMade(boolean b) {
		i3s.setExclamation(b);
		fileSaved = !b;
	}
    public boolean getFileSaved() {
        return fileSaved;
    }
	public boolean imageOk() {
		return img != null;
	}
}

