package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;
import com.youtility.intelliwiz20.Tables.AssignedSitePeople_Table;
import com.youtility.intelliwiz20.Tables.People_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * people related data access object
 */

public class PeopleDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;
    public PeopleDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }



    public void getCount()
    {
        String userName=null;
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
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
    }

    public ArrayList<People> getPeopleList()
    {
        ArrayList<People> peoples=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + People_Table.TABLE_NAME +
                     " order by "+People_Table.PEOPLE_FULLNAME+" ASC",null);

            peoples=new ArrayList<>();

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getLong(c.getColumnIndex(People_Table.PEOPLE_ID))!=-1) {
                            People people=new People();
                            people.setPeoplename(c.getString(c.getColumnIndex(People_Table.PEOPLE_FULLNAME)));
                            people.setPeoplecode(c.getString(c.getColumnIndex(People_Table.PEOPLE_CODE)));
                            people.setPeopleid(c.getLong(c.getColumnIndex(People_Table.PEOPLE_ID)));
                            people.setLoginid(c.getString(c.getColumnIndex(People_Table.PEOPLE_LOGINID)));
                            peoples.add(people);
                        }
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
        return peoples;
    }

    public ArrayList<People> getAssignedSitePeopleList()
    {
        ArrayList<People> peoples=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AssignedSitePeople_Table.TABLE_NAME +
                    " order by "+AssignedSitePeople_Table.PEOPLE_FULLNAME+" ASC",null);

            peoples=new ArrayList<>();

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getLong(c.getColumnIndex(AssignedSitePeople_Table.PEOPLE_ID))!=-1) {
                            People people=new People();
                            people.setPeoplename(c.getString(c.getColumnIndex(AssignedSitePeople_Table.PEOPLE_FULLNAME)));
                            people.setPeoplecode(c.getString(c.getColumnIndex(AssignedSitePeople_Table.PEOPLE_CODE)));
                            people.setPeopleid(c.getLong(c.getColumnIndex(AssignedSitePeople_Table.PEOPLE_ID)));
                            people.setSalt(c.getString(c.getColumnIndex(AssignedSitePeople_Table.PEOPLE_SITES)));
                            peoples.add(people);
                        }
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
        return peoples;
    }

    public String getPeopleLoginID(long peopleid)
    {
        String pLoginId=null;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_ID+" = "+peopleid,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        pLoginId=(c.getString(c.getColumnIndex(People_Table.PEOPLE_LOGINID)));
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
        return pLoginId;
    }

    public String getPeopleName(long id)
    {
        String pName="";
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select "+People_Table.PEOPLE_FULLNAME+" from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_ID+" = "+id,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        pName=(c.getString(c.getColumnIndex(People_Table.PEOPLE_FULLNAME)));
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
        return pName;
    }

    public String getPeopleName(String peopleCode)
    {
        String pName="";
        try {
            db = sqlopenHelper.getReadableDatabase();
            String qry="Select "+People_Table.PEOPLE_FULLNAME+" from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_CODE+" = '"+peopleCode+"'";
            System.out.println("Pname Qry: "+qry);
            c = db.rawQuery(qry,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        pName=(c.getString(c.getColumnIndex(People_Table.PEOPLE_FULLNAME)));

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
        System.out.println("Pname : "+pName);
        return pName;
    }

    public String getPeopleMobile(long id)
    {
        String pName="";
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select "+People_Table.PEOPLE_MOBILENO+" from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_ID+" = "+id,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        pName=(c.getString(c.getColumnIndex(People_Table.PEOPLE_MOBILENO)));
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
        return pName;
    }

    public String getUserMobileLogEmailId(long id)
    {
        String mLogMailId="";
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select "+People_Table.PEOPLE_M_LOG_SEND_TO+" from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_ID+" = "+id,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        mLogMailId=(c.getString(c.getColumnIndex(People_Table.PEOPLE_M_LOG_SEND_TO)));
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
        return mLogMailId;
    }

    public boolean isCheckUserLog(long id)
    {
        boolean isCheck=false;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select "+People_Table.PEOPLE_CAPTURE_M_LOG+" from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_ID+" = "+id,null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getString(c.getColumnIndex(People_Table.PEOPLE_CAPTURE_M_LOG)).equalsIgnoreCase("True"))
                        {
                            isCheck=true;
                        }
                        System.out.println("PEOPLE_CAPTURE_M_LOG: "+isCheck);
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
        return isCheck;
    }

    public long getPeopleId(String peopleCode)
    {
        long peopleID=-1;
        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select "+People_Table.PEOPLE_ID+" from " + People_Table.TABLE_NAME +" where "+People_Table.PEOPLE_CODE+" = '"+peopleCode+"'",null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        peopleID=(c.getLong(c.getColumnIndex(People_Table.PEOPLE_ID)));
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
        return peopleID;
    }
}
