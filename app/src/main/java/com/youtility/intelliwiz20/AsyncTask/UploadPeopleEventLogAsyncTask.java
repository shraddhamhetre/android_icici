package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadPELogDataListener;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.UploadInfoParameter;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by youtility on 13/11/18.
 */

public class UploadPeopleEventLogAsyncTask extends AsyncTask {
    private Context context;
    private PeopleEventLog peopleEventLog;
    private IUploadPELogDataListener iUploadPELogDataListener;
    private URL url;
    private HttpURLConnection connection;
    private int bytesRead;
    private byte[] buffer;
    InputStream is;
    StringBuffer sb;
    int queryStatus=-1;
    long returnidResp=-1;
    private DateFormat dateFormat;
    private TypeAssistDAO typeAssistDAO;
    private SharedPreferences loginPref;

    public UploadPeopleEventLogAsyncTask(Context context, PeopleEventLog peopleEventLog, IUploadPELogDataListener iUploadPELogDataListener)
    {
        this.context=context;
        this.peopleEventLog=peopleEventLog;
        this.iUploadPELogDataListener=iUploadPELogDataListener;
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            url = new URL(Constants.BASE_URL);
            connection = (HttpURLConnection) url.openConnection();
            typeAssistDAO=new TypeAssistDAO(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        String infoParameter=null;
        Gson gson;


        try {
            String peopleEventQuery= DatabaseQuries.PEOPLE_EVENTLOG_INSERT+
                    "(" +
                    peopleEventLog.getAccuracy()+"," +//accuracy
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

            gson = new Gson();

            System.out.println("peopleEventQuery: "+peopleEventQuery.toString());

            UploadInfoParameter uploadInfoParameter=new UploadInfoParameter();
            uploadInfoParameter.setMail("false");
            uploadInfoParameter.setEvent(typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()));
            infoParameter=gson.toJson(uploadInfoParameter);

            ServerRequest serverRequest=new ServerRequest(context);
            HttpResponse response=serverRequest.getPeopleEventLogResponse(peopleEventQuery,infoParameter.trim(),
                    loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                    loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                    loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                    loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

            if(response!=null && response.getStatusLine().getStatusCode()==200)
            {
                is = response.getEntity().getContent();

                sb = new StringBuffer("");
                buffer = new byte[1024];
                bytesRead = 0;
                try {
                    while ((bytesRead = is.read(buffer)) != -1) {
                        sb.append(new String(buffer));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                is.close();
                System.out.println("--------------------SB PeopleEventLog: " + sb.toString());
                JSONObject ob = new JSONObject(sb.toString());
                queryStatus=ob.getInt(Constants.RESPONSE_RC);
                if(queryStatus==0)
                {
                    returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                }

            }


        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        //super.onPostExecute(o);
        iUploadPELogDataListener.uploadSOSPELog(queryStatus,returnidResp);
    }
}
