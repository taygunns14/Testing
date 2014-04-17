package com.example.arduinotrial;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;


public class Preferences extends PreferenceActivity {
	private OnSharedPreferenceChangeListener myPrefListener;
	private SharedPreferences preferences;
	//implements onSharedPreferenceChangeListener
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        //Setting the preferences summaries to their current values. 
	        //The listener will update them if the user changes any.
	        
	        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
	        //THIS IS SHITTY CODING, IT'S GOING TO HAVE TO GET FIXED AT SOME POINT
	        //Might help to actually use string resources for once
	        
	    }

	   @Override
	    public void onBuildHeaders(List<Header> target) {
	        loadHeadersFromResource(R.xml.preference_headers, target);
	    }
	    /*
	  @Override
	    protected void onPause(){
	    	preferences.unregisterOnSharedPreferenceChangeListener(myPrefListener);
	    }
	    @Override
	    protected void onResume(){
	    	preferences.registerOnSharedPreferenceChangeListener(myPrefListener);
	    }
	   	 
	    public void onSharedPreferencedChanged(SharedPreferences sharedPrefs, String key){
	        String newValue = sharedPrefs.getString(key,"");
	        setSummary());       
	    } */
}
