package com.example.arduinotrial;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BeginTrialFragment extends Fragment {
	
	//Container Activity must implement this interface
	//public interface BeginTrialListener {
		//public void beginningSwitch(View view);
	//}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//Inflate the layout for this fragment
		return inflater.inflate(R.layout.begin_trial_view, container, false);
	}
}
