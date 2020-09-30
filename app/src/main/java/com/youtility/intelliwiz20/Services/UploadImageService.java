package com.youtility.intelliwiz20.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.BiodataParameters;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadImageService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_IMAGE = "com.youtility.intelliwiz20.Services.action.IMAGE";
    //private static final String ACTION_IMAGE = "com.youtility.istaging.Services.action.IMAGE";
    private static final String ACTION_RESCHEDULE ="service.intent.action.SERVICE_RESCHEDULE";

    private CheckNetwork checkNetwork;
    private AttachmentDAO attachmentDAO;
    private TypeAssistDAO typeAssistDAO;
    private ArrayList<Attachment>attachmentArrayList;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences sharedPreferences;
    private SharedPreferences applicationPref;
    private SharedPreferences autoSyncPref;


    public UploadImageService() {
        super("UploadImageService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkNetwork=new CheckNetwork(UploadImageService.this);
        attachmentDAO=new AttachmentDAO(UploadImageService.this);
        typeAssistDAO=new TypeAssistDAO(UploadImageService.this);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        applicationPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(UploadImageService.this);
        autoSyncPref=getSharedPreferences(Constants.AUTO_SYNC_PREF,MODE_PRIVATE);

        startActionFoo(UploadImageService.this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, UploadImageService.class);
        intent.setAction(ACTION_IMAGE);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_IMAGE.equals(action)) {
                System.out.println("Upload image service started");
                attachmentDAO.deleteAVP();
                attachmentArrayList=new ArrayList<>();

                attachmentArrayList=attachmentDAO.getUnsyncAttachments(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
                handleActionFoo();
            }
        }


    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        if(checkNetwork.isNetworkConnectionAvailable())
        {
            if(attachmentArrayList!=null && attachmentArrayList.size()>0) {
                for (int i = 0; i < attachmentArrayList.size(); i++)
                    retrieveAndUploadData(attachmentArrayList.get(i));
            }

            //callAutoSyncService();

        }
        //long interval=Integer.parseInt(sharedPreferences.getString(Constants.SETTING_SYNCHRONIZATION_UPLOADDATA_FREQ,"15"))*(60*1000);
        /*long interval=3*(60*1000);
        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "com.youtility.intelliwiz20.Services.UploadImageService");
        i.setAction(ACTION_RESCHEDULE);
        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, interval, pi);*/


    }

    private void callAutoSyncService()
    {
        Intent startIntent = new Intent(UploadImageService.this, AutoSyncService.class);
        startService(startIntent);
    }

    private void retrieveAndUploadData(Attachment attachment)
    {
        if(attachment!=null) {
            if(typeAssistDAO.getEventTypeCode(attachment.getAttachmentType()).equalsIgnoreCase(Constants.ATTACHMENT_TYPE_ATTACHMENT) || typeAssistDAO.getEventTypeCode(attachment.getAttachmentType()).equalsIgnoreCase(Constants.ATTACHMENT_TYPE_SIGN) ) {
                UploadImageAsynTask uploadImageAsynTask = new UploadImageAsynTask(attachment);
                uploadImageAsynTask.execute();
            }
        }
    }


    private class UploadImageAsynTask extends AsyncTask<Void, Integer, Void>
    {
        int serverResponseCode = 0;
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "921b1508a0b342f5bb06dfa40ae1f55d";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile=null;
        String fileName;
        String date=null;
        Attachment attachment;

        public UploadImageAsynTask(Attachment attachment)
        {
            this.attachment=attachment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(attachment!=null && attachment.getFileName()!=null && attachment.getFileName().toString().trim().length()>0) {
                selectedFile = new File(attachment.getFilePath());

                DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                date = df.format(Calendar.getInstance().getTime());


                String[] parts = attachment.getFilePath().split("/");
                fileName = parts[parts.length - 1];
                    System.out.println("FileName: " + fileName);
            }
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            if (selectedFile !=null && !selectedFile.isFile()) {

            }
            else
            {

                System.out.println("image upload call------::");
                autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,true).apply();
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(Constants.BASE_URL);

                    System.out.println("Constants.BASE_URL: "+Constants.BASE_URL);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", attachment.getFilePath());


                    String ss="INSERT INTO attachment(filepath, filename, narration, gpslocation, datetime,cuser, cdtz, muser, mdtz, attachmenttype,ownerid, ownername, buid)" +
                            "VALUES ('"+
                            attachment.getServerPath()+attachment.getOwnerid()+"','"+
                            attachment.getFileName()+"','"+
                            attachment.getNarration()+"','"+
                            attachment.getGpslocation()+"','"+
                            attachment.getDatetime()+"',"+
                            attachment.getCuser()+",'"+
                            attachment.getCdtz()+"',"+
                            attachment.getMuser()+",'"+
                            attachment.getMdtz()+"',"+
                            attachment.getAttachmentType()+","+
                            attachment.getOwnerid()+","+
                            attachment.getOwnername()+","+
                            attachment.getBuid()+
                            ") returning attachmentid;";


                    connection.setRequestProperty("query",ss);

                    System.out.println("Image upload Query: "+ss.toString());



                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    String dispName= "image";

                    Gson gson = new Gson();

                    BiodataParameters biodataParameters=new BiodataParameters();
                    biodataParameters.setJobneedid(-1);
                    biodataParameters.setJobdesc(null);
                    biodataParameters.setJobstatus(-1);//need to get id from type assist for job status
                    biodataParameters.setCuser(attachment.getCuser());
                    biodataParameters.setRemarks(attachment.getNarration());
                    biodataParameters.setAlertto(-1);
                    biodataParameters.setAssigntype(null);
                    biodataParameters.setPelogid(attachment.getOwnerid());
                    biodataParameters.setFilename(fileName);
                    biodataParameters.setPath(attachment.getServerPath()+attachment.getOwnerid());
                    biodataParameters.setPeopleid(attachment.getCuser());


                    String bioData=gson.toJson(biodataParameters);

                    System.out.println("Biodata: "+bioData.toString());

                    CommonFunctions.UploadLog("\n Attachment Upload \n"+"Query: "+ss.toString()+"\n"+"Biodata: "+bioData.toString()+"\n");

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"servicename\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(Constants.SERVICE_UPLOAD_ATTACHMENT);
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"story\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes("1");
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"query\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(ss.toString());
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"biodata\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes("["+bioData.toString()+"]");
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"tzoffset\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"deviceid\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    //dataOutputStream.writeBytes(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
                    dataOutputStream.writeBytes(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"sitecode\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(applicationPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"loginid\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"password\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
                    dataOutputStream.writeBytes(lineEnd);

                   /* dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"filename\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(fileName);
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"path\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(Constants.SERVER_FILE_LOCATION_PATH+"peopleattendance/"+attachment.getPelogid());
                    dataOutputStream.writeBytes(lineEnd);*/

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+dispName+"\";filename=\"" + fileName +"\"" + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {

                        try {

                            //write the bytes read from inputstream
                            dataOutputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                            //Toast.makeText(UploadImageService.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    try {
                        serverResponseCode = connection.getResponseCode();
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(UploadImageService.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                    }
                    String serverResponseMessage = connection.getResponseMessage();

                    System.out.println( "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
                    StringBuffer sb=null;
                    int responseCode=connection.getResponseCode();
                    CommonFunctions.ResponseLog("\n Attachment Response \n"+"OwnerID: "+attachment.getOwnerid()+"\n"+"Response Code: "+responseCode+"\n");

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in=new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()));
                        sb = new StringBuffer("");
                        String line="";

                        while((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        System.out.println("Image SB1: "+sb.toString());

                    }
                    else {
                        System.out.println("Image SB2: "+responseCode);
                    }
                    int rc=-1;
                    if(responseCode==200) {
                        try {
                            String json = sb.toString();
                            CommonFunctions.ResponseLog("\n Attachment Response \n"+"OwnerID: "+attachment.getOwnerid()+"\n"+"Response Log: "+json.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(json);
                            rc = ob.getInt(Constants.RESPONSE_RC);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("serverResponseCode: " + serverResponseCode + " : " + rc);
                        //response code of 200 indicates the server status OK
                        if (serverResponseCode == 200 && rc == 0) {
                            System.out.println("File Upload completed.\n\n " + fileName + " : " + attachment.getAttachmentid());
                            attachmentDAO.changeSycnStatus(String.valueOf(attachment.getAttachmentid()), attachment.getAttachmentType(), attachment.getFileName());
                            attachmentDAO.deleteAVP(attachment.getAttachmentid());
                            File file = new File(attachment.getFilePath());
                            boolean deleted = file.delete();
                            Log.v("Attachment","deleted: " + deleted);
                        }

                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                System.out.println("image upload finish");
                autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();

            }

            return null;
        }

    }

}
