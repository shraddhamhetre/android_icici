package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 14/9/17.
 *
 * people group master data transfer object
 *
 */

public class Group {
    //groupcode,groupname,enable,cdtz,mdtz,isdeleted,cuser,muser

    private long groupid;
    private String groupname;
    private String enable;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private long cuser;
    private long muser;
    private long buid;

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getCdtz() {
        return cdtz;
    }

    public void setCdtz(String cdtz) {
        this.cdtz = cdtz;
    }

    public String getMdtz() {
        return mdtz;
    }

    public void setMdtz(String mdtz) {
        this.mdtz = mdtz;
    }


    public long getCuser() {
        return cuser;
    }

    public void setCuser(long cuser) {
        this.cuser = cuser;
    }

    public long getMuser() {
        return muser;
    }

    public void setMuser(long muser) {
        this.muser = muser;
    }
}
