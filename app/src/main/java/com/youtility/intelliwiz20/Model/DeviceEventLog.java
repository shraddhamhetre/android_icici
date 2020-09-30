package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 5/10/17.
 *
 * device event log data transfer object
 *
 */
//deviceid, eventvalue, gpslocation, accuracy, altitude, batterylevel, signalstrength, availextmemory, availintmemory,
// cdtz, mdtz, isdeleted, cuser, eventtype, muser, peoplecode, signalbandwidth
public class DeviceEventLog {
    private long deviceeventlogid;
    //private long deviceid;
    private String deviceid;

    private String eventvalue;
    private String gpslocation;
    private double accuracy;
    private double altitude;
    private String batterylevel;
    private String signalstrength;
    private String availextmemory;
    private String availintmemory;
    private long eventtype;
    private long peopleid;
    private String signalbandwidth;
    private long cuser;
    private String cdtz;
    private long muser;
    private String mdtz;
    private String androidosversion;
    private String applicationversion;
    private String modelname;
    private long buid;
    private String installedapps;
    private String simserialnumber;
    private String linenumber;
    private String networkprovidername;
    private String stepCount;

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }

    public String getSimserialnumber() {
        return simserialnumber;
    }

    public void setSimserialnumber(String simserialnumber) {
        this.simserialnumber = simserialnumber;
    }

    public String getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(String linenumber) {
        this.linenumber = linenumber;
    }

    public String getNetworkprovidername() {
        return networkprovidername;
    }

    public void setNetworkprovidername(String networkprovidername) {
        this.networkprovidername = networkprovidername;
    }

    //private String isdeleted;


    public String getInstalledapps() {
        return installedapps;
    }

    public void setInstalledapps(String installedapps) {
        this.installedapps = installedapps;
    }

    public String getAndroidosversion() {
        return androidosversion;
    }

    public void setAndroidosversion(String androidosversion) {
        this.androidosversion = androidosversion;
    }

    public String getApplicationversion() {
        return applicationversion;
    }

    public void setApplicationversion(String applicationversion) {
        this.applicationversion = applicationversion;
    }

    public String getModelname() {
        return modelname;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
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

    public long getDeviceeventlogid() {
        return deviceeventlogid;
    }

    public void setDeviceeventlogid(long deviceeventlogid) {
        this.deviceeventlogid = deviceeventlogid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getEventvalue() {
        return eventvalue;
    }

    public void setEventvalue(String eventvalue) {
        this.eventvalue = eventvalue;
    }

    public String getGpslocation() {
        return gpslocation;
    }

    public void setGpslocation(String gpslocation) {
        this.gpslocation = gpslocation;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getBatterylevel() {
        return batterylevel;
    }

    public void setBatterylevel(String batterylevel) {
        this.batterylevel = batterylevel;
    }

    public String getSignalstrength() {
        return signalstrength;
    }

    public void setSignalstrength(String signalstrength) {
        this.signalstrength = signalstrength;
    }

    public String getAvailextmemory() {
        return availextmemory;
    }

    public void setAvailextmemory(String availextmemory) {
        this.availextmemory = availextmemory;
    }

    public String getAvailintmemory() {
        return availintmemory;
    }

    public void setAvailintmemory(String availintmemory) {
        this.availintmemory = availintmemory;
    }

    public long getEventtype() {
        return eventtype;
    }

    public void setEventtype(long eventtype) {
        this.eventtype = eventtype;
    }


    public String getSignalbandwidth() {
        return signalbandwidth;
    }

    public void setSignalbandwidth(String signalbandwidth) {
        this.signalbandwidth = signalbandwidth;
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
