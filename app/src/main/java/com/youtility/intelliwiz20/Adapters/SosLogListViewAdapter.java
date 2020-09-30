package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 *
 * show list of sites and relevant reporting manager
 */

public class SosLogListViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<PeopleEventLog> data;
    private int val;
    public SosLogListViewAdapter(Context context, ArrayList<PeopleEventLog> data, int val)
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
            view=inflter.inflate(R.layout.activity_sos_log_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.siteName=(TextView)view.findViewById(R.id.siteNameTextView);
            viewHolderItem.location=(TextView)view.findViewById(R.id.locationTypeTextView);
            viewHolderItem.timestamp=(TextView)view.findViewById(R.id.TimestampTextView);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }


        final PeopleEventLog sosLog=data.get(i);
        if(sosLog!=null)
        {

            viewHolderItem.siteName.setText("Panic");//viewHolderItem.siteName.setText(siteVisitedLog.getBuname() + "\n" + siteVisitedLog.getRemarks());


            //viewHolderItem.location.setText(siteVisitedLog.getGpslocation());
            viewHolderItem.location.setText(Html.fromHtml(sosLog.getGpslocation()));

            //viewHolderItem.location.setText((context.getResources().getString(R.string.sos_my_location,roundTwoDecimals(19.20), roundTwoDecimals(73.20))));


            viewHolderItem.timestamp.setText(CommonFunctions.getDeviceTimezoneFormatDate(sosLog.getDatetime()));

            viewHolderItem.location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://maps.google.com/maps?q="+sosLog.getGpslocation();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
                    context.startActivity(intent);
                }
            });


            if(val==0)
            {
                viewHolderItem.location.setVisibility(View.VISIBLE);
                viewHolderItem.timestamp.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolderItem.location.setVisibility(View.GONE);
                viewHolderItem.timestamp.setVisibility(View.GONE);
            }
        }


        return view;
    }
    private String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return String.valueOf(Double.valueOf(twoDForm.format(d)));
    }
    static class ViewHolderItem
    {
        TextView siteName;
        TextView location;
        TextView timestamp;
    }
}
