/*******************************************************************************
 *   I3SM: Interactive Individual Identification System Manta version          *
 *                                                                             *
 *   Copyright (C) 2004-2008  Jurgen den Hartog & Renate Reijns                *
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
import javax.swing.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;

class InsertDatabase extends JPanel implements ActionListener {
    static JFrame frame = null;
	static String lastWarning = null;
    
	SharkPanel sp;
    JTextField tf, dirTf;
	JRadioButton viewButtons[] = null;
	JRadioButton sexButtons[] = null;
	String views[] = null;
	
    String orig = null;
    String forig = null;
    String ext = null;

    public InsertDatabase(SharkPanel _sp, String _forig) {
    	System.out.println("InsertDatabase");//Daniel Remove

        sp = _sp;
        forig = _forig;
		views = sp.getViews();
        
        GetImageFile gif = new GetImageFile(forig);
        orig = gif.getImageString();
        ext = gif.getImageExtension();

        if(frame != null)
            frame.dispose();

		// general set-up of main panel
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(10, 1)));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createRigidArea(new Dimension(1, 10)));        
        
		// database sub pane
		ButtonGroup group = new ButtonGroup();
		sexButtons = new JRadioButton[3];
		sexButtons[0] = new JRadioButton("Male");
		sexButtons[1] = new JRadioButton("Female");
		sexButtons[2] = new JRadioButton("Unknown sex");
		group.add(sexButtons[0]); sexButtons[0].setSelected(true);
		group.add(sexButtons[1]);
		group.add(sexButtons[2]);

        JPanel wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createTitledBorder("Database location"));
        wrapper.setLayout(new GridLayout(0, 1));
        JLabel label = new JLabel(" Source image: " + reduceString(orig));
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        wrapper.add(label, BorderLayout.LINE_START);
		
		wrapper.add(sexButtons[0]);
		wrapper.add(sexButtons[1]);
		wrapper.add(sexButtons[2]);
		
        dirTf = new JTextField("Insert individual id name here", 30);
		dirTf.getDocument().addDocumentListener(new TextFieldListener());

        wrapper.add(dirTf);

        centerPanel.add(wrapper);
        centerPanel.add(Box.createRigidArea(new Dimension(1,10)));

		// View selection sub pane
		wrapper = new JPanel();
		wrapper.setLayout(new GridLayout(0, 1));
        wrapper.setBorder(BorderFactory.createTitledBorder("View selection"));
		group = new ButtonGroup();
		viewButtons = new JRadioButton[views.length-1];
		int viewcnt = 0;
		for(int i=1; i<views.length; i++) {
			if(views[i] == null)
				break;
			viewcnt++;
			viewButtons[i-1] = new JRadioButton(views[i]);
			viewButtons[i-1].setActionCommand(views[i]);
			viewButtons[i-1].setSelected(false);
			viewButtons[0].setSelected(true);
			group.add(viewButtons[i-1]);
			wrapper.add(viewButtons[i-1]);
		}
		if(viewcnt > 0) {
			centerPanel.add(wrapper);
			centerPanel.add(Box.createRigidArea(new Dimension(1,10)));
		}

		// Renaming sub pane
        wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createTitledBorder("Naming of the file"));
        wrapper.setLayout(new GridLayout(0, 1));
        JCheckBox renameFile = new JCheckBox("Rename file");
        renameFile.addActionListener(new OptionListener());
        renameFile.setSelected(false);
        wrapper.add(renameFile);

		int index = orig.lastIndexOf('/');
        if(index < orig.lastIndexOf('\\'))
            index = orig.lastIndexOf('\\');
        if(index == -1) {
            JOptionPane.showMessageDialog(frame, "Internal error: move image and fingerprint file \nmanually to proper directory in the database");
            frame.dispose();
            return;
        }
        tf = new JTextField(orig.substring(index+1, orig.length()), 30);
        tf.setEnabled(false);
        wrapper.add(tf);

        centerPanel.add(wrapper);

		// Ok & Cancel sub pane
        wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        JButton ok = new JButton("Ok");
        ok.setMnemonic(KeyEvent.VK_O);
        ok.addActionListener(this);
        JButton cancel = new JButton("Cancel");
        cancel.setMnemonic(KeyEvent.VK_C);
        cancel.addActionListener(this);
        
        wrapper.add(ok);
        wrapper.add(Box.createRigidArea(new Dimension(10,1)));
        wrapper.add(cancel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(1,10)));
        centerPanel.add(wrapper);
        centerPanel.add(Box.createRigidArea(new Dimension(1,10)));

        add(centerPanel);
        add(Box.createRigidArea(new Dimension(10, 1)));

        frame = new JFrame("I3S Manta: New individual in database");
        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { dispose(); } });
		frame.pack();
        frame.setLocation(200, 100);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    String reduceString(String s) {
 		if(s.length() < 50)
			return s;
		int i=0;
		int cnt=0;
		while(s.length() - i > 50 && cnt < 10) {
			i = s.indexOf("\\", i+1);
			cnt++;
		}
		if(cnt == 10)
			i = s.length() - 50;
		if(s.length() <= 50)
			return s.substring(i);
		else
			return new String("..." + s.substring(i));
	}
    
    void dispose() {
        frame.dispose();
        frame = null;
    }
    
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if(source.getText() == "Cancel") {
            dispose();
            return;
        }
        StringBuffer mess = new StringBuffer();
        if(sp.writeFingerprint(mess) == false) {
            JOptionPane.showMessageDialog(null, "Could not write fingerprint file! Check disk space or write permission. \nFile not copied.");
            return;
        }
		
		String viewDir = new String();
		for(int i=0; i<viewButtons.length; i++) {
			if(viewButtons[i] != null && viewButtons[i].isSelected()) {
				viewDir = views[i+1] + "/";
				break;
			}
		}
		
		String sexDir = new String();
		if(sexButtons[0].isSelected())
			sexDir = new String("Male");
		else if(sexButtons[1].isSelected())
			sexDir = new String("Female");
		else if(sexButtons[2].isSelected())
			sexDir = new String("UnknownSex");

		if(dirTf.getText().startsWith("Insert ")) {
            JOptionPane.showMessageDialog(null, "Please choose a proper name for the new individual in the first text field");
			return;
		}
		
        String filename = tf.getText();
		String dir = System.getenv("I3SM_DATA") + "/" + sexDir + "/" + dirTf.getText() + "/" + viewDir;
		File f = new File(dir );
		if(f.exists() && (lastWarning == null || lastWarning.contentEquals(dirTf.getText()) == false)) {
			lastWarning = new String(dirTf.getText());
			if(JOptionPane.showConfirmDialog(null, "This individual already exists. Do you want to continue?", "Individual already exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				return;
		}
		
		if(!f.exists() && f.mkdirs() == false) {
            JOptionPane.showMessageDialog(null, "Could not create directory in the database. Please check whether you have sufficient rights.");
			return;
		}
		f = new File(dir + filename);
		if(f.exists()) {
			if(JOptionPane.showConfirmDialog(null, "A file with this name already exists. Do you want to overwrite?", "File already exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				return;
		}

        // copy fgp file
        if(!filename.endsWith(ext)) {
            JOptionPane.showMessageDialog(null, "Please, add proper file extension in the second text field (" + ext + ")");
            return;
        }

        int index = filename.lastIndexOf(ext);
        CopyFile cf = new CopyFile(forig, dir + filename.substring(0, index) + ".fgp"); 
        if(cf.doIt() == false)
            return;
        // copy img file
        cf = new CopyFile(orig, dir + filename);
        if(cf.doIt() == false)
            return;
        
        sp.killResultWindows();
        sp.close();
        sp.updateDatabase();

        frame.dispose();
        frame = null;
    }
    
    /** An ActionListener that listens to the check box. */
    class OptionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JCheckBox c = (JCheckBox) e.getSource();
                if (c.isSelected())
                    tf.setEnabled(true);
                else
                    tf.setEnabled(false);
        }
    }
    /** An ActionListener that listens to the first textfield. */
    class TextFieldListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
			tf.setText(dirTf.getText() + ".jpg");
		}
		public void insertUpdate(DocumentEvent e) {
			tf.setText(dirTf.getText() + ".jpg");
		}
		public void removeUpdate(DocumentEvent e) {
		}
    }

}
