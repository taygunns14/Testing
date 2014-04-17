package com.example.arduinotrial;

import java.io.IOException;
import java.util.Calendar;

import org.shokai.firmata.ArduinoFirmata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class MainActivity extends FragmentActivity {
	
	//Initialization
	private ArduinoFirmata arduino;
	private String TAG = "ArduinoFragmentsTrial";
	private Handler handler;
	private Handler handler3;
	private Button buttonStage1;
	private Button buttonStage2;
	private int rewardTime;
	private int punishmentTime;
	private int inputPin;
	private int rewardPin;
	private int punishmentPin;
	private int earlyStageRepeatTime;
	private TextView stageHelpText;
	private SharedPreferences prefs;
	private static FragmentManager fm;
	private RelativeLayout mlayout;
	
    private static final String appKey = "gyadr0c5a4ikf82";
    private static final String appSecret = "rjyh241ky9g3vr9";

    private static final int REQUEST_LINK_TO_DBX = 0;

    private Button mLinkButton;
    private DbxAccountManager mDbxAcctMgr;
    private DbxFileSystem dbxFs;
    private String MOUSE_NUMBER;
    private String EXPERIMENT_NUMBER;
    private String FILE_NAME;
    private EditText mouseNumberTextView;
    private EditText experimentNumberTextView;
    private DbxPath filePath;
    private Fragment ia;
	
	//Setting layout and other parameters
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		//Setting the arduino object up
		arduino = new ArduinoFirmata(this);
		Log.v(TAG, "start");
		Log.v(TAG, "ArduinoFirmataVersion : " +ArduinoFirmata.VERSION);
		this.setTitle(this.getTitle()+" v"+ArduinoFirmata.VERSION);
		
		//Setting up sound manager instance
		final Context context = getApplication();
		SoundManager.CreateInstance(context);
		
		//Setting up handler(s) and global variables
		this.handler = new Handler();
		this.handler3 = new Handler();
		fm = this.getSupportFragmentManager();

		//Settings section
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		loadPrefs();
		
		//This is all fragment shit
		String initialTag = "Initial Screen Tag";
		ia = new InitialScreen();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);
		ft.replace(R.id.listFragment, ia, initialTag);
		ft.commit();	
	}
	
	@Override     
	protected void onResume() {
	    super.onResume();            
	    loadPrefs();
	    //Stuff below this was originally in oncreate in the trial dropbox app
        mLinkButton = (Button) findViewById(R.id.link_button);
        mLinkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLinkToDropbox();
            }
        });
        
        //Setting up an account manager
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
        //Inputs for eventual File name
        mouseNumberTextView = (EditText) findViewById(R.id.mouse_number);
        experimentNumberTextView = (EditText) findViewById(R.id.exp_number);
        //Some Jelly Bean bug where you need this line to prevent keyboard from always popping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    if (mDbxAcctMgr.hasLinkedAccount()) {
		    showLinkedView();
            try{
            	//This is the DBX file system we're now working with, based on your linked account
            	dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            } catch (IOException e) {
            	//Do something
            }
		} else {
			showUnlinkedView();
		}
	}
	
	@Override     
	protected void onPause() {         
	    super.onPause();          
	}
	
	public void loadPrefs(){
		//Sets preferences so you can access them:
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.rewardTime = Integer.parseInt(prefs.getString("reward_time", "250"));
		this.punishmentTime = Integer.parseInt(prefs.getString("punishmentTime", "3000"));
		this.inputPin = Integer.parseInt(prefs.getString("input_pin", "8"));
		this.rewardPin = Integer.parseInt(prefs.getString("reward_pin","3"));
		this.punishmentPin = Integer.parseInt(prefs.getString("punishment_pin", "2"));
		this.earlyStageRepeatTime = Integer.parseInt(prefs.getString("early_stage_repeat_time", "30000"));
	}
	
    public void openPreferences(View view){
    	Intent intentPreferences = new Intent(getApplicationContext(), Preferences.class);
    	startActivity(intentPreferences);
    }
    
    //Method that displays textview that says what each stage does
    public void stageHelp(View view){
		this.stageHelpText = (TextView) findViewById(R.id.stages_info_text);
    	boolean on = ((ToggleButton) view).isChecked();
		if (on){
			this.stageHelpText.setVisibility(View.VISIBLE);
		}
		else {
			this.stageHelpText.setVisibility(View.INVISIBLE);
		}
    }
	public void connectArduino(View view){
			try{
				//We connect the arduino
				arduino.connect(); }
			
			catch(IOException e) {
				e.printStackTrace();
				arduino.close();
			}
			catch(InterruptedException e){
				e.printStackTrace();
				arduino.close();}
			}
	
	//Water Flush
	public void waterFlush(View view){
		boolean on = ((ToggleButton) view).isChecked();
		if (on){
			arduino.digitalWrite(rewardPin, true);
		}
		else {
			arduino.digitalWrite(rewardPin,false);
		}
	}
	
	public void showMainActivityFrag(){
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(ia);
		ft.commit();
	}
	//Stage1
	
	public void stage1Start(View view){
		String ftag = "Stage1";
		Stage1Frag bth = new Stage1Frag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.hide(ia);
		ft.add(R.id.listFragment, bth, ftag);
		ft.commit();
	}
	
	public void stage1Response(){
		arduino.digitalWrite(rewardPin,true);
		SoundManager.getInstance().play(1,2);
        writeToFile(getTimeToFile(), "Water Delivery, Automatic");
		handler.postDelayed(new Runnable(){
			public void run(){
				arduino.digitalWrite(rewardPin, false);
			}
		}, rewardTime);
	}
	
	public void stage1Function(View view){
		this.buttonStage1 = (Button) findViewById(R.id.butstage1);
		buttonStage1.setVisibility(View.INVISIBLE);
		final Stage1Frag testfrag = (Stage1Frag) getSupportFragmentManager().findFragmentByTag("Stage1");
		if (testfrag != null) {
			final Thread thread = new Thread(new Runnable() {
				public void run(){
					while(testfrag != null && testfrag.isVisible()) {
						try{
							Thread.sleep(earlyStageRepeatTime);
							handler3.post(new Runnable(){
								@Override
								public void run(){
									stage1Response();
									
								}
							});
						}
						catch(InterruptedException e){
							e.printStackTrace();
						}
				}
					
				}
			});
			thread.start();
		}
		else{
			Toast.makeText(this, "Logic Error at stage0Function" , Toast.LENGTH_LONG).show();
		}
	}
	
	//Stage 2
	public void stage2Start(View view){
		String tagst2 = "Stage2";
		Stage2Frag st2 = new Stage2Frag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.hide(ia);
		ft.add(R.id.listFragment, st2, tagst2);
		ft.commit();
	}
	
	public void stage2Response(View view){
		arduino.digitalWrite(rewardPin,true);
        writeToFile(getTimeToFile(), "Water Delivery");
		SoundManager.getInstance().play(1,2);
		handler.postDelayed(new Runnable(){
			public void run(){
				arduino.digitalWrite(rewardPin, false);
			}
		}, rewardTime);
	}
	
	public void stage2ResponseTimer(){
		arduino.digitalWrite(rewardPin,true);
		SoundManager.getInstance().play(1,2);
        writeToFile(getTimeToFile(), "Water Delivery, Timer");
		handler.postDelayed(new Runnable(){
			public void run(){
				arduino.digitalWrite(rewardPin, false);
			}
		}, rewardTime);
	}
	
	public void stage2Function(View view){
		this.buttonStage2 = (Button) findViewById(R.id.butstage2);
		buttonStage2.setVisibility(View.INVISIBLE);
		final Stage2Frag testFrag = (Stage2Frag) getSupportFragmentManager().findFragmentByTag("Stage2");
		//final InitialScreen initialFrag = (InitialScreen) getSupportFragmentManager().findFragmentByTag("Initial Screen Tag");
		if (testFrag != null) {
			final Thread thread = new Thread(new Runnable() {
				public void run(){
					while(testFrag !=null && testFrag.isVisible()) {
						try{
							Thread.sleep(earlyStageRepeatTime);
							handler3.post(new Runnable(){
								@Override
								public void run(){
									stage2ResponseTimer();
									
								}
							});
						}
						catch(InterruptedException e){
							e.printStackTrace();
						}
				}
					
				}
			});
			thread.start();
		}
		else{
			Toast.makeText(this, "Logic Error at stage2Function" , Toast.LENGTH_LONG).show();
		}
	}
	//Stage3
	public void stage3Start(View view){
		String tagst3 = "Stage3";
		Stage3Frag st3 = new Stage3Frag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.listFragment, st3, tagst3);
		ft.hide(ia);
		ft.commit();
	}
	
	public void stage3Response(View view){
		arduino.digitalWrite(rewardPin,true);
		SoundManager.getInstance().play(1,2);
		writeToFile(getTimeToFile(), "Water Delivery");
		handler.postDelayed(new Runnable(){
			public void run(){
				arduino.digitalWrite(rewardPin, false);
			}
		}, rewardTime);
	}
	//Stage4
	public void stage4Start(View view){
		String tagst4 = "Stage4";
		Stage4Frag st4 = new Stage4Frag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.listFragment, st4, tagst4);
		ft.hide(ia);
		ft.commit();
		arduinoInputThread4(tagst4);
	}
	
	public void resetStage4(){
		RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.stage4layoutinner);
		//RelativeLayout mlayout = (RelativeLayout) view.getParent();
		mlayout.setVisibility(View.INVISIBLE);
	}
	
	public void stimuliStage4(){
		if (fm.findFragmentByTag("Stage4") != null && fm.findFragmentByTag("Stage4").isVisible()){
			RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.stage4layoutinner);
			mlayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void stage4Response(View view){
		arduino.digitalWrite(rewardPin,true);
		SoundManager.getInstance().play(1,2);
		writeToFile(getTimeToFile(), "Water Delivery");
		resetStage4();
		handler.postDelayed(new Runnable(){
			public void run(){
				arduino.digitalWrite(rewardPin, false);
			}
		}, rewardTime);
	}

	public void arduinoInputThread4(String mystring){
		final String stagestring = mystring;
		//Begin log, which is just so we can see what's happening in our app and what code is getting executed
		Log.v(TAG, "Board Version : " +arduino.getBoardVersion());
		//Setting up our input pin (this is for the infrared sensor nose poke) and then turning on pin 13
		//which is the arduino's internal pin, so you can have ambient light/confirm you connected
		arduino.pinMode(inputPin, ArduinoFirmata.INPUT);
		arduino.digitalWrite(13, true);
		Thread thread = new Thread(new Runnable(){
			public void run(){
				if (arduino != null){
				while(arduino.isOpen()){
					try{
						Thread.sleep(100);
						String inputStatus = com.example.arduinotrial.MyApplication.getInputStatus();
						mlayout = (RelativeLayout) findViewById(R.id.stage4layoutinner);
						//Maybe we don't use arduino.isOpen. Maybe we do it by fragvisibility and isopen
						//That way we can decouple each input to its stage
						//Then we'll need to make sure we destroy the thread so we don't lose memory to them
						//So if input is desired (from myapplication), then while isopen and getfragbytag!=null &&isvisible()
						boolean digital_value = arduino.digitalRead(8);
						if (digital_value == true){
							FragmentManager fminside = getSupportFragmentManager();
							String timeoutStatus = com.example.arduinotrial.MyApplication.getTimeoutStatus();
							if (fminside.findFragmentByTag(stagestring) != null && !mlayout.isShown()){
								handler.post(new Runnable(){
									public void run(){	
										//Correct nosepoke, write to file, show stimuli
										writeToFile(getTimeToFile(),"Correct Nosepoke");
										stimuliStage4();
									}
								});
							}
							else if (fminside.findFragmentByTag(stagestring) != null && mlayout.isShown()){
								handler.post(new Runnable(){
									public void run(){	
										//Incorrect nosepoke, write to file
										writeToFile(getTimeToFile(),"Incorrect Nosepoke");
									}
								});
							}
							else {
								handler.post(new Runnable(){
									public void run(){
										Log.v(TAG, "Logic error in arduinoinputthread");
									}
								});
							} 
						}
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			}
		});
		thread.start();
	}
	//Stage 5
	public void startStage5(View view){
		//This occurs when user begins trial from the initial screen (i.e. they press "Stage5")
		String tagst5 = "Stage5";
		Stage5Frag st5 = new Stage5Frag();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.listFragment, st5, tagst5);
		ft.hide(ia);
		ft.commit();
		arduinoInputThread5(tagst5);
	}
	
	public void resetStage5(){
		RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.stage5layoutinner);
		//RelativeLayout mlayout = (RelativeLayout) view.getParent();
		mlayout.setVisibility(View.INVISIBLE);
		//This is where the view on the fragment should be set to gone (i.e. the mouse needs to initiate the trial)
	}

	public void stimuliStage5(){
		//Toast.makeText(this, "stimuliStage5 activated" , Toast.LENGTH_LONG).show();
		if (fm.findFragmentByTag("Stage5") != null && fm.findFragmentByTag("Stage5").isVisible()){
			RelativeLayout mlayout = (RelativeLayout) findViewById(R.id.stage5layoutinner);
			mlayout.setVisibility(View.VISIBLE);
		}
		//This is where the view should be set to active again (i.e. the mouse initiated the trial)
	}
	
	public void arduinoInputThread5(String mystring){
		final String stagestring = mystring;
		//Begin log, which is just so we can see what's happening in our app and what code is getting executed
		Log.v(TAG, "Board Version : " +arduino.getBoardVersion());
		//Setting up our input pin (this is for the infrared sensor nose poke) and then turning on pin 13
		//which is the arduino's internal pin, so you can have ambient light/confirm you connected
		arduino.pinMode(inputPin, ArduinoFirmata.INPUT);
		arduino.digitalWrite(13, true);
		Thread thread = new Thread(new Runnable(){
			public void run(){
				if (arduino != null){
					FragmentManager fminside = getSupportFragmentManager();
					mlayout = (RelativeLayout) findViewById(R.id.stage5layoutinner);
					//&& fminside.findFragmentByTag(stagestring) != null && fminside.findFragmentByTag(stagestring).isVisible()
				while(arduino.isOpen()){
					try{
						Thread.sleep(100);
						String inputStatus = com.example.arduinotrial.MyApplication.getInputStatus();
						//Maybe we don't use arduino.isOpen. Maybe we do it by fragvisibility and isopen
						//That way we can decouple each input to its stage
						//Then we'll need to make sure we destroy the thread so we don't lose memory to them
						//So if input is desired (from myapplication), then while isopen and getfragbytag!=null &&isvisible()
						boolean digital_value = arduino.digitalRead(8);
						if (digital_value == true){
							String timeoutStatus = com.example.arduinotrial.MyApplication.getTimeoutStatus();
							if (!mlayout.isShown()){
								if (timeoutStatus == "NoTimeout" || timeoutStatus == "NotBegun"){
									handler.post(new Runnable(){
										public void run(){	
											//Correct nosepoke, write to file
											writeToFile(getTimeToFile(),"Correct Nosepoke");
											stimuliStage5();
										}
									});
								}
								else if (timeoutStatus == "TimeoutActive"){
									handler.post(new Runnable(){
										public void run(){	
											//Incorrect nosepoke, write to file
											writeToFile(getTimeToFile(),"Incorrect Nosepoke");
										}
									});
								}
								else {
									handler.post(new Runnable(){
										public void run(){
											Log.v(TAG, "Logic error in arduinoinputthread");
										}
									});
								} 
							}
							else {
								//Incorrect nosepoke, write to file
								writeToFile(getTimeToFile(),"Incorrect Nosepoke");
							}
						}
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			}
		});
		thread.start();
	}
	
	
	public void correctResponse(){
		//Pin 3 is pump
		//Pin 13 is Arduino's built-in LED
		arduino.digitalWrite(rewardPin, true);
		com.example.arduinotrial.MyApplication.setTimeoutStatus("TimeoutActive");
        writeToFile(getTimeToFile(), "Water Delivery");
		new UITimeout().execute("");
		handler.postDelayed(new Runnable(){
			@Override
			public void run(){
				arduino.digitalWrite(rewardPin, false);
			}
		}, rewardTime);
	}
	public void incorrectResponse(){
		//Pin 2 is punishment LEDs
		arduino.digitalWrite(punishmentPin, true);
		com.example.arduinotrial.MyApplication.setTimeoutStatus("TimeoutActive");
		writeToFile(getTimeToFile(), "Punishment LEDs");
		new UITimeout().execute("");
		handler.postDelayed(new Runnable(){
			@Override
			public void run(){
				arduino.digitalWrite(punishmentPin, false);
			}
		}, punishmentTime);
	}
	public void mousePoke (View view){
		int ID = view.getId();
		if (ID == R.id.stage5but1){
			resetStage5();
			correctResponse();
			SoundManager.getInstance().play(1,2);
		}
		else if(ID == R.id.stage5but2){
			resetStage5();
			incorrectResponse();
			SoundManager.getInstance().play(2,2);
		}
		else{
			Toast.makeText(this, "You've got a logic error, kid" , Toast.LENGTH_LONG).show();
		}
	}
	
	//Get Time
    public String getTimeToFile(){
   	 Calendar cal = Calendar.getInstance(); 

        String ms = Integer.toString(cal.get(Calendar.MILLISECOND));
        String sec = Integer.toString(cal.get(Calendar.SECOND));
        String min = Integer.toString(cal.get(Calendar.MINUTE));
        String hr= Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        //Require an extra line of code because months are 0 indexed in Java (Jan is 0, not 1)
        int monthzero = cal.get(Calendar.MONTH) + 1;
        String month = Integer.toString(monthzero);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String timestampOutput = day+"/"+month+"/" + year +", " + hr + ":" + min + ":" + sec + "." + ms + " - ";
        return timestampOutput;
   }
	
	//File Writing
	public void writeToFile(String time, String message){
    	try{
    		if (dbxFs.exists(filePath)){
    			DbxFile testFile = dbxFs.open(filePath);
        		try{
        			testFile.appendString(time + message + "\n");
        		}
        		finally{
        			testFile.close();
        		}
    		}
    		else {
    			DbxFile testFile = dbxFs.create(filePath);
        		try 
        		{testFile.appendString(time + message + "\n");
        		}
        		finally {
        			testFile.close();
        		}
    		}

    	}
    	catch (IOException e){
    		//Do something
    	}
    }

    //Getting File name
    public void fileName(View view){
    	Calendar cal = Calendar.getInstance();
    	//Need to put stage in here too
    	 EXPERIMENT_NUMBER = mouseNumberTextView.getText().toString();
    	 MOUSE_NUMBER = experimentNumberTextView.getText().toString();
    	 String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
         String month = Integer.toString(cal.get(Calendar.MONTH));
         String year = Integer.toString(cal.get(Calendar.YEAR));
         FILE_NAME = EXPERIMENT_NUMBER + "_" + MOUSE_NUMBER + "_" + day + "-" + month + "-" + year + ".txt";
         //Here's where you'd want to include the part about what settings you have set up
         
         //Now we have the file name, let's make the path
         filePath = new DbxPath(DbxPath.ROOT, FILE_NAME);
         Toast.makeText(this, "File Path Created" , Toast.LENGTH_SHORT).show();

         //No timestamp, but make it have prefs
    }
    
    public void unlinkAccount(View view){
    	if (mDbxAcctMgr.hasLinkedAccount()){
    		mDbxAcctMgr.unlink();
    		showUnlinkedView();
    	}
    	else{
			Toast.makeText(this, "No Account Linked" , Toast.LENGTH_SHORT).show();

    	}
    }
	
    private void showLinkedView() {
        mLinkButton.setVisibility(View.GONE);
    }

    private void showUnlinkedView() {
        mLinkButton.setVisibility(View.VISIBLE);
    }

    private void onClickLinkToDropbox() {
        mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
            } else {
            	
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
	
    //Separator
	   private class UITimeout extends AsyncTask<String, Void, String> {

	        @Override
	        protected String doInBackground(String...params) {
	                try {
	                    Thread.sleep(5000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            return "Executed";
	        }

	        @Override
	        protected void onPostExecute(String result) {
				com.example.arduinotrial.MyApplication.setTimeoutStatus("NoTimeout");
	        }

	        @Override
	        protected void onPreExecute() {}

	    }
}
