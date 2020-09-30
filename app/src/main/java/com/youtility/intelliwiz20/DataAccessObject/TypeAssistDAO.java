package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.Tables.TypeAssist_Table;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * type assist master data transcation object
 *
 */

public class TypeAssistDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;

    public TypeAssistDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }





    public long getEventTypeID(String eventCode)
    {
        long eventId=-1;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_CODE+" = '"+eventCode+"'",null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        eventId=(c.getLong(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ID)));
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
        return eventId;
    }

    public long getEventTypeIDForAdhoc(String eventName)
    {
        long eventId=-1;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_CODE+" = '"+eventName+"' AND "+TypeAssist_Table.TYPE_ASSIST_TYPE+" = '"+ Constants.IDENTIFIER_JOBNEED+"'",null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        eventId=(c.getLong(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ID)));
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
        return eventId;
    }


    public long getEventTypeID(String eventCode, String eventType)
    {
        long eventId=-1;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_CODE+" = '"+eventCode+"' AND "+TypeAssist_Table.TYPE_ASSIST_TYPE +" like '"+eventType+"'",null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        eventId=(c.getLong(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ID)));
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
        return eventId;
    }


    public ArrayList<TypeAssist> getEventList(String eventName)
    {
        ArrayList<TypeAssist> eventList=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select * from typeassist where tatype like '"+eventName+"' order by tacode asc",null);

            if(c!=null)
            {
                eventList=new ArrayList<>();
                if(c.moveToFirst())
                {
                    do {
                        TypeAssist typeAssist=new TypeAssist();
                        typeAssist.setTaname(c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_NAME)));
                        typeAssist.setTacode(c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE)));
                        typeAssist.setTaid(c.getLong(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_ID)));
                        System.out.println("TStatusCode: "+c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE)));
                        eventList.add(typeAssist);
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
        return eventList;
    }



    public String getEventTypeName(long typeid)
    {
        String eventName="";
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_ID+" = "+typeid,null);
            System.out.println("Event id: "+typeid);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        eventName=(c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_NAME)));
                        System.out.println("Event name: "+eventName);
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
        return eventName;
    }

    public String getEventTypeCode(long typeid)
    {
        String eventCode=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_ID+" = "+typeid,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        eventCode=(c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE)));
                        System.out.println("Event code: "+eventCode);
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
        return eventCode;
    }

    public String getEventTypeCode(String tatype)
    {
        String eventCode=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + TypeAssist_Table.TABLE_NAME +" where "+TypeAssist_Table.TYPE_ASSIST_TYPE+" = '"+tatype+"'",null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        eventCode=(c.getString(c.getColumnIndex(TypeAssist_Table.TYPE_ASSIST_CODE)));
                        System.out.println("Event code: "+eventCode);
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
        return eventCode;
    }
}
