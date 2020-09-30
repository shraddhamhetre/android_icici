package com.youtility.intelliwiz20.Utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    private Activity activity;
    public CameraPreview(Context context, Activity activity, Camera camera) {
        super(context);
        mCamera = camera;
        this.activity=activity;
        this.context=context;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    int getFrontCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();
        for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
        }
        return -1; // No front-facing camera found
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	
        	/*SharedPreferences cam_pref=context.getSharedPreferences("Orientation", context.MODE_PRIVATE);
        	System.out.println("Orientation value "+cam_pref.getBoolean("mode", false));
        	if(cam_pref.getBoolean("mode", false)){
        		
        	}else{
        		mCamera.setDisplayOrientation(90);
        	}*/

            //mCamera.setDisplayOrientation(90);

            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            //android.hardware.Camera.getCameraInfo(getFrontCameraId(), info);

            //System.out.println("Rotation: ----------------------------------------------------"+getCorrectCameraOrientation(info, mCamera));

            mCamera.setDisplayOrientation((getCorrectCameraOrientation(info, mCamera)));


            mCamera.setPreviewDisplay(holder);
            
            
            Camera.Parameters params = mCamera.getParameters();


		     // Check what resolutions are supported by your camera
		     List<Size> sizes = params.getSupportedPictureSizes();
            Camera.Size size = sizes.get(sizes.size()-1);
            params.setPictureSize(size.width, size.height);

		     // Iterate through all available resolutions and choose one.
		     // The chosen resolution will be stored in mSize.
		     Size mSize=null;

		     Camera.Size camsz=getlowestreso(sizes);
		    //params.setPictureSize(camsz.width, camsz.height);

            if (params.getSupportedFocusModes().contains(
                    Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            else
            {
                System.out.println("Auto focus not available");
            }

		    /*if(context.getPackageManager().hasSystemFeature(Parameters.FOCUS_MODE_AUTO)) {
                //params.setFocusMode("android.hardware.camera.autofocus");
                System.out.println("Auto focus mode available");
                params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            }*/

		  long  timeSt= Calendar.getInstance().getTimeInMillis();
		  params.setGpsTimestamp(timeSt);

		 /* Location location = null;

			if (spsApp.appLocation == null) {
				LocationManager locationmanager = (LocationManager) context.getApplicationContext()
						.getSystemService(context.LOCATION_SERVICE);
				location = locationmanager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


			} else {
				location = spsApp.appLocation;
				System.out.println("Gps location dsffg: "+ location.getLatitude());
			}

			if(location != null){
				Util.latitude=location.getLatitude();
				Util.longitude=location.getLongitude();
				System.out.println("latitude******************** "+location.getLatitude());

				params.setGpsLatitude(Util.latitude);
				params.setGpsLongitude(Util.longitude);
			}
		  */
		     mCamera.setParameters(params);


            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("TAG", "Error setting camera preview: " + e.getMessage());
        }
    }

    public int getCorrectCameraOrientation(Camera.CameraInfo info, Camera camera) {

        int rotation = 0;
        rotation=activity.getWindowManager().getDefaultDisplay().getRotation();
        System.out.println("rotation in camerapreview: -------------------------------------------------"+rotation);
        int degrees = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                degrees = -90;
                break;

            case Surface.ROTATION_90:
                degrees = 0;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = -180;
                break;

        }

        int result=0;
        System.out.println("info.facing: "+info.facing);
        if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }else{

            System.out.println("info.orientation: "+info.orientation);
            System.out.println("info.orientation1: "+(info.orientation - degrees + 360));
            result = (info.orientation - degrees + 360) % 360;
        }

        System.out.println("Result: -------------------------------------------------"+result);
        return result;
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("TAG", "Error starting camera preview: " + e.getMessage());
        }
    }
    public static boolean wantToUseThisResolution(Size size){

		if(size.width == 1280 && size.height == 720)
			return true;
		else

		return false;

	}
public Camera.Size getlowestreso(List<Size> size){

    	Camera.Size lowerSize=size.get(0);
    	for(int i=0 ; i< size.size() ; i++){
    	
    		System.out.println("  width "+lowerSize.width);
    		if(lowerSize.width >= size.get(i).width){
    			lowerSize =size.get(i);
    			System.out.println(" lower width "+lowerSize.width +" lower height " + lowerSize.height);
    			if(lowerSize.width <= 320)
    				break;
                    
    		}

    	}
    	return lowerSize;

        /*Camera.Size lowerSize =null;

        System.out.println("Camera size: "+size.size());

        for(int i=0;i<size.size();i++)
        {
            System.out.println("Camera resol: "+size.get(i).height+" * "+size.get(i).width);
        }

        if(size.size() >10)
            lowerSize = size.get((size.size())/5);
        else if(size.size()>7 && size.size()<10)
            lowerSize=size.get((size.size())/4);
        else if(size.size()>5 && size.size()<7)
            lowerSize=size.get((size.size())/3);
        else
            lowerSize = size.get(0);

        //lowerSize = size.get(0);
    	
        System.out.println("Selected resolu: "+lowerSize.height+" * "+lowerSize.width);
		return lowerSize;*/
    	
    }
}