package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.SiteTemplateDAO;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 *
 * show list of sites and relevant reporting manager
 */

public class RequestViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<TypeAssist> data;
    //private LayoutInflater inflter;
    private SiteTemplateDAO siteTemplateDAO;

    public RequestViewAdapter(Context context, ArrayList<TypeAssist> data)
    {
        this.context=context;
        this.data=data;
        siteTemplateDAO=new SiteTemplateDAO(context);
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
            view=inflter.inflate(R.layout.activity_request_template_listview_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.siteName=(TextView)view.findViewById(R.id.templateNameTextView);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }

        final TypeAssist site=data.get(i);
        if(site!=null)
        {
            viewHolderItem.siteName.setText(site.getTaname());
        }

        return view;
    }

    static class ViewHolderItem
    {
        TextView siteName;
    }
}
