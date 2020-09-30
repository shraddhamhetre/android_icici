package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PersonLoggerDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadPersonLoggerDataListener;
import com.youtility.intelliwiz20.Model.PersonLogger;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by youtility on 17/8/18.
 */

public class PersonLoggerAsyncTask extends AsyncTask <Void , Integer, Integer>{

    IUploadPersonLoggerDataListener iUploadPersonLoggerDataListener;
    Context context;

    private SharedPreferences loginPref;
    private byte[] buffer;
    private InputStream is;
    private StringBuffer sb;
    private Gson gson;
    private int bytesRead;
    private PersonLogger personLogger;
    private ArrayList<PersonLogger> personLoggerArrayList;
    private PersonLoggerDAO personLoggerDAO;
    private AttachmentDAO attachmentDAO;

    public PersonLoggerAsyncTask(Context context, IUploadPersonLoggerDataListener iUploadPersonLoggerDataListener)
    {
        this.context=context;
        this.iUploadPersonLoggerDataListener=iUploadPersonLoggerDataListener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        personLoggerDAO=new PersonLoggerDAO(context);
        attachmentDAO=new AttachmentDAO(context);
        personLoggerArrayList=new ArrayList<PersonLogger>();
        personLoggerArrayList=personLoggerDAO.getUnsyncPersonLoggerList( Constants.TACODE_EMPLOYEEREFERENCE,  Constants.SYNC_STATUS_ZERO);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int queryStatus=-1;
        if(personLoggerArrayList!=null && personLoggerArrayList.size()>0)
        {
            System.out.println("personLoggerArrayList: "+personLoggerArrayList.size());
            for(int i=0;i<personLoggerArrayList.size();i++)
            {
                try {
                    personLogger=new PersonLogger();
                    personLogger=personLoggerArrayList.get(i);

                    String insertQuery= DatabaseQuries.PERSON_LOGGER_INSERT+"("+personLogger.getPersonloggerid()+","+personLogger.getIdentifier()+","+personLogger.getPeopleid()+",'"+personLogger.getVisitoridno()+"',"+
                            "'"+personLogger.getFirstname()+"','"+personLogger.getMiddlename()+"','"+personLogger.getLastname()+"','"+personLogger.getMobileno()+"',"+personLogger.getIdprooftype()+","+
                            "'"+personLogger.getPhotoidno()+"','"+personLogger.getBelongings()+"','"+personLogger.getMeetingpurpose()+"','"+personLogger.getScheduledintime()+"','"+personLogger.getScheduledouttime()+"',"+
                            "'"+personLogger.getActualintime()+"','"+personLogger.getActualouttime()+"','"+personLogger.getReferenceid()+"','"+personLogger.getDob()+"','"+personLogger.getLocaladdress()+"',"+
                            "'"+personLogger.getNativeaddress()+"','"+personLogger.getQualification()+"',"+personLogger.isEnglish()+",'"+personLogger.getCurrentemployement()+"',"+
                            personLogger.getLengthofservice()+","+personLogger.getHeightincms()+","+personLogger.getWeightinkgs()+","+personLogger.getWaist()+","+personLogger.getIshandicapped()+","+
                            "'"+personLogger.getIdentificationmark()+"','"+personLogger.getPhysicalcondition()+"','"+personLogger.getReligion()+"','"+personLogger.getCaste()+"','"+personLogger.getMaritalstatus()+"',"+
                            "'"+personLogger.getGender()+"','"+personLogger.getLareacode()+"',"+personLogger.isEnable()+","+personLogger.getCuser()+","+personLogger.getMuser()+","+
                            "'"+personLogger.getCdtz()+"','"+personLogger.getMdtz()+"',"+personLogger.getBuid()+","+personLogger.getClientid()+",'"+personLogger.getLcity()+"','"+personLogger.getNcity()+"',"+personLogger.getLstate()+","+personLogger.getNstate()+",'"+personLogger.getNareacode()+"'"
                            +") returning personloggerid;";


                    System.out.println("Auto Sync personlogger upload data: "+insertQuery);

                    ServerRequest serverRequest=new ServerRequest(context);
                    HttpResponse response=serverRequest.getDeviceEventLogResponse(insertQuery.trim(),
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
                        System.out.println("SB personlogger: " + sb.toString());
                        response.getEntity().consumeContent();

                        CommonFunctions.ResponseLog("\n AutoSync Person logger Event Log Response \n"+personLogger.getPersonloggerid()+"\n"+sb.toString()+"\n");

                        JSONObject ob = new JSONObject(sb.toString());
                        if(ob.getInt(Constants.RESPONSE_RC)==0)
                        {
                            queryStatus=0;
                            long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                            personLoggerDAO.changePeopleLoggerSyncStatus(personLogger.getCdtz(),Constants.SYNC_STATUS_ONE);
                            attachmentDAO.changePersonLogReturnID(String.valueOf(returnidResp),String.valueOf(personLogger.getPersonloggerid()));
                        }
                        else
                        {
                            CommonFunctions.ErrorLog("\n AutoSync Person logger Error: \n"+personLogger.getPersonloggerid()+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n");
                            queryStatus=-1;
                            break;
                        }
                    }
                    else {
                        CommonFunctions.ErrorLog("\n AutoSync Person Error: \n"+personLogger.getPersonloggerid()+"\n"+"Connection not established with server."+"\n");
                        queryStatus=-1;
                        break;
                    }

                }
                catch (Exception e)
                {
                    queryStatus=-1;
                    CommonFunctions.ErrorLog("\n Auto PersonLogger Exception: \n"+e.toString()+" \n");
                }
            }
        }
        else
        {
            queryStatus=0;
        }
        return queryStatus;
    }

    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(integer);
        iUploadPersonLoggerDataListener.finishPersonLoggerUpload(status);
    }
}
