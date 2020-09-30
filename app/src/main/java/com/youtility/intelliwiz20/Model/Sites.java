package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 24/11/17.
 *
 * people assiged site master data transfer object
 *
 */
//"@sitepeopleid@fromdt@uptodt@siteowner@buid@peopleid@reportto@shift@slno@postingrev@contractid@cuser@muser@cdtz@mdtz@isdeleted@worktype"

    //"@int8@date@date@bool@int8@int8@int8@int8@int2@int2@int8@int8@int8@timestamptz@timestamptz@bool@int8"
public class Sites {
    private long sitepeopleid;
    private String fromdt;
    private String uptodt;
    private String siteowner;
    private long buid;
    private long peopleid;
    private long reportto;
    private long shift;
    private int slno;
    private int postingrev;
    private long contractid;
    private long cuser;
    private long muser;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private long worktype;
    private String bucode;
    private String buname;
    private String reportids;
    private String reportnames;
    private String enable;


    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getReportnames() {
        return reportnames;
    }

    public void setReportnames(String reportnames) {
        this.reportnames = reportnames;
    }

    public String getReportids() {
        return reportids;
    }

    public void setReportids(String reportids) {
        this.reportids = reportids;
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

    public long getSitepeopleid() {
        return sitepeopleid;
    }

    public void setSitepeopleid(long sitepeopleid) {
        this.sitepeopleid = sitepeopleid;
    }

    public String getFromdt() {
        return fromdt;
    }

    public void setFromdt(String fromdt) {
        this.fromdt = fromdt;
    }

    public String getUptodt() {
        return uptodt;
    }

    public void setUptodt(String uptodt) {
        this.uptodt = uptodt;
    }

    public String getSiteowner() {
        return siteowner;
    }

    public void setSiteowner(String siteowner) {
        this.siteowner = siteowner;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
    }

    public long getReportto() {
        return reportto;
    }

    public void setReportto(long reportto) {
        this.reportto = reportto;
    }

    public long getShift() {
        return shift;
    }

    public void setShift(long shift) {
        this.shift = shift;
    }

    public int getSlno() {
        return slno;
    }

    public void setSlno(int slno) {
        this.slno = slno;
    }

    public int getPostingrev() {
        return postingrev;
    }

    public void setPostingrev(int postingrev) {
        this.postingrev = postingrev;
    }

    public long getContractid() {
        return contractid;
    }

    public void setContractid(long contractid) {
        this.contractid = contractid;
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


    public long getWorktype() {
        return worktype;
    }

    public void setWorktype(long worktype) {
        this.worktype = worktype;
    }
}
