package com.ericblue.mindstream.preferences;

import java.util.prefs.Preferences;

/**
 * <p>Title:		PreferencesManager</p><br>
 * <p>Description:	Preference Manager to read/write system tray app settings</p><br>
 * @author		    <a href="http://eric-blue.com">Eric Blue</a><br>
 *
 * $Date: 2011-07-24 17:54:27 $ 
 * $Author: ericblue76 $
 * $Revision: 1.2 $
 *
 */


public class PreferenceManager {
	
	static Preferences prefs;

	public static Preferences loadPreferences() {
		
		prefs = Preferences.userRoot().node(PreferenceManager.class.getName());
		return prefs;
		
	}
	
	
}
