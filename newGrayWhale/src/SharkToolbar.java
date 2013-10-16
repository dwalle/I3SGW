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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;


public class SharkToolbar implements ActionListener {
	private JLabel labExclMark = null;
	private JTextField zoomString  = null;
	private JTextField nrSpots  = null;
	private JTextField messages = null;
	private JButton pointer = null;
	private boolean toggle = false;

	private SharkPanel sp;
	private I3SM i3s;
	
	SharkToolbar(I3SM _i3s) {
    	System.out.println("SharkToolbar");//Daniel Remove

		i3s = _i3s;
		sp = i3s.getSharkPanel();
	}
	JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setBackground(new Color(204, 204, 204));
		addButton(toolbar, "Open.png", null, "Open file", "Open a new file", true);
		addButton(toolbar, "Save.png", null, "Save file", "Save the current file", true);
		addButton(toolbar, "Print.png", null, "Print", "Print the current individual", true);
		JButton search = addButton(toolbar, "Search.png", null, "Search", "Pop-up search window", true);
        search.setMnemonic(KeyEvent.VK_S);
		
		addButton(toolbar, "Info.png", null, "Comment", "Edit the metadata", true);
		pointer = addButton(toolbar, "PointerD.png", null, "Pointer", "Toggle spot selection mode", true); 
        pointer.setMnemonic(KeyEvent.VK_P); // | KeyEvent.CTRL_DOWN_MASK);
		
		toolbar.addSeparator(new Dimension(10,0));
	
		addButton(toolbar, "PlusK.png", null,  "ZoomIn",  "Zoom in",  true);
		addButton(toolbar, "MinusK.png", null, "ZoomOut", "Zoom out", true);

		zoomString = new JTextField("100%", 4);
		zoomString.setToolTipText("Percentage zoom");
		zoomString.setMaximumSize(new Dimension(40, 20));
		zoomString.setEnabled(false);
		
		toolbar.addSeparator(new Dimension(3,0));
		toolbar.add(zoomString);
		
		toolbar.addSeparator(new Dimension(10,0));

		nrSpots = new JTextField("# spots: 0", 12);
		nrSpots.setToolTipText("Number of spots selected");
		nrSpots.setMaximumSize(new Dimension(75, 20));
		nrSpots.setEnabled(false);
		toolbar.add(nrSpots);

		toolbar.addSeparator(new Dimension(10,0));
		
		ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/Excl.png"));
		labExclMark = new JLabel(imageIcon);
		imageIcon = new ImageIcon(getClass().getResource("images/ExclD.png"));
		labExclMark.setDisabledIcon(imageIcon);
		labExclMark.setToolTipText("Indicator of changes");
		labExclMark.setEnabled(false);
		toolbar.add(labExclMark);

		toolbar.addSeparator(new Dimension(10,0));
		
		messages = new JTextField("", 50);
		messages.setToolTipText("Area for system messages other than errors");
		messages.setMaximumSize(new Dimension(250, 20));
		messages.setEnabled(false);
		toolbar.add(messages);

		return toolbar;
	}
	public void setExclamation(boolean b) {
		labExclMark.setEnabled(b);
	}
	public void setZoomPercentage(double zoom) {
		Double d = new Double(zoom*100);
		Integer perc = new Integer(d.intValue());
		zoomString.setText(perc.toString() + "%");
	}
	public void setNrSpots(int ns, boolean maxReached) {
		Integer i = new Integer(ns);
		nrSpots.setText("# spots: " + i.toString());

		if(maxReached) {
			nrSpots.setDisabledTextColor(Color.red);
			nrSpots.setBackground(Color.yellow);
		}
		else {
			nrSpots.setDisabledTextColor(new Color(128,128,128));
			nrSpots.setBackground(new Color(212,208,200));
		}
	}
	public void setMessage(String m) {
		messages.setText(m);
		if(m.length() > 0) {
			messages.setDisabledTextColor(Color.red);
			messages.setBackground(Color.yellow);
		}
		else {
			messages.setDisabledTextColor(new Color(128,128,128));
			messages.setBackground(new Color(212,208,200));
		}
	}
	public void actionPerformed(ActionEvent e) {
		StringBuffer mess = new StringBuffer();
		
		if ("Open file".equals(e.getActionCommand())){
			System.out.println("Open image from SharkToolbar");//daniel remove
			i3s.openImage();
		}
		if ("Save file".equals(e.getActionCommand()))
			if(sp.writeFingerprint(mess) == false)
				JOptionPane.showMessageDialog(i3s.getFrame(), mess);
		if ("Print".equals(e.getActionCommand()))
			i3s.printFile();
		if ("Search".equals(e.getActionCommand())) {
            if(sp.imageOk()) {
				new SearchWindow(i3s);
			}
			else
				JOptionPane.showMessageDialog(i3s.getFrame(), "No data to compare. Open an image first.");
        }
		if ("Pointer".equals(e.getActionCommand())) {
			ImageIcon imageIcon;
			sp.toggleEltSelection();
			toggle = !toggle;
			if(toggle)
				imageIcon = new ImageIcon(getClass().getResource("images/Pointer.png"));
			else
				imageIcon = new ImageIcon(getClass().getResource("images/PointerD.png"));
			pointer.setIcon(imageIcon);
			sp.repaint();
		}
		if ("Comment".equals(e.getActionCommand()))
			i3s.editCommentField();
		if ("ZoomIn".equals(e.getActionCommand()))
			sp.setZoom(I3SM.ZOOM_STEPSIZE);
		if ("ZoomOut".equals(e.getActionCommand()))
			sp.setZoom(1 / I3SM.ZOOM_STEPSIZE);
	}
	public JButton addButton(JToolBar tb, String imName, String imDisabledName, String acCom, String tooltip, boolean enabled) {
		ImageIcon imageIcon = new ImageIcon(getClass().getResource("images/" + imName));
		JButton b = new JButton(imageIcon);
		if(imDisabledName != null) {
			imageIcon = new ImageIcon(getClass().getResource("images/" + imDisabledName));
			b.setDisabledIcon(imageIcon);
		}
		b.setMargin(new Insets(0, 0, 0, 0));
		b.setActionCommand(acCom);
		b.setToolTipText(tooltip);
		b.addActionListener(this);
		b.setEnabled(enabled);
		b.setBackground(new Color(204, 204, 204));
		tb.add(b);
		return b;
	}
}
