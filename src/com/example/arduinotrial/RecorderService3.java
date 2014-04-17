package com.example.arduinotrial;

import java.io.IOException;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class RecorderService3 extends Service { 
    private static final String TAG = "RecorderService3"; 
    private SurfaceView mSurfaceView; 
    private SurfaceHolder mSurfaceHolder; 
    private static Camera mServiceCamera; 
    private boolean mRecordingStatus; 
    private MediaRecorder mMediaRecorder; 
    public static final int MEDIA_TYPE_IMAGE = 1; 
    public static final int MEDIA_TYPE_VIDEO = 2; 
      
    @Override
    public void onCreate() { 
        mRecordingStatus = false; 
        //String fragmentOpen = com.example.arduinotrial.MyApplication.getStageActive(); 
        //Class myClass = Class.forName(fragmentOpen); 
        mServiceCamera = Stage3Frag.mCamera; 
        mSurfaceView = Stage3Frag.mSurfaceView; 
        mSurfaceHolder = Stage3Frag.mSurfaceHolder; 
          
        super.onCreate(); 
    } 
  
    @Override
    public IBinder onBind(Intent intent) { 
        // TODO Auto-generated method stub 
        return null; 
    } 
      
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { 
        super.onStartCommand(intent, flags, startId); 
        if (mRecordingStatus == false) 
            startRecording(); 
  
        return START_STICKY; 
    } 
  
    @Override
    public void onDestroy() { 
        stopRecording(); 
        mRecordingStatus = false; 
          
        super.onDestroy(); 
    }    
  
    public boolean startRecording(){ 
        try { 
            Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_SHORT).show(); 
              
            mServiceCamera = Camera.open(1); 
            Camera.Parameters params = mServiceCamera.getParameters(); 
            mServiceCamera.setParameters(params); 
  
            try { 
                mServiceCamera.setPreviewDisplay(mSurfaceHolder); 
                mServiceCamera.startPreview(); 
            } 
            catch (IOException e) { 
                Log.e(TAG, e.getMessage()); 
                e.printStackTrace(); 
            } 
              
            mServiceCamera.unlock(); 
            mMediaRecorder = new MediaRecorder(); 
            mMediaRecorder.setCamera(mServiceCamera); 
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
            mMediaRecorder.setProfile(CamcorderProfile.get(1,CamcorderProfile.QUALITY_HIGH)); 
            mMediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+ 
                    DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime())+ 
                    ".mp4"
            ); 
              
            mMediaRecorder.prepare(); 
            mMediaRecorder.start();  
  
            mRecordingStatus = true; 
              
            return true; 
        } catch (IllegalStateException e) { 
            Log.d(TAG, e.getMessage()); 
            e.printStackTrace(); 
            return false; 
        } catch (IOException e) { 
            Log.d(TAG, e.getMessage()); 
            e.printStackTrace(); 
            return false; 
        } 
    } 
  
    public void stopRecording() { 
        Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show(); 
        try { 
            mServiceCamera.reconnect(); 
        } catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
        mMediaRecorder.stop(); 
        mMediaRecorder.reset(); 
          
        mMediaRecorder.release(); 
        mServiceCamera.release(); 
          
        //mServiceCamera.release(); 
        //mServiceCamera = null; 
    } 
} 