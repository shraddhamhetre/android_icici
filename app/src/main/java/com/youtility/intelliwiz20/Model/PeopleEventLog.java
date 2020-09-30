package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 19/9/17.
 *
 * people event log data transfer object
 */

//accuracy, deviceid, datetime, gpslocation, photorecognitionthreshold,photorecognitionscore, " +
//"photorecognitiontimestamp, photorecognitionserviceresponse,facerecognition, cdtz, mdtz, isdeleted, cuser, muser, peoplecode, peventtype, punchstatus, verifiedby


public class PeopleEventLog {

    private long pelogid;
    private float accuracy;
    //private long deviceid;
    private String deviceid;

    private String datetime;
    private String gpslocation;
    private int photorecognitionthreshold;
    private double photorecognitionscore;
    private String photorecognitiontimestamp;
    private String photorecognitionserviceresponse;
    private String facerecognition;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private long cuser;
    private long muser;
    private long peopleid;
    private long buid;
    private long peventtype;
    private long punchstatus;
    private long verifiedby;
    private long gfid;
    private String scanPeopleCode;
    private long transportmode;
    private double expamt;
    private int duration;
    private int distance;
    private String reference;
    private String remarks;
    private String otherlocation;

    public String getOtherlocation() {
        return otherlocation;
    }

    public void setOtherlocation(String otherlocation) { this.otherlocation = otherlocation; }

    public long getTransportmode() {
        return transportmode;
    }

    public void setTransportmode(long transportmode) {
        this.transportmode = transportmode;
    }

    public double getExpamt() {
        return expamt;
    }

    public void setExpamt(double expamt) {
        this.expamt = expamt;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public String getScanPeopleCode() {
        return scanPeopleCode;
    }

    public void setScanPeopleCode(String scanPeopleCode) {
        this.scanPeopleCode = scanPeopleCode;
    }

    public long getPeventtype() {
        return peventtype;
    }

    public void setPeventtype(long peventtype) {
        this.peventtype = peventtype;
    }

    public long getPunchstatus() {
        return punchstatus;
    }

    public void setPunchstatus(long punchstatus) {
        this.punchstatus = punchstatus;
    }

    public int getPhotorecognitionthreshold() {
        return photorecognitionthreshold;
    }

    public void setPhotorecognitionthreshold(int photorecognitionthreshold) {
        this.photorecognitionthreshold = photorecognitionthreshold;
    }

    public double getPhotorecognitionscore() {
        return photorecognitionscore;
    }

    public void setPhotorecognitionscore(double photorecognitionscore) {
        this.photorecognitionscore = photorecognitionscore;
    }

    public long getGfid() {
        return gfid;
    }

    public void setGfid(long gfid) {
        this.gfid = gfid;
    }

    public long getPelogid() {
        return pelogid;
    }

    public void setPelogid(long pelogid) {
        this.pelogid = pelogid;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    /*public long getDeviceid() {
        return deviceid;
    }*/
    public String getDeviceid() {
        return deviceid;
    }


    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getGpslocation() {
        return gpslocation;
    }

    public void setGpslocation(String gpslocation) {
        this.gpslocation = gpslocation;
    }

    public String getPhotorecognitiontimestamp() {
        return photorecognitiontimestamp;
    }

    public void setPhotorecognitiontimestamp(String photorecognitiontimestamp) {
        this.photorecognitiontimestamp = photorecognitiontimestamp;
    }

    public String getPhotorecognitionserviceresponse() {
        return photorecognitionserviceresponse;
    }

    public void setPhotorecognitionserviceresponse(String photorecognitionserviceresponse) {
        this.photorecognitionserviceresponse = photorecognitionserviceresponse;
    }

    public String getFacerecognition() {
        return facerecognition;
    }

    public void setFacerecognition(String facerecognition) {
        this.facerecognition = facerecognition;
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

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
    }

    public long getVerifiedby() {
        return verifiedby;
    }

    public void setVerifiedby(long verifiedby) {
        this.verifiedby = verifiedby;
    }
}
