package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.Group;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.ResponseReturnIdData;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Model.UploadJobneedParameter;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;
import com.youtility.intelliwiz20.Utils.ServerRequest;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdhocTicketActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IDialogEventListeners {
    private EditText jobDescEdittext;
    private EditText jobRemarkEdittext;
    private Spinner jobPrioritySpinner;
    private Spinner jobAssetLocationSpinner;
    private RadioGroup jobUserGroupRadioGroup;
    private int checkedRadioButton;
    private Spinner jobPeopleGroupSpinner;
    private int selectedRadioButton=-1;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    private ArrayList<TypeAssist> priorityList;
    private ArrayList<String>priorityNameList;
    private ArrayList<Asset> assetLocationList;
    private ArrayList<String>assetLocationNameList;
    private Spinner jobTicketCategorySpinner;
    private ArrayList<TypeAssist>tCategoryList;
    private ArrayList<String>tCategoryNameList;

    private SharedPreferences loginDetailPref;
    private SharedPreferences adhocJobPef;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationMainPref;
    private ArrayList<People>peopleList;
    private PeopleDAO peopleDAO;
    private JobNeedDAO jobNeedDAO;

    private ArrayList<Group>groupList;
    private GroupDAO groupDAO;
    private AttachmentDAO attachmentDAO;

    private Boolean isAttachmentFabOpen = false;
    private FloatingActionButton fabAddAttachment,fabPicture,fabVideo, fabAudio;
    private Animation rotate_forward,rotate_backward,fab_open,fab_close;
    private Button showAttachmentButton;
    private TextView attachmentCountView;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;



    private CheckNetwork checkNetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhoc_ticket);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(AdhocTicketActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(AdhocTicketActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                System.out.println("==========="+accessValue);
                System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
                System.out.println("lattitude"+latitude);
                System.out.println("longitude"+longitude);


                if(accessValue == 0) {
                    if(CommonFunctions.isPermissionGranted(AdhocTicketActivity.this))
                    {
                        if(saveData()) {
                            Snackbar.make(view, getResources().getString(R.string.adhoc_ticket_succeed), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Intent TicketActivity = new Intent(AdhocTicketActivity.this, TicketViewActivity.class);
                            startActivityForResult(TicketActivity, 0);

                        }
                        else
                        {
                            Snackbar.make(view, getResources().getString(R.string.adhoc_fill_req_info), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                    else
                        Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                }else if (accessValue == 1) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
                } else if (accessValue == 2) {
                    customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
                    System.out.println("==========="+accessValue);
                } else if (accessValue == 3) {
                    customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
                    System.out.println("==========="+accessValue);
                } else if (accessValue == 4) {
                    customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
                    System.out.println("==========="+accessValue);
                }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                    System.out.println("===========lat long==0.0");
                }
            }
        });

        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        adhocJobPef.edit().putLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()).apply();

        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        fabAddAttachment=(FloatingActionButton)findViewById(R.id.fabAddAttachment);
        fabAddAttachment.setOnClickListener(this);
        fabPicture=(FloatingActionButton)findViewById(R.id.fabPicture);
        fabPicture.setOnClickListener(this);
        fabVideo=(FloatingActionButton)findViewById(R.id.fabVideo);
        fabVideo.setOnClickListener(this);
        fabAudio=(FloatingActionButton)findViewById(R.id.fabAudio);
        fabAudio.setOnClickListener(this);



        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        checkNetwork=new CheckNetwork(AdhocTicketActivity.this);
        applicationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);

        typeAssistDAO=new TypeAssistDAO(AdhocTicketActivity.this);
        assetDAO=new AssetDAO(AdhocTicketActivity.this);

        priorityList=typeAssistDAO.getEventList("Priority");
        priorityNameList=new ArrayList<>();
        for(int i=0;i<priorityList.size();i++)
        {
            if(priorityList.get(i).getTaid()!=-1)
            {
                priorityNameList.add(priorityList.get(i).getTaname());
            }
        }

        assetLocationList=assetDAO.getAssetLocationList(Constants.IDENTIFIER_ASSET, Constants.TACODE_LOCATION);
        assetLocationNameList=new ArrayList<>();
        for(int i=0;i<assetLocationList.size();i++)
        {
            if(assetLocationList.get(i).getAssetid()!=-1)
            {
                assetLocationNameList.add(assetLocationList.get(i).getAssetname());
            }
        }

        tCategoryList=typeAssistDAO.getEventList("Ticket Category");
        tCategoryNameList=new ArrayList<>();
        for(int i=0;i<tCategoryList.size();i++)
        {
            if(tCategoryList.get(i).getTaid()!=-1)
            {
                tCategoryNameList.add(tCategoryList.get(i).getTaname());
            }
        }

        peopleDAO=new PeopleDAO(AdhocTicketActivity.this);
        peopleList=new ArrayList<People>();
        peopleList=peopleDAO.getPeopleList();

        groupDAO=new GroupDAO(AdhocTicketActivity.this);
        groupList=new ArrayList<Group>();
        groupList=groupDAO.getGroupList();

        attachmentDAO=new AttachmentDAO(AdhocTicketActivity.this);
        jobNeedDAO=new JobNeedDAO(AdhocTicketActivity.this);

        jobDescEdittext=(EditText)findViewById(R.id.jobDescEdittext);
        jobRemarkEdittext=(EditText)findViewById(R.id.jobRemarkEdittext);
        jobPrioritySpinner=(Spinner)findViewById(R.id.jobPrioritySpinner);
        jobAssetLocationSpinner=(Spinner)findViewById(R.id.jobAssetLocationSpinner);
        jobUserGroupRadioGroup=(RadioGroup)findViewById(R.id.userAndGroup);
        jobUserGroupRadioGroup.setOnCheckedChangeListener(this);
        jobPeopleGroupSpinner=(Spinner)findViewById(R.id.jobPeopleGroupSpinner);
        jobTicketCategorySpinner=(Spinner)findViewById(R.id.jobTicketCategorySpinner);

        if(priorityList!=null && priorityList.size()>0) {
            ArrayAdapter priorityAdpt = new ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityNameList);
            priorityAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            jobPrioritySpinner.setAdapter(priorityAdpt);
        }

        if(tCategoryList!=null && tCategoryList.size()>0) {
            ArrayAdapter tCategoryAdpt = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tCategoryNameList);
            tCategoryAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            jobTicketCategorySpinner.setAdapter(tCategoryAdpt);
        }

        if(assetLocationNameList!=null && assetLocationNameList.size()>0) {
            ArrayAdapter tAssetLocationAdpt = new ArrayAdapter(this, android.R.layout.simple_spinner_item, assetLocationNameList);
            tAssetLocationAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            jobAssetLocationSpinner.setAdapter(tAssetLocationAdpt);
        }

        showAttachmentButton=(Button)findViewById(R.id.showAttachments);
        showAttachmentButton.setOnClickListener(this);
        attachmentCountView=(TextView)findViewById(R.id.badge_attachment_count_notification);

    }

    public void animateFAB(){

        if(isAttachmentFabOpen){

            fabAddAttachment.startAnimation(rotate_backward);
            fabPicture.startAnimation(fab_close);
            fabVideo.startAnimation(fab_close);
            fabAudio.startAnimation(fab_close);
            fabPicture.setClickable(false);
            fabVideo.setClickable(false);
            fabAudio.setClickable(false);
            isAttachmentFabOpen = false;

        } else {

            fabAddAttachment.startAnimation(rotate_forward);
            fabPicture.startAnimation(fab_open);
            fabVideo.startAnimation(fab_open);
            fabAudio.startAnimation(fab_open);
            fabPicture.setClickable(true);
            fabVideo.setClickable(true);
            fabAudio.setClickable(true);
            isAttachmentFabOpen = true;

        }
    }

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(AdhocTicketActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
            switch (v.getId()) {
                case R.id.fabAddAttachment:
                    if (CommonFunctions.isPermissionGranted(AdhocTicketActivity.this))
                        animateFAB();
                    else
                        Snackbar.make(fabAddAttachment, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.fabPicture:
                    Intent capturePic = new Intent(AdhocTicketActivity.this, CapturePhotoActivity.class);
                    capturePic.putExtra("FROM", Constants.ATTACHMENT_PICTURE);
                    capturePic.putExtra("TIMESTAMP", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    capturePic.putExtra("JOBNEEDID", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    capturePic.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    capturePic.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TICKET);
                    startActivityForResult(capturePic, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    break;
                case R.id.fabVideo:
                    Intent captureVideo = new Intent(AdhocTicketActivity.this, VideoCaptureActivity.class);
                    captureVideo.putExtra("FROM", Constants.ATTACHMENT_VIDEO);
                    captureVideo.putExtra("TIMESTAMP", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    captureVideo.putExtra("JOBNEEDID", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    captureVideo.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    captureVideo.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TICKET);
                    startActivityForResult(captureVideo, Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                    break;
                case R.id.fabAudio:
                    Intent mediaRecoder = new Intent(AdhocTicketActivity.this, MediaRecoderView.class);
                    mediaRecoder.putExtra("FROM", Constants.ATTACHMENT_AUDIO);
                    mediaRecoder.putExtra("TIMESTAMP", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    mediaRecoder.putExtra("JOBNEEDID", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    mediaRecoder.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    mediaRecoder.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TICKET);
                    startActivityForResult(mediaRecoder, Constants.MIC_RECORD_AUDIO_REQUEST_CODE);
                    break;
                case R.id.showAttachments:
                    callIntent();
                    break;


            }
        }else if (accessValue == 1) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
        } else if (accessValue == 2) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 3) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 4) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
            System.out.println("==========="+accessValue);
        }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
            System.out.println("===========lat long==0.0");
        }
    }

    private void callIntent()
    {
        Intent recAudioIntent=new Intent(AdhocTicketActivity.this, AttachmentListActivity.class);
        recAudioIntent.putExtra("FROM",-1);
        recAudioIntent.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        recAudioIntent.putExtra("JOBNEEDID",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        recAudioIntent.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
        startActivityForResult(recAudioIntent,1);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        checkedRadioButton=radioGroup.getCheckedRadioButtonId();
        preparePeopleGroupSpinner(checkedRadioButton);
    }

    private void preparePeopleGroupSpinner(int val)
    {
        ArrayAdapter<String> spinnerAdapt=null;
        List<String> spinnerValues=new ArrayList<String>();
        switch (val)
        {
            case R.id.userRadio:
                selectedRadioButton=0;
                jobPeopleGroupSpinner.setVisibility(View.VISIBLE);
                for(int i=0;i<peopleList.size();i++)
                {
                    if(peopleList.get(i).getPeopleid()!=-1)
                        spinnerValues.add(i,peopleList.get(i).getPeoplename());
                }
                break;
            case R.id.groupRadio:
                selectedRadioButton=1;
                jobPeopleGroupSpinner.setVisibility(View.VISIBLE);
                for(int i=0;i<groupList.size();i++)
                {
                    if(groupList.get(i).getGroupid()!=-1)
                        spinnerValues.add(i,groupList.get(i).getGroupname());
                }
                break;
            case R.id.selfRadio:
                selectedRadioButton=2;
                jobPeopleGroupSpinner.setVisibility(View.GONE);
                break;
        }

        spinnerAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues);
        spinnerAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobPeopleGroupSpinner.setAdapter(spinnerAdapt);
    }

    private boolean isValidate()
    {
        long atop=-1;
        long atog=-1;

        switch(selectedRadioButton)
        {
            case 0:
                if(peopleList!=null && peopleList.size()>0) {
                    atop = peopleList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getPeopleid();
                    atog = -1;
                }
                break;
            case 1:
                if(groupList!=null && groupList.size()>0) {
                    atog = groupList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getGroupid();
                    atop = -1;
                }
                break;
        }
        if(jobDescEdittext.getText().toString().trim().length()==0)
            return false;
        else if(selectedRadioButton==-1)
            return false;
        else if(atop==-1 && atog==-1)
            return false;
        else
            return  true;

        //return (jobDescEdittext.getText().toString().trim().length()==0);

    }

    @Override
    public void onBackPressed() {
        attachmentDAO.deleteAVP(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        setResult(RESULT_OK);
        finish();
    }

    private boolean saveData()
    {
        if(isValidate()) {
            if (checkNetwork.isNetworkConnectionAvailable()) {
                /*JOBNeedInsertAsynTask jobNeedInsertAsynTask = new JOBNeedInsertAsynTask();
                jobNeedInsertAsynTask.execute();*/

                prepareAdhocTicket();

            } else {
                insertIntoDatabase();
            }
            return  true;
        }
        return false;
    }

    private void prepareAdhocTicket()
    {
        long jobFreq=-1;
        long jobPriority=-1;
        long atop=-1;
        long atog=-1;
        long jobAssetID=-1;
        long peopleID=-1;
        long tCategory=-1;

        if(priorityNameList!=null && priorityNameList.size()>0)
            jobPriority=priorityList.get(jobPrioritySpinner.getSelectedItemPosition()).getTaid();

        if(assetLocationNameList!=null && assetLocationNameList.size()>0)
            jobAssetID=assetLocationList.get(jobAssetLocationSpinner.getSelectedItemPosition()).getAssetid();

        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);

        if(tCategoryNameList!=null && tCategoryList.size()>0)
            tCategory=tCategoryList.get(jobTicketCategorySpinner.getSelectedItemPosition()).getTaid();

        switch(selectedRadioButton)
        {
            case 0:
                atop=peopleList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getPeopleid();
                atog=-1;
                break;
            case 1:
                atog=groupList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getGroupid();
                atop=-1;
                break;
        }

        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        UploadJobneedParameter uploadJobneedParameter=new UploadJobneedParameter();
        uploadJobneedParameter.setDetails(new ArrayList<JobNeedDetails>());
        uploadJobneedParameter.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        uploadJobneedParameter.setJobdesc(jobDescEdittext.getText().toString().trim());
        uploadJobneedParameter.setAatop(-1);
        uploadJobneedParameter.setAssetid(jobAssetID);
        uploadJobneedParameter.setCuser(peopleID);
        uploadJobneedParameter.setFrequency(jobFreq);
        uploadJobneedParameter.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        uploadJobneedParameter.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        uploadJobneedParameter.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        uploadJobneedParameter.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        uploadJobneedParameter.setGracetime(0);
        uploadJobneedParameter.setGroupid(atog);
        uploadJobneedParameter.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED));
        uploadJobneedParameter.setJobid(-1);
        uploadJobneedParameter.setJobstatus(typeAssistDAO.getEventTypeID(Constants.TICKET_STATUS_NEW, Constants.STATUS_TYPE_TICKET));
        uploadJobneedParameter.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
        uploadJobneedParameter.setMuser(peopleID);
        uploadJobneedParameter.setParent(-1);
        uploadJobneedParameter.setPeopleid(atop);
        uploadJobneedParameter.setPerformedby(-1);
        uploadJobneedParameter.setPriority(jobPriority);
        uploadJobneedParameter.setScantype(typeAssistDAO.getEventTypeID(Constants.SCAN_TYPE_QR, Constants.IDENTIFIER_SCANTYPE));
        uploadJobneedParameter.setQuestionsetid(-1);
        uploadJobneedParameter.setGpslocation(gpsLocation);
        uploadJobneedParameter.setRemarks(jobRemarkEdittext.getText().toString().trim());
        uploadJobneedParameter.setTicketcategory(tCategory);
        uploadJobneedParameter.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        uploadJobneedParameter.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));

        Gson gson=new Gson();

        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(Constants.SERVICE_ADHOC);
        uploadParameters.setQuery(gson.toJson(uploadJobneedParameter));
        uploadParameters.setBiodata(gson.toJson(uploadJobneedParameter));
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setTzoffset(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
        uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
        //uploadParameters.setSitecode(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
        uploadParameters.setLoginid(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
        uploadParameters.setPassword(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
        //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
        uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

        System.out.println("Upload Ticket: "+gson.toJson(uploadParameters));

        RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
        Call<ResponseReturnIdData> call=retrofitServices.getServerResponseReturnId(Constants.SERVICE_ADHOC, uploadParameters);
        call.enqueue(new Callback<ResponseReturnIdData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseReturnIdData> call, @NonNull Response<ResponseReturnIdData> response) {
                if(response.isSuccessful() && response.body()!=null)
                {
                    try {

                        if (response.body().getRc() == 0) {
                            attachmentDAO.changeAdhocReturnID(String.valueOf(response.body().getReturnid()), String.valueOf(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1)));
                        }
                        else
                        {
                            CommonFunctions.ErrorLog("\n ADHOC ticket creation failed: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");
                            Toast.makeText(AdhocTicketActivity.this,"ADHOC ticket creation failed.\n"+response.body().getMsg(),Toast.LENGTH_LONG).show();
                        }

                    }catch (Exception e)
                    {

                    }
                    finally {
                        adhocJobPef.edit().clear().apply();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseReturnIdData> call, Throwable t) {
                CommonFunctions.ErrorLog("\n ADHOC ticket creation failed: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");
                Snackbar.make(attachmentCountView, "ADHOC ticket creation failed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                adhocJobPef.edit().clear().apply();
                finish();
            }
        });
    }

    private void insertIntoDatabase()
    {
        String date = null;
        long atop=-1;
        long atog=-1;
        DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date = df.format(Calendar.getInstance().getTime());

        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        switch(selectedRadioButton)
        {
            case 0:
                atop=peopleList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getPeopleid();
                atog=-1;
                break;
            case 1:
                atog=groupList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getGroupid();
                atop=-1;
                break;
            case 2:
                atog=-1;
                atop=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
                break;
        }

        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        jobNeed.setJobdesc(jobDescEdittext.getText().toString().trim());
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.TICKET_STATUS_NEW, Constants.STATUS_TYPE_TICKET));
        jobNeed.setScantype(typeAssistDAO.getEventTypeID(Constants.SCAN_TYPE_ENTERED, Constants.IDENTIFIER_SCANTYPE));
        jobNeed.setReceivedonserver(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setPriority(priorityList.get(jobPrioritySpinner.getSelectedItemPosition()).getTaid());
        jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setEndtime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks(jobRemarkEdittext.getText().toString().trim());
        jobNeed.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setAssetid(-1);
        jobNeed.setAatop(atop);
        jobNeed.setGroupid(atog);
        jobNeed.setPeopleid(atop);
        jobNeed.setQuestionsetid(-1);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED));
        jobNeed.setParent(-1);
        jobNeed.setTicketno(-1);
        jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        jobNeed.setTicketcategory(tCategoryList.get(jobTicketCategorySpinner.getSelectedItemPosition()).getTaid());
        jobNeed.setSeqno(0);
        jobNeed.setPerformedby(-1);
        jobNeed.setJobid(-1);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeedDAO.insertRecord(jobNeed, "0");

        adhocJobPef.edit().clear().apply();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.MIC_RECORD_AUDIO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {

                attachmentCountView.setText(String.valueOf(attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1),adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))));
            }
        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }

    private class JOBNeedInsertAsynTask extends AsyncTask<Void, Integer, Void>
    {
        MediaType JSON;
        OkHttpClient client1;
        StringBuffer sb=null;
        String jobDesc=null;
        String jobRemark=null;
        long jobFreq=-1;
        long jobPriority=-1;
        long atop=-1;
        long atog=-1;
        long jobAssetID=-1;
        long jobQsetId=-1;
        long peopleID=-1;
        long tCategory=-1;

        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        UploadJobneedParameter uploadJobneedParameter;
        ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
        Gson gson;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client1 = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
            jobDesc=jobDescEdittext.getText().toString().trim();
            jobRemark=jobRemarkEdittext.getText().toString().trim();
            if(priorityNameList!=null && priorityNameList.size()>0)
                jobPriority=priorityList.get(jobPrioritySpinner.getSelectedItemPosition()).getTaid();

            if(assetLocationNameList!=null && assetLocationNameList.size()>0)
                jobAssetID=assetLocationList.get(jobAssetLocationSpinner.getSelectedItemPosition()).getAssetid();

            peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);

            if(tCategoryNameList!=null && tCategoryList.size()>0)
                tCategory=tCategoryList.get(jobTicketCategorySpinner.getSelectedItemPosition()).getTaid();

            gson = new Gson();

            switch(selectedRadioButton)
            {
                case 0:
                    atop=peopleList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getPeopleid();
                    atog=-1;
                    break;
                case 1:
                    atog=groupList.get(jobPeopleGroupSpinner.getSelectedItemPosition()).getGroupid();
                    atop=-1;
                    break;
            }


        }



        @Override
        protected Void doInBackground(Void... voids) {

            {
                try {
                    String date = null;
                    URL url = new URL(Constants.BASE_URL); // here is your URL path
                    DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    date = df.format(Calendar.getInstance().getTime());

                    System.out.println("Current date format: " + date);

                    String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

                    /*String insertQuery= DatabaseQuries.JOBNEED_INSERT+
                            "(" +
                            "'" + jobDesc + "'," +//jobdesc
                            "'" + date + "'," +//plandatetime
                            "'" + date + "'," +//expirydatetime
                            "0, " +//gracetime
                            "'" + date + "'," +//receivedonserver
                            "'" + date + "'," +//starttime
                            "'" + date + "'," +//endtime
                            "'" + gpsLocation + "'," +//gpslocation
                            "'" + jobRemark + "'," +//remarks
                            atop+","+//aatop
                            jobAssetID + "," +//assetid
                            "-1,"+//jobid
                            typeAssistDAO.getEventTypeID("ASSIGNED")+"," +//jobstatus
                            typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC)+"," +//jobtype
                            peopleID + "," +//performedby
                            jobQsetId +"," +//questionsetid
                            typeAssistDAO.getEventTypeID("QR")+ "," +//scantype
                            peopleID + "," +//peopleid
                            atog + ", " +//groupid
                            typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED)+"," +//identifier
                            "-1," +//parent
                            peopleID + "," +//cuser
                            peopleID + "," +//muser
                            "'" + date + "'," +//cdtz
                            "'" + date + "'," +//mdtz
                            "'False'" + "," +//isdeleted
                            jobFreq+"," +//jobfrequency
                            jobPriority +//priority
                            ") returning jobneedid;";*/


                    jobNeedDetailsArrayList=new ArrayList<>();
                    uploadJobneedParameter=new UploadJobneedParameter();
                    uploadJobneedParameter.setDetails(jobNeedDetailsArrayList);
                    uploadJobneedParameter.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    uploadJobneedParameter.setJobdesc(jobDesc);
                    uploadJobneedParameter.setAatop(-1);
                    uploadJobneedParameter.setAssetid(jobAssetID);
                    uploadJobneedParameter.setCuser(peopleID);
                    uploadJobneedParameter.setFrequency(jobFreq);
                    uploadJobneedParameter.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
                    uploadJobneedParameter.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
                    uploadJobneedParameter.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
                    uploadJobneedParameter.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
                    uploadJobneedParameter.setGracetime(0);
                    uploadJobneedParameter.setGroupid(atog);
                    uploadJobneedParameter.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED));
                    uploadJobneedParameter.setJobid(-1);
                    uploadJobneedParameter.setJobstatus(typeAssistDAO.getEventTypeID(Constants.TICKET_STATUS_NEW, Constants.STATUS_TYPE_TICKET));
                    uploadJobneedParameter.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
                    uploadJobneedParameter.setMuser(peopleID);
                    uploadJobneedParameter.setParent(-1);
                    uploadJobneedParameter.setPeopleid(atop);
                    uploadJobneedParameter.setPerformedby(-1);
                    uploadJobneedParameter.setPriority(jobPriority);
                    uploadJobneedParameter.setScantype(typeAssistDAO.getEventTypeID(Constants.SCAN_TYPE_QR, Constants.IDENTIFIER_SCANTYPE));
                    uploadJobneedParameter.setQuestionsetid(jobQsetId);
                    uploadJobneedParameter.setGpslocation(gpsLocation);
                    uploadJobneedParameter.setRemarks(jobRemark);
                    //uploadJobneedParameter.setIsdeleted("false");
                    uploadJobneedParameter.setTicketcategory(tCategory);
                    uploadJobneedParameter.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
                    uploadJobneedParameter.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));

                    String ss=gson.toJson(uploadJobneedParameter);

                    System.out.println("UploadTicket data: "+ss);

                    //--------------------------------------------------

                    ServerRequest serverRequest=new ServerRequest(AdhocTicketActivity.this);
                    HttpResponse response=serverRequest.getAdhocLogResponse(ss.trim(),
                            loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                    if(response!=null && response.getStatusLine().getStatusCode()==200)
                    {
                        is = response.getEntity().getContent();

                        sb = new StringBuffer("");
                        buffer = new byte[1024];
                        byteread = 0;
                        try {
                            while ((byteread = is.read(buffer)) != -1) {
                                sb.append(new String(buffer));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        is.close();
                        System.out.println("SB: " + sb.toString());
                        response.getEntity().consumeContent();

                        CommonFunctions.ResponseLog("\n ADHOC Ticket Event Log Response \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n"+sb.toString().trim()+"\n");
                    }
                    else {
                        CommonFunctions.ErrorLog("\n ADHOC Ticket Error: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");
                        System.out.println("SB1: Ticket creation failed");
                    }

                    //------------------------------------------------------------------------------------



                    /*UploadParameters uploadParameters=new UploadParameters();
                    uploadParameters.setServicename(Constants.SERVICE_ADHOC);
                    uploadParameters.setQuery(ss);
                    uploadParameters.setBiodata(ss);
                    uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);


                    String upData = gson.toJson(uploadParameters);
                    System.out.println("upData: "+upData);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
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
                    byteread = 0;
                    while ((byteread = is.read(buffer)) != -1)
                    {
                        out.write(buffer, 0, byteread);
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

                    } else {
                        System.out.println("SB: " + responseCode);
                    }*/
                } catch (Exception e) {
                    System.out.println("SB: " + e.toString());
                }

            }

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            //super.onPostExecute(aVoid);

            try {
                if(sb!=null && sb.length()>0) {
                    JSONObject ob = new JSONObject(sb.toString());

                    int status = ob.getInt(Constants.RESPONSE_RC);//0 means success data, 1 means failed
                    long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                    System.out.println("status: " + status);
                    System.out.println("returnID: " + returnidResp);

                    if (status == 0) {
                        attachmentDAO.changeAdhocReturnID(String.valueOf(returnidResp), String.valueOf(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1)));
                    }
                    else
                    {
                        CommonFunctions.ErrorLog("\n ADHOC Ticket Failed: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");
                    }
                }
                else
                    CommonFunctions.ErrorLog("\n ADHOC Ticket Failed: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");

            }catch (Exception e)
            {

            }
            finally {
                adhocJobPef.edit().clear().apply();
            }


        }
    }
}
