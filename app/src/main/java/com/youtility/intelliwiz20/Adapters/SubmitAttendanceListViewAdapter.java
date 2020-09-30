package com.youtility.intelliwiz20.Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/*
import com.google.android.gms.vision.text.Text;
*/
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IRefreshListView;
import com.youtility.intelliwiz20.Model.MonthView;
import com.youtility.intelliwiz20.Model.SubmitAttendance;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by PrashantD on 9/9/17.
 */

public class SubmitAttendanceListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SubmitAttendance> data;
    private LayoutInflater inflter;
    private PeopleDAO peopleDAO;
    private DatePickerDialog datePickerDialog;
    private TextView attendDates;
    private EditText totalCount1;
    private EditText totalCount2;
    private int currentMonthDays;
    private IRefreshListView iRefreshListView;
    private int mYear, mMonth, mDay;
    private int selectedShiftPosition=0;
    private ArrayList<TypeAssist>shiftTypeList=null;
    private TypeAssistDAO typeAssistDAO;
    public SubmitAttendanceListViewAdapter(Context context, ArrayList<SubmitAttendance> data, int currentMonthDays, IRefreshListView iRefreshListView)
    {
        this.context=context;
        this.data=data;
        inflter = (LayoutInflater.from(context));
        peopleDAO=new PeopleDAO(context);
        this.currentMonthDays=currentMonthDays;
        this.iRefreshListView=iRefreshListView;

        typeAssistDAO=new TypeAssistDAO(context);
        shiftTypeList=typeAssistDAO.getEventList(Constants.SHIFT_TYPE);

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR); // current year
        mMonth = calendar.get(Calendar.MONTH); // current month
        mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day

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


        final SubmitAttendance submitAttendance = data.get(i);

        if (submitAttendance != null)
        {

            view = inflter.inflate(R.layout.activity_submit_monthly_attendance_list_row, null);

            data.get(i).setAbsentOrPresent(1);

            TextView userName = (TextView) view.findViewById(R.id.userAttendanceNameTextView);
            userName.setText(submitAttendance.getPeopleName());

            ImageView viewAttendance=(ImageView)view.findViewById(R.id.viewAttendance);
            viewAttendance.setId(i);
            viewAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ImageView button=(ImageView) v;
                    showAlertDialog(button.getId());
                }
            });

            //------------------weekly off day and count
            TextView userWoff = (TextView) view.findViewById(R.id.userWOTextView);
            userWoff.setId(i);
            TextView userWoffCount = (TextView) view.findViewById(R.id.userWOCountTextView);
            userWoffCount.setId(i);

            userWoff.setText((submitAttendance.getWeeklyOffDays()));
            userWoffCount.setText(String.valueOf(getAttTypeCount(data.get(i).getWeeklyOffDays())));
            data.get(i).setWeeklyOffDaysCount(getAttTypeCount(data.get(i).getWeeklyOffDays()));

            userWoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv= (TextView)v;
                    datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                    {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String ss = vv.getText().toString().trim();
                            StringBuffer sb = new StringBuffer();
                            if (ss.toString().trim().length() != 0) {
                                if (!ss.toString().contains(String.valueOf(dayOfMonth)))
                                    sb.append(ss.toString() + "," + dayOfMonth);
                                else
                                    sb.append(ss.toString());
                            } else
                                sb.append(ss.toString() + dayOfMonth);

                            vv.setText(sb.toString());
                            data.get(i).setWeeklyOffDays(sb.toString());
                            //calculatePresentDays(submitAttendance.getAttendanceDates(), i);
                            iRefreshListView.changeRowData(i, data);
                        }
                    }, mYear,mMonth, mDay);
                    datePickerDialog.show();
                }

            });

            //------------------national holiday and count
            TextView userNHoliday = (TextView) view.findViewById(R.id.userNHTextView);
            userNHoliday.setId(i);
            TextView userNHolidayCount = (TextView) view.findViewById(R.id.userNHCountTextView);
            userNHoliday.setId(i);

            userNHoliday.setText((submitAttendance.getNationalHoliday()));
            userNHolidayCount.setText(String.valueOf(getAttTypeCount(data.get(i).getNationalHoliday())));
            data.get(i).setNationalHolidayCount(getAttTypeCount(data.get(i).getNationalHoliday()));

            userNHoliday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv= (TextView)v;
                    datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                    {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String ss = vv.getText().toString().trim();
                            StringBuffer sb = new StringBuffer();
                            if (ss.trim().length() != 0) {
                                if (!ss.contains(String.valueOf(dayOfMonth)))
                                    sb.append(ss + "," + dayOfMonth);
                                else
                                    sb.append(ss);
                            } else
                                sb.append(ss + dayOfMonth);

                            vv.setText(sb.toString());
                            data.get(i).setNationalHoliday(sb.toString());
                            //calculatePresentDays(submitAttendance.getAttendanceDates(), i);
                            iRefreshListView.changeRowData(i, data);
                        }
                    }, mYear,mMonth, mDay);
                    datePickerDialog.show();
                }

            });


            //---------------------------------Present morning days and count

            TextView pMorningDays=(TextView)view.findViewById(R.id.presentMorningDaysTextview);
            pMorningDays.setTag(i);
            pMorningDays.setId(i);
            pMorningDays.setText(submitAttendance.getpMornDates());
            pMorningDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getpMornDates());
                    custPeopleAttendanceSheet("title","message",0,i,vv.getText().toString());
                }
            });
            TextView pMorningDaysCount = (TextView) view.findViewById(R.id.presentMorningDaysCountTextview);
            pMorningDaysCount.setId(i);
            pMorningDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getpMornDates())));

            //---------------------------------Present afternoon days and count

            TextView pNoonDays=(TextView)view.findViewById(R.id.presentAfternoonDaysTextview);
            pNoonDays.setTag(i);
            pNoonDays.setId(i);
            pNoonDays.setText(submitAttendance.getpNoonDates());
            pNoonDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getpNoonDates());
                    custPeopleAttendanceSheet("title","message",1,i,vv.getText().toString());
                }
            });
            TextView pNoonDaysCount = (TextView) view.findViewById(R.id.presentAfternoonDaysCountTextview);
            pNoonDaysCount.setId(i);
            pNoonDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getpNoonDates())));

            //---------------------------------Present night days and count

            TextView pNightDays=(TextView)view.findViewById(R.id.presentEveningDaysTextview);
            pNightDays.setTag(i);
            pNightDays.setId(i);
            pNightDays.setText(submitAttendance.getpNigtDates());
            pNightDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getpNigtDates());
                    custPeopleAttendanceSheet("title","message",2,i,vv.getText().toString());
                }
            });
            TextView pNightDaysCount = (TextView) view.findViewById(R.id.presentEveningDaysCountTextview);
            pNightDaysCount.setId(i);
            pNightDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getpNigtDates())));

            //---------------------------------Present general days and count

            TextView pGeneralDays=(TextView)view.findViewById(R.id.presentGeneralDaysTextview);
            pGeneralDays.setTag(i);
            pGeneralDays.setId(i);
            pGeneralDays.setText(submitAttendance.getpGenrDates());
            pGeneralDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getpGenrDates());
                    custPeopleAttendanceSheet("title","message",3,i,vv.getText().toString());
                }
            });
            TextView pGeneralDaysCount = (TextView) view.findViewById(R.id.presentGeneralDaysCountTextview);
            pGeneralDaysCount.setId(i);
            pGeneralDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getpGenrDates())));


            //---------------------------------Extra duty morning days and count

            data.get(i).setExtraDutyDaysCount(getAttTypeCount(data.get(i).getExtraDuty()));

            TextView edMorningDays=(TextView)view.findViewById(R.id.edMorningDaysTextview);
            edMorningDays.setTag(i);
            edMorningDays.setId(i);
            edMorningDays.setText(submitAttendance.getEdMornDates());
            edMorningDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getEdMornDates());
                    custPeopleExtraDutySheet("title","message",0,i,vv.getText().toString());
                }
            });
            TextView edMorningDaysCount = (TextView) view.findViewById(R.id.edMorningDaysCountTextview);
            edMorningDaysCount.setId(i);
            edMorningDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getEdMornDates())));

            //---------------------------------Extra duty afternoon days and count

            TextView edNoonDays=(TextView)view.findViewById(R.id.edAfternoonDaysTextview);
            edNoonDays.setTag(i);
            edNoonDays.setId(i);
            edNoonDays.setText(submitAttendance.getEdNoonDates());
            edNoonDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getEdNoonDates());
                    custPeopleExtraDutySheet("title","message",1,i,vv.getText().toString());
                }
            });
            TextView edNoonDaysCount = (TextView) view.findViewById(R.id.edAfternoonDaysCountTextview);
            edNoonDaysCount.setId(i);
            edNoonDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getEdNoonDates())));

            //---------------------------------Extra duty night days and count

            TextView edNightDays=(TextView)view.findViewById(R.id.edEveningDaysTextview);
            edNightDays.setTag(i);
            edNightDays.setId(i);
            edNightDays.setText(submitAttendance.getEdNigtDates());
            edNightDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getEdNigtDates());
                    custPeopleExtraDutySheet("title","message",2,i,vv.getText().toString());
                }
            });
            TextView edNightDaysCount = (TextView) view.findViewById(R.id.edEveningDaysCountTextview);
            edNightDaysCount.setId(i);
            edNightDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getEdNigtDates())));

            //---------------------------------Extra duty general days and count

            TextView edGeneralDays=(TextView)view.findViewById(R.id.edGeneralDaysTextview);
            edGeneralDays.setTag(i);
            edGeneralDays.setId(i);
            edGeneralDays.setText(submitAttendance.getEdGenrDates());
            edGeneralDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getEdGenrDates());
                    custPeopleExtraDutySheet("title","message",3,i,vv.getText().toString());
                }
            });
            TextView edGeneralDaysCount = (TextView) view.findViewById(R.id.edGeneralDaysCountTextview);
            edGeneralDaysCount.setId(i);
            edGeneralDaysCount.setText(String.valueOf(getAttTypeCount(data.get(i).getEdGenrDates())));
        }
        return view;
    }


    private void updateAttendanceDates(final String existdata, final String newData, final int itemPosition, final int type)
    {
        switch(type)
        {
            case 0:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setpMornDates(existdata+","+newData);
                else
                    data.get(itemPosition).setpMornDates(newData);
                break;
            case 1:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setpNoonDates(existdata+","+newData);
                else
                    data.get(itemPosition).setpNoonDates(newData);
                break;
            case 2:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setpNigtDates(existdata+","+newData);
                else
                    data.get(itemPosition).setpNigtDates(newData);
                break;
            case 3:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setpGenrDates(existdata+","+newData);
                else
                    data.get(itemPosition).setpGenrDates(newData);
                break;
        }
        if(data.get(itemPosition).getAttendanceDates().trim().length()>0)
        {
            data.get(itemPosition).setAttendanceDates(data.get(itemPosition).getAttendanceDates().trim()+","+newData);
        }
        else
        {
            data.get(itemPosition).setAttendanceDates(newData);
        }

    }

    private void updateExtraDutyDates(final String existdata, final String newData, final int itemPosition, final int type)
    {
        switch(type)
        {
            case 0:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setEdMornDates(existdata+","+newData);
                else
                    data.get(itemPosition).setEdMornDates(newData);
                break;
            case 1:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setEdNoonDates(existdata+","+newData);
                else
                    data.get(itemPosition).setEdNoonDates(newData);
                break;
            case 2:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setEdNigtDates(existdata+","+newData);
                else
                    data.get(itemPosition).setEdNigtDates(newData);
                break;
            case 3:
                if(existdata.trim().length()>0)
                    data.get(itemPosition).setEdGenrDates(existdata+","+newData);
                else
                    data.get(itemPosition).setEdGenrDates(newData);
                break;
        }
        if(data.get(itemPosition).getExtraDuty().trim().length()>0)
        {
            data.get(itemPosition).setExtraDuty(data.get(itemPosition).getExtraDuty().trim()+","+newData);
        }
        else
        {
            data.get(itemPosition).setExtraDuty(newData);
        }

    }


    private void custPeopleAttendanceSheet(String title, String message, final int type, final int itemPosition,  final String existdata)
    {

        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_monthly_attendsheet_calender_alert_view);
        dialog.setCancelable(false);

        TextView userAttendanceDatesTextview=(TextView) dialog.findViewById(R.id.userAttendanceDatesAlertTextview);
        userAttendanceDatesTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView vv= (TextView)v;
                datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String ss = vv.getText().toString().trim();
                        StringBuffer sb = new StringBuffer();
                        if (ss.trim().length() != 0) {
                            if (!ss.contains(String.valueOf(dayOfMonth)))
                                sb.append(ss + "," + dayOfMonth+":"+getShiftCode(type));
                            else
                                sb.append(ss);

                        } else
                            sb.append(ss + dayOfMonth+":"+getShiftCode(type));

                        vv.setText(sb.toString());
                        calculatePresentDays(sb.toString(), itemPosition);

                        updateAttendanceDates(existdata,sb.toString(),itemPosition,type);

                        /*if(existdata.trim().length()>0)
                            data.get(itemPosition).setAttendanceDates(existdata+","+sb.toString());
                        else
                            data.get(itemPosition).setAttendanceDates(sb.toString());*/
                        iRefreshListView.changeRowData(itemPosition, data);
                    }
                }, mYear,mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
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

    private void custPeopleExtraDutySheet(String title, String message, final int type, final int itemPosition,  final String existdata)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_monthly_attendsheet_calender_alert_view);
        dialog.setCancelable(false);

        TextView userEDTextView=(TextView) dialog.findViewById(R.id.userAttendanceDatesAlertTextview);
        userEDTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView vv= (TextView)v;
                datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String ss = vv.getText().toString().trim();
                        StringBuffer sb = new StringBuffer();
                        if (ss.trim().length() != 0) {
                            if (!ss.contains(String.valueOf(dayOfMonth)))
                                sb.append(ss + "," + dayOfMonth+":"+getShiftCode(type));
                            else
                                sb.append(ss);

                        } else
                            sb.append(ss + dayOfMonth+":"+getShiftCode(type));

                        vv.setText(sb.toString());
                        //calculatePresentDays(sb.toString(), itemPosition);

                        updateExtraDutyDates(existdata,sb.toString(),itemPosition,type);

                        /*if(existdata.trim().length()>0)
                            data.get(itemPosition).setExtraDuty(existdata+","+sb.toString());
                        else
                            data.get(itemPosition).setExtraDuty(sb.toString());*/
                        iRefreshListView.changeRowData(itemPosition, data);
                    }
                }, mYear,mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
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


    private String getShiftCode(int shiftCode)
    {
        switch(shiftCode)
        {
            case 0:
                return "M";
            case 1:
                return "A";
            case 2:
                return "N";
            case 3:
                return "G";
                default:
                    return "M";
        }
        //return String.valueOf(shiftName.charAt(0));
    }

    private int getAttTypeCount(String attDates)
    {
        if(attDates!=null && attDates.trim().length()>0)
        {
            String[] days=attDates.split(",");
            return days.length;
        }
        return 0;
    }

    private int calculatePresentDays(String val, int position)
    {
        if(val!=null && val.length()>0) {
            if (data.get(position).getAbsentOrPresent() == 0) {
                if (val.contains(",")) {
                    String[] dayCount = val.split(",");
                    int pDays = (currentMonthDays - dayCount.length);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) + getAttTypeCount(data.get(position).getExtraDuty()));
                } else {
                    int pDays = (currentMonthDays - 1);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) + getAttTypeCount(data.get(position).getExtraDuty()));
                }


            } else {
                if (val.contains(",")) {
                    String[] dayCount = val.split(",");
                    int pDays = (dayCount.length);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) + getAttTypeCount(data.get(position).getExtraDuty()));
                } else {
                    int pDays = (1);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) +getAttTypeCount(data.get(position).getExtraDuty()));
                }
            }


        }
        return data.get(position).getTotalCount1();

    }

    private void showAlertDialog(int i) {
        // Prepare grid view
        ArrayList<MonthView> monthViews;
        GridView gridView = new GridView(context);

        int defultVal=0;
        int defaultVal1=1;
        System.out.println("Switch Status: "+data.get(i).getAbsentOrPresent());
        if(data.get(i).getAbsentOrPresent()==1)
            defultVal=1;
        else
            defultVal=0;

        monthViews=new ArrayList<>();
        MonthView monthView = null;
        String ss=data.get(i).getAttendanceDates();
        if(ss!=null && ss.trim().length()>0) {
            String[] ss1 = ss.split(",");
            for (int m = 1; m <= currentMonthDays; m++) {
                monthView = new MonthView();
                monthView.setDayNumber(m );

                for (int j = 0; j < ss1.length; j++) {

                    if(defultVal==1) monthView.setDayType(0);
                    else monthView.setDayType(1);
                    String[] monthVal=ss1[j].split(":");
                    if(m==Integer.parseInt(monthVal[0]))
                    {
                        monthView.setPresentDayType(1);
                        monthView.setDutyTypeName("P");
                        monthView.setDutyTypeShift(monthVal[1]);
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
                            monthViews.get(w).setDutyTypeName(monthViews.get(w).getDutyTypeName()+"\nWO");
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
                        String[] monthVal=sWO[j].split(":");

                        if(monthViews.get(w).getDayNumber()==Integer.parseInt(monthVal[0]))
                        {
                            monthViews.get(w).setDutyTypeName(monthViews.get(w).getDutyTypeName()+"\nED");
                            monthViews.get(w).setDutyTypeShift(monthVal[1]);
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
                            monthViews.get(w).setDutyTypeName(monthViews.get(w).getDutyTypeName()+"\nNH");
                        }
                    }
                }
            }

            gridView.setAdapter(new MyAdapter(monthViews));
            gridView.setNumColumns(7);
            gridView.setHorizontalSpacing(10);
            gridView.setVerticalSpacing(10);

            // Set grid view to alertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(gridView);
            builder.setTitle("Attendance Summary");
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
;
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
            //icon.setText(String.valueOf(monthViews.get(position).getDayNumber()));
            if(monthViews.get(position).getPresentDayType()==1)
            {
                icon.setText(String.valueOf(monthViews.get(position).getDayNumber())+":"+monthViews.get(position).getDutyTypeName()+"("+monthViews.get(position).getDutyTypeShift()+")");
                icon.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            }
            else {
                icon.setText(String.valueOf(monthViews.get(position).getDayNumber()));
                icon.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            }

            return convertView;
        }
    }




}



//-----------------------------------------------------------------------------------------------------------------------

/*
public class SubmitAttendanceListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SubmitAttendance> data;
    private LayoutInflater inflter;
    private PeopleDAO peopleDAO;
    private DatePickerDialog datePickerDialog;
    private TextView attendDates;
    private EditText totalCount1;
    private EditText totalCount2;
    private int currentMonthDays;
    private IRefreshListView iRefreshListView;
    private int mYear, mMonth, mDay;
    private int selectedShiftPosition=0;
    private ArrayList<TypeAssist>shiftTypeList=null;
    private TypeAssistDAO typeAssistDAO;
    public SubmitAttendanceListViewAdapter(Context context, ArrayList<SubmitAttendance> data, int currentMonthDays, IRefreshListView iRefreshListView)
    {
        this.context=context;
        this.data=data;
        inflter = (LayoutInflater.from(context));
        peopleDAO=new PeopleDAO(context);
        this.currentMonthDays=currentMonthDays;
        this.iRefreshListView=iRefreshListView;

        typeAssistDAO=new TypeAssistDAO(context);
        shiftTypeList=typeAssistDAO.getEventList(Constants.SHIFT_TYPE);

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR); // current year
        mMonth = calendar.get(Calendar.MONTH); // current month
        mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day

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


        final SubmitAttendance submitAttendance = data.get(i);

        if (submitAttendance != null)
        {

            view = inflter.inflate(R.layout.activity_submit_monthly_attendance_list_row, null);

            TextView userName = (TextView) view.findViewById(R.id.userAttendanceNameTextView);
            attendDates = (TextView) view.findViewById(R.id.userAttendanceDatesTextview);
            attendDates.setTag(i);
            attendDates.setId(i);
            *//*Switch userSwitch = (Switch) view.findViewById(R.id.userAttendanceSwitch);
            userSwitch.setId(i);*//*
            *//*ImageView userViewAttend = (ImageView) view.findViewById(R.id.userAttendanceViewIV);
            userViewAttend.setId(i);*//*
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

            Button viewAttendance=(Button)view.findViewById(R.id.viewAttendance);
            viewAttendance.setId(i);



            *//*totalCount1 = (EditText) view.findViewById(R.id.userTotalCount1TextView);
            totalCount1.setId(i);
            totalCount1.setText(String.valueOf(submitAttendance.getTotalCount1()));

            totalCount2 = (EditText) view.findViewById(R.id.userTotalCount2TextView);
            totalCount2.setId(i);
            totalCount2.setText(String.valueOf(submitAttendance.getTotalCount2()));*//*

            userWoff.setText((submitAttendance.getWeeklyOffDays()));
            userWoffCount.setText(String.valueOf(getAttTypeCount(data.get(i).getWeeklyOffDays())));
            data.get(i).setWeeklyOffDaysCount(getAttTypeCount(data.get(i).getWeeklyOffDays()));

            userNHoliday.setText((submitAttendance.getNationalHoliday()));
            userNHolidayCount.setText(String.valueOf(getAttTypeCount(data.get(i).getNationalHoliday())));
            data.get(i).setNationalHolidayCount(getAttTypeCount(data.get(i).getNationalHoliday()));

            userEDuty.setText((submitAttendance.getExtraDuty()));
            userEDutyCount.setText(String.valueOf(getAttTypeCount(data.get(i).getExtraDuty())));
            data.get(i).setExtraDutyDaysCount(getAttTypeCount(data.get(i).getExtraDuty()));


            userName.setText(submitAttendance.getPeopleName());
            *//*userSwitch.setText(userSwitch.getTextOn());
            userSwitch.setChecked(true);*//*

            attendDates.setText(submitAttendance.getAttendanceDates());
            System.out.println(getAttTypeCount(data.get(i).getAttendanceDates())+" : Count: ");
            attendDatesCount.setText(String.valueOf(getAttTypeCount(data.get(i).getAttendanceDates())));


            userNHoliday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv= (TextView)v;
                    datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                    {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String ss = vv.getText().toString().trim();
                            StringBuffer sb = new StringBuffer();
                            if (ss.trim().length() != 0) {
                                if (!ss.contains(String.valueOf(dayOfMonth)))
                                    sb.append(ss + "," + dayOfMonth);
                                else
                                    sb.append(ss);
                            } else
                                sb.append(ss + dayOfMonth);

                            vv.setText(sb.toString());
                            data.get(i).setNationalHoliday(sb.toString());
                            calculatePresentDays(submitAttendance.getAttendanceDates(), i);
                            iRefreshListView.changeRowData(i, data);
                        }
                    }, mYear,mMonth, mDay);
                    datePickerDialog.show();
                }

            });

            userEDuty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv= (TextView)v;
                    vv.setText(data.get(i).getExtraDuty());
                    custPeopleExtraDutySheet("title","message",0,i,vv.getText().toString());
                }

            });

            userWoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextView vv= (TextView)v;
                    datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                    {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String ss = vv.getText().toString().trim();
                            StringBuffer sb = new StringBuffer();
                            if (ss.toString().trim().length() != 0) {
                                if (!ss.toString().contains(String.valueOf(dayOfMonth)))
                                    sb.append(ss.toString() + "," + dayOfMonth);
                                else
                                    sb.append(ss.toString());
                            } else
                                sb.append(ss.toString() + dayOfMonth);

                            vv.setText(sb.toString());
                            data.get(i).setWeeklyOffDays(sb.toString());
                            calculatePresentDays(submitAttendance.getAttendanceDates(), i);
                            iRefreshListView.changeRowData(i, data);
                        }
                    }, mYear,mMonth, mDay);
                    datePickerDialog.show();
                }

            });

            attendDates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final TextView vv = (TextView) v;
                    vv.setText(data.get(i).getAttendanceDates());
                    custPeopleAttendanceSheet("title","message",0,i,vv.getText().toString());
                }
            });

            *//*userSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buttonView.setText(context.getResources().getString(R.string.switch_present));
                        data.get(i).setAbsentOrPresent(1);
                    } else {
                        buttonView.setText(context.getResources().getString(R.string.switch_absent));
                        data.get(i).setAbsentOrPresent(0);
                    }

                    calculatePresentDays(data.get(i).getAttendanceDates(), i);
                    iRefreshListView.changeRowData(i, data);
                }
            });*//*

            data.get(i).setAbsentOrPresent(1);

            *//*userViewAttend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ImageView vv = (ImageView) v;
                    showAlertDialog(vv.getId());
                }
            });*//*

            viewAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button button=(Button) v;
                    showAlertDialog(button.getId());
                }
            });
        }
        return view;
    }



    private void custPeopleAttendanceSheet(String title, String message, final int type, final int itemPosition,  final String existdata)
    {
        //String[] shifts = { "Morning", "Afternoon", "Night", "General" };
        ArrayList<String> shiftData=new ArrayList<>();
        if(shiftTypeList!=null && shiftTypeList.size()>0)
        {
            for(int i=0;i<shiftTypeList.size();i++)
            {
                shiftData.add(shiftTypeList.get(i).getTaname());
            }

        }

        final ArrayAdapter<String> adp = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, shiftData);

        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_monthly_attendsheet_calender_alert_view);
        dialog.setCancelable(false);

        final Spinner shiftSpinner=(Spinner)dialog.findViewById(R.id.shiftSpinner);
        shiftSpinner.setAdapter(adp);
        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedShiftPosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView userAttendanceDatesTextview=(TextView) dialog.findViewById(R.id.userAttendanceDatesAlertTextview);
        userAttendanceDatesTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView vv= (TextView)v;
                datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String ss = vv.getText().toString().trim();
                        StringBuffer sb = new StringBuffer();
                        if (ss.trim().length() != 0) {
                            if (!ss.contains(String.valueOf(dayOfMonth)))
                                sb.append(ss + "," + dayOfMonth+":"+getShiftCode(shiftSpinner.getAdapter().getItem(selectedShiftPosition).toString()));
                            else
                                sb.append(ss);

                        } else
                            sb.append(ss + dayOfMonth+":"+getShiftCode(shiftSpinner.getAdapter().getItem(selectedShiftPosition).toString()));

                        vv.setText(sb.toString());
                        calculatePresentDays(sb.toString(), itemPosition);

                        if(existdata.trim().length()>0)
                            data.get(itemPosition).setAttendanceDates(existdata+","+sb.toString());
                        else
                            data.get(itemPosition).setAttendanceDates(sb.toString());
                        iRefreshListView.changeRowData(itemPosition, data);
                    }
                }, mYear,mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
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

    private void custPeopleExtraDutySheet(String title, String message, final int type, final int itemPosition,  final String existdata)
    {
        //String[] shifts = { "Morning", "Afternoon", "Night", "General" };
        ArrayList<String> shiftData=new ArrayList<>();
        if(shiftTypeList!=null && shiftTypeList.size()>0)
        {
            for(int i=0;i<shiftTypeList.size();i++)
            {
                shiftData.add(shiftTypeList.get(i).getTaname());
            }

        }

        final ArrayAdapter<String> adp = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, shiftData);

        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cust_monthly_attendsheet_calender_alert_view);
        dialog.setCancelable(false);

        final Spinner shiftSpinner=(Spinner)dialog.findViewById(R.id.shiftSpinner);
        shiftSpinner.setAdapter(adp);
        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedShiftPosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView userEDTextView=(TextView) dialog.findViewById(R.id.userAttendanceDatesAlertTextview);
        userEDTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView vv= (TextView)v;
                datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String ss = vv.getText().toString().trim();
                        StringBuffer sb = new StringBuffer();
                        if (ss.trim().length() != 0) {
                            if (!ss.contains(String.valueOf(dayOfMonth)))
                                sb.append(ss + "," + dayOfMonth+":"+getShiftCode(shiftSpinner.getAdapter().getItem(selectedShiftPosition).toString()));
                            else
                                sb.append(ss);

                        } else
                            sb.append(ss + dayOfMonth+":"+getShiftCode(shiftSpinner.getAdapter().getItem(selectedShiftPosition).toString()));

                        vv.setText(sb.toString());
                        //calculatePresentDays(sb.toString(), itemPosition);
                        if(existdata.trim().length()>0)
                            data.get(itemPosition).setExtraDuty(existdata+","+sb.toString());
                        else
                            data.get(itemPosition).setExtraDuty(sb.toString());
                        iRefreshListView.changeRowData(itemPosition, data);
                    }
                }, mYear,mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Button doneBtn=(Button) dialog.findViewById(R.id.alertDone);
        Button resetBtn=(Button) dialog.findViewById(R.id.alertReset);

        doneBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
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


    private String getShiftCode(String shiftName)
    {
        return String.valueOf(shiftName.charAt(0));
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

    private int calculatePresentDays(String val, int position)
    {
        if(val!=null && val.length()>0) {
            if (data.get(position).getAbsentOrPresent() == 0) {
                if (val.contains(",")) {
                    String[] dayCount = val.split(",");
                    int pDays = (currentMonthDays - dayCount.length);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) + getAttTypeCount(data.get(position).getExtraDuty()));
                } else {
                    int pDays = (currentMonthDays - 1);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) + getAttTypeCount(data.get(position).getExtraDuty()));
                }


            } else {
                if (val.contains(",")) {
                    String[] dayCount = val.split(",");
                    int pDays = (dayCount.length);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) + getAttTypeCount(data.get(position).getExtraDuty()));
                } else {
                    int pDays = (1);
                    System.out.println("Present days: " + pDays);
                    data.get(position).setPresentDaysCount(pDays);
                    data.get(position).setTotalCount1(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())));
                    data.get(position).setTotalCount2(((pDays + getAttTypeCount(data.get(position).getWeeklyOffDays()))-getAttTypeCount(data.get(position).getWeeklyOffDays())) + getAttTypeCount(data.get(position).getNationalHoliday()) +getAttTypeCount(data.get(position).getExtraDuty()));
                }
            }


        }
        return data.get(position).getTotalCount1();

    }

    private void showAlertDialog(int i) {
        // Prepare grid view
        ArrayList<MonthView> monthViews;
        GridView gridView = new GridView(context);

        int defultVal=0;
        int defaultVal1=1;
        System.out.println("Switch Status: "+data.get(i).getAbsentOrPresent());
        if(data.get(i).getAbsentOrPresent()==1)
            defultVal=1;
        else
            defultVal=0;

        monthViews=new ArrayList<>();
        MonthView monthView = null;
        String ss=data.get(i).getAttendanceDates();
        if(ss!=null && ss.trim().length()>0) {
            String[] ss1 = ss.split(",");
            for (int m = 1; m <= currentMonthDays; m++) {
                monthView = new MonthView();
                monthView.setDayNumber(m );

                for (int j = 0; j < ss1.length; j++) {

                    if(defultVal==1) monthView.setDayType(0);
                    else monthView.setDayType(1);
                    String[] monthVal=ss1[j].split(":");
                    if(m==Integer.parseInt(monthVal[0]))
                    {
                        monthView.setPresentDayType(1);
                        monthView.setDutyTypeName("P");
                        monthView.setDutyTypeShift(monthVal[1]);
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
                            monthViews.get(w).setDutyTypeName(monthViews.get(w).getDutyTypeName()+"\nWO");
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
                        String[] monthVal=sWO[j].split(":");

                        if(monthViews.get(w).getDayNumber()==Integer.parseInt(monthVal[0]))
                        {
                            monthViews.get(w).setDutyTypeName(monthViews.get(w).getDutyTypeName()+"\nED");
                            monthViews.get(w).setDutyTypeShift(monthVal[1]);
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
                            monthViews.get(w).setDutyTypeName(monthViews.get(w).getDutyTypeName()+"\nNH");
                        }
                    }
                }
            }

            gridView.setAdapter(new MyAdapter(monthViews));
            gridView.setNumColumns(7);
            gridView.setHorizontalSpacing(10);
            gridView.setVerticalSpacing(10);

            // Set grid view to alertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(gridView);
            builder.setTitle("Attendance Summary");
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
        ;
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
            //icon.setText(String.valueOf(monthViews.get(position).getDayNumber()));
            if(monthViews.get(position).getPresentDayType()==1)
            {
                icon.setText(String.valueOf(monthViews.get(position).getDayNumber())+":"+monthViews.get(position).getDutyTypeName()+"("+monthViews.get(position).getDutyTypeShift()+")");
                icon.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            }
            else {
                icon.setText(String.valueOf(monthViews.get(position).getDayNumber()));
                icon.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            }

            return convertView;
        }
    }




}*/
