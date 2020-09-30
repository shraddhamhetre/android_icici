package com.youtility.intelliwiz20.DataAccessObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Address;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Tables.Address_Table;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 5/9/17.
 *
 * data access object for Asset
 */

public class AssetDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;

    public AssetDAO(Context context)
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
                c=null;
        }
    }

    public String getAssetName(long assetid)
    {
        String assetName="";

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+AssetDetail_Table.ASSET_NAME +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+" = "+assetid,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetName=c.getString(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetName;
    }

    public String getAssetCode(long assetid)
    {
        String assetName="";

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+AssetDetail_Table.ASSET_CODE +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+" = "+assetid,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetName=c.getString(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetName;
    }

    public Asset getAssetAssignedReport(String assetcode)
    {
        Asset asset=null;

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select * from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_CODE+" = '"+assetcode+"' OR "+AssetDetail_Table.ASSET_TEMPCODE+" ='"+assetcode+"'",null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    asset=new Asset();
                    asset.setQsetids(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETIDS)));
                    asset.setQsetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETNAME)));
                    asset.setAssetid(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_ID)));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return asset;
    }

    public String getAssetName(String assetCode)
    {
        String assetName=null;

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+AssetDetail_Table.ASSET_NAME +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_CODE+" = '"+assetCode+"'",null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetName=c.getString(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetName;
    }

    public long getAssetID(String assetcode)
    {
        long assetID=-1;

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+AssetDetail_Table.ASSET_ID +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_CODE+" = '"+assetcode+"' OR "+AssetDetail_Table.ASSET_TEMPCODE+" = '"+assetcode+"'",null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetID=c.getLong(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetID;
    }

    public String getAssetLocation(long assetid)
    {
        String assetLocation=null;

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+AssetDetail_Table.ASSET_LOCATION_NAME +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+" = "+assetid,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetLocation=c.getString(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetLocation;
    }

    public double getAssetMFactor(long assetid)
    {
        double assetMFactor=0.0;

        try{
            db=sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("select "+AssetDetail_Table.ASSET_MFACTOR +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+" = "+assetid,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetMFactor=c.getDouble(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetMFactor;
    }


    public String getTicketAssetLocation(long assetid)
    {
        String assetLocation=null;
        //select * from asset where assetid =152160937979795 and identifier in (select taid from typeassist where tatype='Asset Identifier')
        try{
            db=sqlopenHelper.getReadableDatabase();
            String qury="select "+AssetDetail_Table.ASSET_NAME +" from "+AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+" = "+assetid+" AND "+AssetDetail_Table.ASSET_IDENTIFIER +" IN ( select taid from typeassist where tatype='"+ Constants.IDENTIFIER_ASSET+"' AND tacode='"+Constants.TACODE_LOCATION+"')";
            System.out.println("getTicketAssetLocation: "+qury);
            c = db.rawQuery(qury,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    assetLocation=c.getString(0);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }

        return assetLocation;
    }

    public Address getAssetAddress(long assetid)
    {
        Address address=null;
        try{
            db=sqlopenHelper.getReadableDatabase();
            System.out.println("select * from "+ Address_Table.TABLE_NAME+" where "+Address_Table.ADDRESS_ASSETID+" = "+assetid);
            c = db.rawQuery("select * from "+ Address_Table.TABLE_NAME+" where "+Address_Table.ADDRESS_ASSETID+" = "+assetid,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    address=new Address();
                    address.setAddress(c.getString(c.getColumnIndex(Address_Table.ADDRESS_ADDRESS)));
                    address.setLandmark(c.getString(c.getColumnIndex(Address_Table.ADDRESS_LANDMARK)));
                    address.setMobileno(c.getString(c.getColumnIndex(Address_Table.ADDRESS_MOBILENO)));
                    address.setPhoneno(c.getString(c.getColumnIndex(Address_Table.ADDRESS_PHONENO)));
                    address.setWebsite(c.getString(c.getColumnIndex(Address_Table.ADDRESS_WEBSITE)));
                    address.setGpslocation(c.getString(c.getColumnIndex(Address_Table.ADDRESS_GPSLOCATION)));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(c!=null)
                c=null;
        }
        return address;
    }

    public ArrayList<Asset> getAssetList()
    {
        ArrayList<Asset> assets=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+"!=-1"+
                    " AND "+AssetDetail_Table.ASSET_IDENTIFIER+" in(select taid from TypeAssist where tacode = 'ASSET' OR tacode= 'SMARTPLACE' AND tatype ='Asset Identifier')"+
                    " order by "+AssetDetail_Table.ASSET_CODE+" ASC" ,null);
            assets=new ArrayList<Asset>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        Asset asset=new Asset();
                        asset.setAssetid(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_ID)));
                        asset.setAssetcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_CODE)));
                        asset.setAssetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_NAME)));
                        asset.setEnable(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_ENABLE)));
                        asset.setIscritical(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_IS_CRITICAL)));
                        asset.setIdentifier(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_IDENTIFIER)));
                        asset.setRunningstatus(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_RUNNING_STATUS)));
                        asset.setLoccode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_CODE)));
                        asset.setLocname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_NAME)));

                        asset.setType(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_TYPE)));
                        asset.setCategory(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_CATEGORY)));
                        asset.setSubcategory(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SUBCATEGORY)));
                        asset.setBrand(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_BRAND)));
                        asset.setModel(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_MODEL)));
                        asset.setSupplier(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SUPPLIER)));
                        asset.setCapacity(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_CAPACITY)));
                        asset.setUnit(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_UNIT)));
                        asset.setYom(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_YOM)));
                        asset.setMsn(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_MSN)));
                        asset.setBdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_BILLDATE)));
                        asset.setPdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_PURCHACEDATE)));
                        asset.setIsdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_INSTALLATIONDATE)));
                        asset.setBillval(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_BILLVALUE)));
                        asset.setServprov(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER)));
                        asset.setServprovname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER_NAME)));
                        asset.setService(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICE)));
                        asset.setSfdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEFROMDATE)));
                        asset.setStdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICETODATE)));
                        asset.setMeter(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_METER)));
                        asset.setQsetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETNAME)));
                        asset.setQsetids(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETIDS)));
                        asset.setTempcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_TEMPCODE)));
                        asset.setMultiplicationfactor(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_MFACTOR)));

                        assets.add(asset);
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
                c=null;
        }
        return assets;
    }
    public ArrayList<Asset> getCheckpointList()
    {
        ArrayList<Asset> assets=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+"!=-1"+
                    " AND "+AssetDetail_Table.ASSET_IDENTIFIER+" in(select taid from TypeAssist where tacode= 'CHECKPOINT' AND tatype ='Asset Identifier')"+
                    " order by "+AssetDetail_Table.ASSET_CODE+" ASC" ,null);
            assets=new ArrayList<Asset>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        Asset asset=new Asset();
                        asset.setAssetid(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_ID)));
                        asset.setAssetcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_CODE)));
                        asset.setAssetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_NAME)));
                        asset.setEnable(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_ENABLE)));
                        asset.setIscritical(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_IS_CRITICAL)));
                        asset.setIdentifier(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_IDENTIFIER)));
                        asset.setRunningstatus(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_RUNNING_STATUS)));
                        asset.setLoccode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_CODE)));
                        asset.setLocname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_NAME)));

                        asset.setType(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_TYPE)));
                        asset.setCategory(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_CATEGORY)));
                        asset.setSubcategory(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SUBCATEGORY)));
                        asset.setBrand(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_BRAND)));
                        asset.setModel(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_MODEL)));
                        asset.setSupplier(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SUPPLIER)));
                        asset.setCapacity(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_CAPACITY)));
                        asset.setUnit(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_UNIT)));
                        asset.setYom(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_YOM)));
                        asset.setMsn(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_MSN)));
                        asset.setBdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_BILLDATE)));
                        asset.setPdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_PURCHACEDATE)));
                        asset.setIsdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_INSTALLATIONDATE)));
                        asset.setBillval(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_BILLVALUE)));
                        asset.setServprov(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER)));
                        asset.setServprovname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER_NAME)));
                        asset.setService(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICE)));
                        asset.setSfdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEFROMDATE)));
                        asset.setStdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICETODATE)));
                        asset.setMeter(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_METER)));
                        asset.setQsetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETNAME)));
                        asset.setQsetids(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETIDS)));
                        asset.setTempcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_TEMPCODE)));
                        asset.setMultiplicationfactor(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_MFACTOR)));

                        assets.add(asset);
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
                c=null;
        }
        return assets;
    }

    public ArrayList<Asset> getAssetLocationList(String tatype, String tacode)
    {
        ArrayList<Asset> assets=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_ID+"!=-1"+
                    " AND "+AssetDetail_Table.ASSET_IDENTIFIER+" in(select taid from TypeAssist where tacode = '"+tacode+"' AND tatype ='"+tatype+"')"+
                    " order by "+AssetDetail_Table.ASSET_CODE+" ASC" ,null);
            assets=new ArrayList<Asset>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        Asset asset=new Asset();
                        asset.setAssetid(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_ID)));
                        asset.setAssetcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_CODE)));
                        asset.setAssetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_NAME)));
                        asset.setEnable(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_ENABLE)));
                        asset.setIscritical(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_IS_CRITICAL)));
                        asset.setIdentifier(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_IDENTIFIER)));
                        asset.setRunningstatus(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_RUNNING_STATUS)));
                        asset.setLoccode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_CODE)));
                        asset.setLocname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_LOCATION_NAME)));

                        asset.setType(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_TYPE)));
                        asset.setCategory(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_CATEGORY)));
                        asset.setSubcategory(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SUBCATEGORY)));
                        asset.setBrand(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_BRAND)));
                        asset.setModel(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_MODEL)));
                        asset.setSupplier(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SUPPLIER)));
                        asset.setCapacity(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_CAPACITY)));
                        asset.setUnit(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_UNIT)));
                        asset.setYom(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_YOM)));
                        asset.setMsn(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_MSN)));
                        asset.setBdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_BILLDATE)));
                        asset.setPdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_PURCHACEDATE)));
                        asset.setIsdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_INSTALLATIONDATE)));
                        asset.setBillval(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_BILLVALUE)));
                        asset.setServprov(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER)));
                        asset.setServprovname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEPROVIDER_NAME)));
                        asset.setService(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICE)));
                        asset.setSfdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICEFROMDATE)));
                        asset.setStdate(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_SERVICETODATE)));
                        asset.setMeter(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_METER)));
                        asset.setQsetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETNAME)));
                        asset.setQsetids(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_QSETIDS)));
                        asset.setTempcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_TEMPCODE)));
                        asset.setMultiplicationfactor(c.getDouble(c.getColumnIndex(AssetDetail_Table.ASSET_MFACTOR)));

                        assets.add(asset);
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
                c=null;
        }
        return assets;
    }

    public ArrayList<Asset> getUnsyncAssetList()
    {
        ArrayList<Asset> assets=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + AssetDetail_Table.TABLE_NAME+" where "+AssetDetail_Table.ASSET_SYNC_STATUS+" = 1" ,null);
            assets=new ArrayList<Asset>();
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do {
                        Asset asset=new Asset();
                        asset.setAssetcode(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_CODE)));
                        asset.setAssetname(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_NAME)));
                        asset.setEnable(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_ENABLE)));
                        asset.setIscritical(c.getString(c.getColumnIndex(AssetDetail_Table.ASSET_IS_CRITICAL)));
                        asset.setIdentifier(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_IDENTIFIER)));
                        asset.setRunningstatus(c.getLong(c.getColumnIndex(AssetDetail_Table.ASSET_RUNNING_STATUS)));
                        assets.add(asset);
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
                c=null;
        }
        return assets;
    }

    public int changeRunningStatus(String assetCode, long assetId, long newStatus) {

        try {
            db = sqlopenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AssetDetail_Table.ASSET_RUNNING_STATUS, newStatus);
            values.put(AssetDetail_Table.ASSET_SYNC_STATUS,1);
            return db.update(AssetDetail_Table.TABLE_NAME, values, "assetid=?", new String[] {String.valueOf(assetId)});
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        } finally {
            /*if (null != db) {
                db.close();
            }*/
        }
    }

    public String getAssetRunningStatus(String assetCode) {
        Cursor cursor = null;
        String rStatus = null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            cursor = db.rawQuery("Select " + AssetDetail_Table.ASSET_RUNNING_STATUS + " from " + AssetDetail_Table.TABLE_NAME + " where " + AssetDetail_Table.ASSET_CODE+ "='" + assetCode+"'", null);
            if (cursor.moveToFirst()) {
                rStatus = cursor.getString(0);
            } else {
                rStatus = "WORKING";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*if (null != db) {
                db.close();
            }*/
            if(cursor!=null)
                cursor.close();
        }
        return rStatus;
    }

    //getDashboardCount query: SELECT scheduled, completed,pending,closed FROM ( select  (AssetDetails.assetcode), COUNT(AssetDetails.assetid) as scheduled, COUNT(case when jstatus.tacode = 'WORKING' then jstatus.tacode end) as pending, COUNT(case when jstatus.tacode = 'STANDBY' then jstatus.tacode end) as closed, COUNT(case when jstatus.tacode = 'MAINTENANCE' then jstatus.tacode end) as completed  FROM AssetDetails  INNER JOIN typeassist jstatus on jstatus.taid = AssetDetails.runningstatus  INNER JOIN typeassist i on AssetDetails.identifier=i.taid  WHERE AssetDetails.assetid<> -1  AND AssetDetails.enable = true  GROUP BY AssetDetails.assetcode ORDER BY ( AssetDetails.assetcode) ASC)tsk;



    public String getAssetCount()
    {
        int sCount=0;
        int cCount=0;
        int pCount=0;
        int aCount=0;
        try {
            db = sqlopenHelper.getReadableDatabase();
            String str="SELECT totalasset, maintenance,working,standby " +
                    "FROM ( select  (AssetDetails.assetcode), " +
                    "COUNT(AssetDetails.assetid) as totalasset, " +
                    "COUNT(case when jstatus.tacode = 'WORKING' then jstatus.tacode end) as working, " +
                    "COUNT(case when jstatus.tacode = 'STANDBY' then jstatus.tacode end) as standby, " +
                    "COUNT(case when jstatus.tacode = 'MAINTENANCE' then jstatus.tacode end) as maintenance  " +
                    "FROM AssetDetails  " +
                    "INNER JOIN typeassist jstatus on jstatus.taid = AssetDetails.runningstatus  " +
                    "INNER JOIN typeassist i on AssetDetails.identifier=i.taid  " +
                    "WHERE AssetDetails.assetid<> -1  " +
                    "AND +AssetDetails.identifier in(select taid from TypeAssist where tacode in ('ASSET','SMARTPLACE') AND tatype ='Asset Identifier') "+
                    "GROUP BY AssetDetails.assetcode " +
                    "ORDER BY ( AssetDetails.assetcode) ASC)tsk;";

            //"AND AssetDetails.enable = 'True'  " +

            System.out.println("getDashboardCount query: "+str);

            c=db.rawQuery(str,null);
            if(c!=null)
            {
                if(c.moveToFirst())
                {
                    do
                    {//5~0~5~0
                        sCount=sCount+c.getInt(0);//count
                        cCount=cCount+c.getInt(1);//maintance
                        pCount=pCount+c.getInt(2);//working
                        aCount=aCount+c.getInt(3);//standby

                    }while(c.moveToNext());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null)
            {
                c.close();
                c=null;
            }
        }
        return sCount+"~"+cCount+"~"+pCount+"~"+aCount;
    }
}
