package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.youtility.intelliwiz20.Model.SiteVisitLogGroup;
import com.youtility.intelliwiz20.Model.SiteVisitedLog;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by youtility on 18/7/18.
 */

public class SiteVisitLogEListViewAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private ArrayList<SiteVisitLogGroup> siteVisitLogGroupArrayList;

    public SiteVisitLogEListViewAdapter(Context context,ArrayList<SiteVisitLogGroup> siteVisitLogGroupArrayList)
    {
        this.context=context;
        this.siteVisitLogGroupArrayList=siteVisitLogGroupArrayList;
    }

    @Override
    public int getGroupCount() {
        return siteVisitLogGroupArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<SiteVisitedLog> productList = siteVisitLogGroupArrayList.get(groupPosition).getSiteVisitedLogArrayList();
        return productList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return siteVisitLogGroupArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<SiteVisitedLog> productList = siteVisitLogGroupArrayList.get(groupPosition).getSiteVisitedLogArrayList();
        return productList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        SiteVisitLogGroup headerInfo = (SiteVisitLogGroup) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.activity_site_visited_log_explist_header, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.siteNameHeaderTextView);
        heading.setText(headerInfo.getSiteName().trim());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        SiteVisitedLog detailInfo = (SiteVisitedLog) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_site_visited_log_row, null);
        }

        TextView siteName=(TextView)convertView.findViewById(R.id.siteNameTextView);
        TextView punchStatus=(TextView)convertView.findViewById(R.id.punchTypeTextView);
        TextView punchTimestamp=(TextView)convertView.findViewById(R.id.punchTimestampTextView);

        if(detailInfo!=null)
        {
            if(detailInfo.getRemarks()!=null && !detailInfo.getRemarks().equalsIgnoreCase("null") && detailInfo.getRemarks().trim().length()>0) {
                //siteName.setText(detailInfo.getBuname()+"\n"+detailInfo.getRemarks());
                siteName.setText(detailInfo.getRemarks());
            }
            else {
                //siteName.setText(detailInfo.getBuname());
                siteName.setText("");
            }
            punchStatus.setText(detailInfo.getPunchstatus());
            punchTimestamp.setText(detailInfo.getPunchtime());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
