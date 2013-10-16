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
import java.awt.*;
import javax.swing.*;
import java.util.prefs.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class MetaDataXML extends DefaultHandler
{
	static boolean error = false;
	StringBuffer speciesName[];
	StringBuffer control1[];
	StringBuffer control2[];
	StringBuffer control3[];
	MetaDataList mdl[];
	String eName;
	int cnt;
	String tmpName;
	String tmpType;
	
	public MetaDataXML(String filename) {
    	System.out.println("MetaDataXML");//Daniel Remove
		speciesName = new StringBuffer[20];
		control1 = new StringBuffer[20];
		control2 = new StringBuffer[20];
		control3 = new StringBuffer[20];
		mdl = new MetaDataList[20];
		tmpName = null;
		tmpType = null;

		for(int i=0; i<20; i++) {
			speciesName[i] = new StringBuffer("Generic");
			control1[i] = new StringBuffer("Control1");
			control2[i] = new StringBuffer("Control2");
			control3[i] = new StringBuffer("Control3");
			mdl[i] = new MetaDataList();
		}

        File f = new File(filename);
        if(!f.exists()) {
            JOptionPane.showMessageDialog(null, "File " + filename + " does not exist. Generic controlpoint names are used and it will not be possible to search on metadata.");
			error = true;
            cnt = 1;
			return;
        }

		cnt = 0;

        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( new File(filename), this);
        } catch (Throwable t) {
			JOptionPane.showMessageDialog(null, "An error was encountered while parsing " + filename + ". Generic names are used and it will not be possible to search on metadata.");
			error = true;
			cnt = 1;
			speciesName[0] = new StringBuffer("Generic");
			control1[0] = new StringBuffer("Control1");
			control2[0] = new StringBuffer("Control2");
			control3[0] = new StringBuffer("Control3");
        }
    }
	
	public void getNamesControlPoints(StringBuffer c1, StringBuffer c2, StringBuffer c3) {
        int species = getSpeciesIndex();
		if(species == -1) {
			control1[0] = new StringBuffer("Control1");
			control2[0] = new StringBuffer("Control2");
			control3[0] = new StringBuffer("Control3");
		} else {
			c1.append(control1[species]);
			c2.append(control2[species]);
			c3.append(control3[species]);
		}
	}
	public int getSpeciesIndex() {
        int species = I3SMPrefs.getSpecies();
		if(species >= cnt) {
			if(error == false) {
				JOptionPane.showMessageDialog(null, "The selected species does not match the corresponding xml-file. Generic names are used without metadata.");
				error = true;
			}
			return -1;
		}
		return species;
	}
	public String getSpecies(int i) {
		if((i<0 || i >= cnt)) {
			if(error == false) {
				error = true;
				JOptionPane.showMessageDialog(null, "The selected species does not match the corresponding xml-file. Generic names are used.");
			}
			return new String("Generic");
		}
		return speciesName[i].toString();
	}
	public MetaDataList getMetaDataList(int i) {
		if((i<0 || i >= cnt)) {
			if(error == false) {
				error = true;
				JOptionPane.showMessageDialog(null, "The selected species does not match the corresponding xml-file, no metadata will be available.");
			}
			return new MetaDataList();
		}
		return mdl[i];
	}
	public int getCnt() {
		return cnt;
	}

    //===========================================================
    // SAX DocumentHandler methods
    //===========================================================

    public void startDocument()
    throws SAXException
    {
    }

    public void endDocument()
    throws SAXException
    {
    }

    public void startElement(String namespaceURI,
                             String lName, // local name
                             String qName, // qualified name
                             Attributes attrs)
    throws SAXException
    {
        eName = lName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false

		if(cnt == 20) {
			JOptionPane.showMessageDialog(null, "A maximum of 20 different species has been reached, all other information is discarded.");
		}			
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name 
                if ("".equals(aName)) aName = attrs.getQName(i);
				
				if(aName.equals("type"))
					speciesName[cnt] = new StringBuffer(attrs.getValue(i));
            }
        }
    }

    public void endElement(String namespaceURI,
                           String sName, // simple name
                           String qName  // qualified name
                          ) throws SAXException
    {
		if(!qName.equals("metadata"))
			return;
		tmpName = tmpName.trim();
		tmpType = tmpType.trim();

		if(tmpName == null || tmpType == null)
			throw new SAXException();

		if(tmpType.equals("yesno"))
			mdl[cnt-1].addMember(tmpName, MetaDataList.YESNO);
		else if(tmpType.equals("number"))
			mdl[cnt-1].addMember(tmpName, MetaDataList.NUMBER);
		else if(tmpType.equals("smallmediumlarge"))
			mdl[cnt-1].addMember(tmpName, MetaDataList.SML);
		else
			throw new SAXException();

		tmpName = null;
		tmpType = null;
    }

    public void characters(char buf[], int offset, int len)
    throws SAXException
    {
		do {
			int ii = (int) buf[offset+len-1];
			if(ii == 32 || ii == 10 || ii == 13)
				len--;
			else
				break;
		} while(len > 0);
		if(len == 0)
			return;

        String s = new String(buf, offset, len);

		if(eName.equals("c1"))
			control1[cnt] = new StringBuffer(s);
		if(eName.equals("c2"))
			control2[cnt] = new StringBuffer(s);
		if(eName.equals("c3"))
		{
			control3[cnt] = new StringBuffer(s);
			cnt++;
		}
		if(eName.equals("name"))
			tmpName = s;
		if(eName.equals("type"))
			tmpType = s;
    }
}
