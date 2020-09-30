package com.youtility.intelliwiz20.Model;

import java.io.Serializable;

/**
 * Created by PrashantD on 6/9/17.
 *
 * job need transaction data transfer object
 *
 */
//jobneedid,jobdesc,frequency,plandatetime,expirydatetime,gracetime,jobtype,jobstatus,scantype,receivedonserver,
// priority,starttime,endtime,gpslocation,remarks,cuser,cdtz,muser,mdtz,isdeleted,assetcode,aatog,aatop,jobcode,performedby,qsetcode,jobidentifier, jnpid, ticketno, buid
public class JobNeed implements Serializable {
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
    private String attachmentcount;
    private int ticketno;
    private long buid;
    private int syncstatus;
    private int seqno;
    private long ticketcategory;
    private int ctzoffset;
    private String othersite;
    private double multiplicationfactor;
    private String deviation;

    public double getMultiplicationfactor() {
        return multiplicationfactor;
    }
    public String getDeviation(){
        return deviation;
    }
    public void setDeviation(String deviation) {
        this.deviation = deviation;
    }


    public void setMultiplicationfactor(double multiplicationfactor) {
        this.multiplicationfactor = multiplicationfactor;
    }

    public String getOthersite() {
        return othersite;
    }

    public void setOthersite(String othersite) {
        this.othersite = othersite;
    }

    public int getCtzoffset() {
        return ctzoffset;
    }

    public void setCtzoffset(int ctzoffset) {
        this.ctzoffset = ctzoffset;
    }

    //private String isdeleted;

    public long getTicketcategory() {
        return ticketcategory;
    }

    public void setTicketcategory(long ticketcategory) {
        this.ticketcategory = ticketcategory;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }
    public int getSyncstatus() {
        return syncstatus;
    }

    public void setSyncstatus(int syncstatus) {
        this.syncstatus = syncstatus;
    }

    public int getTicketno() {
        return ticketno;
    }

    public void setTicketno(int ticketno) {
        this.ticketno = ticketno;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getScantype() {
        return scantype;
    }

    public void setScantype(long scantype) {
        this.scantype = scantype;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
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



    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
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


    public String getAttachmentcount() {
        return attachmentcount;
    }

    public void setAttachmentcount(String attachmentcount) {
        this.attachmentcount = attachmentcount;
    }
}
