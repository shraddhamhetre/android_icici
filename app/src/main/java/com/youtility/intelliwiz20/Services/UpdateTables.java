package com.youtility.intelliwiz20.Services;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Interfaces.SyncInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by youtility4 on 4/9/17.
 */

public class UpdateTables implements SyncInterface {
    private Context context;
    private SQLiteDatabase db = null;
    private String ROOT =null;
    private DatabaseUtils.InsertHelper helper;
    private Gson gson;
    private Type listType;
    public UpdateTables(Context context)
    {
        this.context=context;
        ROOT=context.getFilesDir().getPath();
        //ROOT = Environment.getExternalStorageDirectory().getAbsolutePath().toString() +"/"+ Constants.FOLDER_NAME;
        System.out.println("Root: "+ROOT);
    }

    public boolean updateTable()
    {
        gson = new Gson();
        boolean success = true;
        try {
            db = SqliteOpenHelper.getInstance(context).getWritableDatabase();
            db.beginTransaction();
            db.execSQL("PRAGMA foreign_keys=ON;");

            assetMaster();

            jobNeedMaster();

            jobNeedDetailsMaster();

            typeAssistMaster();

            geoFenceDetailsMaster();

            peopleDetailMaster();

            groupDetailMaster();

            questionMaster();

            questionSetMaster();

            questionSetBelongingMaster();

            peopleGroupBelongingMaster();

            siteMaster();
            //-----------------------------------


            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

        }
        return success;
    }

    public String readFile(String fileName)
    {
        try {
            fileName = ROOT + "/"+ fileName;
            FileReader reader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer bf = new StringBuffer(" ");
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                bf.append(line);
            }
            reader.close();
            return bf.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }



        /*fileName = ROOT + "/"+ fileName;
        System.out.println("reading FileName: "+fileName);
        BufferedInputStream f = null;
        BufferedReader reader=null;
        try {
            StringBuffer bf = new StringBuffer(" ");
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"),8192);
            String json = reader.readLine();
            do {
                bf.append(json);
                json = reader.readLine();
            }while (json != null);
            reader.close();
            Log.d("LOGS", " readFile output fileName "+fileName+" data "+new String(bf));
            return bf.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (f != null) {
                try {
                    f.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            reader=null;
        }*/

    }

    @Override
    public boolean assetMaster() throws Exception {
        ArrayList<Asset> assets=null;
        try {
            assets=new ArrayList<Asset>();
            String json = readFile(Constants.FILE_ASSET);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            //int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0)
            {
                int totalRows = ob.getInt("nrow");
                if(totalRows>0)
                {
                    try {
                        db.execSQL("delete from "+ AssetDetail_Table.TABLE_NAME);

                        String resp=ob.getString("row_data");
                        String colums=ob.getString("columns");
                        //System.out.println("status: "+status);
                        String mainSplitRowChar=String.valueOf(resp.charAt(0));
                        String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                        if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                        {
                            mainSplitRowChar="\\|";
                        }
                        else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                        {
                            mainSplitRowChar="\\$";
                        }

                        if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                            mainSplitColumnChar = "\\|";
                        } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                            mainSplitColumnChar = "\\$";
                        }

                        String[] cols=colums.split(mainSplitColumnChar);
                        String[] responseSplit = resp.split(mainSplitRowChar);

                        JSONArray dataArray = new JSONArray();
                        JSONObject dataObject = new JSONObject();

                        for (int i = 1; i < (responseSplit.length); i++) {
                            //System.out.println("split string: " + responseSplit[i].toString());
                            //System.out.println("split string number: "+i);
                            if (responseSplit[i].toString().trim().length() > 0) {
                                Character startDelimitor = responseSplit[i].charAt(0);
                                //System.out.println("Start Delimeter: " + startDelimitor);
                                String[] respRow = null;
                                if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                    respRow = responseSplit[i].toString().trim().split("\\$");
                                } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                                {
                                    respRow = responseSplit[i].toString().trim().split("\\|");
                                }
                                else {
                                    respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                                }


                                if (respRow != null && respRow.length > 0) {

                                    JSONObject jsonObject=new JSONObject();
                                    for(int c=1;c<respRow.length;c++) {
                                        jsonObject.put(cols[c].toString(),respRow[c]);
                                    }
                                    dataArray.put(jsonObject);


                                }


                            }
                        }

                        dataObject.put("Data", dataArray);


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

                        JSONArray data = dataObject.getJSONArray("Data");
                        listType = new TypeToken<ArrayList<Asset>>() {
                        }.getType();
                        assets = gson.fromJson(data.toString(), listType);
                        if(assets!=null && assets.size()>0) {
                            for (Asset asset : assets) {
                                helper.prepareForReplace();
                                helper.bind(idColumn, asset.getAssetid());
                                helper.bind(nameColumn, asset.getAssetname());
                                helper.bind(syncStatusColumn,0);
                                helper.bind(codeColumn, asset.getAssetcode());
                                helper.bind(enableColumn, asset.getEnable());
                                helper.bind(parentColumn, asset.getParent());
                                helper.bind(cuserColumn, asset.getCuser());
                                helper.bind(muserColumn, asset.getMuser());
                                helper.bind(cdtzColumn, asset.getCdtz());
                                //helper.bind(isdeletedColumn, asset.getIsdeleted());
                                helper.bind(mdtzColumn, asset.getMdtz());
                                helper.bind(iscriticalColumn, asset.getIscritical());
                                helper.bind(gpslocationColumn, asset.getGpslocation());
                                helper.bind(identifierColumn, asset.getIdentifier());
                                helper.bind(runningstatusColumn, asset.getRunningstatus());
                                helper.bind(buidColumn, asset.getBuid());
                                helper.execute();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    } finally {
                        helper.close();
                        helper = null;
                        assets = null;
                    }
                }

                return  true;
            }

            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }


    }

    @Override
    public boolean jobNeedMaster() throws Exception {
        ArrayList<JobNeed> jobNeedArrayList= null;
        try {
            jobNeedArrayList=new ArrayList<JobNeed>();
            String json = readFile(Constants.FILE_JOBNEED);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            //int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0){
                int totalRows = ob.getInt("nrow");

                if(totalRows>0)
                {
                    db.execSQL("delete from "+ JOBNeed_Table.TABLE_NAME);

                    String resp=ob.getString("row_data");
                    String colums=ob.getString("columns");
                    //System.out.println("status: "+status);
                    String mainSplitRowChar=String.valueOf(resp.charAt(0));
                    String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                    if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                    {
                        mainSplitRowChar="\\|";
                    }
                    else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                    {
                        mainSplitRowChar="\\$";
                    }


                    if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                        mainSplitColumnChar = "\\|";
                    } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                        mainSplitColumnChar = "\\$";
                    }

                    String[] responseSplit = resp.split(mainSplitRowChar);
                    String[] cols=colums.split(mainSplitColumnChar);

                    JSONArray dataArray = new JSONArray();
                    JSONObject dataObject = new JSONObject();


                    for (int i = 1; i < (responseSplit.length); i++) {
                        //System.out.println("split string: " + responseSplit[i].toString());
                        //System.out.println("split string number: "+i);
                        if (responseSplit[i].toString().trim().length() > 0) {
                            Character startDelimitor = responseSplit[i].charAt(0);
                            //System.out.println("Start Delimeter: " + startDelimitor);
                            String[] respRow = null;
                            if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                respRow = responseSplit[i].toString().trim().split("\\$");
                            } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                            {
                                respRow = responseSplit[i].toString().trim().split("\\|");
                            }
                            else {
                                respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                            }


                            if(respRow!=null && respRow.length>0) {
                                JSONObject jsonObject = new JSONObject();
                                for (int c = 1; c < respRow.length; c++) {
                                    jsonObject.put(cols[c].toString(), respRow[c]);
                                    //System.out.println("cols[c].toString(): "+cols[c].toString());
                                }
                                dataArray.put(jsonObject);
                            }


                        }
                    }

                    dataObject.put("Data", dataArray);

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



                    JSONArray data = dataObject.getJSONArray("Data");
                    listType = new TypeToken<ArrayList<JobNeed>>() {
                    }.getType();
                    jobNeedArrayList = gson.fromJson(data.toString(), listType);

                    try {

                        if(jobNeedArrayList!=null && jobNeedArrayList.size()>0)
                        {
                            for (JobNeed jobNeed : jobNeedArrayList) {
                                helper.prepareForReplace();

                                /*System.out.println("JObneed Desc: "+jobNeed.getJobdesc());
                                System.out.println("JObneed Identifier: "+jobNeed.getIdentifier());
                                System.out.println("JObneed Type: "+jobNeed.getJobtype());
                                System.out.println("Assigned People: "+jobNeed.getAatop());
                                System.out.println("Assigned Group: "+jobNeed.getGroupid());*/

                                helper.bind(idColumn, jobNeed.getJobneedid());
                                helper.bind(descColumn, jobNeed.getJobdesc());
                                helper.bind(jobidColumn, jobNeed.getJobid());
                                helper.bind(freqColumn, jobNeed.getFrequency());
                                helper.bind(pdateColumn, CommonFunctions.getParseDate(jobNeed.getPlandatetime()));
                                helper.bind(expdateColumn, CommonFunctions.getParseDate(jobNeed.getExpirydatetime()));
                                helper.bind(gracetimeColumn, jobNeed.getGracetime());
                                helper.bind(assetcodeColumn, jobNeed.getAssetid());
                                helper.bind(qsetcodeColumn, jobNeed.getQuestionsetid());
                                helper.bind(peoplegroupColumn, jobNeed.getGroupid());
                                helper.bind(aatopColumn, jobNeed.getAatop());
                                helper.bind(jobstatusColumn, jobNeed.getJobstatus());
                                helper.bind(jobtypeColumn, jobNeed.getJobtype());
                                helper.bind(scantypeColumn, jobNeed.getScantype());
                                helper.bind(recvonserverColumn, jobNeed.getReceivedonserver());
                                helper.bind(priorityColumn, jobNeed.getPriority());
                                helper.bind(starttimeColumn, jobNeed.getStarttime());
                                helper.bind(endtimeColumn, jobNeed.getEndtime());
                                helper.bind(performbyColumn, jobNeed.getPerformedby());
                                helper.bind(gpslocColumn, jobNeed.getGpslocation());
                                helper.bind(remarkColumn, jobNeed.getRemarks());
                                //helper.bind(isdeletedColumn, jobNeed.getIsdeleted());
                                helper.bind(cuserColumn, jobNeed.getCuser());
                                helper.bind(muserColumn, jobNeed.getMuser());
                                helper.bind(cdtzColumn, jobNeed.getCdtz());
                                helper.bind(mdtzColumn, jobNeed.getMdtz());
                                helper.bind(attachmentcountColumn, jobNeed.getAttachmentcount());
                                helper.bind(peopleColumn, jobNeed.getPeopleid());
                                helper.bind(syncStatusColumn,"-1");
                                helper.bind(jnidentifierColumn,jobNeed.getIdentifier());
                                helper.bind(jnparentidColumn,jobNeed.getParent());
                                helper.bind(ticketnoColumn, jobNeed.getTicketno());
                                helper.bind(buidColumn, jobNeed.getBuid());
                                helper.execute();
                            }

                        }

                    } catch (Exception e) {
                        throw e;
                        //return true;
                    } finally {
                        helper.close();
                        helper = null;
                        jobNeedArrayList = null;
                    }

                }

                return  true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }
        //return true;
    }

    @Override
    public boolean jobNeedDetailsMaster() throws Exception {
        ArrayList<JobNeedDetails>jobNeedDetailses=null;
        try {
            jobNeedDetailses=new ArrayList<JobNeedDetails>();
            String json = readFile(Constants.FILE_JOBNEED_DETAILS);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {

                db.execSQL("delete from "+ JOBNeedDetails_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                //System.out.println("status: "+status);
                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();


                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                       /* if (respRow != null && respRow.length > 0) {
                            JobNeedDetails jobNeedDetails = new JobNeedDetails();
                            jobNeedDetails.setJndid(respRow[Constants.JOB_NEED_DETAILS_COLUMN_JNDID]);
                            jobNeedDetails.setAlerton(respRow[Constants.JOB_NEED_DETAILS_COLUMN_ALERTON]);
                            jobNeedDetails.setAnswer(respRow[Constants.JOB_NEED_DETAILS_COLUMN_ANSWER]);
                            jobNeedDetails.setJobneedid(respRow[Constants.JOB_NEED_DETAILS_COLUMN_JOBNEEDID]);
                            jobNeedDetails.setMax(respRow[Constants.JOB_NEED_DETAILS_COLUMN_MAX]);
                            jobNeedDetails.setMin(respRow[Constants.JOB_NEED_DETAILS_COLUMN_MIN]);
                            jobNeedDetails.setOption(respRow[Constants.JOB_NEED_DETAILS_COLUMN_OPTION]);
                            jobNeedDetails.setQuestionname(respRow[Constants.JOB_NEED_DETAILS_COLUMN_QUESTIONNAME]);
                            jobNeedDetails.setSeqno(respRow[Constants.JOB_NEED_DETAILS_COLUMN_SEQNO]);
                            jobNeedDetails.setIsmandatory(respRow[Constants.JOB_NEED_DETAILS_COLUMN_ISMANATORY]);
                            jobNeedDetails.setCdtz(respRow[Constants.JOB_NEED_DETAILS_COLUMN_CDTZ]);
                            jobNeedDetails.setMdtz(respRow[Constants.JOB_NEED_DETAILS_COLUMN_MDTZ]);
                            jobNeedDetails.setIsdeleted(respRow[Constants.JOB_NEED_DETAILS_COLUMN_ISDELETED]);
                            jobNeedDetails.setCuser(respRow[Constants.JOB_NEED_DETAILS_COLUMN_CUSER]);
                            jobNeedDetails.setMuser(respRow[Constants.JOB_NEED_DETAILS_COLUMN_MUSER]);
                            jobNeedDetails.setType(respRow[Constants.JOB_NEED_DETAILS_COLUMN_TYPE]);

                            jobNeedDetailses.add(jobNeedDetails);
                        }*/


                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if(jobNeedDetailses!=null &&  jobNeedDetailses.size()>0)
                    {
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
                            helper.bind(cdtzColumn, jobNeedDetails.getCdtz());
                            helper.bind(mdtzColumn, jobNeedDetails.getMdtz());
                            helper.bind(cuserColumn, jobNeedDetails.getCuser());
                            helper.bind(muserColumn, jobNeedDetails.getMuser());
                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    jobNeedDetailses = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean typeAssistMaster() throws Exception {
        ArrayList<TypeAssist>typeAssistArrayList=null;
        try {
            typeAssistArrayList=new ArrayList<TypeAssist>();
            String json = readFile(Constants.FILE_TYPE_ASSIST);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {

                db.execSQL("delete from "+ TypeAssist_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");
                //System.out.println("status: "+status);
                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }


                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().split(startDelimitor.toString(), 0);
                        }

                        /*if (respRow != null && respRow.length > 0) {
                            TypeAssist typeAssist= new TypeAssist();
                            typeAssist.setTacode(respRow[Constants.TYPE_ASSIST_CODE]);
                            typeAssist.setTaname(respRow[Constants.TYPE_ASSIST_NAME]);
                            typeAssist.setTatype(respRow[Constants.TYPE_ASSIST_TYPE]);
                            typeAssist.setCuser(respRow[Constants.TYPE_ASSIST_CUSER]);
                            typeAssist.setCdtz(respRow[Constants.TYPE_ASSIST_CDTZ]);
                            typeAssist.setMuser(respRow[Constants.TYPE_ASSIST_MUSER]);
                            typeAssist.setMdtz(respRow[Constants.TYPE_ASSIST_MDTZ]);
                            typeAssist.setIsdeleted(respRow[Constants.TYPE_ASSIST_ISDELETED]);
                            typeAssist.setParent(respRow[Constants.TYPE_ASSIST_PARENT]);
                            typeAssistArrayList.add(typeAssist);
                        }*/

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }


                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if(typeAssistArrayList!=null && typeAssistArrayList.size()>0)
                    {

                        for (TypeAssist typeAssist: typeAssistArrayList) {
                            helper.prepareForReplace();

                            /*System.out.println("TA id: "+typeAssist.getTaid());
                            System.out.println("TA Code: "+typeAssist.getTacode());
                            System.out.println("TA Type: "+typeAssist.getTatype());*/

                            helper.bind(taidColumn, typeAssist.getTaid());
                            helper.bind(tacodeColumn, typeAssist.getTacode());
                            helper.bind(tanameColumn, typeAssist.getTaname());
                            helper.bind(tatypeColumn, typeAssist.getTatype());
                            helper.bind(tacuserColumn, typeAssist.getCuser());
                            helper.bind(tacdtzColumn, typeAssist.getCdtz());
                            helper.bind(tamuserColumn, typeAssist.getMuser());
                            helper.bind(tamdtzColumn, typeAssist.getMdtz());
                            //helper.bind(taisdeletedColumn, typeAssist.getIsdeleted());
                            helper.bind(taparentColumn, typeAssist.getParent());
                            helper.bind(tabuidColumn, typeAssist.getBuid());
                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    typeAssistArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean geoFenceDetailsMaster() throws Exception {
        ArrayList<Geofence>geofenceArrayList=null;
        try {
            geofenceArrayList=new ArrayList<Geofence>();
            String json = readFile(Constants.FILE_GEOFENCE);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                db.execSQL("delete from "+ Geofence_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");
                //System.out.println("status: "+status);
                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                        /*if (respRow != null && respRow.length > 0) {
                            Geofence geofence= new Geofence();
                            geofence.setGfcode(respRow[Constants.GEOFENCE_CODE]);
                            geofence.setGfname(respRow[Constants.GEOFENCE_NAME]);
                            geofence.setGeofence(respRow[Constants.GEOFENCE_GEOFENCE]);
                            geofence.setAlerttopeople(respRow[Constants.GEOFENCE_ALERTTOPEOPLE]);
                            geofence.setAlerttogroup(respRow[Constants.GEOFENCE_ALERTTOGROUP]);
                            geofence.setAlerttext(respRow[Constants.GEOFENCE_ALERTTEXT]);
                            geofence.setEnable(respRow[Constants.GEOFENCE_ENABLE]);
                            geofence.setAlerttoemail(respRow[Constants.GEOFENCE_ALERTOEMAIL]);
                            geofence.setAlerttomobile(respRow[Constants.GEOFENCE_ALERTTOMOBILE]);
                            geofence.setCuser(respRow[Constants.GEOFENCE_CUSER]);
                            geofence.setCdtz(respRow[Constants.GEOFENCE_CDTZ]);
                            geofence.setMuser(respRow[Constants.GEOFENCE_MUSER]);
                            geofence.setMdtz(respRow[Constants.GEOFENCE_MDTZ]);
                            geofenceArrayList.add(geofence);
                        }*/


                    }
                }


                dataObject.put("Data", dataArray);

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

                try {

                    if(geofenceArrayList!=null && geofenceArrayList.size()>0)
                    {

                        for (Geofence geofence: geofenceArrayList) {
                            helper.prepareForReplace();
                            helper.bind(gfidColumn, geofence.getGfid());
                            helper.bind(gfcodeColumn, geofence.getGfcode());
                            helper.bind(gfnameColumn, geofence.getGfname());
                            helper.bind(gfpointsColumn, geofence.getGeofence());
                            helper.bind(gfenableColumn, geofence.getEnable());
                            helper.bind(gfpeopleColumn, geofence.getPeopleid());
                            helper.bind(gffromdateColumn, geofence.getFromdt());
                            helper.bind(gfuptodateColumn, geofence.getUptodt());
                            helper.bind(gfidentifierColumn, geofence.getIdentifier());
                            helper.bind(gfstarttimeColumn, geofence.getStarttime());
                            helper.bind(gfendtimeColumn, geofence.getEndtime());
                            helper.bind(gfbuidColumn, geofence.getBuid());

                            /*System.out.println("GF name: "+geofence.getGfname());
                            System.out.println("GF code: "+geofence.getGfcode());
                            System.out.println("GF points: "+geofence.getGeofence());
                            System.out.println("GF starttime: "+geofence.getStarttime());
                            System.out.println("GF endtime: "+geofence.getEndtime());*/

                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    geofenceArrayList = null;
                }
                return  true;
            }
            else if (status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }


    @Override
    public boolean peopleDetailMaster() throws Exception {
        ArrayList<People>peopleArrayList=null;
        try {
            peopleArrayList=new ArrayList<People>();
            String json = readFile(Constants.FILE_PEOPLE);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                //db.execSQL("delete from "+Geofence_Table.TABLE_NAME);

                db.execSQL("delete from "+ People_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");
                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        System.out.println("respRow.length: "+respRow.length);


                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                //System.out.println(c+" : "+ cols[c].toString()+" : "+respRow[c]);
                                jsonObject.put(cols[c].toString(), respRow[c]);

                            }
                            dataArray.put(jsonObject);
                        }


                        /*if (respRow != null && respRow.length > 0) {
                            People people= new People();
                            people.setPeoplecode(respRow[Constants.PEOPLE_CODE]);
                            people.setLoginid(respRow[Constants.PEOPLE_LOGINID]);
                            people.setPassword(respRow[Constants.PEOPLE_PASSWORD]);
                            people.setLocationtracking(respRow[Constants.PEOPLE_LOCATIONTRACKING]);
                            people.setSitelead(respRow[Constants.PEOPLE_SITELEAD]);
                            people.setSiteincharge(respRow[Constants.PEOPLE_SITEINCHARGE]);
                            people.setPeoplename(respRow[Constants.PEOPLE_NAME]);
                            people.setGender(respRow[Constants.PEOPLE_GENDER]);
                            people.setMobileno(respRow[Constants.PEOPLE_MOBILENO]);
                            people.setEmail(respRow[Constants.PEOPLE_EMAIL]);
                            people.setDepartment(respRow[Constants.PEOPLE_DEPARMENT]);
                            people.setDesignation(respRow[Constants.PEOPLE_DESIGNATION]);
                            people.setPeopletype(respRow[Constants.PEOPLE_TYPE]);
                            people.setSalt(respRow[Constants.PEOPLE_SALT]);
                            people.setFromdt(respRow[Constants.PEOPLE_FROMDATE]);
                            people.setUptodt(respRow[Constants.PEOPLE_UPTODATE]);
                            people.setIntime(respRow[Constants.PEOPLE_INTIME]);
                            people.setOuttime(respRow[Constants.PEOPLE_OUTTIME]);
                            people.setEnable(respRow[Constants.PEOPLE_ENABLE]);
                            people.setDob(respRow[Constants.PEOPLE_DOB]);
                            people.setDoj(respRow[Constants.PEOPLE_DOJ]);
                            people.setReportto(respRow[Constants.PEOPLE_REPORTTO]);
                            people.setCuser(respRow[Constants.PEOPLE_CUSER]);
                            people.setCdtz(respRow[Constants.PEOPLE_CDTZ]);
                            people.setMuser(respRow[Constants.PEOPLE_MUSER]);
                            people.setMdtz(respRow[Constants.PEOPLE_MDTZ]);
                            people.setIsdeleted(respRow[Constants.PEOPLE_ISDELETED]);
                            //people.setGpslocation(respRow[Constants.PEOPLE_GPSLOCATION]);

                           // System.out.println("PeopleCode: "+respRow[Constants.PEOPLE_CODE]);

                            peopleArrayList.add(people);
                        }*/


                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if(peopleArrayList!=null && peopleArrayList.size()>0)
                    {

                        for (People people: peopleArrayList) {
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
                            helper.bind(pCDTZColumn, people.getCdtz());
                            helper.bind(pMuserColumn, people.getMuser());
                            helper.bind(pMDTZColumn, people.getMdtz());
                            //helper.bind(pIsdeletedColumn, people.getIsdeleted());
                            helper.bind(pbuidColumn, people.getBuid());
                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    peopleArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean groupDetailMaster() throws Exception {
        ArrayList<Group>groupArrayList=null;
        try {
            groupArrayList=new ArrayList<Group>();
            String json = readFile(Constants.FILE_GROUP);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                //db.execSQL("delete from "+Geofence_Table.TABLE_NAME);

                db.execSQL("delete from "+ Group_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        //groupcode,groupname,enable,cdtz,mdtz,isdeleted,cuser,muser
                       /* if (respRow != null && respRow.length > 0) {
                            Group group= new Group();
                            group.setGroupcode(respRow[Constants.GROUP_CODE]);
                            group.setGroupname(respRow[Constants.GROUP_NAME]);
                            group.setEnable(respRow[Constants.GROUP_ENABLE]);
                            group.setCdtz(respRow[Constants.GROUP_CDTZ]);
                            group.setMdtz(respRow[Constants.GROUP_MDTZ]);
                            group.setIsdeleted(respRow[Constants.GROUP_ISDELETED]);
                            group.setCuser(respRow[Constants.GROUP_CUSER]);
                            group.setMuser(respRow[Constants.GROUP_MUSER]);

                            groupArrayList.add(group);
                        }*/
                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if(groupArrayList!=null &&  groupArrayList.size()>0)
                    {
                        for (Group group: groupArrayList) {
                            helper.prepareForReplace();
                            helper.bind(gidColumn, group.getGroupid());
                            helper.bind(gNameColumn, group.getGroupname());
                            helper.bind(gEnableColumn, group.getEnable());
                            helper.bind(gCDTZColumn, group.getCdtz());
                            helper.bind(gMDTZColumn, group.getMdtz());
                            //helper.bind(gIsDeletedColumn, group.getIsdeleted());
                            helper.bind(gCuserColumn, group.getCuser());
                            helper.bind(gMuserColumn, group.getMuser());
                            helper.bind(gbuidColumn, group.getBuid());

                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    groupArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean attendanceHistoryMaster() throws Exception {
        ArrayList<AttendanceHistory>attendanceHistoryArrayList=null;
        try {
            attendanceHistoryArrayList=new ArrayList<AttendanceHistory>();
            String json = readFile(Constants.FILE_ATTENDANCE_HISTORY);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                //db.execSQL("delete from "+Geofence_Table.TABLE_NAME);

                db.execSQL("delete from "+ AttendanceHistoy_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();
                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        /*//pelogid,peoplecode,datetime,punchstatus,peventtype,cuser
                        if (respRow != null && respRow.length > 0) {
                            AttendanceHistory attendanceHistory= new AttendanceHistory();
                            attendanceHistory.setPelogid(respRow[Constants.ATTENDANCEHISOTRY_PELOGID]);
                            attendanceHistory.setPeoplecode(respRow[Constants.ATTENDANCEHISOTRY_PEOPLECODE]);
                            attendanceHistory.setDatetime(respRow[Constants.ATTENDANCEHISOTRY_DATETIME]);
                            attendanceHistory.setPunchstatus(respRow[Constants.ATTENDANCEHISOTRY_PUNCHSTATUS]);
                            attendanceHistory.setPeventtype(respRow[Constants.ATTENDANCEHISOTRY_PUNCHTYPE]);
                            attendanceHistory.setCuser(respRow[Constants.ATTENDANCEHISOTRY_CUSER]);
                            attendanceHistoryArrayList.add(attendanceHistory);
                            group.setGroupcode(respRow[Constants.GROUP_CODE]);
                            attendanceHistoryArrayList.add(group);
                        }*/

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

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

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    attendanceHistoryArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean questionMaster() throws Exception {
        ArrayList<Question>questionArrayList=null;
        try {
            questionArrayList=new ArrayList<Question>();
            String json = readFile(Constants.FILE_QUESTIONS);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                db.execSQL("delete from "+ Question_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();
                for (int i = 1; i < (responseSplit.length); i++) {
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                        //"$questioncode$questionname$option$min$max$alerton$cdtz$mdtz$isdeleted$cuser$muser$type$unit" 13
                        /*if (respRow != null && respRow.length > 0) {
                            Question question= new Question();
                            question.setQuestioncode(respRow[Constants.QUESTION_CODE]);
                            question.setQuestionname(respRow[Constants.QUESTION_NAME]);
                            question.setOptions(respRow[Constants.QUESTION_OPTIONS]);
                            question.setMin(respRow[Constants.QUESTION_MIN]);
                            question.setMax(respRow[Constants.QUESTION_MAX]);
                            question.setAlertOn(respRow[Constants.QUESTION_ALERTON]);
                            question.setCdtz(respRow[Constants.QUESTION_CDTZ]);
                            question.setMdtz(respRow[Constants.QUESTION_MDTZ]);
                            question.setIsDeleted(respRow[Constants.QUESTION_ISDELETED]);
                            question.setCuser(respRow[Constants.QUESTION_CUSER]);
                            question.setMuser(respRow[Constants.QUESTION_MUSER]);
                            question.setType(respRow[Constants.QUESTION_TYPE]);
                            question.setUnit(respRow[Constants.QUESTION_UNIT]);
                            questionArrayList.add(question);

                        }*/


                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if(questionArrayList!=null && questionArrayList.size()>0)
                    {


                        for (Question question: questionArrayList) {
                            helper.prepareForReplace();
                            //System.out.println("Quset Code: "+question.getQuestionid());
                            helper.bind(qCodeColumn, question.getQuestionid());
                            helper.bind(qNameColumn, question.getQuestionname());
                            helper.bind(qOptionsColumn, question.getOptions());
                            helper.bind(qMinColumn, question.getMin());
                            helper.bind(qMaxColumn, question.getMax());
                            helper.bind(qAlertonColumn, question.getAlertOn());
                            helper.bind(qCdtzColumn, question.getCdtz());
                            helper.bind(qMdtzColumn, question.getMdtz());
                            //helper.bind(qIsdeletedColumn, question.getIsDeleted());
                            helper.bind(qCuserColumn, question.getCuser());
                            helper.bind(qMuserColumn, question.getMuser());
                            helper.bind(qTypeColumn, question.getType());
                            helper.bind(qUnitColumn, question.getUnit());
                            helper.bind(qBuidColumn, question.getBuid());

                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    questionArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean questionSetMaster() throws Exception {
        ArrayList<QuestionSet>questionSetArrayList=null;
        try {
            questionSetArrayList=new ArrayList<QuestionSet>();
            String json = readFile(Constants.FILE_QUESTION_SET);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                db.execSQL("delete from "+ QuestionSet_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        /*//"$qsetcode$qsetname$enable$seqno$cdtz$mdtz$isdeleted$cuser$muser$parent$type" 11
                        if (respRow != null && respRow.length > 0) {
                            QuestionSet questionSet= new QuestionSet();
                            questionSet.setQsetcode(respRow[Constants.QUESTIONSET_CODE]);
                            questionSet.setQsetname(respRow[Constants.QUESTIONSET_NAME]);
                            questionSet.setEnable(respRow[Constants.QUESTIONSET_ENABLE]);
                            questionSet.setSeqno(respRow[Constants.QUESTIONSET_SEQNO]);
                            questionSet.setCdtz(respRow[Constants.QUESTIONSET_CDTZ]);
                            questionSet.setMdtz(respRow[Constants.QUESTIONSET_MDTZ]);
                            questionSet.setIsdeleted(respRow[Constants.QUESTIONSET_ISDELETED]);
                            questionSet.setCuser(respRow[Constants.QUESTIONSET_CUSER]);
                            questionSet.setMuser(respRow[Constants.QUESTIONSET_MUSER]);
                            questionSet.setParent(respRow[Constants.QUESTIONSET_PARENT]);
                            questionSet.setType(respRow[Constants.QUESTIONSET_TYPE]);
                            questionSetArrayList.add(questionSet);

                        }*/

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }


                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if( questionSetArrayList!=null &&  questionSetArrayList.size()>0)
                    {
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
                            helper.bind(qsCdtzColumn, questionSet.getCdtz());
                            helper.bind(qsMdtzColumn, questionSet.getMdtz());
                            //helper.bind(qsIsdeletedColumn, questionSet.getIsdeleted());
                            helper.bind(qsCuserColumn, questionSet.getCuser());
                            helper.bind(qsMuserColumn, questionSet.getMuser());
                            helper.bind(qsParentColumn, questionSet.getParent());
                            helper.bind(qsTypeColumn, questionSet.getType());
                            helper.bind(qsBuidColumn, questionSet.getBuid());

                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    questionSetArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean questionSetBelongingMaster() throws Exception {
        ArrayList<QuestionSetBelonging>questionSetBelongingArrayList=null;
        try {
            questionSetBelongingArrayList=new ArrayList<QuestionSetBelonging>();
            String json = readFile(Constants.FILE_QUESTION_SET_BELONGING);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                db.execSQL("delete from "+ QuestionSetBelonging_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }


                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                        //"$qsbid$ismandatory$seqno$cdtz$mdtz$isdeleted$cuser$muser$qsetcode$questioncode" 10
                       /* if (respRow != null && respRow.length > 0) {
                            QuestionSetBelonging questionSetBelonging= new QuestionSetBelonging();
                            questionSetBelonging.setQsbid(respRow[Constants.QUESTIONSETBELONGING_ID]);
                            questionSetBelonging.setIsmandatory(respRow[Constants.QUESTIONSETBELONGING_ISMANDATORY]);
                            questionSetBelonging.setSeqno(respRow[Constants.QUESTIONSETBELONGING_SEQNO]);
                            questionSetBelonging.setCdtz(respRow[Constants.QUESTIONSETBELONGING_CDTZ]);
                            questionSetBelonging.setMdtz(respRow[Constants.QUESTIONSETBELONGING_MDTZ]);
                            questionSetBelonging.setIsdeleted(respRow[Constants.QUESTIONSETBELONGING_ISDELETED]);
                            questionSetBelonging.setCuser(respRow[Constants.QUESTIONSETBELONGING_CUSER]);
                            questionSetBelonging.setMuser(respRow[Constants.QUESTIONSETBELONGING_MUSER]);
                            questionSetBelonging.setQsetcode(respRow[Constants.QUESTIONSETBELONGING_QSETCODE]);
                            questionSetBelonging.setQuestioncode(respRow[Constants.QUESTIONSETBELONGING_QUESTIONCODE]);
                            questionSetBelongingArrayList.add(questionSetBelonging);

                        }*/


                    }
                }

                dataObject.put("Data", dataArray);
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

                try {

                    if(questionSetBelongingArrayList!=null && questionSetBelongingArrayList.size()>0)
                    {


                        for (QuestionSetBelonging questionSetBelonging: questionSetBelongingArrayList) {
                            helper.prepareForReplace();

                            helper.bind(qsbIDColumn, questionSetBelonging.getQsbid());
                            helper.bind(qsbIsMandatoryColumn, questionSetBelonging.getIsmandatory());
                            helper.bind(qsbSeqnoColumn, questionSetBelonging.getSeqno());
                            helper.bind(qsbCdtzColumn, questionSetBelonging.getCdtz());
                            helper.bind(qsbMdtzColumn, questionSetBelonging.getMdtz());
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

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    questionSetBelongingArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean peopleGroupBelongingMaster() throws Exception {
        ArrayList<PeopleGroupBelonging>peopleGroupBelongingArrayList=null;
        try {
            peopleGroupBelongingArrayList=new ArrayList<PeopleGroupBelonging>();
            String json = readFile(Constants.FILE_PEOPLE_GROUP_BELONGING);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                //db.execSQL("delete from "+Geofence_Table.TABLE_NAME);

                db.execSQL("delete from "+ PeopleGroupBelongin_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }

                         //@pgbid@isgrouplead@cdtz@mdtz@isdeleted@cuser@groupcode@muser@peoplecode
                        /*if (respRow != null && respRow.length > 0) {
                            PeopleGroupBelonging peopleGroupBelonging= new PeopleGroupBelonging();
                            peopleGroupBelonging.setPgbid(respRow[Constants.PEOPLE_GROUP_BELONGING_ID]);
                            peopleGroupBelonging.setIsgrouplead(respRow[Constants.PEOPLE_GROUP_BELONGING_ISGROUPLEAD]);
                            peopleGroupBelonging.setCdtz(respRow[Constants.PEOPLE_GROUP_BELONGING_CDTZ]);
                            peopleGroupBelonging.setMdtz(respRow[Constants.PEOPLE_GROUP_BELONGING_MDTZ]);
                            peopleGroupBelonging.setIsdeleted(respRow[Constants.PEOPLE_GROUP_BELONGING_ISDELETED]);
                            peopleGroupBelonging.setCuser(respRow[Constants.PEOPLE_GROUP_BELONGING_CUSER]);
                            peopleGroupBelonging.setGroupcode(respRow[Constants.PEOPLE_GROUP_BELONGING_GROUPCODE]);
                            peopleGroupBelonging.setMuser(respRow[Constants.PEOPLE_GROUP_BELONGING_MUSER]);
                            peopleGroupBelonging.setPeoplecode(respRow[Constants.PEOPLE_GROUP_BELONGING_PEOPLECODE]);

                            peopleGroupBelongingArrayList.add(peopleGroupBelonging);
                        }*/


                    }
                }

                dataObject.put("Data", dataArray);

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

                try {

                    if(peopleGroupBelongingArrayList!=null && peopleGroupBelongingArrayList.size()>0)
                    {
                        for (PeopleGroupBelonging peopleGroupBelonging: peopleGroupBelongingArrayList) {
                            helper.prepareForReplace();
                            helper.bind(pgbIDColumn, peopleGroupBelonging.getPgbid());
                            helper.bind(pgbIsgroupleadColumn, peopleGroupBelonging.getIsgrouplead());
                            helper.bind(pgbCdtzColumn, peopleGroupBelonging.getCdtz());
                            helper.bind(pgbMdtzColumn, peopleGroupBelonging.getMdtz());
                            //helper.bind(pgbIsdeletedColumn, peopleGroupBelonging.getIsdeleted());
                            helper.bind(pgbCuserColumn, peopleGroupBelonging.getCuser());
                            helper.bind(pgbGroupcodeColumn, peopleGroupBelonging.getGroupid());
                            helper.bind(pgbMuserColumn, peopleGroupBelonging.getMuser());
                            helper.bind(pgbPeoplecodeColumn, peopleGroupBelonging.getPeopleid());
                            helper.bind(pgbBuidColumn, peopleGroupBelonging.getBuid());

                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    peopleGroupBelongingArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @Override
    public boolean siteMaster() throws Exception {
        ArrayList<Sites>sitesArrayList=null;
        try {
            sitesArrayList=new ArrayList<Sites>();
            String json = readFile(Constants.FILE_SITES);
            JSONObject ob = new JSONObject(json);
            int status = ob.getInt("rc");
            int totalRows = ob.getInt("nrow");
            //System.out.println("Status: "+status);
            if(status==0 && totalRows>0)
            {
                //db.execSQL("delete from "+Geofence_Table.TABLE_NAME);

                db.execSQL("delete from "+ Sites_Table.TABLE_NAME);

                String resp=ob.getString("row_data");
                String colums=ob.getString("columns");

                String mainSplitRowChar=String.valueOf(resp.charAt(0));
                String mainSplitColumnChar=String.valueOf(colums.charAt(0));

                if(mainSplitRowChar.toString().trim().equalsIgnoreCase("|"))
                {
                    mainSplitRowChar="\\|";
                }
                else if(mainSplitRowChar.toString().trim().equalsIgnoreCase("$"))
                {
                    mainSplitRowChar="\\$";
                }

                if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                    mainSplitColumnChar = "\\|";
                } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                    mainSplitColumnChar = "\\$";
                }

                String[] responseSplit = resp.split(mainSplitRowChar);
                String[] cols=colums.split(mainSplitColumnChar);

                JSONArray dataArray = new JSONArray();
                JSONObject dataObject = new JSONObject();

                for (int i = 1; i < (responseSplit.length); i++) {
                    //System.out.println("split string: " + responseSplit[i].toString());
                    //System.out.println("split string number: "+i);
                    if (responseSplit[i].toString().trim().length() > 0) {
                        Character startDelimitor = responseSplit[i].charAt(0);
                        //System.out.println("Start Delimeter: " + startDelimitor);
                        String[] respRow = null;
                        if (startDelimitor.toString().equalsIgnoreCase("$")) {
                            respRow = responseSplit[i].toString().trim().split("\\$");
                        } else if(startDelimitor.toString().equalsIgnoreCase("|"))
                        {
                            respRow = responseSplit[i].toString().trim().split("\\|");
                        }
                        else {
                            respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                        }

                        if(respRow!=null && respRow.length>0) {
                            JSONObject jsonObject = new JSONObject();
                            for (int c = 1; c < respRow.length; c++) {
                                jsonObject.put(cols[c].toString(), respRow[c]);
                            }
                            dataArray.put(jsonObject);
                        }


                    }
                }

                dataObject.put("Data", dataArray);
                //"@sitepeopleid@fromdt@uptodt@siteowner@buid@peopleid@reportto@shift@slno@postingrev@contractid@cuser@muser@cdtz@mdtz@isdeleted@worktype"

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

                JSONArray data = dataObject.getJSONArray("Data");
                listType = new TypeToken<ArrayList<Sites>>() {
                }.getType();
                sitesArrayList = gson.fromJson(data.toString(), listType);

                try {

                    if(sitesArrayList!=null && sitesArrayList.size()>0)
                    {
                        for (Sites sites: sitesArrayList) {
                            helper.prepareForReplace();
                            helper.bind(sitepeopleidColumn, sites.getSitepeopleid());
                            helper.bind(fromdtColumn, sites.getFromdt());
                            helper.bind(uptodtColumn, sites.getUptodt());
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
                            helper.bind(cdtzColumn, sites.getCdtz());
                            helper.bind(mdtzColumn, sites.getMdtz());
                            //helper.bind(isdeletedColumn, sites.getIsdeleted());
                            helper.bind(worktypeColumn, sites.getWorktype());
                            helper.bind(bu_bunameColumn, sites.getBuname());
                            helper.bind(bu_bucodeColumn, sites.getBucode());

                            helper.execute();
                        }

                    }

                } catch (Exception e) {
                    throw e;
                    //return true;
                } finally {
                    helper.close();
                    helper = null;
                    sitesArrayList = null;
                }
                return  true;
            }
            else if(status==0 && totalRows==0)
            {
                return true;
            }
            else
                throw new Exception("Status not 200");
        }
        catch (Exception e)
        {
            throw e;
        }
    }


}
