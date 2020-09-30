package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.PersonLogger;
import com.youtility.intelliwiz20.Tables.PersonLogger_Table;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * job need related data access object
 *
 */

public class PersonLoggerDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private SharedPreferences loginDetailPref;
    private Cursor c=null;


    public PersonLoggerDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
        loginDetailPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

    }

    /*personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose,
        scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms,
        weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,areacode,enable,cuser,muser,cdtz,mdtz,buid,clientid*/
    public void insertRecord(PersonLogger regRecord, String syncStatus)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(PersonLogger_Table.PLOGGER_SYNC_STATUS, syncStatus);
            values.put(PersonLogger_Table.PLOGGER_ID, regRecord.getPersonloggerid());
            values.put(PersonLogger_Table.PLOGGER_IDENTIFIER, regRecord.getIdentifier());
            values.put(PersonLogger_Table.PLOGGER_PEOPLEID, regRecord.getPeopleid());
            values.put(PersonLogger_Table.PLOGGER_VISITORIDNO, regRecord.getVisitoridno());
            values.put(PersonLogger_Table.PLOGGER_FIRSTNAME, regRecord.getFirstname());
            values.put(PersonLogger_Table.PLOGGER_MIDDLENAME, regRecord.getMiddlename());
            values.put(PersonLogger_Table.PLOGGER_LASTNAME, regRecord.getLastname());
            values.put(PersonLogger_Table.PLOGGER_MOBILENO, regRecord.getMobileno());
            values.put(PersonLogger_Table.PLOGGER_IDPROOFTYPE, regRecord.getIdprooftype());
            values.put(PersonLogger_Table.PLOGGER_PHOTOIDNO, regRecord.getPhotoidno());
            values.put(PersonLogger_Table.PLOGGER_BELONGINGS, regRecord.getBelongings());
            values.put(PersonLogger_Table.PLOGGER_MEETINGPURPOSE, regRecord.getMeetingpurpose());
            values.put(PersonLogger_Table.PLOGGER_SCHEDULE_IN_TIME, regRecord.getScheduledintime());
            values.put(PersonLogger_Table.PLOGGER_SCHEDULE_OUT_TIME, regRecord.getScheduledouttime());
            values.put(PersonLogger_Table.PLOGGER_ACTUAL_IN_TIME, regRecord.getActualintime());
            values.put(PersonLogger_Table.PLOGGER_ACTUAL_OUT_TIME, regRecord.getActualouttime());
            values.put(PersonLogger_Table.PLOGGER_REFERENCEID, regRecord.getReferenceid());
            values.put(PersonLogger_Table.PLOGGER_DOB, regRecord.getDob());

            values.put(PersonLogger_Table.PLOGGER_QUALIFICATION, regRecord.getQualification());
            values.put(PersonLogger_Table.PLOGGER_ENGLISH, regRecord.isEnglish());
            values.put(PersonLogger_Table.PLOGGER_CURRENTEMPLOYEMENT, regRecord.getCurrentemployement());
            values.put(PersonLogger_Table.PLOGGER_LENGTHOFSERVICE, regRecord.getLengthofservice());
            values.put(PersonLogger_Table.PLOGGER_HEIGHT, regRecord.getHeightincms());
            values.put(PersonLogger_Table.PLOGGER_WEIGHT, regRecord.getWeightinkgs());
            values.put(PersonLogger_Table.PLOGGER_WAIST, regRecord.getWaist());
            values.put(PersonLogger_Table.PLOGGER_ISHANDICAPPED, regRecord.getIshandicapped());
            values.put(PersonLogger_Table.PLOGGER_IDENTIFICATIONMARK, regRecord.getIdentificationmark());
            values.put(PersonLogger_Table.PLOGGER_PHYSICALCONDITION, regRecord.getPhysicalcondition());
            values.put(PersonLogger_Table.PLOGGER_RELIGION, regRecord.getReligion());
            values.put(PersonLogger_Table.PLOGGER_CASTE, regRecord.getCaste());

            values.put(PersonLogger_Table.PLOGGER_LOCALADDRESS, regRecord.getLocaladdress());
            values.put(PersonLogger_Table.PLOGGER_L_AREACODE, regRecord.getLareacode());
            values.put(PersonLogger_Table.PLOGGER_L_CITY, regRecord.getLcity());
            values.put(PersonLogger_Table.PLOGGER_L_STATE, regRecord.getLstate());

            values.put(PersonLogger_Table.PLOGGER_NATIVEADDRESS, regRecord.getNativeaddress());
            values.put(PersonLogger_Table.PLOGGER_N_AREACODE, regRecord.getNareacode());
            values.put(PersonLogger_Table.PLOGGER_N_CITY, regRecord.getNcity());
            values.put(PersonLogger_Table.PLOGGER_N_STATE, regRecord.getNstate());

            values.put(PersonLogger_Table.PLOGGER_MARITALSTATUS, regRecord.getMaritalstatus());
            values.put(PersonLogger_Table.PLOGGER_GENDER, regRecord.getGender());

            values.put(PersonLogger_Table.PLOGGER_ENABLE, regRecord.isEnable());
            values.put(PersonLogger_Table.PLOGGER_CUSER, regRecord.getCuser());
            values.put(PersonLogger_Table.PLOGGER_MUSER, regRecord.getMuser());

            values.put(PersonLogger_Table.PLOGGER_CDTZ, regRecord.getCdtz());
            values.put(PersonLogger_Table.PLOGGER_MDTZ, regRecord.getMdtz());
            values.put(PersonLogger_Table.PLOGGER_BUID, regRecord.getBuid());
            values.put(PersonLogger_Table.PLOGGER_CLIENTID, regRecord.getClientid());
            long val= db.insert(PersonLogger_Table.TABLE_NAME, "", values);
            System.out.println("Common Data val: "+val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public void getCount()
    {
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select count(*) from " + PersonLogger_Table.TABLE_NAME ,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    System.out.println("Person logger Count: "+c.getInt(0));
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
    }

    public ArrayList<PersonLogger> getUnsyncPersonLoggerList(String identifier, String status)
    {
        ArrayList<PersonLogger> personLoggerArrayList=new ArrayList<PersonLogger>();
        try {
            db = sqlopenHelper.getReadableDatabase();

            String personLoggerSql="select * from "+PersonLogger_Table.TABLE_NAME +" where "+PersonLogger_Table.PLOGGER_SYNC_STATUS+" = '"+status+"'  AND "+
                    PersonLogger_Table.PLOGGER_IDENTIFIER+" in (select taid from TypeAssist where tacode in('"+ identifier +"') and tatype in ('"+Constants.IDENTIFIER_PERSONLOGGERTYPE+"'))";

            System.out.println("person Query: "+personLoggerSql);
            c = db.rawQuery(personLoggerSql ,null);

            /*personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose,
        scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms,
        weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,areacode,enable,cuser,muser,cdtz,mdtz,buid,clientid*/

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {
                        PersonLogger personLogger=new PersonLogger();
                        personLogger.setPersonloggerid(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_ID)));
                        personLogger.setIdentifier(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_IDENTIFIER)));
                        personLogger.setPeopleid(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_PEOPLEID)));
                        personLogger.setVisitoridno(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_VISITORIDNO)));
                        personLogger.setFirstname(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_FIRSTNAME)));
                        personLogger.setMiddlename(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_MIDDLENAME)));
                        personLogger.setLastname(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_LASTNAME)));
                        personLogger.setMobileno(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_MOBILENO)));
                        personLogger.setIdprooftype(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_IDPROOFTYPE)));
                        personLogger.setPhotoidno(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_PHOTOIDNO)));
                        personLogger.setBelongings(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_BELONGINGS)));
                        personLogger.setMeetingpurpose(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_MEETINGPURPOSE)));
                        personLogger.setScheduledintime(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_SCHEDULE_IN_TIME)));
                        personLogger.setScheduledouttime(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_SCHEDULE_OUT_TIME)));
                        personLogger.setActualintime(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_ACTUAL_IN_TIME)));
                        personLogger.setActualouttime(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_ACTUAL_OUT_TIME)));
                        personLogger.setReferenceid(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_REFERENCEID)));
                        personLogger.setDob(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_DOB)));

                        personLogger.setQualification(Long.valueOf(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_QUALIFICATION))));
                        personLogger.setEnglish(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_ENGLISH)));
                        personLogger.setCurrentemployement(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_CURRENTEMPLOYEMENT)));
                        personLogger.setLengthofservice(c.getDouble(c.getColumnIndex(PersonLogger_Table.PLOGGER_LENGTHOFSERVICE)));
                        personLogger.setHeightincms(c.getDouble(c.getColumnIndex(PersonLogger_Table.PLOGGER_HEIGHT)));
                        personLogger.setWeightinkgs(c.getDouble(c.getColumnIndex(PersonLogger_Table.PLOGGER_WEIGHT)));
                        personLogger.setWaist(c.getDouble(c.getColumnIndex(PersonLogger_Table.PLOGGER_WAIST)));
                        personLogger.setIshandicapped(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_ISHANDICAPPED)));
                        personLogger.setIdentificationmark(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_IDENTIFICATIONMARK)));
                        personLogger.setPhysicalcondition(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_PHYSICALCONDITION)));
                        personLogger.setReligion(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_RELIGION)));
                        personLogger.setCaste(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_CASTE)));
                        personLogger.setMaritalstatus(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_MARITALSTATUS)));
                        personLogger.setGender(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_GENDER)));

                        personLogger.setLocaladdress(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_LOCALADDRESS)));
                        personLogger.setNativeaddress(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_NATIVEADDRESS)));

                        personLogger.setLareacode(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_L_AREACODE)));
                        personLogger.setNareacode(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_N_AREACODE)));

                        personLogger.setLcity(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_L_CITY)));
                        personLogger.setNcity(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_N_CITY)));

                        personLogger.setLstate(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_L_STATE)));
                        personLogger.setNstate(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_N_STATE)));

                        personLogger.setEnable(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_ENABLE)));
                        personLogger.setCuser(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_CUSER)));
                        personLogger.setMuser(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_MUSER)));
                        personLogger.setCdtz(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_CDTZ)));
                        personLogger.setMdtz(c.getString(c.getColumnIndex(PersonLogger_Table.PLOGGER_MDTZ)));
                        personLogger.setBuid(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_BUID)));
                        personLogger.setClientid(c.getLong(c.getColumnIndex(PersonLogger_Table.PLOGGER_CLIENTID)));
                        personLoggerArrayList.add(personLogger);
                    }
                    while(c.moveToNext());
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
        return personLoggerArrayList;
    }



    public int changePeopleLoggerSyncStatus(String cdtz, String status) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PersonLogger_Table.PLOGGER_SYNC_STATUS, status);
            return db.update(PersonLogger_Table.TABLE_NAME, values, "cdtz=?", new String[] { cdtz });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {

        }
    }


}
