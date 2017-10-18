package com.wbapp.omxvideo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ClientSettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.clientprefs);
    }
}
