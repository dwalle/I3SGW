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

import java.io.*;
import javax.swing.*;

class CopyFile {
    byte buf [] = null;
    int size = 0;
    String in = null;
    String out = null;
    
    public CopyFile(String _in, String _out) {
    	System.out.println("CopyIcon");//Daniel Remove
        in = _in;
        out = _out;
        File f = new File(in);
        size = (int) f.length();
        buf = new byte [size];
    }
    public boolean doIt() {
        File fin = new File(in);
        if(fin.isFile() == false || fin.canRead() == false) {
            JOptionPane.showMessageDialog(null, "A problem occurred when trying to read " + in);
            return false;
        }
        File fout = new File(out);
        if(fout.exists()) {
            int n = JOptionPane.showConfirmDialog(null, out + " already exists. Overwrite?",
                                                  "Overwrite?",
                                                  JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.NO_OPTION)
                return false;
        }
        
        StringBuffer mess = new StringBuffer();
        if(readFile(mess) == false) {
            JOptionPane.showMessageDialog(null, "A problem occurred when trying to read " + in);
            return false;
        }
        if(writeFile(mess) == false) {
            JOptionPane.showMessageDialog(null, "A problem occurred when trying to write " + out);
            return false;
        }
        return true;
    }
    
    public boolean writeFile(StringBuffer mess) {
        try {
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(out));
            dout.write(buf, 0, size);
            dout.close();
        } catch(FileNotFoundException fnf) {
            mess.append("Could not open " + out + " for writing.");
            return false;
        } catch(IOException e) {
            mess.append("Error while writing to " + out);
            return false;
        }
        return true;
    }
    
    private boolean readFile(StringBuffer mess) {
        try {
            DataInputStream din = new DataInputStream(new FileInputStream(in));
            din.read(buf, 0, size);
            din.close();
        } catch(FileNotFoundException fnf) {
            mess.append("Could not find file " + in);
        } catch(IOException e) {
            mess.append("Error while reading from " + in);
            return false;
        }
        return true;
    }
}
