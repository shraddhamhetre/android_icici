package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.Adapters.SiteVisitedLogListViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.SiteVisitedLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.SiteVisitedLog;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Tables.SitesVisitedLog_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

public class SiteVisitedLogActivity extends AppCompatActivity {

    private ListView siteVisitLogListView;
    private SiteVisitedLogListViewAdapter siteVisitedLogListViewAdapter;
    private ArrayList<SiteVisitedLog>siteVisitedLogArrayList;
    private SiteVisitedLogDAO siteVisitedLogDAO;
    private SharedPreferences loginDetailPref;
    private DatabaseUtils.InsertHelper helper;
    private SQLiteDatabase db = null;
    private Menu mymenu;
    private boolean isVisitedFilter=false;
    private TypeAssistDAO typeAssistDAO;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_visited_log);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_sitevisitlog));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        siteVisitedLogDAO =new SiteVisitedLogDAO(SiteVisitedLogActivity.this);
        typeAssistDAO=new TypeAssistDAO(SiteVisitedLogActivity.this);

        siteVisitLogListView=(ListView)findViewById(R.id.siteVisitedLogListView);

        ArrayList<SiteVisitedLog> siteVisitedLogArrayList1=siteVisitedLogDAO.getSiteVisitLogFromDB(typeAssistDAO.getEventTypeID("AUDIT", Constants.IDENTIFIER_ATTENDANCE));
        if(siteVisitedLogArrayList1!=null && siteVisitedLogArrayList1.size()>0)
        {
            siteVisitedLogArrayList=siteVisitedLogArrayList1;
            System.out.println("---"+siteVisitedLogArrayList);
            prepareListAdapter(0);
        }

        /*if(siteVisitedLogDAO.getCount()>0)
        {
            siteVisitedLogArrayList=siteVisitedLogDAO.getSiteVisitLog();
            prepareListAdapter();
        }
        else
        {
            getRecordFromServer(0);
        }*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getRecordFromServer(1);
    }

    private void getRecordFromServer(int val)
    {
        if(val==1)
            siteVisitedLogDAO.deletRecords();

        GetSiteVisitLogAsynTask getSiteVisitLogAsynTask=new GetSiteVisitLogAsynTask();
        getSiteVisitLogAsynTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.refresh_menu,menu);
        mymenu=menu;
        return  true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isVisitedFilter)
        {
            MenuItem dItem=menu.findItem(R.id.action_visitedsites);
            dItem.setVisible(true);
            MenuItem rfsItem=menu.findItem(R.id.action_refresh);
            rfsItem.setEnabled(false);
            MenuItem pItem=menu.findItem(R.id.action_notvisitedsites);
            pItem.setVisible(false);
        }
        else
        {
            MenuItem dItem=menu.findItem(R.id.action_visitedsites);
            dItem.setVisible(false);
            MenuItem rfsItem=menu.findItem(R.id.action_refresh);
            rfsItem.setEnabled(true);
            MenuItem pItem=menu.findItem(R.id.action_notvisitedsites);
            pItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_refresh:
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView)inflater.inflate(R.layout.imageview_refresh, null);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);
                item.setActionView(iv);

                getRecordFromServer(1);
                return true;
            case R.id.action_notvisitedsites:
                isVisitedFilter=true;
                toolbar.setTitle(getResources().getString(R.string.title_sitenotvisited));
                siteVisitedLogArrayList=new ArrayList<>();
                siteVisitedLogArrayList=siteVisitedLogDAO.getSiteNotVisitLog();
                if(siteVisitedLogListViewAdapter!=null && siteVisitedLogListViewAdapter.getCount()>0)
                    siteVisitedLogListViewAdapter.clearList();
                if(siteVisitedLogArrayList!=null && siteVisitedLogArrayList.size()>0)
                {
                    for(int i=0;i<siteVisitedLogArrayList.size();i++)
                    {
                        System.out.println("Site Not visited: "+siteVisitedLogArrayList.get(i).getBuname());
                    }

                    prepareListAdapter(1);
                }

                invalidateOptionsMenu();
                return true;
            case R.id.action_visitedsites:
                isVisitedFilter=false;
                toolbar.setTitle(getResources().getString(R.string.title_sitevisitlog));
                if(siteVisitedLogListViewAdapter!=null && siteVisitedLogListViewAdapter.getCount()>0)
                    siteVisitedLogListViewAdapter.clearList();
                if(siteVisitedLogDAO.getCount()>0)
                {
                    siteVisitedLogArrayList=new ArrayList<>();
                    siteVisitedLogArrayList=siteVisitedLogDAO.getSiteVisitLog();

                    prepareListAdapter(0);
                }

                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void resetUpdating()
    {
        // Get our refresh item from the menu
        MenuItem m = mymenu.findItem(R.id.action_refresh);
        if(m.getActionView()!=null)
        {
            // Remove the animation.
            m.getActionView().clearAnimation();
            m.setActionView(null);
        }
    }

    private void prepareListAdapter(int val)
    {
        siteVisitedLogListViewAdapter =new SiteVisitedLogListViewAdapter(SiteVisitedLogActivity.this, siteVisitedLogArrayList,val);
        siteVisitLogListView.setAdapter(siteVisitedLogListViewAdapter);
    }

    private class GetSiteVisitLogAsynTask extends AsyncTask<Void, Integer, Void>
    {
        MediaType JSON;
        OkHttpClient client;
        StringBuffer sb;

        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        private Type listType;
        private ProgressDialog dialog;
        public GetSiteVisitLogAsynTask()
        {
            siteVisitedLogArrayList=new ArrayList<SiteVisitedLog>();
            dialog = new ProgressDialog(SiteVisitedLogActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
            SqliteOpenHelper sqlopenHelper=SqliteOpenHelper.getInstance(SiteVisitedLogActivity.this);
            db=sqlopenHelper.getDatabase();
            dialog.setMessage(getResources().getString(R.string.sitevisitlog_findinguserlog));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String queryInfo=null;
            try {
                //---------------------------------------------------------------------
                Gson gson = new Gson();
                queryInfo= getResources().getString(R.string.get_sitevisitedlog_query,loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                ServerRequest serverRequest=new ServerRequest(SiteVisitedLogActivity.this);
                HttpResponse response=serverRequest.getSiteVisitedLogResponse(queryInfo.trim(),
                                                        loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                                        loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                                        loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                                        loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                System.out.println("JOBHistory response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                if(response!=null && response.getStatusLine().getStatusCode()==200)
                {
                    /*is = response.getEntity().getContent();
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
                    System.out.println("SB SiteVisitedLog: " + sb.toString());
                    response.getEntity().consumeContent();*/


                    //---------------------------
                    is = response.getEntity().getContent();
                    BufferedReader in=new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb=new StringBuilder("");
                    while (true) {
                        String line = in.readLine();
                        if (line == null)
                            break;
                        sb.append(line);
                    }
                    in.close();
                    System.out.println("SB Length: "+sb.toString().length());
                    System.out.println("SB: "+sb.toString());

                    JSONObject ob = new JSONObject(sb.toString());
                    //------------------------------


                    //JSONObject ob = new JSONObject(sb.toString());

                    int status = ob.getInt(Constants.RESPONSE_RC);
                    int nrow = ob.getInt(Constants.RESPONSE_NROW);
                    if(status==0 && nrow>0)
                    {
                        String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                        String colums=ob.getString(Constants.RESPONSE_COLUMNS);
                        System.out.println("status: " + status);
                        //System.out.println("response: " + resp);
                        if(resp.trim().length()>0)
                        {
                            String mainSplitRowChar = String.valueOf(resp.charAt(0));
                            String mainSplitColumnChar=String.valueOf(colums.charAt(0));
                            System.out.println("Starting split char: " + resp.charAt(0));

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

                            String[] responseSplit = resp.split(mainSplitRowChar);
                            String[] cols=colums.split(mainSplitColumnChar);
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

                            siteVisitedLogDAO.deletRecords();

                            helper = new DatabaseUtils.InsertHelper(db, SitesVisitedLog_Table.TABLE_NAME);
                            final int buidColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUID);
                            final int bunameColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME);
                            final int bucodeColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUCODE);
                            final int punchStatusColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHSTATUS);
                            final int punchTimeColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME);
                            final int remarkColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_OTHERSITE);

                            JSONArray data1 = dataObject.getJSONArray("Data");
                            listType = new TypeToken<ArrayList<SiteVisitedLog>>() {
                            }.getType();

                            siteVisitedLogArrayList = gson.fromJson(data1.toString(), listType);

                            if(siteVisitedLogArrayList!=null && siteVisitedLogArrayList.size()>0)
                            {
                                for (SiteVisitedLog siteVisitedLog : siteVisitedLogArrayList) {
                                    helper.prepareForReplace();

                                    helper.bind(buidColumn, siteVisitedLog.getBuid());
                                    helper.bind(bunameColumn, siteVisitedLog.getBuname());
                                    helper.bind(bucodeColumn, siteVisitedLog.getBucode());
                                    helper.bind(punchStatusColumn, siteVisitedLog.getPunchstatus());
                                    helper.bind(punchTimeColumn, CommonFunctions.getParseDatabaseDateFormat(siteVisitedLog.getPunchtime()));
                                    helper.bind(remarkColumn, siteVisitedLog.getOtherlocation());
                                    //helper.bind(remarkColumn, siteVisitedLog.getRemarks());
                                    helper.execute();
                                }

                            }
                        }
                    }

                }
                else
                {
                    System.out.println("SB1 SiteVisitedLog: "+response.getStatusLine().getStatusCode());
                }


                //---------------------------------------------------------------------
                /*URL url = new URL(Constants.BASE_URL); // here is your URL path

                UploadParameters uploadParameters=new UploadParameters();
                uploadParameters.setServicename(Constants.SERVICE_SELECT);
                uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);

                queryInfo= getResources().getString(R.string.get_sitevisitedlog_query,loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));

                uploadParameters.setQuery(queryInfo);

                Gson gson = new Gson();
                String upData = gson.toJson(uploadParameters);
                System.out.println("upData: "+upData);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 *//* milliseconds *//*);
                conn.setConnectTimeout(15000 *//* milliseconds *//*);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    System.out.println("SB: "+sb.toString());

                }
                else {
                    System.out.println("SB: "+responseCode);
                }

                JSONObject ob = new JSONObject(sb.toString());

                int status = ob.getInt(Constants.RESPONSE_RC);
                int nrow = ob.getInt(Constants.RESPONSE_NROW);
                if(status==0 && nrow>0)
                {
                    String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                    String colums=ob.getString(Constants.RESPONSE_COLUMNS);
                    System.out.println("status: " + status);
                    System.out.println("response: " + resp);
                    if(resp.trim().length()>0)
                    {
                        String mainSplitRowChar = String.valueOf(resp.charAt(0));
                        String mainSplitColumnChar=String.valueOf(colums.charAt(0));
                        System.out.println("Starting split char: " + resp.charAt(0));

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

                        String[] responseSplit = resp.split(mainSplitRowChar);
                        String[] cols=colums.split(mainSplitColumnChar);
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

                        helper = new DatabaseUtils.InsertHelper(db, SitesVisitedLog_Table.TABLE_NAME);
                        final int buidColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUID);
                        final int bunameColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUNAME);
                        final int bucodeColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_BUCODE);
                        final int punchStatusColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHSTATUS);
                        final int punchTimeColumn = helper.getColumnIndex(SitesVisitedLog_Table.SITEVISITEDLOG_PUNCHTIME);

                        JSONArray data1 = dataObject.getJSONArray("Data");
                        listType = new TypeToken<ArrayList<SiteVisitedLog>>() {
                        }.getType();

                        siteVisitedLogArrayList = gson.fromJson(data1.toString(), listType);

                        if(siteVisitedLogArrayList!=null && siteVisitedLogArrayList.size()>0)
                        {
                            for (SiteVisitedLog siteVisitedLog : siteVisitedLogArrayList) {
                                helper.prepareForReplace();

                                helper.bind(buidColumn, siteVisitedLog.getBuid());
                                helper.bind(bunameColumn, siteVisitedLog.getBuname());
                                helper.bind(bucodeColumn, siteVisitedLog.getBucode());
                                helper.bind(punchStatusColumn, siteVisitedLog.getPunchstatus());
                                helper.bind(punchTimeColumn, (siteVisitedLog.getPunchtime()));
                                helper.execute();
                            }

                        }
                    }
                }*/


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            resetUpdating();
            if(siteVisitedLogArrayList!=null && siteVisitedLogArrayList.size()>0)
            {
                siteVisitedLogArrayList=new ArrayList<>();
                siteVisitedLogArrayList=siteVisitedLogDAO.getSiteVisitLog();
                siteVisitedLogDAO.getDistinctname();
                prepareListAdapter(0);
            }
        }

    }
}
