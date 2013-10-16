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

public class TextInput extends JPanel implements ActionListener {
	static JFrame frame = null;
	JTextArea textArea;
	SharkPanel sp;
	StringBuffer s, old;
	JButton close;
	
    public TextInput(StringBuffer _s, SharkPanel _sp) {
    	System.out.println("TextInput");//Daniel Remove

		s = _s;
		sp = _sp;
		old = new StringBuffer(s);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Create a text area.
		if(s.length() > 0)
			textArea = new JTextArea(s.toString());
		else
			textArea = new JTextArea("Empty comment field");
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
		
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(500, 250));
        
		//Put everything together.
        add(areaScrollPane, BorderLayout.LINE_START);

		close = new JButton("Save");
        close.setToolTipText("Close and save results");
        close.setMnemonic(KeyEvent.VK_C);
        close.addActionListener(this);

        add(Box.createRigidArea(new Dimension(100,1)));
        add(close);
		
		createAndShowGUI();
    }
    private void createAndShowGUI() {
        if(frame != null)
            frame.dispose();
        frame = new JFrame("I3S Manta: Metadata input");
        frame.setContentPane(this);
        frame.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { frame.dispose(); frame = null; } });
        // frame.setSize(new Dimension(400, 300));                                            
        frame.setLocation(200, 200);
        
        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("images/i3scicon.gif"));
        frame.setIconImage(imageIcon.getImage());
        
        frame.pack();
        frame.setVisible(true);        
    }
    public void actionPerformed(ActionEvent e) {
		if((JButton) e.getSource() == close)
		{
			s.replace(0, s.length(),textArea.getText());
			
			if(s.toString().equals(old.toString()) == false)
				sp.changeMade(true);
			frame.dispose();
		}
    }
}
