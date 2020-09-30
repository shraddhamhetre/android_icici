package com.youtility.intelliwiz20.Model;

/**
 * Created by youtility on 9/3/18.
 */

public class SiteVisitedLog {
    private long  buid;
    private String bucode;
    private String buname;
    private String punchtime;
    private String punchstatus;
    private String remarks;
    private int fromRec;
    private String otherlocation;

    public String getOtherlocation() {
        return otherlocation;
    }

    public void setOtherlocation(String otherlocation) {
        this.otherlocation = otherlocation;
    }

    public int getFromRec() {
        return fromRec;
    }

    public void setFromRec(int fromRec) {
        this.fromRec = fromRec;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public String getBucode() {
        return bucode;
    }

    public void setBucode(String bucode) {
        this.bucode = bucode;
    }

    public String getBuname() {
        return buname;
    }

    public void setBuname(String buname) {
        this.buname = buname;
    }

    public String getPunchtime() {
        return punchtime;
    }

    public void setPunchtime(String punchtime) {
        this.punchtime = punchtime;
    }

    public String getPunchstatus() {
        return punchstatus;
    }

    public void setPunchstatus(String punchstatus) {
        this.punchstatus = punchstatus;
    }
}
