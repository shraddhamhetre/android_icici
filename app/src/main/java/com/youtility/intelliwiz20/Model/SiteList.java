package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 24/11/17.
 *
 * people assiged site master data transfer object
 *
 */
//buid,bucode, buname, butype, butypename, enable, incharge

public class SiteList {
    private long buid;
    private String bucode;
    private String buname;
    private long butype;
    private String butypename;
    private String enable;
    private String incharge;
    private String cdtz;
    private String mdtz;
    private long cuser;
    private long muser;

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

    public long getButype() {
        return butype;
    }

    public void setButype(long butype) {
        this.butype = butype;
    }

    public String getButypename() {
        return butypename;
    }

    public void setButypename(String butypename) {
        this.butypename = butypename;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getIncharge() {
        return incharge;
    }

    public void setIncharge(String incharge) {
        this.incharge = incharge;
    }
}
