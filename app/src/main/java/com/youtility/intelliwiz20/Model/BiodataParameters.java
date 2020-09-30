package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 16/10/17.
 *
 * used in upload data parameter data transefer object
 */

public class BiodataParameters {

    private long jobneedid;
    private String jobdesc;
    private long jobstatus;
    private long cuser;
    private String remarks;
    private long alertto;
    private String assigntype;
    private long pelogid;
    private String filename;
    private String path;
    private long peopleid;

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
    }

    public long getJobneedid() {
        return jobneedid;
    }

    public void setJobneedid(long jobneedid) {
        this.jobneedid = jobneedid;
    }

    public String getJobdesc() {
        return jobdesc;
    }

    public void setJobdesc(String jobdesc) {
        this.jobdesc = jobdesc;
    }

    public long getJobstatus() {
        return jobstatus;
    }

    public void setJobstatus(long jobstatus) {
        this.jobstatus = jobstatus;
    }

    public long getCuser() {
        return cuser;
    }

    public void setCuser(long cuser) {
        this.cuser = cuser;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getAlertto() {
        return alertto;
    }

    public void setAlertto(long alertto) {
        this.alertto = alertto;
    }

    public String getAssigntype() {
        return assigntype;
    }

    public void setAssigntype(String assigntype) {
        this.assigntype = assigntype;
    }

    public long getPelogid() {
        return pelogid;
    }

    public void setPelogid(long pelogid) {
        this.pelogid = pelogid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
