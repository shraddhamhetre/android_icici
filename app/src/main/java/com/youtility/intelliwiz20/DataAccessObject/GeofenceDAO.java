package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Geofence;
import com.youtility.intelliwiz20.Tables.Geofence_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * Geofence related data access object
 *
 */
// gfcode gfname geofence alerttext enable cdtz mdtz isdeleted alerttogroup alerttopeople cuser muser 12
public class GeofenceDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;

    public GeofenceDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }

    public int getGeoFenceCount(long buid)
    {
        Cursor cursor=null;
        int retCount=0;
        try
        {
            db = sqlopenHelper.getReadableDatabase();
            cursor=db.rawQuery("select count(*) from "+ Geofence_Table.TABLE_NAME+" where "+Geofence_Table.GEOFENCE_BUID+" = "+buid+" AND "+Geofence_Table.GEOFENCE_ENABLE+" = 'True' COLLATE NOCASE",null);
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    retCount=cursor.getInt(0);
                }
            }
            return  retCount;

        }catch (Exception e)
        {

        }
        finally {
            if(cursor!=null) {
                cursor.close();
                cursor=null;
            }

        }
        return retCount;
    }


    public ArrayList<Geofence> getGeofenceList(long buid)
    {
        ArrayList<Geofence> geofenceArrayList=null;
        Cursor c = null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + Geofence_Table.TABLE_NAME +" where "+Geofence_Table.GEOFENCE_BUID+" = "+buid +" AND "+Geofence_Table.GEOFENCE_ENABLE+" = 'True' COLLATE NOCASE" ,null);
            geofenceArrayList=new ArrayList<Geofence>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        Geofence geofence=new Geofence();
                        geofence.setGfid(c.getLong(c.getColumnIndex(Geofence_Table.GEOFENCE_ID)));
                        geofence.setGfcode(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_CODE)));
                        geofence.setGfname(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_NAME)));
                        geofence.setGeofence(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_GEOFENCE_POINTS)));
                        geofence.setEnable(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_ENABLE)));
                        geofence.setPeopleid(c.getLong(c.getColumnIndex(Geofence_Table.GEOFENCE_PEOPLEID)));
                        geofence.setFromdt(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_FROMDATE)));
                        geofence.setUptodt(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_UPTODATE)));
                        geofence.setIdentifier(c.getLong(c.getColumnIndex(Geofence_Table.GEOFENCE_IDENTIFIER)));
                        geofence.setStarttime(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_STARTTIME)));
                        geofence.setEndtime(c.getString(c.getColumnIndex(Geofence_Table.GEOFENCE_ENDTIME)));
                        geofence.setEntered(false);

                        System.out.println("geofence.getFromdt(): "+geofence.getFromdt());
                        System.out.println("geofence.getUptodt(): "+geofence.getUptodt());
                        System.out.println("geofence.getStarttime(): "+geofence.getStarttime());
                        System.out.println("geofence.getEndtime(): "+geofence.getEndtime());
                        System.out.println("geofence.getEnable(): "+geofence.getEnable());

                        geofenceArrayList.add(geofence);
                    }while (c.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            /*if(db!=null)
                db.close();*/
        }
        return geofenceArrayList;
    }

}
