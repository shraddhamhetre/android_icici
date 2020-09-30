package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CheckNetwork;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.ServerRequest;


import org.apache.http.HttpResponse;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An activity representing a list of Assets. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AssetDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AssetListActivity extends AppCompatActivity implements IDialogEventListeners, ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static Map<String, Asset> ITEM_MAP=null;
    public static ArrayList<Asset> ITEMS=null ;
    private AssetDAO assetDAO;
    private TypeAssistDAO typeAssistDAO;
    private JobNeedDAO jobNeedDAO;
    private ArrayList<Asset>assetArrayList;
    private Intent startIntent;
    private CheckNetwork checkNetwork;
    private SharedPreferences loginPref;
    private SharedPreferences deviceRelatedPref;
    private FloatingActionButton fab;
    private QuestionDAO questionDAO;
    private ArrayList<QuestionSet> questSetArraylist=null;
    private boolean qSetAvailable=false;
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;
    private CustomAlertDialog customAlertDialog;
    private ConnectivityReceiver connectivityReceiver;
    private SharedPreferences adhocJobPef;
    private SharedPreferences deviceInfoPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);

        startIntent=getIntent();
        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        adhocJobPef=getSharedPreferences(Constants.ADHOC_JOB_TIMESTAMP_PREF,MODE_PRIVATE);

        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        customAlertDialog=new CustomAlertDialog(AssetListActivity.this, this);

        checkNetwork=new CheckNetwork(AssetListActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        assetDAO=new AssetDAO(AssetListActivity.this);
        typeAssistDAO=new TypeAssistDAO(AssetListActivity.this);
        questionDAO=new QuestionDAO(AssetListActivity.this);
        jobNeedDAO=new JobNeedDAO(AssetListActivity.this);

        System.out.println(assetDAO.getAssetCount());

        assetArrayList=new ArrayList<>();
        assetArrayList=assetDAO.getAssetList();

        ITEMS=new ArrayList<Asset>();
        ITEM_MAP =new HashMap<String, Asset>();

        for(int i=0;i<assetArrayList.size();i++)
        {
            ITEMS.add(assetArrayList.get(i));
            ITEM_MAP.put(assetArrayList.get(i).getAssetid() + "", assetArrayList.get(i));
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        if(assetArrayList.size()==0)
        {
            Snackbar.make(fab, getResources().getString(R.string.data_not_found), Snackbar.LENGTH_LONG).show();
        }

        View recyclerView = findViewById(R.id.asset_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.asset_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
        simpleItemRecyclerViewAdapter=new SimpleItemRecyclerViewAdapter(ITEMS);
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
        recyclerView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.asset_menu, menu);

        MenuItem search = menu.findItem(R.id.assetSearch);
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

                simpleItemRecyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String assetCount=null;
        switch (item.getItemId())
        {
            case R.id.action_info:
                assetCount=assetDAO.getAssetCount();
                String[] jobCount = assetCount.split("~");//schedule, complete, pending, closed
                customAlertDialog.assetInfoDialog("Asset Summary",jobCount[0],jobCount[1],jobCount[2],jobCount[3]);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(AssetListActivity.this, isConnected,fab);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
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

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable{

        private ArrayList<Asset> mValues;
        private ArrayList<Asset> mArrayList;
        private Resources res;
        private Drawable workingBullet;
        private Drawable standbyBullet;
        private Drawable mainteninceBullet;

        public SimpleItemRecyclerViewAdapter(ArrayList<Asset> items) {
            mValues = items;
            mArrayList=items;
            res=getResources();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.asset_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            workingBullet=res.getDrawable(R.drawable.bulletcompleted);
            mainteninceBullet=res.getDrawable(R.drawable.bulletclosed);
            standbyBullet=res.getDrawable(R.drawable.bulletinprogress);

            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getAssetid()+"");
            holder.mContentView.setText(mValues.get(position).getAssetname());
            if(!mValues.get(position).getLocname().equalsIgnoreCase("None")) {
                String text = "<font color=#18B064>" + (getResources().getString(R.string.asset_location_view, mValues.get(position).getLocname())) + "</font>";
                holder.mContentLocationView.setText(Html.fromHtml(text));
            }

            String eventTypeName=typeAssistDAO.getEventTypeName(mValues.get(position).getRunningstatus());
            System.out.println("Running status: "+mValues.get(position).getRunningstatus());
            System.out.println("eventTypeName: "+eventTypeName);

            if(eventTypeName!=null) {
                if (eventTypeName.equalsIgnoreCase("Working")) {
                    //holder.mContentView.setCompoundDrawablesRelativeWithIntrinsicBounds(workingBullet, null, null, null);
                    holder.mstatusView.setImageDrawable(workingBullet);
                }
                else if (eventTypeName.equalsIgnoreCase("Standby")) {
                    //holder.mContentView.setCompoundDrawablesRelativeWithIntrinsicBounds(standbyBullet, null, null, null);
                    holder.mstatusView.setImageDrawable(standbyBullet);
                }
                else if (eventTypeName.equalsIgnoreCase("MAINTENANCE")) {
                    //holder.mContentView.setCompoundDrawablesRelativeWithIntrinsicBounds(mainteninceBullet, null, null, null);
                    holder.mstatusView.setImageDrawable(mainteninceBullet);
                }
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int accessValue = CommonFunctions.isAllowToAccessModules(AssetListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                    double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                    double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
                    System.out.println("===========" + accessValue);
                    System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                    if (accessValue == 0) {
                        if (CommonFunctions.isPermissionGranted(AssetListActivity.this)) {
                            if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putString(AssetDetailFragment.ARG_ITEM_ID, holder.mItem.getAssetid() + "");
                                AssetDetailFragment fragment = new AssetDetailFragment();
                                fragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.asset_detail_container, fragment)
                                        .commit();
                            } else {
                                Context context = v.getContext();
                                Intent intent = new Intent(context, AssetDetailActivity.class);
                                intent.putExtra(AssetDetailFragment.ARG_ITEM_ID, holder.mItem.getAssetid() + "");

                                context.startActivity(intent);
                            }
                        } else
                            Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    }else if (accessValue == 1) {
                        customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
                    } else if (accessValue == 2) {
                        customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
                        System.out.println("==========="+accessValue);
                    } else if (accessValue == 3) {
                        customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
                        System.out.println("==========="+accessValue);
                    } else if (accessValue == 4) {
                        customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
                        System.out.println("==========="+accessValue);
                    }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                        customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                        System.out.println("===========lat long==0.0");
                    }
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int accessValue = CommonFunctions.isAllowToAccessModules(AssetListActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
                    double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
                    double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
                    System.out.println("===========" + accessValue);
                    System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

                        if (CommonFunctions.isPermissionGranted(AssetListActivity.this)) {
                            if (accessValue == 0) {
                            //showAssetStatusAlert(holder.mItem.getAssetcode(), holder.mItem.getAssetid(),getRunningStatus(assetDAO.getAssetRunningStatus(holder.mItem.getAssetcode())));
                            System.out.println("Running status: " + holder.mItem.getRunningstatus());
                            showAssetStatusAlert(holder.mItem.getAssetcode(), holder.mItem.getAssetid(), getRunningStatus(typeAssistDAO.getEventTypeCode(holder.mItem.getRunningstatus())), holder.mItem.getRunningstatus());
                            } else if (accessValue == 1) {
                                customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
                            } else if (accessValue == 2) {
                                customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
                                System.out.println("==========="+accessValue);
                            } else if (accessValue == 3) {
                                customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
                                System.out.println("==========="+accessValue);
                            } else if (accessValue == 4) {
                                customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
                                System.out.println("==========="+accessValue);
                            }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
                                customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
                                System.out.println("===========lat long==0.0");
                            }
                        } else
                            Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

                        return false;
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
                        ArrayList<Asset> filteredList = new ArrayList<>();
                        for (Asset asset : mArrayList) {

                            if (asset.getAssetcode().toLowerCase().contains(charString) || asset.getAssetname().toLowerCase().contains(charString)) {

                                filteredList.add(asset);
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
                    mValues = (ArrayList<Asset>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mContentLocationView;
            public final ImageView mstatusView;
            public Asset mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mContentLocationView=(TextView)view.findViewById(R.id.contentLocation);
                mstatusView=(ImageView)view.findViewById(R.id.contentStatus);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private int getRunningStatus(String rStatus)
    {
        int runningStatus=-1;
        if(rStatus!=null) {
            switch (rStatus) {
                case "WORKING":
                    runningStatus = 0;
                    break;
                case "STANDBY":
                    runningStatus = 1;
                    break;
                case "MAINTENANCE":
                    runningStatus = 2;
                    break;
            }
        }
        return  runningStatus;
    }

    private void showAssetStatusAlert(final String assetCode, final long assetID, final int runningStatus, final long assetRunningStatus)
    {
        String updatedStatus="";
        final CharSequence[] alertType={
                Html.fromHtml("<font color='#0c6600'>"+getResources().getString(R.string.assetlistactivity_status_color_working)+"</font>"),
                Html.fromHtml("<font color='#d15000'>"+getResources().getString(R.string.assetlistactivity_status_color_standby)+"</font>"),
                Html.fromHtml("<font color='#cc0018'>"+getResources().getString(R.string.assetlistactivity_status_color_maintenance)+"</font>"),
        };
        AlertDialog.Builder alert = new AlertDialog.Builder(AssetListActivity.this);
        alert.setTitle(getResources().getString(R.string.assetlistactivity_status_changed));

        alert.setSingleChoiceItems(alertType,runningStatus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(runningStatus!=which)
                {
                    dialog.dismiss();
                    if(checkNetwork.isNetworkConnectionAvailable()) {   
                        System.out.println("Asset Prev Status: "+assetRunningStatus);
                        System.out.println("Asset curr Status: "+alertType[which].toString().trim());
                        String prevRunningStatus=typeAssistDAO.getEventTypeCode(assetRunningStatus);
                        String currRunningStatus=alertType[which].toString().trim();
                        if(prevRunningStatus.equalsIgnoreCase("MAINTENANCE") && currRunningStatus.equalsIgnoreCase("WORKING") ||
                                prevRunningStatus.equalsIgnoreCase("MAINTENANCE") && currRunningStatus.equalsIgnoreCase("STANDBY"))
                        {
                            qSetAvailable=true;
                            System.out.println("We can pass to report selection");
                            showDialog(assetCode);
                            callAssetStatusAsynTask(assetCode,assetID,alertType[which].toString().trim());
                        }
                        /*else
                        {
                            callAssetStatusAsynTask(assetCode,assetID,"WORKING");
                        }*/

                        /*assetDAO.changeRunningStatus(assetCode,assetID, typeAssistDAO.getEventTypeID(alertType[which].toString().trim(), Constants.IDENTIFIER_RUNNINGSTATUS));
                        addJobNeedEntry(assetCode, typeAssistDAO.getEventTypeID(alertType[which].toString().trim(), Constants.IDENTIFIER_RUNNINGSTATUS) , assetID);
                        */
                        callAssetStatusAsynTask(assetCode,assetID,alertType[which].toString().trim());

                    }
                    else
                    {
                        Snackbar.make(fab, getResources().getString(R.string.offlinemode_alert_message),Snackbar.LENGTH_LONG).show();
                    }

                }
                else
                    Toast.makeText(AssetListActivity.this, getResources().getString(R.string.assetlistactivity_messge1)+" "+alertType[which], Toast.LENGTH_LONG).show();
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.button_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        alert.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void callAssetStatusAsynTask(String assetCode, long assetID, String runnStatus)
    {
        AssetRunningStatusUpdateAsyntask assetRunningStatusUpdateAsyntask = new AssetRunningStatusUpdateAsyntask(assetCode, assetID ,runnStatus);
        assetRunningStatusUpdateAsyntask.execute();
    }

    private void addJobNeedEntry(String assetdesc, long assetRStatus, long assetId )
    {
        JobNeed jobNeed=new JobNeed();
        jobNeed.setJobneedid(System.currentTimeMillis());
        jobNeed.setJobdesc(assetdesc+"_"+Constants.JOB_NEED_IDENTIFIER_ASSET_LOG);
        jobNeed.setFrequency(-1);
        jobNeed.setPlandatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setExpirydatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setGracetime(0);
        jobNeed.setJobtype(typeAssistDAO.getEventTypeID(Constants.JOB_TYPE_ADHOC, Constants.IDENTIFIER_JOBTYPE));//need to get id form type assist for Constants.JOB_TYPE_ADHOC
        jobNeed.setJobstatus(assetRStatus);//need to get id form type assist for Assigned status
        jobNeed.setScantype(-1);
        jobNeed.setReceivedonserver("");
        jobNeed.setPriority(-1);
        jobNeed.setStarttime("");
        jobNeed.setEndtime("");
        jobNeed.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        jobNeed.setRemarks("");
        jobNeed.setCuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        jobNeed.setMuser(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        //jobNeed.setIsdeleted("false");
        jobNeed.setAssetid(assetId);//aadd asset id scan
        jobNeed.setGroupid(-1);
        jobNeed.setPeopleid(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setAatop(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setJobid(-1);
        jobNeed.setPerformedby(loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        jobNeed.setQuestionsetid(-1);
        jobNeed.setIdentifier(typeAssistDAO.getEventTypeIDForAdhoc(Constants.JOB_NEED_IDENTIFIER_ASSET_LOG));//need to get id form type assist for Constants.JOB_NEED_IDENTIFIER_TOUR
        jobNeed.setParent(-1);
        jobNeed.setTicketcategory(-1);
        jobNeed.setBuid(loginPref.getLong(Constants.LOGIN_SITE_ID,-1));
        jobNeed.setCtzoffset(loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));
        jobNeedDAO.insertRecord(jobNeed,Constants.SYNC_STATUS_ZERO);
    }

    public void showDialog(final String assetCode) {

        if(assetCode!=null && assetCode.trim().length()>0) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(AssetListActivity.this);
            builderSingle.setTitle(getResources().getString(R.string.select_quest_set_title1));

            questSetArraylist = new ArrayList<QuestionSet>();
            //questSetArraylist = questionDAO.getQuestionSetCodeList(assetCode);

            Asset asset=assetDAO.getAssetAssignedReport(assetCode);
            String qSetNameRaw=asset.getQsetname();
            String qSetIdRaw=asset.getQsetids().trim();

            boolean isAvailable=qSetNameRaw.contains("~");
            if(isAvailable)
            {
                String rIds=qSetIdRaw.replace(" ",",");
                questSetArraylist=questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetmaintance_templates_query, rIds));
            }
            else
            {
                questSetArraylist=questionDAO.getQuestionSetCodeListFromTemplate(getResources().getString(R.string.get_assetmaintance_templates_query, qSetIdRaw));
            }

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AssetListActivity.this, android.R.layout.select_dialog_item);

            for (QuestionSet questionSet : questSetArraylist) {
                arrayAdapter.add(questionSet.getQsetname().toString().trim());
            }

            builderSingle.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //String strName = arrayAdapter.getItem(which);
                    showReport(questSetArraylist.get(which).getQuestionsetid(), assetCode);

                }
            });
            builderSingle.create();
            builderSingle.setCancelable(false);
            builderSingle.show();
        }

    }

    private void showReport(long qSetID, String assetCode)
    {
        adhocJobPef.edit().putLong(Constants.ADHOC_TIMESTAMP,System.currentTimeMillis()).apply();
        Intent ii = new Intent(AssetListActivity.this, IncidentReportQuestionActivity.class);
        ii.putExtra("FROM", "ASSET");
        ii.putExtra("ID", qSetID);//need to pass quest set id
        ii.putExtra("ASSETCODE",assetCode);
        ii.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_JOBNEED);
        ii.putExtra("FOLDER",Constants.JOB_NEED_IDENTIFIER_ASSET);
        startActivityForResult(ii, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==RESULT_OK)
        {
            setResult(RESULT_OK);
            finish();
        }
    }

    private class AssetRunningStatusUpdateAsyntask extends AsyncTask<Void, Integer, Void>
    {
        String assetCode; String runningStatus;
        long assetID=-1;
        ProgressDialog dialog;
        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        long eventTypeID=-1;
        private boolean isErrorOccur=false;
        public AssetRunningStatusUpdateAsyntask(String assetCode,long assetID, String runningStatus)
        {
            this.assetCode=assetCode;
            this.runningStatus=runningStatus;
            this.assetID=assetID;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventTypeID=typeAssistDAO.getEventTypeID(runningStatus, Constants.IDENTIFIER_RUNNINGSTATUS);
            dialog = new ProgressDialog(AssetListActivity.this);
            dialog.setMessage(getResources().getString(R.string.please_wait));
            dialog.show();
        }



        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String date = null;
                StringBuffer sb;
                URL url = new URL(Constants.BASE_URL); // here is your URL path

                //String updateQuery="UPDATE asset SET mdtz=now(), muser="+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+" runningstatus='"+runningStatus+"' WHERE  assetcode= '"+assetCode+"' returning assetcode";

                String updateQuery=getResources().getString(R.string.asset_update,loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1), eventTypeID, assetCode);
                System.out.println("Asset updateQuery: "+updateQuery);

                //---------------------------------------------------------------------

                ServerRequest serverRequest=new ServerRequest(AssetListActivity.this);
                HttpResponse response=serverRequest.assetRunningStatusUpdateResponse(updateQuery.trim(),
                        loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                if(response!=null && response.getStatusLine().getStatusCode()==200)
                {
                    is = response.getEntity().getContent();

                    sb = new StringBuffer("");
                    buffer = new byte[1024];
                    byteread = 0;
                    try {
                        while ((byteread = is.read(buffer)) != -1) {
                            sb.append(new String(buffer));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    is.close();
                    System.out.println("SB Asset updateQuery: " + sb.toString());
                    response.getEntity().consumeContent();

                    assetDAO.changeRunningStatus(assetCode,assetID, typeAssistDAO.getEventTypeID(runningStatus, Constants.IDENTIFIER_RUNNINGSTATUS));
                    addJobNeedEntry(assetCode, typeAssistDAO.getEventTypeID(runningStatus, Constants.IDENTIFIER_RUNNINGSTATUS) , assetID);

                }
                else {
                    isErrorOccur=true;
                    System.out.println("SB1 Asset updateQuery: Error");
                }

                //---------------------------------------------------------------------

                /*UploadParameters uploadParameters=new UploadParameters();
                uploadParameters.setServicename(Constants.SERVICE_INSERT);
                uploadParameters.setQuery(updateQuery);
                uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);

                Gson gson = new Gson();
                String upData = gson.toJson(uploadParameters);
                System.out.println("upData: "+upData);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 *//* milliseconds *//*);
                conn.setConnectTimeout(15000 *//* milliseconds *//*);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);//Don't use a cached Copy
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                OutputStream out = new BufferedOutputStream(conn.getOutputStream());


                is = data.getContent();
                buffer = new byte[1024];
                byteread = 0;
                while ((byteread = is.read(buffer)) != -1)
                {
                    out.write(buffer, 0, byteread);
                }
                out.flush();
                out.close();
                is.close();


                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    System.out.println("SB: " + sb.toString());

                } else {
                    System.out.println("SB: " + responseCode);
                }*/
            } catch (Exception e) {
                System.out.println("SB: " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            dialog.dismiss();
            if(isErrorOccur)
            {
                customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.offlinemode_alert_message));
            }
            else {
                if (!qSetAvailable) {
                    finish();
                    startActivity(startIntent);
                }
            }
        }
    }

    //----------------------------------------------- view model class --------------------------------------------------------


}
