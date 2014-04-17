package com.example.arduinotrial;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.example.arduinotrial.R;

public class SoundManager {
		//App context and initializing
		private Context pContext;
		private SoundPool sndPool;
		private HashMap<Integer, Integer> soundMap;
		//Sound Settings
		private float rate = 1.0f;
		private float masterVolume = 1.0f;
		private float leftVolume = 1.0f;
		private float rightVolume = 1.0f;
		private float balance = 0.5f;
		final static int POSSOUND = 1;
		final static int NEGSOUND = 2;
		//To make it global
		private static SoundManager instance;
		
		//Setting up our Sound Manager and storing app context
		private SoundManager(Context appContext){
			sndPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
			soundMap = new HashMap<Integer, Integer>();
			soundMap.put(POSSOUND, sndPool.load(appContext, R.raw.pos,1));
			soundMap.put(NEGSOUND, sndPool.load(appContext, R.raw.neg,1));
			pContext = appContext;
		}

		//play sound
		public void play(int sound_id, int repeats){
			sndPool.play(soundMap.get(sound_id), leftVolume, rightVolume, 1, repeats , rate);
		}
		
		//Creating the instance
		public static void CreateInstance(Context pContext){
			instance = new SoundManager(pContext);
		}
		
		//This will allow you to use the instance and play sound, etc
		public static SoundManager getInstance() {
			return instance;
		}
}
