package com.zhaoshouren.android.wallpaper;

import com.zhaoshouren.android.wallpaper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZhaoShourenWallpaperSettings extends PreferenceActivity 
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	    @Override
	    protected void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        
	        getPreferenceManager().setSharedPreferencesName(
	                ZhaoShourenWallpaper.SHARED_PREFS_NAME);
	        addPreferencesFromResource(R.xml.settings);
	        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	    }

	    @Override
	    protected void onDestroy() {
	        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
	                this);
	        super.onDestroy();
	    }

	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
	    }
	
}
