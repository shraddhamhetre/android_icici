package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 9/9/17.
 */

public class CheckPointListViewAdapter extends BaseAdapter implements Filterable
{
    private Context context;
    private ArrayList<JobNeed> data;
    private ArrayList<JobNeed> mFilterList;
    private LayoutInflater inflter;
    private String jobType;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    public CheckPointListViewAdapter(Context context, ArrayList<JobNeed> data, String jobType)
    {
        this.context=context;
        this.data=data;
        mFilterList=data;
        this.jobType=jobType;
        inflter = (LayoutInflater.from(context));
        jobNeedDAO=new JobNeedDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                System.out.println("charString: "+charString);

                if (charString.isEmpty()) {

                    data = mFilterList;
                } else {

                    ArrayList<JobNeed> filteredList = new ArrayList<>();

                    for (JobNeed jobNeed : mFilterList    ) {

                        if (jobNeed.getJobdesc().trim().toUpperCase().contains(charString.toUpperCase().trim())) {

                            filteredList.add(jobNeed);
                        }

                    }

                    data=filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = data;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList<JobNeed>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolderItem
    {
        private TextView tourName;
        private TextView planDateTV;
        private TextView expDateTV;
        private TextView childCountTextview;
        private ProgressBar progressBar;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        /*ViewHolderItem viewHolderItem;
        if(view==null)
        {

            LayoutInflater inflter = ((Activity)context).getLayoutInflater();
            view=inflter.inflate(R.layout.activity_checkpoint_list_row,viewGroup, false);

            viewHolderItem=new ViewHolderItem();

            viewHolderItem.tourName = (TextView)view.findViewById(R.id.tourNameTextView);
            viewHolderItem.planDateTV=(TextView)view.findViewById(R.id.plandateTextView);
            viewHolderItem.expDateTV=(TextView)view.findViewById(R.id.expirydateTextView);
            viewHolderItem.progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
            viewHolderItem.childCountTextview=(TextView)view.findViewById(R.id.childCountTextview);
            view.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem=(ViewHolderItem)view.getTag();
        }

        JobNeed jobNeed=data.get(i);
        if(jobNeed!=null)
        {
            viewHolderItem.tourName.setText(jobNeed.getJobdesc());

            if(jobType.equalsIgnoreCase(Constants.JOB_TYPE_SCHEDULED)) {
                viewHolderItem.expDateTV.setVisibility(View.VISIBLE);
                viewHolderItem.planDateTV.setText(CommonFunctions.getFormatedDate(jobNeed.getPlandatetime()) );
                viewHolderItem.expDateTV.setText(CommonFunctions.getFormatedDate(jobNeed.getExpirydatetime()) );
                viewHolderItem.progressBar.setMax(jobNeedDAO.getChildCount(jobNeed.getJobneedid()));
                viewHolderItem.progressBar.setProgress(jobNeedDAO.getCompletedChildCount(jobNeed.getJobneedid()));
                viewHolderItem.childCountTextview.setText(jobNeedDAO.getChildCount(jobNeed.getJobneedid()));
            }
            else if(jobType.equalsIgnoreCase(Constants.JOB_TYPE_ADHOC))
            {
                viewHolderItem.planDateTV.setText((data.get(i).getCdtz()) );
                viewHolderItem.expDateTV.setVisibility(View.GONE);
                viewHolderItem.progressBar.setVisibility(View.GONE);
            }
        }*/

        view = inflter.inflate(R.layout.activity_checkpoint_list_row, viewGroup, false);
        TextView tourName = (TextView)view.findViewById(R.id.tourNameTextView);
        TextView planDateTV=(TextView)view.findViewById(R.id.plandateTextView);
        TextView expDateTV=(TextView)view.findViewById(R.id.expirydateTextView);
        ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        TextView childCountTextview=(TextView)view.findViewById(R.id.childCountTextview);

        JobNeed jobNeed=data.get(i);
        if(jobNeed!=null) {
            tourName.setText(jobNeed.getJobdesc());
            String jStatus=typeAssistDAO.getEventTypeCode(jobNeed.getJobstatus());
            if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
                tourName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bulletcompleted, 0, 0, 0);
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
                tourName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bulletclosed,0,0,0);

            if (jobType.equalsIgnoreCase(Constants.JOB_TYPE_SCHEDULED)) {
                int maxTour=jobNeedDAO.getChildCount(jobNeed.getJobneedid());
                int completedTour=jobNeedDAO.getCompletedChildCount(jobNeed.getJobneedid());
                expDateTV.setVisibility(View.VISIBLE);
                planDateTV.setText((jobNeed.getPlandatetime()));
                expDateTV.setText((jobNeed.getExpirydatetime()));
                progressBar.setMax(maxTour);
                progressBar.setProgress(completedTour);
                childCountTextview.setText(context.getResources().getString(R.string.progressbar_values, completedTour, maxTour));
            } else if (jobType.equalsIgnoreCase(Constants.JOB_TYPE_ADHOC)) {
                planDateTV.setText((jobNeed.getCdtz()));
                expDateTV.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                childCountTextview.setVisibility(View.GONE);
            }
        }

        return view;
    }
}
