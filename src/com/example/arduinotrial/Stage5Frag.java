package com.example.arduinotrial; 
  
import android.app.Activity; 
import android.content.Context; 
import android.content.Intent; 
import android.hardware.Camera; 
import android.os.Bundle; 
import android.support.v4.app.Fragment; 
import android.view.LayoutInflater; 
import android.view.SurfaceHolder; 
import android.view.SurfaceView; 
import android.view.View; 
import android.view.View.OnClickListener; 
import android.view.ViewGroup; 
import android.widget.Button; 
import android.widget.RelativeLayout; 
  
public class Stage5Frag extends Fragment implements SurfaceHolder.Callback { 
    public static SurfaceView mSurfaceView; 
    public static SurfaceHolder mSurfaceHolder; 
    public static Camera mCamera; 
    public static boolean mPreviewRunning; 
    public static View myFragmentView; 
    //OnArticleSelectedListener mListener; 
      
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
        com.example.arduinotrial.MyApplication.setStageActive("Stage5Frag"); 
        //Inflate the layout for this fragment 
        View myFragmentView = inflater.inflate(R.layout.stage_5, container, false); 
        mSurfaceView = (SurfaceView) myFragmentView.findViewById(R.id.surfaceView); 
        mSurfaceHolder = mSurfaceView.getHolder(); 
        mSurfaceHolder.addCallback(this); 
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
          
        //Set an onclick listener for stopvideo 
        Button btnStop = (Button) myFragmentView.findViewById(R.id.stopvideo); 
        btnStop.setOnClickListener(new OnClickListener(){ 
            public void onClick(View view){ 
                Intent intent = new Intent(getActivity(), RecorderService5.class); 
                Context ctx = (Context)Stage5Frag.this.getActivity(); 
                ctx.stopService(intent); 
                closeFragment();
            } 
        }); 
        return myFragmentView; 
    } 
      
    public void closeFragment(){ 
    	getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    	((MainActivity)getActivity()).showMainActivityFrag();
    } 
    public void OnAttach(Activity activity){ 
        super.onAttach(activity); 
    } 
      
    @Override
    public void onResume(){ 
        super.onResume(); 
        Intent intent = new Intent(getActivity(), RecorderService5.class); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        Context ctx = (Context)Stage5Frag.this.getActivity(); 
        ctx.startService(intent); 
          
        RelativeLayout mlayout = (RelativeLayout) getActivity().findViewById(R.id.stage5layoutinner); 
        mlayout.setVisibility(View.INVISIBLE); 
    } 
      
    /*public interface OnArticleSelectedListener { 
        public void onArticleSelected(Uri articleUri); 
    } */
      
    @Override
    public void surfaceCreated(SurfaceHolder holder) { 
          
    } 
  
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { 
    } 
      
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { 
        // TODO Auto-generated method stub 
  
    } 
      
    @Override
    public void onPause(){ 
        super.onPause(); 
    } 
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }
} 