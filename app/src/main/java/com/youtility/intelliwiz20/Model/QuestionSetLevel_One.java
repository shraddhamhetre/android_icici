package com.youtility.intelliwiz20.Model;

import java.util.ArrayList;

/**
 * Created by PrashantD on 21/11/17.
 *
 * upload IR related answer's data transfer object
 */

public class QuestionSetLevel_One {
    private String jobDesc;
    private long questionsetid;
    private ArrayList<QuestionSetLevel_Two>subChild;

    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
    }

    public ArrayList<QuestionSetLevel_Two> getSubChild() {
        return subChild;
    }

    public void setSubChild(ArrayList<QuestionSetLevel_Two> subChild) {
        this.subChild = subChild;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }


}
