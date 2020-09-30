package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Group;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.JobNeedHistory;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.ServerRequest;
import com.youtility.intelliwiz20.android.CaptureActivity;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link JOBListActivity}
 * in two-pane mode (on tablets) or a {@link JOBDetailActivity}
 * on handsets.
 */
public class JOBDetailFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, IDialogEventListeners {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private JobNeed mItem;

    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private JobNeedDAO jobNeedDAO;
    private AttachmentDAO attachmentDAO;
    private TypeAssistDAO typeAssistDAO;
    private QuestionDAO questionDAO;
    private AssetDAO assetDAO;



    ArrayList<JobNeedDetails> jobNeedDetailsArrayList;
    ArrayList<JobNeedDetails> jobNeedDetailsHistoryArrayList;

    private LinearLayout activityLayout;
    private LinearLayout dialogLayout;

    private EditText dialogRemarkEdittext;
    private Spinner dialogPeopleGroupSpinner;
    private Spinner dialogStatusSpinner;
    private RadioGroup dialogRadioGroup;
    private ScrollView scrollView;
    private int selectedRadioButton=0;
    private List<String> spinnerValues;
    private ArrayAdapter peopleGroupAdpt;
    private Button dialogCancelButton;
    private Button dialogDoneButton;
    private Button audioCaptureButton,videoCaptureButton, pictureCaptureButton;
    //String[] statusName={"ASSIGNED","IN-PROGRESS","COMPLETED","ARCHIVED","CLOSED"};
    private ArrayList<TypeAssist>statusList;
    private ArrayList<String>statusNameList;
    //private boolean isQuestSetAssigned=false;
    private LinearLayout taskHistoryLinearLayout;
    private SharedPreferences loginDetailPref;
    private CheckNetwork checkNetwork;

    private ArrayList<People>peopleList;
    private PeopleDAO peopleDAO;

    private ArrayList<Group>groupList;
    private GroupDAO groupDAO;

    private String alertType;
    private long alertTo;
    private TextView attachmentCountTextView;
    private Boolean isSDPresent=false;
    private String extStorageDirectory="";
    private final int AUDIO_INTENT=0;
    private final int VIDEO_INTENT=1;
    private final int PICTURE_INTENT=2;
    private long replyTimestamp=-1;
    private int isJobExpired=-1;
    private boolean isJobStatusClosed=false;
    private CustomAlertDialog customAlertDialog;

    private String jobIdentiferType=null;

    private SharedPreferences loginPref;
    private SharedPreferences deviceInfoPref;


    //PENDING/ IN-PROGRESS/ COMPLETED/ ARCHIVED
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JOBDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem =JOBListActivity.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            if(mItem!=null && mItem.getJobneedid()!=-1) {

                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.getJobneedid() + "");
                }
                jobNeedDetailsDAO = new JobNeedDetailsDAO(getActivity());
                jobNeedDAO = new JobNeedDAO(getActivity());
                typeAssistDAO = new TypeAssistDAO(getActivity());
                attachmentDAO = new AttachmentDAO(getActivity());
                peopleDAO = new PeopleDAO(getActivity());
                groupDAO = new GroupDAO(getActivity());
                questionDAO = new QuestionDAO(getActivity());
                assetDAO = new AssetDAO(getActivity());

                customAlertDialog = new CustomAlertDialog(getActivity(), this);
                loginPref = getActivity().getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
                deviceInfoPref = getActivity().getSharedPreferences(Constants.DEVICE_RELATED_PREF, Context.MODE_PRIVATE);

                jobNeedDetailsArrayList = new ArrayList<JobNeedDetails>();
                jobNeedDetailsArrayList = jobNeedDetailsDAO.getJobNeedDetailQuestList(mItem.getJobneedid());


                statusList = typeAssistDAO.getEventList("Job Status");
                statusNameList = new ArrayList<>();
                for (int i = 0; i < statusList.size(); i++) {
                    if (statusList.get(i).getTaid() != -1) {
                        statusNameList.add(statusList.get(i).getTaname());
                    }
                }

                if (jobNeedDetailsArrayList != null && jobNeedDetailsArrayList.size() > 0) {
                    //isQuestSetAssigned = true;
                    for (int i = 0; i < jobNeedDetailsArrayList.size(); i++) {
                        System.out.println("SqNo: " + jobNeedDetailsArrayList.get(i).getSeqno());
                        System.out.println("QuestName: " + jobNeedDetailsArrayList.get(i).getQuestionid());
                        System.out.println("MIN: " + jobNeedDetailsArrayList.get(i).getMin());
                        System.out.println("Max: " + jobNeedDetailsArrayList.get(i).getMax());
                        System.out.println("Options: " + jobNeedDetailsArrayList.get(i).getOption());
                        System.out.println("-------------------------------------------------------------------");
                    }
                } else {
                    //isQuestSetAssigned = false;
                }
                isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                extStorageDirectory = Environment.getExternalStorageDirectory().toString();

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.job_item_detail, container, false);

        loginDetailPref=getActivity().getSharedPreferences(Constants.LOGIN_PREFERENCE,Context.MODE_PRIVATE);
        checkNetwork=new CheckNetwork(getActivity());


        activityLayout=(LinearLayout)rootView.findViewById(R.id.activityLayout);
        dialogLayout=(LinearLayout)rootView.findViewById(R.id.dialogLayout);
        activityLayout.setVisibility(View.VISIBLE);
        dialogLayout.setVisibility(View.INVISIBLE);

        taskHistoryLinearLayout=(LinearLayout)rootView.findViewById(R.id.task_historyLinearLayout);

        dialogCancelButton=(Button)rootView.findViewById(R.id.dialogCancelButton);
        dialogDoneButton=(Button)rootView.findViewById(R.id.dialogDoneButton);

        audioCaptureButton=(Button)rootView.findViewById(R.id.audioCaptureButton);
        videoCaptureButton=(Button)rootView.findViewById(R.id.videoCaptureButton);
        pictureCaptureButton=(Button)rootView.findViewById(R.id.pictureCaptureButton);

        audioCaptureButton.setOnClickListener(this);
        videoCaptureButton.setOnClickListener(this);
        pictureCaptureButton.setOnClickListener(this);

        dialogCancelButton.setOnClickListener(this);
        dialogDoneButton.setOnClickListener(this);

        dialogRemarkEdittext=(EditText)rootView.findViewById(R.id.editTextDialogRemark);
        dialogStatusSpinner=(Spinner)rootView.findViewById(R.id.dialogStatusSpinner);
        if(statusNameList!=null && statusNameList.size()>0) {
            ArrayAdapter statusAdpt = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, statusNameList);
            statusAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dialogStatusSpinner.setAdapter(statusAdpt);
        }

        dialogRadioGroup=(RadioGroup)rootView.findViewById(R.id.dialogRadioGroup);
        dialogRadioGroup.setOnCheckedChangeListener(this);
        dialogPeopleGroupSpinner=(Spinner)rootView.findViewById(R.id.dialogUserGrpSpinner);


        scrollView=(ScrollView)rootView.findViewById(R.id.scrollView);

        ((Button)rootView.findViewById(R.id.jobReplyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(getActivity(), loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
                System.out.println("===========" + accessValue);
                System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                if (accessValue == 0 ) {
                    if (CommonFunctions.isPermissionGranted(getActivity())) {
                        checkTaskExpiry();
                        if (!isJobStatusClosed && (isJobExpired != 0 && isJobExpired != 2)) {
                    /*activityLayout.setVisibility(View.INVISIBLE);
                    dialogLayout.setVisibility(View.VISIBLE);
                    */
                            replyTimestamp = System.currentTimeMillis();
                            Intent replyIntent = new Intent(getActivity(), JobneedReplyActivity.class);
                            replyIntent.putExtra("REPLY_TIMESTAMP", replyTimestamp);
                            replyIntent.putExtra("JOBNEEDID", mItem.getJobneedid());
                            startActivityForResult(replyIntent, 4);
                        } else {
                            if (typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                                Snackbar.make(view, getResources().getString(R.string.job_has_completed), Snackbar.LENGTH_LONG).show();
                            else if (typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                                Snackbar.make(view, getResources().getString(R.string.job_has_closed), Snackbar.LENGTH_LONG).show();
                            else if (isJobExpired == 2)
                                Snackbar.make(view, getResources().getString(R.string.job_has_expired), Snackbar.LENGTH_LONG).show();
                            else if (isJobExpired == 0)
                                Snackbar.make(view, getResources().getString(R.string.job_is_future, mItem.getPlandatetime()), Snackbar.LENGTH_LONG).show();
                        }
                    } else
                        Snackbar.make(scrollView, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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

        ((Button)rootView.findViewById(R.id.jobPerformButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int accessValue = CommonFunctions.isAllowToAccessModules(getActivity(), loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
                System.out.println("===========" + accessValue);
                System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                if (accessValue == 0 ) {
                    if (CommonFunctions.isPermissionGranted(getActivity())) {
                        checkTaskExpiry();
                        if (!isJobStatusClosed && isJobExpired != 0 && isJobExpired != 2) {
                            if (jobNeedDetailsArrayList != null && jobNeedDetailsArrayList.size() > 0) {
                                customAlertDialog.customButtonAlertBox(getResources().getString(R.string.button_start), getResources().getString(R.string.button_cancel), getResources().getString(R.string.joblist_startjob_title) + " " + mItem.getJobdesc() + "?", "JOB", 0);

                            } else {
                                Snackbar.make(view, getResources().getString(R.string.job_quest_not_assigned), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            if (typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                                Snackbar.make(view, getResources().getString(R.string.job_has_completed), Snackbar.LENGTH_LONG).show();
                            else if (typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                                Snackbar.make(view, getResources().getString(R.string.job_has_closed), Snackbar.LENGTH_LONG).show();
                            else if (isJobExpired == 2)
                                Snackbar.make(view, getResources().getString(R.string.job_has_expired), Snackbar.LENGTH_LONG).show();
                            else if (isJobExpired == 0)
                                Snackbar.make(view, getResources().getString(R.string.job_is_future, mItem.getPlandatetime()), Snackbar.LENGTH_LONG).show();
                        }
                    } else
                        Snackbar.make(scrollView, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
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

        //shifted to new activity
        /*((Button)rootView.findViewById(R.id.jobReplyHistoryButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetwork.isNetworkConnectionAvailable())
                {
                    GetJobNeedReplyHistoryAsynTask getJobNeedReplyHistoryAsynTask=new GetJobNeedReplyHistoryAsynTask(0,mItem.getJobneedid());
                    getJobNeedReplyHistoryAsynTask.execute();

                }
                else
                {
                    Snackbar.make(view,"Please check internet connection!!", Snackbar.LENGTH_LONG).show();
                }
            }
        });*/

        ((Button)rootView.findViewById(R.id.jobPerformDetailsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuestionsAnsView();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        String assignedToValue=null;
        // Show the dummy content as text in a TextView.
        if (mItem != null) {

            System.out.println("JOBNeedDetail identifier: "+typeAssistDAO.getEventTypeCode(mItem.getIdentifier()));
            jobIdentiferType=typeAssistDAO.getEventTypeCode(mItem.getIdentifier());

            ((TextView) rootView.findViewById(R.id.item_ticketno)).setText(String.valueOf(mItem.getTicketno()));
            ((TextView) rootView.findViewById(R.id.item_code)).setText(String.valueOf(mItem.getJobneedid()));
            ((TextView) rootView.findViewById(R.id.item_code)).setTextIsSelectable(true);
            ((TextView) rootView.findViewById(R.id.item_description)).setText(mItem.getJobdesc());
            ((TextView) rootView.findViewById(R.id.item_plandate)).setText((mItem.getPlandatetime()));
            //((TextView) rootView.findViewById(R.id.item_plandate)).setText(CommonFunctions.getDeviceTimezoneFormatDate(mItem.getPlandatetime()));
            ((TextView) rootView.findViewById(R.id.item_expirydate)).setText((mItem.getExpirydatetime()));
            //((TextView) rootView.findViewById(R.id.item_expirydate)).setText(CommonFunctions.getDeviceTimezoneFormatDate(mItem.getExpirydatetime()));
            ((TextView) rootView.findViewById(R.id.item_gracetime)).setText(String.valueOf(mItem.getGracetime()));
            ((TextView) rootView.findViewById(R.id.item_frequency)).setText(typeAssistDAO.getEventTypeName(mItem.getFrequency()));
            ((TextView) rootView.findViewById(R.id.item_type)).setText(typeAssistDAO.getEventTypeName(mItem.getJobtype()));
            ((TextView) rootView.findViewById(R.id.item_status)).setText(typeAssistDAO.getEventTypeName(mItem.getJobstatus()));
            ((TextView) rootView.findViewById(R.id.item_priority)).setText(typeAssistDAO.getEventTypeName(mItem.getPriority()));
            ((TextView) rootView.findViewById(R.id.item_tCategory)).setText(typeAssistDAO.getEventTypeName(mItem.getTicketcategory()));
            ((TextView) rootView.findViewById(R.id.item_assginedby)).setText(peopleDAO.getPeopleName(mItem.getCuser()));
            /*System.out.println("Plan date: "+CommonFunctions.getDeviceTimezoneFormatDate(mItem.getPlandatetime()));
            System.out.println("exp date: "+CommonFunctions.getDeviceTimezoneFormatDate(mItem.getExpirydatetime()));*/
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


            checkTaskExpiry();
            /*long backDate=new Date( CommonFunctions.getParse24HrsDate(mItem.getPlandatetime())- (mItem.getGracetime() * 60 * 1000)).getTime();
            isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate(mItem.getExpirydatetime()));*/

            String jobStatus=typeAssistDAO.getEventTypeName(mItem.getJobstatus());
            if(jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED) || jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED)) {
                isJobStatusClosed = true;
            }
            else {
                isJobStatusClosed = false;
            }

            System.out.println("isJobExpired: "+isJobExpired);
            System.out.println("isJobStatusClosed: "+isJobStatusClosed);

            attachmentCountTextView=(TextView)rootView.findViewById(R.id.item_attachmentcount);
            attachmentCountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkNetwork.isNetworkConnectionAvailable())
                    {
                        GetJobNeedReplyHistoryAsynTask getJobNeedReplyHistoryAsynTask=new GetJobNeedReplyHistoryAsynTask(mItem.getJobneedid());
                        getJobNeedReplyHistoryAsynTask.execute();
                    }
                    else
                    {
                        Snackbar.make(view,getResources().getString(R.string.check_internet_connection_msg), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            if(mItem.getAttachmentcount()!=null)
                attachmentCountTextView.setText(mItem.getAttachmentcount());
            else
                attachmentCountTextView.setText("0");
        }

        return rootView;
    }

    private void checkTaskExpiry()
    {
        long backDate=new Date( CommonFunctions.getParse24HrsDate((mItem.getPlandatetime()))- (mItem.getGracetime() * 60 * 1000)).getTime();
        isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((mItem.getExpirydatetime())));
        /*long backDate=new Date( CommonFunctions.getParse24HrsDate((mItem.getPlandatetime()))- (mItem.getGracetime() * 60 * 1000)).getTime();
        isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((mItem.getExpirydatetime())));*/
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {
        if(type==0) {
            if (typeAssistDAO.getEventTypeCode(mItem.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_QR)) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.putExtra("FROM", "JOB");
                startActivityForResult(intent, 5);
            } else if (typeAssistDAO.getEventTypeCode(mItem.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_NFC)) {
                Intent i = new Intent(getActivity(), NFCCodeReaderActivity.class);
                startActivityForResult(i, 6);
            } else if (typeAssistDAO.getEventTypeCode(mItem.getScantype()).equalsIgnoreCase(Constants.SCAN_TYPE_ENTERED)) {
                customAlertDialog.custEnteredScanType(getResources().getString(R.string.joblist_enterassetcode), getResources().getString(R.string.joblist_enterassetcode),1);
            } else {
                if (taskHistoryLinearLayout.getChildCount() > 0)
                    taskHistoryLinearLayout.removeAllViews();

                jobNeedDAO.updateJobNeedStartTime(mItem.getJobneedid());
                CommonFunctions.manualSyncEventLog("JOB_PERFORM_START","JOBNEEDID: "+mItem.getJobneedid(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

                Intent ii = new Intent(getActivity(), IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "JOB");
                ii.putExtra("ID", mItem.getJobneedid());
                ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
                ii.putExtra("FOLDER", jobIdentiferType);
                startActivityForResult(ii, 0);
            }


        }
        else if(type==1)
        {
            long assetIDFrmDB=assetDAO.getAssetID(errorMessage);

            if(assetIDFrmDB==mItem.getAssetid()) {

                if (taskHistoryLinearLayout.getChildCount() > 0)
                    taskHistoryLinearLayout.removeAllViews();

                jobNeedDAO.updateJobNeedStartTime(mItem.getJobneedid());
                CommonFunctions.manualSyncEventLog("JOB_PERFORM_START","JOBNEEDID: "+mItem.getJobneedid(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

                Intent ii = new Intent(getActivity(), IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "JOB");
                ii.putExtra("ID", mItem.getJobneedid());
                ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
                ii.putExtra("FOLDER", jobIdentiferType);
                startActivityForResult(ii, 0);
            }
            else
                Snackbar.make(scrollView,getResources().getString(R.string.joblist_assetcodemismatched), Snackbar.LENGTH_LONG).show();
        }
    }

    private class GetJobNeedReplyHistoryAsynTask extends  AsyncTask<Void, Integer, Void>
    {
        int callType;
        MediaType JSON;
        OkHttpClient client;
        StringBuffer sb;
        long jobneedid;
        ArrayList<JobNeedHistory>jobNeedHistoryArrayList;
        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        private Type listType;
        ProgressDialog pd;
        public GetJobNeedReplyHistoryAsynTask( long jobneedid)
        {
            this.jobneedid=jobneedid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(getActivity(), "", "Please wait...", true);
            pd.show();
            client = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
            jobNeedHistoryArrayList=new ArrayList<JobNeedHistory>();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String queryInfo=getResources().getString(R.string.jobneedattachment_query,jobneedid);
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
                            jobNeedHistoryArrayList = gson.fromJson(data1.toString(), listType);



                        }
                    }


                }
                else {
                    System.out.println("SB1 JOBHistory: "+response.getStatusLine().getStatusCode());
                }

                //----------------------------------------------------------------------------------------------------


/*

                URL url = new URL(Constants.BASE_URL); // here is your URL path

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
            super.onPostExecute(aVoid);
            if(pd!=null)
                pd.dismiss();

            if(jobNeedHistoryArrayList!=null && jobNeedHistoryArrayList.size()>0)
            {

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                    textSrNo.setText(String.valueOf(i+1));

                    textQuestName.setText(jobNeedHistoryArrayList.get(i).getFilename());
                    //textQuestAns.setText("" + jobNeedHistoryArrayList.get(i).getCdtz());
                    textQuestName.setTag(i);
                    try {
                        if(jobNeedHistoryArrayList.get(i).getCdtz()!=null && !jobNeedHistoryArrayList.get(i).getCdtz().equalsIgnoreCase("None")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            Date parsedDate = dateFormat.parse(CommonFunctions.getDeviceTimezoneFormatDate(jobNeedHistoryArrayList.get(i).getCdtz()));
                            System.out.println(CommonFunctions.getFormatedDate(String.valueOf(parsedDate.getTime())));
                            textQuestAns.setText(CommonFunctions.getFormatedDate((String.valueOf(parsedDate.getTime()))));
                            /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            Date parsedDate = dateFormat.parse((jobNeedHistoryArrayList.get(i).getCdtz()));
                            System.out.println(CommonFunctions.getFormatedDate(String.valueOf(parsedDate.getTime())));
                            textQuestAns.setText(CommonFunctions.getFormatedDate((String.valueOf(parsedDate.getTime()))));*/
                        }
                        else
                        {
                            textQuestAns.setText("");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    textQuestName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //if(callType==1)
                            {
                                System.out.println("jobNeedHistory: "+jobNeedHistoryArrayList.get(Integer.parseInt(view.getTag().toString())).getFilepath());
                                //temporary commented
                                /*DownloadAttachmentAsyntask downloadAttachmentAsyntask=new DownloadAttachmentAsyntask(jobNeedHistoryArrayList.get((int)view.getTag()).getFilepath()+"~"+jobNeedHistoryArrayList.get((int)view.getTag()).getFilename());
                                downloadAttachmentAsyntask.execute();*/

                                //uses picasso lib
                               /*String urlString=getResources().getString(R.string.downloadImageFromWeb,Constants.IMAGE_BASE_URL,jobNeedHistoryArrayList.get((int)view.getTag()).getFilename(),jobNeedHistoryArrayList.get((int)view.getTag()).getFilepath());
                                urlString=urlString.replace("/download","/previewImage");
                                System.out.println("Image Url: "+urlString.toString());
                                openAlertWithWebView(null,urlString);*/

                                /*String urlString=getResources().getString(R.string.downloadImageFromWeb,Constants.IMAGE_BASE_URL,jobNeedHistoryArrayList.get((int)view.getTag()).getFilename(),jobNeedHistoryArrayList.get((int)view.getTag()).getFilepath());
                                System.out.println("Image Url: "+urlString.toString());
                                new SendHttpRequestTask(urlString).execute();*/
                            }
                        }
                    });
                    taskHistoryLinearLayout.addView(v);
                }

                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }

    }

    private void openAlertWithWebView(Bitmap bitmap, String  urll)
    {
        /*WebSettings webSettings;
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Title here");

        WebView wv = new WebView(getActivity());

        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.loadUrl(urll);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();*/




        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Title here");

        ImageView wv = new ImageView(getActivity());

        Picasso.with(getActivity()).load(urll).resize(100,100).into(wv,new com.squareup.picasso.Callback()
        {

            @Override
            public void onSuccess() {
                System.out.println("Success image");
            }

            @Override
            public void onError() {
                System.out.println("Error image");
            }
        });

        //wv.setImageURI(Uri.parse(urll));

        //wv.setImageBitmap(bitmap);

        alert.setView(wv);
        alert.setNegativeButton(getResources().getString(R.string.button_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, Bitmap> {
        String urlStr;
        public SendHttpRequestTask(String urlStr)
        {
            this.urlStr=urlStr;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            }catch (Exception e){
                //Log.d(TAG,e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            openAlertWithWebView(result,urlStr);
        }
    }

    private class DownloadAttachmentAsyntask extends AsyncTask<Void, Integer, Bitmap>
    {
        String imgInfo;
        Bitmap bitmap;
        String imgPath, imgName;
        URL urll;
        public DownloadAttachmentAsyntask(String imgInfo)
        {
            this.imgInfo=imgInfo;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String[] img=imgInfo.split("~");
            imgPath=img[0];
            imgName=img[1];
        }



        @Override
        protected Bitmap doInBackground(Void... voids) {

            try
            {
                HttpGet httpRequest = null;

                String dirPath=extStorageDirectory+"/"+Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";

                File file = new File(dirPath,imgName);
                //String urlString="http://192.168.1.254:8000/download?name="+imgName+"&amp;path="+imgPath+"&amp;type=image/png&amp";
                String urlString=getResources().getString(R.string.downloadImageFromWeb,Constants.IMAGE_BASE_URL,imgName,imgPath);

                urll = new URL(urlString);
                System.out.println("Img url: "+urll.toString());
                httpRequest = new HttpGet(urll.toURI());

                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response1 = (HttpResponse) httpclient.execute(httpRequest);

                HttpEntity entity = response1.getEntity();

                BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);

                InputStream instream = bufHttpEntity.getContent();

                bitmap = BitmapFactory.decodeStream(instream);
                FileOutputStream fOut = new FileOutputStream(file);

                if(bitmap!=null)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

                fOut.flush();
                fOut.close();

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap aVoid) {
            //super.onPostExecute(aVoid);
            openAlertWithWebView(aVoid,urll.toString());
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void showQuestionsAnsView()
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        TextView textSrNo;
        TextView textQuestName;
        TextView textQuestAns;



        if(taskHistoryLinearLayout.getChildCount()>0)
            taskHistoryLinearLayout.removeAllViews();

        jobNeedDetailsHistoryArrayList=new ArrayList<JobNeedDetails>();
        jobNeedDetailsHistoryArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(mItem.getJobneedid());
        if(jobNeedDetailsHistoryArrayList!=null && jobNeedDetailsHistoryArrayList.size()>0) {

            for (int i = 0; i < jobNeedDetailsHistoryArrayList.size(); i++) {
                v = inflater.inflate(R.layout.task_history_reading_row, null);
                textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
                textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
                textQuestAns = (TextView) v.findViewById(R.id.questAnsTextView);

                textSrNo.setText(String.valueOf(jobNeedDetailsHistoryArrayList.get(i).getSeqno()));
                textQuestName.setText(questionDAO.getQuestionName(jobNeedDetailsHistoryArrayList.get(i).getQuestionid()));
                if (jobNeedDetailsHistoryArrayList.get(i).getAnswer() != null && !jobNeedDetailsHistoryArrayList.get(i).getAnswer().equalsIgnoreCase("null"))
                    textQuestAns.setText(getResources().getString(R.string.readingdetail_answer, jobNeedDetailsHistoryArrayList.get(i).getAnswer()));
                else
                    textQuestAns.setText(getResources().getString(R.string.readingdetail_answer, "---"));
                taskHistoryLinearLayout.addView(v);
            }
        }

    }


    @Override
    public void onClick(View view) {
        long atog=-1;
        long atop=-1;
        //not in use , now shifted to new activity
        /*switch(view.getId())
        {
            case R.id.dialogCancelButton:
                activityLayout.setVisibility(View.VISIBLE);
                dialogLayout.setVisibility(View.INVISIBLE);
                replyTimestamp=-1l;
                break;
            case R.id.dialogDoneButton:
                if(dialogRemarkEdittext.getText().toString().trim().length()>0)
                {

                    if(checkNetwork.isNetworkConnectionAvailable()) {

                        atog = mItem.getGroupid();

                        if(atog!=-1)
                        {
                            alertTo=atog;
                            //atog = "'" + atog + "'";
                            alertType="GROUP";
                        }

                        System.out.println("ATOG: "+atog);

                        atop = mItem.getPeopleid();
                        if(atop!=-1)
                        {
                            alertTo=atop;
                            //atop = "'" + atop + "'";
                            alertType="PEOPLE";
                        }

                        System.out.println("ATOP: "+atop);

                        System.out.println("Job Status String: "+(dialogStatusSpinner.getSelectedItem().toString()));
                        System.out.println("Job Status id: "+typeAssistDAO.getEventTypeID(dialogStatusSpinner.getSelectedItem().toString().trim()));

                        jobNeedDAO.updateJobNeedRecord(mItem.getJobneedid(), atop, atog, dialogRemarkEdittext.getText().toString().trim(), typeAssistDAO.getEventTypeID(dialogStatusSpinner.getSelectedItem().toString().trim()), loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));

                        Attachment attachment=new Attachment();
                        attachment.setAttachmentid((replyTimestamp));
                        attachment.setAttachmentType(typeAssistDAO.getEventTypeID("REPLY"));
                        attachment.setFilePath(null);
                        attachment.setFileName(null);
                        attachment.setNarration(dialogRemarkEdittext.getText().toString().trim());
                        attachment.setGpslocation("19,19");
                        attachment.setDatetime(CommonFunctions.getParseDate(System.currentTimeMillis()));
                        attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                        attachment.setCdtz(CommonFunctions.getParseDate(System.currentTimeMillis()));
                        attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                        attachment.setMdtz(CommonFunctions.getParseDate(System.currentTimeMillis()));
                        attachment.setIsdeleted("False");
                        attachment.setOwnerid(mItem.getJobneedid());
                        attachment.setOwnername(typeAssistDAO.getEventTypeID("JOBNEED"));

                        attachmentDAO.insertCommonRecord(attachment);

                        dialogRemarkEdittext.setText("");
                        activityLayout.setVisibility(View.VISIBLE);
                        dialogLayout.setVisibility(View.INVISIBLE);

                        *//*JOBNeedUpdateAsynTask jobNeedUpdateAsynTask = new JOBNeedUpdateAsynTask(mItem.getJobneedid(), atop, atog, dialogRemarkEdittext.getText().toString().trim(), typeAssistDAO.getEventTypeID(dialogStatusSpinner.getSelectedItem().toString().trim()), loginDetailPref.getString(Constants.LOGIN_PEOPLE_ID,null));
                        jobNeedUpdateAsynTask.execute();*//*
                    }
                    else
                    {
                        Snackbar.make(view,"Please check internet connection",Snackbar.LENGTH_LONG).show();
                    }


                }
                else
                {
                    dialogRemarkEdittext.setError("Please enter remark");
                }

                break;
            case R.id.audioCaptureButton:
                callIntent(AUDIO_INTENT);
                break;
            case R.id.videoCaptureButton:
                callIntent(VIDEO_INTENT);
                break;
            case R.id.pictureCaptureButton:
                callIntent(PICTURE_INTENT);
                break;
        }*/


    }

    private void callIntent(int intentValue)
    {
        Intent nexIntent=new Intent(getActivity(), AttachmentListActivity.class);
        nexIntent.putExtra("FROM",intentValue);
        nexIntent.putExtra("TIMESTAMP",replyTimestamp);
        nexIntent.putExtra("JOBNEEDID",mItem.getJobneedid());
        nexIntent.putExtra("PARENT_ACTIVITY","JOBNEED");
        startActivityForResult(nexIntent,intentValue);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 || requestCode==4)
        {
            if(resultCode== FragmentActivity.RESULT_OK)
            {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
        else if(requestCode==5)
        {
            if(resultCode==getActivity().RESULT_OK)
            {
                if(data!=null) {

                    String assetCode=data.getStringExtra("SCAN_RESULT");
                    CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_QR,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    CommonFunctions.manualSyncEventLog("QRScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    validateAssetCode(assetCode);
                }
            }
            else {
                Snackbar.make(scrollView, getResources().getString(R.string.joblist_assetcodemismatched), Snackbar.LENGTH_LONG).show();
                /*if (taskHistoryLinearLayout.getChildCount() > 0)
                    taskHistoryLinearLayout.removeAllViews();

                jobNeedDAO.updateJobNeedStartTime(mItem.getJobneedid());

                Intent ii = new Intent(getActivity(), IncidentReportQuestionActivity.class);
                ii.putExtra("FROM", "JOB");
                ii.putExtra("ID", mItem.getJobneedid());
                ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
                ii.putExtra("FOLDER", "TASK");
                startActivityForResult(ii, 0);*/
            }
        }
        else if(requestCode==6)
        {
            if(resultCode==getActivity().RESULT_OK)
            {
                String assetCode=data.getStringExtra("SCAN_RESULT");
                CommonFunctions.manualSyncEventLog("ScanType",Constants.SCAN_TYPE_NFC,CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                CommonFunctions.manualSyncEventLog("NFCScanResult",data.getStringExtra("SCAN_RESULT"),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                validateAssetCode(assetCode);
            }
        }



    }

    private void validateAssetCode(String scanAssetCode)
    {
        long assetIDFrmDB=assetDAO.getAssetID(scanAssetCode);
        if(assetIDFrmDB==mItem.getAssetid()) {

            if (taskHistoryLinearLayout.getChildCount() > 0)
                taskHistoryLinearLayout.removeAllViews();

            jobNeedDAO.updateJobNeedStartTime(mItem.getJobneedid());
            CommonFunctions.manualSyncEventLog("JOB_PERFORM_START","JOBNEEDID: "+mItem.getJobneedid(),CommonFunctions.getTimezoneDate(System.currentTimeMillis()));

            Intent ii = new Intent(getActivity(), IncidentReportQuestionActivity.class);
            ii.putExtra("FROM", "JOB");
            ii.putExtra("ID", mItem.getJobneedid());
            ii.putExtra("PARENT_ACTIVITY", "JOBNEED");
            ii.putExtra("FOLDER", jobIdentiferType);
            startActivityForResult(ii, 0);
        }
        else
        {
            Snackbar.make(scrollView,getResources().getString(R.string.joblist_assetcodemismatched), Snackbar.LENGTH_LONG).show();
        }
    }

    private class JOBNeedUpdateAsynTask extends AsyncTask<Void, Integer, Void>
    {
        MediaType JSON;
        OkHttpClient client1;
        StringBuffer sb;
        long jobneedId, atoPeople, atoGroup, jobStatus, uCode;
        String remark;
        InputStream is;
        byte[] buffer = null;
        int byteread = 0;

        //mItem.getJobneedid(),atop,atog, dialogRemarkEdittext.toString().trim(),dialogStatusSpinner.getSelectedItem().toString()
        public JOBNeedUpdateAsynTask(long jobneedId, long atoPeople, long atoGroup, String remark, long jobStatus, long uCode)
        {
            this.jobneedId=jobneedId;
            this.atoPeople=atoPeople;
            this.atoGroup=atoGroup;
            this.remark=remark;
            this.jobStatus=jobStatus;
            this.uCode=uCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client1 = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
        }


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String date=null;

                URL url = new URL(Constants.BASE_URL); // here is your URL path
                DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                date = df.format(Calendar.getInstance().getTime());

                System.out.println("Current date format: "+date);
                //JSONObject postDataParams = new JSONObject();

                String insertAttachment="INSERT INTO attachment(" +
                        "filepath, filename, narration, gpslocation, datetime," +
                        "cuser, cdtz, muser, mdtz, isdeleted, assetcode, attachmenttype," +
                        "jndid, jobneedid, paid, peoplecode, qsetcode)" +
                        "VALUES (null, null, '"+remark+"', '19,19', now(),'" +
                        ""+uCode+"', now(), '"+uCode+"', now(), false, null, 'REPLY'," +
                        "null, "+jobneedId+", null, '"+uCode+"', null) returning attachmentid;";

                String updateQuery="UPDATE jobneed " +
                        "SET (jobstatus,scantype,receivedonserver,starttime,endtime,gpslocation,remarks,muser,mdtz,performedby,aatog, aatop) =" +
                        "('"+jobStatus+"',"+null+",'"+date+"','"+date+"','"+date+"','19,19','"+remark+"'," +
                        "'"+uCode+"','"+date+"','"+uCode+"',"+atoGroup+","+atoPeople+") where jobneedid ="+jobneedId+" returning jobneedid";

                /*postDataParams.put("servicename", "JobNeedUpdate");
                postDataParams.put("story", "1");//0 to get, 1 to not getting
                postDataParams.put("query1", updateQuery);//stored procedure
                postDataParams.put("query2",insertAttachment);
                postDataParams.put("jobneedid",jobneedId);
                postDataParams.put("jobdesc",mItem.getJobdesc());
                postDataParams.put("jobstatus",jobStatus);
                postDataParams.put("cuser",mItem.getCuser());
                postDataParams.put("remarks",remark);
                postDataParams.put("alertto",alertTo);
                postDataParams.put("assigntype",alertType);

                Log.e("params",postDataParams.toString());*/


                UploadParameters uploadParameters=new UploadParameters();
                uploadParameters.setServicename("JobNeedUpdate");
                uploadParameters.setStory("1");
                /*uploadParameters.setQuery1(updateQuery);
                uploadParameters.setQuery2(insertAttachment);
                uploadParameters.setJobneedid(jobneedId);
                uploadParameters.setJobdesc(mItem.getJobdesc());
                uploadParameters.setJobstatus(jobStatus);
                uploadParameters.setCuser(mItem.getCuser());
                uploadParameters.setRemarks(remark);
                uploadParameters.setAlertto(alertTo);
                uploadParameters.setAssigntype(alertType);*/


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                /*OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();*/

                Gson gson = new Gson();
                String upData = gson.toJson(uploadParameters);
                System.out.println("upData: "+upData);

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

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
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
            }
            catch(Exception e){
                System.out.println("SB: "+e.toString());
            }

            try {
                JSONObject ob = new JSONObject(sb.toString());

                int status = ob.getInt(Constants.RESPONSE_RC);
                String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                System.out.println("status: " + status);
                System.out.println("response: " + resp.toString());


            }catch (Exception e)
            {

            }

            return null;
        }




        /*public String getPostDataString(JSONObject params) throws Exception {

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
        }*/

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);

            //jobNeedDAO.changeJobStatus(mItem.getJobneedid(), dialogStatusSpinner.getSelectedItem().toString());
            dialogRemarkEdittext.setText("");
            activityLayout.setVisibility(View.VISIBLE);
            dialogLayout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        int selectedRadion=radioGroup.getCheckedRadioButtonId();
        spinnerValues=new ArrayList<String>();
        switch (selectedRadion)
        {
            case 0:
                selectedRadioButton=0;
                //spinnerValues.add(0,"TARUN");
                for(int j=0;j<peopleList.size();j++)
                {
                    spinnerValues.add(j,peopleList.get(j).getPeoplename());
                }
                break;
            case 1:
                selectedRadioButton=1;
                //spinnerValues.add(0,"TEST");
                for(int j=0;j<groupList.size();j++)
                {
                    spinnerValues.add(j,groupList.get(j).getGroupname());
                }
                break;
        }

        if(spinnerValues!=null)
        {
            peopleGroupAdpt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerValues);
            peopleGroupAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dialogPeopleGroupSpinner.setAdapter(peopleGroupAdpt);
        }
    }
}
