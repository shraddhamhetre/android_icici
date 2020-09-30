package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.youtility.intelliwiz20.R;

/**
 * Created by PrashantD on 11/08/17.
 *
 * Application access grid view adapter
 *
 */

public class ApplicationGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String>appList;
    public ApplicationGridViewAdapter(Context context, List<String> appList)
    {
        this.context=context;
        this.appList=appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid=null;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            grid=new View(context);
            grid=inflater.inflate(R.layout.app_gridview_row,null);
            TextView appName = (TextView)grid.findViewById(R.id.appName);
            TextView appDesc = (TextView)grid.findViewById(R.id.appDesc);
            ImageView appImage = (ImageView)grid.findViewById(R.id.appIcon);

            appName.setText(appList.get(position).toString().trim());

        }
        else
        {
            grid = (View)convertView;
        }

        return grid;
    }
}
