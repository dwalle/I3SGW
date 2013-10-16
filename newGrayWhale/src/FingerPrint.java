/*******************************************************************************
 *   Changes since 1.0:                                                         *
 *   In 1.1 a slightly different approach is chosen. Instead of a standard     *
 *   comparison space each known individual is now mapped onto the space of    *
 *   the unknown individual. As a consequence it was necessary to give the c++ *
 *   code information about the reference points and to exchange the original  *
 *   elts of the unknown individual instead of the transformed elts.         *
 *                                                                             *
 *   Changes since 1.1:                                                        *
 *   Considerable changes in the GUI (zoom functionality, toolbar). Relevant   *
 *   controls and information for the user are now displayed in the toolbar.   *
 *   First step made towards multi-threaded searching using the SwingWorker    *
 *   class. This was also required for display of the progress bar.            * 
*******************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;


class FingerPrint {
	private I3SM i3s;
    private Element elts[];
    private Point2D refPoint1;
    private Point2D refPoint2;
    private Point2D refPoint3;
	private StringBuffer comment = null;
	private String filename;
	private MetaDataList mdl;
	private int eltcnt;

    public FingerPrint(I3SM _i) {
    	System.out.println("FingerPrint");//Daniel Remove
		i3s = _i;
		reset();
    }
	public void reset() {
        eltcnt = 0;
		mdl = new MetaDataList();
        elts = new Element[SharkPanel.MAX_ELT_CNT+1];
		comment = new StringBuffer();
        refPoint1 = new Point2D();
        refPoint2 = new Point2D();
        refPoint3 = new Point2D();
		filename = null;
	}
    public boolean read(String fname, StringBuffer mess) {
		boolean res = true;
		reset();
		filename = fname;
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(filename));
			res = readData(in, mess);
			in.close();
        } catch(FileNotFoundException fnf) {
            mess.append("Could not open " + filename + " for reading.");
        } catch(IOException e) {
            mess.append("Error while reading from " + filename);
            return false;
        }

        return res;
    }
    public boolean readData(DataInputStream in, StringBuffer mess) {
        try {
            byte id[] = new byte [4];
            in.read(id, 0, 4);
			int version = versionOk(id);

            if(version == -1)
            {
                mess.append("Unknown type of fingerprint file " + filename);
                return false;
            }
            if(version < 2)
            {
                mess.append("Fingerprint file "  + filename + " is from standard I3S and cannot be used.");
                return false;
            }

            refPoint1.setX(in.readDouble());
            refPoint1.setY(in.readDouble());
            refPoint2.setX(in.readDouble());
            refPoint2.setY(in.readDouble());
            refPoint3.setX(in.readDouble());
            refPoint3.setY(in.readDouble());

            eltcnt = in.readInt();

            for (int i = 0; i < eltcnt; i++) {
				elts[i] = new Element();
                elts[i].read(in);
			}

			// read commentfield
			int strlen = in.readInt();
			byte[] buf = new byte[strlen];
			in.read(buf, 0, strlen);
			comment = new StringBuffer(new String(buf));
			
			if(version >= 3) {
				if(mdl.readData(in) == false) {
					mess.append("Error while reading metadata\n");
					throw new IOException();
				}
			}
        } catch(IOException e) {
			reset();
            mess.append("Error while reading from " + filename);
            return false;
        }

        return true;
    }

    public boolean write(StringBuffer mess) {
		if(filename == null) {
            mess.append("Invalid file name.\nNo data has been written.");
            return false;
		}
        if(!refPointsOk()) {
            mess.append("Point out the control points first.\nNo data has been written.");
            return false;
        }

        if(eltcnt < SharkPanel.MIN_ELT_CNT) {
            mess.append("Insufficient input. At least " + SharkPanel.MIN_ELT_CNT + " elements are required.\nNo data has been written.");
            return false;
        }

		boolean res;
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
			res = writeData(out, mess);
			out.close();
        } catch(FileNotFoundException fnf) {
            mess.append("Could not open " + filename + " for writing.");
            return false;
		} catch(IOException e) {
            mess.append("Error while writing to " + filename);
            return false;
		}

		return res;
	}
	
    public boolean writeData(DataOutputStream out, StringBuffer mess) {
        try {
            byte id[] = new byte[4];
            id[0] = 'I'; id[1] = 'm'; id[2] = '2'; id[3] = 'a';
            out.write(id, 0, 4);
            
            out.writeDouble(refPoint1.getX());
            out.writeDouble(refPoint1.getY());
            out.writeDouble(refPoint2.getX());
            out.writeDouble(refPoint2.getY());
            out.writeDouble(refPoint3.getX());
            out.writeDouble(refPoint3.getY());
            out.writeInt(eltcnt);
    
            for (int i = 0; i < eltcnt; i++)
				elts[i].write(out);

			// write comment field
			out.writeInt(comment.length());
			out.write(comment.toString().getBytes(), 0, comment.length());
			// write meta data
			if(mdl.writeData(out) == false) {
				mess.append("Error while writing metadata\n");
				throw new IOException();
			}
        } catch(IOException e) {
            mess.append("Error while writing to " + filename);
            return false;
        }
        
        return true;
    }
	public boolean match(MetaDataCompareList mdcl) {
		return mdcl.match(mdl);
	}
	public void add(Element e) {
		if(eltcnt >= SharkPanel.MAX_ELT_CNT)
			return;
		elts[eltcnt] = e;
		eltcnt++;
	}
	
	public void add(double x, double y) {
		if(refPoint1.isInitialized() == false) {
			refPoint1.setX(x);
			refPoint1.setY(y);
		} else if(refPoint2.isInitialized() == false) {
			refPoint2.setX(x);
			refPoint2.setY(y);
		} else if(refPoint3.isInitialized() == false) {
			refPoint3.setX(x);
			refPoint3.setY(y);
		} else {
			if(eltcnt >= SharkPanel.MAX_ELT_CNT)
				return;
			elts[eltcnt] = new Element(x, y, x, y);
			eltcnt++;
 		}	
	}
    public void removeNearest(double x, double y) {
        double mindist = Double.POSITIVE_INFINITY;
        int index = -1;

        for(int i=0; i<eltcnt; i++)
        {
            double dist = (x - elts[i].cx)*(x - elts[i].cx) + (y - elts[i].cy)*(y - elts[i].cy);
            if(dist < mindist)
            {
                mindist = dist;
                index = i;
            }
        }

        double dist1 = (x - refPoint1.getX())*(x - refPoint1.getX()) + (y - refPoint1.getY())*(y - refPoint1.getY());
        double dist2 = (x - refPoint2.getX())*(x - refPoint2.getX()) + (y - refPoint2.getY())*(y - refPoint2.getY());
        double dist3 = (x - refPoint3.getX())*(x - refPoint3.getX()) + (y - refPoint3.getY())*(y - refPoint3.getY());

        Point2D tmp = refPoint1;
        if(dist2 < dist1) {
            dist1 = dist2;
            tmp = refPoint2;
        }
        if(dist3 < dist1) {
            dist1 = dist3;
            tmp = refPoint3;
        }

        if(dist1 < mindist)
            tmp.reset();
        else
			removeElement(index);
    }
	public void removeElement(int index) {
		if(index < 0 || index >= eltcnt)
			return;

        for(int i=index; i<eltcnt-1; i++)
			elts[i] = elts[i+1].copy();
        eltcnt--;
		elts[eltcnt] = null;
    }
	
  	public void paint(Graphics2D g, double factor, int xOffset, int yOffset, boolean dotSelection, int highlight,
					  String control1, String control2, String control3) {
       	g.setColor(new Color(I3SMPrefs.getReferenceRed(), I3SMPrefs.getReferenceGreen(), I3SMPrefs.getReferenceBlue()));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(i3s != null)
			i3s.setMessage("");

        if(refPoint1.isInitialized()) {
            g.drawString(control1, (int) (refPoint1.getX()*factor) + 10 + xOffset, (int) (refPoint1.getY()*factor) - 20 + yOffset);
            g.drawOval((int)(refPoint1.getX()*factor) - 10 + xOffset, (int)(refPoint1.getY()*factor) - 10 + yOffset, 20, 20);
            g.fill(new Rectangle2D.Double((refPoint1.getX()*factor)-1 + xOffset, (refPoint1.getY()*factor)-1 + yOffset, 3, 3));
        } else if(dotSelection) {
			if(i3s != null)
				i3s.setMessage("Point out " + control1);
        }
        if(refPoint2.isInitialized()) {
            g.drawString(control2, (int) (refPoint2.getX()*factor) + 10 + xOffset, (int) (refPoint2.getY()*factor) - 20 + yOffset);
            g.drawOval((int)(refPoint2.getX()*factor) - 10 + xOffset, (int)(refPoint2.getY()*factor) - 10 + yOffset, 20, 20);
            g.fill(new Rectangle2D.Double(refPoint2.getX()*factor-1 + xOffset, refPoint2.getY()*factor-1 + yOffset, 3, 3));
        } else if(refPoint1.isInitialized() && dotSelection) {
			if(i3s != null)
				i3s.setMessage("Point out " + control2);
        }
        if(refPoint3.isInitialized()) {
            g.drawString(control3, (int) (refPoint3.getX()*factor) + 10 + xOffset, (int) (refPoint3.getY()*factor) + 20 + yOffset);
            g.drawOval((int)(refPoint3.getX()*factor) - 10 + xOffset, (int)(refPoint3.getY()*factor) - 10 + yOffset, 20, 20);
            g.fill(new Rectangle2D.Double(refPoint3.getX()*factor-1 + xOffset, refPoint3.getY()*factor-1 + yOffset, 3, 3));
        } else if(refPoint1.isInitialized() && refPoint2.isInitialized() && dotSelection) {
			if(i3s != null)
				i3s.setMessage("Point out " + control3);
        }
		for(int i=0 ; i<eltcnt; i++) {
			Stroke oldStroke = null;

			if(i == highlight)
			{
				oldStroke = g.getStroke();
				g.setStroke(new BasicStroke(2.0f));
			}
			elts[i].draw(g, factor, xOffset, yOffset, i, false);
	
			if(i == highlight)
				g.setStroke(oldStroke);
        }
    }
    public int print(Graphics2D g2, double scalex, double scaley, int xOffset, int yOffset, int imgHeight) {
		try {
			g2.setColor(Color.red);
			for(int i=0 ; i<eltcnt; i++)
				elts[i].draw(g2, scalex, xOffset, yOffset, i, false);		// @@@ hier moet nog wat mee

			g2.setColor(Color.black);
			g2.setFont(new Font("Arial", Font.PLAIN, 24));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if(filename != null) {
				int index = filename.lastIndexOf('\\');
				g2.drawString(filename.substring(index+1), xOffset, yOffset - 36);
			}
			g2.setFont(new Font("Arial", Font.PLAIN, 12));
			g2.setColor(Color.black);
			
			printComment(g2, xOffset, yOffset + 48 + (int)(imgHeight*scaley));
		} catch(Exception exc) {
			JOptionPane.showMessageDialog(null, "An unexpected error occured while printing (" + exc.toString() + ").");
		}

		return Printable.PAGE_EXISTS;
    }
	
	public void printComment(Graphics2D g, int x, int y) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		String c = comment.toString();
		int lastCut=0;
		for(int i=0; i<c.length(); i++) {
			if((i >= lastCut+60 && (c.charAt(i) == ' ' || c.charAt(i) == (char) 10 || c.charAt(i) == (char) 13)) || (i >= lastCut+80)) {
				g.drawString(c.substring(lastCut, i), x, y);
				y += 20;
				lastCut = i+1;
			}
		}
		if(lastCut < c.length()-1)
			g.drawString(c.substring(lastCut, c.length()), x, y);
	}
	public MetaDataList getMetaDataList() {
		return mdl;
	}
	public StringBuffer getComment() {
		return comment;
	}
	public int getCnt() {
		return eltcnt;
	}
	public Element get(int i) {
		if(i<0 || i>= eltcnt)
			return new Element();
		return elts[i];
	}
	public String getFileName() {
		return filename;
	}
	public void copyData(double data[], double ref[]) {
        for(int i=0; i<eltcnt; i++) {
            data[i*10+0] = elts[i].cx;
            data[i*10+1] = elts[i].cy;
            data[i*10+2] = elts[i].data[0].getX();
            data[i*10+3] = elts[i].data[0].getY();
            data[i*10+4] = elts[i].data[1].getX();
            data[i*10+5] = elts[i].data[1].getY();
            data[i*10+6] = elts[i].data[2].getX();
            data[i*10+7] = elts[i].data[2].getY();
            data[i*10+8] = elts[i].data[3].getX();
            data[i*10+9] = elts[i].data[3].getY();
        }
		
		ref[0] = refPoint1.getX();
		ref[1] = refPoint1.getY();
		ref[2] = refPoint2.getX();
		ref[3] = refPoint2.getY();
		ref[4] = refPoint3.getX();
		ref[5] = refPoint3.getY();
	}
	public boolean isInitialized() {
		if(eltcnt > 0 || refPoint1.isInitialized() || refPoint2.isInitialized() || refPoint3.isInitialized())
			return true;
		return false;
	}
	public boolean refPointEmpty() {
		if(refPoint1.isInitialized() == false || refPoint2.isInitialized() == false || refPoint3.isInitialized() == false)
			return true;
		return false;
	}
	public Point2D getRef1() {
		return refPoint1;
	}
	public Point2D getRef2() {
		return refPoint2;
	}
	public Point2D getRef3() {
		return refPoint3;
	}
	public boolean refPointsOk() {
        return refPoint1.isInitialized() && refPoint2.isInitialized() && refPoint3.isInitialized();
	}
    private int versionOk(byte id[]) {
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
    
}

