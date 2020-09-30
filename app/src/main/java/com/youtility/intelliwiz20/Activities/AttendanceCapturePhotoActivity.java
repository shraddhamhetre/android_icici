package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.BackgroundSoundService;
import com.youtility.intelliwiz20.Utils.CameraPreview;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class AttendanceCapturePhotoActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cpreview;
    private Boolean isSDPresent=false;
    String extStorageDirectory="";
    URI currImageURI=null;
    String imgeURI=null;
    File fileName=null;
    long timeSt;
    MyTimer countdown;
    private static int selectedCamera=-1;
    private String fromActivity=null;
    private String peopleScannedCode=null;
    private long attendanceTimestamp=-1;

    private SharedPreferences deviceRelatedPref;

    private SharedPreferences loginPref;
    private long peopleID=-1;
    private PeopleDAO peopleDAO;
    private TypeAssistDAO typeAssistDAO;

    private boolean safeToTakePicture = true;
    private SharedPreferences autoSyncPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_capture_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setVisibility(View.GONE);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        peopleID=loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);

        peopleDAO =new PeopleDAO(AttendanceCapturePhotoActivity.this);
        typeAssistDAO=new TypeAssistDAO(AttendanceCapturePhotoActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("auto click on fab");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        System.out.println("auto click on fab post delay");

                        camera.takePicture(null, null, mPicture);
                    }
                }, 50);
            }
        });

        fromActivity=getIntent().getStringExtra("FROM");
        peopleScannedCode=getIntent().getStringExtra("CODE");
        attendanceTimestamp=getIntent().getLongExtra("TIMESTAMP",-1);

        if(fromActivity.equalsIgnoreCase("MARK")) {
            selectedCamera = 1;
            fab.setVisibility(View.INVISIBLE);
            countdown = new MyTimer(1500,750);
            countdown.start();
        }
        else {
            selectedCamera = 0;
            fab.setVisibility(View.VISIBLE);
        }

        if(getIntent().hasExtra("CAMERA"))
        {
            selectedCamera=getIntent().getIntExtra("CAMERA",0);
        }

        if(checkcamera(this)){
            camera=getCameraInstance(AttendanceCapturePhotoActivity.this);

            cpreview=new CameraPreview(AttendanceCapturePhotoActivity.this, AttendanceCapturePhotoActivity.this, camera);
            FrameLayout preview=(FrameLayout) findViewById(R.id.camera_preview);

            preview.addView(cpreview);

        }



        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();

    }


    private boolean checkcamera(Context context){

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance(Context context){
        Camera c = null;
        try {

            int cameras= Camera.getNumberOfCameras();
            System.out.println("Cameras "+cameras);
	    	/*Toast.makeText(context, "cameras"+cameras, Toast.LENGTH_LONG).show();*/
            if(cameras > 1){
                c = Camera.open(selectedCamera); // attempt to get a Camera instance

	    	}else{
                Toast.makeText(context, context.getResources().getString(R.string.no_front_camera), Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public class MyTimer extends CountDownTimer
    {


        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);


        }
        @Override
        public void onTick(long l) {
            if(l==100)
            {
                if(fromActivity.equalsIgnoreCase("MARK")) {
                    Intent backgroudSoundService = new Intent(AttendanceCapturePhotoActivity.this, BackgroundSoundService.class);
                    stopService(backgroudSoundService);

                    System.out.println("background sound stop");
                }
            }
        }

        @Override
        public void onFinish() {
            System.out.println("safe to take picture before"+ safeToTakePicture);

            if (safeToTakePicture) {

                System.out.println("safe to take picture after"+ safeToTakePicture);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        System.out.println("clicked picture");
                        camera.takePicture(null, null, mPicture);

                        safeToTakePicture = false;
                    }
                }, 1000);

            /*Intent backgroudSoundService = new Intent(AttendanceCapturePhotoActivity.this, BackgroundSoundService.class);
            backgroudSoundService.putExtra("key",102);
            startService(backgroudSoundService);*/
            }
        }
    }



    private class PictureTakenAsyntask extends AsyncTask<Void, Integer, String>
    {
        Bitmap bitmap1;
        //ProgressDialog dialog;
        public PictureTakenAsyntask(Bitmap bitmap1)
        {
            this.bitmap1=bitmap1;
            //dialog = new ProgressDialog(SelfAttendanceActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog.setMessage(getResources().getString(R.string.please_wait));
            dialog.show();*/
        }

        @Override
        protected String doInBackground(Void... params) {
            final String imgNamePath=saveToSDCard(bitmap1);
            //final String imgNamePath=null;

            return imgNamePath;
        }

        @Override
        protected void onPostExecute(final String imgName) {
            //Toast.makeText(AttendanceCapturePhotoActivity.this,"Attendance Registered. \n Thank You.", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    releaseCamera(imgName);
                }
            }, 500);
        }
    }

    private PictureCallback mPicture=new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            byte[] image=bytes;
            if(image == null){
                safeToTakePicture = true;
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
            System.out.println("width:  " +bitmap.getWidth() + " height:  "+bitmap.getHeight());
            int rotation = getWindowManager().getDefaultDisplay()
                    .getRotation();

            try{
                System.out.println(" start try picturecallback==");

                if(rotation == Surface.ROTATION_0){
                    Matrix matrix = new Matrix();
                    if(selectedCamera==1)
                        matrix.postRotate(-90);
                    else if(selectedCamera==0)
                        matrix.postRotate(+90);

                    bitmap=Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);

                    if(fromActivity.equalsIgnoreCase("MARK")) {
                        Intent backgroudSoundService = new Intent(AttendanceCapturePhotoActivity.this, BackgroundSoundService.class);
                        backgroudSoundService.putExtra("key", 102);
                        startService(backgroudSoundService);

                        System.out.println("auto sound stopped");
                    }

                    PictureTakenAsyntask pictureTakenAsyntask=new PictureTakenAsyntask(bitmap);
                    pictureTakenAsyntask.execute();


               /*final String imgNamePath=saveToSDCard(bitmap);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        releaseCamera(imgNamePath);
                    }
                }, 500);*/

               System.out.println("end try picturecallback==");

                }
            }catch (RuntimeException e){
                System.out.println("catch==");
                e.printStackTrace();

            }

            safeToTakePicture = true;

        }
    };


    private void releaseCamera(String imgNPath)
    {
        if(camera!=null)
            camera.release();
        System.out.println("camera activity finish");

        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMG_PATH", imgNPath);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    private String saveToSDCard(Bitmap pics)
    {
        OutputStream outStream = null;

        if(isSDPresent)
        {
            /*UUID uuid=UUID.randomUUID();
            String randomString=uuid.toString();*/
            String randomString=peopleScannedCode+"_"+System.currentTimeMillis()+"_Attendance";
            String dirPath=extStorageDirectory+"/"+Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";

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
            String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

            //String imgeURI=null;
            File file = new File(dirPath+randomString+".PNG");
            try {
                outStream = new FileOutputStream(file);
                pics.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                imgeURI=dirPath+randomString+".png";

                System.out.println("imgeURI" +imgeURI);
                int fileSize=(int) file.length();
                Bitmap bitmapOrg1 = BitmapFactory.decodeFile(imgeURI);
                Bitmap immagex=bitmapOrg1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();

                //check with new database columns

                Attachment attachment=new Attachment();
                attachment.setAttachmentid(attendanceTimestamp);
                attachment.setFilePath(imgeURI);
                attachment.setFileName(randomString+".png");
                attachment.setNarration("ATTACHMENT");
                attachment.setGpslocation(gpsLocation);
                attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setCuser(peopleID);
                attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setMuser(peopleID);
                attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
                //attachment.setIsdeleted("False");
                attachment.setOwnerid(peopleDAO.getPeopleId(peopleScannedCode));
                attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_PEOPLEEVENTLOG, Constants.IDENTIFIER_OWNER));
                ///home/youtility/youtility2_avpt/ transaction/2018/jan/jobneed/task/5165430541616141/2018Jan214210.png
                attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+"peopleeventlog/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis()));
                //attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"peopleeventlog");
                attachment.setBuid(loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

                AttachmentDAO attachmentDAO=new AttachmentDAO(AttendanceCapturePhotoActivity.this);
                attachmentDAO.insertCommonRecord(attachment);

                Log.e("imaged stored"," "+ file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

        }
        return imgeURI;

    }


}
