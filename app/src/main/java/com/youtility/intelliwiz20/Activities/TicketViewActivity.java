package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.IGridviewItemClickListeners;
import com.youtility.intelliwiz20.Interfaces.RecyclerViewClickListener;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.ResponseData;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.RetrofitClient;
import com.youtility.intelliwiz20.Utils.RetrofitServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IGridviewItemClickListeners,
        IDialogEventListeners, View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static TicketFragment.TicketAdapter ticketAdapter;
    public static AutoclosedFragment.AutoTicketAdapter autoTicketAdapter;
    public static int viewPagerPosition=0;
    private CustomAlertDialog customAlertDialog;

    private SharedPreferences deviceInfoPref;
    private SharedPreferences loginPref;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_ticket_view));
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerPosition=position;
                System.out.println("onPageSelected: "+position);
                if(position==0)
                    toolbar.setTitle(getResources().getString(R.string.title_activity_ticket_view));
                else if(position==1)
                    toolbar.setTitle(getResources().getString(R.string.ticketview_autoclosetab));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CommonFunctions.isPermissionGranted(TicketViewActivity.this)) {
                    Intent adhocTicketActivity = new Intent(TicketViewActivity.this, AdhocTicketActivity.class);
                    startActivityForResult(adhocTicketActivity, 0);
                }
                else
                    Snackbar.make(view, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
            }
        });

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
                /*if(newText.length()>0) {
                    if (ticketAdapter!=null && ticketAdapter.getItemCount() > 0)
                        ticketAdapter.getFilter().filter(newText);
                }*/

                if(viewPagerPosition==0)
                {
                    if (ticketAdapter!=null)
                        ticketAdapter.getFilter().filter(newText);
                }
                else if(viewPagerPosition==1)
                {
                    if(autoTicketAdapter!=null)
                        autoTicketAdapter.getFilter().filter(newText);
                }

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onGridViewItemClick(int position, String appName, boolean isAccess, String appCode) {

    }

    @Override
    public void onLongGridViewItemClick(int position, String appName, boolean isAccess, String appCode) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TicketFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerViewClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private RecyclerView.LayoutManager mLayoutManager;
        private ArrayList<JobNeed> jobNeedArrayList;
        private JobNeedDAO jobNeedDAO;
        private TypeAssistDAO typeAssistDAO;
        private SharedPreferences loginDetailPref;
        private SharedPreferences deviceRelatedPref;
        private SharedPreferences syncOffsetPref;
        private SharedPreferences applicationPref;


        public TicketFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            loginDetailPref=getActivity().getSharedPreferences(Constants.LOGIN_PREFERENCE,MODE_PRIVATE);
            deviceRelatedPref=getActivity().getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
            syncOffsetPref=getActivity().getSharedPreferences(Constants.SYNC_OFFSET_PREF,MODE_PRIVATE);
            applicationPref=getActivity().getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TicketFragment newInstance(int position, String pHolderText) {
            TicketFragment fragment = new TicketFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ticket_view, container, false);
            mRecyclerView=(RecyclerView)rootView.findViewById(R.id.ticketRecyclerView);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);

            jobNeedDAO=new JobNeedDAO(getActivity());
            typeAssistDAO=new TypeAssistDAO(getActivity());

            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getTicketList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
            if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                System.out.println("jobNeedArrayList.size(): "+jobNeedArrayList.size());
            ticketAdapter=new TicketAdapter(getActivity(), jobNeedArrayList,this);
            mRecyclerView.setAdapter(ticketAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            return rootView;
        }

        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(true);
            syncOffsetPref.edit().putInt(Constants.SYNC_TICKET_OFFSET,(syncOffsetPref.getInt(Constants.SYNC_TICKET_OFFSET,0)* 0)).apply();
            // Fetching data from server
            prepareTicketPaginationRequest();
        }

        private void prepareTicketPaginationRequest()
        {

            //String queryInfo=CommonFunctions.getQuery(13, getActivity());
            String queryInfo=CommonFunctions.getQuery(13, getActivity());
            System.out.println("===query"+queryInfo);
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
            progressDialog.show(); // show progress dialog

            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_SELECT);
            uploadParameters.setQuery(queryInfo);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
            uploadParameters.setSitecode(applicationPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
            uploadParameters.setLoginid(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
            uploadParameters.setPassword(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            Gson gson=new Gson();

            System.out.println(gson.toJson(uploadParameters).toString());


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            RetrofitServices retrofitServices = retrofit.create(RetrofitServices.class);
            Call<ResponseData> call1 = retrofitServices.getMoreTickets(uploadParameters);
            call1.enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    System.out.println("Hello");
                    System.out.println("response body: "+response.body().getRc());
                    System.out.println("response message: "+response.message());
                    System.out.println("response code: "+response.code());
                    System.out.println("response isSuccessful: "+response.isSuccessful());
                    progressDialog.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                    Type listType;
                    if(response.code()==200)
                    {
                        System.out.println("response.body().toString(): "+response.body().toString());
                        try {
                            if(response.body().getRc()==0)
                            {
                                if(response.body().getNrow()>0) {
                                    String mainSplitRowChar = String.valueOf(response.body().getRow_data().charAt(0));
                                    String mainSplitColumnChar = String.valueOf(response.body().getColumns().charAt(0));

                                    if (mainSplitRowChar.trim().equalsIgnoreCase("|")) {
                                        mainSplitRowChar = "\\|";
                                    } else if (mainSplitRowChar.trim().equalsIgnoreCase("$")) {
                                        mainSplitRowChar = "\\$";
                                    }

                                    if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                                        mainSplitColumnChar = "\\|";
                                    } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                                        mainSplitColumnChar = "\\$";
                                    }

                                    String[] responseSplit = response.body().getRow_data().split(mainSplitRowChar);
                                    String[] cols = response.body().getColumns().split(mainSplitColumnChar);
                                    System.out.println("Length: " + responseSplit.length);

                                    JSONArray dataArray = new JSONArray();
                                    JSONObject dataObject = new JSONObject();


                                    for (int i = 1; i < (responseSplit.length); i++) {
                                        System.out.println("split string: " + responseSplit[i]);
                                        //System.out.println("split string number: "+i);
                                        if (responseSplit[i].trim().length() > 0) {
                                            Character startDelimitor = responseSplit[i].charAt(0);
                                            System.out.println("Start Delimeter: " + startDelimitor);
                                            String[] respRow = null;
                                            if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                                respRow = responseSplit[i].trim().split("\\$");
                                            } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                                respRow = responseSplit[i].trim().split("\\|");
                                            } else {
                                                respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                                            }


                                            if (respRow != null && respRow.length > 0) {
                                                JSONObject jsonObject = new JSONObject();
                                                for (int c = 1; c < respRow.length; c++) {
                                                    jsonObject.put(cols[c], respRow[c]);
                                                }
                                                dataArray.put(jsonObject);
                                            }


                                        }

                                    }

                                    dataObject.put("Data", dataArray);
                                    Gson gson = new Gson();
                                    JSONArray data1 = dataObject.getJSONArray("Data");
                                    listType = new TypeToken<ArrayList<JobNeed>>() {
                                    }.getType();
                                    jobNeedArrayList = new ArrayList<>();
                                    jobNeedArrayList = gson.fromJson(data1.toString(), listType);
                                    if (jobNeedArrayList != null && jobNeedArrayList.size() > 0)
                                        insertDataIntoDB();

                                    syncOffsetPref.edit().putLong(Constants.SYNC_TICKET_TIMESTAMP, System.currentTimeMillis()).apply();
                                }
                                else
                                    Snackbar.make(mRecyclerView,getResources().getString(R.string.ticketview_nomorerecord),Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    progressDialog.dismiss();
                    call.cancel();
                    Snackbar.make(mRecyclerView,t.getMessage(),Snackbar.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

            /*RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
            Call<ResponseData> call1=retrofitServices.getServerResponse(Constants.SERVICE_SELECT,uploadParameters);*/
            /*call1.enqueue(new retrofit2.Callback<ResponseData>() {
                @Override
                public void onResponse(@NonNull Call<ResponseData> call, @NonNull Response<ResponseData> response) {
                    progressDialog.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                    Type listType;
                    if(response.isSuccessful() && response.body()!=null)
                    {
                        try {
                            if(response.body().getNrow()>0)
                            {
                                String mainSplitRowChar = String.valueOf(response.body().getRow_data().charAt(0));
                                String mainSplitColumnChar=String.valueOf(response.body().getColumns().charAt(0));

                                if (mainSplitRowChar.trim().equalsIgnoreCase("|")) {
                                    mainSplitRowChar = "\\|";
                                } else if (mainSplitRowChar.trim().equalsIgnoreCase("$")) {
                                    mainSplitRowChar = "\\$";
                                }

                                if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                                    mainSplitColumnChar = "\\|";
                                } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                                    mainSplitColumnChar = "\\$";
                                }

                                String[] responseSplit = response.body().getRow_data().split(mainSplitRowChar);
                                String[] cols=response.body().getColumns().split(mainSplitColumnChar);
                                System.out.println("Length: " + responseSplit.length);

                                JSONArray dataArray = new JSONArray();
                                JSONObject dataObject = new JSONObject();


                                for (int i = 1; i < (responseSplit.length); i++) {
                                    System.out.println("split string: " + responseSplit[i]);
                                    //System.out.println("split string number: "+i);
                                    if (responseSplit[i].trim().length() > 0) {
                                        Character startDelimitor = responseSplit[i].charAt(0);
                                        System.out.println("Start Delimeter: " + startDelimitor);
                                        String[] respRow = null;
                                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                            respRow = responseSplit[i].trim().split("\\$");
                                        } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                            respRow = responseSplit[i].trim().split("\\|");
                                        } else {
                                            respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                                        }


                                        if(respRow!=null && respRow.length>0) {
                                            JSONObject jsonObject = new JSONObject();
                                            for (int c = 1; c < respRow.length; c++) {
                                                jsonObject.put(cols[c], respRow[c]);
                                            }
                                            dataArray.put(jsonObject);
                                        }


                                    }

                                }

                                dataObject.put("Data", dataArray);
                                Gson gson = new Gson();
                                JSONArray data1 = dataObject.getJSONArray("Data");
                                listType = new TypeToken<ArrayList<JobNeed>>() {
                                }.getType();
                                jobNeedArrayList=new ArrayList<>();
                                jobNeedArrayList = gson.fromJson(data1.toString(), listType);
                                if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                                    insertDataIntoDB();

                                syncOffsetPref.edit().putLong(Constants.SYNC_TICKET_TIMESTAMP,System.currentTimeMillis()).apply();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseData> call, Throwable t) {
                    progressDialog.dismiss();
                    call.cancel();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });*/

        }

        private void insertDataIntoDB()
        {
            DatabaseUtils.InsertHelper helper;
            SQLiteDatabase db = null;
            SqliteOpenHelper sqlopenHelper=SqliteOpenHelper.getInstance(getActivity());
            db=sqlopenHelper.getDatabase();

            helper = new DatabaseUtils.InsertHelper(db, JOBNeed_Table.TABLE_NAME);
            final int idColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ID);
            final int descColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_DESC);
            final int pdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME);
            final int expdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME);
            final int gracetimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME);
            final int recvonserverColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER);
            final int starttimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME);
            final int endtimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME);
            final int gpslocColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION);
            final int remarkColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK);
            final int cuserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER);
            final int muserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER);
            final int cdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ);
            final int mdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ);
            final int attachmentcountColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT);
            final int aatopColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP);
            final int assetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID);
            final int freqColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY);
            final int jobidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID);
            final int jobstatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS);
            final int jobtypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE);
            final int performbyColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY);
            final int priorityColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY);
            final int qsetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID);
            final int scantypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE);
            final int peopleColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID);
            final int peoplegroupColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID);
            final int jnidentifierColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER);
            final int jnparentidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT);
            final int syncStatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SYNC_STATUS);
            final int ticketnoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO);
            final int buidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_BUID);
            final int seqNoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO);
            final int ticketCategoryColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY);
            final int cdtzoffsetColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET);
            final int mfactorColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR);

            for (JobNeed jobNeed : jobNeedArrayList)
            {
                helper.prepareForReplace();
                helper.bind(idColumn, jobNeed.getJobneedid());
                helper.bind(descColumn, jobNeed.getJobdesc());
                helper.bind(jobidColumn, jobNeed.getJobid());
                helper.bind(freqColumn, jobNeed.getFrequency());
                helper.bind(pdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getPlandatetime()));
                helper.bind(expdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getExpirydatetime()));
                helper.bind(gracetimeColumn, jobNeed.getGracetime());
                helper.bind(assetcodeColumn, jobNeed.getAssetid());
                helper.bind(qsetcodeColumn, jobNeed.getQuestionsetid());
                helper.bind(peoplegroupColumn, jobNeed.getGroupid());
                helper.bind(aatopColumn, jobNeed.getAatop());
                helper.bind(jobstatusColumn, jobNeed.getJobstatus());
                helper.bind(jobtypeColumn, jobNeed.getJobtype());
                helper.bind(scantypeColumn, jobNeed.getScantype());
                helper.bind(recvonserverColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getReceivedonserver()));
                helper.bind(priorityColumn, jobNeed.getPriority());

                helper.bind(starttimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getStarttime()));

                helper.bind(endtimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getEndtime()));

                helper.bind(performbyColumn, jobNeed.getPerformedby());
                helper.bind(gpslocColumn, jobNeed.getGpslocation());
                helper.bind(remarkColumn, jobNeed.getRemarks());
                helper.bind(cuserColumn, jobNeed.getCuser());
                helper.bind(muserColumn, jobNeed.getMuser());
                helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getCdtz()));
                helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getMdtz()));
                helper.bind(attachmentcountColumn, jobNeed.getAttachmentcount());
                helper.bind(peopleColumn, jobNeed.getPeopleid());
                helper.bind(syncStatusColumn,"-1");
                helper.bind(jnidentifierColumn,jobNeed.getIdentifier());
                helper.bind(jnparentidColumn,jobNeed.getParent());
                helper.bind(ticketnoColumn, jobNeed.getTicketno());
                helper.bind(buidColumn, jobNeed.getBuid());
                helper.bind(seqNoColumn, jobNeed.getSeqno());
                helper.bind(ticketCategoryColumn, jobNeed.getTicketcategory());
                helper.bind(cdtzoffsetColumn, jobNeed.getCtzoffset());
                helper.bind(mfactorColumn, jobNeed.getMultiplicationfactor());
                helper.execute();
            }

            helper=null;
            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getTicketList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
            ticketAdapter=new TicketFragment.TicketAdapter(getActivity(), jobNeedArrayList,this );
            mRecyclerView.setAdapter(ticketAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }


        @Override
        public void onClick(View view, int position, long jobneedid, int isExpiredValue, String jobStatus) {
            //Toast.makeText(getActivity(),jobStatus,Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),TicketDetailsActivity.class);
            intent.putExtra("JOBNEEDID",jobneedid);
            startActivityForResult(intent,0);
        }

        public static class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> implements Filterable
        {
            private ArrayList<JobNeed> mValues;
            private ArrayList<JobNeed> mArrayList;
            private Context mContext;
            private TypeAssistDAO taDAO;
            private Drawable bulletLow;
            private Drawable bulletMedium;
            private Drawable bulletHigh;
            private RecyclerViewClickListener mListener;

            public TicketAdapter(Context mContext, ArrayList<JobNeed> mValues,RecyclerViewClickListener mListener)
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
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.helpdesk_item_list_content, parent, false);
                return new ViewHolder(view);
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position) {
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

                /*if(jPriority!=null) {
                    if (jPriority.equalsIgnoreCase("HIGH")) {
                        holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                    } else if (jPriority.equalsIgnoreCase("MEDIUM")) {
                        holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                    } else if (jPriority.equalsIgnoreCase("LOW")) {
                        holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(R.color.colorYellow));
                    }
                }*/

                System.out.println("Plan date: "+mValues.get(position).getPlandatetime());

                String text =null;
                text = "<font color=#18B064>"+ (mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
                holder.mpDateView.setText(Html.fromHtml(text));
                /*if(jStatus!=null) {
                    if (jStatus.equalsIgnoreCase(Constants.TICKET_STATUS_RESOLVED) || jStatus.equalsIgnoreCase(Constants.TICKET_STATUS_CANCELLED)) {
                        holder.listRowLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.listviewDivider));
                    } else {
                        holder.listRowLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                    }
                }*/

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
    }

    public static class AutoclosedFragment extends Fragment implements View.OnClickListener, RecyclerViewClickListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private Button mButton;
        private RecyclerView.LayoutManager mLayoutManager;
        private ArrayList<JobNeed> jobNeedArrayList;
        private JobNeedDAO jobNeedDAO;
        private SharedPreferences loginDetailPref;
        private SharedPreferences deviceRelatedPref;
        private SharedPreferences applicationPref;

        public AutoclosedFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AutoclosedFragment newInstance(int position, String pHolderText) {
            AutoclosedFragment fragment = new AutoclosedFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            loginDetailPref=getActivity().getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
            deviceRelatedPref=getActivity().getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);
            applicationPref=getActivity().getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_autocloseticket_view, container, false);
            mRecyclerView=(RecyclerView)rootView.findViewById(R.id.autoclosedtktRecyclerView);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mButton=(Button)rootView.findViewById(R.id.getAutoClosedTickets);
            mButton.setOnClickListener(this);

            jobNeedDAO=new JobNeedDAO(getActivity());

            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getAutoClosedTicketList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
            System.out.println("jobNeedArrayList from db.size(): "+jobNeedArrayList.size());
            autoTicketAdapter=new AutoTicketAdapter(getActivity(), jobNeedArrayList,this);
            mRecyclerView.setAdapter(autoTicketAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            return rootView;
        }

        @Override
        public void onClick(View v) {
            System.out.println("deleteAutoClosedTask valu: "+jobNeedDAO.deleteAutoClosedTicket());
            prepareAutoClosedRequest();
        }

        @Override
        public void onClick(View view, int position, long jobneedid, int isExpiredValue, String jobStatus) {
            //Toast.makeText(getActivity(),jobStatus,Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),TicketDetailsActivity.class);
            intent.putExtra("JOBNEEDID",jobneedid);
            startActivityForResult(intent,0);
        }


        public static class AutoTicketAdapter extends RecyclerView.Adapter<AutoTicketAdapter.ViewHolder> implements Filterable
        {
            private ArrayList<JobNeed> mValues;
            private ArrayList<JobNeed> mArrayList;
            private Context mContext;
            private TypeAssistDAO taDAO;
            private Drawable bulletLow;
            private Drawable bulletMedium;
            private Drawable bulletHigh;
            private RecyclerViewClickListener mListener;

            public AutoTicketAdapter(Context mContext, ArrayList<JobNeed> mValues,RecyclerViewClickListener mListener)
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
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.helpdesk_item_list_content, parent, false);
                return new AutoclosedFragment.AutoTicketAdapter.ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position) {
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

                /*if(jPriority!=null) {
                    if (jPriority.equalsIgnoreCase("HIGH")) {
                        holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                    } else if (jPriority.equalsIgnoreCase("MEDIUM")) {
                        holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                    } else if (jPriority.equalsIgnoreCase("LOW")) {
                        holder.mStatusView.setBackgroundColor(mContext.getResources().getColor(R.color.colorYellow));
                    }
                }*/
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

        private void prepareAutoClosedRequest()
        {
            String queryInfo=CommonFunctions.getQuery(19, getActivity());
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage(getResources().getString(R.string.please_wait)); // set message
            progressDialog.show(); // show progress dialog

            UploadParameters uploadParameters=new UploadParameters();
            uploadParameters.setServicename(Constants.SERVICE_SELECT);
            uploadParameters.setQuery(queryInfo);
            uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
            uploadParameters.setTzoffset(String.valueOf(loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
            uploadParameters.setSitecode(applicationPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
            uploadParameters.setLoginid(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
            uploadParameters.setPassword(loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
            //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
            uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

            /*Gson gson=new Gson();
            System.out.println(gson.toJson(uploadParameters));*/
            RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
            Call<ResponseData> call=retrofitServices.getAutoCloseTicket(uploadParameters);
            call.enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    progressDialog.dismiss();
                    //System.out.println("Hello");
                    Type listType;
                    if(response.code()==200)
                    {
                        System.out.println("response.body().toString(): "+response.body().toString());
                        try {
                            if(response.body().getRc()==0) {
                                if (response.body().getNrow() > 0) {
                                    String mainSplitRowChar = String.valueOf(response.body().getRow_data().charAt(0));
                                    String mainSplitColumnChar = String.valueOf(response.body().getColumns().charAt(0));

                                    if (mainSplitRowChar.trim().equalsIgnoreCase("|")) {
                                        mainSplitRowChar = "\\|";
                                    } else if (mainSplitRowChar.trim().equalsIgnoreCase("$")) {
                                        mainSplitRowChar = "\\$";
                                    }

                                    if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                                        mainSplitColumnChar = "\\|";
                                    } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                                        mainSplitColumnChar = "\\$";
                                    }

                                    String[] responseSplit = response.body().getRow_data().split(mainSplitRowChar);
                                    String[] cols = response.body().getColumns().split(mainSplitColumnChar);
                                    System.out.println("Length: " + responseSplit.length);

                                    JSONArray dataArray = new JSONArray();
                                    JSONObject dataObject = new JSONObject();


                                    for (int i = 1; i < (responseSplit.length); i++) {
                                        System.out.println("split string: " + responseSplit[i]);
                                        //System.out.println("split string number: "+i);
                                        if (responseSplit[i].trim().length() > 0) {
                                            Character startDelimitor = responseSplit[i].charAt(0);
                                            System.out.println("Start Delimeter: " + startDelimitor);
                                            String[] respRow = null;
                                            if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                                respRow = responseSplit[i].trim().split("\\$");
                                            } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                                respRow = responseSplit[i].trim().split("\\|");
                                            } else {
                                                respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                                            }


                                            if (respRow != null && respRow.length > 0) {
                                                JSONObject jsonObject = new JSONObject();
                                                for (int c = 1; c < respRow.length; c++) {
                                                    jsonObject.put(cols[c], respRow[c]);
                                                }
                                                dataArray.put(jsonObject);
                                            }


                                        }

                                    }

                                    dataObject.put("Data", dataArray);
                                    Gson gson = new Gson();
                                    JSONArray data1 = dataObject.getJSONArray("Data");
                                    listType = new TypeToken<ArrayList<JobNeed>>() {
                                    }.getType();
                                    jobNeedArrayList = new ArrayList<>();
                                    jobNeedArrayList = gson.fromJson(data1.toString(), listType);
                                    if (jobNeedArrayList != null && jobNeedArrayList.size() > 0)
                                        insertDataIntoDB();


                                }
                                else
                                {
                                    Snackbar.make(mRecyclerView,getResources().getString(R.string.ticketview_nomoreautoclosedrecord),Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    progressDialog.dismiss();

                }
            });

            /*call.enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(@NonNull Call<ResponseData> call, @NonNull Response<ResponseData> response) {
                    progressDialog.dismiss();
                    Type listType;
                    if(response.isSuccessful())
                    {
                        try {
                            if(response.body().getRc()==0) {
                                if (response.body().getNrow() > 0) {
                                    String mainSplitRowChar = String.valueOf(response.body().getRow_data().charAt(0));
                                    String mainSplitColumnChar = String.valueOf(response.body().getColumns().charAt(0));

                                    if (mainSplitRowChar.trim().equalsIgnoreCase("|")) {
                                        mainSplitRowChar = "\\|";
                                    } else if (mainSplitRowChar.trim().equalsIgnoreCase("$")) {
                                        mainSplitRowChar = "\\$";
                                    }

                                    if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                                        mainSplitColumnChar = "\\|";
                                    } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                                        mainSplitColumnChar = "\\$";
                                    }

                                    String[] responseSplit = response.body().getRow_data().split(mainSplitRowChar);
                                    String[] cols = response.body().getColumns().split(mainSplitColumnChar);
                                    System.out.println("Length: " + responseSplit.length);

                                    JSONArray dataArray = new JSONArray();
                                    JSONObject dataObject = new JSONObject();


                                    for (int i = 1; i < (responseSplit.length); i++) {
                                        System.out.println("split string: " + responseSplit[i]);
                                        //System.out.println("split string number: "+i);
                                        if (responseSplit[i].trim().length() > 0) {
                                            Character startDelimitor = responseSplit[i].charAt(0);
                                            System.out.println("Start Delimeter: " + startDelimitor);
                                            String[] respRow = null;
                                            if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                                respRow = responseSplit[i].trim().split("\\$");
                                            } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                                respRow = responseSplit[i].trim().split("\\|");
                                            } else {
                                                respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                                            }


                                            if (respRow != null && respRow.length > 0) {
                                                JSONObject jsonObject = new JSONObject();
                                                for (int c = 1; c < respRow.length; c++) {
                                                    jsonObject.put(cols[c], respRow[c]);
                                                }
                                                dataArray.put(jsonObject);
                                            }


                                        }

                                    }

                                    dataObject.put("Data", dataArray);
                                    Gson gson = new Gson();
                                    JSONArray data1 = dataObject.getJSONArray("Data");
                                    listType = new TypeToken<ArrayList<JobNeed>>() {
                                    }.getType();
                                    jobNeedArrayList = new ArrayList<>();
                                    jobNeedArrayList = gson.fromJson(data1.toString(), listType);
                                    if (jobNeedArrayList != null && jobNeedArrayList.size() > 0)
                                        insertDataIntoDB();


                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });*/
        }

        private void insertDataIntoDB()
        {
            DatabaseUtils.InsertHelper helper;
            SQLiteDatabase db = null;
            SqliteOpenHelper sqlopenHelper=SqliteOpenHelper.getInstance(getActivity());
            db=sqlopenHelper.getDatabase();

            helper = new DatabaseUtils.InsertHelper(db, JOBNeed_Table.TABLE_NAME);
            final int idColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ID);
            final int descColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_DESC);
            final int pdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PLANDATETIME);
            final int expdateColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_EXPIRYDATETIME);
            final int gracetimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GRACETIME);
            final int recvonserverColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_RECEIVEDONSERVER);
            final int starttimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTARTTIME);
            final int endtimeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBENDTIME);
            final int gpslocColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GPSLOCATION);
            final int remarkColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_REMARK);
            final int cuserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CUSER);
            final int muserColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MUSER);
            final int cdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZ);
            final int mdtzColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MDTZ);
            final int attachmentcountColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ATTACHMENTCOUNT);
            final int aatopColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_AATOP);
            final int assetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ASSETID);
            final int freqColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_FREQUENCY);
            final int jobidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBID);
            final int jobstatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBSTATUS);
            final int jobtypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_JOBTYPE);
            final int performbyColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PERFORMEDBY);
            final int priorityColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PRIORITY);
            final int qsetcodeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_QSETID);
            final int scantypeColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SCANTYPE);
            final int peopleColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PEOPLEID);
            final int peoplegroupColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_GROUPID);
            final int jnidentifierColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_IDENTIFIER);
            final int jnparentidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_PARENT);
            final int syncStatusColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SYNC_STATUS);
            final int ticketnoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETNO);
            final int buidColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_BUID);
            final int seqNoColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_SEQNO);
            final int ticketCategoryColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_TICKETCATEGORY);
            final int cdtzoffsetColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_CDTZOFFSET);
            final int mfactorColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_MFACTOR);

            for (JobNeed jobNeed : jobNeedArrayList)
            {
                helper.prepareForReplace();
                helper.bind(idColumn, jobNeed.getJobneedid());
                helper.bind(descColumn, jobNeed.getJobdesc());
                helper.bind(jobidColumn, jobNeed.getJobid());
                helper.bind(freqColumn, jobNeed.getFrequency());
                helper.bind(pdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getPlandatetime()));
                helper.bind(expdateColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getExpirydatetime()));
                helper.bind(gracetimeColumn, jobNeed.getGracetime());
                helper.bind(assetcodeColumn, jobNeed.getAssetid());
                helper.bind(qsetcodeColumn, jobNeed.getQuestionsetid());
                helper.bind(peoplegroupColumn, jobNeed.getGroupid());
                helper.bind(aatopColumn, jobNeed.getAatop());
                helper.bind(jobstatusColumn, jobNeed.getJobstatus());
                helper.bind(jobtypeColumn, jobNeed.getJobtype());
                helper.bind(scantypeColumn, jobNeed.getScantype());
                helper.bind(recvonserverColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getReceivedonserver()));
                helper.bind(priorityColumn, jobNeed.getPriority());

                helper.bind(starttimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getStarttime()));

                helper.bind(endtimeColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getEndtime()));

                helper.bind(performbyColumn, jobNeed.getPerformedby());
                helper.bind(gpslocColumn, jobNeed.getGpslocation());
                helper.bind(remarkColumn, jobNeed.getRemarks());
                helper.bind(cuserColumn, jobNeed.getCuser());
                helper.bind(muserColumn, jobNeed.getMuser());
                helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getCdtz()));
                helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeed.getMdtz()));
                helper.bind(attachmentcountColumn, jobNeed.getAttachmentcount());
                helper.bind(peopleColumn, jobNeed.getPeopleid());
                helper.bind(syncStatusColumn,"-1");
                helper.bind(jnidentifierColumn,jobNeed.getIdentifier());
                helper.bind(jnparentidColumn,jobNeed.getParent());
                helper.bind(ticketnoColumn, jobNeed.getTicketno());
                helper.bind(buidColumn, jobNeed.getBuid());
                helper.bind(seqNoColumn, jobNeed.getSeqno());
                helper.bind(ticketCategoryColumn, jobNeed.getTicketcategory());
                helper.bind(cdtzoffsetColumn, jobNeed.getCtzoffset());
                helper.bind(mfactorColumn, jobNeed.getMultiplicationfactor());
                helper.execute();
            }

            helper=null;
            jobNeedArrayList=new ArrayList<>();
            jobNeedArrayList=jobNeedDAO.getAutoClosedTicketList(Constants.JOB_NEED_IDENTIFIER_TICKET,Constants.IDENTIFIER_JOBNEED,Constants.JOB_TYPE_ADHOC);
            autoTicketAdapter=new AutoTicketAdapter(getActivity(), jobNeedArrayList,this);
            mRecyclerView.setAdapter(autoTicketAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return TicketFragment.newInstance(position,"Page No: "+(position+1));
                case 1:
                    return AutoclosedFragment.newInstance(position,"Page No: "+(position+1));
                    default:return null;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
