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
import java.io.*;
import java.awt.*;
import javax.swing.*;

class GetImageFile {
    String s = null;
    String ext = null;
    
    public GetImageFile(String buf) {
    	System.out.println("GetImageFile");//Daniel Remove
       s = buf.replaceAll(".fgp", ".jpg");
       ext = new String(".jpg");
       File f = new File(s);
       if(f.exists())
            return;
       s = buf.replaceAll(".fgp", ".jpeg");
       ext = new String(".jpeg");
       f = new File(s);
       if(f.exists())
            return;
       s = buf.replaceAll(".fgp", ".JPEG");
       ext = new String(".JPEG");
       f = new File(s);
       if(f.exists())
            return;
       s = buf.replaceAll(".fgp", ".JPG");
       ext = new String(".JPG");
       f = new File(s);
       if(f.exists())
           return;
       s = buf.replaceAll(".fgp", ".gif");
       ext = new String(".gif");
       f = new File(s);
       if(f.exists())
           return;
       s = buf.replaceAll(".fgp", ".GIF");
       ext = new String(".GIF");       
       f = new File(s);
       if(f.exists())
           return;
       s = buf.replaceAll(".fgp", ".png");
       ext = new String(".png");
       f = new File(s);
       if(f.exists())
           return;
       s = buf.replaceAll(".fgp", ".PNG");
       ext = new String(".PNG");
       f = new File(s);
       if(f.exists())
           return;
       s = buf.replaceAll(".fgp", ".tif");
       ext = new String(".tif");       
       f = new File(s);
       if(f.exists())
           return;
       s = buf.replaceAll(".fgp", ".TIF");
       ext = new String(".TIF");
       f = new File(s);
       if(f.exists())
           return;
       s = new String(buf);
	   JOptionPane.showMessageDialog(null, "Could not find a corresponding image file for " + buf);
	}
        
    public String getImageString() {
        return s;
    }
    public String getImageExtension() {
        return ext;
    }
}
