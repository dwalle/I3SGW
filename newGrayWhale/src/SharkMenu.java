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
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;

class SharkMenu implements ActionListener {
    final String Item1 = "File";
    final String Item1_1 = "Open image";
    final String Item1_3 = "Save fingerprint";
    final String Item1_4 = "Close image";
    final String Item1_5 = "Print image";
    final String Item1_6 = "Exit";
	
	final String Item2 = "Edit";
	final String Item2_0 = "Copy";
	final String Item2_1 = "Paste";
	final String Item2_2 = "Delete";
	final String Item2_3 = "Undo";
	final String Item2_4 = "Redo";

    final String Item2_5 = "Selection left";
    final String Item2_6 = "Selection right";
    final String Item2_7 = "Selection up";
    final String Item2_8 = "Selection down";
	
    final String Item3 = "Database";
    final String Item3_1 = "Search in database";
    final String Item3_2 = "Insert in database";
    final String Item3_3 = "Update database";
    final String Item5 = "Tools";
	final String Item5_1 = "Options";
    final String Item6 = "Help";
	final String Item6_1 = "Open manual";
    final String Item6_2 = "About I3SGW...";

	private SharkPanel sp;
	private I3SM i3s;

	SharkMenu(I3SM _i) {
		System.out.println("SharkMenu_SharkMenu()");//Daniel Remove
		System.out.println("______________________");//Daniel Remove
		sp  = _i.getSharkPanel();
		i3s = _i;
	}
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
		
        // Menu #1
        JMenu menu = addMenu(menuBar, Item1, KeyEvent.VK_F);
		addMenuItem(menu, Item1_1, KeyEvent.VK_O, KeyEvent.VK_O);
		addMenuItem(menu, Item1_3, KeyEvent.VK_S, KeyEvent.VK_S);
		addMenuItem(menu, Item1_4, KeyEvent.VK_C, -1);
		addMenuItem(menu, Item1_5, KeyEvent.VK_P, KeyEvent.VK_P);
        menu.addSeparator();
		addMenuItem(menu, Item1_6, KeyEvent.VK_X, KeyEvent.VK_X);
            
        // Menu #2
        menu = addMenu(menuBar, Item2, KeyEvent.VK_E);
		addMenuItem(menu, Item2_0, KeyEvent.VK_K, KeyEvent.VK_C);
		addMenuItem(menu, Item2_1, KeyEvent.VK_P, KeyEvent.VK_V);
		addMenuItem(menu, Item2_2, KeyEvent.VK_W, KeyEvent.VK_DELETE);
		addMenuItem(menu, Item2_3, KeyEvent.VK_O, KeyEvent.VK_Z);
		addMenuItem(menu, Item2_4, KeyEvent.VK_D, KeyEvent.VK_R);
        menu.addSeparator();
		addMenuItem(menu, Item2_5, KeyEvent.VK_L, KeyEvent.VK_LEFT);
		addMenuItem(menu, Item2_6, KeyEvent.VK_R, KeyEvent.VK_RIGHT);
		addMenuItem(menu, Item2_7, KeyEvent.VK_B, KeyEvent.VK_UP);
		addMenuItem(menu, Item2_8, KeyEvent.VK_E, KeyEvent.VK_DOWN);

        // Menu #3
        menu = addMenu(menuBar, Item3, KeyEvent.VK_D);
		addMenuItem(menu, Item3_1, KeyEvent.VK_S, -1);
		addMenuItem(menu, Item3_2, KeyEvent.VK_I, -1);
		addMenuItem(menu, Item3_3, KeyEvent.VK_U, -1);

        // Menu #5
        menu = addMenu(menuBar, Item5, KeyEvent.VK_T);
		addMenuItem(menu, Item5_1, KeyEvent.VK_O, -1);

        // Menu #6
        menu = addMenu(menuBar, Item6, KeyEvent.VK_H);
		addMenuItem(menu, Item6_1, KeyEvent.VK_O, -1);
		addMenuItem(menu, Item6_2, KeyEvent.VK_A, -1);

        return menuBar;
    }
	private JMenu addMenu(JMenuBar menuBar, String name, int keyEv) {
        JMenu menu = new JMenu(name);
		//menu.setBackground(new Color(204, 204, 204));
        menu.setMnemonic(keyEv);
        menuBar.add(menu);
		return menu;
	}
	public void addMenuItem(JMenu menu, String name, int altKey, int accKey) {
        JMenuItem menuItem = new JMenuItem(name, altKey);
		//menuItem.setBackground(new Color(204, 204, 204));
		if(accKey != -1)
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accKey, ActionEvent.CTRL_MASK));            
        menuItem.addActionListener(this);
        menu.add(menuItem);
	}
    
    public void actionPerformed(ActionEvent e) {
        StringBuffer mess = new StringBuffer();
        JMenuItem source = (JMenuItem)(e.getSource());

        String filename = null;

        if(source.getText() == Item1_1){
        	System.out.println("Open image from SharkMenu");//Daniel Remove
            i3s.openImage();
        }
        if(source.getText() == Item1_3) {
            if(sp.writeFingerprint(mess) == false)
                JOptionPane.showMessageDialog(i3s.getFrame(), mess);
        }
        if(source.getText() == Item1_4) {
            i3s.saveResultsPrompt();              
            i3s.getFrame().setTitle("I3SGW: Intelligent Individual Identification System (Gray Whale 1.0)");
            sp.close();
        }
        if(source.getText() == Item1_5) {
            sp.printFile();
        }
        if(source.getText() == Item1_6) {
            i3s.exit();
        }
		if(source.getText() == Item2_0) {
			sp.copySelected();
		}
		if(source.getText() == Item2_1) {
			sp.pasteSelected();
		}
		if(source.getText() == Item2_2) {
			sp.deleteSelected();
		}
		if(source.getText() == Item2_3) {
			sp.undoAction();
		}
		if(source.getText() == Item2_4) {
			sp.redoAction();
		}
		if(source.getText() == Item2_5) {
			sp.moveSelected(-1, 0);
		}
		if(source.getText() == Item2_6) {
			sp.moveSelected(1, 0);
		}
		if(source.getText() == Item2_7) {
			sp.moveSelected(0, -1);
		}
		if(source.getText() == Item2_8) {
			sp.moveSelected(0, 1);
		}
        if(source.getText() == Item3_1) {
            if(sp.imageOk()) {
				new SearchWindow(i3s);
			}
			else
				JOptionPane.showMessageDialog(i3s.getFrame(), "No data to compare. Open an image first.");
        }
        if(source.getText() == Item3_2) {
            if(sp.insertInDatabase(mess) == false)
                JOptionPane.showMessageDialog(i3s.getFrame(), mess);
        }
        if(source.getText() == Item3_3)
            sp.updateDatabase();
        if(source.getText() == Item5_1)
            new OptionWindow(sp);
        if(source.getText() == Item6_1) {
        	//this area would open the manual
        	JOptionPane.showMessageDialog(null, "No Maunual exists....yet.");
        	
		}
        if(source.getText() == Item6_2)
            new AboutI3SM();
    }
}

