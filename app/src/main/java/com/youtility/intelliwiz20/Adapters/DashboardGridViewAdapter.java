package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Interfaces.IGridviewItemClickListeners;
import com.youtility.intelliwiz20.Model.ApplicationAccess;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;


/**
 * Created by PrashantD on 11/08/17.
 */

public class DashboardGridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ApplicationAccess>applicationAccessArrayList;
    private IGridviewItemClickListeners iGridviewItemClickListeners;
    private LayoutInflater inflater;

    public DashboardGridViewAdapter(Context context, ArrayList<ApplicationAccess>applicationAccessArrayList, IGridviewItemClickListeners iGridviewItemClickListeners)
    {
        this.context=context;
        this.applicationAccessArrayList=applicationAccessArrayList;
        this.iGridviewItemClickListeners=iGridviewItemClickListeners;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return applicationAccessArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        //return applicationAccessArrayList.get(position);
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.app_gridview_row, null);
        holder.os_text =(TextView) rowView.findViewById(R.id.appName);
        //holder.os_text.setTextColor(Color.parseColor("#00aa00"));
        holder.os_img =(ImageView) rowView.findViewById(R.id.appIcon);
        holder.os_descText=(TextView)rowView.findViewById(R.id.appDesc);
        holder.cardLinerlayout=(LinearLayout)rowView.findViewById(R.id.cardlinearlayout);
        //holder.os_img_overlay=(ImageView)rowView.findViewById(R.id.appIcon1);

        holder.os_text.setText(applicationAccessArrayList.get(position).getAppName().toString().trim());

        holder.os_descText.setMovementMethod(new ScrollingMovementMethod());

        if(!applicationAccessArrayList.get(position).getIsAccess()) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_do_not_disturb_alt);
            holder.os_img.setImageDrawable(drawable);
            holder.os_descText.setText(context.getResources().getString(R.string.dashboard_noaccess));
            holder.cardLinerlayout.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        }
        else
        {
            holder.os_img.setImageResource(applicationAccessArrayList.get(position).getAppImage());
            //holder.os_descText.setText(applicationAccessArrayList.get(position).getAppDesc().toString().trim());
            /*if (position % 2 == 1) {
                holder.cardLinerlayout.setBackgroundColor(Color.BLUE);
            } else {
                holder.cardLinerlayout.setBackgroundColor(Color.CYAN);
            }*/
        }

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(applicationAccessArrayList.get(position).getIsAccess())
                {
                    iGridviewItemClickListeners.onGridViewItemClick(position,applicationAccessArrayList.get(position).getAppName(),true, applicationAccessArrayList.get(position).getAppCode());
                }
                else
                {
                    Toast.makeText(context, context.getResources().getString(R.string.dashboard_noaccess_msg),Toast.LENGTH_LONG).show();
                }
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(applicationAccessArrayList.get(position).getIsAccess())
                {
                    iGridviewItemClickListeners.onLongGridViewItemClick(position,applicationAccessArrayList.get(position).getAppName(),true, applicationAccessArrayList.get(position).getAppCode());
                }
                else
                {
                    Toast.makeText(context, context.getResources().getString(R.string.dashboard_noaccess_msg),Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        return rowView;

       /* View grid=null;

        if(convertView==null)
        {
            grid=new View(context);
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid=inflater.inflate(R.layout.app_gridview_row,null);
            TextView appName = (TextView)grid.findViewById(R.id.appName);
            TextView appDesc = (TextView)grid.findViewById(R.id.appDesc);
            ImageView appImage = (ImageView)grid.findViewById(R.id.appIcon);

            appName.setText(appList.get(position).toString().trim());
            System.out.println(appList.get(position).toString().trim()+" : "+position);
            appImage.setImageResource(dashboardImgList.getResourceId(position,0));

        }
        else
        {
            grid = (View)convertView;
        }

        return grid;*/


        /*convertView = inflater.inflate(R.layout.app_gridview_row, null); // inflate the layout
        TextView appName = (TextView)convertView.findViewById(R.id.appName);
        TextView appDesc = (TextView)convertView.findViewById(R.id.appDesc);
        //appDesc.setMovementMethod(new ScrollingMovementMethod());

        ImageView appImage = (ImageView)convertView.findViewById(R.id.appIcon);

        appName.setText(applicationAccessArrayList.get(position).getAppName().toString().trim());
        System.out.println(applicationAccessArrayList.get(position).getAppName().toString().trim()+" : "+position);
        appDesc.setText(applicationAccessArrayList.get(position).getAppDesc().toString().trim());

        if(applicationAccessArrayList.get(position).getIsAccess())
            appImage.setImageResource(applicationAccessArrayList.get(position).getAppImage());
        else {
            Drawable drawable=context.getResources().getDrawable(R.drawable.semi_tran_roundbox);
            appImage.setImageDrawable(drawable);
        }
        return convertView;*/
    }


    public class Holder
    {
        TextView os_text;
        ImageView os_img;
        TextView os_descText;
        LinearLayout cardLinerlayout;
        //ImageView os_img_overlay;
    }
}
