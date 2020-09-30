package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

/*
import com.google.android.gms.vision.text.Text;
*/
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
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
import com.youtility.intelliwiz20.Utils.ServerRequest;

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

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link HelpDeskListActivity}
 * in two-pane mode (on tablets) or a {@link HelpDeskDetailActivity}
 * on handsets.
 */
public class HelpDeskDetailFragment extends Fragment implements View.OnClickListener, IDialogEventListeners {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    ArrayList<JobNeedDetails> jobNeedDetailsArrayList;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private TypeAssistDAO typeAssistDAO;
    private PeopleDAO peopleDAO;
    private GroupDAO groupDAO;
    private JobNeedDAO jobNeedDAO;
    private AttachmentDAO attachmentDAO;
    private AssetDAO assetDAO;
    private TextView attachmentCountTextView;
    private CheckNetwork checkNetwork;
    ArrayList<JobNeedHistory> jobNeedHistoryArrayList;
    ArrayList<JobNeedHistory> jobNeedAttachmentHistoryArrayList;
    private TextView replyHistoryCountView;
    private Button helpDeskReplyHistoryButton;
    private LinearLayout ticketHistoryLinearLayout;
    private int isReplyHistory=0;
    private String extStorageDirectory="";
    private Boolean isSDPresent=false;
    private LinearLayout replyDialogLinearlayout;
    private Spinner replyStatusSpinner;
    private EditText replyEdittext;
    private TextView assetLocationTextview;
    private Button replyCancelButton, replyOkButton;
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private long replyTimestamp=-1;
    //String[] statusName={"ASSIGNED","INPROGRESS","COMPLETED","ARCHIVED","CLOSED"};
    private ArrayList<TypeAssist>statusList;
    private ArrayList<String>statusNameList;
    private boolean isShowHide=false;

    private Button showAttachment;
    private ImageView addPictureIV, addAudioIV, addVideoIV;
    private TextView addAttachmentCountView;
    private CustomAlertDialog customAlertDialog;

    private TicketHistoryAlertDialogAdapter adapter;
    private RecyclerView recyclerView;


    private ScrollView scrollView;
    int height;

    /**
     * The dummy content this fragment is presenting.
     */
    private JobNeed mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HelpDeskDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = HelpDeskListActivity.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mItem.getJobneedid()+"");
                appBarLayout.setTitle(getResources().getString(R.string.ticket_list_row_ticketno,mItem.getTicketno()));
            }

            jobNeedDetailsDAO=new JobNeedDetailsDAO(getActivity());
            typeAssistDAO=new TypeAssistDAO(getActivity());
            peopleDAO=new PeopleDAO(getActivity());
            groupDAO=new GroupDAO(getActivity());
            jobNeedDAO=new JobNeedDAO(getActivity());
            attachmentDAO=new AttachmentDAO(getActivity());
            assetDAO=new AssetDAO(getActivity());

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

            loginDetailPref=getActivity().getSharedPreferences(Constants.LOGIN_PREFERENCE,Context.MODE_PRIVATE);
            deviceRelatedPref=getActivity().getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);

            isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.helpdesk_item_detail, container, false);
        checkNetwork=new CheckNetwork(getActivity());
        customAlertDialog=new CustomAlertDialog(getActivity(),this);
        String assignedToValue=null;

        /*WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        height = metrics.heightPixels;

        System.out.println("Screen w and h: "+width+" : "+height);*/

        scrollView=(ScrollView)rootView.findViewById(R.id.scrollView);


        showAttachment=(Button)rootView.findViewById(R.id.showAttachments);
        showAttachment.setOnClickListener(this);
        addAudioIV=(ImageView)rootView.findViewById(R.id.addAudioIV);
        addAudioIV.setOnClickListener(this);
        addVideoIV=(ImageView)rootView.findViewById(R.id.addVideoIV);
        addVideoIV.setOnClickListener(this);
        addPictureIV=(ImageView)rootView.findViewById(R.id.addPictureIV);
        addPictureIV.setOnClickListener(this);
        addAttachmentCountView=(TextView)rootView.findViewById(R.id.badge_attachment_count_notification);

        recyclerView=rootView.findViewById(R.id.recycler_view_reply_history);

        ticketHistoryLinearLayout=(LinearLayout)rootView.findViewById(R.id.helpdesk_historyLinearLayout);
        replyDialogLinearlayout=(LinearLayout)rootView.findViewById(R.id.dialogReplyLinearLayout);
        replyDialogLinearlayout.setVisibility(View.GONE);

        replyStatusSpinner=(Spinner)rootView.findViewById(R.id.dialogStatusSpinner);
        replyEdittext=(EditText)rootView.findViewById(R.id.dialogRemarkEdittext);
        replyCancelButton=(Button)rootView.findViewById(R.id.replyCancelButton);
        replyOkButton=(Button)rootView.findViewById(R.id.replyOkButton);

        //replyHistoryCountView=(TextView)rootView.findViewById(R.id.badge_reply_count_notification);

        if(statusNameList!=null && statusNameList.size()>0) {
            ArrayAdapter statusAdpt = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, statusNameList);
            statusAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            replyStatusSpinner.setAdapter(statusAdpt);
        }

        replyCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyDialogLinearlayout.setVisibility(View.GONE);
                attachmentDAO.deleteAVP(replyTimestamp);
                replyTimestamp=-1;
            }
        });

        replyOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(CommonFunctions.isPermissionGranted(getActivity())) {
                        if (replyEdittext.getText().toString().trim().length() > 0) {
                            saveReply();
                            replyDialogLinearlayout.setVisibility(View.GONE);
                            replyTimestamp = -1;
                        } else
                            replyEdittext.setError(getResources().getString(R.string.ticket_error_msg_remark));
                    }
                    else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

            }
        });

        ((Button)rootView.findViewById(R.id.helpDesKReplyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("JObStatus id: "+mItem.getJobstatus());
                System.out.println("JObStatus code: "+typeAssistDAO.getEventTypeCode(mItem.getJobstatus()));

                /*if((typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase("COMPLETED")))
                {
                    Snackbar.make(replyCancelButton, "Ticket has been "+typeAssistDAO.getEventTypeName(mItem.getJobstatus()), Snackbar.LENGTH_LONG).show();
                }
                else if((typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase("CANCELLED")))
                {
                    Snackbar.make(replyCancelButton, "Ticket has been "+typeAssistDAO.getEventTypeName(mItem.getJobstatus()), Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    replyDialogLinearlayout.setVisibility(View.VISIBLE);
                    replyTimestamp = System.currentTimeMillis();
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }*/

                replyDialogLinearlayout.setVisibility(View.VISIBLE);
                replyTimestamp = System.currentTimeMillis();
                scrollView.fullScroll(ScrollView.FOCUS_UP);


            }
        });

        //removed history button
        /*((Button)rootView.findViewById(R.id.helpDeskReplyHistoryButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*isReplyHistory=0;
                if(!isShowHide) {
                    isShowHide=true;
                    showReplyHistory();

                }
                else
                {
                    isShowHide=false;
                    hideReplyHistory();
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }*//*

                //fetchReplyHistory();
            }
        });*/


        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.item_ticketno)).setText(mItem.getTicketno()+"");
            //((TextView) rootView.findViewById(R.id.item_code)).setText(mItem.getJobneedid()+"");
            ((TextView) rootView.findViewById(R.id.item_description)).setText(mItem.getJobdesc());

            /*if(CommonFunctions.isLong(mItem.getPlandatetime()))
            {
                ((TextView) rootView.findViewById(R.id.item_plandate)).setText(CommonFunctions.getFormatedDate(mItem.getPlandatetime()));
            }
            else
            {
                ((TextView) rootView.findViewById(R.id.item_plandate)).setText((mItem.getPlandatetime()));
            }*/


            ((TextView) rootView.findViewById(R.id.item_plandate)).setText((mItem.getPlandatetime()));

            ((TextView) rootView.findViewById(R.id.item_status)).setText(typeAssistDAO.getEventTypeName(mItem.getJobstatus()));
            ((TextView) rootView.findViewById(R.id.item_tCategory)).setText(typeAssistDAO.getEventTypeName(mItem.getTicketcategory()));
            ((TextView) rootView.findViewById(R.id.item_assginedby)).setText(peopleDAO.getPeopleName(mItem.getCuser()));
            ((TextView) rootView.findViewById(R.id.item_performby)).setText(peopleDAO.getPeopleName(mItem.getPerformedby()));

            System.out.println("BUID: "+mItem.getBuid());
            System.out.println("Ticket Category: "+mItem.getTicketcategory());

            if(mItem.getGroupid()!=-1) {
                assignedToValue = groupDAO.getGroupName(mItem.getGroupid());
            }
            if(mItem.getPeopleid()!=-1) {
                assignedToValue = peopleDAO.getPeopleName(mItem.getPeopleid());
            }
            /*if(mItem.getAatop()!=-1)
            {
                assignedToValue = peopleDAO.getPeopleName(mItem.getAatop());
            }*/
            ((TextView) rootView.findViewById(R.id.item_assginedto)).setText(assignedToValue);

            assetLocationTextview=(TextView)rootView.findViewById(R.id.item_assetlocation);
            if(mItem.getAssetid()!=-1)
            {
                assetLocationTextview.setText(assetDAO.getTicketAssetLocation(mItem.getAssetid()));
            }


            attachmentCountTextView=(TextView)rootView.findViewById(R.id.item_attachmentcount);
            attachmentCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkNetwork.isNetworkConnectionAvailable())
                    {
                        isReplyHistory=1;
                        /*GetJobNeedReplyHistoryAsynTask getJobNeedReplyHistoryAsynTask=new GetJobNeedReplyHistoryAsynTask();
                        getJobNeedReplyHistoryAsynTask.execute();*/
                        fetchReplyHistory(getResources().getString(R.string.jobneedattachment_query,mItem.getJobneedid()));

                    }
                    else
                    {
                        Snackbar.make(view,getResources().getString(R.string.check_internet_connection_msg), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            System.out.println("mItem.getAttachmentcount(): "+mItem.getAttachmentcount());

            if(mItem.getAttachmentcount()!=null)
                attachmentCountTextView.setText(mItem.getAttachmentcount());
            else
                attachmentCountTextView.setText("0");

            /*GetJobNeedReplyHistoryAsynTask getJobNeedReplyHistoryAsynTask=new GetJobNeedReplyHistoryAsynTask();
            getJobNeedReplyHistoryAsynTask.execute();*/

            fetchReplyHistory(getResources().getString(R.string.jobneedhistory_query,mItem.getJobneedid(), "(select taid from typeassist where tacode='REPLY')"));

            String jobStatus=typeAssistDAO.getEventTypeName(mItem.getJobstatus());

            if(jobStatus.equalsIgnoreCase(Constants.TICKET_STATUS_NEW))
                customAlertDialog.customButtonAlertBox(getResources().getString(R.string.button_start),getResources().getString(R.string.button_cancel),getResources().getString(R.string.ticket_start_alert_msg), "Ticket",0);

        }

        return rootView;
    }


    private void saveReply()
    {
        //jobNeedDAO.updateJobNeedRecord(mItem.getJobneedid(),-1,-1, replyEdittext.getText().toString().trim(), typeAssistDAO.getEventTypeID(replyStatusSpinner.getSelectedItem().toString().trim()), loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        System.out.println("statusList.get(replyStatusSpinner.getSelectedItemPosition()).getTaid(): "+statusList.get(replyStatusSpinner.getSelectedItemPosition()).getTaid());
        System.out.println("replyStatusSpinner.getSelectedItemPosition(): "+replyStatusSpinner.getSelectedItemPosition());
        jobNeedDAO.updateJobNeedRecord(mItem.getJobneedid(),-1,-1, replyEdittext.getText().toString().trim(), statusList.get(replyStatusSpinner.getSelectedItemPosition()).getTaid(), loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));


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
        //attachment.setIsdeleted("False");
        attachment.setOwnerid(mItem.getJobneedid());
        attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_OWNER_TYPE_JOBNEED, Constants.IDENTIFIER_OWNER));
        attachment.setBuid(loginDetailPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));

        attachmentDAO.insertCommonRecord(attachment);

        jobNeedDAO.getJobNeedDetails(mItem.getJobneedid());
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void hideReplyHistory()
    {
        ticketHistoryLinearLayout.setVisibility(View.GONE);
    }

    private void showReplyHistory()
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        TextView textSrNo;
        TextView textQuestName;
        TextView textQuestAns;



        if(ticketHistoryLinearLayout.getChildCount()>0)
            ticketHistoryLinearLayout.removeAllViews();

        for(int i=0;i<jobNeedHistoryArrayList.size();i++)
        {
            if(jobNeedHistoryArrayList.get(i).getNarration()!=null  && jobNeedHistoryArrayList.get(i).getNarration().trim().length()>0) {
                v = inflater.inflate(R.layout.task_history_reading_row, null);
                textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
                textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
                textQuestAns = (TextView) v.findViewById(R.id.questAnsTextView);

                textSrNo.setText(String.valueOf(i + 1));
                textQuestName.setTag(i);
                textQuestName.setText(jobNeedHistoryArrayList.get(i).getNarration());


                try {
                    if (jobNeedHistoryArrayList.get(i).getDatetime() != null && !jobNeedHistoryArrayList.get(i).getDatetime().equalsIgnoreCase("None")) {
                        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        Date parsedDate = dateFormat.parse(jobNeedHistoryArrayList.get(i).getDatetime());
                        System.out.println(CommonFunctions.getParseDatabaseDateFormat(String.valueOf(parsedDate.getTime())));
                        textQuestAns.setText(CommonFunctions.getParseDatabaseDateFormat(String.valueOf(parsedDate.getTime())));*/
                        textQuestAns.setText(CommonFunctions.getDeviceTimezoneFormatDate(jobNeedHistoryArrayList.get(i).getDatetime()));
                    } else {
                        textQuestAns.setText("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ticketHistoryLinearLayout.addView(v);
            }
        }

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ticketHistoryLinearLayout.setVisibility(View.VISIBLE);
                ticketHistoryLinearLayout.requestFocus(View.FOCUS_UP);
                //scrollView.pageScroll(ScrollView.FOCUS_UP);
                //ticketHistoryLinearLayout.scrollBy(0, +50);
            }
        },1000);


    }

    private void showAttachmentHistory()
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        TextView textSrNo;
        TextView textQuestName;
        TextView textQuestAns;

        if(ticketHistoryLinearLayout.getChildCount()>0)
            ticketHistoryLinearLayout.removeAllViews();

        for(int i=0;i<jobNeedAttachmentHistoryArrayList.size();i++)
        {
            v = inflater.inflate(R.layout.task_history_reading_row, null);
            textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
            textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
            textQuestAns=(TextView) v.findViewById(R.id.questAnsTextView);

            textSrNo.setText(String.valueOf(i+1));
            textQuestName.setTag(i);

            textQuestName.setText(jobNeedAttachmentHistoryArrayList.get(i).getFilename());
            System.out.println("TicketAttachment Path: "+jobNeedAttachmentHistoryArrayList.get(i).getFilepath());

            try {
                if(jobNeedAttachmentHistoryArrayList.get(i).getDatetime()!=null && !jobNeedAttachmentHistoryArrayList.get(i).getDatetime().equalsIgnoreCase("None")) {
                    /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Date parsedDate = dateFormat.parse(jobNeedAttachmentHistoryArrayList.get(i).getDatetime());
                    System.out.println(CommonFunctions.getFormatedDate(String.valueOf(parsedDate.getTime())));
                    textQuestAns.setText(CommonFunctions.getFormatedDate(String.valueOf(parsedDate.getTime())));*/
                    textQuestAns.setText(CommonFunctions.getDeviceTimezoneFormatDate(jobNeedAttachmentHistoryArrayList.get(i).getDatetime()));
                }
                else
                {
                    textQuestAns.setText("");
                }

                textQuestName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("jobNeedAttachmentHistory: "+jobNeedAttachmentHistoryArrayList.get(Integer.parseInt(v.getTag().toString())).getFilepath());
                        /*DownloadAttachmentAsyntask downloadAttachmentAsyntask=new DownloadAttachmentAsyntask(jobNeedAttachmentHistoryArrayList.get((int)v.getTag()).getFilepath()+"~"+jobNeedAttachmentHistoryArrayList.get((int)v.getTag()).getFilename());
                        downloadAttachmentAsyntask.execute();*/

                        DownloadImage downloadAttachmentAsyntask=new DownloadImage(jobNeedAttachmentHistoryArrayList.get((int)v.getTag()).getFilepath()+"~"+jobNeedAttachmentHistoryArrayList.get((int)v.getTag()).getFilename());
                        downloadAttachmentAsyntask.execute();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            ticketHistoryLinearLayout.addView(v);
        }

        ticketHistoryLinearLayout.requestFocus();

        /*scrollView.post(new Runnable() {
            @Override
            public void run() {
                //scrollView.fullScroll(View.FOCUS_DOWN);
                //scrollView.scrollTo(0, scrollView.getBottom());
                scrollView.pageScroll(ScrollView.FOCUS_UP);
            }
        });*/

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.showAttachments:
                callIntent();
                break;
            case R.id.addAudioIV:
                Intent mediaRecoder=new Intent(getActivity(),MediaRecoderView.class);
                mediaRecoder.putExtra("FROM",Constants.ATTACHMENT_AUDIO);
                mediaRecoder.putExtra("TIMESTAMP",replyTimestamp);
                mediaRecoder.putExtra("JOBNEEDID",mItem.getJobneedid());
                mediaRecoder.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                mediaRecoder.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TICKET);
                startActivityForResult(mediaRecoder, Constants.MIC_RECORD_AUDIO_REQUEST_CODE);
                break;
            case R.id.addVideoIV:
                Intent captureVideo=new Intent(getActivity(), VideoCaptureActivity.class);
                captureVideo.putExtra("FROM",Constants.ATTACHMENT_VIDEO);
                captureVideo.putExtra("TIMESTAMP",replyTimestamp);
                captureVideo.putExtra("JOBNEEDID",mItem.getJobneedid());
                captureVideo.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                captureVideo.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TICKET);
                startActivityForResult(captureVideo,Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                break;
            case R.id.addPictureIV:
                Intent capturePic=new Intent(getActivity(), CapturePhotoActivity.class);
                capturePic.putExtra("FROM",Constants.ATTACHMENT_PICTURE);
                capturePic.putExtra("TIMESTAMP",replyTimestamp);
                capturePic.putExtra("JOBNEEDID",mItem.getJobneedid());
                capturePic.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
                capturePic.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_TICKET);
                startActivityForResult(capturePic,Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Constants.MIC_RECORD_AUDIO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE || requestCode==Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode== FragmentActivity.RESULT_OK)
            {
                System.out.println("Attachment Count: "+attachmentDAO.getAttachmentCount(mItem.getJobneedid(),replyTimestamp));
                addAttachmentCountView.setText(String.valueOf(attachmentDAO.getAttachmentCount(mItem.getJobneedid(),replyTimestamp)));
            }
        }
    }

    private void callIntent()
    {
        Intent recAudioIntent=new Intent(getActivity(), AttachmentListActivity.class);
        recAudioIntent.putExtra("FROM",0);
        recAudioIntent.putExtra("TIMESTAMP",replyTimestamp);
        recAudioIntent.putExtra("JOBNEEDID",mItem.getJobneedid());
        recAudioIntent.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
        startActivityForResult(recAudioIntent,1);
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        jobNeedDAO.changeJobStartTime(mItem.getJobneedid(), typeAssistDAO.getEventTypeID(Constants.TICKET_STATUS_OPEN, Constants.STATUS_TYPE_TICKET));
    }

    private void fetchReplyHistory(String queryInfo)
    {
        //String queryInfo=getResources().getString(R.string.jobneedhistory_query,mItem.getJobneedid(), "(select taid from typeassist where tacode='REPLY')");

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show(); // show progress dialog

        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(Constants.SERVICE_SELECT);
        uploadParameters.setQuery(queryInfo);
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setTzoffset(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
        uploadParameters.setSitecode(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
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

                            if(isReplyHistory==1)
                            {
                                showAttachmentAlertDialog(jobNeedHistoryArrayList);
                            }
                            else {
                                if (jobNeedHistoryArrayList != null && jobNeedHistoryArrayList.size() > 0) {
                                    adapter = new TicketHistoryAlertDialogAdapter(jobNeedHistoryArrayList);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(adapter);
                                }
                            }

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attachments");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                dialog.dismiss();
            }
        });
        builder.show();
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
            if(isReplyHistory==0)
                jobNeedHistoryArrayList=new ArrayList<JobNeedHistory>();
            else if(isReplyHistory==1)
                jobNeedAttachmentHistoryArrayList=new ArrayList<JobNeedHistory>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                String queryInfo=null;

                if(isReplyHistory==0)
                    queryInfo= getResources().getString(R.string.jobneedhistory_query,mItem.getJobneedid(),"(select taid from typeassist where tacode='REPLY')");
                else
                    queryInfo= getResources().getString(R.string.jobneedattachment_query,mItem.getJobneedid());

                //---------------------------------------------------------------------

                ServerRequest serverRequest=new ServerRequest(getActivity());
                HttpResponse response=serverRequest.getReplyHistoryResponse(queryInfo.trim(),
                                                                            loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
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

                            if(isReplyHistory==0)
                                jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);
                            else if(isReplyHistory==1)
                                jobNeedAttachmentHistoryArrayList=gson.fromJson(data1.toString(), listType);

                        }
                    }


                }
                else {
                    System.out.println("SB1 JOBHistory: "+response.getStatusLine().getStatusCode());
                }

                //----------------------------------------------------------------------------------------------------



                /*URL url = new URL(Constants.BASE_URL); // here is your URL path

                UploadParameters uploadParameters=new UploadParameters();
                uploadParameters.setServicename(Constants.SERVICE_SELECT);
                uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
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

                        if(isReplyHistory==0)
                            jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);
                        else if(isReplyHistory==1)
                            jobNeedAttachmentHistoryArrayList=gson.fromJson(data1.toString(), listType);



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
            if(isReplyHistory==0 && jobNeedHistoryArrayList!=null && jobNeedHistoryArrayList.size()>0) {
                replyHistoryCountView.setText(String.valueOf(jobNeedHistoryArrayList.size()));
            }
            else if(isReplyHistory==1 && jobNeedAttachmentHistoryArrayList!=null && jobNeedAttachmentHistoryArrayList.size()>0)
            {
                ticketHistoryLinearLayout.setVisibility(View.VISIBLE);
                showAttachmentHistory();

            }
        }

    }


    private void openAlertWithWebView(Bitmap bitmap)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Attachment");

        ImageView wv = new ImageView(getActivity());

        wv.setImageBitmap(bitmap);

        alert.setView(wv);
        alert.setNegativeButton(getResources().getString(R.string.button_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {

        String imgInfo;
        String imgPath, imgName;
        public DownloadImage(String imgInfo)
        {
            this.imgInfo=imgInfo;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Void... URL) {

            String[] img=imgInfo.split("~");
            imgPath=img[0];
            imgName=img[1];

            String urlString=getResources().getString(R.string.downloadImageFromWeb,Constants.IMAGE_BASE_URL,imgName,imgPath);
            System.out.println("urlString : "+urlString);
            //String urlString="http://192.168.1.254:8000/download?filename="+imgName+"&amp;filepath="+imgPath+"&amp;type=image/png&amp";


            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(urlString).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            openAlertWithWebView(result);

        }
    }
}
