package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.RecyclerViewClickListener;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;
import java.util.Date;

public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.MyViewHolder> implements  Filterable{

    private ArrayList<JobNeed> mValues;
    private ArrayList<JobNeed> mFilteredList;
    private Drawable bulletAssigned;
    private Drawable bulletInprogress;
    private Drawable bulletCompleted;
    private Drawable bulletArchived;
    private Drawable bulletClosed;
    private Drawable assignedToPeople;
    private Drawable assignedToGroup;
    private Context context;
    private AssetDAO assetDAO;
    private TypeAssistDAO typeAssistDAO;

    private RecyclerViewClickListener mListener;


    public TaskListRecyclerViewAdapter(Context context,ArrayList<JobNeed> items,RecyclerViewClickListener mListener) {
        this.context=context;
        mValues = items;
        mFilteredList=items;
        assetDAO=new AssetDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        this.mListener=mListener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_item_list_content, parent, false);
        return new TaskListRecyclerViewAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getJobneedid()+"");
        if(assetDAO.getAssetName(mValues.get(position).getAssetid()).trim().length()>0)
            holder.mContentView.setText(assetDAO.getAssetName(mValues.get(position).getAssetid())+"");
        else
            holder.mContentView.setText(assetDAO.getAssetCode(mValues.get(position).getAssetid())+"");

        System.out.println("Status: "+mValues.get(position).getJobstatus());
        if(mValues.get(position).getJobstatus()!=-1) {
            String jStatus = typeAssistDAO.getEventTypeCode(mValues.get(position).getJobstatus());

            if(jStatus!=null) {
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
        }


        //holder.mpDateView.setText(CommonFunctions.getFormatedDateWithoutTime(mValues.get(position).getPlandatetime())+" "+mValues.get(position).getJobdesc());

        System.out.println("-----------------------From DB: "+mValues.get(position).getPlandatetime());
        System.out.println("-----------------------From Conversion: "+ CommonFunctions.getDeviceTimezoneFormatDate(mValues.get(position).getPlandatetime()));
        String text = "<font color=#18B064>"+((mValues.get(position).getPlandatetime()))+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
        holder.mpDateView.setText(Html.fromHtml(text));

        if(mValues.get(position).getPeopleid()!=-1)
            holder.mAssignedToImageview.setImageDrawable(assignedToPeople);
        else if(mValues.get(position).getGroupid()!=-1)
            holder.mAssignedToImageview.setImageDrawable(assignedToGroup);


        //holder.mDescView.setText(mValues.get(position).getJobdesc());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonFunctions.isPermissionGranted(context))
                {
                    int expVal=isTaskExpired(mValues.get(position));
                    if(typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED)
                            || typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()).equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                        mListener.onClick(v,position,mValues.get(position).getJobneedid(),-1, typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()));
                    else if(expVal==2)
                    {
                        mListener.onClick(v,position,mValues.get(position).getJobneedid(),2, typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()));
                    }
                    else if(expVal==0)
                    {
                        mListener.onClick(v,position,mValues.get(position).getJobneedid(),0,typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()));
                        Snackbar.make(v, context.getResources().getString(R.string.job_is_future, holder.mItem.getPlandatetime()), Snackbar.LENGTH_LONG).show();
                    }
                    else
                        mListener.onClick(v,position,mValues.get(position).getJobneedid(),1,typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()));

                    //mListener.onClick(v,position,mValues.get(position).getJobneedid(),1,typeAssistDAO.getEventTypeName(mValues.get(position).getJobstatus()));

                }
                else
                    Snackbar.make(holder.mView,context.getResources().getString(R.string.error_msg_grant_permission),Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private int isTaskExpired(JobNeed jobNeed)
    {
        System.out.println("Jobneed plandate: "+jobNeed.getPlandatetime());
        System.out.println("Jobneed expirydate: "+jobNeed.getExpirydatetime());
        System.out.println("Jobneed gracetime: "+jobNeed.getGracetime());

        long backDate=new Date( CommonFunctions.getParse24HrsDate((jobNeed.getPlandatetime()))- (jobNeed.getGracetime() * 60 * 1000)).getTime();
        return CommonFunctions.isInBetweenDate(backDate,CommonFunctions.getParse24HrsDate((jobNeed.getExpirydatetime())));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                System.out.println("charString: "+charString);

                if (charString.isEmpty()) {

                    mValues = mFilteredList;
                } else {

                    ArrayList<JobNeed> filteredList = new ArrayList<>();

                    for (JobNeed jobNeed : mFilteredList    ) {

                        if (jobNeed.getJobdesc().trim().toUpperCase().contains(charString.toUpperCase().trim()) ||
                                jobNeed.getPlandatetime().trim().toUpperCase().contains(charString.toUpperCase().trim())) {

                            filteredList.add(jobNeed);
                        }

                    }

                    mValues=filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mValues;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mValues = (ArrayList<JobNeed>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mpDateView;
        public final ImageView mColorCodeBulletView;
        public final ImageView mAssignedToImageview;
        //public final TextView mDescView;
        public JobNeed mItem;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mpDateView = (TextView) view.findViewById(R.id.jnPDateDesc);
            mColorCodeBulletView=(ImageView)view.findViewById(R.id.colorCodeBullet);
            mAssignedToImageview=(ImageView)view.findViewById(R.id.assignedToImageview);
            //mDescView = (TextView) view.findViewById(R.id.jnDesc);

            bulletAssigned=context.getResources().getDrawable(R.drawable.bulletassigned);
            bulletInprogress=context.getResources().getDrawable(R.drawable.bulletinprogress);
            bulletCompleted=context.getResources().getDrawable(R.drawable.bulletcompleted);
            bulletArchived=context.getResources().getDrawable(R.drawable.bulletarchived);
            bulletClosed=context.getResources().getDrawable(R.drawable.bulletclosed);
            assignedToGroup=context.getResources().getDrawable(R.drawable.ic_group_black);
            assignedToPeople=context.getResources().getDrawable(R.drawable.ic_person_black);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
