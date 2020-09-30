package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IAsynCompletedListener;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
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
 * Created by youtility on 25/5/18.
 */

public class AddOtherSiteAsyncTask extends AsyncTask<Void, Integer, Integer>
{
    private Context context;
    private IAsynCompletedListener iAsynCompletedListener;
    private String siteName;
    private String siteCode;
    private SharedPreferences loginPref;
    private TypeAssistDAO typeAssistDAO;
    private long returnid=-1;
    private byte[] buffer;
    private InputStream is;
    private StringBuffer sb;
    private int bytesRead;
    public AddOtherSiteAsyncTask(Context context,IAsynCompletedListener iAsynCompletedListener,String siteName,String siteCode)
    {
        this.context=context;
        this.iAsynCompletedListener=iAsynCompletedListener;
        this.siteName=siteName;
        this.siteCode=siteCode;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        typeAssistDAO=new TypeAssistDAO(context);
    }

    //bucode, buname, isvendor, webcapability,mobilecapability, isserviceprovider, parent, identifier, enable,cdtz, mdtz, cuser, reportcapability, muser, iswh
    @Override
    protected Integer doInBackground(Void... params) {
        int status=-1;
        try {
            String insertQuery= DatabaseQuries.BU_INSERT+"( " +
                    "'"+siteCode+"', " +//bucode
                    "'"+siteName+"', " +//buname
                    "'false', " +//isvendor
                    "''," +//webcapability
                    "'',"+//mobilecapability
                    "'TRUE',"+//isserviceprovider
                    loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+", "+//parent
                    typeAssistDAO.getEventTypeID(Constants.OTHER_SITE_CODE, Constants.IDENTIFIER_BU)+", " +//identifier
                    "'True', "+//enable
                    "'"+ CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"','"+//cdtz
                    CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"',"+//mdtz
                    loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+", '"+//cuser
                    "',"+//reportcapability
                    loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+", '"+//muser
                    "False') returning buid;";

            ServerRequest serverRequest=new ServerRequest(context);
            HttpResponse response=serverRequest.getOtherSiteAddLogResponse(insertQuery.trim(),
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
                System.out.println("SB Othersite response log: " + sb.toString());
                response.getEntity().consumeContent();

                JSONObject ob = new JSONObject(sb.toString());
                if(ob.getInt(Constants.RESPONSE_RC)==0)
                {
                    returnid = ob.getLong(Constants.RESPONSE_RETURNID);
                    status= 0;
                }
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
        }
        return  status;
    }

    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(integer);
        iAsynCompletedListener.asyncComplete(false, status,returnid );
    }

}
