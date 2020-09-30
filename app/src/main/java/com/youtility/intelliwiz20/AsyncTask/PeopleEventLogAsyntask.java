package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadPeopleEventDataListener;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.UploadInfoParameter;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

/**
 * Created by PrashantD on 22/9/17.
 *
 *  not in used
 */

public class PeopleEventLogAsyntask extends AsyncTask<Void, Integer, Integer>
{
    Context context;
    IUploadPeopleEventDataListener iUploadPeopleEventDataListener;
    PeopleEventLog peopleEventLog;
    private PeopleEventLogDAO peopleEventLogDAO;
    private ArrayList<PeopleEventLog> peopleEventLogArrayList;
    private AttachmentDAO attachmentDAO;
    private TypeAssistDAO typeAssistDAO;

    private SharedPreferences loginPref;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private Gson gson;


    public PeopleEventLogAsyntask(Context context, IUploadPeopleEventDataListener iUploadPeopleEventDataListener)
    {
        this.context=context;
        this.iUploadPeopleEventDataListener=iUploadPeopleEventDataListener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        peopleEventLogDAO=new PeopleEventLogDAO(context);
        attachmentDAO=new AttachmentDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        peopleEventLogArrayList=new ArrayList<>();
        peopleEventLogArrayList=peopleEventLogDAO.getEvents();

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int queryStatus=-1;
        String infoParameter=null;
        System.out.println("Thread: PeopleEventLogAsyntask() current thread" + Thread.currentThread().getName());

        if(peopleEventLogArrayList!=null && peopleEventLogArrayList.size()>0)
        {
            System.out.println("AutoSync people event log size: "+peopleEventLogArrayList.size());
            for(int i=0;i<peopleEventLogArrayList.size();i++)
            {
                peopleEventLog=new PeopleEventLog();
                peopleEventLog=peopleEventLogArrayList.get(i);

                if(typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()).equalsIgnoreCase("SOS") || typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()).equalsIgnoreCase("GEOFENCE") )
                {
                    UploadInfoParameter uploadInfoParameter=new UploadInfoParameter();
                    uploadInfoParameter.setMail("true");
                    uploadInfoParameter.setEvent(typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()));
                    infoParameter=gson.toJson(uploadInfoParameter);
                }
                else
                {
                    UploadInfoParameter uploadInfoParameter=new UploadInfoParameter();
                    uploadInfoParameter.setMail("false");
                    uploadInfoParameter.setEvent(typeAssistDAO.getEventTypeName(peopleEventLog.getPeventtype()));
                    infoParameter=gson.toJson(uploadInfoParameter);
                }


                String peopleEventQuery= DatabaseQuries.PEOPLE_EVENTLOG_INSERT+
                        "(" +
                        peopleEventLog.getAccuracy()+"," +//accuracy
                        "'"+ CommonFunctions.getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"'," +//datetime
                        "'"+peopleEventLog.getGpslocation()+"'," +//gpslocation
                        "-1,"+//photorecognitionthreshold
                        "-1,"+//photorecognitionscore
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
                        peopleEventLog.getCdtz()+"','" +//cdtz
                        peopleEventLog.getMdtz()+"'," +//mdtz
                        peopleEventLog.getGfid()+","+//gfid
                        "'"+peopleEventLog.getDeviceid()+"',"+//deviceid
                        peopleEventLog.getTransportmode()+","+
                        peopleEventLog.getExpamt()+","+
                        peopleEventLog.getDuration()+","+
                        "'"+peopleEventLog.getReference()+"',"+
                        "'"+peopleEventLog.getRemarks().replace("'","''")+"',"+
                        peopleEventLog.getDistance()+","+
                        "'"+peopleEventLog.getOtherlocation().replace("'","''")+"'"+
                        ") returning pelogid;";

                System.out.println("peopleEventQuery: "+peopleEventQuery);


                try {
                    ServerRequest serverRequest=new ServerRequest(context);
                    HttpResponse response=serverRequest.getPeopleEventLogResponse(peopleEventQuery,infoParameter.trim(),
                            loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                    //System.out.println("PeopleEventLog response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                    System.out.println("responce=="+response+"==getstatus=="+response.getStatusLine().getStatusCode());
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
                        System.out.println("SB PeopleEventLog: " + sb.toString());
                        response.getEntity().consumeContent();

                        CommonFunctions.ResponseLog("\n AutoSync People Event Log Response \n"+CommonFunctions.getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"\n"+sb.toString()+"\n");


                        JSONObject ob = new JSONObject(sb.toString());
                        if(ob.getInt(Constants.RESPONSE_RC)==0)
                        {
                            queryStatus=0;
                            long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                            peopleEventLogDAO.changeSyncStatus(String.valueOf(peopleEventLog.getDatetime()));
                            attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnidResp),String.valueOf(peopleEventLog.getDatetime()));
                        }
                        else
                        {
                            CommonFunctions.ErrorLog("\n AutoSync People event log Error: \n"+CommonFunctions.getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n ");
                            queryStatus=-1;
                            break;
                        }
                    }
                    else {
                        CommonFunctions.ErrorLog("\nAutoSync People event log Error: \n"+CommonFunctions.getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"\n"+"Connection not established with server."+"\n"+"responce=="+response+"==getstatus=="+response.getStatusLine().getStatusCode());
                        System.out.println("SB1 PeopleEventLog: ERROR ");
                        queryStatus=-1;
                        break;
                    }


                } catch (UnknownHostException e) {
                    e.printStackTrace();
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

                }
            }
        }
        else
        {
            queryStatus=0;
        }


        return  queryStatus;
    }


    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(status);
        iUploadPeopleEventDataListener.finishPeopleEventLogUpload(status);
    }



}
