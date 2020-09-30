package com.youtility.intelliwiz20.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadAttachmentLogDataListener;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.BiodataParameters;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by youtility on 13/11/18.
 */

public class UploadAttachmentLogAsyncTask extends AsyncTask {
    private Context context;
    private long returnId;
    private IUploadAttachmentLogDataListener iUploadAttachmentLogDataListener;
    private AttachmentDAO attachmentDAO;
    private Attachment attachment;
    private long cTimestamp;
    File selectedFile=null;
    String fileName;
    String date=null;
    int serverResponseCode = 0;
    HttpURLConnection connection;
    DataOutputStream dataOutputStream;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "921b1508a0b342f5bb06dfa40ae1f55d";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private ProgressDialog progressDialog;
    private SharedPreferences applicationPref;

    public UploadAttachmentLogAsyncTask(Context context, long returnId, long cTimestamp,IUploadAttachmentLogDataListener iUploadAttachmentLogDataListener)
    {
        this.context=context;
        this.returnId=returnId;
        this.iUploadAttachmentLogDataListener=iUploadAttachmentLogDataListener;
        this.cTimestamp=cTimestamp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        attachmentDAO=new AttachmentDAO(this.context);
        loginDetailPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        applicationPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF,Context.MODE_PRIVATE);

        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait");
        attachment=new Attachment();
        attachment=attachmentDAO.getAttachment(returnId,cTimestamp);
        //System.out.print("getAttachmentid: "+attachment.getAttachmentid()+" : "+cTimestamp);
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
    protected Object doInBackground(Object[] objects) {
        if (selectedFile !=null && !selectedFile.isFile()) {

        }
        else
        {
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
                //dataOutputStream.writeBytes(String.valueOf(loginDetailPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,""))+"."+String.valueOf(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,"")));
                dataOutputStream.writeBytes(applicationPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"loginid\""+ lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(String.valueOf(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,"")));
                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"password\""+ lineEnd);
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(String.valueOf(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,"")));
                dataOutputStream.writeBytes(lineEnd);

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
                        Toast.makeText(context, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
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
                    System.out.println("sitecode===" +loginDetailPref.getString(Constants.LOGIN_SITE_CODE,"")
                    );
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
                        File file = new File(attachment.getFilePath());
                        boolean deleted = file.delete();
                        Log.v("Attachment","deleted: " + deleted);
                    }

                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e){
                e.printStackTrace();
                System.out.println("stop");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        //super.onPostExecute(o);
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
        iUploadAttachmentLogDataListener.uploadAttachmentLog(0,-1);
    }
}
