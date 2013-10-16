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

import java.util.prefs.*;

public class I3SMPrefs {
    private static final String SpeciesTag = "Species";
    private static final String LoggingTag = "Logging";
    private static final String Top50Tag = "Top50";
	private static final String EllipseRedTag = "EllipseRed";
	private static final String EllipseGreenTag = "EllipseGreen";
	private static final String EllipseBlueTag = "EllipseBlue";
	private static final String ReferenceRedTag = "ReferenceRed";
	private static final String ReferenceGreenTag = "ReferenceGreen";
	private static final String ReferenceBlueTag = "ReferenceBlue";
	private static final String ControlRedTag = "ControlRed";
	private static final String ControlGreenTag = "ControlGreen";
	private static final String ControlBlueTag = "ControlBlue";
	private static final String CenterRedTag = "CenterRed";
	private static final String CenterGreenTag = "CenterGreen";
	private static final String CenterBlueTag = "CenterBlue";
	
    private static final String ViewTag = "View";
    private static final String DbPartTag = "DbPart";
	private static final String WinXTag = "WinSizeX";
	private static final String WinYTag = "WinSizeY";

    static public int getSpecies() {
		return Preferences.userRoot().node("I3SM_Options").getInt(SpeciesTag, 0);
	}
    static public int getView() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ViewTag, 0);
	}
    static public int getDbPart() {
		return Preferences.userRoot().node("I3SM_Options").getInt(DbPartTag, 0);
	}
	static public boolean getLogging() {
		return Preferences.userRoot().node("I3SM_Options").getBoolean(LoggingTag, false);
    }
	static public boolean getTop50() {
		return Preferences.userRoot().node("I3SM_Options").getBoolean(Top50Tag, true);
	}
	static public int getEllipseRed() {
		return Preferences.userRoot().node("I3SM_Options").getInt(EllipseRedTag, 255);
	}
	static public int getEllipseGreen() {
		return Preferences.userRoot().node("I3SM_Options").getInt(EllipseGreenTag, 0);
	}
	static public int getEllipseBlue() {
		return Preferences.userRoot().node("I3SM_Options").getInt(EllipseBlueTag, 0);
	}
	static public int getReferenceBlue() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ReferenceBlueTag, 0);
	}
	static public int getReferenceRed() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ReferenceRedTag, 255);
	}
	static public int getReferenceGreen() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ReferenceGreenTag, 0);
	}
	static public int getControlRed() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ControlRedTag, 0);
	}
	static public int getControlGreen() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ControlGreenTag, 255);
	}
	static public int getControlBlue() {
		return Preferences.userRoot().node("I3SM_Options").getInt(ControlBlueTag, 0);
	}
	static public int getCenterRed() {
		return Preferences.userRoot().node("I3SM_Options").getInt(CenterRedTag, 0);
	}
	static public int getCenterGreen() {
		return Preferences.userRoot().node("I3SM_Options").getInt(CenterGreenTag, 0);
	}
	static public int getCenterBlue() {
		return Preferences.userRoot().node("I3SM_Options").getInt(CenterBlueTag, 255);
	}
	static public int getWinSizeX() {
		return Preferences.userRoot().node("I3SM_Options").getInt(WinXTag, 1024);
    }
	static public int getWinSizeY() {
		return Preferences.userRoot().node("I3SM_Options").getInt(WinYTag, 768);
    }
	static public void putSpecies(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(SpeciesTag, i);
	}
	static public void putView(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ViewTag, i);
	}
	static public void putDbPart(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(DbPartTag, i);
	}
	static public void putWinSizeX(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(WinXTag, i);
	}
	static public void putWinSizeY(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(WinYTag, i);
	}
    static public void putLogging(boolean b) {
		Preferences.userRoot().node("I3SM_Options").putBoolean(LoggingTag, b);
	}
    static public void putTop50(boolean b) {
		Preferences.userRoot().node("I3SM_Options").putBoolean(Top50Tag, b);
	}
	static public void putReferenceRed(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ReferenceRedTag, i);
	}
	static public void putReferenceGreen(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ReferenceGreenTag, i);
	}
	static public void putReferenceBlue(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ReferenceBlueTag, i);
	}
	static public void putEllipseRed(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(EllipseRedTag, i);
	}
	static public void putEllipseGreen(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(EllipseGreenTag, i);
	}
	static public void putEllipseBlue(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(EllipseBlueTag, i);
	}
	static public void putControlRed(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ControlRedTag, i);
	}
	static public void putControlGreen(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ControlGreenTag, i);
	}
	static public void putControlBlue(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(ControlBlueTag, i);
	}
	static public void putCenterRed(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(CenterRedTag, i);
	}
	static public void putCenterGreen(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(CenterGreenTag, i);
	}
	static public void putCenterBlue(int i) {
		Preferences.userRoot().node("I3SM_Options").putInt(CenterBlueTag, i);
	}
}
