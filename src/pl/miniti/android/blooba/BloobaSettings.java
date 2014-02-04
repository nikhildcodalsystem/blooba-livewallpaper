package pl.miniti.android.blooba;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BloobaSettings extends PreferenceActivity {
	@Override
	@SuppressWarnings("all")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.blooba_settings);
	}

}
