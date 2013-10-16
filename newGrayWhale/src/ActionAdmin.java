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
import java.io.*;


class ActionAdmin {
	final static int NR_ACTIONS=500;		// buffersize for undo and redo actions
    
	private byte actionBuffer[][];
	private int lastAction;
	private int lastRedo;

    public ActionAdmin() {
    	System.out.println("ActionAdmin");//Daniel Remove
		initActionBuffer();
    }

	public void initActionBuffer() {
		actionBuffer = new byte[NR_ACTIONS][];
		clearActionBuffer();
	}
	public void clearActionBuffer() {
		for(int i=0; i<NR_ACTIONS; i++)
			actionBuffer[i] = null;
		lastAction = 0;
		lastRedo = 0;
	}
	public void addAction(FingerPrint fp)
	{
		ByteArrayOutputStream ba = new ByteArrayOutputStream(10240);	// initial buffer size of 10kb, should be sufficient
   		DataOutputStream out = new DataOutputStream(ba);
		StringBuffer mess = new StringBuffer();
		
		if(fp.writeData(out, mess) == false)
		{
			JOptionPane.showMessageDialog(null, "Error during action recording: " + mess);
			return;
		}

		byte snapshot[] = ba.toByteArray();
		
		if(lastAction > 0 && equalArray(snapshot, actionBuffer[lastAction-1]))
			return;

		actionBuffer[lastAction] = snapshot;
		lastAction++;
		
		if(lastAction >= NR_ACTIONS)	// prevent buffer overflow, delete first action
		{
			for(int i=0; i<NR_ACTIONS-1; i++)
				actionBuffer[i] = actionBuffer[i+1];
			lastAction = NR_ACTIONS-1;
			actionBuffer[lastAction] = null;
		}
		lastRedo = lastAction;
	}
	public void undoAction(FingerPrint fp) {
		if(lastAction <= 0)
			return;

		if(lastAction == lastRedo) {
			addAction(fp);
			lastAction--;
		}
		
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(actionBuffer[lastAction-1]));
		lastAction--;

		StringBuffer mess = new StringBuffer();
		
		if(fp.readData(in, mess) == false) {
			JOptionPane.showMessageDialog(null, "Error during undoing action: " + mess);
			return;
		}
	}
	public void removeAction(FingerPrint fp) {		// like undo but without redo-option
		if(lastAction <= 0)
			return;

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(actionBuffer[lastAction-1]));
		lastAction--;
		lastRedo--;

		StringBuffer mess = new StringBuffer();

		if(fp.readData(in, mess) == false) {
			JOptionPane.showMessageDialog(null, "Error during undoing action: " + mess);
			return;
		}
	}
	public void redoAction(FingerPrint fp) {
		if(lastAction >= lastRedo)
			return;
		
		ByteArrayOutputStream ba = new ByteArrayOutputStream(10240);	// initial buffer size of 10kb, should be sufficient
		DataOutputStream out = new DataOutputStream(ba);
		StringBuffer mess = new StringBuffer();

		if(fp.writeData(out, mess) == false)
		{
			JOptionPane.showMessageDialog(null, "Error during redoing action: " + mess);
			return;
		}

		byte snapshot[] = ba.toByteArray();

		lastAction++;

		if(equalArray(snapshot, actionBuffer[lastAction-1]))
			lastAction++;

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(actionBuffer[lastAction-1]));

		if(fp.readData(in, mess) == false) {
			JOptionPane.showMessageDialog(null, "Error during undoing action: " + mess);
			return;
		}
	}
	public boolean empty() {
		return lastAction == 0;
	}
	private boolean equalArray(byte a1[], byte a2[]) {
		if(a1.length != a2.length)
			return false;
		for(int i=0; i<a1.length; i++)
			if(a1[i] != a2[i])
				return false;
		return true;
	}
}

