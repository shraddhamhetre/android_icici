package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.youtility.intelliwiz20.Interfaces.IGridviewChangeBackgroundItemListeners;
import com.youtility.intelliwiz20.Model.TransportMode;
import com.youtility.intelliwiz20.R;

import java.util.List;

/**
 * Created by PrashantD on 11/08/17.
 */

public class TransportModeGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<TransportMode>appList;
    private IGridviewChangeBackgroundItemListeners iGridviewItemClickListeners;
    public TransportModeGridViewAdapter(Context context, List<TransportMode> appList,IGridviewChangeBackgroundItemListeners iGridviewItemClickListeners)
    {
        this.context=context;
        this.appList=appList;
        this.iGridviewItemClickListeners=iGridviewItemClickListeners;
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        /*View grid=null;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            grid=new View(context);
            grid=inflater.inflate(R.layout.transportmode_gridview_row,null);
            TextView gridTimeTextview = (TextView)grid.findViewById(R.id.gridTimeTextview);
            TextView gridDistanceTextview = (TextView)grid.findViewById(R.id.gridDistanceTextview);
            TextView gridMoneyTextview = (TextView)grid.findViewById(R.id.gridMoneyTextview);
            Button gridTravelModeButton=(Button)grid.findViewById(R.id.gridTravelModeButton);

            gridTravelModeButton.setText(appList.get(position).getTravelMode());
            gridTimeTextview.setText(appList.get(position).getTravelTime());
            gridDistanceTextview.setText(appList.get(position).getTravelDistance());
            gridMoneyTextview.setText(appList.get(position).getTravelMoney());

        }
        else
        {
            grid = (View)convertView;
        }*/

        Holder holder=new Holder();
        View rowView;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.transportmode_gridview_row, null);
        holder.gridTimeTextview =(TextView) rowView.findViewById(R.id.gridTimeTextview);
        holder.gridDistanceTextview =(TextView) rowView.findViewById(R.id.gridDistanceTextview);
        holder.gridMoneyTextview=(TextView)rowView.findViewById(R.id.gridMoneyTextview);
        holder.gridTravelModeButton=(Button)rowView.findViewById(R.id.gridTravelModeButton);

        if(appList.get(position).getSelected())
        {
            holder.gridTravelModeButton.setTextColor(Color.RED);
            holder.gridTravelModeButton.setBackgroundColor(context.getResources().getColor(R.color.button_background));
        }
        else
        {
            holder.gridTravelModeButton.setTextColor(Color.WHITE);
            holder.gridTravelModeButton.setBackgroundColor(context.getResources().getColor(R.color.web_screen_background));
        }

        holder.gridTravelModeButton.setText(appList.get(position).getTravelMode());
        holder.gridTimeTextview.setText(appList.get(position).getTravelTime());
        holder.gridDistanceTextview.setText(appList.get(position).getTravelDistance());
        holder.gridMoneyTextview.setText(appList.get(position).getTravelMoney());

        holder.gridTravelModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGridviewItemClickListeners.onGridViewItemClick(position,appList.get(position).getTravelMode(),(Button)v);
            }
        });

        return rowView;

    }

    public class Holder
    {
        TextView gridTimeTextview;
        TextView gridDistanceTextview;
        TextView gridMoneyTextview;
        Button gridTravelModeButton;
    }
}
