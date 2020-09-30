package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 24/1/18.
 *
 * Data transfer object for login
 */


/*{"mobilecapability": "areaofficer checkpoint travelexpense assetmaintenance ppmplanner employeereference panic sitevisitlog userlocation asset schedulesitereport selfattendance leaderboard branchmanager sitetour siteattendance ticket addleave takeattendance incidentreport task conveyance",
        "username": "SWATI", "emergencycontact": "8600701556,9594875568", "clientid": 1, "peopleid": 153329030096042,
        "clientname": "SPS", "loginid": "SWATI", "story": "", "emergencyemail": "ashish.mhashilkar@youtility.in,dinesh.rajbhar@youtility.in",
        "clientenable": true, "sitename": "DD Corp", "captchafrequency": 10, "peoplecode": "SWATI", "enablesleepingguard": false, "rc": 0,
        "msg": "Login successful.", "status": "True", "appversion": "1.3.4.1", "auth": "True", "loggername": "SPS|SWATI - ", "debug": false,
        "isadmin": false, "password": "b410d4d4d7f81fbfbd65db5db7b9be1b", "makestory": true, "gpsenable": true, "reason": "OK", "clientcode": "SPS",
        "peoplename": "Swati Chavan", "siteid": 153328901261682, "siteenable": true, "peopleenable": true, "salt": "20188312014848",
        "sitecode": "DDCORP"}*/

public class LoginResponse {

    private String mobilecapability;
    private String sitecode;
    private long siteid;
    private String sitename;
    private boolean isadmin;
    private long clientid;
    private String clientname;
    private String clientcode;
    private long peopleid;
    private String peoplecode;
    private String peoplename;
    private String loginid;
    private String gpsenable;
    private String emergencycontact;
    private String emergencyemail;
    private String enablesleepingguard;
    private int captchafrequency;
    private int rc;
    private boolean status;
    private boolean auth;
    private String appversion;
    private String msg;
    private String skipsiteaudit;
    private String deviceevent;
    private int pvideolength;
    private String email;
    private String mobileno;

    public String getDeviceevent() {
        return deviceevent;
    }

    public void setDeviceevent(String deviceevent) {
        this.deviceevent = deviceevent;
    }

    public String getSkipsiteaudit() {
        return skipsiteaudit;
    }

    public void setSkipsiteaudit(String skipsiteaudit) {
        this.skipsiteaudit = skipsiteaudit;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public int getCaptchafrequency() {
        return captchafrequency;
    }

    public void setCaptchafrequency(int captchafrequency) {
        this.captchafrequency = captchafrequency;
    }

    public String getEnablesleepingguard() {
        return enablesleepingguard;
    }

    public int pvideolength() {
        return pvideolength;
    }
    public String email() {
        return email;
    }
    public String mobileno() {
        return mobileno;
    }

    public void setEnablesleepingguard(String enablesleepingguard) {
        this.enablesleepingguard = enablesleepingguard;
    }

    public String getEmergencycontact() {
        return emergencycontact;
    }

    public void setEmergencycontact(String emergencycontact) {
        this.emergencycontact = emergencycontact;
    }

    public String getEmergencyemail() {
        return emergencyemail;
    }

    public void setEmergencyemail(String emergencyemail) {
        this.emergencyemail = emergencyemail;
    }

    public String getGpsenable() {
        return gpsenable;
    }

    public void setGpsenable(String gpsenable) {
        this.gpsenable = gpsenable;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getClientcode() {
        return clientcode;
    }

    public void setClientcode(String clientcode) {
        this.clientcode = clientcode;
    }

    public String getMobilecapability() {
        return mobilecapability;
    }

    public void setMobilecapability(String mobilecapability) {
        this.mobilecapability = mobilecapability;
    }

    public String getSitecode() {
        return sitecode;
    }

    public void setSitecode(String sitecode) {
        this.sitecode = sitecode;
    }

    public long getSiteid() {
        return siteid;
    }

    public void setSiteid(long siteid) {
        this.siteid = siteid;
    }

    public boolean isadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }

    public long getClientid() {
        return clientid;
    }

    public void setClientid(long clientid) {
        this.clientid = clientid;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public long getPeopleid() {
        return peopleid;
    }

    public void setPeopleid(long peopleid) {
        this.peopleid = peopleid;
    }

    public String getPeoplecode() {
        return peoplecode;
    }

    public void setPeoplecode(String peoplecode) {
        this.peoplecode = peoplecode;
    }

    public String getPeoplename() {
        return peoplename;
    }

    public void setPeoplename(String peoplename) {
        this.peoplename = peoplename;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
}
