package com.youtility.intelliwiz20.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.DataAccessObject.StepsCountLogDAO;
import com.youtility.intelliwiz20.Model.StepCount;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StepCounterActivity extends AppCompatActivity {


    private TextView stepCounterTextview;
    private TextView stepCountTimeTextView;
    public static final String TAG = "StepCounter";
    private SharedPreferences stepCountPref;
    private EditText startTimeEdittext, endTimeEdittext, startDateEdittext, endDateEdittext;
    private Button configButton;
    private Spinner buzzerTimeSpinner;
    private CheckBox stepCounterEnableCheckBox;

    private LinearLayout stepCountHistoryLL;
    private StepsCountLogDAO stepsCountLogDAO;
    private ArrayList<StepCount>stepCountArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountPref=getSharedPreferences(Constants.STEP_COUNTER_PREF, MODE_PRIVATE);

        stepCounterTextview=(TextView)findViewById(R.id.stepCountTextView);
        stepCountTimeTextView=(TextView)findViewById(R.id.stepCountTimeTextView);
        startTimeEdittext=(EditText)findViewById(R.id.startTimeEditText);
        endTimeEdittext=(EditText)findViewById(R.id.endTimeEditText);
        startDateEdittext=(EditText)findViewById(R.id.startDateEditText);
        endDateEdittext=(EditText)findViewById(R.id.endDateEditText);
        configButton=(Button)findViewById(R.id.configButton);
        buzzerTimeSpinner=(Spinner)findViewById(R.id.buzzerTimeSpinner);
        stepCounterEnableCheckBox=(CheckBox)findViewById(R.id.stepCountEnable);

        stepsCountLogDAO=new StepsCountLogDAO(StepCounterActivity.this);
        stepCountArrayList=new ArrayList<>();
        stepCountArrayList=stepsCountLogDAO.getStepCountsLog();

        stepCountHistoryLL=(LinearLayout)findViewById(R.id.stepcount_historyLinearLayout);
        if(stepCountHistoryLL.getChildCount()>0)
        {
            stepCountHistoryLL.removeAllViews();
        }

        if(stepCountArrayList!=null && stepCountArrayList.size()>0)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            TextView textSrNo;
            TextView textQuestName;
            TextView textQuestAns;

            for(int i=0;i<stepCountArrayList.size();i++) {
                v = inflater.inflate(R.layout.task_history_reading_row, null);
                textSrNo = (TextView) v.findViewById(R.id.srNoTextView);
                textQuestName = (TextView) v.findViewById(R.id.questNameTextView);
                textQuestAns = (TextView) v.findViewById(R.id.questAnsTextView);

                textSrNo.setText(""+(i+1));
                textQuestName.setText(CommonFunctions.getFormatedDate(stepCountArrayList.get(i).getStepCountTimestamp()));
                textQuestAns.setText(stepCountArrayList.get(i).getSteps()+" Steps taken in "+stepCountArrayList.get(i).getStepsTaken());
                stepCountHistoryLL.addView(v);
            }

        }


        stepCounterEnableCheckBox.setChecked(stepCountPref.getBoolean(Constants.STEP_COUNTER_ENABLE,false));

        List<Integer> spinnerArray = new ArrayList<>();
        spinnerArray.add(10);
        spinnerArray.add(15);
        spinnerArray.add(20);
        spinnerArray.add(30);
        spinnerArray.add(45);
        spinnerArray.add(60);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                StepCounterActivity.this,
                android.R.layout.simple_spinner_item,
                spinnerArray
        );
        buzzerTimeSpinner.setAdapter(adapter);
        buzzerTimeSpinner.setSelection(getIndex(buzzerTimeSpinner, stepCountPref.getInt(Constants.STEP_COUNTER_BUZZ_TIMER,10)));

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date strDate =  sdf.parse(stepCountPref.getString(Constants.STEP_COUNTER_END_DATE, CommonFunctions.getFromToDate1()));
            if (System.currentTimeMillis() > strDate.getTime()) {
                System.out.println("Current more");
                stepCountPref.edit().putString(Constants.STEP_COUNTER_START_DATE,CommonFunctions.getFromToDate1()).apply();
                stepCountPref.edit().putString(Constants.STEP_COUNTER_END_DATE,CommonFunctions.getFromToDate1()).apply();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startTimeEdittext.getText().toString().trim().length()>0 && endTimeEdittext.getText().toString().trim().length()>0)
                {
                    stepCountPref.edit().putString(Constants.STEP_COUNTER_START_TIME,startTimeEdittext.getText().toString().trim()).apply();
                    stepCountPref.edit().putString(Constants.STEP_COUNTER_END_TIME,endTimeEdittext.getText().toString().trim()).apply();
                    stepCountPref.edit().putString(Constants.STEP_COUNTER_START_DATE,startDateEdittext.getText().toString().trim()).apply();
                    stepCountPref.edit().putString(Constants.STEP_COUNTER_END_DATE,endDateEdittext.getText().toString().trim()).apply();
                    stepCountPref.edit().putInt(Constants.STEP_COUNTER_BUZZ_TIMER,Integer.parseInt(buzzerTimeSpinner.getSelectedItem().toString())).apply();
                    stepCountPref.edit().putBoolean(Constants.STEP_COUNTER_ENABLE, stepCounterEnableCheckBox.isChecked()).apply();
                    stepCountPref.edit().putBoolean(Constants.STEP_COUNTER_ISRUNNING,false).apply();
                    Toast.makeText(StepCounterActivity.this,getResources().getString(R.string.stepcounter_reconfig_succeed), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        startDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate=Calendar.getInstance();
                DatePickerDialog mDatePicker=new DatePickerDialog(StepCounterActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                    {
                        Calendar myCal=Calendar.getInstance();
                        myCal.set(Calendar.YEAR, selectedyear);
                        myCal.set(Calendar.MONTH, selectedmonth);
                        myCal.set(Calendar.DAY_OF_MONTH, selectedday);

                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        startDateEdittext.setText(sdf.format(myCal.getTime()));

                    }
                },mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle(getResources().getString(R.string.ngentry_seletdate));
                mDatePicker.show();
            }
        });

        endDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate=Calendar.getInstance();
                DatePickerDialog mDatePicker=new DatePickerDialog(StepCounterActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                    {
                        Calendar myCal=Calendar.getInstance();
                        myCal.set(Calendar.YEAR, selectedyear);
                        myCal.set(Calendar.MONTH, selectedmonth);
                        myCal.set(Calendar.DAY_OF_MONTH, selectedday);

                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        endDateEdittext.setText(sdf.format(myCal.getTime()));

                    }
                },mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle(getResources().getString(R.string.ngentry_seletdate));
                mDatePicker.show();
            }
        });

        //System.out.println("Steps count difference: "+(stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0)-stepCountPref.getLong(Constants.STEP_COUNTER_LAST_COUNT,0)));
        //System.out.println("Steps time difference: "+(stepCountPref.getLong(Constants.STEP_COUNTER_TIMESTAMP,System.currentTimeMillis())-stepCountPref.getLong(Constants.STEP_COUNTER_LAST_TIMESTAMP,System.currentTimeMillis())));

        String minDiff=CommonFunctions.getDateDifference(stepCountPref.getLong(Constants.STEP_COUNTER_TIMESTAMP,System.currentTimeMillis()),stepCountPref.getLong(Constants.STEP_COUNTER_LAST_TIMESTAMP,System.currentTimeMillis()));

        stepCounterTextview.setText(getResources().getString(R.string.stepcounter_totalstepcount)+stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0));
        stepCountTimeTextView.setText((stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0)-stepCountPref.getLong(Constants.STEP_COUNTER_LAST_COUNT,0)) +" Steps taken in last "+minDiff);

        startTimeEdittext.setText(stepCountPref.getString(Constants.STEP_COUNTER_START_TIME,"00:00:00"));
        endTimeEdittext.setText(stepCountPref.getString(Constants.STEP_COUNTER_END_TIME,"23:59:09"));

        startDateEdittext.setText(stepCountPref.getString(Constants.STEP_COUNTER_START_DATE,CommonFunctions.getFromToDate1()));
        endDateEdittext.setText(stepCountPref.getString(Constants.STEP_COUNTER_END_DATE,CommonFunctions.getFromToDate1()));

        updateView();

    }

    private int getIndex(Spinner spinner, int myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (Integer.parseInt(spinner.getItemAtPosition(i).toString())==myString){
                index = i;
            }
        }
        return index;
    }


    private void updateView()
    {
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                //System.out.println("Steps count difference: "+(stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0)-stepCountPref.getLong(Constants.STEP_COUNTER_LAST_COUNT,0)));
                //System.out.println("Steps time difference: "+(stepCountPref.getLong(Constants.STEP_COUNTER_TIMESTAMP,System.currentTimeMillis())-stepCountPref.getLong(Constants.STEP_COUNTER_LAST_TIMESTAMP,System.currentTimeMillis())));

                String minDiff=CommonFunctions.getDateDifference(stepCountPref.getLong(Constants.STEP_COUNTER_TIMESTAMP,System.currentTimeMillis()),stepCountPref.getLong(Constants.STEP_COUNTER_LAST_TIMESTAMP,System.currentTimeMillis()));

                stepCounterTextview.setText(getResources().getString(R.string.stepcounter_totalstepcount)+stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0));
                stepCountTimeTextView.setText((stepCountPref.getLong(Constants.STEP_COUNTER_COUNT,0)-stepCountPref.getLong(Constants.STEP_COUNTER_LAST_COUNT,0)) +" Steps taken in last "+minDiff);

                handler.postDelayed( this, 60 * 1000 );
            }
        }, 60 * 1000 );
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }



}
