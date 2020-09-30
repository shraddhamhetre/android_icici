package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youtility.intelliwiz20.Model.SiteVisitedLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 *
 * show list of sites and relevant reporting manager
 */

public class SiteVisitedLogListViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<SiteVisitedLog> data;
    private int val;
    public SiteVisitedLogListViewAdapter(Context context, ArrayList<SiteVisitedLog> data, int val)
    {
        this.context=context;
        this.data=data;
        this.val=val;
    }

    public void clearList()
    {
        data.clear();
        notifyDataSetInvalidated();
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolderItem viewHolderItem;
        if(view==null)
        {
            LayoutInflater inflter = ((Activity)context).getLayoutInflater();
            view=inflter.inflate(R.layout.activity_site_visited_log_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.siteName=(TextView)view.findViewById(R.id.siteNameTextView);
            viewHolderItem.punchStatus=(TextView)view.findViewById(R.id.punchTypeTextView);
            viewHolderItem.punchTimestamp=(TextView)view.findViewById(R.id.punchTimestampTextView);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }


        SiteVisitedLog siteVisitedLog=data.get(i);
        if(siteVisitedLog!=null)
        {
            System.out.println("remarks+"+siteVisitedLog.getRemarks());
            System.out.println("buname+"+siteVisitedLog.getBuname());

            if(siteVisitedLog.getRemarks()!=null && !siteVisitedLog.getRemarks().equalsIgnoreCase("null") && siteVisitedLog.getRemarks().trim().length()>0) {
                if (siteVisitedLog.getBuname() != null && !siteVisitedLog.getBuname().equalsIgnoreCase("null") && siteVisitedLog.getBuname().trim().length() > 0)
                    viewHolderItem.siteName.setText(siteVisitedLog.getBuname());//viewHolderItem.siteName.setText(siteVisitedLog.getBuname() + "\n" + siteVisitedLog.getRemarks());
                else
                    viewHolderItem.siteName.setText(siteVisitedLog.getRemarks());
            }
            else
                viewHolderItem.siteName.setText(siteVisitedLog.getBuname());

            viewHolderItem.punchStatus.setText(siteVisitedLog.getPunchstatus());
            if(siteVisitedLog.getPunchstatus()!=null) {
                if (siteVisitedLog.getPunchstatus().equalsIgnoreCase(Constants.ATTENDANCE_PUNCH_TYPE_IN))
                    viewHolderItem.punchStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
                else
                    viewHolderItem.punchStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
            }
            viewHolderItem.punchTimestamp.setText(siteVisitedLog.getPunchtime());

            if(val==0)
            {
                viewHolderItem.punchStatus.setVisibility(View.VISIBLE);
                viewHolderItem.punchTimestamp.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolderItem.punchStatus.setVisibility(View.GONE);
                viewHolderItem.punchTimestamp.setVisibility(View.GONE);
            }
        }


        return view;
    }

    static class ViewHolderItem
    {
        TextView siteName;
        TextView punchStatus;
        TextView punchTimestamp;
    }
}
