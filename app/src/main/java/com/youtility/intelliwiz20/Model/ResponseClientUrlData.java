package com.youtility.intelliwiz20.Model;

import com.google.gson.annotations.SerializedName;

/*{
        "clienturl": "http://192.168.1.150:8000/service/",
        "story": "",
        "appversion": "1.3.4.2",
        "loggername": "",
        "msg": "Got url[http://192.168.1.150:8000/service/] for client[YTPLD]",
        "reason": "OK",
        "rc": 0,
        "debug": false,
        "makestory": true
        }*/

public class ResponseClientUrlData {
    @SerializedName("clienturl")
    private String clienturl;
    @SerializedName("msg")
    private String msg;
    @SerializedName("story")
    private String story;
    @SerializedName("rc")
    private int rc;

    public ResponseClientUrlData(String clienturl, String msg, String story, int rc )
    {
        this.clienturl=clienturl;
        this.msg=msg;
        this.story=story;
        this.rc=rc;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getClienturl() {
        return clienturl;
    }

    public void setClienturl(String clienturl) {
        this.clienturl = clienturl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
}
