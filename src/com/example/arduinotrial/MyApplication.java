package com.example.arduinotrial;

import android.app.Application;

public class MyApplication extends Application {
	private static String timeout = "NotBegun";
	private static String stage_active = "Null";
	private static String input_status = "No Input Required";
	
	public static String getTimeoutStatus(){
		return timeout;
	}
	
	public static void setTimeoutStatus(String thisTimeout){
		timeout = thisTimeout;
	}
	
	public static String getStageActive(){
		return stage_active;
	}
	
	public static void setStageActive(String this_stage_active){
		stage_active = this_stage_active;
	}
	public static String getInputStatus(){
		return input_status;
		
	}
	public static void setInputStatus(String new_input_status){
		input_status = new_input_status;
	}
}
