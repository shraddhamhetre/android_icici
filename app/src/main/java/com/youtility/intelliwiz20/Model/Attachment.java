package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 11/9/17.
 *
 * Data transfer object for attachment
 */

public class Attachment {

    private long attachmentid;
    private String FilePath;
    private String FileName;
    private String Narration;
    private String gpslocation;
    private String datetime;
    private long cuser;
    private String cdtz;
    private long muser;
    private String mdtz;
    private long AttachmentType;
    //private String isdeleted;
    private long ownerid;
    private long ownername;
    private String serverPath;
    private int attachmentCategory;
    private long buid;

    public int getAttachmentCategory() {
        return attachmentCategory;
    }

    public void setAttachmentCategory(int attachmentCategory) {
        this.attachmentCategory = attachmentCategory;
    }

   /* private String peoplecode;
    private String paid;
    private String pelogid;
    private String jobneedid;
    private String jndid;
    private String acode;
    private String qscode;*/

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public long getAttachmentid() {
        return attachmentid;
    }

    public void setAttachmentid(long attachmentid) {
        this.attachmentid = attachmentid;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getNarration() {
        return Narration;
    }

    public void setNarration(String narration) {
        Narration = narration;
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

    public long getAttachmentType() {
        return AttachmentType;
    }

    public void setAttachmentType(long attachmentType) {
        AttachmentType = attachmentType;
    }

    public long getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(long ownerid) {
        this.ownerid = ownerid;
    }

    public long getOwnername() {
        return ownername;
    }

    public void setOwnername(long ownername) {
        this.ownername = ownername;
    }
}
