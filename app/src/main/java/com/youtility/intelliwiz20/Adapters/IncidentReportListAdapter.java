package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by PrashantD on 12/12/17.
 */

public class IncidentReportListAdapter extends BaseAdapter
{
    private ArrayList<JobNeed> data;
    private LayoutInflater inflter;
    private Context context;
    public IncidentReportListAdapter(Context context,ArrayList<JobNeed> data )
    {
        this.context=context;
        this.data=data;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*ViewHolderItem viewHolderItem;
        if(convertView==null)
        {
            LayoutInflater inflter = ((Activity)context).getLayoutInflater();
            convertView=inflter.inflate(R.layout.activity_incident_report_list_row,parent, false);

            viewHolderItem=new ViewHolderItem();

            viewHolderItem.irId = (TextView)convertView.findViewById(R.id.id);
            viewHolderItem.irContent = (TextView)convertView.findViewById(R.id.content);
            viewHolderItem.irDate = (TextView)convertView.findViewById(R.id.irDate);
            viewHolderItem.irStatus=(ImageView)convertView.findViewById(R.id.colorCodeBullet);

        }
        else
        {
            viewHolderItem=(ViewHolderItem)convertView.getTag();
        }

        JobNeed jobNeed=data.get(position);

        if(jobNeed!=null)
        {
            viewHolderItem.irId.setText(String.valueOf(jobNeed.getJobneedid()));
            viewHolderItem.irContent.setText(jobNeed.getRemarks()+" Status: "+jobNeed.getSyncstatus());
            viewHolderItem.irDate.setText(jobNeed.getPlandatetime());
            if(jobNeed.getSyncstatus()==0)
                viewHolderItem.irStatus.setImageResource((R.drawable.ic_done_black_24dp));
            else
                viewHolderItem.irStatus.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        }*/


        convertView = inflter.inflate(R.layout.activity_incident_report_list_row, parent, false);
        TextView irId = (TextView)convertView.findViewById(R.id.id);
        irId.setTextColor(Color.parseColor("#ffffff"));

        TextView irContent = (TextView)convertView.findViewById(R.id.content);
        irContent.setTextColor(Color.parseColor("#ffffff"));

        TextView irDate = (TextView)convertView.findViewById(R.id.irDate);
        irDate.setTextColor(Color.parseColor("#ffffff"));

        ImageView irStatus=(ImageView)convertView.findViewById(R.id.colorCodeBullet);
        if(data.get(position)!=null)
        {
            irId.setText(String.valueOf(data.get(position).getJobneedid()));
            irContent.setText(data.get(position).getRemarks());
            irDate.setText(data.get(position).getPlandatetime());
            System.out.println("syncStatus==+"+data.get(position).getSyncstatus());
            if(data.get(position).getSyncstatus()==0)
                irStatus.setImageResource((R.drawable.ic_done_black_24dp));
            else
                irStatus.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        }
        return convertView;
    }

    static class ViewHolderItem
    {
        private TextView irId;
        private TextView irContent;
        private TextView irDate;
        private ImageView irStatus;
    }
}
