package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedHistory;
import com.youtility.intelliwiz20.Model.ResponseData;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailsActivity extends AppCompatActivity implements IDialogEventListeners, View.OnClickListener {
    private long jobneedid=-1;
    private JobNeed jobNeed;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleDAO peopleDAO;
    private GroupDAO groupDAO;
    private AssetDAO assetDAO;
    private AttachmentDAO attachmentDAO;
    private TextView attachmentCountTextView;
    private CheckNetwork checkNetwork;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationPref;
    private ArrayList<JobNeedHistory> jobNeedHistoryArrayList;
    private FloatingActionButton replyFButton;
    private LinearLayout dialogReplyLinearLayout;
    private TicketHistoryAlertDialogAdapter adapter;
    private RecyclerView mRecyclerView;
    private Spinner replyStatusSpinner;
    private EditText replyEdittext;
    private Button replyCancelButton, replyOkButton;
    private ArrayList<TypeAssist>statusList;
    private ArrayList<String>statusNameList;
    private long replyTimestamp=-1;
    private CustomAlertDialog customAlertDialog;
    private Button showAttachment;
    private ImageView addPictureIV, addAudioIV, addVideoIV;
    private TextView addAttachmentCountView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);

        jobNeedDAO=new JobNeedDAO(TicketDetailsActivity.this);
        typeAssistDAO=new TypeAssistDAO(TicketDetailsActivity.this);
        peopleDAO=new PeopleDAO(TicketDetailsActivity.this);
        groupDAO=new GroupDAO(TicketDetailsActivity.this);
        assetDAO=new AssetDAO(TicketDetailsActivity.this);
        attachmentDAO=new AttachmentDAO(TicketDetailsActivity.this);
        checkNetwork=new CheckNetwork(TicketDetailsActivity.this);

        replyTimestamp=System.currentTimeMillis();

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);
        applicationPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF, MODE_PRIVATE);

        customAlertDialog=new CustomAlertDialog(TicketDetailsActivity.this, this);

        statusList=typeAssistDAO.getEventList("Ticket Status");
        statusNameList=new ArrayList<>();
        for(int i=0;i<statusList.size();i++)
        {
            if(statusList.get(i).getTaid()!=-1)
            {
                if(statusList.get(i).getTacode().equalsIgnoreCase(Constants.TICKET_STATUS_OPEN))
                    statusNameList.add(getResources().getString(R.string.ticket_custom_open_reply));
                else if(statusList.get(i).getTacode().equalsIgnoreCase(Constants.TICKET_STATUS_CANCELLED))
                    statusNameList.add(getResources().getString(R.string.ticket_custom_cancelled));
                else if(statusList.get(i).getTacode().equalsIgnoreCase(Constants.TICKET_STATUS_RESOLVED))
                    statusNameList.add(getResources().getString(R.string.ticket_custom_complete));
                else if(statusList.get(i).getTacode().equalsIgnoreCase(Constants.TICKET_STATUS_NEW))
                    statusNameList.add(getResources().getString(R.string.ticket_custom_new));
                else if(statusList.get(i).getTacode().equalsIgnoreCase(Constants.TICKET_STATUS_ESCALATED))
                    statusNameList.add(getResources().getString(R.string.ticket_custom_escalated));
            }
        }

        jobneedid=getIntent().getLongExtra("JOBNEEDID",-1);
        if(jobneedid!=-1) {
            jobNeed=new JobNeed();
            jobNeed=jobNeedDAO.getJobNeedDetails(jobneedid);
        }
        replyFButton=(FloatingActionButton)findViewById(R.id.replyFabButton);
        replyFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonFunctions.isPermissionGranted(TicketDetailsActivity.this))
                {
                    if(jobNeed!=null && jobNeed.getJobneedid()!=-1)
                    {
                        if(typeAssistDAO.getEventTypeName(jobNeed.getJobstatus()).equalsIgnoreCase(Constants.TICKET_STATUS_RESOLVED))
                        {
                            Snackbar.make(replyFButton,getResources().getString(R.string.ticketdetails_ticketresolved),Snackbar.LENGTH_LONG).show();
                        }
                        else
                        {
                            if(dialogReplyLinearLayout.getVisibility()==View.VISIBLE)
                                dialogReplyLinearLayout.setVisibility(View.GONE);
                            else if(dialogReplyLinearLayout.getVisibility()==View.GONE)
                                dialogReplyLinearLayout.setVisibility(View.VISIBLE);
                        }
                    }

                }
                else
                {
                    Toast.makeText(TicketDetailsActivity.this,getResources().getString(R.string.error_msg_grant_permission),Toast.LENGTH_LONG).show();
                }

            }
        });


        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view_reply_history);
        dialogReplyLinearLayout=(LinearLayout)findViewById(R.id.dialogReplyLinearLayout);

        //--------------------------reply view related----------------------------------
        showAttachment=(Button)findViewById(R.id.showAttachments);
        showAttachment.setOnClickListener(this);
        addAudioIV=(ImageView)findViewById(R.id.addAudioIV);
        addAudioIV.setOnClickListener(this);
        addVideoIV=(ImageView)findViewById(R.id.addVideoIV);
        addVideoIV.setOnClickListener(this);
        addPictureIV=(ImageView)findViewById(R.id.addPictureIV);
        addPictureIV.setOnClickListener(this);
        addAttachmentCountView=(TextView)findViewById(R.id.badge_attachment_count_notification);
        //--------------------------------------------------------------------------------

        if(jobNeed!=null)
        {
            ((TextView) findViewById(R.id.item_code)).setText(String.valueOf(jobNeed.getTicketno()));
            ((TextView) findViewById(R.id.item_description)).setText(jobNeed.getJobdesc());
            ((TextView) findViewById(R.id.item_plandate)).setText((jobNeed.getCdtz()));
            ((TextView) findViewById(R.id.item_tCategory)).setText(typeAssistDAO.getEventTypeName(jobNeed.getTicketcategory()));
            ((TextView) findViewById(R.id.item_status)).setText(typeAssistDAO.getEventTypeName(jobNeed.getJobstatus()));
            ((TextView) findViewById(R.id.item_assginedby)).setText(peopleDAO.getPeopleName(jobNeed.getCuser()));
            ((TextView) findViewById(R.id.item_performby)).setText(peopleDAO.getPeopleName(jobNeed.getPerformedby()));

            String assignedToValue=null;
            if(jobNeed.getGroupid()!=-1) {
                assignedToValue = groupDAO.getGroupName(jobNeed.getGroupid());
            }
            if(jobNeed.getPeopleid()!=-1) {
                assignedToValue = peopleDAO.getPeopleName(jobNeed.getPeopleid());
            }
            ((TextView) findViewById(R.id.item_assginedto)).setText(assignedToValue);

            if(jobNeed.getAssetid()!=-1)
            {
                ((TextView) findViewById(R.id.item_assetlocation)).setText(assetDAO.getTicketAssetLocation(jobNeed.getAssetid()));
            }

            attachmentCountTextView=(TextView)findViewById(R.id.item_attachmentcount);
            if(jobNeed.getAttachmentcount()!=null)
                attachmentCountTextView.setText(jobNeed.getAttachmentcount());
            else
                attachmentCountTextView.setText("0");
            attachmentCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkNetwork.isNetworkConnectionAvailable())
                    {
                        fetchReplyHistory(getResources().getString(R.string.jobneedattachment_query,jobNeed.getJobneedid()),1);//for attachment history

                    }
                    else
                    {
                        Snackbar.make(v,getResources().getString(R.string.check_internet_connection_msg), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            fetchReplyHistory(getResources().getString(R.string.jobneedhistory_query,jobNeed.getJobneedid(), "(select taid from typeassist where tacode='REPLY')"),0);

            replyStatusSpinner=(Spinner)findViewById(R.id.dialogStatusSpinner);
            replyEdittext=(EditText)findViewById(R.id.dialogRemarkEdittext);
            replyCancelButton=(Button)findViewById(R.id.replyCancelButton);
            replyOkButton=(Button)findViewById(R.id.replyOkButton);

            if(statusNameList!=null && statusNameList.size()>0) {
                ArrayAdapter statusAdpt = new ArrayAdapter(TicketDetailsActivity.this, android.R.layout.simple_spinner_item, statusNameList);
                statusAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                replyStatusSpinner.setAdapter(statusAdpt);
            }

            replyCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogReplyLinearLayout.setVisibility(View.GONE);
                    attachmentDAO.deleteAVP(replyTimestamp);
                    replyTimestamp=-1;
                }
            });

            replyOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonFunctions.isPermissionGranted(TicketDetailsActivity.this)) {
                        if (replyEdittext.getText().toString().trim().length() > 0) {
                            saveReply();
                            dialogReplyLinearLayout.setVisibility(View.GONE);
                            replyTimestamp = -1;
                        } else
                            replyEdittext.setError(getResources().getString(R.string.ticket_error_msg_remark));
                    }
                    else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

                }
            });

            String jobStatus=typeAssistDAO.getEventTypeName(jobNeed.getJobstatus());

            if(jobStatus.equalsIgnoreCase(Constants.TICKET_STATUS_NEW))
                customAlertDialog.customButtonAlertBox(getResources().getString(R.string.button_start),getResources().getString(R.string.button_cancel),getResources().getString(R.string.ticket_start_alert_msg), "Ticket",0);

        }
    }

    private void saveReply()
    {
        System.out.println("statusList.get(replyStatusSpinner.getSelectedItemPosition()).getTaid(): "+statusList.get(replyStatusSpinner.getSelectedItemPosition()).getTaid());
        System.out.println("replyStatusSpinner.getSelectedItemPosition(): "+replyStatusSpinner.getSelectedItemPosition());
        jobNeedDAO.updateJobNeedRecord(jobNeed.getJobneedid(),-1,-1, replyEdittext.getText().toString().trim(), statusList.get(replyStatusSpinner.getSelectedItemPosition()).getTaid(), loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));

        Attachment attachment=new Attachment();
        attachment.setAttachmentid((replyTimestamp));
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_REPLY, Constants.IDENTIFIER_ATTACHMENT));
        attachment.setFilePath(null);
        attachment.setFileName(null);
        attachment.setNarration(replyEdittext.getText().toString().trim());
        attachment.setGpslocation("19,19");
        attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setOwnerid(jobNeed.getJobneedid());
        attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_JOBNEED, Constants.IDENTIFIER_OWNER));
        attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

        attachmentDAO.insertCommonRecord(attachment);

        jobNeedDAO.getJobNeedDetails(jobNeed.getJobneedid());
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void fetchReplyHistory(String queryInfo, final int val)
    {
        final ProgressDialog progressDialog = new ProgressDialog(TicketDetailsActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
        progressDialog.show(); // show progress dialog

        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(Constants.SERVICE_SELECT);
        uploadParameters.setQuery(queryInfo);
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setTzoffset(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
        uploadParameters.setSitecode(applicationPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
        uploadParameters.setLoginid(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
        uploadParameters.setPassword(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
        //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
        uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

        Gson gson=new Gson();
        System.out.println("uploadParameters: "+gson.toJson(uploadParameters));
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

                            if(val==1)
                            {
                                showAttachmentAlertDialog(jobNeedHistoryArrayList);
                            }
                            else {
                                if (jobNeedHistoryArrayList != null && jobNeedHistoryArrayList.size() > 0) {
                                    adapter = new TicketHistoryAlertDialogAdapter(jobNeedHistoryArrayList);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TicketDetailsActivity.this);
                                    mRecyclerView.setLayoutManager(layoutManager);
                                    mRecyclerView.setAdapter(adapter);
                                }
                            }

                        }
                        else
                        {
                            if(val==0)
                                Snackbar.make(replyFButton,getResources().getString(R.string.ticketdetails_replyhistorynotfound),Snackbar.LENGTH_LONG).show();
                            else if(val==1)
                                Snackbar.make(replyFButton,getResources().getString(R.string.ticketdetails_attachmenthistorynotfound),Snackbar.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseData> call, Throwable t) {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(TicketDetailsActivity.this);
        builder.setTitle("Attachments");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        jobNeedDAO.changeJobStartTime(jobNeed.getJobneedid(), typeAssistDAO.getEventTypeID(Constants.TICKET_STATUS_OPEN, Constants.STATUS_TYPE_TICKET));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.MIC_RECORD_AUDIO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode== FragmentActivity.RESULT_OK)
            {
                System.out.println("Attachment Count: "+attachmentDAO.getAttachmentCount(jobNeed.getJobneedid(),replyTimestamp));
                addAttachmentCountView.setText(String.valueOf(attachmentDAO.getAttachmentCount(jobNeed.getJobneedid(),replyTimestamp)));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.showAttachments:
                callAttachmentIntent(3,3,AttachmentListActivity.class);
                break;
            case R.id.addAudioIV:
                callAttachmentIntent(Constants.ATTACHMENT_AUDIO,Constants.MIC_RECORD_AUDIO_REQUEST_CODE,MediaRecoderView.class);
                break;
            case R.id.addVideoIV:
                callAttachmentIntent(Constants.ATTACHMENT_VIDEO,Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE,VideoCaptureActivity.class);
                break;
            case R.id.addPictureIV:
                callAttachmentIntent(Constants.ATTACHMENT_PICTURE,Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE,CapturePhotoActivity.class);
                break;
        }
    }


    private void callAttachmentIntent(int aType, int requestCode,Class activityClassName)
    {

        Intent mediaRecoder= null;
        try {
            mediaRecoder = new Intent(TicketDetailsActivity.this,activityClassName);
            mediaRecoder.putExtra("FROM",aType);
            mediaRecoder.putExtra("TIMESTAMP",replyTimestamp);
            mediaRecoder.putExtra("JOBNEEDID",jobNeed.getJobneedid());
            mediaRecoder.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
            mediaRecoder.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TICKET);
            startActivityForResult(mediaRecoder, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class TicketHistoryAlertDialogAdapter extends RecyclerView.Adapter<TicketHistoryAlertDialogAdapter.ViewHolder>
    {
        private ArrayList<JobNeedHistory> jobNeedHistories;
        public TicketHistoryAlertDialogAdapter(ArrayList<JobNeedHistory> jobNeedHistories)
        {
            this.jobNeedHistories=jobNeedHistories;
        }

        @Override
        public TicketHistoryAlertDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.task_history_reading_row, parent, false);
            return new TicketHistoryAlertDialogAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TicketHistoryAlertDialogAdapter.ViewHolder holder, int position) {
            holder.txtSrNo.setText(String.valueOf(position+1)+": ");
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
