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
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.text.*;

public class MetaDataInput extends JPanel implements ActionListener {
	static JFrame frame = null;
	JTextArea textArea;
	SharkPanel sp;
	FingerPrint f;
	StringBuffer s, old;
	MetaDataList mdl, oldMdl;
	Object choice[] = null;
	JButton close, cancel;
	
    public MetaDataInput(FingerPrint _f, MetaDataList _mdl, SharkPanel _sp) {
    	System.out.println("MetaDataInput");//Daniel Remove

		f = _f;
		s = f.getComment();
		sp = _sp;
		mdl = _mdl;	// remember this is the metadatalist read from the xml file, not the mdl belonging to the fingerprint
		old = new StringBuffer(s);
		oldMdl = f.getMetaDataList().copy();

        setLayout(null);

        //Create a text area.
		if(s.length() > 0)
			textArea = new JTextArea(s.toString());
		else
			textArea = new JTextArea("Empty comment field");
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
		
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setBounds(10, 10, 260, 120);
        add(areaScrollPane);

		int height = 0;

		if(mdl.getCnt() > 0) {
			JPanel metadataPanel = new JPanel();
			metadataPanel.setLayout(null);
			choice = new Object[mdl.getCnt()];
			
			for(int i=0; i<mdl.getCnt(); i++) {
				JLabel nmLabel = new JLabel(mdl.getName(i));
				nmLabel.setBounds(10, 12+i*24, 165, 24);

				metadataPanel.add(nmLabel);
				Number o = oldMdl.getMember(mdl.getName(i), mdl.getType(i));

				switch(mdl.getType(i)) {
					case MetaDataList.NUMBER:
						JFormattedTextField fld = new JFormattedTextField(NumberFormat.getNumberInstance());
						fld.setColumns(10);
						if(o != null)
							fld.setValue((Double) o);
						fld.setBounds(175, 15+i*24, 70, 20);
						metadataPanel.add(fld);
						choice[i] = fld;
						break;
					case MetaDataList.SML:
						JComboBox sml = new JComboBox();
						sml.addItem((Object) new String("Unknown"));
						sml.addItem((Object) new String("Small"));
						sml.addItem((Object) new String("Medium"));
						sml.addItem((Object) new String("Large"));
						if(o != null)
							sml.setSelectedIndex(((Integer) o).intValue());
						sml.setBounds(175, 15+i*24, 70, 20);
						metadataPanel.add(sml);
						choice[i] = sml;
						break;
					case MetaDataList.YESNO:
						JComboBox yn = new JComboBox();
						yn.addItem((Object) new String("Unknown"));
						yn.addItem((Object) new String("Yes"));
						yn.addItem((Object) new String("No"));
						if(o != null)
							yn.setSelectedIndex(((Integer) o).intValue());
						yn.setBounds(175, 15+i*24, 70, 20);
						metadataPanel.add(yn);
						choice[i] = yn;
						break;
					default:
						JOptionPane.showMessageDialog(null, "This error should not occur! Please report this error to i3s@reijns.com");
				}						
			}
			metadataPanel.setBorder(BorderFactory.createTitledBorder("Metadata elements"));
			metadataPanel.setBounds(10, 140, 255, 24*mdl.getCnt()+20);
			add(metadataPanel);

			height = 24*mdl.getCnt()+30;
		}
		
		close = new JButton("OK");
        close.setToolTipText("Close and keep results");
        close.setMnemonic(KeyEvent.VK_O);
        close.addActionListener(this);
		close.setBounds(50, 140+height, 70, 20);
        add(close);
		
		cancel = new JButton("Cancel");
        cancel.setToolTipText("Close and discard results");
        cancel.setMnemonic(KeyEvent.VK_C);
        cancel.addActionListener(this);
		cancel.setBounds(140, 140+height, 70, 20);
        add(cancel);
		
		createAndShowGUI(275, 195+height);
    }
	private void copyMetaDataFromWindow() {
		for(int i=0; i<mdl.cnt; i++) {
			Number o = null;
			int val;

			switch(mdl.getType(i)) {
				case MetaDataList.YESNO:
				case MetaDataList.SML:
					val = ((JComboBox)choice[i]).getSelectedIndex();
					o = new Integer(val);
					break;
				case MetaDataList.NUMBER:
					double dtmp = -1;
					if(((JFormattedTextField) choice[i]).getValue() != null)
						dtmp = ((Number)((JFormattedTextField) choice[i]).getValue()).doubleValue();
					o = new Double(dtmp);
					break;
				default:
					JOptionPane.showMessageDialog(null, "This error should not occur! Please report this error to i3s@reijns.com");
			}
			f.getMetaDataList().addMember(mdl.getName(i), mdl.getType(i), o);
		}
	}
    private void createAndShowGUI(int w, int h) {
        if(frame != null)
            frame.dispose();
        frame = new JFrame("I3S Manta: Metadata input");
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { frame.dispose(); frame = null; } });
        frame.setLocation(200, 200);
        frame.setSize(w, h);
		frame.setResizable(false);

        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        frame.setVisible(true);        
		
    }
    public void actionPerformed(ActionEvent e) {
		if((JButton) e.getSource() == close)
		{
			copyMetaDataFromWindow();

			s.replace(0, s.length(),textArea.getText());

			if((s.toString().equals(old.toString()) == false && s.toString().equals("Empty comment field") == false) || 
				f.getMetaDataList().equals(oldMdl) == false)
				sp.changeMade(true);
		}
		frame.dispose();
    }
}
