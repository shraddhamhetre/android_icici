package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.AsyncTask.EmailReadingDataLogFileAsynTask;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.CommonDAO;
import com.youtility.intelliwiz20.DataAccessObject.DeviceEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.PersonLoggerDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Address;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.AssignedSitesPeoples;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.AttendanceHistory;
import com.youtility.intelliwiz20.Model.BiodataParameters;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Model.Geofence;
import com.youtility.intelliwiz20.Model.Group;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.Othersite;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.PeopleGroupBelonging;
import com.youtility.intelliwiz20.Model.PersonLogger;
import com.youtility.intelliwiz20.Model.Question;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.QuestionSetBelonging;
import com.youtility.intelliwiz20.Model.QuestionSetLevel_Two;
import com.youtility.intelliwiz20.Model.SiteList;
import com.youtility.intelliwiz20.Model.Sites;
import com.youtility.intelliwiz20.Model.SitesInformation;
import com.youtility.intelliwiz20.Model.TemplateList;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Model.UploadIncidentReportParameter;
import com.youtility.intelliwiz20.Model.UploadInfoParameter;
import com.youtility.intelliwiz20.Model.UploadJobneedParameter;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.Address_Table;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;
import com.youtility.intelliwiz20.Tables.AssignedSitePeople_Table;
import com.youtility.intelliwiz20.Tables.AttendanceHistoy_Table;
import com.youtility.intelliwiz20.Tables.Geofence_Table;
import com.youtility.intelliwiz20.Tables.Group_Table;
import com.youtility.intelliwiz20.Tables.JOBNeedDetails_Table;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Tables.PeopleGroupBelongin_Table;
import com.youtility.intelliwiz20.Tables.People_Table;
import com.youtility.intelliwiz20.Tables.QuestionSetBelonging_Table;
import com.youtility.intelliwiz20.Tables.QuestionSet_Table;
import com.youtility.intelliwiz20.Tables.Question_Table;
import com.youtility.intelliwiz20.Tables.SiteList_Table;
import com.youtility.intelliwiz20.Tables.SitesInfo_Table;
import com.youtility.intelliwiz20.Tables.TemplateList_Table;
import com.youtility.intelliwiz20.Tables.TypeAssist_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import static com.youtility.intelliwiz20.Utils.CommonFunctions.getTimezoneDate;

public class SyncronizationViewActivity extends Activity {
    private ProgressBar progressBar;
    private TextView progressCount;
    private LinearLayout activityLayout;

    private SharedPreferences loginPref;

    private ProgressBar progressBar2;
    private TextView progressCount2;

    private TextView titleTextview, titleTextview1, titleTextview2;
    private ImageView logoImageView;
    private Button syncStatusButton, syncReportButton;
    private TextView errorLogView;
    private StringBuilder errorLogBuilder;

    private PeopleEventLogDAO peopleEventLogDAO;
    private TypeAssistDAO typeAssistDAO;
    private AttachmentDAO attachmentDAO;
    private JobNeedDAO jobNeedDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private DeviceEventLogDAO deviceEventLogDAO;
    private PersonLoggerDAO personLoggerDAO;
    private AssetDAO assetDAO;

    private SharedPreferences applicationPref;
    private SharedPreferences autoSyncPref;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;

    private ArrayList<PeopleEventLog>peopleEventLogArrayList;
    private ArrayList<DeviceEventLog>deviceEventLogArrayList;
    private ArrayList<JobNeed>adhocJobArrayList;
    private ArrayList<JobNeed>incidentReportArrayList;
    private ArrayList<JobNeed>jobUpdateArrayList;
    private ArrayList<PersonLogger>personLoggerArrayList;
    private ArrayList<Attachment>attachmentArrayList;

    private UploadJobneedParameter uploadJobneedParameter;

    private DateFormat dateFormat;
    String peoplegetquery=null;
    String infoParameter=null;
    Gson gson ;
    private URL url;
    private HttpURLConnection connection;
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;

    InputStream is;
    StringBuffer sb;
    private int pCount=0;

    private DatabaseUtils.InsertHelper helper;
    private Type listType;
    private SQLiteDatabase db = null;

    private boolean isCompleted=true;
    private boolean isUploadCompleted=true;
    private boolean isSyncInProgress=false;

    private SharedPreferences otherSiteListPref;
    private SharedPreferences syncPref;
    private SharedPreferences syncSummaryPref;
    private SharedPreferences syncOffsetPref;

    private int adhocCountSucceed=0;
    private int adhocCountFailed=0;

    private int irCountSucceed=0;
    private int irCountFailed=0;

    private int jnupdateCountSucceed=0;
    private int jnupdateCountFailed=0;

    private int emprefCountSucceed=0;
    private int emprefCountFailed=0;

    private int saCountSucceed=0;
    private int saCountFailed=0;

    private int syncFailedRecordCount=-1;

    private CommonDAO commonDAO;
    Context context;



    enum SyncCount
    {
        ASSET,JN, JND, TA, GF, PEOPLE, GROUP, ATTHISTORY,QUEST,QSET,QSB,PGB,SP,TICKET, ADDRESS,OTHERSITE, SITEINFO, TEMPLATE, SITEPEOPLE
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_syncronization_view);

        errorLogBuilder=new StringBuilder();

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        otherSiteListPref=getSharedPreferences(Constants.OTHER_SITE_LIST_PREF, MODE_PRIVATE);
        syncPref=getSharedPreferences(Constants.SYNC_PREF, MODE_PRIVATE);
        syncSummaryPref=getSharedPreferences(Constants.SYNC_SUMMARY_PREF, MODE_PRIVATE);
        syncOffsetPref=getSharedPreferences(Constants.SYNC_OFFSET_PREF, MODE_PRIVATE);

        syncOffsetPref.edit().putLong(Constants.SYNC_TICKET_TIMESTAMP, 0).apply();

        peopleEventLogDAO=new PeopleEventLogDAO(SyncronizationViewActivity.this);
        typeAssistDAO=new TypeAssistDAO(SyncronizationViewActivity.this);
        attachmentDAO=new AttachmentDAO(SyncronizationViewActivity.this);
        deviceEventLogDAO=new DeviceEventLogDAO(SyncronizationViewActivity.this);
        jobNeedDAO=new JobNeedDAO(SyncronizationViewActivity.this);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(SyncronizationViewActivity.this);
        personLoggerDAO=new PersonLoggerDAO(SyncronizationViewActivity.this);
        assetDAO=new AssetDAO(SyncronizationViewActivity.this);

        applicationPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);
        autoSyncPref=getSharedPreferences(Constants.AUTO_SYNC_PREF,MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);

        commonDAO = new CommonDAO(SyncronizationViewActivity.this);


        gson = new Gson();

        activityLayout=(LinearLayout)findViewById(R.id.activityLayout);

        progressBar=(ProgressBar)findViewById(R.id.progressBar1);
        progressBar.setMax(1000);
        Resources res = getResources();
        progressBar.setProgressDrawable(res.getDrawable( R.drawable.progress));

        //progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.button_background), PorterDuff.Mode.SRC_IN);

       /* Drawable bgDrawable = progressBar.getProgressDrawable();
        bgDrawable.setColorFilter(getResources().getColor(R.color.coral_blue), PorterDuff.Mode.DARKEN);
        progressBar.setProgressDrawable(bgDrawable);*/

        progressCount=(TextView)findViewById(R.id.progressCount);

        progressBar2=(ProgressBar)findViewById(R.id.progressBar2);
        progressBar2.setMax(18);
        Resources res1 = getResources();
        progressBar2.setProgressDrawable(res1.getDrawable( R.drawable.progress));
        //progressBar2.getProgressDrawable().setColorFilter(getResources().getColor(R.color.button_background), PorterDuff.Mode.SRC_IN);

        /*Drawable bgDrawable1 = progressBar2.getProgressDrawable();
        bgDrawable1.setColorFilter(getResources().getColor(R.color.coral_blue), PorterDuff.Mode.DARKEN);
        progressBar2.setProgressDrawable(bgDrawable1);*/

        progressCount2=(TextView)findViewById(R.id.progressCount2);

        titleTextview=(TextView)findViewById(R.id.textView) ;
        titleTextview1=(TextView)findViewById(R.id.textView1) ;
        titleTextview2=(TextView)findViewById(R.id.textView2) ;

        logoImageView=(ImageView)findViewById(R.id.logoImageView);

        if(loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE,"").contains("ALSTOM"))
            logoImageView.setImageResource(R.drawable.alstom);
        else
            logoImageView.setImageResource(R.drawable.youtility_logo);

        //titleTextview.setText(getResources().getString(R.string.sync_dialog_title)+" \n"+getResources().getString(R.string.server_data_sync));
        errorLogView=(TextView)findViewById(R.id.errorInfoView);
        errorLogView.setVisibility(View.GONE);

        syncReportButton=(Button)findViewById(R.id.syncReportButton);
        syncReportButton.setVisibility(View.GONE);
        syncReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailReadingDataLogFileAsynTask emailReadingDataLogFileAsynTask = new EmailReadingDataLogFileAsynTask(SyncronizationViewActivity.this, "info@youtility.in");
                emailReadingDataLogFileAsynTask.execute();
            }
        });

        syncStatusButton=(Button)findViewById(R.id.syncStatusButton);
        syncStatusButton.setText(getResources().getString(R.string.sync_dialog_syncinprogress));
        syncStatusButton.setEnabled(false);
        syncStatusButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        syncStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                syncPref.edit().putBoolean(Constants.SYNC_MANUAL_RUNNING,false).apply();
                syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_ADHOC_COUNT,adhocCountSucceed+":"+adhocCountFailed).apply();
                syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_IR_COUNT,irCountSucceed+":"+irCountFailed).apply();
                syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_JNUPDATE_COUNT,jnupdateCountSucceed+":"+jnupdateCountFailed).apply();
                syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_EMPREF_COUNT,emprefCountSucceed+":"+emprefCountFailed).apply();
                syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_SA_COUNT, saCountSucceed+":"+saCountFailed).apply();
                isSyncInProgress=false;
                setResult(RESULT_OK);
                finish();
            }
        });

        if(!isSyncInProgress) {

            syncFailedRecordCount=0;

            isSyncInProgress=true;
            syncPref.edit().putBoolean(Constants.SYNC_MANUAL_RUNNING,true).apply();

            SqliteOpenHelper sqlopenHelper=SqliteOpenHelper.getInstance(SyncronizationViewActivity.this);
            db=sqlopenHelper.getDatabase();

            PELogAsyntask peLogAsyntask = new PELogAsyntask();//for people event log
            peLogAsyntask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("IN Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("IN Pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("IN Resume");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("IN Restart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("IN Restart");
    }

    private void syncFailedAlert()
    {
        syncReportButton.setVisibility(View.VISIBLE);
        if(errorLogBuilder.toString().trim().length()>0)
        {
            errorLogView.setVisibility(View.VISIBLE);
            errorLogView.setText(errorLogBuilder.toString().trim());
        }
        syncStatusButton.setEnabled(true);
        syncStatusButton.setText(getResources().getString(R.string.sync_dialog_failed_msg));
        syncStatusButton.setBackground(getResources().getDrawable(R.drawable.rounder_delete_button));
        titleTextview.setText(getResources().getString(R.string.sync_dialog_title));
        loginPref.edit().putBoolean(Constants.IS_SYNC_DONE,false).apply();
        isSyncInProgress=true;
    }

    private class PELogAsyntask extends AsyncTask<Void, Integer, Void>
    {
        private PeopleEventLog peopleEventLog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            peopleEventLogArrayList=new ArrayList<>();
            peopleEventLogArrayList=peopleEventLogDAO.getEvents();
            dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            titleTextview1.setText(getResources().getString(R.string.sync_dialog_upload_msg));
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(peopleEventLogArrayList!=null && peopleEventLogArrayList.size()>0)
            {
                System.out.println("peopleEventLogArrayList: "+peopleEventLogArrayList.size());

                for(int i =0;i<peopleEventLogArrayList.size();i++)
                {
                    pCount++;
                    publishProgress(pCount);
                    try {
                        peopleEventLog=new PeopleEventLog();
                        peopleEventLog=peopleEventLogArrayList.get(i);

                        url = new URL(Constants.BASE_URL);
                        connection = (HttpURLConnection) url.openConnection();

                        peoplegetquery=peopleEventLog.getPeopleid()+"";
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
                        //accuracy, datetime, gpslocation, photorecognitionthreshold, photorecognitionscore, photorecognitiontimestamp, " +
                        //"photorecognitionserviceresponse, facerecognition, peopleid, peventtype, punchstatus, verifiedby, buid, cuser, muser, cdtz, mdtz,
                        // gfid, deviceid, transportmode, expamt, duration, reference, remarks, distance
                        String peopleEventQuery= DatabaseQuries.PEOPLE_EVENTLOG_INSERT+
                                "(" +
                                peopleEventLog.getAccuracy()+"," +//accuracy
                                "'"+ getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"'," +//datetime
                                "'"+peopleEventLog.getGpslocation()+"'," +//gpslocation
                                "-1,"+//photorecognitionthreshold
                                "-1,"+//photorecognitionscore
                                "now(),"+//photorecognitiontimestamp
                                "null,"+//photorecognitionserviceresponse
                                "false,"+//facerecognition
                                peoplegetquery+"," +//peopleid
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

                        /*String peopleEventQuery= DatabaseQuries.PEOPLE_EVENTLOG_INSERT+
                                "(" +
                                peopleEventLog.getAccuracy()+"," +//accuracy
                                "'"+CommonFunctions.getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"'," +//datetime
                                "'"+peopleEventLog.getGpslocation()+"'," +//gpslocation
                                "-1,"+//photorecognitionthreshold
                                "-1,"+//photorecognitionscore
                                "now(),"+//photorecognitiontimestamp
                                "null,"+//photorecognitionserviceresponse
                                "false,"+//facerecognition
                                peoplegetquery+"," +//peopleid
                                peopleEventLog.getPeventtype()+"," +//peventtype
                                peopleEventLog.getPunchstatus()+","+//punchstatus
                                peopleEventLog.getVerifiedby()+","+//verifiedby
                                peopleEventLog.getBuid()+","+//siteid
                                peopleEventLog.getCuser()+","+//cuser
                                peopleEventLog.getMuser()+",'"+//muser
                                peopleEventLog.getCdtz()+"','" +//cdtz
                                peopleEventLog.getMdtz()+"'," +//mdtz
                                peopleEventLog.getGfid()+","+//gfid
                                peopleEventLog.getDeviceid()+","+//deviceid
                                peopleEventLog.getTransportmode()+","+
                                peopleEventLog.getExpamt()+","+
                                peopleEventLog.getDuration()+","+
                                "'"+peopleEventLog.getReference()+"',"+
                                "'"+CommonFunctions.escapeMetaCharacters(peopleEventLog.getRemarks().trim())+"',"+
                                peopleEventLog.getDistance()+","+
                                "'"+CommonFunctions.escapeMetaCharacters(peopleEventLog.getRemarks().trim())+"'"+
                                ") returning pelogid;";*/


                        /*dateFormat.format(new Date(Long.valueOf(peopleEventLog.getCdtz())))+"','" +//cdtz
                                dateFormat.format(new Date(Long.valueOf(peopleEventLog.getMdtz())))+"'," +//mdtz*/
                        System.out.println("peopleEventQuery: "+peopleEventQuery);
                        CommonFunctions.manualSyncReadingLog("PeopleEventLog",peopleEventQuery.trim(), getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime())));

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getPeopleEventLogResponse(peopleEventQuery,infoParameter.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("PeopleEventLog response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            CommonFunctions.manualSyncReadingLog("PeopleEventLogResponse",sb.toString().trim(), getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime())));
                            CommonFunctions.ResponseLog("\n <People Event Log Response> \n"+ getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"\n"+sb.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                peopleEventLogDAO.changeSyncStatus(String.valueOf(peopleEventLog.getDatetime()));
                                //attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnidResp),String.valueOf(peopleEventLog.getDatetime()));
                                attachmentDAO.changePeopleEventLogReturnID(String.valueOf(peopleEventLog.getReference()),String.valueOf(peopleEventLog.getDatetime()));

                                isUploadCompleted=true;
                            }
                            else
                            {
                                errorLogBuilder.append("\n PeopleEvent: "+ob.getString(Constants.RESPONSE_MSG));
                                CommonFunctions.ErrorLog("\n <People event log Error> \n"+ getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"\n"+sb.toString()+" \n ");
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                //break;
                            }
                        }
                        else {
                            errorLogBuilder.append("\n PeopleEvent: "+response.getStatusLine().getStatusCode());
                            CommonFunctions.ErrorLog("\n <People event log Error> \n"+ getTimezoneDate(Long.valueOf(peopleEventLog.getDatetime()))+"\n");
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            //break;
                        }


                        /*else
                        {
                            db.execSQL("delete from "+ PeopleEventLog_Table.TABLE_NAME+" where "+PeopleEventLog_Table.PE_DATETIME+" ='"+peopleEventLog.getDatetime()+"'");
                        }*/


                        /*UploadParameters uploadParameters=new UploadParameters();
                        uploadParameters.setServicename(Constants.SERVICE_INSERT);
                        uploadParameters.setQuery(peopleEventQuery);
                        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
                        uploadParameters.setInfo(infoParameter.trim());


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
                        if(ob.getInt(Constants.RESPONSE_RC)==0)
                        {
                            long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                            peopleEventLogDAO.changeSyncStatus(String.valueOf(peopleEventLog.getDatetime()));
                            attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnidResp),String.valueOf(peopleEventLog.getDatetime()));
                        }
                        else
                        {
                            db.execSQL("delete from "+ PeopleEventLog_Table.TABLE_NAME+" where "+PeopleEventLog_Table.PE_DATETIME+" ='"+peopleEventLog.getDatetime()+"'");
                        }*/

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
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("people event log isUploadCompleted: "+isUploadCompleted);
            /*if(isUploadCompleted)
            {
                System.out.println("ADHOCLogAsyntask started...");
                ADHOCLogAsyntask adhocLogAsyntask = new ADHOCLogAsyntask();//for adhoc job , tour, ticket
                adhocLogAsyntask.execute();
            }
            else
            {
                syncFailedAlert();
            }*/
            if(isUploadCompleted)
            {
                System.out.println("Deviceevent log started...");
                DELogAsyntask deLogAsyntask = new DELogAsyntask();//for device event log
                deLogAsyntask.execute();
            }
            else
            {
                syncFailedAlert();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressCount.setText(progressBar.getProgress()+"/"+progressBar.getMax());
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }


    }


    private class DELogAsyntask extends AsyncTask<Void , Integer, Void>
    {
        private DeviceEventLog deviceEventLog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //deviceEventLogDAO.deleteRec();
            deviceEventLogArrayList=new ArrayList<DeviceEventLog>();
            deviceEventLogArrayList=deviceEventLogDAO.getUnsyncDeviceEventsLogs(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
        }

        @Override
        protected Void doInBackground(Void... params) {

            //System.out.println("deviceEventLogArrayList: "+deviceEventLogArrayList.size());
            if(deviceEventLogArrayList!=null && deviceEventLogArrayList.size()>0)
            {

                for(int i=0;i<deviceEventLogArrayList.size();i++)
                {
                    System.out.println("deviceEventLogNumber: "+i);
                    // System.out.println("deviceEventLogNumber: "+deviceEventLog.getDeviceeventlogid());
                    try {
                        pCount++;
                        publishProgress(pCount);
                        deviceEventLog=new DeviceEventLog();
                        deviceEventLog=deviceEventLogArrayList.get(i);
                        url = new URL(Constants.BASE_URL);


                        String insertQuery= DatabaseQuries.DEVICE_EVENTLOG_INSERT+"( '"+deviceEventLog.getDeviceid()+"' , '"+deviceEventLog.getEventvalue()+"', '"+deviceEventLog.getGpslocation()+"', "+deviceEventLog.getAccuracy()+"," +
                                deviceEventLog.getAltitude()+", '"+deviceEventLog.getBatterylevel()+"', '"+deviceEventLog.getSignalstrength()+"', '"+deviceEventLog.getAvailextmemory()+"', '"+deviceEventLog.getAvailintmemory()+"', "+
                                "'"+deviceEventLog.getCdtz()+"','"+deviceEventLog.getMdtz()+"', "+deviceEventLog.getCuser()+","+deviceEventLog.getEventtype()+","+deviceEventLog.getMuser()+","+
                                deviceEventLog.getPeopleid()+",'"+deviceEventLog.getSignalbandwidth()+"',"+deviceEventLog.getBuid()+",'"+deviceEventLog.getAndroidosversion()+"','"+deviceEventLog.getApplicationversion()+"'," +
                                "'"+deviceEventLog.getModelname()+"','"+deviceEventLog.getInstalledapps()+"','"+deviceEventLog.getSimserialnumber()+"','"+deviceEventLog.getLinenumber()+"','"+deviceEventLog.getNetworkprovidername()+"','"+deviceEventLog.getStepCount()+"') returning deviceeventlogid;";

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getDeviceEventLogResponse(insertQuery.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("DeviceEventLog response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB DeviceEventLog: " + sb.toString());
                            response.getEntity().consumeContent();

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                deviceEventLogDAO.changeSyncStatus(deviceEventLog.getCdtz());
                                isUploadCompleted=true;
                            }
                            else {
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                errorLogBuilder.append("\n DeviceEvent: "+ob.getString(Constants.RESPONSE_MSG));
                            }
                        }
                        else {
                            errorLogBuilder.append("\n DeviceEvent: "+response.getStatusLine().getStatusCode());
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            System.out.println("SB1 DeviceEventLog: ERROR");
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isUploadCompleted)
            {
                System.out.println("ADHOCLogAsyntask started...");
                ADHOCLogAsyntask adhocLogAsyntask = new ADHOCLogAsyntask();//for adhoc job , tour, ticket
                adhocLogAsyntask.execute();
            }
            else
            {
                syncFailedAlert();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressCount.setText(progressBar.getProgress()+"/"+progressBar.getMax());
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }


    }

    private class ADHOCLogAsyntask extends AsyncTask<Void, Integer, Void>
    {
        private JobNeed jobNeed;

        private ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adhocJobArrayList=new ArrayList<>();
            adhocJobArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"+Constants.JOB_NEED_IDENTIFIER_TASK+"','"
                    +Constants.JOB_NEED_IDENTIFIER_TICKET+"','"+Constants.JOB_NEED_IDENTIFIER_ASSET+"','"
                    +Constants.JOB_NEED_IDENTIFIER_ASSET_LOG+"','"+Constants.JOB_NEED_IDENTIFIER_ASSET_AUDIT+"','"
                    +Constants.JOB_NEED_IDENTIFIER_INTERNAL_REQUEST+"'",0);
            System.out.println("adhocJobArrayList: "+adhocJobArrayList.size());

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(adhocJobArrayList!=null && adhocJobArrayList.size()>0)
            {
                for(int i=0;i<adhocJobArrayList.size();i++)
                {
                    try {
                        pCount++;
                        publishProgress(pCount);

                        jobNeed=adhocJobArrayList.get(i);

                        jobNeedDetailsArrayList=new ArrayList<>();
                        jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeed.getJobneedid());
                        uploadJobneedParameter=new UploadJobneedParameter();
                        uploadJobneedParameter.setJobdesc(jobNeed.getJobdesc());
                        uploadJobneedParameter.setAatop(jobNeed.getAatop());
                        uploadJobneedParameter.setAssetid(jobNeed.getAssetid());
                        //uploadJobneedParameter.setAssetid(-1);
                        uploadJobneedParameter.setCuser(jobNeed.getCuser());
                        uploadJobneedParameter.setFrequency(jobNeed.getFrequency());
                        uploadJobneedParameter.setPlandatetime((jobNeed.getPlandatetime()));
                        uploadJobneedParameter.setExpirydatetime((jobNeed.getExpirydatetime()));
                        uploadJobneedParameter.setGracetime(jobNeed.getGracetime());
                        uploadJobneedParameter.setGroupid(jobNeed.getGroupid());
                        uploadJobneedParameter.setIdentifier(jobNeed.getIdentifier());
                        if(jobNeed.getJobid()==0)
                            uploadJobneedParameter.setJobid(-1);
                        else
                            uploadJobneedParameter.setJobid(jobNeed.getJobid());
                        uploadJobneedParameter.setJobneedid(jobNeed.getJobneedid());
                        uploadJobneedParameter.setJobstatus(jobNeed.getJobstatus());
                        uploadJobneedParameter.setJobtype(jobNeed.getJobtype());
                        uploadJobneedParameter.setMuser(jobNeed.getMuser());
                        uploadJobneedParameter.setParent(jobNeed.getParent());
                        uploadJobneedParameter.setPeopleid(jobNeed.getPeopleid());
                        if(jobNeed.getPerformedby()==0)
                            uploadJobneedParameter.setPerformedby(-1);
                        else
                            uploadJobneedParameter.setPerformedby(jobNeed.getPerformedby());

                        uploadJobneedParameter.setPriority(jobNeed.getPriority());
                        uploadJobneedParameter.setScantype(jobNeed.getScantype());
                        uploadJobneedParameter.setQuestionsetid(jobNeed.getQuestionsetid());
                        uploadJobneedParameter.setDetails(jobNeedDetailsArrayList);
                        uploadJobneedParameter.setBuid(jobNeed.getBuid());
                        uploadJobneedParameter.setTicketcategory(jobNeed.getTicketcategory());
                        uploadJobneedParameter.setGpslocation(jobNeed.getGpslocation());
                        uploadJobneedParameter.setCtzoffset(jobNeed.getCtzoffset());
                        uploadJobneedParameter.setStarttime(jobNeed.getStarttime());
                        uploadJobneedParameter.setEndtime(jobNeed.getEndtime());
                        uploadJobneedParameter.setMultiplicationfactor(assetDAO.getAssetMFactor(jobNeed.getAssetid()));
                        uploadJobneedParameter.setRemarks(jobNeed.getRemarks());

                        String ss=gson.toJson(uploadJobneedParameter);

                        CommonFunctions.manualSyncReadingLog("ADHOC",ss.trim(),jobNeed.getPlandatetime());

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getAdhocLogResponse(ss.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("ADHOCLogAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB ADHOCLogAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();

                            CommonFunctions.manualSyncReadingLog("ADHOCResponse",sb.toString().trim(),jobNeed.getPlandatetime());
                            CommonFunctions.ResponseLog("\n <ADHOC Event Log Response> \n"+jobNeed.getJobneedid()+"\n"+sb.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                adhocCountSucceed=adhocCountSucceed+1;
                                jobNeedDetailsDAO.deleteRec(adhocJobArrayList.get(i).getJobneedid());
                                jobNeedDAO.changeJobNeedSyncStatus(adhocJobArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ONE);
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changeAdhocReturnID(String.valueOf(returnidResp),String.valueOf(adhocJobArrayList.get(i).getJobneedid()));
                                isUploadCompleted=true;
                            }
                            else
                            {
                                errorLogBuilder.append("\n ADHOC: "+ob.getString(Constants.RESPONSE_MSG));
                                adhocCountFailed=adhocCountFailed+1;
                                CommonFunctions.ErrorLog("\n <ADHOC Error> \n "+jobNeed.getJobneedid()+"\n"+sb.toString()+" \n");
                                jobNeedDAO.changeJobNeedSyncStatus(adhocJobArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ZERO);
                                System.out.println("ADHOC upload found error");
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                //break;
                            }
                        }
                        else {
                            errorLogBuilder.append("\n ADHOC: "+response.getStatusLine().getStatusCode());
                            //System.out.println("SB1 ADHOCLogAsyntask: "+response.getStatusLine().getStatusCode());
                            adhocCountFailed=adhocCountFailed+1;
                            CommonFunctions.ErrorLog("\n <ADHOC Error> \n"+jobNeed.getJobneedid()+"\n");
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            //break;
                        }




                        /*url = new URL(Constants.BASE_URL); // here is your URL path
                        UploadParameters uploadParameters=new UploadParameters();
                        uploadParameters.setServicename(Constants.SERVICE_ADHOC);
                        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
                        uploadParameters.setQuery(ss);
                        uploadParameters.setBiodata(ss);

                        String upData = gson.toJson(uploadParameters);

                        System.out.println("upData: "+upData);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(15000 );
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");


                        StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                        OutputStream out = new BufferedOutputStream(conn.getOutputStream());


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


                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream()));
                            sb = new StringBuffer("");
                            String line = "";

                            while ((line = in.readLine()) != null) {

                                sb.append(line);
                                break;
                            }

                            in.close();
                            System.out.println("SB: " + sb.toString());

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {

                                jobNeedDAO.changeJobNeedSyncStatus(adhocJobArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ONE);
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changeAdhocReturnID(String.valueOf(returnidResp),String.valueOf(adhocJobArrayList.get(i).getJobneedid()));
                            }

                        }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("Adhoc log isUploadCompleted: "+isUploadCompleted);
            if(isUploadCompleted)
            {
                IRLogAsyntask irLogAsyntask = new IRLogAsyntask();// for incident report
                irLogAsyntask.execute();
            }
            else
                syncFailedAlert();


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressCount.setText(progressBar.getProgress()+"/"+progressBar.getMax());
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }


    }


    private class IRLogAsyntask extends AsyncTask<Void, Integer, Void>
    {
        private JobNeed jobNeed;
        private UploadIncidentReportParameter uploadIncidentReportParameter;
        private ArrayList<JobNeed> jobNeedChildArrayList;
        private ArrayList<JobNeedDetails>jobNeedChildDetailsArrayList;
        private QuestionSetLevel_Two questionSetLevelTwo=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            incidentReportArrayList=new ArrayList<>();
            incidentReportArrayList=jobNeedDAO.getUnsyncIRList();
            System.out.println("incidentReportArrayList: "+incidentReportArrayList.size());
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressCount.setText(progressBar.getProgress()+"/"+progressBar.getMax());
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(incidentReportArrayList!=null && incidentReportArrayList.size()>0)
            {
                System.out.println("incidentReportArrayList: "+incidentReportArrayList.size());
                for(int i=0;i<incidentReportArrayList.size();i++)
                {
                    pCount++;
                    publishProgress(pCount);

                    try {
                        jobNeed=new JobNeed();
                        jobNeed=incidentReportArrayList.get(i);
                        ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();

                        jobNeedChildArrayList=new ArrayList<>();
                        jobNeedChildArrayList=jobNeedDAO.getChildCheckPointList(jobNeed.getQuestionsetid());
                        questionSetLevelTwoArrayList=new ArrayList<>();
                        if(jobNeedChildArrayList!=null && jobNeedChildArrayList.size()>0)
                        {
                            System.out.println("jobNeedChildArrayList: "+jobNeedChildArrayList.size());
                            for(int j=0;j<jobNeedChildArrayList.size();j++)
                            {
                                questionSetLevelTwo = new QuestionSetLevel_Two();
                                questionSetLevelTwo.setQuestionsetid(jobNeedChildArrayList.get(j).getQuestionsetid());
                                questionSetLevelTwo.setJobdesc(jobNeedChildArrayList.get(j).getJobdesc());
                                questionSetLevelTwo.setSeqno(jobNeedChildArrayList.get(j).getSeqno());
                                jobNeedChildDetailsArrayList=new ArrayList<>();
                                jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getQuestionsetid());
                                //jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getJobneedid());
                                questionSetLevelTwo.setDetails(jobNeedChildDetailsArrayList);
                                questionSetLevelTwoArrayList.add(questionSetLevelTwo);
                            }
                        }

                        uploadIncidentReportParameter=new UploadIncidentReportParameter();
                        uploadIncidentReportParameter.setJobdesc(jobNeed.getJobdesc());
                        uploadIncidentReportParameter.setAatop(jobNeed.getAatop());
                        uploadIncidentReportParameter.setAssetid(jobNeed.getAssetid());
                        uploadIncidentReportParameter.setCuser(jobNeed.getCuser());
                        uploadIncidentReportParameter.setFrequency(jobNeed.getFrequency());
                        uploadIncidentReportParameter.setPlandatetime(jobNeed.getPlandatetime());
                        uploadIncidentReportParameter.setExpirydatetime(jobNeed.getExpirydatetime());
                        uploadIncidentReportParameter.setGracetime(jobNeed.getGracetime());
                        uploadIncidentReportParameter.setGroupid(jobNeed.getGroupid());
                        uploadIncidentReportParameter.setIdentifier(jobNeed.getIdentifier());
                        uploadIncidentReportParameter.setJobid(jobNeed.getJobid());
                        uploadIncidentReportParameter.setJobneedid(jobNeed.getJobneedid());
                        uploadIncidentReportParameter.setJobstatus(jobNeed.getJobstatus());
                        uploadIncidentReportParameter.setJobtype(jobNeed.getJobtype());
                        uploadIncidentReportParameter.setMuser(jobNeed.getMuser());
                        uploadIncidentReportParameter.setParent(jobNeed.getParent());
                        uploadIncidentReportParameter.setPeopleid(jobNeed.getPeopleid());
                        uploadIncidentReportParameter.setPerformedby(jobNeed.getPerformedby());
                        uploadIncidentReportParameter.setPriority(jobNeed.getPriority());
                        uploadIncidentReportParameter.setScantype(jobNeed.getScantype());
                        uploadIncidentReportParameter.setQuestionsetid(jobNeed.getQuestionsetid());
                        uploadIncidentReportParameter.setBuid(jobNeed.getBuid());
                        uploadIncidentReportParameter.setGpslocation(jobNeed.getGpslocation());
                        uploadIncidentReportParameter.setCdtzoffset(jobNeed.getCtzoffset());
                        uploadIncidentReportParameter.setOthersite(jobNeed.getOthersite());

                        uploadIncidentReportParameter.setChild(questionSetLevelTwoArrayList);

                        String ss=gson.toJson(uploadIncidentReportParameter);
                        System.out.println("Incident report SS: "+ss);

                        CommonFunctions.manualSyncReadingLog("IRLog",ss.trim(),jobNeed.getPlandatetime());

                        //---------------------------------------------------------------------

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getIncidentReportLogResponse(ss.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("IRLogAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB IRLogAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();

                            CommonFunctions.manualSyncReadingLog("IRLogResponse",sb.toString().trim(),jobNeed.getPlandatetime());

                            CommonFunctions.ResponseLog("\n <IR Event Log Response> \n"+jobNeed.getJobneedid()+"\n"+sb.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                irCountSucceed=irCountSucceed+1;
                                jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ONE);
                                if(jobNeedChildArrayList!=null && jobNeedChildArrayList.size()>0)
                                {
                                    for(int cJob=0;cJob<jobNeedChildArrayList.size();cJob++) {
                                        jobNeedDAO.changeJobNeedChildSyncStatus(jobNeedChildArrayList.get(cJob).getQuestionsetid(), Constants.SYNC_STATUS_ONE);
                                    }
                                }
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changePelogReturnID(String.valueOf(returnidResp),(incidentReportArrayList.get(i).getJobneedid()));
                                jobNeedDAO.getCount();
                                isUploadCompleted=true;
                            }
                            else {
                                errorLogBuilder.append("\n Incident: "+ob.getString(Constants.RESPONSE_MSG));
                                irCountFailed=irCountFailed+1;
                                CommonFunctions.ErrorLog("\n <IR Error> \n "+jobNeed.getJobneedid()+"\n"+sb.toString()+" \n");
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                //break;
                            }

                        }
                        else {
                            errorLogBuilder.append("\n Incident: "+response.getStatusLine().getStatusCode());
                            //System.out.println("SB1 IRLogAsyntask: "+response.getStatusLine().getStatusCode());
                            irCountFailed=irCountFailed+1;
                            CommonFunctions.ErrorLog("\n <IR Error> \n"+jobNeed.getJobneedid()+"\n");
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            //break;
                        }



                        //-------------------------------------------------------------------------------------



                        /*url = new URL(Constants.BASE_URL); // here is your URL path
                        UploadParameters uploadParameters=new UploadParameters();
                        uploadParameters.setServicename(Constants.SERVICE_IR_INSERT);
                        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
                        uploadParameters.setQuery(ss);
                        uploadParameters.setBiodata(ss);

                        String upData = gson.toJson(uploadParameters);

                        System.out.println("upData: "+upData);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(15000 );
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");


                        StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                        OutputStream out = new BufferedOutputStream(conn.getOutputStream());


                        is = data.getContent();
                        buffer = new byte[1024];
                        bytesRead= 0;
                        while ((bytesRead = is.read(buffer)) != -1)
                        {
                            out.write(buffer, 0, bytesRead);
                        }
                        out.flush();
                        out.close();
                        is.close();

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream()));
                            sb = new StringBuffer("");
                            String line = "";

                            while ((line = in.readLine()) != null) {

                                sb.append(line);
                                break;
                            }

                            in.close();
                            System.out.println("SB: " + sb.toString());

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ONE);
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changePelogReturnID(String.valueOf(returnidResp),(incidentReportArrayList.get(i).getJobneedid()));

                            }
                        }*/
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            incidentReportArrayList=null;
            if(isUploadCompleted)
            {
                JOBUpdateAsyntask jobUpdateAsyntask = new JOBUpdateAsyntask();// including jobneed and jobneed details in json
                jobUpdateAsyntask.execute();
            }
            else
                syncFailedAlert();
        }
    }

    private class JOBUpdateAsyntask extends AsyncTask<Void, Integer, Void>
    {
        private JobNeed jobNeed;
        private ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
        long atog=-1;
        long atop=-1;
        String alertType=null;
        long alertTo=-1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jobUpdateArrayList=new ArrayList<JobNeed>();
            jobUpdateArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"
                    +Constants.JOB_NEED_IDENTIFIER_TASK+"','"
                    +Constants.JOB_NEED_IDENTIFIER_TICKET+"','"
                    +Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"','"
                    +Constants.JOB_NEED_IDENTIFIER_PPM+"'",2);

            System.out.println("jobNeedArrayList.size(): "+jobUpdateArrayList.size());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(jobUpdateArrayList!=null && jobUpdateArrayList.size()>0)
            {
                for(int i=0;i<jobUpdateArrayList.size();i++)
                {
                    pCount++;
                    publishProgress(pCount);

                    try {
                        jobNeed=new JobNeed();
                        jobNeed=jobUpdateArrayList.get(i);

                        if(jobNeed.getPeopleid()==-1)
                            atop=-1;
                        else
                            atop=jobNeed.getPeopleid();

                        if(jobNeed.getGroupid()==-1)
                            atog=-1;
                        else
                            atog=jobNeed.getGroupid();

                        System.out.println("atop: "+atop);
                        System.out.println("atog: "+atog);

                        if(atog!=-1) {
                            alertTo=atog;
                            alertType="GROUP";
                        }
                        if(atop!=-1)
                        {
                            alertTo=atop;
                            alertType="PEOPLE";
                        }
                        System.out.println("alertTo: "+alertTo);
                        System.out.println("alertType: "+alertType);

                        String dev= "false";
                        System.out.println("jobNeed.getDeviation()"+jobNeed.getDeviation());
                        if(jobNeed.getDeviation() != null && (jobNeed.getDeviation()).equals("1")){
                            dev= "true";
                            System.out.println("dev======"+dev+"="+jobNeed.getDeviation());
                        }else {
                            System.out.println("dev======"+dev+"="+jobNeed.getDeviation());
                        }
                        uploadJobneedParameter=new UploadJobneedParameter();

                        jobNeedDetailsArrayList=new ArrayList<>();

                        jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeed.getJobneedid());

                        uploadJobneedParameter=new UploadJobneedParameter();
                        uploadJobneedParameter.setJobdesc(jobNeed.getJobdesc());
                        uploadJobneedParameter.setAatop(jobNeed.getAatop());
                        uploadJobneedParameter.setAssetid(jobNeed.getAssetid());
                        uploadJobneedParameter.setCuser(jobNeed.getCuser());
                        uploadJobneedParameter.setFrequency(jobNeed.getFrequency());
                        System.out.println("jobNeed.getPlandatetime(): "+jobNeed.getPlandatetime());
                        uploadJobneedParameter.setPlandatetime((jobNeed.getPlandatetime()));
                        uploadJobneedParameter.setExpirydatetime((jobNeed.getExpirydatetime()));
                        uploadJobneedParameter.setGracetime(jobNeed.getGracetime());
                        uploadJobneedParameter.setGroupid(jobNeed.getGroupid());
                        uploadJobneedParameter.setIdentifier(jobNeed.getIdentifier());
                        uploadJobneedParameter.setJobid(jobNeed.getJobid());
                        uploadJobneedParameter.setJobneedid(jobNeed.getJobneedid());
                        uploadJobneedParameter.setJobstatus(jobNeed.getJobstatus());
                        uploadJobneedParameter.setJobtype(jobNeed.getJobtype());
                        uploadJobneedParameter.setMuser(jobNeed.getMuser());
                        uploadJobneedParameter.setParent(jobNeed.getParent());
                        uploadJobneedParameter.setPeopleid(jobNeed.getPeopleid());
                        uploadJobneedParameter.setPerformedby(jobNeed.getPerformedby());
                        uploadJobneedParameter.setPriority(jobNeed.getPriority());
                        uploadJobneedParameter.setScantype(jobNeed.getScantype());
                        uploadJobneedParameter.setQuestionsetid(jobNeed.getQuestionsetid());
                        uploadJobneedParameter.setDetails(jobNeedDetailsArrayList);
                        uploadJobneedParameter.setBuid(jobNeed.getBuid());
                        uploadJobneedParameter.setTicketcategory(jobNeed.getTicketcategory());
                        uploadJobneedParameter.setGpslocation(jobNeed.getGpslocation());
                        uploadJobneedParameter.setStarttime(jobNeed.getStarttime());
                        uploadJobneedParameter.setEndtime(jobNeed.getEndtime());
                        uploadJobneedParameter.setCtzoffset(jobNeed.getCtzoffset());
                        uploadJobneedParameter.setCdtz(jobNeed.getCdtz());
                        uploadJobneedParameter.setMdtz(jobNeed.getMdtz());
                        uploadJobneedParameter.setMultiplicationfactor(jobNeed.getMultiplicationfactor());
                        System.out.println("upload deviation"+ jobNeed.getDeviation());
                        //uploadJobneedParameter.setDeviation(jobNeed.getDeviation().);
                        uploadJobneedParameter.setDeviation(dev);

                        url = new URL(Constants.BASE_URL);
                        String ss=gson.toJson(uploadJobneedParameter);
                        System.out.println("SS: "+ss);

                        BiodataParameters biodataParameters=new BiodataParameters();
                        biodataParameters.setJobneedid(jobNeed.getJobneedid());
                        biodataParameters.setJobdesc(jobNeed.getJobdesc());
                        biodataParameters.setJobstatus(jobNeed.getJobstatus());
                        biodataParameters.setCuser(jobNeed.getCuser());
                        biodataParameters.setRemarks(jobNeed.getRemarks());
                        biodataParameters.setAlertto(alertTo);
                        biodataParameters.setAssigntype(alertType);

                        String bioData=gson.toJson(biodataParameters);
                        System.out.println("bioData: "+bioData);

                        CommonFunctions.manualSyncReadingLog("JOBNeedUpdateLog",bioData.trim(),jobNeed.getPlandatetime());

                        //---------------------------------------------------------------------

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getJOBUpdateLogResponse(ss.trim(), ss.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("JOBUpdateAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB JOBUpdateAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();

                            CommonFunctions.manualSyncReadingLog("JOBNeedUpdateLogResponse",sb.toString().trim(),jobNeed.getPlandatetime());
                            CommonFunctions.ResponseLog("\n <JOB Update Event Log Response> \n"+jobNeed.getJobneedid()+"\n"+sb.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                jnupdateCountSucceed=jnupdateCountSucceed+1;
                                jobNeedDAO.changeJobNeedSyncStatus(jobUpdateArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ONE);
                                isUploadCompleted=true;
                            }
                            else {
                                errorLogBuilder.append("\n Info: "+ob.getString(Constants.RESPONSE_MSG));
                                jnupdateCountFailed=jnupdateCountFailed+1;
                                CommonFunctions.ErrorLog("\n <JOB Update Error> \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+" \n");
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                //break;
                            }
                        }
                        else {
                            errorLogBuilder.append("\n Info: "+response.getStatusLine().getStatusCode());
                            jnupdateCountFailed=jnupdateCountFailed+1;
                            CommonFunctions.ErrorLog("\n <JOB Update Error> \n"+jobNeed.getJobneedid()+"\n");
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            //break;
                        }



                        //-------------------------------------------------------------------------------------


                        /*UploadParameters uploadParameters=new UploadParameters();
                        uploadParameters.setServicename(Constants.SERVICE_TASK_TOUR_UPDATE);
                        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
                        uploadParameters.setQuery(ss);
                        uploadParameters.setBiodata(ss);

                        String upData = gson.toJson(uploadParameters);
                        System.out.println("upData: "+upData);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(15000 *//* milliseconds *//*);
                        conn.setConnectTimeout(15000 *//* milliseconds *//*);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);//Don't use a cached Copy
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");

                        StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                        OutputStream out = new BufferedOutputStream(conn.getOutputStream());

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

                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream()));
                            sb = new StringBuffer("");
                            String line = "";

                            while ((line = in.readLine()) != null) {

                                sb.append(line);
                                break;
                            }

                            in.close();
                            System.out.println("JobneedUpdateAsyntask SB: " + sb.toString());

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                jobNeedDAO.changeJobNeedSyncStatus(jobUpdateArrayList.get(i).getJobneedid(),Constants.SYNC_STATUS_ONE);
                                *//*long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changePeopleEventLogReturnID(String.valueOf(returnidResp),String.valueOf(jobUpdateArrayList.get(i).getJobneedid()));*//*
                            }
                        }*/
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isUploadCompleted)
            {
                ReplyUploadAsyntask replyUploadAsyntask = new ReplyUploadAsyntask();// upload attachment type 'reply' records only
                replyUploadAsyntask.execute();
            }
            else
                syncFailedAlert();
        }
    }


    private class ReplyUploadAsyntask extends AsyncTask<Void, Integer, Void>
    {
        private ArrayList<Attachment>jobNeedReplyAttachmentArrayList;
        private Attachment attachment;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jobNeedReplyAttachmentArrayList=new ArrayList<Attachment>();
            jobNeedReplyAttachmentArrayList=attachmentDAO.getUnsyncJobNeedReplyAttachments();
            if(jobNeedReplyAttachmentArrayList!=null && jobNeedReplyAttachmentArrayList.size()>0)
                System.out.println("jobNeedReplyAttachmentArrayList.size(): "+jobNeedReplyAttachmentArrayList.size());
            else
                System.out.println("jobNeedReplyAttachmentArrayList.size(): 0");
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(jobNeedReplyAttachmentArrayList!=null && jobNeedReplyAttachmentArrayList.size()>0)
            {
                for(int i =0;i<jobNeedReplyAttachmentArrayList.size();i++)
                {
                    pCount++;
                    publishProgress(pCount);

                    try {
                        attachment=new Attachment();
                        attachment=jobNeedReplyAttachmentArrayList.get(i);
                        url = new URL(Constants.BASE_URL);
                        //INSERT INTO attachment(filepath, filename, narration, gpslocation, datetime, ownername, ownerid, attachmenttype, cuser, muser, cdtz, mdtz, isdeleted) VALUES
                        //filepath, filename, narration, gpslocation, datetime, ownername, ownerid, attachmenttype, cuser, muser, cdtz, mdtz, buid
                        String insertAttachment= DatabaseQuries.ATTACHMENT_INSERT+"( null, null,'"+attachment.getNarration()+"','"+attachment.getGpslocation()+"','"+attachment.getDatetime()+"',"+
                                attachment.getOwnername()+","+attachment.getOwnerid()+","+attachment.getAttachmentType()+","+attachment.getCuser()+","+
                                attachment.getMuser()+",'"+attachment.getCdtz()+"','"+attachment.getMdtz()+"',"+attachment.getBuid()+") returning attachmentid;";

                        System.out.println("Insert Reply: "+insertAttachment);

                        CommonFunctions.manualSyncReadingLog("ReplyLog",insertAttachment.trim(),attachment.getDatetime());
                        //---------------------------------------------------------------------

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getReplyLogResponse(insertAttachment,
                                String.valueOf(loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("ReplyUploadAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB ReplyUploadAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();
                            CommonFunctions.manualSyncReadingLog("ReplyLogResponse",sb.toString().trim(),attachment.getDatetime());
                            CommonFunctions.ResponseLog("\n Reply Event Log Response \n"+attachment.getAttachmentid()+"\n"+sb.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                attachmentDAO.changeJNReplySycnStatus(jobNeedReplyAttachmentArrayList.get(i).getAttachmentid(),jobNeedReplyAttachmentArrayList.get(i).getAttachmentType());
                                isUploadCompleted=true;
                            }else {
                                errorLogBuilder.append("\n Reply: "+ob.getString(Constants.RESPONSE_MSG));
                                CommonFunctions.ErrorLog("\n <Reply Error> \n"+attachment.getAttachmentid()+"\n"+sb.toString()+" \n");
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                //break;
                            }
                        }
                        else {
                            errorLogBuilder.append("\n Reply: "+response.getStatusLine().getStatusCode());
                            CommonFunctions.ErrorLog("\n <Reply Error> \n"+attachment.getAttachmentid()+"\n");
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            //break;
                        }



                        //-------------------------------------------------------------------------------------


                        /*UploadParameters uploadParameters=new UploadParameters();
                        uploadParameters.setServicename(Constants.SERVICE_INSERT);
                        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
                        uploadParameters.setQuery(insertAttachment);


                        String upData = gson.toJson(uploadParameters);

                        System.out.println("upData: "+upData);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(15000 *//* milliseconds *//*);
                        conn.setConnectTimeout(15000 *//* milliseconds *//*);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);//Don't use a cached Copy
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");


                        StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                        OutputStream out = new BufferedOutputStream(conn.getOutputStream());


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


                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream()));
                            sb = new StringBuffer("");
                            String line = "";

                            while ((line = in.readLine()) != null) {

                                sb.append(line);
                                break;
                            }

                            in.close();
                            System.out.println("SB: " + sb.toString());

                            JSONObject ob = new JSONObject(sb.toString());
                            System.out.println("Status: "+ob.getInt(Constants.RESPONSE_RC));
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                attachmentDAO.changeJNReplySycnStatus(jobNeedReplyAttachmentArrayList.get(i).getAttachmentid(),jobNeedReplyAttachmentArrayList.get(i).getAttachmentType());
                            }

                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*titleTextview1.setText("Data uploaded.");
            pCount=0;
            progressBar.setProgress(1000);*/
            if(isUploadCompleted)
            {
                PersonLoggerAsyntask personLoggerAsyntask = new PersonLoggerAsyntask(); //upload person logger records
                personLoggerAsyntask.execute();
            }
            else
                syncFailedAlert();
        }

    }

    private class PersonLoggerAsyntask extends AsyncTask<Void , Integer, Void>
    {
        private PersonLogger personLogger;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            personLoggerArrayList=new ArrayList<PersonLogger>();
            personLoggerArrayList=personLoggerDAO.getUnsyncPersonLoggerList( Constants.TACODE_EMPLOYEEREFERENCE,  Constants.SYNC_STATUS_ZERO);
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(personLoggerArrayList!=null && personLoggerArrayList.size()>0)
            {
                System.out.println("personLoggerArrayList: "+personLoggerArrayList.size());

                for(int i=0;i<personLoggerArrayList.size();i++)
                {
                    try {
                        pCount++;
                        publishProgress(pCount);
                        personLogger=new PersonLogger();
                        personLogger=personLoggerArrayList.get(i);
                        url = new URL(Constants.BASE_URL);
//56954
/*personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose,
        scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms,
        weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,areacode,enable,cuser,muser,cdtz,mdtz,buid,clientid*/

                        String insertQuery= DatabaseQuries.PERSON_LOGGER_INSERT+"("+personLogger.getPersonloggerid()+","+personLogger.getIdentifier()+","+personLogger.getPeopleid()+",'"+personLogger.getVisitoridno()+"',"+
                                "'"+personLogger.getFirstname()+"','"+personLogger.getMiddlename()+"','"+personLogger.getLastname()+"','"+personLogger.getMobileno()+"',"+personLogger.getIdprooftype()+","+
                                "'"+personLogger.getPhotoidno()+"','"+personLogger.getBelongings()+"','"+personLogger.getMeetingpurpose()+"','"+personLogger.getScheduledintime()+"','"+personLogger.getScheduledouttime()+"',"+
                                "'"+personLogger.getActualintime()+"','"+personLogger.getActualouttime()+"','"+personLogger.getReferenceid()+"','"+personLogger.getDob()+"','"+personLogger.getLocaladdress()+"',"+
                                "'"+personLogger.getNativeaddress()+"','"+personLogger.getQualification()+"',"+personLogger.isEnglish()+",'"+personLogger.getCurrentemployement()+"',"+
                                personLogger.getLengthofservice()+","+personLogger.getHeightincms()+","+personLogger.getWeightinkgs()+","+personLogger.getWaist()+","+personLogger.getIshandicapped()+","+
                                "'"+personLogger.getIdentificationmark()+"','"+personLogger.getPhysicalcondition()+"','"+personLogger.getReligion()+"','"+personLogger.getCaste()+"','"+personLogger.getMaritalstatus()+"',"+
                                "'"+personLogger.getGender()+"','"+personLogger.getLareacode()+"',"+personLogger.isEnable()+","+personLogger.getCuser()+","+personLogger.getMuser()+","+
                                "'"+personLogger.getCdtz()+"','"+personLogger.getMdtz()+"',"+personLogger.getBuid()+","+personLogger.getClientid()+",'"+personLogger.getLcity().trim()+"','"+personLogger.getNcity().trim()+"',"+personLogger.getLstate()+","+personLogger.getNstate()+",'"+personLogger.getNareacode()+"'"
                                +") returning personloggerid;";


                        System.out.println("personlogger upload data: "+insertQuery);

                        CommonFunctions.manualSyncReadingLog("PersonLogger",insertQuery.trim(),personLogger.getCdtz());

                        ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                        HttpResponse response=serverRequest.getDeviceEventLogResponse(insertQuery.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("DeviceEventLog response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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

                            CommonFunctions.manualSyncReadingLog("PersonLoggerResponse",sb.toString().trim(),personLogger.getCdtz());

                            CommonFunctions.ResponseLog("\n <Person logger Event Log Response> \n"+personLogger.getPersonloggerid()+"\n"+sb.toString().trim()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                emprefCountSucceed=emprefCountSucceed+1;
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                personLoggerDAO.changePeopleLoggerSyncStatus(personLogger.getCdtz(),Constants.SYNC_STATUS_ONE);
                                attachmentDAO.changePersonLogReturnID(String.valueOf(returnidResp),String.valueOf(personLogger.getPersonloggerid()));
                                isUploadCompleted=true;
                            }
                            else {
                                errorLogBuilder.append("\n PersonLog: "+ob.getString(Constants.RESPONSE_MSG));
                                emprefCountFailed=emprefCountFailed+1;
                                CommonFunctions.ErrorLog("\n <Person logger Error> \n"+personLogger.getPersonloggerid()+"\n"+sb.toString()+" \n");
                                syncFailedRecordCount++;
                                isUploadCompleted=true;//false;
                                //break;
                            }

                        }
                        else {
                            errorLogBuilder.append("\n PersonLog: "+response.getStatusLine().getStatusCode());
                            emprefCountFailed=emprefCountFailed+1;
                            System.out.println("SB1 personlogger: ERROR");
                            CommonFunctions.ErrorLog("\n <Person Error> \n"+personLogger.getPersonloggerid()+"\n");
                            syncFailedRecordCount++;
                            isUploadCompleted=true;//false;
                            //break;
                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(isUploadCompleted)
            {
                SiteAuditReportAsyntask personLoggerAsyntask = new SiteAuditReportAsyntask(); //upload Site audit log
                personLoggerAsyntask.execute();
            }
            else
                syncFailedAlert();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressCount.setText(progressBar.getProgress()+"/"+progressBar.getMax());
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }


    }

    private class SiteAuditReportAsyntask extends AsyncTask<Void, Integer, Void>
    {
        private JobNeed jobNeed;
        private UploadIncidentReportParameter uploadIncidentReportParameter;
        private ArrayList<JobNeed> jobNeedChildArrayList;
        private ArrayList<JobNeedDetails>jobNeedChildDetailsArrayList;
        private QuestionSetLevel_Two questionSetLevelTwo=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            incidentReportArrayList=new ArrayList<>();
            incidentReportArrayList=jobNeedDAO.getUnsyncSiteReportList();

        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            //progressCount.setText(progressBar.getProgress()+"/"+progressBar.getMax());
            progressCount.setText(String.valueOf(progressBar.getProgress()));
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(incidentReportArrayList!=null && incidentReportArrayList.size()>0)
            {
                System.out.println("SiteAuditReport ArrayList: "+incidentReportArrayList.size());
                for(int i=0;i<incidentReportArrayList.size();i++)
                {
                    pCount++;
                    publishProgress(pCount);

                    try {
                        jobNeed=new JobNeed();
                        jobNeed=incidentReportArrayList.get(i);
                        System.out.println("jobNeed.getJobneedid(): "+jobNeed.getJobneedid());
                        System.out.println("jobNeed.getJobdesc(): "+jobNeed.getJobdesc());
                        if(jobNeed.getQuestionsetid()==-1)
                        {
                            //"jobneedid, jobdesc, plandatetime, expirydatetime,gracetime, receivedonserver,
                            // starttime, endtime, gpslocation," +
                            //"remarks, aatop, assetid, frequency, jobid, jobtype, jobstatus, performedby,
                            // priority, questionsetid, scantype, peopleid," +
                            //"groupid, identifier, parent,cuser,cdtz,muser,mdtz,ticketcategory,buid, ctzoffset
                            String insertQuery= DatabaseQuries.SKIP_SALOG_INSERT+"( "+
                                    "'"+jobNeed.getJobdesc()+"'"+","+
                                    "'"+jobNeed.getPlandatetime()+"'"+","+
                                    "'"+jobNeed.getExpirydatetime()+"'"+","+
                                    jobNeed.getGracetime()+","+
                                    "'"+jobNeed.getStarttime()+"'"+","+
                                    "'"+jobNeed.getEndtime()+"'"+","+
                                    "'"+jobNeed.getGpslocation()+"'"+","+
                                    "'"+jobNeed.getRemarks()+"'"+","+
                                    jobNeed.getAatop()+","+
                                    jobNeed.getAssetid()+","+
                                    jobNeed.getFrequency()+","+
                                    jobNeed.getJobid()+","+
                                    jobNeed.getJobtype()+","+
                                    jobNeed.getJobstatus()+","+
                                    jobNeed.getPerformedby()+","+
                                    jobNeed.getPriority()+","+
                                    jobNeed.getQuestionsetid()+","+
                                    jobNeed.getScantype()+","+
                                    jobNeed.getPeopleid()+","+
                                    jobNeed.getGroupid()+","+
                                    jobNeed.getIdentifier()+","+
                                    jobNeed.getParent()+","+
                                    jobNeed.getCuser()+","+
                                    "'"+jobNeed.getCdtz()+"'"+","+
                                    jobNeed.getMuser()+","+
                                    "'"+jobNeed.getMdtz()+"'"+","+
                                    jobNeed.getTicketcategory()+","+
                                    jobNeed.getBuid()+","+
                                    jobNeed.getCtzoffset()+
                                    ") returning jobneedid;";


                            ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                            HttpResponse response=serverRequest.getSkipSALogResponse(insertQuery.trim(),
                                    loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                    loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                    loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                    loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                            //System.out.println("SiteReportAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                            if (response != null && response.getStatusLine().getStatusCode() == 200) {
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
                                System.out.println("SB SkipSALogAsyntask: " + sb.toString());
                                response.getEntity().consumeContent();

                                CommonFunctions.manualSyncReadingLog("SkipSALogResponse",sb.toString().trim(),jobNeed.getPlandatetime());

                                CommonFunctions.ResponseLog("\n <SkipSA Event Log Response> \n"+jobNeed.getJobneedid()+"\n"+sb.toString().trim()+"\n");

                                JSONObject ob = new JSONObject(sb.toString());
                                System.out.println("SkipSALogAsyntask ob.getInt(Constants.RESPONSE_RC): " + ob.getInt(Constants.RESPONSE_RC));
                                if (ob.getInt(Constants.RESPONSE_RC) == 0) {
                                    saCountSucceed=saCountSucceed+1;
                                    jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(), Constants.SYNC_STATUS_ONE);
                                    isUploadCompleted = true;
                                } else {
                                    errorLogBuilder.append("\n Skip Site Audit: "+ob.getString(Constants.RESPONSE_MSG));
                                    saCountFailed=saCountFailed+1;
                                    CommonFunctions.ErrorLog("\n <Skip SA Error> \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+" \n");
                                    isUploadCompleted = true;
                                    //break;
                                }
                            } else {
                                errorLogBuilder.append("\nSkip Site Audit: "+response.getStatusLine().getStatusCode());
                                saCountFailed=saCountFailed+1;
                                CommonFunctions.ErrorLog("\n <Skip SA Error> \n"+jobNeed.getJobneedid()+"\n");
                                isUploadCompleted = true;//false;
                                //break;
                            }
                        }
                        else
                        {
                            ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();
                            jobNeedChildArrayList=new ArrayList<>();
                            jobNeedChildArrayList=jobNeedDAO.getSiteReportChildSectionList(jobNeed.getJobneedid());
                            questionSetLevelTwoArrayList=new ArrayList<>();
                            if(jobNeedChildArrayList!=null && jobNeedChildArrayList.size()>0)
                            {
                                System.out.println("jobNeedChildArrayList: "+jobNeedChildArrayList.size());
                                for(int j=0;j<jobNeedChildArrayList.size();j++)
                                {
                                    questionSetLevelTwo = new QuestionSetLevel_Two();
                                    questionSetLevelTwo.setQuestionsetid(jobNeedChildArrayList.get(j).getQuestionsetid());
                                    questionSetLevelTwo.setJobdesc(jobNeedChildArrayList.get(j).getJobdesc());
                                    questionSetLevelTwo.setSeqno(jobNeedChildArrayList.get(j).getSeqno());

                                    System.out.println("child jobNeed.getJobneedid(): "+jobNeedChildArrayList.get(j).getJobneedid());
                                    System.out.println("child jobNeed.getParent(): "+jobNeedChildArrayList.get(j).getParent());

                                    jobNeedChildDetailsArrayList=new ArrayList<>();
                                    jobNeedChildDetailsArrayList=jobNeedDetailsDAO.test_getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getJobneedid());
                                    //jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getJobneedid());
                                    questionSetLevelTwo.setDetails(jobNeedChildDetailsArrayList);
                                    questionSetLevelTwoArrayList.add(questionSetLevelTwo);
                                }
                            }
                            if(jobNeedChildDetailsArrayList!=null && jobNeedChildDetailsArrayList.size()>0)
                            {
                                uploadIncidentReportParameter = new UploadIncidentReportParameter();
                                uploadIncidentReportParameter.setJobdesc(jobNeed.getJobdesc());
                                uploadIncidentReportParameter.setAatop(jobNeed.getAatop());
                                uploadIncidentReportParameter.setAssetid(jobNeed.getAssetid());
                                uploadIncidentReportParameter.setCuser(jobNeed.getCuser());
                                uploadIncidentReportParameter.setFrequency(jobNeed.getFrequency());
                                uploadIncidentReportParameter.setPlandatetime(jobNeed.getPlandatetime());
                                uploadIncidentReportParameter.setExpirydatetime(jobNeed.getExpirydatetime());
                                uploadIncidentReportParameter.setGracetime(jobNeed.getGracetime());
                                uploadIncidentReportParameter.setGroupid(jobNeed.getGroupid());
                                uploadIncidentReportParameter.setIdentifier(jobNeed.getIdentifier());
                                uploadIncidentReportParameter.setJobid(jobNeed.getJobid());
                                uploadIncidentReportParameter.setJobneedid(jobNeed.getJobneedid());
                                uploadIncidentReportParameter.setJobstatus(jobNeed.getJobstatus());
                                uploadIncidentReportParameter.setJobtype(jobNeed.getJobtype());
                                uploadIncidentReportParameter.setMuser(jobNeed.getMuser());
                                uploadIncidentReportParameter.setParent(jobNeed.getParent());
                                uploadIncidentReportParameter.setPeopleid(jobNeed.getPeopleid());
                                uploadIncidentReportParameter.setPerformedby(jobNeed.getPerformedby());
                                uploadIncidentReportParameter.setPriority(jobNeed.getPriority());
                                uploadIncidentReportParameter.setScantype(jobNeed.getScantype());
                                uploadIncidentReportParameter.setQuestionsetid(jobNeed.getQuestionsetid());
                                uploadIncidentReportParameter.setBuid(jobNeed.getBuid());
                                uploadIncidentReportParameter.setGpslocation(jobNeed.getGpslocation());
                                uploadIncidentReportParameter.setCdtzoffset(jobNeed.getCtzoffset());
                                uploadIncidentReportParameter.setOthersite(jobNeed.getOthersite());


                                uploadIncidentReportParameter.setChild(questionSetLevelTwoArrayList);

                                String ss = gson.toJson(uploadIncidentReportParameter);
                                System.out.println("SiteAuditReport report SS: " + ss);


                                CommonFunctions.manualSyncReadingLog("SALog",ss.trim(),jobNeed.getPlandatetime());

                                //---------------------------------------------------------------------

                                ServerRequest serverRequest = new ServerRequest(SyncronizationViewActivity.this);
                                HttpResponse response = serverRequest.getIncidentReportLogResponse(ss.trim(),
                                        loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER, 0),
                                        loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE, ""),
                                        loginPref.getString(Constants.LOGIN_ENTERED_USER_ID, ""),
                                        loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS, ""));

                                //System.out.println("SiteReportAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                                if (response != null && response.getStatusLine().getStatusCode() == 200) {
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
                                    System.out.println("SB IRLogAsyntask: " + sb.toString());
                                    response.getEntity().consumeContent();

                                    CommonFunctions.manualSyncReadingLog("SALogResponse",sb.toString().trim(),jobNeed.getPlandatetime());

                                    CommonFunctions.ResponseLog("\n <SA Event Log Response> \n"+jobNeed.getJobneedid()+"\n"+sb.toString().trim()+"\n");

                                    JSONObject ob = new JSONObject(sb.toString());
                                    System.out.println("IRLogAsyntask ob.getInt(Constants.RESPONSE_RC): " + ob.getInt(Constants.RESPONSE_RC));
                                    if (ob.getInt(Constants.RESPONSE_RC) == 0) {
                                        saCountSucceed=saCountSucceed+1;
                                        jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(), Constants.SYNC_STATUS_ONE);
                                        if (jobNeedChildArrayList != null && jobNeedChildArrayList.size() > 0) {
                                            for (int cJob = 0; cJob < jobNeedChildArrayList.size(); cJob++) {
                                                jobNeedDAO.changeJobNeedSyncStatus(jobNeedChildArrayList.get(cJob).getJobneedid(), Constants.SYNC_STATUS_ONE);
                                            }
                                        }
                                        long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                        //attachmentDAO.changePelogReturnID(String.valueOf(returnidResp), (incidentReportArrayList.get(i).getJobneedid()));
                                        attachmentDAO.changePelogReturnID(String.valueOf(returnidResp), (jobNeed.getJobneedid()));
                                        jobNeedDAO.getCount();
                                        isUploadCompleted = true;
                                    } else {
                                        errorLogBuilder.append("\n Site Audit: "+ob.getString(Constants.RESPONSE_MSG));
                                        saCountFailed=saCountFailed+1;
                                        CommonFunctions.ErrorLog("\n <SA Error> \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+" \n");
                                        syncFailedRecordCount++;
                                        isUploadCompleted=true;//false;
                                        //break;
                                    }
                                } else {
                                    // errorLogBuilder.append("\n Site Audit: "+response.getStatusLine().getStatusCode());
                                    saCountFailed=saCountFailed+1;
                                    CommonFunctions.ErrorLog("\n <SA Error> \n"+jobNeed.getJobneedid()+"\n");
                                    syncFailedRecordCount++;
                                    isUploadCompleted=true;//false;
                                    //break;
                                }
                            }
                            else
                            {
                                System.out.println("Site Audit duplicate record found");
                                jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(), Constants.SYNC_STATUS_ONE);
                                jobNeedDAO.deleteRec(incidentReportArrayList.get(i).getJobneedid());
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if(isUploadCompleted) {
                titleTextview1.setText(getResources().getString(R.string.sync_dialog_datauploaded));
                pCount = 0;
                progressBar.setProgress(1000);

                //checkVersion();

                /*DownloadDataAsyntask downloadDataAsyntask = new DownloadDataAsyntask();
                downloadDataAsyntask.execute();*/
                System.out.println("Upload image service started");
                attachmentDAO.deleteAVP();
                attachmentArrayList=new ArrayList<>();

                attachmentArrayList=attachmentDAO.getUnsyncAttachments(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
                handleActionFoo();
            }
            else
                syncFailedAlert();
        }
    }

    private void handleActionFoo() {
        System.out.println("attachment count-----" + attachmentArrayList.size());

        if(attachmentArrayList!=null && attachmentArrayList.size()>0) {
            System.out.println("attachment count-----" + attachmentArrayList.size());
/*            for (int i = 0; i < attachmentArrayList.size(); i++)
                retrieveAndUploadData(attachmentArrayList.get(i));*/
            UploadImageAsynTask uploadImageAsynTask = new UploadImageAsynTask();
            uploadImageAsynTask.execute();
        }
        else {
            DownloadDataAsyntask downloadDataAsyntask = new DownloadDataAsyntask();
            downloadDataAsyntask.execute();
        }
        //callAutoSyncService();

    }

    private void retrieveAndUploadData(Attachment attachment)
    {
        if(attachment!=null) {
            if(typeAssistDAO.getEventTypeCode(attachment.getAttachmentType()).equalsIgnoreCase(Constants.ATTACHMENT_TYPE_ATTACHMENT) || typeAssistDAO.getEventTypeCode(attachment.getAttachmentType()).equalsIgnoreCase(Constants.ATTACHMENT_TYPE_SIGN) ) {
/*                UploadImageAsynTask uploadImageAsynTask = new UploadImageAsynTask(attachment);
                uploadImageAsynTask.execute();*/
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

        //public UploadImageAsynTask(Attachment attachment)
        {
            this.attachment=attachment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           /* if(attachment!=null && attachment.getFileName()!=null && attachment.getFileName().toString().trim().length()>0) {
                selectedFile = new File(attachment.getFilePath());

                DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                date = df.format(Calendar.getInstance().getTime());


                String[] parts = attachment.getFilePath().split("/");
                fileName = parts[parts.length - 1];
                System.out.println("FileName: " + fileName);
            }*/
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < attachmentArrayList.size(); i++){
                System.out.println("attachment count-----123=" + attachmentArrayList.size());
                System.out.println("attachment count-----1=" + attachmentArrayList.get(i));
                attachment = attachmentArrayList.get(i);

                if(attachment!=null && attachment.getFileName()!=null && attachment.getFileName().toString().trim().length()>0) {
                    selectedFile = new File(attachment.getFilePath());

                    DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    date = df.format(Calendar.getInstance().getTime());


                    String[] parts = attachment.getFilePath().split("/");
                    fileName = parts[parts.length - 1];
                    System.out.println("FileName: " + fileName);
                }

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
                            Toast.makeText(SyncronizationViewActivity.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
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

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("image upload finish");
                    autoSyncPref.edit().putBoolean(Constants.IS_AUTO_SYNC_RUNNING,false).apply();

                }


            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println(" Download calling--------=");

            DownloadDataAsyntask downloadDataAsyntask = new DownloadDataAsyntask();
            downloadDataAsyntask.execute();

        }

    }



    private class DownloadDataAsyntask extends AsyncTask<Void, Integer, Void>
    {
        String date="1970-01-01 00:00:00";
        int counter=0;
        public DownloadDataAsyntask()
        {

            //db.beginTransaction();
            //db.execSQL("PRAGMA foreign_keys=ON;");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            titleTextview2.setText(getResources().getString(R.string.sync_dialog_download_msg));

            //dropAllUserTables(new SqliteOpenHelper(context).getDatabase());
            checkVersion();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                for(SyncCount c:SyncCount.values())
                {
                    counter=c.ordinal();

                    publishProgress((counter+1));

                    //---------------------------------------------------------------------
                    System.out.println("-------------------------------------------------Counter : "+(counter));

                    ServerRequest serverRequest=new ServerRequest(SyncronizationViewActivity.this);
                    HttpResponse response=serverRequest.getDownloadDataLogResponse(CommonFunctions.getQuery(counter, SyncronizationViewActivity.this),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""),
                            loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));

                    //System.out.println("DownloadAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                    if(response!=null && response.getStatusLine().getStatusCode()==HttpsURLConnection.HTTP_OK)
                    {
                        //InputStream is = response.getEntity().getContent();

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

                        System.out.println("SB Length: "+sb.toString().length());

                        CommonFunctions.DownloadedDataLog("\n"+counter+" : "+sb.toString()+"\n");

                        JSONObject ob = new JSONObject(sb.toString());

                        if(ob.getInt(Constants.RESPONSE_RC)==0)
                        {
                            int totalRows = ob.getInt(Constants.RESPONSE_NROW);
                            if(totalRows>0)
                            {
                                updateData(counter,ob, c.name());
                            }
                        }
                        else
                        {
                            errorLogBuilder.append("\n Info: "+ob.getString(Constants.RESPONSE_MSG));
                            isCompleted=false;
                            break;
                        }
                    }
                    else {
                        errorLogBuilder.append("\n Info: "+response.getStatusLine().getStatusCode());
                        isCompleted=false;
                        break;
                    }


                    //-------------------------------------------------------------------------------------


                    /*UploadParameters uploadParameters=new UploadParameters();
                    uploadParameters.setServicename(Constants.SERVICE_SELECT);
                    uploadParameters.setQuery(CommonFunctions.getQuery(counter, SyncronizationViewActivity.this));
                    uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);

                    URL url = new URL(Constants.BASE_URL);
                    String upData = gson.toJson(uploadParameters);

                    System.out.println("Download Data: "+upData);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 *//* milliseconds *//*);
                    conn.setConnectTimeout(15000 *//* milliseconds *//*);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());
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

                    int responseCode=conn.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK)
                    {
                        BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line="";
                        while((line = in.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        in.close();
                        System.out.println("SB: "+sb.toString());

                        JSONObject ob = new JSONObject(sb.toString());
                        int status = ob.getInt(Constants.RESPONSE_RC);
                        if(status==0)
                        {

                            int totalRows = ob.getInt(Constants.RESPONSE_NROW);
                            if(totalRows>0)
                            {
                                updateData(counter,ob, c.name());

                            }
                        }
                        else
                        {
                            isCompleted=false;
                            break;
                        }
                    }
                    else {
                        isCompleted = false;
                    }*/
                }


            } catch (Exception e) {
                e.printStackTrace();
                isCompleted = false;
                CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
            }

            try {
                //createDatabaseBackup();
                exportDB();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            syncPref.edit().putLong(Constants.SYNC_TIMESTAMP, System.currentTimeMillis()).apply();
            syncOffsetPref.edit().putLong(Constants.SYNC_TICKET_TIMESTAMP, System.currentTimeMillis()).apply();
            if(isCompleted && (syncFailedRecordCount==0)) {

                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                titleTextview2.setText(getResources().getString(R.string.sync_dialog_datadownloaded));
                System.out.println("Downloading completed.");
                syncStatusButton.setEnabled(true);
                syncStatusButton.setBackgroundColor(getResources().getColor(R.color.button_background));
                syncStatusButton.setText(getResources().getString(R.string.sync_dialog_success_msg));
                titleTextview.setText(getResources().getString(R.string.sync_dialog_title));
                loginPref.edit().putBoolean(Constants.IS_SYNC_DONE,true).apply();
                //checkVersion();
            }
            else {
                String syncFailRecCountMsg="("+syncFailedRecordCount+") record/s failed to upload.";
                if(syncFailedRecordCount>0)
                    errorLogView.setText(syncFailRecCountMsg);

                if (errorLogBuilder.toString().trim().length() > 0) {
                    errorLogView.setVisibility(View.VISIBLE);
                    errorLogView.setText(errorLogView.getText().toString().trim()+"\n"+errorLogBuilder.toString().trim());
                }

                syncReportButton.setVisibility(View.VISIBLE);
                System.out.println("Downloading failed.");
                syncStatusButton.setEnabled(true);
                syncStatusButton.setText(getResources().getString(R.string.sync_dialog_failed_msg));
                syncStatusButton.setBackgroundResource(R.drawable.rounder_delete_button);
                //syncStatusButton.setBackgroundColor(getResources().getColor(R.color.button_delete_background));
                titleTextview.setText(getResources().getString(R.string.sync_dialog_title));
                loginPref.edit().putBoolean(Constants.IS_SYNC_DONE,false).apply();
            }

            isSyncInProgress=true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar2.setProgress(values[0]);
            progressCount2.setText(progressBar2.getProgress()+"/"+progressBar2.getMax());
        }
    }

    private void checkVersion() {
        int newVersion = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int intVersion = pInfo.versionCode;
            newVersion = intVersion;

            System.out.println("version name ::"+ newVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int versionOnSP = syncPref.getInt(Constants.SYNC_VERSION, 20);
        System.out.println("version name versionOnSP  :: "+ versionOnSP);

        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/com.youtility.intelliwiz20/databases/"+SqliteOpenHelper.DB_NAME;
        File currentDB = new File(data, currentDBPath);

        if (newVersion == versionOnSP) {
            System.out.println("Your version is updated ::"+ System.currentTimeMillis());
        } else {
            System.out.println("Your version is not updated ::"+System.currentTimeMillis());
            Long date= Long.valueOf(19800000);
            dropAllUserTables(new SqliteOpenHelper(context).getDatabase());
            syncPref.edit().putInt(Constants.SYNC_VERSION, newVersion).apply();
            syncPref.edit().putLong(Constants.SYNC_TIMESTAMP, date).apply();
            /*commonDAO.deleteAllData();
            System.out.println("Tables deleted");

            SQLiteDatabase.deleteDatabase(currentDB);
            System.out.println("database deleted");

            SqliteOpenHelper dbh = new SqliteOpenHelper(this);
            SQLiteDatabase db = dbh.getWritableDatabase();
            System.out.println("InLogin--db.getVersion(): "+db.getVersion());

            DownloadDataAsyntask downloadDataAsyntask = new DownloadDataAsyntask();
            downloadDataAsyntask.execute();*/


        }
    }

    private void dropAllUserTables(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        File data = Environment.getDataDirectory();
        String currentDBPath = "/data/com.youtility.intelliwiz20/databases/"+SqliteOpenHelper.DB_NAME;
        File currentDB = new File(data, currentDBPath);
        //noinspection TryFinallyCanBeTryWithResources not available with API < 19
        try {
            List<String> tables = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                String tableName= cursor.getString(0);
                tables.add(tableName);
                System.out.println("tableName ::"+tableName);
            }

            for (String table : tables) {
                if (table.startsWith("sqlite_")) {
                    continue;
                }
                db.execSQL("DROP TABLE IF EXISTS " + table);
                System.out.println( "Dropped table " + table);
            }
            SqliteOpenHelper sqlopenHelper;
            sqlopenHelper=SqliteOpenHelper.getInstance(context);
            sqlopenHelper.CreateTables(db);

        } finally {
            cursor.close();
        }
    }

    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/com.youtility.intelliwiz20/databases/"+SqliteOpenHelper.DB_NAME;
        //String currentDBPath = "/data/com.youtility.istaging/databases/"+SqliteOpenHelper.DB_NAME;
        String backupDBPath = Constants.FOLDER_NAME + "/"+SqliteOpenHelper.DB_NAME+"_backup";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void createDatabaseBackup()
    {
        try{
            File f=new File("/data/data/com.youtility.intelliwiz20/databases/"+ SqliteOpenHelper.DB_NAME);     // internal db copy
            //File f=new File("/data/data/com.youtility.istaging/databases/"+ SqliteOpenHelper.DB_NAME);     // internal db copy

            File fext=new File(Environment.getExternalStorageDirectory().getPath() + "/"+ SqliteOpenHelper.DB_NAME); // external db copy



            if(  f.exists()){
                InputStream isnew = new FileInputStream(new File("/data/data/com.youtility.intelliwiz20/databases/"+ SqliteOpenHelper.DB_NAME));
                //InputStream isnew = new FileInputStream(new File("/data/data/com.youtility.istaging/databases/"+ SqliteOpenHelper.DB_NAME));
                String outnew ="data/data/com.youtility.intelliwiz20/databases/" + SqliteOpenHelper.DB_NAME; //Environment.getExternalStorageDirectory().getPath() + "/" + DBHelper.DB_NAME+"_prev";
                //String outnew ="data/data/com.youtility.istaging/databases/" + SqliteOpenHelper.DB_NAME; //Environment.getExternalStorageDirectory().getPath() + "/" + DBHelper.DB_NAME+"_prev";
                OutputStream osnew = new FileOutputStream(outnew, false);
                byte[] buffernew = new byte[1024];
                int lengthnew;
                while ((lengthnew = isnew.read(buffernew)) > 0) {
                    osnew.write(buffernew, 0, lengthnew);
                }
                osnew.flush();
                osnew.close();
                isnew.close();
            }else if(!fext.exists()){

                InputStream is = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/" + SqliteOpenHelper.DB_NAME));
                String out ="data/data/com.youtility.intelliwiz20/databases/" + SqliteOpenHelper.DB_NAME;
                //String out ="data/data/com.youtility.istaging/databases/" + SqliteOpenHelper.DB_NAME;
                OutputStream os = new FileOutputStream(out, false);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            }else{
                System.out.println("Please check SD card ");
                throw new Exception();
            }



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean updateData(int position, JSONObject ob, String val)
    {
        ArrayList<Asset> assets=null;
        ArrayList<JobNeed> jobNeedArrayList= null;
        ArrayList<JobNeedDetails>jobNeedDetailses=null;
        ArrayList<TypeAssist>typeAssistArrayList=null;
        ArrayList<Geofence>geofenceArrayList=null;
        ArrayList<People>peopleArrayList=null;
        ArrayList<Group>groupArrayList=null;
        ArrayList<AttendanceHistory>attendanceHistoryArrayList=null;
        ArrayList<Question>questionArrayList=null;
        ArrayList<QuestionSet>questionSetArrayList=null;
        ArrayList<QuestionSetBelonging>questionSetBelongingArrayList=null;
        ArrayList<PeopleGroupBelonging>peopleGroupBelongingArrayList=null;
        ArrayList<Sites>sitesArrayListOld=null;
        ArrayList<SiteList>sitesArrayList=null;
        ArrayList<JobNeed>ticketArrayList=null;
        ArrayList<JobNeedDetails>ticketDetailses=null;
        ArrayList<Address>addressArrayList=null;
        ArrayList<Othersite>othersiteArrayList=null;
        ArrayList<SitesInformation> sitesInformationArrayList=null;
        ArrayList<TemplateList>templateListArrayList=null;
        ArrayList<AssignedSitesPeoples>assignedSitesPeoplesArrayList=null;
        JSONObject dataObject ;
        switch(position)
        {
            case 0:
                assets=new ArrayList<Asset>();
                try {
                    //db.execSQL("delete from "+ AssetDetail_Table.TABLE_NAME);
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, AssetDetail_Table.TABLE_NAME);
                    final int idColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_ID);
                    final int nameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_NAME);
                    final int syncStatusColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SYNC_STATUS);
                    final int codeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CODE);
                    final int enableColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_ENABLE);
                    final int parentColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_PARENT);
                    final int cuserColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CUSER);
                    final int muserColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CDTZ);
                    //final int isdeletedColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_IS_DELETED);
                    final int mdtzColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MDTZ);
                    final int iscriticalColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_IS_CRITICAL);
                    final int gpslocationColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_GPS_LOCATION);
                    final int identifierColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_IDENTIFIER);
                    final int runningstatusColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_RUNNING_STATUS);
                    final int buidColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BUID);
                    final int loccodeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_CODE);
                    final int locnameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_NAME);

                    final int typeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_TYPE);
                    final int categoryColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CATEGORY);
                    final int subcategoryColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SUBCATEGORY);
                    final int brandColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BRAND);
                    final int modelColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MODEL);
                    final int supplierColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SUPPLIER);
                    final int capacityColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CAPACITY);
                    final int unitColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_UNIT);
                    final int yomColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_YOM);
                    final int msnColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MSN);
                    final int bdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BILLDATE);
                    final int pdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_PURCHACEDATE);
                    final int isdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_INSTALLATIONDATE);
                    final int billvalColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BILLVALUE);
                    final int servprovColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER);
                    final int servprovnameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER_NAME);
                    final int serviceColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICE);
                    final int sfdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICEFROMDATE);
                    final int stdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICETODATE);
                    final int meterColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_METER);
                    final int qsetidsColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_QSETIDS);
                    final int qsetnameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_QSETNAME);
                    final int tempCodeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_TEMPCODE);
                    final int mfactColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MFACTOR);

                    JSONArray dataArrayList = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Asset>>() {
                    }.getType();
                    assets = gson.fromJson(dataArrayList.toString(), listType);

                    for(Asset dto : assets)
                    {
                        db.execSQL("delete from "+AssetDetail_Table.TABLE_NAME +" where "+AssetDetail_Table.ASSET_ID+" = "+dto.getAssetid());
                    }

                    if(assets!=null && assets.size()>0) {

                        System.out.println("------------------------------------------------------Asset Count: "+assets.size());

                        for (Asset asset : assets) {
                            helper.prepareForReplace();
                            /*System.out.println("asset.getAssetname(): "+asset.getAssetname());
                            System.out.println("asset.getAssetcode(): "+asset.getAssetcode());
                            System.out.println("asset.getAssetid(): "+asset.getAssetid());*/
                            /*System.out.println("------------------------------------------------------------------------------");
                            System.out.println("asset.getEnable(): "+asset.getEnable());
                            System.out.println("asset.getAssetname(): "+asset.getAssetname());
                            System.out.println("asset.getType(): "+asset.getType());
                            System.out.println("asset.getMultiplicationfactor(): "+asset.getMultiplicationfactor());*/

                            helper.bind(idColumn, asset.getAssetid());
                            helper.bind(nameColumn, asset.getAssetname());
                            helper.bind(syncStatusColumn,0);
                            helper.bind(codeColumn, asset.getAssetcode());
                            helper.bind(enableColumn, asset.getEnable());
                            helper.bind(parentColumn, asset.getParent());
                            helper.bind(cuserColumn, asset.getCuser());
                            helper.bind(muserColumn, asset.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(asset.getCdtz()));
                            //helper.bind(isdeletedColumn, asset.getIsdeleted());
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(asset.getMdtz()));
                            helper.bind(iscriticalColumn, asset.getIscritical());
                            helper.bind(gpslocationColumn, asset.getGpslocation());
                            helper.bind(identifierColumn, asset.getIdentifier());
                            helper.bind(runningstatusColumn, asset.getRunningstatus());
                            helper.bind(buidColumn, asset.getBuid());
                            helper.bind(loccodeColumn,asset.getLoccode());
                            helper.bind(locnameColumn, asset.getLocname());
                            helper.bind(typeColumn, asset.getType());
                            helper.bind(categoryColumn, asset.getCategory());
                            helper.bind(subcategoryColumn, asset.getSubcategory());
                            helper.bind(brandColumn, asset.getBrand());
                            helper.bind(modelColumn, asset.getModel());
                            helper.bind(supplierColumn, asset.getSupplier());
                            helper.bind(capacityColumn, asset.getCapacity());
                            helper.bind(unitColumn, asset.getUnit());
                            helper.bind(yomColumn, asset.getYom());
                            helper.bind(msnColumn, asset.getMsn());
                            helper.bind(bdateColumn, asset.getBdate());
                            helper.bind(pdateColumn, asset.getPdate());
                            helper.bind(isdateColumn, asset.getIsdate());
                            helper.bind(billvalColumn, asset.getBillval());
                            helper.bind(serviceColumn, asset.getService());
                            helper.bind(servprovColumn, asset.getServprov());
                            helper.bind(servprovnameColumn, asset.getServprovname());
                            helper.bind(sfdateColumn, asset.getSfdate());
                            helper.bind(stdateColumn, asset.getStdate());
                            helper.bind(meterColumn, asset.getMeter());
                            helper.bind(qsetidsColumn, asset.getQsetids());
                            helper.bind(qsetnameColumn, asset.getQsetname());
                            helper.bind(tempCodeColumn, asset.getTempcode());
                            helper.bind(mfactColumn,asset.getMultiplicationfactor());
                            helper.execute();
                        }
                    }
                    isCompleted=true;
                    ob=null;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    //helper.close();
                    helper = null;
                    assets = null;
                }
                break;
            case 1:
                try {
                    StringBuilder sb=null;
                    jobNeedArrayList=new ArrayList<JobNeed>();
                    //db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS +" NOT IN('0','2') " );
                            /*"AND "
                            +JOBNeed_Table.JOBNEED_IDENTIFIER +" not in(select taid from TypeAssist where tacode = '"+Constants.JOB_NEED_IDENTIFIER_PPM+"' AND tatype ='"+Constants.IDENTIFIER_JOBNEED+"') AND "
                            +JOBNeed_Table.JOBNEED_JOBTYPE + " not in(select taid from TypeAssist where tacode = '"+Constants.JOB_TYPE_SCHEDULED+"')");*/
                    //db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME );
                    jobNeedDAO.getCount();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    //P065410

                    helper = new DatabaseUtils.InsertHelper(db, JOBNeed_Table.TABLE_NAME);
                    final int idColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ID);
                    final int descColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_DESC);
                    final int pdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME);
                    final int expdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME);
                    final int gracetimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME);
                    final int recvonserverColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER);
                    final int starttimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME);
                    final int endtimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME);
                    final int gpslocColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION);
                    final int remarkColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK);
                    //final int isdeletedColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED);
                    final int cuserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER);
                    final int muserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ);
                    final int attachmentcountColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT);
                    final int aatopColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP);
                    final int assetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID);
                    final int freqColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY);
                    final int jobidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID);
                    final int jobstatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS);
                    final int jobtypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE);
                    final int performbyColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY);
                    final int priorityColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY);
                    final int qsetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID);
                    final int scantypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE);
                    final int peopleColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID);
                    final int peoplegroupColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID);
                    final int jnidentifierColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER);
                    final int jnparentidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT);
                    final int syncStatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SYNC_STATUS);
                    final int ticketnoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO);
                    final int buidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_BUID);
                    final int seqNoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO);
                    final int ticketCategoryColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY);
                    final int cdtzoffsetColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET);
                    final int mfactorColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR);
                    final int mfactorColumn1 = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR1);
                    final int mfactorColumn2 = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR2);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeed>>() {
                    }.getType();
                    jobNeedArrayList = gson.fromJson(data.toString(), listType);

                    for(JobNeed dto : jobNeedArrayList)
                    {
                        db.execSQL("delete from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_ID+" = "+dto.getJobneedid());
                    }

                    if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                    {
                        sb=new StringBuilder();
                        System.out.println("------------------------------------------------------Jobneed Count: "+jobNeedArrayList.size());
                        for (JobNeed jobNeed : jobNeedArrayList) {
                            helper.prepareForReplace();

                            sb.append(jobNeed.getJobneedid()+",");

                            /*System.out.println("JObneed ID: "+jobNeed.getJobneedid());
                            System.out.println("JObneed Description: "+jobNeed.getJobdesc());
                            System.out.println("JObneed Status: "+jobNeed.getJobstatus());*/

                            /*System.out.println("JObneed ID: "+jobNeed.getJobneedid());
                            System.out.println("JObneed Description: "+jobNeed.getJobdesc());
                            System.out.println("JObneed questionset ID: "+jobNeed.getQuestionsetid());*/

                            /*System.out.println("JObneed Desc: "+jobNeed.getJobdesc());
                            System.out.println("JObneed format date: "+CommonFunctions.getParseDate(jobNeed.getPlandatetime()));
                            System.out.println("JObneed planned date: "+(jobNeed.getPlandatetime()));
                            System.out.println("JObneed Identifier: "+jobNeed.getIdentifier());
                            System.out.println("JObneed Type: "+jobNeed.getJobtype());
                            System.out.println("Assigned People: "+jobNeed.getAatop());
                            System.out.println("Assigned Group: "+jobNeed.getGroupid());
                            System.out.println("-----------------------------------------------------------------");*/

                            helper.bind(idColumn, jobNeed.getJobneedid());
                            helper.bind(descColumn, jobNeed.getJobdesc());
                            helper.bind(jobidColumn, jobNeed.getJobid());
                            helper.bind(freqColumn, jobNeed.getFrequency());
                            /*System.out.println("JOBNeed ID: "+jobNeed.getJobneedid());
                            System.out.println("ConversionDate: "+CommonFunctions.getParseDatabaseDateFormat(jobNeed.getPlandatetime()));
                            System.out.println("DB date: "+(jobNeed.getPlandatetime()));*/
                            helper.bind(pdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getPlandatetime()));
                            helper.bind(expdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getExpirydatetime()));
                            helper.bind(gracetimeColumn, jobNeed.getGracetime());
                            helper.bind(assetcodeColumn, jobNeed.getAssetid());
                            helper.bind(qsetcodeColumn, jobNeed.getQuestionsetid());
                            helper.bind(peoplegroupColumn, jobNeed.getGroupid());
                            helper.bind(aatopColumn, jobNeed.getAatop());
                            helper.bind(jobstatusColumn, jobNeed.getJobstatus());
                            helper.bind(jobtypeColumn, jobNeed.getJobtype());
                            helper.bind(scantypeColumn, jobNeed.getScantype());
                            helper.bind(recvonserverColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getReceivedonserver()));
                            helper.bind(priorityColumn, jobNeed.getPriority());

                            helper.bind(starttimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getStarttime()));

                            helper.bind(endtimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getEndtime()));


                            helper.bind(performbyColumn, jobNeed.getPerformedby());
                            helper.bind(gpslocColumn, jobNeed.getGpslocation());
                            helper.bind(remarkColumn, jobNeed.getRemarks());
                            //helper.bind(isdeletedColumn, jobNeed.getIsdeleted());
                            helper.bind(cuserColumn, jobNeed.getCuser());
                            helper.bind(muserColumn, jobNeed.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getMdtz()));
                            helper.bind(attachmentcountColumn, jobNeed.getAttachmentcount());
                            helper.bind(peopleColumn, jobNeed.getPeopleid());
                            helper.bind(syncStatusColumn,"-1");
                            helper.bind(jnidentifierColumn,jobNeed.getIdentifier());
                            helper.bind(jnparentidColumn,jobNeed.getParent());
                            helper.bind(ticketnoColumn, jobNeed.getTicketno());
                            helper.bind(buidColumn, jobNeed.getBuid());
                            helper.bind(seqNoColumn, jobNeed.getSeqno());
                            helper.bind(ticketCategoryColumn, jobNeed.getTicketcategory());
                            helper.bind(cdtzoffsetColumn, jobNeed.getCtzoffset());
                            helper.bind(mfactorColumn, jobNeed.getMultiplicationfactor());
                            helper.bind(mfactorColumn1, jobNeed.getMultiplicationfactor());
                            helper.bind(mfactorColumn2, jobNeed.getMultiplicationfactor());

                            //System.out.println("ctzOffset: "+jobNeed.getCtzoffset());

                            helper.execute();
                        }

                    }
                    else {
                        DatabaseQuries.JOBNEEDIDS="";
                    }
                    if(sb!=null && sb.toString().trim().length()>0)
                    {
                        DatabaseQuries.JOBNEEDIDS = sb.toString().trim().substring(0, sb.toString().trim().length() - 1);
                    }
                    else
                    {
                        DatabaseQuries.JOBNEEDIDS="";
                    }

                    System.out.println("JOBNeedID in JOBNEED Sync: "+DatabaseQuries.JOBNEEDIDS);

                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    //helper.close();
                    helper = null;
                    jobNeedArrayList = null;
                }
                break;
            case 2:

                try {

                    System.out.println("DatabaseQuries.JOBNEEDIDS in JOBNEEDDETAILS: "+DatabaseQuries.JOBNEEDIDS);
                    db.execSQL("delete from "+ JOBNeedDetails_Table.TABLE_NAME +" where "+JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID+" in("+DatabaseQuries.JOBNEEDIDS+")");

                    //db.execSQL("delete from "+ JOBNeedDetails_Table.TABLE_NAME);
                    jobNeedDetailses=new ArrayList<JobNeedDetails>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, JOBNeedDetails_Table.TABLE_NAME);
                    final int jndidColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID);
                    final int jnidColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID);
                    final int seqNoColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO);
                    final int questNameColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID);
                    final int ansColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER);
                    final int minColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN);
                    final int maxColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX);
                    final int optionColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION);
                    final int alertonColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON);

                    final int typeColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE);
                    final int ismandatoryColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY);
                    final int cdtzColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ);
                    final int cuserColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER);
                    final int muserColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeedDetails>>() {
                    }.getType();
                    jobNeedDetailses = gson.fromJson(data.toString(), listType);
                    if(jobNeedDetailses!=null &&  jobNeedDetailses.size()>0)
                    {
                        System.out.println("------------------------------------------------------Jobneeddetails Count: "+jobNeedDetailses.size());

                        for (JobNeedDetails jobNeedDetails : jobNeedDetailses) {
                            helper.prepareForReplace();

                            /*System.out.println("getJndid id: "+jobNeedDetails.getJndid());
                             *//*System.out.println("getMin: "+jobNeedDetails.getMin());
                            System.out.println("getMax: "+jobNeedDetails.getMax());*//*
                            System.out.println("getQuestionid: "+jobNeedDetails.getQuestionid());
                            System.out.println("getJobneedid: "+jobNeedDetails.getJobneedid());*/

                            /*System.out.println("getQuestionid: "+jobNeedDetails.getQuestionid());
                            System.out.println("getJobneedid: "+jobNeedDetails.getJobneedid());*/

                            helper.bind(jndidColumn, jobNeedDetails.getJndid());
                            helper.bind(jnidColumn, jobNeedDetails.getJobneedid());
                            helper.bind(seqNoColumn, jobNeedDetails.getSeqno());
                            helper.bind(questNameColumn, jobNeedDetails.getQuestionid());
                            helper.bind(ansColumn, jobNeedDetails.getAnswer());
                            helper.bind(minColumn, jobNeedDetails.getMin());
                            helper.bind(maxColumn, jobNeedDetails.getMax());
                            helper.bind(optionColumn, jobNeedDetails.getOption());
                            helper.bind(alertonColumn, jobNeedDetails.getAlerton());
                            helper.bind(typeColumn, jobNeedDetails.getType());
                            helper.bind(ismandatoryColumn, jobNeedDetails.getIsmandatory());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeedDetails.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeedDetails.getMdtz()));
                            helper.bind(cuserColumn, jobNeedDetails.getCuser());
                            helper.bind(muserColumn, jobNeedDetails.getMuser());
                            helper.execute();
                        }

                    }

                    DatabaseQuries.JOBNEEDIDS="";
                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper = null;
                    jobNeedDetailses = null;
                }
                break;
            case 3:
                try {
                    //db.execSQL("delete from "+ TypeAssist_Table.TABLE_NAME);
                    typeAssistArrayList=new ArrayList<>();

                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, TypeAssist_Table.TABLE_NAME);
                    final int taidColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ID);
                    final int tacodeColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE);
                    final int tanameColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_NAME);
                    final int tatypeColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_TYPE);
                    final int tacuserColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CUSER);
                    final int tacdtzColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CDTZ);
                    final int tamuserColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_MUSER);
                    final int tamdtzColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_MDTZ);
                    //final int taisdeletedColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ISDELETED);
                    final int taparentColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_PARENT);
                    final int tabuidColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<TypeAssist>>() {
                    }.getType();
                    typeAssistArrayList = gson.fromJson(data.toString(), listType);

                    for(TypeAssist dto : typeAssistArrayList)
                    {
                        db.execSQL("delete from "+TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_ID+" = "+dto.getTaid());
                    }

                    if(typeAssistArrayList!=null && typeAssistArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Typeassist Count: "+typeAssistArrayList.size());

                        for (TypeAssist typeAssist: typeAssistArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("TA id: "+typeAssist.getTaid());
                            System.out.println("TA Code: "+typeAssist.getTacode());
                            System.out.println("TA Type: "+typeAssist.getTatype());
                            System.out.println("-----------------------------------------------------------------");*/

                            helper.bind(taidColumn, typeAssist.getTaid());
                            helper.bind(tacodeColumn, typeAssist.getTacode());
                            helper.bind(tanameColumn, typeAssist.getTaname());
                            helper.bind(tatypeColumn, typeAssist.getTatype());
                            helper.bind(tacuserColumn, typeAssist.getCuser());
                            helper.bind(tacdtzColumn, CommonFunctions.getParseDatabaseDateFormat(typeAssist.getCdtz()));
                            helper.bind(tamuserColumn, typeAssist.getMuser());
                            helper.bind(tamdtzColumn, CommonFunctions.getParseDatabaseDateFormat(typeAssist.getMdtz()));
                            //helper.bind(taisdeletedColumn, typeAssist.getIsdeleted());
                            helper.bind(taparentColumn, typeAssist.getParent());
                            helper.bind(tabuidColumn, typeAssist.getBuid());
                            helper.execute();
                        }

                    }
                    isCompleted=true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    typeAssistArrayList=null;
                }
                break;
            case 4:
                try {
                    db.execSQL("delete from "+ Geofence_Table.TABLE_NAME);
                    geofenceArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, Geofence_Table.TABLE_NAME);
                    final int gfidColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_ID);
                    final int gfcodeColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_CODE);
                    final int gfnameColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_NAME);
                    final int gfpointsColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_GEOFENCE_POINTS);
                    final int gfenableColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_ENABLE);

                    final int gfpeopleColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_PEOPLEID);
                    final int gffromdateColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_FROMDATE);
                    final int gfuptodateColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_UPTODATE);
                    final int gfidentifierColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_IDENTIFIER);
                    final int gfstarttimeColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_STARTTIME);
                    final int gfendtimeColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_ENDTIME);
                    final int gfbuidColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Geofence>>() {
                    }.getType();
                    geofenceArrayList = gson.fromJson(data.toString(), listType);
                    if(geofenceArrayList!=null && geofenceArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------Geofence Count: "+geofenceArrayList.size());

                        for (Geofence geofence: geofenceArrayList) {
                            helper.prepareForReplace();
                            helper.bind(gfidColumn, geofence.getGfid());
                            helper.bind(gfcodeColumn, geofence.getGfcode());
                            helper.bind(gfnameColumn, geofence.getGfname());
                            helper.bind(gfpointsColumn, geofence.getGeofence());
                            helper.bind(gfenableColumn, geofence.getEnable());
                            helper.bind(gfpeopleColumn, geofence.getPeopleid());
                            helper.bind(gffromdateColumn, CommonFunctions.getParseDatabaseDateFormat(geofence.getFromdt()));
                            helper.bind(gfuptodateColumn, CommonFunctions.getParseDatabaseDateFormat(geofence.getUptodt()));
                            helper.bind(gfidentifierColumn, geofence.getIdentifier());
                            helper.bind(gfstarttimeColumn, (geofence.getStarttime()));
                            helper.bind(gfendtimeColumn, (geofence.getEndtime()));
                            helper.bind(gfbuidColumn, geofence.getBuid());

                           /* System.out.println("GF name: "+geofence.getGfname());
                            System.out.println("GF code: "+geofence.getGfcode());
                            System.out.println("GF id: "+geofence.getGfid());*/
                            System.out.println("-----------------------------------------------------------------");

                            /*System.out.println("GF name: "+geofence.getGfname());
                            System.out.println("GF code: "+geofence.getGfcode());
                            System.out.println("GF points: "+geofence.getGeofence());
                            System.out.println("GF starttime: "+geofence.getStarttime());
                            System.out.println("GF endtime: "+geofence.getEndtime());
                            System.out.println("GF fromdate: "+geofence.getFromdt());
                            System.out.println("GF todate: "+geofence.getUptodt());*/

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    geofenceArrayList=null;
                }
                break;
            case 5:
                try {
                    //db.execSQL("delete from "+ People_Table.TABLE_NAME);
                    peopleArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, People_Table.TABLE_NAME);
                    final int pidColumn = helper.getColumnIndex(People_Table.PEOPLE_ID);
                    final int pCodeColumn = helper.getColumnIndex(People_Table.PEOPLE_CODE);
                    final int pLoginidColumn = helper.getColumnIndex(People_Table.PEOPLE_LOGINID);
                    final int pPasswordColumn = helper.getColumnIndex(People_Table.PEOPLE_PASSWORD);
                    final int pLocTrackColumn = helper.getColumnIndex(People_Table.PEOPLE_LOCATIONTRACKING);
                    final int pNameColumn = helper.getColumnIndex(People_Table.PEOPLE_FULLNAME);
                    final int pGenderColumn = helper.getColumnIndex(People_Table.PEOPLE_GENDER);
                    final int pMobileColumn = helper.getColumnIndex(People_Table.PEOPLE_MOBILENO);
                    final int pEmailColumn = helper.getColumnIndex(People_Table.PEOPLE_EMAIL);
                    final int pDeptColumn = helper.getColumnIndex(People_Table.PEOPLE_DEPARTMENT);
                    final int pDesigColumn = helper.getColumnIndex(People_Table.PEOPLE_DESGINATION);
                    final int pTypeColumn = helper.getColumnIndex(People_Table.PEOPLE_TYPE);
                    final int pSaltColumn = helper.getColumnIndex(People_Table.PEOPLE_SALT);
                    final int pEnableColumn = helper.getColumnIndex(People_Table.PEOPLE_ENABLE);
                    final int pDOBColumn = helper.getColumnIndex(People_Table.PEOPLE_DOB);
                    final int pDOJColumn = helper.getColumnIndex(People_Table.PEOPLE_DOJ);
                    final int pReportToColumn = helper.getColumnIndex(People_Table.PEOPLE_REPORTTO);
                    final int pCuserColumn = helper.getColumnIndex(People_Table.PEOPLE_CUSER);
                    final int pCDTZColumn = helper.getColumnIndex(People_Table.PEOPLE_CDTZ);
                    final int pMuserColumn = helper.getColumnIndex(People_Table.PEOPLE_MUSER);
                    final int pMDTZColumn = helper.getColumnIndex(People_Table.PEOPLE_MDTZ);
                    //final int pIsdeletedColumn = helper.getColumnIndex(People_Table.PEOPLE_ISDELETED);
                    final int pbuidColumn = helper.getColumnIndex(People_Table.PEOPLE_BUID);

                    final int pCapLogColumn = helper.getColumnIndex(People_Table.PEOPLE_CAPTURE_M_LOG);
                    final int pCapLogSendToColumn = helper.getColumnIndex(People_Table.PEOPLE_M_LOG_SEND_TO);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<People>>() {
                    }.getType();
                    peopleArrayList = gson.fromJson(data.toString(), listType);

                    for(People dto : peopleArrayList)
                    {
                        db.execSQL("delete from "+People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_ID+" = "+dto.getPeopleid());
                    }

                    if(peopleArrayList!=null && peopleArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Peoples Count: "+peopleArrayList.size());

                        for (People people: peopleArrayList) {

                            /*System.out.println("People getDesignation: "+people.getDesignation());
                            System.out.println("People getPeoplecode: "+people.getPeoplecode());
                            System.out.println("-----------------------------------------------------------------");*/

                            //System.out.println("People getPeoplecode: "+people.getPeoplecode());

                            helper.prepareForReplace();
                            helper.bind(pCodeColumn, people.getPeoplecode());
                            helper.bind(pidColumn, people.getPeopleid());
                            helper.bind(pLoginidColumn, people.getLoginid());
                            helper.bind(pPasswordColumn, people.getPassword());
                            helper.bind(pLocTrackColumn, people.getLocationtracking());
                            helper.bind(pNameColumn, people.getPeoplename());
                            helper.bind(pGenderColumn, people.getGender());
                            helper.bind(pMobileColumn, people.getMobileno());
                            helper.bind(pEmailColumn, people.getEmail());
                            helper.bind(pDeptColumn, people.getDepartment());
                            helper.bind(pDesigColumn, people.getDesignation());
                            helper.bind(pTypeColumn, people.getPeopletype());
                            helper.bind(pSaltColumn, people.getSalt());
                            helper.bind(pEnableColumn, people.getEnable());
                            helper.bind(pDOBColumn, people.getDob());
                            helper.bind(pDOJColumn, people.getDoj());
                            helper.bind(pReportToColumn, people.getReportto());
                            helper.bind(pCuserColumn, people.getCuser());
                            helper.bind(pCDTZColumn, CommonFunctions.getParseDatabaseDateFormat(people.getCdtz()));
                            helper.bind(pMuserColumn, people.getMuser());
                            helper.bind(pMDTZColumn, CommonFunctions.getParseDatabaseDateFormat(people.getMdtz()));
                            //helper.bind(pIsdeletedColumn, people.getIsdeleted());
                            helper.bind(pbuidColumn, people.getBuid());
                            helper.bind(pCapLogColumn, people.getCapturemlog());
                            helper.bind(pCapLogSendToColumn,people.getMlogsendto());
                            helper.execute();
                        }

                    }
                    isCompleted=true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    peopleArrayList=null;
                }
                break;
            case 6:
                try {
                    //db.execSQL("delete from "+ Group_Table.TABLE_NAME);
                    groupArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, Group_Table.TABLE_NAME);
                    final int gidColumn = helper.getColumnIndex(Group_Table.GROUP_ID);
                    final int gNameColumn = helper.getColumnIndex(Group_Table.GROUP_NAME);
                    final int gEnableColumn = helper.getColumnIndex(Group_Table.GROUP_ENABLE);
                    final int gCDTZColumn = helper.getColumnIndex(Group_Table.GROUP_CDTZ);
                    final int gMDTZColumn = helper.getColumnIndex(Group_Table.GROUP_MDTZ);
                    //final int gIsDeletedColumn = helper.getColumnIndex(Group_Table.GROUP_ISDELETED);
                    final int gCuserColumn = helper.getColumnIndex(Group_Table.GROUP_CUSER);
                    final int gMuserColumn = helper.getColumnIndex(Group_Table.GROUP_MUSER);
                    final int gbuidColumn = helper.getColumnIndex(Group_Table.GROUP_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Group>>() {
                    }.getType();
                    groupArrayList = gson.fromJson(data.toString(), listType);

                    for(Group dto : groupArrayList)
                    {
                        db.execSQL("delete from "+Group_Table.TABLE_NAME +" where "+Group_Table.GROUP_ID+" = "+dto.getGroupid());
                    }

                    if(groupArrayList!=null &&  groupArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------Group Count: "+groupArrayList.size());

                        for (Group group: groupArrayList) {
                            helper.prepareForReplace();
                            helper.bind(gidColumn, group.getGroupid());
                            helper.bind(gNameColumn, group.getGroupname());
                            helper.bind(gEnableColumn, group.getEnable());
                            helper.bind(gCDTZColumn, CommonFunctions.getParseDatabaseDateFormat(group.getCdtz()));
                            helper.bind(gMDTZColumn, CommonFunctions.getParseDatabaseDateFormat(group.getMdtz()));
                            //helper.bind(gIsDeletedColumn, group.getIsdeleted());
                            helper.bind(gCuserColumn, group.getCuser());
                            helper.bind(gMuserColumn, group.getMuser());
                            helper.bind(gbuidColumn, group.getBuid());

                            /*System.out.println("group Enable: "+group.getEnable());
                            System.out.println("groupid: "+group.getGroupid());
                            System.out.println("-----------------------------------------------------------------");*/

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    groupArrayList=null;
                }
                break;
            case 7:
                try {
                    db.execSQL("delete from "+ AttendanceHistoy_Table.TABLE_NAME);
                    attendanceHistoryArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, AttendanceHistoy_Table.TABLE_NAME);
                    final int ahPelogColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PELOGID);
                    final int ahPeopleCodeColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PEOPLECODE);
                    final int ahDatetimeColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_DATETIME);
                    final int ahPunchStatusColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PUNCHSTATUS);
                    final int ahPunchTypeColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PUNCHTYPE);
                    final int ahCuserColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_CUSER);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<AttendanceHistory>>() {
                    }.getType();
                    attendanceHistoryArrayList = gson.fromJson(data.toString(), listType);

                    if(attendanceHistoryArrayList!=null && attendanceHistoryArrayList.size()>0)
                    {
                        for (AttendanceHistory attendanceHistory: attendanceHistoryArrayList) {
                            helper.prepareForReplace();
                            helper.bind(ahPelogColumn, attendanceHistory.getPelogid());
                            helper.bind(ahPeopleCodeColumn, attendanceHistory.getPeoplecode());
                            helper.bind(ahDatetimeColumn, attendanceHistory.getDatetime());
                            helper.bind(ahPunchStatusColumn, attendanceHistory.getPunchstatus());
                            helper.bind(ahPunchTypeColumn, attendanceHistory.getPeventtype());
                            helper.bind(ahCuserColumn, attendanceHistory.getCuser());

                            helper.execute();
                        }

                    }
                    isCompleted=true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    attendanceHistoryArrayList=null;
                }
                break;
            case 8:
                try {
                    //db.execSQL("delete from "+ Question_Table.TABLE_NAME);
                    questionArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, Question_Table.TABLE_NAME);
                    final int qCodeColumn = helper.getColumnIndex(Question_Table.QUESTION_ID);
                    final int qNameColumn = helper.getColumnIndex(Question_Table.QUESTION_NAME);
                    final int qOptionsColumn = helper.getColumnIndex(Question_Table.QUESTION_OPTIONS);
                    final int qMinColumn = helper.getColumnIndex(Question_Table.QUESTION_MIN);
                    final int qMaxColumn = helper.getColumnIndex(Question_Table.QUESTION_MAX);
                    final int qAlertonColumn = helper.getColumnIndex(Question_Table.QUESTION_ALERTON);
                    final int qCdtzColumn = helper.getColumnIndex(Question_Table.QUESTION_CDTZ);
                    final int qMdtzColumn = helper.getColumnIndex(Question_Table.QUESTION_MDTZ);
                    //final int qIsdeletedColumn = helper.getColumnIndex(Question_Table.QUESTION_ISDELETED);
                    final int qCuserColumn = helper.getColumnIndex(Question_Table.QUESTION_CUSER);
                    final int qMuserColumn = helper.getColumnIndex(Question_Table.QUESTION_MUSER);
                    final int qTypeColumn = helper.getColumnIndex(Question_Table.QUESTION_TYPE);
                    final int qUnitColumn = helper.getColumnIndex(Question_Table.QUESTION_UNIT);
                    final int qBuidColumn = helper.getColumnIndex(Question_Table.QUESTION_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Question>>() {
                    }.getType();
                    questionArrayList = gson.fromJson(data.toString(), listType);

                    for(Question dto : questionArrayList)
                    {
                        db.execSQL("delete from "+Question_Table.TABLE_NAME +" where "+Question_Table.QUESTION_ID+" = "+dto.getQuestionid());
                    }

                    if(questionArrayList!=null && questionArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------Question Count: "+questionArrayList.size());
                        for (Question question: questionArrayList) {
                            helper.prepareForReplace();

                            System.out.println("Question id: "+question.getQuestionid());
                            System.out.println("Question name: "+question.getQuestionname());
                            System.out.println("Question options: "+question.getOptions());
                            System.out.println("Question type: "+question.getType());
                            System.out.println("-----------------------------------------------------------------------------------");

                            //System.out.println("Quset Code: "+question.getQuestionid());
                            helper.bind(qCodeColumn, question.getQuestionid());
                            helper.bind(qNameColumn, question.getQuestionname());
                            helper.bind(qOptionsColumn, question.getOptions());
                            helper.bind(qMinColumn, question.getMin());
                            helper.bind(qMaxColumn, question.getMax());
                            helper.bind(qAlertonColumn, question.getAlertOn());
                            helper.bind(qCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(question.getCdtz()));
                            helper.bind(qMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(question.getMdtz()));
                            //helper.bind(qIsdeletedColumn, question.getIsDeleted());
                            helper.bind(qCuserColumn, question.getCuser());
                            helper.bind(qMuserColumn, question.getMuser());
                            helper.bind(qTypeColumn, question.getType());
                            helper.bind(qUnitColumn, question.getUnit());
                            helper.bind(qBuidColumn, question.getBuid());

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    questionArrayList=null;
                }
                break;
            case 9:
                try {
                    //db.execSQL("delete from "+ QuestionSet_Table.TABLE_NAME);
                    questionSetArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, QuestionSet_Table.TABLE_NAME);
                    final int qsCodeColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ID);
                    final int qsAssetidColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ASSETID);
                    final int qsNameColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_NAME);
                    final int qsEnableColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ENABLE);
                    final int qsSeqNoColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_SEQNO);
                    final int qsCdtzColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_CDTZ);
                    final int qsMdtzColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_MDTZ);
                    //final int qsIsdeletedColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ISDELETED);
                    final int qsCuserColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_CUSER);
                    final int qsMuserColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_MUSER);
                    final int qsParentColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_PARENT);
                    final int qsTypeColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_TYPE);
                    final int qsBuidColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_BUID);
                    final int qsUrlColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_URL);

                    /*final int qsassetincludesColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ASSETINCLUDES);
                    final int qsbuincludesColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_BUINCLUDES);*/

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<QuestionSet>>() {
                    }.getType();
                    questionSetArrayList = gson.fromJson(data.toString(), listType);

                    for(QuestionSet dto : questionSetArrayList)
                    {
                        db.execSQL("delete from "+QuestionSet_Table.TABLE_NAME +" where "+QuestionSet_Table.QUESTION_SET_ID+" = "+dto.getQuestionsetid());
                    }

                    if( questionSetArrayList!=null &&  questionSetArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Qset Count: "+questionSetArrayList.size());

                        for (QuestionSet questionSet: questionSetArrayList) {
                            helper.prepareForReplace();
                            /*System.out.println("QSet name: "+questionSet.getQsetname());
                            System.out.println("QSet Assetid: "+questionSet.getAssetid());
                            System.out.println("QSet id: "+questionSet.getQuestionsetid());
                            System.out.println("QSet url: "+questionSet.getUrl());
                            System.out.println("-----------------------------------------------------------------------------------");*/

                            System.out.println("QSet id: "+questionSet.getQuestionsetid());
                            System.out.println("QSet name: "+questionSet.getQsetname());
                            System.out.println("QSet parent: "+questionSet.getParent());
                            System.out.println("QSet type: "+questionSet.getType());

                            helper.bind(qsCodeColumn, questionSet.getQuestionsetid());
                            helper.bind(qsAssetidColumn, questionSet.getAssetid());
                            helper.bind(qsNameColumn, questionSet.getQsetname());
                            helper.bind(qsEnableColumn, questionSet.getEnable());
                            helper.bind(qsSeqNoColumn, questionSet.getSeqno());
                            helper.bind(qsCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSet.getCdtz()));
                            helper.bind(qsMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSet.getMdtz()));
                            //helper.bind(qsIsdeletedColumn, questionSet.getIsdeleted());
                            helper.bind(qsCuserColumn, questionSet.getCuser());
                            helper.bind(qsMuserColumn, questionSet.getMuser());
                            helper.bind(qsParentColumn, questionSet.getParent());
                            helper.bind(qsTypeColumn, questionSet.getType());
                            helper.bind(qsBuidColumn, questionSet.getBuid());
                            helper.bind(qsUrlColumn, questionSet.getUrl());
                            /*helper.bind(qsassetincludesColumn, questionSet.getAssetincludes());
                            helper.bind(qsbuincludesColumn, questionSet.getBuincludes());*/

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    questionSetArrayList=null;
                }
                break;
            case 10:
                try {
                    //db.execSQL("delete from "+ QuestionSetBelonging_Table.TABLE_NAME);
                    questionSetBelongingArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, QuestionSetBelonging_Table.TABLE_NAME);
                    final int qsbIDColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ID);
                    final int qsbIsMandatoryColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ISMANDATORY);
                    final int qsbSeqnoColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_SEQNO);
                    final int qsbCdtzColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_CDTZ);
                    final int qsbMdtzColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MDTZ);
                    //final int qsbIsdeletedColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ISDELETED);
                    final int qsbCuserColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_CUSER);
                    final int qsbMuserColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MUSER);
                    final int qsbQsetcodeColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_QUESTIONSETID);
                    final int qsbQestCodeColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_QUESTIONID);
                    final int qsbMinColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MIN);
                    final int qsbMaxColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MAX);
                    final int qsbAlertonColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ALERTON);
                    final int qsbOptionColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_OPTION);
                    final int qsbBuidColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_BUID);


                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<QuestionSetBelonging>>() {
                    }.getType();
                    questionSetBelongingArrayList = gson.fromJson(data.toString(), listType);

                    /*for(QuestionSetBelonging dto : questionSetBelongingArrayList)
                    {
                        db.execSQL("delete from "+QuestionSetBelonging_Table.TABLE_NAME +" where "+QuestionSetBelonging_Table.QUESTIONSETBELONGING_ID+" = "+dto.getQsbid());
                    }*/

                    db.execSQL("delete from "+QuestionSetBelonging_Table.TABLE_NAME);

                    if(questionSetBelongingArrayList!=null && questionSetBelongingArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------QsetBelonging Count: "+questionSetBelongingArrayList.size());
                        for (QuestionSetBelonging questionSetBelonging: questionSetBelongingArrayList) {
                            helper.prepareForReplace();

                            System.out.println("QSetBelonging qid: "+questionSetBelonging.getQuestionid());
                            System.out.println("QSetBelonging qsetid: "+questionSetBelonging.getQuestionsetid());
                            System.out.println("QSetBelonging qOption: "+questionSetBelonging.getOption());
                            System.out.println("-----------------------------------------------------------------------------------");

                            helper.bind(qsbIDColumn, questionSetBelonging.getQsbid());
                            helper.bind(qsbIsMandatoryColumn, questionSetBelonging.getIsmandatory());
                            helper.bind(qsbSeqnoColumn, questionSetBelonging.getSeqno());
                            helper.bind(qsbCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSetBelonging.getCdtz()));
                            helper.bind(qsbMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSetBelonging.getMdtz()));
                            //helper.bind(qsbIsdeletedColumn, questionSetBelonging.getIsdeleted());
                            helper.bind(qsbCuserColumn, questionSetBelonging.getCuser());
                            helper.bind(qsbMuserColumn, questionSetBelonging.getMuser());
                            helper.bind(qsbQsetcodeColumn, questionSetBelonging.getQuestionsetid());
                            helper.bind(qsbQestCodeColumn, questionSetBelonging.getQuestionid());
                            helper.bind(qsbMinColumn, questionSetBelonging.getMin());
                            helper.bind(qsbMaxColumn, questionSetBelonging.getMax());
                            helper.bind(qsbAlertonColumn, questionSetBelonging.getAlerton());
                            helper.bind(qsbOptionColumn, questionSetBelonging.getOption());
                            helper.bind(qsbBuidColumn, questionSetBelonging.getBuid());

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    questionSetBelongingArrayList=null;
                }
                break;
            case 11:
                try {
                    //db.execSQL("delete from "+ PeopleGroupBelongin_Table.TABLE_NAME);
                    peopleGroupBelongingArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, PeopleGroupBelongin_Table.TABLE_NAME);
                    final int pgbIDColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_ID);
                    final int pgbIsgroupleadColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_ISGROUPLEAD);
                    final int pgbCdtzColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_CDTZ);
                    final int pgbMdtzColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_MDTZ);
                    //final int pgbIsdeletedColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_ISDELETED);
                    final int pgbCuserColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_CUSER);
                    final int pgbGroupcodeColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_GROUPID);
                    final int pgbMuserColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_MUSER);
                    final int pgbPeoplecodeColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_PEOPLEID);
                    final int pgbBuidColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<PeopleGroupBelonging>>() {
                    }.getType();
                    peopleGroupBelongingArrayList = gson.fromJson(data.toString(), listType);

                    for(PeopleGroupBelonging dto : peopleGroupBelongingArrayList)
                    {
                        db.execSQL("delete from "+PeopleGroupBelongin_Table.TABLE_NAME +" where "+PeopleGroupBelongin_Table.PGB_ID+" = "+dto.getPgbid());
                    }

                    if(peopleGroupBelongingArrayList!=null && peopleGroupBelongingArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------peoplegroupbelonging Count: "+peopleGroupBelongingArrayList.size());

                        for (PeopleGroupBelonging peopleGroupBelonging: peopleGroupBelongingArrayList) {
                            helper.prepareForReplace();
                            helper.bind(pgbIDColumn, peopleGroupBelonging.getPgbid());
                            helper.bind(pgbIsgroupleadColumn, peopleGroupBelonging.getIsgrouplead());
                            helper.bind(pgbCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(peopleGroupBelonging.getCdtz()));
                            helper.bind(pgbMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(peopleGroupBelonging.getMdtz()));
                            //helper.bind(pgbIsdeletedColumn, peopleGroupBelonging.getIsdeleted());
                            helper.bind(pgbCuserColumn, peopleGroupBelonging.getCuser());
                            helper.bind(pgbGroupcodeColumn, peopleGroupBelonging.getGroupid());
                            helper.bind(pgbMuserColumn, peopleGroupBelonging.getMuser());
                            helper.bind(pgbPeoplecodeColumn, peopleGroupBelonging.getPeopleid());
                            helper.bind(pgbBuidColumn, peopleGroupBelonging.getBuid());

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    peopleGroupBelongingArrayList=null;
                }
                break;
            case 12:
                try {
                    db.execSQL("delete from "+ SiteList_Table.TABLE_NAME);
                    sitesArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, SiteList_Table.TABLE_NAME);

                    final int buidColumn = helper.getColumnIndex(SiteList_Table.SITE_PEOPLE_BUILD);
                    final int bu_bunameColumn = helper.getColumnIndex(SiteList_Table.BU_NAME);
                    final int bu_bucodeColumn = helper.getColumnIndex(SiteList_Table.BU_CODE);
                    final int siteenableColumn = helper.getColumnIndex(SiteList_Table.SITE_ENABLE);
                    final int cuserColumn = helper.getColumnIndex(SiteList_Table.SITE_PEOPLE_CUSER);
                    final int muserColumn = helper.getColumnIndex(SiteList_Table.SITE_PEOPLE_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(SiteList_Table.SITE_PEOPLE_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(SiteList_Table.SITE_PEOPLE_MDTZ);

                    final int butypeColumn=helper.getColumnIndex(SiteList_Table.BU_TYPE);
                    final int butypenameColumn=helper.getColumnIndex(SiteList_Table.BU_TYPENAME);
                    final int siteinchageColumn=helper.getColumnIndex(SiteList_Table.SITE_INCHARGE);


                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<SiteList>>() {
                    }.getType();
                    sitesArrayList = gson.fromJson(data.toString(), listType);



                    if(sitesArrayList!=null && sitesArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Sites Count: "+sitesArrayList.size());


                        for (SiteList sites: sitesArrayList) {
                            helper.prepareForReplace();
                            helper.bind(buidColumn, sites.getBuid());
                            helper.bind(cuserColumn, sites.getCuser());
                            helper.bind(muserColumn, sites.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(sites.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(sites.getMdtz()));
                            helper.bind(bu_bunameColumn, sites.getBuname());
                            helper.bind(bu_bucodeColumn, sites.getBucode());
                            helper.bind(siteenableColumn, sites.getEnable());
                            helper.bind(butypeColumn, sites.getButype());
                            helper.bind(butypenameColumn, sites.getButypename());
                            helper.bind(siteinchageColumn, sites.getIncharge());

                            /*System.out.println("getBuid: "+sites.getBuid());
                            System.out.println("getBuname: "+sites.getBuname());
                            System.out.println("getBucode: "+sites.getBucode());
                            System.out.println("getEnable: "+sites.getEnable());
                            System.out.println("getButype: "+sites.getButype());
                            System.out.println("getButypename: "+sites.getButypename());
                            System.out.println("getIncharge: "+sites.getIncharge());*/

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    sitesArrayList=null;
                }
                /*try {
                    db.execSQL("delete from "+ Sites_Table.TABLE_NAME);
                    sitesArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, Sites_Table.TABLE_NAME);

                    final int buidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_BUILD);
                    final int bu_bunameColumn = helper.getColumnIndex(Sites_Table.BU_NAME);
                    final int bu_bucodeColumn = helper.getColumnIndex(Sites_Table.BU_CODE);

                    final int sitepeopleidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_ID);
                    final int fromdtColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_FROMDATE);
                    final int uptodtColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_UPTODATE);
                    final int siteownerColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_SITEOWNER);

                    final int peopleidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_ID);
                    final int reporttoColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_REPORTTO);
                    final int shiftColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_SHIFT);
                    final int slnoColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_SLNO);
                    final int postingrevColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_POSTINGREV);
                    final int contractidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_CONTRACTID);
                    final int cuserColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_CUSER);
                    final int muserColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_MDTZ);
                    final int worktypeColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_WORKTYPE);

                    final int sitereportidColumn = helper.getColumnIndex(Sites_Table.SITE_REPORT_ID);
                    final int sitereportnameColumn = helper.getColumnIndex(Sites_Table.SITE_REPORT_NAME);
                    final int siteenableColumn = helper.getColumnIndex(Sites_Table.SITE_ENABLE);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Sites>>() {
                    }.getType();
                    sitesArrayList = gson.fromJson(data.toString(), listType);



                    if(sitesArrayList!=null && sitesArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Sites Count: "+sitesArrayList.size());


                        for (Sites sites: sitesArrayList) {
                            helper.prepareForReplace();
                            helper.bind(sitepeopleidColumn, sites.getSitepeopleid());
                            helper.bind(fromdtColumn, (sites.getFromdt()));
                            helper.bind(uptodtColumn, (sites.getUptodt()));
                            helper.bind(siteownerColumn, sites.getSiteowner());
                            helper.bind(buidColumn, sites.getBuid());
                            helper.bind(peopleidColumn, sites.getPeopleid());
                            helper.bind(reporttoColumn, sites.getReportto());
                            helper.bind(shiftColumn, sites.getShift());
                            helper.bind(slnoColumn, sites.getSlno());
                            helper.bind(postingrevColumn, sites.getPostingrev());
                            helper.bind(contractidColumn, sites.getContractid());
                            helper.bind(cuserColumn, sites.getCuser());
                            helper.bind(muserColumn, sites.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(sites.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(sites.getMdtz()));
                            helper.bind(worktypeColumn, sites.getWorktype());
                            helper.bind(bu_bunameColumn, sites.getBuname());
                            helper.bind(bu_bucodeColumn, sites.getBucode());
                            helper.bind(sitereportidColumn, sites.getReportids());
                            helper.bind(sitereportnameColumn, sites.getReportnames());
                            helper.bind(siteenableColumn, sites.getEnable());

                            helper.execute();
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    sitesArrayList=null;
                }*/
                break;
            case 13:
                try {

                    db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME+" where "+JOBNeed_Table.JOBNEED_IDENTIFIER+" = (select taid from TypeAssist where tacode = '"+Constants.JOB_NEED_IDENTIFIER_TICKET+"') AND "+JOBNeed_Table.JOBNEED_SYNC_STATUS +" NOT IN(0,2)");
                    ticketArrayList=new ArrayList<JobNeed>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, JOBNeed_Table.TABLE_NAME);
                    final int idColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ID);
                    final int descColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_DESC);
                    final int pdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME);
                    final int expdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME);
                    final int gracetimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME);
                    final int recvonserverColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER);
                    final int starttimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME);
                    final int endtimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME);
                    final int gpslocColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION);
                    final int remarkColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK);
                    //final int isdeletedColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED);
                    final int cuserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER);
                    final int muserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ);
                    final int attachmentcountColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT);
                    final int aatopColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP);
                    final int assetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID);
                    final int freqColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY);
                    final int jobidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID);
                    final int jobstatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS);
                    final int jobtypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE);
                    final int performbyColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY);
                    final int priorityColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY);
                    final int qsetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID);
                    final int scantypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE);
                    final int peopleColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID);
                    final int peoplegroupColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID);
                    final int jnidentifierColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER);
                    final int jnparentidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT);
                    final int syncStatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SYNC_STATUS);
                    final int ticketnoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO);
                    final int buidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_BUID);
                    final int seqNoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO);
                    final int ticketCategoryColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY);
                    final int cdtzoffsetColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeed>>() {
                    }.getType();
                    ticketArrayList = gson.fromJson(data.toString(), listType);

                    if(ticketArrayList!=null && ticketArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Ticket Count: "+ticketArrayList.size());

                        for (JobNeed jobNeed : ticketArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("JObneed Desc: "+jobNeed.getJobdesc());
                            System.out.println("JObneed TICKET ID: "+jobNeed.getTicketno());
                            System.out.println("JObneed Identifier: "+jobNeed.getIdentifier());
                            System.out.println("JObneed Type: "+jobNeed.getJobtype());
                            System.out.println("Assigned getAatop: "+jobNeed.getAatop());
                            System.out.println("Assigned Group: "+jobNeed.getGroupid());
                            System.out.println("Assigned People: "+jobNeed.getPeopleid());
                            System.out.println("Assigned Parent: "+jobNeed.getParent());
                            System.out.println("Jobneed status: "+jobNeed.getJobstatus());
                            System.out.println("-----------------------------------------------------");*/

                            helper.bind(idColumn, jobNeed.getJobneedid());
                            helper.bind(descColumn, jobNeed.getJobdesc());
                            helper.bind(jobidColumn, jobNeed.getJobid());
                            helper.bind(freqColumn, jobNeed.getFrequency());
                            /*helper.bind(pdateColumn, CommonFunctions.getParseDate(jobNeed.getPlandatetime()));
                            helper.bind(expdateColumn, CommonFunctions.getParseDate(jobNeed.getExpirydatetime()));*/
                            helper.bind(pdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getPlandatetime()));
                            helper.bind(expdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getExpirydatetime()));
                            helper.bind(gracetimeColumn, jobNeed.getGracetime());
                            helper.bind(assetcodeColumn, jobNeed.getAssetid());
                            helper.bind(qsetcodeColumn, jobNeed.getQuestionsetid());
                            helper.bind(peoplegroupColumn, jobNeed.getGroupid());
                            helper.bind(aatopColumn, jobNeed.getAatop());
                            helper.bind(jobstatusColumn, jobNeed.getJobstatus());
                            helper.bind(jobtypeColumn, jobNeed.getJobtype());
                            helper.bind(scantypeColumn, jobNeed.getScantype());
                            helper.bind(recvonserverColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getReceivedonserver()));
                            helper.bind(priorityColumn, jobNeed.getPriority());
                            helper.bind(starttimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getStarttime()));
                            helper.bind(endtimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getEndtime()));
                            helper.bind(performbyColumn, jobNeed.getPerformedby());
                            helper.bind(gpslocColumn, jobNeed.getGpslocation());
                            helper.bind(remarkColumn, jobNeed.getRemarks());
                            //helper.bind(isdeletedColumn, jobNeed.getIsdeleted());
                            helper.bind(cuserColumn, jobNeed.getCuser());
                            helper.bind(muserColumn, jobNeed.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getMdtz()));
                            helper.bind(attachmentcountColumn, jobNeed.getAttachmentcount());
                            helper.bind(peopleColumn, jobNeed.getPeopleid());
                            helper.bind(syncStatusColumn,"-1");
                            helper.bind(jnidentifierColumn,jobNeed.getIdentifier());
                            helper.bind(jnparentidColumn,jobNeed.getParent());
                            helper.bind(ticketnoColumn, jobNeed.getTicketno());
                            helper.bind(buidColumn, jobNeed.getBuid());
                            helper.bind(seqNoColumn, jobNeed.getSeqno());
                            helper.bind(ticketCategoryColumn, jobNeed.getTicketcategory());
                            helper.bind(cdtzoffsetColumn, jobNeed.getCtzoffset());


                            helper.execute();
                        }

                    }

                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper = null;
                    ticketArrayList = null;
                }
                break;
            case 14:
                try {
                    //addressid	address	landmark	postalcode	mobileno	phoneno	faxno
                    // website	email	gpslocation	addresstype	city	state	country	peopleid
                    // siteid	cuser	muser	cdtz	mdtz	assetid	buid

                    db.execSQL("delete from "+ Address_Table.TABLE_NAME);
                    addressArrayList=new ArrayList<Address>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, Address_Table.TABLE_NAME);
                    final int idColumn = helper.getColumnIndex(Address_Table.ADDRESS_ID);
                    final int addressColumn = helper.getColumnIndex(Address_Table.ADDRESS_ADDRESS);
                    final int landmarkColumn = helper.getColumnIndex(Address_Table.ADDRESS_LANDMARK);
                    final int postalcodeColumn = helper.getColumnIndex(Address_Table.ADDRESS_POSTALCODE);
                    final int mobilenoColumn = helper.getColumnIndex(Address_Table.ADDRESS_MOBILENO);
                    final int phonenoColumn = helper.getColumnIndex(Address_Table.ADDRESS_PHONENO);
                    final int faxnoColumn = helper.getColumnIndex(Address_Table.ADDRESS_FAXNO);
                    final int websiteColumn = helper.getColumnIndex(Address_Table.ADDRESS_WEBSITE);
                    final int emailColumn = helper.getColumnIndex(Address_Table.ADDRESS_EMAIL);
                    final int gpslocColumn = helper.getColumnIndex(Address_Table.ADDRESS_GPSLOCATION);
                    final int addtypeColumn = helper.getColumnIndex(Address_Table.ADDRESS_TYPE);
                    final int cityColumn = helper.getColumnIndex(Address_Table.ADDRESS_CITY);
                    final int stateColumn = helper.getColumnIndex(Address_Table.ADDRESS_STATE);
                    final int countryColumn = helper.getColumnIndex(Address_Table.ADDRESS_COUNTRY);
                    final int peopleidColumn = helper.getColumnIndex(Address_Table.ADDRESS_PEOPLEID);
                    final int siteidColumn = helper.getColumnIndex(Address_Table.ADDRESS_SITEID);
                    final int cuserColumn = helper.getColumnIndex(Address_Table.ADDRESS_CUSER);
                    final int muserColumn = helper.getColumnIndex(Address_Table.ADDRESS_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(Address_Table.ADDRESS_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(Address_Table.ADDRESS_MDTZ);
                    final int assetidColumn = helper.getColumnIndex(Address_Table.ADDRESS_ASSETID);
                    final int buidColumn = helper.getColumnIndex(Address_Table.ADDRESS_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Address>>() {
                    }.getType();
                    addressArrayList = gson.fromJson(data.toString(), listType);

                    if(addressArrayList!=null && addressArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Address Count: "+addressArrayList.size());

                        for (Address address : addressArrayList) {
                            helper.prepareForReplace();

                            helper.bind(idColumn, address.getAddressid());
                            helper.bind(addressColumn, address.getAddress());
                            helper.bind(landmarkColumn, address.getLandmark());
                            helper.bind(postalcodeColumn, address.getPostalcode());
                            helper.bind(mobilenoColumn, (address.getMobileno()));
                            helper.bind(phonenoColumn, (address.getPhoneno()));
                            helper.bind(faxnoColumn, address.getFaxno());
                            helper.bind(websiteColumn, address.getWebsite());
                            helper.bind(emailColumn, address.getEmail());
                            helper.bind(gpslocColumn, address.getGpslocation());
                            helper.bind(addtypeColumn, address.getAddresstype());
                            helper.bind(cityColumn, address.getCity());
                            helper.bind(stateColumn, address.getState());
                            helper.bind(countryColumn, address.getCountry());
                            helper.bind(peopleidColumn, (address.getPeopleid()));
                            helper.bind(siteidColumn, address.getSiteid());
                            helper.bind(cuserColumn, address.getCuser());
                            helper.bind(muserColumn, address.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(address.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(address.getMdtz()));
                            helper.bind(assetidColumn, address.getAssetid());
                            helper.bind(buidColumn, address.getCuser());

                            /*System.out.println("Address Assetid: "+address.getAssetid());
                            System.out.println("Address address: "+address.getAddress());*/

                            helper.execute();
                        }

                    }

                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper = null;
                    addressArrayList = null;
                }
                break;
            case 15:
                try {
                    othersiteArrayList=new ArrayList<Othersite>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Othersite>>() {
                    }.getType();
                    othersiteArrayList = gson.fromJson(data.toString(), listType);
                    if(othersiteArrayList!=null && othersiteArrayList.size()>0)
                    {
                        otherSiteListPref.edit().clear().apply();

                        ArrayList<String>othList=new ArrayList<>();
                        System.out.println("------------------------------------------------------othersiteArrayList Count: "+othersiteArrayList.size());
                        //DatabaseQuries.otherSiteArraylist=new ArrayList<>();
                        for(Othersite othersite:othersiteArrayList)
                        {
                            othList.add(othersite.getOthersite());
                        }

                        Set<String> set = new HashSet<String>();
                        set.addAll(othList);
                        otherSiteListPref.edit().putStringSet(Constants.OTHER_SITES, set).apply();

                    }

                    isCompleted=true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                }
                break;
            case 16:
                try {
                    db.execSQL("delete from "+ SitesInfo_Table.TABLE_NAME);
                    sitesInformationArrayList=new ArrayList<SitesInformation>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    //@contract@lrev@contractid@constartdate@conenddate@sincharge@simob@siteid@site@address@landmark@postalcode@mobileno@gpslocation@totstrength@strength
                    helper = new DatabaseUtils.InsertHelper(db, SitesInfo_Table.TABLE_NAME);
                    final int contractColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACT);
                    final int revisionColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_REVISION_NUMBER);
                    final int contractidColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACTID);
                    final int contractsdateColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACT_SDATE);
                    final int contractedateColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACT_EDATE);
                    final int siteinchageColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_INCHARGE);
                    final int sitemobileColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEMOBILE);
                    final int siteidColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEID);
                    final int sitenameColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_SITENAME);
                    final int siteaddressColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEADDRESS);
                    final int sitelandmarkColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_SITELANDMARK);
                    final int postalcodeColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_POSTALCODE);
                    final int mobileColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_MOBILE);
                    final int sitegpsColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEGPS);
                    final int tstrengthColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_TOTALSTRENGTH);
                    final int strengthColumn = helper.getColumnIndex(SitesInfo_Table.SITE_INFO_STRENGTH);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<SitesInformation>>() {
                    }.getType();
                    sitesInformationArrayList = gson.fromJson(data.toString(), listType);


                    /*{"deviceid":"356478087090385","loginid":"P065410","password":"P065410","query":"select * from get_people_template_list(152750363842728 , 152759623490714)","servicename":"Select","sitecode":"SUKHI.HOMUMBAI","story":"1","tzoffset":"330"}
                    2019-05-14 17:02:16.819 245-408/? I/BufferQueueProducer: [co

                    {"status": "True", "username": "P065410", "story": "", "nrow": 14, "appversion": "1.3.4.4", "auth": "True", "loggername": "SUKHI|P065410 - ", "clientcode": "SUKHI", "msg": "Total (14) record fetched.", "reason": "OK", "row_data": "@,152785746298151,SHORTAGE REPORT,153579365380306 152897793397550 153189116212021 152897793401097 152897793398832 153579365380297 152897793398830 6325755988051062 152897793401104 152897793400189 152897793397546 153579365379773 152897793398846 153579365380300 152897793401113 153862942935864 152897793401118 152897793398811 152897793401116 152897793398808 152897793401106 152897793400200 152897793398844 152897793401108 152897793398841 153579365379769 152897793401176 152897793401099 152897793401135 152897793398813 152897793401111 152897793401128 152897793401102 152897793401170 6342957630162243 152897793401125 152897793401130 152897793401179 152897793397543 152897793401122 152897793398848 152897793401043 153579365380304 153259693748846 152897793401132 152897793398836 152897793400192 152897793401165 152897793401137 152897793401094 152897793400194 153579365379778 152897793398818 152897793401120 152897793398839 152897793398834 152897793401181 152897793401174 153120575318944 152897793398863@,152785746298220,OFFICE VISIT REPORT - SPS,152897793401120 152897793401170 152897793401116 153259693748846 152897793401108 152897793401128 152897793401106 152897793401099 152897793401179 152897793401104 152897793401102 152897793401094 152897793401137 152897793401165 152897793401132 152897793401111 152897793401122 152897793401181 153120575318944 152750409662689 152897793401130 152897793401174 152897793401176 152897793401135 152897793401125 152897793401118 152897793401113 152897793401097@,152785746298252,ATM/ BRANCHES SITE VISIT REPORT - AO / AM / NR,152897793398818 152897793398811 152897793398813 152897793398808@,152785746298262,COLLECTION DEPARTMENT QUESTIONAIRE,152897793398863 152897793398846 152897793400200 152897793401043 152897793398848 6325755988051062 152897793398844 152897793398841 153189116212021 152897793397543 6342957630162243@,152785746298268,SPECIAL VISIT REPORT,152897793401135 152897793400192 152897793401094 152897793401137 152897793401120 152897793398818 152897793398836 152897793401165 152897793398813 152897793401122 153579365380306 152897793401102 152897793401099 153579365379769 152897793398841 152897793398811 152897793398808 152897793401106 152897793400200 153579365380300 152897793401113 152897793398846 6325755988051062 152897793398832 152897793398863 152897793400194 152897793401176 152897793401130 152897793397543 -1 153259693748846 152897793397546 152897793401128 153862942935864 152897793400189 153579365379778 152897793398839 152897793401181 152897793398834 152897793401174 152897793397550 153120575318944 153189116212021 152897793401097 153579365380297 152897793398830 152897793401104 152897793401118 153579365379773 152897793401116 152897793401108 152897793398844 152897793401111 152897793401125 6342957630162243 152897793401170 152897793401179 152897793398848 153579365380304 152897793401043 152897793401132@,152785746298272,NIGHT ROUNDER VISIT REPORT,152897793401111 152897793401102 152897793398839 153579365379769 152897793397550 152897793398813 152897793401106 152897793401176 152897793398808 152897793398836 152897793401097 152897793401165 153189116212021 153259693748846 152897793401181 152897793398811 152897793398848 152897793401174 152897793401122 153120575318944 153579365380306 152897793401099 152897793398834 152897793401043 153579365380304 152897793401132 153579365379773 152897793401108 152897793398844 152897793400192 152897793401179 152897793401118 152897793401120 152897793401170 152897793397543 6342957630162243 152897793401094 152897793401137 152897793401125 152897793397546 152897793398863 6325755988051062 152897793401116 152897793401135 152897793401104 152897793398832 152897793401128 153862942935864 152897793401113 153579365380300 152897793400194 152897793400189 152897793398846 152897793401130 152897793398841 153579365379778 152897793400200 15289779
                        2019-05-14 17:02:18.991 13744-13754/? I/art: Enter while loop.*/




                    if(sitesInformationArrayList!=null && sitesInformationArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Site info Count: "+sitesInformationArrayList.size());

                        for (SitesInformation address : sitesInformationArrayList) {
                            helper.prepareForReplace();

                            helper.bind(contractColumn, address.getContract());
                            helper.bind(revisionColumn, address.getLrev());
                            helper.bind(contractidColumn, address.getContractid());
                            helper.bind(contractsdateColumn, address.getConstartdate());
                            helper.bind(contractedateColumn, (address.getConenddate()));
                            helper.bind(siteinchageColumn, (address.getSincharge()));
                            helper.bind(sitemobileColumn, address.getSimob());
                            helper.bind(siteidColumn, address.getSiteid());
                            helper.bind(sitenameColumn, address.getSite());
                            helper.bind(siteaddressColumn, address.getAddress());
                            helper.bind(sitelandmarkColumn, address.getLandmark());
                            helper.bind(postalcodeColumn, address.getPostalcode());
                            helper.bind(mobileColumn, address.getMobileno());
                            helper.bind(sitegpsColumn, address.getGpslocation());
                            helper.bind(tstrengthColumn, (address.getTotstrength()));
                            helper.bind(strengthColumn, address.getStrength());
                            helper.execute();
                        }

                    }

                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper = null;
                    sitesInformationArrayList = null;
                }
                break;
            case 17:
                try {
                    db.execSQL("delete from "+ TemplateList_Table.TABLE_NAME);
                    templateListArrayList=new ArrayList<TemplateList>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, TemplateList_Table.TABLE_NAME);
                    final int tQsetIdColumn = helper.getColumnIndex(TemplateList_Table.TEMPLATE_QSETID);
                    final int tQSetNameColumn = helper.getColumnIndex(TemplateList_Table.TEMPLATE_QSETNAME);
                    final int tSitesColumn = helper.getColumnIndex(TemplateList_Table.TEMPLATE_SITES);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<TemplateList>>() {
                    }.getType();
                    templateListArrayList = gson.fromJson(data.toString(), listType);

                    if(templateListArrayList!=null && templateListArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------template Count: "+templateListArrayList.size());

                        for (TemplateList address : templateListArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("address.getQsetname(): "+address.getQsetname());
                            System.out.println("address.getQuestionsetid(): "+address.getQuestionsetid());
                            System.out.println("address.getSites(): "+address.getSites());*/

                            helper.bind(tQsetIdColumn, address.getQuestionsetid());
                            helper.bind(tQSetNameColumn, address.getQsetname());
                            helper.bind(tSitesColumn, address.getSites());
                            helper.execute();
                        }

                    }

                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper = null;
                    templateListArrayList = null;
                }
                break;
            case 18:
                try {
                    db.execSQL("delete from "+ AssignedSitePeople_Table.TABLE_NAME);
                    assignedSitesPeoplesArrayList=new ArrayList<AssignedSitesPeoples>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, AssignedSitePeople_Table.TABLE_NAME);
                    final int aspIDColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_ID);
                    final int aspCodeColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_CODE);
                    final int aspNameColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_FULLNAME);
                    final int aspMobileColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_MOBILENO);
                    final int aspEmailColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_EMAIL);
                    final int aspDesignationColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_DESGINATION);
                    final int aspSitesColumn = helper.getColumnIndex(AssignedSitePeople_Table.PEOPLE_SITES);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<AssignedSitesPeoples>>() {
                    }.getType();
                    assignedSitesPeoplesArrayList = gson.fromJson(data.toString(), listType);

                    if(assignedSitesPeoplesArrayList!=null && assignedSitesPeoplesArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Assigned site people Count: "+assignedSitesPeoplesArrayList.size());


                        for (AssignedSitesPeoples assignedSitesPeoples : assignedSitesPeoplesArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("assignedSitesPeoples.getDesignationname(): "+assignedSitesPeoples.getDesignationname());
                            System.out.println("assignedSitesPeoples.getPeoplecode(): "+assignedSitesPeoples.getPeoplecode());*/

                            helper.bind(aspIDColumn, assignedSitesPeoples.getPeopleid());
                            helper.bind(aspCodeColumn, assignedSitesPeoples.getPeoplecode());
                            helper.bind(aspNameColumn, assignedSitesPeoples.getPeoplename());
                            helper.bind(aspMobileColumn, assignedSitesPeoples.getMobileno());
                            helper.bind(aspEmailColumn, assignedSitesPeoples.getEmail());
                            helper.bind(aspDesignationColumn, assignedSitesPeoples.getDesignationname());
                            helper.bind(aspSitesColumn, assignedSitesPeoples.getSites());
                            helper.execute();
                        }

                    }

                    isCompleted=true;

                } catch (Exception e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper = null;
                    assignedSitesPeoplesArrayList = null;
                }
                break;
            /*case 18:
                try {
                    sitesArrayListOld=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Sites>>() {
                    }.getType();
                    sitesArrayListOld = gson.fromJson(data.toString(), listType);



                    if(sitesArrayListOld!=null && sitesArrayListOld.size()>0)
                    {
                        System.out.println("------------------------------------------------------OLD Sites Count: "+sitesArrayListOld.size());


                        for (Sites sites: sitesArrayListOld) {
                            System.out.println("sites.getBuname(): "+sites.getBuname());
                            System.out.println("sites.getReportto(): "+sites.getReportto());
                            System.out.println("sites.getReportids(): "+sites.getReportids());
                            System.out.println("sites.getReportnames(): "+sites.getReportnames());
                        }

                    }
                    isCompleted=true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                    isCompleted=false;
                    CommonFunctions.ErrorLog("\n"+e.toString()+"\n");
                } finally {
                    helper=null;
                    sitesArrayListOld=null;
                }
                break;*/
        }
        return isCompleted;
    }



    private JSONObject getDataObject(JSONObject ob)
    {
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

        return dataObject;
    }
}

