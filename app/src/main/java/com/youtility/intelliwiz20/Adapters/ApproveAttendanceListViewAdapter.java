package com.youtility.intelliwiz20.Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.Model.MonthView;
import com.youtility.intelliwiz20.Model.SubmitAttendance;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 */

public class ApproveAttendanceListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SubmitAttendance> data;
    private LayoutInflater inflter;
    private PeopleDAO peopleDAO;
    private DatePickerDialog datePickerDialog;
    //TextView attendDates;
    private int currentMonthDays;
    public ApproveAttendanceListViewAdapter(Context context, ArrayList<SubmitAttendance> data, int currentMonthDays)
    {
        this.context=context;
        this.data=data;
        inflter = (LayoutInflater.from(context));
        peopleDAO=new PeopleDAO(context);
        this.currentMonthDays=currentMonthDays;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_approve_monthly_attendance_list_row, null);
        TextView userName = (TextView)view.findViewById(R.id.userAttendanceNameTextView);
        TextView attendDates = (TextView)view.findViewById(R.id.userAttendanceDatesTextview);
        attendDates.setTag(i);
        attendDates.setId(i);
        Switch userSwitch = (Switch)view.findViewById(R.id.userAttendanceSwitch);
        userSwitch.setId(i);
        ImageView userViewAttend=(ImageView)view.findViewById(R.id.userAttendanceViewIV);
        userViewAttend.setId(i);

        TextView userWoff = (TextView) view.findViewById(R.id.userWOTextView);
        userWoff.setId(i);
        TextView userNHoliday = (TextView) view.findViewById(R.id.userNHTextView);
        userNHoliday.setId(i);
        TextView userEDuty = (TextView) view.findViewById(R.id.userEDTextView);
        userEDuty.setId(i);
        TextView attendDatesCount = (TextView) view.findViewById(R.id.userAttendanceDatesCountTextview);
        attendDatesCount.setId(i);
        TextView userWoffCount = (TextView) view.findViewById(R.id.userWOCountTextView);
        userWoffCount.setId(i);
        TextView userNHolidayCount = (TextView) view.findViewById(R.id.userNHCountTextView);
        userNHoliday.setId(i);
        TextView userEDutyCount = (TextView) view.findViewById(R.id.userEDCountTextView);
        userEDuty.setId(i);

        EditText totalCount1 = (EditText) view.findViewById(R.id.userTotalCount1TextView);
        totalCount1.setId(i);
        EditText totalCount2 = (EditText) view.findViewById(R.id.userTotalCount2TextView);
        totalCount2.setId(i);


        userName.setText(data.get(i).getPeopleName());
        if(data.get(i).getAbsentOrPresent()==0) {
            userSwitch.setChecked(false);
            userSwitch.setText(userSwitch.getTextOff());
        }
        else {
            userSwitch.setText(userSwitch.getTextOn());
            userSwitch.setChecked(true);
        }
        attendDates.setText(data.get(i).getAttendanceDates());

        userWoff.setText((data.get(i).getWeeklyOffDays()));
        userWoffCount.setText(String.valueOf(getAttTypeCount(data.get(i).getWeeklyOffDays())));

        userNHoliday.setText((data.get(i).getNationalHoliday()));
        userNHolidayCount.setText(String.valueOf(getAttTypeCount(data.get(i).getNationalHoliday())));

        userEDuty.setText((data.get(i).getExtraDuty()));
        userEDutyCount.setText(String.valueOf(getAttTypeCount(data.get(i).getExtraDuty())));

        totalCount1.setText(String.valueOf(data.get(i).getTotalCount1()));
        totalCount2.setText(String.valueOf(data.get(i).getTotalCount2()));

        userViewAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView vv=(ImageView) v;
                showAlertDialog(vv.getId());
            }
        });
        return view;
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

    /*private void showAlertDialog(int i) {
        // Prepare grid view
        ArrayList<MonthView> monthViews;
        GridView gridView = new GridView(context);

        boolean defultVal=false;
        System.out.println("Switch Status: "+data.get(i).getAbsentOrPresent());
        if(data.get(i).getAbsentOrPresent()==1)
            defultVal=true;
        else
            defultVal=false;

        monthViews=new ArrayList<>();
        MonthView monthView = null;
        String ss=data.get(i).getAttendanceDates();
        if(ss!=null && ss.toString().trim().length()>0) {
            //ss = ss.substring(0, ss.toString().length() - 1);

            String[] ss1 = ss.split(",");
            for (int m = 1; m <= currentMonthDays; m++) {
                monthView = new MonthView();
                monthView.setDayNumber(m );

                *//*for (int j = 0; j < ss1.length; j++) {
                    if (m == Integer.parseInt(ss1[j])) {
                        monthView.setPresent(defultVal);
                        break;
                    } else {
                        monthView.setPresent(!defultVal);

                    }

                }*//*
                monthViews.add(monthView);


            }


            gridView.setAdapter(new MyAdapter(monthViews));
            gridView.setNumColumns(7);

            // Set grid view to alertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(gridView);
            builder.setTitle("Calender View");
            builder.show();
        }
    }*/

    private void showAlertDialog(int i) {
        // Prepare grid view
        ArrayList<MonthView> monthViews;
        MonthView monthView = null;
        GridView gridView = new GridView(context);

        int defultVal=-1;
        System.out.println("Switch Status: "+data.get(i).getAbsentOrPresent());
        if(data.get(i).getAbsentOrPresent()==1)
            defultVal=1;
        else
            defultVal=0;

        monthViews=new ArrayList<>();

        String ss=data.get(i).getAttendanceDates();
        if(ss!=null && ss.trim().length()>0) {
            String[] ss1 = ss.split(",");
            for (int m = 1; m <= currentMonthDays; m++) {
                monthView = new MonthView();
                monthView.setDayNumber(m );

                for (int j = 0; j < ss1.length; j++) {
                    if(defultVal==1) monthView.setDayType(0);
                    else monthView.setDayType(1);

                    if(m==Integer.parseInt(ss1[j]))
                    {
                        monthView.setDayType(defultVal);
                        break;
                    }

                }
                monthViews.add(monthView);

            }

            for(int w=0;w<monthViews.size();w++)
            {
                String wo=data.get(i).getWeeklyOffDays();
                if(wo!=null && wo.trim().length()>0)
                {
                    String[] sWO=wo.split(",");
                    for(int j=0;j<sWO.length;j++)
                    {
                        if(monthViews.get(w).getDayNumber()==Integer.parseInt(sWO[j]))
                        {
                            monthViews.get(w).setDayType(2);
                        }
                    }
                }
            }

            for(int w=0;w<monthViews.size();w++)
            {
                String ed=data.get(i).getExtraDuty();
                if(ed!=null && ed.trim().length()>0)
                {
                    String[] sWO=ed.split(",");
                    for(int j=0;j<sWO.length;j++)
                    {
                        if(monthViews.get(w).getDayNumber()==Integer.parseInt(sWO[j]))
                        {
                            monthViews.get(w).setDayType(3);
                        }
                    }
                }
            }

            String nh=data.get(i).getNationalHoliday();
            if(nh!=null && nh.trim().length()>0)
            {
                for(int w=0;w<monthViews.size();w++)
                {
                    String[] sNH=nh.split(",");
                    for(int j=0;j<sNH.length;j++)
                    {
                        if(monthViews.get(w).getDayNumber()==Integer.parseInt(sNH[j]))
                        {
                            monthViews.get(w).setDayType(4);
                        }
                    }
                }
            }

            gridView.setAdapter(new MyAdapter(monthViews));
            gridView.setNumColumns(7);

            // Set grid view to alertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(gridView);
            builder.setTitle("Calender View");
            builder.show();
        }
    }

    private class MyAdapter extends BaseAdapter
    {
        LayoutInflater inflter;
        ArrayList<MonthView> monthViews;
        public MyAdapter( ArrayList<MonthView> monthViews)
        {
            this.monthViews=monthViews;
            inflter=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return monthViews.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflter.inflate(R.layout.monthly_gridview_items, null); // inflate the layout
            TextView icon = (TextView) convertView.findViewById(R.id.dayTextView); // get the reference of ImageView
            icon.setText(monthViews.get(position).getDayNumber());
            if(monthViews.get(position).getDayType()==1)
                icon.setBackgroundResource(R.drawable.green_circle);
            else if(monthViews.get(position).getDayType()==2)
                icon.setBackgroundResource(R.drawable.gray_circle);
            else if(monthViews.get(position).getDayType()==3)
                icon.setBackgroundResource(R.drawable.blue_circle);
            else if(monthViews.get(position).getDayType()==4)
                icon.setBackgroundResource(R.drawable.ic_action_cancel);
            else
                icon.setBackgroundResource(R.drawable.red_circle);
            return convertView;
        }
    }


}
