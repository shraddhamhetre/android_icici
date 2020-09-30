package com.youtility.intelliwiz20.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.youtility.intelliwiz20.Interfaces.SyncInterface;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;

/**
 * Created by youtility4 on 4/9/17.
 */

public class RestApi implements SyncInterface {
    private final YoutilityServer server;
    private Context context;
    String date=null;
    private SharedPreferences loginRelatedPref;


    public RestApi(YoutilityServer youServer, Context context, String currDate) {
        this.server = youServer;
        this.context=context;
        date = currDate;
        date="1970-01-01 00:00:00";
        loginRelatedPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);

    }
    @Override
    public boolean assetMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_ASSETS+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_ASSET);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean jobNeedMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_JOBNEED+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_JOBNEED);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean jobNeedDetailsMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_JOBNEEDDETAILS+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_JOBNEED_DETAILS);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean typeAssistMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_TYPEASSIST+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_TYPE_ASSIST);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean geoFenceDetailsMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_GEOFRENCE+"("+loginRelatedPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")",Constants.STORY_NOT_REQUIRED, Constants.FILE_GEOFENCE);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean peopleDetailMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_PEOPLE+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_PEOPLE);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean groupDetailMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_GROUP+"('"+date+"')",Constants.STORY_NOT_REQUIRED, Constants.FILE_GROUP);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean attendanceHistoryMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_PEOPLEATTENDANCE+"()",Constants.STORY_NOT_REQUIRED, Constants.FILE_ATTENDANCE_HISTORY);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean questionMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_QUESTIONS+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_QUESTIONS);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean questionSetMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_QUESTIONSET+"('"+date+"')",Constants.STORY_NOT_REQUIRED, Constants.FILE_QUESTION_SET);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean questionSetBelongingMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_QUESTIONSETBELONGING+"('"+date+"')",Constants.STORY_NOT_REQUIRED, Constants.FILE_QUESTION_SET_BELONGING);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean peopleGroupBelongingMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, DatabaseQuries.GET_PEOPLEGROUPBELONGING+"('"+date+"')", Constants.STORY_NOT_REQUIRED,Constants.FILE_PEOPLE_GROUP_BELONGING);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean siteMaster() throws Exception {
        try
        {
            return server.downloadData(Constants.SERVICE_SELECT, context.getResources().getString(R.string.getSites,loginRelatedPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)), Constants.STORY_NOT_REQUIRED,Constants.FILE_SITES);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
