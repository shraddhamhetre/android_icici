package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Tables.DeviceEventLog_Table;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * Device event log data access object
 *
 */
//deviceid, eventvalue, gpslocation, accuracy, altitude, batterylevel, signalstrength, availextmemory, availintmemory,
// cdtz, mdtz, isdeleted, cuser, eventtype, muser, peoplecode, signalbandwidth
public class DeviceEventLogDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;

    public DeviceEventLogDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }

    public void insertRecord(DeviceEventLog regRecord, String status)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(DeviceEventLog_Table.DEVICEEVENT_LOGID,regRecord.getDeviceeventlogid());
            values.put(DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS, status);
            values.put(DeviceEventLog_Table.DEVICEEVENT_ACCURACY, regRecord.getAccuracy());
            values.put(DeviceEventLog_Table.DEVICEEVENT_IMEI, regRecord.getDeviceid());
            values.put(DeviceEventLog_Table.DEVICEEVENT_ALTITUDE, regRecord.getAltitude());
            values.put(DeviceEventLog_Table.DEVICEEVENT_GPS_LOCATION, regRecord.getGpslocation());
            values.put(DeviceEventLog_Table.DEVICEEVENT_BATTERYLEVEL, regRecord.getBatterylevel());
            values.put(DeviceEventLog_Table.DEVICEEVENT_SIGNALSTRENGTH, regRecord.getSignalstrength());
            values.put(DeviceEventLog_Table.DEVICEEVENT_AVAILEXTERNALMEMORY, regRecord.getAvailextmemory());
            values.put(DeviceEventLog_Table.DEVICEEVENT_AVAILINTERNALMEMORY, regRecord.getAvailintmemory());
            values.put(DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE, regRecord.getEventtype());
            //values.put(DeviceEventLog_Table.DEVICEEVENT_ISDELETED, regRecord.getIsdeleted());
            values.put(DeviceEventLog_Table.DEVICEEVENT_PEOPLEID, regRecord.getPeopleid());
            values.put(DeviceEventLog_Table.DEVICEEVENT_EVENTVALUE, regRecord.getEventvalue());
            values.put(DeviceEventLog_Table.DEVICEEVENT_BADNWIDTHSIGNAL, regRecord.getSignalbandwidth());
            values.put(DeviceEventLog_Table.DEVICEEVENT_CDTZ, regRecord.getCdtz());
            values.put(DeviceEventLog_Table.DEVICEEVENT_MDTZ, regRecord.getMdtz());
            values.put(DeviceEventLog_Table.DEVICEEVENT_CUSER, regRecord.getCuser());
            values.put(DeviceEventLog_Table.DEVICEEVENT_MUSER, regRecord.getMuser());
            values.put(DeviceEventLog_Table.DEVICEEVENT_BUID, regRecord.getBuid());
            values.put(DeviceEventLog_Table.DEVICEEVENT_APPLICATION_VERSION, regRecord.getApplicationversion());
            values.put(DeviceEventLog_Table.DEVICEEVENT_ANDROID_VERSION, regRecord.getAndroidosversion());
            values.put(DeviceEventLog_Table.DEVICEEVENT_MODEL_NAME, regRecord.getModelname());
            values.put(DeviceEventLog_Table.DEVICEEVENT_INSTALLED_APPS, regRecord.getInstalledapps());
            values.put(DeviceEventLog_Table.DEVICEEVENT_SIM_NUMBER, regRecord.getSimserialnumber());
            values.put(DeviceEventLog_Table.DEVICEEVENT_LINE_NUMBER, regRecord.getLinenumber());
            values.put(DeviceEventLog_Table.DEVICEEVENT_NETWORK_PROVIDER_NAME, regRecord.getNetworkprovidername());
            values.put(DeviceEventLog_Table.DEVICEEVENT_STEP_COUNT,regRecord.getStepCount());
            long val= db.insert(DeviceEventLog_Table.TABLE_NAME, "", values);
            //System.out.println("DeviceEventLog inserted: "+val+" : "+regRecord.getEventvalue()+":"+regRecord.getGpslocation()+":"+regRecord.getAndroidosversion()+":"+regRecord.getModelname());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
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
//deviceid, eventvalue, gpslocation, accuracy, altitude, batterylevel, signalstrength, availextmemory, availintmemory,
// cdtz, mdtz, isdeleted, cuser, eventtype, muser, peoplecode, signalbandwidth
    public ArrayList<DeviceEventLog> getUnsyncDeviceEvents(long siteID)
    {
        ArrayList<DeviceEventLog> eventLogs=null;
        Cursor c = null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            //c = db.rawQuery("Select * from " + DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS+" ='0' limit 1" ,null);
            c = db.rawQuery("Select * from " + DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS+" ='0' AND "
                            +DeviceEventLog_Table.DEVICEEVENT_BUID+" = "+siteID+" AND "
                            +DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE+ " in(select taid from TypeAssist where (tacode = 'TRACKING' OR tacode = 'CAPTCHA') AND tatype ='Event Type')",null);
            eventLogs=new ArrayList<DeviceEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        DeviceEventLog eventLog=new DeviceEventLog();
                        eventLog.setAccuracy(c.getInt(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ACCURACY)));
                        eventLog.setDeviceid(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_IMEI)));
                        eventLog.setEventvalue(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_EVENTVALUE)));
                        eventLog.setGpslocation(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_GPS_LOCATION)));
                        eventLog.setAltitude(c.getDouble(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ALTITUDE)));
                        eventLog.setBatterylevel(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BATTERYLEVEL)));
                        eventLog.setSignalstrength(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_SIGNALSTRENGTH)));
                        eventLog.setAvailextmemory(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_AVAILEXTERNALMEMORY)));
                        eventLog.setAvailintmemory(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_AVAILINTERNALMEMORY)));
                        eventLog.setCdtz(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_CDTZ)));
                        eventLog.setMdtz(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MDTZ)));
                        eventLog.setCuser(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_CUSER)));
                        eventLog.setMuser(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MUSER)));
                        eventLog.setPeopleid(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_PEOPLEID)));
                       // eventLog.setIsdeleted(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ISDELETED)));
                        eventLog.setEventtype(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE)));
                        eventLog.setSignalbandwidth(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BADNWIDTHSIGNAL)));
                        eventLog.setBuid(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BUID)));
                        eventLog.setApplicationversion(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_APPLICATION_VERSION)));
                        eventLog.setAndroidosversion(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ANDROID_VERSION)));
                        eventLog.setModelname(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MODEL_NAME)));
                        eventLog.setInstalledapps(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_INSTALLED_APPS)));

                        eventLog.setSimserialnumber(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_SIM_NUMBER)));
                        eventLog.setLinenumber(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_LINE_NUMBER)));
                        eventLog.setNetworkprovidername(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_NETWORK_PROVIDER_NAME)));
                        eventLog.setStepCount(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_STEP_COUNT)));

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

        }
        return eventLogs;
    }

    public ArrayList<DeviceEventLog> getUnsyncDeviceEventsLogs(long siteID)
    {
        ArrayList<DeviceEventLog> eventLogs=null;
        Cursor c = null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            //c = db.rawQuery("Select * from " + DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS+" ='0' limit 1" ,null);
            c = db.rawQuery("Select * from " + DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS+" ='0' AND "
                    +DeviceEventLog_Table.DEVICEEVENT_BUID+" = "+siteID+" AND "
                    +DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE+ " in(select taid from TypeAssist " +
                    "where (tacode = 'GPSSWITCHEDON' OR tacode = 'GPSSWITCHEDOFF' OR tacode = 'AIRPLANEMODEOFF' OR tacode = 'AIRPLANEMODEON' OR tacode = 'MOBILEDATADISABLE' OR tacode = 'MOBILEDATAENABLE' OR tacode = 'WIFIDISABLE' OR tacode = 'WIFIENABLE') " +
                    "AND tatype ='Event Type')",null);
            eventLogs=new ArrayList<DeviceEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        DeviceEventLog eventLog=new DeviceEventLog();
                        eventLog.setAccuracy(c.getInt(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ACCURACY)));
                        eventLog.setDeviceid(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_IMEI)));
                        eventLog.setEventvalue(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_EVENTVALUE)));
                        eventLog.setGpslocation(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_GPS_LOCATION)));
                        eventLog.setAltitude(c.getDouble(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ALTITUDE)));
                        eventLog.setBatterylevel(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BATTERYLEVEL)));
                        eventLog.setSignalstrength(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_SIGNALSTRENGTH)));
                        eventLog.setAvailextmemory(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_AVAILEXTERNALMEMORY)));
                        eventLog.setAvailintmemory(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_AVAILINTERNALMEMORY)));
                        eventLog.setCdtz(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_CDTZ)));
                        eventLog.setMdtz(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MDTZ)));
                        eventLog.setCuser(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_CUSER)));
                        eventLog.setMuser(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MUSER)));
                        eventLog.setPeopleid(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_PEOPLEID)));
                        // eventLog.setIsdeleted(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ISDELETED)));
                        eventLog.setEventtype(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE)));
                        eventLog.setSignalbandwidth(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BADNWIDTHSIGNAL)));
                        eventLog.setBuid(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BUID)));
                        eventLog.setApplicationversion(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_APPLICATION_VERSION)));
                        eventLog.setAndroidosversion(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ANDROID_VERSION)));
                        eventLog.setModelname(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MODEL_NAME)));
                        eventLog.setInstalledapps(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_INSTALLED_APPS)));

                        eventLog.setSimserialnumber(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_SIM_NUMBER)));
                        eventLog.setLinenumber(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_LINE_NUMBER)));
                        eventLog.setNetworkprovidername(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_NETWORK_PROVIDER_NAME)));
                        eventLog.setStepCount(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_STEP_COUNT)));

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

        }
        return eventLogs;
    }

    public ArrayList<DeviceEventLog> getUnsyncStepEvents(long siteID)
    {
        ArrayList<DeviceEventLog> eventLogs=null;
        Cursor c = null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            String qry="Select * from " + DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS+" ='0' AND "
                    +DeviceEventLog_Table.DEVICEEVENT_BUID+" = "+siteID+" AND "
                    +DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE+ " in(select taid from TypeAssist where tacode = 'STEPCOUNT' AND tatype ='Event Type') ORDER BY strftime('%s' ,"+ DeviceEventLog_Table.DEVICEEVENT_CDTZ +") ASC";

            c = db.rawQuery(qry,null);

            System.out.println("StepCount Query: "+qry);
            eventLogs=new ArrayList<DeviceEventLog>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        DeviceEventLog eventLog=new DeviceEventLog();
                        eventLog.setAccuracy(c.getInt(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ACCURACY)));
                        eventLog.setDeviceid(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_IMEI)));
                        eventLog.setEventvalue(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_EVENTVALUE)));
                        eventLog.setGpslocation(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_GPS_LOCATION)));
                        eventLog.setAltitude(c.getDouble(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ALTITUDE)));
                        eventLog.setBatterylevel(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BATTERYLEVEL)));
                        eventLog.setSignalstrength(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_SIGNALSTRENGTH)));
                        eventLog.setAvailextmemory(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_AVAILEXTERNALMEMORY)));
                        eventLog.setAvailintmemory(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_AVAILINTERNALMEMORY)));
                        eventLog.setCdtz(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_CDTZ)));
                        eventLog.setMdtz(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MDTZ)));
                        eventLog.setCuser(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_CUSER)));
                        eventLog.setMuser(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MUSER)));
                        eventLog.setPeopleid(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_PEOPLEID)));
                        // eventLog.setIsdeleted(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ISDELETED)));
                        eventLog.setEventtype(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_EVENTTYPE)));
                        eventLog.setSignalbandwidth(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BADNWIDTHSIGNAL)));
                        eventLog.setBuid(c.getLong(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_BUID)));
                        eventLog.setApplicationversion(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_APPLICATION_VERSION)));
                        eventLog.setAndroidosversion(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_ANDROID_VERSION)));
                        eventLog.setModelname(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_MODEL_NAME)));
                        eventLog.setInstalledapps(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_INSTALLED_APPS)));

                        eventLog.setSimserialnumber(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_SIM_NUMBER)));
                        eventLog.setLinenumber(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_LINE_NUMBER)));
                        eventLog.setNetworkprovidername(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_NETWORK_PROVIDER_NAME)));
                        eventLog.setStepCount(c.getString(c.getColumnIndex(DeviceEventLog_Table.DEVICEEVENT_STEP_COUNT)));

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

        }
        return eventLogs;
    }


    public int changeSyncStatus(String peDateTime) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DeviceEventLog_Table.DEVICEEVENT_SYNCSTATUS, Constants.SYNC_STATUS_ONE);
            //return db.update(DeviceEventLog_Table.TABLE_NAME, values, "cdtz=?", new String[] { peDateTime });
            return db.update(DeviceEventLog_Table.TABLE_NAME, values, DeviceEventLog_Table.DEVICEEVENT_CDTZ+"='"+peDateTime+"'", null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void deleteRec()
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            db.rawQuery("delete from DeviceEventLog", null);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }
    public void deleteRec(String peDateTime )
    {
        //db.delete(DATABASE_TABLE, KEY_NAME + "=" + name, null)
        try {
            db = sqlopenHelper.getReadableDatabase();
            db.execSQL("delete from "+ DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_CDTZ+" ='"+peDateTime+"'");
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void deleteStepCountRec(String ids )
    {
        //db.delete(DATABASE_TABLE, KEY_NAME + "=" + name, null)
        try {
            db = sqlopenHelper.getReadableDatabase();
            db.execSQL("delete from "+ DeviceEventLog_Table.TABLE_NAME+" where "+DeviceEventLog_Table.DEVICEEVENT_CDTZ+" in ("+ids+")");
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }
}
