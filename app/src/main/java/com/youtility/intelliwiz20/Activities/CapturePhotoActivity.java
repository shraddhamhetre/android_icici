package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CameraPreview;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class CapturePhotoActivity extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners {

    private Boolean isFabOpen = false;
    private FloatingActionButton fabMain,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private int fromActivity=-1;
    private String peopleScannedCode=" ";
    private long attachmentTimestamp=-1;
    private Boolean isSDPresent=false;
    private String extStorageDirectory="";
    private Camera camera;
    private CameraPreview cpreview;
    private String imgeURI=null;
    private FloatingActionButton fab;
    private boolean isFlashOn=false;
    private boolean isFrontCam=false;
    private int cam_number=0;
    private FrameLayout preview;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences loginDetailPref;
    private long jobneedid=-1;
    private TypeAssistDAO typeAssistDAO;

    private String parentActivity=null;
    private String parentFolder=null;

    private SharedPreferences autoSyncPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parentActivity=getIntent().getStringExtra("PARENT_ACTIVITY");
        parentFolder=getIntent().getStringExtra("FOLDER");

        if(getIntent().hasExtra("JOBNEEDID"))
        {
            jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);
            System.out.println("jobneedid::"+jobneedid);
        }

        if(getIntent().hasExtra("CAMERA"))
        {
            cam_number=getIntent().getIntExtra("CAMERA",0);
        }



        fromActivity=getIntent().getIntExtra("FROM",0);
        attachmentTimestamp=getIntent().getLongExtra("TIMESTAMP",-1);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,Context.MODE_PRIVATE);
        typeAssistDAO=new TypeAssistDAO(CapturePhotoActivity.this);

        autoSyncPref=getSharedPreferences(Constants.AUTO_SYNC_PREF, MODE_PRIVATE);


        customAlertDialog=new CustomAlertDialog(CapturePhotoActivity.this,this);

        fabMain = (FloatingActionButton)findViewById(R.id.fabMain);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabMain.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                camera.takePicture(null, null, mPicture);
                fab.setEnabled(false);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        if(cam_number==-1)
            cam_number=0;

        if(checkcamera(this)){
            camera=getCameraInstance(CapturePhotoActivity.this, isFlashOn, cam_number);

            cpreview=new CameraPreview(CapturePhotoActivity.this, CapturePhotoActivity.this, camera);
            preview=(FrameLayout) findViewById(R.id.camera_preview);

            preview.addView(cpreview);

        }

        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(CapturePhotoActivity.this)
                .setTitle("Alert")
                .setMessage("Do you want to exit?").setCancelable(false)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Continue with delete operation
                        if(camera!=null) {
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                        setResult(RESULT_CANCELED);
                        finish();

                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        //super.onBackPressed();

    }

    private boolean checkcamera(Context context){

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance(Context context,boolean val, int cam){
        Camera c = null;
        try {

            int cameras= Camera.getNumberOfCameras();
            System.out.println("Cameras "+cameras);
	    	/*Toast.makeText(context, "cameras"+cameras, Toast.LENGTH_LONG).show();*/
            if(cameras > 1){
                if(val)
                {
                    c = Camera.open(cam); // attempt to get a Camera instance
                    Camera.Parameters parameters = c.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    c.setParameters(parameters);
                }
                else
                {
                    c = Camera.open(cam); // attempt to get a Camera instance
                    Camera.Parameters parameters = c.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    c.setParameters(parameters);
                }


            }else{
                Toast.makeText(context, context.getResources().getString(R.string.no_front_camera), Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fabMain:
                    animateFAB();
                    break;
                case R.id.fab1:

                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                        if (!isFlashOn) {
                            isFlashOn = true;
                            fab1.setImageResource(R.drawable.ic_flash_off_white_24dp);
                            camera = getCameraInstance(CapturePhotoActivity.this, isFlashOn, cam_number);
                            if (preview != null) {
                                preview.removeAllViewsInLayout();

                            }
                            cpreview = new CameraPreview(CapturePhotoActivity.this, CapturePhotoActivity.this, camera);
                            preview.addView(cpreview);
                        } else {
                            isFlashOn = false;
                            fab1.setImageResource(R.drawable.ic_flash_on_white_24dp);
                            camera = getCameraInstance(CapturePhotoActivity.this, isFlashOn, cam_number);
                            if (preview != null) {
                                preview.removeAllViewsInLayout();

                            }
                            cpreview = new CameraPreview(CapturePhotoActivity.this, CapturePhotoActivity.this, camera);
                            preview.addView(cpreview);
                        }
                    break;
                case R.id.fab2:

                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                        if (!isFrontCam) {
                            isFrontCam = true;
                            cam_number = 1;
                            camera = getCameraInstance(CapturePhotoActivity.this, isFlashOn, cam_number);
                            if (preview != null) {
                                preview.removeAllViewsInLayout();

                            }
                            cpreview = new CameraPreview(CapturePhotoActivity.this, CapturePhotoActivity.this, camera);
                            preview.addView(cpreview);
                        } else {
                            isFrontCam = false;
                            cam_number = 0;
                            camera = getCameraInstance(CapturePhotoActivity.this, isFlashOn, cam_number);
                            if (preview != null) {
                                preview.removeAllViewsInLayout();

                            }
                            cpreview = new CameraPreview(CapturePhotoActivity.this, CapturePhotoActivity.this, camera);
                            preview.addView(cpreview);
                        }
                    break;
            }
    }

    public void animateFAB(){

        if(isFabOpen){

            fabMain.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fabMain.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }

    private Camera.PictureCallback mPicture=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
/*
            if (!autoSyncPref.getBoolean(Constants.IS_AUTO_SYNC_RUNNING,true)) {
*/
                byte[] image = bytes;

                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                System.out.println("Capture Image size: width:  " + bitmap.getWidth() + ", height:  " + bitmap.getHeight());
                int rotation = getWindowManager().getDefaultDisplay().getRotation();

                System.out.println("Rotation in capturephotoactivity: " + rotation);

            /*bitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

            System.out.println("Capture Image with matrix size: width:  " +bitmap.getWidth() + ", height:  "+bitmap.getHeight());

            String imgNamePath=saveToSDCard(bitmap);

            System.out.println("camera activity finish");
            customAlertDialog.showYesNoAlertBox("Do you want to add photo annotation?",imgNamePath,0);*/
                Matrix matrix = new Matrix();
                if (rotation == Surface.ROTATION_0) {
                    matrix.postRotate(getRotationDegree(rotation));
                } else if (rotation == Surface.ROTATION_90) {
                    matrix.postRotate(getRotationDegree(rotation));
                } else if (rotation == Surface.ROTATION_180) {
                    matrix.postRotate(getRotationDegree(rotation));
                } else if (rotation == Surface.ROTATION_270) {
                    matrix.postRotate(getRotationDegree(rotation));
                }
                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                //bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                System.out.println("Capture Image with matrix size: width:  " + scaled.getWidth() + ", height:  " + scaled.getHeight());

                PictureTakenAsyntask pictureTakenAsyntask = new PictureTakenAsyntask(scaled);
                pictureTakenAsyntask.execute();

            /*String imgNamePath=saveToSDCard(bitmap);
            System.out.println("camera activity finish");
            customAlertDialog.showYesNoAlertBox("Do you want to add photo annotation?",imgNamePath,0);*/
            /*} else {
            customAlertDialog.commonDialog1("Alert","Please wait... Synchronization in progress");
        }*/
        }
    };

    private class PictureTakenAsyntask extends AsyncTask<Void, Integer, Void>
    {
        Bitmap bitmap1;
        String imgNamePath;
        //ProgressDialog dialog;
        public PictureTakenAsyntask(Bitmap bitmap1)
        {
            this.bitmap1=bitmap1;
            //dialog = new ProgressDialog(CapturePhotoActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog.setMessage(getResources().getString(R.string.please_wait));
            dialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            imgNamePath=saveToSDCard(bitmap1);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            goBackToActivity(imgNamePath);

            ///super.onPostExecute(aVoid);
            /*if(dialog!=null && dialog.isShowing())
                dialog.dismiss();*/

            //customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.alerttitle),getResources().getString(R.string.photoannotation_title),imgNamePath,0);
        }
    }




    private int getRotationDegree(int rotation)
    {
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

        int result;
        if(cam_number==1)
        {
            /*result = (rotation + degrees) % 360;
            result = (360 - result) % 360;*/
            if(rotation==0)
                result=-90;
            else
                result=0;
        }
        else {
            result = (rotation - degrees + 360) % 360;
        }

        System.out.println("Result: -------------------------------------------------"+result);
        return result;
    }

    private String saveToSDCard(Bitmap pics)
    {
        OutputStream outStream = null;

        if(isSDPresent)
        {
            /*UUID uuid=UUID.randomUUID();
            String randomString=uuid.toString();*/
            String randomString= CommonFunctions.getFileNameFromDate(System.currentTimeMillis());
            String dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";
            System.out.println("dirPath"+dirPath);

            try {
                if(CommonFunctions.checkFileExists(dirPath))
                {
                    System.out.println("Directory already exits created");
                }
                else
                {
                    File dir = new File(dirPath);
                    dir.mkdirs();
                    System.out.println("Directory created");
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            //String imgeURI=null;
            File file = new File(dirPath+randomString+".png");
            try {
                outStream = new FileOutputStream(file);
                pics.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                System.out.println("Capture Image compress: width:  " +pics.getWidth() + ", height:  "+pics.getHeight());

                imgeURI=dirPath+randomString+".png";

                System.out.println("imgeURI" +imgeURI + "serverpath: "+Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+parentActivity.toLowerCase(Locale.ENGLISH)+"/"+parentFolder.toLowerCase(Locale.ENGLISH)+"/");
                int fileSize=(int) file.length();
                Bitmap bitmapOrg1 = BitmapFactory.decodeFile(imgeURI);
                System.out.println("Capture Image decode file size: width:  " +bitmapOrg1.getWidth() + ", height:  "+bitmapOrg1.getHeight());

                Bitmap immagex=bitmapOrg1;
                System.out.println("Capture Image imageex size: width:  " +immagex.getWidth() + ", height:  "+immagex.getHeight());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
                System.out.println("Capture Image immageex compress size: width:  " +immagex.getWidth() + ", height:  "+immagex.getHeight());
                byte[] b = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();

                String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

                //check with new database columns

                Attachment attachment=new Attachment();
                attachment.setAttachmentid(attachmentTimestamp);
                attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
                attachment.setFilePath(imgeURI);
                attachment.setFileName(randomString+".png");
                attachment.setNarration(peopleScannedCode);
                attachment.setGpslocation(gpsLocation);
                attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setOwnername(typeAssistDAO.getEventTypeID(parentActivity));//need to pass table name according to
                attachment.setOwnerid(jobneedid);//need to pass jobneedid/peopleeventlogid
                ///home/youtility/youtility2_avpt/ transaction/2018/jan/jobneed/task/5165430541616141/2018Jan214210.png
                //old path
                //attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+parentActivity+"/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"/");
                //new path
                attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+parentActivity.toLowerCase(Locale.ENGLISH)+"/"+parentFolder.toLowerCase(Locale.ENGLISH)+"/");
                //attachment.setIsdeleted("False");
                attachment.setAttachmentCategory(Constants.ATTACHMENT_PICTURE);
                attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

                AttachmentDAO attachmentDAO=new AttachmentDAO(CapturePhotoActivity.this);
                attachmentDAO.insertCommonRecord(attachment);

                Log.e("image stored"," "+ file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }


        }
        return imgeURI;

    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        goBackToActivity(errorMessage);
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        System.out.println("Dont do annotation:==");

        /*Intent nxtActivity=new Intent(CapturePhotoActivity.this, PhotoAnnotationActivity.class);
        nxtActivity.putExtra("PATH",errorMessage);
        startActivityForResult(nxtActivity,0);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null) {
                    goBackToActivity(data.getStringExtra("PATH"));
                }
            }
            else if(resultCode==RESULT_CANCELED)
            {
                setResult(RESULT_CANCELED);
                if(camera!=null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
                finish();

            }
        }
    }

    private void goBackToActivity(String imgPath)
    {
        if(camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMG_PATH", imgPath);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
