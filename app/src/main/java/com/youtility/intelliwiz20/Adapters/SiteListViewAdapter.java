package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Activities.SiteInfoActivity;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.Model.SiteList;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 *
 * show list of sites and relevant reporting manager
 */

public class SiteListViewAdapter extends BaseAdapter
{
    private Context context;
    //private ArrayList<Sites> data;
    //private Sites siteInfo;
    private ArrayList<SiteList> data;
    private SiteList siteInfo;
    private PeopleDAO peopleDAO;

    /*public SiteListViewAdapter(Context context, ArrayList<Sites> data)
    {
        this.context=context;
        this.data=data;

        peopleDAO=new PeopleDAO(context);
    }*/

    public SiteListViewAdapter(Context context, ArrayList<SiteList> data)
    {
        this.context=context;
        this.data=data;

        peopleDAO=new PeopleDAO(context);
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
            view=inflter.inflate(R.layout.activity_site_list_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.siteName=(TextView)view.findViewById(R.id.siteNameTextView);
            viewHolderItem.reportToName=(TextView)view.findViewById(R.id.reportToTextView);
            viewHolderItem.siteInfoImageView=(ImageView)view.findViewById(R.id.siteInfo);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }


        //final Sites site=data.get(i);
        final SiteList site=data.get(i);
        if(site!=null)
        {
            siteInfo=new SiteList();
            siteInfo=site;
            viewHolderItem.siteName.setText(site.getBuname());
            if(!site.getIncharge().equalsIgnoreCase("null") && site.getIncharge().trim().length()>0)
            {
                viewHolderItem.reportToName.setText(context.getResources().getString(R.string.sitelistview_siteincharge));
                //viewHolderItem.reportToName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.baseline_more_horiz_black_48,0);
            }
            else {
                viewHolderItem.reportToName.setText(context.getResources().getString(R.string.sitelistview_siteinchargenotavail));
                //viewHolderItem.reportToName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }

            viewHolderItem.reportToName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!site.getIncharge().equalsIgnoreCase("null") && site.getIncharge().trim().length()>0)
                    {
                        final ArrayAdapter<String> inchargeList=new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
                        if(site.getIncharge().trim().length()>0)
                        {
                            if(site.getIncharge().contains(","))
                            {
                                String[] inchrgList=site.getIncharge().split(",");
                                if(inchrgList.length>0)
                                {
                                    for(int p=0;p<inchrgList.length;p++)
                                    {
                                        inchargeList.add(inchrgList[p].replace("~"," : "));
                                    }
                                }
                            }
                            else
                            {
                                inchargeList.add(site.getIncharge().replace("~"," : "));
                            }
                        }

                        new AlertDialog.Builder(v.getContext())
                                .setTitle(context.getResources().getString(R.string.sitelistview_siteincharge_dialogtitle))
                                .setIcon(android.R.drawable.ic_menu_info_details)
                                .setAdapter(inchargeList,null)
                                .setSingleChoiceItems(inchargeList, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String []selectedItem = inchargeList.getItem(which).split(":");
                                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", selectedItem[1]);
                                        if (clipboard == null) return;
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(context,context.getResources().getString(R.string.sitelistview_numbercopied),Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.ok, null).show();
                    }

                }
            });


            /*if(site.getReportids().equalsIgnoreCase("-1"))
                viewHolderItem.siteName.setTextColor(context.getResources().getColor(R.color.colorRed));
            viewHolderItem.reportToName.setText(context.getResources().getString(R.string.sitelist_reportto, peopleDAO.getPeopleName(data.get(i).getReportto())));*/
            viewHolderItem.siteInfoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callIntent(site);
                }
            });

        }

        /*view = inflter.inflate(R.layout.activity_site_list_row, null);
        TextView siteName = (TextView)view.findViewById(R.id.siteNameTextView);
        TextView reportToName = (TextView)view.findViewById(R.id.reportToTextView);
        siteName.setText(data.get(i).getBuname()+"");
        reportToName.setText(context.getResources().getString(R.string.sitelist_reportto)+peopleDAO.getPeopleName(data.get(i).getReportto())+"");
        return view;*/

        return view;
    }

    private void callIntent(SiteList siteInf)
    {
        Intent infoIntent=new Intent(context, SiteInfoActivity.class);
        infoIntent.putExtra("SITE_ID",siteInf.getBuid());
        infoIntent.putExtra("SITE_NAME",siteInf.getBuname());
        context.startActivity(infoIntent);
    }

    static class ViewHolderItem
    {
        TextView siteName;
        TextView reportToName;
        ImageView siteInfoImageView;
    }
}
