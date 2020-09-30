package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Group;
import com.youtility.intelliwiz20.Tables.Group_Table;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 * people group related data access object
 *
 */

public class GroupDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c=null;

    public GroupDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }


    public String getGroupName(long id)
    {
        String groupName="";

        try
        {
            db=sqlopenHelper.getReadableDatabase();
            c=db.rawQuery("Select "+ Group_Table.GROUP_NAME+" from "+Group_Table.TABLE_NAME+" where "+Group_Table.GROUP_ID+" = "+id,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    groupName=c.getString(0);
                }
            }
        }
        catch (Exception e)
        {

        }
        finally {
            if(c!=null) {
                c.close();
                c=null;
            }
        }
        return groupName;
    }

    public ArrayList<Group> getGroupList()
    {
        ArrayList<Group> groups=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + Group_Table.TABLE_NAME+
                    " Where "+Group_Table.GROUP_ENABLE+" = 'True'"+
                    " order by "+Group_Table.GROUP_NAME+" ASC" ,null);

            String ss="Select * from " + Group_Table.TABLE_NAME+
                    " Where "+Group_Table.GROUP_ENABLE+" = 'True'"+
                    " order by "+Group_Table.GROUP_NAME+" ASC";

            System.out.println("GroupList: "+ss);
            groups=new ArrayList<Group>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        if(c.getLong(c.getColumnIndex(Group_Table.GROUP_ID))!=-1) {
                            Group group = new Group();
                            group.setGroupname(c.getString(c.getColumnIndex(Group_Table.GROUP_NAME)));
                            group.setGroupid(c.getLong(c.getColumnIndex(Group_Table.GROUP_ID)));
                            groups.add(group);
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
            if(c!=null) {
                c.close();
                c=null;
            }
        }
        return groups;
    }
}
