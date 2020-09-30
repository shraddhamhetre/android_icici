package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 26/9/17.
 *
 *
 * question and set mapping data transfer object
 */
//"$qsbid$ismandatory$seqno$cdtz$mdtz$isdeleted$cuser$muser$qsetcode$questioncode"
public class QuestionSetBelonging {
    private long qsbid;
    private String ismandatory;
    private String seqno;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private long cuser;
    private long muser;
    private long questionsetid;
    private long questionid;
    private String min;
    private String max;
    private String option;
    private String alerton;

    private long buid;

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getQsbid() {
        return qsbid;
    }

    public void setQsbid(long qsbid) {
        this.qsbid = qsbid;
    }

    public String getIsmandatory() {
        return ismandatory;
    }

    public void setIsmandatory(String ismandatory) {
        this.ismandatory = ismandatory;
    }

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
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

    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
    }

    public long getQuestionid() {
        return questionid;
    }

    public void setQuestionid(long questionid) {
        this.questionid = questionid;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
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
}
