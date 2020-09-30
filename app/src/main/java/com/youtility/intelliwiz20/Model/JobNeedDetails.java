package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 6/9/17.
 *
 * job need details data transfer object
 */
//jndid,seqno,questionname,type,answer,option,min,max,alerton,ismandatory,cdtz,mdtz,isdeleted,cuser,jobneedid,muser
public class JobNeedDetails {
    private long jndid;
    private int seqno;
    private long questionid;
    private String answer;
    private String option;
    private double min;
    private double max;
    private String alerton;
    private long jobneedid;
    private long type;
    private String ismandatory;
    private String cdtz;
    private String mdtz;
    private long cuser;
    private long muser;

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public long getJndid() {
        return jndid;
    }

    public void setJndid(long jndid) {
        this.jndid = jndid;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    public long getQuestionid() {
        return questionid;
    }

    public void setQuestionid(long questionid) {
        this.questionid = questionid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getAlerton() {
        return alerton;
    }

    public void setAlerton(String alerton) {
        this.alerton = alerton;
    }

    public long getJobneedid() {
        return jobneedid;
    }

    public void setJobneedid(long jobneedid) {
        this.jobneedid = jobneedid;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getIsmandatory() {
        return ismandatory;
    }

    public void setIsmandatory(String ismandatory) {
        this.ismandatory = ismandatory;
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

}
