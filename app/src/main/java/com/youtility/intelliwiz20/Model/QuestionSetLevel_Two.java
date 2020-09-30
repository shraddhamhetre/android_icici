package com.youtility.intelliwiz20.Model;

import java.util.ArrayList;

/**
 * Created by PrashantD on 21/11/17.
 * question ans data transfer object
 *
 */

public class QuestionSetLevel_Two {

    private String jobdesc;
    private long questionsetid;
    private int seqno;

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    private ArrayList<JobNeedDetails> details;

    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
    }

    public String getJobdesc() {
        return jobdesc;
    }

    public void setJobdesc(String jobdesc) {
        this.jobdesc = jobdesc;
    }

    public ArrayList<JobNeedDetails> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<JobNeedDetails> details) {
        this.details = details;
    }
}
