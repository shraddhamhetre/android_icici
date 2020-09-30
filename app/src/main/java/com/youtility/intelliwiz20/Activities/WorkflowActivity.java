package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.android.CaptureActivity;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WorkflowActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private WebView webView;
    private AlertDialog alertDialog;
    private QuestionDAO questionDAO;
    private CharSequence[] workflowTemplateList  =null;// {"People Posting Request","QR Code"};
    private ArrayList<QuestionSet> questionSetArrayList=null;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workflow);

        questionDAO=new QuestionDAO(WorkflowActivity.this);
        questionSetArrayList=questionDAO.getWorkFlowQuestionSet();
        if(questionSetArrayList!=null && questionSetArrayList.size()>0)
        {
            workflowTemplateList=new String[questionSetArrayList.size()];//questionSetArrayList.toArray(new CharSequence[questionSetArrayList.size()]);
            for(int i=0;i<questionSetArrayList.size();i++)
            {
                workflowTemplateList[i]=questionSetArrayList.get(i).getQsetname();
            }
            showAlertOptions();
        }

        webView = (WebView) findViewById(R.id.fullscreen_content);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_qrcode)
        {
            Intent intent=new Intent(WorkflowActivity.this, CaptureActivity.class);
            intent.putExtra("FROM","WORKFLOW");
            startActivityForResult(intent,1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertOptions()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkflowActivity.this);


        builder.setTitle(getResources().getString(R.string.joblist_selecturchoice_title));

        builder.setSingleChoiceItems(workflowTemplateList, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        alertDialog.dismiss();
                        loadWebViewURL(questionSetArrayList.get(item).getUrl().trim());

                    }
                })
                .setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void loadWebViewURL(String url)
    {
        final ProgressDialog pd = ProgressDialog.show(WorkflowActivity.this, "", "Please wait...", true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pd.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                pd.dismiss();
                Toast.makeText(WorkflowActivity.this,
                        "The Requested Page Does Not Exist", Toast.LENGTH_LONG).show();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        webView.loadUrl(url);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    loadWebViewURL(data.getStringExtra("SCAN_RESULT"));
                }
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(WorkflowActivity.this, isConnected,webView);
    }
}
