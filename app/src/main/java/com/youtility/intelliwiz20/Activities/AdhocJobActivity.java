package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.Group;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.Question;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Model.UploadJobneedParameter;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class AdhocJobActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, View.OnTouchListener, AdapterView.OnItemSelectedListener, IDialogEventListeners {
    private EditText jobDescEdittext;
    private Spinner jobFreqSpinner;
    private Spinner jobQuestSetSpinner;
    private Spinner jobAssetSpinner;
    private EditText jobRemarkEdittext;
    private Spinner jobPrioritySpinner;
    private Spinner jobTicketCategorySpinner;
    private RadioGroup jobUserGroupRadioGroup;
    private int checkedRadioButton;
    private Spinner jobPeopleGroupSpinner;
    private int selectedRadioButton=-1;
    private Button selfPerformButton;
    private ArrayList<Asset> assetList;
    private ArrayList<String> assetNameList;
    private AssetDAO assetDAO;
    private TypeAssistDAO typeAssistDAO;
    private ArrayList<TypeAssist>frequencyList;
    private ArrayList<String>frequencyNameList;
    private ArrayList<TypeAssist>priorityList;
    private ArrayList<String>priorityNameList;
    private ArrayList<TypeAssist>tCategoryList;
    private ArrayList<String>tCategoryNameList;

    private ArrayList<People>peopleList;
    private PeopleDAO peopleDAO;

    private ArrayList<Group>groupList;
    private GroupDAO groupDAO;

    private ImageView showQuestListIV;

    private String adhocReturnID;

    private AttachmentDAO attachmentDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private JobNeedDAO jobNeedDAO;

    private SharedPreferences loginDetailPref;
    private SharedPreferences adhocJobPef;
    private SharedPreferences deviceRelatedPref;

    private CheckNetwork checkNetwork;

    private FloatingActionButton fab;

    private QuestionDAO questionDAO;
    private ArrayList<QuestionSet>questionSetArrayList;
    private ArrayList<String>questionSetNameArrayList;

    private SharedPreferences loginPref;
    private SharedPreferences deviceInfoPref;

    private Boolean isAttachmentFabOpen = false;
    private FloatingActionButton fabAddAttachment,fabPicture,fabVideo, fabAudio;
    private Animation rotate_forward,rotate_backward,fab_open,fab_close;
    private Button showAttachmentButton;
    private TextView attachmentCountView;
    private boolean isJobPerform=false;
    private CustomAlertDialog customAlertDialog;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhoc_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_adhoc_job));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF, MODE_PRIVATE);
        adhocJobPef.edit().putLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()).apply();
        adhocJobPef.edit().putString(Constants.ADHOC_TYPE, Constants.SCAN_TYPE_SKIP).apply();

        customAlertDialog=new CustomAlertDialog(AdhocJobActivity.this, this);

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        checkNetwork=new CheckNetwork(AdhocJobActivity.this);

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        questionDAO=new QuestionDAO(AdhocJobActivity.this);
        typeAssistDAO=new TypeAssistDAO(AdhocJobActivity.this);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(AdhocJobActivity.this);
        jobNeedDAO=new JobNeedDAO(AdhocJobActivity.this);


        frequencyList=typeAssistDAO.getEventList("Frequency");
        frequencyNameList=new ArrayList<>();
        for(int i=0;i<frequencyList.size();i++)
        {
            if(frequencyList.get(i).getTaid()!=-1)
            {
                frequencyNameList.add(frequencyList.get(i).getTaname());
            }
        }

        priorityList=typeAssistDAO.getEventList("Priority");
        priorityNameList=new ArrayList<>();
        for(int i=0;i<priorityList.size();i++)
        {
            if(priorityList.get(i).getTaid()!=-1)
            {
                priorityNameList.add(priorityList.get(i).getTaname());
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


        assetDAO=new AssetDAO(AdhocJobActivity.this);
        assetList=assetDAO.getAssetList();
        assetNameList=new ArrayList<>();
        for(int i=0;i<assetList.size();i++)
        {
            if(assetList.get(i).getAssetid()!=-1)
                assetNameList.add(assetList.get(i).getAssetname());
        }

        peopleDAO=new PeopleDAO(AdhocJobActivity.this);
        peopleList=new ArrayList<People>();
        peopleList=peopleDAO.getPeopleList();

        groupDAO=new GroupDAO(AdhocJobActivity.this);
        groupList=new ArrayList<Group>();
        groupList=groupDAO.getGroupList();

        attachmentDAO=new AttachmentDAO(AdhocJobActivity.this);


        jobDescEdittext=(EditText)findViewById(R.id.jobDescEdittext);
        jobFreqSpinner=(Spinner)findViewById(R.id.jobFreqSpinner);
        jobAssetSpinner=(Spinner)findViewById(R.id.jobAssetSpinner);
        jobTicketCategorySpinner=(Spinner)findViewById(R.id.jobTicketCategorySpinner);
        jobRemarkEdittext=(EditText)findViewById(R.id.jobRemarkEdittext);
        jobPrioritySpinner=(Spinner)findViewById(R.id.jobPrioritySpinner);
        jobUserGroupRadioGroup=(RadioGroup)findViewById(R.id.userAndGroup);
        jobUserGroupRadioGroup.setOnCheckedChangeListener(this);
        jobPeopleGroupSpinner=(Spinner)findViewById(R.id.jobPeopleGroupSpinner);
        jobQuestSetSpinner=(Spinner)findViewById(R.id.jobQuestSetSpinner);
        showQuestListIV=(ImageView)findViewById(R.id.showQuestListIV);
        selfPerformButton=(Button)findViewById(R.id.selfPerformButton);
        selfPerformButton.setOnClickListener(this);

        jobDescEdittext.setText(getResources().getString(R.string.adhoc_default_description, CommonFunctions.getFormatedDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))));

        showQuestListIV.setOnClickListener(this);

        ArrayAdapter freqAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,frequencyNameList);
        freqAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobFreqSpinner.setAdapter(freqAdpt);

        ArrayAdapter assetAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,assetNameList);
        assetAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobAssetSpinner.setAdapter(assetAdpt);
        jobAssetSpinner.setOnItemSelectedListener(this);

        ArrayAdapter priorityAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,priorityNameList);
        priorityAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobPrioritySpinner.setAdapter(priorityAdpt);

        ArrayAdapter tCategoryAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,tCategoryNameList);
        tCategoryAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobTicketCategorySpinner.setAdapter(tCategoryAdpt);

        fab = (FloatingActionButton) findViewById(R.id.fabSaveTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(saveData()) {
                    Snackbar.make(view, getResources().getString(R.string.adhoc_succeed), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else
                {
                    Snackbar.make(view, "Please fill required information!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }*/
                if(CommonFunctions.isPermissionGranted(AdhocJobActivity.this))
                    saveData();
                else
                    Snackbar.make(fab,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
            }
        });


        //fab.setOnTouchListener(this);

        /*fabAddAttachment=(FloatingActionButton)findViewById(R.id.fabAddAttachment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent capturePictureIntent=new Intent(AdhocJobActivity.this, AttachmentListActivity.class);
                capturePictureIntent.putExtra("FROM",PICTURE_INTENT);
                startActivityForResult(capturePictureIntent,PICTURE_INTENT);
            }
        });*/

        fabAddAttachment=(FloatingActionButton)findViewById(R.id.fabAddAttachmentTask);
        fabAddAttachment.setOnClickListener(this);
        fabPicture=(FloatingActionButton)findViewById(R.id.fabPictureTask);
        fabPicture.setOnClickListener(this);
        fabVideo=(FloatingActionButton)findViewById(R.id.fabVideoTask);
        fabVideo.setOnClickListener(this);
        fabAudio=(FloatingActionButton)findViewById(R.id.fabAudioTask);
        fabAudio.setOnClickListener(this);

        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        showAttachmentButton=(Button)findViewById(R.id.showAttachments);
        showAttachmentButton.setOnClickListener(this);
        attachmentCountView=(TextView)findViewById(R.id.badge_attachment_count_notification);

        /*showAdhocAudio=(Button)findViewById(R.id.showAdhocAudio);
        addAdhocAudio=(ImageView)findViewById(R.id.addAudioIV);
        showAdhocAudio.setOnClickListener(this);
        addAdhocAudio.setOnClickListener(this);

        showAdhocVideo=(Button)findViewById(R.id.showAdhocVideo);
        addAdhocVideo=(ImageView)findViewById(R.id.addVideoIV);
        showAdhocVideo.setOnClickListener(this);
        addAdhocVideo.setOnClickListener(this);

        showAdhocPicture=(Button)findViewById(R.id.showAdhocPic);
        addAdhocPicture=(ImageView)findViewById(R.id.addPictureIV);
        showAdhocPicture.setOnClickListener(this);
        addAdhocPicture.setOnClickListener(this);

        audioCountView=(TextView)findViewById(R.id.badge_audio_count_notification);
        videoCountView=(TextView)findViewById(R.id.badge_video_count_notification);
        pictureCountView=(TextView)findViewById(R.id.badge_picture_count_notification);*/

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
            case 2:
                atop=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
                atog=-1;
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

    }

    private void saveData()
    {
        if(isValidate()) {
            /*if (checkNetwork.isNetworkConnectionAvailable()) {

                JOBNeedInsertAsynTask jobNeedInsertAsynTask = new JOBNeedInsertAsynTask();
                jobNeedInsertAsynTask.execute();
            } else {
                CommonFunctions.EventLog("\n ADHOC JOB Saved in DB \n"+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n\n");
                insertIntoDatabase();
                Snackbar.make(fab, getResources().getString(R.string.adhoc_succeed), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                setResult(RESULT_OK);
                finish();
            }*/

            CommonFunctions.EventLog("\n <ADHOC JOB Saved in DB> \n"+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n\n");



            insertIntoDatabase();
            Snackbar.make(fab, getResources().getString(R.string.adhoc_succeed), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            adhocJobPef.edit().clear().apply();
            setResult(RESULT_OK);
            finish();
        }
        else
        {
            Snackbar.make(fab, getResources().getString(R.string.adhoc_fill_req_info), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        checkedRadioButton=radioGroup.getCheckedRadioButtonId();

        preparePeopleGroupSpinner(checkedRadioButton);

    }

    @Override
    public void onBackPressed() {
        attachmentDAO.deleteAVP(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        adhocJobPef.edit().clear().apply();
        setResult(RESULT_OK);
        finish();
    }

    private void preparePeopleGroupSpinner(int val)
    {
        ArrayAdapter<String> spinnerAdapt=null;
        List<String> spinnerValues=new ArrayList<String>();
        switch (val)
        {
            case R.id.userRadio:
                selectedRadioButton=0;
                selfPerformButton.setVisibility(View.GONE);
                jobPeopleGroupSpinner.setVisibility(View.VISIBLE);
                for(int i=0;i<peopleList.size();i++)
                {
                    if(peopleList.get(i).getPeopleid()!=-1)
                        spinnerValues.add(i,peopleList.get(i).getPeoplename());
                }
                break;
            case R.id.groupRadio:
                selectedRadioButton=1;
                selfPerformButton.setVisibility(View.GONE);
                jobPeopleGroupSpinner.setVisibility(View.VISIBLE);
                for(int i=0;i<groupList.size();i++)
                {
                    if(groupList.get(i).getGroupid()!=-1)
                        spinnerValues.add(i,groupList.get(i).getGroupname());
                }
                break;
            case R.id.selfRadio:
                selectedRadioButton=2;
                selfPerformButton.setVisibility(View.VISIBLE);
                jobPeopleGroupSpinner.setVisibility(View.GONE);
                break;
        }

        spinnerAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues);
        spinnerAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobPeopleGroupSpinner.setAdapter(spinnerAdapt);
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
    public void onClick(View view) {
        int accessValue = CommonFunctions.isAllowToAccessModules(AdhocJobActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
            switch (view.getId()) {
                case R.id.fabAddAttachmentTask:
                    if (CommonFunctions.isPermissionGranted(AdhocJobActivity.this))
                        animateFAB();
                    else
                        Snackbar.make(fab, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.fabAudioTask:
                    Intent mediaRecoder = new Intent(AdhocJobActivity.this, MediaRecoderView.class);
                    mediaRecoder.putExtra("FROM", Constants.ATTACHMENT_AUDIO);
                    mediaRecoder.putExtra("TIMESTAMP", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    mediaRecoder.putExtra("JOBNEEDID", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    mediaRecoder.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    mediaRecoder.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(mediaRecoder, Constants.MIC_RECORD_AUDIO_REQUEST_CODE);
                    break;
                case R.id.fabVideoTask:
                    Intent captureVideo = new Intent(AdhocJobActivity.this, VideoCaptureActivity.class);
                    captureVideo.putExtra("FROM", Constants.ATTACHMENT_VIDEO);
                    captureVideo.putExtra("TIMESTAMP", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    captureVideo.putExtra("JOBNEEDID", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    captureVideo.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    captureVideo.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(captureVideo, Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                    break;
                case R.id.fabPictureTask:
                    Intent capturePic = new Intent(AdhocJobActivity.this, CapturePhotoActivity.class);
                    capturePic.putExtra("FROM", Constants.ATTACHMENT_PICTURE);
                    capturePic.putExtra("TIMESTAMP", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    capturePic.putExtra("JOBNEEDID", adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    capturePic.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    capturePic.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(capturePic, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                /*Intent capturePic=new Intent(AdhocJobActivity.this, CameraActivity.class);
                startActivityForResult(capturePic,Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/
                    break;
                case R.id.showAttachments:
                    callIntent();
                    break;
            /*case R.id.showAdhocAudio:
                callIntent(AUDIO_INTENT);
                break;
            case R.id.showAdhocVideo:
                callIntent(VIDEO_INTENT);
                break;
            case R.id.showAdhocPic:
                callIntent(PICTURE_INTENT);
                break;
            case R.id.addAudioIV:
                Intent mediaRecoder=new Intent(AdhocJobActivity.this,MediaRecoderView.class);
                mediaRecoder.putExtra("FROM",Constants.ATTACHMENT_AUDIO);
                mediaRecoder.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                mediaRecoder.putExtra("JOBNEEDID",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                mediaRecoder.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                mediaRecoder.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TASK);
                startActivityForResult(mediaRecoder, Constants.MIC_RECORD_AUDIO_REQUEST_CODE);
                break;
            case R.id.addVideoIV:
                Intent captureVideo=new Intent(AdhocJobActivity.this, VideoCaptureActivity.class);
                captureVideo.putExtra("FROM",Constants.ATTACHMENT_VIDEO);
                captureVideo.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                captureVideo.putExtra("JOBNEEDID",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                captureVideo.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                captureVideo.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TASK);
                startActivityForResult(captureVideo,Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                break;
            case R.id.addPictureIV:
                Intent capturePic=new Intent(AdhocJobActivity.this, CapturePhotoActivity.class);
                capturePic.putExtra("FROM",Constants.ATTACHMENT_PICTURE);
                capturePic.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                capturePic.putExtra("JOBNEEDID",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                capturePic.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                capturePic.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TASK);
                startActivityForResult(capturePic,Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                break;*/
                case R.id.showQuestListIV:
                    if (jobQuestSetSpinner.getSelectedItem() != null && questionSetArrayList != null && questionSetArrayList.size() > 0) {
                        ArrayList<Question> questNames = questionDAO.getQuestions(questionSetArrayList.get(jobQuestSetSpinner.getSelectedItemPosition()).getQuestionsetid());
                        showQuestionNamesInAlertDialog(questNames);
                    }
                    break;
                case R.id.selfPerformButton:

                    if (CommonFunctions.isPermissionGranted(AdhocJobActivity.this)) {
                        if (questionSetArrayList != null && questionSetArrayList.size() > 0) {
                            Intent ii = new Intent(AdhocJobActivity.this, IncidentReportQuestionActivity.class);
                            ii.putExtra("FROM", "ADHOC");
                            ii.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                            ii.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                            ii.putExtra("ID", questionSetArrayList.get(jobQuestSetSpinner.getSelectedItemPosition()).getQuestionsetid());//need to pass quest set id
                            //questionSetArrayList.get(jobQuestSetSpinner.getSelectedItemPosition()).getQuestionsetid();
                            ii.putExtra("ASSETCODE", assetList.get(jobAssetSpinner.getSelectedItemPosition()).getAssetcode());
                            startActivityForResult(ii, 11);
                        } else {
                            Snackbar.make(view, getResources().getString(R.string.adhoc_questionset_error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else
                        Snackbar.make(fab, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.MIC_RECORD_AUDIO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                System.out.println("Attachment Count: "+attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1),adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
                attachmentCountView.setText(String.valueOf(attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1),adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))));
            }
        }
        else if(requestCode==11)
        {
            if(resultCode==RESULT_OK) {
                isJobPerform = true;
                selfPerformButton.setEnabled(false);
            }
            else {
                isJobPerform = false;
                selfPerformButton.setEnabled(true);
            }
        }
        /*else if(requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                System.out.println("Video Count: "+attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_VIDEO));
                videoCountView.setText(""+attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_VIDEO));
            }
        }
        else if(requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                System.out.println("Picture Count: "+attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_PICTURE));
                pictureCountView.setText(""+attachmentDAO.getAttachmentCount(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_PICTURE));
            }
        }*/
    }

    private void callIntent()
    {
        Intent recAudioIntent=new Intent(AdhocJobActivity.this, AttachmentListActivity.class);
        recAudioIntent.putExtra("FROM",0);
        recAudioIntent.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        recAudioIntent.putExtra("JOBNEEDID",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        recAudioIntent.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
        startActivityForResult(recAudioIntent,2);
    }

    private void showQuestionNamesInAlertDialog(ArrayList<Question>questNames)
    {
        CharSequence[]items=new CharSequence[questNames.size()];
        if(questNames!=null && questNames.size()>0)
        {
            for(int i=0;i<questNames.size();i++) {
                items[i]="Quest "+(i+1)+": "+questNames.get(i).getQuestionname();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.adhoc_questionname_title));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x=0.0f;
        float y=0.0f;
        switch (motionEvent.getAction()){

            case MotionEvent.ACTION_MOVE:
                fab.setX(fab.getX() + (motionEvent.getX() - x));
                fab.setY(fab.getY() + (motionEvent.getY() - y));
                return true;
            case MotionEvent.ACTION_DOWN:
                x = motionEvent.getX();
                y = motionEvent.getY();
                return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId())
        {
            case R.id.jobAssetSpinner:

                /*questionSetArrayList=new ArrayList<>();
                questionSetNameArrayList=new ArrayList<>();
                questionSetArrayList=questionDAO.getQuestionSetCodeList(assetList.get(jobAssetSpinner.getSelectedItemPosition()).getAssetcode());//fill depends on selected asset
                for(int ii=0;ii<questionSetArrayList.size();ii++)
                {
                    if(questionSetArrayList.get(ii).getQuestionsetid()!=-1)
                        questionSetNameArrayList.add(questionSetArrayList.get(ii).getQsetname());
                }

                ArrayAdapter questAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,questionSetNameArrayList);
                questAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                jobQuestSetSpinner.setAdapter(questAdpt);*/

                //long assetIDFrmDB=assetDAO.getAssetID(assetList.get(jobAssetSpinner.getSelectedItemPosition()).getAssetcode());

                questionSetArrayList=new ArrayList<>();
                questionSetNameArrayList=new ArrayList<>();

                Asset asset=assetDAO.getAssetAssignedReport(assetList.get(jobAssetSpinner.getSelectedItemPosition()).getAssetcode());
                if(asset!=null)
                {
                    String qSetNameRaw = asset.getQsetname();
                    String qSetIdRaw = asset.getQsetids();

                    if(qSetIdRaw!=null && !qSetIdRaw.equalsIgnoreCase("null") && qSetIdRaw.trim().length()>0)
                    {
                        boolean isAvailable = qSetNameRaw.contains("~");
                        if (isAvailable) {
                            String rIds = qSetIdRaw.replace(" ", ",");
                            questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetadhoc_templates_query, rIds));
                        } else {
                            questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetadhoc_templates_query, qSetIdRaw));
                        }

                        if(questionSetArrayList!=null && questionSetArrayList.size()>0)
                        {
                            for (QuestionSet questionSet : questionSetArrayList) {
                                if(questionSet.getQuestionsetid()!=-1)
                                    questionSetNameArrayList.add(questionSet.getQsetname().toString().trim());
                            }

                            ArrayAdapter questAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,questionSetNameArrayList);
                            questAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            jobQuestSetSpinner.setAdapter(questAdpt);
                        }
                    }
                }


                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

        long expDate=adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis());
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(expDate);
        cal.add(Calendar.MINUTE, +10);
        System.out.println("ExpiryTime: "+CommonFunctions.getTimezoneDate(cal.getTimeInMillis()));

        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
        jobNeed.setJobdesc(jobDescEdittext.getText().toString().trim());
        jobNeed.setFrequency(frequencyList.get(jobFreqSpinner.getSelectedItemPosition()).getTaid());
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(cal.getTimeInMillis()));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));

        if(!isJobPerform)
            jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_ASSIGNED,Constants.STATUS_TYPE_JOBNEED));
        else
            jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED,Constants.STATUS_TYPE_JOBNEED));

        jobNeed.setScantype(typeAssistDAO.getEventTypeID(Constants.SCAN_TYPE_SKIP, Constants.IDENTIFIER_SCANTYPE));
        jobNeed.setReceivedonserver(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setPriority(priorityList.get(jobPrioritySpinner.getSelectedItemPosition()).getTaid());
        //jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, System.currentTimeMillis())));
        jobNeed.setEndtime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks(jobRemarkEdittext.getText().toString().trim());
        jobNeed.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        jobNeed.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
        if(assetList !=null && assetList.size()>0)
            jobNeed.setAssetid(assetList.get(jobAssetSpinner.getSelectedItemPosition()).getAssetid());
        else
            jobNeed.setAssetid(-1);

        jobNeed.setAatop(atop);
        jobNeed.setGroupid(atog);
        jobNeed.setPeopleid(atop);
        if(questionSetArrayList!=null && questionSetArrayList.size()>0)
            jobNeed.setQuestionsetid(questionSetArrayList.get(jobQuestSetSpinner.getSelectedItemPosition()).getQuestionsetid());
        else
            jobNeed.setQuestionsetid(-1);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED));
        jobNeed.setParent(-1);
        jobNeed.setTicketno(-1);
        jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        if(tCategoryList!=null && tCategoryList.size()>0)
            jobNeed.setTicketcategory(tCategoryList.get(jobTicketCategorySpinner.getSelectedItemPosition()).getTaid());
        else
            jobNeed.setTicketcategory(-1);
        jobNeed.setSeqno(0);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeed.setPerformedby(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setJobid(-1);
        jobNeedDAO.insertRecord(jobNeed, "0");

        CommonFunctions.manualSyncEventLog("ADHOC_SKIP","JOBNEEDID: "+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n JOB_STARTED: "+jobNeed.getStarttime()+"\n JOB_END: "+jobNeed.getEndtime(),jobNeed.getEndtime());

        /*ArrayList<QuestionAnswerTransaction> questionArrayList=questionDAO.getChkPointQuestions(questionSetArrayList.get(jobQuestSetSpinner.getSelectedItemPosition()).getQuestionsetid(),
                                                                                                adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()),
                                                                                                loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1),
                                                                                                Constants.ATTACHMENT_OWNER_TYPE_JOBNEED, Constants.JOB_NEED_IDENTIFIER_TASK);
        if(questionArrayList!=null && questionArrayList.size()>0)
        {
            for(int i=0;i<questionArrayList.size();i++)
            {
                JobNeedDetails jobNeedDetails=new JobNeedDetails();
                jobNeedDetails.setJndid(System.currentTimeMillis());
                jobNeedDetails.setSeqno(questionArrayList.get(i).getSeqno());
                jobNeedDetails.setQuestionid(questionArrayList.get(i).getQuestionid());//add qeustion id from quedstion name
                jobNeedDetails.setType(questionArrayList.get(i).getType());
                jobNeedDetails.setAnswer("");
                jobNeedDetails.setOption(questionArrayList.get(i).getOptions());
                jobNeedDetails.setMin(questionArrayList.get(i).getMin());
                jobNeedDetails.setMax(questionArrayList.get(i).getMax());
                jobNeedDetails.setAlerton(questionArrayList.get(i).getAlerton());
                jobNeedDetails.setIsmandatory(questionArrayList.get(i).getIsmandatory());
                jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                jobNeedDetails.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                jobNeedDetails.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
                jobNeedDetails.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));

                jobNeedDetailsDAO.insertRecord(jobNeedDetails);
            }
        }*/

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
        StringBuffer sb;
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
            //jobFreq=frequencyList.get(jobFreqSpinner.getSelectedItemPosition()).getTaid();
            if(priorityList!=null && priorityList.size()>0)
                jobPriority=priorityList.get(jobPrioritySpinner.getSelectedItemPosition()).getTaid();
            if(assetList!=null && assetList.size()>0)
                jobAssetID=assetList.get(jobAssetSpinner.getSelectedItemPosition()).getAssetid();

            if(questionSetArrayList!=null && questionSetArrayList.size()>0)
                jobQsetId=questionSetArrayList.get(jobQuestSetSpinner.getSelectedItemPosition()).getQuestionsetid();

            peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);

            if(tCategoryList!=null && tCategoryList.size()>0)
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
                case 2:
                    atog=-1;
                    atop=peopleID;
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
                    /*String insertQuery="INSERT INTO jobneed( jobdesc, frequency, plandatetime, expirydatetime, gracetime, jobtype, jobstatus,  scantype, receivedonserver, priority,starttime, endtime, gpslocation, remarks, cuser,  cdtz, muser,mdtz, isdeleted, assetcode, aatog, aatop, jobcode, performedby,  qsetcode, jobidentifier, jnpid)  VALUES" +
                            "('" + jobDesc + "', '" + jobFreq + "', '" + date + "', '" + date + " ','0',  '" + Constants.JOB_TYPE_ADHOC + "', 'ASSIGNED', " + null + ", '" + date + "','" + jobPriority + "','" + date + "', '" + date + "', '"+gpsLocation+"',  '" + jobRemark + "', " + peopleID + ",'" + date + "'," + peopleID + ",'" + date + "', 'False', '" + jobAsset + "'," + atog + ", " + atop + ", " + null + ", " + peopleID + ",'" + jobQsetName + "','"+Constants.JOB_NEED_IDENTIFIER_TASK+"',-1) returning jobneedid;";*/

                    //jobneedid	jobdesc	plandatetime	expirydatetime	gracetime	receivedonserver	starttime	endtime	gpslocation	remarks	aatop	assetid	jobid	jobstatus
                    // jobtype	performedby	questionsetid	scantype	peopleid	groupid	identifier	parent	cuser	muser	cdtz	mdtz	isdeleted	frequency   priority


                    //jobneedid	,jobdesc	,plandatetime,	expirydatetime,gracetime	,receivedonserver,	starttime	,endtime	,gpslocation
                    // ,remarks	,aatop	,assetid	,frequency	,jobid	,jobstatus	,jobtype	,performedby	,priority	,questionsetid	,scantype
                    // ,peopleid	,groupid	,identifier	,parent	,cuser	,muser	,cdtz	,mdtz	,isdeleted

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
                            typeAssistDAO.getEventTypeID("ASSIGNED",Constants.STATUS_TYPE_JOBNEED)+"," +//jobstatus
                            typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC)+"," +//jobtype
                            peopleID + "," +//performedby
                            jobQsetId +"," +//questionsetid
                            typeAssistDAO.getEventTypeID("QR")+ "," +//scantype
                            peopleID + "," +//peopleid
                            atog + ", " +//groupid
                            typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED)+"," +//identifier
                            "-1," +//parent
                            peopleID + "," +//cuser
                            peopleID + "," +//muser
                            "'" + date + "'," +//cdtz
                            "'" + date + "'," +//mdtz
                            "'False'" + "," +//isdeleted
                            jobFreq+"," +//jobfrequency
                            jobPriority +//priority
                            ") returning jobneedid;";*/
                    long expDate=adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1);
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(expDate);
                    cal.add(Calendar.MINUTE, +10);
                    System.out.println("ExpiryTime: "+CommonFunctions.getTimezoneDate(cal.getTimeInMillis()));

                    jobNeedDetailsArrayList=new ArrayList<>();
                    jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    uploadJobneedParameter=new UploadJobneedParameter();
                    uploadJobneedParameter.setDetails(jobNeedDetailsArrayList);
                    uploadJobneedParameter.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    uploadJobneedParameter.setJobdesc(jobDesc);
                    uploadJobneedParameter.setAatop(-1);
                    uploadJobneedParameter.setAssetid(jobAssetID);
                    uploadJobneedParameter.setCuser(peopleID);
                    uploadJobneedParameter.setFrequency(-1);
                    uploadJobneedParameter.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
                    uploadJobneedParameter.setExpirydatetime(CommonFunctions.getTimezoneDate(cal.getTimeInMillis()));
                    uploadJobneedParameter.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
                    uploadJobneedParameter.setEndtime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    uploadJobneedParameter.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
                    uploadJobneedParameter.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
                    uploadJobneedParameter.setGracetime(0);
                    uploadJobneedParameter.setGroupid(atog);
                    uploadJobneedParameter.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED));
                    uploadJobneedParameter.setJobid(-1);
                    if(!isJobPerform)
                        uploadJobneedParameter.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_ASSIGNED,Constants.STATUS_TYPE_JOBNEED));
                    else
                        uploadJobneedParameter.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED,Constants.STATUS_TYPE_JOBNEED));
                    uploadJobneedParameter.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));
                    uploadJobneedParameter.setMuser(peopleID);
                    uploadJobneedParameter.setParent(-1);
                    uploadJobneedParameter.setPeopleid(atop);
                    uploadJobneedParameter.setPerformedby(-1);
                    uploadJobneedParameter.setPriority(jobPriority);
                    uploadJobneedParameter.setScantype(typeAssistDAO.getEventTypeID(Constants.SCAN_TYPE_ENTERED,Constants.IDENTIFIER_SCANTYPE));
                    uploadJobneedParameter.setQuestionsetid(jobQsetId);
                    uploadJobneedParameter.setGpslocation(gpsLocation);
                    uploadJobneedParameter.setRemarks(jobRemark);
                    //uploadJobneedParameter.setIsdeleted("false");
                    uploadJobneedParameter.setTicketcategory(tCategory);
                    uploadJobneedParameter.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
                    uploadJobneedParameter.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));

                    String ss=gson.toJson(uploadJobneedParameter);
                    System.out.println("ADHOC Entered job paramerter: "+ss);

                    //--------------------------------------------------

                    ServerRequest serverRequest=new ServerRequest(AdhocJobActivity.this);
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

                        CommonFunctions.ResponseLog("\n ADHOC Event Log Response \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n"+sb.toString().trim()+"\n");

                    }
                    else
                    {
                        CommonFunctions.ErrorLog("\n ADHOC Error: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");
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

            super.onPostExecute(aVoid);

            try {
                if(sb!=null && sb.toString().trim().length()>0)
                {
                    JSONObject ob = new JSONObject(sb.toString());

                    int status = ob.getInt(Constants.RESPONSE_RC);//0 means success data, 1 means failed
                    long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                    System.out.println("status: " + status);
                    System.out.println("returnID: " + returnidResp);
                    adhocReturnID=String.valueOf(returnidResp);

                    if(status==0)
                    {
                        CommonFunctions.EventLog("\n "+getResources().getString(R.string.adhoc_succeed)+" \n"+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n\n");
                        attachmentDAO.changeAdhocReturnID(adhocReturnID,String.valueOf(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)));
                        jobNeedDAO.updateJobNeedRecordFromAdhoc(returnidResp, typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));
                        adhocJobPef.edit().clear().apply();
                        //callImageUploadAsynTask();
                        Snackbar.make(fab, getResources().getString(R.string.adhoc_succeed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        /*setResult(RESULT_OK);
                        finish();*/
                    }
                    else
                    {
                        //Snackbar.make(fab, getResources().getString(R.string.adhoc_failed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        insertIntoDatabase();
                        CommonFunctions.ErrorLog("\n ADHOC Failed: \n"+adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)+"\n");
                        customAlertDialog.commonDialog(getResources().getString(R.string.alerttitle),getResources().getString(R.string.adhoc_failed));
                    }
                }
                else {
                    insertIntoDatabase();
                    CommonFunctions.ErrorLog("\n ADHOC Failed: \n" + adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1) + "\n");
                }


            }catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                setResult(RESULT_OK);
                finish();
            }


        }
    }
// not in used------------------------------------------------------------------------------------
    private void callImageUploadAsynTask()
    {
        ArrayList<Attachment> attachmentList=new ArrayList<Attachment>();
        attachmentList=attachmentDAO.getAttachments((adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1)),-1,-1);
        System.out.println("attachmentList size: "+attachmentList.size());
        if(attachmentList.size()>0)
        {
            for(int i=0;i<attachmentList.size();i++) {

                UploadImageAsynTask uploadImageAsynTask=new UploadImageAsynTask(attachmentList.get(i).getFilePath(), attachmentList.get(i).getOwnerid());
                uploadImageAsynTask.execute();
            }
        }
    }

    private class UploadImageAsynTask extends AsyncTask<Void, Integer, Void>
    {
        String imgURI;

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "921b1508a0b342f5bb06dfa40ae1f55d";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File selectedFile;
        String fileName;
        long jobneedid;
        long peopleID=-1;

        public UploadImageAsynTask(String imgURI,long jobneedid)
        {
            this.imgURI=imgURI;
            this.jobneedid=jobneedid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
            selectedFile = new File(imgURI);
            System.out.println("selectedFile : "+selectedFile.getAbsolutePath());


            String[] parts = imgURI.split("/");
            fileName = parts[parts.length - 1];
            System.out.println("FileName: "+fileName);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if(serverResponseCode==200)
                adhocJobPef.edit().clear().apply();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (!selectedFile.isFile()) {

            }
            else
            {
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(Constants.BASE_URL);

                    //attachmentid,	filepath,	filename,	narration,	gpslocation,	datetime,
                    // ownerid,	cuser,	muser,	cdtz,	mdtz,	isdeleted,	ownername,	attachmenttype

                    String ss="INSERT INTO attachment(filepath, filename, narration, gpslocation, datetime,cuser, cdtz, muser, mdtz, isdeleted, attachmenttype,ownerid, ownername)" +
                            "VALUES ('"+
                            Constants.SERVER_FILE_LOCATION_PATH+"jobneed/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"/"+","+
                            selectedFile.getName()+","+
                            "'',"+
                            "'19,19',"+
                            "now(),"+
                            jobneedid+","+
                            peopleID+","+
                            peopleID+","+
                            "now(),"+
                            "now(),"+
                            "'False',"+
                            "'',"+
                            typeAssistDAO.getEventTypeID("ATTACHMENT", Constants.IDENTIFIER_ATTACHMENT)+") returning attachmentid;";



                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");

                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", imgURI);


                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    String dispName= "image";

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"servicename\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes("UploadAttachment");
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"query\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(ss.toString());
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"filename\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(fileName);
                    dataOutputStream.writeBytes(lineEnd);

                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"path\""+ lineEnd);
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(Constants.SERVER_FILE_LOCATION_PATH+"jobneed/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"/"+jobneedid);
                    dataOutputStream.writeBytes(lineEnd);

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
                            Toast.makeText(AdhocJobActivity.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AdhocJobActivity.this, "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                    }
                    String serverResponseMessage = connection.getResponseMessage();

                    System.out.println( "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status OK
                    if (serverResponseCode == 200) {
                        System.out.println("File Upload completed.\n" + fileName);
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    //os.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }

}
