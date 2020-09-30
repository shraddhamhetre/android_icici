package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 26/9/17.
 *
 * Question set master data transfer object
 *
 */
//"$qsetcode$qsetname$enable$seqno$cdtz$mdtz$isdeleted$cuser$muser$parent$type" 11
public class QuestionSet {
    private long questionsetid;
    private String qsetname;
    private long assetid;
    private String enable;
    private int seqno;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private long cuser;
    private long muser;
    private long parent;
    private long type;
    private long buid;
    private String buincludes;
    private String assetincludes;
    private String url;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBuincludes() {
        return buincludes;
    }

    public void setBuincludes(String buincludes) {
        this.buincludes = buincludes;
    }

    public String getAssetincludes() {
        return assetincludes;
    }

    public void setAssetincludes(String assetincludes) {
        this.assetincludes = assetincludes;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
    }

    public String getQsetname() {
        return qsetname;
    }

    public void setQsetname(String qsetname) {
        this.qsetname = qsetname;
    }

    public long getAssetid() {
        return assetid;
    }

    public void setAssetid(long assetid) {
        this.assetid = assetid;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
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

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }
}
