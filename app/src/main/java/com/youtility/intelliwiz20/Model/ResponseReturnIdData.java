package com.youtility.intelliwiz20.Model;

import com.google.gson.annotations.SerializedName;

/*{"status": "True", "username": "SWATI", "story": "", "returnid": 154745961627146, "appversion": "1.3.4.1",
        "auth": "True", "loggername": "SPS|SWATI - ", "clientcode": "SPS",
        "msg": "Record inserted successfully got returnid: 154745961627146", "reason": "OK",
        "siteid": 153328901261682, "clientid": 1, "rc": 0, "debug": false, "peopleid": 153329030096042, "makestory": true}*/

public class ResponseReturnIdData {
    @SerializedName("status")
    private String status;
    @SerializedName("rc")
    private int rc;
    @SerializedName("returnid")
    private long returnid;
    @SerializedName("msg")
    private String msg;


    public ResponseReturnIdData(String status, int rc, long returnid , String msg)
    {
        this.status=status;
        this.rc=rc;
        this.returnid=returnid;
        this.msg=msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public long getReturnid() {
        return returnid;
    }

    public void setReturnid(long returnid) {
        this.returnid = returnid;
    }

}
