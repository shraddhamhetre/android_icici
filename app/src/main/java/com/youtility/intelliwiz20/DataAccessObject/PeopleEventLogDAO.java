package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Tables.PeopleEventLog_Table;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * people event log data access object
 */
//accuracy, deviceid, datetime, gpslocation, photorecognitionthreshold,photorecognitionscore, " +
//"photorecognitiontimestamp, photorecognitionserviceresponse,facerecognition, cdtz, mdtz, isdeleted, cuser, muser, peoplecode, peventtype, punchstatus, verifiedby

public class PeopleEventLogDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;

    public PeopleEventLogDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }


    public void insertRecord(PeopleEventLog regRecord)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(PeopleEventLog_Table.PE_SYNCSTATUS, "0");
            values.put(PeopleEventLog_Table.PE_LOGID,regRecord.getPelogid());
            values.put(PeopleEventLog_Table.PE_ACCURACY, regRecord.getAccuracy());
            values.put(PeopleEventLog_Table.PE_DEVICEID, regRecord.getDeviceid());
            values.put(PeopleEventLog_Table.PE_DATETIME, regRecord.getDatetime());
            values.put(PeopleEventLog_Table.PE_GPSLOCATION, regRecord.getGpslocation());
            values.put(PeopleEventLog_Table.PE_PR_THRESHOLD, regRecord.getPhotorecognitionthreshold());
            values.put(PeopleEventLog_Table.PE_PR_SCORE, regRecord.getPhotorecognitionscore());
            values.put(PeopleEventLog_Table.PE_PR_TIMESTAMP, regRecord.getPhotorecognitiontimestamp());
            values.put(PeopleEventLog_Table.PE_PR_RESPONSE, regRecord.getPhotorecognitionserviceresponse());
            values.put(PeopleEventLog_Table.PE_FACEREGONITION, regRecord.getFacerecognition());
            //values.put(PeopleEventLog_Table.PE_ISDELETED, regRecord.getIsdeleted());
            values.put(PeopleEventLog_Table.PE_PEOPLEID, regRecord.getPeopleid());
            values.put(PeopleEventLog_Table.PE_TYPE, regRecord.getPeventtype());

            values.put(PeopleEventLog_Table.PE_PUNCHSTATUS, regRecord.getPunchstatus());
            values.put(PeopleEventLog_Table.PE_VARIFIEDBY, regRecord.getVerifiedby());
            values.put(PeopleEventLog_Table.PE_CDTZ, regRecord.getCdtz());
            values.put(PeopleEventLog_Table.PE_MDTZ, regRecord.getMdtz());
            values.put(PeopleEventLog_Table.PE_CUSER, regRecord.getCuser());
            values.put(PeopleEventLog_Table.PE_MUSER, regRecord.getMuser());
            values.put(PeopleEventLog_Table.PE_SCAN_PEOPLECODE, regRecord.getScanPeopleCode());
            values.put(PeopleEventLog_Table.PE_BUID, regRecord.getBuid());
            values.put(PeopleEventLog_Table.PE_GFID, regRecord.getGfid());

            values.put(PeopleEventLog_Table.PE_DISTANCE, regRecord.getDistance());
            values.put(PeopleEventLog_Table.PE_DURATION, regRecord.getDuration());
            values.put(PeopleEventLog_Table.PE_EXPENCES, regRecord.getExpamt());
            values.put(PeopleEventLog_Table.PE_REFERENCE, regRecord.getReference());
            values.put(PeopleEventLog_Table.PE_REMARKS, regRecord.getRemarks());
            values.put(PeopleEventLog_Table.PE_TRANSPORTMODE, regRecord.getTransportmode());
            values.put(PeopleEventLog_Table.PE_OTHERLOCATION, regRecord.getOtherlocation());

            if(isRecordExists(regRecord)) {
                long val = db.insert(PeopleEventLog_Table.TABLE_NAME, "", values);
                System.out.println("insertPeopleEventLogRecord People event log inserted val: " + val + " : " + regRecord.getPeventtype()+" : "+regRecord.getPunchstatus());
            }
            else
                System.out.println("insertPeopleEventLogRecord People event log already existed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }



//String punchStatus, String empCode, long inoutTimestamp, long siteID, String siteName
    private boolean isRecordExists(PeopleEventLog peopleEventLog)
    {
        Cursor cursor=null;
        boolean retVal=true;
        try {
            db = sqlopenHelper.getReadableDatabase();
            cursor=db.rawQuery("select * from "+PeopleEventLog_Table.TABLE_NAME + " where "+
                                PeopleEventLog_Table.PE_PUNCHSTATUS+" = "+peopleEventLog.getPunchstatus()+" AND "+
                                PeopleEventLog_Table.PE_PEOPLEID+" = "+peopleEventLog.getPeopleid()+" AND "+
                                PeopleEventLog_Table.PE_DATETIME+" ='"+peopleEventLog.getDatetime()+"' AND "+
                                PeopleEventLog_Table.PE_BUID+" = "+peopleEventLog.getBuid(),null);
            if(cursor.getCount()<=0)
            {
                cursor.close();
                retVal=false;
            }
            cursor.close();
            retVal=true;

        }catch (Exception e)
        {

        }
        return retVal;
    }

    /*public void getCount()
    {
        String userName=null;
        Cursor c = null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + AssetDetail_Table.TABLE_NAME ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("Count: "+c.getInt(0));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {

        }
    }
*/

    //accuracy, deviceid, datetime, gpslocation, photorecognitionthreshold,photorecognitionscore, " +
//"photorecognitiontimestamp, photorecognitionserviceresponse,facerecognition, cdtz, mdtz, isdeleted, cuser, muser, peoplecode, peventtype, punchstatus, verifiedby

    public ArrayList<PeopleEventLog> getEvents()
    {
        ArrayList<PeopleEventLog> eventLogs=null;

        //accuracy, datetime, gpslocation, photorecognitionthreshold, photorecognitionscore, photorecognitiontimestamp, " +
        //"photorecognitionserviceresponse, facerecognition, peopleid, peventtype, punchstatus, verifiedby, buid, cuser, muser, cdtz, mdtz,
        // gfid, deviceid, transportmode, expamt, duration, reference, remarks, distance
        try {
            db = sqlopenHelper.getReadableDatabase();
            //c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME+" where "+PeopleEventLog_Table.PE_SYNCSTATUS+" ='0' limit 1" ,null);
            c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME+" where "+PeopleEventLog_Table.PE_SYNCSTATUS+" ='0'" ,null);
            eventLogs=new ArrayList<PeopleEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        PeopleEventLog eventLog=new PeopleEventLog();
                        eventLog.setPelogid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_LOGID)));
                        eventLog.setAccuracy(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_ACCURACY)));
                        //eventLog.setDeviceid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_DEVICEID)));
                        eventLog.setDeviceid(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DEVICEID)));

                        eventLog.setDatetime(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DATETIME)));
                        eventLog.setGpslocation(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_GPSLOCATION)));
                        eventLog.setPhotorecognitionthreshold(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_PR_THRESHOLD)));
                        eventLog.setPhotorecognitionscore(c.getDouble(c.getColumnIndex(PeopleEventLog_Table.PE_PR_SCORE)));
                        eventLog.setPhotorecognitiontimestamp(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_PR_TIMESTAMP)));
                        eventLog.setPhotorecognitionserviceresponse(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_PR_RESPONSE)));
                        eventLog.setFacerecognition(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_FACEREGONITION)));
                        eventLog.setCdtz(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_CDTZ)));
                        eventLog.setMdtz(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_MDTZ)));
                        eventLog.setCuser(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_CUSER)));
                        eventLog.setMuser(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_MUSER)));
                        eventLog.setPeopleid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PEOPLEID)));
                        eventLog.setPeventtype(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_TYPE)));
                        eventLog.setPunchstatus(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PUNCHSTATUS)));
                        eventLog.setVerifiedby(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_VARIFIEDBY)));
                        eventLog.setScanPeopleCode(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_SCAN_PEOPLECODE)));
                        eventLog.setBuid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_BUID)));
                        eventLog.setGfid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_GFID)));
                        eventLog.setTransportmode(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_TRANSPORTMODE)));
                        eventLog.setExpamt(c.getDouble(c.getColumnIndex(PeopleEventLog_Table.PE_EXPENCES)));
                        eventLog.setDuration(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_DURATION)));
                        eventLog.setReference(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_REFERENCE)));
                        eventLog.setRemarks(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_REMARKS)));
                        eventLog.setDistance(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_DISTANCE)));
                        eventLog.setOtherlocation(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_OTHERLOCATION)));
                        eventLogs.add(eventLog);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return eventLogs;
    }

    public ArrayList<PeopleEventLog> getConveyanceLog()
    {
        ArrayList<PeopleEventLog> eventLogs=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            /*c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME
                    +" where "+PeopleEventLog_Table.PE_TYPE+" in(select taid from TypeAssist where tacode = 'CONVEYANCE')"
                    +" order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") ASC",null);*/


            //select * from peopleeventlog where reference in ( select reference from peopleeventlog where peventtype=90)
            c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME
                    +" where "+PeopleEventLog_Table.PE_REFERENCE+" in(select "+PeopleEventLog_Table.PE_REFERENCE+" from "+PeopleEventLog_Table.TABLE_NAME+" where "+PeopleEventLog_Table.PE_TYPE+"= (select taid from TypeAssist where tacode = 'CONVEYANCE'))"
                    +" order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") DESC",null);
            eventLogs=new ArrayList<PeopleEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        PeopleEventLog eventLog=new PeopleEventLog();
                        eventLog.setRemarks(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_REMARKS)));
                        eventLog.setReference(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_REFERENCE)));
                        eventLog.setPunchstatus(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PUNCHSTATUS)));
                        eventLog.setDatetime(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DATETIME)));

                        eventLog.setDistance(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_DISTANCE)));
                        eventLog.setDuration(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_DURATION)));
                        eventLog.setExpamt(c.getDouble(c.getColumnIndex(PeopleEventLog_Table.PE_EXPENCES)));

                        eventLogs.add(eventLog);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        System.out.println("event"+ eventLogs);
        return eventLogs;
    }


    public ArrayList<PeopleEventLog> getSosLog()
    {
        ArrayList<PeopleEventLog> eventLogs=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            /*c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME
                    +" where "+PeopleEventLog_Table.PE_TYPE+" in(select taid from TypeAssist where tacode = 'CONVEYANCE')"
                    +" order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") ASC",null);*/


            //select * from peopleeventlog where reference in ( select reference from peopleeventlog where peventtype=90)
            c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME
                    +" where "+PeopleEventLog_Table.PE_TYPE+" in(select taid from TypeAssist where tacode = 'CONVEYANCE')"
                    +" order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") ASC",null);
            eventLogs=new ArrayList<PeopleEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        PeopleEventLog eventLog=new PeopleEventLog();
                        eventLog.setRemarks(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_CUSER)));
                        eventLog.setReference(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_LOGID)));
                        eventLog.setDatetime(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DATETIME)));
                        eventLog.setDistance(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_GPSLOCATION)));
                        eventLog.setDuration(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_BUID)));

                        eventLogs.add(eventLog);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        System.out.println("event"+ eventLogs);
        return eventLogs;
    }

    public ArrayList<PeopleEventLog> getSiteVisitEventLogs()
    {
        ArrayList<PeopleEventLog> eventLogs=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME
                        +" where "+PeopleEventLog_Table.PE_TYPE+" in(select taid from TypeAssist where tacode = 'AUDIT')"
                        +" order by strftime('%s' ,"+ PeopleEventLog_Table.PE_DATETIME +") ASC",null);
            eventLogs=new ArrayList<PeopleEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        PeopleEventLog eventLog=new PeopleEventLog();
                        eventLog.setAccuracy(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_ACCURACY)));
                        //eventLog.setDeviceid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_DEVICEID)));
                        eventLog.setDeviceid(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DEVICEID)));

                        eventLog.setDatetime(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DATETIME)));
                        eventLog.setGpslocation(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_GPSLOCATION)));
                        eventLog.setPhotorecognitionthreshold(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_PR_THRESHOLD)));
                        eventLog.setPhotorecognitionscore(c.getDouble(c.getColumnIndex(PeopleEventLog_Table.PE_PR_SCORE)));
                        eventLog.setPhotorecognitiontimestamp(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_PR_TIMESTAMP)));
                        eventLog.setPhotorecognitionserviceresponse(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_PR_RESPONSE)));
                        eventLog.setFacerecognition(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_FACEREGONITION)));
                        eventLog.setCdtz(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_CDTZ)));
                        eventLog.setMdtz(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_MDTZ)));
                        eventLog.setCuser(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_CUSER)));
                        eventLog.setMuser(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_MUSER)));
                        eventLog.setPeopleid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PEOPLEID)));
                        eventLog.setPeventtype(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_TYPE)));
                        eventLog.setPunchstatus(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PUNCHSTATUS)));
                        eventLog.setVerifiedby(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_VARIFIEDBY)));
                        eventLog.setScanPeopleCode(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_SCAN_PEOPLECODE)));
                        eventLog.setBuid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_BUID)));
                        eventLog.setGfid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_GFID)));
                        eventLogs.add(eventLog);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return eventLogs;
    }

    public PeopleEventLog getEventLog(long pelogid)
    {
        PeopleEventLog eventLog=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + PeopleEventLog_Table.TABLE_NAME
                    +" where "+PeopleEventLog_Table.PE_LOGID+" = "+pelogid,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    eventLog=new PeopleEventLog();
                    eventLog.setPelogid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_LOGID)));
                    eventLog.setAccuracy(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_ACCURACY)));
                    //eventLog.setDeviceid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_DEVICEID)));
                    eventLog.setDeviceid(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DEVICEID)));

                    eventLog.setDatetime(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_DATETIME)));
                    eventLog.setGpslocation(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_GPSLOCATION)));
                    eventLog.setPhotorecognitionthreshold(c.getInt(c.getColumnIndex(PeopleEventLog_Table.PE_PR_THRESHOLD)));
                    eventLog.setPhotorecognitionscore(c.getDouble(c.getColumnIndex(PeopleEventLog_Table.PE_PR_SCORE)));
                    eventLog.setPhotorecognitiontimestamp(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_PR_TIMESTAMP)));
                    eventLog.setPhotorecognitionserviceresponse(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_PR_RESPONSE)));
                    eventLog.setFacerecognition(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_FACEREGONITION)));
                    eventLog.setCdtz(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_CDTZ)));
                    eventLog.setMdtz(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_MDTZ)));
                    eventLog.setCuser(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_CUSER)));
                    eventLog.setMuser(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_MUSER)));
                    eventLog.setPeopleid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PEOPLEID)));
                    eventLog.setPeventtype(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_TYPE)));
                    eventLog.setPunchstatus(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_PUNCHSTATUS)));
                    eventLog.setVerifiedby(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_VARIFIEDBY)));
                    eventLog.setScanPeopleCode(c.getString(c.getColumnIndex(PeopleEventLog_Table.PE_SCAN_PEOPLECODE)));
                    eventLog.setBuid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_BUID)));
                    eventLog.setGfid(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_GFID)));
                    eventLog.setTransportmode(c.getLong(c.getColumnIndex(PeopleEventLog_Table.PE_TRANSPORTMODE)));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return eventLog;
    }


    public int changeSyncStatus(String peDateTime) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PeopleEventLog_Table.PE_SYNCSTATUS, Constants.SYNC_STATUS_ONE);
            return db.update(PeopleEventLog_Table.TABLE_NAME, values, "datetime=?", new String[] { peDateTime });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }


    public void deletRecords()
    {
        db = sqlopenHelper.getReadableDatabase();
        db.execSQL("delete from "+ PeopleEventLog_Table.TABLE_NAME);
        //c = db.rawQuery("delete from " + SitesVisitedLog_Table.TABLE_NAME ,null);
    }

    public int changeSOSSyncStatus(long cTimestamp, long returnId, String peDateTime) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PeopleEventLog_Table.PE_SYNCSTATUS, Constants.SYNC_STATUS_ONE);
            values.put(PeopleEventLog_Table.PE_LOGID, returnId);
            return db.update(PeopleEventLog_Table.TABLE_NAME, values, "datetime=?", new String[] { peDateTime });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }
}
