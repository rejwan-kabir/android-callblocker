package test.android.callblocker.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import test.android.callblocker.R;

public class SettingsActivity extends PreferenceActivity {
	// addPreferencesFromResource is depecated from API 11.
	// we have done this for supporting devices < 3.0
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.settings );
		// TODO : See
		// http://stackoverflow.com/questions/6822319/what-to-use-instead-of-addpreferencesfromresource-in-a-preferenceactivity
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
