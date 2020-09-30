package com.youtility.intelliwiz20.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.Adapters.SiteVisitLogEListViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.SiteVisitedLogDAO;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.SiteVisitLogGroup;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SiteVisitedLogExpLVActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private SiteVisitedLogDAO siteVisitedLogDAO;
    private ArrayList<SiteVisitLogGroup>siteVisitLogGroupArrayList=null;
    private LinkedHashMap<String, SiteVisitLogGroup> subjects = new LinkedHashMap<String, SiteVisitLogGroup>();
    private SiteVisitLogEListViewAdapter siteVisitLogEListViewAdapter=null;
    private DatabaseUtils.InsertHelper helper;
    private SQLiteDatabase db = null;
    private SharedPreferences loginDetailPref;
    private ArrayList<SiteVisitedLog>siteVisitedLogArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_visited_log_exp_lv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        siteVisitedLogDAO=new SiteVisitedLogDAO(SiteVisitedLogExpLVActivity.this);
        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        expandableListView=(ExpandableListView)findViewById(R.id.expandablelistview);

        siteVisitLogGroupArrayList=siteVisitedLogDAO.getDistinctname();

        if(siteVisitLogGroupArrayList!=null && siteVisitLogGroupArrayList.size()>0)
        {
            for(int i=0;i<siteVisitLogGroupArrayList.size();i++) {
                subjects.put(siteVisitLogGroupArrayList.get(i).getSiteName(), siteVisitLogGroupArrayList.get(i));
            }
            prepareListAdapter();
        }
        else
        {
            getRecordFromServer(0);
        }



        //expandAll();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRecordFromServer(1);
            }
        });
    }

    private void getRecordFromServer(int val)
    {
        if(val==1)
            siteVisitedLogDAO.deletRecords();

        GetSiteVisitLogAsynTask getSiteVisitLogAsynTask=new GetSiteVisitLogAsynTask();
        getSiteVisitLogAsynTask.execute();
    }

    private void expandAll() {
        int count = siteVisitLogEListViewAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }

    private void prepareListAdapter()
    {
        siteVisitLogEListViewAdapter=new SiteVisitLogEListViewAdapter(SiteVisitedLogExpLVActivity.this, siteVisitLogGroupArrayList);
        expandableListView.setAdapter(siteVisitLogEListViewAdapter);
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
            siteVisitLogGroupArrayList=new ArrayList<SiteVisitLogGroup>();
            dialog = new ProgressDialog(SiteVisitedLogExpLVActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = new OkHttpClient();
            JSON = MediaType.parse("application/json; charset=utf-8");
            SqliteOpenHelper sqlopenHelper=SqliteOpenHelper.getInstance(SiteVisitedLogExpLVActivity.this);
            db=sqlopenHelper.getDatabase();

            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String queryInfo=null;
            try {
                //---------------------------------------------------------------------
                Gson gson = new Gson();
                queryInfo= getResources().getString(R.string.get_sitevisitedlog_query,loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                ServerRequest serverRequest=new ServerRequest(SiteVisitedLogExpLVActivity.this);
                HttpResponse response=serverRequest.getSiteVisitedLogResponse(queryInfo.trim(),
                        loginDetailPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                        loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                        loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                        loginDetailPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                System.out.println("JOBHistory response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                if(response.getStatusLine().getStatusCode()==200)
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
                    System.out.println("SB SiteVisitedLog: " + sb.toString());
                    response.getEntity().consumeContent();

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
                                    helper.bind(punchTimeColumn, CommonFunctions.getParseDatabaseDateFormat1(siteVisitedLog.getPunchtime()));
                                    helper.bind(remarkColumn, siteVisitedLog.getRemarks());
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
            //resetUpdating();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(siteVisitedLogArrayList!=null && siteVisitedLogArrayList.size()>0)
            {
                siteVisitLogGroupArrayList=siteVisitedLogDAO.getDistinctname();

                if(siteVisitLogGroupArrayList!=null && siteVisitLogGroupArrayList.size()>0)
                {
                    for(int i=0;i<siteVisitLogGroupArrayList.size();i++) {
                        subjects.put(siteVisitLogGroupArrayList.get(i).getSiteName(), siteVisitLogGroupArrayList.get(i));
                    }
                    prepareListAdapter();
                }
            }
        }

    }

}
