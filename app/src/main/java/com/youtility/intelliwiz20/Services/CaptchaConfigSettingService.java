package com.youtility.intelliwiz20.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.CaptchaConfigSetting;
import com.youtility.intelliwiz20.Tables.CaptchaConfig_Table;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CaptchaConfigSettingService extends Service {
    // TODO: Rename actions, choose action names that describe tasks that this
    private static final String ACTION_RESCHEDULE = "service.intent.action.SERVICE_RESCHEDULE";
    private CheckNetwork checkNetwork;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    private SQLiteDatabase db = null;
    private DatabaseUtils.InsertHelper helper;
    private Context context;

    public CaptchaConfigSettingService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkNetwork=new CheckNetwork(CaptchaConfigSettingService.this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent();
        i.setClassName(getApplicationContext(), "CaptchaConfigSettingService");
        i.setAction(ACTION_RESCHEDULE);
        pi = PendingIntent.getService(getBaseContext(), 0, i, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        new Asynctask().execute();


        int interval=(60*1000);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, pi);


        return Service.START_NOT_STICKY;
    }

    private class Asynctask extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("captcha service");
            getUpdatedCaptchaConfig();
            return null;
        }
    }

    private void getUpdatedCaptchaConfig()
    {
        SharedPreferences loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        SharedPreferences devicePref=getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        InputStream is;
        if(devicePref.getString(Constants.DEVICE_IMEI,"") == "-1"){
                System.out.println("====capta deviceid-1");
        }else{
            try {
                ServerRequest serverRequest=new ServerRequest(CaptchaConfigSettingService.this);
                HttpResponse response=serverRequest.getDownloadDataLogResponse(CommonFunctions.getCaptchaConfigQuery( CaptchaConfigSettingService.this),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""),
                        loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));

                if(response!=null && response.getStatusLine().getStatusCode()== HttpsURLConnection.HTTP_OK)
                {
                    is = response.getEntity().getContent();

                    BufferedReader in=new BufferedReader(new InputStreamReader(is));
                    //StringBuffer sb = new StringBuffer("");
                    StringBuilder sb=new StringBuilder("");

                    while (true) {
                        String line = in.readLine();
                        if (line == null)
                            break;
                        sb.append(line);
                    }

                    in.close();

                    System.out.println("SB Length captcha: "+sb.toString().length());

                    CommonFunctions.DownloadedDataLog(sb.toString()+"\n");

                    JSONObject ob = new JSONObject(sb.toString());

                    System.out.println("SB Length captcha: "+ob.getInt(Constants.RESPONSE_RC));
                    devicePref.getString(Constants.DEVICE_IMEI,"");
                    if(devicePref.getString(Constants.DEVICE_IMEI,"") == "-1"){
                        System.out.println("DEVICE_IMEI:::::::dont upload"+devicePref.getString(Constants.DEVICE_IMEI,""));
                    }
                    else {
                        System.out.println("DEVICE_IMEI:::::::"+devicePref.getString(Constants.DEVICE_IMEI,""));
                    }

                    if(ob.getInt(Constants.RESPONSE_RC)==0)
                    {
                        int totalRows = ob.getInt(Constants.RESPONSE_NROW);
                        if(totalRows>0)
                        {
                            updateData(ob);
                        }
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
            } finally {
            }

        }



    }

    private void updateData (JSONObject ob)
    {
        JSONObject dataObject ;
       // DatabaseUtils.InsertHelper helper;
        Type listType;
        ArrayList<CaptchaConfigSetting> captchaConfigSettings=new ArrayList<>();
       // dataObject = new JSONObject();
        dataObject=getDataObject(ob);
        System.out.println("data object:"+ dataObject);
        Gson gson;

        db = SqliteOpenHelper.getInstance(context).getWritableDatabase();

        try {
            helper = new DatabaseUtils.InsertHelper(db, CaptchaConfig_Table.TABLE_NAME);



            final int capConfigEnableColumn = helper.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_ENABLE);
            final int capConfigStartColumn = helper.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_STARTTIME);
            final int capConfigEndColumn = helper.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_ENDTIME);
            final int capConfigFreqColumn = helper.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_FREQUENCY);

            gson=new Gson();

            JSONArray data = dataObject.getJSONArray("Data");
            listType = new TypeToken<ArrayList<CaptchaConfigSetting>>() {
            }.getType();
            captchaConfigSettings = gson.fromJson(data.toString(), listType);

            if(captchaConfigSettings!=null && captchaConfigSettings.size()>0)
            {
                System.out.println("------------------------------------------------------Captcha config Setting count: "+captchaConfigSettings.size());


                for (CaptchaConfigSetting captchaConfigSetting : captchaConfigSettings) {
                    helper.prepareForReplace();

                    System.out.println("captchaConfigSetting.getStarttime(): "+captchaConfigSetting.getStarttime());
                    System.out.println("captchaConfigSetting.getEndtime(): "+captchaConfigSetting.getEndtime());
                    System.out.println("captchaConfigSetting.getCaptchafreq(): "+captchaConfigSetting.getCaptchafreq());

                    helper.bind(capConfigEnableColumn, captchaConfigSetting.isEnablesleepingguard());
                    helper.bind(capConfigStartColumn, captchaConfigSetting.getStarttime());
                    helper.bind(capConfigEndColumn, captchaConfigSetting.getEndtime());
                    helper.bind(capConfigFreqColumn, captchaConfigSetting.getCaptchafreq());
                    helper.execute();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(captchaConfigSettings!= null && captchaConfigSettings.size()>0)
                captchaConfigSettings.clear();
            captchaConfigSettings=null;
        }
    }

    private JSONObject getDataObject(JSONObject ob)
    {
        System.out.println("getDataObject done");
        JSONObject dataObject = new JSONObject();
        try {
            String resp=ob.getString(Constants.RESPONSE_ROWDATA);
            String colums=ob.getString(Constants.RESPONSE_COLUMNS);
            //System.out.println("status: "+status);
            String mainSplitRowChar=String.valueOf(resp.charAt(0));
            String mainSplitColumnChar=String.valueOf(colums.charAt(0));

            if(mainSplitRowChar.trim().equalsIgnoreCase("|"))
            {
                mainSplitRowChar="\\|";
            }
            else if(mainSplitRowChar.trim().equalsIgnoreCase("$"))
            {
                mainSplitRowChar="\\$";
            }

            if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                mainSplitColumnChar = "\\|";
            } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                mainSplitColumnChar = "\\$";
            }



            String[] cols=colums.split(mainSplitColumnChar);
            String[] responseSplit = resp.split(mainSplitRowChar);

            JSONArray dataArray = new JSONArray();


            for (int i = 1; i < (responseSplit.length); i++) {
                //System.out.println("split string: " + responseSplit[i].toString());
                //System.out.println("split string number: "+i);
                if (responseSplit[i].trim().length() > 0) {
                    Character startDelimitor = responseSplit[i].charAt(0);
                    //System.out.println("Start Delimeter: " + startDelimitor);
                    String[] respRow = null;
                    if (startDelimitor.toString().equalsIgnoreCase("$")) {
                        respRow = responseSplit[i].trim().split("\\$");
                    } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                    {
                        respRow = responseSplit[i].trim().split("\\|");
                    }
                    else {
                        respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                    }


                    if (respRow != null && respRow.length > 0) {

                        JSONObject jsonObject=new JSONObject();
                        for(int c=1;c<respRow.length;c++) {
                            jsonObject.put(cols[c],respRow[c]);
                        }
                        dataArray.put(jsonObject);

                    }

                }
            }

            dataObject.put("Data", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(" return getDataObject done"+dataObject);
        return dataObject;
    }
}
