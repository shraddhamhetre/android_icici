package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 8/9/17.
 * type assist master data transfer object
 *
 */
//tacode,taname,tatype,cuser,cdtz,muser,mdtz,isdeleted,parent
public class TypeAssist {
    private long taid;
    private String tacode;
    private String taname;
    private String tatype;
    private long cuser;
    private String cdtz;
    private long muser;
    private String mdtz;
    //private String isdeleted;
    private long parent;

    private long buid;

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getTaid() {
        return taid;
    }

    public void setTaid(long taid) {
        this.taid = taid;
    }

    public String getTacode() {
        return tacode;
    }

    public void setTacode(String tacode) {
        this.tacode = tacode;
    }

    public String getTaname() {
        return taname;
    }

    public void setTaname(String taname) {
        this.taname = taname;
    }

    public String getTatype() {
        return tatype;
    }

    public void setTatype(String tatype) {
        this.tatype = tatype;
    }

    public long getCuser() {
        return cuser;
    }

    public void setCuser(long cuser) {
        this.cuser = cuser;
    }

    public String getCdtz() {
        return cdtz;
    }

    public void setCdtz(String cdtz) {
        this.cdtz = cdtz;
    }

    public long getMuser() {
        return muser;
    }

    public void setMuser(long muser) {
        this.muser = muser;
    }

    public String getMdtz() {
        return mdtz;
    }

    public void setMdtz(String mdtz) {
        this.mdtz = mdtz;
    }


    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }
}
