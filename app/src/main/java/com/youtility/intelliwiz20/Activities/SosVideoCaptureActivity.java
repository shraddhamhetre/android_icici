package com.youtility.intelliwiz20.Activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Address;
import android.location.Geocoder;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.AsyncTask.UploadAttachmentLogAsyncTask;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadAttachmentLogDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadPELogDataListener;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import com.youtility.intelliwiz20.Utils.CheckNetwork;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static android.view.SurfaceHolder.Callback;
import static android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS;

public class SosVideoCaptureActivity extends AppCompatActivity implements View.OnClickListener, IUploadPELogDataListener, IUploadAttachmentLogDataListener, Callback, MediaRecorder.OnInfoListener/*, IDialogEventListeners*/ {
    private int fromActivity=-1;
    private String peopleScannedCode=null;
    private long attachmentTimestamp=-1;

    private PeopleEventLogDAO peopleEventLogDAO;
    private AttachmentDAO attachmentDAO;

    private long startTime = 0L;
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;

    private Button startBtn;
    private Button listVideoBtn;
    private TextView countdownText;

    private Handler myHandler = new Handler();
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public MediaRecorder mrec ;
    private Camera mCamera;
    private String extStorageDirectory = "";
    private boolean isSDPresent;
    public static int rotate;
    private String fileName=null;
    private String dirPath=null;
    private long jobneedid=-1;
    private SharedPreferences deviceRelatedPref;

    private PeopleEventLog peopleEventLog;
    private CheckNetwork checkNetwork;



    private SharedPreferences loginrelatedPref;

    private String parentActivity=null;
    private String parentFolder=null;
    private SharedPreferences loginDetailPref;
    private TypeAssistDAO typeAssistDAO;
    private SharedPreferences sharedPreferences;
    private CustomAlertDialog customAlertDialog;
    private String tempSendingMsg=null;
    private int fallDownCount=0;
    static long returnid= 0;
    static long currentTimestamp= 0;



    public static void uploadAttendanceAttachment1(long retId, long cTimestamp) {
        returnid = retId;
        currentTimestamp = cTimestamp;

            System.out.println("return id before"+ returnid);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_sos_capture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().hasExtra("JOBNEEDID"))
        {
            jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);
        }


        //customAlertDialog=new CustomAlertDialog(SosVideoCaptureActivity.this, this);

        parentActivity=getIntent().getStringExtra("PARENT_ACTIVITY");
        parentFolder=getIntent().getStringExtra("FOLDER");

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SosVideoCaptureActivity.this);

        typeAssistDAO=new TypeAssistDAO(SosVideoCaptureActivity.this);

        peopleEventLogDAO=new PeopleEventLogDAO(SosVideoCaptureActivity.this);
        attachmentDAO=new AttachmentDAO(SosVideoCaptureActivity.this);

        fromActivity=getIntent().getIntExtra("FROM",0);
        peopleScannedCode=getIntent().getStringExtra("CODE");
        attachmentTimestamp=getIntent().getLongExtra("TIMESTAMP",-1);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        loginrelatedPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);

        checkNetwork=new CheckNetwork(SosVideoCaptureActivity.this);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //startBtn=(Button)findViewById(R.id.startvideocapture);
        listVideoBtn=(Button)findViewById(R.id.stopvideocapture);



        //startBtn.setOnClickListener(this);
        listVideoBtn.setOnClickListener(this);

        countdownText=(TextView)findViewById(R.id.countdownTextview);

        //initMediaRecorder();
        surfaceView = (SurfaceView)findViewById(R.id.videocaptureview);
        mCamera = Camera.open();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SURFACE_TYPE_PUSH_BUFFERS);

        extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        isSDPresent = android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        System.out.println("return id after"+ returnid);

        //startVideoAsyntask startVideoAsyntask=new startVideoAsyntask();
        //startVideoAsyntask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }




    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.startvideocapture:
                startRecording();
                break;
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus)
        {
            // play video call
            startRecording();

        }
    }



    private Runnable updateTimerMethod = new Runnable()
    {

        @Override
        public void run()
        {
            timeInMillies = SystemClock.uptimeMillis()-startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            countdownText.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));
            myHandler.postDelayed(this, 0);
        }

    };

    private void startRecording()
    {
        int interval= 0;
        //startBtn.setClickable(false);

        if(mCamera==null)
            mCamera = Camera.open();
        if(isSDPresent)
        {
            fileName= CommonFunctions.getFileNameFromDate(System.currentTimeMillis())+".mp4";
            dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";

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

            File file=new File(dirPath,fileName);

            Parameters params = mCamera.getParameters();
            if(SosVideoCaptureActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);

            if (params.getSupportedFocusModes().contains(
                    Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            else
            {
                System.out.println("Auto focus not available");
            }

            mCamera.setParameters(params);

            mrec = new MediaRecorder();

            mCamera.lock();
            mCamera.unlock();

            mrec.setCamera(mCamera);

            mrec.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mrec.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            mrec.setProfile(camcorderProfile_HQ);

            //int videoInterval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ,"10000"));
            int videoInterval=loginDetailPref.getInt(Constants.pvideolength,10);

            if(videoInterval == 0){
                videoInterval = 5;
                interval=videoInterval*1000;

            }else {
                interval=videoInterval*1000;

            }


            //interval=videoInterval*1000;
            System.out.println("interval "+ interval);
            mrec.setMaxDuration(interval);
            mrec.setOnInfoListener(this);

            mrec.setVideoFrameRate(30);
            mrec.setPreviewDisplay(surfaceHolder.getSurface());
            mrec.setOrientationHint(90);
            mrec.setOutputFile(dirPath+fileName);
            try {

                mrec.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mrec.start();

            startTime = SystemClock.uptimeMillis();
            myHandler.postDelayed(updateTimerMethod, 0);
        }


    }

    private void saveToSDCard()
    {
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        //check with new database columns

        Attachment attachment=new Attachment();
        attachment.setAttachmentid(attachmentTimestamp);
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
        attachment.setFilePath(dirPath+fileName);
        attachment.setFileName(fileName);
        attachment.setNarration(peopleScannedCode);
        attachment.setGpslocation(gpsLocation);
        attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setOwnername(typeAssistDAO.getEventTypeID(parentActivity, Constants.IDENTIFIER_OWNER));//need to pass table name according to
        attachment.setOwnerid(jobneedid);//need to pass jobneedid/peopleeventlogid
        //attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+parentActivity+"/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"/");
        attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+parentActivity.toLowerCase(Locale.ENGLISH)+"/"+parentFolder.toLowerCase(Locale.ENGLISH)+"/");
        //attachment.setIsdeleted("False");
        attachment.setAttachmentCategory(Constants.ATTACHMENT_VIDEO);
        attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

        AttachmentDAO attachmentDAO=new AttachmentDAO(SosVideoCaptureActivity.this);

        System.out.println("Attachment Inserted owner id: "+jobneedid);
        attachmentDAO.insertCommonRecord(attachment);

        String emailID= sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_EMAILID,"");
        String pfilepath=  dirPath + fileName;
        String pfileName=  fileName;


        System.out.println("filepath"+dirPath+fileName);
        System.out.println("filename"+fileName);

        prepareSendingMessage();

        EmailAsyncTask emailAsyncTask = new EmailAsyncTask(emailID, pfilepath, pfileName);
        emailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //uploadSOSPELog(0, returnid);


    }

    @Override
    public void uploadSOSPELog(int status, long returnId) {

        System.out.println("====returnId "+returnId);
        if(status==0 && returnId!=-1)
        {
            //peopleEventLogDAO.changeSOSSyncStatus(-1,returnid,peopleEventLog.getDatetime());
            attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnid),String.valueOf(currentTimestamp));
            uploadAttendanceAttachment(returnid);
        }


    }

    private void uploadAttendanceAttachment(long retId)
    {
        if(checkNetwork.isNetworkConnectionAvailable())
        {
            UploadAttachmentLogAsyncTask uploadAttachmentLogAsyncTask=new UploadAttachmentLogAsyncTask(SosVideoCaptureActivity.this, retId, currentTimestamp,this);
            uploadAttachmentLogAsyncTask.execute();
        }
    }

    @Override
    public void uploadAttachmentLog(int status, long returnId) {

    }


    public void prepareSendingMessage()
    {

        Double latCor=0.0, lonCor=0.0;
        List<Address> addresses;
        String message;
        StringBuilder stringBuilder;
        String mobileno=loginDetailPref.getString(Constants.mobileno,"");

        String name= sharedPreferences.getString(Constants.SETTING_GENERAL_LOGIN_USER_NAME,"");

        //String helpMessage= sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_HELP_MESSAGE,"I need help") + "! Mobile No: " +mobileno +". ";
        String helpMessage= "I need help" + "! Mobile No: " +mobileno +". ";

        System.out.println("helpMessage: "+helpMessage);

        message=getResources().getString(R.string.sos_help_message, name , helpMessage);

        latCor=Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        lonCor=Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
        if(latCor==0.0 && lonCor==0.0)
        {
            stringBuilder = new StringBuilder(message);
            tempSendingMsg=stringBuilder.toString().trim();
        }
        else
        {
            try {
                Geocoder geoCoder=new Geocoder(SosVideoCaptureActivity.this, Locale.ENGLISH);
                addresses=geoCoder.getFromLocation(latCor, lonCor, 1);
                StringBuilder builder = new StringBuilder(message);
                builder.append(getResources().getString(R.string.sos_my_location,roundTwoDecimals(latCor), roundTwoDecimals(lonCor)));
                builder.append("\n");
                if(geoCoder.isPresent())
                {
                    Address returnAddress = addresses.get(0);
                    stringBuilder=new StringBuilder();
                    stringBuilder.append(returnAddress.getAddressLine(0)+",");
                    stringBuilder.append(returnAddress.getLocality() + ",");
                    stringBuilder.append(returnAddress.getCountryName() + ",");
                    stringBuilder.append(returnAddress.getPostalCode() + ".");
                    builder.append("\n"+stringBuilder.toString());
                }
                System.out.println("Sending message: "+builder.toString());
                tempSendingMsg=builder.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    private String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return String.valueOf(Double.valueOf(twoDForm.format(d)));
    }

    public class EmailAsyncTask extends AsyncTask<Void, Void, Void>
    {
        String emailID, pfilepath, pfileName;
        public EmailAsyncTask(String emailID, String pfilepath, String pfileName)
        {
            this.emailID= emailID;
            this.pfilepath= pfilepath;
            this.pfileName= pfileName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(checkGoogleConnection())
            {
                try {
                    //lets now create and send the mail
                    /*final String username = "info@youtility.in";
                    final String password = "77jabra&&77";*/
                    /*final String username = "support@youtility.in";
                    final String password = "supportanjali2017";*/

                    final String username = "AKIA4QJPZVEWENJJUF7R";
                    final String password = "BKIkrUK5C1tx2mj/lViWVHKyjoTqfsbO3bEc+IE3Z1Jg";

                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
                    props.put("mail.smtp.port", "587");

                    Session session = Session.getInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(username, password);
                                }
                            });

                    MimeMessage message = new MimeMessage(session);
                    message.addHeader("Content-Type", "text/html; charset=utf-8");

                    String recipient = emailID;
                    if(emailID.contains(","))
                    {
                        String[] recipientList = recipient.split(",");
                        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
                        int counter = 0;
                        for (String recipients : recipientList) {
                            recipientAddress[counter] = new InternetAddress(recipients.trim());
                            counter++;
                        }
                        message.setRecipients(Message.RecipientType.TO, recipientAddress);
                    }
                    else
                    {
                        //message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress("shraddha.mhetre@youtility.in "));
                        message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress(emailID));
                    }


                    //message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress(emailID));
                    Log.d("EmailDataFilesAsyncTask"," got message recipients as "+ InternetAddress.toString(message.getAllRecipients()));

                    message.setFrom(new InternetAddress("support@youtility.in"));
                    message.setSubject("SOS emergency mail");

                    BodyPart messageBodyPart = new MimeBodyPart();

                    String body="";
                    String tdstyle= "style='background:#ABE7ED;font-weight:bold;font-size:14px;'";
                    body+= "<table style='background:#EEF1F5;' cellpadding=8 cellspacing=2>";
                    body+= "<tr> <td align='right' "+tdstyle+">Message: </td><td>"+tempSendingMsg+"</td></tr>";
                    body+="</table>";

                    // Now set the actual message
                    messageBodyPart.setContent(body, "text/html;charset=UTF-8");

                    // Create a multipar message
                    Multipart multipart = new MimeMultipart();

                    // Set text message part
                    multipart.addBodyPart(messageBodyPart);

                    // Part two is attachment
                    messageBodyPart = new MimeBodyPart();
                    String file = pfilepath;
                    String filename = pfileName;
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);

                    // Send the complete message parts
                    message.setContent(multipart);

 /*                   Multipart multipart = new MimeMultipart();
                    String body="";

                    String tdstyle= "style='background:#ABE7ED;font-weight:bold;font-size:14px;'";


                    body+= "<table style='background:#EEF1F5;' cellpadding=8 cellspacing=2>";
                    body+= "<tr> <td align='right' "+tdstyle+">Message: </td><td>"+tempSendingMsg+"</td></tr>";
                    body+="</table>";

                    MimeBodyPart htmlTextPart = new MimeBodyPart();
                    //htmlTextPart.setContent(body, "text/html;charset=UTF-8");
                    htmlTextPart.setText("panic");

                    String file = pfilepath;
                    String fileName = pfileName;
                    DataSource source = new FileDataSource(file);
                    htmlTextPart.setDataHandler(new DataHandler(source));
                    htmlTextPart.setFileName(fileName);
                    multipart.addBodyPart(htmlTextPart);

                    message.setContent(multipart);*/

                    Transport.send(message);

                    /*MimeBodyPart b = new MimeBodyPart();
                    b.setDisposition(MimeBodyPart.INLINE);
                    b.setText(tempSendingMsg);
                    //ByteArrayDataSource ds1 = new ByteArrayDataSource(mailbody.getBytes(Charset.forName("utf-8")), "text/html; charset=utf-8");
                    //b.setDataHandler(new DataHandler(ds1));
                    multipart.addBodyPart(b);

                    message.setContent(multipart);
                    Transport.send(message);*/
                    Log.d("EmailDataFilesAsyncTask", "message emailed");
                    //}
                } catch (AddressException e) {
                    Log.d("EmailDataFilesAsyncTask"," Exception in sending data zip email"+e);
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            fallDownCount=0;
            uploadSOSPELog(0, returnid);

            //sosText.setText(getResources().getString(R.string.sos_sent));
        }

        private boolean checkGoogleConnection() {
            boolean ret=false;
            String baseUrl = "https://google.com";
            try {
                URL url = new URL(baseUrl);
                ret=checkConnection(url);
            } catch (MalformedURLException e) {
                Log.e("EmailDataFilesAsyncTask","checkGoogleConnection() got Malformed URL baseUrl="+baseUrl,e);
                e.printStackTrace();
            }

            return ret;
        }

        private boolean checkConnection(URL url) {
            boolean ret=false;
            try {
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                System.setProperty("http.keepAlive", "false");
                urlc.setConnectTimeout(20*1000);//20 seconds timeout for connect attempt      // 10 s.
                urlc.setReadTimeout(20*1000);//20 seconds timeout for read attempt
                urlc.connect();
                int respcode=urlc.getResponseCode();
                Log.e("EmailDataFilesAsyncTask","checkConnection() got urlc.getResponseCode()="+respcode);
                if (respcode == 200 || respcode == 202) ret=true;
                else ret=false;
            } catch (IOException e) {
                Log.e("EmailDataFilesAsyncTask","checkConnection() got Exception for url="+url,e);
                e.printStackTrace();
            }
            return ret;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        prepareMediaRecorder(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera(mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera!=null)
            mCamera.release();
    }

    private void prepareMediaRecorder(SurfaceHolder holder)
    {
        //mCamera = Camera.open();
        if (mCamera != null) {
            System.out.println("preparing media recorder");
            /*Parameters params = mCamera.getParameters();

            if(VideoCaptureActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);

            mCamera.setParameters(params);*/
            mCamera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        else {

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_camera_framework_bug),
                    Toast.LENGTH_LONG).show();

            finish();
        }
    }

    public void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        stopPreview();
        // set preview size and make any resize, rotate or
        // reformatting changes here
        setCamera(camera);

        // start preview with new settings
        startPreview();
    }

    public void startPreview(){
        try {
            if(mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
        setCameraRotation();
    }

    public void stopPreview(){
        try {
            if(mCamera != null)
                mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            e.printStackTrace();
        }
    }


    public void setCameraRotation() {
        try {
            System.out.println("test");

            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);
            int cameraRotationOffset = camInfo.orientation;
            // ...

            Camera.Parameters parameters = mCamera.getParameters();


            int rotation = 0;
            rotation=getWindowManager().getDefaultDisplay().getRotation();

            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break; // Natural orientation
                case Surface.ROTATION_90:
                    degrees = 90;
                    break; // Landscape left
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;// Upside down
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;// Landscape right
            }
            int displayRotation;
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                displayRotation = (cameraRotationOffset + degrees) % 360;
                displayRotation = (360 - displayRotation) % 360; // compensate
                // the
                // mirror
            } else { // back-facing
                displayRotation = (cameraRotationOffset - degrees + 360) % 360;
            }

            mCamera.setDisplayOrientation(displayRotation);


            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotate = (360 + cameraRotationOffset + degrees) % 360;
            } else {
                rotate = (360 + cameraRotationOffset - degrees) % 360;
            }

            parameters.set("orientation", "portrait");
            parameters.setRotation(rotate);
            mCamera.setParameters(parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int i1) {
        if(what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
        {
            System.out.println("Recording has been stopped");

            mrec.stop();
            mrec.release();
            mCamera.stopPreview();

            mCamera.lock();
            mCamera.release();
            myHandler.removeCallbacks(updateTimerMethod);

            SaveVideoAsyntask saveVideoAsyntask=new SaveVideoAsyntask();
            saveVideoAsyntask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }


    private class SaveVideoAsyntask extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPostExecute(Void aVoid) {

           /* if(parentFolder.equalsIgnoreCase("SOS")) {
                customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.title_activity_video_capture),getResources().getString(R.string.videorecording_recordvideomoreseconds,(Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ, "10000")) / 1000)),getResources().getString(R.string.title_activity_video_capture),0);
                //customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.title_activity_video_capture), "Do you want to record video for more " + (Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ, "10000")) / 1000) + " seconds?", getResources().getString(R.string.title_activity_video_capture), 0);
            }
            else
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("IMG_PATH", dirPath+fileName);
                setResult(RESULT_OK,returnIntent);
                finish();
            }*/

            Intent returnIntent = new Intent();
            returnIntent.putExtra("IMG_PATH", dirPath+fileName);
            setResult(RESULT_OK,returnIntent);
            finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveToSDCard();
                }
            });

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
