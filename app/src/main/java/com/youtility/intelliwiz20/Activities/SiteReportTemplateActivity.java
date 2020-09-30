package com.youtility.intelliwiz20.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Adapters.SiteTemplateViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.SiteTemplateDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.TemplateList;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Services.SiteReportDoneOrNotCheckService;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;

import java.util.ArrayList;

public class SiteReportTemplateActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, IDialogEventListeners {

    private SharedPreferences siteAuditPref;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private Button checkoutButton;
    private TextView selectedSiteNameTextView;
    private ListView templateListView;
    ArrayList<QuestionSet> questionSetArrayList = null;
    private JobNeedDAO jobNeedDAO;
    private SiteTemplateDAO siteTemplateDAO;
    private PeopleDAO peopleDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleEventLogDAO peopleEventLogDAO;
    private SiteTemplateViewAdapter siteTemplateViewAdapter;
    private CustomAlertDialog customAlertDialog;

    private SharedPreferences deviceInfoPref;
    private SharedPreferences loginPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_report_template);

        siteAuditPref=getSharedPreferences(Constants.SITE_AUDIT_PREF, MODE_PRIVATE);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        jobNeedDAO=new JobNeedDAO(SiteReportTemplateActivity.this);
        siteTemplateDAO =new SiteTemplateDAO(SiteReportTemplateActivity.this);
        peopleDAO=new PeopleDAO(SiteReportTemplateActivity.this);
        typeAssistDAO=new TypeAssistDAO(SiteReportTemplateActivity.this);
        peopleEventLogDAO=new PeopleEventLogDAO(SiteReportTemplateActivity.this);

        customAlertDialog=new CustomAlertDialog(SiteReportTemplateActivity.this,this);

        System.out.println("Site Name: "+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""));
        System.out.println("Site Checkin: "+siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_CHECKIN,false));
        System.out.println("Site Name: "+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""));


        if(!siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED,false))
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, false).apply();

        checkoutButton=(Button)findViewById(R.id.siteCheckOutButton);
        selectedSiteNameTextView=(TextView)findViewById(R.id.selectedSiteNameTextView);
        templateListView=(ListView)findViewById(R.id.siteReportTemplateListView);

        templateListView.setOnItemClickListener(this);
        selectedSiteNameTextView.setText(siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME,""));

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int accessValue = CommonFunctions.isAllowToAccessModules(SiteReportTemplateActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                if (accessValue == 0) {
                    if (!isValidSkipSiteAudit()) {
                        if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false)) {
                            System.out.println("====not othersite====1");
                            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, true).apply();
                            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, System.currentTimeMillis()).apply();
                            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, true).apply();
                            insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_OUT, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""), "");
                            showToastMsg(getResources().getString(R.string.srtemplate_chkout_succeed));
                            System.out.println("out site:" + siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""));

                            siteTemplateDAO.deleteRec();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showToastMsg(getResources().getString(R.string.srtemplate_onereport_needtofill));
                        }
                    } else {
                        if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false)) {
                            System.out.println("====othersite====2");
                            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, true).apply();
                            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, System.currentTimeMillis()).apply();
                            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED, true).apply();
                            insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_OUT, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""), "");
                            System.out.println("out site:" + siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""));
                            showToastMsg(getResources().getString(R.string.srtemplate_chkout_succeed));
                            siteTemplateDAO.deleteRec();
                            setResult(RESULT_OK);
                            finish();
                            siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, "").apply();

                        } else
                            callCustAlertScreen();
                    }
                } else if (accessValue == 1) {
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
                } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                    System.out.println("===========lat long==0.0");
                }
                //checkoutconfirmation();

                /*if (siteAuditPref.getBoolean(Constants.SITE_AUDIT_ISPERFORMED, false)) {
                    checkoutconfirmation();
                } else {
                    showToastMsg("At least one report need to fill");
                }*/

            }
        });

        perpareQSetList();

        Intent alertService = new Intent(SiteReportTemplateActivity.this, SiteReportDoneOrNotCheckService.class);
        startService(alertService);

    }

    private boolean isValidSkipSiteAudit()
    {
        /*if(loginDetailPref.getString(Constants.LOGIN_USER_CLIENT_CODE,"").contains("SUKHI") || loginDetailPref.getString(Constants.LOGIN_USER_CLIENT_CODE,"").contains("MFCS"))
            return false;
        else
            return true;*/
        return loginDetailPref.getBoolean(Constants.LOGIN_CONFIG_SITE_AUDIT_SKIP,false);
    }

    private void checkoutconfirmation()
    {
        customAlertDialog.showYesNoAlertBox(getResources().getString(R.string.checkout_alert_title),"Do you want to check out?","",0);
    }

    private void showToastMsg(String msg)
    {
        Toast.makeText(SiteReportTemplateActivity.this,msg,Toast.LENGTH_LONG).show();
    }

    private void insertPeopleEventLogRecord(String punchStatus, String empCode, long inoutTimestamp, long siteID, String siteName ,String remark)
    {
        //accuracy, datetime, gpslocation, photorecognitionthreshold, photorecognitionscore, photorecognitiontimestamp, photorecognitionserviceresponse,
        //facerecognition, peopleid, peventtype, punchstatus, verifiedby, siteid, cuser, muser, cdtz, mdtz, isdeleted, gfid-, deviceid
        System.out.println("pelg sitename"+ siteName);
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

        long scannedPeopleId=peopleDAO.getPeopleId(empCode);

        PeopleEventLog peopleEventLog=new PeopleEventLog();
        peopleEventLog.setAccuracy(-1);
        peopleEventLog.setDeviceid("-1");
        peopleEventLog.setDatetime(String.valueOf(inoutTimestamp));
        peopleEventLog.setGpslocation(gpsLocation);
        peopleEventLog.setPhotorecognitionthreshold(-1);
        peopleEventLog.setPhotorecognitionscore(-1);
        peopleEventLog.setPhotorecognitiontimestamp(null);
        peopleEventLog.setPhotorecognitionserviceresponse(null);
        peopleEventLog.setFacerecognition("false");
        peopleEventLog.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        peopleEventLog.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //peopleEventLog.setIsdeleted("false");
        peopleEventLog.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        peopleEventLog.setPeopleid(scannedPeopleId);
        peopleEventLog.setPeventtype(typeAssistDAO.getEventTypeID("AUDIT", Constants.IDENTIFIER_ATTENDANCE));
        peopleEventLog.setPunchstatus(typeAssistDAO.getEventTypeID(punchStatus, Constants.IDENTIFIER_PUNCHSTATUS));
        peopleEventLog.setVerifiedby(-1);
        peopleEventLog.setScanPeopleCode(empCode);
        peopleEventLog.setBuid(siteID);
        peopleEventLog.setGfid(-1);
        peopleEventLog.setDistance(0);
        peopleEventLog.setDuration(0);
        peopleEventLog.setExpamt(0.0);
        peopleEventLog.setReference("");
        peopleEventLog.setRemarks(remark);
        peopleEventLog.setTransportmode(-1);
        peopleEventLog.setOtherlocation(siteName);
        peopleEventLogDAO.insertRecord(peopleEventLog);

    }


    private void callCustAlertScreen()
    {
        customAlertDialog.custSiteChkOutRemark(getResources().getString(R.string.alerttitle),getResources().getString(R.string.srtemplate_chkout_withoutform),1);
        //Toast.makeText(SiteReportTemplateActivity.this, "Please mention reason",Toast.LENGTH_SHORT).show();
    }

    private void perpareQSetList()
    {
        if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION,false))
        {
            ArrayList<TemplateList>templateList=new ArrayList<>();
            templateList=siteTemplateDAO.getOtherSiteTemplateList();

            if(templateList!=null && templateList.size()>0)
            {
                siteTemplateViewAdapter=new SiteTemplateViewAdapter(SiteReportTemplateActivity.this, templateList);
                templateListView.setAdapter(siteTemplateViewAdapter);

            }

            /*questionSetArrayList = new ArrayList<QuestionSet>();
            questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_questionsetcode_query, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));
            if(questionSetArrayList!=null && questionSetArrayList.size()>0)
            {
                siteTemplateViewAdapter=new SiteTemplateViewAdapter(SiteReportTemplateActivity.this, questionSetArrayList);
                templateListView.setAdapter(siteTemplateViewAdapter);

            }*/
        }
        else
        {
            ArrayList<TemplateList>templateList=new ArrayList<>();
            templateList=siteTemplateDAO.getTemplateList(siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1));

            if(templateList!=null && templateList.size()>0)
            {
                siteTemplateViewAdapter=new SiteTemplateViewAdapter(SiteReportTemplateActivity.this, templateList);
                templateListView.setAdapter(siteTemplateViewAdapter);

            }

            /*Sites sites=siteDAO.getSiteInfo(siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1));

            if(sites!=null)
            {
                String reportName = sites.getReportnames();//sitesArrayList.get(i).getReportnames();
                System.out.println("reportName: " + reportName);
                String reportIDs = sites.getReportids();//sitesArrayList.get(i).getReportids().trim();
                System.out.println("reportIds: " + reportIDs);
                questionSetArrayList = new ArrayList<QuestionSet>();

                if (reportIDs.equalsIgnoreCase("-1")) {
                    questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_questionsetcode_query, loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID, -1)));
                } else {
                    boolean isAvailable = reportName.contains("~");
                    if (isAvailable) {
                        String rIds = reportIDs.replace(" ", ",");
                        questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_templates_query, rIds));
                    } else {
                        questionSetArrayList = questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_siteaudit_templates_query, reportIDs));
                    }
                }

                if(questionSetArrayList!=null && questionSetArrayList.size()>0)
                {
                    siteTemplateViewAdapter=new SiteTemplateViewAdapter(SiteReportTemplateActivity.this, questionSetArrayList);
                    templateListView.setAdapter(siteTemplateViewAdapter);

                }
            }*/
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(this,DashboardActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            if(resultCode==RESULT_OK)
            {
                perpareQSetList();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*QuestionSet questionSet=(QuestionSet) parent.getAdapter().getItem(position);

        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, System.currentTimeMillis()).apply();

        siteAuditPref.edit().putLong(Constants.SITE_AUDIT_QUESTIONSETID, questionSet.getQuestionsetid()).apply();
        Intent fillReportIntent = new Intent(SiteReportTemplateActivity.this, IncidentReportQuestionActivity.class);
        fillReportIntent.putExtra("FROM", "SITEREPORT");
        fillReportIntent.putExtra("ID", siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP, -1));
        fillReportIntent.putExtra("QUESTIONSETID", questionSet.getQuestionsetid());
        fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
        fillReportIntent.putExtra("FOLDER", "SITEREPORT");
        startActivityForResult(fillReportIntent, 0);*/

        int accessValue = CommonFunctions.isAllowToAccessModules(SiteReportTemplateActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));

        if (accessValue == 0) {
            TemplateList questionSet = (TemplateList) parent.getAdapter().getItem(position);

            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_TIMESTAMP, System.currentTimeMillis()).apply();

            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_QUESTIONSETID, questionSet.getQuestionsetid()).apply();
            Intent fillReportIntent = new Intent(SiteReportTemplateActivity.this, IncidentReportQuestionActivity.class);
            fillReportIntent.putExtra("FROM", "SITEREPORT");
            fillReportIntent.putExtra("ID", siteAuditPref.getLong(Constants.SITE_AUDIT_TIMESTAMP, -1));
            fillReportIntent.putExtra("QUESTIONSETID", questionSet.getQuestionsetid());
            fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
            fillReportIntent.putExtra("FOLDER", "SITEREPORT");
            startActivityForResult(fillReportIntent, 0);
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
        } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
            System.out.println("===========lat long==0.0");
        }

    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==1)
        {
            System.out.println("====othersite===="+siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""));
            showToastMsg(getResources().getString(R.string.srtemplate_chkout_succeed));
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, true).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED,true).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, System.currentTimeMillis()).apply();
            insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_OUT, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1), siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""),errorMessage);
            addSkipSiteReportJobNeed(errorMessage,siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1),siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""));
            siteTemplateDAO.deleteRec();

            setResult(RESULT_OK);
            finish();
            siteAuditPref.edit().putString(Constants.SITE_AUDIT_SITENAME, "").apply();

        }
        /*else if(type==0)
        {
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKOUT, true).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, System.currentTimeMillis()).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_SITE_CHECKIN,false).apply();
            siteAuditPref.edit().putLong(Constants.SITE_AUDIT_SITE_CHECKIN_TIMESTAMP, -1).apply();
            siteAuditPref.edit().putBoolean(Constants.SITE_AUDIT_ISPERFORMED,true).apply();
            if(siteAuditPref.getBoolean(Constants.SITE_AUDIT_SITE_ISOTHERLOCATION, false)) {
                insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_OUT, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1),
                        siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""));
            }
            else
            {
                insertPeopleEventLogRecord(Constants.ATTENDANCE_PUNCH_TYPE_OUT, loginDetailPref.getString(Constants.LOGIN_PEOPLE_CODE, ""), siteAuditPref.getLong(Constants.SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP, -1),
                siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID, -1), siteAuditPref.getString(Constants.SITE_AUDIT_SITENAME, ""), "");
            }
            showToastMsg("Site check out successfully");
            siteTemplateDAO.deleteRec();
            setResult(RESULT_OK);
            finish();

        }*/

    }

    private void addSkipSiteReportJobNeed(String reasonMsg, long srTimestamp, String siteName)
    {
        String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");
        System.out.println("buid"+ siteName);
        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(System.currentTimeMillis());
        jobNeed.setJobdesc("Skip Site Report");
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(typeAssistDAO.getEventTypeID("LOW", Constants.IDENTIFIER_PRIORITY));
        jobNeed.setStarttime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setEndtime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setGpslocation(gpsLocation);
        jobNeed.setRemarks(reasonMsg);
        jobNeed.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(-1);//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setAatop(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setQuestionsetid(-1);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_SITEREPORT));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(-1);
        jobNeed.setTicketcategory(typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_AUTOCLOSED, "Ticket Category"));
        jobNeed.setBuid(loginDetailPref.getLong(Constants.LOGIN_SITE_ID,-1));
        jobNeed.setCtzoffset(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeed.setBuid(siteAuditPref.getLong(Constants.SITE_AUDIT_SITEID,-1));
        jobNeed.setOthersite(siteName);
        jobNeedDAO.insertRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
    }
}
