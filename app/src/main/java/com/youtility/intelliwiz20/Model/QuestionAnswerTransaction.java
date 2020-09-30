package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 26/10/17.
 * Upload IR, check point or task related question's answer data transfer object
 *
 */

public class QuestionAnswerTransaction {
    private long questAnsTransId;
    private long qsetID;
    private int seqno;
    private long questionid;
    private String questionname;
    private double min;
    private double max;
    private long type;
    private String options;
    private long unit;
    private long cuser;
    private String cdtz;
    private long muser;
    private String mdtz;
    private String questAnswer;
    private long assetID;
    private String questionsetName;
    private long parentId;
    private String alerton;
    private String ismandatory;
    private long buid;
    private long jobneedid;
    private String parentFolder;
    private String parentActivity;
    private long jndid;
    private long timestamp;
    private String imagePath;
    private boolean isCorrect;

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getJndid() {
        return jndid;
    }

    public void setJndid(long jndid) {
        this.jndid = jndid;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public String getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(String parentActivity) {
        this.parentActivity = parentActivity;
    }

    public long getJobneedid() {
        return jobneedid;
    }

    public void setJobneedid(long jobneedid) {
        this.jobneedid = jobneedid;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public String getIsmandatory() {
        return ismandatory;
    }

    public void setIsmandatory(String ismandatory) {
        this.ismandatory = ismandatory;
    }

    public String getAlerton() {
        return alerton;
    }

    public void setAlerton(String alerton) {
        this.alerton = alerton;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getQuestionsetName() {
        return questionsetName;
    }

    public void setQuestionsetName(String questionsetName) {
        this.questionsetName = questionsetName;
    }

    public long getQuestionid() {
        return questionid;
    }

    public void setQuestionid(long questionid) {
        this.questionid = questionid;
    }

    public long getAssetID() {
        return assetID;
    }

    public void setAssetID(long assetID) {
        this.assetID = assetID;
    }

    public long getQsetID() {
        return qsetID;
    }

    public void setQsetID(long qsetID) {
        this.qsetID = qsetID;
    }


    public String getQuestAnswer() {
        return questAnswer;
    }

    public void setQuestAnswer(String questAnswer) {
        this.questAnswer = questAnswer;
    }

    public long getQuestAnsTransId() {
        return questAnsTransId;
    }

    public void setQuestAnsTransId(long questAnsTransId) {
        this.questAnsTransId = questAnsTransId;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    public String getQuestionname() {
        return questionname;
    }

    public void setQuestionname(String questionname) {
        this.questionname = questionname;
    }

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

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
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
}
