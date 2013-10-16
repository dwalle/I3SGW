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
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 *   GNU General Public License for more details.                              *
 *                                                                             *
 *   You should have received a copy of the GNU General Public License         *
 *   along with this program; see the file COPYING GPL v2.txt. If not,         *
 *   write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, *
 *   Boston, MA 02111-1307, USA.                                               *
 *                                                                             *
 *******************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;

class BothSharks extends JPanel {
	private Point2D s1[] = null;
	private Point2D s2[] = null;
	private int nrpairs = 0;
	private double score;
	private String orig = null;
	private String found = null;
	private Image origImg = null;
	private Image foundImg = null;
	private Image bgImg = null;
	private int shortIndex1 = 0;
	private int shortIndex2 = 0;
	private FingerPrint fp1;
	private FingerPrint fp2;

	private int s2Len = 0;

	public BothSharks(String forig, String ffound, Point2D _s1[],
			Point2D _s2[], int _pairs[], double _score) {
		System.out.println("BothSharks");// Daniel Remove
		s1 = new Point2D[_s1.length];
		s2 = new Point2D[_s2.length];

		for (int i = 0; i < _s1.length; i++)
			s1[i] = new Point2D(_s1[i].getX(), _s1[i].getY());
		for (int i = 0; i < _s2.length; i++)
			s2[i] = new Point2D(_s2[i].getX(), _s2[i].getY());

		for (int i = 0; i < s2.length; i++) {
			if (s2[i].getX() > -1000000) 
				s2Len++;
		}

		for (nrpairs = 0; nrpairs < _pairs.length && _pairs[nrpairs] != -1; nrpairs++);
		score = _score;

		GetImageFile gif = new GetImageFile(forig);
		orig = gif.getImageString();
		gif = new GetImageFile(ffound);
		found = gif.getImageString();

		ImageIcon imageIcon = new ImageIcon(orig);
		origImg = imageIcon.getImage();
		imageIcon = new ImageIcon(found);
		foundImg = imageIcon.getImage();

		shortIndex1 = shortenFileName(orig, 45);
		shortIndex2 = shortenFileName(found, 45);

		fp1 = new FingerPrint(null);
		StringBuffer dummy = new StringBuffer();
		String fgp = new String(new StringBuffer(orig.substring(0,
				orig.length() - 4)).append(".fgp"));
		if (!fp1.read(fgp, dummy))
			fp1 = null;

		fp2 = new FingerPrint(null);
		fgp = new String(new StringBuffer(
				found.substring(0, found.length() - 4)).append(".fgp"));

		if (!fp2.read(fgp, dummy))
			fp2 = null;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);

		g2.setColor(Color.red);
		g2.drawString(
				"Nr of spot pairs: " + nrpairs + "   Score: "
						+ nf.format(score), 420, 20);
		if (shortIndex1 <= 0)
			g2.drawString("Unknown individual: " + orig.substring(shortIndex1),
					420, 60);
		else
			g2.drawString(
					"Unknown individual: ..." + orig.substring(shortIndex1),
					420, 60);

		if (fp1 != null)
			fp1.printComment(g2, 420, 90);

		System.out.println(orig);
		if (origImg != null)
			paintImage(g2, origImg, s1, 0);

		g2.drawString("Number of Spots: " + s1.length, 420, 80);

		g2.setColor(Color.blue);
		if (shortIndex2 <= 0)
			g2.drawString("Found individual: " + found.substring(shortIndex2),
					420, 300);
		else
			g2.drawString(
					"Found individual: ..." + found.substring(shortIndex2),
					420, 300);

		if (fp2 != null)
			fp2.printComment(g2, 420, 330);

		System.out.println(found);
		if (foundImg != null)
			paintImage(g2, foundImg, s2, 280);

		g2.drawString("Number of Spots: " + s2Len, 420, 350);
	}

	private void paintImage(Graphics2D g, Image im, Point2D s[], int offsety) {
		double iw = (double) im.getWidth(this);
		double ih = (double) im.getHeight(this);
		double factor;
		System.out.println("iw: "+iw+ " ih: "+ih);
		if (iw / ih < 1020.0 / 700.0)//1020/700=1.45
			factor = 280 / ih;
		else
			factor = 373.33 / iw;

		g.drawImage(im, 0, offsety, (int) (iw * factor), (int) (ih * factor),
				Color.white, this);

		for (int i = 0; i < s.length; i++) {
			if (i < 3) // is a reference point
				g.draw(new Rectangle2D.Double(s[i].getX() * factor - 3, s[i]
						.getY() * factor - 3 + offsety, 7, 7));
			if (s[i].isInitialized())
				g.fill(new Rectangle2D.Double(s[i].getX() * factor - 1, s[i]
						.getY() * factor - 1 + offsety, 3, 3));
		}
	}

	private int shortenFileName(String s, int maxlength) {
		if (s.length() <= maxlength)
			return 0;

		int index = -1;
		while (s.length() - index > maxlength) {
			int val = Math.min(s.indexOf('/', index + 1),
					s.indexOf('\\', index + 1));
			if (val == -1)
				break;
			else
				index = val;
		}
		if (s.length() - index > maxlength)
			index = s.length() - maxlength;
		return index;
	}
}
