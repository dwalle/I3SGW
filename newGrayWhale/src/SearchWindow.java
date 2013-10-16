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
import javax.swing.text.*;
import java.text.*;

public class SearchWindow extends JPanel implements ActionListener  {
    static JFrame frame = null;
	static JRadioButton[] views = null;
	static JRadioButton[] dbParts = null;
	static JComboBox[] critCB = null;
	static Object[] choice = null;

	MetaDataList mdl;
	String[] viewStrings = null;
	I3SM i3s = null;
	int vc = 0;
	
    public SearchWindow(I3SM _i) {
    	System.out.println("SearchWindow");//Daniel Remove

		setLayout(null);
		i3s = _i;
		vc = i3s.getViewCnt();
		MetaDataXML md = new MetaDataXML(System.getenv("I3SM_DATA") + "/metadata.xml");
		mdl = md.getMetaDataList(md.getSpeciesIndex());

        JPanel radioPanel = new JPanel();
		radioPanel.setLayout(null);
        ButtonGroup group = new ButtonGroup();

		views = new JRadioButton[vc];
		int maxWidth = 80;		// minimal width of maximum
		int height = 110;
		
		viewStrings = i3s.getViews();
		
		for(int i=0; i<vc; i++) {
			if((int) (viewStrings[i].length()*7) > maxWidth)
				maxWidth = (int) (viewStrings[i].length()*7);
			views[i] = new JRadioButton(viewStrings[i]);
			views[i].setSelected(false);
			views[i].setBounds(10, 15+i*25, maxWidth, 32);
			group.add(views[i]);
			radioPanel.add(views[i]);
		}

		if(32*vc > height)
			height = 32*vc;
		
        radioPanel.setBorder(BorderFactory.createTitledBorder("Views"));
		radioPanel.setBounds(10, 10, maxWidth+25, height);

        add(radioPanel);

        radioPanel = new JPanel();
		radioPanel.setLayout(null);
        group = new ButtonGroup();

		dbParts = new JRadioButton[3];
		dbParts[0] = new JRadioButton("Entire database");
		dbParts[0].setSelected(false);
		dbParts[0].setBounds(10, 15, 165, 32);
		group.add(dbParts[0]);
		radioPanel.add(dbParts[0]);
		dbParts[1] = new JRadioButton("Female and unknown sex");
		dbParts[1].setSelected(false);
		dbParts[1].setBounds(10, 40, 165, 32);
		group.add(dbParts[1]);
		radioPanel.add(dbParts[1]);
		dbParts[2] = new JRadioButton("Male and unknown sex");
		dbParts[2].setSelected(false);
		dbParts[2].setBounds(10, 65, 165, 32);
		group.add(dbParts[2]);
		radioPanel.add(dbParts[2]);

		radioPanel.setBorder(BorderFactory.createTitledBorder("Database parts"));
		radioPanel.setBounds(maxWidth+45, 10, 185, height);
        add(radioPanel);

		maxWidth += 195;

		if(mdl.getCnt() > 0) {
			JPanel metadataPanel = new JPanel();
			metadataPanel.setLayout(null);
			critCB = new JComboBox[mdl.getCnt()];
			choice = new Object[mdl.getCnt()];
			
			for(int i=0; i<mdl.getCnt(); i++) {
				JLabel nmLabel = new JLabel(limitSize(mdl.getName(i)));
				nmLabel.setBounds(10, 15+i*32, maxWidth-150, 32);
				critCB[i] = new JComboBox();
				
				switch(mdl.getType(i)) {
					case MetaDataList.NUMBER:
					case MetaDataList.SML:
						critCB[i].addItem((Object) new String("don't care"));
						critCB[i].addItem((Object) new String("equals"));
						critCB[i].addItem((Object) new String("is less than"));
						critCB[i].addItem((Object) new String("is greater than"));
						break;
					case MetaDataList.YESNO:
						critCB[i].addItem((Object) new String("don't care"));
						critCB[i].addItem((Object) new String("equals"));
						break;
				}
				critCB[i].setBounds(maxWidth-170, 21+i*32, 105, 20);

				metadataPanel.add(nmLabel);
				metadataPanel.add(critCB[i]);

				switch(mdl.getType(i)) {
					case MetaDataList.NUMBER:
						JFormattedTextField fld = new JFormattedTextField(NumberFormat.getNumberInstance());
						fld.setColumns(10);
						fld.setBounds(maxWidth-55, 21+i*32, 70, 20);
						metadataPanel.add(fld);
						choice[i] = fld;
						break;
					case MetaDataList.SML:
						JComboBox sml = new JComboBox();
						sml.addItem((Object) new String("Small"));
						sml.addItem((Object) new String("Medium"));
						sml.addItem((Object) new String("Large"));
						sml.setBounds(maxWidth-55, 21+i*32, 70, 20);
						metadataPanel.add(sml);
						choice[i] = sml;
						break;
					case MetaDataList.YESNO:
						JComboBox yn = new JComboBox();
						yn.addItem((Object) new String("Yes"));
						yn.addItem((Object) new String("No"));
						yn.setBounds(maxWidth-55, 21+i*32, 70, 20);
						metadataPanel.add(yn);
						choice[i] = yn;
						break;
				}						
			}
			metadataPanel.setBorder(BorderFactory.createTitledBorder("Metadata elements"));
			metadataPanel.setBounds(10, height+15, maxWidth+25, 32*mdl.getCnt()+20);
			add(metadataPanel);

			height += 32*mdl.getCnt()+20;
		}
		
		JButton search = new JButton("Search");
		search.setBounds((maxWidth+40)/2-50, height+25, 100, 20);
        search.addActionListener(this);
		add(search);

		readPrefs();
		createAndShowGUI(maxWidth+50, height+25+60);
    }
    public void actionPerformed(ActionEvent e) {
		writePrefs();
        StringBuffer mess = new StringBuffer();

		String instr;
		int i;
 		for(i=0; i<3; i++)
			if(dbParts[i].isSelected())
				break;
		switch(i) {
			case 0: instr = new String("U"); break;
			case 1: instr = new String("F"); break;
			default: instr = new String("M"); break;
		}
		for(i=0; i<vc; i++)
			if(views[i].isSelected())
				break;
		instr = instr.concat(viewStrings[i].toLowerCase());

		MetaDataCompareList mdcl = captureMetadataFromWindow();

        if(i3s.getSharkPanel().compareWithDatabase(instr, mdcl, mess) == false)
			JOptionPane.showMessageDialog(i3s.getFrame(), mess);

		frame.dispose();
    }
	private MetaDataCompareList captureMetadataFromWindow() {
		// capture metadata preferences
		MetaDataCompareList mdcl = new MetaDataCompareList();
		if(mdl.getCnt() == 0)
			return mdcl;
		
		for(int i=0; i<mdl.getCnt(); i++) {
			String nm = mdl.getName(i);
			int tp = mdl.getType(i);
			int cmp = critCB[i].getSelectedIndex();
			Number o = null;
			int val;
			switch(tp) {
				case MetaDataList.YESNO:
				case MetaDataList.SML:
					val = ((JComboBox)choice[i]).getSelectedIndex()+1;	// correct for 0 which is undefined
					o = new Integer(val);
					break;
				case MetaDataList.NUMBER:
					double dtmp = -1;
					if(((JFormattedTextField) choice[i]).getValue() != null)
						dtmp = ((Number)((JFormattedTextField) choice[i]).getValue()).doubleValue();
					o = new Double(dtmp);
					break;
			}
			mdcl.addMember(nm, tp, cmp, o);
		}
		return mdcl;
	}
	
    public void readPrefs() {
		int i = I3SMPrefs.getView();
		if(i>= vc)
			i=0;
		views[i].setSelected(true);

		i = I3SMPrefs.getDbPart();
		if(i < 0 || i>2)
			i=0;
		dbParts[i].setSelected(true);
    }
    public void writePrefs() {
		int i;
		for(i=0; i<vc; i++)
			if(views[i].isSelected())
				break;
        I3SMPrefs.putView(i);
 		for(i=0; i<3; i++)
			if(dbParts[i].isSelected())
				break;
        I3SMPrefs.putDbPart(i);
    }
    private void createAndShowGUI(int w, int h) {
        if(frame != null)
            frame.dispose();
        frame = new JFrame("I3S Manta: Search the database");
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { frame.dispose(); frame = null; } });
        frame.setLocation(200, 200);
        frame.setSize(w, h);
		frame.setResizable(false);

        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        frame.setVisible(true);        
    }
	private String limitSize(String s) {
		StringBuffer sb = new StringBuffer(s);
		if(sb.length() > 16) {
			sb.delete(14, sb.length()-1);
			sb.append("...");
		}
		return sb.toString();
	}
}
