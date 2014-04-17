package com.example.arduinotrial; 
  
import java.io.File; 
import java.io.IOException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
  
import android.app.Service; 
import android.content.Intent; 
import android.hardware.Camera; 
import android.media.CamcorderProfile; 
import android.media.MediaRecorder; 
import android.net.Uri; 
import android.os.Environment; 
import android.os.IBinder; 
import android.text.format.DateFormat; 
import android.util.Log; 
import android.view.SurfaceHolder; 
import android.view.SurfaceView; 
import android.widget.Toast; 
  
public class RecorderService5 extends Service { 
    private static final String TAG = "RecorderService5"; 
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
        mServiceCamera = Stage5Frag.mCamera; 
        mSurfaceView = Stage5Frag.mSurfaceView; 
        mSurfaceHolder = Stage5Frag.mSurfaceHolder; 
          
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
            //Camera.Parameters p = mServiceCamera.getParameters(); 
              
            //final List<Size> listSize = p.getSupportedPreviewSizes(); 
            //Size mPreviewSize = listSize.get(2); 
            //Log.v(TAG, "use: width = " + mPreviewSize.width  
                //      + " height = " + mPreviewSize.height); 
            //p.setPreviewSize(mPreviewSize.width, mPreviewSize.height); 
            //p.setPreviewFormat(PixelFormat.YCbCr_420_SP); 
            //mServiceCamera.setParameters(p); 
  
            try { 
                mServiceCamera.setPreviewDisplay(mSurfaceHolder); 
                mServiceCamera.startPreview(); 
            } 
            catch (IOException e) { 
                Log.e(TAG, e.getMessage()); 
                e.printStackTrace(); 
            } 
              
            mServiceCamera.unlock(); 
            //String mFileName =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/test.mp4"; 
            mMediaRecorder = new MediaRecorder(); 
            mMediaRecorder.setCamera(mServiceCamera); 
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
            mMediaRecorder.setProfile(CamcorderProfile.get(1,CamcorderProfile.QUALITY_HIGH)); 
            //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
            //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
            //mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT); 
            mMediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+ 
                    DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime())+ 
                    ".mp4"
            ); 
                    //getOutputMediaFileUri(MEDIA_TYPE_VIDEO).toString()); 
            //Log.d(TAG,getOutputMediaFileUri(MEDIA_TYPE_VIDEO).toString()); 
                    //Environment.getExternalStoragePublicDirectory( 
                    //Environment.DIRECTORY_PICTURES)+"/"+ 
                    //DateFormat.format("yyyy-MM-dd_kk-mm-ss", new Date().getTime())+ 
                    //".mp4"); 
                    // 
            //mMediaRecorder.setVideoFrameRate(30); 
            //mMediaRecorder.setVideoSize(mPreviewSize.width, mPreviewSize.height); 
            //mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface()); 
              
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
      
      
    /** Create a file Uri for saving an image or video */
      
    /** Create a file Uri for saving an image or video */
  /*private static Uri getOutputMediaFileUri(int type){ 
        return Uri.fromFile(getOutputMediaFile(type)); 
    } */
  
/** Create a File for saving an image or video */
/*private static File getOutputMediaFile(int type){ 
    // To be safe, you should check that the SDCard is mounted 
    // using Environment.getExternalStorageState() before doing this. 
  
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory( 
              Environment.DIRECTORY_PICTURES), "Recording"); 
    // This location works best if you want the created images to be shared 
    // between applications and persist after your app has been uninstalled. 
    Log.d(TAG,"We made the mediaStorageDir"); 
    if (mediaStorageDir.exists()){ 
        Log.d(TAG,"The directory exists"); 
  
    } 
    // Create the storage directory if it does not exist 
    if (! mediaStorageDir.exists()){ 
        Log.d(TAG,"directory does not exist"); 
        //mediaStorageDir.mkdirs(); 
        if (! mediaStorageDir.mkdirs()){ 
            Log.d(TAG, "failed to create directory"); 
            return null; 
        } 
    } 
  
    // Create a media file name 
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); 
    File mediaFile; 
    Log.v(TAG,"We've created the file"); 
    if (type == MEDIA_TYPE_IMAGE){ 
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + 
        "IMG_" + timeStamp + ".jpg"); 
    } else if(type == MEDIA_TYPE_VIDEO) { 
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + 
        "VID_" + timeStamp + ".mp4"); 
        Log.v(TAG,"We've created a video file"); 
    } else { 
         Log.d(TAG,"failed to make file"); 
        return null; 
    } 
    Log.d(TAG,"We're returning the file"); 
    return mediaFile; 
    }  */
} 