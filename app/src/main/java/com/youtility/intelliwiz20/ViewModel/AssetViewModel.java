package com.youtility.intelliwiz20.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.Asset;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;

import java.util.ArrayList;
import java.util.List;

public class AssetViewModel extends AndroidViewModel {

    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c = null;
    //private MutableLiveData<List<Asset>> assetList;
    private List<Asset> assetList;

    public AssetViewModel(@NonNull Application application) {
        super(application);
        sqlopenHelper=new SqliteOpenHelper(application);
    }

    public List<Asset> getAssetList()
    {
        if(assetList==null)
        {
            assetList=loadAssetList();
        }
        return assetList;

    }

    private List<Asset> loadAssetList()
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
}
