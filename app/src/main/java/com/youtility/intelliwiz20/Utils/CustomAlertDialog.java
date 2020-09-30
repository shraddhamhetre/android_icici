package com.youtility.intelliwiz20.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by PrashantD on 8/9/17.
 */

public class CustomAlertDialog {
    private Context context;
    private IDialogEventListeners dialogInterface;
    private SharedPreferences deviceInfoPref;
    Intent intent1;

    public CustomAlertDialog(Context context, IDialogEventListeners dialogInterface)
    {
        this.context=context;
        this.dialogInterface=dialogInterface;
    }

    public void commonDialog(String title,String message)
    {
        final Dialog dialog = new Dialog(context, R.style.Theme_Alertdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ok_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        TextView alertMessage=(TextView)dialog.findViewById(R.id.alertMessage);

        alertTitle.setText(title);
        alertMessage.setText(message);

        Button okBtn=(Button) dialog.findViewById(R.id.alertOK);

        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                dialogInterface.onNegativeClickListener(0,"","");
            }
        });


        dialog.show();
    }

    public void commonDialog1(String title,String message)
    {
        final Dialog dialog = new Dialog(context, R.style.Theme_Alertdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ok_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        TextView alertMessage=(TextView)dialog.findViewById(R.id.alertMessage);

        alertTitle.setText(title);
        alertMessage.setText(message);

        Button okBtn=(Button) dialog.findViewById(R.id.alertOK);

        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    public void commonDialog2(String title, String message, final int accessValue)
    {

        final Dialog dialog = new Dialog(context, R.style.Theme_Alertdialog2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.retry_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        TextView alertMessage=(TextView)dialog.findViewById(R.id.alertMessage);

        alertTitle.setText(title);
        alertMessage.setText(message);

        Button okBtn=(Button) dialog.findViewById(R.id.alertRETRY);

        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (accessValue == 2){
                    intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent1);
                    dialog.dismiss();
                }else  if (accessValue == 3){
                    intent1 = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    context.startActivity(intent1);
                    dialog.dismiss();
                }
                else  if (accessValue == 4){
                    intent1 = new Intent(Settings.ACTION_SETTINGS);
                    context.startActivity(intent1);
                    dialog.dismiss();
                }else  if (accessValue == 5){
                    System.out.println("fetch geo cordinates===");
                    dialog.dismiss();
                }
                /*intent1=new Intent("android.location.GPS_ENABLED_CHANGE");
                intent1.putExtra("enabled", true);
                context.sendBroadcast(intent1);*/
            }
        });

        dialog.show();
    }



    public void showPendingEntryDialog(String title,String message)
    {
        final Dialog dialog = new Dialog(context, R.style.Theme_Alertdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ok_details_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        TextView alertMessage=(TextView)dialog.findViewById(R.id.alertMessage);

        alertTitle.setText(title);
        alertMessage.setText(message);

        Button okBtn=(Button) dialog.findViewById(R.id.alertOK);
        Button detailsBtn=(Button) dialog.findViewById(R.id.alertDetails);

        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogInterface.onPoistiveClickListener(2,"","");
            }
        });

        dialog.show();
    }

    public void JOBInfoDialog(String title,String totalCnt, String completCount, String pendingCount, String autoCnt)
    {
        final Dialog dialog = new Dialog(context, R.style.Theme_Alertdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.jobneed_info_dialog_view);
        dialog.setCancelable(false);
//schedule, complete, pending, closed
        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        TextView tCount=(TextView)dialog.findViewById(R.id.totalTaskCount);
        TextView aCount=(TextView)dialog.findViewById(R.id.totalAssignedCount);
        TextView cCount=(TextView)dialog.findViewById(R.id.totalCompletedCount);
        TextView autoCCount=(TextView)dialog.findViewById(R.id.totalAutoClosedCount);
        PieChart pieChart = (PieChart) dialog.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        ProgressBar pProgress = (ProgressBar) dialog.findViewById(R.id.circle_progress_bar_pendingtask);
        ProgressBar cProgress = (ProgressBar) dialog.findViewById(R.id.circle_progress_bar_completedtask);
        ProgressBar aProgress = (ProgressBar) dialog.findViewById(R.id.circle_progress_bar_closedtask);
        if(Integer.parseInt(totalCnt)!=0)
        {
            pProgress.setProgress(((Integer.parseInt(pendingCount)*100)/Integer.parseInt(totalCnt)));
            cProgress.setProgress(((Integer.parseInt(completCount)*100)/Integer.parseInt(totalCnt)));
            aProgress.setProgress(((Integer.parseInt(autoCnt)*100)/Integer.parseInt(totalCnt)));

            //------------------------------------------------------

            ArrayList<Entry> yvalues = new ArrayList<Entry>();
            yvalues.add(new Entry(Float.valueOf(pendingCount), 0));
            yvalues.add(new Entry(Float.valueOf(completCount), 1));
            yvalues.add(new Entry(Float.valueOf(autoCnt), 2));


            PieDataSet dataSet = new PieDataSet(yvalues, "");

            ArrayList<String> xVals = new ArrayList<String>();

            xVals.add(context.getResources().getString(R.string.joblist_pending_title)+":"+pendingCount);
            xVals.add(context.getResources().getString(R.string.joblist_completed_title)+":"+completCount);
            xVals.add(context.getResources().getString(R.string.joblist_autoclosed_title)+":"+autoCnt);


            PieData data = new PieData(xVals, dataSet);
            // In Percentage term
            data.setValueFormatter(new PercentFormatter());
            // Default value
            //data.setValueFormatter(new LargeValueFormatter());
            pieChart.setData(data);
            pieChart.setDescription(context.getResources().getString(R.string.joblist_schedulecount_title)+" : "+totalCnt);

            pieChart.setDrawHoleEnabled(false);
            pieChart.setTransparentCircleRadius(15f);
            pieChart.setHoleRadius(15f);

            int[] TEST_COLOR={Color.rgb(241, 196, 15), Color.rgb(92, 198, 81), Color.rgb(241, 43, 15 )};


            dataSet.setColors(TEST_COLOR);
            data.setValueTextSize(7f);
            data.setValueTextColor(Color.DKGRAY);

            pieChart.animateXY(1400, 1400);

            //--------------------------------------------------------

        }


        alertTitle.setText(title);
        tCount.setText(totalCnt);
        aCount.setText(pendingCount);
        cCount.setText(completCount);
        autoCCount.setText(autoCnt);

        Button okBtn=(Button) dialog.findViewById(R.id.alertOK);

        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                dialogInterface.onNegativeClickListener(0,"","");
            }
        });


        dialog.show();
    }


    public void assetInfoDialog(String title,String totalCnt, String maintanceCount, String workingCount, String standbyCnt)
    {
        final Dialog dialog = new Dialog(context, R.style.Theme_Alertdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.jobneed_info_dialog_view);
        dialog.setCancelable(false);
//schedule, complete, pending, closed
        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        PieChart pieChart = (PieChart) dialog.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        if(Integer.parseInt(totalCnt)!=0)
        {
            //------------------------------------------------------

            ArrayList<Entry> yvalues = new ArrayList<Entry>();
            yvalues.add(new Entry(Float.valueOf(standbyCnt), 0));
            yvalues.add(new Entry(Float.valueOf(workingCount), 1));
            yvalues.add(new Entry(Float.valueOf(maintanceCount), 2));


            PieDataSet dataSet = new PieDataSet(yvalues, "");

            ArrayList<String> xVals = new ArrayList<String>();

            xVals.add(context.getResources().getString(R.string.assetlistactivity_status_color_standby)+": "+standbyCnt);
            xVals.add(context.getResources().getString(R.string.assetlistactivity_status_color_working)+": "+workingCount);
            xVals.add(context.getResources().getString(R.string.assetlistactivity_status_color_maintenance)+": "+maintanceCount);


            PieData data = new PieData(xVals, dataSet);
            // In Percentage term
            data.setValueFormatter(new PercentFormatter());
            // Default value
            //data.setValueFormatter(new LargeValueFormatter());
            pieChart.setData(data);
            pieChart.setDescription(context.getResources().getString(R.string.assetsummary_totalasset)+totalCnt);

            pieChart.setDrawHoleEnabled(false);
            pieChart.setTransparentCircleRadius(15f);
            pieChart.setHoleRadius(15f);

            int[] TEST_COLOR={Color.rgb(241, 196, 15), Color.rgb(92, 198, 81), Color.rgb(241, 43, 15 )};


            dataSet.setColors(TEST_COLOR);
            data.setValueTextSize(7f);
            data.setValueTextColor(Color.DKGRAY);

            pieChart.animateXY(1400, 1400);

            //--------------------------------------------------------

        }


        alertTitle.setText(title);

        Button okBtn=(Button) dialog.findViewById(R.id.alertOK);

        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                dialogInterface.onNegativeClickListener(0,"","");
            }
        });


        dialog.show();
    }

    public void showYesNoAlertBox(String title,String msg,final String msg2,final int type)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setIcon(R.mipmap.ic_launcher_round);
        alert.setMessage(msg);

        alert.setPositiveButton(context.getResources().getString(R.string.button_yes), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
                dialogInterface.onPoistiveClickListener(type,msg2,"");
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.button_no), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                dialogInterface.onNegativeClickListener(type,msg2,"");
            }
        });

        alert.show();
        alert.create();
        alert.setCancelable(false);
    }

    public void showUpdateApplicationAlertBox(String msg,final String msg2,final int type)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getResources().getString(R.string.alerttitle));
        alert.setIcon(R.mipmap.ic_launcher_round);
        alert.setMessage(msg);

        alert.setPositiveButton(context.getResources().getString(R.string.button_update), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
                dialogInterface.onPoistiveClickListener(type,msg2,"");
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.button_notnow), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                dialogInterface.onNegativeClickListener(type,msg2,"");
            }
        });

        alert.show();
        alert.create();
        alert.setCancelable(false);
    }

    public void customButtonAlertBox(String positiveBtn, String negativeBtn, String msg,final String title, final int type)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setIcon(R.mipmap.ic_launcher_round);
        alert.setMessage(msg);

        alert.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                dialog.dismiss();
                dialogInterface.onPoistiveClickListener(type,"","");
            }
        });
        alert.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                dialogInterface.onNegativeClickListener(type,"","");
            }
        });

        alert.show();
        alert.create();
        alert.setCancelable(false);
    }

    public void changePasswordForm(String title,String message)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password_form_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText changePass_old=(EditText)dialog.findViewById(R.id.changePass_old);
        final EditText changePass_newpass=(EditText)dialog.findViewById(R.id.changePass_newpass);
        final EditText changePass_reenterpass=(EditText)dialog.findViewById(R.id.changePass_reenterpass);

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(changePass_old!=null && changePass_old.getText().toString().trim().length()>0 &&
                        changePass_newpass!=null && changePass_newpass.getText().toString().trim().length()>0 &&
                        changePass_reenterpass!=null && changePass_reenterpass.getText().toString().trim().length()>0)
                {
                    if(changePass_newpass.getText().toString().trim().equalsIgnoreCase(changePass_reenterpass.getText().toString().trim())) {
                        dialog.dismiss();
                        dialogInterface.onPoistiveClickListener(0, changePass_old.getText().toString().trim() + "," + changePass_newpass.getText().toString(), "");
                    }
                    else
                    {
                        if(changePass_newpass!=null && changePass_newpass.getText().toString().trim().length()>0)
                            changePass_newpass.setError("New Password and Re-Enter Password are not matched!!");
                    }
                }
                else {
                    if(changePass_old!=null)
                        changePass_old.setError("Please fill all information..");
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    public void custFeedbackForm(String title,String message)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_feedback_form_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText editText=(EditText)dialog.findViewById(R.id.alertMessage);
        final EditText feedback_name=(EditText)dialog.findViewById(R.id.feeback_name);
        final EditText feedback_email=(EditText)dialog.findViewById(R.id.feeback_email);
        final EditText feedback_contact=(EditText)dialog.findViewById(R.id.feeback_contact);

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editText!=null && editText.getText().toString().trim().length()>0 &&
                        feedback_name!=null && feedback_name.getText().toString().trim().length()>0 &&
                        feedback_email!=null && feedback_email.getText().toString().trim().length()>0 &&
                        feedback_contact!=null && feedback_contact.getText().toString().trim().length()>0)
                {
                    dialog.dismiss();
                    dialogInterface.onPoistiveClickListener(0,editText.getText().toString().trim()+","+feedback_name.getText().toString()+","+feedback_email.getText().toString()+","+feedback_contact.getText().toString(), "");
                }
                else {
                    if(editText!=null)
                        editText.setError(context.getResources().getString(R.string.feedbackform_fillallinfo));
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    public void addOtherSiteForm(String title, String message, final int type)
    {
        SharedPreferences otherSiteListPref;
        List<String> retivedList=null;

        System.out.println("title= "+ title + "message= "+ message + "type= " +type);
        /*final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_othersite_form_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText site_name=(EditText)dialog.findViewById(R.id.othersite_name);
        final EditText site_code=(EditText)dialog.findViewById(R.id.othersite_code);
        final EditText site_clientname=(EditText)dialog.findViewById(R.id.othersite_clientname);
        site_clientname.setText(message);

        Button doneBtn=(Button) dialog.findViewById(R.id.othersite_submit);
        Button resetBtn=(Button) dialog.findViewById(R.id.othersite_cancel);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(site_name!=null && site_name.getText().toString().trim().length()>0 &&
                        site_code!=null && site_code.getText().toString().trim().length()>0 )
                {
                    dialog.dismiss();
                    dialogInterface.onPoistiveClickListener(type,site_name.getText().toString().trim()+"~"+site_code.getText().toString()+"~"+site_clientname.getText().toString(), "");
                }
                else {
                    if(site_name!=null)
                        site_name.setError("Please fill all fields..");
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });*/

        //String[] othSites = {"Other site 1", "site 2", "Other site 3", "site 4", "Other site 5", "site 6", "Other site 7", "site 8"};

        otherSiteListPref=context.getSharedPreferences(Constants.OTHER_SITE_LIST_PREF,Context.MODE_PRIVATE);

        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_othersite_form_alert_view);
        dialog.setCancelable(false);

        Set<String> set = otherSiteListPref.getStringSet(Constants.OTHER_SITES, null);
        if(set!=null)
            retivedList=new ArrayList<>(set);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, retivedList);

            TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
            alertTitle.setText(title);
            final AutoCompleteTextView other_listedsite_name=(AutoCompleteTextView)dialog.findViewById(R.id.othersite_name_autocomplete);
            other_listedsite_name.setAdapter(adapter);
            other_listedsite_name.setThreshold(1);
            other_listedsite_name.setTextColor(context.getResources().getColor(R.color.text_color));

            final EditText other_sitename=(EditText)dialog.findViewById(R.id.othersite_code);
            final EditText site_clientname=(EditText)dialog.findViewById(R.id.othersite_clientname);
            site_clientname.setText(message);

            other_listedsite_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s!=null && s.length()>0)
                        other_sitename.setText("");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            other_sitename.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s!=null && s.length()>0)
                    {
                        other_listedsite_name.setText("");
                        System.out.println(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            Button doneBtn=(Button) dialog.findViewById(R.id.othersite_submit);
            Button resetBtn=(Button) dialog.findViewById(R.id.othersite_cancel);

            doneBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(other_listedsite_name.getText().toString().trim().length()>0 )
                    {
                        dialog.dismiss();
                        System.out.println("type111"+ type);

                        dialogInterface.onPoistiveClickListener(type,other_listedsite_name.getText().toString().trim()+"~"+other_sitename.getText().toString()+"~"+site_clientname.getText().toString(), "");
                    }
                    else
                    {
                        if(other_sitename.getText().toString().trim().length()>0)
                        {
                            dialog.dismiss();
                            String formatedSiteName=CommonFunctions.escapeMetaCharacters(other_sitename.getText().toString().trim());
                            System.out.println("type111"+ type);
                            dialogInterface.onPoistiveClickListener(type,other_listedsite_name.getText().toString().trim()+"~"+other_sitename.getText().toString().trim()+"~"+site_clientname.getText().toString(), "");
                        }
                        else
                        {
                            other_listedsite_name.setError(context.getResources().getString(R.string.othersite_addnew_error));
                        }
                    }
                }
            });
            resetBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();

                }
            });

            dialog.show();


    }


    public void custEnteredScanType(String title,String message, final int type)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_entered_scantype_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText editText=(EditText)dialog.findViewById(R.id.asset_code);

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editText!=null && editText.getText().toString().trim().length()>0 )
                {
                    dialog.dismiss();
                    dialogInterface.onPoistiveClickListener(type,editText.getText().toString().trim(), "");
                }
                else {
                    if(editText!=null)
                        editText.setError(context.getResources().getString(R.string.scantypealert_enterassetcode));
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });


        dialog.show();
    }


    public void custPeopleAttendanceSheet(String title,String message, final int type)
    {


        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_entered_scantype_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText editText=(EditText)dialog.findViewById(R.id.asset_code);

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editText!=null && editText.getText().toString().trim().length()>0 )
                {
                    dialog.dismiss();
                    dialogInterface.onPoistiveClickListener(type,editText.getText().toString().trim(), "");
                }
                else {
                    if(editText!=null)
                        editText.setError(context.getResources().getString(R.string.scantypealert_enterassetcode));
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });


        dialog.show();
    }


    public void syncSummaryReport()
    {
        SharedPreferences syncSummaryPref=context.getSharedPreferences(Constants.SYNC_SUMMARY_PREF,Context.MODE_PRIVATE);
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_sync_summary_view);
        dialog.setCancelable(false);
        String[] adhocCnt=syncSummaryPref.getString(Constants.SYNC_SUMMARY_ADHOC_COUNT,"0:0").split(":");
        final TextView adhocSCnt=(TextView) dialog.findViewById(R.id.adhocSucceedCnt);
        final TextView adhocFCnt=(TextView) dialog.findViewById(R.id.adhocFailedCnt);
        adhocSCnt.setText(adhocCnt[0]);
        adhocFCnt.setText(adhocCnt[1]);

        String[] irCnt=syncSummaryPref.getString(Constants.SYNC_SUMMARY_IR_COUNT,"0:0").split(":");
        final TextView irSCnt=(TextView) dialog.findViewById(R.id.irSucceedCnt);
        final TextView irFCnt=(TextView) dialog.findViewById(R.id.irFailedCnt);
        irSCnt.setText(irCnt[0]);
        irFCnt.setText(irCnt[1]);

        String[] jnupdateCnt=syncSummaryPref.getString(Constants.SYNC_SUMMARY_JNUPDATE_COUNT,"0:0").split(":");
        final TextView jndSCnt=(TextView) dialog.findViewById(R.id.jnupdateSucceedCnt);
        final TextView jndFCnt=(TextView) dialog.findViewById(R.id.jnupdateFailedCnt);
        jndSCnt.setText(jnupdateCnt[0]);
        jndFCnt.setText(jnupdateCnt[1]);

        String[] emprefCnt=syncSummaryPref.getString(Constants.SYNC_SUMMARY_EMPREF_COUNT,"0:0").split(":");
        final TextView emprefSCnt=(TextView) dialog.findViewById(R.id.emprefSucceedCnt);
        final TextView emprefFCnt=(TextView) dialog.findViewById(R.id.emprefFailedCnt);
        emprefSCnt.setText(emprefCnt[0]);
        emprefFCnt.setText(emprefCnt[1]);

        String[] saCnt=syncSummaryPref.getString(Constants.SYNC_SUMMARY_SA_COUNT,"0:0").split(":");
        final TextView saSCnt=(TextView) dialog.findViewById(R.id.saSucceedCnt);
        final TextView saFCnt=(TextView) dialog.findViewById(R.id.saFailedCnt);
        saSCnt.setText(saCnt[0]);
        saFCnt.setText(saCnt[1]);

        final TextView readingCaptTV=(TextView) dialog.findViewById(R.id.readingCapturedTextview);
        if(syncSummaryPref.getString(Constants.SYNC_SUMMARY_MAIL_SENT,"FALSE").equalsIgnoreCase("FALSE"))
            readingCaptTV.setVisibility(View.GONE);
        else
            readingCaptTV.setVisibility(View.VISIBLE);

        Button doneBtn=(Button) dialog.findViewById(R.id.syncSummaryOk);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    public void custSiteChkOutRemark(String title,String message, final int type)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_entered_checkoutremark_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText editText=(EditText)dialog.findViewById(R.id.chkoutremark);

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editText!=null && editText.getText().toString().trim().length()>0 )
                {
                    dialog.dismiss();
                    dialogInterface.onPoistiveClickListener(type,editText.getText().toString().trim(), "");
                }
                else {
                    if(editText!=null)
                        editText.setError(context.getResources().getString(R.string.alertdialog_sitechkoutremark));
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    public void takeEnteredEmpCode(String title,String message, final int type)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_entered_checkoutremark_alert_view);
        dialog.setCancelable(false);

        TextView alertTitle=(TextView)dialog.findViewById(R.id.alertTitle);
        alertTitle.setText(title);
        final EditText editText=(EditText)dialog.findViewById(R.id.chkoutremark);

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editText!=null && editText.getText().toString().trim().length()>0 )
                {
                    dialog.dismiss();
                    dialogInterface.onPoistiveClickListener(type,editText.getText().toString().trim(), "");
                }
                else {
                    if(editText!=null)
                        editText.setError(context.getResources().getString(R.string.alertdialog_enteremp_or_ticketnumber));
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });


        dialog.show();
    }
}
