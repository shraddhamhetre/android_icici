package com.youtility.intelliwiz20.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.Adapters.SubmitAttendanceListViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.AttendanceSheetDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.Interfaces.IRefreshListView;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.ResponseClientUrlData;
import com.youtility.intelliwiz20.Model.SubmitAttendance;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.RetrofitERPURL;
import com.youtility.intelliwiz20.Utils.RetrofitServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitMonthlyAttendanceActivity extends AppCompatActivity implements IRefreshListView {
    private PeopleDAO peopleDAO;
    private ListView amUserAttendanceListview;
    private int currentMonthDays=0;
    private ArrayList<SubmitAttendance>submitAttendanceArrayList;
    private ArrayList<People>peopleArrayList;
    private SubmitAttendance submitAttendance;
    private SubmitAttendanceListViewAdapter submitAttendanceListViewAdapter;
    private AttendanceSheetDAO attendanceSheetDAO;
    private TextView getFinalTotalCount;
    private TextView pdCountTextView, edCountTextView;
    private int pdCount=0, edCount=0;
    private SharedPreferences loginPref;
    AlertDialog alertDialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_monthly_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setSubtitle(CommonFunctions.getCurrentMonthName(System.currentTimeMillis()));

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);


        peopleDAO=new PeopleDAO(SubmitMonthlyAttendanceActivity.this);
        attendanceSheetDAO=new AttendanceSheetDAO(SubmitMonthlyAttendanceActivity.this);

        amUserAttendanceListview=(ListView)findViewById(R.id.amUserattendanceListview);
        getFinalTotalCount=(TextView)findViewById(R.id.getFinalTotalTextView);
        pdCountTextView=(TextView)findViewById(R.id.pdCountTextView);
        edCountTextView=(TextView)findViewById(R.id.edCountTextView);

        //currentMonthYear=(TextView)findViewById(R.id.currentMonthYear);
        submitAttendanceArrayList=new ArrayList<>();
        Calendar mycal = new GregorianCalendar();
        currentMonthDays=mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //currentMonthYear.setText(CommonFunctions.getCurrentMonth(System.currentTimeMillis()));

        if(attendanceSheetDAO.getCount()==0) {
            peopleArrayList = peopleDAO.getPeopleList();
            if (peopleArrayList != null && peopleArrayList.size() > 0) {
                for (int i = 0; i < peopleArrayList.size(); i++) {
                    submitAttendance = new SubmitAttendance();
                    submitAttendance.setPeopleId(peopleArrayList.get(i).getPeopleid());
                    submitAttendance.setPeopleName(peopleArrayList.get(i).getPeoplename());
                    submitAttendance.setPeopleLoginId(peopleArrayList.get(i).getLoginid());

                    submitAttendance.setAbsentOrPresent(1);
                    submitAttendance.setAttendanceDates("");
                    submitAttendance.setApprovalStatus(0);
                    submitAttendance.setAttendanceMonth(CommonFunctions.getCurrentMonthName(System.currentTimeMillis()));

                    submitAttendance.setSiteid(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
                    submitAttendance.setCdtz("1970-01-01 00:00:00");
                    submitAttendance.setMdtz("1970-01-01 00:00:00");
                    submitAttendance.setCuser(-1);
                    submitAttendance.setMuser(-1);
                    submitAttendance.setSyncStatus(0);

                    submitAttendance.setPresentDaysCount(0);
                    submitAttendance.setNationalHolidayCount(0);
                    submitAttendance.setExtraDutyDaysCount(0);
                    submitAttendance.setWeeklyOffDaysCount(0);

                    submitAttendance.setWeeklyOffDays("");
                    submitAttendance.setNationalHoliday("");
                    submitAttendance.setExtraDuty("");
                    submitAttendance.setRemark("");
                    submitAttendance.setTotalCount1(0);
                    submitAttendance.setTotalCount2(0);

                    submitAttendance.setContractId(153328909344483l);
                    submitAttendance.setContractName("DD Corp 2018-2020");
                    submitAttendance.setDesignation("Security Officer");

                    submitAttendance.setSiteCode(loginPref.getString(Constants.LOGIN_SITE_CODE,""));
                    submitAttendance.setSiteName(loginPref.getString(Constants.LOGIN_SITE_NAME,""));

                    submitAttendanceArrayList.add(submitAttendance);
                }
            }
        }
        else
        {
            submitAttendanceArrayList=attendanceSheetDAO.getPeopleAttendanceList();
            if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0) {
                updateFinalCount();
            }
        }

        if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0) {
            submitAttendanceListViewAdapter = new SubmitAttendanceListViewAdapter(SubmitMonthlyAttendanceActivity.this, submitAttendanceArrayList,currentMonthDays, this);
            amUserAttendanceListview.setAdapter(submitAttendanceListViewAdapter);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSave);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Data save successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0) {
                    for (int i = 0; i < submitAttendanceArrayList.size(); i++) {
                        System.out.println("Record: " + submitAttendanceArrayList.get(i).getPeopleName() + " : " + submitAttendanceArrayList.get(i).getAbsentOrPresent() + " : " + submitAttendanceArrayList.get(i).getAttendanceDates());
                        attendanceSheetDAO.insertOrUpdateRecord(submitAttendanceArrayList.get(i));

                    }
                    updateFinalCount();
                }
            }
        });

        showAttendancePeriod();
    }

    private void updateAttendancesheetperiod(String asheetPeriod)
    {
        if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0)
        {
            for(int i=0;i<submitAttendanceArrayList.size();i++)
            {
                submitAttendanceArrayList.get(i).setPeriod(asheetPeriod);
            }
        }
    }

    private void showAttendancePeriod()
    {
        final String[] attendancePeriod = { "Period 1\n( 1st To EOM)", "Period 2\n(15th To 14th)", "Period 3\n(25th To 24th)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SubmitMonthlyAttendanceActivity.this);

        builder.setTitle(getResources().getString(R.string.joblist_selecturchoice_title));

        builder.setSingleChoiceItems(attendancePeriod, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                updateAttendancesheetperiod(attendancePeriod[item]);
                alertDialog.dismiss();
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


    private void updateFinalCount()
    {
        pdCount=0;
        edCount=0;
        for (int i = 0; i < submitAttendanceArrayList.size(); i++) {
            pdCount=pdCount+submitAttendanceArrayList.get(i).getPresentDaysCount();
            edCount=edCount+getAttTypeCount(submitAttendanceArrayList.get(i).getExtraDuty());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pdCountTextView.setText(String.valueOf(pdCount));
                    edCountTextView.setText(String.valueOf(edCount));
                }
            });
        }
    }

    private int getAttTypeCount(String attDates)
    {
        if(attDates.trim().length()>0)
        {
            String[] days=attDates.split(",");
            return days.length;
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.upload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_upload)
        {
            if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0) {
                /*for (int i = 0; i < submitAttendanceArrayList.size(); i++) {
                    System.out.println("Record: " + submitAttendanceArrayList.get(i).getPeopleName() + " : " + submitAttendanceArrayList.get(i).getAbsentOrPresent() + " : " + submitAttendanceArrayList.get(i).getAttendanceDates());
                    attendanceSheetDAO.changeStatus(submitAttendanceArrayList.get(i).getPeopleId(), 1, 1);
                }
                setResult(RESULT_OK);
                finish();*/
                sendDataToERPServer();

            }
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void sendDataToERPServer()
    {
        final ProgressDialog progressDialog = new ProgressDialog(SubmitMonthlyAttendanceActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
        progressDialog.show(); // show progress dialog


        Gson gson = new Gson();
        String upData = gson.toJson(submitAttendanceArrayList);
        System.out.println("Domain Name upData: "+upData);
        CommonFunctions.UploadLog("\n <Server URL > \n"+upData+"\n");

        RetrofitServices retrofitServices= RetrofitERPURL.getClient().create(RetrofitServices.class);

        Call<ResponseClientUrlData> call=retrofitServices.getNotice(upData);
        call.enqueue(new Callback<ResponseClientUrlData>() {
            @Override
            public void onResponse(Call<ResponseClientUrlData> call, Response<ResponseClientUrlData> response) {
                progressDialog.dismiss();
                System.out.println(" Code: "+response.code());
                System.out.println(" response.message: "+response.message());
                if(response.isSuccessful() && response.body()!=null)
                {
                    System.out.println("response.body().getRc(): "+response.body().getRc());
                    if(response.body().getRc()==0)
                    {
                        attendanceSheetDAO.deleteRecords();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else
                    {
                        Snackbar.make(amUserAttendanceListview,getResources().getString(R.string.serverselection_notablefetchinfo),Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(response.code()==500)
                        Snackbar.make(amUserAttendanceListview, "Internal Server Error", Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(amUserAttendanceListview,"Unable to connect to server",Snackbar.LENGTH_LONG).show();

                    attendanceSheetDAO.deleteRecords();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseClientUrlData> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void changeRowData(int currentposition, ArrayList<SubmitAttendance> localData) {
        this.submitAttendanceArrayList=localData;
        submitAttendanceListViewAdapter.notifyDataSetChanged();
        submitAttendanceListViewAdapter.notifyDataSetInvalidated();
        /*if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0) {
            submitAttendanceListViewAdapter = new SubmitAttendanceListViewAdapter(SubmitMonthlyAttendanceActivity.this, submitAttendanceArrayList,currentMonthDays, this);
            amUserAttendanceListview.setAdapter(submitAttendanceListViewAdapter);
        }*/
    }
}
