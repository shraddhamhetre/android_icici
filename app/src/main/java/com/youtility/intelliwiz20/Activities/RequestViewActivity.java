package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.youtility.intelliwiz20.Adapters.RequestHistoryViewAdapter;
import com.youtility.intelliwiz20.Adapters.RequestViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.VerticalTextView;

import java.util.ArrayList;

public class RequestViewActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    VerticalTextView txtTemplates, txtRequests;
    LinearLayout llTemplate, llRequests;
    ListView templateListView, requestListView;
    private QuestionDAO questionDAO;
    private TypeAssistDAO typeAssistDAO;
    private RequestViewAdapter requestViewAdapter;
    private RequestHistoryViewAdapter requestHistoryViewAdapter;
    ArrayList<TypeAssist> requestTypeList;
    private SharedPreferences adhocPref;
    private ArrayList<JobNeed>requestJobneedArraylist;
    private JobNeedDAO jobNeedDAO;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view);

        llTemplate = (LinearLayout) findViewById(R.id.llTemplate);
        llRequests = (LinearLayout) findViewById(R.id.llRequests);

        txtTemplates = (VerticalTextView) findViewById(R.id.txtTemplates);
        txtRequests = (VerticalTextView) findViewById(R.id.txtRequest);

        templateListView=(ListView)findViewById(R.id.requestTemplateListView);
        requestListView=(ListView)findViewById(R.id.requestListView);
        questionDAO=new QuestionDAO(RequestViewActivity.this);
        typeAssistDAO=new TypeAssistDAO(RequestViewActivity.this);
        jobNeedDAO=new JobNeedDAO(RequestViewActivity.this);

        adhocPref=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF,MODE_PRIVATE);

        requestTypeList=new ArrayList<>();
        requestTypeList=typeAssistDAO.getEventList("Request");

        if(requestTypeList!=null && requestTypeList.size()>0)
        {
            requestViewAdapter=new RequestViewAdapter(RequestViewActivity.this, requestTypeList);
            templateListView.setAdapter(requestViewAdapter);
        }

        txtTemplates.setOnClickListener(this);
        txtRequests.setOnClickListener(this);

        templateListView.setOnItemClickListener(this);
        requestListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtTemplates:
                showTemplatesInForm();
                break;
            case R.id.txtRequest:
                showRequestInForm();
                break;
        }
    }

    private void showTemplatesInForm() {
        System.out.println("showTemplatesInForm");

        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llRequests.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.15f;
        llRequests.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llTemplate.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.85f;
        llTemplate.requestLayout();

        txtRequests.setVisibility(View.VISIBLE);
        txtTemplates.setVisibility(View.GONE);
        Animation translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left_to_right);
        llTemplate.startAnimation(translate);

        if(requestTypeList!=null && requestTypeList.size()>0)
        {
            System.out.println("show request view activity");
            requestViewAdapter=new RequestViewAdapter(RequestViewActivity.this, requestTypeList);
            templateListView.setAdapter(requestViewAdapter);
        }else {
            Snackbar.make(navigation,getResources().getString(R.string.data_not_found),Snackbar.LENGTH_LONG).show();

        }

    }

    private void showRequestInForm() {
        System.out.println("showRequestInForm");

        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llTemplate.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.15f;
        llTemplate.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llRequests.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.85f;
        llRequests.requestLayout();

        txtRequests.setVisibility(View.GONE);
        txtTemplates.setVisibility(View.VISIBLE);
        Animation translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right_to_left);
        llRequests.startAnimation(translate);

        requestJobneedArraylist=new ArrayList<>();
        requestJobneedArraylist=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_INTERNAL_REQUEST,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
        requestHistoryViewAdapter=new RequestHistoryViewAdapter(RequestViewActivity.this,requestJobneedArraylist);
        requestListView.setAdapter(requestHistoryViewAdapter);

    }


    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(RequestViewActivity.this);
        switch (parent.getId())
        {
            case R.id.requestTemplateListView:
                dialog.show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TypeAssist typeAssist=(TypeAssist) parent.getAdapter().getItem(position);
                        System.out.println("Selected Request name: "+typeAssist.getTacode()+" : "+typeAssist.getTaid());
                        adhocPref.edit().putString(Constants.ADHOC_ASSET,typeAssist.getTaname()).apply();
                        ArrayList<QuestionSet> questionSetArrayList=questionDAO.getRequestQuestionSet(typeAssist.getTaid());
                        if(questionSetArrayList!=null && questionSetArrayList.size()>0)
                        {
                            //showDialog(questionSetArrayList);
                            adhocPref.edit().putLong(Constants.ADHOC_TIMESTAMP, System.currentTimeMillis()).apply();
                            adhocPref.edit().putLong(Constants.ADHOC_QSET,questionSetArrayList.get(0).getQuestionsetid()).apply();
                            adhocPref.edit().putString(Constants.ADHOC_QSET_NAME,questionSetArrayList.get(0).getQsetname()).apply();

                            System.out.println("questionSetArrayList.get(0).getQuestionsetid(): "+questionSetArrayList.get(0).getQuestionsetid());
                            dialog.dismiss();
                            Intent fillReportIntent = new Intent(RequestViewActivity.this, IncidentReportQuestionActivity.class);
                            fillReportIntent.putExtra("FROM", "REQUEST");
                            fillReportIntent.putExtra("ID", System.currentTimeMillis());
                            fillReportIntent.putExtra("QUESTIONSETID", questionSetArrayList.get(0).getQuestionsetid());
                            fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                            fillReportIntent.putExtra("FOLDER", "REQUEST");
                            startActivityForResult(fillReportIntent, 0);
                        }else {
                            System.out.println("ques length"+ questionSetArrayList.size());
                        }
                    }
                });
                /*dialog.setMessage("please wait.");
                dialog.show();
                TypeAssist typeAssist=(TypeAssist) parent.getAdapter().getItem(position);
                System.out.println("Selected Request name: "+typeAssist.getTacode()+" : "+typeAssist.getTaid());
                adhocPref.edit().putString(Constants.ADHOC_ASSET,typeAssist.getTaname()).apply();
                ArrayList<QuestionSet> questionSetArrayList=questionDAO.getRequestQuestionSet(typeAssist.getTaid());
                if(questionSetArrayList!=null && questionSetArrayList.size()>0)
                {
                    //showDialog(questionSetArrayList);
                    adhocPref.edit().putLong(Constants.ADHOC_TIMESTAMP, System.currentTimeMillis()).apply();
                    adhocPref.edit().putLong(Constants.ADHOC_QSET,questionSetArrayList.get(0).getQuestionsetid()).apply();
                    adhocPref.edit().putString(Constants.ADHOC_QSET_NAME,questionSetArrayList.get(0).getQsetname()).apply();

                    System.out.println("questionSetArrayList.get(0).getQuestionsetid(): "+questionSetArrayList.get(0).getQuestionsetid());

                    dialog.dismiss();

                    Intent fillReportIntent = new Intent(RequestViewActivity.this, IncidentReportQuestionActivity.class);
                    fillReportIntent.putExtra("FROM", "REQUEST");
                    fillReportIntent.putExtra("ID", System.currentTimeMillis());
                    fillReportIntent.putExtra("QUESTIONSETID", questionSetArrayList.get(0).getQuestionsetid());
                    fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                    fillReportIntent.putExtra("FOLDER", "REQUEST");
                    startActivityForResult(fillReportIntent, 0);
                }
                else
                    dialog.dismiss();*/

                break;
            case R.id.requestListView:
                JobNeed jobNeed= (JobNeed) parent.getAdapter().getItem(position);
                System.out.println("Details: "+jobNeed.getJobdesc()+" "+jobNeed.getRemarks());
                Intent reqDetailsIntent=new Intent(RequestViewActivity.this, RequestViewDetailsActivity.class);
                reqDetailsIntent.putExtra("REQUESTID",jobNeed.getJobneedid());
                startActivityForResult(reqDetailsIntent,1);
                break;
        }

    }

    public void showDialog(final ArrayList<QuestionSet> questionSetArrayList) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(RequestViewActivity.this);
        builderSingle.setTitle(getResources().getString(R.string.select_quest_set_title1));
        //builderSingle.setTitle(Html.fromHtml("<p style='color=#ffffff; background-color=#000000'>"+getResources().getString(R.string.select_quest_set_title)+"</p>"));


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RequestViewActivity.this, android.R.layout.select_dialog_item);

        for (QuestionSet questionSet: questionSetArrayList) {
            arrayAdapter.add(questionSet.getQsetname().trim());
        }

        builderSingle.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adhocPref.edit().putLong(Constants.ADHOC_TIMESTAMP, System.currentTimeMillis()).apply();
                adhocPref.edit().putLong(Constants.ADHOC_QSET,questionSetArrayList.get(which).getQuestionsetid()).apply();
                adhocPref.edit().putString(Constants.ADHOC_QSET_NAME,questionSetArrayList.get(which).getQsetname()).apply();

                Intent fillReportIntent = new Intent(RequestViewActivity.this, IncidentReportQuestionActivity.class);
                fillReportIntent.putExtra("FROM", "REQUEST");
                fillReportIntent.putExtra("ID", System.currentTimeMillis());
                fillReportIntent.putExtra("QUESTIONSETID", questionSetArrayList.get(which).getQuestionsetid());
                fillReportIntent.putExtra("PARENT_ACTIVITY", "JOBNEED");
                fillReportIntent.putExtra("FOLDER", "REQUEST");
                startActivityForResult(fillReportIntent, 0);

            }
        });
        builderSingle.show();
        builderSingle.setCancelable(false);

    }
}
