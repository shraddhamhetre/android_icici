package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadSOSPELogDataListener;
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
 * Created by PrashantD on 30/11/17.
 *
 * not in used
 */

public class UploadSOSPELogAsyncTask extends AsyncTask<Void, Integer, Integer>{
    private Context context;
    private PeopleEventLog peopleEventLog;
    IUploadSOSPELogDataListener iUploadSOSPELogDataListener;
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

    public UploadSOSPELogAsyncTask(Context context, PeopleEventLog peopleEventLog, IUploadSOSPELogDataListener iUploadSOSPELogDataListener)
    {
        this.context=context;
        this.peopleEventLog=peopleEventLog;
        this.iUploadSOSPELogDataListener=iUploadSOSPELogDataListener;
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
    protected Integer doInBackground(Void... params) {
        String peoplegetquery=null;
        String infoParameter=null;
        Gson gson;

        try {
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

            gson = new Gson();

            System.out.println("peopleEventQuery: "+peopleEventQuery.toString());

            UploadInfoParameter uploadInfoParameter=new UploadInfoParameter();
            uploadInfoParameter.setMail("true");
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
                    //peopleEventLogDAO.changeSyncStatus(String.valueOf(peopleEventLog.getDatetime()));
                }

            }



            /*gson = new Gson();

            UploadInfoParameter uploadInfoParameter=new UploadInfoParameter();
            uploadInfoParameter.setMail("true");
            uploadInfoParameter.setEvent(typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()));
            infoParameter=gson.toJson(uploadInfoParameter);

            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(peopleEventQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setInfo(infoParameter.toString());


            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            connection.setReadTimeout(15000 *//* milliseconds *//*);
            connection.setConnectTimeout(15000 *//* milliseconds *//*);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");



            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());

            is = data.getContent();
            buffer = new byte[1024];
            bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1)
            {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            out.close();
            is.close();

            int responseCode=connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {

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
                System.out.println("SB0: "+sb.toString());

            }
            else {
                System.out.println("SB1: "+responseCode);
            }

            JSONObject ob = new JSONObject(sb.toString());
            queryStatus=ob.getInt("rc");*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return  queryStatus;
    }


    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(status);
        iUploadSOSPELogDataListener.uploadSOSPELog(status,returnidResp);
    }

}






