package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.AsyncTask.UploadAttachmentLogAsyncTask;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.IUploadAttachmentLogDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadSOSPELogDataListener;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.ResponseReturnIdData;
import com.youtility.intelliwiz20.Model.UploadInfoParameter;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;
import com.youtility.intelliwiz20.Utils.WaveDrawable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SOSActivity extends AppCompatActivity implements View.OnClickListener, IUploadSOSPELogDataListener, IDialogEventListeners, SensorEventListener{

    private Button sosCancelButton;
    private TextView sosText, sosDigitText;
    private ImageView waveDrawbleImageView;
    private WaveDrawable waveDrawable;
    private Interpolator interpolator;
    private Animation animation;
    private Vibrator vibrator;
    private int count=1;
    private MyTimer countdown;
    private SharedPreferences loginrelatedPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationMainPref;
    //private SharedPreferences emergencyContactInfoPref;
    private String tempSendingMsg=null;
    private long peopleID=-1;
    private TypeAssistDAO typeAssistDAO;
    private PeopleEventLogDAO peopleEventLogDAO;
    private CheckNetwork checkNetwork;
    private boolean isInfoAvailable=false;
    private long currentTimestamp=-1;
    private long retrunIdResp=-1;

    private SharedPreferences sharedPreferences;


    //-------------------------------------- sensor related

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1000;
    private TextView xaxis, yaxis, zaxis;
    private int fallDownCount=0;
    private long pelogId=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        loginrelatedPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        applicationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);
        //emergencyContactInfoPref=getSharedPreferences(Constants.EMERGENCY_CONTACT_INFO_PREF, MODE_PRIVATE);
        peopleID=loginrelatedPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        typeAssistDAO=new TypeAssistDAO(SOSActivity.this);
        peopleEventLogDAO=new PeopleEventLogDAO(SOSActivity.this);
        checkNetwork=new CheckNetwork(SOSActivity.this);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SOSActivity.this);
        componentInitialise();
        String emgergencyNumber=sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_NUMBER,"");
        if(emgergencyNumber!=null && emgergencyNumber.trim().length()>0) {
            isInfoAvailable=true;
            //startCountdownTimer();
            startSOS();
        }
        else {
            isInfoAvailable=false;
            //Snackbar.make(sosCancelButton, getResources().getString(R.string.sos_emergency_contact_num_error), Snackbar.LENGTH_LONG).show();
        }

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        xaxis=(TextView)findViewById(R.id.xaxis);
        yaxis=(TextView)findViewById(R.id.yaxis);
        zaxis=(TextView)findViewById(R.id.zaxis);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void componentInitialise()
    {
        //sosCancelButton=(Button)findViewById(R.id.sosCancelButton);
        //sosCancelButton.setOnClickListener(this);
        sosText=(TextView)findViewById(R.id.sosText);
        //sosDigitText=(TextView)findViewById(R.id.sosDigitText);
        waveDrawbleImageView=(ImageView)findViewById(R.id.waveDrawableImageView);
        waveDrawable=new WaveDrawable(Color.parseColor("#FF0000"),300);
        waveDrawbleImageView.setBackgroundDrawable(waveDrawable);
        interpolator=new OvershootInterpolator();
        waveDrawable.setWaveInterpolator(interpolator);

    }


    private void startCountdownTimer()
    {
        //set first parameter from setting
        count=3;
        countdown=new MyTimer((count*1000), 1000);
        countdown.start();
        waveDrawable.startAnimation();


    }

    @Override
    public void onClick(View v) {
        if(isInfoAvailable) {
            //countdown.cancel();
            //vibrator.cancel();
            //stopSOS();
            //waveDrawable.stopAnimation();
            //sosDigitText.setVisibility(View.GONE);
            //sosCancelButton.setVisibility(View.GONE);
            System.out.println("cancel click");
        }
        Snackbar.make(v,getResources().getString(R.string.sos_cancelled),Snackbar.LENGTH_LONG).show();
        finish();

    }

    private void stopSOS()
    {
        sosText.clearAnimation();
        sosText.setText("");
        //countdown.cancel();
    }

    private void startSOS()
    {


        if(CommonFunctions.isSimAvailable(SOSActivity.this)) {
            currentTimestamp=System.currentTimeMillis();

            insertPeopleEventLogRecord();//save to db

            prepareSendingMessage();

            Snackbar.make(sosText, getResources().getString(R.string.sos_sending), Snackbar.LENGTH_LONG).show();

            if(tempSendingMsg!=null && tempSendingMsg.length()>0) {

                //SMSendingMethod(tempSendingMsg);
            }
        }
        else {
            sosText.setText("");
            CustomAlertDialog customAlertDialog=new CustomAlertDialog(SOSActivity.this,this);
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle),getResources().getString(R.string.sos_simnotavailabel));
        }
    }

    public void prepareSendingMessage()
    {
        Double latCor=0.0, lonCor=0.0;
        List<Address> addresses;
        String message;
        StringBuilder stringBuilder;


        message=getResources().getString(R.string.sos_help_message, loginrelatedPref.getString(Constants.LOGIN_USER_ID,""),sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_HELP_MESSAGE,""));
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
                Geocoder geoCoder=new Geocoder(SOSActivity.this, Locale.ENGLISH);
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
                //System.out.println("Sending message: "+builder.toString());
                tempSendingMsg=builder.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        float gravityV[]=new float[3];
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {

            /*final float alpha = 0.8f;
//gravity is calculated here
            gravityV[0] = alpha * gravityV[0] + (1 - alpha) * sensorEvent.values[0];
            gravityV[1] = alpha * gravityV[1] + (1 - alpha)* sensorEvent.values[1];
            gravityV[2] = alpha * gravityV[2] + (1 - alpha) * sensorEvent.values[2];
//acceleration retrieved from the event and the gravity is removed
            float x = sensorEvent.values[0] - gravityV[0];
            float y = sensorEvent.values[1] - gravityV[1];
            float z = sensorEvent.values[2] - gravityV[2];



            float accelationSquareRoot = (x * x + y * y + z * z)
                    / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

            xaxis.setText(x+"");
            yaxis.setText(y+"");
            zaxis.setText(z+"");

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                System.out.println("speed : "+speed);
                if(fallDownCount==0) {
                    if (speed > SHAKE_THRESHOLD) {
                        Toast.makeText(SOSActivity.this, "Fall Down : " + speed, Toast.LENGTH_SHORT).show();
                        fallDownCount++;
                        startCountdownTimer();
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }*/

        }
        else if(mySensor.getType()== Sensor.TYPE_GYROSCOPE)
        {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            System.out.println("Gyroscope: "+x+" : "+y+" : "+z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public class MyTimer extends CountDownTimer
    {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            sosText.setText(getResources().getString(R.string.sos_starting));
            vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern ={0,500,500};

            vibrator.vibrate(pattern,0);
            sosDigitText.setVisibility(View.VISIBLE);
            sosCancelButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            count--;
            sosDigitText.setText(count+"");
        }

        @Override
        public void onFinish() {
            vibrator.cancel();
            sosText.setText(getResources().getString(R.string.sos_sendingpanic));
            startSOS();
            if(waveDrawable.isAnimationRunning())
                waveDrawable.stopAnimation();

            sosDigitText.setVisibility(View.GONE);
            sosCancelButton.setVisibility(View.GONE);
        }
    }


    private String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return String.valueOf(Double.valueOf(twoDForm.format(d)));
    }

    private void insertPeopleEventLogRecord()
    {

        //accuracy-, datetime-, gpslocation-, photorecognitionthreshold-, photorecognitionscore-, photorecognitiontimestamp-, photorecognitionserviceresponse-,
        //  facerecognition-, peopleid-, peventtype-, punchstatus-, verifiedby-, siteid, cuser-, muser-, cdtz-, mdtz-, isdeleted-, gfid, deviceid-

        PeopleEventLog peopleEventLog=new PeopleEventLog();
        peopleEventLog.setPelogid(currentTimestamp);
        peopleEventLog.setAccuracy(Float.valueOf(deviceRelatedPref.getString(Constants.DEVICE_ACCURACY,"-1")));
        //peopleEventLog.setDeviceid(deviceRelatedPref.getLong(Constants.DEVICE_IMEI,-1));
        peopleEventLog.setDeviceid(deviceRelatedPref.getString(Constants.DEVICE_IMEI,"-1"));

        peopleEventLog.setDatetime(String.valueOf(currentTimestamp));
        peopleEventLog.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp(null);
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setCuser(peopleID);
        peopleEventLog.setMuser(peopleID);
        peopleEventLog.setPeopleid(peopleID);
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID("SOS", Constants.IDENTIFIER_EVENTTYPE));
        peopleEventLog.setPunchstatus(-1);
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode("");
        peopleEventLog.setGfid(-1);
        peopleEventLog.setBuid(loginrelatedPref.getLong(Constants.LOGIN_SITE_ID,-1));
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        peopleEventLog.setRemarks("");
        peopleEventLog.setTransportmode(-1);
        peopleEventLog.setOtherlocation("");

        peopleEventLogDAO.insertRecord(peopleEventLog);
        if(checkNetwork.isNetworkConnectionAvailable())
        {
            /*UploadSOSPELogAsyncTask uploadSOSPELogAsyncTask=new UploadSOSPELogAsyncTask(SOSActivity.this, peopleEventLog,this);
            uploadSOSPELogAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

            prepareSOSData(peopleEventLog);
        }
        else
            callVideoRecordingIntent();
    }

    private void prepareSOSData(PeopleEventLog peopleEventLog)
    {
        String peopleEventQuery= DatabaseQuries.PEOPLE_EVENTLOG_INSERT+
                "(" +
                "-1," +//accuracy
                "now()," +//datetime
                "'"+peopleEventLog.getGpslocation()+"'," +//gpslocation
                peopleEventLog.getPhotorecognitionthreshold()+","+//photorecognitionthreshold
                peopleEventLog.getPhotorecognitionscore()+","+//photorecognitionscore
                "now(),"+//photorecognitiontimestamp
                "null,"+//photorecognitionserviceresponse
                "false,"+//facerecognition
                peopleEventLog.getPeopleid()+"," +//peopleid
                peopleEventLog.getPeventtype()+"," +//peventtype
                peopleEventLog.getPunchstatus()+","+//punchstatus
                peopleEventLog.getVerifiedby()+","+//verifiedby
                peopleEventLog.getBuid()+","+//siteid
                peopleEventLog.getCuser()+","+//cuser
                peopleEventLog.getMuser()+",'"+//muser
                (peopleEventLog.getCdtz())+"','" +//cdtz
                (peopleEventLog.getMdtz())+"'," +//mdtz
                peopleEventLog.getGfid()+","+//gfid
                "'"+peopleEventLog.getDeviceid()+"',"+//deviceid
                peopleEventLog.getTransportmode()+","+
                peopleEventLog.getExpamt()+","+
                peopleEventLog.getDuration()+","+
                "'"+peopleEventLog.getReference()+"',"+
                "'"+peopleEventLog.getRemarks()+"',"+
                peopleEventLog.getDistance()+","+
                "'"+peopleEventLog.getOtherlocation()+"'"+
                ") returning pelogid;";

        Gson gson=new Gson();
        UploadInfoParameter uploadInfoParameter=new UploadInfoParameter();
        uploadInfoParameter.setMail("true");
        uploadInfoParameter.setEvent(typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()));
        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(Constants.SERVICE_INSERT);
        uploadParameters.setQuery(peopleEventQuery);
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setInfo(gson.toJson(uploadInfoParameter));
        uploadParameters.setTzoffset(String.valueOf(loginrelatedPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
        uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginrelatedPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
        uploadParameters.setLoginid(loginrelatedPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
        uploadParameters.setPassword(loginrelatedPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
        //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
        uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

        RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
        Call<ResponseReturnIdData> call=retrofitServices.getServerResponseReturnId(Constants.SERVICE_INSERT,uploadParameters);
        call.enqueue(new Callback<ResponseReturnIdData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseReturnIdData> call, @NonNull Response<ResponseReturnIdData> response) {
                if(response.isSuccessful() && response.body()!=null)
                {
                    if(response.body().getRc()==0)
                    {
                        retrunIdResp=response.body().getReturnid();
                        peopleEventLogDAO.changeSOSSyncStatus(currentTimestamp,retrunIdResp,String.valueOf(currentTimestamp));
                        callVideoRecordingIntent();

                        System.out.println("return id"+ retrunIdResp);

                        SosVideoCaptureActivity.uploadAttendanceAttachment1(retrunIdResp, currentTimestamp);


                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseReturnIdData> call, Throwable t) {

            }
        });
    }

    @Override
    public void uploadSOSPELog(int status, long retrunIdResp) {
        System.out.println("Status: "+status+" : retrunIdResp: "+retrunIdResp);
       if(status==0)
       {
           this.retrunIdResp=retrunIdResp;
           peopleEventLogDAO.changeSOSSyncStatus(currentTimestamp,retrunIdResp,String.valueOf(currentTimestamp));

           callVideoRecordingIntent();
           //SosVideoCaptureActivity.uploadAttendanceAttachment(retrunIdResp);

           System.out.println("return id"+ retrunIdResp);

       }
        /*setResult(RESULT_OK);
        finish();*/
    }

    private void uploadAttendanceAttachment(long retId)
    {
        if(checkNetwork.isNetworkConnectionAvailable())
        {
            UploadAttachmentLogAsyncTask uploadAttachmentLogAsyncTask=new UploadAttachmentLogAsyncTask(SOSActivity.this,retId,currentTimestamp, (IUploadAttachmentLogDataListener) this);
            uploadAttachmentLogAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }

    }

    public void composeMmsMessage(String message, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
        intent.putExtra("sms_body", message);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

  /*  private void SMSendingMethod(String tempSendingMsg1)
    {
        Double latCor=0.0, lonCor=0.0;
        String emailID=sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_EMAILID,"");
        String phoneNo=sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_NUMBER,"");
        latCor=Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        lonCor=Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));

        String name= sharedPreferences.getString(Constants.SETTING_GENERAL_LOGIN_USER_NAME,"");

        String uri = getResources().getString(R.string.sos_help_message,name ,
                sharedPreferences.getString(Constants.SETTING_GENERAL_CONTACT_HELP_MESSAGE,""))+" "+getResources().getString(R.string.sos_my_location,roundTwoDecimals(latCor), roundTwoDecimals(lonCor));

        StringBuffer smsBody = new StringBuffer();
        smsBody.append(Uri.parse(uri));
        List pn= Arrays.asList(phoneNo.split(","));

        for (int i = 0; i < pn.size();i++) {
            sendSMS((String) pn.get(i), smsBody.toString());
        }

        *//*if(emailID!=null && emailID.trim().length()>0) {
            EmailAsyncTask emailAsyncTask = new EmailAsyncTask(emailID);
            emailAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }*//*
    }*/

    public class EmailAsyncTask extends AsyncTask<Void, Void, Void>
    {
        String emailID;
        public EmailAsyncTask(String emailID)
        {
            this.emailID=emailID;
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

                    Multipart multipart = new MimeMultipart();
                    String body="";

                    String tdstyle= "style='background:#ABE7ED;font-weight:bold;font-size:14px;'";


                    body+= "<table style='background:#EEF1F5;' cellpadding=8 cellspacing=2>";
                    body+= "<tr> <td align='right' "+tdstyle+">Message: </td><td>"+tempSendingMsg+"</td></tr>";
                    body+="</table>";

                    MimeBodyPart htmlTextPart = new MimeBodyPart();
                    htmlTextPart.setContent(body, "text/html;charset=UTF-8");

   /*                 String file = "path of file to be attached";
                    String fileName = "attachmentName";
                    DataSource source = new FileDataSource(file);
                    htmlTextPart.setDataHandler(new DataHandler(source));
                    htmlTextPart.setFileName(fileName);
*/
                    multipart.addBodyPart(htmlTextPart);

                    message.setContent(multipart);
                    Transport.send(message);

                    /*MimeBodyPart b = new MimeBodyPart();
                    b.setDisposition(MimeBodyPart.INLINE);
                    b.setText(tempSendingMsg);
                    //ByteArrayDataSource ds1 = new ByteArrayDataSource(mailbody.getBytes(Charset.forName("utf-8")), "text/html; charset=utf-8");
                    //b.setDataHandler(new DataHandler(ds1));
                    multipart.addBodyPart(b);

                    message.setContent(multipart);
                    Transport.send(message);*/
                    Log.d("EmailDataFilesAsyncTask", " message emailed");
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
            sosText.setText(getResources().getString(R.string.sos_sent));
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


    private void callVideoRecordingIntent()
    {
        Intent captureVideo=new Intent(SOSActivity.this, SosVideoCaptureActivity.class);
        captureVideo.putExtra("FROM",Constants.ATTACHMENT_VIDEO);
        captureVideo.putExtra("TIMESTAMP",currentTimestamp);
        captureVideo.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_PEOPLEEVENTLOG);
        captureVideo.putExtra("FOLDER","SOS");
        captureVideo.putExtra("JOBNEEDID",retrunIdResp);
        startActivityForResult(captureVideo,Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        System.out.println("sos----");

    }

    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                finish();
            }
        }
    }


}
