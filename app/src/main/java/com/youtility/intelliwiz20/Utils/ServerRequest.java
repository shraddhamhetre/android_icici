package com.youtility.intelliwiz20.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.Model.UploadLoginParameters;
import com.youtility.intelliwiz20.Model.UploadParameters;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;

/**
 * Created by youtility on 23/3/18.
 */

public class ServerRequest {
    private Context context;
    private HttpResponse response;
    private HttpClient client;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationMainPref;
    public ServerRequest(Context context) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException {
        this.context=context;
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 100000);
        HttpConnectionParams.setSoTimeout(params, 100000);
        ConnManagerParams.setMaxTotalConnections(params, 100);
        ConnManagerParams.setTimeout(params, 100000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", socketFactory, 443));
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        client = new DefaultHttpClient(manager, params);


        /*KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        SSLSocketFactory sf = new SSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpParams params1 = new BasicHttpParams();
        HttpProtocolParams.setVersion(params1, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params1, HTTP.UTF_8);

        SchemeRegistry registry1 = new SchemeRegistry();
        registry1.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry1.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry1);

        client=new DefaultHttpClient(ccm, params1);*/
    }

    public HttpResponse getLoginResponse(String loginId, String loginPass, String loginSiteCode)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadLoginParameters uploadParameters=new UploadLoginParameters();
            uploadParameters.setServicename(Constants.SERVICE_LOGIN);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setLoginid(loginId);
            uploadParameters.setPassword(loginPass);
            uploadParameters.setSitecode(loginSiteCode);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Login Upload> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            System.out.println("PostData URL: "+postRequest.getURI().toString());
            System.out.println("PostData: "+postRequest.getParams().toString());
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("getLoginResponse UnsupportedEncodingException: "+e.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            System.out.println("getLoginResponse ClientProtocolException: "+e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("getLoginResponse IOException: "+e.toString());
        }

        return response;
    }

    public HttpResponse getVersionNumber()
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        try {
            UploadLoginParameters uploadParameters=new UploadLoginParameters();
            uploadParameters.setServicename(Constants.SERVICE_LOGIN);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getLogoutResponse(String loginId, String loginPass, String loginSiteCode)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadLoginParameters uploadParameters=new UploadLoginParameters();
            uploadParameters.setServicename(Constants.SERVICE_LOGOUT);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setLoginid(loginId);
            uploadParameters.setPassword(loginPass);
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginSiteCode);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Logout Upload> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getOtherSiteAddLogResponse(String insertQuery, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    public HttpResponse getPeopleEventLogResponse(String peopleEventQuery, String infoParameter, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(peopleEventQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setInfo(infoParameter.trim());
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <People Event Log Upload> \n"+upData+"\n");
            System.out.println("before server request");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);

            System.out.println("after server request");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getDeviceEventLogResponse(String insertQuery, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("DeviceEventLog upData: "+upData);

            //CommonFunctions.UploadLog("\n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getSkipSALogResponse(String insertQuery, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("SkipSAEventLog upData: "+upData);

            //CommonFunctions.UploadLog("\n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getAdhocLogResponse(String insertQuery, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_ADHOC);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setBiodata(insertQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <ADHOC Upload> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getIncidentReportLogResponse(String insertQuery, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_IR_INSERT);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setBiodata(insertQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            System.out.println("Query: "+uploadParameters.getQuery());
            System.out.println("Biodata: "+uploadParameters.getBiodata());

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <IR Or SA Upload> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getJOBUpdateLogResponse(String insertQuery, String bioData, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_TASK_TOUR_UPDATE);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setBiodata(bioData);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <JOB Update Upload> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getReplyLogResponse(String insertQuery, String timezoneoffset, String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(insertQuery);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(timezoneoffset);
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Reply Upload> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getDownloadDataLogResponse(String query, String siteCode, String loginID, String loginPass, int tzOffset)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_SELECT);
            uploadParameters.setQuery(query);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            //uploadParameters.setSitecode(siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            uploadParameters.setTzoffset(String.valueOf(tzOffset));
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            System.out.println("Client code: "+applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,""));
            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Download Data Query> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;


            StringEntity data=new StringEntity(upData,"UTF-8");
            data.setContentType("application/json");
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getReplyHistoryResponse(String query, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_SELECT);
            uploadParameters.setQuery(query);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Reply History Response> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse assetRunningStatusUpdateResponse(String query, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_INSERT);
            uploadParameters.setQuery(query);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(offset));
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Asset Status Update> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getSiteVisitedLogResponse(String query, int offset,String siteCode, String loginID, String loginPass )
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_SELECT);
            uploadParameters.setQuery(query);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            uploadParameters.setTzoffset(String.valueOf(offset));
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <Site Visit Log> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse getUserLocationLogResponse(String query, int offset,String siteCode, String loginID, String loginPass)
    {
        HttpResponse response = null;
        deviceRelatedPref=context.getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);
        applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF, Context.MODE_PRIVATE);
        try {
            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_SELECT);
            uploadParameters.setQuery(query);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+siteCode);
            uploadParameters.setLoginid(loginID);
            uploadParameters.setPassword(loginPass);
            uploadParameters.setTzoffset(String.valueOf(offset));
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            CommonFunctions.UploadLog("\n <User Location Response> \n"+upData+"\n");

            HttpPost postRequest = new HttpPost(Constants.BASE_URL);
            HttpGet getRequest;
            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            postRequest.setEntity(data);
            response = client.execute(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
