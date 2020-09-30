package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.SitesInformation;
import com.youtility.intelliwiz20.Tables.SitesInfo_Table;

/**
 * Created by PrashantD on 5/9/17.
 *
 * login user assigned sites data access object
 *
 */

public class SiteInformationDAO {
    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    Cursor c = null;

    public SiteInformationDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }





    public SitesInformation getSiteInformation(long siteid)
    {
        SitesInformation sitesInformation=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            //c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" order by "+Sites_Table.SITE_PEOPLE_SLNO+" ASC",null);
            c = db.rawQuery("Select * from " + SitesInfo_Table.TABLE_NAME +" where "+ SitesInfo_Table.SITE_INFO_SITEID+" = "+siteid,null);
            //c = db.rawQuery("Select * from " + Sites_Table.TABLE_NAME +" order by "+Sites_Table.BU_NAME+" ASC",null);

            if(c!=null)
            {
                if(c.moveToFirst())
                {//@contract@lrev@contractid@constartdate@conenddate@sincharge@simob@siteid@site@address@landmark@postalcode@mobileno@gpslocation@totstrength@strength
                    do {
                        sitesInformation=new SitesInformation();
                        sitesInformation.setContract(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACT)));
                        sitesInformation.setLrev(c.getInt(c.getColumnIndex(SitesInfo_Table.SITE_INFO_REVISION_NUMBER)));
                        sitesInformation.setContractid(c.getLong(c.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACTID)));
                        sitesInformation.setConstartdate(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACT_SDATE)));
                        sitesInformation.setConenddate(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_CONTRACT_EDATE)));
                        sitesInformation.setSincharge(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_INCHARGE)));
                        sitesInformation.setSimob(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEMOBILE)));
                        sitesInformation.setSiteid(c.getLong(c.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEID)));
                        sitesInformation.setSite(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_SITENAME)));
                        sitesInformation.setAddress(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEADDRESS)));
                        sitesInformation.setLandmark(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_SITELANDMARK)));
                        sitesInformation.setPostalcode(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_POSTALCODE)));
                        sitesInformation.setMobileno(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_MOBILE)));
                        sitesInformation.setGpslocation(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_SITEGPS)));
                        sitesInformation.setTotstrength(c.getInt(c.getColumnIndex(SitesInfo_Table.SITE_INFO_TOTALSTRENGTH)));
                        sitesInformation.setStrength(c.getString(c.getColumnIndex(SitesInfo_Table.SITE_INFO_STRENGTH)));

                        System.out.println("sitesInformation.getAddress(): "+sitesInformation.getAddress());
                        System.out.println("sitesInformation.getSincharge(): "+sitesInformation.getSincharge());
                        System.out.println("sitesInformation.getSimob(): "+sitesInformation.getSimob());
                        System.out.println("sitesInformation.getGpslocation(): "+sitesInformation.getGpslocation());
                        System.out.println("sitesInformation.getContract(): "+sitesInformation.getContract());
                        System.out.println("sitesInformation.getTotstrength(): "+sitesInformation.getTotstrength());
                        System.out.println("sitesInformation.getStrength(): "+sitesInformation.getStrength());
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
        return sitesInformation;
    }


}
