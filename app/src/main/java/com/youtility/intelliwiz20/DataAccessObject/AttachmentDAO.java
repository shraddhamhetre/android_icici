package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Tables.Attachment_Table;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * data access object for Attachments
 */

public class AttachmentDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor cursor=null;

    public AttachmentDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }

    public void insertCommonRecord(Attachment regRecord)
    {
        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            ContentValues v = null;
            values.put(Attachment_Table.ATTACHMENT_SYNC_STATUS, "0");
            values.put(Attachment_Table.ATTACHMENT_ID, regRecord.getAttachmentid());
            values.put(Attachment_Table.ATTACHMENT_TYPE, regRecord.getAttachmentType());
            values.put(Attachment_Table.ATTACHMENT_FILEPATH, regRecord.getFilePath());
            values.put(Attachment_Table.ATTACHMENT_FILENAME, regRecord.getFileName());
            values.put(Attachment_Table.ATTACHMENT_NARRATION, regRecord.getNarration());
            values.put(Attachment_Table.ATTACHMENT_GPSLOCATION, regRecord.getGpslocation());
            //values.put(Attachment_Table.ATTACHMENT_ISDELETED, regRecord.getIsdeleted());
            values.put(Attachment_Table.ATTACHMENT_DATETIME, regRecord.getDatetime());
            values.put(Attachment_Table.ATTACHMENT_CUSER, regRecord.getCuser());
            values.put(Attachment_Table.ATTACHMENT_CDTZ, regRecord.getCdtz());
            values.put(Attachment_Table.ATTACHMENT_MUSER, regRecord.getMuser());
            values.put(Attachment_Table.ATTACHMENT_MDTZ, regRecord.getMdtz());
            values.put(Attachment_Table.ATTACHMENT_OWNERID, regRecord.getOwnerid());
            values.put(Attachment_Table.ATTACHMENT_OWNERNAME, regRecord.getOwnername());
            values.put(Attachment_Table.ATTACHMENT_SERVERPATH, regRecord.getServerPath());
            values.put(Attachment_Table.ATTACHMENT_CATEGORY, regRecord.getAttachmentCategory());
            values.put(Attachment_Table.ATTACHMENT_BUID, regRecord.getBuid());

            long val= db.insert(Attachment_Table.TABLE_NAME, "", values);
            System.out.println("Common Data val: "+val+" ----- "+regRecord.getAttachmentid());
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

    public int getAttachmentCount(long ownerid, long attachmentid, int attachmentCategory)
    {
        int attCount=0;

        try
        {
            db=sqlopenHelper.getReadableDatabase();
            cursor=db.rawQuery("select count(*) from "+Attachment_Table.TABLE_NAME+" where "+
                    Attachment_Table.ATTACHMENT_ID+" = "+attachmentid +" AND "+
                    Attachment_Table.ATTACHMENT_CATEGORY+" = "+attachmentCategory +" AND "+
                    Attachment_Table.ATTACHMENT_OWNERID+" = "+ownerid,null);
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    attCount=cursor.getInt(0);
                }
            }
        }catch (Exception e)
        {

        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return attCount;
    }

    public int getAttachmentCount(long ownerid, long attachmentid)
    {
        int attCount=0;

        try
        {
            db=sqlopenHelper.getReadableDatabase();
            cursor=db.rawQuery("select count(*) from "+Attachment_Table.TABLE_NAME+" where "+
                    Attachment_Table.ATTACHMENT_ID+" = "+attachmentid +" AND "+
                    Attachment_Table.ATTACHMENT_OWNERID+" = "+ownerid,null);
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    return cursor.getInt(0);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return 0;
    }

    public Attachment getAttachment(long ownerId, long attachmentID)
    {
        Attachment attachment=null;
        //filepath, filename, narration, gpslocation, datetime,cuser, cdtz, muser, mdtz, attachmenttype,ownerid, ownername, buid
        try {
            db = sqlopenHelper.getReadableDatabase();
            cursor = db.rawQuery("Select * from " + Attachment_Table.TABLE_NAME+" where "+
                    Attachment_Table.ATTACHMENT_ID+" ="+attachmentID +" AND "+
                    Attachment_Table.ATTACHMENT_OWNERID+" = "+ownerId,null);
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        attachment=new Attachment();
                        attachment.setFilePath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILEPATH)));
                        attachment.setFileName(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILENAME)));
                        attachment.setNarration(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_NARRATION)));
                        attachment.setGpslocation(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_GPSLOCATION)));
                        attachment.setDatetime(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_DATETIME)));
                        attachment.setCuser(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_CUSER)));
                        attachment.setCdtz(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_CDTZ)));
                        attachment.setMuser(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_MUSER)));
                        attachment.setMdtz(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_MDTZ)));
                        attachment.setAttachmentType(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_TYPE)));
                        attachment.setOwnerid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_OWNERID)));
                        attachment.setOwnername(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_OWNERNAME)));
                        attachment.setBuid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_BUID)));
                        attachment.setServerPath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_SERVERPATH)));
                    }while (cursor.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return attachment;
    }

    public ArrayList<Attachment> getAttachments(long attachmentID, int attachmentCategory, long jobneedid)
    {
        ArrayList<Attachment> attachments=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            cursor = db.rawQuery("Select * from " + Attachment_Table.TABLE_NAME+" where "+
                    Attachment_Table.ATTACHMENT_ID+" ="+attachmentID +" AND "+
                    Attachment_Table.ATTACHMENT_CATEGORY+" = "+attachmentCategory+" AND "+
                    Attachment_Table.ATTACHMENT_OWNERID+" = "+jobneedid,null);
            attachments=new ArrayList<Attachment>();
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        Attachment attachment=new Attachment();
                        attachment.setFileName(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILENAME)));
                        attachment.setFilePath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILEPATH)));
                        //attachment.setJobneedid(c.getString(c.getColumnIndex(Attachment_Table.ATTACHMENT_JOBNEEDID)));
                        attachments.add(attachment);
                    }while (cursor.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return attachments;
    }

    public ArrayList<Attachment> getAttachments(long attachmentID, long jobneedid)
    {
        ArrayList<Attachment> attachments=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            cursor = db.rawQuery("Select * from " + Attachment_Table.TABLE_NAME+" where "+
                    Attachment_Table.ATTACHMENT_ID+" ="+attachmentID +" AND "+
                    Attachment_Table.ATTACHMENT_OWNERID+" = "+jobneedid,null);
            attachments=new ArrayList<Attachment>();
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        Attachment attachment=new Attachment();
                        attachment.setFileName(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILENAME)));
                        attachment.setFilePath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILEPATH)));
                        //attachment.setJobneedid(c.getString(c.getColumnIndex(Attachment_Table.ATTACHMENT_JOBNEEDID)));
                        attachments.add(attachment);
                    }while (cursor.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return attachments;
    }

    public ArrayList<Attachment> getUnsyncJobNeedReplyAttachments()
    {
        ArrayList<Attachment> attachments=null;

        try {
            db = sqlopenHelper.getReadableDatabase();

            String replyQuery="Select * from " + Attachment_Table.TABLE_NAME+" where "+Attachment_Table.ATTACHMENT_SYNC_STATUS+" = '0' AND "+Attachment_Table.ATTACHMENT_TYPE+" in(select taid from TypeAssist where tacode = 'REPLY') ";
            System.out.println("upload reply query: "+replyQuery);
            cursor = db.rawQuery(replyQuery,null);
            //c = db.rawQuery("Select * from " + Attachment_Table.TABLE_NAME+" where "+Attachment_Table.ATTACHMENT_SYNC_STATUS+" = '0' AND "+Attachment_Table.ATTACHMENT_TYPE+" in(select taid from TypeAssist where tacode = 'REPLY') limit 1",null);
            attachments=new ArrayList<Attachment>();
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        Attachment attachment=new Attachment();
                        attachment.setAttachmentid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_ID)));
                        attachment.setAttachmentType(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_TYPE)));
                        attachment.setNarration(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_NARRATION)));
                        attachment.setGpslocation(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_GPSLOCATION)));
                        attachment.setDatetime(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_DATETIME)));
                        attachment.setCuser(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_CUSER)));
                        attachment.setCdtz(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_CDTZ)));
                        attachment.setMuser(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_MUSER)));
                        attachment.setMdtz(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_MDTZ)));
                        attachment.setFileName(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILENAME)));
                        attachment.setFilePath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILEPATH)));
                        attachment.setOwnerid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_OWNERID)));
                        attachment.setOwnername(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_OWNERNAME)));
                        attachment.setServerPath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_SERVERPATH)));
                        //attachment.setIsdeleted(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_ISDELETED)));
                        attachment.setBuid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_BUID)));
                        attachments.add(attachment);
                    }while (cursor.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return attachments;
    }

    public ArrayList<Attachment> getUnsyncAttachments(long attachmentType)
    {
        ArrayList<Attachment> attachments=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            //cursor = db.rawQuery("Select * from " + Attachment_Table.TABLE_NAME+" where "+Attachment_Table.ATTACHMENT_SYNC_STATUS+" = '0' AND "+Attachment_Table.ATTACHMENT_TYPE+" = "+attachmentType ,null);
            String aQuery="Select * from " + Attachment_Table.TABLE_NAME+" where "+Attachment_Table.ATTACHMENT_SYNC_STATUS+" = '0' AND "+
                    Attachment_Table.ATTACHMENT_TYPE+" in (select taid from TypeAssist where tacode in('SIGN','ATTACHMENT') AND tatype in('"+ Constants.IDENTIFIER_ATTACHMENT+"'))";
            System.out.println("Attachmentquery: "+aQuery);
            cursor = db.rawQuery( aQuery,null);

//String jobNeedSql="select * from "+JOBNeed_Table.TABLE_NAME +" where "+JOBNeed_Table.JOBNEED_SYNC_STATUS+" = '"+status+"'  AND "+
 //           JOBNeed_Table.JOBNEED_IDENTIFIER+" in (select taid from TypeAssist where tacode in("+ identifier +") and tatype in ('"+Constants.IDENTIFIER_JOBNEED+"'))";


            attachments=new ArrayList<Attachment>();
            if(cursor!=null)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        Attachment attachment=new Attachment();
                        attachment.setAttachmentid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_ID)));
                        attachment.setAttachmentType(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_TYPE)));
                        attachment.setNarration(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_NARRATION)));
                        attachment.setGpslocation(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_GPSLOCATION)));
                        attachment.setDatetime(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_DATETIME)));
                        attachment.setCuser(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_CUSER)));
                        attachment.setCdtz(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_CDTZ)));
                        attachment.setMuser(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_MUSER)));
                        attachment.setMdtz(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_MDTZ)));
                        attachment.setFileName(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILENAME)));
                        attachment.setFilePath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_FILEPATH)));
                        attachment.setOwnerid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_OWNERID)));
                        attachment.setOwnername(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_OWNERNAME)));
                        attachment.setServerPath(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_SERVERPATH)));
                        //attachment.setIsdeleted(cursor.getString(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_ISDELETED)));
                        attachment.setBuid(cursor.getLong(cursor.getColumnIndex(Attachment_Table.ATTACHMENT_BUID)));
                        attachments.add(attachment);
                    }while (cursor.moveToNext());


                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor=null;
        }
        return attachments;
    }

    public void deleteAVP(long attachmentid)
    {
        try {
            int val=db.delete(Attachment_Table.TABLE_NAME, Attachment_Table.ATTACHMENT_ID+" = "+attachmentid	, null);
            System.out.println("record deleted: "+val);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteAVP()
    {
        try {
            int val=db.delete(Attachment_Table.TABLE_NAME, Attachment_Table.ATTACHMENT_SYNC_STATUS+" = '1'", null);
            System.out.println("record deleted: "+val);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public int changeAdhocReturnID(String  jnid, String timestamp) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_OWNERID, jnid);
            return db.update(Attachment_Table.TABLE_NAME, values, "attachmentid=?", new String[] { timestamp });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }
    //5043538161862542

    public int changePeopleEventLogReturnID(String  pelogid, String timestamp) {
        System.out.println("pelogid as ownerid: "+ pelogid);

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_OWNERID, pelogid);
            return db.update(Attachment_Table.TABLE_NAME, values, "attachmentid=?", new String[] { timestamp });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changePersonLogReturnID(String  pelogid, String timestamp) {

        try {
            System.out.println("changePersonLogReturnID pelogid"+pelogid);
            System.out.println("changePersonLogReturnID timestamp"+timestamp);
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_OWNERID, pelogid);
            return db.update(Attachment_Table.TABLE_NAME, values, "attachmentid=?", new String[] { timestamp });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changePelogReturnID(String  pelogid, long jobneedid) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_OWNERID, pelogid);
            System.out.println("Change attachment id: "+pelogid+" : "+jobneedid);
            return db.update(Attachment_Table.TABLE_NAME, values, "ownerid=?", new String[] { String.valueOf(jobneedid) });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changeSycnStatus( String timestamp, long attachmentType, String fileName) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_SYNC_STATUS,Constants.SYNC_STATUS_ONE);
            return db.update(Attachment_Table.TABLE_NAME, values, "attachmentid=? AND AttachmentType=? AND FileName=? ", new String[] { timestamp, String.valueOf(attachmentType), fileName });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changeJNSycnStatus( long jnid, long attachmenttype, String fileName) {

        try {

            System.out.println("Attachment jnid: "+jnid);
            System.out.println("Attachment type: "+attachmenttype);

            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_SYNC_STATUS, Constants.SYNC_STATUS_ONE);
            return db.update(Attachment_Table.TABLE_NAME, values, "attachmentid=? AND AttachmentType=?", new String[] { String.valueOf(jnid), String.valueOf(attachmenttype), fileName });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public int changeJNReplySycnStatus( long jnid, long attachmenttype) {

        try {

            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Attachment_Table.ATTACHMENT_SYNC_STATUS, Constants.SYNC_STATUS_ONE);
            return db.update(Attachment_Table.TABLE_NAME, values, "attachmentid=? AND AttachmentType=?", new String[] { String.valueOf(jnid), String.valueOf(attachmenttype) });
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
