package com.youtility.intelliwiz20.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.youtility.intelliwiz20.Adapters.ApproveAttendanceListViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.AttendanceSheetDAO;
import com.youtility.intelliwiz20.Model.SubmitAttendance;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ApproveMonthlyAttendanceActivity extends AppCompatActivity {
    private ListView bmUserAttendanceListview;
    private int currentMonthDays=0;
    private ArrayList<SubmitAttendance> submitAttendanceArrayList;
    private ApproveAttendanceListViewAdapter approveAttendanceListViewAdapter;
    private AttendanceSheetDAO attendanceSheetDAO;
    private TextView totalDutiesTV, totalWOffTV, totalContractDutiesTV;
    private Switch approvalSwitch;
    private Button submitButton;
    private EditText commentEdittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_monthly_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setSubtitle(CommonFunctions.getCurrentMonth(System.currentTimeMillis()));

        attendanceSheetDAO=new AttendanceSheetDAO(ApproveMonthlyAttendanceActivity.this);

        bmUserAttendanceListview=(ListView)findViewById(R.id.bmUserattendanceListview);
        totalDutiesTV=(TextView)findViewById(R.id.totalDutiesCountTextview) ;
        totalWOffTV=(TextView)findViewById(R.id.totalWoffCountTextview);
        totalContractDutiesTV=(TextView)findViewById(R.id.totalContractDutiesCountTextview);
        approvalSwitch=(Switch)findViewById(R.id.approvalSwitch);
        commentEdittext=(EditText)findViewById(R.id.commentEdittext);
        submitButton=(Button)findViewById(R.id.submitButton);


        submitAttendanceArrayList=new ArrayList<>();
        Calendar mycal = new GregorianCalendar();
        currentMonthDays=mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        submitAttendanceArrayList=attendanceSheetDAO.getSubmittedPeopleAttendanceList();

        if(submitAttendanceArrayList!=null && submitAttendanceArrayList.size()>0) {

            prepareFinalCount();

            approveAttendanceListViewAdapter = new ApproveAttendanceListViewAdapter(ApproveMonthlyAttendanceActivity.this, submitAttendanceArrayList,currentMonthDays);
            bmUserAttendanceListview.setAdapter(approveAttendanceListViewAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void prepareFinalCount()
    {
        int pdCount=0;
        int woCount=0;
        for (int i = 0; i < submitAttendanceArrayList.size(); i++) {
            pdCount=pdCount+submitAttendanceArrayList.get(i).getPresentDaysCount();
            woCount=woCount+getAttTypeCount(submitAttendanceArrayList.get(i).getWeeklyOffDays());
        }
        updateFinalCount(pdCount, woCount);
    }

    private void updateFinalCount(final int pCount, final int wCount)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                totalDutiesTV.setText(String.valueOf(pCount));
                totalWOffTV.setText(String.valueOf(wCount));
            }
        });
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
                }*/
                setResult(RESULT_OK);
                finish();

            }
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
}
