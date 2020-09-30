package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 20/9/17.
 *
 * not in used
 */

//pelogid,peoplecode,datetime,punchstatus,peventtype,cuser


public class AttendanceHistory {
    private long pelogid;
    private long peoplecode;
    private String datetime;
    private long punchstatus;
    private long peventtype;
    private long cuser;

    public long getPelogid() {
        return pelogid;
    }

    public void setPelogid(long pelogid) {
        this.pelogid = pelogid;
    }

    public long getPeoplecode() {
        return peoplecode;
    }

    public void setPeoplecode(long peoplecode) {
        this.peoplecode = peoplecode;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public long getPunchstatus() {
        return punchstatus;
    }

    public void setPunchstatus(long punchstatus) {
        this.punchstatus = punchstatus;
    }

    public long getPeventtype() {
        return peventtype;
    }

    public void setPeventtype(long peventtype) {
        this.peventtype = peventtype;
    }

    public long getCuser() {
        return cuser;
    }

    public void setCuser(long cuser) {
        this.cuser = cuser;
    }
}

