package com.hust.hddv.vietnamesettsengine;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

public class VietnameseTTSEngineSettings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
    }

    protected boolean isValidFragment(String fragmentName) {
        return VietnameseTTSEngineSettings.class.getName().equals(fragmentName) ||
                GeneralSettingsFragment.class.getName().equals(fragmentName);
    }

}
