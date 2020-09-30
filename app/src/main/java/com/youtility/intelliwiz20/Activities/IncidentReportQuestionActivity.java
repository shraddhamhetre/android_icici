package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Adapters.CheckPointQuestAdapter;
import com.youtility.intelliwiz20.Adapters.IncidentReportQuesAdapter;
import com.youtility.intelliwiz20.Adapters.JobNeedDetailsQuesAdapter;
import com.youtility.intelliwiz20.Adapters.SiteReportQuesAdapter;
import com.youtility.intelliwiz20.AsyncTask.JobneedUpdateAsyntask;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.SiteTemplateDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.IUploadJobNeedInsertDataListener;
import com.youtility.intelliwiz20.Interfaces.IUploadJobneedUpdateDataListener;
import com.youtility.intelliwiz20.Model.ChildSection;
import com.youtility.intelliwiz20.Model.ChildSectionQuest;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.ParentSection;
import com.youtility.intelliwiz20.Model.Question;
import com.youtility.intelliwiz20.Model.QuestionAnswerTransaction;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.QuestionSetLevel_One;
import com.youtility.intelliwiz20.Model.QuestionSetLevel_Two;
import com.youtility.intelliwiz20.Model.UploadIncidentReportParameter;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.AutoSyncService;
import com.youtility.intelliwiz20.Services.UploadImageService;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.android.CaptureActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class IncidentReportQuestionActivity extends AppCompatActivity implements AbsListView.OnScrollListener, View.OnClickListener, IDialogEventListeners, IUploadJobNeedInsertDataListener,IUploadJobneedUpdateDataListener {
    private ListView listView;
    private Button submitButton;
    private Button saveButton;
    private IncidentReportQuesAdapter incidentReportQuesAdapter;
    private ArrayList<QuestionAnswerTransaction>irQuestionArrayList;

    private ArrayList<QuestionAnswerTransaction>questionAnswerTransactionArrayList;
    private CheckPointQuestAdapter checkPointQuestAdapter;

    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private QuestionDAO questionDAO;
    private AttachmentDAO attachmentDAO;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;



    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;

    //ArrayList<JobNeedDetails> jobNeedDetailsArrayList;

    ArrayList<QuestionAnswerTransaction> jndForReadingArrayList;

    private JobNeedDetailsQuesAdapter jobNeedDetailsQuesAdapter;
    private String fromActivity=null;
    private String parentActivity=null;
    private String parentFolder=null;
    private long parentJobNeedId=-1;
    private long fromID=-1;
    private long fromQuestionSetID=-1;
    private long selectedCheckpointId =-1;
    private String fromQuestionSetName=null;

    private long peopleID=-1;
    private String gpsLocation=null;

    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences adhocJobPef;
    private SharedPreferences siteAuditPref;
    private SharedPreferences tourChildPref;
    private long currentTimestamp=-1;

    private JobNeed jobNeed;

    private final int AUDIO_INTENT=0;
    private final int VIDEO_INTENT=1;
    private final int PICTURE_INTENT=2;

    private final int SURVEY_VALIDATION=0;
    private final int ADHOC_VALIDATION=1;
    private final int READING_VALIDATION=3;

    //private ArrayList<QuestionSet>questionSetArrayList;
    private ArrayList<QuestionSet> subsetQuestionset;
    private ArrayList<QuestionAnswerTransaction> questionSubSetArrayList;

    private ArrayList<JobNeed>schIReportParentList;
    private ArrayList<JobNeed>schSiteReportParentList;

    private  ArrayList<JobNeedDetails>schIReportchildList;


    private ArrayList<JobNeedDetails>schSiteReportChildList;

    private MenuItem item_pic;
    private MenuItem item_video;
    private MenuItem item_audio;


    private ParentSection parentSection=null;
    private SiteReportQuesAdapter siteReportQuesAdapter;

    String imageTempName;
    int position=-1;
    int qType=-1;

    private String questionErrorMsg=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report_question);

        fromActivity=getIntent().getStringExtra("FROM");
        parentActivity=getIntent().getStringExtra("PARENT_ACTIVITY");
        parentFolder=getIntent().getStringExtra("FOLDER");

        if(getIntent().hasExtra("PARENTID"))
        {
            parentJobNeedId=getIntent().getLongExtra("PARENTID",-1);
        }

        customAlertDialog=new CustomAlertDialog(IncidentReportQuestionActivity.this,this);

        System.out.println("parentActivity: "+parentActivity.toLowerCase(Locale.ENGLISH)+" : parentFolder: "+parentFolder.toLowerCase(Locale.ENGLISH));

        fromID=getIntent().getLongExtra("ID",-1);
        if(fromActivity.equalsIgnoreCase("INCIDENTREPORT") || fromActivity.equalsIgnoreCase("INCIDENTREPORT_LIST")
                || fromActivity.equalsIgnoreCase("SITEREPORT") || fromActivity.equalsIgnoreCase("SITEREPORT_SCHEDULE")
                || fromActivity.equalsIgnoreCase("REQUEST")) {
            fromQuestionSetID = getIntent().getLongExtra("QUESTIONSETID", -1);
        }

        selectedCheckpointId = getIntent().getLongExtra("ASSETID", -1);




        currentTimestamp=System.currentTimeMillis();

        //test====

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(IncidentReportQuestionActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        //end test=====

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF,MODE_PRIVATE);
        siteAuditPref=getSharedPreferences(Constants.SITE_AUDIT_PREF, MODE_PRIVATE);
        tourChildPref=getSharedPreferences(Constants.TOUR_CHILD_PREF, MODE_PRIVATE);

        peopleID=loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1);
        gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        jobNeedDetailsDAO=new JobNeedDetailsDAO(IncidentReportQuestionActivity.this);
        questionDAO=new QuestionDAO(IncidentReportQuestionActivity.this);
        jobNeedDAO=new JobNeedDAO(IncidentReportQuestionActivity.this);
        typeAssistDAO=new TypeAssistDAO(IncidentReportQuestionActivity.this);
        assetDAO=new AssetDAO(IncidentReportQuestionActivity.this);
        attachmentDAO=new AttachmentDAO(IncidentReportQuestionActivity.this);

        fromQuestionSetName=questionDAO.getQuestionSetName(fromQuestionSetID);

        listView=(ListView)findViewById(R.id.questionListView);
        listView.setOnScrollListener(this);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

        submitButton=(Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        saveButton=(Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        if(fromActivity.equalsIgnoreCase("INCIDENTREPORT") || fromActivity.equalsIgnoreCase("INCIDENTREPORT_LIST"))
        {
            saveButton.setVisibility(View.VISIBLE);
        }
        else
            saveButton.setVisibility(View.GONE);


//----------------------------------------------------------------------------prepare listview arraylist
        irQuestionArrayList=new ArrayList<QuestionAnswerTransaction>();
        questionAnswerTransactionArrayList=new ArrayList<QuestionAnswerTransaction>();

        if(fromActivity.equalsIgnoreCase("CHECKPOINT") || fromActivity.equalsIgnoreCase("ASSET") || fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("ADHOC_SCAN"))
        {
            //System.out.println("AssetID: "+assetDAO.getAssetID(getIntent().getStringExtra("ASSETCODE")));
            questionAnswerTransactionArrayList=questionDAO.getChkPointQuestions(fromID,currentTimestamp,loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1), parentFolder, parentActivity);
        }
        else if(fromActivity.equalsIgnoreCase("REQUEST"))
        {
            questionAnswerTransactionArrayList=questionDAO.getChkPointQuestions(fromQuestionSetID,currentTimestamp,loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1), parentFolder, parentActivity);
        }
        else if(fromActivity.equalsIgnoreCase("TOUR"))
        {
            jobNeed=new JobNeed();
            jobNeed=jobNeedDAO.getJobNeedDetails(fromID);
            if(jobNeed!=null)
            {
                //jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(fromID);
                jndForReadingArrayList=jobNeedDetailsDAO.getJNDQuestListForReading(fromID, loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1), parentFolder, parentActivity);
            }
        }
        else if(fromActivity.equalsIgnoreCase("JOB"))
        {
            jobNeed=new JobNeed();
            jobNeed=jobNeedDAO.getJobNeedDetails(fromID);
            if(jobNeed!=null) {
                //jobNeedDetailsArrayList = jobNeedDetailsDAO.getJNDQuestListForReading(fromID, loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1), parentFolder, parentActivity);
                jndForReadingArrayList=jobNeedDetailsDAO.getJNDQuestListForReading(fromID, loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1), parentFolder, parentActivity);
            }
        }
        else if(fromActivity.equalsIgnoreCase("INCIDENTREPORT_LIST"))
        {

            System.out.println("C: INCIDENTREPORT_LIST");
            ArrayList<ChildSection>childSectionArrayList=null;
            ArrayList<ChildSectionQuest> childSectionQuestArrayList=null;
            ChildSection childSection=null;
            long childTimestamp=-1;
            parentSection=new ParentSection();
            parentSection.setParentSecId(fromID);
            parentSection.setParentId(-1);
            parentSection.setqSetId(fromQuestionSetID);
            parentSection.setqSetName(fromQuestionSetName);
            System.out.println("setParentSecId-ir: "+fromID);
            System.out.println("setqSetId-ir: "+fromQuestionSetID);
            System.out.println("setqSetName-ir: "+fromQuestionSetName);

            ArrayList<QuestionSet>subsetQuestionset1=new ArrayList<>();
            subsetQuestionset1=questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
            System.out.println("subsetQuestionset-ir: "+subsetQuestionset1.size());
            System.out.println("setParentSecId-ir: "+fromID);

            schIReportParentList=new ArrayList<>();
            schIReportParentList=jobNeedDAO.getIrReportchildList(fromID);
            System.out.println("schIReportParentList---"+ schIReportParentList.size());
            if(subsetQuestionset1!=null && subsetQuestionset1.size()>0) {
                childSectionArrayList = new ArrayList<>();
                /*for(int i=0;i<subsetQuestionset1.size();i++)
                {*/

                for (int c = 0; c < schIReportParentList.size(); c++) {
                    childTimestamp = System.currentTimeMillis();
                    childSection = new ChildSection();
                    childSection.setParentId(fromID);
                    childSection.setChildQSetId(schIReportParentList.get(c).getQuestionsetid());
                    childSection.setChildSecId(schIReportParentList.get(c).getJobneedid());
                    childSection.setChildQSetName(questionDAO.getQuestionSetName(schIReportParentList.get(c).getQuestionsetid()));
                    childSection.setChildSeqNo(c+1);

                    System.out.println("schIReportParentList.get(c).getQuestionsetid() " + questionDAO.getQuestionSetName(schIReportParentList.get(c).getQuestionsetid()));

                    ArrayList<Question> questionArrayList = questionDAO.getQuestions(schIReportParentList.get(c).getQuestionsetid());
                    System.out.println("questionArrayList-ir: " + questionArrayList.size());

                    if (questionArrayList != null && questionArrayList.size() > 0) {
                        childSectionQuestArrayList = new ArrayList<>();
                        for (int q = 0; q < questionArrayList.size(); q++) {
                            System.out.println("qid-ir: " + questionArrayList.get(q).getQuestionname());

                            ChildSectionQuest childSectionQuest = new ChildSectionQuest();
                            childSectionQuest.setParentId(schIReportParentList.get(c).getParent());
                            childSectionQuest.setQsetID(schIReportParentList.get(c).getQuestionsetid());
                            childSectionQuest.setQuestionsetName(questionDAO.getQuestionSetName(schIReportParentList.get(c).getQuestionsetid()));
                            childSectionQuest.setQuestionid(questionArrayList.get(q).getQuestionid());
                            childSectionQuest.setQuestionname(questionArrayList.get(q).getQuestionname());
                            childSectionQuest.setQuestAnsTransId(System.currentTimeMillis());
                            childSectionQuest.setMin(questionArrayList.get(q).getMin());
                            childSectionQuest.setMax(questionArrayList.get(q).getMax());
                            childSectionQuest.setType(questionArrayList.get(q).getType());
                            childSectionQuest.setOptions(questionArrayList.get(q).getOptions());
                            childSectionQuest.setUnit(questionArrayList.get(q).getUnit());
                            childSectionQuest.setCuser(questionArrayList.get(q).getCuser());
                            childSectionQuest.setCdtz(questionArrayList.get(q).getCdtz());
                            childSectionQuest.setMuser(questionArrayList.get(q).getMuser());
                            childSectionQuest.setMdtz(questionArrayList.get(q).getMdtz());
                            childSectionQuest.setQuestAnswer("");
                            childSectionQuest.setSeqno(questionArrayList.get(q).getSeqno());
                            childSectionQuest.setIsmandatory(questionArrayList.get(q).getIsmandatory());
                            childSectionQuest.setAlerton(questionArrayList.get(q).getAlertOn());
                            childSectionQuest.setJobneedid(schIReportParentList.get(c).getJobneedid());
                            childSectionQuest.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1));
                            childSectionQuest.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                            childSectionQuest.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));

                            childSectionQuestArrayList.add(childSectionQuest);

                        }
                        childSection.setChildSectionQuestArrayList(childSectionQuestArrayList);

                    }

                    childSectionArrayList.add(childSection);
                }
            /*}*/

            }
            parentSection.setChildSectionArrayList(childSectionArrayList);

            System.out.println("getChildSectionArrayList after"+parentSection.getChildSectionArrayList().size());


            System.out.println("setChildSectionArrayList.. =="+childSectionArrayList.size());

            questionSubSetArrayList=new ArrayList<>();
            QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
            questionAnswerTransaction1.setQsetID(fromQuestionSetID);
            questionAnswerTransaction1.setQuestionsetName(fromQuestionSetName);
            questionAnswerTransaction1.setParentId(-1);
            questionAnswerTransaction1.setQuestAnsTransId(-1);
            questionAnswerTransaction1.setCorrect(true);
            irQuestionArrayList.add(questionAnswerTransaction1);
            System.out.println("irQuestionArrayList.. =="+irQuestionArrayList.size());

            schIReportParentList=new ArrayList<>();
            schIReportParentList=jobNeedDAO.getIrReportSectionsList(fromID);
            System.out.println("schIReportParentList.. =="+schIReportParentList.size());

            if(schIReportParentList!=null && schIReportParentList.size()>0)
            {
                System.out.println("schSiteReportParentList.size(): "+schIReportParentList.size());
                for(int k=0;k<schIReportParentList.size();k++)
                {

                    QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                    questionAnswerTransaction2.setQsetID(schIReportParentList.get(k).getQuestionsetid());
                    questionAnswerTransaction2.setQuestionsetName(questionDAO.getQuestionSetName(schIReportParentList.get(k).getQuestionsetid()));
                    questionAnswerTransaction2.setParentId(fromQuestionSetID);
                    questionAnswerTransaction2.setSeqno((k+1));
                    questionAnswerTransaction2.setQuestAnsTransId(-1);
                    questionAnswerTransaction2.setCorrect(true);
                    System.out.println("name qset: "+schIReportParentList.get(k).getQuestionsetid());

                    irQuestionArrayList.add(questionAnswerTransaction2);
                    questionSubSetArrayList.add(questionAnswerTransaction2);

                    //ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());
                    ArrayList<JobNeedDetails> jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(schIReportParentList.get(k).getJobneedid());
                    if(jobNeedDetailsArrayList!=null && jobNeedDetailsArrayList.size()>0)
                    {
                        for(int j=0;j<jobNeedDetailsArrayList.size();j++)
                        {
                            System.out.println("jobNeedDetailsArrayList final: "+jobNeedDetailsArrayList.size());

                            System.out.println("answer final: "+jobNeedDetailsArrayList.get(j).getAnswer());

                            QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
                            questionAnswerTransaction.setQuestionsetName(questionDAO.getQuestionSetName(schIReportParentList.get(k).getQuestionsetid()));
                            questionAnswerTransaction.setQsetID(schIReportParentList.get(k).getQuestionsetid());
                            questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                            questionAnswerTransaction.setQuestionid(jobNeedDetailsArrayList.get(j).getQuestionid());
                            questionAnswerTransaction.setQuestionname(questionDAO.getQuestionName(jobNeedDetailsArrayList.get(j).getQuestionid()));
                            questionAnswerTransaction.setMin(jobNeedDetailsArrayList.get(j).getMin());
                            questionAnswerTransaction.setMax(jobNeedDetailsArrayList.get(j).getMax());
                            questionAnswerTransaction.setType(jobNeedDetailsArrayList.get(j).getType());
                            questionAnswerTransaction.setOptions(jobNeedDetailsArrayList.get(j).getOption());
                            questionAnswerTransaction.setCuser(jobNeedDetailsArrayList.get(j).getCuser());
                            questionAnswerTransaction.setCdtz(jobNeedDetailsArrayList.get(j).getCdtz());
                            questionAnswerTransaction.setMuser(jobNeedDetailsArrayList.get(j).getMuser());
                            questionAnswerTransaction.setMdtz(jobNeedDetailsArrayList.get(j).getMdtz());
                            questionAnswerTransaction.setAlerton(jobNeedDetailsArrayList.get(j).getAlerton());
                            questionAnswerTransaction.setQuestAnswer(jobNeedDetailsArrayList.get(j).getAnswer());
                            questionAnswerTransaction.setParentId(schIReportParentList.get(k).getQuestionsetid());
                            questionAnswerTransaction.setSeqno(jobNeedDetailsArrayList.get(j).getSeqno());
                            questionAnswerTransaction.setIsmandatory(jobNeedDetailsArrayList.get(j).getIsmandatory());
                            questionAnswerTransaction.setJobneedid(schIReportParentList.get(k).getJobneedid());
                            questionAnswerTransaction.setJndid(jobNeedDetailsArrayList.get(j).getJndid());
                            questionAnswerTransaction.setBuid(siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1));
                            questionAnswerTransaction.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                            questionAnswerTransaction.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));
                            questionAnswerTransaction.setCorrect(true);
                            irQuestionArrayList.add(questionAnswerTransaction);
                        }
                    }


                }
            }



/*            subsetQuestionset=new ArrayList<>();
            subsetQuestionset= questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);

            if(subsetQuestionset!=null && subsetQuestionset.size()>0)
            {
                System.out.println("subsetQuestionset.size(): "+subsetQuestionset.size());
                for(int k=0;k<subsetQuestionset.size();k++)
                {
                    System.out.println("Question Sub SetName: "+k+" : "+subsetQuestionset.get(k).getQsetname());
                    System.out.println("-------------------------------------------------------------");

                    QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                    questionAnswerTransaction2.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                    questionAnswerTransaction2.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                    questionAnswerTransaction2.setParentId(fromQuestionSetID);
                    questionAnswerTransaction2.setSeqno((k+1));
                    questionAnswerTransaction2.setQuestAnsTransId(-1);
                    questionAnswerTransaction2.setCorrect(true);

                    irQuestionArrayList.add(questionAnswerTransaction2);
                    questionSubSetArrayList.add(questionAnswerTransaction2);
                    ArrayList<JobNeedDetails>questionArrayList1=jobNeedDetailsDAO.getJobNeedDetailQuestList(subsetQuestionset.get(k).getQuestionsetid());

                    if(questionArrayList1!=null && questionArrayList1.size()>0)
                    {
                        for(int j=0;j<questionArrayList1.size();j++)
                        {
                            if(questionArrayList1.get(j).getQuestionid()!=-1 && (questionArrayList1.get(j).getType()!=0 || questionArrayList1.get(j).getType()!=-1)) {
                                QuestionAnswerTransaction questionAnswerTransaction = new QuestionAnswerTransaction();
                                questionAnswerTransaction.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                                questionAnswerTransaction.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                                questionAnswerTransaction.setQuestionid(questionArrayList1.get(j).getQuestionid());
                                questionAnswerTransaction.setQuestionname(questionDAO.getQuestionName(questionArrayList1.get(j).getQuestionid()));
                                questionAnswerTransaction.setMin(questionArrayList1.get(j).getMin());
                                questionAnswerTransaction.setMax(questionArrayList1.get(j).getMax());
                                questionAnswerTransaction.setType(questionArrayList1.get(j).getType());
                                questionAnswerTransaction.setOptions(questionArrayList1.get(j).getOption());
                                //questionAnswerTransaction.setUnit(questionArrayList1.get(j).get());
                                questionAnswerTransaction.setCuser(questionArrayList1.get(j).getCuser());
                                questionAnswerTransaction.setCdtz(questionArrayList1.get(j).getCdtz());
                                questionAnswerTransaction.setMuser(questionArrayList1.get(j).getMuser());
                                questionAnswerTransaction.setMdtz(questionArrayList1.get(j).getMdtz());
                                questionAnswerTransaction.setQuestAnswer(questionArrayList1.get(j).getAnswer());
                                questionAnswerTransaction.setParentId(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setSeqno(questionArrayList1.get(j).getSeqno());
                                questionAnswerTransaction.setBuid(fromID);
                                questionAnswerTransaction.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setIsmandatory(questionArrayList1.get(j).getIsmandatory());
                                questionAnswerTransaction.setCorrect(true);
                                irQuestionArrayList.add(questionAnswerTransaction);
                            }
                        }
                    }

                }
            }*/

            /*questionSubSetArrayList=new ArrayList<>();
            if(fromQuestionSetID!=-1)
            {
                QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
                questionAnswerTransaction1.setQsetID(fromQuestionSetID);
                questionAnswerTransaction1.setQuestionsetName(fromQuestionSetName);
                questionAnswerTransaction1.setParentId(-1);
                questionAnswerTransaction1.setQuestAnsTransId(-1);

                irQuestionArrayList.add(questionAnswerTransaction1);
                subsetQuestionset=new ArrayList<>();
                subsetQuestionset= questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
                if(subsetQuestionset!=null && subsetQuestionset.size()>0)
                {
                    System.out.println("subsetQuestionset.size(): "+subsetQuestionset.size());
                    for(int k=0;k<subsetQuestionset.size();k++)
                    {
                        System.out.println("Question Sub SetName: "+k+" : "+subsetQuestionset.get(k).getQsetname());
                        System.out.println("-------------------------------------------------------------");

                        QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                        questionAnswerTransaction2.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                        questionAnswerTransaction2.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                        questionAnswerTransaction2.setParentId(fromQuestionSetID);

                        questionAnswerTransaction2.setQuestAnsTransId(-1);
                        irQuestionArrayList.add(questionAnswerTransaction2);
                        questionSubSetArrayList.add(questionAnswerTransaction2);
                        ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());
                        if(questionArrayList!=null && questionArrayList.size()>0)
                        {
                            for(int j=0;j<questionArrayList.size();j++)
                            {
                                System.out.println("QuestionName: "+questionArrayList.get(j).getQuestionname());

                                QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
                                questionAnswerTransaction.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                                questionAnswerTransaction.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                                questionAnswerTransaction.setQuestionid(questionArrayList.get(j).getQuestionid());
                                questionAnswerTransaction.setQuestionname(questionArrayList.get(j).getQuestionname());
                                questionAnswerTransaction.setMin(questionArrayList.get(j).getMin());
                                questionAnswerTransaction.setMax(questionArrayList.get(j).getMax());
                                questionAnswerTransaction.setType(questionArrayList.get(j).getType());
                                questionAnswerTransaction.setOptions(questionArrayList.get(j).getOptions());
                                questionAnswerTransaction.setUnit(questionArrayList.get(j).getUnit());
                                questionAnswerTransaction.setCuser(questionArrayList.get(j).getCuser());
                                questionAnswerTransaction.setCdtz(questionArrayList.get(j).getCdtz());
                                questionAnswerTransaction.setMuser(questionArrayList.get(j).getMuser());
                                questionAnswerTransaction.setMdtz(questionArrayList.get(j).getMdtz());
                                questionAnswerTransaction.setQuestAnswer("");
                                questionAnswerTransaction.setParentId(subsetQuestionset.get(k).getQuestionsetid());
                                irQuestionArrayList.add(questionAnswerTransaction);
                            }
                        }


                    }
                }
            }*/
        }
        else if(fromActivity.equalsIgnoreCase("INCIDENTREPORT"))
        {
            System.out.println("C: INCIDENTREPORT");

/*            questionSubSetArrayList=new ArrayList<>();
            if(fromQuestionSetID!=-1)
            {
                QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
                questionAnswerTransaction1.setQsetID(fromQuestionSetID);
                questionAnswerTransaction1.setQuestionsetName(fromQuestionSetName);
                questionAnswerTransaction1.setParentId(-1);
                questionAnswerTransaction1.setQuestAnsTransId(-1);
                questionAnswerTransaction1.setCorrect(true);

                irQuestionArrayList.add(questionAnswerTransaction1);
                subsetQuestionset=new ArrayList<>();
                subsetQuestionset= questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
                if(subsetQuestionset!=null && subsetQuestionset.size()>0)
                {
                    System.out.println("subsetQuestionset.size(): "+subsetQuestionset.size());
                    for(int k=0;k<subsetQuestionset.size();k++)
                    {
                        System.out.println("Question Sub SetName: "+k+" : "+subsetQuestionset.get(k).getQsetname());
                        System.out.println("-------------------------------------------------------------");

                        QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                        questionAnswerTransaction2.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                        questionAnswerTransaction2.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                        questionAnswerTransaction2.setParentId(fromQuestionSetID);
                        questionAnswerTransaction2.setSeqno((k+1));
                        questionAnswerTransaction2.setQuestAnsTransId(-1);
                        questionAnswerTransaction2.setCorrect(true);

                        irQuestionArrayList.add(questionAnswerTransaction2);
                        questionSubSetArrayList.add(questionAnswerTransaction2);
                        ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());
                        if(questionArrayList!=null && questionArrayList.size()>0)
                        {
                            for(int j=0;j<questionArrayList.size();j++)
                            {
                                System.out.println("QuestionName: "+questionArrayList.get(j).getQuestionname());

                                QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
                                questionAnswerTransaction.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                                questionAnswerTransaction.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                                questionAnswerTransaction.setQuestionid(questionArrayList.get(j).getQuestionid());
                                questionAnswerTransaction.setQuestionname(questionArrayList.get(j).getQuestionname());
                                questionAnswerTransaction.setMin(questionArrayList.get(j).getMin());
                                questionAnswerTransaction.setMax(questionArrayList.get(j).getMax());
                                questionAnswerTransaction.setType(questionArrayList.get(j).getType());
                                questionAnswerTransaction.setOptions(questionArrayList.get(j).getOptions());
                                questionAnswerTransaction.setUnit(questionArrayList.get(j).getUnit());
                                questionAnswerTransaction.setCuser(questionArrayList.get(j).getCuser());
                                questionAnswerTransaction.setCdtz(questionArrayList.get(j).getCdtz());
                                questionAnswerTransaction.setMuser(questionArrayList.get(j).getMuser());
                                questionAnswerTransaction.setMdtz(questionArrayList.get(j).getMdtz());
                                questionAnswerTransaction.setQuestAnswer("");
                                questionAnswerTransaction.setParentId(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setSeqno(questionArrayList.get(j).getSeqno());
                                questionAnswerTransaction.setIsmandatory(questionArrayList.get(j).getIsmandatory());
                                questionAnswerTransaction.setJobneedid(fromID);
                                questionAnswerTransaction.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                                questionAnswerTransaction.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setCorrect(true);
                                irQuestionArrayList.add(questionAnswerTransaction);
                            }
                        }


                    }
                }
            }*/

            ArrayList<ChildSection>childSectionArrayList=null;
            ArrayList<ChildSectionQuest> childSectionQuestArrayList=null;
            ChildSection childSection=null;
            long childTimestamp=-1;
            parentSection=new ParentSection();
            parentSection.setParentSecId(fromID);
            parentSection.setParentId(-1);
            parentSection.setqSetId(fromQuestionSetID);
            parentSection.setqSetName(fromQuestionSetName);
            System.out.println("fromQuestionSetID-ir: "+fromQuestionSetID);
            System.out.println("fromQuestionSetName-ir: "+fromQuestionSetName);
            System.out.println("fromID-ir: "+fromID);


            ArrayList<QuestionSet>subsetQuestionset1=new ArrayList<>();
            subsetQuestionset1=questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
            System.out.println("subsetQuestionset-ir: "+subsetQuestionset1.size());
            if(subsetQuestionset1!=null && subsetQuestionset1.size()>0)
            {
                childSectionArrayList=new ArrayList<>();

                System.out.println("subsetQuestionset-ir: "+subsetQuestionset1.size());

                for(int c=0;c<subsetQuestionset1.size();c++)
                {
                    System.out.println("subsetQuestionset-ir: "+subsetQuestionset1.size());

                    childTimestamp=System.currentTimeMillis();
                    childSection=new ChildSection();
                    childSection.setParentId(fromID);
                    childSection.setChildQSetId(subsetQuestionset1.get(c).getQuestionsetid());
                    childSection.setChildSecId(childTimestamp);
                    childSection.setChildQSetName(subsetQuestionset1.get(c).getQsetname());
                    childSection.setChildSeqNo(subsetQuestionset1.get(c).getSeqno());
                    ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset1.get(c).getQuestionsetid());
                    System.out.println("questionArrayList: "+questionArrayList.size());


                    if(questionArrayList!=null && questionArrayList.size()>0)
                    {
                        childSectionQuestArrayList=new ArrayList<>();
                        for(int q=0;q<questionArrayList.size();q++)
                        {
                            ChildSectionQuest childSectionQuest=new ChildSectionQuest();
                            childSectionQuest.setParentId(childTimestamp);
                            childSectionQuest.setQsetID(subsetQuestionset1.get(c).getQuestionsetid());
                            childSectionQuest.setQuestionsetName(subsetQuestionset1.get(c).getQsetname());
                            childSectionQuest.setQuestionid(questionArrayList.get(q).getQuestionid());
                            childSectionQuest.setQuestionname(questionArrayList.get(q).getQuestionname());
                            childSectionQuest.setQuestAnsTransId(System.currentTimeMillis());
                            childSectionQuest.setMin(questionArrayList.get(q).getMin());
                            childSectionQuest.setMax(questionArrayList.get(q).getMax());
                            childSectionQuest.setType(questionArrayList.get(q).getType());
                            childSectionQuest.setOptions(questionArrayList.get(q).getOptions());
                            childSectionQuest.setUnit(questionArrayList.get(q).getUnit());
                            childSectionQuest.setCuser(questionArrayList.get(q).getCuser());
                            childSectionQuest.setCdtz(questionArrayList.get(q).getCdtz());
                            childSectionQuest.setMuser(questionArrayList.get(q).getMuser());
                            childSectionQuest.setMdtz(questionArrayList.get(q).getMdtz());
                            childSectionQuest.setQuestAnswer("");
                            childSectionQuest.setSeqno(questionArrayList.get(q).getSeqno());
                            childSectionQuest.setIsmandatory(questionArrayList.get(q).getIsmandatory());
                            childSectionQuest.setAlerton(questionArrayList.get(q).getAlertOn());
                            childSectionQuest.setJobneedid(childTimestamp);
                            childSectionQuest.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                            childSectionQuest.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                            childSectionQuest.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));

                            childSectionQuestArrayList.add(childSectionQuest);

                        }
                        childSection.setChildSectionQuestArrayList(childSectionQuestArrayList);

                    }


                    childSectionArrayList.add(childSection);
                }



            }
            parentSection.setChildSectionArrayList(childSectionArrayList);

            /*System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("parentSection.getParentSecId(): "+parentSection.getParentSecId());
            System.out.println("parentSection.getParentId(): "+parentSection.getParentId());
            System.out.println("parentSection.getqSetId(): "+parentSection.getqSetId());
            System.out.println("parentSection.getqSetName()"+parentSection.getqSetName());

            if(parentSection.getChildSectionArrayList()!=null && parentSection.getChildSectionArrayList().size()>0)
            {
                System.out.println("parentSection.getChildSectionArrayList().size(): "+parentSection.getChildSectionArrayList().size());
                for(int cc=0;cc<parentSection.getChildSectionArrayList().size();cc++)
                {
                    System.out.println("--getChildSecId: "+parentSection.getChildSectionArrayList().get(cc).getChildSecId());
                    System.out.println("--getChildQSetId: "+parentSection.getChildSectionArrayList().get(cc).getChildQSetId());
                    System.out.println("--getChildQSetName: "+parentSection.getChildSectionArrayList().get(cc).getChildQSetName());
                    System.out.println("--child getParentId: "+parentSection.getChildSectionArrayList().get(cc).getParentId());

                    if(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList()!=null && parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size()>0)
                    {
                        System.out.println("--getChildSectionQuestArrayList().size(): "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size());
                        for(int qq=0;qq<parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size();qq++)
                        {
                            System.out.println("--------getQuestionid: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());
                            System.out.println("--------getQuestionname: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionname());
                            System.out.println("--------getQsetID: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQsetID());
                            System.out.println("--------question getParentId: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                        }

                    }
                }
            }
            System.out.println("-----------------------------------------------------------------------------------");*/


            questionSubSetArrayList=new ArrayList<>();
            if(fromQuestionSetID!=-1)
            {
                QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
                questionAnswerTransaction1.setQsetID(fromQuestionSetID);
                questionAnswerTransaction1.setQuestionsetName(fromQuestionSetName);
                questionAnswerTransaction1.setParentId(-1);
                questionAnswerTransaction1.setQuestAnsTransId(-1);
                questionAnswerTransaction1.setCorrect(true);

                irQuestionArrayList.add(questionAnswerTransaction1);
                subsetQuestionset=new ArrayList<>();
                subsetQuestionset= questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
                if(subsetQuestionset!=null && subsetQuestionset.size()>0)
                {
                    System.out.println("subsetQuestionset.size()irf : "+subsetQuestionset.size());
                    for(int k=0;k<subsetQuestionset.size();k++)
                    {
                        System.out.println("Question Sub SetName: "+k+" : "+subsetQuestionset.get(k).getQsetname());
                        System.out.println("-------------------------------------------------------------");

                        QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                        questionAnswerTransaction2.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                        questionAnswerTransaction2.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                        questionAnswerTransaction2.setParentId(fromQuestionSetID);
                        questionAnswerTransaction2.setSeqno((k+1));
                        questionAnswerTransaction2.setQuestAnsTransId(-1);
                        questionAnswerTransaction2.setTimestamp(System.currentTimeMillis());
                        questionAnswerTransaction2.setCorrect(true);

                        irQuestionArrayList.add(questionAnswerTransaction2);
                        questionSubSetArrayList.add(questionAnswerTransaction2);
                        ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());

                        System.out.println("qalist=="+questionArrayList.size());
                        if(questionArrayList!=null && questionArrayList.size()>0)
                        {
                            for(int j=0;j<questionArrayList.size();j++)
                            {
                                System.out.println("QuestionName: "+questionArrayList.get(j).getQuestionname());

                                QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
                                questionAnswerTransaction.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                                questionAnswerTransaction.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                                questionAnswerTransaction.setQuestionid(questionArrayList.get(j).getQuestionid());
                                questionAnswerTransaction.setQuestionname(questionArrayList.get(j).getQuestionname());
                                questionAnswerTransaction.setMin(questionArrayList.get(j).getMin());
                                questionAnswerTransaction.setMax(questionArrayList.get(j).getMax());
                                questionAnswerTransaction.setType(questionArrayList.get(j).getType());
                                questionAnswerTransaction.setOptions(questionArrayList.get(j).getOptions());
                                questionAnswerTransaction.setUnit(questionArrayList.get(j).getUnit());
                                questionAnswerTransaction.setCuser(questionArrayList.get(j).getCuser());
                                questionAnswerTransaction.setCdtz(questionArrayList.get(j).getCdtz());
                                questionAnswerTransaction.setMuser(questionArrayList.get(j).getMuser());
                                questionAnswerTransaction.setMdtz(questionArrayList.get(j).getMdtz());
                                questionAnswerTransaction.setQuestAnswer("");
                                questionAnswerTransaction.setParentId(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setSeqno(questionArrayList.get(j).getSeqno());
                                questionAnswerTransaction.setIsmandatory(questionArrayList.get(j).getIsmandatory());
                                questionAnswerTransaction.setAlerton(questionArrayList.get(j).getAlertOn());
                                questionAnswerTransaction.setJobneedid(questionAnswerTransaction2.getTimestamp());//fromid
                                questionAnswerTransaction.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                                questionAnswerTransaction.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setCorrect(true);
                                irQuestionArrayList.add(questionAnswerTransaction);
                            }
                        }


                    }
                }
            }
        }
        else if(fromActivity.equalsIgnoreCase("SITEREPORT"))
        {
            System.out.println("C: SITEREPORT");

            ArrayList<ChildSection>childSectionArrayList=null;
            ArrayList<ChildSectionQuest> childSectionQuestArrayList=null;
            ChildSection childSection=null;
            long childTimestamp=-1;
            parentSection=new ParentSection();
            parentSection.setParentSecId(fromID);
            parentSection.setParentId(-1);
            parentSection.setqSetId(fromQuestionSetID);
            parentSection.setqSetName(fromQuestionSetName);
            ArrayList<QuestionSet>subsetQuestionset1=new ArrayList<>();
            subsetQuestionset1=questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
            System.out.println("subsetQuestionset1: "+subsetQuestionset1.size());
            if(subsetQuestionset1!=null && subsetQuestionset1.size()>0)
            {
                childSectionArrayList=new ArrayList<>();
                for(int c=0;c<subsetQuestionset1.size();c++)
                {
                    childTimestamp=System.currentTimeMillis();
                    childSection=new ChildSection();
                    childSection.setParentId(fromID);
                    childSection.setChildQSetId(subsetQuestionset1.get(c).getQuestionsetid());
                    childSection.setChildSecId(childTimestamp);
                    childSection.setChildQSetName(subsetQuestionset1.get(c).getQsetname());
                    childSection.setChildSeqNo(subsetQuestionset1.get(c).getSeqno());
                    ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset1.get(c).getQuestionsetid());
                    System.out.println("questionArrayList: "+questionArrayList.size());
                    if(questionArrayList!=null && questionArrayList.size()>0)
                    {
                        childSectionQuestArrayList=new ArrayList<>();
                        for(int q=0;q<questionArrayList.size();q++)
                        {
                            ChildSectionQuest childSectionQuest=new ChildSectionQuest();
                            childSectionQuest.setParentId(childTimestamp);
                            childSectionQuest.setQsetID(subsetQuestionset1.get(c).getQuestionsetid());
                            childSectionQuest.setQuestionsetName(subsetQuestionset1.get(c).getQsetname());

                            childSectionQuest.setQuestionid(questionArrayList.get(q).getQuestionid());
                            childSectionQuest.setQuestionname(questionArrayList.get(q).getQuestionname());

                            childSectionQuest.setQuestAnsTransId(System.currentTimeMillis());
                            childSectionQuest.setMin(questionArrayList.get(q).getMin());
                            childSectionQuest.setMax(questionArrayList.get(q).getMax());
                            childSectionQuest.setType(questionArrayList.get(q).getType());
                            childSectionQuest.setOptions(questionArrayList.get(q).getOptions());
                            childSectionQuest.setUnit(questionArrayList.get(q).getUnit());
                            childSectionQuest.setCuser(questionArrayList.get(q).getCuser());
                            childSectionQuest.setCdtz(questionArrayList.get(q).getCdtz());
                            childSectionQuest.setMuser(questionArrayList.get(q).getMuser());
                            childSectionQuest.setMdtz(questionArrayList.get(q).getMdtz());
                            childSectionQuest.setQuestAnswer("");
                            childSectionQuest.setSeqno(questionArrayList.get(q).getSeqno());
                            childSectionQuest.setIsmandatory(questionArrayList.get(q).getIsmandatory());
                            childSectionQuest.setAlerton(questionArrayList.get(q).getAlertOn());
                            childSectionQuest.setJobneedid(childTimestamp);
                            childSectionQuest.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                            childSectionQuest.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                            childSectionQuest.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));

                            childSectionQuestArrayList.add(childSectionQuest);

                        }
                        childSection.setChildSectionQuestArrayList(childSectionQuestArrayList);

                    }


                    childSectionArrayList.add(childSection);
                }



            }
            parentSection.setChildSectionArrayList(childSectionArrayList);

            /*System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("parentSection.getParentSecId(): "+parentSection.getParentSecId());
            System.out.println("parentSection.getParentId(): "+parentSection.getParentId());
            System.out.println("parentSection.getqSetId(): "+parentSection.getqSetId());
            System.out.println("parentSection.getqSetName()"+parentSection.getqSetName());

            if(parentSection.getChildSectionArrayList()!=null && parentSection.getChildSectionArrayList().size()>0)
            {
                System.out.println("parentSection.getChildSectionArrayList().size(): "+parentSection.getChildSectionArrayList().size());
                for(int cc=0;cc<parentSection.getChildSectionArrayList().size();cc++)
                {
                    System.out.println("--getChildSecId: "+parentSection.getChildSectionArrayList().get(cc).getChildSecId());
                    System.out.println("--getChildQSetId: "+parentSection.getChildSectionArrayList().get(cc).getChildQSetId());
                    System.out.println("--getChildQSetName: "+parentSection.getChildSectionArrayList().get(cc).getChildQSetName());
                    System.out.println("--child getParentId: "+parentSection.getChildSectionArrayList().get(cc).getParentId());

                    if(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList()!=null && parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size()>0)
                    {
                        System.out.println("--getChildSectionQuestArrayList().size(): "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size());
                        for(int qq=0;qq<parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size();qq++)
                        {
                            System.out.println("--------getQuestionid: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());
                            System.out.println("--------getQuestionname: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionname());
                            System.out.println("--------getQsetID: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQsetID());
                            System.out.println("--------question getParentId: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                        }

                    }
                }
            }
            System.out.println("-----------------------------------------------------------------------------------");*/


            questionSubSetArrayList=new ArrayList<>();
            if(fromQuestionSetID!=-1)
            {
                QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
                questionAnswerTransaction1.setQsetID(fromQuestionSetID);
                questionAnswerTransaction1.setQuestionsetName(fromQuestionSetName);
                questionAnswerTransaction1.setParentId(-1);
                questionAnswerTransaction1.setQuestAnsTransId(-1);
                questionAnswerTransaction1.setCorrect(true);

                irQuestionArrayList.add(questionAnswerTransaction1);
                subsetQuestionset=new ArrayList<>();
                subsetQuestionset= questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);
                if(subsetQuestionset!=null && subsetQuestionset.size()>0)
                {
                    System.out.println("subsetQuestionset.size(): "+subsetQuestionset.size());
                    for(int k=0;k<subsetQuestionset.size();k++)
                    {
                        System.out.println("Question Sub SetName: "+k+" : "+subsetQuestionset.get(k).getQsetname());
                        System.out.println("-------------------------------------------------------------");

                        QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                        questionAnswerTransaction2.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                        questionAnswerTransaction2.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                        questionAnswerTransaction2.setParentId(fromQuestionSetID);
                        questionAnswerTransaction2.setSeqno((k+1));
                        questionAnswerTransaction2.setQuestAnsTransId(-1);
                        questionAnswerTransaction2.setTimestamp(System.currentTimeMillis());
                        questionAnswerTransaction2.setCorrect(true);

                        irQuestionArrayList.add(questionAnswerTransaction2);
                        questionSubSetArrayList.add(questionAnswerTransaction2);
                        ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());
                        if(questionArrayList!=null && questionArrayList.size()>0)
                        {
                            for(int j=0;j<questionArrayList.size();j++)
                            {
                                System.out.println("QuestionName: "+questionArrayList.get(j).getQuestionname());

                                QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
                                questionAnswerTransaction.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
                                questionAnswerTransaction.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                                questionAnswerTransaction.setQuestionid(questionArrayList.get(j).getQuestionid());
                                questionAnswerTransaction.setQuestionname(questionArrayList.get(j).getQuestionname());
                                questionAnswerTransaction.setMin(questionArrayList.get(j).getMin());
                                questionAnswerTransaction.setMax(questionArrayList.get(j).getMax());
                                questionAnswerTransaction.setType(questionArrayList.get(j).getType());
                                questionAnswerTransaction.setOptions(questionArrayList.get(j).getOptions());
                                questionAnswerTransaction.setUnit(questionArrayList.get(j).getUnit());
                                questionAnswerTransaction.setCuser(questionArrayList.get(j).getCuser());
                                questionAnswerTransaction.setCdtz(questionArrayList.get(j).getCdtz());
                                questionAnswerTransaction.setMuser(questionArrayList.get(j).getMuser());
                                questionAnswerTransaction.setMdtz(questionArrayList.get(j).getMdtz());
                                questionAnswerTransaction.setQuestAnswer("");
                                questionAnswerTransaction.setParentId(subsetQuestionset.get(k).getQuestionsetid());
                                questionAnswerTransaction.setSeqno(questionArrayList.get(j).getSeqno());
                                questionAnswerTransaction.setIsmandatory(questionArrayList.get(j).getIsmandatory());
                                questionAnswerTransaction.setAlerton(questionArrayList.get(j).getAlertOn());
                                questionAnswerTransaction.setJobneedid(questionAnswerTransaction2.getTimestamp());//fromid
                                questionAnswerTransaction.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                                questionAnswerTransaction.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setCorrect(true);
                                irQuestionArrayList.add(questionAnswerTransaction);
                            }
                        }


                    }
                }
            }
        }
        else if(fromActivity.equalsIgnoreCase("SITEREPORT_SCHEDULE"))
        {
            jobNeed=jobNeedDAO.getJobNeedDetails(fromID);

            questionSubSetArrayList=new ArrayList<>();
            if(fromQuestionSetID!=-1)
            {
                QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
                questionAnswerTransaction1.setQsetID(fromQuestionSetID);
                questionAnswerTransaction1.setQuestionsetName(fromQuestionSetName);
                questionAnswerTransaction1.setParentId(-1);
                questionAnswerTransaction1.setQuestAnsTransId(-1);
                questionAnswerTransaction1.setCorrect(true);


                irQuestionArrayList.add(questionAnswerTransaction1);
                /*subsetQuestionset=new ArrayList<>();
                subsetQuestionset= questionDAO.getQuestionSubSetCodeList(fromQuestionSetID);*/

                schSiteReportParentList=new ArrayList<>();
                schSiteReportParentList=jobNeedDAO.getSiteReportSectionsList(fromID);

                if(schSiteReportParentList!=null && schSiteReportParentList.size()>0)
                {
                    System.out.println("schSiteReportParentList.size(): "+schSiteReportParentList.size());
                    for(int k=0;k<schSiteReportParentList.size();k++)
                    {
                        System.out.println("Question Sub SetName: "+k+" : "+schSiteReportParentList.get(k).getJobdesc());
                        System.out.println("Question Sub seqno: "+k+" : "+schSiteReportParentList.get(k).getSeqno());
                        System.out.println("-------------------------------------------------------------");

                        QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
                        questionAnswerTransaction2.setQsetID(schSiteReportParentList.get(k).getQuestionsetid());
                        questionAnswerTransaction2.setQuestionsetName(schSiteReportParentList.get(k).getJobdesc());
                        questionAnswerTransaction2.setParentId(fromQuestionSetID);
                        questionAnswerTransaction2.setSeqno((k+1));
                        questionAnswerTransaction2.setQuestAnsTransId(-1);
                        questionAnswerTransaction2.setCorrect(true);

                        irQuestionArrayList.add(questionAnswerTransaction2);
                        questionSubSetArrayList.add(questionAnswerTransaction2);
                        //ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());
                        ArrayList<JobNeedDetails> jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(schSiteReportParentList.get(k).getJobneedid());
                        if(jobNeedDetailsArrayList!=null && jobNeedDetailsArrayList.size()>0)
                        {
                            for(int j=0;j<jobNeedDetailsArrayList.size();j++)
                            {
                                System.out.println("QuestionName: "+questionDAO.getQuestionName(jobNeedDetailsArrayList.get(j).getQuestionid()));

                                QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
                                questionAnswerTransaction.setQuestionsetName(questionDAO.getQuestionSetName(schSiteReportParentList.get(k).getQuestionsetid()));
                                questionAnswerTransaction.setQsetID(schSiteReportParentList.get(k).getQuestionsetid());
                                questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
                                questionAnswerTransaction.setQuestionid(jobNeedDetailsArrayList.get(j).getQuestionid());
                                questionAnswerTransaction.setQuestionname(questionDAO.getQuestionName(jobNeedDetailsArrayList.get(j).getQuestionid()));
                                questionAnswerTransaction.setMin(jobNeedDetailsArrayList.get(j).getMin());
                                questionAnswerTransaction.setMax(jobNeedDetailsArrayList.get(j).getMax());
                                questionAnswerTransaction.setType(jobNeedDetailsArrayList.get(j).getType());
                                questionAnswerTransaction.setOptions(jobNeedDetailsArrayList.get(j).getOption());
                                questionAnswerTransaction.setCuser(jobNeedDetailsArrayList.get(j).getCuser());
                                questionAnswerTransaction.setCdtz(jobNeedDetailsArrayList.get(j).getCdtz());
                                questionAnswerTransaction.setMuser(jobNeedDetailsArrayList.get(j).getMuser());
                                questionAnswerTransaction.setMdtz(jobNeedDetailsArrayList.get(j).getMdtz());
                                questionAnswerTransaction.setAlerton(jobNeedDetailsArrayList.get(j).getAlerton());
                                questionAnswerTransaction.setQuestAnswer("");
                                questionAnswerTransaction.setParentId(schSiteReportParentList.get(k).getQuestionsetid());
                                questionAnswerTransaction.setSeqno(jobNeedDetailsArrayList.get(j).getSeqno());
                                questionAnswerTransaction.setIsmandatory(jobNeedDetailsArrayList.get(j).getIsmandatory());
                                questionAnswerTransaction.setJobneedid(schSiteReportParentList.get(k).getJobneedid());
                                questionAnswerTransaction.setJndid(jobNeedDetailsArrayList.get(j).getJndid());
                                questionAnswerTransaction.setBuid(siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1));
                                questionAnswerTransaction.setParentActivity(parentActivity.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setParentFolder(parentFolder.toLowerCase(Locale.ENGLISH));
                                questionAnswerTransaction.setCorrect(true);
                                irQuestionArrayList.add(questionAnswerTransaction);
                            }
                        }


                    }
                }
            }
        }
        /*else {
            for (int i = 0; i < 10; i++) {
                QuestionAnswerTransaction question = new QuestionAnswerTransaction();
                question.setQuestionname("Question " + i);
                question.setType(i);
                irQuestionArrayList.add(question);
            }
        }*/
//---------------------------------------------------------------------set adapter for listview
        if(fromActivity.equalsIgnoreCase("INCIDENTREPORT") || fromActivity.equalsIgnoreCase("INCIDENTREPORT_LIST")
                || fromActivity.equalsIgnoreCase("SITEREPORT") || fromActivity.equalsIgnoreCase("SITEREPORT_SCHEDULE")) {
            if (irQuestionArrayList != null && irQuestionArrayList.size() > 0) {
                incidentReportQuesAdapter = new IncidentReportQuesAdapter(IncidentReportQuestionActivity.this, R.layout.activity_incident_report_question_row, irQuestionArrayList,fromID);
                listView.setAdapter(incidentReportQuesAdapter);
            }
        }
        /*else if(fromActivity.equalsIgnoreCase("SITEREPORT"))
        {
            if(parentSection.getChildSectionArrayList()!=null && parentSection.getChildSectionArrayList().size()>0)
            {
                ArrayList<ParentSection> parentSectionArrayList=new ArrayList<>();
                parentSectionArrayList.add(parentSection);
                siteReportQuesAdapter=new SiteReportQuesAdapter(IncidentReportQuestionActivity.this, R.layout.activity_incident_report_question_row,parentSectionArrayList);
                listView.setAdapter(siteReportQuesAdapter);
            }
        }*/
        else if(fromActivity.equalsIgnoreCase("JOB"))
        {
            if(jndForReadingArrayList!=null && jndForReadingArrayList.size()>0)
            {
                jobNeedDetailsQuesAdapter=new JobNeedDetailsQuesAdapter(IncidentReportQuestionActivity.this, R.layout.activity_incident_report_question_row,jndForReadingArrayList );
                listView.setAdapter(jobNeedDetailsQuesAdapter);
            }
        }
        else if(fromActivity.equalsIgnoreCase("TOUR"))
        {
            if(jndForReadingArrayList!=null && jndForReadingArrayList.size()>0)
            {
                jobNeedDetailsQuesAdapter=new JobNeedDetailsQuesAdapter(IncidentReportQuestionActivity.this, R.layout.activity_incident_report_question_row,jndForReadingArrayList );
                listView.setAdapter(jobNeedDetailsQuesAdapter);
            }
        }
        else if(fromActivity.equalsIgnoreCase("CHECKPOINT") || fromActivity.equalsIgnoreCase("ASSET") || fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("ADHOC_SCAN") || fromActivity.equalsIgnoreCase("REQUEST"))
        {
            if(questionAnswerTransactionArrayList!=null && questionAnswerTransactionArrayList.size()>0)
            {
                checkPointQuestAdapter=new CheckPointQuestAdapter(IncidentReportQuestionActivity.this, R.layout.activity_incident_report_question_row,questionAnswerTransactionArrayList);
                listView.setAdapter(checkPointQuestAdapter);
            }
        }

        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        //listView.setFriction(ViewConfiguration.getScrollFriction()*2);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.avp_menu, menu);

        item_pic=menu.findItem(R.id.action_picture);
        item_audio=menu.findItem(R.id.action_audio);
        item_video=menu.findItem(R.id.action_video);

        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.MIC_RECORD_AUDIO_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK) {
                int audioCount=0;
                if(fromActivity.equalsIgnoreCase("ADHOC_SCAN") || fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("CHECKPOINT"))
                {
                    audioCount=attachmentDAO.getAttachmentCount(fromID, adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_AUDIO);
                }
                else if(fromActivity.equalsIgnoreCase("TOUR"))
                {
                    audioCount=attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_AUDIO);
                }
                else
                {
                    audioCount=attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_AUDIO);
                }
                System.out.println("Audio Count: " + "" + audioCount);
                LayerDrawable audioIcon = (LayerDrawable) item_audio.getIcon();
                CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this, audioIcon, "" + audioCount, R.id.ic_audio_badge);
                invalidateOptionsMenu();
            }
        }
        else if(requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK) {

                int videoCount=0;
                if(fromActivity.equalsIgnoreCase("ADHOC_SCAN") || fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("CHECKPOINT"))
                {
                    videoCount=attachmentDAO.getAttachmentCount(fromID, adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_VIDEO);
                }
                else if(fromActivity.equalsIgnoreCase("TOUR"))
                {
                    videoCount=attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_VIDEO);
                }
                else
                {
                    videoCount=attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_VIDEO);
                }

                LayerDrawable videoIcon = (LayerDrawable) item_video.getIcon();
                System.out.println("video Count: " + "" + videoCount);
                CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this, videoIcon, "" + videoCount, R.id.ic_video_badge);
                invalidateOptionsMenu();
            }
        }
        else if(requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    if(qType!=-1) {
                        if (fromActivity.equalsIgnoreCase("CHECKPOINT") || fromActivity.equalsIgnoreCase("ASSET") || fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("ADHOC_SCAN")) {
                            checkPointQuestAdapter.getData().get(position).setImagePath(data.getStringExtra("IMG_PATH"));
                            checkPointQuestAdapter.getData().get(position).setQuestAnswer("Done");
                            checkPointQuestAdapter.notifyDataSetChanged();
                        }
                        else if(fromActivity.equalsIgnoreCase("TOUR")|| fromActivity.equalsIgnoreCase("JOB"))
                        {
                            jobNeedDetailsQuesAdapter.getData().get(position).setImagePath(data.getStringExtra("IMG_PATH"));
                            jobNeedDetailsQuesAdapter.getData().get(position).setQuestAnswer("Done");
                            jobNeedDetailsQuesAdapter.notifyDataSetChanged();
                        }
                        else if(fromActivity.equalsIgnoreCase("INCIDENTREPORT") || fromActivity.equalsIgnoreCase("INCIDENTREPORT_LIST")
                                || fromActivity.equalsIgnoreCase("SITEREPORT") || fromActivity.equalsIgnoreCase("SITEREPORT_SCHEDULE"))
                        {
                            incidentReportQuesAdapter.getData().get(position).setImagePath(data.getStringExtra("IMG_PATH"));
                            incidentReportQuesAdapter.getData().get(position).setQuestAnswer("Done");
                            incidentReportQuesAdapter.notifyDataSetChanged();
                        }

                    }
                }

                int imgCount=0;
                if(fromActivity.equalsIgnoreCase("ADHOC_SCAN") || fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("CHECKPOINT"))
                {
                    imgCount=attachmentDAO.getAttachmentCount(fromID, adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_PICTURE);
                }
                else if(fromActivity.equalsIgnoreCase("TOUR"))
                {
                    imgCount=attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_PICTURE);
                }
                else
                {
                    imgCount=attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_PICTURE);
                }
                LayerDrawable picIcon=(LayerDrawable)item_pic.getIcon();
                CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this,picIcon,""+imgCount, R.id.ic_picture_badge);
                invalidateOptionsMenu();


                /*int imgCount=0;
                LayerDrawable picIcon=(LayerDrawable)item_pic.getIcon();
                if(fromActivity.equalsIgnoreCase("TOUR"))
                    imgCount=attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_PICTURE);
                else
                    imgCount=attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_PICTURE);

                System.out.println("Pic Count: "+""+imgCount);
                CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this,picIcon,""+imgCount,R.id.ic_picture_badge);
                invalidateOptionsMenu();*/
            }
        }
        else if(requestCode==100)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    onCaptureImageResult(data);
                }
            }
        }
        else if(requestCode==3)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    checkPointQuestAdapter.getData().get(position).setQuestAnswer(data.getStringExtra("SCAN_RESULT"));
                    checkPointQuestAdapter.notifyDataSetChanged();
                }
            }
        }


    }

    private void onCaptureImageResult(Intent data) {

        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), imageBitmap, imageTempName);
        String picturePath = getRealPathFromURI(tempUri);
        checkPointQuestAdapter.setImageInItem(position, picturePath);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage, String imageName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        qType=-1;

        switch(item.getItemId())
        {

            case R.id.action_audio:
                if(CommonFunctions.isPermissionGranted(IncidentReportQuestionActivity.this))
                    callIntent(AUDIO_INTENT);
                else
                    Snackbar.make(saveButton,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.action_video:
                if(CommonFunctions.isPermissionGranted(IncidentReportQuestionActivity.this))
                    callIntent(VIDEO_INTENT);
                else
                    Snackbar.make(saveButton,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.action_picture:
                if(CommonFunctions.isPermissionGranted(IncidentReportQuestionActivity.this))
                    callIntent(PICTURE_INTENT);
                else
                    Snackbar.make(saveButton,getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int imgCount=0;
        LayerDrawable picIcon=(LayerDrawable)item_pic.getIcon();
        /*if(fromActivity.equalsIgnoreCase("TOUR"))
            imgCount=attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_PICTURE);
        else
            imgCount=attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_PICTURE);
        System.out.println("Pic Count: "+""+imgCount);*/
        if(fromActivity.equalsIgnoreCase("ADHOC_SCAN")|| fromActivity.equalsIgnoreCase("ADHOC")|| fromActivity.equalsIgnoreCase("CHECKPOINT"))
            imgCount= attachmentDAO.getAttachmentCount(fromID, adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_PICTURE);
        else if(fromActivity.equalsIgnoreCase("TOUR"))
            imgCount= attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_PICTURE);
        else
            imgCount= attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_PICTURE);
        System.out.println("Pic Count: "+""+imgCount);
        CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this,picIcon,""+imgCount,R.id.ic_picture_badge);


        int audioCount=0;
        if(fromActivity.equalsIgnoreCase("ADHOC_SCAN")|| fromActivity.equalsIgnoreCase("ADHOC")|| fromActivity.equalsIgnoreCase("CHECKPOINT"))
            audioCount= attachmentDAO.getAttachmentCount(fromID, adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_AUDIO);
        else if(fromActivity.equalsIgnoreCase("TOUR"))
            audioCount= attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_AUDIO);
        else
            audioCount= attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_AUDIO);
        LayerDrawable audioIcon=(LayerDrawable)item_audio.getIcon();
        System.out.println("audio Count: "+""+audioCount);
        CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this,audioIcon,""+audioCount,R.id.ic_audio_badge);


        int videoCount=0;
        if(fromActivity.equalsIgnoreCase("ADHOC_SCAN")|| fromActivity.equalsIgnoreCase("ADHOC")|| fromActivity.equalsIgnoreCase("CHECKPOINT"))
            videoCount= attachmentDAO.getAttachmentCount(fromID, adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1), Constants.ATTACHMENT_VIDEO);
        else if(fromActivity.equalsIgnoreCase("TOUR"))
            videoCount= attachmentDAO.getAttachmentCount(parentJobNeedId, currentTimestamp, Constants.ATTACHMENT_VIDEO);
        else
            videoCount= attachmentDAO.getAttachmentCount(fromID, currentTimestamp, Constants.ATTACHMENT_VIDEO);
        LayerDrawable videoIcon=(LayerDrawable)item_video.getIcon();
        System.out.println("video Count: "+""+videoCount);
        CommonFunctions.setBadgeCount(IncidentReportQuestionActivity.this,videoIcon,""+videoCount,R.id.ic_video_badge);

        invalidateOptionsMenu();
        return true;
    }

    public void captureImage(int pos, int qType) {
        position = pos;
        this.qType=qType;
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        startActivityForResult(intent, 100);*/

        callIntent(PICTURE_INTENT);
    }

    public void captureQrCode(int pos) {
        position = pos;
        Intent intent= new Intent(IncidentReportQuestionActivity.this, CaptureActivity.class);
        intent.putExtra("FROM","CHECKPOINT");
        startActivityForResult(intent, 3);
    }

    private void callIntent(int intentValue)
    {
        if(intentValue==AUDIO_INTENT)
        {
            Intent capturePic=new Intent(IncidentReportQuestionActivity.this, MediaRecoderView.class);
            capturePic.putExtra("FROM",Constants.ATTACHMENT_AUDIO);
            if(fromActivity.equalsIgnoreCase("ADHOC_SCAN")|| fromActivity.equalsIgnoreCase("ADHOC")|| fromActivity.equalsIgnoreCase("CHECKPOINT"))
                capturePic.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
            else
                capturePic.putExtra("TIMESTAMP",currentTimestamp);

            if(fromActivity.equalsIgnoreCase("TOUR"))
                capturePic.putExtra("JOBNEEDID",parentJobNeedId);
            else
                capturePic.putExtra("JOBNEEDID",fromID);

            capturePic.putExtra("PARENT_ACTIVITY", parentActivity);
            capturePic.putExtra("FOLDER",parentFolder);
            startActivityForResult(capturePic,Constants.MIC_RECORD_AUDIO_REQUEST_CODE);
        }
        else if(intentValue==VIDEO_INTENT)
        {
            Intent capturePic=new Intent(IncidentReportQuestionActivity.this, VideoCaptureActivity.class);
            capturePic.putExtra("FROM",Constants.ATTACHMENT_VIDEO);
            if(fromActivity.equalsIgnoreCase("ADHOC_SCAN")|| fromActivity.equalsIgnoreCase("ADHOC")|| fromActivity.equalsIgnoreCase("CHECKPOINT"))
                capturePic.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
            else
                capturePic.putExtra("TIMESTAMP",currentTimestamp);

            if(fromActivity.equalsIgnoreCase("TOUR"))
                capturePic.putExtra("JOBNEEDID",parentJobNeedId);
            else
                capturePic.putExtra("JOBNEEDID",fromID);

            capturePic.putExtra("PARENT_ACTIVITY",parentActivity);
            capturePic.putExtra("FOLDER",parentFolder);
            startActivityForResult(capturePic,Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        }
        else if(intentValue==PICTURE_INTENT)
        {
            Intent capturePic=new Intent(IncidentReportQuestionActivity.this, CapturePhotoActivity.class);
            capturePic.putExtra("FROM",Constants.ATTACHMENT_PICTURE);
            if(fromActivity.equalsIgnoreCase("ADHOC_SCAN")|| fromActivity.equalsIgnoreCase("ADHOC")|| fromActivity.equalsIgnoreCase("CHECKPOINT"))
                capturePic.putExtra("TIMESTAMP",adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
            else
                capturePic.putExtra("TIMESTAMP",currentTimestamp);

            if(fromActivity.equalsIgnoreCase("TOUR"))
                capturePic.putExtra("JOBNEEDID",parentJobNeedId);
            else
                capturePic.putExtra("JOBNEEDID",fromID);

            capturePic.putExtra("PARENT_ACTIVITY",parentActivity);
            capturePic.putExtra("FOLDER",parentFolder);
            capturePic.putExtra("CAMERA",(qType));
            startActivityForResult(capturePic,Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        /*Intent nexIntent=new Intent(IncidentReportQuestionActivity.this, AttachmentListActivity.class);
        nexIntent.putExtra("FROM",intentValue);
        nexIntent.putExtra("TIMESTAMP",currentTimestamp);
        nexIntent.putExtra("JOBNEEDID",fromID);
        nexIntent.putExtra("PARENT_ACTIVITY", fromActivity);
        startActivityForResult(nexIntent,intentValue);*/
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==SCROLL_STATE_FLING)
        {
            InputMethodManager inputMethodManger = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void saveJOBNeedActivity()
    {
        boolean isValidData = true;
        if(jobNeedDetailsQuesAdapter!=null && jobNeedDetailsQuesAdapter.getData().size()>0) {
            CommonFunctions.ReadingLog("\n"+"Reading Taken"+"\n"+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"\n"+"JOB Need ID: "+fromID+"\n");

            for (QuestionAnswerTransaction quest : jobNeedDetailsQuesAdapter.getData()) {
                jobNeedDetailsDAO.changeQuestionAns(quest.getJndid(), quest.getQuestAnswer(), quest.getQuestionid());
                CommonFunctions.ReadingLog("\n"+"Reading: "+"\n"+quest.getJobneedid()+" : "+quest.getJndid()+" : "+quest.getQuestAnswer()+" : "+quest.getQuestionname()+"\n");
                CommonFunctions.manualSyncEventLog("JOB_PERFORM_READING","\n"+"Reading: "+"\n"+quest.getJobneedid()+" : "+quest.getJndid()+" : "+quest.getQuestAnswer()+" : "+quest.getQuestionname()+"\n",CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                System.out.println("Ans: " + quest.getQuestAnswer().trim());
                /*if (quest.getQuestAnswer().trim().length() > 0)
                    System.out.println("Done");
                else {
                    System.out.println("Failed");
                    isValidData = false;
                }*/
            }

            if (isValidData) {

                jobNeedDAO.changeJobNeedSyncStatus(fromID, Constants.SYNC_STATUS_TWO);
                //jobNeedDAO.changeJobStatus(fromID,typeAssistDAO.getEventTypeID("COMPLETED", Constants.STATUS_TYPE_JOBNEED));
                jobNeedDAO.updateJobNeedRecord(fromID, jobNeed.getPeopleid(), jobNeed.getGroupid(), "Reading Done", typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED), peopleID);
                CommonFunctions.EventLog("\n Reading Done: \n JOBNeed Id: "+fromID+"\n Time: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" \n");
                CommonFunctions.manualSyncEventLog("JOB_PERFORM_END","JOBNEEDID: "+fromID,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            }
        }

        setResult(RESULT_OK);
        finish();
    }


    private void saveIRActivity(String syncStatus, String identifier, long siteID)
    {
        System.out.println("saveIRActivity called::");
            System.out.println("save activity is going on");
            boolean isValidData = true;
            ArrayList<JobNeedDetails> jobNeedDetailsArrayList = new ArrayList<>();
            UploadIncidentReportParameter uploadIncidentReportParameter = new UploadIncidentReportParameter();
            ArrayList<QuestionSetLevel_One> questionSetLevelOneArrayList = new ArrayList<>();
            ArrayList<QuestionAnswerTransaction> questionAnswerTransactionArrayListtemp = new ArrayList<>();
            QuestionSetLevel_One questionSetLevelOne = null;
            ArrayList<QuestionSetLevel_Two> questionSetLevelTwoArrayList = new ArrayList<>();
            QuestionSetLevel_Two questionSetLevelTwo = null;

/*        if(incidentReportQuesAdapter!=null && incidentReportQuesAdapter.getData().size()>0) {
            //insert data into database
            for (QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData()) {
                if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                    JobNeedDetails jobNeedDetails = new JobNeedDetails();
                    jobNeedDetails.setJndid(System.currentTimeMillis());
                    jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
                    jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
                    jobNeedDetails.setType(questionAnswerTransaction.getType());
                    jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
                    System.out.println("SiteAudit Ans: " + questionAnswerTransaction.getQuestAnswer());
                    jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
                    jobNeedDetails.setMin(questionAnswerTransaction.getMin());
                    jobNeedDetails.setMax(questionAnswerTransaction.getMax());
                    jobNeedDetails.setAlerton("");
                    jobNeedDetails.setIsmandatory("");
                    jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setCuser(peopleID);
                    jobNeedDetails.setJobneedid(questionAnswerTransaction.getParentId());
                    //jobNeedDetails.setJobneedid(fromID);
                    jobNeedDetails.setMuser(peopleID);


                    //jobNeedDetailsDAO.insertOrUpdateRecord(jobNeedDetails);
                    jobNeedDetailsDAO.test_insertOrUpdateRecord(jobNeedDetails);
                }
            }


            System.out.println("Jobneed details count: " + jobNeedDetailsDAO.getCount(fromID));

            JobNeed jobNeed = new JobNeed();
            jobNeed.setJobneedid(fromID);
            jobNeed.setJobdesc(fromQuestionSetName);

            jobNeed.setFrequency(-1);
            jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
            jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
            jobNeed.setGracetime(0);
            jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
            jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
            jobNeed.setScantype(-1);
            jobNeed.setReceivedonserver("");
            jobNeed.setPriority(-1);
            jobNeed.setStarttime("");
            jobNeed.setEndtime("");
            jobNeed.setGpslocation(gpsLocation);
            jobNeed.setRemarks(fromQuestionSetName);
            jobNeed.setCuser(peopleID);
            jobNeed.setCdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
            jobNeed.setMuser(peopleID);
            jobNeed.setMdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
            //jobNeed.setIsdeleted("false");
            jobNeed.setAssetid(-1);//aadd asset id scan
            jobNeed.setGroupid(-1);
            jobNeed.setPeopleid(peopleID);
            jobNeed.setAatop(peopleID);
            jobNeed.setJobid(-1);
            jobNeed.setPerformedby(peopleID);
            jobNeed.setQuestionsetid(fromQuestionSetID);
            jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(identifier, Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
            jobNeed.setParent(-1);
            jobNeed.setSeqno(-1);
            jobNeed.setTicketcategory(-1);
            jobNeed.setBuid(siteID);
            jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER, 0));
            if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, false))
                jobNeed.setOthersite(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "Other Site"));
            else
                jobNeed.setOthersite("");

            System.out.println("Parent JOBneedid: " + fromID);
            System.out.println("Parent JOBneedquestsetname: " + fromQuestionSetName);
            System.out.println("Parent JOBneedParentid: -1");
            //jobNeedDAO.insertOrUpdateParentRecord(jobNeed, syncStatus);
            jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed, syncStatus);


            CommonFunctions.EventLog("\n Incident Report Saved: \n JOBNeed Id: "+fromID+"\n Time: "+CommonFunctions.getTimezoneDate(currentTimestamp)+"\n");

            for (int qss = 0; qss < questionSubSetArrayList.size(); qss++) {
                if (fromQuestionSetID == questionSubSetArrayList.get(qss).getParentId()) {
                    jobNeed = new JobNeed();
                    jobNeed.setJobneedid(fromID);
                    jobNeed.setJobdesc(questionSubSetArrayList.get(qss).getQuestionsetName());
                    jobNeed.setSeqno(questionSubSetArrayList.get(qss).getSeqno());

                    jobNeed.setFrequency(-1);
                    jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
                    jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
                    jobNeed.setGracetime(0);
                    jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
                    jobNeed.setJobstatus(-1);//need to get id form type assist for Assigned status
                    jobNeed.setScantype(-1);
                    jobNeed.setReceivedonserver("");
                    jobNeed.setPriority(-1);
                    jobNeed.setStarttime("");
                    jobNeed.setEndtime("");
                    jobNeed.setGpslocation(gpsLocation);
                    jobNeed.setRemarks("");
                    jobNeed.setCuser(peopleID);
                    jobNeed.setCdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
                    jobNeed.setMuser(peopleID);
                    jobNeed.setMdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
                    //jobNeed.setIsdeleted("false");
                    jobNeed.setAssetid(-1);//aadd asset id scan
                    jobNeed.setGroupid(-1);
                    jobNeed.setPeopleid(peopleID);
                    jobNeed.setAatop(peopleID);
                    jobNeed.setJobid(-1);
                    jobNeed.setPerformedby(peopleID);
                    jobNeed.setQuestionsetid(questionSubSetArrayList.get(qss).getQsetID());
                    jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(identifier, Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
                    //jobNeed.setParent(fromQuestionSetID);

                    jobNeed.setParent(fromID);

                    jobNeed.setTicketcategory(-1);
                    jobNeed.setBuid(siteID);
                    jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER, 0));
                    if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, false))
                        jobNeed.setOthersite(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, "Other Site"));
                    else
                        jobNeed.setOthersite("");

                    System.out.println("Child JOBneedid: " + jobNeed.getJobneedid());
                    System.out.println("Child JOBneed qsetid: " + questionSubSetArrayList.get(qss).getQsetID());
                    System.out.println("Child JOBneed qsetname: " + questionSubSetArrayList.get(qss).getQuestionsetName());
                    System.out.println("Child JOBneedParentid: " + fromQuestionSetID);
                    //jobNeedDAO.insertOrUpdateRecord(jobNeed, syncStatus);
                    jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed, syncStatus);

                }
            }


            jobNeedDAO.getJobNeedCount(fromID);
        }*/

            //----------------------------------------------------------------------------

            ArrayList<ChildSectionQuest> childSectionQuestArrayList1 = new ArrayList<>();
            System.out.println("incidentReportQuesAdapter"+ incidentReportQuesAdapter.getData().size());


            if (incidentReportQuesAdapter != null && incidentReportQuesAdapter.getData().size() > 0) {
                for (QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData()) {
                    if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                        ChildSectionQuest childSectionQuest = new ChildSectionQuest();
                        childSectionQuest.setQuestionname(questionAnswerTransaction.getQuestionname());
                        childSectionQuest.setQuestionid(questionAnswerTransaction.getQuestionid());
                        childSectionQuest.setQuestAnswer(questionAnswerTransaction.getQuestAnswer());
                        childSectionQuest.setQsetID(questionAnswerTransaction.getQsetID());
                        childSectionQuest.setAlerton(questionAnswerTransaction.getAlerton());
                        childSectionQuestArrayList1.add(childSectionQuest);
                    }
                }
            }

            System.out.println("child section array=="+ childSectionQuestArrayList1.size());

         /*   System.out.println("parentSection.getParentSecId(): " + parentSection.getParentSecId());
            System.out.println("parentSection.getParentId(): " + parentSection.getParentId());
            System.out.println("parentSection.getqSetId(): " + parentSection.getqSetId());
            System.out.println("parentSection.getqSetName()" + parentSection.getqSetName());*/
            if (parentSection.getChildSectionArrayList() != null && parentSection.getChildSectionArrayList().size() > 0) {
                System.out.println("parentSection.getChildSectionArrayList().size(): " + parentSection.getChildSectionArrayList().size());
                System.out.println("parentSection.getqSetName() " +parentSection.getqSetName());

                addParentSection(syncStatus, parentSection.getParentSecId(), parentSection.getqSetId(), parentSection.getqSetName(), parentSection.getParentId(), identifier, siteID);

                if (parentSection.getChildSectionArrayList().size() > 0) {
                    for (int cc = 0; cc < parentSection.getChildSectionArrayList().size(); cc++) {
                        System.out.println("--getChildSecId: " + parentSection.getChildSectionArrayList().get(cc).getChildSecId());
                        System.out.println("--getChildQSetId: " + parentSection.getChildSectionArrayList().get(cc).getChildQSetId());
                        System.out.println("--getChildQSetName: " + parentSection.getChildSectionArrayList().get(cc).getChildQSetName());
                        System.out.println("--child getParentId: " + parentSection.getChildSectionArrayList().get(cc).getParentId());
                        System.out.println("--getChildSectionQuestArrayList().size(): " + parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size());
                        addChildSection(syncStatus, parentSection.getChildSectionArrayList().get(cc).getChildSecId(), parentSection.getChildSectionArrayList().get(cc).getChildQSetId(),
                                parentSection.getChildSectionArrayList().get(cc).getChildQSetName(), parentSection.getChildSectionArrayList().get(cc).getParentId(),
                                parentSection.getChildSectionArrayList().get(cc).getChildSeqNo(), identifier, siteID);
                        if (parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size() > 0) {
                            for (int qq = 0; qq < parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size(); qq++) {
                                System.out.println("--------getQuestionid: " + parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());
                                System.out.println("--------getQuestionname: " + parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionname());
                                System.out.println("--------getQsetID: " + parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQsetID());
                                System.out.println("--------question getParentId: " + parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                                for (int qq1 = 0; qq1 < childSectionQuestArrayList1.size(); qq1++) {
                                    //System.out.println("1Compare Values: "+childSectionQuestArrayList1.get(qq1).getQuestionid()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());
                                    //System.out.println("2Compare Values: "+childSectionQuestArrayList1.get(qq1).getQuestAnsTransId()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestAnsTransId());
                                    // System.out.println("3Compare Values: "+childSectionQuestArrayList1.get(qq1).getParentId()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                                    //System.out.println("4Compare Values: "+childSectionQuestArrayList1.get(qq1).getJobneedid()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getJobneedid());
                                    if (childSectionQuestArrayList1.get(qq1).getQuestionid() == parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid() &&
                                            childSectionQuestArrayList1.get(qq1).getQsetID() == parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQsetID()) {
/*                                        System.out.println("--------question Answer: " + childSectionQuestArrayList1.get(qq1).getQuestAnswer());
                                        System.out.println("--------question jobneedid: " + childSectionQuestArrayList1.get(qq1).getJobneedid());*/
                                        JobNeedDetails jobNeedDetails = new JobNeedDetails();
                                        jobNeedDetails.setJndid(System.currentTimeMillis());
                                        jobNeedDetails.setSeqno(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getSeqno());
                                        jobNeedDetails.setQuestionid(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());//add qeustion id from quedstion name
                                        jobNeedDetails.setType(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getType());
                                        jobNeedDetails.setAnswer(childSectionQuestArrayList1.get(qq1).getQuestAnswer());
                                        jobNeedDetails.setOption(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getOptions());
                                        jobNeedDetails.setMin(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getMin());
                                        jobNeedDetails.setMax(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getMax());
                                        jobNeedDetails.setAlerton(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getAlerton());
                                        jobNeedDetails.setIsmandatory("");
                                        jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                                        jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                                        jobNeedDetails.setCuser(peopleID);
                                        jobNeedDetails.setJobneedid(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                                        jobNeedDetails.setMuser(peopleID);

                                        jobNeedDetailsDAO.test_insertOrUpdateRecord(jobNeedDetails);
                                        //jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed, syncStatus);
                                        /*Intent nxtActivity = new Intent(IncidentReportQuestionActivity.this, IncidentReportListActivity.class);
                                        startActivityForResult(nxtActivity, 0);*/

                                    }
                                }

                            }

                        }
                    }
                }
            }
            //---------------------------------------------------------------------------------------------
            setResult(RESULT_OK);
            finish();

            if(syncStatus == "3") {
                System.out.println("ir saved");
            }else {
                System.out.println("upload called");
                UploadIRdata();
            }


/*        UploadAttachmentLogAsyncTask uploadAttachmentLogAsyncTask=new UploadAttachmentLogAsyncTask(IncidentReportQuestionActivity.this,retId,pelogId,this);
        uploadAttachmentLogAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);*/
    }

    private void UploadIRdata() {
        /*JobNeedInsertAsynctask jobNeedInsertAsynctask=new JobNeedInsertAsynctask(IncidentReportQuestionActivity.this,this);
        jobNeedInsertAsynctask.execute();*/
        //Uploadattachment();
        UploadIRattachment();

    }

    private void UploadIRattachment() {
        Intent startIntent = new Intent(IncidentReportQuestionActivity.this, AutoSyncService.class);
        startService(startIntent);

    }


    //parentSection.getParentSecId(),parentSection.getqSetId(),parentSection.getqSetName(),parentSection.getParentId()
    private void addParentSection(String syncStatus, long pSecId, long pQSetId, String pQSetName, long pParentId, String identifier, long siteID)
    {

        System.out.println("pSecId p"+ pSecId);
        System.out.println("pQSetId p"+ pQSetId);
        System.out.println("pParentId p"+ pParentId);


        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(pSecId);
        jobNeed.setQuestionsetid(pQSetId);
        jobNeed.setJobdesc(pQSetName);
        jobNeed.setRemarks(pQSetName);
        jobNeed.setParent(pParentId);
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setCuser(peopleID);
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setMuser(peopleID);
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
        //jobNeed.setAssetid(-1);aadd asset id scan
        jobNeed.setAssetid(selectedCheckpointId);
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(peopleID);
        jobNeed.setAatop(peopleID);
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(peopleID);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(identifier,Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setSeqno(-1);
        jobNeed.setTicketcategory(-1);
        jobNeed.setBuid(siteID);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION,false))
            jobNeed.setOthersite(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,"Other Site"));
        else
            jobNeed.setOthersite("");

        //jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
        jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed, syncStatus);


        CommonFunctions.EventLog("\n ADHOC Site Audit Report Saved: \n JOBNeed Id: "+pSecId+"\n Time: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
    }
    //parentSection.getChildSectionArrayList().get(cc).getChildSecId(), parentSection.getChildSectionArrayList().get(cc).getChildQSetId(),
    //parentSection.getChildSectionArrayList().get(cc).getChildQSetName(), parentSection.getChildSectionArrayList().get(cc).getParentId(),identifier, siteID
    private void addChildSection(String syncStatus, long cSecId, long cQSetId, String cQSetName, long cParentId, int cSeqNo, String identifier, long siteID)
    {
        System.out.println("cSecId p"+ cSecId);
        System.out.println("cQSetId p"+ cQSetId);
        System.out.println("cParentId p"+ cParentId);
        System.out.println("cQSetName p"+ cQSetName);


        jobNeed = new JobNeed();
        jobNeed.setJobneedid(cSecId);
        jobNeed.setJobdesc(cQSetName);
        jobNeed.setSeqno(cSeqNo);
        jobNeed.setQuestionsetid(cQSetId);
        jobNeed.setParent(cParentId);
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(-1);//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks("");
        jobNeed.setCuser(peopleID);
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setMuser(peopleID);
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
        //jobNeed.setIsdeleted("false");
        //jobNeed.setAssetid(-1);//aadd asset id scan
        jobNeed.setAssetid(selectedCheckpointId);
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(peopleID);
        jobNeed.setAatop(peopleID);
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(peopleID);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(identifier, Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setTicketcategory(-1);
        jobNeed.setBuid(siteID);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION,false))
            jobNeed.setOthersite(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,"Other Site"));
        else
            jobNeed.setOthersite("");

        /*System.out.println("Child JOBneed id: "+jobNeed.getJobneedid());
        System.out.println("Child JOBneed qsetid: "+jobNeed.getQuestionsetid());
        System.out.println("Child JOBneed qsetname: "+jobNeed.getJobdesc());
        System.out.println("Child JOBneed Parentid: "+jobNeed.getParent());*/
        //jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
        jobNeedDAO.test_insertOrUpdateParentRecord(jobNeed, syncStatus);

    }

    private void saveSiteAuditActivity(String syncStatus, String identifier, long siteID)
    {
       /* boolean isValidData = true;
        ArrayList<JobNeedDetails>jobNeedDetailsArrayList=new ArrayList<>();
        UploadIncidentReportParameter uploadIncidentReportParameter=new UploadIncidentReportParameter();
        ArrayList<QuestionSetLevel_One>questionSetLevelOneArrayList=new ArrayList<>();
        ArrayList<QuestionAnswerTransaction>questionAnswerTransactionArrayListtemp=new ArrayList<>();
        QuestionSetLevel_One questionSetLevelOne=null;
        ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();
        QuestionSetLevel_Two questionSetLevelTwo=null;*/

        //insert data into database
        /*for(QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData())
        {
            if(questionAnswerTransaction.getQuestAnsTransId()!=-1) {
                JobNeedDetails jobNeedDetails = new JobNeedDetails();
                jobNeedDetails.setJndid(System.currentTimeMillis());
                jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
                jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
                jobNeedDetails.setType(questionAnswerTransaction.getType());
                jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());

                jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
                jobNeedDetails.setMin(questionAnswerTransaction.getMin());
                jobNeedDetails.setMax(questionAnswerTransaction.getMax());
                jobNeedDetails.setAlerton("");
                jobNeedDetails.setIsmandatory("");
                jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                jobNeedDetails.setCuser(peopleID);
                jobNeedDetails.setJobneedid(questionAnswerTransaction.getParentId());
                System.out.println("SiteAudit Ans: "+questionAnswerTransaction.getQuestAnswer());
                System.out.println("SiteAudit jobneedid: "+questionAnswerTransaction.getParentId());
                //jobNeedDetails.setJobneedid(fromID);
                jobNeedDetails.setMuser(peopleID);


                jobNeedDetailsDAO.insertOrUpdateRecord(jobNeedDetails);
            }
        }*/

        //----------------------------------------------------------------------------

        ArrayList<ChildSectionQuest> childSectionQuestArrayList1=new ArrayList<>();
        if(incidentReportQuesAdapter!=null && incidentReportQuesAdapter.getData().size()>0) {
            for (QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData()) {
                if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                    ChildSectionQuest childSectionQuest=new ChildSectionQuest();
                    childSectionQuest.setQuestionname(questionAnswerTransaction.getQuestionname());
                    childSectionQuest.setQuestionid(questionAnswerTransaction.getQuestionid());
                    childSectionQuest.setQuestAnswer(questionAnswerTransaction.getQuestAnswer());
                    childSectionQuest.setQsetID(questionAnswerTransaction.getQsetID());
                    childSectionQuest.setAlerton(questionAnswerTransaction.getAlerton());
                    childSectionQuestArrayList1.add(childSectionQuest);
                }
            }
        }


        System.out.println("parentSection.getParentSecId(): "+parentSection.getParentSecId());
        System.out.println("parentSection.getParentId(): "+parentSection.getParentId());
        System.out.println("parentSection.getqSetId(): "+parentSection.getqSetId());
        System.out.println("parentSection.getqSetName()"+parentSection.getqSetName());
        if(parentSection.getChildSectionArrayList()!=null && parentSection.getChildSectionArrayList().size()>0)
        {
            System.out.println("parentSection.getChildSectionArrayList().size(): "+parentSection.getChildSectionArrayList().size());
            addParentSection(syncStatus, parentSection.getParentSecId(),parentSection.getqSetId(),parentSection.getqSetName(),parentSection.getParentId(), identifier, siteID );

            if(parentSection.getChildSectionArrayList().size()>0)
            {
                for(int cc=0;cc<parentSection.getChildSectionArrayList().size();cc++)
                {
                    System.out.println("--getChildSecId: "+parentSection.getChildSectionArrayList().get(cc).getChildSecId());
                    System.out.println("--getChildQSetId: "+parentSection.getChildSectionArrayList().get(cc).getChildQSetId());
                    System.out.println("--getChildQSetName: "+parentSection.getChildSectionArrayList().get(cc).getChildQSetName());
                    System.out.println("--child getParentId: "+parentSection.getChildSectionArrayList().get(cc).getParentId());
                    System.out.println("--getChildSectionQuestArrayList().size(): "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size());
                    addChildSection(syncStatus, parentSection.getChildSectionArrayList().get(cc).getChildSecId(),parentSection.getChildSectionArrayList().get(cc).getChildQSetId(),
                                            parentSection.getChildSectionArrayList().get(cc).getChildQSetName(), parentSection.getChildSectionArrayList().get(cc).getParentId(),
                                            parentSection.getChildSectionArrayList().get(cc).getChildSeqNo(),identifier, siteID  );
                    if(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size()>0)
                    {
                        for(int qq=0;qq<parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().size();qq++)
                        {
                            System.out.println("--------getQuestionid: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());
                            System.out.println("--------getQuestionname: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionname());
                            System.out.println("--------getQsetID: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQsetID());
                            System.out.println("--------question getParentId: "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                            for(int qq1=0;qq1<childSectionQuestArrayList1.size();qq1++)
                            {
                                //System.out.println("1Compare Values: "+childSectionQuestArrayList1.get(qq1).getQuestionid()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());
                                //System.out.println("2Compare Values: "+childSectionQuestArrayList1.get(qq1).getQuestAnsTransId()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestAnsTransId());
                               // System.out.println("3Compare Values: "+childSectionQuestArrayList1.get(qq1).getParentId()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                                //System.out.println("4Compare Values: "+childSectionQuestArrayList1.get(qq1).getJobneedid()+" : "+parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getJobneedid());
                                if(childSectionQuestArrayList1.get(qq1).getQuestionid()==parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid() &&
                                        childSectionQuestArrayList1.get(qq1).getQsetID()==parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQsetID()) {
                                    System.out.println("--------question Answer: " + childSectionQuestArrayList1.get(qq1).getQuestAnswer());
                                    System.out.println("--------question jobneedid: " + childSectionQuestArrayList1.get(qq1).getJobneedid());
                                    JobNeedDetails jobNeedDetails = new JobNeedDetails();
                                    jobNeedDetails.setJndid(System.currentTimeMillis());
                                    jobNeedDetails.setSeqno(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getSeqno());
                                    jobNeedDetails.setQuestionid(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getQuestionid());//add qeustion id from quedstion name
                                    jobNeedDetails.setType(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getType());
                                    jobNeedDetails.setAnswer(childSectionQuestArrayList1.get(qq1).getQuestAnswer());

                                    jobNeedDetails.setOption(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getOptions());
                                    jobNeedDetails.setMin(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getMin());
                                    jobNeedDetails.setMax(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getMax());
                                    jobNeedDetails.setAlerton(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getAlerton());
                                    jobNeedDetails.setIsmandatory("");
                                    jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                                    jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                                    jobNeedDetails.setCuser(peopleID);
                                    jobNeedDetails.setJobneedid(parentSection.getChildSectionArrayList().get(cc).getChildSectionQuestArrayList().get(qq).getParentId());
                                    jobNeedDetails.setMuser(peopleID);

                                    jobNeedDetailsDAO.test_insertOrUpdateRecord(jobNeedDetails);
                                }
                            }

                        }

                    }
                }
            }
        }



        //---------------------------------------------------------------------------------------------


        /*System.out.println("Jobneed details count: "+jobNeedDetailsDAO.getCount(fromID));

        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(fromID);
        jobNeed.setJobdesc(fromQuestionSetName);

        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks(fromQuestionSetName);
        jobNeed.setCuser(peopleID);
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
        jobNeed.setMuser(peopleID);
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
        //jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(-1);//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(peopleID);
        jobNeed.setAatop(peopleID);
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(peopleID);
        jobNeed.setQuestionsetid(fromQuestionSetID);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(identifier,Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(-1);
        jobNeed.setSeqno(-1);
        jobNeed.setTicketcategory(-1);
        jobNeed.setBuid(siteID);
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION,false))
            jobNeed.setOthersite(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,"Other Site"));
        else
            jobNeed.setOthersite("");

        System.out.println("Parent JOBneedid: "+fromID);
        System.out.println("Parent JOBneedquestsetname: "+fromQuestionSetName);
        System.out.println("Parent JOBneedParentid: -1");
        jobNeedDAO.insertOrUpdateParentRecord(jobNeed,syncStatus);

        for(int qss=0;qss<questionSubSetArrayList.size();qss++)
        {
            if(fromQuestionSetID==questionSubSetArrayList.get(qss).getParentId()) {
                jobNeed = new JobNeed();
                jobNeed.setJobneedid(System.currentTimeMillis());//fromid
                jobNeed.setJobdesc(questionSubSetArrayList.get(qss).getQuestionsetName());
                jobNeed.setSeqno(questionSubSetArrayList.get(qss).getSeqno());

                jobNeed.setFrequency(-1);
                jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
                jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(currentTimestamp));
                jobNeed.setGracetime(0);
                jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
                jobNeed.setJobstatus(-1);//need to get id form type assist for Assigned status
                jobNeed.setScantype(-1);
                jobNeed.setReceivedonserver("");
                jobNeed.setPriority(-1);
                jobNeed.setStarttime("");
                jobNeed.setEndtime("");
                jobNeed.setGpslocation(gpsLocation);
                jobNeed.setRemarks("");
                jobNeed.setCuser(peopleID);
                jobNeed.setCdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
                jobNeed.setMuser(peopleID);
                jobNeed.setMdtz(CommonFunctions.getTimezoneDate(currentTimestamp));
                jobNeed.setAssetid(-1);//aadd asset id scan
                jobNeed.setGroupid(-1);
                jobNeed.setPeopleid(peopleID);
                jobNeed.setAatop(peopleID);
                jobNeed.setJobid(-1);
                jobNeed.setPerformedby(peopleID);
                jobNeed.setQuestionsetid(questionSubSetArrayList.get(qss).getQsetID());
                jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(identifier, Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
                jobNeed.setParent(fromID);
                jobNeed.setTicketcategory(-1);
                jobNeed.setBuid(siteID);
                jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
                if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION,false))
                    jobNeed.setOthersite(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,"Other Site"));
                else
                    jobNeed.setOthersite("");

                System.out.println("Child JOBneedid: "+jobNeed.getJobneedid());
                System.out.println("Child JOBneed qsetid: "+jobNeed.getQuestionsetid());
                System.out.println("Child JOBneed qsetname: "+questionSubSetArrayList.get(qss).getQuestionsetName());
                System.out.println("Child JOBneedParentid: "+jobNeed.getParent());
                jobNeedDAO.insertOrUpdateRecord(jobNeed,syncStatus);

                if(incidentReportQuesAdapter!=null && incidentReportQuesAdapter.getData().size()>0) {
                    for (QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData()) {
                        if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                            if (questionSubSetArrayList.get(qss).getTimestamp() == questionAnswerTransaction.getJobneedid()) {
                                JobNeedDetails jobNeedDetails = new JobNeedDetails();
                                jobNeedDetails.setJndid(System.currentTimeMillis());
                                jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
                                jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
                                jobNeedDetails.setType(questionAnswerTransaction.getType());
                                jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());

                                jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
                                jobNeedDetails.setMin(questionAnswerTransaction.getMin());
                                jobNeedDetails.setMax(questionAnswerTransaction.getMax());
                                jobNeedDetails.setAlerton("");
                                jobNeedDetails.setIsmandatory("");
                                jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                                jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                                jobNeedDetails.setCuser(peopleID);
                                System.out.println("SiteAudit Ans: " + jobNeedDetails.getAnswer());
                                System.out.println("SiteAudit jobneedid: " + jobNeedDetails.getJobneedid());
                                jobNeedDetails.setJobneedid(jobNeed.getJobneedid());
                                jobNeedDetails.setMuser(peopleID);

                                jobNeedDetailsDAO.insertOrUpdateRecord(jobNeedDetails);
                            }

                        }
                    }
                }
            }
        }


        jobNeedDAO.getJobNeedCount(fromID);*/

        if(fromActivity.equalsIgnoreCase("SITEREPORT"))
        {
            //long peopleid, long siteid, String sitename, long chkintime, long qsetid, String qsetname
            SiteTemplateDAO siteTemplateDAO=new SiteTemplateDAO(IncidentReportQuestionActivity.this);
            siteTemplateDAO.insertRecord(siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP,-1),peopleID, siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1),siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""),siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP,-1),siteAuditPref.getLong(Constants.SITE_AUDIT_QUESTIONSETID,-1),"QsetNaeme");

            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, true).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_QUESTIONSETID,-1).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP,-1).apply();

        }

        setResult(RESULT_OK);
        finish();
    }

    private void saveTOURActivity()
    {
        System.out.println("save tour");
        boolean isValidData = true;
        if(jobNeedDetailsQuesAdapter!=null && jobNeedDetailsQuesAdapter.getData().size()>0) {
            for (QuestionAnswerTransaction quest : jobNeedDetailsQuesAdapter.getData()) {
                jobNeedDetailsDAO.changeQuestionAns(quest.getJndid(), quest.getQuestAnswer(), quest.getQuestionid());
                jobNeedDAO.changeJobNeedSyncStatus(quest.getJobneedid(), Constants.SYNC_STATUS_TWO);

                System.out.println("Ans: " + quest.getQuestAnswer().trim());
                if (quest.getQuestAnswer().trim().length() > 0)
                    System.out.println("Done");
                else {
                    System.out.println("Failed");
                    //isValidData = false;
                    break;
                }
            }


            if (isValidData) {
                //String jndid= tourChildPref.getString(Constants.TOUR_CHILD_PARENT_JOBNEEDID,)
                //child tour mark as completed
                //jobNeedDAO.changeJobStatus(fromID,typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));
                gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");
                CommonFunctions.EventLog("\n Tour Child Completed: \n JOBNeed Id: "+fromID+"\n Time: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"\n");
                jobNeedDAO.updateChildTourCompleted(fromID, tourChildPref.getString(Constants.TOUR_CHILD_STARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis())), CommonFunctions.getTimezoneDate(System.currentTimeMillis()), peopleID, typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED),gpsLocation, jobNeed.getParent());
                /*tourChildPref.edit().putString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())).apply();
                jobNeedDAO.updateParentTourPartiallyCompleted(tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID,-1),tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())), loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1),typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_PARTIALLY_COMPLETED, Constants.STATUS_TYPE_JOBNEED),fromID );
                jobNeedDAO.changeJobNeedSyncStatus(tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID,-1),Constants.SYNC_STATUS_TWO);*/

                Long pjobneed =tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID,-1);

                int maxTour=jobNeedDAO.getChildCount(pjobneed);
                int completedTour=jobNeedDAO.getCompletedChildCount(pjobneed);
                if(maxTour==completedTour)
                {
                    System.out.println("Completed: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" --- jobneedid: "+pjobneed);
                    jobNeedDAO.updateParentTourCompleted(pjobneed,tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())),CommonFunctions.getTimezoneDate(System.currentTimeMillis()),loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1) ,typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));
                    jobNeedDAO.changeJobNeedSyncStatus(pjobneed,Constants.SYNC_STATUS_TWO);
                }
                else if(completedTour==1 || completedTour >=1)
                {
                    System.out.println("PartiallyCompleted: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" --- jobneedid: "+pjobneed);
                    tourChildPref.edit().putString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())).apply();
                    jobNeedDAO.updateParentTourPartiallyCompleted(pjobneed,tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())), loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1),typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_PARTIALLY_COMPLETED, Constants.STATUS_TYPE_JOBNEED), tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,0) );
                    jobNeedDAO.changeJobNeedSyncStatus(pjobneed,Constants.SYNC_STATUS_TWO);
                }
            }
        }

        setResult(RESULT_OK);
        finish();

        UploadJobneeddata();

    }

    private void UploadJobneeddata() {
        System.out.println("Upload Jobneed started==");
        JobneedUpdateAsyntask jobneedUpdateAsyntask=new JobneedUpdateAsyntask(IncidentReportQuestionActivity.this, (IUploadJobneedUpdateDataListener) this);
        jobneedUpdateAsyntask.execute();
        Uploadattachment();

    }
    private void Uploadattachment(){
        Intent startIntent = new Intent(IncidentReportQuestionActivity.this, UploadImageService.class);
        startService(startIntent);
    }


    private void saveCHKPOINTActivity(String desc, long identifier )
    {
        boolean isValidData = true;

        if(isValidData)
        {
            //jobdesc, frequency, plandatetime, expirydatetime, gracetime, jobtype, jobstatus,  scantype, receivedonserver, priority,starttime, endtime, gpslocation,
            // remarks, cuser,  cdtz, muser,mdtz, isdeleted, assetcode, aatog, aatop, jobcode, performedby,  qsetcode

            JobNeed jobNeed=new JobNeed();
            jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
            jobNeed.setJobdesc(desc);
            jobNeed.setFrequency(-1);
            jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setGracetime(0);
            jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
            jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
            jobNeed.setScantype(typeAssistDAO.getEventTypeID(adhocJobPef.getString(Constants.ADHOC_TYPE,Constants.SCAN_TYPE_QR), Constants.IDENTIFIER_SCANTYPE));
            jobNeed.setReceivedonserver("");
            jobNeed.setPriority(typeAssistDAO.getEventTypeID("LOW", Constants.IDENTIFIER_PRIORITY));
            jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setEndtime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setGpslocation(gpsLocation);
            jobNeed.setRemarks("");
            jobNeed.setCuser(peopleID);
            jobNeed.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setMuser(peopleID);
            jobNeed.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            //jobNeed.setIsdeleted("false");
            jobNeed.setAssetid(assetDAO.getAssetID(getIntent().getStringExtra("ASSETCODE")));//aadd asset id scan
            jobNeed.setGroupid(-1);
            jobNeed.setPeopleid(peopleID);
            jobNeed.setAatop(peopleID);
            jobNeed.setJobid(-1);
            jobNeed.setPerformedby(peopleID);
            jobNeed.setQuestionsetid(fromID);
            jobNeed.setIdentifier(identifier);//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
            jobNeed.setParent(-1);
            jobNeed.setTicketcategory(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_AUTOCLOSED, "Ticket Category"));
            jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
            jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
            jobNeedDAO.insertRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
            CommonFunctions.EventLog("\n ADHOC Checkpoint Done: \n JOBNeed Id: "+jobNeed.getJobneedid()+"\n Time: "+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n");

            if(checkPointQuestAdapter!=null && checkPointQuestAdapter.getData().size()>0) {
                //jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
                for (QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData()) {
                    JobNeedDetails jobNeedDetails = new JobNeedDetails();
                    jobNeedDetails.setJndid(System.currentTimeMillis());
                    jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
                    jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
                    jobNeedDetails.setType(questionAnswerTransaction.getType());
                    jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
                    jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
                    jobNeedDetails.setMin(questionAnswerTransaction.getMin());
                    jobNeedDetails.setMax(questionAnswerTransaction.getMax());
                    jobNeedDetails.setAlerton(questionAnswerTransaction.getAlerton());
                    jobNeedDetails.setIsmandatory(questionAnswerTransaction.getIsmandatory());
                    jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setCuser(peopleID);
                    jobNeedDetails.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    jobNeedDetails.setMuser(peopleID);


                    jobNeedDetailsDAO.insertRecord(jobNeedDetails);
                }
            }

            setResult(RESULT_OK);
            finish();
        }
        else
        {
            Toast.makeText(IncidentReportQuestionActivity.this,getResources().getString(R.string.fill_mandetory_error_msg), Toast.LENGTH_LONG).show();
        }
        UploadIRdata();
    }

    private void saveREQUESTActivity(String desc, long identifier )
    {
        boolean isValidData = true;

        if(isValidData)
        {
            //jobdesc, frequency, plandatetime, expirydatetime, gracetime, jobtype, jobstatus,  scantype, receivedonserver, priority,starttime, endtime, gpslocation,
            // remarks, cuser,  cdtz, muser,mdtz, isdeleted, assetcode, aatog, aatop, jobcode, performedby,  qsetcode

            gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

            JobNeed jobNeed=new JobNeed();
            jobNeed.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()));
            jobNeed.setJobdesc(desc);
            jobNeed.setFrequency(-1);
            jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setGracetime(0);
            jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
            jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_ASSIGNED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
            jobNeed.setScantype(-1);
            jobNeed.setReceivedonserver("");
            jobNeed.setPriority(typeAssistDAO.getEventTypeID("LOW", Constants.IDENTIFIER_PRIORITY));
            jobNeed.setStarttime(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setEndtime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
            jobNeed.setGpslocation(gpsLocation);
            jobNeed.setRemarks(adhocJobPef.getString(Constants.ADHOC_ASSET,""));
            jobNeed.setCuser(peopleID);
            jobNeed.setCdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            jobNeed.setMuser(peopleID);
            jobNeed.setMdtz(CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis())));
            //jobNeed.setIsdeleted("false");
            jobNeed.setAssetid(-1);//aadd asset id scan
            jobNeed.setGroupid(-1);
            jobNeed.setPeopleid(peopleID);
            jobNeed.setAatop(peopleID);
            jobNeed.setJobid(-1);
            jobNeed.setPerformedby(peopleID);
            jobNeed.setQuestionsetid(fromQuestionSetID);
            jobNeed.setIdentifier(identifier);//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
            jobNeed.setParent(-1);
            jobNeed.setTicketcategory(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_AUTOCLOSED, "Ticket Category"));
            jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
            jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
            jobNeedDAO.insertRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
            CommonFunctions.EventLog("\n ADHOC Request Done: \n JOBNeed Id: "+jobNeed.getJobneedid()+"\n Time: "+CommonFunctions.getTimezoneDate(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1))+"\n");

            if(checkPointQuestAdapter!=null && checkPointQuestAdapter.getData().size()>0) {
                //jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
                for (QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData()) {
                    JobNeedDetails jobNeedDetails = new JobNeedDetails();
                    jobNeedDetails.setJndid(System.currentTimeMillis());
                    jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
                    jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
                    jobNeedDetails.setType(questionAnswerTransaction.getType());
                    jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
                    jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
                    jobNeedDetails.setMin(questionAnswerTransaction.getMin());
                    jobNeedDetails.setMax(questionAnswerTransaction.getMax());
                    jobNeedDetails.setAlerton(questionAnswerTransaction.getAlerton());
                    jobNeedDetails.setIsmandatory(questionAnswerTransaction.getIsmandatory());
                    jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setCuser(peopleID);
                    jobNeedDetails.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
                    jobNeedDetails.setMuser(peopleID);


                    jobNeedDetailsDAO.insertRecord(jobNeedDetails);
                }
            }

            setResult(RESULT_OK);
            finish();
        }
        else
        {
            Toast.makeText(IncidentReportQuestionActivity.this,getResources().getString(R.string.fill_mandetory_error_msg), Toast.LENGTH_LONG).show();
        }
    }

    private void saveADHOCActivity()
    {
        boolean isValidData = true;


        if(isValidData)
        {

            CommonFunctions.ReadingLog("\n"+"ADHOC Reading Taken"+"\n"+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"\n"+"JOB Need ID: "+fromID+"\n");
            //jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
            if(checkPointQuestAdapter!=null && checkPointQuestAdapter.getData().size()>0) {

                System.out.println("checkPointQuestAdapter size: " + checkPointQuestAdapter.getData().size());
                for (QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData()) {
                    JobNeedDetails jobNeedDetails = new JobNeedDetails();
                    jobNeedDetails.setJndid(System.currentTimeMillis());
                    jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
                    jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
                    jobNeedDetails.setType(questionAnswerTransaction.getType());
                    jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
                    jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
                    jobNeedDetails.setMin(questionAnswerTransaction.getMin());
                    jobNeedDetails.setMax(questionAnswerTransaction.getMax());
                    jobNeedDetails.setAlerton(questionAnswerTransaction.getAlerton());
                    jobNeedDetails.setIsmandatory(questionAnswerTransaction.getIsmandatory());
                    jobNeedDetails.setAlerton(questionAnswerTransaction.getAlerton());
                    jobNeedDetails.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    jobNeedDetails.setCuser(peopleID);
                    jobNeedDetails.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP, -1));
                    jobNeedDetails.setMuser(peopleID);

                    CommonFunctions.ReadingLog("\n"+"Reading: "+"\n"+jobNeedDetails.getJobneedid()+" : "+jobNeedDetails.getJndid()+" : "+jobNeedDetails.getAnswer()+" : "+jobNeedDetails.getQuestionid()+"\n");

                    CommonFunctions.manualSyncEventLog("ADHOC_READING","\n"+"Reading: "+"\n"+jobNeedDetails.getJobneedid()+" : "+jobNeedDetails.getJndid()+" : "+jobNeedDetails.getAnswer()+" : "+jobNeedDetails.getQuestionid()+"\n",CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

                    jobNeedDetailsDAO.insertRecord(jobNeedDetails);
                }
            }

            setResult(RESULT_OK);
            finish();
        }
        else
        {
            Toast.makeText(IncidentReportQuestionActivity.this,getResources().getString(R.string.fill_mandetory_error_msg), Toast.LENGTH_LONG).show();
        }
    }



    private void saveScheduleSiteReportActivity()
    {
        boolean isValidData = true;
        if(incidentReportQuesAdapter!=null && incidentReportQuesAdapter.getData().size()>0) {
            for (QuestionAnswerTransaction quest : incidentReportQuesAdapter.getData()) {
                if (quest.getQuestAnsTransId() != -1) {

                    jobNeedDetailsDAO.changeQuestionAns(quest.getJndid(), quest.getQuestAnswer(), quest.getQuestionid());

                    System.out.println("Ans: " + quest.getQuestAnswer().trim());

                }

            }

            if (isValidData) {

                if (jobNeedDAO.getChildCount(fromID) > 0) {
                    for (JobNeed jobNeed : jobNeedDAO.getChildCheckPointList(fromID)) {
                        System.out.println("Updated child JOB Need id: " + jobNeed.getJobneedid());
                        jobNeedDAO.changeJobNeedSyncStatus(jobNeed.getJobneedid(), Constants.SYNC_STATUS_TWO);
                    }
                }

                CommonFunctions.EventLog("\n Site Report Done: \n JOBNeed Id: "+fromID+"\n Time: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                System.out.println("Updated parent JOB Need id: " + fromID);
                jobNeedDAO.changeJobNeedSyncStatus(fromID, Constants.SYNC_STATUS_TWO);
                //jobNeedDAO.changeJobStatus(fromID,typeAssistDAO.getEventTypeID("COMPLETED", Constants.STATUS_TYPE_JOBNEED));
                jobNeedDAO.updateJobNeedRecord(fromID, jobNeed.getPeopleid(), jobNeed.getGroupid(), "Site Report Done", typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED), peopleID);

            }
        }

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.alerttitle),getResources().getString(R.string.doyouwanttoexit),"",0);
        /*setResult(RESULT_CANCELED);
        finish();*/
    }

    private Toast mToastToShow;
    public void showToast(String ss) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 10;
        mToastToShow = Toast.makeText(IncidentReportQuestionActivity.this, ss, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 10 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }
            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        mToastToShow.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
        View view1=mToastToShow.getView();
        view1.setBackgroundColor(getResources().getColor(R.color.button_background));
        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();

    }

    private boolean isDataValid(int val)
    {
        if(val==ADHOC_VALIDATION)
        {
            if(checkPointQuestAdapter!=null && checkPointQuestAdapter.getData().size()>0) {
                for (QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData()) {
                    if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                        if (questionAnswerTransaction.getIsmandatory().equalsIgnoreCase("True") || questionAnswerTransaction.getIsmandatory().equalsIgnoreCase("false")) {

                            if (!questionAnswerTransaction.isCorrect()) {
                                questionErrorMsg="Please enter valid input for "+questionAnswerTransaction.getQuestionname()+"!";
                                return false;
                            }
                        }
                    }
                }
            }
        }
        else if(val==SURVEY_VALIDATION) {
            if(incidentReportQuesAdapter!=null && incidentReportQuesAdapter.getData().size()>0) {
                for (QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData()) {
                    if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                        if (questionAnswerTransaction.getIsmandatory().equalsIgnoreCase("True") || questionAnswerTransaction.getIsmandatory().equalsIgnoreCase("false")) {
                            if (!questionAnswerTransaction.isCorrect()) {
                                questionErrorMsg="Please enter valid input for "+questionAnswerTransaction.getQuestionname()+"!";
                                return false;
                            }
                        }
                    }
                }
            }
        }
        else if(val==READING_VALIDATION)
        {
            if(jobNeedDetailsQuesAdapter!=null && jobNeedDetailsQuesAdapter.getData().size()>0) {
                for (QuestionAnswerTransaction quest : jobNeedDetailsQuesAdapter.getData()) {
                    if (quest.getQuestionid() != -1) {
                        if (quest.getIsmandatory().equalsIgnoreCase("True") || quest.getIsmandatory().equalsIgnoreCase("false")) {
                            if (!quest.isCorrect()) {
                                questionErrorMsg="Please enter valid input for "+quest.getQuestionname()+"!";
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isMandatoryFilled(int val)
    {
        if(val==SURVEY_VALIDATION) {
            if(incidentReportQuesAdapter!=null && incidentReportQuesAdapter.getData().size()>0) {
                for (QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData()) {
                    if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                        if (questionAnswerTransaction.getIsmandatory().equalsIgnoreCase("True")) {
                            if (questionAnswerTransaction.getQuestAnswer().trim().length() == 0)
                                return false;
                        }
                    }
                }
            }
        }
        else if(val==ADHOC_VALIDATION)
        {
            if(checkPointQuestAdapter!=null && checkPointQuestAdapter.getData().size()>0) {
                for (QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData()) {
                    if (questionAnswerTransaction.getQuestAnsTransId() != -1) {
                        if (questionAnswerTransaction.getIsmandatory().equalsIgnoreCase("True")) {

                            if (questionAnswerTransaction.getQuestAnswer().trim().length() == 0)
                                return false;
                        }
                    }
                }
            }
        }
        else if(val==READING_VALIDATION)
        {
            if(jobNeedDetailsQuesAdapter!=null && jobNeedDetailsQuesAdapter.getData().size()>0) {
                for (QuestionAnswerTransaction quest : jobNeedDetailsQuesAdapter.getData()) {
                    if (quest.getQuestionid() != -1) {
                        if (quest.getIsmandatory().equalsIgnoreCase("True")) {

                            if (quest.getQuestAnswer().trim().length() == 0 || quest.getQuestAnswer() == null || quest.getQuestAnswer().trim().equalsIgnoreCase("null"))
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(IncidentReportQuestionActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0 ) {
            switch (v.getId()) {
                case R.id.submitButton:
                    if (CommonFunctions.isPermissionGranted(IncidentReportQuestionActivity.this)) {
                        submitButton.setEnabled(false);
                        if (fromActivity.equalsIgnoreCase("JOB")) {
                            if (isMandatoryFilled(READING_VALIDATION)) {
                                if (isDataValid(READING_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveJOBNeedActivity();
                                        }
                                    });
                                } else {
                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }

                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        } else if (fromActivity.equalsIgnoreCase("INCIDENTREPORT") || fromActivity.equalsIgnoreCase("INCIDENTREPORT_LIST")) {
                            if (isMandatoryFilled(SURVEY_VALIDATION)) {
                                if (isDataValid(SURVEY_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveIRActivity(Constants.SYNC_STATUS_ZERO, Constants.JOB_NEED_IDENTIFIER_INCIDENT, loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1));
                                        }
                                    });
                                } else {
                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }

                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        } else if (fromActivity.equalsIgnoreCase("SITEREPORT")) {

                                if (isMandatoryFilled(SURVEY_VALIDATION)) {
                                    if (isDataValid(SURVEY_VALIDATION)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                saveSiteAuditActivity(Constants.SYNC_STATUS_ZERO, Constants.JOB_NEED_IDENTIFIER_SITEREPORT, siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1));
                                            }
                                        });
                                    } else {
                                        //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                        Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                        submitButton.setEnabled(true);
                                    }

                                } else {
                                    //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }
                        } else if (fromActivity.equalsIgnoreCase("SITEREPORT_SCHEDULE")) {
                            if (isMandatoryFilled(SURVEY_VALIDATION)) {
                                if (isDataValid(SURVEY_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveScheduleSiteReportActivity();
                                        }
                                    });
                                } else {
                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }


                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        } else if (fromActivity.equalsIgnoreCase("TOUR")) {
                            if (isMandatoryFilled(READING_VALIDATION)) {
                                if (isDataValid(READING_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveTOURActivity();
                                        }
                                    });
                                } else {
                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }
                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        } else if (fromActivity.equalsIgnoreCase("CHECKPOINT")) {
                            if (isMandatoryFilled(ADHOC_VALIDATION)) {
                                if (isDataValid(ADHOC_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveCHKPOINTActivity(Constants.JOB_TYPE_ADHOC + " " + assetDAO.getAssetName(getIntent().getStringExtra("ASSETCODE")),
                                                    typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_TOUR));
                                        }
                                    });
                                } else {
                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }
                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        } else if (fromActivity.equalsIgnoreCase("REQUEST")) {
                            if (isMandatoryFilled(ADHOC_VALIDATION)) {
                                if (isDataValid(ADHOC_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveREQUESTActivity(adhocJobPef.getString(Constants.ADHOC_QSET_NAME, ""),
                                                    typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_INTERNAL_REQUEST));
                                        }
                                    });
                                }
                            }
                        } else if (fromActivity.equalsIgnoreCase("ASSET")) {
                            if (isMandatoryFilled(ADHOC_VALIDATION)) {
                                if (isDataValid(ADHOC_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveCHKPOINTActivity(assetDAO.getAssetName(getIntent().getStringExtra("ASSETCODE")),
                                                    typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_ASSET));
                                        }
                                    });
                                } else {
                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }
                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        } else if (fromActivity.equalsIgnoreCase("ADHOC") || fromActivity.equalsIgnoreCase("ADHOC_SCAN")) {
                            if (isMandatoryFilled(ADHOC_VALIDATION)) {
                                if (isDataValid(ADHOC_VALIDATION)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveADHOCActivity();
                                        }
                                    });
                                } else {

                                    //Snackbar.make(submitButton, "Please fill valid data", Snackbar.LENGTH_LONG).show();
                                    Toasty.error(IncidentReportQuestionActivity.this, questionErrorMsg, Toast.LENGTH_LONG, true).show();
                                    submitButton.setEnabled(true);
                                }

                            } else {
                                //Snackbar.make(submitButton, getResources().getString(R.string.fill_mandetoryfield_error_msg), Snackbar.LENGTH_LONG).show();
                                Toasty.error(IncidentReportQuestionActivity.this, getResources().getString(R.string.fill_mandetoryfield_error_msg), Toast.LENGTH_LONG, true).show();
                                submitButton.setEnabled(true);
                            }
                        }
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

                    break;
                case R.id.saveButton:
                    if (CommonFunctions.isPermissionGranted(IncidentReportQuestionActivity.this)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveIRActivity(Constants.SYNC_STATUS_THREE, Constants.JOB_NEED_IDENTIFIER_INCIDENT, loginDetailPref.getLong(Constants.LOGIN_SITE_ID, -1));
                            }
                        });
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

                    break;
            }
        }
        else if (accessValue == 1) {
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
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==0)
        {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void finishAllJobNeedInsertUpload() {

    }

    @Override
    public void finishJobNeedInsertUpload(int status) {

    }

    @Override
    public void finishAllJobneedUpdateUpload() {

    }

    @Override
    public void finishJobneedUpdateUpload(int status) {

    }
}

//3 level incident report question set from database
/*
else if(fromActivity.equalsIgnoreCase("INCIDENTREPORT"))
        {
        questionSetArrayList=new ArrayList<>();
        questionSubSetArrayList=new ArrayList<>();
        questionSetArrayList=questionDAO.getQuestionSetCodeList();
        if(questionSetArrayList!=null && questionSetArrayList.size()>0)
        {
        for(int i=0;i<questionSetArrayList.size();i++) {
        System.out.println("QuestionSetName: "+i+" : "+questionSetArrayList.get(i).getQsetname());
        System.out.println("-------------------------------------------------------------");

        QuestionAnswerTransaction questionAnswerTransaction1=new QuestionAnswerTransaction();
        questionAnswerTransaction1.setQsetID(questionSetArrayList.get(i).getQuestionsetid());
        questionAnswerTransaction1.setQuestionsetName(questionSetArrayList.get(i).getQsetname());
        questionAnswerTransaction1.setParentId(-1);
        questionAnswerTransaction1.setQuestAnsTransId(-1);

        irQuestionArrayList.add(questionAnswerTransaction1);
        subsetQuestionset=new ArrayList<>();
        subsetQuestionset= questionDAO.getQuestionSubSetCodeList(questionSetArrayList.get(i).getQuestionsetid());
        if(subsetQuestionset!=null && subsetQuestionset.size()>0)
        {
        for(int k=0;k<subsetQuestionset.size();k++)
        {
        System.out.println("Question Sub SetName: "+k+" : "+subsetQuestionset.get(k).getQsetname());
        System.out.println("-------------------------------------------------------------");

        QuestionAnswerTransaction questionAnswerTransaction2=new QuestionAnswerTransaction();
        questionAnswerTransaction2.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
        questionAnswerTransaction2.setQuestionsetName(subsetQuestionset.get(k).getQsetname());
        questionAnswerTransaction2.setParentId(questionSetArrayList.get(i).getQuestionsetid());
        questionAnswerTransaction2.setQuestAnsTransId(-1);
        irQuestionArrayList.add(questionAnswerTransaction2);
        questionSubSetArrayList.add(questionAnswerTransaction2);

        ArrayList<Question> questionArrayList= questionDAO.getQuestions(subsetQuestionset.get(k).getQuestionsetid());
        if(questionArrayList!=null && questionArrayList.size()>0)
        {
        for(int j=0;j<questionArrayList.size();j++)
        {
        System.out.println("QuestionName: "+questionArrayList.get(j).getQuestionname());

        QuestionAnswerTransaction questionAnswerTransaction=new QuestionAnswerTransaction();
        questionAnswerTransaction.setQuestionsetName("");
        questionAnswerTransaction.setQsetID(subsetQuestionset.get(k).getQuestionsetid());
        questionAnswerTransaction.setQuestAnsTransId(System.currentTimeMillis());
        questionAnswerTransaction.setQuestionid(questionArrayList.get(j).getQuestionid());
        questionAnswerTransaction.setQuestionname(questionArrayList.get(j).getQuestionname());
        questionAnswerTransaction.setMin(questionArrayList.get(j).getMin());
        questionAnswerTransaction.setMax(questionArrayList.get(j).getMax());
        questionAnswerTransaction.setType(questionArrayList.get(j).getType());
        questionAnswerTransaction.setOptions(questionArrayList.get(j).getOptions());
        questionAnswerTransaction.setUnit(questionArrayList.get(j).getUnit());
        questionAnswerTransaction.setCuser(questionArrayList.get(j).getCuser());
        questionAnswerTransaction.setCdtz(questionArrayList.get(j).getCdtz());
        questionAnswerTransaction.setMuser(questionArrayList.get(j).getMuser());
        questionAnswerTransaction.setMdtz(questionArrayList.get(j).getMdtz());
        questionAnswerTransaction.setQuestAnswer("");
        questionAnswerTransaction.setParentId(subsetQuestionset.get(k).getQuestionsetid());
        irQuestionArrayList.add(questionAnswerTransaction);
        }
        }

        }
        }




        }
        }
        }*/



//fetched from database and create json format
            /*ArrayList<JobNeed>jobNeedArrayList=jobNeedDAO.getUnsyncIRList();
            ArrayList<JobNeed>jobNeedChildArrayList;
            ArrayList<JobNeedDetails>jobNeedChildDetailsArrayList;
            if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
            {
                for(int i=0;i<jobNeedArrayList.size();i++)
                {
                    questionSetLevelOne=new QuestionSetLevel_One();
                    questionSetLevelOne.setQuestionsetid(jobNeedArrayList.get(i).getQuestionsetid());
                    jobNeedChildArrayList=new ArrayList<>();
                    jobNeedChildArrayList=jobNeedDAO.getChildCheckPointList(jobNeedArrayList.get(i).getQuestionsetid());
                    questionSetLevelTwoArrayList=new ArrayList<>();
                    if(jobNeedChildArrayList!=null && jobNeedChildArrayList.size()>0)
                    {
                        for(int j=0;j<jobNeedChildArrayList.size();j++)
                        {
                            questionSetLevelTwo = new QuestionSetLevel_Two();
                            questionSetLevelTwo.setQuestionsetid(jobNeedChildArrayList.get(j).getQuestionsetid());
                            jobNeedChildDetailsArrayList=new ArrayList<>();
                            jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getQuestionsetid());
                            questionSetLevelTwo.setDetails(jobNeedChildDetailsArrayList);
                            questionSetLevelTwoArrayList.add(questionSetLevelTwo);
                        }


                        questionSetLevelOne.setSubChild(questionSetLevelTwoArrayList);
                    }
                    questionSetLevelOneArrayList.add(questionSetLevelOne);
                }


            }

            uploadIncidentReportParameter=new UploadIncidentReportParameter();
            uploadIncidentReportParameter.setJobdesc("INCIDENTREPORT");
            uploadIncidentReportParameter.setAatop(-1);
            uploadIncidentReportParameter.setAssetid(-1);
            uploadIncidentReportParameter.setCuser(peopleID);
            uploadIncidentReportParameter.setFrequency(-1);
            uploadIncidentReportParameter.setPlandatetime(CommonFunctions.getFormatedDate(System.currentTimeMillis()));
            uploadIncidentReportParameter.setExpirydatetime(CommonFunctions.getFormatedDate(System.currentTimeMillis()));
            uploadIncidentReportParameter.setGracetime(0);
            uploadIncidentReportParameter.setGroupid(-1);
            uploadIncidentReportParameter.setIdentifier(typeAssistDAO.getEventTypeID("INCIDENTREPORT"));
            uploadIncidentReportParameter.setJobid(-1);
            uploadIncidentReportParameter.setJobneedid(fromID);
            uploadIncidentReportParameter.setJobstatus(-1);
            uploadIncidentReportParameter.setJobtype(-1);
            uploadIncidentReportParameter.setMuser(peopleID);
            uploadIncidentReportParameter.setParent(-1);
            uploadIncidentReportParameter.setPeopleid(peopleID);
            uploadIncidentReportParameter.setPerformedby(peopleID);
            uploadIncidentReportParameter.setPriority(typeAssistDAO.getEventTypeID("HIGH"));
            uploadIncidentReportParameter.setScantype(-1);
            uploadIncidentReportParameter.setChild(questionSetLevelOneArrayList);*/


//completed json format for incident report
            /*for(QuestionAnswerTransaction quest: incidentReportQuesAdapter.getData())
            {
                if(quest.getQuestAnswer()!=null && quest.getQsetID()!=-1) {
                    QuestionAnswerTransaction questionAnswerTransaction= new QuestionAnswerTransaction();
                    questionAnswerTransaction.setQuestAnswer(quest.getQuestAnswer());
                    questionAnswerTransaction.setQuestionid(quest.getQuestionid());
                    questionAnswerTransaction.setQuestionsetName(quest.getQuestionsetName());
                    questionAnswerTransaction.setQsetID(quest.getQsetID());
                    questionAnswerTransactionArrayListtemp.add(questionAnswerTransaction);
                }
            }

            for(int qs=0;qs<questionSetArrayList.size();qs++)
            {
                questionSetLevelOne=new QuestionSetLevel_One();
                questionSetLevelOne.setJobDesc(questionSetArrayList.get(qs).getQsetname());
                questionSetLevelOne.setQuestionsetid(questionSetArrayList.get(qs).getQuestionsetid());

                for(int qss=0;qss<questionSubSetArrayList.size();qss++)
                {
                    if(questionSetArrayList.get(qs).getQuestionsetid()==questionSubSetArrayList.get(qss).getParentId()) {
                        questionSetLevelTwo = new QuestionSetLevel_Two();
                        questionSetLevelTwo.setJobDesc(questionSubSetArrayList.get(qss).getQuestionsetName());
                        questionSetLevelTwo.setQuestionsetid(questionSubSetArrayList.get(qss).getQsetID());
                        for(int q=0;q<questionAnswerTransactionArrayListtemp.size();q++)
                        {
                            if(questionAnswerTransactionArrayListtemp.get(q).getQsetID()==questionSubSetArrayList.get(qs).getQsetID())
                            {
                                //answer":"0","cuser":0,"jndid":0,"jobneedid":0,"max":0.0,"min":0.0,"muser":0,"questionid":0,"seqno":0,"type":0
                                JobNeedDetails jobNeedDetails=new JobNeedDetails();
                                jobNeedDetails.setAnswer(questionAnswerTransactionArrayListtemp.get(q).getQuestAnswer());
                                jobNeedDetails.setCuser(questionAnswerTransactionArrayListtemp.get(q).getCuser());
                                jobNeedDetails.setJndid(-1);
                                jobNeedDetails.setJobneedid(-1);
                                jobNeedDetails.setMax(questionAnswerTransactionArrayListtemp.get(q).getMax());
                                jobNeedDetails.setMin(questionAnswerTransactionArrayListtemp.get(q).getMin());
                                jobNeedDetails.setMuser(questionAnswerTransactionArrayListtemp.get(q).getMuser());
                                jobNeedDetails.setQuestionid(questionAnswerTransactionArrayListtemp.get(q).getQuestionid());
                                jobNeedDetails.setSeqno(questionAnswerTransactionArrayListtemp.get(q).getSeqno());
                                jobNeedDetails.setType(questionAnswerTransactionArrayListtemp.get(q).getType());
                                jobNeedDetailsArrayList.add(jobNeedDetails);
                                questionSetLevelTwo.setDetails(jobNeedDetailsArrayList);
                            }
                            else
                            {
                                jobNeedDetailsArrayList=new ArrayList<>();
                            }
                        }


                        questionSetLevelTwoArrayList.add(questionSetLevelTwo);
                        questionSetLevelOne.setSubChild(questionSetLevelTwoArrayList);
                    }
                    else
                    {

                        questionSetLevelTwoArrayList=new ArrayList<>();
                    }

                }

                questionSetLevelOneArrayList.add(questionSetLevelOne);
            }

            uploadIncidentReportParameter=new UploadIncidentReportParameter();
            uploadIncidentReportParameter.setJobdesc("INCIDENTREPORT");
            uploadIncidentReportParameter.setAatop(-1);
            uploadIncidentReportParameter.setAssetid(-1);
            uploadIncidentReportParameter.setCuser(peopleID);
            uploadIncidentReportParameter.setFrequency(-1);
            uploadIncidentReportParameter.setPlandatetime(CommonFunctions.getFormatedDate(System.currentTimeMillis()));
            uploadIncidentReportParameter.setExpirydatetime(CommonFunctions.getFormatedDate(System.currentTimeMillis()));
            uploadIncidentReportParameter.setGracetime(0);
            uploadIncidentReportParameter.setGroupid(-1);
            uploadIncidentReportParameter.setIdentifier(typeAssistDAO.getEventTypeID("INCIDENTREPORT"));
            uploadIncidentReportParameter.setJobid(-1);
            uploadIncidentReportParameter.setJobneedid(fromID);
            uploadIncidentReportParameter.setJobstatus(-1);
            uploadIncidentReportParameter.setJobtype(-1);
            uploadIncidentReportParameter.setMuser(peopleID);
            uploadIncidentReportParameter.setParent(-1);
            uploadIncidentReportParameter.setPeopleid(peopleID);
            uploadIncidentReportParameter.setPerformedby(peopleID);
            uploadIncidentReportParameter.setPriority(typeAssistDAO.getEventTypeID("HIGH"));
            uploadIncidentReportParameter.setScantype(-1);
            uploadIncidentReportParameter.setChild(questionSetLevelOneArrayList);*/


            /*Gson gson=new Gson();
            String ss=gson.toJson(uploadIncidentReportParameter);
            System.out.println("SS: "+ss.toString());*/

//--------------------------------------------------------------------- onclick events

/*
if(fromActivity.equalsIgnoreCase("JOB"))//no need to change , its working
        {
        boolean isValidData = true;
        for (JobNeedDetails quest : jobNeedDetailsQuesAdapter.getData()) {
        jobNeedDetailsDAO.changeQuestionAns(quest.getJndid(), quest.getAnswer(), quest.getQuestionid());

        System.out.println("Ans: " + quest.getAnswer().toString().trim());
        if (quest.getAnswer().toString().trim().length() > 0)
        System.out.println("Done");
        else {
        System.out.println("Failed");
        isValidData = false;
        }
        }

        if (isValidData) {

        jobNeedDAO.changeJobNeedSyncStatus(fromID,"2");

        }

        setResult(RESULT_OK);
        finish();
        }
        else if(fromActivity.equalsIgnoreCase("INCIDENTREPORT"))
        {
        boolean isValidData = true;
        ArrayList<JobNeedDetails>jobNeedDetailsArrayList=new ArrayList<>();
        UploadIncidentReportParameter uploadIncidentReportParameter=new UploadIncidentReportParameter();
        ArrayList<QuestionSetLevel_One>questionSetLevelOneArrayList=new ArrayList<>();
        ArrayList<QuestionAnswerTransaction>questionAnswerTransactionArrayListtemp=new ArrayList<>();
        QuestionSetLevel_One questionSetLevelOne=null;
        ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();
        QuestionSetLevel_Two questionSetLevelTwo=null;

        //insert data into database
        for(QuestionAnswerTransaction questionAnswerTransaction : incidentReportQuesAdapter.getData())
        {
        JobNeedDetails jobNeedDetails=new JobNeedDetails();
        jobNeedDetails.setJndid(System.currentTimeMillis());
        jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
        jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
        jobNeedDetails.setType(questionAnswerTransaction.getType());
        jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
        jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
        jobNeedDetails.setMin(questionAnswerTransaction.getMin());
        jobNeedDetails.setMax(questionAnswerTransaction.getMax());
        jobNeedDetails.setAlerton("");
        jobNeedDetails.setIsmandatory("");
        jobNeedDetails.setCdtz(questionAnswerTransaction.getCdtz());
        jobNeedDetails.setMdtz(questionAnswerTransaction.getMdtz());
        jobNeedDetails.setCuser(peopleID);
        jobNeedDetails.setJobneedid(questionAnswerTransaction.getParentId());
        jobNeedDetails.setMuser(peopleID);

        jobNeedDetailsDAO.insertRecord(jobNeedDetails);
        }

        //for(int qs=0;qs<questionSetArrayList.size();qs++)
        {
        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(currentTimestamp);
        jobNeed.setJobdesc(fromQuestionSetName);
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setExpirydatetime(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(-1);//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks("");
        jobNeed.setCuser(peopleID);
        jobNeed.setCdtz(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setMuser(peopleID);
        jobNeed.setMdtz(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(-1);//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(peopleID);
        jobNeed.setAatop(peopleID);
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(peopleID);
        jobNeed.setQuestionsetid(fromQuestionSetID);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_INCIDENT,Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(-1);
        jobNeedDAO.insertRecord(jobNeed);

        for(int qss=0;qss<questionSubSetArrayList.size();qss++)
        {
        if(fromQuestionSetID==questionSubSetArrayList.get(qss).getParentId()) {
        jobNeed = new JobNeed();
        jobNeed.setJobneedid(currentTimestamp);
        jobNeed.setJobdesc(questionSubSetArrayList.get(qss).getQuestionsetName());
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setExpirydatetime(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(-1);//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(-1);//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks("");
        jobNeed.setCuser(peopleID);
        jobNeed.setCdtz(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setMuser(peopleID);
        jobNeed.setMdtz(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(-1);//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(peopleID);
        jobNeed.setAatop(peopleID);
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(peopleID);
        jobNeed.setQuestionsetid(questionSubSetArrayList.get(qss).getQsetID());
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeID(Constants.JOB_NEED_IDENTIFIER_INCIDENT, Constants.IDENTIFIER_JOBNEED));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(fromQuestionSetID);
        jobNeedDAO.insertRecord(jobNeed);
        }
        }
        }

        setResult(RESULT_OK);
        finish();


        }
        else if(fromActivity.equalsIgnoreCase("TOUR"))
        {
        boolean isValidData = true;
        for (JobNeedDetails quest : jobNeedDetailsQuesAdapter.getData()) {
        jobNeedDetailsDAO.changeQuestionAns(quest.getJndid(), quest.getAnswer(), quest.getQuestionid());
        jobNeedDAO.changeJobNeedSyncStatus(quest.getJobneedid(),"2");

        System.out.println("Ans: " + quest.getAnswer().toString().trim());
        if (quest.getAnswer().toString().trim().length() > 0)
        System.out.println("Done");
        else {
        System.out.println("Failed");
        isValidData = false;
        }
        }
        ArrayList<JobNeedDetails>jobNeedDetailsArrayList1=new ArrayList<>();
        for(JobNeedDetails quest : jobNeedDetailsQuesAdapter.getData())
        {
        jobNeedDetailsArrayList1.add(quest);
        }

        for(JobNeedDetails quest : jobNeedDetailsArrayList1)
        {
        System.out.println("Seqno: "+quest.getSeqno());
        System.out.println("Questionid: "+quest.getQuestionid());
        System.out.println("Answer: "+quest.getAnswer());
        System.out.println("JObNeedid: "+quest.getJobneedid());
        System.out.println("---------------------------------------------------------------");
        }



        //will use in sync process to upload data, its just dummy data to check json format

        UploadJobneedParameter uploadJobneedParameter=new UploadJobneedParameter();
        uploadJobneedParameter.setJobneedid(jobNeed.getJobneedid());
        uploadJobneedParameter.setJobdesc(jobNeed.getJobdesc());
        uploadJobneedParameter.setPlandatetime(jobNeed.getPlandatetime());
        uploadJobneedParameter.setExpirydatetime(jobNeed.getExpirydatetime());
        uploadJobneedParameter.setGracetime(jobNeed.getGracetime());
        uploadJobneedParameter.setReceivedonserver(jobNeed.getReceivedonserver());
        uploadJobneedParameter.setStarttime(jobNeed.getStarttime());
        uploadJobneedParameter.setEndtime(jobNeed.getEndtime());
        uploadJobneedParameter.setGpslocation(jobNeed.getGpslocation());
        uploadJobneedParameter.setRemarks(jobNeed.getRemarks());
        uploadJobneedParameter.setAatop(jobNeed.getAatop());
        uploadJobneedParameter.setAssetid(jobNeed.getAssetid());
        uploadJobneedParameter.setFrequency(jobNeed.getFrequency());
        uploadJobneedParameter.setJobid(jobNeed.getJobid());
        uploadJobneedParameter.setJobtype(jobNeed.getJobtype());
        uploadJobneedParameter.setJobstatus(jobNeed.getJobstatus());
        uploadJobneedParameter.setPerformedby(jobNeed.getPerformedby());
        uploadJobneedParameter.setPriority(jobNeed.getPriority());
        uploadJobneedParameter.setQuestionsetid(jobNeed.getQuestionsetid());
        uploadJobneedParameter.setScantype(jobNeed.getScantype());
        uploadJobneedParameter.setPeopleid(jobNeed.getPeopleid());
        uploadJobneedParameter.setGroupid(jobNeed.getGroupid());
        uploadJobneedParameter.setIdentifier(jobNeed.getIdentifier());
        uploadJobneedParameter.setParent(jobNeed.getParent());
        uploadJobneedParameter.setCuser(jobNeed.getCuser());
        uploadJobneedParameter.setCdtz(jobNeed.getCdtz());
        uploadJobneedParameter.setMdtz(jobNeed.getMdtz());
        uploadJobneedParameter.setMuser(jobNeed.getMuser());
        uploadJobneedParameter.setIsdeleted(jobNeed.getIsdeleted());
        uploadJobneedParameter.setDetails(jobNeedDetailsArrayList1);

        Gson gson=new Gson();
        String ss=gson.toJson(uploadJobneedParameter);
        System.out.println("SS: "+ss.toString());


        if (isValidData) {
        jobNeedDAO.changeJobStatus(fromID,typeAssistDAO.getEventTypeID("COMPLETED"));
                */
/*for (JobNeedDetails quest : jobNeedDetailsQuesAdapter.getData()) {
                    JOBNeedDetailUpdateAsynTask jobNeedDetailUpdateAsynTask = new JOBNeedDetailUpdateAsynTask(quest);
                    jobNeedDetailUpdateAsynTask.execute();
                }*//*


        }



        setResult(RESULT_OK);
        finish();
        }
        else if(fromActivity.equalsIgnoreCase("CHECKPOINT"))
        {
        boolean isValidData = true;


            */
/*for(QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData())
            {
                System.out.println("Ans: "+questionAnswerTransaction.getQuestAnswer().toString().trim());
                if(questionAnswerTransaction.getQuestAnswer().toString().trim().length()==0)
                    isValidData=false;
            }*//*


        if(isValidData)
        {
        //jobdesc, frequency, plandatetime, expirydatetime, gracetime, jobtype, jobstatus,  scantype, receivedonserver, priority,starttime, endtime, gpslocation,
        // remarks, cuser,  cdtz, muser,mdtz, isdeleted, assetcode, aatog, aatop, jobcode, performedby,  qsetcode




        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(currentTimestamp);
        jobNeed.setJobdesc(Constants.JOB_TYPE_ADHOC+" "+getIntent().getStringExtra("ASSETCODE"));
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setExpirydatetime(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID("COMPLETED"));//need to get id form type assist for Assigned status
        jobNeed.setScantype(typeAssistDAO.getEventTypeID("QR"));
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks("");
        jobNeed.setCuser(peopleID);
        jobNeed.setCdtz(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setMuser(peopleID);
        jobNeed.setMdtz(CommonFunctions.getParseDate(currentTimestamp));
        jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(assetDAO.getAssetID(getIntent().getStringExtra("ASSETCODE")));//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(peopleID);
        jobNeed.setAatop(peopleID);
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(peopleID);
        jobNeed.setQuestionsetid(fromID);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_TOUR));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(-1);
        jobNeedDAO.insertRecord(jobNeed);

        //jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
        for(QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData())
        {
        JobNeedDetails jobNeedDetails=new JobNeedDetails();
        jobNeedDetails.setJndid(System.currentTimeMillis());
        jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
        jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
        jobNeedDetails.setType(questionAnswerTransaction.getType());
        jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
        jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
        jobNeedDetails.setMin(questionAnswerTransaction.getMin());
        jobNeedDetails.setMax(questionAnswerTransaction.getMax());
        jobNeedDetails.setAlerton("");
        jobNeedDetails.setIsmandatory("");
        jobNeedDetails.setCdtz(questionAnswerTransaction.getCdtz());
        jobNeedDetails.setMdtz(questionAnswerTransaction.getMdtz());
        jobNeedDetails.setCuser(peopleID);
        jobNeedDetails.setJobneedid(currentTimestamp);
        jobNeedDetails.setMuser(peopleID);

        jobNeedDetailsDAO.insertRecord(jobNeedDetails);
        }

        //fetched from db and the crate json format
                */
/*ArrayList<JobNeed>jobNeedArrayList=jobNeedDAO.getUnsyncJobList(Constants.JOB_NEED_IDENTIFIER_TOUR);
                ArrayList<JobNeedDetails>jobNeedDetailsArrayList=new ArrayList<>();
                UploadJobneedParameter uploadJobneedParameter=null;
                for(int i=0;i<jobNeedArrayList.size();i++)
                {
                    jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedArrayList.get(i).getJobneedid());
                    uploadJobneedParameter=new UploadJobneedParameter();
                    uploadJobneedParameter.setJobNeedDetailsList(jobNeedDetailsArrayList);
                    uploadJobneedParameter.setJobneedid(jobNeedArrayList.get(i).getJobneedid());
                    uploadJobneedParameter.setJobdesc(jobNeedArrayList.get(i).getJobdesc());

                    Gson gson=new Gson();
                    String ss=gson.toJson(uploadJobneedParameter);
                    System.out.println("SS: "+i+" : "+ss.toString());

                }*//*


        setResult(RESULT_OK);
        finish();
        }
        else
        {
        Toast.makeText(IncidentReportQuestionActivity.this,"Please fill data in all field and then try to save", Toast.LENGTH_LONG).show();
        }

        }
        else if(fromActivity.equalsIgnoreCase("ADHOC"))
        {
        boolean isValidData = true;


        if(isValidData)
        {
        //jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
        for(QuestionAnswerTransaction questionAnswerTransaction : checkPointQuestAdapter.getData())
        {
        JobNeedDetails jobNeedDetails=new JobNeedDetails();
        jobNeedDetails.setJndid(System.currentTimeMillis());
        jobNeedDetails.setSeqno(questionAnswerTransaction.getSeqno());
        jobNeedDetails.setQuestionid(questionAnswerTransaction.getQuestionid());//add qeustion id from quedstion name
        jobNeedDetails.setType(questionAnswerTransaction.getType());
        jobNeedDetails.setAnswer(questionAnswerTransaction.getQuestAnswer());
        jobNeedDetails.setOption(questionAnswerTransaction.getOptions());
        jobNeedDetails.setMin(questionAnswerTransaction.getMin());
        jobNeedDetails.setMax(questionAnswerTransaction.getMax());
        jobNeedDetails.setAlerton("");
        jobNeedDetails.setIsmandatory("");
        jobNeedDetails.setCdtz(questionAnswerTransaction.getCdtz());
        jobNeedDetails.setMdtz(questionAnswerTransaction.getMdtz());
        jobNeedDetails.setCuser(peopleID);
        jobNeedDetails.setJobneedid(adhocJobPef.getLong(Constants.ADHOC_TIMESTAMP,-1));
        jobNeedDetails.setMuser(peopleID);

        jobNeedDetailsDAO.insertRecord(jobNeedDetails);
        }

        setResult(RESULT_OK);
        finish();
        }
        else
        {
        Toast.makeText(IncidentReportQuestionActivity.this,"Please fill data in all field and then try to save", Toast.LENGTH_LONG).show();
        }

        }*/
