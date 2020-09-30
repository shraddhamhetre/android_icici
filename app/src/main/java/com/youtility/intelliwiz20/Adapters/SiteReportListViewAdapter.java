package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by youtility on 7/4/18.
 */

public class SiteReportListViewAdapter extends RecyclerView.Adapter<SiteReportListViewAdapter.MyViewHolder>{
    private Drawable bulletAssigned;
    private Drawable bulletInprogress;
    private Drawable bulletCompleted;
    private Drawable bulletArchived;
    private Drawable bulletClosed;
    private Drawable assignedToPeople;
    private Drawable assignedToGroup;
    private Context mContext;
    private ArrayList<JobNeed>jobNeedArrayList;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;
    private QuestionDAO questionDAO;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(JobNeed item);
    }

    public SiteReportListViewAdapter(Context mContext, ArrayList<JobNeed>jobNeedArrayList, OnItemClickListener listener)
    {
        this.mContext=mContext;
        this.jobNeedArrayList=jobNeedArrayList;
        typeAssistDAO=new TypeAssistDAO(this.mContext);
        assetDAO=new AssetDAO(this.mContext);
        questionDAO=new QuestionDAO(this.mContext);
        this.listener=listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mIdView;
        public TextView mContentView;
        public TextView mpDateView;
        public ImageView mColorCodeBulletView;
        public ImageView mAssignedToImageview;

        public MyViewHolder(View view) {
            super(view);
            mView=view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mpDateView = (TextView) view.findViewById(R.id.jnPDateDesc);
            mColorCodeBulletView=(ImageView)view.findViewById(R.id.colorCodeBullet);
            mAssignedToImageview=(ImageView)view.findViewById(R.id.assignedToImageview);

            bulletAssigned=mContext.getResources().getDrawable(R.drawable.bulletassigned);
            bulletInprogress=mContext.getResources().getDrawable(R.drawable.bulletinprogress);
            bulletCompleted=mContext.getResources().getDrawable(R.drawable.bulletcompleted);
            bulletArchived=mContext.getResources().getDrawable(R.drawable.bulletarchived);
            bulletClosed=mContext.getResources().getDrawable(R.drawable.bulletclosed);
            assignedToGroup=mContext.getResources().getDrawable(R.drawable.ic_group_black);
            assignedToPeople=mContext.getResources().getDrawable(R.drawable.ic_person_black);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_list_content, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(SiteReportListViewAdapter.MyViewHolder holder, final int position) {
        final JobNeed jobNeed=jobNeedArrayList.get(position);
        holder.mIdView.setText(jobNeed.getJobneedid()+"");
        holder.mContentView.setText(questionDAO.getQuestionSetName(jobNeed.getQuestionsetid())+"");
        System.out.println("Status: "+jobNeed.getJobstatus());
        String jStatus=typeAssistDAO.getEventTypeCode(jobNeed.getJobstatus());

        if(jobNeed.getJobstatus()!=-1) {
            if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_ASSIGNED)) {
                //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletAssigned,null,null,null);
                holder.mColorCodeBulletView.setImageDrawable(bulletAssigned);
            } else if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_INPROGRESS)) {
                //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletInprogress,null,null,null);
                holder.mColorCodeBulletView.setImageDrawable(bulletInprogress);
            } else if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED)) {
                //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletCompleted,null,null,null);
                holder.mColorCodeBulletView.setImageDrawable(bulletCompleted);
            } else if (jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED)) {
                //holder.mContentView.setCompoundDrawablesWithIntrinsicBounds(bulletClosed,null,null,null);
                holder.mColorCodeBulletView.setImageDrawable(bulletClosed);
            }
        }
        //holder.mpDateView.setText(CommonFunctions.getFormatedDateWithoutTime(mValues.get(position).getPlandatetime())+" "+mValues.get(position).getJobdesc());

        System.out.println("-----------------------From DB: "+jobNeed.getPlandatetime());
        System.out.println("-----------------------From Conversion: "+ CommonFunctions.getDeviceTimezoneFormatDate(jobNeed.getPlandatetime()));
        String text = "<font color=#18B064>"+((jobNeed.getPlandatetime()))+"</font> <font color=#919191>"+" "+jobNeed.getJobdesc()+"</font>";
        holder.mpDateView.setText(Html.fromHtml(text));

        if(jobNeed.getPeopleid()!=-1)
            holder.mAssignedToImageview.setImageDrawable(assignedToPeople);
        else if(jobNeed.getGroupid()!=-1)
            holder.mAssignedToImageview.setImageDrawable(assignedToGroup);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Position: "+position);
                listener.onItemClick(jobNeed);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobNeedArrayList.size();
    }


}
