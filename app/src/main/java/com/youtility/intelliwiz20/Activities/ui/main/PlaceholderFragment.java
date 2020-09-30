package com.youtility.intelliwiz20.Activities.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;

import com.youtility.intelliwiz20.Activities.PPMPlannerView;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.RecyclerViewClickListener;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements RecyclerViewClickListener,SearchView.OnQueryTextListener  {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    RecyclerView list;
    private JobNeedDAO jobNeedDAO;
    private ArrayList<JobNeed> jobNeedArrayList;
    private PPMAdapter ppmAdapter;



    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.search);
        SearchView sv = new SearchView(((PPMPlannerView) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(this);
        sv.setIconifiedByDefault(false);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ppmplaceholder_fragment_main, container, false);
        list = (RecyclerView) root.findViewById(R.id.ppmRecyclerView);
        jobNeedDAO=new JobNeedDAO(getActivity());
        prepareStatuswiseJobneed(0);

        return root;
    }


    private void prepareStatuswiseJobneed(int val)
    {
        if(val==0)
        {
            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(Constants.JOB_NEED_IDENTIFIER_PPM,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_ASSIGNED);
            if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                System.out.println("jobNeedArrayList.size(): "+jobNeedArrayList.size());
            ppmAdapter=new PPMAdapter(getActivity(), jobNeedArrayList,this);
            list.setAdapter(ppmAdapter);
        }else if(val==1)
        {
            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getStatusBaseJobList(Constants.JOB_NEED_IDENTIFIER_PPM,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED,Constants.JOBNEED_STATUS_COMPLETED);
            if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                System.out.println("jobNeedArrayList.size(): "+jobNeedArrayList.size());
            ppmAdapter=new PPMAdapter(getActivity(), jobNeedArrayList,this);
            list.setAdapter(ppmAdapter);
        }else if(val==2)
        {
            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_PPM,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_SCHEDULED);
            ppmAdapter=new PPMAdapter(getActivity(), jobNeedArrayList,this);
            list.setAdapter(ppmAdapter);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static class PPMAdapter extends RecyclerView.Adapter<PPMAdapter.ViewHolder> implements Filterable
    {
        private ArrayList<JobNeed> mValues;
        private ArrayList<JobNeed> mArrayList;
        private Context mContext;
        private TypeAssistDAO taDAO;
        private Drawable bulletLow;
        private Drawable bulletMedium;
        private Drawable bulletHigh;
        private RecyclerViewClickListener mListener;

        public PPMAdapter(Context mContext, ArrayList<JobNeed> mValues,RecyclerViewClickListener mListener)
        {
            this.mContext=mContext;
            this.mValues=mValues;
            this.mArrayList=mValues;
            this.mListener=mListener;
            taDAO=new TypeAssistDAO(mContext);
            bulletLow=mContext.getResources().getDrawable(R.drawable.bulletassigned);
            bulletMedium=mContext.getResources().getDrawable(R.drawable.bulletinprogress);
            bulletHigh=mContext.getResources().getDrawable(R.drawable.bulletclosed);
        }
        @Override
        public PPMAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.helpdesk_item_list_content, parent, false);
            return new PPMAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PPMAdapter.ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).getJobneedid()+"");
            holder.mIdView.setText(mContext.getResources().getString(R.string.ticket_list_row_ticketno,mValues.get(position).getTicketno()));
            String jPriority=taDAO.getEventTypeCode(mValues.get(position).getPriority());
            String jStatus=taDAO.getEventTypeCode(mValues.get(position).getJobstatus());
            System.out.println("Ticket Status: "+jStatus);
            System.out.println("Ticket Priority: "+jPriority);
            System.out.println("Ticket id: "+mValues.get(position).getTicketno());
            holder.mStatusView.setText(jStatus);

            holder.mColorCodeBulletView.setImageDrawable(null);

            if(jPriority!=null) {
                if (jPriority.equalsIgnoreCase("HIGH")) {
                    holder.mColorCodeBulletView.setImageDrawable(bulletHigh);
                } else if (jPriority.equalsIgnoreCase("MEDIUM")) {
                    holder.mColorCodeBulletView.setImageDrawable(bulletMedium);
                } else if (jPriority.equalsIgnoreCase("LOW")) {
                    holder.mColorCodeBulletView.setImageDrawable(bulletLow);
                }
            }

            System.out.println("Plan date: "+mValues.get(position).getPlandatetime());

            String text =null;
            text = "<font color=#18B064>"+ (mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
            holder.mpDateView.setText(Html.fromHtml(text));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(v,position,holder.mItem.getJobneedid(),0,holder.mItem.getTicketno()+"");
                }
            });

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
                    //System.out.println("charString.length(): "+charString.length());

                    if (charString.isEmpty()) {

                        mValues = mArrayList;
                    }
                    else
                    {
                        ArrayList<JobNeed> filteredList = new ArrayList<>();
                        for (JobNeed jobNeed : mArrayList) {

                            if (jobNeed.getJobdesc().toLowerCase().contains(charString) || String.valueOf(jobNeed.getTicketno()).contains(charString)) {

                                filteredList.add(jobNeed);
                            }
                        }

                        mValues = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mValues;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mValues = (ArrayList<JobNeed>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mIdView;
            private final TextView mContentView;
            private final TextView mpDateView;
            private final ImageView mColorCodeBulletView;
            private final TextView mStatusView;
            private final LinearLayout listRowLinearLayout;
            private JobNeed mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mpDateView = (TextView) view.findViewById(R.id.jnPDateDesc);
                mColorCodeBulletView=(ImageView)view.findViewById(R.id.colorCodeBullet);
                mStatusView=(TextView)view.findViewById(R.id.status);
                listRowLinearLayout=(LinearLayout)view.findViewById(R.id.listRowLinearLayout);

            }
        }
    }


    @Override
    public void onClick(View view, int position, long jobneedid, int isExpiredValue, String jobStatus) {

    }
}