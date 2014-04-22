/**
 * BloobaSettings.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 4, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity for initializing Blooba settings screen
 */
public class BloobaSettings extends PreferenceActivity {

	/**
	 * Merely loads the resource with preference screen configuration
	 * 
	 * @param savedInstanceState
	 *            context
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.blooba_settings);
	}

}
