package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 */

public class CheckPointChildListViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<JobNeed> data;
    private LayoutInflater inflter;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    public CheckPointChildListViewAdapter(Context context, ArrayList<JobNeed> data)
    {
        this.context=context;
        this.data=data;
        inflter = (LayoutInflater.from(context));
        jobNeedDAO=new JobNeedDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        assetDAO=new AssetDAO(context);
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
        return (0);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolderItem viewHolderItem;
        if(view==null)
        {
            LayoutInflater inflter = ((Activity)context).getLayoutInflater();
            view=inflter.inflate(R.layout.activity_checkpoint_child_list_row,viewGroup, false);
            viewHolderItem=new ViewHolderItem();
            viewHolderItem.tourName=(TextView)view.findViewById(R.id.childTourNameTextView);
            viewHolderItem.tourDate=(TextView)view.findViewById(R.id.childTourDateTextView);
            viewHolderItem.statusView=(ImageView)view.findViewById(R.id.statusView);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }

        JobNeed jobNeed=data.get(i);
        if(jobNeed!=null)
        {
            viewHolderItem.tourName.setText(assetDAO.getAssetName(jobNeed.getAssetid()));
            viewHolderItem.tourDate.setText((jobNeed.getPlandatetime()));
            String jStatus=typeAssistDAO.getEventTypeCode(jobNeed.getJobstatus());
            if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_ASSIGNED))
                viewHolderItem.tourName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.bulletassigned,0);
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_INPROGRESS))
                viewHolderItem.tourName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.bulletinprogress,0);
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                viewHolderItem.tourName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.bulletcompleted,0);
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                viewHolderItem.tourName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.bulletclosed,0);

            /*if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_ASSIGNED))
                viewHolderItem.statusView.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_INPROGRESS))
                viewHolderItem.statusView.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                viewHolderItem.statusView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                viewHolderItem.statusView.setBackgroundColor(context.getResources().getColor(R.color.colorRed));*/
        }


        /*view = inflter.inflate(R.layout.activity_checkpoint_child_list_row, null);
        TextView tourName = (TextView)view.findViewById(R.id.childTourNameTextView);
        TextView tourDate = (TextView)view.findViewById(R.id.childTourDateTextView);
        ImageView statusView=(ImageView)view.findViewById(R.id.statusView);
        tourName.setText(data.get(i).getJobdesc()+"");
        tourDate.setText(CommonFunctions.getFormatedDate(data.get(i).getPlandatetime()));
        String jStatus=typeAssistDAO.getEventTypeName(data.get(i).getJobstatus());
        if(jStatus.equalsIgnoreCase("Pending") || jStatus.equalsIgnoreCase("Assigned"))
            statusView.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
        else if(jStatus.equalsIgnoreCase("Inprogress"))
            statusView.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
        else if(jStatus.equalsIgnoreCase("Completed"))
            statusView.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
        else if(jStatus.equalsIgnoreCase("archived"))
            statusView.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
        else if(jStatus.equalsIgnoreCase("closed"))
            statusView.setBackgroundColor(context.getResources().getColor(R.color.colorRed));*/

        return view;
    }

    static class ViewHolderItem
    {
        private TextView tourName;
        private TextView tourDate;
        private ImageView statusView;
    }
}
