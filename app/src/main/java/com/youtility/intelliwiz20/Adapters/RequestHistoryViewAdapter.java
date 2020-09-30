package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 *
 * show list of requested template history
 */

public class RequestHistoryViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<JobNeed> data;

    public RequestHistoryViewAdapter(Context context, ArrayList<JobNeed> data)
    {
        this.context=context;
        this.data=data;
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
            view=inflter.inflate(R.layout.activity_request_history_listview_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.requestType=(TextView)view.findViewById(R.id.requestType);
            viewHolderItem.requestDesc=(TextView)view.findViewById(R.id.requestDesc);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }

        final JobNeed jobNeed=data.get(i);
        if(jobNeed!=null)
        {
            viewHolderItem.requestType.setText(jobNeed.getRemarks());
            String text =null;
            text = "<font color=#117A65>"+ (jobNeed.getPlandatetime())+"</font> <font color=#34495E>"+" "+jobNeed.getJobdesc()+"</font>";
            viewHolderItem.requestDesc.setText(Html.fromHtml(text));
        }

        return view;
    }

    static class ViewHolderItem
    {
        TextView requestType;
        TextView requestDesc;
    }
}
