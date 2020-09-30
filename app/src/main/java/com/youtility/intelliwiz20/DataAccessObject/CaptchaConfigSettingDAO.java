package com.youtility.intelliwiz20.DataAccessObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youtility.intelliwiz20.Database.SqliteOpenHelper;
import com.youtility.intelliwiz20.Model.CaptchaConfigSetting;
import com.youtility.intelliwiz20.Tables.CaptchaConfig_Table;

public class CaptchaConfigSettingDAO {

    private SqliteOpenHelper sqlopenHelper;
    private SQLiteDatabase db;
    private Cursor c=null;

    public CaptchaConfigSettingDAO(Context context)
    {
        sqlopenHelper=SqliteOpenHelper.getInstance(context);
        db=sqlopenHelper.getDatabase();
    }

    public CaptchaConfigSetting getCaptchaSetting()
    {
        CaptchaConfigSetting captchaConfigSetting=null;

        try {
            db = sqlopenHelper.getReadableDatabase();
            c = db.rawQuery("Select * from " + CaptchaConfig_Table.TABLE_NAME,null);
            String ss="Select * from " + CaptchaConfig_Table.TABLE_NAME +
                    " limit 1";

            //System.out.println("GroupList: "+ss);
            captchaConfigSetting=new CaptchaConfigSetting();


            if(c!=null)
            {
                System.out.println("GroupList: 1 "+ c.getCount());
                if(c.moveToFirst())
                {
                    System.out.println("GroupList: 2 ");
                    do {
                        if(c.getString(c.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_ENABLE)).equalsIgnoreCase("t")) {
                            System.out.println("GroupList: 3 ");
                            captchaConfigSetting.setCaptchafreq(c.getInt(c.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_FREQUENCY)));
                            captchaConfigSetting.setStarttime(c.getString(c.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_STARTTIME)));
                            captchaConfigSetting.setEndtime(c.getString(c.getColumnIndex(CaptchaConfig_Table.CAPTCHA_CONFIG_ENDTIME)));
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
        System.out.println("captchaConfigSetting: "+captchaConfigSetting);
        return captchaConfigSetting;
    }


}
