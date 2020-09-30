package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Interfaces.IDownloadDataListener;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Model.AttendanceHistory;
import com.youtility.intelliwiz20.Model.Geofence;
import com.youtility.intelliwiz20.Model.Group;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.PeopleGroupBelonging;
import com.youtility.intelliwiz20.Model.Question;
import com.youtility.intelliwiz20.Model.QuestionSet;
import com.youtility.intelliwiz20.Model.QuestionSetBelonging;
import com.youtility.intelliwiz20.Model.Sites;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;
import com.youtility.intelliwiz20.Tables.AttendanceHistoy_Table;
import com.youtility.intelliwiz20.Tables.Geofence_Table;
import com.youtility.intelliwiz20.Tables.Group_Table;
import com.youtility.intelliwiz20.Tables.JOBNeedDetails_Table;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Tables.PeopleGroupBelongin_Table;
import com.youtility.intelliwiz20.Tables.People_Table;
import com.youtility.intelliwiz20.Tables.QuestionSetBelonging_Table;
import com.youtility.intelliwiz20.Tables.QuestionSet_Table;
import com.youtility.intelliwiz20.Tables.Question_Table;
import com.youtility.intelliwiz20.Tables.Sites_Table;
import com.youtility.intelliwiz20.Tables.TypeAssist_Table;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by youtility on 24/4/18.
 */

public class DownloadDataAsynctask extends AsyncTask <Void , Integer, Integer> {
    private int counter=-1;
    private Context context;
    private SharedPreferences loginPref;
    private InputStream is;
    private StringBuffer sb;
    private String counterName;
    private Type listType;
    private SQLiteDatabase db = null;
    private Gson gson;
    private int counterVal=-1;
    private IDownloadDataListener iDownloadDataListener;
    enum SyncCount
    {
        ASSET,JN, JND, TA, GF, PEOPLE, GROUP, ATTHISTORY,QUEST,QSET,QSB,PGB,SP,TICKET,TEMPLATE
    }

    /*public DownloadDataAsynctask(Context context, int counterVal, String counterName, IDownloadDataListener iDownloadDataListener)
    {
        this.context=context;
        this.counterVal=counterVal;
        this.counterName=counterName;
        this.iDownloadDataListener=iDownloadDataListener;

    }*/

    public DownloadDataAsynctask(Context context, IDownloadDataListener iDownloadDataListener)
    {
        this.context=context;
        this.iDownloadDataListener=iDownloadDataListener;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

        SqliteOpenHelper sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        gson=new Gson();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int status=-1;

        for(int i=0; i<SyncCount.values().length;i++)
        {
            counterVal=i;
            counterName=getSyncServiceName(counterVal);

            try {

                ServerRequest serverRequest=new ServerRequest(context);
                HttpResponse response=serverRequest.getDownloadDataLogResponse(CommonFunctions.getQuery(counterVal, context),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                        loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""),
                        loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0));


                //System.out.println("DownloadAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                if(response!=null) {
                    if (response.getStatusLine().getStatusCode() == HttpsURLConnection.HTTP_OK) {
                        is = response.getEntity().getContent();

                        BufferedReader in = new BufferedReader(new InputStreamReader(is));
                        //StringBuffer sb = new StringBuffer("");
                        StringBuilder sb = new StringBuilder("");

                        while (true) {
                            String line = in.readLine();
                            if (line == null)
                                break;
                            sb.append(line);
                        }

                        in.close();

                        CommonFunctions.ResponseLog("\n" + sb.toString() + "\n");

                        JSONObject ob = new JSONObject(sb.toString());
                        if (ob.getInt(Constants.RESPONSE_RC) == 0) {
                            int totalRows = ob.getInt(Constants.RESPONSE_NROW);
                            if (totalRows > 0) {
                                status = updateData(counterVal, ob, counterName);

                            } else
                                status = 0;
                        } else {
                            status = -1;
                            break;
                        }
                    } else {
                        status = -1;
                        break;
                    }
                }
                else
                {
                    status=-1;
                    break;
                }


            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        return status;
    }

    @Override
    protected void onPostExecute(Integer val) {
        //super.onPostExecute(val);
        iDownloadDataListener.finishDownloadingData(val);

    }

    private String getSyncServiceName(int counterVal)
    {
        for(SyncCount c: SyncCount.values())
        {
            if(c.ordinal()==counterVal)
                return c.name();
        }
        return "";
    }

    private JSONObject getDataObject(JSONObject ob)
    {
        JSONObject dataObject = new JSONObject();
        try {
            String resp=ob.getString(Constants.RESPONSE_ROWDATA);
            String colums=ob.getString(Constants.RESPONSE_COLUMNS);
            //System.out.println("status: "+status);
            String mainSplitRowChar=String.valueOf(resp.charAt(0));
            String mainSplitColumnChar=String.valueOf(colums.charAt(0));

            if(mainSplitRowChar.trim().equalsIgnoreCase("|"))
            {
                mainSplitRowChar="\\|";
            }
            else if(mainSplitRowChar.trim().equalsIgnoreCase("$"))
            {
                mainSplitRowChar="\\$";
            }

            if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                mainSplitColumnChar = "\\|";
            } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                mainSplitColumnChar = "\\$";
            }

            String[] cols=colums.split(mainSplitColumnChar);
            String[] responseSplit = resp.split(mainSplitRowChar);

            JSONArray dataArray = new JSONArray();


            for (int i = 1; i < (responseSplit.length); i++) {
                //System.out.println("split string: " + responseSplit[i].toString());
                //System.out.println("split string number: "+i);
                if (responseSplit[i].trim().length() > 0) {
                    Character startDelimitor = responseSplit[i].charAt(0);
                    //System.out.println("Start Delimeter: " + startDelimitor);
                    String[] respRow = null;
                    if (startDelimitor.toString().equalsIgnoreCase("$")) {
                        respRow = responseSplit[i].trim().split("\\$");
                    } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                    {
                        respRow = responseSplit[i].trim().split("\\|");
                    }
                    else {
                        respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                    }


                    if (respRow != null && respRow.length > 0) {

                        JSONObject jsonObject=new JSONObject();
                        for(int c=1;c<respRow.length;c++) {
                            jsonObject.put(cols[c],respRow[c]);
                        }
                        dataArray.put(jsonObject);

                    }

                }
            }

            dataObject.put("Data", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataObject;
    }


    private int updateData(int position, JSONObject ob, String val)
    {
        DatabaseUtils.InsertHelper helper;
        int retVal=-1;

        ArrayList<Asset> assets=null;
        ArrayList<JobNeed> jobNeedArrayList= null;
        ArrayList<JobNeedDetails>jobNeedDetailses=null;
        ArrayList<TypeAssist>typeAssistArrayList=null;
        ArrayList<Geofence>geofenceArrayList=null;
        ArrayList<People>peopleArrayList=null;
        ArrayList<Group>groupArrayList=null;
        ArrayList<AttendanceHistory>attendanceHistoryArrayList=null;
        ArrayList<Question>questionArrayList=null;
        ArrayList<QuestionSet>questionSetArrayList=null;
        ArrayList<QuestionSetBelonging>questionSetBelongingArrayList=null;
        ArrayList<PeopleGroupBelonging>peopleGroupBelongingArrayList=null;
        ArrayList<Sites>sitesArrayList=null;
        ArrayList<JobNeed>ticketArrayList=null;
        ArrayList<JobNeedDetails>ticketDetailses=null;
        JSONObject dataObject ;
        switch(position)
        {
            case 0:
                assets=new ArrayList<Asset>();
                try {
                    db.execSQL("delete from "+ AssetDetail_Table.TABLE_NAME);
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, AssetDetail_Table.TABLE_NAME);
                    final int idColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_ID);
                    final int nameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_NAME);
                    final int syncStatusColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SYNC_STATUS);
                    final int codeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CODE);
                    final int enableColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_ENABLE);
                    final int parentColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_PARENT);
                    final int cuserColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CUSER);
                    final int muserColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CDTZ);
                    //final int isdeletedColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_IS_DELETED);
                    final int mdtzColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MDTZ);
                    final int iscriticalColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_IS_CRITICAL);
                    final int gpslocationColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_GPS_LOCATION);
                    final int identifierColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_IDENTIFIER);
                    final int runningstatusColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_RUNNING_STATUS);
                    final int buidColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BUID);
                    final int loccodeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_CODE);
                    final int locnameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_NAME);

                    final int typeColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_TYPE);
                    final int categoryColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CATEGORY);
                    final int subcategoryColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SUBCATEGORY);
                    final int brandColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BRAND);
                    final int modelColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MODEL);
                    final int supplierColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SUPPLIER);
                    final int capacityColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_CAPACITY);
                    final int unitColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_UNIT);
                    final int yomColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_YOM);
                    final int msnColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_MSN);
                    final int bdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BILLDATE);
                    final int pdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_PURCHACEDATE);
                    final int isdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_INSTALLATIONDATE);
                    final int billvalColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_BILLVALUE);
                    final int servprovColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER);
                    final int servprovnameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER_NAME);
                    final int serviceColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICE);
                    final int sfdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICEFROMDATE);
                    final int stdateColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_SERVICETODATE);
                    final int meterColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_METER);
                    final int qsetidsColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_QSETIDS);
                    final int qsetnameColumn = helper.getColumnIndex(AssetDetail_Table.ASSET_QSETNAME);

                    JSONArray dataArrayList = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Asset>>() {
                    }.getType();
                    assets = gson.fromJson(dataArrayList.toString(), listType);
                    if(assets!=null && assets.size()>0) {

                        System.out.println("------------------------------------------------------Asset Count: "+assets.size());

                        for (Asset asset : assets) {
                            helper.prepareForReplace();
                            //System.out.println("asset.getAssetname(): "+asset.getAssetname());
                            helper.bind(idColumn, asset.getAssetid());
                            helper.bind(nameColumn, asset.getAssetname());
                            helper.bind(syncStatusColumn,0);
                            helper.bind(codeColumn, asset.getAssetcode());
                            helper.bind(enableColumn, asset.getEnable());
                            helper.bind(parentColumn, asset.getParent());
                            helper.bind(cuserColumn, asset.getCuser());
                            helper.bind(muserColumn, asset.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(asset.getCdtz()));
                            //helper.bind(isdeletedColumn, asset.getIsdeleted());
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(asset.getMdtz()));
                            helper.bind(iscriticalColumn, asset.getIscritical());
                            helper.bind(gpslocationColumn, asset.getGpslocation());
                            helper.bind(identifierColumn, asset.getIdentifier());
                            helper.bind(runningstatusColumn, asset.getRunningstatus());
                            helper.bind(buidColumn, asset.getBuid());
                            helper.bind(loccodeColumn,asset.getLoccode());
                            helper.bind(locnameColumn, asset.getLocname());

                            helper.bind(typeColumn, asset.getType());
                            helper.bind(categoryColumn, asset.getCategory());
                            helper.bind(subcategoryColumn, asset.getSubcategory());
                            helper.bind(brandColumn, asset.getBrand());
                            helper.bind(modelColumn, asset.getModel());
                            helper.bind(supplierColumn, asset.getSupplier());
                            helper.bind(capacityColumn, asset.getCapacity());
                            helper.bind(unitColumn, asset.getUnit());
                            helper.bind(yomColumn, asset.getYom());
                            helper.bind(msnColumn, asset.getMsn());
                            helper.bind(bdateColumn, asset.getBdate());
                            helper.bind(pdateColumn, asset.getPdate());
                            helper.bind(isdateColumn, asset.getIsdate());
                            helper.bind(billvalColumn, asset.getBillval());
                            helper.bind(servprovColumn, asset.getServprov());
                            helper.bind(servprovnameColumn, asset.getServprovname());
                            helper.bind(serviceColumn, asset.getService());
                            helper.bind(sfdateColumn, asset.getSfdate());
                            helper.bind(stdateColumn, asset.getStdate());
                            helper.bind(meterColumn, asset.getMeter());
                            helper.bind(qsetidsColumn, asset.getQsetids());
                            helper.bind(qsetnameColumn, asset.getQsetname());

                            helper.execute();
                        }
                    }
                    retVal=0;
                    ob=null;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    //helper.close();
                    helper = null;
                    assets = null;
                }
                break;
            case 1:
                try {
                    StringBuffer sb=new StringBuffer();
                    jobNeedArrayList=new ArrayList<JobNeed>();
                    db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS +" NOT IN(0,2)");
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

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
                    //final int isdeletedColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED);
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

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeed>>() {
                    }.getType();
                    jobNeedArrayList = gson.fromJson(data.toString(), listType);

                    if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Jobneed Count: "+jobNeedArrayList.size());
                        for (JobNeed jobNeed : jobNeedArrayList) {
                            helper.prepareForReplace();

                            sb.append(jobNeed.getJobneedid()+",");

                            /*System.out.println("JObneed Desc: "+jobNeed.getJobdesc());
                            System.out.println("JObneed format date: "+CommonFunctions.getParseDate(jobNeed.getPlandatetime()));
                            System.out.println("JObneed planned date: "+(jobNeed.getPlandatetime()));*/
                            /*System.out.println("JObneed Identifier: "+jobNeed.getIdentifier());
                            System.out.println("JObneed Type: "+jobNeed.getJobtype());
                            System.out.println("-----------------------------------------------------------------");
                            System.out.println("Assigned People: "+jobNeed.getAatop());
                            System.out.println("Assigned Group: "+jobNeed.getGroupid());
*/

                            //System.out.println("JObneed planned date: "+(jobNeed.getPlandatetime()));

                            helper.bind(idColumn, jobNeed.getJobneedid());
                            helper.bind(descColumn, jobNeed.getJobdesc());
                            helper.bind(jobidColumn, jobNeed.getJobid());
                            helper.bind(freqColumn, jobNeed.getFrequency());
                            /*System.out.println("JOBNeed ID: "+jobNeed.getJobneedid());
                            System.out.println("ConversionDate: "+CommonFunctions.getParseDatabaseDateFormat(jobNeed.getPlandatetime()));
                            System.out.println("DB date: "+(jobNeed.getPlandatetime()));*/
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
                            //helper.bind(isdeletedColumn, jobNeed.getIsdeleted());
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

                            //System.out.println("ctzOffset: "+jobNeed.getCtzoffset());

                            helper.execute();
                        }

                    }
                    if(sb!=null && sb.toString().trim().length()>0)
                    {
                        DatabaseQuries.JOBNEEDIDS = sb.toString().trim().substring(0, sb.toString().trim().length() - 1);
                    }

                    retVal=0;

                } catch (Exception e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    //helper.close();
                    helper = null;
                    jobNeedArrayList = null;
                }
                break;
            case 2:

                try {
                    db.execSQL("delete from "+ JOBNeedDetails_Table.TABLE_NAME);
                    jobNeedDetailses=new ArrayList<JobNeedDetails>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, JOBNeedDetails_Table.TABLE_NAME);
                    final int jndidColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ID);
                    final int jnidColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_JOBNEEDID);
                    final int seqNoColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_SEQNO);
                    final int questNameColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_QUESTIONID);
                    final int ansColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ANSWER);
                    final int minColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MIN);
                    final int maxColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MAX);
                    final int optionColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_OPTION);
                    final int alertonColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ALERTON);

                    final int typeColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_TYPE);
                    final int ismandatoryColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_ISMANATORY);
                    final int cdtzColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MDTZ);
                    final int cuserColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_CUSER);
                    final int muserColumn = helper.getColumnIndex(JOBNeedDetails_Table.JOBNEEDDETAILS_MUSER);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeedDetails>>() {
                    }.getType();
                    jobNeedDetailses = gson.fromJson(data.toString(), listType);
                    if(jobNeedDetailses!=null &&  jobNeedDetailses.size()>0)
                    {
                        System.out.println("------------------------------------------------------Jobneeddetails Count: "+jobNeedDetailses.size());

                        for (JobNeedDetails jobNeedDetails : jobNeedDetailses) {
                            helper.prepareForReplace();
                            helper.bind(jndidColumn, jobNeedDetails.getJndid());
                            helper.bind(jnidColumn, jobNeedDetails.getJobneedid());
                            helper.bind(seqNoColumn, jobNeedDetails.getSeqno());
                            helper.bind(questNameColumn, jobNeedDetails.getQuestionid());
                            helper.bind(ansColumn, jobNeedDetails.getAnswer());
                            helper.bind(minColumn, jobNeedDetails.getMin());
                            helper.bind(maxColumn, jobNeedDetails.getMax());
                            helper.bind(optionColumn, jobNeedDetails.getOption());
                            helper.bind(alertonColumn, jobNeedDetails.getAlerton());
                            helper.bind(typeColumn, jobNeedDetails.getType());
                            helper.bind(ismandatoryColumn, jobNeedDetails.getIsmandatory());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeedDetails.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(jobNeedDetails.getMdtz()));
                            helper.bind(cuserColumn, jobNeedDetails.getCuser());
                            helper.bind(muserColumn, jobNeedDetails.getMuser());
                            helper.execute();
                        }

                    }
                    retVal=0;

                } catch (Exception e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper = null;
                    jobNeedDetailses = null;
                }
                break;
            case 3:
                try {
                    db.execSQL("delete from "+ TypeAssist_Table.TABLE_NAME);
                    typeAssistArrayList=new ArrayList<>();

                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, TypeAssist_Table.TABLE_NAME);
                    final int taidColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ID);
                    final int tacodeColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE);
                    final int tanameColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_NAME);
                    final int tatypeColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_TYPE);
                    final int tacuserColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CUSER);
                    final int tacdtzColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CDTZ);
                    final int tamuserColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_MUSER);
                    final int tamdtzColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_MDTZ);
                    //final int taisdeletedColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ISDELETED);
                    final int taparentColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_PARENT);
                    final int tabuidColumn = helper.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<TypeAssist>>() {
                    }.getType();
                    typeAssistArrayList = gson.fromJson(data.toString(), listType);
                    if(typeAssistArrayList!=null && typeAssistArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Typeassist Count: "+typeAssistArrayList.size());

                        for (TypeAssist typeAssist: typeAssistArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("TA id: "+typeAssist.getTaid());
                            System.out.println("TA Code: "+typeAssist.getTacode());
                            System.out.println("TA Type: "+typeAssist.getTatype());
                            System.out.println("-----------------------------------------------------------------");*/

                            helper.bind(taidColumn, typeAssist.getTaid());
                            helper.bind(tacodeColumn, typeAssist.getTacode());
                            helper.bind(tanameColumn, typeAssist.getTaname());
                            helper.bind(tatypeColumn, typeAssist.getTatype());
                            helper.bind(tacuserColumn, typeAssist.getCuser());
                            helper.bind(tacdtzColumn, CommonFunctions.getParseDatabaseDateFormat(typeAssist.getCdtz()));
                            helper.bind(tamuserColumn, typeAssist.getMuser());
                            helper.bind(tamdtzColumn, CommonFunctions.getParseDatabaseDateFormat(typeAssist.getMdtz()));
                            //helper.bind(taisdeletedColumn, typeAssist.getIsdeleted());
                            helper.bind(taparentColumn, typeAssist.getParent());
                            helper.bind(tabuidColumn, typeAssist.getBuid());
                            helper.execute();
                        }

                    }
                    retVal=0;

                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    typeAssistArrayList=null;
                }
                break;
            case 4:
                try {
                    db.execSQL("delete from "+ Geofence_Table.TABLE_NAME);
                    geofenceArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, Geofence_Table.TABLE_NAME);
                    final int gfidColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_ID);
                    final int gfcodeColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_CODE);
                    final int gfnameColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_NAME);
                    final int gfpointsColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_GEOFENCE_POINTS);
                    final int gfenableColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_ENABLE);

                    final int gfpeopleColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_PEOPLEID);
                    final int gffromdateColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_FROMDATE);
                    final int gfuptodateColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_UPTODATE);
                    final int gfidentifierColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_IDENTIFIER);
                    final int gfstarttimeColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_STARTTIME);
                    final int gfendtimeColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_ENDTIME);
                    final int gfbuidColumn = helper.getColumnIndex(Geofence_Table.GEOFENCE_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Geofence>>() {
                    }.getType();
                    geofenceArrayList = gson.fromJson(data.toString(), listType);
                    if(geofenceArrayList!=null && geofenceArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------Geofence Count: "+geofenceArrayList.size());

                        for (Geofence geofence: geofenceArrayList) {
                            helper.prepareForReplace();
                            helper.bind(gfidColumn, geofence.getGfid());
                            helper.bind(gfcodeColumn, geofence.getGfcode());
                            helper.bind(gfnameColumn, geofence.getGfname());
                            helper.bind(gfpointsColumn, geofence.getGeofence());
                            helper.bind(gfenableColumn, geofence.getEnable());
                            helper.bind(gfpeopleColumn, geofence.getPeopleid());
                            helper.bind(gffromdateColumn, CommonFunctions.getParseDatabaseDateFormat(geofence.getFromdt()));
                            helper.bind(gfuptodateColumn, CommonFunctions.getParseDatabaseDateFormat(geofence.getUptodt()));
                            helper.bind(gfidentifierColumn, geofence.getIdentifier());
                            helper.bind(gfstarttimeColumn, (geofence.getStarttime()));
                            helper.bind(gfendtimeColumn, (geofence.getEndtime()));
                            helper.bind(gfbuidColumn, geofence.getBuid());

                           /* System.out.println("GF name: "+geofence.getGfname());
                            System.out.println("GF code: "+geofence.getGfcode());
                            System.out.println("GF id: "+geofence.getGfid());*/
                            /*System.out.println("-----------------------------------------------------------------");

                            System.out.println("GF name: "+geofence.getGfname());
                            System.out.println("GF code: "+geofence.getGfcode());
                            System.out.println("GF points: "+geofence.getGeofence());
                            System.out.println("GF starttime: "+geofence.getStarttime());
                            System.out.println("GF endtime: "+geofence.getEndtime());
                            System.out.println("GF fromdate: "+geofence.getFromdt());
                            System.out.println("GF todate: "+geofence.getUptodt());*/

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    geofenceArrayList=null;
                }
                break;
            case 5:
                try {
                    db.execSQL("delete from "+ People_Table.TABLE_NAME);
                    peopleArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, People_Table.TABLE_NAME);
                    final int pidColumn = helper.getColumnIndex(People_Table.PEOPLE_ID);
                    final int pCodeColumn = helper.getColumnIndex(People_Table.PEOPLE_CODE);
                    final int pLoginidColumn = helper.getColumnIndex(People_Table.PEOPLE_LOGINID);
                    final int pPasswordColumn = helper.getColumnIndex(People_Table.PEOPLE_PASSWORD);
                    final int pLocTrackColumn = helper.getColumnIndex(People_Table.PEOPLE_LOCATIONTRACKING);
                    final int pNameColumn = helper.getColumnIndex(People_Table.PEOPLE_FULLNAME);
                    final int pGenderColumn = helper.getColumnIndex(People_Table.PEOPLE_GENDER);
                    final int pMobileColumn = helper.getColumnIndex(People_Table.PEOPLE_MOBILENO);
                    final int pEmailColumn = helper.getColumnIndex(People_Table.PEOPLE_EMAIL);
                    final int pDeptColumn = helper.getColumnIndex(People_Table.PEOPLE_DEPARTMENT);
                    final int pDesigColumn = helper.getColumnIndex(People_Table.PEOPLE_DESGINATION);
                    final int pTypeColumn = helper.getColumnIndex(People_Table.PEOPLE_TYPE);
                    final int pSaltColumn = helper.getColumnIndex(People_Table.PEOPLE_SALT);
                    final int pEnableColumn = helper.getColumnIndex(People_Table.PEOPLE_ENABLE);
                    final int pDOBColumn = helper.getColumnIndex(People_Table.PEOPLE_DOB);
                    final int pDOJColumn = helper.getColumnIndex(People_Table.PEOPLE_DOJ);
                    final int pReportToColumn = helper.getColumnIndex(People_Table.PEOPLE_REPORTTO);
                    final int pCuserColumn = helper.getColumnIndex(People_Table.PEOPLE_CUSER);
                    final int pCDTZColumn = helper.getColumnIndex(People_Table.PEOPLE_CDTZ);
                    final int pMuserColumn = helper.getColumnIndex(People_Table.PEOPLE_MUSER);
                    final int pMDTZColumn = helper.getColumnIndex(People_Table.PEOPLE_MDTZ);
                    //final int pIsdeletedColumn = helper.getColumnIndex(People_Table.PEOPLE_ISDELETED);
                    final int pbuidColumn = helper.getColumnIndex(People_Table.PEOPLE_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<People>>() {
                    }.getType();
                    peopleArrayList = gson.fromJson(data.toString(), listType);

                    if(peopleArrayList!=null && peopleArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Peoples Count: "+peopleArrayList.size());

                        for (People people: peopleArrayList) {

                            /*System.out.println("PeopleCode: "+people.getPeoplecode());
                            System.out.println("PeopleId: "+people.getPeopleid());
                            System.out.println("-----------------------------------------------------------------");*/

                            helper.prepareForReplace();
                            helper.bind(pCodeColumn, people.getPeoplecode());
                            helper.bind(pidColumn, people.getPeopleid());
                            helper.bind(pLoginidColumn, people.getLoginid());
                            helper.bind(pPasswordColumn, people.getPassword());
                            helper.bind(pLocTrackColumn, people.getLocationtracking());
                            helper.bind(pNameColumn, people.getPeoplename());
                            helper.bind(pGenderColumn, people.getGender());
                            helper.bind(pMobileColumn, people.getMobileno());
                            helper.bind(pEmailColumn, people.getEmail());
                            helper.bind(pDeptColumn, people.getDepartment());
                            helper.bind(pDesigColumn, people.getDesignation());
                            helper.bind(pTypeColumn, people.getPeopletype());
                            helper.bind(pSaltColumn, people.getSalt());
                            helper.bind(pEnableColumn, people.getEnable());
                            helper.bind(pDOBColumn, people.getDob());
                            helper.bind(pDOJColumn, people.getDoj());
                            helper.bind(pReportToColumn, people.getReportto());
                            helper.bind(pCuserColumn, people.getCuser());
                            helper.bind(pCDTZColumn, CommonFunctions.getParseDatabaseDateFormat(people.getCdtz()));
                            helper.bind(pMuserColumn, people.getMuser());
                            helper.bind(pMDTZColumn, CommonFunctions.getParseDatabaseDateFormat(people.getMdtz()));
                            //helper.bind(pIsdeletedColumn, people.getIsdeleted());
                            helper.bind(pbuidColumn, people.getBuid());
                            helper.execute();
                        }

                    }
                    retVal=0;

                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    peopleArrayList=null;
                }
                break;
            case 6:
                try {
                    db.execSQL("delete from "+ Group_Table.TABLE_NAME);
                    groupArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, Group_Table.TABLE_NAME);
                    final int gidColumn = helper.getColumnIndex(Group_Table.GROUP_ID);
                    final int gNameColumn = helper.getColumnIndex(Group_Table.GROUP_NAME);
                    final int gEnableColumn = helper.getColumnIndex(Group_Table.GROUP_ENABLE);
                    final int gCDTZColumn = helper.getColumnIndex(Group_Table.GROUP_CDTZ);
                    final int gMDTZColumn = helper.getColumnIndex(Group_Table.GROUP_MDTZ);
                    //final int gIsDeletedColumn = helper.getColumnIndex(Group_Table.GROUP_ISDELETED);
                    final int gCuserColumn = helper.getColumnIndex(Group_Table.GROUP_CUSER);
                    final int gMuserColumn = helper.getColumnIndex(Group_Table.GROUP_MUSER);
                    final int gbuidColumn = helper.getColumnIndex(Group_Table.GROUP_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Group>>() {
                    }.getType();
                    groupArrayList = gson.fromJson(data.toString(), listType);
                    if(groupArrayList!=null &&  groupArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Group Count: "+groupArrayList.size());

                        for (Group group: groupArrayList) {
                            helper.prepareForReplace();
                            helper.bind(gidColumn, group.getGroupid());
                            helper.bind(gNameColumn, group.getGroupname());
                            helper.bind(gEnableColumn, group.getEnable());
                            helper.bind(gCDTZColumn, CommonFunctions.getParseDatabaseDateFormat(group.getCdtz()));
                            helper.bind(gMDTZColumn, CommonFunctions.getParseDatabaseDateFormat(group.getMdtz()));
                            //helper.bind(gIsDeletedColumn, group.getIsdeleted());
                            helper.bind(gCuserColumn, group.getCuser());
                            helper.bind(gMuserColumn, group.getMuser());
                            helper.bind(gbuidColumn, group.getBuid());

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    groupArrayList=null;
                }
                break;
            case 7:
                try {
                    db.execSQL("delete from "+ AttendanceHistoy_Table.TABLE_NAME);
                    attendanceHistoryArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, AttendanceHistoy_Table.TABLE_NAME);
                    final int ahPelogColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PELOGID);
                    final int ahPeopleCodeColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PEOPLECODE);
                    final int ahDatetimeColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_DATETIME);
                    final int ahPunchStatusColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PUNCHSTATUS);
                    final int ahPunchTypeColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_PUNCHTYPE);
                    final int ahCuserColumn = helper.getColumnIndex(AttendanceHistoy_Table.ATTENDANCEHISOTY_CUSER);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<AttendanceHistory>>() {
                    }.getType();
                    attendanceHistoryArrayList = gson.fromJson(data.toString(), listType);

                    if(attendanceHistoryArrayList!=null && attendanceHistoryArrayList.size()>0)
                    {
                        for (AttendanceHistory attendanceHistory: attendanceHistoryArrayList) {
                            helper.prepareForReplace();
                            helper.bind(ahPelogColumn, attendanceHistory.getPelogid());
                            helper.bind(ahPeopleCodeColumn, attendanceHistory.getPeoplecode());
                            helper.bind(ahDatetimeColumn, attendanceHistory.getDatetime());
                            helper.bind(ahPunchStatusColumn, attendanceHistory.getPunchstatus());
                            helper.bind(ahPunchTypeColumn, attendanceHistory.getPeventtype());
                            helper.bind(ahCuserColumn, attendanceHistory.getCuser());

                            helper.execute();
                        }

                    }
                    retVal=0;

                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    attendanceHistoryArrayList=null;
                }
                break;
            case 8:
                try {
                    db.execSQL("delete from "+ Question_Table.TABLE_NAME);
                    questionArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

                    helper = new DatabaseUtils.InsertHelper(db, Question_Table.TABLE_NAME);
                    final int qCodeColumn = helper.getColumnIndex(Question_Table.QUESTION_ID);
                    final int qNameColumn = helper.getColumnIndex(Question_Table.QUESTION_NAME);
                    final int qOptionsColumn = helper.getColumnIndex(Question_Table.QUESTION_OPTIONS);
                    final int qMinColumn = helper.getColumnIndex(Question_Table.QUESTION_MIN);
                    final int qMaxColumn = helper.getColumnIndex(Question_Table.QUESTION_MAX);
                    final int qAlertonColumn = helper.getColumnIndex(Question_Table.QUESTION_ALERTON);
                    final int qCdtzColumn = helper.getColumnIndex(Question_Table.QUESTION_CDTZ);
                    final int qMdtzColumn = helper.getColumnIndex(Question_Table.QUESTION_MDTZ);
                    //final int qIsdeletedColumn = helper.getColumnIndex(Question_Table.QUESTION_ISDELETED);
                    final int qCuserColumn = helper.getColumnIndex(Question_Table.QUESTION_CUSER);
                    final int qMuserColumn = helper.getColumnIndex(Question_Table.QUESTION_MUSER);
                    final int qTypeColumn = helper.getColumnIndex(Question_Table.QUESTION_TYPE);
                    final int qUnitColumn = helper.getColumnIndex(Question_Table.QUESTION_UNIT);
                    final int qBuidColumn = helper.getColumnIndex(Question_Table.QUESTION_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Question>>() {
                    }.getType();
                    questionArrayList = gson.fromJson(data.toString(), listType);
                    if(questionArrayList!=null && questionArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------Question Count: "+questionArrayList.size());
                        for (Question question: questionArrayList) {
                            helper.prepareForReplace();
                            //System.out.println("Quset Code: "+question.getQuestionid());
                            helper.bind(qCodeColumn, question.getQuestionid());
                            helper.bind(qNameColumn, question.getQuestionname());
                            helper.bind(qOptionsColumn, question.getOptions());
                            helper.bind(qMinColumn, question.getMin());
                            helper.bind(qMaxColumn, question.getMax());
                            helper.bind(qAlertonColumn, question.getAlertOn());
                            helper.bind(qCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(question.getCdtz()));
                            helper.bind(qMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(question.getMdtz()));
                            //helper.bind(qIsdeletedColumn, question.getIsDeleted());
                            helper.bind(qCuserColumn, question.getCuser());
                            helper.bind(qMuserColumn, question.getMuser());
                            helper.bind(qTypeColumn, question.getType());
                            helper.bind(qUnitColumn, question.getUnit());
                            helper.bind(qBuidColumn, question.getBuid());

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    questionArrayList=null;
                }
                break;
            case 9:
                try {
                    db.execSQL("delete from "+ QuestionSet_Table.TABLE_NAME);
                    questionSetArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, QuestionSet_Table.TABLE_NAME);
                    final int qsCodeColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ID);
                    final int qsAssetidColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ASSETID);
                    final int qsNameColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_NAME);
                    final int qsEnableColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ENABLE);
                    final int qsSeqNoColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_SEQNO);
                    final int qsCdtzColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_CDTZ);
                    final int qsMdtzColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_MDTZ);
                    //final int qsIsdeletedColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_ISDELETED);
                    final int qsCuserColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_CUSER);
                    final int qsMuserColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_MUSER);
                    final int qsParentColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_PARENT);
                    final int qsTypeColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_TYPE);
                    final int qsBuidColumn = helper.getColumnIndex(QuestionSet_Table.QUESTION_SET_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<QuestionSet>>() {
                    }.getType();
                    questionSetArrayList = gson.fromJson(data.toString(), listType);
                    if( questionSetArrayList!=null &&  questionSetArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Qset Count: "+questionSetArrayList.size());

                        for (QuestionSet questionSet: questionSetArrayList) {
                            helper.prepareForReplace();
                            /*System.out.println("QSet id: "+questionSet.getQuestionsetid());
                            System.out.println("QSet name: "+questionSet.getQsetname());
                            System.out.println("QSet parent: "+questionSet.getParent());
                            System.out.println("QSet type: "+questionSet.getType());*/
                            helper.bind(qsCodeColumn, questionSet.getQuestionsetid());
                            helper.bind(qsAssetidColumn, questionSet.getAssetid());
                            helper.bind(qsNameColumn, questionSet.getQsetname());
                            helper.bind(qsEnableColumn, questionSet.getEnable());
                            helper.bind(qsSeqNoColumn, questionSet.getSeqno());
                            helper.bind(qsCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSet.getCdtz()));
                            helper.bind(qsMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSet.getMdtz()));
                            //helper.bind(qsIsdeletedColumn, questionSet.getIsdeleted());
                            helper.bind(qsCuserColumn, questionSet.getCuser());
                            helper.bind(qsMuserColumn, questionSet.getMuser());
                            helper.bind(qsParentColumn, questionSet.getParent());
                            helper.bind(qsTypeColumn, questionSet.getType());
                            helper.bind(qsBuidColumn, questionSet.getBuid());

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    questionSetArrayList=null;
                }
                break;
            case 10:
                try {
                    db.execSQL("delete from "+ QuestionSetBelonging_Table.TABLE_NAME);
                    questionSetBelongingArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, QuestionSetBelonging_Table.TABLE_NAME);
                    final int qsbIDColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ID);
                    final int qsbIsMandatoryColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ISMANDATORY);
                    final int qsbSeqnoColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_SEQNO);
                    final int qsbCdtzColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_CDTZ);
                    final int qsbMdtzColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MDTZ);
                    //final int qsbIsdeletedColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ISDELETED);
                    final int qsbCuserColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_CUSER);
                    final int qsbMuserColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MUSER);
                    final int qsbQsetcodeColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_QUESTIONSETID);
                    final int qsbQestCodeColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_QUESTIONID);
                    final int qsbMinColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MIN);
                    final int qsbMaxColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_MAX);
                    final int qsbAlertonColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_ALERTON);
                    final int qsbOptionColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_OPTION);
                    final int qsbBuidColumn = helper.getColumnIndex(QuestionSetBelonging_Table.QUESTIONSETBELONGING_BUID);


                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<QuestionSetBelonging>>() {
                    }.getType();
                    questionSetBelongingArrayList = gson.fromJson(data.toString(), listType);
                    if(questionSetBelongingArrayList!=null && questionSetBelongingArrayList.size()>0)
                    {

                        System.out.println("------------------------------------------------------QsetBelonging Count: "+questionSetBelongingArrayList.size());
                        for (QuestionSetBelonging questionSetBelonging: questionSetBelongingArrayList) {
                            helper.prepareForReplace();

                            helper.bind(qsbIDColumn, questionSetBelonging.getQsbid());
                            helper.bind(qsbIsMandatoryColumn, questionSetBelonging.getIsmandatory());
                            helper.bind(qsbSeqnoColumn, questionSetBelonging.getSeqno());
                            helper.bind(qsbCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSetBelonging.getCdtz()));
                            helper.bind(qsbMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(questionSetBelonging.getMdtz()));
                            //helper.bind(qsbIsdeletedColumn, questionSetBelonging.getIsdeleted());
                            helper.bind(qsbCuserColumn, questionSetBelonging.getCuser());
                            helper.bind(qsbMuserColumn, questionSetBelonging.getMuser());
                            helper.bind(qsbQsetcodeColumn, questionSetBelonging.getQuestionsetid());
                            helper.bind(qsbQestCodeColumn, questionSetBelonging.getQuestionid());
                            helper.bind(qsbMinColumn, questionSetBelonging.getMin());
                            helper.bind(qsbMaxColumn, questionSetBelonging.getMax());
                            helper.bind(qsbAlertonColumn, questionSetBelonging.getAlerton());
                            helper.bind(qsbOptionColumn, questionSetBelonging.getOption());
                            helper.bind(qsbBuidColumn, questionSetBelonging.getBuid());

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    questionSetBelongingArrayList=null;
                }
                break;
            case 11:
                try {
                    db.execSQL("delete from "+ PeopleGroupBelongin_Table.TABLE_NAME);
                    peopleGroupBelongingArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, PeopleGroupBelongin_Table.TABLE_NAME);
                    final int pgbIDColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_ID);
                    final int pgbIsgroupleadColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_ISGROUPLEAD);
                    final int pgbCdtzColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_CDTZ);
                    final int pgbMdtzColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_MDTZ);
                    //final int pgbIsdeletedColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_ISDELETED);
                    final int pgbCuserColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_CUSER);
                    final int pgbGroupcodeColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_GROUPID);
                    final int pgbMuserColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_MUSER);
                    final int pgbPeoplecodeColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_PEOPLEID);
                    final int pgbBuidColumn = helper.getColumnIndex(PeopleGroupBelongin_Table.PGB_BUID);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<PeopleGroupBelonging>>() {
                    }.getType();
                    peopleGroupBelongingArrayList = gson.fromJson(data.toString(), listType);
                    if(peopleGroupBelongingArrayList!=null && peopleGroupBelongingArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------peoplegroupbelonging Count: "+peopleGroupBelongingArrayList.size());

                        for (PeopleGroupBelonging peopleGroupBelonging: peopleGroupBelongingArrayList) {
                            helper.prepareForReplace();
                            helper.bind(pgbIDColumn, peopleGroupBelonging.getPgbid());
                            helper.bind(pgbIsgroupleadColumn, peopleGroupBelonging.getIsgrouplead());
                            helper.bind(pgbCdtzColumn, CommonFunctions.getParseDatabaseDateFormat(peopleGroupBelonging.getCdtz()));
                            helper.bind(pgbMdtzColumn, CommonFunctions.getParseDatabaseDateFormat(peopleGroupBelonging.getMdtz()));
                            //helper.bind(pgbIsdeletedColumn, peopleGroupBelonging.getIsdeleted());
                            helper.bind(pgbCuserColumn, peopleGroupBelonging.getCuser());
                            helper.bind(pgbGroupcodeColumn, peopleGroupBelonging.getGroupid());
                            helper.bind(pgbMuserColumn, peopleGroupBelonging.getMuser());
                            helper.bind(pgbPeoplecodeColumn, peopleGroupBelonging.getPeopleid());
                            helper.bind(pgbBuidColumn, peopleGroupBelonging.getBuid());

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    peopleGroupBelongingArrayList=null;
                }
                break;
            case 12:
                try {
                    db.execSQL("delete from "+ Sites_Table.TABLE_NAME);
                    sitesArrayList=new ArrayList<>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);
                    helper = new DatabaseUtils.InsertHelper(db, Sites_Table.TABLE_NAME);
                    final int sitepeopleidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_ID);
                    final int fromdtColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_FROMDATE);
                    final int uptodtColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_UPTODATE);
                    final int siteownerColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_SITEOWNER);
                    final int buidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_BUILD);
                    final int peopleidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_ID);
                    final int reporttoColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_REPORTTO);
                    final int shiftColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_SHIFT);
                    final int slnoColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_SLNO);
                    final int postingrevColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_POSTINGREV);
                    final int contractidColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_CONTRACTID);
                    final int cuserColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_CUSER);
                    final int muserColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_MUSER);
                    final int cdtzColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_CDTZ);
                    final int mdtzColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_MDTZ);
                    //final int isdeletedColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_ISDELETED);
                    final int worktypeColumn = helper.getColumnIndex(Sites_Table.SITE_PEOPLE_WORKTYPE);
                    final int bu_bunameColumn = helper.getColumnIndex(Sites_Table.BU_NAME);
                    final int bu_bucodeColumn = helper.getColumnIndex(Sites_Table.BU_CODE);
                    final int sitereportidColumn = helper.getColumnIndex(Sites_Table.SITE_REPORT_ID);
                    final int sitereportnameColumn = helper.getColumnIndex(Sites_Table.SITE_REPORT_NAME);

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<Sites>>() {
                    }.getType();
                    sitesArrayList = gson.fromJson(data.toString(), listType);
                    if(sitesArrayList!=null && sitesArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Sites Count: "+sitesArrayList.size());

                        for (Sites sites: sitesArrayList) {
                            helper.prepareForReplace();
                            helper.bind(sitepeopleidColumn, sites.getSitepeopleid());
                            helper.bind(fromdtColumn, (sites.getFromdt()));
                            helper.bind(uptodtColumn, (sites.getUptodt()));
                            helper.bind(siteownerColumn, sites.getSiteowner());
                            helper.bind(buidColumn, sites.getBuid());
                            helper.bind(peopleidColumn, sites.getPeopleid());
                            helper.bind(reporttoColumn, sites.getReportto());
                            helper.bind(shiftColumn, sites.getShift());
                            helper.bind(slnoColumn, sites.getSlno());
                            helper.bind(postingrevColumn, sites.getPostingrev());
                            helper.bind(contractidColumn, sites.getContractid());
                            helper.bind(cuserColumn, sites.getCuser());
                            helper.bind(muserColumn, sites.getMuser());
                            helper.bind(cdtzColumn, CommonFunctions.getParseDatabaseDateFormat(sites.getCdtz()));
                            helper.bind(mdtzColumn, CommonFunctions.getParseDatabaseDateFormat(sites.getMdtz()));
                            //helper.bind(isdeletedColumn, sites.getIsdeleted());
                            helper.bind(worktypeColumn, sites.getWorktype());
                            helper.bind(bu_bunameColumn, sites.getBuname());
                            helper.bind(bu_bucodeColumn, sites.getBucode());
                            helper.bind(sitereportidColumn, sites.getReportids());
                            helper.bind(sitereportnameColumn, sites.getReportnames());

                            helper.execute();
                        }

                    }
                    retVal=0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    retVal=-1;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper=null;
                    sitesArrayList=null;
                }
                break;
            case 13:
                try {

                    db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME+" where "+JOBNeed_Table.JOBNEED_IDENTIFIER+" = (select taid from TypeAssist where tacode = '"+Constants.JOB_NEED_IDENTIFIER_TICKET+"') AND "+JOBNeed_Table.JOBNEED_SYNC_STATUS +" NOT IN(0,2)");
                    ticketArrayList=new ArrayList<JobNeed>();
                    dataObject = new JSONObject();
                    dataObject=getDataObject(ob);

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
                    //final int isdeletedColumn = helper.getColumnIndex(JOBNeed_Table.JOBNEED_ISDELETED);
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

                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeed>>() {
                    }.getType();
                    ticketArrayList = gson.fromJson(data.toString(), listType);

                    if(ticketArrayList!=null && ticketArrayList.size()>0)
                    {
                        System.out.println("------------------------------------------------------Ticket Count: "+ticketArrayList.size());

                        for (JobNeed jobNeed : ticketArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("JObneed Desc: "+jobNeed.getJobdesc());
                            System.out.println("JObneed Identifier: "+jobNeed.getIdentifier());
                            System.out.println("JObneed Type: "+jobNeed.getJobtype());
                            System.out.println("Assigned People: "+jobNeed.getAatop());
                            System.out.println("Assigned Group: "+jobNeed.getGroupid());
                            System.out.println("Assigned People: "+jobNeed.getPeopleid());*/

                            helper.bind(idColumn, jobNeed.getJobneedid());
                            helper.bind(descColumn, jobNeed.getJobdesc());
                            helper.bind(jobidColumn, jobNeed.getJobid());
                            helper.bind(freqColumn, jobNeed.getFrequency());
                            /*helper.bind(pdateColumn, CommonFunctions.getParseDate(jobNeed.getPlandatetime()));
                            helper.bind(expdateColumn, CommonFunctions.getParseDate(jobNeed.getExpirydatetime()));*/
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
                            //helper.bind(isdeletedColumn, jobNeed.getIsdeleted());
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


                            helper.execute();
                        }

                    }

                    retVal=0;

                } catch (Exception e) {
                    e.printStackTrace();
                    retVal=-1;
                } finally {
                    helper = null;
                    ticketArrayList = null;
                }
                break;
        }
        return retVal;
    }
}
