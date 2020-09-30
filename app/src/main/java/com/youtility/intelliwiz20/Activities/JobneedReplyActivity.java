package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.JobNeedHistory;
import com.youtility.intelliwiz20.Model.ResponseData;
import com.youtility.intelliwiz20.Model.TypeAssist;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobneedReplyActivity extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners {
    private EditText remarkEdittext;
    private Button cancelButton;
    private Button doneButton;
    private Spinner statusSpinner;
    private Button historyShowButton;//audioShowButton,videoShowButton, pictureShowButton, historyShowButton;
    private TextView replyHistoryCountView;//audioCountView, videoCountView, pictureCountView, replyHistoryCountView;
    private TextView attachmentCountView;
    private Button showAttachmentButton;
    private ImageView audioAddIV, videoAddIV, pictureAddIV;
    //String[] statusName={"ASSIGNED","INPROGRESS","COMPLETED","ARCHIVED","CLOSED"};
    private ArrayList<TypeAssist>statusList;
    private ArrayList<String>statusNameList;
    private LinearLayout taskHistoryLinearLayout;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationMainPref;
    private CheckNetwork checkNetwork;
    private final int AUDIO_INTENT=0;
    private final int VIDEO_INTENT=1;
    private final int PICTURE_INTENT=2;
    private long replyTimestamp=-1L;
    private long jobneedid=-1;
    ArrayList<JobNeedHistory> jobNeedHistoryArrayList;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AttachmentDAO attachmentDAO;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;

    private TaskHistoryAlertDialogAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobneed_reply);

        replyTimestamp=getIntent().getLongExtra("REPLY_TIMESTAMP",-1);
        jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        applicationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(JobneedReplyActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        jobNeedDAO=new JobNeedDAO(JobneedReplyActivity.this);
        typeAssistDAO=new TypeAssistDAO(JobneedReplyActivity.this);
        attachmentDAO=new AttachmentDAO(JobneedReplyActivity.this);

        checkNetwork=new CheckNetwork(JobneedReplyActivity.this);

        remarkEdittext=(EditText)findViewById(R.id.editTextDialogRemark);
        statusSpinner=(Spinner)findViewById(R.id.dialogStatusSpinner);
        cancelButton=(Button)findViewById(R.id.dialogCancelButton);
        doneButton=(Button)findViewById(R.id.dialogDoneButton);

        //historyShowButton=(Button)findViewById(R.id.showHistoryButton);
        showAttachmentButton=(Button)findViewById(R.id.showAttachments);


        statusList=typeAssistDAO.getEventList("Job Status");
        statusNameList=new ArrayList<>();
        for(int i=0;i<statusList.size();i++)
        {
            if(statusList.get(i).getTaid()!=-1)
            {
                statusNameList.add(statusList.get(i).getTaname());
            }
        }
        ArrayAdapter statusAdpt = new ArrayAdapter(JobneedReplyActivity.this,android.R.layout.simple_spinner_item,statusNameList);
        statusAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdpt);

        doneButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        //historyShowButton.setOnClickListener(this);
        showAttachmentButton.setOnClickListener(this);


        //replyHistoryCountView=(TextView)findViewById(R.id.badge_reply_count_notification);
        attachmentCountView=(TextView)findViewById(R.id.badge_attachment_count_notification);

        audioAddIV=(ImageView)findViewById(R.id.addAudioIV);
        videoAddIV=(ImageView)findViewById(R.id.addVideoIV);
        pictureAddIV=(ImageView)findViewById(R.id.addPictureIV);

        audioAddIV.setOnClickListener(this);
        videoAddIV.setOnClickListener(this);
        pictureAddIV.setOnClickListener(this);

        taskHistoryLinearLayout=(LinearLayout)findViewById(R.id.task_historyLinearLayout);

        /*GetJobNeedReplyHistoryAsynTask getJobNeedReplyHistoryAsynTask=new GetJobNeedReplyHistoryAsynTask();
        getJobNeedReplyHistoryAsynTask.execute();*/

        prepareReplyHistory();
    }

    private void prepareReplyHistory()
    {
        String queryInfo=getResources().getString(R.string.jobneedhistory_query,jobneedid, "(select taid from typeassist where tacode='REPLY')");

        final ProgressDialog progressDialog = new ProgressDialog(JobneedReplyActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
        progressDialog.show(); // show progress dialog

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

        RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
        Call<ResponseData> call=retrofitServices.getServerResponse(Constants.SERVICE_SELECT,uploadParameters);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseData> call, @NonNull Response<ResponseData> response) {
                progressDialog.dismiss();
                Type listType;
                if(response.isSuccessful() && response.body()!=null)
                {
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
                            jobNeedHistoryArrayList=new ArrayList<>();
                            jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);

                            if(jobNeedHistoryArrayList!=null && jobNeedHistoryArrayList.size()>0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //replyHistoryCountView.setText(String.valueOf(jobNeedHistoryArrayList.size()));
                                        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_reply_history);
                                        adapter = new TaskHistoryAlertDialogAdapter(jobNeedHistoryArrayList);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JobneedReplyActivity.this);
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(adapter);
                                    }
                                });

                            }

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

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(JobneedReplyActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
        System.out.println("===========" + accessValue);
        System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if (accessValue == 0 ) {
            switch (v.getId()) {
                case R.id.dialogCancelButton:
                    attachmentDAO.deleteAVP(replyTimestamp);
                    replyTimestamp = -1;
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.dialogDoneButton:
                    saveReply();
                    break;
                case R.id.showAttachments:
                    callIntent();
                    break;

            /*case R.id.showHistoryButton:
                //showReplyHistory();
                *//*if(jobNeedHistoryArrayList!=null && jobNeedHistoryArrayList.size()>0)
                    showAlertDialog();*//*
                break;*/
                case R.id.addAudioIV:
                    Intent mediaRecoder = new Intent(JobneedReplyActivity.this, MediaRecoderView.class);
                    mediaRecoder.putExtra("FROM", Constants.ATTACHMENT_AUDIO);
                    mediaRecoder.putExtra("TIMESTAMP", replyTimestamp);
                    mediaRecoder.putExtra("JOBNEEDID", jobneedid);
                    mediaRecoder.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    mediaRecoder.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(mediaRecoder, Constants.MIC_RECORD_AUDIO_REQUEST_CODE);
                    break;
                case R.id.addVideoIV:
                    Intent captureVideo = new Intent(JobneedReplyActivity.this, VideoCaptureActivity.class);
                    captureVideo.putExtra("FROM", Constants.ATTACHMENT_VIDEO);
                    captureVideo.putExtra("TIMESTAMP", replyTimestamp);
                    captureVideo.putExtra("JOBNEEDID", jobneedid);
                    captureVideo.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    captureVideo.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(captureVideo, Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                    break;
                case R.id.addPictureIV:
                    Intent capturePic = new Intent(JobneedReplyActivity.this, CapturePhotoActivity.class);
                    capturePic.putExtra("FROM", Constants.ATTACHMENT_PICTURE);
                    capturePic.putExtra("TIMESTAMP", replyTimestamp);
                    capturePic.putExtra("JOBNEEDID", jobneedid);
                    capturePic.putExtra("PARENT_ACTIVITY", Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                    capturePic.putExtra("FOLDER", Constants.JOB_NEED_IDENTIFIER_TASK);
                    startActivityForResult(capturePic, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
        Intent nexIntent=new Intent(JobneedReplyActivity.this, AttachmentListActivity.class);
        nexIntent.putExtra("FROM",0);
        nexIntent.putExtra("TIMESTAMP",replyTimestamp);
        nexIntent.putExtra("JOBNEEDID",jobneedid);
        startActivityForResult(nexIntent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.MIC_RECORD_AUDIO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                System.out.println("Attachment Count: "+attachmentDAO.getAttachmentCount(jobneedid,replyTimestamp));
                attachmentCountView.setText(String.valueOf(attachmentDAO.getAttachmentCount(jobneedid,replyTimestamp)));
            }
        }
    }

    private void saveReply()
    {
        if(remarkEdittext.getText().toString().trim().length()>0)
        {
            if(checkNetwork.isNetworkConnectionAvailable())
            {
                //jobNeedDAO.updateJobNeedRecord(jobneedid,-1,-1, remarkEdittext.getText().toString().trim(), typeAssistDAO.getEventTypeID(statusSpinner.getSelectedItem().toString().trim()), loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                jobNeedDAO.updateJobNeedRecord(jobneedid,-1,-1, remarkEdittext.getText().toString().trim(), statusList.get(statusSpinner.getSelectedItemPosition()).getTaid(), loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                Attachment attachment=new Attachment();
                attachment.setAttachmentid((replyTimestamp));
                attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_REPLY, Constants.IDENTIFIER_ATTACHMENT));
                attachment.setFilePath(null);
                attachment.setFileName(null);
                attachment.setNarration(remarkEdittext.getText().toString().trim());
                attachment.setGpslocation("19,19");
                attachment.setDatetime((CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                System.out.println("Timezonedate1: "+CommonFunctions.getDeviceTimezoneFormatDate(CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                //attachment.setDatetime((CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                System.out.println("Timezonedate2: "+(CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                attachment.setCdtz((CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                //attachment.setCdtz((CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                attachment.setMdtz((CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                //attachment.setMdtz((CommonFunctions.getTimezoneDate(System.currentTimeMillis())));
                //attachment.setIsdeleted("False");
                attachment.setOwnerid(jobneedid);
                attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_JOBNEED, Constants.IDENTIFIER_OWNER));
                attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

                attachmentDAO.insertCommonRecord(attachment);

                setResult(RESULT_OK);
                finish();
            }
            else
            {
                Toast.makeText(JobneedReplyActivity.this,getResources().getString(R.string.check_internet_connection_msg),Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            remarkEdittext.setError(getResources().getString(R.string.ticket_error_msg_remark));
        }
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }

    private class GetJobNeedReplyHistoryAsynTask extends AsyncTask<Void, Integer, Void>
    {
        MediaType JSON;
        OkHttpClient client;
        StringBuffer sb;

        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        private Type listType;
        public GetJobNeedReplyHistoryAsynTask()
        {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
            jobNeedHistoryArrayList=new ArrayList<JobNeedHistory>();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                //---------------------------------------------------------------------
                String queryInfo=getResources().getString(R.string.jobneedhistory_query,jobneedid, "(select taid from typeassist where tacode='REPLY')");

                ServerRequest serverRequest=new ServerRequest(JobneedReplyActivity.this);
                HttpResponse response=serverRequest.getReplyHistoryResponse(queryInfo.trim(), loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                                                                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                                                                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                                                                            loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                System.out.println("JOBHistory response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                if(response.getStatusLine().getStatusCode()==200)
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
                    System.out.println("SB JOBHistory: " + sb.toString());
                    response.getEntity().consumeContent();


                    JSONObject ob = new JSONObject(sb.toString());
                    int status = ob.getInt(Constants.RESPONSE_RC);
                    int nrow = ob.getInt(Constants.RESPONSE_NROW);
                    if(status==0 && nrow>0)
                    {
                        String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                        String colums=ob.getString(Constants.RESPONSE_COLUMNS);
                        System.out.println("status: " + status);
                        System.out.println("response: " + resp);
                        if(resp.trim().length()>0)
                        {
                            String mainSplitRowChar = String.valueOf(resp.charAt(0));
                            String mainSplitColumnChar=String.valueOf(colums.charAt(0));
                            System.out.println("Starting split char: " + resp.charAt(0));

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

                            String[] responseSplit = resp.split(mainSplitRowChar);
                            String[] cols=colums.split(mainSplitColumnChar);
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
                            jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);



                        }
                    }

                }
                else
                {
                    System.out.println("SB1 JOBHistory: "+response.getStatusLine().getStatusCode());
                }

                //-------------------------------------------------------------------------------------------------------

                /*URL url = new URL(Constants.BASE_URL); // here is your URL path

                UploadParameters uploadParameters=new UploadParameters();
                uploadParameters.setServicename(Constants.SERVICE_SELECT);
                uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);

                String queryInfo=getResources().getString(R.string.jobneedhistory_query,jobneedid, "(select taid from typeassist where tacode='REPLY')");

                uploadParameters.setQuery(queryInfo);

                Gson gson = new Gson();
                String upData = gson.toJson(uploadParameters);
                System.out.println("upData: "+upData);

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
                byteread = 0;
                while ((byteread = is.read(buffer)) != -1)
                {
                    out.write(buffer, 0, byteread);
                }
                out.flush();
                out.close();
                is.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    System.out.println("SB: "+sb.toString());

                }
                else {
                    System.out.println("SB: "+responseCode);
                }

                JSONObject ob = new JSONObject(sb.toString());

                int status = ob.getInt(Constants.RESPONSE_RC);
                int nrow = ob.getInt(Constants.RESPONSE_NROW);
                if(status==0 && nrow>0)
                {
                    String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                    String colums=ob.getString(Constants.RESPONSE_COLUMNS);
                    System.out.println("status: " + status);
                    System.out.println("response: " + resp);
                    if(resp.trim().length()>0)
                    {
                        String mainSplitRowChar = String.valueOf(resp.charAt(0));
                        String mainSplitColumnChar=String.valueOf(colums.charAt(0));
                        System.out.println("Starting split char: " + resp.charAt(0));

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

                        String[] responseSplit = resp.split(mainSplitRowChar);
                        String[] cols=colums.split(mainSplitColumnChar);
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

                        JSONArray data1 = dataObject.getJSONArray("Data");
                        listType = new TypeToken<ArrayList<JobNeedHistory>>() {
                        }.getType();
                        jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);



                    }
                }*/


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if(jobNeedHistoryArrayList!=null && jobNeedHistoryArrayList.size()>0)
            {
                replyHistoryCountView.setText(String.valueOf(jobNeedHistoryArrayList.size()));
            }
        }

    }

    private void showReplyHistory()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        TextView textSrNo;
        TextView textQuestName;
        TextView textQuestAns;

        if(taskHistoryLinearLayout.getChildCount()>0)
            taskHistoryLinearLayout.removeAllViews();

        for(int i=0;i<jobNeedHistoryArrayList.size();i++)
        {
            v = inflater.inflate(R.layout.task_history_reading_row, null);
            textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
            textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
            textQuestAns=(TextView) v.findViewById(R.id.questAnsTextView);

            textSrNo.setText(String.valueOf((i+1)));
            textQuestName.setText(jobNeedHistoryArrayList.get(i).getNarration());

            try {
                if(jobNeedHistoryArrayList.get(i).getDatetime()!=null && !jobNeedHistoryArrayList.get(i).getDatetime().equalsIgnoreCase("None")) {
                    /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date parsedDate = dateFormat.parse(jobNeedHistoryArrayList.get(i).getDatetime());*/
                    /*System.out.println(CommonFunctions.getDeviceTimezoneFormatDate(CommonFunctions.getFormatedDate(String.valueOf(parsedDate.getTime()))));
                    textQuestAns.setText(CommonFunctions.getDeviceTimezoneFormatDate(CommonFunctions.getFormatedDate(String.valueOf(parsedDate.getTime()))));*/

                    System.out.println("Parsed date: "+CommonFunctions.getDeviceTimezoneFormatDate(jobNeedHistoryArrayList.get(i).getDatetime()));
                    textQuestAns.setText(CommonFunctions.getDeviceTimezoneFormatDate(jobNeedHistoryArrayList.get(i).getDatetime()));

                    /*System.out.println("Parsed date: "+(jobNeedHistoryArrayList.get(i).getDatetime()));
                    textQuestAns.setText((jobNeedHistoryArrayList.get(i).getDatetime()));*/
                }
                else
                {
                    textQuestAns.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            taskHistoryLinearLayout.addView(v);
        }
    }



   public class TaskHistoryAlertDialogAdapter extends RecyclerView.Adapter<TaskHistoryAlertDialogAdapter.ViewHolder>
   {
       private ArrayList<JobNeedHistory> jobNeedHistories;
       public TaskHistoryAlertDialogAdapter(ArrayList<JobNeedHistory> jobNeedHistories)
        {
            this.jobNeedHistories=jobNeedHistories;
        }

       @Override
       public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
           View view = layoutInflater.inflate(R.layout.task_history_reading_row, parent, false);
           return new ViewHolder(view);
       }

       @Override
       public void onBindViewHolder(ViewHolder holder, int position) {
           holder.txtSrNo.setText((position+1)+"");
           holder.txtQName.setText(jobNeedHistories.get(position).getNarration());
           holder.txtQAns.setText(CommonFunctions.getDeviceTimezoneFormatDate(jobNeedHistories.get(position).getDatetime()));
       }

       @Override
       public int getItemCount() {
           return jobNeedHistories.size();
       }

       class ViewHolder extends RecyclerView.ViewHolder {

           TextView txtSrNo, txtQName, txtQAns;

           ViewHolder(View itemView) {
               super(itemView);
               txtSrNo = (TextView) itemView.findViewById(R.id.srNoTextView);
               txtQName = (TextView) itemView.findViewById(R.id.questNameTextView);
               txtQAns = (TextView) itemView.findViewById(R.id.questAnsTextView);
           }
       }
   }
}
