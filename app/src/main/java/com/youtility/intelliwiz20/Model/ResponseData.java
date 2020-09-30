package com.youtility.intelliwiz20.Model;

import com.google.gson.annotations.SerializedName;

/*{"status": "True", "username": "SWATI", "story": "", "nrow": 0, "appversion": "1.3.4.1", "auth": "True", "loggername": "SPS|SWATI - ",
        "clientcode": "SPS", "msg": "Total (0) record fetched.", "reason": "OK", "row_data": "", "siteid": 153328901261682, "clientid": 1,
        "rc": 0, "debug": false, "ncol": 12, "peopleid": 153329030096042, "makestory": true,
        "columns": "@attachmentid@filepath@filename@narration@gpslocation@datetime@cuser@cdtz@muser@mdtz@attachmenttype@ownername",
        "coltype": "@int8@varchar@varchar@text@varchar@timestamptz@int8@timestamptz@int8@timestamptz@int8@int8"}*/

public class ResponseData {
    @SerializedName("status")
    private String status;
    @SerializedName("nrow")
    private int nrow;
    @SerializedName("row_data")
    private String row_data;
    @SerializedName("rc")
    private int rc;
    @SerializedName("columns")
    private String columns;

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public ResponseData(String status,int nrow, String row_data,int rc, String columns )
    {
        this.status=status;
        this.nrow=nrow;
        this.row_data=row_data;
        this.rc=rc;
        this.columns=columns;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNrow() {
        return nrow;
    }

    public void setNrow(int nrow) {
        this.nrow = nrow;
    }

    public String getRow_data() {
        return row_data;
    }

    public void setRow_data(String row_data) {
        this.row_data = row_data;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    @Override
    public String toString() {
        return "Status: "+status+" ,NRow: "+nrow+" ,RowData: "+row_data+" ,Rc: "+rc+" ,Columns: "+columns;
    }
}
