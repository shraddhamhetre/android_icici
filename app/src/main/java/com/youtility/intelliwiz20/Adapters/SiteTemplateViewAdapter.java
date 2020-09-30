package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.SiteTemplateDAO;
import com.youtility.intelliwiz20.Model.TemplateList;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 *
 * show list of sites and relevant reporting manager
 */

public class SiteTemplateViewAdapter extends BaseAdapter
{
    private Context context;
    //private ArrayList<QuestionSet> data;
    private ArrayList<TemplateList> data;
    //private LayoutInflater inflter;
    private SiteTemplateDAO siteTemplateDAO;
    /*public SiteTemplateViewAdapter(Context context, ArrayList<QuestionSet> data)
    {
        this.context=context;
        this.data=data;

        siteTemplateDAO=new SiteTemplateDAO(context);
    }*/

    public SiteTemplateViewAdapter(Context context, ArrayList<TemplateList> data)
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
            view=inflter.inflate(R.layout.activity_site_report_template_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.siteName=(TextView)view.findViewById(R.id.templateNameTextView);
            viewHolderItem.templateCount=(TextView)view.findViewById(R.id.badge_template_count_notification);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }


        //final QuestionSet site=data.get(i);
        final TemplateList site=data.get(i);
        if(site!=null)
        {
            int tCount=siteTemplateDAO.getCount(site.getQuestionsetid());
            if(tCount>0)
                viewHolderItem.siteName.setTextColor(context.getResources().getColor(R.color.colorGreen));
            else
                viewHolderItem.siteName.setTextColor(context.getResources().getColor(R.color.colorBlack));

            viewHolderItem.siteName.setText(site.getQsetname());
            viewHolderItem.templateCount.setText(String.valueOf(tCount));
            System.out.println("siteTemplateDAO.getCount(): "+siteTemplateDAO.getCount(site.getQuestionsetid()));
        }

        return view;
    }

    static class ViewHolderItem
    {
        TextView siteName;
        TextView templateCount;
        ImageView siteInfoImageView;
    }
}
