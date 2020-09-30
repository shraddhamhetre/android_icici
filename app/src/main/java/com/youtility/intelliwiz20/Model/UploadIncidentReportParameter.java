package com.youtility.intelliwiz20.Model;

import java.util.ArrayList;

/**
 * Created by PrashantD on 21/11/17.
 *
 * upload incident related question answer set
 *
 */

public class UploadIncidentReportParameter {
    private long jobneedid;
    private String jobdesc;
    private String plandatetime;
    private String expirydatetime;
    private int gracetime;
    private String receivedonserver;
    private String starttime;
    private String endtime;
    private String gpslocation;
    private String remarks;
    private long aatop;
    private long assetid;
    private long frequency;
    private long jobid;
    private long jobtype;
    private long jobstatus;
    private long performedby;
    private long priority;
    private long questionsetid;
    private long scantype;
    private long peopleid;
    private long groupid;
    private long identifier;
    private long parent;
    private long cuser;
    private String cdtz;
    private long muser;
    private String mdtz;
    private long buid;
    private int cdtzoffset;
    private String othersite;

    public String getOthersite() {
        return othersite;
    }

    public void setOthersite(String othersite) {
        this.othersite = othersite;
    }

    public int getCdtzoffset() {
        return cdtzoffset;
    }

    public void setCdtzoffset(int cdtzoffset) {
        this.cdtzoffset = cdtzoffset;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    //private String isdeleted;
    private ArrayList<QuestionSetLevel_Two>child;

    public ArrayList<QuestionSetLevel_Two> getChild() {
        return child;
    }

    public void setChild(ArrayList<QuestionSetLevel_Two> child) {
        this.child = child;
    }
    /*private ArrayList<QuestionSetLevel_One>child;

    public ArrayList<QuestionSetLevel_One> getChild() {
        return child;
    }

    public void setChild(ArrayList<QuestionSetLevel_One> child) {
        this.child = child;
    }*/

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

    public String getPlandatetime() {
        return plandatetime;
    }

    public void setPlandatetime(String plandatetime) {
        this.plandatetime = plandatetime;
    }

    public String getExpirydatetime() {
        return expirydatetime;
    }

    public void setExpirydatetime(String expirydatetime) {
        this.expirydatetime = expirydatetime;
    }

    public int getGracetime() {
        return gracetime;
    }

    public void setGracetime(int gracetime) {
        this.gracetime = gracetime;
    }

    public String getReceivedonserver() {
        return receivedonserver;
    }

    public void setReceivedonserver(String receivedonserver) {
        this.receivedonserver = receivedonserver;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getGpslocation() {
        return gpslocation;
    }

    public void setGpslocation(String gpslocation) {
        this.gpslocation = gpslocation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getAatop() {
        return aatop;
    }

    public void setAatop(long aatop) {
        this.aatop = aatop;
    }

    public long getAssetid() {
        return assetid;
    }

    public void setAssetid(long assetid) {
        this.assetid = assetid;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getJobid() {
        return jobid;
    }

    public void setJobid(long jobid) {
        this.jobid = jobid;
    }

    public long getJobtype() {
        return jobtype;
    }

    public void setJobtype(long jobtype) {
        this.jobtype = jobtype;
    }

    public long getJobstatus() {
        return jobstatus;
    }

    public void setJobstatus(long jobstatus) {
        this.jobstatus = jobstatus;
    }

    public long getPerformedby() {
        return performedby;
    }

    public void setPerformedby(long performedby) {
        this.performedby = performedby;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
    }

    public long getScantype() {
        return scantype;
    }

    public void setScantype(long scantype) {
        this.scantype = scantype;
    }

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
    }

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
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



}
