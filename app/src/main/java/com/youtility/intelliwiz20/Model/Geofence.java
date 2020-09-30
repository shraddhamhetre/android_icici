package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 8/9/17.
 *
 * geofence data transfer object
 *
 */
// g.gfid, g.gfcode, g.gfname, g.geofence, g.enable, j.aaatop as peopleid, j.fromdt, j.uptodt, j.identifier, j.starttime, j.endtime,
public class Geofence {
    private long gfid;
    private String gfcode;
    private String gfname;
    private String geofence;
    private String enable;
    private long peopleid;
    private String fromdt;
    private String uptodt;
    private long identifier;
    private String starttime;
    private String endtime;
    private boolean isEntered;

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    private long buid;


    public boolean isEntered() {
        return isEntered;
    }

    public void setEntered(boolean entered) {
        isEntered = entered;
    }


    public long getGfid() {
        return gfid;
    }

    public void setGfid(long gfid) {
        this.gfid = gfid;
    }

    public String getGfcode() {
        return gfcode;
    }

    public void setGfcode(String gfcode) {
        this.gfcode = gfcode;
    }

    public String getGfname() {
        return gfname;
    }

    public void setGfname(String gfname) {
        this.gfname = gfname;
    }

    public String getGeofence() {
        return geofence;
    }

    public void setGeofence(String geofence) {
        this.geofence = geofence;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
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

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
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
}
