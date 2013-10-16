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
import java.text.*;

public class MetaDataCompareList extends MetaDataList
{
	final static int MAX_FIELDS = 20;
	
	Integer compare[];
	
	public MetaDataCompareList() {
		super();    	
		System.out.println("MetaDataCOmpareList");//Daniel Remove
		compare = new Integer[MAX_FIELDS];
		for(int i=0; i<MAX_FIELDS; i++) 
			compare[i] = null;
    }
	public boolean match(MetaDataList mdl) {
		for(int i=0; i<cnt; i++) {
			Number val = mdl.getValue(name[i].toString(), type[i].intValue());

			if(val == null)
				continue;
	
			switch(compare[i].intValue()) {
				case 0: break; // don't care
				case 1: // equals
					if(type[i].intValue() == NUMBER) {
						if(val.doubleValue() != value[i].doubleValue() && val.doubleValue() != -1)
							return false;
					}
					else
						if(val.intValue() != value[i].intValue() && val.intValue() != UNDEFINED)
							return false;
					break;
				case 2: // smaller than
					if(type[i].intValue() == NUMBER) {
						if(val.doubleValue() >= value[i].doubleValue() && val.doubleValue() != -1)
							return false;
					}
					else
						if(val.intValue() >= value[i].intValue() && val.intValue() != UNDEFINED)
							return false;
					break;
				case 3: // greater than
					if(type[i].intValue() == NUMBER) {
						if(val.doubleValue() <= value[i].doubleValue() && val.doubleValue() != -1)
							return false;
					}
					else
						if(val.intValue() <= value[i].intValue() && val.intValue() != UNDEFINED)
							return false;
					break;
			}
		}
		return true;
	}
	void getMember(String nm, int tp, Integer cmp, Number val) {
		for(int i=0; i<cnt; i++) {
			if(name[i].toString().equals(nm) && tp == type[i].intValue()) {
				val = value[i];
				cmp = compare[i];
			}
		}
		cmp = null;
		val = null;
	}
	// AddMember does not allow multiple elements with the same name. In this case the last one overwrites
	void addMember(String nm, int tp, int cmp, Number val) {
		if(tp < YESNO || tp > SML || nm == null || nm.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Parameter error in MetaDataCompareList::addMember.");
			return;
		}
		if(val != null &&
		   ((tp == YESNO && val.getClass().getName() != "java.lang.Integer") || 
		    (tp == SML && val.getClass().getName() != "java.lang.Integer") || 
		    (tp == NUMBER && val.getClass().getName() != "java.lang.Double"))) {
			JOptionPane.showMessageDialog(null, "Parameter error in MetaDataCompareList::addMember.");
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
		if(cnt == MAX_FIELDS && error == false) {
			JOptionPane.showMessageDialog(null, "Maximum number (" + MAX_FIELDS + ") of metadata elements has been reached.");
			error = true;
			return;
		}
		name[cnt] = new StringBuffer(nm);
		type[cnt] = new Integer(tp);
		value[cnt] = val;
		compare[cnt] = new Integer(cmp);
		cnt++;
	}
}
