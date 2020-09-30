package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Adapters.CheckPointChildListViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.android.CaptureActivity;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;


import java.util.ArrayList;
import java.util.Date;

public  class CheckpointChildListActivity extends Activity implements AdapterView.OnItemClickListener, IDialogEventListeners {
    private long jobneedid=-1;
    private JobNeedDAO jobNeedDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private AssetDAO assetDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleDAO peopleDAO;
    private GroupDAO groupDAO;
    private String jobneedParentDesc=null;
    private TextView parentNameTextView;
    private TextView parentStatusTextView;
    private TextView parentAssingedToTextView;
    private ListView checkPointChildListView;
    private CheckPointChildListViewAdapter checkPointChildListViewAdapter;
    ArrayList<JobNeed> childCheckPointArraylist;
    //private long selectedAssetId=-1;
    //private long selectedJobneedId=-1;
    private SharedPreferences tourChildPref;
    private SharedPreferences loginPref;
    private SharedPreferences deviceRelatedPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkpoint_child_list);
        jobNeedDAO=new JobNeedDAO(CheckpointChildListActivity.this);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(CheckpointChildListActivity.this);
        assetDAO=new AssetDAO(CheckpointChildListActivity.this);
        typeAssistDAO=new TypeAssistDAO(CheckpointChildListActivity.this);
        peopleDAO=new PeopleDAO(CheckpointChildListActivity.this);
        groupDAO=new GroupDAO(CheckpointChildListActivity.this);

        customAlertDialog = new CustomAlertDialog(CheckpointChildListActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        tourChildPref=getSharedPreferences(Constants.TOUR_CHILD_PREF, MODE_PRIVATE);
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);
        jobneedParentDesc=getIntent().getStringExtra("JOBNEEDDESC");

        System.out.println("jobneedParentDesc: "+jobneedParentDesc);

        System.out.println("Child Count: "+jobNeedDAO.getChildCount(jobneedid));
        System.out.println("Child completed Count: "+jobNeedDAO.getCompletedChildCount(jobneedid));

        long jstatus=typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED);
        if(jobNeedDAO.getChildCount(jobneedid)==jobNeedDAO.getCompletedChildCount(jobneedid))
        {
            jobNeedDAO.changeJobNeedSyncStatus(jobneedid,Constants.SYNC_STATUS_TWO);
            jobNeedDAO.changeJobStatus(jobneedid, jstatus);
        }


        childCheckPointArraylist=new ArrayList<JobNeed>();
        childCheckPointArraylist=jobNeedDAO.getChildCheckPointList(jobneedid);
        if(childCheckPointArraylist!=null && childCheckPointArraylist.size()>0)
        {
            for(int c=0;c<childCheckPointArraylist.size();c++)
            {
                System.out.println("child Check point Desc: "+childCheckPointArraylist.get(c).getJobdesc());
                System.out.println("child Check point ID: "+childCheckPointArraylist.get(c).getJobneedid());
                System.out.println("child Check point Parent ID: "+childCheckPointArraylist.get(c).getParent());
            }
        }

        parentNameTextView=(TextView)findViewById(R.id.parentCheckPoint);
        //parentNameTextView.setText(getResources().getString(R.string.tour_parent)+"\n"+jobneedParentDesc);
        parentNameTextView.setText(jobneedParentDesc);
        parentStatusTextView=(TextView)findViewById(R.id.parentStatus);
        parentAssingedToTextView=(TextView)findViewById(R.id.parentAssignedto);

        JobNeed jobNeed=jobNeedDAO.getJobNeedDetails(jobneedid);
        if(jobNeed!=null)
        {
            System.out.println("Parent jobstatus: "+jobNeed.getJobstatus());
            System.out.println("Parent assignedtopeople: "+jobNeed.getPeopleid());
            System.out.println("Parent assignedtogroup: "+jobNeed.getGroupid());

            parentStatusTextView.setText(getResources().getString(R.string.ticket_jStatus)+typeAssistDAO.getEventTypeName(jobNeed.getJobstatus()));
            if(jobNeed.getPeopleid()!=-1)
                parentAssingedToTextView.setText(getResources().getString(R.string.adhoc_assignto)+peopleDAO.getPeopleName(jobNeed.getPeopleid()));
            else if(jobNeed.getGroupid()!=-1)
                parentAssingedToTextView.setText(getResources().getString(R.string.adhoc_assignto)+groupDAO.getGroupName(jobNeed.getGroupid()));
        }

        checkPointChildListView=(ListView)findViewById(R.id.checkpointChildListView);
        checkPointChildListViewAdapter=new CheckPointChildListViewAdapter(CheckpointChildListActivity.this, childCheckPointArraylist);
        checkPointChildListView.setAdapter(checkPointChildListViewAdapter);
        checkPointChildListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int accessValue = CommonFunctions.isAllowToAccessModules(CheckpointChildListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        System.out.println("==========="+accessValue);
        System.out.println("==========="+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0 ) {
            if (CommonFunctions.isPermissionGranted(CheckpointChildListActivity.this)) {
                System.out.println("AssetID: " + childCheckPointArraylist.get(i).getAssetid());
                System.out.println("AssetName: " + assetDAO.getAssetName(childCheckPointArraylist.get(i).getAssetid()));
                System.out.println("PlanDate: " + (childCheckPointArraylist.get(i).getPlandatetime()));
                System.out.println("Grace Time: " + childCheckPointArraylist.get(i).getGracetime());

                System.out.println("BackDate: " + new Date(CommonFunctions.getParseDate(childCheckPointArraylist.get(i).getPlandatetime()) - (childCheckPointArraylist.get(i).getGracetime() * 60 * 1000)).getTime());
                System.out.println("NextDate: " + (childCheckPointArraylist.get(i).getExpirydatetime()));

                System.out.println("BackDate: " + CommonFunctions.getFormatedDate(new Date(CommonFunctions.getParseDate(childCheckPointArraylist.get(i).getPlandatetime()) - (childCheckPointArraylist.get(i).getGracetime() * 60 * 1000)).getTime()));
                System.out.println("NextDate: " + (childCheckPointArraylist.get(i).getExpirydatetime()));

                long backDate = new Date(CommonFunctions.getParse24HrsDate(childCheckPointArraylist.get(i).getPlandatetime()) - (childCheckPointArraylist.get(i).getGracetime() * 60 * 1000)).getTime();

                //System.out.println("isValid: "+CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParseDate(childCheckPointArraylist.get(i).getExpirydatetime())));

                //selectedAssetId=childCheckPointArraylist.get(i).getAssetid();

                //selectedJobneedId=childCheckPointArraylist.get(i).getJobneedid();

                tourChildPref.edit().putLong(Constants.TOUR_CHILD_ASSETID, childCheckPointArraylist.get(i).getAssetid()).apply();
                tourChildPref.edit().putLong(Constants.TOUR_CHILD_QUESTIONSETID, childCheckPointArraylist.get(i).getQuestionsetid()).apply();
                tourChildPref.edit().putLong(Constants.TOUR_CHILD_JOBNEEDID, childCheckPointArraylist.get(i).getJobneedid()).apply();
                tourChildPref.edit().putLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID, jobneedid).apply();

                System.out.println("onItemClick selectedAssetId: " + tourChildPref.getLong(Constants.TOUR_CHILD_ASSETID, -1));
                System.out.println("onItemClick selectedParentID: " + jobneedid);
                System.out.println("onItemClick selectedChildID: " + childCheckPointArraylist.get(i).getJobneedid());

                String jobStatus = typeAssistDAO.getEventTypeCode(childCheckPointArraylist.get(i).getJobstatus());
                System.out.println("jobStatus: " + jobStatus);
                if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED)) {

                    Toast.makeText(CheckpointChildListActivity.this, getResources().getString(R.string.tour_checkpoint_completed), Toast.LENGTH_LONG).show();
                } else if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED)) {
                    Toast.makeText(CheckpointChildListActivity.this, getResources().getString(R.string.tour_checkpoint_closed), Toast.LENGTH_LONG).show();
                } else {
                    int isJobExpired = CommonFunctions.isInBetweenDate(backDate, CommonFunctions.getParse24HrsDate(childCheckPointArraylist.get(i).getExpirydatetime()));
                    /*if (isJobExpired == 1) {*/
                        System.out.println("ScanType: " + childCheckPointArraylist.get(i).getScantype());
                        if (childCheckPointArraylist.get(i).getScantype() != -1 && typeAssistDAO.getEventTypeCode(childCheckPointArraylist.get(i).getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_QR)) {

                            tourChildPref.edit().putString(Constants.TOUR_CHILD_STARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis())).apply();
                            Intent intent = new Intent(CheckpointChildListActivity.this, CaptureActivity.class);
                            intent.putExtra("FROM", "CHECKPOINT");
                            startActivityForResult(intent, 0);
                        } else if (typeAssistDAO.getEventTypeCode(childCheckPointArraylist.get(i).getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_NFC)) {
                            tourChildPref.edit().putString(Constants.TOUR_CHILD_STARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis())).apply();
                            Intent intent = new Intent(CheckpointChildListActivity.this, NFCCodeReaderActivity.class);
                            intent.putExtra("FROM", "CHECKPOINT");
                            startActivityForResult(intent, 2);
                        } else {
                            jobNeedDAO.changeJobNeedSyncStatus(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID, -1), Constants.SYNC_STATUS_TWO);
                            String gpsLocation = deviceRelatedPref.getString(Constants.DEVICE_LATITUDE, "0.0") + "," + deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE, "0.0");
                            //CommonFunctions.EventLog("\n Tour Child Completed: \n JOBNeed Id: "+tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1)+"\n Time: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"\n");
                            jobNeedDAO.updateChildTourCompleted(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID, -1), tourChildPref.getString(Constants.TOUR_CHILD_STARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis())), CommonFunctions.getTimezoneDate(System.currentTimeMillis()), loginPref.getLong(Constants.LOGIN_PEOPLE_ID, -1), typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED), gpsLocation, tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID, -1));
                            refreshAdapter();
                        }
                    /*} else if (isJobExpired == 0) {
                        Toast.makeText(CheckpointChildListActivity.this, getResources().getString(R.string.job_is_future, childCheckPointArraylist.get(i).getPlandatetime()), Toast.LENGTH_LONG).show();
                        //showCustomToastMsg(getResources().getString(R.string.job_is_future,childCheckPointArraylist.get(i).getPlandatetime()));
                    } else if (isJobExpired == 2) {
                        Toast.makeText(CheckpointChildListActivity.this, getResources().getString(R.string.job_has_expired), Toast.LENGTH_SHORT).show();
                    }*/
            /*{
                Intent ii = new Intent(CheckpointChildListActivity.this, IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "TOUR");
                ii.putExtra("ID", selectedJobneedId);
                ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
                ii.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TOUR);
                startActivityForResult(ii, 1);
            }*/

                }
            } else
                Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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
        if(requestCode==0)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    String assetCode=data.getStringExtra("SCAN_RESULT");
                    long assetIDFrmDB=assetDAO.getAssetID(assetCode);
                    System.out.println("assetIDFrmDB: "+assetIDFrmDB);
                    System.out.println("selectedAssetId: "+tourChildPref.getLong(Constants.TOUR_CHILD_ASSETID,-1));
                    System.out.println("selectedChildjobneedid: "+tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1));
                    System.out.println("JObneeddetailcount: "+jobNeedDetailsDAO.getChkPointQuestionCount(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1)));
                    if(assetIDFrmDB==tourChildPref.getLong(Constants.TOUR_CHILD_ASSETID,-1))
                    {
                        if(jobNeedDetailsDAO.getChkPointQuestionCount(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1))>0)
                        {
                            Intent ii = new Intent(CheckpointChildListActivity.this, IncidentReportQuestionActivity.class);
                            ii.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_TOUR);
                            ii.putExtra("ID", tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1));
                            ii.putExtra("PARENT_ACTIVITY","JOBNEED");
                            ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TOUR);
                            ii.putExtra("PARENTID",jobneedid);
                            startActivityForResult(ii, 1);
                        }
                        else
                        {
                            jobNeedDAO.changeJobNeedSyncStatus(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1), Constants.SYNC_STATUS_TWO);
                            String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");
                            //CommonFunctions.EventLog("\n Tour Child Completed: \n JOBNeed Id: "+tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1)+"\n Time: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+"\n");
                            jobNeedDAO.updateChildTourCompleted(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1), tourChildPref.getString(Constants.TOUR_CHILD_STARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis())), CommonFunctions.getTimezoneDate(System.currentTimeMillis()), loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1), typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED),gpsLocation, tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID,-1));
                            refreshAdapter();

                        }

                    }
                    else
                    {
                        Toast.makeText(CheckpointChildListActivity.this,getResources().getString(R.string.tour_incorrect_asset_chkpoint),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        else if(requestCode==2)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    String assetCode=data.getStringExtra("SCAN_RESULT");
                    long assetIDFrmDB=assetDAO.getAssetID(assetCode);
                    System.out.println("assetIDFrmDB: "+assetIDFrmDB);
                    System.out.println("selectedAssetId: "+tourChildPref.getLong(Constants.TOUR_CHILD_ASSETID,-1));
                    if(assetIDFrmDB==tourChildPref.getLong(Constants.TOUR_CHILD_ASSETID,-1))
                    {
                        if(jobNeedDetailsDAO.getChkPointQuestionCount(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1))>0)
                        {
                            Intent ii = new Intent(CheckpointChildListActivity.this, IncidentReportQuestionActivity.class);
                            ii.putExtra("FROM", Constants.JOB_NEED_IDENTIFIER_TOUR);
                            ii.putExtra("ID", tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1));
                            ii.putExtra("PARENT_ACTIVITY","JOBNEED");
                            ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TOUR);
                            ii.putExtra("PARENTID",jobneedid);
                            startActivityForResult(ii, 1);
                        }
                        else
                        {
                            jobNeedDAO.changeJobNeedSyncStatus(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1), Constants.SYNC_STATUS_TWO);
                            String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");
                            jobNeedDAO.updateChildTourCompleted(tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,-1), tourChildPref.getString(Constants.TOUR_CHILD_STARTTIME, CommonFunctions.getTimezoneDate(System.currentTimeMillis())), CommonFunctions.getTimezoneDate(System.currentTimeMillis()), loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1), typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED),gpsLocation, tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID,-1));
                            refreshAdapter();

                        }
                    }
                    else
                    {
                        Toast.makeText(CheckpointChildListActivity.this,getResources().getString(R.string.tour_incorrect_asset_chkpoint),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        else if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                refreshAdapter();
                /*int maxTour=jobNeedDAO.getChildCount(jobneedid);
                int completedTour=jobNeedDAO.getCompletedChildCount(jobneedid);
                if(maxTour==completedTour)
                {
                    System.out.println("Completed: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" --- jobneedid: "+jobneedid);
                    jobNeedDAO.updateParentTourCompleted(jobneedid,tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())),CommonFunctions.getTimezoneDate(System.currentTimeMillis()),loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1) ,typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));
                    jobNeedDAO.changeJobNeedSyncStatus(jobneedid,Constants.SYNC_STATUS_TWO);
                }
                else if(completedTour==1 || completedTour >=1)
                {
                    System.out.println("PartiallyCompleted: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" --- jobneedid: "+jobneedid);
                    tourChildPref.edit().putString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())).apply();
                    jobNeedDAO.updateParentTourPartiallyCompleted(jobneedid,tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())), loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1),typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_PARTIALLY_COMPLETED, Constants.STATUS_TYPE_JOBNEED) );
                    jobNeedDAO.changeJobNeedSyncStatus(jobneedid,Constants.SYNC_STATUS_TWO);
                }

                childCheckPointArraylist=new ArrayList<JobNeed>();
                childCheckPointArraylist=jobNeedDAO.getChildCheckPointList(jobneedid);
                checkPointChildListViewAdapter=new CheckPointChildListViewAdapter(CheckpointChildListActivity.this, childCheckPointArraylist);
                checkPointChildListViewAdapter.notifyDataSetChanged();
                checkPointChildListView.setAdapter(checkPointChildListViewAdapter);*/

            }
        }
    }

    private void refreshAdapter()
    {
        System.out.println("jobneedid: "+jobneedid);
        System.out.println("jobneedid from pref: "+tourChildPref.getLong(Constants.TOUR_CHILD_PARENT_JOBNEEDID,-1));
        int maxTour=jobNeedDAO.getChildCount(jobneedid);
        int completedTour=jobNeedDAO.getCompletedChildCount(jobneedid);
        if(maxTour==completedTour)
        {
            System.out.println("Completed: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" --- jobneedid: "+jobneedid);
            jobNeedDAO.updateParentTourCompleted(jobneedid,tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())),CommonFunctions.getTimezoneDate(System.currentTimeMillis()),loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1) ,typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_COMPLETED, Constants.STATUS_TYPE_JOBNEED));
            jobNeedDAO.changeJobNeedSyncStatus(jobneedid,Constants.SYNC_STATUS_TWO);
        }
        else if(completedTour==1 || completedTour >=1)
        {
            System.out.println("PartiallyCompleted: "+CommonFunctions.getTimezoneDate(System.currentTimeMillis())+" --- jobneedid: "+jobneedid);
            tourChildPref.edit().putString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())).apply();
            jobNeedDAO.updateParentTourPartiallyCompleted(jobneedid,tourChildPref.getString(Constants.TOUR_PARENT_STARTTIME,CommonFunctions.getTimezoneDate(System.currentTimeMillis())), loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1),typeAssistDAO.getEventTypeID(Constants.JOBNEED_STATUS_PARTIALLY_COMPLETED, Constants.STATUS_TYPE_JOBNEED), tourChildPref.getLong(Constants.TOUR_CHILD_JOBNEEDID,0) );
            jobNeedDAO.changeJobNeedSyncStatus(jobneedid,Constants.SYNC_STATUS_TWO);
        }

        childCheckPointArraylist=new ArrayList<JobNeed>();
        childCheckPointArraylist=jobNeedDAO.getChildCheckPointList(jobneedid);
        checkPointChildListViewAdapter=new CheckPointChildListViewAdapter(CheckpointChildListActivity.this, childCheckPointArraylist);
        checkPointChildListViewAdapter.notifyDataSetChanged();
        checkPointChildListView.setAdapter(checkPointChildListViewAdapter);
    }

    @Override
    protected void onResume() {
        System.out.println("OnResume checkpointchildlist");
        super.onResume();
    }

    @Override
    protected void onStop() {
        System.out.println("OnStop checkpointchildlist");
        super.onStop();
    }

    @Override
    protected void onPause() {
        System.out.println("OnPause checkpointchildlist");
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    public void showCustomToastMsg(String toastMsg)
    {
        View toastView = getLayoutInflater().inflate(R.layout.custom_toast_msg_layout, null);
        TextView tMsg=((TextView) toastView).findViewById(R.id.msgToastTextView);
        tMsg.setText(toastMsg);
        // Initiate the Toast instance.
        Toast toast = new Toast(getApplicationContext());
        // Set custom view in toast.
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0,0);
        toast.show();
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }
}
