package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 28/9/17.
 */

public class UploadParameters {
    private String servicename;
    private String query;
    private String story;
    private String biodata;
    private String info;
    private String sitecode;
    private String loginid;
    private String password;
    private String tzoffset;
    private String deviceid;


    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getTzoffset() {
        return tzoffset;
    }

    public void setTzoffset(String tzoffset) {
        this.tzoffset = tzoffset;
    }

    public String getSitecode() {
        return sitecode;
    }

    public void setSitecode(String sitecode) {
        this.sitecode = sitecode;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    /*private String query1;
    private String query2;
    private String jobneedid;
    private String jobdesc;
    private String jobstatus;
    private String cuser;
    private String remarks;
    private String alertto;
    private String assigntype;*/

    public String getBiodata() {
        return biodata;
    }

    public void setBiodata(String biodata) {
        this.biodata = biodata;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
