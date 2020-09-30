package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.JobNeedHistory;
import com.youtility.intelliwiz20.Model.ResponseData;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;
import com.youtility.intelliwiz20.android.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsActivity extends AppCompatActivity implements IDialogEventListeners {
    private JobNeed jobNeed;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleDAO peopleDAO;
    private GroupDAO groupDAO;
    private QuestionDAO questionDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private AssetDAO assetDAO;
    private long jobneedid=-1;
    private int isJobExpired=-1;
    private String jobStatus=null;
    private TextView questionSetTextview, attachmentCountTextview;
    private ArrayList<JobNeedDetails> jobNeedDetailsHistoryArrayList;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationMainPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;
    private SharedPreferences loginPref;

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        applicationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        jobNeedDAO=new JobNeedDAO(TaskDetailsActivity.this);
        typeAssistDAO=new TypeAssistDAO(TaskDetailsActivity.this);
        peopleDAO=new PeopleDAO(TaskDetailsActivity.this);
        groupDAO=new GroupDAO(TaskDetailsActivity.this);
        questionDAO=new QuestionDAO(TaskDetailsActivity.this);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(TaskDetailsActivity.this);
        assetDAO=new AssetDAO(TaskDetailsActivity.this);

        customAlertDialog=new CustomAlertDialog(TaskDetailsActivity.this, this);

        isJobExpired=getIntent().getIntExtra("JOBEXPIRED",-1);
        jobStatus=getIntent().getStringExtra("JOBSTATUS");
        jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);

        if(jobneedid!=-1)
            jobNeed=jobNeedDAO.getJobNeedDetails(jobneedid);

        questionSetTextview=(TextView)findViewById(R.id.item_questionsetid);
        attachmentCountTextview=(TextView)findViewById(R.id.item_attachmentcount);

        if(jobNeed!=null)
        {
            String assignedToValue=null;

            jobNeedDetailsHistoryArrayList=new ArrayList<JobNeedDetails>();
            jobNeedDetailsHistoryArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeed.getJobneedid());

            ((TextView) findViewById(R.id.item_ticketno)).setText(String.valueOf(jobNeed.getTicketno()));
            ((TextView) findViewById(R.id.item_code)).setText(String.valueOf(jobNeed.getJobneedid()));
            ((TextView) findViewById(R.id.item_description)).setText(jobNeed.getJobdesc());
            ((TextView) findViewById(R.id.item_plandate)).setText((jobNeed.getPlandatetime()));
            ((TextView) findViewById(R.id.item_expirydate)).setText((jobNeed.getExpirydatetime()));
            ((TextView) findViewById(R.id.item_gracetime)).setText(String.valueOf(jobNeed.getGracetime()));
            ((TextView) findViewById(R.id.item_frequency)).setText(typeAssistDAO.getEventTypeName(jobNeed.getFrequency()));
            ((TextView) findViewById(R.id.item_type)).setText(typeAssistDAO.getEventTypeName(jobNeed.getJobtype()));
            ((TextView) findViewById(R.id.item_status)).setText(typeAssistDAO.getEventTypeName(jobNeed.getJobstatus()));
            ((TextView) findViewById(R.id.item_priority)).setText(typeAssistDAO.getEventTypeName(jobNeed.getPriority()));
            ((TextView) findViewById(R.id.item_tCategory)).setText(typeAssistDAO.getEventTypeName(jobNeed.getTicketcategory()));
            ((TextView) findViewById(R.id.item_assginedby)).setText(peopleDAO.getPeopleName(jobNeed.getCuser()));
            questionSetTextview.setText(questionDAO.getQuestionSetName(jobNeed.getQuestionsetid()));
            questionSetTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(jobNeedDetailsHistoryArrayList!=null && jobNeedDetailsHistoryArrayList.size()>0)
                    {
                        System.out.println("jobNeedDetailsHistoryArrayList: "+jobNeedDetailsHistoryArrayList.size());
                        showAlertDialog();
                    }
                    else
                    {
                        Toast.makeText(TaskDetailsActivity.this,getResources().getString(R.string.jobneeddetails_detailsnotfound), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if(jobNeed.getAttachmentcount()!=null)
                attachmentCountTextview.setText(jobNeed.getAttachmentcount());
            else
                attachmentCountTextview.setText("0");

            attachmentCountTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(jobNeed!=null && jobNeed.getJobid()!=-1)
                        prepareAttachmentDownloadData(jobNeed.getJobneedid());
                }
            });

            if(jobNeed.getGroupid()!=-1) {
                assignedToValue = groupDAO.getGroupName(jobNeed.getGroupid());
            }
            if(jobNeed.getPeopleid()!=-1) {
                assignedToValue = peopleDAO.getPeopleName(jobNeed.getPeopleid());
            }
            ((TextView) findViewById(R.id.item_assginedto)).setText(assignedToValue);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.performFabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(TaskDetailsActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));

                if (accessValue == 0) {

                    if (jobNeed != null) {
                        System.out.println("jobStatus: " + jobStatus);
                        System.out.println("isJobExpired: " + isJobExpired);
                        if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                            Snackbar.make(view, getResources().getString(R.string.job_has_closed), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        else if (jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                            Snackbar.make(view, getResources().getString(R.string.job_has_completed), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        else if (isJobExpired == 2)
                            Snackbar.make(view, getResources().getString(R.string.job_has_expired), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        else if (isJobExpired == 0)
                            Snackbar.make(view, getResources().getString(R.string.job_is_future, jobNeed.getPlandatetime()), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        else if (isJobExpired == 1) {
                            if (jobNeedDetailsHistoryArrayList != null && jobNeedDetailsHistoryArrayList.size() > 0)
                                customAlertDialog.customButtonAlertBox(getResources().getString(R.string.button_start), getResources().getString(R.string.button_cancel), getResources().getString(R.string.joblist_startjob_title) + " " + jobNeed.getJobdesc() + "?", "JOB", 0);
                            else
                                Snackbar.make(view, getResources().getString(R.string.jobneeddetails_detailsnotfound), Snackbar.LENGTH_LONG).show();
                        }
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
                } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                    System.out.println("===========lat long==0.0");
                }

            }
        });

        FloatingActionButton fabReply=(FloatingActionButton)findViewById(R.id.replyFabButton);
        fabReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(TaskDetailsActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));

                if (accessValue == 0) {
                    if (jobNeed != null) {
                    /*if(jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                        Snackbar.make(view, getResources().getString(R.string.job_has_closed), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else if(jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                        Snackbar.make(view, getResources().getString(R.string.job_has_completed), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else if(isJobExpired==2)
                        Snackbar.make(view, getResources().getString(R.string.job_has_expired), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else if(isJobExpired==0)
                        Snackbar.make(view, getResources().getString(R.string.job_is_future, jobNeed.getPlandatetime()), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else if(isJobExpired==1)
                    {
                        Intent replyIntent = new Intent(TaskDetailsActivity.this, JobneedReplyActivity.class);
                        replyIntent.putExtra("REPLY_TIMESTAMP", System.currentTimeMillis());
                        replyIntent.putExtra("JOBNEEDID", jobNeed.getJobneedid());
                        startActivityForResult(replyIntent, 1);
                    }*/
                        Intent replyIntent = new Intent(TaskDetailsActivity.this, JobneedReplyActivity.class);
                        replyIntent.putExtra("REPLY_TIMESTAMP", System.currentTimeMillis());
                        replyIntent.putExtra("JOBNEEDID", jobNeed.getJobneedid());
                        startActivityForResult(replyIntent, 1);
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
                } else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                    customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                    System.out.println("===========lat long==0.0");
                }
            }
        });
    }

    private void prepareAttachmentDownloadData(long jnId)
    {
        final ProgressDialog progressDialog = new ProgressDialog(TaskDetailsActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show(); // show progress dialog


        String queryInfo=getResources().getString(R.string.jobneedattachment_query,jnId);

        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(Constants.SERVICE_SELECT);
        uploadParameters.setQuery(queryInfo);
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setTzoffset(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
        uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
        uploadParameters.setLoginid(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
        uploadParameters.setPassword(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
        //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
        uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

        Gson gson=new Gson();

        System.out.println("UploadParameter: "+gson.toJson(uploadParameters));

        RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
        Call<ResponseData> call=retrofitServices.getServerResponse(Constants.SERVICE_SELECT,uploadParameters);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseData> call, @NonNull Response<ResponseData> response) {
                progressDialog.dismiss();
                Type listType;
                if(response.isSuccessful() && response.body()!=null)
                {
                    System.out.println("response.getStatus(): "+response.body().getStatus());
                    System.out.println("response.getRow_data(): "+response.body().getRow_data());
                    System.out.println("response.getNrow(): "+response.body().getNrow()+"");
                    System.out.println("response.getRc(): "+response.body().getRc());
                    System.out.println("response.getColumns(): "+response.body().getColumns());
                    try {
                        if(response.body().getNrow()>0)
                        {
                            String mainSplitRowChar = String.valueOf(response.body().getRow_data().charAt(0));
                            String mainSplitColumnChar=String.valueOf(response.body().getColumns().charAt(0));

                            if (mainSplitRowChar.trim().equalsIgnoreCase("|")) {
                                mainSplitRowChar = "\\|";
                            } else if (mainSplitRowChar.trim().equalsIgnoreCase("$")) {
                                mainSplitRowChar = "\\$";
                            }

                            if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                                mainSplitColumnChar = "\\|";
                            } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                                mainSplitColumnChar = "\\$";
                            }

                            String[] responseSplit = response.body().getRow_data().split(mainSplitRowChar);
                            String[] cols=response.body().getColumns().split(mainSplitColumnChar);
                            System.out.println("Length: " + responseSplit.length);

                            JSONArray dataArray = new JSONArray();
                            JSONObject dataObject = new JSONObject();


                            for (int i = 1; i < (responseSplit.length); i++) {
                                System.out.println("split string: " + responseSplit[i]);
                                //System.out.println("split string number: "+i);
                                if (responseSplit[i].trim().length() > 0) {
                                    Character startDelimitor = responseSplit[i].charAt(0);
                                    System.out.println("Start Delimeter: " + startDelimitor);
                                    String[] respRow = null;
                                    if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                        respRow = responseSplit[i].trim().split("\\$");
                                    } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                        respRow = responseSplit[i].trim().split("\\|");
                                    } else {
                                        respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                                    }


                                    if(respRow!=null && respRow.length>0) {
                                        JSONObject jsonObject = new JSONObject();
                                        for (int c = 1; c < respRow.length; c++) {
                                            jsonObject.put(cols[c], respRow[c]);
                                        }
                                        dataArray.put(jsonObject);
                                    }


                                }

                            }

                            dataObject.put("Data", dataArray);
                            Gson gson = new Gson();
                            JSONArray data1 = dataObject.getJSONArray("Data");
                            listType = new TypeToken<ArrayList<JobNeedHistory>>() {
                            }.getType();
                            ArrayList<JobNeedHistory> jobNeedHistoryArrayList=new ArrayList<>();
                            jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);

                            if(jobNeedHistoryArrayList!=null && jobNeedHistoryArrayList.size()>0)
                                showAttachmentAlertDialog(jobNeedHistoryArrayList);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
            }
        });


    }

    private void showAttachmentAlertDialog(ArrayList<JobNeedHistory> jobNeedHistories)
    {
        ArrayList<String> attachmentName=new ArrayList<>();

        for (JobNeedHistory str : jobNeedHistories) {
            attachmentName.add(str.getFilename());
        }

        CharSequence[] cs = attachmentName.toArray(new CharSequence[attachmentName.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attachments");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void showAlertDialog()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(TaskDetailsActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View convertView=inflater.inflate(R.layout.dialog_listview,null);
        alertDialog.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setView(convertView);
        ListView dialogView=(ListView)convertView.findViewById(R.id.dialogListView);
        final AlertDialog dialog=alertDialog.create();
        dialog.setTitle(getResources().getString(R.string.quetsetdetails_title));
        AlertDialogAdapter alertDialogAdapter=new AlertDialogAdapter(TaskDetailsActivity.this, R.layout.task_history_reading_row,jobNeedDetailsHistoryArrayList);
        dialogView.setAdapter(alertDialogAdapter);
        dialog.show();
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==0)
        {
            System.out.println("Scan Type: "+typeAssistDAO.getEventTypeCode(jobNeed.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_QR));
            if (typeAssistDAO.getEventTypeCode(jobNeed.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_QR)) {
                Intent intent = new Intent(TaskDetailsActivity.this, CaptureActivity.class);
                intent.putExtra("FROM", "JOB");
                startActivityForResult(intent, 2);
            } else if (typeAssistDAO.getEventTypeCode(jobNeed.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_NFC)) {
                Intent i = new Intent(TaskDetailsActivity.this, NFCCodeReaderActivity.class);
                startActivityForResult(i, 3);
            } else if (typeAssistDAO.getEventTypeCode(jobNeed.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_ENTERED)) {
                customAlertDialog.custEnteredScanType(getResources().getString(R.string.joblist_enterassetcode), getResources().getString(R.string.joblist_enterassetcode),1);
            } else {
                jobNeedDAO.updateJobNeedStartTime(jobNeed.getJobneedid());
                CommonFunctions.manualSyncEventLog("JOB_PERFORM_START","JOBNEEDID: "+jobNeed.getJobneedid(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

                Intent ii = new Intent(TaskDetailsActivity.this, IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "JOB");
                ii.putExtra("ID", jobNeed.getJobneedid());
                ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
                ii.putExtra("FOLDER", typeAssistDAO.getEventTypeCode(jobNeed.getIdentifier()));
                startActivityForResult(ii, 0);
            }
        }
        else if(type==1)
        {
            long assetIDFrmDB=assetDAO.getAssetID(errorMessage);

            if(assetIDFrmDB==jobNeed.getAssetid()) {

                jobNeedDAO.updateJobNeedStartTime(jobNeed.getJobneedid());
                CommonFunctions.manualSyncEventLog("JOB_PERFORM_START","JOBNEEDID: "+jobNeed.getJobneedid(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

                Intent ii = new Intent(TaskDetailsActivity.this, IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "JOB");
                ii.putExtra("ID", jobNeed.getJobneedid());
                ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
                ii.putExtra("FOLDER", typeAssistDAO.getEventTypeCode(jobNeed.getIdentifier()));
                startActivityForResult(ii, 0);
            }
            else
                Snackbar.make(questionSetTextview,getResources().getString(R.string.joblist_assetcodemismatched), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            if(resultCode==RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
        }
        else if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {

            }
        }
        else if(requestCode==2)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null) {

                    String assetCode=data.getStringExtra("SCAN_RESULT");
                    CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_QR,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    CommonFunctions.manualSyncEventLog("QRScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    validateAssetCode(assetCode);
                }
            }
        }
        else if(requestCode==3)
        {
            if(resultCode==RESULT_OK)
            {
                if(resultCode==RESULT_OK)
                {
                    String assetCode=data.getStringExtra("SCAN_RESULT");
                    CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_NFC,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    CommonFunctions.manualSyncEventLog("NFCScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    validateAssetCode(assetCode);
                }
            }
        }
    }

    private void validateAssetCode(String scanAssetCode)
    {
        long assetIDFrmDB=assetDAO.getAssetID(scanAssetCode);
        if(assetIDFrmDB==jobNeed.getAssetid()) {

            jobNeedDAO.updateJobNeedStartTime(jobNeed.getJobneedid());
            CommonFunctions.manualSyncEventLog("JOB_PERFORM_START","JOBNEEDID: "+jobNeed.getJobneedid(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

            Intent ii = new Intent(TaskDetailsActivity.this, IncidentReportQuestionActivity.class);
            ii.putExtra("FROM", "JOB");
            ii.putExtra("ID", jobNeed.getJobneedid());
            ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
            ii.putExtra("FOLDER", typeAssistDAO.getEventTypeCode(jobNeed.getIdentifier()));
            startActivityForResult(ii, 0);
        }
        else
        {
            Snackbar.make(questionSetTextview,getResources().getString(R.string.joblist_assetcodemismatched), Snackbar.LENGTH_LONG).show();
        }
    }

    private class ViewHolder
    {
        TextView srno;
        TextView questName;
        TextView questAns;
    }

    private class AlertDialogAdapter extends ArrayAdapter<JobNeedDetails>
    {
        LayoutInflater inflater;
        Context mContext;
        List<JobNeedDetails> mjobNeedDetails;



        public AlertDialogAdapter(@NonNull Context context, int resource, @NonNull List<JobNeedDetails> jobNeedDetails) {
            super(context, resource, jobNeedDetails);
            mContext=context;
            mjobNeedDetails=jobNeedDetails;
            inflater=LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView==null)
            {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.task_history_reading_row, null);
                viewHolder.srno = (TextView) convertView.findViewById(R.id.srNoTextView);
                viewHolder.questName = (TextView) convertView.findViewById(R.id.questNameTextView);
                viewHolder.questAns = (TextView) convertView.findViewById(R.id.questAnsTextView);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder) convertView.getTag();
            }

            viewHolder.srno.setText(String.valueOf(mjobNeedDetails.get(position).getSeqno()));
            viewHolder.questName.setText(questionDAO.getQuestionName(mjobNeedDetails.get(position).getQuestionid()));
            if (mjobNeedDetails.get(position).getAnswer() != null && !mjobNeedDetails.get(position).getAnswer().equalsIgnoreCase("null"))
                viewHolder.questAns.setText(getResources().getString(R.string.readingdetail_answer, mjobNeedDetails.get(position).getAnswer()));
            else
                viewHolder.questAns.setText(getResources().getString(R.string.readingdetail_answer, "---"));

            return  convertView;
        }
    }


}
