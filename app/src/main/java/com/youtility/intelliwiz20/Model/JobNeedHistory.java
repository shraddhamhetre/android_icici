package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 27/9/17.
 *
 * job need related history transfer data object
 */
//"  attachmentid  filepath  filename  narration  gpslocation  datetime  cuser  cdtz  muser  mdtz  isdeleted  assetcode
// attachmenttype  jndid  jobneedid  paid  peoplecode  qsetcode  pelogid" 19

//"columns": "attachmentid filepath filename narration gpslocation datetime cuser cdtz muser mdtz isdeleted assetcode
// attachmenttype jndid jobneedid paid peoplecode qsetcode pelogid gfcode"
public class JobNeedHistory {
    private String attachmentid;
    private String filepath;
    private String filename;
    private String narration;
    private String gpslocation;
    private String datetime;
    private String cuser;
    private String cdtz;
    private String muser;
    private String mdtz;
    //private String isdeleted;
    private String assetcode;
    private String attachmenttype;
    private String jndid;
    private String jobneedid;
    private String paid;
    private String peoplecode;
    private String qsetcode;
    private String pelogid;
    private String gfcode;

    public String getGfcode() {
        return gfcode;
    }

    public void setGfcode(String gfcode) {
        this.gfcode = gfcode;
    }

    public String getAttachmentid() {
        return attachmentid;
    }

    public void setAttachmentid(String attachmentid) {
        this.attachmentid = attachmentid;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getGpslocation() {
        return gpslocation;
    }

    public void setGpslocation(String gpslocation) {
        this.gpslocation = gpslocation;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCuser() {
        return cuser;
    }

    public void setCuser(String cuser) {
        this.cuser = cuser;
    }

    public String getCdtz() {
        return cdtz;
    }

    public void setCdtz(String cdtz) {
        this.cdtz = cdtz;
    }

    public String getMuser() {
        return muser;
    }

    public void setMuser(String muser) {
        this.muser = muser;
    }

    public String getMdtz() {
        return mdtz;
    }

    public void setMdtz(String mdtz) {
        this.mdtz = mdtz;
    }


    public String getAssetcode() {
        return assetcode;
    }

    public void setAssetcode(String assetcode) {
        this.assetcode = assetcode;
    }

    public String getAttachmenttype() {
        return attachmenttype;
    }

    public void setAttachmenttype(String attachmenttype) {
        this.attachmenttype = attachmenttype;
    }

    public String getJndid() {
        return jndid;
    }

    public void setJndid(String jndid) {
        this.jndid = jndid;
    }

    public String getJobneedid() {
        return jobneedid;
    }

    public void setJobneedid(String jobneedid) {
        this.jobneedid = jobneedid;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getPeoplecode() {
        return peoplecode;
    }

    public void setPeoplecode(String peoplecode) {
        this.peoplecode = peoplecode;
    }

    public String getQsetcode() {
        return qsetcode;
    }

    public void setQsetcode(String qsetcode) {
        this.qsetcode = qsetcode;
    }

    public String getPelogid() {
        return pelogid;
    }

    public void setPelogid(String pelogid) {
        this.pelogid = pelogid;
    }
}
