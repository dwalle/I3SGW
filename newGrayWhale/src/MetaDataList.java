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

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.prefs.*;

public class MetaDataList implements Cloneable
{
	boolean error = false;

	// metadata types, YESNO = yes or no; NUMBER is a double; SML = small,medium,large
	final static int YESNO     = 0;
	final static int NUMBER    = 1;
	final static int SML       = 2;
	final static int UNDEFINED = 0;
	final static int SMALL     = 1;
	final static int MEDIUM    = 2;
	final static int LARGE     = 3;
	
	StringBuffer name[];
	Number value[];
	Integer type[];
	int cnt;
	
	public MetaDataList() {
    	System.out.println("MetaDataList");//Daniel Remove

		name = new StringBuffer[20];
		type = new Integer[20];
		value = new Number[20];

		for(int i=0; i<20; i++) {
			name[i] = null;
			value[i] = null;
			type[i] = null;
		}
		cnt = 0;
    }
	
	void addMember(String nm, int tp) {
		if(tp < YESNO || tp > SML || nm == null || nm == "") {
			JOptionPane.showMessageDialog(null, "Parameter error in MetaDataList::addMember.");
			return;
		}
		if(cnt == 20 && error == false) {
			JOptionPane.showMessageDialog(null, "Maximum number (20) of metadata elements has been reached.");
			error = true;
			return;
		}
		name[cnt] = new StringBuffer(nm);
		type[cnt] = new Integer(tp);
		if(type[cnt].intValue() == NUMBER)
			value[cnt] = new Double(-1);
		else
			value[cnt] = new Integer(UNDEFINED);
		cnt++;
	}
	Number getMember(String nm, int tp) {
		for(int i=0; i<cnt; i++)
			if(name[i].toString().equals(nm) && tp == type[i].intValue())
				return (Number) value[i];
		return null;
	}
	// AddMember does not allow multiple elements with the same name. In this case the last one overwrites
	void addMember(String nm, int tp, Number val) {
		if(tp < YESNO || tp > SML || nm == null || nm.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Parameter error in MetaDataList::addMember.");
			return;
		}
		if(val != null &&
		   ((tp == YESNO && val.getClass().getName() != "java.lang.Integer") || 
		    (tp == SML && val.getClass().getName() != "java.lang.Integer") || 
		    (tp == NUMBER && val.getClass().getName() != "java.lang.Double"))) {
			JOptionPane.showMessageDialog(null, "Parameter error in MetaDataList::addMember.");
			return;
		}
		for(int i=0; i<cnt; i++) {
			if(name[i].toString().equals(nm)) {
				if(tp != type[i]) {
					type[i] = new Integer(tp);
					JOptionPane.showMessageDialog(null, "Resetting type of data element " + nm + ".");
				}
				value[i] = val;
				return;
			}
		}
		if(cnt == 20 && error == false) {
			JOptionPane.showMessageDialog(null, "Maximum number (20) of metadata elements has been reached.");
			error = true;
			return;
		}
		name[cnt] = new StringBuffer(nm);
		type[cnt] = new Integer(tp);
		value[cnt] = val;
		cnt++;
	}
	public MetaDataList copy() {
		MetaDataList cp = new MetaDataList();
		cp.cnt = cnt;
		for(int i=0; i<cnt; i++) {
			cp.name[i] = new StringBuffer(name[i].toString());
			cp.type[i] = new Integer(type[i].intValue());
			if(value[i] == null)
				continue;
			if(value[i].getClass().getName() == "java.lang.Double")
				cp.value[i] = new Double(((Double)value[i]).doubleValue());
			else if(value[i].getClass().getName() == "java.lang.Integer")
				cp.value[i] = new Integer(((Integer)value[i]).intValue());
		}
		
		return cp;
	}
	public boolean equals(MetaDataList m) {
		if(cnt != m.cnt)
			return false;
		for(int i=0; i<cnt; i++) {
			if(name[i].toString().equals(m.name[i].toString()) == false)
				return false;
			if(type[i].equals(m.type[i]) == false)
				return false;
			if(value[i] == null && m.value[i] == null)
				continue;
			if(value[i] == null || m.value[i] == null)
				return false;
			if(value[i].getClass().getName() == "java.lang.Double" && ((Double)value[i]).doubleValue() != ((Double)m.value[i]).doubleValue())
				return false;
			if(value[i].getClass().getName() == "java.lang.Integer" && ((Integer)value[i]).intValue() != ((Integer)m.value[i]).intValue())
				return false;
		}
			
		return true;
	}
    public boolean writeData(DataOutputStream out) {
        try {
			out.writeInt(cnt);
			for(int i=0; i<cnt; i++) {
				out.writeInt(name[i].length());
				out.write(name[i].toString().getBytes(), 0, name[i].length());
				out.writeInt(type[i].intValue());
				switch(type[i].intValue()) {
					case YESNO:  out.writeInt(((Integer) value[i]).intValue()); break;
					case SML:    out.writeInt(((Integer) value[i]).intValue()); break;
					case NUMBER: out.writeDouble(((Double) value[i]).doubleValue()); break;
				}
			}
        } catch(IOException e) {
            return false;
        }
        return true;
    }
    public boolean readData(DataInputStream in) {
        try {
			cnt = in.readInt();
			for(int i=0; i<cnt; i++) {
				int strlen = in.readInt();
				byte[] buf = new byte[strlen];
				in.read(buf, 0, strlen);
				name[i] = new StringBuffer(new String(buf));
				type[i] = new Integer(in.readInt());
				switch(type[i].intValue()) {
					case YESNO:  
					case SML:    value[i] = new Integer(in.readInt()); break;
					case NUMBER: value[i] = new Double(in.readDouble()); break;
				}
			}
        } catch(IOException e) {
            return false;
        }
        return true;
    }
	
	public void setValue(int i, Number val) {
		if(i<0 || i >= cnt) {
			JOptionPane.showMessageDialog(null, "The selected metadata is not available ("+ i + ").");
			return;
		}
		value[i] = val;
	}
	public String getName(int i) {
		if(i<0 || i >= cnt) {
			JOptionPane.showMessageDialog(null, "The selected metadata name is not available ("+ i + ").");
			return new String("Empty");
		}
		return name[i].toString();
	}
	public int getType(int i) {
		if(i<0 || i >= cnt) {
			JOptionPane.showMessageDialog(null, "The selected metadata type is not available ("+ i + ").");
			return -1;
		}
		return type[i].intValue();
	}
	public Number getValue(String nm, int tp) {
		for(int i=0; i<cnt; i++) {
			if(!name[i].toString().equals(nm) || tp != type[i].intValue())
				continue;
			return value[i];
		}
		return null;
	}
	public Number getValue(int i) {
		if(i<0 || i >= cnt) {
			JOptionPane.showMessageDialog(null, "The selected metadata name is not available ("+ i + ").");
			return null;
		}
		return value[i];
	}
	public int getCnt() {
		return cnt;
	}
}
