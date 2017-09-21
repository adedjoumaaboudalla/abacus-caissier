package pv.projects.mediasoft.com.pventes.activities;

import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceActivity;

import pv.projects.mediasoft.com.pventes.R;

public class ReglageActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
