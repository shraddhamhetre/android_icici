package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;

import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link HelpDeskDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class HelpDeskListActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<JobNeed> jobNeedArrayList;
    private ArrayList<JobNeed> searchJobNeedArrayList;
    public static final Map<String, JobNeed> ITEM_MAP =new HashMap<String, JobNeed>();
    public static final ArrayList<JobNeed> ITEMS =new ArrayList<JobNeed>();
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;
    private View recyclerView;

    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private FloatingActionButton fabMain,fabAdhocTaskCreate,fabAdhocTaskList;

    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private boolean isDateFilter=false;
    private FloatingActionButton fab;
    private ConnectivityReceiver connectivityReceiver;

    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpdesk_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.module_ticket));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        jobNeedDAO=new JobNeedDAO(HelpDeskListActivity.this);
        typeAssistDAO=new TypeAssistDAO(HelpDeskListActivity.this);


        jobNeedArrayList=new ArrayList<>();

        jobNeedArrayList=jobNeedDAO.getTicketList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);



        for (int j = 0; j < jobNeedArrayList.size(); j++) {
            ITEMS.add(jobNeedArrayList.get(j));
            ITEM_MAP.put(jobNeedArrayList.get(j).getJobneedid() + "", jobNeedArrayList.get(j));
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonFunctions.isPermissionGranted(HelpDeskListActivity.this)) {
                    Intent adhocTicketActivity = new Intent(HelpDeskListActivity.this, AdhocTicketActivity.class);
                    startActivityForResult(adhocTicketActivity, 0);
                }
                else
                    Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

            }
        });

        if(jobNeedArrayList.size()==0)
        {
            Snackbar.make(fab,getResources().getString(R.string.data_not_found), Snackbar.LENGTH_LONG).show();
        }

        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            recyclerView = findViewById(R.id.item_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.item_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }
        }

        /*fabMain = (FloatingActionButton)findViewById(R.id.fabMain);
        fabAdhocTaskCreate = (FloatingActionButton)findViewById(R.id.fabAdhocTaskCreate);
        fabAdhocTaskList = (FloatingActionButton)findViewById(R.id.fabAdhocTaskList);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabMain.setOnClickListener(this);
        fabAdhocTaskCreate.setOnClickListener(this);
        fabAdhocTaskList.setOnClickListener(this);*/
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        simpleItemRecyclerViewAdapter=new SimpleItemRecyclerViewAdapter(jobNeedArrayList);
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
        recyclerView.invalidate();
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public void animateFAB(){

        if(isFabOpen){

            fabMain.startAnimation(rotate_backward);
            fabAdhocTaskCreate.startAnimation(fab_close);
            fabAdhocTaskList.startAnimation(fab_close);
            fabAdhocTaskCreate.setClickable(false);
            fabAdhocTaskList.setClickable(false);
            isFabOpen = false;

        } else {

            fabMain.startAnimation(rotate_forward);
            fabAdhocTaskCreate.startAnimation(fab_open);
            fabAdhocTaskList.startAnimation(fab_open);
            fabAdhocTaskCreate.setClickable(true);
            fabAdhocTaskList.setClickable(true);
            isFabOpen = true;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ticket_orderby_menu, menu);

        MenuItem search = menu.findItem(R.id.ticketSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>0) {
                    if (simpleItemRecyclerViewAdapter!=null && simpleItemRecyclerViewAdapter.getItemCount() > 0)
                            simpleItemRecyclerViewAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isDateFilter)
        {
            MenuItem dItem=menu.findItem(R.id.action_datewise);
            dItem.setVisible(true);
            MenuItem pItem=menu.findItem(R.id.action_prioritywise);
            pItem.setVisible(false);
        }
        else
        {
            MenuItem dItem=menu.findItem(R.id.action_datewise);
            dItem.setVisible(false);
            MenuItem pItem=menu.findItem(R.id.action_prioritywise);
            pItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_datewise:
                isDateFilter=false;
                jobNeedArrayList=new ArrayList<>();
                jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
                refreshView();
                invalidateOptionsMenu();
                return true;
            case R.id.action_prioritywise:
                isDateFilter=true;
                jobNeedArrayList=new ArrayList<>();
                jobNeedArrayList=jobNeedDAO.getJobListDesc(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
                refreshView();
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshView()
    {
        for (int j = 0; j < jobNeedArrayList.size(); j++) {
            ITEMS.add(jobNeedArrayList.get(j));
            ITEM_MAP.put(jobNeedArrayList.get(j).getJobneedid() + "", jobNeedArrayList.get(j));
        }
        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0) {
            recyclerView = findViewById(R.id.item_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);

            if (findViewById(R.id.item_detail_container) != null) {
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
        jobNeedArrayList=jobNeedDAO.getJobList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
        refreshView();
    }

    @Override
    public void onClick(View v) {
        /*switch(v.getId())
        {
            case R.id.fabMain:
                animateFAB();
                break;
            case R.id.fabAdhocTaskCreate:
                Intent adhocJobActivity=new Intent(HelpDeskListActivity.this,AdhocJobActivity.class);
                startActivityForResult(adhocJobActivity,0);
                break;
            case R.id.fabAdhocTaskList:
                Intent adhocJobListActivity=new Intent(HelpDeskListActivity.this, AdhocJobListActivityListActivity.class);
                startActivityForResult(adhocJobListActivity,1);
                break;
        }*/
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(HelpDeskListActivity.this, isConnected,fab);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable{

        private ArrayList<JobNeed> mValues;
        private ArrayList<JobNeed> mArrayList;
        private Drawable bulletLow;
        private Drawable bulletMedium;
        private Drawable bulletHigh;

        public SimpleItemRecyclerViewAdapter(ArrayList<JobNeed> items) {
            mValues = items;
            mArrayList=items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.helpdesk_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            //holder.mIdView.setText(mValues.get(position).getJobneedid()+"");
            holder.mIdView.setText(getResources().getString(R.string.ticket_list_row_ticketno,mValues.get(position).getTicketno()));
            String jPriority=typeAssistDAO.getEventTypeCode(mValues.get(position).getPriority());
            String jStatus=typeAssistDAO.getEventTypeCode(mValues.get(position).getJobstatus());
            System.out.println("Ticket Status: "+jStatus);
            System.out.println("Ticket Priority: "+jPriority);
            System.out.println("Ticket id: "+mValues.get(position).getTicketno());
            holder.mStatusView.setText(jStatus);

            holder.mColorCodeBulletView.setImageDrawable(bulletHigh);

            if(jPriority!=null) {
                if (jPriority.equalsIgnoreCase("HIGH")) {
                    holder.mColorCodeBulletView.setImageDrawable(bulletHigh);
                } else if (jPriority.equalsIgnoreCase("MEDIUM")) {
                    holder.mColorCodeBulletView.setImageDrawable(bulletMedium);
                } else if (jPriority.equalsIgnoreCase("LOW")) {
                    holder.mColorCodeBulletView.setImageDrawable(bulletLow);
                }
            }


            //String text = "<font color=#18B064>"+ CommonFunctions.getFormatedDate(mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";

            System.out.println("Plan date: "+mValues.get(position).getPlandatetime());

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
            if(jStatus!=null) {
                if (jStatus.equalsIgnoreCase(Constants.TICKET_STATUS_RESOLVED) || jStatus.equalsIgnoreCase(Constants.TICKET_STATUS_CANCELLED)) {
                    holder.listRowLinearLayout.setBackgroundColor(getResources().getColor(R.color.listviewDivider));
                } else {
                    holder.listRowLinearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                }
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CommonFunctions.isPermissionGranted(HelpDeskListActivity.this)) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(HelpDeskDetailFragment.ARG_ITEM_ID, holder.mItem.getJobneedid() + "");
                            HelpDeskDetailFragment fragment = new HelpDeskDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, HelpDeskDetailActivity.class);
                            intent.putExtra(HelpDeskDetailFragment.ARG_ITEM_ID, holder.mItem.getJobneedid() + "");
                            context.startActivity(intent);
                        }
                    }
                    else
                        Toast.makeText(HelpDeskListActivity.this,getResources().getString(R.string.error_msg_grant_permission),Toast.LENGTH_LONG).show();
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mpDateView;
            public final ImageView mColorCodeBulletView;
            public final TextView mStatusView;
            public final LinearLayout listRowLinearLayout;
            public JobNeed mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mpDateView = (TextView) view.findViewById(R.id.jnPDateDesc);
                mColorCodeBulletView=(ImageView)view.findViewById(R.id.colorCodeBullet);
                mStatusView=(TextView)view.findViewById(R.id.status);
                listRowLinearLayout=(LinearLayout)view.findViewById(R.id.listRowLinearLayout);

                bulletLow=getResources().getDrawable(R.drawable.bulletassigned);
                bulletMedium=getResources().getDrawable(R.drawable.bulletinprogress);
                bulletHigh=getResources().getDrawable(R.drawable.bulletclosed);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
