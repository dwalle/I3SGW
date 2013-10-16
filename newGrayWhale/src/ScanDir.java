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

import java.util.*;
import java.io.File;
import javax.swing.*;

public class ScanDir {
	int cnt = 0;
	boolean filesOutsideViews = false;
	
	public ScanDir(String[] types) {
    	System.out.println("ScanDir");//Daniel Remove

		String ev = System.getenv("I3SM_DATA");
		File male    = new File(ev + "/Male");
		File female  = new File(ev + "/Female");
		File unknown = new File(ev + "/UnknownSex");
		
		types[0] = new String("All views");	// do not change this label without changing the c++ sources in Compare::find
		cnt = 1;

		if(!male.exists() || !female.exists() || !unknown.exists()) {
			JOptionPane.showMessageDialog(null, "One or more of the directories Male, Female and UnknownSex do not exist.\nIt is not possible to search in parts of the database.");
			return;
		}

		File[] mfl = male.listFiles(new DirFilter());
		cnt = parseForDirectories(mfl, types, cnt);
		File[] ffl = female.listFiles(new DirFilter());
		if(cnt < I3SM.MAX_VIEWS)
			cnt = parseForDirectories(ffl, types, cnt);
		File[] ufl = unknown.listFiles(new DirFilter());
		if(cnt < I3SM.MAX_VIEWS)
			cnt = parseForDirectories(ufl, types, cnt);
		if(cnt > 1 && filesOutsideViews)
			JOptionPane.showMessageDialog(null, "Your database is inconsistent. You have defined " + (cnt-1) + " views but there are still image files outside the view directories.\nThese images will only be found when searching within 'All views'.");

		return;
	}
	public int getCnt() {
		return cnt;
	}
	private int parseForDirectories(File[] fl, String[] sl, int cnt) {
		for(int i=0;i<fl.length; i++) {
			File[] typel = fl[i].listFiles(new DirFilter());
			
			if(filesOutsideViews == false) {
				File[] imagel = fl[i].listFiles(new JpegFilter());
				if(imagel.length > 0)
					filesOutsideViews = true;
			}

			for(int j=0; j<typel.length; j++) {
				if(cnt == I3SM.MAX_VIEWS) {
					JOptionPane.showMessageDialog(null, "The maximum number of separate views (" + I3SM.MAX_VIEWS + ") has been reached.\nYou probably moved a directory to the wrong location.");
					return cnt;
				}
				
				String s = typel[j].getName().toLowerCase();
				char[] ca = s.toCharArray();
				if(ca[0] >= 'a' && ca[0] <= 'z')
					ca[0] = (char) (ca[0] - ('a'-'A'));
				s = new String(ca);
				
				boolean found = false;
				for(int k=0; k<sl.length; k++) {
					if(sl[k] == null)
						break;
					if(sl[k].equals(s)) {
						found = true;
						break;
					}
				}
				if(found == false) {
					sl[cnt] = s;
					cnt++;
				}
			}
		}
		return cnt;
	}
}

