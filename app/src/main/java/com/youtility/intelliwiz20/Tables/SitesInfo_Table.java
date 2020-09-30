package com.youtility.intelliwiz20.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by PrashantD on 17/08/17.
 *
 * login user assigned sites master table
 */

//@contract@lrev@contractid@constartdate@conenddate@sincharge@simob@siteid@site@address@landmark@postalcode@mobileno@gpslocation@totstrength@strength
    //@varchar@int4@int8@date@date@text@text@int8@text@text@text@text@text@text@numeric@text

public class SitesInfo_Table implements BaseColumns {

    public static final String TABLE_NAME= "SitesInfo";

    public static final String SITE_INFO_CONTRACT="contract";
    public static final String SITE_INFO_REVISION_NUMBER="lrev";
    public static final String SITE_INFO_CONTRACTID="contractid";

    public static final String SITE_INFO_CONTRACT_SDATE="constartdate";
    public static final String SITE_INFO_CONTRACT_EDATE="conenddate";

    public static final String SITE_INFO_INCHARGE="sincharge";
    public static final String SITE_INFO_MOBILE="simob";
    public static final String SITE_INFO_SITEID="siteid";
    public static final String SITE_INFO_SITENAME="site";
    public static final String SITE_INFO_SITEADDRESS="address";

    public static final String SITE_INFO_SITELANDMARK="landmark";
    public static final String SITE_INFO_POSTALCODE="postalcode";

    public static final String SITE_INFO_SITEMOBILE="mobileno";
    public static final String SITE_INFO_SITEGPS="gpslocation";
    public static final String SITE_INFO_TOTALSTRENGTH="totstrength";
    public static final String SITE_INFO_STRENGTH="strength";

    private static final String TEXT_TYPE="text";
    private static final String INT_TYPE="Integer";
    private static final String ID_TYPE="INTEGER PRIMARY KEY AUTOINCREMENT";

    private static final String CREATE_TABLE=" CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
            _ID                             +" "+   ID_TYPE     +   ","+
            SITE_INFO_CONTRACT              +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_REVISION_NUMBER       +" "+   INT_TYPE    +   ","+
            SITE_INFO_CONTRACTID            +" "+   INT_TYPE    +   ","+
            SITE_INFO_CONTRACT_SDATE        +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_CONTRACT_EDATE        +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_INCHARGE              +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_MOBILE                +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_SITEID                +" "+   INT_TYPE   +   ","+
            SITE_INFO_SITENAME              +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_SITEADDRESS           +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_SITELANDMARK          +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_POSTALCODE            +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_SITEMOBILE            +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_SITEGPS               +" "+   TEXT_TYPE    +   ","+
            SITE_INFO_TOTALSTRENGTH         +" "+   INT_TYPE   +   ","+
            SITE_INFO_STRENGTH              +" "+   TEXT_TYPE   +
            ");";


    public static void OnCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE);
        System.out.println("Sites INFO Table Created");
    }

    public static void OnUpgarde(SQLiteDatabase db, int oldv, int newv)
    {

    }

}
