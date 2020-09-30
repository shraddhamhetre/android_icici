package com.youtility.intelliwiz20.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.GroupDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.JobNeedHistory;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment representing a single AdhocJobListActivity detail screen.
 * This fragment is either contained in a {@link AdhocJobListActivityListActivity}
 * in two-pane mode (on tablets) or a {@link AdhocJobListActivityDetailActivity}
 * on handsets.
 */
public class AdhocJobListActivityDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private AssetDAO assetDAO;
    private QuestionDAO questionDAO;
    private TypeAssistDAO typeAssistDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private PeopleDAO peopleDAO;
    private GroupDAO groupDAO;
    private ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
    private ArrayList<JobNeedHistory>jobNeedAttachmentHistoryArrayList;
    private LinearLayout readingHistoryLinearlayout;
    private LinearLayout readingAttachmentHistoryLinearLayout;
    private TextView attachmentCounttextView;
    private String extStorageDirectory="";
    private TextView readingDetailTitle, attachmentDetailTitle;
    private int isJobExpired=-1;
    private boolean isJobStatusClosed=false;
    private long replyTimestamp=-1l;
    private boolean isPerformed=false;
    private SharedPreferences loginDetailPref;


    private JobNeed mItem;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AdhocJobListActivityDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assetDAO=new AssetDAO(getActivity());
        questionDAO=new QuestionDAO(getActivity());
        jobNeedDetailsDAO=new JobNeedDetailsDAO(getActivity());
        typeAssistDAO=new TypeAssistDAO(getActivity());
        peopleDAO=new PeopleDAO(getActivity());
        groupDAO=new GroupDAO(getActivity());
        loginDetailPref=getActivity().getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        extStorageDirectory = Environment.getExternalStorageDirectory().toString();

        if(jobNeedDetailsArrayList!=null && jobNeedDetailsArrayList.size()>0)
        {
            isPerformed=true;
            /*for(int i=0;i<jobNeedDetailsArrayList.size();i++)
            {
                System.out.println("SqNo: "+jobNeedDetailsArrayList.get(i).getSeqno());
                System.out.println("QuestName: "+jobNeedDetailsArrayList.get(i).getQuestionid());
                System.out.println("MIN: "+jobNeedDetailsArrayList.get(i).getMin());
                System.out.println("Max: "+jobNeedDetailsArrayList.get(i).getMax());
                System.out.println("Options: "+jobNeedDetailsArrayList.get(i).getOption());
                System.out.println("-------------------------------------------------------------------");
            }*/
        }
        else
        {
            isPerformed=false;
        }


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem =AdhocJobListActivityListActivity.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getJobneedid()+"");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        TextView textSrNo;
        TextView textQuestName;
        TextView textQuestAns;
        String assignedToValue=null;

        View rootView = inflater.inflate(R.layout.adhocjoblistactivity_detail, container, false);

        readingHistoryLinearlayout=(LinearLayout)rootView.findViewById(R.id.readingHistoryLinearLayout);
        readingAttachmentHistoryLinearLayout=(LinearLayout)rootView.findViewById(R.id.readingAttachmentHistoryLinearLayout);

        readingDetailTitle=(TextView)rootView.findViewById(R.id.readingdetailTitle);
        readingDetailTitle.setVisibility(View.GONE);
        attachmentDetailTitle=(TextView)rootView.findViewById(R.id.attachmentdetailTitle);
        attachmentDetailTitle.setVisibility(View.GONE);

        //jobexpiry value 0 for future , 1 for in between, 2 for expired
        ((Button)rootView.findViewById(R.id.jobReplyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTaskExpiry();
                if(!isJobStatusClosed && (isJobExpired!=0 && isJobExpired!=2)) {

                    replyTimestamp = System.currentTimeMillis();
                    Intent replyIntent=new Intent(getActivity(), JobneedReplyActivity.class);
                    replyIntent.putExtra("REPLY_TIMESTAMP",replyTimestamp);
                    replyIntent.putExtra("JOBNEEDID", mItem.getJobneedid());
                    startActivityForResult(replyIntent,4);
                }
                else
                {
                    if(typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                        Snackbar.make(v,getResources().getString(R.string.job_has_completed),Snackbar.LENGTH_LONG).show();
                    else if(typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                        Snackbar.make(v,getResources().getString(R.string.job_has_closed),Snackbar.LENGTH_LONG).show();
                    else if(isJobExpired==2)
                        Snackbar.make(v,getResources().getString(R.string.job_has_expired),Snackbar.LENGTH_LONG).show();
                    else if(isJobExpired==0)
                        Snackbar.make(v,getResources().getString(R.string.job_is_future, mItem.getPlandatetime()),Snackbar.LENGTH_LONG).show();
                }
            }
        });

        ((Button)rootView.findViewById(R.id.jobPerformButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTaskExpiry();
                if(!isJobStatusClosed && (isJobExpired!=0 && isJobExpired!=2)) {
                    if (isPerformed) {

                        Intent ii = new Intent(getActivity(), IncidentReportQuestionActivity.class);
                        ii.putExtra("FROM", "JOB");
                        ii.putExtra("ID", mItem.getJobneedid());
                        ii.putExtra("PARENT_ACTIVITY","JOBNEED");
                        ii.putExtra("FOLDER","TASK");
                        startActivityForResult(ii, 0);
                    } else {
                        Snackbar.make(v, getResources().getString(R.string.job_quest_not_assigned), Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                        Snackbar.make(v,getResources().getString(R.string.job_has_completed),Snackbar.LENGTH_LONG).show();
                    else if(typeAssistDAO.getEventTypeCode(mItem.getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                        Snackbar.make(v,getResources().getString(R.string.job_has_closed),Snackbar.LENGTH_LONG).show();
                    else if(isJobExpired==2)
                        Snackbar.make(v,getResources().getString(R.string.job_has_expired),Snackbar.LENGTH_LONG).show();
                    else if(isJobExpired==0)
                        Snackbar.make(v,getResources().getString(R.string.job_is_future, mItem.getPlandatetime()),Snackbar.LENGTH_LONG).show();
                }
            }
        });

        ((Button)rootView.findViewById(R.id.jobPerformDetailsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readingDetailTitle.setVisibility(View.VISIBLE);
                readingHistoryLinearlayout.setVisibility(View.VISIBLE);
            }
        });

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_id)).setText(String.valueOf(mItem.getJobneedid()));
            ((TextView) rootView.findViewById(R.id.item_ticketno)).setText(String.valueOf(mItem.getTicketno()));
            ((TextView) rootView.findViewById(R.id.item_description)).setText(mItem.getJobdesc());
            ((TextView) rootView.findViewById(R.id.item_asset)).setText(assetDAO.getAssetName(mItem.getAssetid()));
            ((TextView) rootView.findViewById(R.id.item_qset)).setText(questionDAO.getQuestionSetName(mItem.getQuestionsetid()));
            ((TextView) rootView.findViewById(R.id.item_tCategory)).setText(typeAssistDAO.getEventTypeName(mItem.getTicketcategory()));
            ((TextView) rootView.findViewById(R.id.item_tAssignedBy)).setText(peopleDAO.getPeopleName(mItem.getCuser()));
            ((TextView) rootView.findViewById(R.id.item_plandate)).setText(mItem.getPlandatetime());
            ((TextView) rootView.findViewById(R.id.item_expireDate)).setText(mItem.getExpirydatetime());
            ((TextView) rootView.findViewById(R.id.item_graceTime)).setText(mItem.getGracetime()+"");

            if(mItem.getGroupid()!=-1) {
                assignedToValue = groupDAO.getGroupName(mItem.getGroupid());
            }
            if(mItem.getPeopleid()!=-1) {
                assignedToValue = peopleDAO.getPeopleName(mItem.getPeopleid());
            }

            /*long backDate=-1;
            backDate=new Date(CommonFunctions.getParseDate(mItem.getPlandatetime())-(mItem.getGracetime() * 60 * 1000)).getTime();
            isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParseDate(mItem.getExpirydatetime()));*/

            checkTaskExpiry();

            /*if(CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParseDate(mItem.getExpirydatetime())))
                isJobExpired=false;
            else
                isJobExpired=true;*/


            /*if(CommonFunctions.isLong(mItem.getPlandatetime()))
            {
                backDate=new Date( Long.valueOf(mItem.getPlandatetime())- (mItem.getGracetime() * 60 * 1000)).getTime();
                if(CommonFunctions.isInBetweenDate(backDate,Long.valueOf(mItem.getExpirydatetime())))
                    isJobExpired=false;
                else
                    isJobExpired=true;
            }
            else
            {
                backDate=new Date(CommonFunctions.getParseDate(mItem.getPlandatetime())-(mItem.getGracetime() * 60 * 1000)).getTime();
                if(CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParseDate(mItem.getExpirydatetime())))
                    isJobExpired=false;
                else
                    isJobExpired=true;
            }*/



            /*Date d=new Date(Long.valueOf(mItem.getExpirydatetime()));
            Date e =new Date();
            if(d.after(e))
                isJobExpired=false;
            else
                isJobExpired=true;*/

            String jobStatus=typeAssistDAO.getEventTypeName(mItem.getJobstatus());
            if(jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED) || jobStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED)) {
                isJobStatusClosed = true;
            }
            else {
                isJobStatusClosed = false;
            }

            ((TextView) rootView.findViewById(R.id.item_tAssignedTo)).setText(assignedToValue);

            attachmentCounttextView=(TextView)rootView.findViewById(R.id.item_tAttachmentCount);
            attachmentCounttextView.setText(mItem.getAttachmentcount());
            attachmentCounttextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetJobNeedReplyHistoryAsynTask getJobNeedReplyHistoryAsynTask=new GetJobNeedReplyHistoryAsynTask();
                    getJobNeedReplyHistoryAsynTask.execute();
                }
            });
            System.out.println("Attachment count: "+mItem.getAttachmentcount());

            if(readingHistoryLinearlayout.getChildCount()>0)
                readingHistoryLinearlayout.removeAllViews();

            jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(mItem.getJobneedid());
            if(jobNeedDetailsArrayList!=null && jobNeedDetailsArrayList.size()>0)
            {
                isPerformed=true;
                readingDetailTitle.setVisibility(View.INVISIBLE);
                readingHistoryLinearlayout.setVisibility(View.INVISIBLE);
                for(int i=0;i<jobNeedDetailsArrayList.size();i++)
                {
                    v = inflater.inflate(R.layout.task_history_reading_row, null);
                    textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
                    textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
                    textQuestAns=(TextView) v.findViewById(R.id.questAnsTextView);

                    textSrNo.setText(String.valueOf(i+1));
                    textQuestName.setText(questionDAO.getQuestionName(jobNeedDetailsArrayList.get(i).getQuestionid()));
                    if(jobNeedDetailsArrayList.get(i).getAnswer()!=null && !jobNeedDetailsArrayList.get(i).getAnswer().equalsIgnoreCase("null"))
                        textQuestAns.setText(getResources().getString(R.string.readingdetail_answer, jobNeedDetailsArrayList.get(i).getAnswer()));
                    else
                        textQuestAns.setText(getResources().getString(R.string.readingdetail_answer, "---"));
                    readingHistoryLinearlayout.addView(v);
                }
            }
            else
                isPerformed=false;
        }

        return rootView;
    }

    private void checkTaskExpiry()
    {
        long backDate=new Date( CommonFunctions.getParse24HrsDate(mItem.getPlandatetime())- (mItem.getGracetime() * 60 * 1000)).getTime();
        isJobExpired=CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate(mItem.getExpirydatetime()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            if(resultCode== FragmentActivity.RESULT_OK)
            {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
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
                }
                else {
                    System.out.println("SB1 JOBHistory: "+response.getStatusLine().getStatusCode());
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
                        Gson gson = new Gson();
                        JSONArray data1 = dataObject.getJSONArray("Data");
                        listType = new TypeToken<ArrayList<JobNeedHistory>>() {
                        }.getType();

                        jobNeedAttachmentHistoryArrayList=gson.fromJson(data1.toString(), listType);

                    }
                }

                //-------------------------------------------------------------------------------------


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
            if( jobNeedAttachmentHistoryArrayList!=null && jobNeedAttachmentHistoryArrayList.size()>0)
            {
                attachmentDetailTitle.setVisibility(View.VISIBLE);
                showAttachmentHistory();
            }
        }

    }

    private class DownloadAttachmentAsyntask extends AsyncTask<Void, Integer, Bitmap>
    {
        String imgInfo;
        Bitmap bitmap;
        String imgPath, imgName;
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

                URL urll = new URL(urlString);
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
            openAlertWithWebView(aVoid);
        }
    }

    private void openAlertWithWebView(Bitmap bitmap)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.adhoc_attchcount));

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

    private void showAttachmentHistory()
    {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        TextView textSrNo;
        TextView textQuestName;
        TextView textQuestAns;

        if(readingAttachmentHistoryLinearLayout.getChildCount()>0)
            readingAttachmentHistoryLinearLayout.removeAllViews();

        for(int i=0;i<jobNeedAttachmentHistoryArrayList.size();i++)
        {
            v = inflater.inflate(R.layout.task_history_reading_row, null);
            textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
            textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
            textQuestAns=(TextView) v.findViewById(R.id.questAnsTextView);

            textSrNo.setText(String.valueOf(i+1));
            textQuestName.setTag(i);

            textQuestName.setText(jobNeedAttachmentHistoryArrayList.get(i).getFilename());

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
                        DownloadAttachmentAsyntask downloadAttachmentAsyntask=new DownloadAttachmentAsyntask(jobNeedAttachmentHistoryArrayList.get((int)v.getTag()).getFilepath()+"~"+jobNeedAttachmentHistoryArrayList.get((int)v.getTag()).getFilename());
                        downloadAttachmentAsyntask.execute();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            readingAttachmentHistoryLinearLayout.addView(v);
        }
    }
}
