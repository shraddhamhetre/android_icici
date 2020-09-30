package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.youtility.intelliwiz20.DataAccessObject.DeviceEventLogDAO;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by youtility4 on 6/10/17.
 *
 * not in used
 */

public class UserStepCountLogAsyntask extends AsyncTask<Void, Integer, Integer>
{   Context context;
    DeviceEventLog deviceEventLog;
    private SharedPreferences loginPref;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private String ids;
    private DeviceEventLogDAO deviceEventLogDAO;

    public UserStepCountLogAsyntask(Context context,DeviceEventLog deviceEventLog,String ids)
    {
        this.context=context;
        this.deviceEventLog=deviceEventLog;
        this.ids=ids;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceEventLogDAO=new DeviceEventLogDAO(context);
    }


    @Override
    protected Integer doInBackground(Void... voids) {
        int status=-1;

        if(deviceEventLog!=null)
        {
            try {
                String insertQuery= DatabaseQuries.DEVICE_EVENTLOG_INSERT+"( "+deviceEventLog.getDeviceid()+", '"+deviceEventLog.getEventvalue()+"', '"+deviceEventLog.getGpslocation()+"', "+deviceEventLog.getAccuracy()+"," +
                        deviceEventLog.getAltitude()+", '"+deviceEventLog.getBatterylevel()+"', '"+deviceEventLog.getSignalstrength()+"', '"+deviceEventLog.getAvailextmemory()+"', '"+deviceEventLog.getAvailintmemory()+"', "+
                        "'"+deviceEventLog.getCdtz()+"','"+deviceEventLog.getMdtz()+"', "+deviceEventLog.getCuser()+","+deviceEventLog.getEventtype()+","+deviceEventLog.getMuser()+","+
                        deviceEventLog.getPeopleid()+",'"+deviceEventLog.getSignalbandwidth()+"',"+deviceEventLog.getBuid()+",'"+deviceEventLog.getAndroidosversion()+"','"+deviceEventLog.getApplicationversion()+"'," +
                        "'"+deviceEventLog.getModelname()+"','"+deviceEventLog.getInstalledapps()+"','"+deviceEventLog.getSimserialnumber()+"','"+deviceEventLog.getLinenumber()+"','"+deviceEventLog.getNetworkprovidername()+"','"+deviceEventLog.getStepCount()+"') returning deviceeventlogid;";

                System.out.println("step count log"+ insertQuery);
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
                    System.out.println("SB DeviceStepCountLog: " + sb.toString());
                    response.getEntity().consumeContent();

                    JSONObject ob = new JSONObject(sb.toString());
                    if(ob.getInt(Constants.RESPONSE_RC)==0)
                    {
                        status=0;
                        deviceEventLogDAO.deleteStepCountRec(ids);
                    }
                    else
                    {
                        status=-1;
                    }
                }
                else {

                    System.out.println("SB1 DeviceStepCountLog ERROR ");
                    status=-1;
                }


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
        }
        else
        {
            status=0;
        }


        return status;
    }

    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(status);
    }

}
