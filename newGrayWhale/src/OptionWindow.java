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

public class OptionWindow extends JPanel implements ActionListener  {
    private static final String SpeciesTag = "Species";
    private static final String LoggingTag = "Logging";

    static JFrame frame = null;
	JRadioButton loggingOn = null;
	JRadioButton loggingOff = null;
	JRadioButton showTop50 = null;
	JRadioButton showAll = null;
	JRadioButton[] species = null;
	JButton reference = null;
	JButton ellipse = null;
	JButton control = null;
	JButton center = null;
	Color ellipseColor = null;
	Color referenceColor = null;
	Color centerColor = null;
	Color controlColor = null;

	MetaDataXML metadata;
	SharkPanel sp = null;
	
    public OptionWindow(SharkPanel _sp) {
    	System.out.println("OptionWindow");//Daniel Remove

        setLayout(null);
			
        ButtonGroup group = new ButtonGroup();
        JPanel panel = new JPanel(new GridLayout(0, 1));

		metadata = new MetaDataXML(System.getenv("I3SM_DATA") + "/metadata.xml");
		sp = _sp;
		species = new JRadioButton[metadata.getCnt()];
		int maxWidth = 150;

		// Species settings
		for(int i=0; i<metadata.getCnt(); i++) {
			species[i] = new JRadioButton(metadata.getSpecies(i));
			species[i].setSelected(false);
			species[i].addActionListener(this);
			species[i].setActionCommand("SpeciesRadio");
			group.add(species[i]);
			panel.add(species[i]);
			if((int) (metadata.getSpecies(i).length()*7) > maxWidth)
				maxWidth = (int) (metadata.getSpecies(i).length()*7);
		}

        panel.setBorder(BorderFactory.createTitledBorder("Species"));
		panel.setBounds(10, 10, maxWidth+25, 32*metadata.getCnt());
        add(panel);

		// Logging settings
        loggingOff = new JRadioButton("Logging off");
        loggingOff.setActionCommand("Logging off");

        loggingOn = new JRadioButton("Logging on");
        loggingOn.setActionCommand("Logging on");

        group = new ButtonGroup();
        group.add(loggingOff);
        group.add(loggingOn);

        panel = new JPanel(new GridLayout(0, 1));
        panel.add(loggingOff);
        panel.add(loggingOn);
        panel.setBorder(BorderFactory.createTitledBorder("Logging results"));
		panel.setBounds(10, 32*metadata.getCnt()+20, maxWidth+25, 80);
        add(panel);
		
		// Settings for result array size 
        showTop50 = new JRadioButton("Show Top 50");
        showTop50.setActionCommand("Show Top 50");

        showAll = new JRadioButton("Show all");
        showAll.setActionCommand("Show all");

        group = new ButtonGroup();
        group.add(showTop50);
        group.add(showAll);

        panel = new JPanel(new GridLayout(0, 1));
        panel.add(showTop50);
        panel.add(showAll);
        panel.setBorder(BorderFactory.createTitledBorder("Show results"));
		panel.setBounds(10, 32*metadata.getCnt()+100, maxWidth+25, 80);
        add(panel);
		
		// Color settings
		reference = new JButton();
		reference.setActionCommand("reference");
		reference.addActionListener(this);
		ellipse = new JButton();
		ellipse.setActionCommand("ellipse");
		ellipse.addActionListener(this);
		control = new JButton();
		control.setActionCommand("control");
		control.addActionListener(this);
		center = new JButton();
		center.setActionCommand("center");
		center.addActionListener(this);

        panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Reference: "));
		panel.add(reference);
        panel.add(new JLabel("Ellipse: "));
		panel.add(ellipse);
        panel.add(new JLabel("Control:"));
		panel.add(control);
        panel.add(new JLabel("Center:"));
		panel.add(center);
        panel.setBorder(BorderFactory.createTitledBorder("Color settings"));
		panel.setBounds(10, 32*metadata.getCnt()+180, maxWidth+25, 120);
        add(panel);
		
		JButton close = new JButton("Close");
		close.setBounds((maxWidth+50)/2-40, 32*metadata.getCnt()+315, 80, 20);
		close.setActionCommand("close");
        close.addActionListener(this);
		add(close);

		readOptions();

		createAndShowGUI(maxWidth+50, 32*metadata.getCnt()+370);
    }

    public void actionPerformed(ActionEvent e) {
		if("SpeciesRadio".equals(e.getActionCommand())) {
			int i;
			for(i=0; i<species.length; i++)
				if(species[i].isSelected())
					break;
			I3SMPrefs.putSpecies(i);
			sp.getMetaDataList();
		}
		if("close".equals(e.getActionCommand())) {
			writeOptions();
			sp.setControlStrings(metadata);
			frame.dispose();
		}
		if("reference".equals(e.getActionCommand())) {
			ColorChooser cc = new ColorChooser(referenceColor, "Choose reference color", this);
		}
		if("ellipse".equals(e.getActionCommand())) {
			ColorChooser cc = new ColorChooser(ellipseColor, "Choose ellipse color", this);
		}
		if("center".equals(e.getActionCommand())) {
			ColorChooser cc = new ColorChooser(centerColor, "Choose center color", this);
		}
		if("control".equals(e.getActionCommand())) {
			ColorChooser cc = new ColorChooser(controlColor, "Choose control color", this);
		}
    }

	public void setReferenceColor(Color c) {
		referenceColor = c;
		reference.setIcon(new ColorIcon(80, 20, c));
	}
	public void setEllipseColor(Color c) {
		ellipseColor = c;
		ellipse.setIcon(new ColorIcon(80, 20, c));
	}
	public void setControlColor(Color c) {
		controlColor = c;
		control.setIcon(new ColorIcon(80, 20, c));
	}
	public void setCenterColor(Color c) {
		centerColor = c;
		center.setIcon(new ColorIcon(80, 20, c));
	}
    public void readOptions() {
		int i = I3SMPrefs.getSpecies();
		if(i>= metadata.getCnt())
			i=0;

		species[i].setSelected(true);

		boolean log = I3SMPrefs.getLogging();
		loggingOn.setSelected(log);
		loggingOff.setSelected(!log);
		
		boolean show = I3SMPrefs.getTop50();
		showTop50.setSelected(show);
		showAll.setSelected(!show);
		
		int r = I3SMPrefs.getEllipseRed();
		int g = I3SMPrefs.getEllipseGreen();
		int b = I3SMPrefs.getEllipseBlue();
		ellipseColor = new Color(r, g, b);
		ellipse.setIcon(new ColorIcon(80, 20, new Color(r, g, b)));

		r = I3SMPrefs.getReferenceRed();
		g = I3SMPrefs.getReferenceGreen();
		b = I3SMPrefs.getReferenceBlue();
		referenceColor = new Color(r, g, b);
		reference.setIcon(new ColorIcon(80, 20, new Color(r, g, b)));

		r = I3SMPrefs.getCenterRed();
		g = I3SMPrefs.getCenterGreen();
		b = I3SMPrefs.getCenterBlue();
		centerColor = new Color(r, g, b);
		center.setIcon(new ColorIcon(80, 20, new Color(r, g, b)));
		
		r = I3SMPrefs.getControlRed();
		g = I3SMPrefs.getControlGreen();
		b = I3SMPrefs.getControlBlue();
		controlColor = new Color(r, g, b);
		control.setIcon(new ColorIcon(80, 20, new Color(r, g, b)));
    }
    public void writeOptions() {
		int i;
		for(i=0; i<species.length; i++)
			if(species[i].isSelected())
				break;
		
        I3SMPrefs.putSpecies(i);
		I3SMPrefs.putLogging(loggingOn.isSelected());
		I3SMPrefs.putTop50(showTop50.isSelected());

		I3SMPrefs.putEllipseRed(ellipseColor.getRed());
		I3SMPrefs.putEllipseGreen(ellipseColor.getGreen());
		I3SMPrefs.putEllipseBlue(ellipseColor.getBlue());

		I3SMPrefs.putReferenceRed(referenceColor.getRed());
		I3SMPrefs.putReferenceGreen(referenceColor.getGreen());
		I3SMPrefs.putReferenceBlue(referenceColor.getBlue());

		I3SMPrefs.putControlRed(controlColor.getRed());
		I3SMPrefs.putControlGreen(controlColor.getGreen());
		I3SMPrefs.putControlBlue(controlColor.getBlue());
		
		I3SMPrefs.putCenterRed(centerColor.getRed());
		I3SMPrefs.putCenterGreen(centerColor.getGreen());
		I3SMPrefs.putCenterBlue(centerColor.getBlue());
    }
    private void createAndShowGUI(int w, int h) {
        if(frame != null)
            frame.dispose();
        frame = new JFrame("I3S Gray Whale: Options");
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { frame.dispose(); frame = null; } });
        frame.setLocation(200, 150);
        frame.setSize(w, h);
        frame.setResizable(false);

        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        frame.setVisible(true);        
    }
}
