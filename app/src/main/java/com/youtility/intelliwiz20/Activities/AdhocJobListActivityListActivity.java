package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;

import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AdhocJobListActivityDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AdhocJobListActivityListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private AssetDAO assetDAO;

    private ArrayList<JobNeed> jobNeedArrayList;
    public static final Map<String, JobNeed> ITEM_MAP =new HashMap<String, JobNeed>();
    public static final ArrayList<JobNeed> ITEMS =new ArrayList<JobNeed>();
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;
    private View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhocjoblistactivity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //toolbar.setTitle(getTitle());

        jobNeedDAO=new JobNeedDAO(AdhocJobListActivityListActivity.this);
        typeAssistDAO=new TypeAssistDAO(AdhocJobListActivityListActivity.this);
        assetDAO=new AssetDAO(AdhocJobListActivityListActivity.this);

        jobNeedArrayList=new ArrayList<>();

        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        for (int j = 0; j < jobNeedArrayList.size(); j++) {
            ITEMS.add(jobNeedArrayList.get(j));
            ITEM_MAP.put(jobNeedArrayList.get(j).getJobneedid() + "", jobNeedArrayList.get(j));
        }

        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            recyclerView = findViewById(R.id.adhocjoblistactivity_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.adhocjoblistactivity_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }
        }


    }

    private void refreshView()
    {
        for (int j = 0; j < jobNeedArrayList.size(); j++) {
            ITEMS.add(jobNeedArrayList.get(j));
            ITEM_MAP.put(jobNeedArrayList.get(j).getJobneedid() + "", jobNeedArrayList.get(j));
        }
        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            recyclerView = findViewById(R.id.adhocjoblistactivity_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.adhocjoblistactivity_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        jobNeedArrayList=new ArrayList<>();
        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TASK,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
        refreshView();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
        simpleItemRecyclerViewAdapter=new SimpleItemRecyclerViewAdapter(jobNeedArrayList);
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
        recyclerView.invalidate();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<JobNeed> mValues;
        private Drawable bulletAssigned;
        private Drawable bulletInprogress;
        private Drawable bulletCompleted;
        private Drawable bulletArchived;
        private Drawable bulletClosed;

        public SimpleItemRecyclerViewAdapter(ArrayList<JobNeed> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adhocjoblistactivity_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getJobneedid()+"");
            holder.mContentView.setText(assetDAO.getAssetName(mValues.get(position).getAssetid())+"");
            String jStatus=typeAssistDAO.getEventTypeCode(mValues.get(position).getJobstatus());

            if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_ASSIGNED))
            {
                holder.mColorCodeBulletView.setImageDrawable(bulletAssigned);
            }
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_INPROGRESS))
            {
                holder.mColorCodeBulletView.setImageDrawable(bulletInprogress);
            }
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_COMPLETED))
            {
                holder.mColorCodeBulletView.setImageDrawable(bulletCompleted);
            }
            else if(jStatus.equalsIgnoreCase(Constants.JOBNEED_STATUS_AUTOCLOSED))
            {
                holder.mColorCodeBulletView.setImageDrawable(bulletClosed);
            }

            //System.out.println("Plan date: "+mValues.get(position).getPlandatetime());


            String text =null;
            text = "<font color=#18B064>"+ (mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
            /*if(CommonFunctions.isLong(mValues.get(position).getPlandatetime()))
            {
                System.out.println("Format date: "+CommonFunctions.getFormatedDate(mValues.get(position).getPlandatetime()));
                text = "<font color=#18B064>"+ CommonFunctions.getFormatedDate(mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
            }
            else
            {
                text = "<font color=#18B064>"+ (mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
            }*/


            holder.mpDateView.setText(Html.fromHtml(text));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(AdhocJobListActivityDetailFragment.ARG_ITEM_ID, holder.mItem.getJobneedid()+"");
                        AdhocJobListActivityDetailFragment fragment = new AdhocJobListActivityDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.adhocjoblistactivity_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, AdhocJobListActivityDetailActivity.class);
                        intent.putExtra(AdhocJobListActivityDetailFragment.ARG_ITEM_ID, holder.mItem.getJobneedid()+"");

                        context.startActivity(intent);
                    }
                }
            });
        }



        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mpDateView;
            public final ImageView mColorCodeBulletView;
            public JobNeed mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mpDateView = (TextView) view.findViewById(R.id.jnPDateDesc);
                mColorCodeBulletView=(ImageView)view.findViewById(R.id.colorCodeBullet);

                bulletAssigned=getResources().getDrawable(R.drawable.bulletassigned);
                bulletInprogress=getResources().getDrawable(R.drawable.bulletinprogress);
                bulletCompleted=getResources().getDrawable(R.drawable.bulletcompleted);
                bulletArchived=getResources().getDrawable(R.drawable.bulletarchived);
                bulletClosed=getResources().getDrawable(R.drawable.bulletclosed);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
