package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 27/9/17.
 *
 * people and group mapping data transfer object
 */
//@pgbid@isgrouplead@cdtz@mdtz@isdeleted@cuser@groupcode@muser@peoplecode
public class PeopleGroupBelonging {
    private long pgbid;
    private String isgrouplead;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private long cuser;
    private long groupid;
    private long muser;
    private long peopleid;

    private long buid;

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getPgbid() {
        return pgbid;
    }

    public void setPgbid(long pgbid) {
        this.pgbid = pgbid;
    }

    public String getIsgrouplead() {
        return isgrouplead;
    }

    public void setIsgrouplead(String isgrouplead) {
        this.isgrouplead = isgrouplead;
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

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public long getMuser() {
        return muser;
    }

    public void setMuser(long muser) {
        this.muser = muser;
    }

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
    }
}
